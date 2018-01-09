package com.iitr.saurabh.anonymoustwitter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbr;
    private EditText mSendText;
    private ImageButton mSelectPhotos;
    private ImageButton mSendMassagebtn;
    private RecyclerView mPostList;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabase;
    private final List<Send> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private FirebaseRecyclerAdapter mAdapter;
    private static final int ITEMS_TO_LOAD = 20;
    private int mCurrentPage = 1;
    private boolean mOnceLike = false;
    private long count=0;
    private RelativeLayout mMainLayout;
    FirebaseAuth.AuthStateListener mAuthListener;



    @Override
protected void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
    mAdapter.startListening();

   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mToolbr = (Toolbar)findViewById(R.id.main_page_toolbar);
        mSendMassagebtn = (ImageButton) findViewById(R.id.send_text);
        mSelectPhotos = (ImageButton) findViewById(R.id.add_photos);
        mPostList = (RecyclerView) findViewById(R.id.Massage_list);
        mSendText = (EditText) findViewById(R.id.send_Massage);
        mLinearLayout = new LinearLayoutManager(this);
        mLinearLayout.setReverseLayout(false);
        mLinearLayout.setStackFromEnd(true);
        mPostList.setHasFixedSize(true);
        mPostList.setLayoutManager(mLinearLayout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);
        mDatabase.keepSynced(true);
        mMainLayout = (RelativeLayout) findViewById(R.id.main_activity_layout);

        setSupportActionBar(mToolbr);
        getSupportActionBar().setTitle("Anonytter");






       //photo selection.
        mSelectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,postPhoto.class);
                startActivity(intent);
                finish();
            }
        });


        //message sending button.
        mSendMassagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMassage();
                mSendText.getText().clear();
            }
        });


        loadMessages();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() ==null){
                    startActivity(new Intent(MainActivity.this,StartActivity.class));

                }
            }
        };
       setUpFirebaseAdapter();


    }



     public void setUpFirebaseAdapter(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Message");

         final FirebaseRecyclerOptions<Send> options =
                 new FirebaseRecyclerOptions.Builder<Send>()
                         .setQuery(ref, Send.class)
                         .build();

          mAdapter = new FirebaseRecyclerAdapter<Send, PostViewHolder>(options) {
              @Override
              public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                  View view = LayoutInflater.from(parent.getContext())
                          .inflate(R.layout.post_text_row, parent, false);

                  return new PostViewHolder(view);
              }

              @Override
              protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull final Send model) {
                  final String Tweet_key = getRef(position).getKey();
                  holder.setMessage(model.getText());
                  holder.setImage(getApplicationContext(),model.getImageUrl());
                  holder.changeLikebtn(Tweet_key);
                  holder.like(model.getNoOfLikes());

                  holder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {

                          mOnceLike = true;
                              ref.addValueEventListener(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(DataSnapshot dataSnapshot) {
                                      if (mOnceLike) {

                                          if (dataSnapshot.child(Tweet_key).child("UserId").hasChild(mAuth.getCurrentUser().getUid())) {
                                              ref.child(Tweet_key).child("UserId").child(mAuth.getCurrentUser().getUid()).removeValue();
                                              count = dataSnapshot.child(Tweet_key).child("UserId").getChildrenCount();
                                              ref.child(Tweet_key).child("NoOfLikes").setValue(count-1);
                                              mOnceLike = false;
                                          } else {
                                              ref.child(Tweet_key).child("UserId").child(mAuth.getCurrentUser().getUid()).setValue("Liked");
                                              count = dataSnapshot.child(Tweet_key).child("UserId").getChildrenCount();
                                              ref.child(Tweet_key).child("NoOfLikes").setValue(count+1);
                                              mOnceLike = false;
                                          }
                                      }
                                  }

                                  @Override
                                  public void onCancelled(DatabaseError databaseError) {

                                  }
                              });



                      }
                  });


              }
         };
         mPostList.setAdapter(mAdapter);
     }

     public static class PostViewHolder extends RecyclerView.ViewHolder{
         ImageButton mLikebtn;
         DatabaseReference mChangeLikeImageref;
         FirebaseAuth mAuth;
        public PostViewHolder(View itemView) {
           super(itemView);
            mLikebtn = (ImageButton)itemView.findViewById(R.id.like_btn);
            mChangeLikeImageref = FirebaseDatabase.getInstance().getReference().child("Message");
            mChangeLikeImageref.keepSynced(true);
            mAuth = FirebaseAuth.getInstance();
            mChangeLikeImageref.keepSynced(true);
       }
       public void setMessage(String msg){
           TextView message_set = (TextView) itemView.findViewById(R.id.text_massage);
           message_set.setText(msg);
       }
         public void setImage(Context ctx,String image){
             ImageView image_set = (ImageView) itemView.findViewById(R.id.Image_Post);
             Picasso.with(ctx).load(image).resize(1100,700).into(image_set);
         }

        public void like(long likes){
            TextView mLikeText = (TextView) itemView.findViewById(R.id.No_of_likes);
            String s = String.valueOf(likes);
            mLikeText.setText(s);

         }
         public void changeLikebtn(final String Tweet_key){

            mChangeLikeImageref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Tweet_key).child("UserId").hasChild(mAuth.getCurrentUser().getUid())){
                         mLikebtn.setImageResource(R.mipmap.ic_love_red);
                    }else{
                        mLikebtn.setImageResource(R.mipmap.ic_love_gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
         }



   }


    //Initial load all previous messages.
    private void loadMessages() {
        Query mQuery = mDatabase.child("Message").limitToLast(mCurrentPage * ITEMS_TO_LOAD);
        mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Send mMessage = dataSnapshot.getValue(Send.class);
                messagesList.add(mMessage);
                mPostList.scrollToPosition(messagesList.size() - 1);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //sending message button.
    private void sendMassage() {
        final String send_text = mSendText.getText().toString().trim();
        if (!TextUtils.isEmpty(send_text)){
            DatabaseReference mSend = mDatabase.child("Message").push();
            mSend.child("text").setValue(send_text);
        }
    }


   //back to start menu method.
    public  void backToStart(){
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    //side menu option.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }



    //side menu option click method.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
     if (item.getItemId() == R.id.main_logout){
         FirebaseAuth.getInstance().signOut();
         backToStart();
     }else if(item.getItemId() == R.id.chat_wall1){
         mMainLayout.setBackgroundResource(R.mipmap.cha_wall1);
     }else if(item.getItemId() == R.id.chat_wall2){
         mMainLayout.setBackgroundResource(R.mipmap.chat_wall2);
     }
        return true;
    }


}

