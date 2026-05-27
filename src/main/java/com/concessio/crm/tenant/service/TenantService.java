package com.concessio.crm.tenant.service;

import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.tenant.model.TenantStatus;
import com.concessio.crm.tenant.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TenantService {

    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public Tenant create(String code, String businessName) {
        logger.info("Creating new tenant with code: {} and name: {}", code, businessName);

        if (tenantRepository.existsByCode(code)) {
            logger.error("Tenant with code {} already exists", code);
            throw new IllegalArgumentException("Tenant with code '" + code + "' already exists");
        }

        Tenant tenant = new Tenant(code, businessName);
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant savedTenant = tenantRepository.save(tenant);
        logger.info("Tenant created successfully: {} with ID: {}", savedTenant.getName(), savedTenant.getId());

        return savedTenant;
    }

    public Tenant findById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with ID: " + id));
    }

    public Tenant findByName(String name) {
        return tenantRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with name: " + name));
    }

    public Tenant findByCode(String code) {
        return tenantRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with code: " + code));
    }
}
