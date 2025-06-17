package ar.edu.udecy.web.inventory.service.impl;

import ar.edu.udecy.web.inventory.config.CopyNonNullConfig;
import ar.edu.udecy.web.inventory.dto.ProductDTO;
import ar.edu.udecy.web.inventory.entity.CurrentStockEntity;
import ar.edu.udecy.web.inventory.entity.ProductEntity;
import ar.edu.udecy.web.inventory.handler.exception.ProductAlreadyExistsException;
import ar.edu.udecy.web.inventory.handler.exception.ResourceNotFoundException;
import ar.edu.udecy.web.inventory.repository.CurrentStockRepository;
import ar.edu.udecy.web.inventory.repository.ProductRepository;
import ar.edu.udecy.web.inventory.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CurrentStockRepository currentStockRepository;

    @Override
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findById(String productId) {
        return productRepository.findById(productId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " not found"));
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        if (productRepository.existsById(productDTO.getProductId())) {
            throw new ProductAlreadyExistsException("Product with ID " + productDTO.getProductId() + " already exists");
        }

        ProductEntity productEntity = convertToEntity(productDTO);
        ProductEntity savedProduct = productRepository.save(productEntity);

        // Save corresponding CurrentStockEntity
        CurrentStockEntity currentStockEntity = new CurrentStockEntity();
        currentStockEntity.setProduct(savedProduct); // Associate with ProductEntity
        currentStockEntity.setQuantity(0); // Default quantity
        currentStockEntity.setLastUpdated(LocalDateTime.now());
        currentStockEntity.setTotalInventoryCost(BigDecimal.ZERO); // Default inventory cost
        currentStockRepository.save(currentStockEntity);

        return convertToDTO(savedProduct);
    }
    @Override
    public ProductDTO update(String productId, ProductDTO productDTO) {
        ProductEntity existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " not found"));

        CopyNonNullConfig.copyNonNullProperties(productDTO, existingProduct);
        return convertToDTO(productRepository.save(existingProduct));
    }

    @Override
    public void deleteById(String productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " not found"));
        productRepository.delete(product);
    }

    private ProductDTO convertToDTO(ProductEntity productEntity) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(productEntity, dto);
        return dto;
    }

    private ProductEntity convertToEntity(ProductDTO dto) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(dto, productEntity);
        return productEntity;
    }
}