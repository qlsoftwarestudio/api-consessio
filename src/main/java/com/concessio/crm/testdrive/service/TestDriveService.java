package com.concessio.crm.testdrive.service;

import com.concessio.crm.tenant.TenantContext;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.testdrive.model.TestDrive;
import com.concessio.crm.testdrive.model.TestDriveStatus;
import com.concessio.crm.testdrive.repository.TestDriveRepository;
import com.concessio.crm.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestDriveService {

    private final TestDriveRepository testDriveRepository;

    public TestDriveService(TestDriveRepository testDriveRepository) {
        this.testDriveRepository = testDriveRepository;
    }

    @Transactional(readOnly = true)
    public List<TestDrive> findAllByTenant(Long tenantId) {
        return testDriveRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public List<TestDrive> findByLead(Long leadId) {
        return testDriveRepository.findByLeadId(leadId);
    }

    @Transactional(readOnly = true)
    public List<TestDrive> findByStatus(Long tenantId, TestDriveStatus status) {
        return testDriveRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Transactional(readOnly = true)
    public List<TestDrive> findByDateRange(Long tenantId, LocalDateTime start, LocalDateTime end) {
        return testDriveRepository.findByTenantIdAndDateRange(tenantId, start, end);
    }

    @Transactional(readOnly = true)
    public Optional<TestDrive> findById(Long id, Long tenantId) {
        return testDriveRepository.findByIdAndTenantId(id, tenantId);
    }

    public TestDrive create(TestDrive testDrive, Long tenantId, Long userId) {
        // Validaciones de negocio
        if (testDrive.getScheduledAt() == null) {
            throw new IllegalArgumentException("La fecha del test drive es obligatoria");
        }
        
        if (testDrive.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha del test drive no puede ser en el pasado");
        }

        // Asignar tenant y usuario
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        testDrive.setTenant(tenant);

        User scheduledBy = new User();
        scheduledBy.setId(userId);
        testDrive.setScheduledBy(scheduledBy);

        testDrive.setStatus(TestDriveStatus.AGENDADO);
        testDrive.setCreatedAt(LocalDateTime.now());
        
        return testDriveRepository.save(testDrive);
    }

    public TestDrive confirm(Long id, Long tenantId) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        // Validación de estado
        if (testDrive.getStatus() == TestDriveStatus.CANCELADO) {
            throw new IllegalStateException("No se puede confirmar un test drive cancelado");
        }

        testDrive.setStatus(TestDriveStatus.CONFIRMADO);
        testDrive.setConfirmationSent(true);
        testDrive.setConfirmedAt(LocalDateTime.now());
        testDrive.setUpdatedAt(LocalDateTime.now());

        return testDriveRepository.save(testDrive);
    }

    public TestDrive complete(Long id, Long tenantId, String notes) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        // Validación de estado
        if (testDrive.getStatus() == TestDriveStatus.CANCELADO) {
            throw new IllegalStateException("No se puede completar un test drive cancelado");
        }
        
        if (testDrive.getStatus() == TestDriveStatus.AGENDADO) {
            throw new IllegalStateException("El test drive debe estar confirmado antes de completarse");
        }

        testDrive.setStatus(TestDriveStatus.COMPLETADO);
        testDrive.setNotesResult(notes);
        testDrive.setCompletedAt(LocalDateTime.now());
        testDrive.setUpdatedAt(LocalDateTime.now());

        return testDriveRepository.save(testDrive);
    }

    public TestDrive cancel(Long id, Long tenantId) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        // No permitir cancelar si ya está completado
        if (testDrive.getStatus() == TestDriveStatus.COMPLETADO) {
            throw new IllegalStateException("No se puede cancelar un test drive ya completado");
        }

        testDrive.setStatus(TestDriveStatus.CANCELADO);
        testDrive.setUpdatedAt(LocalDateTime.now());

        return testDriveRepository.save(testDrive);
    }

    public void delete(Long id, Long tenantId) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        testDriveRepository.delete(testDrive);
    }

    @Transactional(readOnly = true)
    public long countByTenantAndStatus(Long tenantId, TestDriveStatus status) {
        return testDriveRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Transactional(readOnly = true)
    public List<TestDrive> findTodayTestDrives(Long tenantId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59);
        return testDriveRepository.findByTenantIdAndDateRange(tenantId, startOfDay, endOfDay);
    }
}
