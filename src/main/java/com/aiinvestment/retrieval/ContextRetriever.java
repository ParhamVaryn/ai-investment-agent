package com.aiinvestment.retrieval;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ContextRetriever {
    private final Driver neo4jDriver;

    public ContextRetriever(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public List<String> retrieveContext(String symbol) {
        try (Session session = neo4jDriver.session()) {
            return session.run(
                    "MATCH (s:Stock {symbol: $symbol})-[:CORRELATED_WITH]->(c) RETURN c.symbol",
                    Map.of("symbol", symbol)
            ).list(record -> record.get("c.symbol").asString());
        }
    }
}
