package ar.edu.udecy.web.inventory.service.impl;

import ar.edu.udecy.web.inventory.config.CopyNonNullConfig;
import ar.edu.udecy.web.inventory.dto.InventoryMovementDTO;
import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import ar.edu.udecy.web.inventory.entity.InventoryMovementEntity;
import ar.edu.udecy.web.inventory.entity.ProductEntity;
import ar.edu.udecy.web.inventory.handler.exception.NegativeQuantityException;
import ar.edu.udecy.web.inventory.handler.exception.ProductAlreadyExistsException;
import ar.edu.udecy.web.inventory.handler.exception.ResourceNotFoundException;
import ar.edu.udecy.web.inventory.handler.exception.StockNotFoundException;
import ar.edu.udecy.web.inventory.repository.CurrentStockRepository;
import ar.edu.udecy.web.inventory.repository.InventoryMovementRepository;
import ar.edu.udecy.web.inventory.repository.ProductRepository;
import ar.edu.udecy.web.inventory.service.InventoryMovementService;
import ch.qos.logback.core.joran.conditional.IfAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;
    @Autowired
    private CurrentStockRepository currentStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<InventoryMovementDTO> findAll() {
        return inventoryMovementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryMovementDTO findById(String movementId) {
        InventoryMovementEntity entity = inventoryMovementRepository.findById(movementId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory movement not found with ID: " + movementId));
        return convertToDTO(entity);
    }

    @Override
    public InventoryMovementDTO save(InventoryMovementDTO inventoryMovementDTO) {
        validateProductDTO(inventoryMovementDTO);
        if (inventoryMovementDTO.getQuantity() < 0) {
            throw new NegativeQuantityException("Quantity cannot be negative");
        }
        inventoryMovementRepository.findById(inventoryMovementDTO.getMovementId())
                .ifPresent(existing -> {
                    throw new ProductAlreadyExistsException("Inventory movement with ID " + inventoryMovementDTO.getMovementId() + " already exists");
                });

        if  (inventoryMovementDTO.getProductId() == null) {
            throw new ResourceNotFoundException("Product information is missing in the request.");
        }


        ProductEntity product = findProductById(inventoryMovementDTO.getProductId());
        inventoryMovementDTO.setDate(Objects.isNull(inventoryMovementDTO.getDate())? LocalDateTime.now() : inventoryMovementDTO.getDate());
        deductFromCurrentStockPost(null ,product, inventoryMovementDTO.getQuantity(),inventoryMovementDTO.getMovementType());
        InventoryMovementEntity entity = convertToEntity(inventoryMovementDTO, product);
        InventoryMovementEntity savedEntity = inventoryMovementRepository.save(entity);

        return convertToDTO(savedEntity);
    }

    @Override
    public InventoryMovementDTO update(String movementId, InventoryMovementDTO inventoryMovementDTO) {
        InventoryMovementEntity existingInventory = inventoryMovementRepository.findById(movementId)
                .orElseThrow(() -> new ResourceNotFoundException("Movement with ID " + movementId + " not found"));

        validateProductDTO(inventoryMovementDTO);

        if (inventoryMovementDTO.getQuantity() < 0) {
            throw new NegativeQuantityException("Quantity cannot be negative");
        }

        ProductEntity product = findProductById(inventoryMovementDTO.getProductId());
        // Deduct quantity from current stock if quantity is updated
        if (inventoryMovementDTO.getQuantity() >= 0) {

            if (Objects.isNull(inventoryMovementDTO.getMovementId())) {
                deductFromCurrentStockPut(existingInventory, product, inventoryMovementDTO.getQuantity(), inventoryMovementDTO.getMovementType());
            } else {
                deductFromCurrentStockPost(existingInventory, product, inventoryMovementDTO.getQuantity(), inventoryMovementDTO.getMovementType());
            }
        }
        inventoryMovementDTO.setDate(LocalDateTime.now());
        CopyNonNullConfig.copyNonNullProperties(inventoryMovementDTO, existingInventory);

        InventoryMovementEntity updatedEntity = inventoryMovementRepository.save(existingInventory);
        return convertToDTO(updatedEntity);
    }

    @Override
    public void deleteById(String movementId) {
        if (!inventoryMovementRepository.existsById(movementId)) {
            throw new ResourceNotFoundException("Inventory movement not found with ID: " + movementId);
        }
        inventoryMovementRepository.deleteById(movementId);
    }

    private InventoryMovementDTO convertToDTO(InventoryMovementEntity entity) {
        return new InventoryMovementDTO(
                entity.getMovementId(),
                entity.getDate(),
                entity.getProductId(),
                entity.getMovementType(),
                entity.getQuantity(),
                entity.getOrderId(),
                entity.getNotes()
        );
    }

    private InventoryMovementEntity convertToEntity(InventoryMovementDTO dto, ProductEntity product) {
        return InventoryMovementEntity.builder()
                .movementId(dto.getMovementId())
                .date(dto.getDate())
                .productId(product.getProductId())
                .movementType(dto.getMovementType())
                .quantity(dto.getQuantity())
                .orderId(dto.getOrderId())
                .notes(dto.getNotes())
                .build();
    }

    private ProductEntity findProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    private void validateProductDTO(InventoryMovementDTO inventoryMovementDTO) {
        if (inventoryMovementDTO.getProductId() == null) {
            throw new ResourceNotFoundException("Product information is missing in the request.");
        }
    }

    private void deductFromCurrentStockPost(InventoryMovementEntity existingInventory, ProductEntity productEntity, int quantity, String movementType) {
        CurrentStockEntity currentStock = currentStockRepository.findByProduct_ProductId(productEntity.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Current stock not found for product ID: " + productEntity.getProductId()));

        switch (movementType.toUpperCase()) {
            case "OUTBOUND":
                if (currentStock.getQuantity() < quantity) {
                    throw new StockNotFoundException("Insufficient stock for product ID: " + productEntity.getProductId());
                }
                currentStock.setQuantity(currentStock.getQuantity() - quantity);
                break;

            case "INBOUND":
                currentStock.setQuantity(currentStock.getQuantity() + quantity);
                break;

            case "UPDATE":
                currentStock.setQuantity(currentStock.getQuantity() + existingInventory.getQuantity() - quantity);
                break;

            default:
                throw new StockNotFoundException("Invalid movement type: " + movementType);
        }

        currentStock.setTotalInventoryCost(currentStock.getTotalInventoryCost().add(
                BigDecimal.valueOf(quantity).multiply(productEntity.getSalePrice())));
        currentStock.setLastUpdated(LocalDateTime.now());
        currentStockRepository.save(currentStock);
    }
    private void deductFromCurrentStockPut(InventoryMovementEntity existingInventory, ProductEntity productEntity, int quantity, String movementType) {
        CurrentStockEntity currentStock = currentStockRepository.findByProduct_ProductId(productEntity.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Current stock not found for product ID: " + productEntity.getProductId()));
        switch (movementType.toUpperCase()) {
            case "OUTBOUND":
                if (currentStock.getQuantity() < quantity) {
                    throw new StockNotFoundException("Insufficient stock for product ID: " + productEntity.getProductId());
                }
                currentStock.setQuantity(currentStock.getQuantity() + existingInventory.getQuantity() - quantity
                );
                break;

            case "INBOUND":
                currentStock.setQuantity(currentStock.getQuantity() - existingInventory.getQuantity() + quantity);
                break;

            case "UPDATE":
                currentStock.setQuantity(currentStock.getQuantity() + existingInventory.getQuantity() - quantity);
                break;

            default:
                throw new StockNotFoundException("Invalid movement type: " + movementType);
        }

        currentStock.setTotalInventoryCost( BigDecimal.valueOf(quantity).multiply(productEntity.getSalePrice()));
        currentStock.setLastUpdated(LocalDateTime.now());
        currentStockRepository.save(currentStock);
    }
}