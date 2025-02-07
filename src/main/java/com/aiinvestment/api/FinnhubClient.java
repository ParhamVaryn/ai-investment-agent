package com.aiinvestment.api;

import com.aiinvestment.model.MarketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Component
@Slf4j
public class FinnhubClient {

    @Value("${finnhub.api.token}")
    private String token;

    private final RestTemplate restTemplate;

    public FinnhubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MarketData fetchStockData(String symbol) {
        String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, token);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("c") == null) {
                log.warn("No quote data returned for symbol {}", symbol);
                return null;
            }

            MarketData data = new MarketData();
            data.setSymbol(symbol);
            data.setPrice(Double.parseDouble(response.get("c").toString()));
            long epochSeconds = Long.parseLong(response.get("t").toString());
            data.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault()));

            log.info("Successfully fetched Finnhub data for {}: price={}, timestamp={}",
                    symbol, data.getPrice(), data.getTimestamp());
            return data;
        } catch (Exception e) {
            log.error("Error fetching Finnhub data for symbol {}: {}", symbol, e.getMessage(), e);
            return null;
        }
    }
}