package com.freshmart.backend.service.interfaces.product_management_module;

import java.util.List;

import com.freshmart.backend.dto.request.product_management_module.CreateProductRequest;
import com.freshmart.backend.dto.request.product_management_module.UpdateProductRequest;
import com.freshmart.backend.dto.response.product_management_module.ProductResponse;

/** Implements FE-03 (Product Management): customer browsing + manager CRUD. */
public interface ProductService {

    /**
     * UC09 - Browse & Search Product. keyword/categoryId are both optional
     * (pass null for either to skip that filter) — see
     * ProductRepository#searchActiveProducts.
     */
    List<ProductResponse> browseActiveProducts(String keyword, Long categoryId);

    /** Manager catalog view: every product regardless of isActive. */
    List<ProductResponse> listAllProducts();

    ProductResponse getProduct(Long productId);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);
}
