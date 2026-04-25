package com.concessio.crm.excel.service;

import com.concessio.crm.activity.model.Activity;
import com.concessio.crm.activity.model.ActivityType;
import com.concessio.crm.activity.repository.ActivityRepository;
import com.concessio.crm.excel.dto.ExcelLeadRow;
import com.concessio.crm.excel.dto.ExcelProcessResult;
import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import com.concessio.crm.lead.repository.LeadRepository;
import com.concessio.crm.user.model.User;
import com.concessio.crm.user.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ExcelProcessorService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public ExcelProcessorService(LeadRepository leadRepository, 
                                  UserRepository userRepository,
                                  ActivityRepository activityRepository) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public ExcelProcessResult processExcelFile(MultipartFile file, Long tenantId, Long userId) throws IOException {
        ExcelProcessResult result = new ExcelProcessResult();
        
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        
        int rowCount = 0;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }
            rowCount++;
            
            try {
                ExcelLeadRow leadRow = parseRow(row);
                
                if (!leadRow.isValid()) {
                    result.addError(row.getRowNum(), "VALIDACION", "Fila incompleta: nombre, apellido y telefono son obligatorios");
                    result.setErrorRows(result.getErrorRows() + 1);
                    continue;
                }
                
                // Check for duplicates by DNI or phone
                if (isDuplicate(leadRow, tenantId)) {
                    result.addError(row.getRowNum(), "DUPLICADO", "Lead ya existe con DNI o telefono: " + leadRow.getDni() + "/" + leadRow.getTelefono());
                    result.setDuplicateRows(result.getDuplicateRows() + 1);
                    continue;
                }
                
                // Create lead
                Lead lead = createLeadFromRow(leadRow, tenantId, userId);
                Lead savedLead = leadRepository.save(lead);
                
                // Log activity
                logActivity(savedLead, userId, tenantId);
                
                result.getCreatedLeadIds().add(savedLead.getId());
                result.setSuccessRows(result.getSuccessRows() + 1);
                
            } catch (Exception e) {
                result.addError(row.getRowNum(), "ERROR", "Error procesando fila: " + e.getMessage());
                result.setErrorRows(result.getErrorRows() + 1);
            }
        }
        
        workbook.close();
        result.setTotalRows(rowCount);
        
        return result;
    }
    
    private ExcelLeadRow parseRow(Row row) {
        ExcelLeadRow leadRow = new ExcelLeadRow();
        
        leadRow.setNombre(getCellValue(row.getCell(0)));
        leadRow.setApellido(getCellValue(row.getCell(1)));
        leadRow.setTelefono(getCellValue(row.getCell(2)));
        leadRow.setEmail(getCellValue(row.getCell(3)));
        leadRow.setDni(getCellValue(row.getCell(4)));
        leadRow.setModeloInteres(getCellValue(row.getCell(5)));
        leadRow.setTipoVehiculo(getCellValue(row.getCell(6)));
        leadRow.setOrigen(getCellValue(row.getCell(7)));
        leadRow.setNotas(getCellValue(row.getCell(8)));
        leadRow.setVendedoraAsignada(getCellValue(row.getCell(9)));
        
        return leadRow;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private boolean isDuplicate(ExcelLeadRow row, Long tenantId) {
        // Check by phone
        if (row.getTelefono() != null && !row.getTelefono().isEmpty()) {
            Optional<Lead> existing = leadRepository.findByTenantIdAndPhone(tenantId, row.getTelefono());
            if (existing.isPresent()) {
                return true;
            }
        }
        
        // Check by DNI if available
        if (row.getDni() != null && !row.getDni().isEmpty()) {
            // Note: Would need to add findByDni method to repository if needed
            // For now, phone is the primary duplicate check
        }
        
        return false;
    }
    
    private Lead createLeadFromRow(ExcelLeadRow row, Long tenantId, Long userId) {
        Lead lead = new Lead();
        lead.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        lead.setFirstName(row.getNombre());
        lead.setLastName(row.getApellido());
        lead.setPhone(row.getTelefono());
        lead.setEmail(row.getEmail());
        lead.setVehicleInterest(row.getModeloInteres());
        lead.setSource(row.getOrigen() != null ? row.getOrigen() : "EXCEL_MASIVO");
        lead.setStatus(LeadStatus.NUEVO);
        lead.setNotes(row.getNotas());
        lead.setCreatedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());
        
        // Assign to user if specified and exists
        if (row.getVendedoraAsignada() != null && !row.getVendedoraAsignada().isEmpty()) {
            userRepository.findByEmail(row.getVendedoraAsignada())
                .ifPresent(u -> lead.setAssignedTo(u));
        }
        
        // If no assignment, assign to the user uploading
        if (lead.getAssignedTo() == null) {
            User currentUser = userRepository.findById(userId).orElse(null);
            lead.setAssignedTo(currentUser);
        }
        
        return lead;
    }
    
    private void logActivity(Lead lead, Long userId, Long tenantId) {
        Activity activity = new Activity();
        activity.setLead(lead);
        activity.setUser(new User() {{ setId(userId); }});
        activity.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        activity.setType(ActivityType.EXCEL_UPLOAD);
        activity.setDescription("Lead creado via carga masiva Excel: " + lead.getFirstName() + " " + lead.getLastName());
        activity.setMetadata("{\"source\": \"excel\", \"vehicle\": \"" + lead.getVehicleInterest() + "\"}");
        activity.setCreatedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }
    
    public byte[] generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Leads");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nombre", "Apellido", "Telefono", "Email", "DNI", 
                              "Modelo_Interes", "Tipo_Vehiculo", "Origen", "Notas", "Vendedora_Asignada"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add sample data row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("Juan");
            sampleRow.createCell(1).setCellValue("Perez");
            sampleRow.createCell(2).setCellValue("+54123456789");
            sampleRow.createCell(3).setCellValue("juan.perez@email.com");
            sampleRow.createCell(4).setCellValue("12345678");
            sampleRow.createCell(5).setCellValue("Fiat Cronos Drive");
            sampleRow.createCell(6).setCellValue("Auto");
            sampleRow.createCell(7).setCellValue("EXCEL_MASIVO");
            sampleRow.createCell(8).setCellValue("Interesado en financiacion");
            sampleRow.createCell(9).setCellValue("vendedora@giamma.com");
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Error generating template", e);
        }
    }
}
