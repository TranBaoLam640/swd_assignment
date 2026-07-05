package com.freshmart.backend.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pagination wrapper used as the {@code data} payload of {@link ApiResponse}
 * for any listing endpoint (product list, order list, review list, ...).
 *
 * <pre>{@code
 * {
 *   "success": true,
 *   "code": 200,
 *   "message": "OK",
 *   "data": {
 *     "content": [ ... ],
 *     "page": 0,
 *     "size": 10,
 *     "totalElements": 125,
 *     "totalPages": 13,
 *     "first": true,
 *     "last": false
 *   },
 *   "timestamp": "..."
 * }
 * }</pre>
 *
 * @param <T> type of each item in {@link #getContent()}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /** Items on the current page. */
    private List<T> content;

    /** Zero-based current page index. */
    private int page;

    /** Page size (max items per page). */
    private int size;

    /** Total number of items across all pages. */
    private long totalElements;

    /** Total number of pages. */
    private int totalPages;

    /** Whether this is the first page. */
    private boolean first;

    /** Whether this is the last page. */
    private boolean last;

    /** Builds a {@link PageResponse} directly from a Spring Data {@link Page}. */
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
