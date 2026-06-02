package com.concessio.crm.testdrive.controller;

import com.concessio.crm.tenant.TenantContext;
import com.concessio.crm.testdrive.dto.CancelTestDriveRequest;
import com.concessio.crm.testdrive.dto.CompleteTestDriveRequest;
import com.concessio.crm.testdrive.model.TestDrive;
import com.concessio.crm.testdrive.service.TestDriveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-drives")
public class TestDriveController {

    private final TestDriveService testDriveService;

    public TestDriveController(TestDriveService testDriveService) {
        this.testDriveService = testDriveService;
    }

    @GetMapping
    public ResponseEntity<List<TestDrive>> getAllTestDrives() {
        Long tenantId = TenantContext.getCurrentTenant();
        List<TestDrive> testDrives = testDriveService.findAllByTenant(tenantId);
        return ResponseEntity.ok(testDrives);
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<TestDrive>> getTestDrivesByLead(@PathVariable Long leadId) {
        List<TestDrive> testDrives = testDriveService.findByLead(leadId);
        return ResponseEntity.ok(testDrives);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TestDrive>> getTestDrivesByStatus(@PathVariable String status) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<TestDrive> testDrives = testDriveService.findByStatus(tenantId, 
            com.concessio.crm.testdrive.model.TestDriveStatus.valueOf(status));
        return ResponseEntity.ok(testDrives);
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<TestDrive>> getCalendarView(
            @RequestParam java.time.LocalDateTime start,
            @RequestParam java.time.LocalDateTime end) {
        Long tenantId = TenantContext.getCurrentTenant();
        List<TestDrive> testDrives = testDriveService.findByDateRange(tenantId, start, end);
        return ResponseEntity.ok(testDrives);
    }

    @GetMapping("/today")
    public ResponseEntity<List<TestDrive>> getTodayTestDrives() {
        Long tenantId = TenantContext.getCurrentTenant();
        List<TestDrive> testDrives = testDriveService.findTodayTestDrives(tenantId);
        return ResponseEntity.ok(testDrives);
    }

    @GetMapping("/my-test-drives")
    public ResponseEntity<List<TestDrive>> getMyTestDrives(@RequestAttribute Long userId) {
        List<TestDrive> testDrives = testDriveService.findMyTestDrives(userId);
        return ResponseEntity.ok(testDrives);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDrive> getTestDriveById(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        return testDriveService.findById(id, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TestDrive> createTestDrive(@RequestBody TestDrive testDrive, 
                                                       @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        TestDrive saved = testDriveService.create(testDrive, tenantId, userId);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<TestDrive> confirmTestDrive(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            TestDrive updated = testDriveService.confirm(id, tenantId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestDrive> updateTestDrive(@PathVariable Long id, @RequestBody TestDrive testDrive,
                                                       @RequestAttribute Long userId) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            TestDrive updated = testDriveService.update(id, testDrive, tenantId, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TestDrive> completeTestDrive(@PathVariable Long id, @RequestBody CompleteTestDriveRequest request) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            TestDrive updated = testDriveService.complete(id, tenantId, request.getNotes(), request.getKmAfter());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TestDrive> cancelTestDrive(@PathVariable Long id, @RequestBody(required = false) CancelTestDriveRequest request) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            String reason = request != null ? request.getReason() : null;
            TestDrive updated = testDriveService.cancel(id, tenantId, reason);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/no-show")
    public ResponseEntity<TestDrive> noShowTestDrive(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            TestDrive updated = testDriveService.noShow(id, tenantId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestDrive(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        try {
            testDriveService.delete(id, tenantId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
