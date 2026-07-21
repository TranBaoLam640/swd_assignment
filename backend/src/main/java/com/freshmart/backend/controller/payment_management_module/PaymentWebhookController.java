package com.freshmart.backend.controller.payment_management_module;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.freshmart.backend.service.interfaces.payment_management_module.PaymentService;

/**
 * VNPAY callback endpoints — deliberately public (see the
 * "/api/v1/payment/vnpay/**" permitAll rule added to SecurityConfig for
 * this), since VNPAY's servers call these without any of our JWTs.
 *
 * <p>Neither method returns {@code ApiResponse} — the Return URL is a
 * browser redirect, and the IPN endpoint must reply in the exact
 * {@code {"RspCode": "...", "Message": "..."}} shape VNPAY's protocol
 * expects, not our own envelope (see {@code ApiResponse}'s own Javadoc,
 * which already called this out before this controller existed).
 */
@RestController
public class PaymentWebhookController {

    private final PaymentService paymentService;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/api/v1/payment/vnpay/return")
    public RedirectView handleReturn(@RequestParam Map<String, String> allParams) {
        return new RedirectView(paymentService.handleReturn(allParams));
    }

    @GetMapping("/api/v1/payment/vnpay/ipn")
    public Map<String, String> handleIpn(@RequestParam Map<String, String> allParams) {
        return paymentService.handleIpn(allParams);
    }
}
