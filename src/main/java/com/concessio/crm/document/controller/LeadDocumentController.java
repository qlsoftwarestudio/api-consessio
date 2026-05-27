package com.concessio.crm.document.controller;

import com.concessio.crm.document.model.DocumentType;
import com.concessio.crm.document.model.LeadDocument;
import com.concessio.crm.document.service.LeadDocumentService;
import com.concessio.crm.tenant.TenantContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class LeadDocumentController {

    private final LeadDocumentService leadDocumentService;

    public LeadDocumentController(LeadDocumentService leadDocumentService) {
        this.leadDocumentService = leadDocumentService;
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<LeadDocument>> getDocumentsByLead(@PathVariable Long leadId) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<LeadDocument> documents = leadDocumentService.findByLead(tenantId, leadId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/lead/{leadId}/type/{type}")
    public ResponseEntity<List<LeadDocument>> getDocumentsByLeadAndType(
            @PathVariable Long leadId, 
            @PathVariable DocumentType type) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<LeadDocument> documents = leadDocumentService.findByLeadAndType(tenantId, leadId, type);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/lead/{leadId}/verified")
    public ResponseEntity<List<LeadDocument>> getVerifiedDocuments(@PathVariable Long leadId) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<LeadDocument> documents = leadDocumentService.findVerifiedByLead(tenantId, leadId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/lead/{leadId}/checklist")
    public ResponseEntity<List<DocumentType>> getDocumentChecklist(@PathVariable Long leadId) {
        // Retorna lista de tipos de documentos que YA están verificados
        Long tenantId = TenantContext.getCurrentTenant();
        List<DocumentType> verifiedTypes = leadDocumentService.getVerifiedTypes(tenantId, leadId);
        return ResponseEntity.ok(verifiedTypes);
    }

    @GetMapping("/lead/{leadId}/stats")
    public ResponseEntity<LeadDocumentService.DocumentStats> getDocumentStats(@PathVariable Long leadId) {
        Long tenantId = TenantContext.getCurrentTenant();
        LeadDocumentService.DocumentStats stats = leadDocumentService.getDocumentStats(tenantId, leadId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LeadDocument> uploadDocument(
            @RequestPart("document") LeadDocument document,
            @RequestPart("file") MultipartFile file,
            @RequestAttribute Long userId) throws IOException {
        Long tenantId = TenantContext.getCurrentTenant();
        LeadDocument saved = leadDocumentService.uploadDocument(
            document, 
            file.getBytes(), 
            file.getOriginalFilename(),
            tenantId, 
            userId
        );
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        LeadDocument document = leadDocumentService.findById(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        
        byte[] content = leadDocumentService.getFileContent(id, tenantId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(content);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<LeadDocument> verifyDocument(@PathVariable Long id, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            LeadDocument updated = leadDocumentService.verifyDocument(id, tenantId, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            leadDocumentService.deleteDocument(id, tenantId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
