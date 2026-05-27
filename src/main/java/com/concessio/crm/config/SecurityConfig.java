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
                        
                        // Users - Solo ADMIN puede gestionar usuarios
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.POST, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                        
                        // Leads - ADMIN y VENDEDOR pueden operar
                        .requestMatchers("/api/leads/**").hasAnyRole("ADMIN", "VENDEDOR")
                        
                        // Vehicles - Solo ADMIN puede modificar, todos pueden ver
                        .requestMatchers(HttpMethod.GET, "/api/vehicles/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.POST, "/api/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/vehicles/**").hasRole("ADMIN")
                        
                        // Quotations - ADMIN y VENDEDOR
                        .requestMatchers("/api/quotations/**").hasAnyRole("ADMIN", "VENDEDOR")
                        
                        // Test Drives - ADMIN y VENDEDOR
                        .requestMatchers("/api/test-drives/**").hasAnyRole("ADMIN", "VENDEDOR")
                        
                        // Documents - ADMIN y VENDEDOR
                        .requestMatchers("/api/documents/**").hasAnyRole("ADMIN", "VENDEDOR")
                        
                        // Activities - ADMIN y VENDEDOR
                        .requestMatchers("/api/activities/**").hasAnyRole("ADMIN", "VENDEDOR")
                        
                        // Excel Upload - Solo ADMIN
                        .requestMatchers("/api/excel/**").hasRole("ADMIN")
                        
                        // Dashboard/Stats - ADMIN y VENDEDOR
                        .requestMatchers("/api/stats/**").hasAnyRole("ADMIN", "VENDEDOR")
                        
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
                "https://www.consessio.com"
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

