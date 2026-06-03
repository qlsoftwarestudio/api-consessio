package com.concessio.crm.copilot.service;

import com.concessio.crm.activity.model.Activity;
import com.concessio.crm.activity.repository.ActivityRepository;
import com.concessio.crm.copilot.dto.*;
import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import com.concessio.crm.lead.repository.LeadRepository;
import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.repository.QuotationRepository;
import com.concessio.crm.testdrive.model.TestDrive;
import com.concessio.crm.testdrive.model.TestDriveStatus;
import com.concessio.crm.testdrive.repository.TestDriveRepository;
import com.concessio.crm.user.model.User;
import com.concessio.crm.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copiloto comercial basado en reglas sobre los datos existentes del CRM.
 * No usa modelos de IA externos: deriva prioridades, alertas y recomendaciones
 * de los estados, fechas y actividad de leads y test drives.
 */
@Service
@Transactional(readOnly = true)
public class CopilotService {

    // Estados activos del pipeline (oportunidades vivas)
    private static final Set<LeadStatus> ACTIVE_STATUSES = EnumSet.of(
            LeadStatus.NUEVO,
            LeadStatus.CONTACTADO,
            LeadStatus.COTIZADO,
            LeadStatus.TEST_DRIVE_AGENDADO,
            LeadStatus.TEST_DRIVE_COMPLETADO,
            LeadStatus.NEGOCIACION,
            LeadStatus.RESERVADO,
            LeadStatus.DOCUMENTACION_COMPLETA,
            LeadStatus.NO_CONTESTA
    );

    private static final int HOT_LEAD_THRESHOLD = 60;
    private static final int ABANDONED_DAYS_THRESHOLD = 5;

    private final LeadRepository leadRepository;
    private final ActivityRepository activityRepository;
    private final TestDriveRepository testDriveRepository;
    private final UserRepository userRepository;
    private final QuotationRepository quotationRepository;

    public CopilotService(LeadRepository leadRepository,
                          ActivityRepository activityRepository,
                          TestDriveRepository testDriveRepository,
                          UserRepository userRepository,
                          QuotationRepository quotationRepository) {
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
        this.testDriveRepository = testDriveRepository;
        this.userRepository = userRepository;
        this.quotationRepository = quotationRepository;
    }

    // ==================== LEADS CALIENTES ====================

