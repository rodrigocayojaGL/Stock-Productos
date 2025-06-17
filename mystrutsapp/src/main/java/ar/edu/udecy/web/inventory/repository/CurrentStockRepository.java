package ar.edu.udecy.web.inventory.repository;

import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CurrentStockRepository extends JpaRepository<CurrentStockEntity, Long> {
    Optional<CurrentStockEntity> findByProduct_ProductId(String productId);
    Optional<CurrentStockEntity> findByLastUpdated(LocalDateTime lastUpdated);
}