package com.freshmart.backend.controller.product_management_module;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.request.product_management_module.CreateProductRequest;
import com.freshmart.backend.dto.request.product_management_module.UpdateProductRequest;
import com.freshmart.backend.dto.response.product_management_module.ProductResponse;
import com.freshmart.backend.service.interfaces.product_management_module.ProductService;

import jakarta.validation.Valid;

/**
 * Browsing endpoints under /api/v1/products are already permitAll (GET)
 * in SecurityConfig. Manager endpoints under /api/v1/manager/products are
 * already restricted to MANAGER by the existing "/api/v1/manager/**"
 * rule — no SecurityConfig change needed.
 */
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * UC09 - Browse & Search Product. Both params are optional: omit
     * keyword/categoryId (or send blank) to browse the full active catalog,
     * same as before this UC was implemented.
     */
    @GetMapping("/api/v1/products")
    public ApiResponse<List<ProductResponse>> browseProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(productService.browseActiveProducts(keyword, categoryId));
    }

    @GetMapping("/api/v1/products/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        return ApiResponse.success(productService.getProduct(productId));
    }

    /** Manager catalog view: every product, including inactive/hidden ones. */
    @GetMapping("/api/v1/manager/products")
    public ApiResponse<List<ProductResponse>> listAllProducts() {
        return ApiResponse.success(productService.listAllProducts());
    }

    @PostMapping("/api/v1/manager/products")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success(201, "Product created", productService.createProduct(request));
    }

    @PutMapping("/api/v1/manager/products/{productId}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long productId,
                                                       @Valid @RequestBody UpdateProductRequest request) {
        return ApiResponse.success(200, "Product updated", productService.updateProduct(productId, request));
    }
}
