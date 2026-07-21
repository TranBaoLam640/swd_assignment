package com.freshmart.backend.dto.response.payment_management_module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Response for UC33 Step 6 - the VNPAY hosted-checkout URL to redirect the browser to. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlResponse {
    private String paymentUrl;
}
