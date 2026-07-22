package com.freshmart.backend.dto.request.product_management_module;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request body for customer product review submission. */
@Getter
@Setter
@NoArgsConstructor
public class CreateProductReviewRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;

    private String imageUrl;

    private String videoUrl;
}
