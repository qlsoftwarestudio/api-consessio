package com.concessio.crm.copilot.dto;

import com.concessio.crm.lead.model.LeadStatus;

/**
 * Proxima mejor accion recomendada para un lead segun su etapa en el pipeline.
 */
public record NextActionDTO(
        Long leadId,
        String fullName,
        LeadStatus status,
        String action,
        String priority,
        String suggestedMessage
) {}
