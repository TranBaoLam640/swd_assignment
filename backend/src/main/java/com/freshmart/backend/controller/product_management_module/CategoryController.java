package com.freshmart.backend.controller.product_management_module;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.response.product_management_module.CategoryResponse;
import com.freshmart.backend.service.interfaces.product_management_module.CategoryService;

/**
 * Public: GET /api/v1/categories feeds the category filter menu in UC09 -
 * Browse & Search Product (BR-01: browsing needs no auth). Made permitAll
 * in SecurityConfig alongside /api/v1/products/**.
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> listCategories() {
        return ApiResponse.success(categoryService.listCategories());
    }
}
