package com.concessio.crm.activity.controller;

import com.concessio.crm.activity.model.Activity;
import com.concessio.crm.activity.model.ActivityType;
import com.concessio.crm.activity.repository.ActivityRepository;
import com.concessio.crm.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<Activity>> getActivitiesByLead(@PathVariable Long leadId) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<Activity> activities = activityRepository.findByTenantIdAndLeadId(tenantId, leadId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/lead/{leadId}/timeline")
    public ResponseEntity<List<Activity>> getLeadTimeline(@PathVariable Long leadId) {
        // Timeline ordenado por fecha descendente
        List<Activity> activities = activityRepository.findByLeadIdOrderByCreatedAtDesc(leadId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Activity>> getActivitiesByType(@PathVariable ActivityType type) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<Activity> activities = activityRepository.findByTenantIdAndType(tenantId, type);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/my-activities")
    public ResponseEntity<List<Activity>> getMyActivities(@RequestAttribute Long userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getActivityStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<Object[]> stats = activityRepository.countByTypeForPeriod(tenantId, start, end);
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity, @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        activity.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        activity.setUser(new com.concessio.crm.user.model.User() {{ setId(userId); }});
        activity.setCreatedAt(LocalDateTime.now());
        Activity saved = activityRepository.save(activity);
        return ResponseEntity.ok(saved);
    }
}
