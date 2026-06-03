package com.concessio.crm.copilot.dto;

import com.concessio.crm.lead.model.LeadStatus;

/**
 * Lead abandonado: oportunidad activa sin actividad comercial reciente.
 */
public record AbandonedLeadDTO(
        Long leadId,
        String fullName,
        String phone,
        LeadStatus status,
        long daysSinceLastContact,
        String assignedToName,
        String suggestedAction
) {}
