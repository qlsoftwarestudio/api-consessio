package com.concessio.crm.vehicle.model;

import com.concessio.crm.tenant.model.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant; // Sucursal donde está físicamente

    @NotBlank
    @Column(unique = true, nullable = false)
    private String vin; // Número de chasis único

    @NotBlank
    private String model; // Ej: "Fiat Cronos Drive 1.3"

    private String color;

    private Integer year;

    @NotNull
    private BigDecimal priceList; // Precio de lista

    private BigDecimal priceCash; // Precio contado (con descuento)

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    private String location; // Ubicación física en sucursal

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public Vehicle() {
        this.createdAt = LocalDateTime.now();
        this.status = VehicleStatus.DISPONIBLE;
    }

    public Vehicle(String vin, String model, String color, Integer year, BigDecimal priceList, Tenant tenant) {
        this();
        this.vin = vin;
        this.model = model;
        this.color = color;
        this.year = year;
        this.priceList = priceList;
        this.tenant = tenant;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getPriceList() { return priceList; }
    public void setPriceList(BigDecimal priceList) { this.priceList = priceList; }

    public BigDecimal getPriceCash() { return priceCash; }
    public void setPriceCash(BigDecimal priceCash) { this.priceCash = priceCash; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
