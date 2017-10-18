package com.example.bwhsm.bramsmit_pset6;

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

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private FirebaseAuth mAuth;

    private String email;
    private String password;

    @InjectView (R.id.input_email)      EditText emailText;
    @InjectView (R.id.input_password)   EditText passwordText;
    @InjectView (R.id.btn_signup)       Button signupButton;
    @InjectView (R.id.link_login)       TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    emailText.setError("You must enter a valid email address");
                }
                else if (TextUtils.isEmpty(password) || password.length() < 6) {
                    passwordText.setError("Your password should contain a minimum of 6 characters");
                }
                else {
                    createUser();
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

    }

    public void createUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("create user", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "created user: " + email,
                                    Toast.LENGTH_SHORT).show();
                            goToMain();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        goToLogin();
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        this.startActivity(loginIntent);
        finish();
    }

    private void goToMain() {
        Intent mainIntent = new Intent(this,MainActivity.class);
        this.startActivity(mainIntent);
        finish();
    }
}
