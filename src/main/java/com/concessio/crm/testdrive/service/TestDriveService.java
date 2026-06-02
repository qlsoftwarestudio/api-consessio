package com.concessio.crm.testdrive.service;

import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.lead.model.LeadStatus;
import com.concessio.crm.lead.repository.LeadRepository;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.testdrive.model.TestDrive;
import com.concessio.crm.testdrive.model.TestDriveStatus;
import com.concessio.crm.testdrive.repository.TestDriveRepository;
import com.concessio.crm.user.model.User;
import com.concessio.crm.vehicle.model.Vehicle;
import com.concessio.crm.vehicle.model.VehicleStatus;
import com.concessio.crm.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestDriveService {

    private final TestDriveRepository testDriveRepository;
    private final VehicleRepository vehicleRepository;
    private final LeadRepository leadRepository;

    public TestDriveService(TestDriveRepository testDriveRepository,
                            VehicleRepository vehicleRepository,
                            LeadRepository leadRepository) {
        this.testDriveRepository = testDriveRepository;
        this.vehicleRepository = vehicleRepository;
        this.leadRepository = leadRepository;
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

    @Transactional(readOnly = true)
    public List<TestDrive> findMyTestDrives(Long userId) {
        return testDriveRepository.findByScheduledById(userId);
    }

    @Transactional(readOnly = true)
    public List<TestDrive> findTodayTestDrives(Long tenantId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59);
        return testDriveRepository.findByTenantIdAndDateRange(tenantId, startOfDay, endOfDay);
    }

    public TestDrive create(TestDrive testDrive, Long tenantId, Long userId) {
        // Validaciones básicas
        if (testDrive.getScheduledAt() == null) {
            throw new IllegalArgumentException("La fecha del test drive es obligatoria");
        }
        if (testDrive.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha del test drive no puede ser en el pasado");
        }
        if (testDrive.getDurationMinutes() == null || testDrive.getDurationMinutes() <= 0) {
            testDrive.setDurationMinutes(60);
        }

        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        testDrive.setTenant(tenant);

        User scheduledBy = new User();
        scheduledBy.setId(userId);
        testDrive.setScheduledBy(scheduledBy);

        // Validar vehículo y solapamiento
        if (testDrive.getVehicle() != null && testDrive.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findByIdAndTenantId(testDrive.getVehicle().getId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

            // Validar que el vehículo esté disponible
            if (vehicle.getStatus() != VehicleStatus.DISPONIBLE) {
                throw new IllegalStateException("El vehículo no está disponible para test drive (estado: " + vehicle.getStatus() + ")");
            }

            // Validar solapamiento
            LocalDateTime endTime = testDrive.getScheduledAt().plusMinutes(testDrive.getDurationMinutes());
            List<TestDrive> potentialOverlaps = testDriveRepository.findPotentialOverlapsByVehicle(
                    vehicle.getId(), tenantId, endTime, -1L);
            boolean hasOverlap = potentialOverlaps.stream().anyMatch(td -> {
                LocalDateTime tdEnd = td.getScheduledAt().plusMinutes(td.getDurationMinutes());
                return td.getScheduledAt().isBefore(endTime) && tdEnd.isAfter(testDrive.getScheduledAt());
            });
            if (hasOverlap) {
                throw new IllegalStateException("El vehículo ya tiene un test drive agendado en ese horario");
            }

            // Reservar vehículo
            vehicle.setStatus(VehicleStatus.EN_TEST_DRIVE);
            vehicleRepository.save(vehicle);
        }

        // Actualizar estado del lead
        if (testDrive.getLead() != null && testDrive.getLead().getId() != null) {
            Lead lead = leadRepository.findByIdAndTenantId(testDrive.getLead().getId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Lead no encontrado"));
            lead.setStatus(LeadStatus.TEST_DRIVE_AGENDADO);
            lead.setLastContactAt(LocalDateTime.now());
            leadRepository.save(lead);
        }

        testDrive.setStatus(TestDriveStatus.AGENDADO);
        testDrive.setCreatedAt(LocalDateTime.now());
        return testDriveRepository.save(testDrive);
    }

    public TestDrive update(Long id, TestDrive updated, Long tenantId, Long userId) {
        TestDrive existing = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        // Solo permitir editar si está AGENDADO
        if (existing.getStatus() != TestDriveStatus.AGENDADO) {
            throw new IllegalStateException("Solo se puede editar un test drive en estado AGENDADO");
        }

        // Actualizar campos editables
        if (updated.getScheduledAt() != null) {
            if (updated.getScheduledAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La fecha no puede ser en el pasado");
            }
            existing.setScheduledAt(updated.getScheduledAt());
        }
        if (updated.getDurationMinutes() != null && updated.getDurationMinutes() > 0) {
            existing.setDurationMinutes(updated.getDurationMinutes());
        }
        if (updated.getLocation() != null) {
            existing.setLocation(updated.getLocation());
        }
        if (updated.getAccompanieBy() != null) {
            existing.setAccompanieBy(updated.getAccompanieBy());
        }
        if (updated.getClientLicenseNumber() != null) {
            existing.setClientLicenseNumber(updated.getClientLicenseNumber());
        }
        if (updated.getClientLicenseType() != null) {
            existing.setClientLicenseType(updated.getClientLicenseType());
        }
        if (updated.getClientLicenseExpiry() != null) {
            existing.setClientLicenseExpiry(updated.getClientLicenseExpiry());
        }
        if (updated.getVehicle() != null && updated.getVehicle().getId() != null) {
            Vehicle newVehicle = vehicleRepository.findByIdAndTenantId(updated.getVehicle().getId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

            // Si cambió el vehículo
            if (existing.getVehicle() == null || !existing.getVehicle().getId().equals(newVehicle.getId())) {
                // Liberar vehículo anterior si existía
                if (existing.getVehicle() != null && existing.getVehicle().getId() != null) {
                    Vehicle oldVehicle = vehicleRepository.findByIdAndTenantId(existing.getVehicle().getId(), tenantId).orElse(null);
                    if (oldVehicle != null) {
                        oldVehicle.setStatus(VehicleStatus.DISPONIBLE);
                        vehicleRepository.save(oldVehicle);
                    }
                }
                // Validar nuevo vehículo
                if (newVehicle.getStatus() != VehicleStatus.DISPONIBLE) {
                    throw new IllegalStateException("El vehículo no está disponible (estado: " + newVehicle.getStatus() + ")");
                }
                // Validar solapamiento
                LocalDateTime endTime = existing.getScheduledAt().plusMinutes(existing.getDurationMinutes());
                List<TestDrive> potentialOverlaps = testDriveRepository.findPotentialOverlapsByVehicle(
                        newVehicle.getId(), tenantId, endTime, existing.getId());
                boolean hasOverlap = potentialOverlaps.stream().anyMatch(td -> {
                    LocalDateTime tdEnd = td.getScheduledAt().plusMinutes(td.getDurationMinutes());
                    return td.getScheduledAt().isBefore(endTime) && tdEnd.isAfter(existing.getScheduledAt());
                });
                if (hasOverlap) {
                    throw new IllegalStateException("El vehículo ya tiene un test drive en ese horario");
                }
                newVehicle.setStatus(VehicleStatus.EN_TEST_DRIVE);
                vehicleRepository.save(newVehicle);
            }
            existing.setVehicle(newVehicle);
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return testDriveRepository.save(existing);
    }

    public TestDrive confirm(Long id, Long tenantId) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        if (testDrive.getStatus() == TestDriveStatus.CANCELADO) {
            throw new IllegalStateException("No se puede confirmar un test drive cancelado");
        }

        testDrive.setStatus(TestDriveStatus.CONFIRMADO);
        testDrive.setConfirmationSent(true);
        testDrive.setConfirmedAt(LocalDateTime.now());
        testDrive.setUpdatedAt(LocalDateTime.now());

        return testDriveRepository.save(testDrive);
    }

    public TestDrive complete(Long id, Long tenantId, String notes, Integer kmAfter) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        if (testDrive.getStatus() == TestDriveStatus.CANCELADO) {
            throw new IllegalStateException("No se puede completar un test drive cancelado");
        }
        if (testDrive.getStatus() == TestDriveStatus.AGENDADO) {
            throw new IllegalStateException("El test drive debe estar confirmado antes de completarse");
        }

        // Validar km
        if (kmAfter != null && testDrive.getKmBefore() != null && kmAfter < testDrive.getKmBefore()) {
            throw new IllegalArgumentException("El kilometraje final no puede ser menor que el inicial");
        }
        if (kmAfter != null) {
            testDrive.setKmAfter(kmAfter);
        }

        testDrive.setStatus(TestDriveStatus.COMPLETADO);
        testDrive.setNotesResult(notes);
        testDrive.setCompletedAt(LocalDateTime.now());
        testDrive.setUpdatedAt(LocalDateTime.now());

        // Liberar vehículo
        if (testDrive.getVehicle() != null && testDrive.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findByIdAndTenantId(testDrive.getVehicle().getId(), tenantId).orElse(null);
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.DISPONIBLE);
                vehicleRepository.save(vehicle);
            }
        }

        // Actualizar lead
        if (testDrive.getLead() != null && testDrive.getLead().getId() != null) {
            Lead lead = leadRepository.findByIdAndTenantId(testDrive.getLead().getId(), tenantId).orElse(null);
            if (lead != null) {
                lead.setStatus(LeadStatus.TEST_DRIVE_COMPLETADO);
                lead.setLastContactAt(LocalDateTime.now());
                leadRepository.save(lead);
            }
        }

        return testDriveRepository.save(testDrive);
    }

    public TestDrive cancel(Long id, Long tenantId, String reason) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        if (testDrive.getStatus() == TestDriveStatus.COMPLETADO) {
            throw new IllegalStateException("No se puede cancelar un test drive ya completado");
        }

        testDrive.setStatus(TestDriveStatus.CANCELADO);
        testDrive.setCancellationReason(reason);
        testDrive.setUpdatedAt(LocalDateTime.now());

        // Liberar vehículo
        if (testDrive.getVehicle() != null && testDrive.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findByIdAndTenantId(testDrive.getVehicle().getId(), tenantId).orElse(null);
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.DISPONIBLE);
                vehicleRepository.save(vehicle);
            }
        }

        // Revertir lead (volver a CONTACTADO o dejar en estado actual)
        if (testDrive.getLead() != null && testDrive.getLead().getId() != null) {
            Lead lead = leadRepository.findByIdAndTenantId(testDrive.getLead().getId(), tenantId).orElse(null);
            if (lead != null && lead.getStatus() == LeadStatus.TEST_DRIVE_AGENDADO) {
                lead.setStatus(LeadStatus.CONTACTADO);
                lead.setLastContactAt(LocalDateTime.now());
                leadRepository.save(lead);
            }
        }

        return testDriveRepository.save(testDrive);
    }

    public TestDrive noShow(Long id, Long tenantId) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        if (testDrive.getStatus() == TestDriveStatus.COMPLETADO || testDrive.getStatus() == TestDriveStatus.CANCELADO) {
            throw new IllegalStateException("No se puede marcar como no-show un test drive ya finalizado");
        }

        testDrive.setStatus(TestDriveStatus.NO_SHOW);
        testDrive.setNoShow(true);
        testDrive.setUpdatedAt(LocalDateTime.now());

        // Liberar vehículo
        if (testDrive.getVehicle() != null && testDrive.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findByIdAndTenantId(testDrive.getVehicle().getId(), tenantId).orElse(null);
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.DISPONIBLE);
                vehicleRepository.save(vehicle);
            }
        }

        // Revertir lead
        if (testDrive.getLead() != null && testDrive.getLead().getId() != null) {
            Lead lead = leadRepository.findByIdAndTenantId(testDrive.getLead().getId(), tenantId).orElse(null);
            if (lead != null && lead.getStatus() == LeadStatus.TEST_DRIVE_AGENDADO) {
                lead.setStatus(LeadStatus.CONTACTADO);
                lead.setLastContactAt(LocalDateTime.now());
                leadRepository.save(lead);
            }
        }

        return testDriveRepository.save(testDrive);
    }

    public void delete(Long id, Long tenantId) {
        TestDrive testDrive = testDriveRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Test drive no encontrado"));

        // Liberar vehículo si está ocupado
        if (testDrive.getVehicle() != null && testDrive.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findByIdAndTenantId(testDrive.getVehicle().getId(), tenantId).orElse(null);
            if (vehicle != null && vehicle.getStatus() == VehicleStatus.EN_TEST_DRIVE) {
                vehicle.setStatus(VehicleStatus.DISPONIBLE);
                vehicleRepository.save(vehicle);
            }
        }

        testDriveRepository.delete(testDrive);
    }

    @Transactional(readOnly = true)
    public long countByTenantAndStatus(Long tenantId, TestDriveStatus status) {
        return testDriveRepository.countByTenantIdAndStatus(tenantId, status);
    }
}
