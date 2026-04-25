# 📚 API COMPLETA - Concessio CRM

**Versión:** 1.1 MVP+  
**Base URL:** `http://localhost:8080`  
**Fecha:** Abril 13, 2026  

---

## 📋 ÍNDICE

1. [Autenticación](#-autenticación)
2. [Leads](#-leads)
3. [Vehículos](#-vehículos)
4. [Cotizaciones](#-cotizaciones)
5. [Usuarios](#-usuarios)
6. [Actividades](#-actividades)
7. [Test Drives](#-test-drives)
8. [Documentos](#-documentos)
9. [Excel Upload](#-excel-upload)
10. [Dashboard/Stats](#-dashboardstats)

---

## 🔐 AUTENTICACIÓN

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@concesionario.com",
  "password": "password123"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "email": "admin@concesionario.com",
  "role": "ADMIN",
  "tenantId": 1,
  "userId": 1
}
```

### Onboarding (Crear Tenant + Admin)
```http
POST /api/auth/onboarding
Content-Type: application/json

{
  "businessName": "AutoSur Concesionario",
  "adminName": "Juan",
  "adminLastname": "Pérez",
  "adminEmail": "juan@autosur.com",
  "adminPassword": "SecurePass123"
}
```

**Headers Requeridos para endpoints protegidos:**
```
Authorization: Bearer {token}
```

---

## 👤 LEADS

### Listar Leads (Paginado)
```http
GET /api/leads?page=0&size=20&sort=createdAt,desc
Authorization: Bearer {token}
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Juan",
      "lastName": "Pérez",
      "phone": "+54 11 1234 5678",
      "email": "juan@email.com",
      "dni": "30123456",
      "status": "NUEVO",
      "source": "WEB",
      "assignedTo": {
        "id": 2,
        "name": "María",
        "lastname": "González"
      },
      "createdAt": "2026-04-13T10:30:00",
      "updatedAt": "2026-04-13T10:30:00"
    }
  ],
  "totalElements": 150,
  "totalPages": 8,
  "size": 20,
  "number": 0
}
```

### Buscar Leads
```http
GET /api/leads/search?query=Juan&page=0&size=20
```

### Filtrar por Estado
```http
GET /api/leads/status/{status}?page=0&size=20
```

Estados disponibles: `NUEVO`, `CONTACTADO`, `EN_SEGUIMIENTO`, `COTIZADO`, `TEST_DRIVE`, `NEGOCIACION`, `ENTREGADO`, `DESCARTADO`

### Mis Leads Asignados
```http
GET /api/leads/my-leads?page=0&size=20
```

### Leads Sin Asignar
```http
GET /api/leads/unassigned?page=0&size=20
```

### Obtener Lead por ID
```http
GET /api/leads/{id}
```

### Crear Lead
```http
POST /api/leads
Content-Type: application/json

{
  "firstName": "Juan",
  "lastName": "Pérez",
  "phone": "+54 11 1234 5678",
  "email": "juan@email.com",
  "dni": "30123456",
  "status": "NUEVO",
  "source": "WEB",
  "notes": "Interesado en Fiat Cronos"
}
```

### Actualizar Lead
```http
PUT /api/leads/{id}
Content-Type: application/json

{
  "firstName": "Juan Carlos",
  "status": "CONTACTADO"
}
```

### Eliminar Lead
```http
DELETE /api/leads/{id}
```

### Asignar Lead a Usuario
```http
POST /api/leads/{id}/assign?userId=2
```

### Cambiar Estado
```http
POST /api/leads/{id}/status?status=COTIZADO
```

### Estadísticas de Leads
```http
GET /api/leads/stats/by-status
```

**Response:**
```json
{
  "NUEVO": 25,
  "CONTACTADO": 15,
  "EN_SEGUIMIENTO": 10,
  "COTIZADO": 8,
  "ENTREGADO": 5
}
```

---

## 🚗 VEHÍCULOS

### Listar Vehículos (Paginado)
```http
GET /api/vehicles?page=0&size=20&sort=model,asc
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "vin": "3C6TRVDG6HE543210",
      "brand": "Fiat",
      "model": "Cronos",
      "year": 2025,
      "color": "Rojo",
      "priceList": 18500000.00,
      "status": "DISPONIBLE",
      "branch": "Sucursal Centro",
      "createdAt": "2026-04-01T00:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 3
}
```

### Listar Todos (Sin paginar)
```http
GET /api/vehicles/all
```

### Vehículos Disponibles
```http
GET /api/vehicles/available?page=0&size=20
```

### Buscar por Modelo
```http
GET /api/vehicles/search?model=Cronos&page=0&size=20
```

### Obtener por ID
```http
GET /api/vehicles/{id}
```

### Obtener por VIN
```http
GET /api/vehicles/vin/{vin}
```

### Crear Vehículo
```http
POST /api/vehicles
Content-Type: application/json

{
  "vin": "3C6TRVDG6HE543211",
  "brand": "Fiat",
  "model": "Pulse",
  "year": 2025,
  "color": "Azul",
  "priceList": 16500000.00,
  "status": "DISPONIBLE",
  "branch": "Sucursal Norte"
}
```

### Actualizar Vehículo
```http
PUT /api/vehicles/{id}
Content-Type: application/json

{
  "priceList": 17000000.00,
  "color": "Negro"
}
```

### Eliminar Vehículo
```http
DELETE /api/vehicles/{id}
```

### Reservar Vehículo
```http
POST /api/vehicles/{id}/reserve
```

### Marcar como Vendido
```http
POST /api/vehicles/{id}/sell
```

### Verificar Disponibilidad
```http
GET /api/vehicles/{id}/available
```

**Response:** `true` o `false`

### Estadísticas
```http
GET /api/vehicles/stats/by-status
```

---

## 💰 COTIZACIONES

### Listar Cotizaciones (Paginado)
```http
GET /api/quotations?page=0&size=20&sort=createdAt,desc
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "type": "FINANCIADO",
      "vehicleModel": "Fiat Cronos",
      "priceList": 18500000.00,
      "discount": 500000.00,
      "priceFinal": 18000000.00,
      "downPayment": 5000000.00,
      "financingMonths": 48,
      "interestRate": 12.5,
      "monthlyPayment": 352450.00,
      "totalInterest": 1917600.00,
      "totalFinancingCost": 19917600.00,
      "bank": "Santander",
      "lead": {
        "id": 1,
        "firstName": "Juan",
        "lastName": "Pérez"
      },
      "validUntil": "2026-04-20T10:30:00",
      "sentToCustomer": false,
      "createdAt": "2026-04-13T10:30:00"
    }
  ],
  "totalElements": 30,
  "totalPages": 2
}
```

### Listar Todas (Sin paginar)
```http
GET /api/quotations/all
```

### Cotizaciones por Lead
```http
GET /api/quotations/lead/{leadId}?page=0&size=20
```

### Cotizaciones por Tipo
```http
GET /api/quotations/type/{type}?page=0&size=20
```

Tipos: `CONTADO`, `FINANCIADO`, `PLAN_FIAT`

### Cotizaciones Válidas (no vencidas)
```http
GET /api/quotations/valid?page=0&size=20
```

### Obtener por ID
```http
GET /api/quotations/{id}
```

### Crear Cotización - CONTADO
```http
POST /api/quotations
Content-Type: application/json

{
  "type": "CONTADO",
  "vehicleModel": "Fiat Cronos",
  "priceList": 18500000,
  "discount": 500000,
  "priceFinal": 18000000,
  "lead": {
    "id": 1
  }
}
```

### Crear Cotización - FINANCIADO
```http
POST /api/quotations
Content-Type: application/json

{
  "type": "FINANCIADO",
  "vehicleModel": "Fiat Cronos",
  "priceList": 18500000,
  "discount": 500000,
  "priceFinal": 18000000,
  "downPayment": 5000000,
  "financingMonths": 48,
  "interestRate": 12.5,
  "bank": "Santander",
  "lead": {
    "id": 1
  }
}
```

**Campos calculados automáticamente:**
- `monthlyPayment`: Cuota mensual (sistema francés)
- `totalInterest`: Total de intereses
- `totalFinancingCost`: Costo total del financiamiento

### Crear Cotización - PLAN FIAT
```http
POST /api/quotations
Content-Type: application/json

{
  "type": "PLAN_FIAT",
  "vehicleModel": "Fiat Cronos",
  "priceList": 18500000,
  "discount": 0,
  "priceFinal": 18500000,
  "planType": "70/30",
  "planInstallments": 84,
  "lead": {
    "id": 1
  }
}
```

**Tipos de Plan Fiat:**
- `100%`: 100% financiado en cuotas
- `70/30`: 70% en cuotas, 30% al adjudicar
- `50/50`: 50% en cuotas, 50% al adjudicar
- `ADQUIRIDO`: Plan para usados

### Marcar como Enviada
```http
POST /api/quotations/{id}/send
```

### Eliminar Cotización
```http
DELETE /api/quotations/{id}
```

### Estadísticas por Tipo
```http
GET /api/quotations/stats/by-type
```

**Response:**
```json
{
  "CONTADO": 15,
  "FINANCIADO": 20,
  "PLAN_FIAT": 10
}
```

---

## 👥 USUARIOS

### Listar Usuarios
```http
GET /api/users
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Juan",
    "lastname": "Pérez",
    "email": "juan@concesionario.com",
    "role": "ADMIN",
    "phone": "+54 11 1234 5678",
    "active": true
  }
]
```

### Obtener Usuario por ID
```http
GET /api/users/{id}
```

### Crear Usuario
```http
POST /api/users
Content-Type: application/json

