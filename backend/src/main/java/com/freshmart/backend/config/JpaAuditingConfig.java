package com.freshmart.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA auditing so {@code @CreatedDate}/{@code @LastModifiedDate}
 * on {@link com.freshmart.backend.data_access.entity.BaseEntity} are populated
 * automatically on insert/update, without services setting them manually.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
