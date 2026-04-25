package com.concessio.crm.quotation.controller;

import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import com.concessio.crm.quotation.service.QuotationService;
import com.concessio.crm.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @GetMapping
    public ResponseEntity<Page<Quotation>> getAllQuotations(Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Quotation> quotations = quotationService.findAllByTenant(tenantId, pageable);
        return ResponseEntity.ok(quotations);
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<Page<Quotation>> getQuotationsByLead(@PathVariable Long leadId, Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Quotation> quotations = quotationService.findByLead(tenantId, leadId, pageable);
        return ResponseEntity.ok(quotations);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Quotation>> getQuotationsByType(@PathVariable QuotationType type, Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Quotation> quotations = quotationService.findByType(tenantId, type, pageable);
        return ResponseEntity.ok(quotations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quotation> getQuotationById(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        Quotation quotation = quotationService.findById(id, tenantId);
        return ResponseEntity.ok(quotation);
    }

    @PostMapping
    public ResponseEntity<Quotation> createQuotation(@RequestBody Quotation quotation, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        Quotation saved = quotationService.createQuotation(quotation, tenantId, userId);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quotation> updateQuotation(@PathVariable Long id, @RequestBody Quotation quotation, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        Quotation updated = quotationService.updateQuotation(id, quotation, tenantId, userId);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<Quotation> markAsSent(@PathVariable Long id, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        Quotation updated = quotationService.markAsSent(id, tenantId, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        quotationService.deleteQuotation(id, tenantId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/by-type")
    public ResponseEntity<Map<QuotationType, Long>> getStatsByType() {
        Long tenantId = TenantContext.getCurrentTenant();
        Map<QuotationType, Long> stats = quotationService.getStatsByType(tenantId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/valid")
    public ResponseEntity<Page<Quotation>> getValidQuotations(Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Quotation> quotations = quotationService.getValidQuotations(tenantId, pageable);
        return ResponseEntity.ok(quotations);
    }
}
