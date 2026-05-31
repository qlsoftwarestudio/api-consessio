package com.concessio.crm.user.service;

import com.concessio.crm.exceptions.ResourceNotFoundException;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import com.concessio.crm.tenant.TenantContext;
import com.concessio.crm.tenant.repository.TenantRepository;
import com.concessio.crm.user.dto.PaginatedResponse;
import com.concessio.crm.user.dto.UserResponseDTO;
import com.concessio.crm.user.mapper.UserMapper;
import com.concessio.crm.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TenantRepository tenantRepository;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserMapper userMapper, TenantRepository tenantRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.tenantRepository = tenantRepository;
    }

    public UserResponseDTO createUser(User user) {
        Long currentTenantId = TenantContext.getCurrentTenant();
        if (currentTenantId == null) {
            throw new IllegalStateException("No tenant context found. Cannot create user.");
        }
        
        if (repository.existsByEmailAndTenantId(user.getEmail(), currentTenantId)) {
            throw new IllegalArgumentException("El email ya está registrado en este tenant");
        }
        
        logger.info("Creating user with email: {} for tenant: {}", user.getEmail(), currentTenantId);
        logger.info("User details: {}", user);
        
        // Get tenant from database
        Tenant tenant = tenantRepository.findById(currentTenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant not found with id: " + currentTenantId));
        
        user.setTenant(tenant);
        
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        User savedUser = repository.save(user);
        logger.info("User saved successfully with ID: {} for tenant: {}", savedUser.getId(), currentTenantId);
        
        return userMapper.toResponse(savedUser);
    }

    public PaginatedResponse<UserResponseDTO> getAllUsers(int page, int size, String sortBy) {
        Long currentTenantId = TenantContext.getCurrentTenant();
        if (currentTenantId == null) {
            throw new IllegalStateException("No tenant context found. Cannot list users.");
        }
        
        logger.info("Getting users for tenant: {}", currentTenantId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> userPage = repository.findByTenantIdAndIsActiveTrue(currentTenantId, pageable);
        Page<UserResponseDTO> responsePage = userPage.map(userMapper::toResponse);
        
        logger.info("Found {} users for tenant: {}", userPage.getTotalElements(), currentTenantId);
        
        return PaginatedResponse.of(responsePage);
    }

    
    public Optional<UserResponseDTO> getUserById(Long id) {
        return repository.findById(id).map(userMapper::toResponse);
    }

    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        repository.save(user);
        logger.info("Soft deleted user with id: {}", id);
    }

    public boolean userExists(Long id) {
        return repository.existsById(id);
    }
}

