package com.concessio.crm.activity.model;

public enum ActivityType {
    LEAD_CREATED,          // Lead creado
    LEAD_UPDATED,          // Lead actualizado
    LEAD_ASSIGNED,         // Lead asignado a vendedora
    STATUS_CHANGED,        // Cambio de estado en pipeline
    LLAMADA,               // Llamada telefónica
    WHATSAPP,              // Mensaje WhatsApp
    EMAIL,                 // Email enviado
    COTIZACION,            // Cotización generada
    TEST_DRIVE_AGENDADO,   // Test drive agendado
    TEST_DRIVE_COMPLETADO, // Test drive realizado
    DOCUMENTO_SUBIDO,        // Documento subido
    DOCUMENTO_VERIFICADO,  // Documento verificado
    NOTA,                  // Nota agregada
    RESERVA,               // Reserva realizada
    VENTA,                 // Venta concretada
    EXCEL_UPLOAD           // Carga masiva de Excel
}
