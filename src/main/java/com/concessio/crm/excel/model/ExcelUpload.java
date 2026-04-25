package com.concessio.crm.excel.model;

import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "excel_uploads")
public class ExcelUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @NotBlank
    private String fileName; // Nombre del archivo Excel

    private Long fileSize; // Tamaño en bytes

    private Integer totalRows; // Total de filas en el Excel

    private Integer validRows; // Filas válidas procesadas

    private Integer invalidRows; // Filas con error

    private Integer duplicateRows; // Duplicados detectados

    @NotNull
    @Enumerated(EnumType.STRING)
    private ExcelUploadStatus status;

    @Column(length = 2000)
    private String columnMapping; // JSON con mapeo de columnas (colA -> nombre, colB -> telefono)

    @Column(length = 5000)
    private String errors; // JSON con errores por fila

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public ExcelUpload() {
        this.createdAt = LocalDateTime.now();
        this.status = ExcelUploadStatus.UPLOADED;
        this.validRows = 0;
        this.invalidRows = 0;
        this.duplicateRows = 0;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Integer getTotalRows() { return totalRows; }
    public void setTotalRows(Integer totalRows) { this.totalRows = totalRows; }

    public Integer getValidRows() { return validRows; }
    public void setValidRows(Integer validRows) { this.validRows = validRows; }

    public Integer getInvalidRows() { return invalidRows; }
    public void setInvalidRows(Integer invalidRows) { this.invalidRows = invalidRows; }

    public Integer getDuplicateRows() { return duplicateRows; }
    public void setDuplicateRows(Integer duplicateRows) { this.duplicateRows = duplicateRows; }

    public ExcelUploadStatus getStatus() { return status; }
    public void setStatus(ExcelUploadStatus status) { this.status = status; }

    public String getColumnMapping() { return columnMapping; }
    public void setColumnMapping(String columnMapping) { this.columnMapping = columnMapping; }

    public String getErrors() { return errors; }
    public void setErrors(String errors) { this.errors = errors; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
