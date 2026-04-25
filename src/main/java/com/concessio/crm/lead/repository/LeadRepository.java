package com.concessio.crm.lead.repository;

import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByTenantId(Long tenantId);

    Page<Lead> findByTenantId(Long tenantId, Pageable pageable);

    List<Lead> findByTenantIdAndStatus(Long tenantId, LeadStatus status);

    Page<Lead> findByTenantIdAndStatus(Long tenantId, LeadStatus status, Pageable pageable);

    List<Lead> findByAssignedToId(Long userId);

    List<Lead> findByTenantIdAndAssignedToId(Long tenantId, Long userId);

    Page<Lead> findByTenantIdAndAssignedToId(Long tenantId, Long userId, Pageable pageable);

    Optional<Lead> findByIdAndTenantId(Long id, Long tenantId);

    @Query("SELECT l FROM Lead l WHERE l.tenant.id = :tenantId AND l.status IN :statuses")
    List<Lead> findByTenantIdAndStatuses(@Param("tenantId") Long tenantId, @Param("statuses") List<LeadStatus> statuses);

    long countByTenantIdAndStatus(Long tenantId, LeadStatus status);

    @Query("SELECT l.status, COUNT(l) FROM Lead l WHERE l.tenant.id = :tenantId GROUP BY l.status")
    List<Object[]> countByStatusGrouped(@Param("tenantId") Long tenantId);

    Optional<Lead> findByTenantIdAndPhone(Long tenantId, String phone);

    @Query("SELECT l FROM Lead l WHERE l.tenant.id = :tenantId AND " +
           "(LOWER(l.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "l.phone LIKE CONCAT('%', :search, '%') OR " +
           "LOWER(l.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Lead> searchByTenantId(@Param("tenantId") Long tenantId, @Param("search") String search, Pageable pageable);
}
