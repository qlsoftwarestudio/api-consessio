package com.concessio.crm.quotation.service;

import com.concessio.crm.activity.model.Activity;
import com.concessio.crm.activity.model.ActivityType;
import com.concessio.crm.activity.repository.ActivityRepository;
import com.concessio.crm.exceptions.ResourceNotFoundException;
import com.concessio.crm.quotation.calculation.QuotationCalculationContext;
import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import com.concessio.crm.quotation.repository.QuotationRepository;
import com.concessio.crm.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final ActivityRepository activityRepository;
    private final QuotationCalculationContext calculationContext;

    public QuotationService(QuotationRepository quotationRepository,
                            ActivityRepository activityRepository,
                            QuotationCalculationContext calculationContext) {
        this.quotationRepository = quotationRepository;
        this.activityRepository = activityRepository;
        this.calculationContext = calculationContext;
    }

    @Transactional(readOnly = true)
    public List<Quotation> findAllByTenant(Long tenantId) {
        return quotationRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public Page<Quotation> findAllByTenant(Long tenantId, Pageable pageable) {
        return quotationRepository.findByTenantId(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Quotation> findByLead(Long tenantId, Long leadId) {
        return quotationRepository.findByTenantIdAndLeadId(tenantId, leadId);
    }

    @Transactional(readOnly = true)
    public Page<Quotation> findByLead(Long tenantId, Long leadId, Pageable pageable) {
        return quotationRepository.findByTenantIdAndLeadId(tenantId, leadId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Quotation> findByType(Long tenantId, QuotationType type) {
        return quotationRepository.findByTenantIdAndType(tenantId, type);
    }

    @Transactional(readOnly = true)
    public Page<Quotation> findByType(Long tenantId, QuotationType type, Pageable pageable) {
        return quotationRepository.findByTenantIdAndType(tenantId, type, pageable);
    }

    @Transactional(readOnly = true)
    public Quotation findById(Long id, Long tenantId) {
        return quotationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada: " + id));
    }

    @Transactional
    public Quotation createQuotation(Quotation quotation, Long tenantId, Long createdByUserId) {
        quotation.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        quotation.setCreatedBy(new User() {{ setId(createdByUserId); }});
        quotation.setCreatedAt(LocalDateTime.now());
        quotation.setValidUntil(LocalDateTime.now().plusDays(7));
        quotation.setSentToCustomer(false);

        // Calcular precio final si es necesario
        if (quotation.getPriceFinal() == null && quotation.getPriceList() != null) {
            BigDecimal discount = quotation.getDiscount() != null ? quotation.getDiscount() : BigDecimal.ZERO;
            quotation.setPriceFinal(quotation.getPriceList().subtract(discount));
        }

        // Use Strategy Pattern for type-specific validation and calculation
        calculationContext.validate(quotation);
        calculationContext.calculate(quotation);

        Quotation saved = quotationRepository.save(quotation);

        // Log activity
        logActivity(saved.getLead(), ActivityType.COTIZACION,
                "Cotización " + saved.getType() + " creada: " + saved.getVehicleModel() + " - $" + saved.getPriceFinal(),
                tenantId, createdByUserId);

        return saved;
    }

    @Transactional
    public Quotation updateQuotation(Long id, Quotation quotationUpdate, Long tenantId, Long updatedByUserId) {
        Quotation existing = findById(id, tenantId);

        existing.setVehicleModel(quotationUpdate.getVehicleModel());
        existing.setVehicleVin(quotationUpdate.getVehicleVin());
        existing.setPriceList(quotationUpdate.getPriceList());
        existing.setDiscount(quotationUpdate.getDiscount());
        existing.setPriceFinal(quotationUpdate.getPriceFinal());
        existing.setNotes(quotationUpdate.getNotes());

        // Update type-specific fields
        if (existing.getType() == QuotationType.FINANCIADO) {
            existing.setDownPayment(quotationUpdate.getDownPayment());
            existing.setFinancingMonths(quotationUpdate.getFinancingMonths());
            existing.setInterestRate(quotationUpdate.getInterestRate());
            existing.setBank(quotationUpdate.getBank());
        }

        if (existing.getType() == QuotationType.PLAN_FIAT) {
            existing.setPlanType(quotationUpdate.getPlanType());
            existing.setPlanInstallments(quotationUpdate.getPlanInstallments());
            existing.setPlanInstallmentAmount(quotationUpdate.getPlanInstallmentAmount());
            existing.setPlanAdjudication(quotationUpdate.getPlanAdjudication());
        }

        // Recalculate using Strategy Pattern
        calculationContext.validate(existing);
        calculationContext.calculate(existing);

        Quotation saved = quotationRepository.save(existing);

        logActivity(saved.getLead(), ActivityType.COTIZACION,
                "Cotización actualizada: " + saved.getVehicleModel(),
                tenantId, updatedByUserId);

        return saved;
    }

    @Transactional
    public Quotation markAsSent(Long id, Long tenantId, Long userId) {
        Quotation quotation = findById(id, tenantId);
        quotation.setSentToCustomer(true);
        quotation.setSentAt(LocalDateTime.now());

        Quotation saved = quotationRepository.save(quotation);

        logActivity(saved.getLead(), ActivityType.COTIZACION,
                "Cotización enviada al cliente",
                tenantId, userId);

        return saved;
    }

    @Transactional
    public void deleteQuotation(Long id, Long tenantId) {
        Quotation quotation = findById(id, tenantId);
        quotationRepository.delete(quotation);
    }

    @Transactional(readOnly = true)
    public long countByTenant(Long tenantId) {
        return quotationRepository.countByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public Map<QuotationType, Long> getStatsByType(Long tenantId) {
        List<Quotation> quotations = quotationRepository.findByTenantId(tenantId);
        return quotations.stream()
                .collect(Collectors.groupingBy(
                        Quotation::getType,
                        Collectors.counting()
                ));
    }

    @Transactional(readOnly = true)
    public List<Quotation> getValidQuotations(Long tenantId) {
        return quotationRepository.findByTenantId(tenantId).stream()
                .filter(q -> q.getValidUntil().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Quotation> getValidQuotations(Long tenantId, Pageable pageable) {
        List<Quotation> valid = getValidQuotations(tenantId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), valid.size());
        if (start > valid.size()) {
            start = end = 0;
        }
        return new PageImpl<>(
                valid.subList(start, end),
                pageable,
                valid.size()
        );
    }


    private void logActivity(com.concessio.crm.lead.model.Lead lead, ActivityType type,
                             String description, Long tenantId, Long userId) {
        Activity activity = new Activity();
        activity.setLead(lead);
        activity.setType(type);
        activity.setDescription(description);
        activity.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        if (userId != null) {
            activity.setUser(new User() {{ setId(userId); }});
        }
        activity.setCreatedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }
}
