package ar.edu.udecy.web.inventory.service.impl;

import ar.edu.udecy.web.inventory.config.CopyNonNullConfig;
import ar.edu.udecy.web.inventory.dto.CurrentStockDTO;
import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import ar.edu.udecy.web.inventory.entity.ProductEntity;
import ar.edu.udecy.web.inventory.handler.exception.NegativeQuantityException;
import ar.edu.udecy.web.inventory.handler.exception.ResourceNotFoundException;
import ar.edu.udecy.web.inventory.repository.CurrentStockRepository;
import ar.edu.udecy.web.inventory.repository.ProductRepository;
import ar.edu.udecy.web.inventory.service.CurrentStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrentStockServiceImpl implements CurrentStockService {

    @Autowired
    private CurrentStockRepository currentStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<CurrentStockDTO> findAll() {
        return currentStockRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CurrentStockDTO findById(Long productId) {
        CurrentStockEntity entity = currentStockRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Current stock not found for product ID: " + productId));
        return convertToDTO(entity);
    }

    @Override
    public CurrentStockDTO save(CurrentStockDTO currentStockDTO) {
        validateQuantity(currentStockDTO.getQuantity());

        ProductEntity product = productRepository.findById(currentStockDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + currentStockDTO.getProductId()));

        CurrentStockEntity entity = new CurrentStockEntity();
        entity.setProduct(product);
        entity.setQuantity(currentStockDTO.getQuantity());
        entity.setLastUpdated(LocalDateTime.now());
        entity.setTotalInventoryCost(calculateInventoryCost(currentStockDTO.getQuantity(), product.getSalePrice()));

        CurrentStockEntity savedEntity = currentStockRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    public CurrentStockDTO update(Long productId, CurrentStockDTO currentStockDTO) {
        validateQuantity(currentStockDTO.getQuantity());

        CurrentStockEntity entity = currentStockRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Current stock not found for product ID: " + productId));

        ProductEntity product = productRepository.findById(currentStockDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + currentStockDTO.getProductId()));

        currentStockDTO.setTotalInventoryCost(calculateInventoryCost(currentStockDTO.getQuantity(), product.getSalePrice()));
        currentStockDTO.setLastUpdated(LocalDateTime.now());
        CopyNonNullConfig.copyNonNullProperties(currentStockDTO, entity);

        CurrentStockEntity updatedEntity = currentStockRepository.save(entity);
        return convertToDTO(updatedEntity);
    }

    @Override
    public void deleteById(Long productId) {
        CurrentStockEntity entity = currentStockRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Current stock not found for product ID: " + productId));
        currentStockRepository.delete(entity);
    }

    private CurrentStockDTO convertToDTO(CurrentStockEntity entity) {
        return new CurrentStockDTO(
                entity.getId(),
                entity.getProduct().getProductId(),
                entity.getQuantity(),
                entity.getLastUpdated(),
                entity.getTotalInventoryCost());
    }

    private void validateQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new NegativeQuantityException("Quantity cannot be negative");
        }
    }

    private BigDecimal calculateInventoryCost(Integer quantity, BigDecimal salePrice) {
        return BigDecimal.valueOf(quantity).multiply(salePrice);
    }
}