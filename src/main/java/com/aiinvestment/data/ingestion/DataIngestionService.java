package com.aiinvestment.data.ingestion;

import com.aiinvestment.api.FinnhubClient;
import com.aiinvestment.api.FinnhubPeersClient;
import com.aiinvestment.data.storage.Neo4jStorage;
import com.aiinvestment.data.storage.PostgreSQLStorage;
import com.aiinvestment.model.MarketData;
import com.aiinvestment.model.StockDataEntity;
import org.springframework.stereotype.Service;

@Service
public class DataIngestionService {
    private final FinnhubClient finnhubClient;
    private final PostgreSQLStorage postgresStorage;
    private final Neo4jStorage neo4jStorage;
    private final FinnhubPeersClient finnhubPeersClient;

    public DataIngestionService(FinnhubClient finnhubClient,
                                PostgreSQLStorage postgresStorage,
                                Neo4jStorage neo4jStorage,
                                FinnhubPeersClient finnhubPeersClient) {
        this.finnhubClient = finnhubClient;
        this.postgresStorage = postgresStorage;
        this.neo4jStorage = neo4jStorage;
        this.finnhubPeersClient = finnhubPeersClient;
    }

    public MarketData ingestData(String symbol) {
        MarketData data = finnhubClient.fetchStockData(symbol);
        if (data != null) {
            // Persist to Postgre
            StockDataEntity entity = new StockDataEntity();
            entity.setSymbol(data.getSymbol());
            entity.setPrice(data.getPrice());
            entity.setTimestamp(data.getTimestamp());
            postgresStorage.save(entity);

            // Fetch peer information
            var peers = finnhubPeersClient.fetchPeers(symbol);
            neo4jStorage.saveWithPeers(data, peers);
        }
        return data;
    }
}