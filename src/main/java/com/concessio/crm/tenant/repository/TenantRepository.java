package com.concessio.crm.tenant.repository;

import com.concessio.crm.tenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByName(String name);
    Optional<Tenant> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
