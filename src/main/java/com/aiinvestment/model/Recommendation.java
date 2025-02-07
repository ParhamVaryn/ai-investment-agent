package com.aiinvestment.model;

import lombok.Data;

@Data
public class Recommendation {
    private String action;
    private String reasoning;

    public Recommendation(String action, String reasoning) {
        this.action = action;
        this.reasoning = reasoning;
    }
}