{
  "name": "María",
  "lastname": "González",
  "email": "maria@concesionario.com",
  "password": "SecurePass123",
  "role": "VENDEDOR",
  "phone": "+54 11 8765 4321"
}
```

Roles: `ADMIN`, `VENDEDOR`, `GERENTE`, `ADMINISTRATIVO`

### Actualizar Usuario
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "María Elena",
  "phone": "+54 11 9999 8888"
}
```

### Eliminar Usuario
```http
DELETE /api/users/{id}
```

### Mi Perfil
```http
GET /api/users/me
```

---

## 📋 ACTIVIDADES

### Listar Actividades
```http
GET /api/activities?leadId=1&page=0&size=20
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "type": "COTIZACION",
      "description": "Cotización FINANCIADO creada: Fiat Cronos - $18000000",
      "lead": {
        "id": 1,
        "firstName": "Juan",
        "lastName": "Pérez"
      },
      "user": {
        "id": 2,
        "name": "María",
        "lastname": "González"
      },
      "createdAt": "2026-04-13T10:30:00"
    }
  ],
  "totalElements": 50
}
```

### Actividades por Lead
```http
GET /api/activities/lead/{leadId}?page=0&size=20
```

### Timeline del Lead (cronología)
```http
GET /api/activities/lead/{leadId}/timeline
```

