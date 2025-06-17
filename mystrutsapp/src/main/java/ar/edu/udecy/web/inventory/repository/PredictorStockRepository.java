package ar.edu.udecy.web.inventory.repository;

import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import ar.edu.udecy.web.inventory.entity.PredictorStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictorStockRepository extends JpaRepository<PredictorStockEntity, Long> {
    Optional<PredictorStockEntity> findByProduct_ProductId(String productId);
}