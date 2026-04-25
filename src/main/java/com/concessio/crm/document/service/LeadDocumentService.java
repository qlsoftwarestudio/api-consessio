package com.concessio.crm.document.service;

import com.concessio.crm.document.model.DocumentType;
import com.concessio.crm.document.model.LeadDocument;
import com.concessio.crm.document.repository.LeadDocumentRepository;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LeadDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(LeadDocumentService.class);

    private final LeadDocumentRepository leadDocumentRepository;
    
    @Value("${app.storage.path:./uploads}")
    private String storagePath;

    public LeadDocumentService(LeadDocumentRepository leadDocumentRepository) {
        this.leadDocumentRepository = leadDocumentRepository;
    }

    @Transactional(readOnly = true)
    public List<LeadDocument> findByLead(Long tenantId, Long leadId) {
        return leadDocumentRepository.findByTenantIdAndLeadId(tenantId, leadId);
    }

    @Transactional(readOnly = true)
    public List<LeadDocument> findByLeadAndType(Long leadId, DocumentType type) {
        return leadDocumentRepository.findByLeadIdAndType(leadId, type);
    }

    @Transactional(readOnly = true)
    public List<LeadDocument> findVerifiedByLead(Long leadId) {
        return leadDocumentRepository.findByLeadIdAndVerified(leadId, true);
    }

    @Transactional(readOnly = true)
    public Optional<LeadDocument> findById(Long id, Long tenantId) {
        return leadDocumentRepository.findByIdAndTenantId(id, tenantId);
    }

    @Transactional(readOnly = true)
    public DocumentStats getDocumentStats(Long leadId) {
        long total = leadDocumentRepository.countByLeadId(leadId);
        long verified = leadDocumentRepository.countByLeadIdAndVerified(leadId, true);
        return new DocumentStats(total, verified, total - verified);
    }

    @Transactional(readOnly = true)
    public List<DocumentType> getVerifiedTypes(Long leadId) {
        return leadDocumentRepository.findVerifiedTypesByLeadId(leadId);
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
        
        // Crear estructura de carpetas por tenant
        Path tenantPath = Paths.get(storagePath, "tenant_" + tenantId);
        Path leadPath = tenantPath.resolve("lead_" + document.getLead().getId());
        
        try {
            // Crear directorios si no existen
            Files.createDirectories(leadPath);
            
            // Guardar archivo físico
            Path filePath = leadPath.resolve(storedFilename);
            Files.write(filePath, fileContent);
            
            logger.info("Archivo guardado: {} para tenant {} lead {}", 
                       storedFilename, tenantId, document.getLead().getId());
            
        } catch (IOException e) {
            logger.error("Error al guardar archivo: {}", e.getMessage());
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }

        // Configurar metadatos del documento
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        document.setTenant(tenant);

        User uploadedBy = new User();
        uploadedBy.setId(userId);
        document.setUploadedBy(uploadedBy);

        document.setFileName(originalFilename);
        document.setStoredFilename(storedFilename);
        document.setStorageUrl(leadPath.resolve(storedFilename).toString());
        document.setFileSize((long) fileContent.length);
        document.setMimeType(detectMimeType(originalFilename));
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

        // Eliminar archivo físico
        try {
            Path filePath = Paths.get(document.getStorageUrl());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Archivo eliminado: {}", document.getStoredFilename());
            }
        } catch (IOException e) {
            logger.error("Error al eliminar archivo físico: {}", e.getMessage());
            // Continuar con la eliminación del registro aunque falle el archivo
        }

        leadDocumentRepository.delete(document);
    }

    public byte[] getFileContent(Long id, Long tenantId) {
        LeadDocument document = leadDocumentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        try {
            Path filePath = Paths.get(document.getStorageUrl());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            logger.error("Error al leer archivo: {}", e.getMessage());
            throw new RuntimeException("No se pudo leer el archivo", e);
        }
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
