package com.concessio.crm.user.model;

public enum Role {
    GERENTE,      // Acceso total, todas las sucursales
    SUPERVISOR,   // Gestión de su sucursal asignada
    VENDEDORA,    // Asesora de ventas - opera leads
    PLANES,       // Operador Plan Fiat - documentación
    ADMIN_SISTEMA // Administrador técnico
}
