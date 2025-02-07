package com.aiinvestment.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class FinnhubFinancialsClient {

    @Value("${finnhub.api.token}")
    private String token;

    private final RestTemplate restTemplate;

    public FinnhubFinancialsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchBasicFinancials(String symbol) {
        String url = String.format("https://finnhub.io/api/v1/stock/metric?symbol=%s&metric=all&token=%s", symbol, token);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return response;
        } catch (Exception e) {
            log.error("Error fetching financials for symbol {}: {}", symbol, e.getMessage(), e);
            return Map.of();
        }
    }
} 