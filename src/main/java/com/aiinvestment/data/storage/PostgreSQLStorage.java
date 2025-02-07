package com.aiinvestment.data.storage;

import com.aiinvestment.model.StockDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgreSQLStorage extends JpaRepository<StockDataEntity, Long> {
}
