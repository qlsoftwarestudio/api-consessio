package com.concessio.crm.document.repository;

import com.concessio.crm.document.model.DocumentType;
import com.concessio.crm.document.model.LeadDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadDocumentRepository extends JpaRepository<LeadDocument, Long> {

    List<LeadDocument> findByTenantId(Long tenantId);

    List<LeadDocument> findByLeadId(Long leadId);

    List<LeadDocument> findByTenantIdAndLeadId(Long tenantId, Long leadId);

    List<LeadDocument> findByLeadIdAndType(Long leadId, DocumentType type);

    List<LeadDocument> findByLeadIdAndVerified(Long leadId, boolean verified);

    @Query("SELECT ld.type, COUNT(ld) FROM LeadDocument ld WHERE ld.lead.id = :leadId GROUP BY ld.type")
    List<Object[]> countByTypeForLead(@Param("leadId") Long leadId);

    @Query("SELECT ld.type FROM LeadDocument ld WHERE ld.lead.id = :leadId AND ld.verified = true")
    List<DocumentType> findVerifiedTypesByLeadId(@Param("leadId") Long leadId);

    Optional<LeadDocument> findByIdAndTenantId(Long id, Long tenantId);

    long countByLeadId(Long leadId);

    long countByLeadIdAndVerified(Long leadId, boolean verified);
}