### Crear Actividad
```http
POST /api/activities
Content-Type: application/json

{
  "type": "LLAMADA",
  "description": "Llamada de seguimiento - cliente interesado",
  "lead": {
    "id": 1
  }
}
```

Tipos: `CREACION`, `ACTUALIZACION`, `COTIZACION`, `TEST_DRIVE`, `LLAMADA`, `EMAIL`, `VISITA`, `ESTADO_CAMBIADO`, `ASIGNACION`, `NOTA`

---

## 🚘 TEST DRIVES

### Listar Test Drives
```http
GET /api/test-drives?page=0&size=20
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "scheduledDate": "2026-04-15T14:00:00",
      "duration": 30,
      "status": "PENDIENTE",
      "notes": "Cliente quiere probar el Cronos",
      "lead": {
        "id": 1,
        "firstName": "Juan",
        "lastName": "Pérez"
      },
      "vehicle": {
        "id": 1,
        "brand": "Fiat",
        "model": "Cronos",
        "vin": "3C6TRVDG6HE543210"
      },
      "assignedTo": {
        "id": 2,
        "name": "María",
        "lastname": "González"
      }
    }
  ],
  "totalElements": 10
}
```

### Test Drives por Lead
```http
GET /api/test-drives/lead/{leadId}
```

### Test Drives por Vehículo
```http
GET /api/test-drives/vehicle/{vehicleId}
```

### Calendario (por fecha)
```http
GET /api/test-drives/calendar?date=2026-04-15
```

### Crear Test Drive
```http
POST /api/test-drives
Content-Type: application/json

{
  "scheduledDate": "2026-04-15T14:00:00",
  "duration": 30,
  "notes": "Cliente quiere probar el Cronos",
  "lead": {
    "id": 1
  },
  "vehicle": {
    "id": 1
  }
}
```

### Completar Test Drive
```http
POST /api/test-drives/{id}/complete
Content-Type: application/json

{
  "feedback": "Cliente muy satisfecho, quiere cotizar"
}
```

