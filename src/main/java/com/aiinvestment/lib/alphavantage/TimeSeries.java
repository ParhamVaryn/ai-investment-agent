package com.aiinvestment.lib.alphavantage;

public class TimeSeries {
    private final Config config;

    public TimeSeries(Config config) {
        this.config = config;
    }
    
    // Example synchronous fetch method.
    public String fetchData(String symbol, String interval) {
        // In a complete implementation, use config.getHttpClient() to call:
        // e.g., BASE_URL + "function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=" + interval + "&apikey=" + config.getKey();
        // Parse the JSON response and return appropriate data.
        // For demonstration purposes, return a dummy JSON string.
        return "{\"symbol\": \"" + symbol + "\", \"interval\": \"" + interval + "\", \"data\": [] }";
    }
} 