package com.concessio.crm.user.repository;

import com.concessio.crm.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAll(Pageable pageable);
    Page<User> findByTenantId(Long tenantId, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByEmailAndTenantId(String email, Long tenantId);
    Optional<User> findByEmail(String email);
    boolean existsByIdAndTenantId(Long id, Long tenantId);
}

