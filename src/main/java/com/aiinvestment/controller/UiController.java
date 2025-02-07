package com.aiinvestment.controller;

import com.aiinvestment.data.ingestion.DataIngestionService;
import com.aiinvestment.data.processing.IndicatorProcessor;
import com.aiinvestment.engine.DecisionEngine;
import com.aiinvestment.model.MarketData;
import com.aiinvestment.model.Recommendation;
import com.aiinvestment.model.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ui")
public class UiController {

    private final DataIngestionService dataIngestionService;
    private final DecisionEngine decisionEngine;
    private final IndicatorProcessor indicatorProcessor;

    @Autowired
    public UiController(DataIngestionService dataIngestionService,
                        DecisionEngine decisionEngine,
                        IndicatorProcessor indicatorProcessor) {
        this.dataIngestionService = dataIngestionService;
        this.decisionEngine = decisionEngine;
        this.indicatorProcessor = indicatorProcessor;
    }

    @GetMapping("/analyze")
    public ResponseEntity<String> analyze(@RequestParam("symbol") String symbol) {
        MarketData data = dataIngestionService.ingestData(symbol);
        List<Indicator> indicators = indicatorProcessor.computeIndicators(data);
        Recommendation recommendation = decisionEngine.generateRecommendation(symbol, data, indicators);

        String html = "<html><head><meta charset=\"UTF-8\"><title>Analysis for " + symbol + "</title></head><body>" +
                      "<h1>Analysis for " + symbol + "</h1>" +
                      "<h2>Action: " + recommendation.getAction() + "</h2>" +
                      "<pre style=\"white-space: pre-wrap; font-family: monospace;\">" + 
                      escapeHtml(recommendation.getReasoning()) + 
                      "</pre>" +
                      "</body></html>";
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                .body(html);
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }

    @GetMapping("")
    public ResponseEntity<String> showForm() {
        String html = "<html>" +
                      "<head><meta charset=\"UTF-8\"><title>Stock Analysis Form</title></head>" +
                      "<body>" +
                      "<h1>Stock Analysis</h1>" +
                      "<form action=\"/ui/analyze\" method=\"get\">" +
                      "Stock Symbol: <input type=\"text\" name=\"symbol\" placeholder=\"e.g., AAPL\" />" +
                      "<button type=\"submit\">Analyze</button>" +
                      "</form>" +
                      "</body>" +
                      "</html>";
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                .body(html);
    }
} 