package com.aiinvestment.data.storage;

import com.aiinvestment.model.MarketData;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.List;

@Repository
public class Neo4jStorage {
    private final Driver driver;

    public Neo4jStorage(Driver driver) {
        this.driver = driver;
    }

    public void saveWithPeers(MarketData data, List<String> peers) {
        try (Session session = driver.session()) {
            session.run(
                    "MERGE (s:Stock {symbol: $symbol}) " +
                            "SET s.price = $price, s.timestamp = $timestamp " +
                            "WITH s " +
                            "UNWIND $peers as peerSymbol " +
                            "MERGE (p:Stock {symbol: peerSymbol}) " +
                            "MERGE (s)-[:PEER_OF]->(p)",
                    Map.of(
                            "symbol", data.getSymbol(),
                            "price", data.getPrice(),
                            "timestamp", data.getTimestamp().toString(),
                            "peers", peers
                    )
            );
        }
    }

    public Double getPeerAverage(String symbol) {
        try (Session session = driver.session()) {
            var result = session.run(
                    "MATCH (s:Stock {symbol: $symbol})-[:PEER_OF]->(p:Stock) " +
                            "WHERE p.price IS NOT NULL " +
                            "RETURN avg(p.price) as avgPrice",
                    Map.of("symbol", symbol)
            );
            if (result.hasNext()){
                return result.next().get("avgPrice").asDouble();
            }
            return null;
        }
    }
}