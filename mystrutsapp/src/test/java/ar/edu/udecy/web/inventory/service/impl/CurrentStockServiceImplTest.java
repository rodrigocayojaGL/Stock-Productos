package ar.edu.udecy.web.inventory.service.impl;

import ar.edu.udecy.web.inventory.dto.CurrentStockDTO;
import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import ar.edu.udecy.web.inventory.entity.ProductEntity;
import ar.edu.udecy.web.inventory.handler.exception.NegativeQuantityException;
import ar.edu.udecy.web.inventory.handler.exception.ResourceNotFoundException;
import ar.edu.udecy.web.inventory.repository.CurrentStockRepository;
import ar.edu.udecy.web.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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
class CurrentStockServiceTest {

    @Mock
    private CurrentStockRepository currentStockRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CurrentStockServiceImpl currentStockService;

    private ProductEntity product;
    private CurrentStockEntity currentStockEntity;

    @BeforeEach
    void setUp() {
        product = new ProductEntity();
        product.setProductId("1L");
        product.setSalePrice(BigDecimal.valueOf(50));

        currentStockEntity = new CurrentStockEntity();
        currentStockEntity.setId(1L);
        currentStockEntity.setProduct(product);
        currentStockEntity.setQuantity(10);
        currentStockEntity.setLastUpdated(LocalDateTime.now());
        currentStockEntity.setTotalInventoryCost(BigDecimal.valueOf(500));
    }

    @Test
    void testFindById_Success() {
        when(currentStockRepository.findById(1L)).thenReturn(Optional.of(currentStockEntity));

        CurrentStockDTO result = currentStockService.findById(1L);

        assertNotNull(result);
        assertEquals("1L", result.getProductId());
        assertEquals(10, result.getQuantity());
        assertEquals(BigDecimal.valueOf(500), result.getTotalInventoryCost());
    }

    @Test
    void testFindById_ThrowsResourceNotFoundException() {
        when(currentStockRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currentStockService.findById(2L));
    }

    @Test
    void testSave_Success() {
        CurrentStockDTO dto = new CurrentStockDTO(null, "1L", 10, LocalDateTime.now(), BigDecimal.ZERO);
        when(productRepository.findById("1L")).thenReturn(Optional.of(product));
        when(currentStockRepository.save(any(CurrentStockEntity.class))).thenReturn(currentStockEntity);

        CurrentStockDTO result = currentStockService.save(dto);

        assertNotNull(result);
        assertEquals(10, result.getQuantity());
        assertEquals(BigDecimal.valueOf(500), result.getTotalInventoryCost());
    }

    @Test
    void testSave_ThrowsNegativeQuantityException() {
        CurrentStockDTO dto = new CurrentStockDTO(null, "1L", -5, LocalDateTime.now(), BigDecimal.ZERO);

        assertThrows(NegativeQuantityException.class, () -> currentStockService.save(dto));
    }

    @Test
    void testUpdate_Success() {
        CurrentStockDTO dto = new CurrentStockDTO(1L, "1L", 20, LocalDateTime.now(), BigDecimal.ZERO);
        when(currentStockRepository.findById(1L)).thenReturn(Optional.of(currentStockEntity));
        when(productRepository.findById("1L")).thenReturn(Optional.of(product));
        when(currentStockRepository.save(any(CurrentStockEntity.class))).thenReturn(currentStockEntity);

        CurrentStockDTO result = currentStockService.update(1L, dto);

        assertNotNull(result);
        assertEquals(20, result.getQuantity());
        assertEquals(BigDecimal.valueOf(1000), result.getTotalInventoryCost());
    }

    @Test
    void testUpdate_ThrowsResourceNotFoundException() {
        CurrentStockDTO dto = new CurrentStockDTO(99L, "1L", 10, LocalDateTime.now(), BigDecimal.ZERO);
        when(currentStockRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currentStockService.update(99L, dto));
    }

    @Test
    void testDeleteById_Success() {
        when(currentStockRepository.findById(1L)).thenReturn(Optional.of(currentStockEntity));

        currentStockService.deleteById(1L);

        verify(currentStockRepository, times(1)).delete(currentStockEntity);
    }

    @Test
    void testDeleteById_ThrowsResourceNotFoundException() {
        when(currentStockRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currentStockService.deleteById(2L));
    }
}
