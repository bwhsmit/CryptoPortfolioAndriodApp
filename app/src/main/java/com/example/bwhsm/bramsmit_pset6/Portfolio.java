package com.example.bwhsm.bramsmit_pset6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bwhsm on 17-10-2017.
 */

public class Portfolio {
    private Map<String,Double> coins;
    private double totalValue;


    public Portfolio() {
        coins = new HashMap<String,Double>();
    }

    public Portfolio(Map<String, Double> coins) {
        this.coins = coins;
    }

    public Double getAmount(String coinId) {
        return coins.get(coinId);
    }

    public void addCoin(String coinId, Double amount) {
        coins.put(coinId,amount);
    }

    public void removeCoin(String coinId) {
        coins.remove(coinId);
    }

    public void addAmount(String coinId, Double amount) {
        coins.put(coinId, coins.get(coinId) + amount);
    }

    public void setCoins(Map<String, Double> coins) {
        this.coins = coins;
    }

    public Map<String, Double> getCoins() {
        return coins;
    }

    public List<String> getCoinList() {
        List<String> coinList = new ArrayList<String>(coins.keySet());
        return coinList;
    }
    // TODO setCoinList function to prevent warnings from Firebase
    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public boolean isEmpty() {
        return coins.isEmpty();
    }
}
