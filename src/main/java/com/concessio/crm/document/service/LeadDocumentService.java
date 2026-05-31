package com.concessio.crm.document.service;

import com.concessio.crm.document.model.DocumentType;
import com.concessio.crm.document.model.LeadDocument;
import com.concessio.crm.document.repository.LeadDocumentRepository;
import com.concessio.crm.storage.StorageService;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LeadDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(LeadDocumentService.class);

    private final LeadDocumentRepository leadDocumentRepository;
    private final StorageService storageService;

    public LeadDocumentService(LeadDocumentRepository leadDocumentRepository, StorageService storageService) {
        this.leadDocumentRepository = leadDocumentRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public List<LeadDocument> findByLead(Long tenantId, Long leadId) {
        return leadDocumentRepository.findByTenantIdAndLeadId(tenantId, leadId);
    }

    @Transactional(readOnly = true)
    public List<LeadDocument> findByLeadAndType(Long tenantId, Long leadId, DocumentType type) {
        return leadDocumentRepository.findByTenantIdAndLeadIdAndType(tenantId, leadId, type);
    }

    @Transactional(readOnly = true)
    public List<LeadDocument> findVerifiedByLead(Long tenantId, Long leadId) {
        return leadDocumentRepository.findByTenantIdAndLeadIdAndVerified(tenantId, leadId, true);
    }

    @Transactional(readOnly = true)
    public Optional<LeadDocument> findById(Long id, Long tenantId) {
        return leadDocumentRepository.findByIdAndTenantId(id, tenantId);
    }

    @Transactional(readOnly = true)
    public DocumentStats getDocumentStats(Long tenantId, Long leadId) {
        long total = leadDocumentRepository.countByTenantIdAndLeadId(tenantId, leadId);
        long verified = leadDocumentRepository.countByTenantIdAndLeadIdAndVerified(tenantId, leadId, true);
        return new DocumentStats(total, verified, total - verified);
    }

    @Transactional(readOnly = true)
    public List<DocumentType> getVerifiedTypes(Long tenantId, Long leadId) {
        return leadDocumentRepository.findVerifiedTypesByTenantIdAndLeadId(tenantId, leadId);
    }

    public LeadDocument uploadDocument(LeadDocument document, byte[] fileContent, 
                                       String originalFilename, Long tenantId, Long userId) {
        // Validaciones
        if (document.getLead() == null || document.getLead().getId() == null) {
            throw new IllegalArgumentException("El documento debe estar asociado a un lead");
        }
        
        if (document.getType() == null) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio");
        }

        // Generar nombre único para el archivo
        String fileExtension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + fileExtension;
        String storageKey = "tenant_" + tenantId + "/lead_" + document.getLead().getId() + "/" + storedFilename;

        // Subir archivo al storage (local o R2)
        String mimeType = detectMimeType(originalFilename);
        storageService.uploadFile(storageKey, fileContent, mimeType);

        logger.info("Archivo guardado: {} para tenant {} lead {}",
                   storedFilename, tenantId, document.getLead().getId());

        // Configurar metadatos del documento
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        document.setTenant(tenant);

        User uploadedBy = new User();
        uploadedBy.setId(userId);
        document.setUploadedBy(uploadedBy);

        document.setFileName(originalFilename);
        document.setStoredFilename(storedFilename);
        document.setStorageUrl(storageKey);
        document.setFileSize((long) fileContent.length);
        document.setMimeType(mimeType);
        document.setUploadedAt(LocalDateTime.now());
        document.setVerified(false);

        return leadDocumentRepository.save(document);
    }

    public LeadDocument verifyDocument(Long id, Long tenantId, Long userId) {
        LeadDocument document = leadDocumentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        User verifiedBy = new User();
        verifiedBy.setId(userId);
        
        document.setVerified(true);
        document.setVerifiedBy(verifiedBy);
        document.setVerifiedAt(LocalDateTime.now());

        return leadDocumentRepository.save(document);
    }

    public void deleteDocument(Long id, Long tenantId) {
        LeadDocument document = leadDocumentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        // Eliminar archivo del storage
        storageService.deleteFile(document.getStorageUrl());

        leadDocumentRepository.delete(document);
    }

    public byte[] getFileContent(Long id, Long tenantId) {
        LeadDocument document = leadDocumentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        return storageService.downloadFile(document.getStorageUrl());
    }

    // Métodos auxiliares
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String detectMimeType(String filename) {
        String ext = getFileExtension(filename).toLowerCase();
        return switch (ext) {
            case ".pdf" -> "application/pdf";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".doc" -> "application/msword";
            case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    // Clase interna para estadísticas
    public record DocumentStats(long totalDocs, long verifiedDocs, long pendingDocs) {}
}
