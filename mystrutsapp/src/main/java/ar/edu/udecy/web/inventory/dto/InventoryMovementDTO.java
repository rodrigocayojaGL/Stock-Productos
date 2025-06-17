package ar.edu.udecy.web.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMovementDTO {
    private String movementId;
    private LocalDateTime date;
    private String productId;
    private String movementType;
    private int quantity;
    private String orderId;
    private String notes;

}