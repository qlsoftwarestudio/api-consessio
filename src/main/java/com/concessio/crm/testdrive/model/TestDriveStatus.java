package com.concessio.crm.testdrive.model;

public enum TestDriveStatus {
    AGENDADO,    // Turno programado
    CONFIRMADO,  // Cliente confirmó asistencia
    COMPLETADO,  // Test drive realizado
    CANCELADO,   // Cancelado por cliente o vendedora
    NO_SHOW      // Cliente no asistió
}
