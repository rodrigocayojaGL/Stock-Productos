package ar.edu.udecy.web.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "predictor_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PredictorStockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "units_sold")
    private Integer unitsSold;

    @Column(name = "avg_sale_price")
    private BigDecimal avgSalePrice;

    @Column(name = "promotion_active")
    private boolean promotionActive;

    @Column(name = "special_event")
    private String specialEvent;



}