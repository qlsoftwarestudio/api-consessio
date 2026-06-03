package com.concessio.crm.copilot.dto;

import com.concessio.crm.lead.model.LeadStatus;

import java.time.LocalDateTime;

/**
 * Lead caliente detectado por el copiloto comercial.
 * El score (0-100) y la razon se calculan por reglas sobre datos existentes.
 */
public record HotLeadDTO(
        Long leadId,
        String fullName,
        String phone,
        LeadStatus status,
        int score,
        String reason,
        String suggestedAction,
        String assignedToName,
        String vehicleInterest,
        LocalDateTime lastContactAt
) {}
