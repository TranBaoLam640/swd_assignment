package com.freshmart.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Exposes the {@code CorsConfigurationSource} bean that {@code
 * SecurityConfig}'s {@code .cors(...)} wires into the Spring Security
 * filter chain, so cross-origin requests from the frontend (React/Vite
 * dev server, and later the deployed FE origin) are allowed.
 *
 * <p>This used to be a {@code WebMvcConfigurer.addCorsMappings(...)}
 * bean instead. That only configures CORS for Spring MVC's own
 * dispatch — it is never consulted by Spring Security's authorization
 * filter. Since {@code SecurityConfig} had no {@code .cors(...)} call
 * and no rule explicitly permitted the {@code OPTIONS} method, every
 * cross-origin preflight request fell through to {@code
 * .anyRequest().authenticated()} and was rejected before any {@code
 * Access-Control-Allow-Origin} header was ever added — the browser then
 * reports "Response to preflight request doesn't pass access control
 * check". Exposing a {@code CorsConfigurationSource} bean here and
 * wiring {@code .cors(Customizer.withDefaults())} into {@code
 * SecurityConfig} lets Spring Security's own CORS filter (which runs
 * before authorization) handle preflight requests correctly for every
 * endpoint, public or protected.
 *
 * <p>Allowed origins are read from {@code app.cors.allowed-origins} in
 * application.yaml; defaults to common local Vite/CRA dev ports if not set.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String[] allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
