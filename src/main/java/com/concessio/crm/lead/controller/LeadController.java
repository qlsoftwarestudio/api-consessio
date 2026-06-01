package com.concessio.crm.lead.controller;

import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import com.concessio.crm.lead.service.LeadService;
import com.concessio.crm.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<Page<Lead>> getAllLeads(Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Lead> leads = leadService.findAllByTenant(tenantId, pageable);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Lead>> getLeadsByStatus(@PathVariable LeadStatus status, Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Lead> leads = leadService.findByStatus(tenantId, status, pageable);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/my-leads")
    public ResponseEntity<Page<Lead>> getMyLeads(@RequestAttribute Long userId, Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Lead> leads = leadService.findByAssignedUser(tenantId, userId, pageable);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<Page<Lead>> getUnassignedLeads(Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Lead> leads = leadService.findUnassigned(tenantId, pageable);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Lead>> searchLeads(@RequestParam String query, Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Lead> leads = leadService.search(tenantId, query, pageable);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        Lead lead = leadService.findById(id, tenantId);
        return ResponseEntity.ok(lead);
    }

    @PostMapping
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        Lead saved = leadService.createLead(lead, tenantId, userId);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lead> updateLead(@PathVariable Long id, @RequestBody Lead lead, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        Lead updated = leadService.updateLead(id, lead, tenantId, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        leadService.deleteLead(id, tenantId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Lead> updateStatus(@PathVariable Long id, @RequestBody LeadStatus status, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        Lead updated = leadService.updateStatus(id, status, tenantId, userId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<Lead> assignLead(@PathVariable Long id, @PathVariable Long userId, @RequestAttribute(name = "userId") Long currentUserId) {
        Long tenantId = TenantContext.getCurrentTenant();
        Lead updated = leadService.assignToUser(id, userId, tenantId, currentUserId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<Map<LeadStatus, Long>> getStatsByStatus() {
        Long tenantId = TenantContext.getCurrentTenant();
        Map<LeadStatus, Long> stats = leadService.getStatsByStatus(tenantId);
        return ResponseEntity.ok(stats);
    }
}
