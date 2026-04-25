package com.concessio.crm.document.model;

public enum DocumentType {
    DNI_FRENTE,          // Frente del DNI
    DNI_DORSO,           // Dorso del DNI
    CUIL_CUIT,           // Constancia CUIL/CUIT
    RECIBO_SUELDO_1,     // Último recibo de sueldo
    RECIBO_SUELDO_2,     // Penúltimo recibo
    RECIBO_SUELDO_3,     // Antepenúltimo recibo
    SERVICIO,            // Factura de servicio a nombre
    GARANTE_DNI_FRENTE,  // DNI frente del garante
    GARANTE_DNI_DORSO,   // DNI dorso del garante
    GARANTE_CUIL,        // CUIL del garante
    GARANTE_RECIBO_1,    // Recibo del garante
    CONTRATO_RESERVA,    // Contrato de reserva firmado
    ORDEN_COMPRA,        // Orden de compra
    OTRO                 // Otro documento
}
