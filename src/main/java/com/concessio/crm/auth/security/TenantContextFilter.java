package com.concessio.crm.auth.security;

import com.concessio.crm.auth.service.JwtService;
import com.concessio.crm.tenant.TenantContext;
import com.concessio.crm.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TenantContextFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TenantContextFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractEmail(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extraer y establecer tenantId del JWT
                Long tenantId = jwtService.extractTenantId(jwt);
                if (tenantId == null) {
                    logger.warn("JWT token does not contain tenantId");
                    filterChain.doFilter(request, response);
                    return;
                }

                userRepository.findByEmailAndTenantId(userEmail, tenantId).ifPresent(user -> {
                    UserDetails userDetails = User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .roles(user.getRole().name())
                            .build();

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        TenantContext.setCurrentTenant(tenantId);
                        request.setAttribute("tenantId", tenantId);
                        request.setAttribute("userId", user.getId());
                        logger.debug("Tenant context set to: {}", tenantId);
                    }
                });
            }
        } catch (Exception e) {
            logger.warn("JWT authentication skipped: {}", e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpiar el context al final del request
            TenantContext.clear();
        }
    }
}
