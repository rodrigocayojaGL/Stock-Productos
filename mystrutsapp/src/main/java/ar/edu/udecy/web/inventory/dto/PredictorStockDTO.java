package ar.edu.udecy.web.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictorStockDTO {
    private Long id;
    private LocalDateTime date;
    private String productId;
    private Integer unitsSold;
    private BigDecimal avgSalePrice;
    private boolean promotionActive;
    private String specialEvent;

}