package com.concessio.crm.quotation.controller;

import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import com.concessio.crm.quotation.service.QuotationService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotationControllerTest {

    @Mock
    private QuotationService quotationService;

    @InjectMocks
    private QuotationController quotationController;

    private Quotation quotation;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        quotation = new Quotation();
        quotation.setId(1L);
        quotation.setVehicleModel("Fiat Cronos");
        quotation.setType(QuotationType.CONTADO);
        quotation.setPriceFinal(new BigDecimal("18000000"));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAllQuotations_Success() {
        Page<Quotation> page = new PageImpl<>(List.of(quotation), pageable, 1);
        when(quotationService.findAllByTenant(1L, pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Quotation>> response = quotationController.getAllQuotations(pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetQuotationsByLead_Success() {
        Page<Quotation> page = new PageImpl<>(List.of(quotation), pageable, 1);
        when(quotationService.findByLead(1L, 2L, pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Quotation>> response = quotationController.getQuotationsByLead(2L, pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals("Fiat Cronos", response.getBody().getContent().get(0).getVehicleModel());
    }

    @Test
    void testGetQuotationsByType_Success() {
        Page<Quotation> page = new PageImpl<>(List.of(quotation), pageable, 1);
        when(quotationService.findByType(1L, QuotationType.CONTADO, pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Quotation>> response = quotationController.getQuotationsByType(QuotationType.CONTADO, pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(QuotationType.CONTADO, response.getBody().getContent().get(0).getType());
    }

    @Test
    void testGetQuotationById_Success() {
        when(quotationService.findById(1L, 1L)).thenReturn(quotation);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Quotation> response = quotationController.getQuotationById(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testCreateQuotation_Success() {
        when(quotationService.createQuotation(any(), eq(1L), eq(2L))).thenReturn(quotation);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Quotation> response = quotationController.createQuotation(quotation, 2L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("18000000"), response.getBody().getPriceFinal());
    }

    @Test
    void testMarkAsSent_Success() {
        quotation.setSentToCustomer(true);
        when(quotationService.markAsSent(1L, 1L, 2L)).thenReturn(quotation);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Quotation> response = quotationController.markAsSent(1L, 2L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSentToCustomer());
    }

    @Test
    void testDeleteQuotation_Success() {
        doNothing().when(quotationService).deleteQuotation(1L, 1L);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Void> response = quotationController.deleteQuotation(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetStatsByType_Success() {
        Map<QuotationType, Long> stats = Map.of(
                QuotationType.CONTADO, 10L,
                QuotationType.FINANCIADO, 5L,
                QuotationType.PLAN_FIAT, 3L
        );
        when(quotationService.getStatsByType(1L)).thenReturn(stats);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Map<QuotationType, Long>> response = quotationController.getStatsByType();
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(10L, response.getBody().get(QuotationType.CONTADO));
    }
}
