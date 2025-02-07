package com.aiinvestment.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MarketData {
    private String symbol;
    private double price;
    private double volume;
    private LocalDateTime timestamp;
}
