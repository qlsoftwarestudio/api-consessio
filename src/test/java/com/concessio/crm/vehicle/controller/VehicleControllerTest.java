package com.concessio.crm.vehicle.controller;

import com.concessio.crm.vehicle.model.Vehicle;
import com.concessio.crm.vehicle.model.VehicleStatus;
import com.concessio.crm.vehicle.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private Vehicle vehicle;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setVin("3C6TRVDG6HE543210");
        vehicle.setModel("Fiat Cronos");
        vehicle.setStatus(VehicleStatus.DISPONIBLE);
        vehicle.setPriceList(new BigDecimal("18500000"));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAllVehicles_Success() {
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle), pageable, 1);
        when(vehicleService.findAllByTenant(1L, pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Vehicle>> response = vehicleController.getAllVehicles(pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetAvailableVehicles_Success() {
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle), pageable, 1);
        when(vehicleService.findAvailable(1L, pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Vehicle>> response = vehicleController.getAvailableVehicles(pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(VehicleStatus.DISPONIBLE, response.getBody().getContent().get(0).getStatus());
    }

    @Test
    void testGetVehicleById_Success() {
        when(vehicleService.findById(1L, 1L)).thenReturn(vehicle);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals("Fiat Cronos", response.getBody().getModel());
    }

    @Test
    void testGetVehicleByVin_Success() {
        when(vehicleService.findByVin("3C6TRVDG6HE543210")).thenReturn(Optional.of(vehicle));

        ResponseEntity<Vehicle> response = vehicleController.getVehicleByVin("3C6TRVDG6HE543210");

        assertNotNull(response.getBody());
        assertEquals("3C6TRVDG6HE543210", response.getBody().getVin());
    }

    @Test
    void testSearchByModel_Success() {
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle), pageable, 1);
        when(vehicleService.searchByModel(1L, "Cronos", pageable)).thenReturn(page);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Page<Vehicle>> response = vehicleController.searchByModel("Cronos", pageable);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
    }

    @Test
    void testCreateVehicle_Success() {
        when(vehicleService.createVehicle(any(), eq(1L))).thenReturn(vehicle);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Vehicle> response = vehicleController.createVehicle(vehicle);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testReserveVehicle_Success() {
        vehicle.setStatus(VehicleStatus.RESERVADO);
        when(vehicleService.reserve(1L, 1L)).thenReturn(vehicle);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Vehicle> response = vehicleController.reserveVehicle(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(VehicleStatus.RESERVADO, response.getBody().getStatus());
    }

    @Test
    void testMarkAsSold_Success() {
        vehicle.setStatus(VehicleStatus.VENDIDO);
        when(vehicleService.markAsSold(1L, 1L)).thenReturn(vehicle);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Vehicle> response = vehicleController.markAsSold(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertEquals(VehicleStatus.VENDIDO, response.getBody().getStatus());
    }

    @Test
    void testCheckAvailability_Success() {
        when(vehicleService.isAvailable(1L, 1L)).thenReturn(true);

        com.concessio.crm.tenant.TenantContext.setCurrentTenant(1L);
        ResponseEntity<Boolean> response = vehicleController.checkAvailability(1L);
        com.concessio.crm.tenant.TenantContext.clear();

        assertNotNull(response.getBody());
        assertTrue(response.getBody());
    }
}
