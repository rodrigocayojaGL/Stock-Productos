package ar.edu.udecy.web.inventory.service.impl;
import static org.mockito.ArgumentMatchers.any;
import ar.edu.udecy.web.inventory.dto.InventoryMovementDTO;
import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import ar.edu.udecy.web.inventory.entity.InventoryMovementEntity;
import ar.edu.udecy.web.inventory.entity.ProductEntity;
import ar.edu.udecy.web.inventory.handler.exception.NegativeQuantityException;
import ar.edu.udecy.web.inventory.handler.exception.ProductAlreadyExistsException;
import ar.edu.udecy.web.inventory.handler.exception.ResourceNotFoundException;
import ar.edu.udecy.web.inventory.repository.CurrentStockRepository;
import ar.edu.udecy.web.inventory.repository.InventoryMovementRepository;
import ar.edu.udecy.web.inventory.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryMovementServiceImplTest {

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CurrentStockRepository currentStockRepository;
    @InjectMocks
    private InventoryMovementServiceImpl inventoryMovementService;

    @Test
    void testFindById_Success() {
        String movementId = "1";
        InventoryMovementEntity entity = new InventoryMovementEntity();
        entity.setMovementId(movementId);
        when(inventoryMovementRepository.findById(movementId)).thenReturn(Optional.of(entity));

        InventoryMovementDTO result = inventoryMovementService.findById(movementId);
        assertEquals(movementId, result.getMovementId());
    }

    @Test
    void testFindById_NotFound() {
        when(inventoryMovementRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> inventoryMovementService.findById("99"));
    }

    @Test
    void testSave_NegativeQuantity() {
        InventoryMovementDTO dto = new InventoryMovementDTO("1", LocalDateTime.now(), "P001", "INBOUND", -5, "O001", "Test");
        assertThrows(NegativeQuantityException.class, () -> inventoryMovementService.save(dto));
    }

    @Test
    void testSave_ProductAlreadyExists() {
        InventoryMovementDTO dto = new InventoryMovementDTO("1", LocalDateTime.now(), "P001", "INBOUND", 5, "O001", "Test");
        when(inventoryMovementRepository.findById("1")).thenReturn(Optional.of(new InventoryMovementEntity()));

        assertThrows(ProductAlreadyExistsException.class, () -> inventoryMovementService.save(dto));
    }
}
