package com.freshmart.backend.dto.request.order_management_module;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request body for UC17 - Cancel Order; reason is required per the use case's main sequence. */
@Getter
@Setter
@NoArgsConstructor
public class CancelOrderRequest {

    @NotBlank
    private String reason;
}
