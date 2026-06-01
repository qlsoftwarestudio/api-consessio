# 📘 Concessio - API Documentation

**Versión:** 1.0 MVP  
**Fecha:** Abril 2026  
**Base URL:** `http://localhost:8080`

---

## 📋 Índice

1. [Autenticación](#-autenticación)
2. [Onboarding](#-onboarding)
3. [Leads](#-leads)
4. [Vehículos](#-vehículos)
5. [Cotizaciones](#-cotizaciones)
6. [Test Drives](#-test-drives)
7. [Documentos](#-documentos)
8. [Excel - Carga Masiva](#-excel---carga-masiva)
9. [Actividades](#-actividades)
10. [Usuarios](#-usuarios)

---

## 🔐 Autenticación

### POST /auth/onboarding
**Descripción:** Crear nuevo tenant con usuario admin  
**Auth:** No requerida

**Request Body:**
```json
{
  "businessName": "Concesionario Concessio Test",
  "adminName": "Juan",
  "adminLastname": "Gerente",
  "adminEmail": "gerente@test.com",
  "password": "password123"
}
```

**Response 200:**
```json
{
  "message": "Tenant and admin user created successfully",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### POST /auth/login
**Descripción:** Login de usuario  
**Auth:** No requerida

**Request Body:**
```json
{
  "email": "gerente@test.com",
  "password": "password123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "gerente@test.com",
  "role": "GERENTE"
}
```

---

### POST /auth/register
**Descripción:** Registrar nuevo usuario en tenant existente  
**Auth:** JWT + Role GERENTE/SUPERVISOR

**Headers:**
```
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Request Body:**
```json
{
  "name": "María",
  "lastname": "Vendedora",
  "email": "maria@test.com",
  "password": "pass123",
  "phone": "+5491123456789",
  "role": "VENDEDORA"
}
```

---

## 👥 Leads

### GET /api/leads
**Descripción:** Listar todos los leads del tenant  
**Auth:** JWT requerido

**Query Params:**
- `status` (optional): NUEVO, CONTACTADO, COTIZADO, etc.
- `assignedTo` (optional): ID de vendedora

**Response 200:**
```json
[
  {
    "id": 1,
    "firstName": "Juan",
    "lastName": "Pérez",
    "phone": "+5491123456789",
    "email": "juan@test.com",
    "vehicleInterest": "Fiat Cronos",
    "status": "NUEVO",
    "source": "WEB",
    "assignedTo": {
      "id": 2,
      "name": "María"
    },
    "createdAt": "2026-04-13T10:00:00"
  }
]
```

---

### POST /api/leads
**Descripción:** Crear nuevo lead

**Request Body:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "phone": "+5491123456789",
  "email": "juan@test.com",
  "vehicleInterest": "Fiat Cronos",
  "source": "WEB",
  "notes": "Interesado en financiación"
}
```

---

### GET /api/leads/{id}
**Descripción:** Obtener lead por ID

---

### PUT /api/leads/{id}
**Descripción:** Actualizar lead

---

### DELETE /api/leads/{id}
**Descripción:** Eliminar lead

---

### GET /api/leads/stats
**Descripción:** Estadísticas de leads por estado

**Response 200:**
```json
{
  "NUEVO": 15,
  "CONTACTADO": 8,
  "COTIZADO": 5,
  "TEST_DRIVE": 3,
  "NEGOCIACION": 2,
  "RESERVADO": 1,
  "VENDIDO": 1,
  "PERDIDO": 2
}
```

---

## 🚗 Vehículos

### GET /api/vehicles
**Descripción:** Listar stock de vehículos

**Query Params:**
- `status`: DISPONIBLE, RESERVADO, VENDIDO
- `model`: Filtrar por modelo

**Response 200:**
```json
[
  {
    "id": 1,
    "vin": "3C6TRVDG6HE543210",
    "model": "Fiat Cronos Drive 1.3",
    "color": "Rojo",
    "year": 2026,
    "listPrice": 18500000.00,
    "costPrice": 16500000.00,
    "status": "DISPONIBLE",
    "location": "Belgrano",
    "createdAt": "2026-04-01T00:00:00"
  }
]
```

---

### POST /api/vehicles
**Descripción:** Agregar vehículo al stock

**Request Body:**
```json
{
  "vin": "3C6TRVDG6HE543210",
  "model": "Fiat Cronos Drive 1.3",
  "color": "Rojo",
  "year": 2026,
  "listPrice": 18500000.00,
  "costPrice": 16500000.00,
  "location": "Belgrano"
}
```

---

### GET /api/vehicles/vin/{vin}
**Descripción:** Buscar vehículo por VIN

---

### GET /api/vehicles/{id}/availability
**Descripción:** Verificar disponibilidad de vehículo

**Response 200:**
```json
{
  "available": true,
  "status": "DISPONIBLE",
  "message": "Vehículo disponible para reserva"
}
```

---

## 💰 Cotizaciones

### GET /api/quotations
**Descripción:** Listar cotizaciones

**Query Params:**
- `leadId`: Filtrar por lead
- `type`: CONTADO, FINANCIADO, PLAN_FIAT

---

### POST /api/quotations
**Descripción:** Crear cotización

**Request Body (Contado):**
```json
{
  "type": "CONTADO",
  "lead": { "id": 1 },
  "vehicle": { "id": 1 },
  "listPrice": 18500000.00,
  "discount": 500000.00,
  "finalPrice": 18000000.00,
  "notes": "Incluye transferencia"
}
```

**Request Body (Financiado):**
```json
{
  "type": "FINANCIADO",
  "lead": { "id": 1 },
  "vehicle": { "id": 1 },
  "listPrice": 18500000.00,
  "downPayment": 5550000.00,
  "financedAmount": 12950000.00,
  "interestRate": 25.5,
  "installments": 48,
  "installmentAmount": 412500.00,
  "bank": "Banco Nación",
  "notes": "Pre-aprobado"
}
```

**Request Body (Plan Fiat):**
```json
{
  "type": "PLAN_FIAT",
  "lead": { "id": 1 },
  "vehicle": { "id": 1 },
  "listPrice": 18500000.00,
  "planName": "Plan 84 Cuotas",
  "planInstallments": 84,
  "planInstallmentAmount": 220238.00,
  "planSubscription": 50000.00,
  "notes": "Pago adelantado cuota 1"
}
```

---

### GET /api/quotations/lead/{leadId}
**Descripción:** Obtener cotizaciones de un lead

---

### PUT /api/quotations/{id}/mark-sent
**Descripción:** Marcar cotización como enviada al cliente

---

## 🚗 Test Drives

### GET /api/test-drives
**Descripción:** Listar todos los test drives

**Query Params:**
- `status`: AGENDADO, CONFIRMADO, COMPLETADO, CANCELADO, NO_SHOW

---

### POST /api/test-drives
**Descripción:** Agendar test drive

**Request Body:**
```json
{
  "lead": { "id": 1 },
  "vehicle": { "id": 1 },
  "vehicleModel": "Fiat Cronos Drive",
  "scheduledAt": "2026-04-20T11:00:00",
  "location": "Sucursal Belgrano",
  "notes": "Cliente trae licencia"
}
```

---

### GET /api/test-drives/calendar
**Descripción:** Ver test drives en rango de fechas (vista calendario)

**Query Params:**
- `start`: 2026-04-20T00:00:00
- `end`: 2026-04-27T23:59:59

---

### PUT /api/test-drives/{id}/confirm
**Descripción:** Confirmar test drive (cliente confirmó asistencia)

---

### PUT /api/test-drives/{id}/complete
**Descripción:** Completar test drive

**Request Body:**
```json
{
  "notes": "Cliente muy interesado, prefiere financiación",
  "interested": true
}
```

---

### PUT /api/test-drives/{id}/cancel
**Descripción:** Cancelar test drive

---

## 📄 Documentos

### GET /api/documents/lead/{leadId}
**Descripción:** Listar documentos de un lead

---

### POST /api/documents
**Descripción:** Registrar documento subido

**Request Body:**
```json
{
  "lead": { "id": 1 },
  "type": "DNI_FRENTE",
  "fileName": "dni_juan_frente.jpg",
  "storageUrl": "https://storage.Concessio.com/docs/123",
  "fileSize": 2048000,
  "mimeType": "image/jpeg"
}
```

---

### GET /api/documents/lead/{leadId}/checklist
**Descripción:** Ver documentos verificados (checklist visual)

**Response 200:**
```json
[
  "DNI_FRENTE",
  "DNI_DORSO",
  "RECIBO_SUELDO_1"
]
```

---

### GET /api/documents/lead/{leadId}/stats
**Descripción:** Estadísticas de documentación

**Response 200:**
```json
{
  "totalDocs": 5,
  "verifiedDocs": 3,
  "pendingDocs": 2
}
```

---

### PUT /api/documents/{id}/verify
**Descripción:** Verificar documento (rol SUPERVISOR/GERENTE)

---

## 📊 Excel - Carga Masiva

### GET /api/excel/template
**Descripción:** Descargar template Excel para carga masiva  
**Response:** Archivo .xlsx (3.7KB)

---

### POST /api/excel/upload
**Descripción:** Subir y procesar archivo Excel  
**Content-Type:** multipart/form-data

**Form Data:**
- `file`: Archivo Excel (.xlsx o .xls)

**Response 200:**
```json
{
  "totalRows": 200,
  "successRows": 195,
  "errorRows": 2,
  "duplicateRows": 3,
  "createdLeadIds": [101, 102, 103, ...],
  "errors": [
    {
      "rowNumber": 45,
      "field": "VALIDACION",
      "message": "Fila incompleta: nombre, apellido y telefono son obligatorios"
    }
  ]
}
```

---

### GET /api/excel/uploads
**Descripción:** Historial de cargas Excel

---

### GET /api/excel/uploads/{id}
**Descripción:** Detalle de una carga específica

---

## 📝 Actividades

### GET /api/activities/lead/{leadId}
**Descripción:** Timeline de actividades de un lead

---

### GET /api/activities/lead/{leadId}/timeline
**Descripción:** Timeline ordenado por fecha descendente

**Response 200:**
```json
[
  {
    "id": 1,
    "type": "COTIZACION",
    "description": "Cotización enviada al cliente",
    "user": { "id": 2, "name": "María" },
    "createdAt": "2026-04-13T15:30:00"
  },
  {
    "id": 2,
    "type": "LLAMADA",
    "description": "Llamada de seguimiento",
    "createdAt": "2026-04-13T14:00:00"
  }
]
```

---

### GET /api/activities/my
**Descripción:** Mis actividades (usuario logueado)

---

### GET /api/activities/stats?start=2026-04-01&end=2026-04-30
**Descripción:** Estadísticas de actividades por período

---

## 👤 Usuarios

### GET /users
**Descripción:** Listar usuarios del tenant  
**Auth:** GERENTE, SUPERVISOR

---

### GET /users/{id}
**Descripción:** Obtener usuario por ID

---

### POST /users
**Descripción:** Crear usuario  
**Auth:** GERENTE

---

### PUT /users/{id}
**Descripción:** Actualizar usuario

---

### DELETE /users/{id}
**Descripción:** Eliminar usuario  
**Auth:** GERENTE

---

## 🔑 Autenticación JWT

### Header Requerido
```
Authorization: Bearer {token}
```

### Claims del Token
```json
{
  "sub": "gerente@test.com",
  "iat": 1776123650,
  "exp": 1776210050,
  "tenantId": 1
}
```

### Roles Disponibles
- `GERENTE` - Acceso total
- `SUPERVISOR` - Gestión de vendedoras y verificación de docs
- `VENDEDORA` - Gestión de leads y cotizaciones
- `PLANES` - Especialista en Plan Fiat
- `ADMIN_SISTEMA` - Administración técnica

---

## ❌ Códigos de Error

| Código | Descripción |
|--------|-------------|
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Token inválido o expirado |
| 403 | Forbidden - Sin permisos para el recurso |
| 404 | Not Found - Recurso no existe |
| 409 | Conflict - Duplicado o conflicto de datos |
| 500 | Internal Server Error |

---

## 📦 Enums

### LeadStatus
- `NUEVO` - Lead recién ingresado
- `CONTACTADO` - Primer contacto realizado
- `COTIZADO` - Cotización enviada
- `TEST_DRIVE` - Test drive agendado o realizado
- `NEGOCIACION` - En negociación de condiciones
- `RESERVADO` - Vehículo reservado
- `VENDIDO` - Venta concretada
- `PERDIDO` - Lead perdido/competencia
- `NO_CONTACTADO` - No se pudo contactar

### VehicleStatus
- `DISPONIBLE` - En stock listo para vender
- `RESERVADO` - Reservado por cliente
- `VENDIDO` - Vendido, fuera de stock
- `EN_TRANSITO` - En camino a sucursal
- `EN_PREPARACION` - En preparación física
- `NO_DISPONIBLE` - No disponible para venta

### QuotationType
- `CONTADO` - Pago contado
- `FINANCIADO` - Financiación bancaria
- `PLAN_FIAT` - Plan de ahorro Fiat

### DocumentType
- `DNI_FRENTE`, `DNI_DORSO`
- `CUIL_CUIT`
- `RECIBO_SUELDO_1`, `RECIBO_SUELDO_2`, `RECIBO_SUELDO_3`
- `SERVICIO`
- `GARANTE_DNI_FRENTE`, `GARANTE_DNI_DORSO`, `GARANTE_CUIL`, `GARANTE_RECIBO_1`
- `CONTRATO_RESERVA`, `ORDEN_COMPRA`
- `OTRO`

### TestDriveStatus
- `AGENDADO` - Turno programado
- `CONFIRMADO` - Cliente confirmó asistencia
- `COMPLETADO` - Test drive realizado
- `CANCELADO` - Cancelado por cliente o vendedora
- `NO_SHOW` - Cliente no asistió

### Role
- `GERENTE` - Gerente de sucursal/concesionario
- `SUPERVISOR` - Supervisor de ventas
- `VENDEDORA` - Vendedora de autos
- `PLANES` - Especialista en Plan Fiat
- `ADMIN_SISTEMA` - Administrador técnico del sistema

---

**Documento generado automáticamente por Concessio Backend**  
**Total endpoints documentados: 50+**
