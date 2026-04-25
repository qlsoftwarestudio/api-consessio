package com.concessio.crm.vehicle.service;

import com.concessio.crm.exceptions.ResourceNotFoundException;
import com.concessio.crm.vehicle.model.Vehicle;
import com.concessio.crm.vehicle.model.VehicleStatus;
import com.concessio.crm.vehicle.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAllByTenant(Long tenantId) {
        return vehicleRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findAllByTenant(Long tenantId, Pageable pageable) {
        return vehicleRepository.findByTenantId(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAvailable(Long tenantId) {
        return vehicleRepository.findByTenantIdAndStatus(tenantId, VehicleStatus.DISPONIBLE);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findAvailable(Long tenantId, Pageable pageable) {
        return vehicleRepository.findByTenantIdAndStatus(tenantId, VehicleStatus.DISPONIBLE, pageable);
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id, Long tenantId) {
        return vehicleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Vehicle> findByVin(String vin) {
        return vehicleRepository.findByVin(vin);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> searchByModel(Long tenantId, String model) {
        return vehicleRepository.findByTenantIdAndModelContainingIgnoreCase(tenantId, model);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> searchByModel(Long tenantId, String model, Pageable pageable) {
        return vehicleRepository.findByTenantIdAndModelContainingIgnoreCase(tenantId, model, pageable);
    }

    @Transactional
    public Vehicle createVehicle(Vehicle vehicle, Long tenantId) {
        // Validar VIN único
        if (vehicleRepository.findByVin(vehicle.getVin()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehículo con VIN: " + vehicle.getVin());
        }

        vehicle.setTenant(new com.concessio.crm.tenant.model.Tenant() {{ setId(tenantId); }});
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());

        if (vehicle.getStatus() == null) {
            vehicle.setStatus(VehicleStatus.DISPONIBLE);
        }

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle vehicleUpdate, Long tenantId) {
        Vehicle existing = findById(id, tenantId);

        // Validar VIN único si cambió
        if (!existing.getVin().equals(vehicleUpdate.getVin())) {
            vehicleRepository.findByVin(vehicleUpdate.getVin()).ifPresent(v -> {
                throw new IllegalArgumentException("Ya existe otro vehículo con VIN: " + vehicleUpdate.getVin());
            });
        }

        existing.setVin(vehicleUpdate.getVin());
        existing.setModel(vehicleUpdate.getModel());
        existing.setColor(vehicleUpdate.getColor());
        existing.setYear(vehicleUpdate.getYear());
        existing.setPriceList(vehicleUpdate.getPriceList());
        existing.setPriceCash(vehicleUpdate.getPriceCash());
        existing.setStatus(vehicleUpdate.getStatus());
        existing.setLocation(vehicleUpdate.getLocation());
        existing.setUpdatedAt(LocalDateTime.now());

        return vehicleRepository.save(existing);
    }

    @Transactional
    public void deleteVehicle(Long id, Long tenantId) {
        Vehicle vehicle = findById(id, tenantId);
        vehicleRepository.delete(vehicle);
    }

    @Transactional
    public Vehicle updateStatus(Long vehicleId, VehicleStatus newStatus, Long tenantId) {
        Vehicle vehicle = findById(vehicleId, tenantId);

        // Validar transiciones de estado
        if (vehicle.getStatus() == VehicleStatus.VENDIDO && newStatus != VehicleStatus.VENDIDO) {
            throw new IllegalStateException("No se puede cambiar el estado de un vehículo vendido");
        }

        vehicle.setStatus(newStatus);
        vehicle.setUpdatedAt(LocalDateTime.now());

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle reserve(Long vehicleId, Long tenantId) {
        Vehicle vehicle = findById(vehicleId, tenantId);

        if (vehicle.getStatus() != VehicleStatus.DISPONIBLE) {
            throw new IllegalStateException("El vehículo no está disponible para reserva. Estado actual: " + vehicle.getStatus());
        }

        vehicle.setStatus(VehicleStatus.RESERVADO);
        vehicle.setUpdatedAt(LocalDateTime.now());

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle markAsSold(Long vehicleId, Long tenantId) {
        Vehicle vehicle = findById(vehicleId, tenantId);

        if (vehicle.getStatus() == VehicleStatus.VENDIDO) {
            throw new IllegalStateException("El vehículo ya está marcado como vendido");
        }

        vehicle.setStatus(VehicleStatus.VENDIDO);
        vehicle.setUpdatedAt(LocalDateTime.now());

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle releaseReservation(Long vehicleId, Long tenantId) {
        Vehicle vehicle = findById(vehicleId, tenantId);

        if (vehicle.getStatus() != VehicleStatus.RESERVADO) {
            throw new IllegalStateException("El vehículo no está reservado. Estado actual: " + vehicle.getStatus());
        }

        vehicle.setStatus(VehicleStatus.DISPONIBLE);
        vehicle.setUpdatedAt(LocalDateTime.now());

        return vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(Long vehicleId, Long tenantId) {
        try {
            Vehicle vehicle = findById(vehicleId, tenantId);
            return vehicle.getStatus() == VehicleStatus.DISPONIBLE;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public long countByTenant(Long tenantId) {
        return vehicleRepository.findByTenantId(tenantId).size();
    }

    @Transactional(readOnly = true)
    public long countByStatus(Long tenantId, VehicleStatus status) {
        return vehicleRepository.findByTenantIdAndStatus(tenantId, status).size();
    }
}
