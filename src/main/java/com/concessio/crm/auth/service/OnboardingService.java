package com.concessio.crm.auth.service;

import com.concessio.crm.auth.dto.OnboardingRequest;
import com.concessio.crm.user.model.Role;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import com.concessio.crm.tenant.service.TenantService;
import com.concessio.crm.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OnboardingService {

    private static final Logger logger = LoggerFactory.getLogger(OnboardingService.class);

    private final TenantService tenantService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public OnboardingService(TenantService tenantService,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.tenantService = tenantService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public String onboard(OnboardingRequest request) {
        logger.info("Starting onboarding process for business: {}", request.getBusinessName());

        // 1. Create Tenant
        // Generate code from business name (e.g., "Giamma Belgrano" -> "BEL")
        String code = generateTenantCode(request.getBusinessName());
        Tenant tenant = tenantService.create(code, request.getBusinessName());
        logger.info("Tenant created with ID: {}", tenant.getId());

        // 2. Create Admin User
        User adminUser = createAdminUser(tenant, request);
        logger.info("Admin user created with ID: {}", adminUser.getId());

        // 3. Generate JWT token
        String token = jwtService.generateToken(adminUser.getEmail(), tenant.getId());
        logger.info("JWT token generated for admin user");

        return token;
    }

    private User createAdminUser(Tenant tenant, OnboardingRequest request) {
        User admin = new User(
            request.getAdminName(),
            request.getAdminLastname(),
            request.getAdminEmail(),
            Role.GERENTE,
            true,
            tenant
        );

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        admin.setPassword(hashedPassword);

        return userRepository.save(admin);
    }

    private String generateTenantCode(String businessName) {
        // Generar código de sucursal a partir del nombre
        // Ej: "Giamma Belgrano" -> "BEL"
        // Ej: "Giamma Mataderos" -> "MAT"
        String[] words = businessName.toUpperCase().split("\\s+");
        if (words.length > 1) {
            // Usar la última palabra (generalmente la ubicación)
            String location = words[words.length - 1];
            return location.substring(0, Math.min(3, location.length()));
        }
        return businessName.substring(0, Math.min(3, businessName.length())).toUpperCase();
    }
}
