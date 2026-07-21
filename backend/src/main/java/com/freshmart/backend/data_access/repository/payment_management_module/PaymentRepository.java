package com.freshmart.backend.data_access.repository.payment_management_module;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freshmart.backend.data_access.entity.Payment;

/** Matches PaymentRepository needs: lookups by order (history) and by our own gateway reference (callback handling). */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByGatewayTxnRef(String gatewayTxnRef);
}
