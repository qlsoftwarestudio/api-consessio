package com.concessio.crm.excel.controller;

import com.concessio.crm.excel.dto.ExcelProcessResult;
import com.concessio.crm.excel.model.ExcelUpload;
import com.concessio.crm.excel.model.ExcelUploadStatus;
import com.concessio.crm.excel.repository.ExcelUploadRepository;
import com.concessio.crm.excel.service.ExcelProcessorService;
import com.concessio.crm.tenant.TenantContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelUploadController {

    private final ExcelUploadRepository excelUploadRepository;
    private final ExcelProcessorService excelProcessorService;

    public ExcelUploadController(ExcelUploadRepository excelUploadRepository, 
                                  ExcelProcessorService excelProcessorService) {
        this.excelUploadRepository = excelUploadRepository;
        this.excelProcessorService = excelProcessorService;
    }

    @GetMapping("/uploads")
    public ResponseEntity<List<ExcelUpload>> getAllUploads() {
        Long tenantId = TenantContext.getCurrentTenant();
        List<ExcelUpload> uploads = excelUploadRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        return ResponseEntity.ok(uploads);
    }

    @GetMapping("/uploads/status/{status}")
    public ResponseEntity<List<ExcelUpload>> getUploadsByStatus(@PathVariable ExcelUploadStatus status) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<ExcelUpload> uploads = excelUploadRepository.findByTenantIdAndStatus(tenantId, status);
        return ResponseEntity.ok(uploads);
    }

    @GetMapping("/uploads/{id}")
    public ResponseEntity<ExcelUpload> getUploadById(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        return excelUploadRepository.findByIdAndTenantId(id, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/uploads")
    public ResponseEntity<ExcelUpload> registerUpload(@RequestBody ExcelUpload upload, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        upload.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        upload.setUploadedBy(new com.concessio.crm.user.model.User() {{ setId(userId); }});
        upload.setStatus(ExcelUploadStatus.UPLOADED);
        ExcelUpload saved = excelUploadRepository.save(upload);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/uploads/{id}/process")
    public ResponseEntity<ExcelUpload> markAsProcessing(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        return excelUploadRepository.findByIdAndTenantId(id, tenantId)
                .map(upload -> {
                    upload.setStatus(ExcelUploadStatus.PROCESSING);
                    ExcelUpload updated = excelUploadRepository.save(upload);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/uploads/{id}/complete")
    public ResponseEntity<ExcelUpload> markAsCompleted(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        return excelUploadRepository.findByIdAndTenantId(id, tenantId)
                .map(upload -> {
                    upload.setStatus(ExcelUploadStatus.COMPLETED);
                    upload.setProcessedAt(java.time.LocalDateTime.now());
                    ExcelUpload updated = excelUploadRepository.save(upload);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/uploads/{id}/error")
    public ResponseEntity<ExcelUpload> markAsError(@PathVariable Long id, @RequestBody String errors) {
        Long tenantId = TenantContext.getCurrentTenant();
        return excelUploadRepository.findByIdAndTenantId(id, tenantId)
                .map(upload -> {
                    upload.setStatus(ExcelUploadStatus.ERROR);
                    upload.setErrors(errors);
                    ExcelUpload updated = excelUploadRepository.save(upload);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] templateBytes = excelProcessorService.generateTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "template_leads_giamma.xlsx");
        headers.setContentLength(templateBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndProcessExcel(@RequestParam("file") MultipartFile file, 
                                                    @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }
        
        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            return ResponseEntity.badRequest().body("Solo se aceptan archivos Excel (.xlsx o .xls)");
        }
        
        try {
            // Register upload
            ExcelUpload upload = new ExcelUpload();
            upload.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
            upload.setUploadedBy(new com.concessio.crm.user.model.User() {{ setId(userId); }});
            upload.setFileName(file.getOriginalFilename());
            upload.setFileSize(file.getSize());
            upload.setStatus(ExcelUploadStatus.PROCESSING);
            ExcelUpload savedUpload = excelUploadRepository.save(upload);
            
            // Process file
            ExcelProcessResult result = excelProcessorService.processExcelFile(file, tenantId, userId);
            
            // Update upload status
            savedUpload.setStatus(result.getErrorRows() > 0 ? ExcelUploadStatus.COMPLETED : ExcelUploadStatus.COMPLETED);
            savedUpload.setTotalRows(result.getTotalRows());
            savedUpload.setValidRows(result.getSuccessRows());
            savedUpload.setInvalidRows(result.getErrorRows());
            savedUpload.setDuplicateRows(result.getDuplicateRows());
            savedUpload.setProcessedAt(java.time.LocalDateTime.now());
            if (!result.getErrors().isEmpty()) {
                savedUpload.setErrors(result.getErrors().toString());
            }
            excelUploadRepository.save(savedUpload);
            
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error leyendo archivo Excel: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error procesando archivo: " + e.getMessage());
        }
    }
}
