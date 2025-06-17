package ar.edu.udecy.web.inventory.service.impl;

import ar.edu.udecy.web.inventory.config.CopyNonNullConfig;
import ar.edu.udecy.web.inventory.dto.InventoryMovementDTO;
import ar.edu.udecy.web.inventory.dto.PredictorStockDTO;
import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import ar.edu.udecy.web.inventory.entity.InventoryMovementEntity;
import ar.edu.udecy.web.inventory.entity.PredictorStockEntity;
import ar.edu.udecy.web.inventory.entity.ProductEntity;
import ar.edu.udecy.web.inventory.handler.exception.ProductAlreadyExistsException;
import ar.edu.udecy.web.inventory.handler.exception.ResourceNotFoundException;
import ar.edu.udecy.web.inventory.repository.CurrentStockRepository;
import ar.edu.udecy.web.inventory.repository.InventoryMovementRepository;
import ar.edu.udecy.web.inventory.repository.PredictorStockRepository;
import ar.edu.udecy.web.inventory.repository.ProductRepository;
import ar.edu.udecy.web.inventory.service.CurrentStockService;
import ar.edu.udecy.web.inventory.service.InventoryMovementService;
import ar.edu.udecy.web.inventory.service.PredictorStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PredictorStockProductServiceImpl implements PredictorStockService {

    @Autowired
    private PredictorStockRepository predictorStockRepository;

    @Autowired
    private InventoryMovementService inventoryMovementService;

    @Autowired
    private CurrentStockRepository currentStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<PredictorStockDTO> findAll() {
        return predictorStockRepository.findAll().stream()
                .map(entity -> mapToDTO(entity, null)) // Pass null for movementId
                .collect(Collectors.toList());
    }

    @Override
    public PredictorStockDTO findById(Long id) {
        return predictorStockRepository.findById(id)
                .map(entity -> mapToDTO(entity, null)) // Pass null for movementId
                .orElseThrow(() -> new ResourceNotFoundException("PredictorStock with ID " + id + " not found"));
    }

    @Override
    public PredictorStockDTO save(PredictorStockDTO predictorStockDTO) {
        validatePredictorStockExists(predictorStockDTO.getId());

        ProductEntity product = findProductById(predictorStockDTO.getProductId());

        predictorStockDTO.setDate(Objects.isNull(predictorStockDTO.getDate()) ? LocalDateTime.now() : predictorStockDTO.getDate());
        PredictorStockEntity entity = mapToEntity(predictorStockDTO, product);

        // Save PredictorStock
        PredictorStockEntity savedEntity = predictorStockRepository.save(entity);

        // Register Current Stock and Inventory Movement
        InventoryMovementDTO dto  = new InventoryMovementDTO();
        String moventId = generateMovementId();
        dto.setMovementId(moventId); // Replace `generateMovementId()` with your logic to generate or retrieve the ID
        dto.setProductId(savedEntity.getProduct().getProductId());
        dto.setQuantity(savedEntity.getUnitsSold());
        dto.setMovementType("OUTBOUND");
        dto.setDate(savedEntity.getDate());
        dto.setOrderId(generateOrderId());
        inventoryMovementService.save(dto);
        return mapToDTO(savedEntity, moventId);
    }

    private String generateOrderId() {
        // Generate a unique order ID starting with "OXXX"
        return "O" + System.currentTimeMillis();
    }
    private String generateMovementId() {
        // Generate a unique ID starting with "MXXX"
        return "M" + System.currentTimeMillis();
    }

    @Override
    public PredictorStockDTO update(Long id, PredictorStockDTO predictorStockDTO) {
        // Fetch the existing entity
        PredictorStockEntity existingEntity = findPredictorStockById(id);

        // Validate and fetch the associated product
        ProductEntity product = validateProductExists(predictorStockDTO.getProductId());

        // Update fields in the entity
        predictorStockDTO.setDate(Objects.isNull(predictorStockDTO.getDate()) ? LocalDateTime.now() : predictorStockDTO.getDate());
        CopyNonNullConfig.copyNonNullProperties(predictorStockDTO, existingEntity);
        existingEntity.setProduct(product);

        // Save the updated entity
        PredictorStockEntity updatedEntity = predictorStockRepository.save(existingEntity);

        // Update inventory movement

        InventoryMovementDTO movementDTO = createInventoryMovementDTO(updatedEntity,predictorStockDTO.getMoventId());
        inventoryMovementService.update(predictorStockDTO.getMoventId(), movementDTO);

        // Return the updated DTO
        return mapToDTO(updatedEntity, predictorStockDTO.getMoventId());
    }

    private InventoryMovementDTO createInventoryMovementDTO(PredictorStockEntity entity, String movementId) {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setMovementId(movementId);
        dto.setProductId(entity.getProduct().getProductId());
        dto.setQuantity(entity.getUnitsSold());
        dto.setMovementType("UPDATE");
        dto.setDate(LocalDateTime.now());
        dto.setOrderId(generateOrderId());
        return dto;
    }
    @Override
    public void deleteById(Long id) {
        PredictorStockEntity entity = findPredictorStockById(id);
        predictorStockRepository.delete(entity);
    }



    private PredictorStockDTO mapToDTO(PredictorStockEntity entity, String movmentId) {
        return new PredictorStockDTO(
                entity.getId(),
                entity.getDate(),
                entity.getProduct().getProductId(),
                entity.getUnitsSold(),
                entity.getAvgSalePrice(),
                entity.isPromotionActive(),
                entity.getSpecialEvent(),
                movmentId
        );
    }

    private PredictorStockEntity mapToEntity(PredictorStockDTO dto, ProductEntity product) {
        PredictorStockEntity entity = new PredictorStockEntity();
        entity.setId(dto.getId());
        entity.setDate(dto.getDate());
        entity.setProduct(product);
        entity.setUnitsSold(dto.getUnitsSold());
        entity.setAvgSalePrice(dto.getAvgSalePrice());
        entity.setPromotionActive(dto.isPromotionActive());
        entity.setSpecialEvent(dto.getSpecialEvent());
        return entity;
    }

    private void validatePredictorStockExists(Long id) {
        if (predictorStockRepository.existsById(id)) {
            throw new ProductAlreadyExistsException("Predictor Stock with ID " + id + " already exists");
        }
    }

    private ProductEntity findProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    private PredictorStockEntity findPredictorStockById(Long id) {
        return predictorStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PredictorStock with ID " + id + " not found"));
    }

    private ProductEntity validateProductExists(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    // This method generates a purchase recommendation based on the current stock and average sales. Not implemented in the original code.
    public void generatePurchaseRecommendation(LocalDateTime date) {
        CurrentStockEntity stock = currentStockRepository.findByLastUpdated(date)
                .orElseThrow(() -> new ResourceNotFoundException("Current stock not found for date: " + date));

        int threshold = 10; // Critical threshold
        int avgSales = calculateAverageSales(stock.getProduct().getProductId()); // Calculate avgSales
        if (stock.getQuantity() <= threshold) {
            int recommendedQuantity = calculateRecommendedQuantity(stock.getQuantity(), threshold, avgSales);
            logRecommendation(stock.getProduct().getProductName(), recommendedQuantity);
        }
    }

    private int calculateRecommendedQuantity(int currentQuantity, int threshold, int avgSales) {
        return (threshold - currentQuantity) + avgSales;
    }

    private void logRecommendation(String productName, int recommendedQuantity) {
        System.out.println("⚠ RECOMMENDATION: Restock " + recommendedQuantity + " units of " + productName);
    }

    private int calculateAverageSales(String productId) {
        Optional<PredictorStockEntity> salesHistory = predictorStockRepository.findByProduct_ProductId(productId);

        if (salesHistory.isEmpty()) {
            throw new ResourceNotFoundException("No sales history found for product ID: " + productId);
        }

        return (int) salesHistory.stream()
                .mapToInt(PredictorStockEntity::getUnitsSold)
                .average()
                .orElse(0);
    }
}