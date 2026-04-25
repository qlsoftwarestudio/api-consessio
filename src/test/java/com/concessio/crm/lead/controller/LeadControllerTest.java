package com.concessio.crm.lead.controller;

import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import com.concessio.crm.lead.service.LeadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadControllerTest {

    @Mock
    private LeadService leadService;

    @InjectMocks
    private LeadController leadController;

    private Lead lead;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        lead = new Lead();
        lead.setId(1L);
        lead.setFirstName("Juan");
        lead.setLastName("Pérez");
        lead.setStatus(LeadStatus.NUEVO);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAllLeads_Success() {
        Page<Lead> page = new PageImpl<>(List.of(lead), pageable, 1);
        when(leadService.findAllByTenant(1L, pageable)).thenReturn(page);

        // Simulate tenant context
        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Lead>> response = leadController.getAllLeads(pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(leadService, times(1)).findAllByTenant(1L, pageable);
    }

    @Test
    void testGetLeadsByStatus_Success() {
        Page<Lead> page = new PageImpl<>(List.of(lead), pageable, 1);
        when(leadService.findByStatus(1L, LeadStatus.NUEVO, pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Lead>> response = leadController.getLeadsByStatus(LeadStatus.NUEVO, pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetMyLeads_Success() {
        Page<Lead> page = new PageImpl<>(List.of(lead), pageable, 1);
        when(leadService.findByAssignedUser(1L, 2L, pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Lead>> response = leadController.getMyLeads(2L, pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testSearchLeads_Success() {
        Page<Lead> page = new PageImpl<>(List.of(lead), pageable, 1);
        when(leadService.search(1L, "Juan", pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Lead>> response = leadController.searchLeads("Juan", pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testCreateLead_Success() {
        when(leadService.createLead(any(), eq(1L), eq(2L))).thenReturn(lead);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Lead> response = leadController.createLead(lead, 2L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals("Juan", response.getBody().getFirstName());
    }

    @Test
    void testGetLeadById_Success() {
        when(leadService.findById(1L, 1L)).thenReturn(lead);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Lead> response = leadController.getLeadById(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testUpdateLead_Success() {
        when(leadService.updateLead(eq(1L), any(), eq(1L), eq(2L))).thenReturn(lead);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Lead> response = leadController.updateLead(1L, lead, 2L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteLead_Success() {
        doNothing().when(leadService).deleteLead(1L, 1L);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Void> response = leadController.deleteLead(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testAssignLead_Success() {
        when(leadService.assignToUser(1L, 3L, 1L, 2L)).thenReturn(lead);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Lead> response = leadController.assignLead(1L, 3L, 2L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
    }
}
