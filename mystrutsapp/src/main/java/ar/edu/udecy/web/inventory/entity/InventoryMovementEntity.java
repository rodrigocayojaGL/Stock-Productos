package ar.edu.udecy.web.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class InventoryMovementEntity {
    @Id
    @Column(name = "movement_id", nullable = false)
    private String movementId;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "movement_type")
    private String movementType;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "notes")
    private String notes;
}