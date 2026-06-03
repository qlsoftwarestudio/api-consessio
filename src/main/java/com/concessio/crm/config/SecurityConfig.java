package com.concessio.crm.config;

import com.concessio.crm.auth.security.TenantContextFilter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TenantContextFilter tenantContextFilter;

    public SecurityConfig(TenantContextFilter tenantContextFilter) {
        this.tenantContextFilter = tenantContextFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .contentTypeOptions(contentTypeOptions -> {})
                        .frameOptions(frameOptions -> frameOptions.deny())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public endpoints
                        .requestMatchers("/auth/**", "/health", "/actuator/**").permitAll()
                        
                        // Users - GERENTE/SUPERVISOR/ADMIN_SISTEMA admin; todos leen
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")
                        .requestMatchers(HttpMethod.POST, "/users/**").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")

                        // Leads - todos los roles
                        .requestMatchers("/api/leads/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")

                        // Vehicles - GERENTE/SUPERVISOR/ADMIN_SISTEMA modifica; todos leen
                        .requestMatchers(HttpMethod.GET, "/api/vehicles/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")
                        .requestMatchers(HttpMethod.POST, "/api/vehicles/**").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")
                        .requestMatchers(HttpMethod.PUT, "/api/vehicles/**").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")
                        .requestMatchers(HttpMethod.DELETE, "/api/vehicles/**").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")

                        // Quotations - todos
                        .requestMatchers("/api/quotations/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")

                        // Test Drives - todos
                        .requestMatchers("/api/test-drives/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")

                        // Documents - todos
                        .requestMatchers("/api/documents/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")

                        // Activities - todos
                        .requestMatchers("/api/activities/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")

                        // Excel Upload - GERENTE/SUPERVISOR/ADMIN_SISTEMA
                        .requestMatchers("/api/excel/**").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")

                        // Dashboard/Stats - todos
                        .requestMatchers("/api/stats/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")

                        // Copilot - ranking solo gerencia/supervision; resto todos los roles
                        .requestMatchers("/api/copilot/ranking").hasAnyRole("GERENTE", "SUPERVISOR", "ADMIN_SISTEMA")
                        .requestMatchers("/api/copilot/**").hasAnyRole("GERENTE", "SUPERVISOR", "VENDEDORA", "PLANES", "ADMIN_SISTEMA")
                        
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "https://consessio-front.vercel.app",
                "https://www.consessio.com",
                "http://localhost:8081"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

