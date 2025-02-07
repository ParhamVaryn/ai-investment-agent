package com.aiinvestment.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class FinnhubPeersClient {
    
    @Value("${finnhub.api.token}")
    private String token;

    private final RestTemplate restTemplate;

    public FinnhubPeersClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public List<String> fetchPeers(String symbol) {
        String url = String.format("https://finnhub.io/api/v1/stock/peers?symbol=%s&token=%s", symbol, token);
        try {
            @SuppressWarnings("unchecked")
            List<String> peers = restTemplate.getForObject(url, List.class);
            return peers;
        } catch(Exception e) {
            log.error("Error fetching peers for symbol {}: {}", symbol, e.getMessage(), e);
            return List.of();
        }
    }
} 