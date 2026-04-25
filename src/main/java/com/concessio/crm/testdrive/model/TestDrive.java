package com.concessio.crm.testdrive.model;

import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import com.concessio.crm.vehicle.model.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_drives")
public class TestDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle; // Vehículo específico (opcional)

    private String vehicleModel; // Modelo solicitado (ej: "Fiat Cronos Drive")

    @NotNull
    private LocalDateTime scheduledAt; // Fecha y hora agendada

    private String location; // Ubicación/sucursal donde se hace

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_by", nullable = false)
    private User scheduledBy; // Vendedora que agendó

    @Enumerated(EnumType.STRING)
    private TestDriveStatus status;

    private boolean reminderSent; // Recordatorio enviado 24hs antes
    private LocalDateTime reminderSentAt;

    private boolean confirmationSent; // Cliente confirmó asistencia
    private LocalDateTime confirmedAt;

    @Column(length = 1000)
    private String notesResult; // Notas del resultado (qué opinó el cliente)

    private boolean interested; // Cliente mostró interés post-test

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public TestDrive() {
        this.createdAt = LocalDateTime.now();
        this.status = TestDriveStatus.AGENDADO;
        this.reminderSent = false;
        this.confirmationSent = false;
        this.interested = false;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public User getScheduledBy() { return scheduledBy; }
    public void setScheduledBy(User scheduledBy) { this.scheduledBy = scheduledBy; }

    public TestDriveStatus getStatus() { return status; }
    public void setStatus(TestDriveStatus status) { this.status = status; }

    public boolean isReminderSent() { return reminderSent; }
    public void setReminderSent(boolean reminderSent) { this.reminderSent = reminderSent; }

    public LocalDateTime getReminderSentAt() { return reminderSentAt; }
    public void setReminderSentAt(LocalDateTime reminderSentAt) { this.reminderSentAt = reminderSentAt; }

    public boolean isConfirmationSent() { return confirmationSent; }
    public void setConfirmationSent(boolean confirmationSent) { this.confirmationSent = confirmationSent; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public String getNotesResult() { return notesResult; }
    public void setNotesResult(String notesResult) { this.notesResult = notesResult; }

    public boolean isInterested() { return interested; }
    public void setInterested(boolean interested) { this.interested = interested; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
