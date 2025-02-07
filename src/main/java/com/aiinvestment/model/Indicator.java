package com.aiinvestment.model;

import lombok.Data;

@Data
public class Indicator {
    private String name;
    private double value;

    public Indicator(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString(){
        return String.format("%s: %.2f", name, value);
    }
}
