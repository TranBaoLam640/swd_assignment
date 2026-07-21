package com.freshmart.backend.data_access.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.freshmart.backend.enums.payment_management_module.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "gateway_txn_ref", nullable = false, unique = true, length = 100)
    private String gatewayTxnRef;

    @Column(name = "transaction_code", length = 255)
    private String transactionCode;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_gateway", nullable = false, length = 255)
    private String paymentGateway;

    @Column(name = "payment_message", length = 255)
    private String paymentMessage;

    // NOTE: @Lob alone does not retroactively widen an already-existing
    // column — Hibernate's ddl-auto: update only ADDS missing tables/columns,
    // it never ALTERs an existing column's type. If this entity created the
    // "raw_response" column earlier as something small (e.g. VARCHAR(255)),
    // you must run `ALTER TABLE payment MODIFY raw_response LONGTEXT;`
    // directly against the database — this annotation only guarantees the
    // right column type for a brand-new/from-scratch database.
    @Lob
    @Column(name = "raw_response", columnDefinition = "LONGTEXT")
    private String rawResponse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private Instant paidAt;
}
