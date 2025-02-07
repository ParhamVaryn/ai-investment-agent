package com.aiinvestment.controller;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final ExecutorService executorService;
    private final IngestionService ingestionService;
    private final IndicatorProcessor indicatorProcessor;
    private final DecisionEngine decisionEngine;

    public AgentController(ExecutorService executorService, IngestionService ingestionService, IndicatorProcessor indicatorProcessor, DecisionEngine decisionEngine) {
        this.executorService = executorService;
        this.ingestionService = ingestionService;
        this.indicatorProcessor = indicatorProcessor;
        this.decisionEngine = decisionEngine;
    }

    @GetMapping("/analyze/{symbol}")
    public Recommendation analyzeStock(@PathVariable String symbol) {
        // Chain asynchronous tasks: first fetch market data, then compute indicators and generate recommendation concurrently.
        CompletableFuture<Recommendation> recommendationFuture =
            CompletableFuture.supplyAsync(() -> ingestionService.ingestData(symbol), executorService)
                .thenApplyAsync(data -> {
                    if (data == null) {
                        return new Recommendation("HOLD", "Failed to fetch market data.");
                    }
                    // Compute indicators concurrently on the same executor.
                    var indicators = indicatorProcessor.computeIndicators(data);
                    return decisionEngine.generateRecommendation(symbol, data, indicators);
                }, executorService);
        return recommendationFuture.join();
    }
} 