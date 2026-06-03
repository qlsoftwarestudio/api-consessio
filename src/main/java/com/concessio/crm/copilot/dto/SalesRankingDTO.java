package com.concessio.crm.copilot.dto;

import java.math.BigDecimal;

/**
 * Posicion de un vendedor en el ranking comercial del periodo.
 * revenue = plata generada por las ventas (suma del precio final cotizado).
 */
public record SalesRankingDTO(
        Long userId,
        String name,
        long leadsAssigned,
        long activitiesCount,
        long sales,
        double conversionRate,
        BigDecimal revenue
) {}
