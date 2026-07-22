package com.freshmart.backend.service.impl.product_management_module;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.data_access.entity.Category;
import com.freshmart.backend.data_access.entity.Product;
import com.freshmart.backend.data_access.entity.Shop;
import com.freshmart.backend.data_access.repository.product_management_module.CategoryRepository;
import com.freshmart.backend.data_access.repository.product_management_module.ProductRepository;
import com.freshmart.backend.data_access.repository.product_management_module.ProductReviewRepository;
import com.freshmart.backend.data_access.repository.product_management_module.ShopRepository;
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
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository reviewRepository;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;

    public ProductServiceImpl(ProductRepository productRepository,
                               ShopRepository shopRepository,
                               CategoryRepository categoryRepository,
                               ProductReviewRepository reviewRepository,
                               ProductMapper productMapper,
                               InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
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
        String shopName = getShopName(product.getShopId());
        String categoryName = getCategoryName(product.getCategoryId());
        ReviewSummary reviewSummary = getReviewSummary(product.getId());
        return productMapper.toResponse(
                product,
                shopName,
                categoryName,
                reviewSummary.averageRating(),
                reviewSummary.reviewCount(),
                stock);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> ResourceNotFoundException.of("Shop", request.getShopId()));

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

        return productMapper.toResponse(product, shop.getShopName(), getCategoryName(product.getCategoryId()), 0.0, 0L, 0);
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
        return productMapper.toResponse(
                product,
                getShopName(product.getShopId()),
                getCategoryName(product.getCategoryId()),
                getReviewSummary(product.getId()).averageRating(),
                getReviewSummary(product.getId()).reviewCount(),
                stock);
    }

    private List<ProductResponse> toResponsesWithStock(List<Product> products) {
        if (products.isEmpty()) {
            return List.of();
        }
        Map<Long, Integer> stockMap = inventoryService.getStockMap(products.stream().map(Product::getId).toList());
        Map<Long, String> shopNameMap = shopRepository
                .findAllById(products.stream().map(Product::getShopId).distinct().toList())
                .stream()
                .collect(java.util.stream.Collectors.toMap(Shop::getId, Shop::getShopName));
        Map<Long, String> categoryNameMap = categoryRepository
                .findAllById(products.stream()
                        .map(Product::getCategoryId)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .toList())
                .stream()
                .collect(java.util.stream.Collectors.toMap(Category::getId, Category::getCategoryName));
        Map<Long, ReviewSummary> reviewSummaryMap = reviewRepository
                .summarizeByProductIds(products.stream().map(Product::getId).toList())
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (Long) row[0],
                        row -> new ReviewSummary(((Number) row[1]).doubleValue(), ((Number) row[2]).longValue())));
        return products.stream()
                .map(p -> productMapper.toResponse(
                        p,
                        shopNameMap.get(p.getShopId()),
                        categoryNameMap.get(p.getCategoryId()),
                        reviewSummaryMap.getOrDefault(p.getId(), new ReviewSummary(0.0, 0L)).averageRating(),
                        reviewSummaryMap.getOrDefault(p.getId(), new ReviewSummary(0.0, 0L)).reviewCount(),
                        stockMap.getOrDefault(p.getId(), 0)))
                .toList();
    }

    private String getShopName(Long shopId) {
        return shopRepository.findById(shopId)
                .map(Shop::getShopName)
                .orElse(null);
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .map(Category::getCategoryName)
                .orElse(null);
    }

    private ReviewSummary getReviewSummary(Long productId) {
        return reviewRepository.summarizeByProductIds(List.of(productId)).stream()
                .findFirst()
                .map(row -> new ReviewSummary(((Number) row[1]).doubleValue(), ((Number) row[2]).longValue()))
                .orElse(new ReviewSummary(0.0, 0L));
    }

    private record ReviewSummary(Double averageRating, Long reviewCount) {
    }
}
