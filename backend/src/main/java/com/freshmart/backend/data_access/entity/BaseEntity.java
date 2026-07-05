package com.freshmart.backend.data_access.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Common fields shared by every JPA entity in the ERD (User, Product, Order,
 * Shop, Review, ...): surrogate primary key plus created/updated timestamps.
 *
 * <p>{@code @MappedSuperclass} means this class does not get its own table;
 * its fields are inlined into each subclass's table instead. Entities extend
 * it like:
 * <pre>{@code
 * @Entity
 * public class Product extends BaseEntity {
 *     private String name;
 *     ...
 * }
 * }</pre>
 *
 * <p>{@code createdAt}/{@code updatedAt} are set automatically by Spring
 * Data JPA auditing ({@link AuditingEntityListener}) — no need to set them
 * manually in services. Auditing is enabled via {@code @EnableJpaAuditing}
 * in {@code config.JpaAuditingConfig}.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}