    public List<HotLeadDTO> getHotLeads(Long tenantId, int limit) {
        return leadRepository.findByTenantId(tenantId).stream()
                .filter(l -> ACTIVE_STATUSES.contains(l.getStatus()))
                .map(this::toHotLead)
                .filter(h -> h.score() >= HOT_LEAD_THRESHOLD)
                .sorted(Comparator.comparingInt(HotLeadDTO::score).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private HotLeadDTO toHotLead(Lead lead) {
        int score = scoreLead(lead);
        String reason = buildScoreReason(lead, score);
        return new HotLeadDTO(
                lead.getId(),
                lead.getFullName(),
                lead.getPhone(),
                lead.getStatus(),
                score,
                reason,
                nextActionLabel(lead.getStatus()),
                assignedName(lead),
                lead.getVehicleInterest(),
                lead.getLastContactAt()
        );
    }

    private int scoreLead(Lead lead) {
        int base = switch (lead.getStatus()) {
            case RESERVADO -> 90;
            case DOCUMENTACION_COMPLETA -> 85;
            case NEGOCIACION -> 80;
            case TEST_DRIVE_COMPLETADO -> 70;
            case COTIZADO -> 60;
            case TEST_DRIVE_AGENDADO -> 55;
            case CONTACTADO -> 40;
            case NUEVO -> 30;
            case NO_CONTESTA -> 20;
            default -> 0;
        };

        long days = daysSinceLastContact(lead);
        int recency;
        if (days <= 2) recency = 10;
        else if (days <= 7) recency = 0;
        else if (days <= 14) recency = -10;
        else recency = -20;

        return Math.max(0, Math.min(100, base + recency));
    }

    private String buildScoreReason(Lead lead, int score) {
        long days = daysSinceLastContact(lead);
        String etapa = switch (lead.getStatus()) {
            case RESERVADO -> "tiene sena pagada";
            case DOCUMENTACION_COMPLETA -> "tiene la documentacion lista";
            case NEGOCIACION -> "esta negociando terminos";
            case TEST_DRIVE_COMPLETADO -> "ya probo el auto";
            case COTIZADO -> "recibio cotizacion";
            case TEST_DRIVE_AGENDADO -> "tiene test drive agendado";
            case CONTACTADO -> "fue contactado";
            case NUEVO -> "es un lead nuevo";
            case NO_CONTESTA -> "no responde aun";
            default -> "en proceso";
        };
        String tiempo = days <= 2 ? "con contacto reciente"
                : days <= 7 ? "contactado esta semana"
                : "sin contacto hace " + days + " dias";
        return "Score " + score + ": " + etapa + " y " + tiempo + ".";
    }

    // ==================== LEADS ABANDONADOS ====================

    public List<AbandonedLeadDTO> getAbandonedLeads(Long tenantId, int days) {
        int threshold = days > 0 ? days : ABANDONED_DAYS_THRESHOLD;
        return leadRepository.findByTenantId(tenantId).stream()
                .filter(l -> ACTIVE_STATUSES.contains(l.getStatus()))
                .filter(l -> daysSinceLastContact(l) >= threshold)
                .map(l -> new AbandonedLeadDTO(
                        l.getId(),
                        l.getFullName(),
                        l.getPhone(),
                        l.getStatus(),
                        daysSinceLastContact(l),
                        assignedName(l),
                        nextActionLabel(l.getStatus())
                ))
                .sorted(Comparator.comparingLong(AbandonedLeadDTO::daysSinceLastContact).reversed())
                .collect(Collectors.toList());
    }

    // ==================== PROXIMAS ACCIONES ====================

    public List<NextActionDTO> getNextActions(Long tenantId, Long userId, int limit) {
        List<Lead> leads = (userId != null)
                ? leadRepository.findByTenantIdAndAssignedToId(tenantId, userId)
                : leadRepository.findByTenantId(tenantId);

        return leads.stream()
                .filter(l -> ACTIVE_STATUSES.contains(l.getStatus()))
                .sorted(Comparator.comparingInt(this::scoreLead).reversed())
                .limit(limit)
                .map(this::toNextAction)
                .collect(Collectors.toList());
    }

    private NextActionDTO toNextAction(Lead lead) {
        return new NextActionDTO(
                lead.getId(),
                lead.getFullName(),
                lead.getStatus(),
                nextActionLabel(lead.getStatus()),
                actionPriority(lead.getStatus()),
                suggestedMessage(lead)
        );
    }

    private String nextActionLabel(LeadStatus status) {
        return switch (status) {
            case NUEVO -> "Primer contacto: llamar o enviar WhatsApp";
            case CONTACTADO -> "Enviar cotizacion del modelo de interes";
            case COTIZADO -> "Agendar un test drive";
            case TEST_DRIVE_AGENDADO -> "Confirmar asistencia al test drive";
            case TEST_DRIVE_COMPLETADO -> "Avanzar a negociacion y enviar propuesta";
            case NEGOCIACION -> "Cerrar la reserva con sena";
            case RESERVADO -> "Completar la documentacion del comprador";
            case DOCUMENTACION_COMPLETA -> "Coordinar fecha de entrega";
            case NO_CONTESTA -> "Reintentar contacto por otro canal";
            default -> "Revisar el lead";
        };
    }

    private String actionPriority(LeadStatus status) {
        return switch (status) {
            case NEGOCIACION, RESERVADO, TEST_DRIVE_COMPLETADO, COTIZADO -> "ALTA";
            case NUEVO, TEST_DRIVE_AGENDADO, DOCUMENTACION_COMPLETA -> "MEDIA";
            default -> "BAJA";
        };
    }

    private String suggestedMessage(Lead lead) {
        String nombre = lead.getFirstName() != null ? lead.getFirstName() : "";
        String modelo = lead.getVehicleInterest() != null && !lead.getVehicleInterest().isBlank()
                ? lead.getVehicleInterest() : "el vehiculo que te interesa";
        return switch (lead.getStatus()) {
            case NUEVO -> "Hola " + nombre + ", soy de la concesionaria. Vi tu interes en "
                    + modelo + ". Cuando tenes un momento para que te asesore?";
            case CONTACTADO -> "Hola " + nombre + ", te preparo una cotizacion de " + modelo
                    + " para que la veas con numeros claros. Te la envio hoy?";
            case COTIZADO -> "Hola " + nombre + ", te gustaria probar " + modelo
                    + " con un test drive sin compromiso? Tengo turnos esta semana.";
            case TEST_DRIVE_AGENDADO -> "Hola " + nombre + ", te confirmo tu test drive de " + modelo
                    + ". Seguis disponible en el horario acordado?";
            case TEST_DRIVE_COMPLETADO -> "Hola " + nombre + ", que te parecio " + modelo
                    + "? Te paso una propuesta para avanzar con la compra?";
            case NEGOCIACION -> "Hola " + nombre + ", podemos cerrar la operacion de " + modelo
                    + " con una sena para reservar la unidad. Avanzamos?";
            case RESERVADO -> "Hola " + nombre + ", para avanzar con la entrega necesitamos completar "
                    + "la documentacion. Te paso el listado de lo que falta?";
            case DOCUMENTACION_COMPLETA -> "Hola " + nombre + ", ya tenemos todo listo. "
                    + "Coordinamos la fecha de entrega de " + modelo + "?";
            case NO_CONTESTA -> "Hola " + nombre + ", intente comunicarme sin suerte. "
                    + "Preferis que te escriba por este medio?";
            default -> "Hola " + nombre + ", retomo el contacto para ayudarte con tu compra.";
        };
    }

    // ==================== RANKING DE VENDEDORES ====================

    public List<SalesRankingDTO> getSalesRanking(Long tenantId, int days) {
        LocalDateTime start = LocalDate.now().minusDays(days > 0 ? days : 30).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        List<User> users = userRepository
                .findByTenantIdAndIsActiveTrue(tenantId, PageRequest.of(0, 1000))
                .getContent();

        List<Lead> leads = leadRepository.findByTenantId(tenantId);

        // Conteo de actividades por usuario en el periodo
        Map<Long, Long> activitiesByUser = activityRepository
                .findByTenantIdAndDateRange(tenantId, start, end).stream()
                .filter(a -> a.getUser() != null && a.getUser().getId() != null)
                .collect(Collectors.groupingBy(a -> a.getUser().getId(), Collectors.counting()));

        // Precio final por lead (cotizacion mas reciente) para valorizar ventas
        Map<Long, BigDecimal> priceByLead = bestPriceByLead(tenantId);

        List<SalesRankingDTO> ranking = new ArrayList<>();
        for (User u : users) {
            List<Lead> assigned = leads.stream()
                    .filter(l -> l.getAssignedTo() != null && u.getId().equals(l.getAssignedTo().getId()))
                    .toList();
            long leadsAssigned = assigned.size();
            List<Lead> soldLeads = assigned.stream()
                    .filter(this::isSale)
                    .toList();
            long sales = soldLeads.size();
            BigDecimal revenue = soldLeads.stream()
                    .map(l -> priceByLead.getOrDefault(l.getId(), BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long activities = activitiesByUser.getOrDefault(u.getId(), 0L);
            double conversion = leadsAssigned > 0 ? (double) sales / leadsAssigned * 100.0 : 0.0;

            ranking.add(new SalesRankingDTO(
                    u.getId(),
                    u.getName() + " " + u.getLastname(),
                    leadsAssigned,
                    activities,
                    sales,
                    Math.round(conversion * 10.0) / 10.0,
                    revenue
            ));
        }

        ranking.sort(Comparator
                .comparing(SalesRankingDTO::revenue).reversed()
                .thenComparing(Comparator.comparingLong(SalesRankingDTO::sales).reversed())
                .thenComparing(Comparator.comparingLong(SalesRankingDTO::activitiesCount).reversed()));
        return ranking;
    }

    /** Mapa leadId -> precio final de su cotizacion mas reciente (para valorizar ventas). */
    private Map<Long, BigDecimal> bestPriceByLead(Long tenantId) {
        Map<Long, BigDecimal> result = new HashMap<>();
        Map<Long, LocalDateTime> latestAt = new HashMap<>();
        for (Quotation q : quotationRepository.findByTenantId(tenantId)) {
            if (q.getLead() == null || q.getLead().getId() == null || q.getPriceFinal() == null) continue;
            Long leadId = q.getLead().getId();
            LocalDateTime createdAt = q.getCreatedAt() != null ? q.getCreatedAt() : LocalDateTime.MIN;
            LocalDateTime current = latestAt.get(leadId);
            if (current == null || createdAt.isAfter(current)) {
                latestAt.put(leadId, createdAt);
                result.put(leadId, q.getPriceFinal());
            }
        }
        return result;
    }

    private boolean isSale(Lead lead) {
        return lead.getStatus() == LeadStatus.ENTREGADO || lead.getStatus() == LeadStatus.RESERVADO;
    }

    // ==================== RESUMEN DIARIO ====================

    public DailySummaryDTO getDailySummary(Long tenantId) {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        List<Lead> leads = leadRepository.findByTenantId(tenantId);

        long newLeadsToday = leads.stream()
                .filter(l -> l.getCreatedAt() != null
                        && !l.getCreatedAt().isBefore(dayStart)
                        && l.getCreatedAt().isBefore(dayEnd))
                .count();

        long hotLeads = getHotLeads(tenantId, Integer.MAX_VALUE).size();
        long abandoned = getAbandonedLeads(tenantId, ABANDONED_DAYS_THRESHOLD).size();

        List<TestDrive> testDrivesToday = testDriveRepository
                .findByTenantIdAndDateRange(tenantId, dayStart, dayEnd);
        long tdToday = testDrivesToday.size();

        long pendingTd = testDriveRepository.countByTenantIdAndStatus(tenantId, TestDriveStatus.AGENDADO)
                + testDriveRepository.countByTenantIdAndStatus(tenantId, TestDriveStatus.CONFIRMADO);

        List<Lead> soldThisMonth = leads.stream()
                .filter(l -> l.getStatus() == LeadStatus.ENTREGADO)
                .filter(l -> l.getUpdatedAt() != null && !l.getUpdatedAt().isBefore(monthStart))
                .toList();
        long salesThisMonth = soldThisMonth.size();

        Map<Long, BigDecimal> priceByLead = bestPriceByLead(tenantId);
        BigDecimal revenueThisMonth = soldThisMonth.stream()
                .map(l -> priceByLead.getOrDefault(l.getId(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> highlights = new ArrayList<>();
        if (hotLeads > 0) highlights.add(hotLeads + " leads calientes para contactar hoy");
        if (abandoned > 0) highlights.add(abandoned + " oportunidades sin actividad reciente");
        if (tdToday > 0) highlights.add(tdToday + " test drives agendados para hoy");
        if (newLeadsToday > 0) highlights.add(newLeadsToday + " leads nuevos ingresaron hoy");
        if (salesThisMonth > 0) highlights.add(salesThisMonth + " ventas concretadas este mes");
        if (revenueThisMonth.signum() > 0) highlights.add("$" + revenueThisMonth.toPlainString() + " facturados este mes");
        if (highlights.isEmpty()) highlights.add("Sin alertas criticas. Buen momento para reactivar leads frios.");

        String headline = buildHeadline(hotLeads, abandoned, tdToday);

        return new DailySummaryDTO(
                today,
                newLeadsToday,
                hotLeads,
                abandoned,
                tdToday,
                pendingTd,
                salesThisMonth,
                revenueThisMonth,
                headline,
                highlights
        );
    }

    private String buildHeadline(long hot, long abandoned, long tdToday) {
        if (hot > 0 && tdToday > 0) {
            return "Tenes " + hot + " leads calientes y " + tdToday
                    + " test drives hoy. Priorizalos para no perder ventas.";
        }
        if (hot > 0) {
            return "Tenes " + hot + " leads calientes esperando una accion. Empeza por ellos.";
        }
        if (abandoned > 0) {
            return "Hay " + abandoned + " oportunidades enfriandose. Reactivalas hoy.";
        }
        return "Dia tranquilo: aprovecha para reactivar leads frios y cargar seguimientos.";
    }

    // ==================== HELPERS ====================

    private long daysSinceLastContact(Lead lead) {
        LocalDateTime ref = lead.getLastContactAt() != null
                ? lead.getLastContactAt()
                : lead.getCreatedAt();
        if (ref == null) return 0;
        return Math.max(0, Duration.between(ref, LocalDateTime.now()).toDays());
    }

    private String assignedName(Lead lead) {
        User u = lead.getAssignedTo();
        if (u == null) return "Sin asignar";
        return u.getName() + " " + u.getLastname();
    }
}
