package com.freshmart.backend.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;

/**
 * Standard pagination/sorting query params for any listing endpoint, bound
 * directly from the request, e.g.:
 * <pre>{@code
 * GET /api/v1/products?page=0&size=10&sortBy=price&direction=asc
 *
 * @GetMapping
 * public ApiResponse<PageResponse<ProductResponse>> getProducts(PageRequestParams params) {
 *     Page<Product> page = productRepository.findAll(params.toPageable());
 *     return ApiResponse.success(PageResponse.of(page.map(mapper::toResponse)));
 * }
 * }</pre>
 */
@Getter
@Setter
public class PageRequestParams {

    /** Zero-based page index requested by the client. */
    private int page = 0;

    /** Number of items per page. */
    private int size = 10;

    /** Field to sort by, e.g. "price", "createdAt". Null/blank means unsorted. */
    private String sortBy;

    /** Sort direction: "asc" or "desc" (case-insensitive). Defaults to "asc". */
    private String direction = "asc";

    /** Builds a Spring Data {@link Pageable} from these params, ready to pass into a repository call. */
    public Pageable toPageable() {
        if (sortBy == null || sortBy.isBlank()) {
            return PageRequest.of(page, size);
        }
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(dir, sortBy));
    }
}
