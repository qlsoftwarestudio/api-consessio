package com.concessio.crm.vehicle.repository;

import com.concessio.crm.vehicle.model.Vehicle;
import com.concessio.crm.vehicle.model.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByTenantId(Long tenantId);

    Page<Vehicle> findByTenantId(Long tenantId, Pageable pageable);

    List<Vehicle> findByTenantIdAndStatus(Long tenantId, VehicleStatus status);

    Page<Vehicle> findByTenantIdAndStatus(Long tenantId, VehicleStatus status, Pageable pageable);

    Optional<Vehicle> findByVin(String vin);

    Optional<Vehicle> findByIdAndTenantId(Long id, Long tenantId);

    List<Vehicle> findByTenantIdAndModelContainingIgnoreCase(Long tenantId, String model);

    Page<Vehicle> findByTenantIdAndModelContainingIgnoreCase(Long tenantId, String model, Pageable pageable);

    long countByTenantIdAndStatus(Long tenantId, VehicleStatus status);
}
