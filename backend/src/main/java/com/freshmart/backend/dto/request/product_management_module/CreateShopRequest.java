package com.freshmart.backend.dto.request.product_management_module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request body for manager shop creation. */
@Getter
@Setter
@NoArgsConstructor
public class CreateShopRequest {

    @NotNull
    private Long ownerId;

    @NotBlank
    private String shopName;

    @NotBlank
    private String shopAddress;

    private String shopDescription;

    @NotBlank
    private String status;
}
