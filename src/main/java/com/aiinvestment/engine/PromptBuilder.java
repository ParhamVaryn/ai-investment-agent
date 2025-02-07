package com.aiinvestment.engine;

import com.aiinvestment.model.MarketData;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

public class PromptBuilder {
    public static String buildPrompt(String symbol,
                                     MarketData data,
                                     List<?> indicators,
                                     List<String> context,
                                     List<Map<String, Object>> news,
                                     Map<String, Object> financials) {
        Gson gson = new Gson();

        List<Map<String, Object>> latestNews = news.stream()
            .sorted((a, b) -> {
                long dtA = Long.parseLong(a.get("datetime").toString());
                long dtB = Long.parseLong(b.get("datetime").toString());
                return Long.compare(dtB, dtA);
            })
            .limit(3)
            .collect(Collectors.toList());

        Map<String, Object> filteredFinancials = new LinkedHashMap<>();
        if(financials.containsKey("metric")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> metricInfo = (Map<String, Object>) financials.get("metric");
            filteredFinancials.put("52WeekHigh", metricInfo.get("52WeekHigh"));
            filteredFinancials.put("52WeekLow", metricInfo.get("52WeekLow"));
            filteredFinancials.put("10DayAverageTradingVolume", metricInfo.get("10DayAverageTradingVolume"));
            filteredFinancials.put("currentRatio", metricInfo.get("currentRatio"));
            filteredFinancials.put("salesPerShare", metricInfo.get("salesPerShare"));
            filteredFinancials.put("netMargin", metricInfo.get("netMargin"));
            filteredFinancials.put("52WeekPriceReturnDaily", metricInfo.get("52WeekPriceReturnDaily"));
        } else {
            filteredFinancials = financials;
        }

        String marketDataJson = String.format("{\"Price\": %.2f, \"Volume\": %.2f, \"Timestamp\": \"%s\"}",
                data.getPrice(), data.getVolume(), data.getTimestamp().toString());
        String newsJson = gson.toJson(latestNews);
        String financialJson = gson.toJson(filteredFinancials);
        String contextJson = gson.toJson(context);

        return String.format("Below is the factual data provided for stock %s:\n" +
                        "Market Data: %s\n" +
                        "Company News (raw): %s\n" +
                        "Financial Metrics (raw): %s\n" +
                        "Context: %s\n\n" +
                        "IMPORTANT: Please echo back EXACTLY the JSON data provided above as plain text.\n" +
                        "For 'Market Data' and 'Financial Metrics', convert the JSON into a list where each key-value pair is on its own separate line in the format: Key: Value (do not include curly braces or commas).\n" +
                        "For 'Company News (raw)', display each news article with each field on a separate line (for example, 'Category: ...', 'Datetime: ...', etc.).\n" +
                        "Ensure that the output is properly encoded so that special characters (such as apostrophes) appear correctly (for example, use ' instead of â€™) and do not include extraneous escape sequences like \\n or \\u003C.\n" +
                        "Then, provide your BUY/SELL/HOLD recommendation with thorough reasoning in plain text, dividing your analysis into sections for: Market Data Analysis, Company News Analysis, and Financial Metrics Analysis.\n",
                symbol, marketDataJson, newsJson, financialJson, contextJson);
    }
}