package com.concessio.crm.copilot.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Resumen diario generado por el copiloto: sintetiza la actividad comercial
 * y entrega highlights accionables en lenguaje simple.
 */
public record DailySummaryDTO(
        LocalDate date,
        long newLeadsToday,
        long hotLeadsCount,
        long abandonedLeadsCount,
        long testDrivesToday,
        long pendingTestDrives,
        long salesThisMonth,
        BigDecimal salesRevenueThisMonth,
        String headline,
        List<String> highlights
) {}
