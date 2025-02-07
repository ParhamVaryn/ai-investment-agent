package com.aiinvestment.lib.alphavantage;

public class AlphaVantage {
    private static AlphaVantage INSTANCE;
    private Config config;

    private AlphaVantage() { }

    public void init(Config config) {
        this.config = config;
    }

    public static AlphaVantage api() {
        if (INSTANCE == null) {
            INSTANCE = new AlphaVantage();
        }
        return INSTANCE;
    }

    public TimeSeries timeSeries() {
        return new TimeSeries(this.config);
    }
    
    // You can add additional methods (forex, exchangeRate, etc.) as needed.
} 