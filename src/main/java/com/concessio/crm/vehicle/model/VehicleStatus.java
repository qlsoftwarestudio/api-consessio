package com.concessio.crm.vehicle.model;

public enum VehicleStatus {
    DISPONIBLE,     // Listo para venta
    RESERVADO,      // Seña pagada, en trámite
    VENDIDO,        // Entregado al cliente
    EN_TRANSITO,    // Viniendo de fábrica/importación
    EN_PREPARACION, // Llegó, en revisión/lavado
    EN_TEST_DRIVE,  // Ocupado en test drive
    NO_DISPONIBLE   // Dañado, otro concesionario, etc.
}
