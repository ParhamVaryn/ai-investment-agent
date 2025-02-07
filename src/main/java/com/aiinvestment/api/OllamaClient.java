package com.aiinvestment.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OllamaClient {
    private static final String OLLAMA_URL = System.getProperty("ollama.endpoint.url", "http://localhost:11434/api/generate");
    private final HttpClient client;

    public OllamaClient() {
        this.client = HttpClient.newHttpClient();
    }

    /**
     * Sends a prompt to the Ollama API and aggregates the streaming response.
     *
     * @param prompt the prompt text
     * @return the full aggregated response
     * @throws Exception if an error occurs
     */
    public String getAggregatedCompletion(String prompt) throws Exception {
        JsonObject payload = new JsonObject();
        payload.addProperty("model", "llama3.2");
        payload.addProperty("prompt", prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<Stream<String>> response = client.send(request, HttpResponse.BodyHandlers.ofLines());
        StringBuilder aggregated = new StringBuilder();

        try (Stream<String> lines = response.body()) {
            for (String line : (Iterable<String>) lines::iterator) {
                if (line == null || line.isBlank()) continue;
                JsonObject jsonLine = JsonParser.parseString(line).getAsJsonObject();
                String fragment = jsonLine.has("response") ? jsonLine.get("response").getAsString() : "";
                aggregated.append(fragment);
                if (jsonLine.has("done") && jsonLine.get("done").getAsBoolean()) {
                    break;
                }
            }
        }
        return aggregated.toString().trim();
    }
}