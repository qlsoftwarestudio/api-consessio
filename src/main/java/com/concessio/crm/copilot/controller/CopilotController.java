package com.concessio.crm.copilot.controller;

import com.concessio.crm.copilot.dto.*;
import com.concessio.crm.copilot.service.CopilotService;
import com.concessio.crm.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints del copiloto comercial (reglas sobre datos existentes).
 * Pensados para alimentar el dashboard del frontend.
 */
@RestController
@RequestMapping("/api/copilot")
public class CopilotController {

    private final CopilotService copilotService;

    public CopilotController(CopilotService copilotService) {
        this.copilotService = copilotService;
    }

    /** Resumen diario accionable para arrancar el dia con foco. */
    @GetMapping("/daily-summary")
    public ResponseEntity<DailySummaryDTO> getDailySummary() {
        Long tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(copilotService.getDailySummary(tenantId));
    }

    /** Leads calientes priorizados por score. */
    @GetMapping("/hot-leads")
    public ResponseEntity<List<HotLeadDTO>> getHotLeads(
            @RequestParam(defaultValue = "10") int limit) {
        Long tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(copilotService.getHotLeads(tenantId, limit));
    }

    /** Oportunidades activas sin actividad comercial reciente. */
    @GetMapping("/abandoned-leads")
    public ResponseEntity<List<AbandonedLeadDTO>> getAbandonedLeads(
            @RequestParam(defaultValue = "5") int days) {
        Long tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(copilotService.getAbandonedLeads(tenantId, days));
    }

    /** Proximas mejores acciones para todos los leads del tenant. */
    @GetMapping("/next-actions")
    public ResponseEntity<List<NextActionDTO>> getNextActions(
            @RequestParam(defaultValue = "10") int limit) {
        Long tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(copilotService.getNextActions(tenantId, null, limit));
    }

    /** Proximas acciones recomendadas solo para los leads del usuario actual. */
    @GetMapping("/my-next-actions")
    public ResponseEntity<List<NextActionDTO>> getMyNextActions(
            @RequestAttribute Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        Long tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(copilotService.getNextActions(tenantId, userId, limit));
    }

    /** Ranking de vendedores del periodo (default 30 dias). */
    @GetMapping("/ranking")
    public ResponseEntity<List<SalesRankingDTO>> getRanking(
            @RequestParam(defaultValue = "30") int days) {
        Long tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(copilotService.getSalesRanking(tenantId, days));
    }
}
