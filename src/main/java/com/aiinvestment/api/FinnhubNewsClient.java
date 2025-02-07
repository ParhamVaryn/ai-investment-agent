package com.aiinvestment.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FinnhubNewsClient {

    @Value("${finnhub.api.token}")
    private String token;

    private final RestTemplate restTemplate;

    public FinnhubNewsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> fetchCompanyNews(String symbol, LocalDate fromDate, LocalDate toDate) {
        String from = fromDate.format(DateTimeFormatter.ISO_DATE);
        String to = toDate.format(DateTimeFormatter.ISO_DATE);
        String url = String.format("https://finnhub.io/api/v1/company-news?symbol=%s&from=%s&to=%s&token=%s",
                symbol, from, to, token);
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> newsList = restTemplate.getForObject(url, List.class);
            return newsList;
        } catch (Exception e) {
            log.error("Error fetching company news for symbol {}: {}", symbol, e.getMessage(), e);
            return List.of();
        }
    }
} 