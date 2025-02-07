package com.aiinvestment.controller;

import com.aiinvestment.data.ingestion.DataIngestionService;
import com.aiinvestment.data.processing.IndicatorProcessor;
import com.aiinvestment.engine.DecisionEngine;
import com.aiinvestment.model.MarketData;
import com.aiinvestment.model.Recommendation;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class AgentController {
    private final DecisionEngine decisionEngine;
    private final DataIngestionService ingestionService;
    private final IndicatorProcessor indicatorProcessor;

    // thread pool for concurrncy
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public AgentController(DecisionEngine decisionEngine,
                           DataIngestionService ingestionService,
                           IndicatorProcessor indicatorProcessor) {
        this.decisionEngine = decisionEngine;
        this.ingestionService = ingestionService;
        this.indicatorProcessor = indicatorProcessor;
    }

    @GetMapping("/analyze/{symbol}")
    public Recommendation analyzeStock(@PathVariable String symbol) {
        // Fetching asynchronously
        CompletableFuture<MarketData> futureData =
                CompletableFuture.supplyAsync(() -> ingestionService.ingestData(symbol), executorService);
        MarketData data = futureData.join();
        if (data == null) {
            return new Recommendation("HOLD", "Failed to fetch market data.");
        }
        var indicators = indicatorProcessor.computeIndicators(data);
        return decisionEngine.generateRecommendation(symbol, data, indicators);
    }
}
