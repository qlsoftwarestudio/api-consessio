package com.concessio.crm.lead.service;

import com.concessio.crm.activity.repository.ActivityRepository;
import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import com.concessio.crm.lead.repository.LeadRepository;
import com.concessio.crm.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private LeadService leadService;

    private Lead lead;

    @BeforeEach
    void setUp() {
        lead = new Lead();
        lead.setId(1L);
        lead.setFirstName("Juan");
        lead.setLastName("Pérez");
        lead.setPhone("+5491123456789");
        lead.setEmail("juan@test.com");
        lead.setVehicleInterest("Fiat Cronos");
        lead.setStatus(LeadStatus.NUEVO);
    }

    @Test
    void testCreateLead_Success() {
        when(leadRepository.save(any())).thenReturn(lead);
        when(activityRepository.save(any())).thenReturn(null);

        Lead result = leadService.createLead(lead, 1L, 1L);

        assertNotNull(result);
        assertEquals(LeadStatus.NUEVO, result.getStatus());
        assertNotNull(result.getCreatedAt());
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    void testUpdateStatus_Success() {
        when(leadRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any())).thenReturn(lead);
        when(activityRepository.save(any())).thenReturn(null);

        Lead result = leadService.updateStatus(1L, LeadStatus.COTIZADO, 1L, 1L);

        assertEquals(LeadStatus.COTIZADO, result.getStatus());
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    void testAssignToUser_Success() {
        when(leadRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any())).thenReturn(lead);
        when(activityRepository.save(any())).thenReturn(null);

        Lead result = leadService.assignToUser(1L, 2L, 1L, 1L);

        assertNotNull(result.getAssignedTo());
        assertEquals(2L, result.getAssignedTo().getId());
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    void testUpdateStatus_SameStatus_NoActivity() {
        // Si el status no cambia, no debe loguear actividad
        lead.setStatus(LeadStatus.COTIZADO);
        when(leadRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(lead));

        Lead result = leadService.updateStatus(1L, LeadStatus.COTIZADO, 1L, 1L);

        assertEquals(LeadStatus.COTIZADO, result.getStatus());
        verify(activityRepository, never()).save(any());
    }

    @Test
    void testUpdateStatus_ToEntregado_SetsUpdatedAt() {
        when(leadRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any())).thenReturn(lead);
        when(activityRepository.save(any())).thenReturn(null);

        Lead result = leadService.updateStatus(1L, LeadStatus.ENTREGADO, 1L, 1L);

        assertEquals(LeadStatus.ENTREGADO, result.getStatus());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        when(leadRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(com.concessio.crm.exceptions.ResourceNotFoundException.class,
                () -> leadService.findById(1L, 1L));
    }
}
