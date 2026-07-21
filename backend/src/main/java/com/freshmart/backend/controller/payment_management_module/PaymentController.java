package com.freshmart.backend.controller.payment_management_module;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freshmart.backend.common.dto.ApiResponse;
import com.freshmart.backend.dto.response.payment_management_module.PaymentUrlResponse;
import com.freshmart.backend.service.interfaces.payment_management_module.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Customer-facing payment endpoint — implements UC33 Step 6. Requires a
 * customer JWT (covered by SecurityConfig's existing
 * "/api/v1/customer/**" -> CUSTOMER rule); the actual VNPAY callbacks are
 * handled by {@link PaymentWebhookController} instead, since those calls
 * come from VNPAY's servers, not a logged-in browser.
 */
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/v1/customer/orders/{orderId}/payment-url")
    public ApiResponse<PaymentUrlResponse> createPaymentUrl(@AuthenticationPrincipal Long userId,
                                                             @PathVariable Long orderId,
                                                             HttpServletRequest request) {
        String url = paymentService.createPaymentUrl(userId, orderId, request.getRemoteAddr());
        return ApiResponse.success(new PaymentUrlResponse(url));
    }
}
