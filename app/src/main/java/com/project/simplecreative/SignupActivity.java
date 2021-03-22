package com.project.simplecreative;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.simplecreative.Model.UserModel;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText name, email, pass;
    private TextView login;
    private Button signup;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (firebaseAuth.getCurrentUser() != null){
            UserID = firebaseAuth.getCurrentUser().getUid();
        }

        name = findViewById(R.id.signupName);
        email = findViewById(R.id.signupEmail);
        pass = findViewById(R.id.signupPassword);
        signup = findViewById(R.id.signupBtn);
        login = findViewById(R.id.signupLogin);
        progressBar = findViewById(R.id.signupProgressBar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    //Check Empty
    private void signup() {
        Log.d(TAG, "signup: starting...");

        String sName = name.getText().toString();
        String sEmail = email.getText().toString();
        String sPass = pass.getText().toString();

        if (sName.isEmpty() || sEmail.isEmpty()){
            Toast.makeText(SignupActivity.this, "Please fill user or email", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else if (pass.length()<8){
            Toast.makeText(getApplicationContext(), "Password must be at least 8 char", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else {
            checkDisplayName(sName, sEmail, sPass);
        }
    }

    //Check Display Name Exist
    private void checkDisplayName(final String sName, final String sEmail, final String sPass) {
        databaseReference.child(getString(R.string.firebase_user)).orderByChild(getString(R.string.firebase_email)).equalTo(sEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, "Display Name Already Used", Toast.LENGTH_SHORT).show();
                }else {
                    firebaseAuth.createUserWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Sign Up Success");
                            Toast.makeText(SignupActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();

                            UserID = firebaseAuth.getCurrentUser().getUid();
                            addNewData(sEmail, sName, "", "", "", "");

                            //startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        }
                        if (!task.isSuccessful()){
                            Log.d(TAG, "onComplete: Sign Up Failed");
                            Toast.makeText(SignupActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage() );
            }
        });
    }

    //Add New Data
    private void addNewData(String sEmail, String sName, String s, String s1, String s2, String s3) {
        UserModel userModel = new UserModel(UserID, sEmail, sName, s, s1, s2, s3, 1);
        databaseReference.child(getString(R.string.firebase_user))
                .child(UserID)
                .setValue(userModel);
    }

}
