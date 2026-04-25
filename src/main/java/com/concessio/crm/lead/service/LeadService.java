package com.concessio.crm.lead.service;

import com.concessio.crm.activity.model.Activity;
import com.concessio.crm.activity.model.ActivityType;
import com.concessio.crm.activity.repository.ActivityRepository;
import com.concessio.crm.exceptions.ResourceNotFoundException;
import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import com.concessio.crm.lead.repository.LeadRepository;
import com.concessio.crm.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final ActivityRepository activityRepository;

    public LeadService(LeadRepository leadRepository, ActivityRepository activityRepository) {
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<Lead> findAllByTenant(Long tenantId) {
        return leadRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public Page<Lead> findAllByTenant(Long tenantId, Pageable pageable) {
        return leadRepository.findByTenantId(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Lead> findByStatus(Long tenantId, LeadStatus status) {
        return leadRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Transactional(readOnly = true)
    public Page<Lead> findByStatus(Long tenantId, LeadStatus status, Pageable pageable) {
        return leadRepository.findByTenantIdAndStatus(tenantId, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Lead> findByAssignedUser(Long tenantId, Long userId) {
        return leadRepository.findByTenantIdAndAssignedToId(tenantId, userId);
    }

    @Transactional(readOnly = true)
    public Page<Lead> findByAssignedUser(Long tenantId, Long userId, Pageable pageable) {
        return leadRepository.findByTenantIdAndAssignedToId(tenantId, userId, pageable);
    }

    @Transactional(readOnly = true)
    public Lead findById(Long id, Long tenantId) {
        return leadRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado: " + id));
    }

    @Transactional
    public Lead createLead(Lead lead, Long tenantId, Long createdByUserId) {
        lead.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        lead.setCreatedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());

        if (lead.getStatus() == null) {
            lead.setStatus(LeadStatus.NUEVO);
        }

        Lead saved = leadRepository.save(lead);

        // Log activity
        logActivity(saved, ActivityType.LEAD_CREATED,
                "Lead creado: " + saved.getFirstName() + " " + saved.getLastName(),
                tenantId, createdByUserId);

        return saved;
    }

    @Transactional
    public Lead updateLead(Long id, Lead leadUpdate, Long tenantId, Long updatedByUserId) {
        Lead existing = findById(id, tenantId);

        LeadStatus oldStatus = existing.getStatus();

        existing.setFirstName(leadUpdate.getFirstName());
        existing.setLastName(leadUpdate.getLastName());
        existing.setPhone(leadUpdate.getPhone());
        existing.setEmail(leadUpdate.getEmail());
        existing.setVehicleInterest(leadUpdate.getVehicleInterest());
        existing.setNotes(leadUpdate.getNotes());
        existing.setUpdatedAt(LocalDateTime.now());

        // Si cambió el estado, loggear
        if (leadUpdate.getStatus() != null && leadUpdate.getStatus() != oldStatus) {
            existing.setStatus(leadUpdate.getStatus());
            logActivity(existing, ActivityType.STATUS_CHANGED,
                    "Estado cambiado: " + oldStatus + " → " + leadUpdate.getStatus(),
                    tenantId, updatedByUserId);
        }

        // Si cambió la asignación
        if (leadUpdate.getAssignedTo() != null &&
                (existing.getAssignedTo() == null ||
                        !existing.getAssignedTo().getId().equals(leadUpdate.getAssignedTo().getId()))) {
            existing.setAssignedTo(leadUpdate.getAssignedTo());
            logActivity(existing, ActivityType.LEAD_ASSIGNED,
                    "Asignado a vendedor ID: " + leadUpdate.getAssignedTo().getId(),
                    tenantId, updatedByUserId);
        }

        return leadRepository.save(existing);
    }

    @Transactional
    public void deleteLead(Long id, Long tenantId) {
        Lead lead = findById(id, tenantId);
        leadRepository.delete(lead);
    }

    @Transactional
    public Lead assignToUser(Long leadId, Long userId, Long tenantId, Long assignedByUserId) {
        Lead lead = findById(leadId, tenantId);
        User user = new User();
        user.setId(userId);
        lead.setAssignedTo(user);
        lead.setUpdatedAt(LocalDateTime.now());

        Lead saved = leadRepository.save(lead);

        logActivity(saved, ActivityType.LEAD_ASSIGNED,
                "Lead asignado a vendedor ID: " + userId,
                tenantId, assignedByUserId);

        return saved;
    }

    @Transactional
    public Lead updateStatus(Long leadId, LeadStatus newStatus, Long tenantId, Long userId) {
        Lead lead = findById(leadId, tenantId);
        LeadStatus oldStatus = lead.getStatus();

        if (oldStatus == newStatus) {
            return lead;
        }

        lead.setStatus(newStatus);
        lead.setUpdatedAt(LocalDateTime.now());

        Lead saved = leadRepository.save(lead);

        logActivity(saved, ActivityType.STATUS_CHANGED,
                "Estado actualizado: " + oldStatus + " → " + newStatus,
                tenantId, userId);

        return saved;
    }

    @Transactional(readOnly = true)
    public Map<LeadStatus, Long> getStatsByStatus(Long tenantId) {
        List<Lead> leads = leadRepository.findByTenantId(tenantId);
        Map<LeadStatus, Long> stats = new HashMap<>();
        for (Lead lead : leads) {
            stats.merge(lead.getStatus(), 1L, Long::sum);
        }
        return stats;
    }

    @Transactional(readOnly = true)
    public long countByTenant(Long tenantId) {
        return leadRepository.findByTenantId(tenantId).size();
    }

    @Transactional(readOnly = true)
    public long countByStatus(Long tenantId, LeadStatus status) {
        return leadRepository.findByTenantIdAndStatus(tenantId, status).size();
    }

    @Transactional(readOnly = true)
    public List<Lead> findUnassigned(Long tenantId) {
        return leadRepository.findByTenantId(tenantId).stream()
                .filter(lead -> lead.getAssignedTo() == null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Lead> findUnassigned(Long tenantId, Pageable pageable) {
        // Get all and filter since we need custom filtering
        List<Lead> unassigned = findUnassigned(tenantId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), unassigned.size());
        if (start > unassigned.size()) {
            start = end = 0;
        }
        return new org.springframework.data.domain.PageImpl<>(
                unassigned.subList(start, end),
                pageable,
                unassigned.size()
        );
    }

    @Transactional(readOnly = true)
    public Page<Lead> search(Long tenantId, String query, Pageable pageable) {
        return leadRepository.searchByTenantId(tenantId, query, pageable);
    }

    private void logActivity(Lead lead, ActivityType type, String description, Long tenantId, Long userId) {
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
