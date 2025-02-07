package com.aiinvestment.engine;

import com.aiinvestment.api.FinnhubFinancialsClient;
import com.aiinvestment.api.FinnhubNewsClient;
import com.aiinvestment.api.OllamaClient;
import com.aiinvestment.model.Indicator;
import com.aiinvestment.model.MarketData;
import com.aiinvestment.model.Recommendation;
import com.aiinvestment.retrieval.ContextRetriever;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DecisionEngine {

    private final OllamaClient ollamaClient;
    private final ContextRetriever contextRetriever;
    private final FinnhubNewsClient newsClient;
    private final FinnhubFinancialsClient financialsClient;

    // Spring will auto-wire the necessary components
    public DecisionEngine(ContextRetriever contextRetriever,
                          FinnhubNewsClient newsClient,
                          FinnhubFinancialsClient financialsClient) {
        this.ollamaClient = new OllamaClient();
        this.contextRetriever = contextRetriever;
        this.newsClient = newsClient;
        this.financialsClient = financialsClient;
    }

    public Recommendation generateRecommendation(String symbol, MarketData data, List<Indicator> indicators) {
        List<String> context = contextRetriever.retrieveContext(symbol);
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        List<Map<String, Object>> news = newsClient.fetchCompanyNews(symbol, weekAgo, today);
        Map<String, Object> financials = financialsClient.fetchBasicFinancials(symbol);

        String promptStr = PromptBuilder.buildPrompt(symbol, data, indicators, context, news, financials);
        try {
            String answer = ollamaClient.getAggregatedCompletion(promptStr);
            return new Recommendation("LLAMA_RESPONSE", answer);
        } catch (Exception e) {
            return new Recommendation("HOLD", "Error calling local LLM: " + e.getMessage());
        }
    }
}