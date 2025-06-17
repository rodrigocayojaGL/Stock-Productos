package ar.edu.udecy.web.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "current_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity" )
    private Integer quantity;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "total_inventory_cost")
    private BigDecimal totalInventoryCost;

}