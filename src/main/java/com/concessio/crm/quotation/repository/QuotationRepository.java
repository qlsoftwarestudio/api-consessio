package com.concessio.crm.quotation.repository;

import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByTenantId(Long tenantId);

    Page<Quotation> findByTenantId(Long tenantId, Pageable pageable);

    List<Quotation> findByLeadId(Long leadId);

    List<Quotation> findByTenantIdAndLeadId(Long tenantId, Long leadId);

    Page<Quotation> findByTenantIdAndLeadId(Long tenantId, Long leadId, Pageable pageable);

    List<Quotation> findByCreatedById(Long userId);

    List<Quotation> findByTenantIdAndType(Long tenantId, QuotationType type);

    Page<Quotation> findByTenantIdAndType(Long tenantId, QuotationType type, Pageable pageable);

    Optional<Quotation> findByIdAndTenantId(Long id, Long tenantId);

    long countByTenantId(Long tenantId);

    long countByLeadId(Long leadId);
}
