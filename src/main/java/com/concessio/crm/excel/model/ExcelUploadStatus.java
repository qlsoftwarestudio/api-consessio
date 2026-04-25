package com.concessio.crm.excel.model;

public enum ExcelUploadStatus {
    UPLOADED,    // Archivo subido, esperando procesamiento
    PROCESSING,  // Procesando filas
    COMPLETED,   // Procesamiento exitoso
    ERROR,       // Error en procesamiento
    CANCELLED    // Cancelado por usuario
}
