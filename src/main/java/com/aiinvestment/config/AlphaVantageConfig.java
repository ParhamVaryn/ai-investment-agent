package com.aiinvestment.config;

import com.aiinvestment.lib.alphavantage.AlphaVantage;
import com.aiinvestment.lib.alphavantage.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class AlphaVantageConfig {

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Config cfg = Config.builder()
                .key(apiKey)
                .timeOut(10)
                .build();
        AlphaVantage.api().init(cfg);
        System.out.println("AlphaVantage API initialized with key: " + apiKey);
    }
}