package com.aiinvestment.data.processing;

import com.aiinvestment.model.Indicator;
import com.aiinvestment.model.MarketData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndicatorProcessor {
    public List<Indicator> computeIndicators(MarketData data) {
        //removed sample RSI & MACD
        return List.of();
    }
}
