package com.concessio.crm.lead.model;

import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant; // Sucursal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo; // Vendedor/a asignada

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phone;

    @Email
    private String email;

    private String source; // Origen: WEB, WHATSAPP, FERIA, REFERIDO, PILOT, ETC

    private String vehicleInterest; // Modelo de interés: "Cronos Drive 1.3"

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    @Column(length = 1000)
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastContactAt;

    // Constructor
    public Lead() {
        this.createdAt = LocalDateTime.now();
        this.status = LeadStatus.NUEVO;
    }

    public Lead(String firstName, String lastName, String phone, String source, Tenant tenant) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.source = source;
        this.tenant = tenant;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getVehicleInterest() { return vehicleInterest; }
    public void setVehicleInterest(String vehicleInterest) { this.vehicleInterest = vehicleInterest; }

    public LeadStatus getStatus() { return status; }
    public void setStatus(LeadStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastContactAt() { return lastContactAt; }
    public void setLastContactAt(LocalDateTime lastContactAt) { this.lastContactAt = lastContactAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
