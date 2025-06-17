package ar.edu.udecy.web.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentStockDTO {

    private Long id;
    private String productId;
    private Integer quantity;
    private LocalDateTime lastUpdated;
    private BigDecimal totalInventoryCost;
}