package com.concessio.crm.excel.repository;

import com.concessio.crm.excel.model.ExcelUpload;
import com.concessio.crm.excel.model.ExcelUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExcelUploadRepository extends JpaRepository<ExcelUpload, Long> {

    List<ExcelUpload> findByTenantId(Long tenantId);

    List<ExcelUpload> findByUploadedById(Long userId);

    List<ExcelUpload> findByTenantIdAndStatus(Long tenantId, ExcelUploadStatus status);

    List<ExcelUpload> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    Optional<ExcelUpload> findByIdAndTenantId(Long id, Long tenantId);
}
