package com.freshmart.backend.service.interfaces.product_management_module;

import java.util.List;

import com.freshmart.backend.dto.response.product_management_module.CategoryResponse;

/** Read-only category list feeding the browse/search filter menu (UC09). */
public interface CategoryService {

    List<CategoryResponse> listCategories();
}
