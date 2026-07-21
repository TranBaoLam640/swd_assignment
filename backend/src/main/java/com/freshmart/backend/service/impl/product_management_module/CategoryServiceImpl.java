package com.freshmart.backend.service.impl.product_management_module;

import java.util.List;

import org.springframework.stereotype.Service;

import com.freshmart.backend.data_access.repository.product_management_module.CategoryRepository;
import com.freshmart.backend.dto.response.product_management_module.CategoryResponse;
import com.freshmart.backend.mapper.product_management_module.CategoryMapper;
import com.freshmart.backend.service.interfaces.product_management_module.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }
}
