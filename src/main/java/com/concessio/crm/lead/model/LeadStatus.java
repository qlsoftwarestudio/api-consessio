package com.concessio.crm.lead.model;

public enum LeadStatus {
    NUEVO,              // Lead recién creado, sin contactar
    CONTACTADO,         // Primer contacto realizado
    COTIZADO,           // Cotización enviada
    TEST_DRIVE_AGENDADO, // Test drive programado
    TEST_DRIVE_COMPLETADO, // Cliente probó el auto
    NEGOCIACION,        // Discutiendo términos
    RESERVADO,          // Seña pagada
    DOCUMENTACION_COMPLETA, // Papeles listos
    ENTREGADO,          // Auto entregado
    NO_CONTESTA,        // Intentos de contacto fallidos
    CANCELADO           // Lead descartado
}