### Cancelar Test Drive
```http
POST /api/test-drives/{id}/cancel?reason=Cliente no pudo asistir
```

---

## 📄 DOCUMENTOS

### Listar Documentos del Lead
```http
GET /api/documents/lead/{leadId}
```

**Response:**
```json
[
  {
    "id": 1,
    "type": "DNI",
    "originalFilename": "dni_juan.pdf",
    "storedFilename": "dni_juan_abc123.pdf",
    "contentType": "application/pdf",
    "size": 245760,
    "uploadedAt": "2026-04-13T10:30:00"
  }
]
```

### Subir Documento
```http
POST /api/documents/upload
Content-Type: multipart/form-data

leadId: 1
type: DNI
file: [archivo.pdf]
```

Tipos: `DNI`, `LICENCIA`, `PASAPORTE`, `COMPROBANTE_DOMICILIO`, `RECIBO_SUELDO`, `FICHA_INSCRIPCION`, `CONTRATO`, `OTRO`

### Descargar Documento
```http
GET /api/documents/{id}/download
```

**Response:** Archivo binario con `Content-Disposition: attachment`

### Eliminar Documento
```http
DELETE /api/documents/{id}
```

---

## 📊 EXCEL UPLOAD

### Subir Excel de Leads
```http
POST /api/excel/upload-leads
Content-Type: multipart/form-data

file: [leads.xlsx]
```

**Formato esperado del Excel:**
| Nombre | Apellido | Teléfono | Email | DNI | Fuente |
|--------|----------|----------|-------|-----|--------|
| Juan | Pérez | +54 11 1234 5678 | juan@email.com | 30123456 | WEB |
| María | López | +54 11 8765 4321 | maria@email.com | 30123457 | REFERIDO |

**Response:**
```json
{
  "success": true,
  "processed": 25,
  "duplicates": 3,
  "errors": 1,
  "message": "24 leads creados exitosamente. 3 duplicados ignorados. 1 error."
}
```

### Procesar Excel (endpoint manual)
```http
POST /api/excel/process
Content-Type: multipart/form-data

file: [leads.xlsx]
```

---

## 📈 DASHBOARD/STATS

### Dashboard General
```http
GET /api/dashboard
```

**Response:**
```json
{
  "leads": {
    "total": 150,
    "newThisMonth": 45,
    "byStatus": {
      "NUEVO": 25,
      "CONTACTADO": 15,
      "EN_SEGUIMIENTO": 10,
      "COTIZADO": 8,
      "ENTREGADO": 5
    }
  },
  "vehicles": {
    "total": 50,
    "available": 30,
    "reserved": 10,
    "sold": 10
  },
  "quotations": {
    "total": 80,
    "thisMonth": 25,
    "byType": {
      "CONTADO": 30,
      "FINANCIADO": 35,
      "PLAN_FIAT": 15
    },
    "totalValue": 1450000000.00
  },
  "testDrives": {
    "total": 40,
    "pending": 15,
    "completed": 25
  },
  "conversionRate": 12.5
}
```

### KPIs Principales
```http
GET /api/dashboard/kpis
```

### Pipeline de Ventas
```http
GET /api/dashboard/pipeline
```

**Response:**
```json
{
  "NUEVO": { "count": 25, "value": 0 },
  "CONTACTADO": { "count": 15, "value": 150000000 },
  "EN_SEGUIMIENTO": { "count": 10, "value": 200000000 },
  "COTIZADO": { "count": 8, "value": 180000000 },
  "TEST_DRIVE": { "count": 3, "value": 90000000 },
  "NEGOCIACION": { "count": 2, "value": 60000000 },
  "ENTREGADO": { "count": 5, "value": 150000000 }
}
```

---

## 🔧 MODELOS DE DATOS

### Lead
```json
{
  "id": 1,
  "firstName": "string (required)",
  "lastName": "string (required)",
  "phone": "string",
  "email": "string",
  "dni": "string",
  "address": "string",
  "city": "string",
  "status": "NUEVO | CONTACTADO | EN_SEGUIMIENTO | COTIZADO | TEST_DRIVE | NEGOCIACION | ENTREGADO | DESCARTADO",
  "source": "WEB | REFERIDO | CARTEL | REDES_SOCIALES | EXCEL | TELEFONO | VISITA | OTRO",
  "notes": "string",
  "assignedTo": { "id": 1 },
  "createdAt": "2026-04-13T10:30:00",
  "updatedAt": "2026-04-13T10:30:00"
}
```

