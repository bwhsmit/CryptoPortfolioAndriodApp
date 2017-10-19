package com.example.bwhsm.bramsmit_pset6;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Portfolio Class. Each User has a unique portfolio which contains all it's most recent data.
 * This object will be stored in Firebase's realtimedatabase
 */

public class Portfolio {
    private HashMap<String,Coin> coinTable;
    private double totalValue;

    private static final String TAG = "Portfolio";

    public Portfolio() {
        coinTable = new HashMap<String,Coin>();
    }

    public Portfolio(ArrayList<Coin> coinArray) {
        for (int i=0;i<coinArray.size();i++) {
            coinTable.put(coinArray.get(i).getId(),coinArray.get(i));
        }
    }

    public Double getAmount(String coinId) {
         return coinTable.get(coinId).getAmount();
    }

    public void removeCoin(String coinId) {
        coinTable.remove(coinId);

    }

    public void addCoin(Coin coin) {
            coinTable.put(coin.getId(),coin);
    }

    public void setCoinTable(HashMap<String, Coin> coinTable) {
        this.coinTable = coinTable;
    }

    public HashMap<String, Coin> getCoinTable() {
        return coinTable;
    }

    // TODO setCoinList function to prevent warnings from Firebase
    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public boolean isEmpty() {
        if (coinTable == null ) {
            Log.d(TAG, "isEmpty: coinTable not initialized");
            return true;
        }
        return coinTable.isEmpty();
    }

    public ArrayList<String> getCoinIdList() {
        return new ArrayList<String>(coinTable.keySet());
    }

    public ArrayList<Coin> getCoinList() {
        return new ArrayList<Coin>(coinTable.values());
    }
}
