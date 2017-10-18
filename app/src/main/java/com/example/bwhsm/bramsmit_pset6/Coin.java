package com.example.bwhsm.bramsmit_pset6;

/**
 * Created by bwhsm on 17-10-2017.
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

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getPriceUSD() {
        return priceUSD;
    }

    public void setPriceUSD(float priceUSD) {
        this.priceUSD = priceUSD;
    }

    public double getPriceBTC() {
        return priceBTC;
    }

    public void setPriceBTC(float priceBTC) {
        this.priceBTC = priceBTC;
    }

    public double getMarketCapUSD() {
        return marketCapUSD;
    }

    public void setMarketCapUSD(float marketCapUSD) {
        this.marketCapUSD = marketCapUSD;
    }

    public double getPercentChange_1h() {
        return percentChange_1h;
    }

    public void setPercentChange_1h(float percentChange_1h) {
        this.percentChange_1h = percentChange_1h;
    }

    public double getPercentChange_24h() {
        return percentChange_24h;
    }

    public void setPercentChange_24h(float percentChange_24h) {
        this.percentChange_24h = percentChange_24h;
    }

    public double getPercentChange_7d() {
        return percentChange_7d;
    }

    public void setPercentChange_7d(float percentChange_7d) {
        this.percentChange_7d = percentChange_7d;
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getHoldingValue() {
        double value = priceUSD * amount;
        return value;
    }

}
