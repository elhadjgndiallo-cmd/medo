package com.medo.api.inventaire.dao;

import com.medo.api.inventaire.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByVarianteId(UUID varianteId);

    @Query("SELECT s FROM Stock s WHERE s.quantiteTotale <= 0")
    List<Stock> findRuptures();

    @Query("SELECT s FROM Stock s WHERE s.quantiteTotale > 0 AND s.quantiteTotale <= s.seuilMin")
    List<Stock> findStocksBas();

    @Query("SELECT COUNT(s) FROM Stock s WHERE s.quantiteTotale <= 0")
    long countRuptures();

    @Query("SELECT COUNT(s) FROM Stock s WHERE s.quantiteTotale > 0 AND s.quantiteTotale <= s.seuilMin")
    long countStocksBas();
}
