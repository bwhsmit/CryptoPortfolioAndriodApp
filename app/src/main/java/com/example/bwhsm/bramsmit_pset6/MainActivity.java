package com.example.bwhsm.bramsmit_pset6;

/**
 * Created by bwhsm on 19-9-2017.
 *
 * MainActivity that handles the app's UI and Database interaction
 *
 */

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

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth                    mAuth;
    private FirebaseAuth.AuthStateListener  mAuthListener;
    private DatabaseReference               mDatabase;

    Portfolio mPortfolio;

    ArrayList<Coin> coinData;

    @InjectView (R.id.tvTotalValue) TextView                tvTotalValue;
    @InjectView (R.id.addButton)    FloatingActionButton    addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inject XML items
        ButterKnife.inject(this);


        // Set the App bar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mPortfolio = new Portfolio();
        // Firebase listener for login status
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in, load data from database
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    getFromDb();
                } else {
                    // User is signed out, redirect to LogIn activity
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    goToLogin();
                }
                // ...
            }
        };


        //
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCoin();
            }
        });
    }

    // Create Logout button in the appbar
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

    private void addCoin() {
        // Dialog for adding new coins
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
                        String newCoinId = inputId.getText().toString();
                        String newCoinAmount = inputAmount.getText().toString();
                        // add new coin to Database
                        Coin coin = new Coin(newCoinId, Double.valueOf(newCoinAmount));
                        mPortfolio.addCoin(coin);
                        getCoinData(coin);
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

    private void getFromDb() {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // Rebuild portfolio from database
                mPortfolio = dataSnapshot.getValue(Portfolio.class);
                if (mPortfolio != null) {
                    if (! mPortfolio.isEmpty()) {
                        // get up to date coin data from the CoinMarketCap API
                        getCoinData();
                    }
                }
                else {
                    mPortfolio = new Portfolio();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // Initializes the listView, which displays the user's current portfolio status
    private void loadListView() {
        ArrayAdapter arrayAdapter = new CustomAdapter(this,mPortfolio.getCoinList());
        ListView lv = (ListView) findViewById(R.id.mainLV);
        lv.setAdapter(arrayAdapter);

        // Allow for item deletion with LongClickListener
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Coin coin = (Coin) parent.getItemAtPosition(position);
                mPortfolio.removeCoin(coin.getId());
                getCoinData();
                return true;
            }
        });
    }

    // Get coin data for the user's whole portfolio from the CoinMarketCap API
    private void getCoinData() {
        List<String> coinList = mPortfolio.getCoinIdList();

        // ApiAsyncTask gets the coin data from the api and calls storeCoinData() on postExecute()
        ApiAsyncTask asyncTask = new ApiAsyncTask(this);
        asyncTask.execute(coinList.toArray(new String[coinList.size()]));
    }

    // Get coin data for a specific coin
    private void getCoinData(Coin newCoin) {

        ApiAsyncTask asyncTask = new ApiAsyncTask(this);
        asyncTask.execute(newCoin.getId());
    }

    public void storeCoinData(ArrayList<Coin> coinList) {

        // Add the amounts from the database to each coin
        for (int i=0;i<coinList.size();i++) {
            Coin coin = coinList.get(i);
            coin.setAmount(mPortfolio.getAmount(coin.getId()));
            mPortfolio.addCoin(coin);
        }

        // Update the user's current total portfolio value
        calcTotalValue();

        // Update the database with the gathered data
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(mPortfolio);
        loadListView();
    }

    private void calcTotalValue() {
        // Calculate total portfolio value and log it
        ArrayList<Coin> coinList = mPortfolio.getCoinList();
        double totalValue = 0;
        for (int i=0;i<coinList.size();i++) {
            totalValue += coinList.get(i).getHoldingValue();
        }
        tvTotalValue.setText("$" + new DecimalFormat("##.##").format(totalValue));
        mPortfolio.setTotalValue(totalValue);
        Log.d(TAG, "Total Value is: " + mPortfolio.getTotalValue());
    }


}


