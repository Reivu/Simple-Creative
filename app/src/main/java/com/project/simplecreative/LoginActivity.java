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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText email, password;
    private TextView signup;
    private Button login;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            UserID = firebaseAuth.getCurrentUser().getUid();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.loginSignup);
        progressBar = findViewById(R.id.loginProgressBar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    //Firebase Login
    private void login() {
        Log.d(TAG, "login: starting...");

        final String lEmail = email.getText().toString();
        String lPass = password.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        if (lEmail.isEmpty() || lPass.isEmpty()){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Username or Password is Wrong!!", Toast.LENGTH_SHORT).show();
        }else {
            firebaseAuth.signInWithEmailAndPassword(lEmail, lPass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Login success for " + lEmail);

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, getString(R.string.auth_success), Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                if (!task.isSuccessful()){
                    Log.d(TAG, "onComplete: Login Failed");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                }
                }
            });
        }
    }
}
