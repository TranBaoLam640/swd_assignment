package com.freshmart.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.freshmart.backend.security.JwtAccessDeniedHandler;
import com.freshmart.backend.security.JwtAuthenticationEntryPoint;
import com.freshmart.backend.security.JwtAuthenticationFilter;

/**
 * Central security rules: stateless JWT auth, public vs protected endpoints,
 * and JSON error responses for 401/403 (see JwtAuthenticationEntryPoint /
 * JwtAccessDeniedHandler). No AuthenticationManager bean is needed here —
 * AuthServiceImpl verifies credentials itself (per the login sequence
 * diagram) rather than delegating to Spring's AuthenticationManager.
 *
 * <p>{@code .cors(Customizer.withDefaults())} makes Spring Security pick
 * up the {@code CorsConfigurationSource} bean from {@code CorsConfig}
 * and handle CORS preflight (OPTIONS) requests itself, before the
 * {@code authorizeHttpRequests} rules below run — without this, every
 * cross-origin preflight fell through to {@code
 * .anyRequest().authenticated()} and got rejected with no CORS headers
 * attached, which is exactly the "Response to preflight request doesn't
 * pass access control check" error browsers report. See CorsConfig's
 * Javadoc for the full explanation.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize("hasRole('ADMIN')") once role-based endpoints are added
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    /**
     * Used by AuthServiceImpl to hash/verify passwords (per class diagram).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // --- Public, không cần token ---
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        // UC09 - Browse & Search Product's category filter menu (BR-01:
                        // browsing needs no auth) — same public treatment as /products.
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        // VNPAY calls these directly (Return URL redirect + server-to-server IPN),
                        // never carrying one of our JWTs — see PaymentWebhookController.
                        .requestMatchers("/api/v1/payment/vnpay/**").permitAll()

                        // --- Riêng theo từng role, phân biệt bằng path prefix ---
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/shipper/**").hasRole("SHIPPER")
                        .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")

                        // --- Chung, chỉ cần đăng nhập (bất kỳ role nào) ---
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
