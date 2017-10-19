package com.example.bwhsm.bramsmit_pset6;

import android.util.Log;
import android.util.TimingLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bwhsm on 19-9-2017.
 *
 * HttpRequestHelper handles the direct interaction and requests to the CoinMarketCapAPI
 * It requests the API for the current coin data and after processing returns it to the ApiAsyncTask
 */

public class HttpRequestHelper {
    private static final String TAG = "HttpRequestHelper";

    protected static synchronized String downloadFromServer() {
        String result = "";

        URL url = null;
        try {
            url = new URL("https://api.coinmarketcap.com/v1/ticker/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connect;

        if (url != null) {
            try {
                connect = (HttpURLConnection) url.openConnection();
                connect.setRequestMethod("GET");

                Integer responseCode = connect.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                    // Use Stringbuilder to append the lines from the InputStream together efficiently
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = bReader.readLine()) != null) {
                        total.append(line);
                    }
                    result = total.toString();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
