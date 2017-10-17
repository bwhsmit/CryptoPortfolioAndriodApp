package com.example.bwhsm.bramsmit_pset6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bwhsm on 17-10-2017.
 */

public class Portfolio {
    private Map<String,Float> coins;
    private int totalValue;

    public Portfolio() {
        coins = new HashMap<String,Float>();
    }

    public Portfolio(Map<String, Float> coins) {
        this.coins = coins;
    }

    public Float getAmount(String coinId) {
        return coins.get(coinId);
    }

    public void addCoin(String coinId, Float amount) {
        coins.put(coinId,amount);
    }

    public void removeCoin(String coinId) {
        coins.remove(coinId);
    }

    public void addAmount(String coinId, Float amount) {
        coins.put(coinId, coins.get(coinId) + amount);
    }

    public void setCoins(Map<String, Float> coins) {
        this.coins = coins;
    }

    public Map<String, Float> getCoins() {
        return coins;
    }

    public List<String> getCoinList() {
        List<String> coinList = new ArrayList<String>(coins.keySet());
        return coinList;
    }
    // TODO setCoinList function to prevent warnings from Firebase
    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }
}
