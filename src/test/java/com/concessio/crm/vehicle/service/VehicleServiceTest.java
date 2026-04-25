package com.concessio.crm.vehicle.service;

import com.concessio.crm.vehicle.model.Vehicle;
import com.concessio.crm.vehicle.model.VehicleStatus;
import com.concessio.crm.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setVin("3C6TRVDG6HE543210");
        vehicle.setModel("Fiat Cronos Drive 1.3");
        vehicle.setColor("Rojo");
        vehicle.setYear(2026);
        vehicle.setPriceList(new BigDecimal("18500000"));
        vehicle.setStatus(VehicleStatus.DISPONIBLE);
        vehicle.setLocation("Belgrano");
    }

    @Test
    void testCreateVehicle_Success() {
        when(vehicleRepository.findByVin(any())).thenReturn(Optional.empty());
        when(vehicleRepository.save(any())).thenReturn(vehicle);

        Vehicle result = vehicleService.createVehicle(vehicle, 1L);

        assertNotNull(result);
        assertEquals("3C6TRVDG6HE543210", result.getVin());
        assertEquals(VehicleStatus.DISPONIBLE, result.getStatus());
    }

    @Test
    void testCreateVehicle_DuplicateVin_ThrowsException() {
        when(vehicleRepository.findByVin(any())).thenReturn(Optional.of(vehicle));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.createVehicle(vehicle, 1L)
        );

        assertTrue(exception.getMessage().contains("VIN"));
    }

    @Test
    void testReserveVehicle_Success() {
        when(vehicleRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any())).thenReturn(vehicle);

        Vehicle result = vehicleService.reserve(1L, 1L);

        assertEquals(VehicleStatus.RESERVADO, result.getStatus());
    }

    @Test
    void testReserveVehicle_NotAvailable_ThrowsException() {
        vehicle.setStatus(VehicleStatus.VENDIDO);
        when(vehicleRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(vehicle));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> vehicleService.reserve(1L, 1L)
        );

        assertTrue(exception.getMessage().contains("no está disponible"));
    }

    @Test
    void testIsAvailable_True() {
        when(vehicleRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(vehicle));

        boolean available = vehicleService.isAvailable(1L, 1L);

        assertTrue(available);
    }

    @Test
    void testIsAvailable_False() {
        vehicle.setStatus(VehicleStatus.RESERVADO);
        when(vehicleRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(vehicle));

        boolean available = vehicleService.isAvailable(1L, 1L);

        assertFalse(available);
    }

    @Test
    void testMarkAsSold_Success() {
        vehicle.setStatus(VehicleStatus.RESERVADO);
        when(vehicleRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any())).thenReturn(vehicle);

        Vehicle result = vehicleService.markAsSold(1L, 1L);

        assertEquals(VehicleStatus.VENDIDO, result.getStatus());
    }

    @Test
    void testReleaseReservation_Success() {
        vehicle.setStatus(VehicleStatus.RESERVADO);
        when(vehicleRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any())).thenReturn(vehicle);

        Vehicle result = vehicleService.releaseReservation(1L, 1L);

        assertEquals(VehicleStatus.DISPONIBLE, result.getStatus());
    }
}
