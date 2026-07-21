package com.freshmart.backend.service.impl.product_management_module;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.data_access.entity.Product;
import com.freshmart.backend.data_access.repository.product_management_module.ProductRepository;
import com.freshmart.backend.dto.request.product_management_module.CreateProductRequest;
import com.freshmart.backend.dto.request.product_management_module.UpdateProductRequest;
import com.freshmart.backend.dto.response.product_management_module.ProductResponse;
import com.freshmart.backend.mapper.product_management_module.ProductMapper;
import com.freshmart.backend.service.interfaces.inventory_management_module.InventoryService;
import com.freshmart.backend.service.interfaces.product_management_module.ProductService;

/**
 * Implements FE-03. Per UC "Browse and Search Products", only active
 * products are returned by browseActiveProducts() — optionally narrowed by
 * a keyword (matched against productName) and/or a categoryId, see
 * ProductRepository#searchActiveProducts. listAllProducts() is the
 * manager-facing catalog view (includes inactive/hidden products, so
 * managers can find and re-enable them); getProduct() returns any single
 * product regardless of status.
 *
 * <p>Every ProductResponse now also carries stockQuantity (via
 * InventoryService), so the storefront/cart UI can validate "don't add
 * more than what's in stock" without a separate round-trip per product.
 *
 * <p>createProduct() also provisions the product's Inventory row (per SDS
 * 2.2.8, inventory is a strict one-to-one with product) — see
 * {@link InventoryService#createForProduct(Long)}. Manager then
 * increases/decreases/sets that stock via the Inventory Management module.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;

    public ProductServiceImpl(ProductRepository productRepository,
                               ProductMapper productMapper,
                               InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    public List<ProductResponse> browseActiveProducts(String keyword, Long categoryId) {
        List<Product> products = productRepository.searchActiveProducts(keyword, categoryId);
        return toResponsesWithStock(products);
    }

    @Override
    public List<ProductResponse> listAllProducts() {
        List<Product> products = productRepository.findAll();
        return toResponsesWithStock(products);
    }

    @Override
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", productId));
        int stock = inventoryService.getByProduct(productId).getStockQuantity();
        return productMapper.toResponse(product, stock);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setShopId(request.getShopId());
        product.setCategoryId(request.getCategoryId());
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(true);
        productRepository.save(product);

        inventoryService.createForProduct(product.getId());

        return productMapper.toResponse(product, 0);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", productId));

        product.setCategoryId(request.getCategoryId());
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(request.getIsActive());
        productRepository.save(product);

        int stock = inventoryService.getByProduct(productId).getStockQuantity();
        return productMapper.toResponse(product, stock);
    }

    private List<ProductResponse> toResponsesWithStock(List<Product> products) {
        Map<Long, Integer> stockMap = inventoryService.getStockMap(products.stream().map(Product::getId).toList());
        return products.stream()
                .map(p -> productMapper.toResponse(p, stockMap.getOrDefault(p.getId(), 0)))
                .toList();
    }
}
