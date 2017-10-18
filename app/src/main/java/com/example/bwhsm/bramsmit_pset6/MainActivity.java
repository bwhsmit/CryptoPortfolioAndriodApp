package com.example.bwhsm.bramsmit_pset6;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    Portfolio mPortfolio;

    ArrayList<Coin> coinData;

    @InjectView (R.id.tvTotalValue) TextView                tvTotalValue;
    @InjectView (R.id.addButton)    FloatingActionButton    addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        // Set the App bar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mPortfolio = new Portfolio();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    getFromDb();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    goToLogin();
                }
                // ...
            }
        };

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCoin();
            }
        });
    }

    private void addCoin() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_dialog_box,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        final EditText inputId = (EditText) view.findViewById(R.id.input_id);
        final EditText inputAmount = (EditText) view.findViewById(R.id.input_amount);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO get user input
                        String newCoinId = inputId.getText().toString();
                        String newCoinAmount = inputAmount.getText().toString();
                        addToDb(newCoinId, newCoinAmount);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.getInstance().signOut();
            goToLogin();
        }
        return true;
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(this,LoginActivity.class);
        this.startActivity(loginIntent);
        finish();
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
//            mAuth.getInstance().signOut();
        }

    }

    private void addToDb(String newCoinId, String newCoinAmount) {
        mPortfolio.addCoin(newCoinId, Double.valueOf(newCoinAmount));
        Log.d(TAG, "addToDb: " + newCoinId + ";" + newCoinAmount);
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(mPortfolio);
        getCoinData();
    }

    private void getFromDb() {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                mPortfolio = dataSnapshot.getValue(Portfolio.class);
                Log.d(TAG, "onDataChange: ");
                if (! mPortfolio.isEmpty()) {
                    // get coinData from the CoinMarketCap API
                    getCoinData();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void loadListView() {
        ArrayAdapter arrayAdapter = new CustomAdapter(this,coinData);
        ListView lv = (ListView) findViewById(R.id.mainLV);
        lv.setAdapter(arrayAdapter);
    }

    // get coinData from the CoinMarketCap API
    private void getCoinData() {
        coinData = new ArrayList<Coin>();
        List<String> coinList = mPortfolio.getCoinList();
        for (int i=0;i<coinList.size();i++) {
            ApiAsyncTask asyncTask = new ApiAsyncTask(this);
            asyncTask.execute(coinList.get(i));

        }
        Log.d(TAG, "getCoinData: " + coinData.size());
    }

    public void storeCoinData(Coin coin) {
        coin.setAmount(mPortfolio.getAmount(coin.getId()));
        coinData.add(coin);
        Log.d(TAG, "storeCoinData: " + coin.getId());

        // calculate total portfolio value and log it
        if (coinData.size() == mPortfolio.getCoinList().size()) {
            double totalValue = 0;
            for (int i=0;i<coinData.size();i++) {
                totalValue += coinData.get(i).getHoldingValue();
            }
            tvTotalValue.setText("$" + new DecimalFormat("##.##").format(totalValue));
            mPortfolio.setTotalValue(totalValue);
            Log.d(TAG, "Total Value is: " + mPortfolio.getTotalValue());
            loadListView();
        }
    }

}


