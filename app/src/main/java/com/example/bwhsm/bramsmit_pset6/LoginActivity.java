package com.example.bwhsm.bramsmit_pset6;

/**
 * Created by bwhsm on 19-9-2017.
 *
 * This Login activity handles the user login process
 *
 */


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private String email;
    private String password;

    @InjectView (R.id.input_email)      EditText emailText;
    @InjectView (R.id.input_password)   EditText passwordText;
    @InjectView (R.id.btn_login)        Button loginButton;
    @InjectView (R.id.link_signup)      TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    emailText.setError("You must enter a valid email address");
                }
                else if (TextUtils.isEmpty(password)) {
                    passwordText.setError("You must enter a password");
                }
                else {
                    logIn();
                }
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignup();
            }
        });

    }

    private void logIn() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "logged in user: " + email,
                                    Toast.LENGTH_SHORT).show();
                            goToMain();
                        }
                        // ...


                    }
                });
    }

    private void goToMain() {
        Intent mainIntent = new Intent(this,MainActivity.class);
        this.startActivity(mainIntent);
        finish();
    }

    private void goToSignup() {
        Intent signupIntent = new Intent(this, SignupActivity.class);
        this.startActivity(signupIntent);
        finish();
    }


}
