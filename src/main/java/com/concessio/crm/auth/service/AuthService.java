package com.concessio.crm.auth.service;

import com.concessio.crm.exceptions.AuthenticationException;
import com.concessio.crm.user.model.Role;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import com.concessio.crm.tenant.TenantContext;
import com.concessio.crm.tenant.service.TenantService;
import com.concessio.crm.user.repository.UserRepository;
import com.concessio.crm.user.dto.UserRequestDTO;
import com.concessio.crm.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final TenantService tenantService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder encoder,
                       JwtService jwtService,
                       UserMapper userMapper,
                       TenantService tenantService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.tenantService = tenantService;
    }

    public String register(UserRequestDTO userRequest) {

        logger.info("Attempting to register user with email: {}", userRequest.getEmail());

        // Get current tenant from context
        Long currentTenantId = TenantContext.getCurrentTenant();
        if (currentTenantId == null) {
            throw new IllegalStateException("No tenant context found. User registration must be done within a tenant.");
        }

        // Check if user already exists in current tenant
        if (userRepository.existsByEmailAndTenantId(userRequest.getEmail(), currentTenantId)) {
            logger.error("Registration failed: User with email {} already exists in tenant {}", userRequest.getEmail(), currentTenantId);
            throw new RuntimeException("User with email " + userRequest.getEmail() + " already exists in this tenant");
        }

        // Get tenant
        Tenant tenant = tenantService.findById(currentTenantId);

        // Create new user from DTO using mapper
        User newUser = userMapper.toEntity(userRequest);
        newUser.setTenant(tenant);
        
        // Encode password
        String hashedPassword = encoder.encode(userRequest.getPassword());
        newUser.setPassword(hashedPassword);

        try {
            User savedUser = userRepository.save(newUser);
            logger.info("User registered successfully: {} with ID: {} in tenant: {}", savedUser.getEmail(), savedUser.getId(), currentTenantId);
            String token = jwtService.generateToken(savedUser.getEmail(), currentTenantId);
            return token;
        } catch (Exception e) {
            logger.error("Failed to save user: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to register user: " + e.getMessage());
        }
    }

    public String login(String tenantCode, String email, String password) {

        Tenant tenant = tenantService.findByCode(tenantCode);

        User user = userRepository.findByEmailAndTenantIdAndIsActiveTrue(email, tenant.getId())
                .orElseThrow(() -> new AuthenticationException("User not found or inactive"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        return jwtService.generateToken(email, tenant.getId());
    }
}

