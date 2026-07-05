package com.freshmart.backend.external.email;

import org.springframework.stereotype.Component;

/**
 * Template implementation of {@link EmailService} backed by SMTP
 * (e.g. Spring's {@code JavaMailSender}). Fill in the TODOs when wiring up
 * the real mail provider.
 */
@Component
public class SmtpEmailServiceImpl implements EmailService {

    @Override
    public boolean sendResetPasswordEmail(String email, String token) {
        // TODO: build reset-password link from token and send via mail sender
        return false;
    }

    @Override
    public boolean sendWelcomeEmail(String email) {
        // TODO: send welcome email
        return false;
    }

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        // TODO: send generic email via mail sender
        return false;
    }
}
