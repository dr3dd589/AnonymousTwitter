package com.iitr.saurabh.anonymoustwitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class postPhoto extends AppCompatActivity {
    private ImageButton mSelectPicbtn;
    private static final int GALLERY_REQUEST = 1;
    private EditText mAboutPic;
    private ImageButton mPostbtn;
    private Uri ImageUri;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseRef;
    private ProgressDialog mPrgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);


        mSelectPicbtn = (ImageButton) findViewById(R.id.add_pic);
     mAboutPic = (EditText)findViewById(R.id.about_pic);
     mPostbtn = (ImageButton) findViewById(R.id.post_btn);
     mStorage = FirebaseStorage.getInstance().getReference();
     mPrgs = new  ProgressDialog(this);
     mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Message");


     mPostbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             PostDatabase();
         }
     });



     mSelectPicbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
             galleryIntent.setType("image/*");
             startActivityForResult(galleryIntent,GALLERY_REQUEST);

         }
     });
    }

    private void PostDatabase() {

        mPrgs.setMessage("Posting Tweet...");
        mPrgs.setCanceledOnTouchOutside(false);
        mPrgs.show();

     final String AboutPic = mAboutPic.getText().toString().trim();
     if (!TextUtils.isEmpty(AboutPic) && ImageUri !=null){

         StorageReference PicFilepath = mStorage.child(ImageUri.getLastPathSegment());

         PicFilepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                 Uri DownldUri = taskSnapshot.getDownloadUrl();
                 DatabaseReference mPost = mDatabaseRef.push();
                 mPost.child("text").setValue(AboutPic);
                 mPost.child("ImageUrl").setValue(DownldUri.toString());
                 mPrgs.dismiss();

                 Intent intent = new Intent(postPhoto.this,MainActivity.class);
                 startActivity(intent);

             }
         });
     }
     else {
         mPrgs.dismiss();
         Toast.makeText(this,"Please select pic and write about it something..", Toast.LENGTH_SHORT).show();
     }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode==RESULT_OK){

             ImageUri = data.getData();
            Picasso.with(this).load(ImageUri).fit().into(mSelectPicbtn);
        }

    }
}
