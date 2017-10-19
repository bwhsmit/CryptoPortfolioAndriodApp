package com.example.bwhsm.bramsmit_pset6;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bwhsm on 17-10-2017.
 */

public class ApiAsyncTask extends AsyncTask<String, Integer, String> {
    Context context;
    MainActivity mainAct;
    // List containing the Id's of the coins we want to receive data of.
    ArrayList<String> coinIDList;

    private static final String TAG = "ApiAsyncTask";

    /** constructor */
    public ApiAsyncTask(MainActivity main) {
        this.mainAct = main;
        this.context = this.mainAct.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute: started Api request");
    }

    @Override
    protected String doInBackground(String... params) {
        coinIDList = new ArrayList<String>(Arrays.asList(params));
        return HttpRequestHelper.downloadFromServer();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result.length() == 0) {
            Log.d(TAG, "onPostExecute: No results found");
//            Toast.makeText(context, "No results found...", Toast.LENGTH_SHORT).show();
        }
        else {
            ArrayList<Coin> coinList = new ArrayList<Coin>();
            try {
                JSONArray array = new JSONArray(result);
                for (int i=0; i<array.length();i++) {
                    JSONObject coinData = array.getJSONObject(i);
                    String id = coinData.getString("id");
                    if (coinIDList.contains(id)) {
                        Coin coin = new Coin();
                        String name = coinData.getString("name");
                        String symbol = coinData.getString("symbol");
                        int rank = coinData.getInt("rank");
                        double priceUSD = coinData.getDouble("price_usd");
                        double priceBTC = coinData.getDouble("price_btc");
                        double marketCapUSD = coinData.getDouble("market_cap_usd");
                        double percentChange_1h = 0;
                        if (!coinData.isNull("percent_change_1h")){
                            percentChange_1h = coinData.getDouble("percent_change_1h");
                        }
                        double percentChange_24h = coinData.getDouble("percent_change_24h");
                        double percentChange_7d = coinData.getDouble("percent_change_7d");

                        coin = new Coin(id,name,symbol,rank,priceUSD,priceBTC,marketCapUSD,percentChange_1h,percentChange_24h,percentChange_7d);
                        coinList.add(coin);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.mainAct.storeCoinData(coinList);
        }
    }
}
