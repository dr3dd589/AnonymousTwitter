package com.iitr.saurabh.anonymoustwitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mAnonmsName;
    private TextInputLayout mEmail;
    private TextInputLayout mPass;
    private ImageButton mReg;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mRegPrgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mAnonmsName = (TextInputLayout) findViewById(R.id.reg_Name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_Email);
        mPass = (TextInputLayout) findViewById(R.id.reg_Pass);
        mReg = (ImageButton) findViewById(R.id.Reg_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRegPrgs = new ProgressDialog(this);

        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_Name = mAnonmsName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String pass = mPass.getEditText().getText().toString();

                    if (!TextUtils.isEmpty(display_Name)||!TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)){
                        mRegPrgs.setTitle("Registering User");
                        mRegPrgs.setMessage("Please wait while we creating your account");
                        mRegPrgs.setCanceledOnTouchOutside(false);
                        mRegPrgs.show();

                        register_user(display_Name,email,pass);
                        DatabaseReference mUser = mDatabase.child("UserDetails").push();
                        mUser.child("AnonymousName").setValue(display_Name);
                        mUser.child("UserMail").setValue(email);
                    }else {
                        Toast.makeText(RegisterActivity.this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private void register_user(String AnonymousName, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    mRegPrgs.dismiss();
                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }
                else {
                    mRegPrgs.hide();
                    Toast.makeText(RegisterActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void LogIn(View view) {

        Intent intent = new Intent(RegisterActivity.this,StartActivity.class);
        startActivity(intent);
        finish();
    }
}
