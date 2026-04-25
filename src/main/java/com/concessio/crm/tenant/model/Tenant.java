package com.concessio.crm.tenant.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // Código de sucursal ej: "BEL", "MAT", "ALS"

    @Column(nullable = false)
    private String name; // Nombre ej: "Giamma Belgrano"

    private String address;
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tenant() {}

    public Tenant(String code, String name) {
        this.code = code;
        this.name = name;
        this.status = TenantStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == TenantStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
