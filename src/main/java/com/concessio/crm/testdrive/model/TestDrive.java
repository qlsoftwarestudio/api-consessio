package com.concessio.crm.testdrive.model;

import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import com.concessio.crm.vehicle.model.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
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

    @Column(nullable = false)
    private Integer durationMinutes = 60; // Duración por defecto: 1 hora

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accompanied_by")
    private User accompanieBy; // Vendedor que acompaña al cliente

    private String clientLicenseNumber; // Número de licencia del cliente
    private String clientLicenseType;   // Tipo: A, B, C, etc.
    private LocalDate clientLicenseExpiry; // Vencimiento

    private Integer kmBefore;  // Kilometraje al salir
    private Integer kmAfter;   // Kilometraje al retornar

    @Column(length = 2000)
    private String vehicleConditionBefore; // Estado del vehículo antes del test

    @Column(length = 2000)
    private String vehicleConditionAfter;  // Estado del vehículo al retornar

    private String cancellationReason; // Motivo de cancelación

    @Column(nullable = false)
    private boolean noShow = false; // Cliente no asistió

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
        this.noShow = false;
        this.durationMinutes = 60;
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

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public User getAccompanieBy() { return accompanieBy; }
    public void setAccompanieBy(User accompanieBy) { this.accompanieBy = accompanieBy; }

    public String getClientLicenseNumber() { return clientLicenseNumber; }
    public void setClientLicenseNumber(String clientLicenseNumber) { this.clientLicenseNumber = clientLicenseNumber; }

    public String getClientLicenseType() { return clientLicenseType; }
    public void setClientLicenseType(String clientLicenseType) { this.clientLicenseType = clientLicenseType; }

    public LocalDate getClientLicenseExpiry() { return clientLicenseExpiry; }
    public void setClientLicenseExpiry(LocalDate clientLicenseExpiry) { this.clientLicenseExpiry = clientLicenseExpiry; }

    public Integer getKmBefore() { return kmBefore; }
    public void setKmBefore(Integer kmBefore) { this.kmBefore = kmBefore; }

    public Integer getKmAfter() { return kmAfter; }
    public void setKmAfter(Integer kmAfter) { this.kmAfter = kmAfter; }

    public String getVehicleConditionBefore() { return vehicleConditionBefore; }
    public void setVehicleConditionBefore(String vehicleConditionBefore) { this.vehicleConditionBefore = vehicleConditionBefore; }

    public String getVehicleConditionAfter() { return vehicleConditionAfter; }
    public void setVehicleConditionAfter(String vehicleConditionAfter) { this.vehicleConditionAfter = vehicleConditionAfter; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public boolean isNoShow() { return noShow; }
    public void setNoShow(boolean noShow) { this.noShow = noShow; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
