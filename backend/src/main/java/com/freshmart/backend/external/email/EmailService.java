package com.freshmart.backend.external.email;

/**
 * Adapter contract for sending transactional emails ({@code <<External>>} in
 * the SDS Authentication class diagram). Business services depend on this
 * interface, not on a concrete mail library, so the provider can be swapped
 * (SMTP, SendGrid, ...) without touching calling code.
 */
public interface EmailService {

    /**
     * Sends the "forgot password" email containing the reset token/link.
     */
    boolean sendResetPasswordEmail(String email, String token);

    /**
     * Sends a welcome email after successful registration.
     */
    boolean sendWelcomeEmail(String email);

    /**
     * Generic transactional email, reused by other modules
     * (order confirmation, order status updates, refund notifications, etc.).
     */
    boolean sendEmail(String to, String subject, String body);
}