### Vehicle
```json
{
  "id": 1,
  "vin": "string (unique, required)",
  "brand": "string",
  "model": "string (required)",
  "year": 2025,
  "color": "string",
  "priceList": "decimal",
  "status": "DISPONIBLE | RESERVADO | VENDIDO | NO_DISPONIBLE",
  "branch": "string",
  "notes": "string",
  "createdAt": "2026-04-13T10:30:00",
  "updatedAt": "2026-04-13T10:30:00"
}
```

### Quotation
```json
{
  "id": 1,
  "type": "CONTADO | FINANCIADO | PLAN_FIAT",
  "vehicleModel": "string",
  "priceList": "decimal",
  "discount": "decimal",
  "priceFinal": "decimal (required)",
  "downPayment": "decimal",
  "financingMonths": "integer",
  "interestRate": "decimal",
  "monthlyPayment": "decimal (calculated)",
  "bank": "string",
  "planType": "100% | 70/30 | 50/50 | ADQUIRIDO",
  "planInstallments": "integer",
  "planInstallmentAmount": "decimal",
  "planAdjudication": "integer",
  "totalInterest": "decimal (calculated)",
  "totalFinancingCost": "decimal (calculated)",
  "notes": "string",
  "lead": { "id": 1 },
  "validUntil": "2026-04-20T10:30:00",
  "sentToCustomer": false,
  "sentAt": null,
  "createdAt": "2026-04-13T10:30:00"
}
```

### User
```json
{
  "id": 1,
  "name": "string (required)",
  "lastname": "string (required)",
  "email": "string (unique, required)",
  "password": "string (required - min 6 chars)",
  "role": "ADMIN | VENDEDOR | GERENTE | ADMINISTRATIVO",
  "phone": "string",
  "active": true,
  "createdAt": "2026-04-13T10:30:00"
}
```

---

## ⚠️ CÓDIGOS DE ERROR

| Código | Descripción |
|--------|-------------|
| **400** | Bad Request - Datos inválidos |
| **401** | Unauthorized - Token inválido o expirado |
| **403** | Forbidden - Sin permisos para esta operación |
| **404** | Not Found - Recurso no encontrado |
| **409** | Conflict - Conflicto (ej: DNI duplicado) |
| **422** | Unprocessable Entity - Validación de negocio falló |
| **500** | Internal Server Error - Error del servidor |

### Formato de Error
```json
{
  "timestamp": "2026-04-13T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El precio final es obligatorio para cotización al contado",
  "path": "/api/quotations"
}
```

---

## 📊 PAGINACIÓN

Todos los endpoints de listado soportan paginación con estos parámetros:

| Parámetro | Descripción | Default |
|-----------|-------------|---------|
| `page` | Número de página (0-indexed) | 0 |
| `size` | Tamaño de página | 20 |
| `sort` | Campo y dirección | createdAt,desc |

**Ejemplos:**
```
GET /api/leads?page=0&size=10
GET /api/leads?page=1&size=20&sort=firstName,asc
GET /api/vehicles?sort=priceList,desc
GET /api/quotations?page=0&size=50&sort=createdAt,desc
```

---

## 🔐 SEGURIDAD

### Roles y Permisos

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Todos los endpoints |
| **GERENTE** | Dashboard, reportes, todos los leads/vehículos/cotizaciones |
| **VENDEDOR** | Sus leads asignados, cotizaciones, test drives |
| **ADMINISTRATIVO** | Documentos, Excel upload, usuarios (solo lectura) |

### Headers Requeridos

```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

---

## 📝 NOTAS

- **Multi-tenant:** Todos los endpoints filtran automáticamente por `tenantId` del JWT
- **Timezone:** Todas las fechas en UTC-3 (Argentina)
- **Moneda:** Pesos Argentinos (ARS)
- **Formato de fechas:** ISO 8601 (`YYYY-MM-DDTHH:mm:ss`)
- **Decimales:** 2 decimales para valores monetarios

---

**Documentación generada:** Abril 2026  
**Versión API:** 1.1  
**Postman Collection:** Disponible en `/docs/postman-collection.json`
