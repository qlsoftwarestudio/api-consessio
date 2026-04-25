package com.concessio.crm.vehicle.controller;

import com.concessio.crm.tenant.TenantContext;
import com.concessio.crm.vehicle.model.Vehicle;
import com.concessio.crm.vehicle.model.VehicleStatus;
import com.concessio.crm.vehicle.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<Page<Vehicle>> getAllVehicles(Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Vehicle> vehicles = vehicleService.findAllByTenant(tenantId, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/available")
    public ResponseEntity<Page<Vehicle>> getAvailableVehicles(Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Vehicle> vehicles = vehicleService.findAvailable(tenantId, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        Vehicle vehicle = vehicleService.findById(id, tenantId);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/vin/{vin}")
    public ResponseEntity<Vehicle> getVehicleByVin(@PathVariable String vin) {
        return vehicleService.findByVin(vin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Vehicle>> searchByModel(@RequestParam String model, Pageable pageable) {
        Long tenantId = TenantContext.getCurrentTenant();
        Page<Vehicle> vehicles = vehicleService.searchByModel(tenantId, model, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        Long tenantId = TenantContext.getCurrentTenant();
        Vehicle saved = vehicleService.createVehicle(vehicle, tenantId);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        Long tenantId = TenantContext.getCurrentTenant();
        Vehicle updated = vehicleService.updateVehicle(id, vehicle, tenantId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        vehicleService.deleteVehicle(id, tenantId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Vehicle> updateStatus(@PathVariable Long id, @RequestBody VehicleStatus status) {
        Long tenantId = TenantContext.getCurrentTenant();
        Vehicle updated = vehicleService.updateStatus(id, status, tenantId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/reserve")
    public ResponseEntity<Vehicle> reserveVehicle(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        Vehicle updated = vehicleService.reserve(id, tenantId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/sell")
    public ResponseEntity<Vehicle> markAsSold(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        Vehicle updated = vehicleService.markAsSold(id, tenantId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/release")
    public ResponseEntity<Vehicle> releaseReservation(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        Vehicle updated = vehicleService.releaseReservation(id, tenantId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable Long id) {
        Long tenantId = TenantContext.getCurrentTenant();
        boolean available = vehicleService.isAvailable(id, tenantId);
        return ResponseEntity.ok(available);
    }
}
