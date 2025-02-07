package com.aiinvestment.api;

import com.aiinvestment.model.MarketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@Slf4j
public class AlphaVantageClient {
    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AlphaVantageClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public MarketData fetchStockData(String symbol) {
        String url = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=%s",
                symbol, apiKey
        );
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response.containsKey("Error Message")) {
                log.error("Alpha Vantage API error: {}", response.get("Error Message"));
                return null;
            }

            Map<String, Map<String, String>> timeSeries = 
                    (Map<String, Map<String, String>>) response.get("Time Series (5min)");
            
            if (timeSeries == null || timeSeries.isEmpty()) {
                log.warn("No time series data returned for symbol {}", symbol);
                return null;
            }

            String latestTimestamp = timeSeries.keySet().iterator().next();
            Map<String, String> latestData = timeSeries.get(latestTimestamp);

            MarketData data = new MarketData();
            data.setSymbol(symbol);
            data.setPrice(parseDouble(latestData.get("4. close")));
            data.setVolume(parseDouble(latestData.get("5. volume")));
            data.setTimestamp(LocalDateTime.parse(latestTimestamp, DATE_TIME_FORMATTER));

            log.info("Successfully fetched data for {}: price=${}, volume={}", 
                    symbol, data.getPrice(), data.getVolume());
            return data;

        } catch (Exception e) {
            log.error("Error fetching stock data for symbol {}: {}", symbol, e.getMessage(), e);
            return null;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse value: {}", value);
            return 0.0;
        }
    }
}
