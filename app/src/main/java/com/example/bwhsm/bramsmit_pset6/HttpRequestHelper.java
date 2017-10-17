package com.example.bwhsm.bramsmit_pset6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bwhsm on 19-9-2017.
 */

public class HttpRequestHelper {

    protected static synchronized String downloadFromServer(String... params) {
        String result = "";
        String chosenTag = params[0];

        URL url = null;
        try {
            url = new URL("https://api.coinmarketcap.com/v1/ticker/" + chosenTag);
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
                    String line;
                    while ((line = bReader.readLine()) != null) {
                        result += line;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
