package com.example.bwhsm.bramsmit_pset6;

/**
 * Created by bwhsm on 17-10-2017.
 *
 * The Coin class stores all data received from the CoinMarketCap API and the
 * amount the user currently possesses
 */

public class Coin {
    private String id;
    private String name;
    private String symbol;
    private int rank;
    private double priceUSD;
    private double priceBTC;
    private double marketCapUSD;
    private double percentChange_1h;
    private double percentChange_24h;
    private double percentChange_7d;
    private double amount;

    public Coin() {}

    public Coin(String id, String name, String symbol, int rank, double priceUSD,
                double priceBTC, double marketCapUSD, double percentChange_1h,
                double percentChange_24h, double percentChange_7d) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.rank = rank;
        this.priceUSD = priceUSD;
        this.priceBTC = priceBTC;
        this.marketCapUSD = marketCapUSD;
        this.percentChange_1h = percentChange_1h;
        this.percentChange_24h = percentChange_24h;
        this.percentChange_7d = percentChange_7d;
    }

    public Coin(String id, Double amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPriceUSD() {
        return priceUSD;
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Returns the user's total holding value
    public double getHoldingValue() {
        double value = priceUSD * amount;
        return value;
    }

}
