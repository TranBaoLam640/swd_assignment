package com.freshmart.backend.mapper.product_management_module;

import org.springframework.stereotype.Component;

import com.freshmart.backend.data_access.entity.Category;
import com.freshmart.backend.dto.response.product_management_module.CategoryResponse;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getCategoryName());
    }
}
