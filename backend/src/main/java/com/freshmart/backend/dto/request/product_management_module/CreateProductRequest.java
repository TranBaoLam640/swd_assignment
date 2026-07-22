package com.freshmart.backend.dto.request.product_management_module;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.freshmart.backend.enums.product_management_module.PriceUnit;

/** Request body for Manager product creation (FE-03: Product Management). */
@Getter
@Setter
@NoArgsConstructor
public class CreateProductRequest {

    @NotNull
    private Long shopId;

    private Long categoryId;

    @NotBlank
    private String productName;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    @NotNull
    private PriceUnit priceUnit = PriceUnit.KG;

    @NotNull
    @jakarta.validation.constraints.Min(1)
    private Integer priceQuantityGrams = 1000;

    private String imageUrl;
}
