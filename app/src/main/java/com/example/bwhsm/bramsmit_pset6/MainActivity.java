package com.example.bwhsm.bramsmit_pset6;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    Portfolio mPortfolio;

    ArrayList<Coin> coinData = new ArrayList<Coin>();

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the App bar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        email = "test@test.com";
        password = "password123";

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("singed in", "onAuthStateChanged:signed_in:" + user.getUid());
                    mPortfolio = new Portfolio();
                } else {
                    // User is signed out
                    Log.d("singed out", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

//        createUser();

        findViewById(R.id.logInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                addToDb();
            }
        });

        findViewById(R.id.getButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFromDb();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    private void addListeners() {
        findViewById(R.id.logInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                addToDb();
            }
        });

        findViewById(R.id.getButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFromDb();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
            mAuth.getInstance().signOut();
        }

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
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "created user: " + email,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void logIn() {
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
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "logged in user: " + email,
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...


                    }
                });
    }

    private void addToDb() {
        EditText et1 = (EditText) findViewById(R.id.et1);
        EditText et2 = (EditText) findViewById(R.id.et2);

        String newCoinId = et1.getText().toString();
        Float newCoinAmount = Float.valueOf(et2.getText().toString());
        mPortfolio.addCoin(newCoinId, newCoinAmount);

        Log.d(TAG, "addToDb: " + newCoinId + ";" + newCoinAmount);
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(mPortfolio);
    }

    private void getFromDb() {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                mPortfolio = dataSnapshot.getValue(Portfolio.class);
                TextView tv1 = (TextView) findViewById(R.id.tv1);

                tv1.setText(mPortfolio.getAmount("bitcoin").toString());

                // get coinData from the CoinMarketCap API
                getCoinData();

                // TODO calculate total portfolio value and log it
//                tv1.setText(mPortfolio.getTotalValue());
//                Log.d(TAG, "Total Value is: " + mPortfolio.getTotalValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // get coinData from the CoinMarketCap API
    private void getCoinData() {
        List<String> coinList = mPortfolio.getCoinList();
        for (int i=0;i<coinList.size();i++) {
            ApiAsyncTask asyncTask = new ApiAsyncTask(this);
            asyncTask.execute(coinList.get(i));
        }
    }

    public void storeCoinData(Coin coin) {
        coinData.add(coin);
        Log.d(TAG, "storeCoinData: " + coin.getId());
    }
}


