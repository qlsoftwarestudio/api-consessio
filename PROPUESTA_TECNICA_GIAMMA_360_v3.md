# 📋 PROPUESTA TÉCNICA - Concessio MVP (Versión Refinada)

**Cliente:** Concessio Fiat CABA  
**Fecha:** Abril 2026  
**Versión:** 3.0 (Post-Feedback Gerencia)  
**Elaborado por:** QLSoftware Studio - Emilio Quilodran

---

## 1. RESUMEN EJECUTIVO

Esta propuesta técnica detalla la implementación de **Concessio**, sistema de gestión comercial para Concessio Fiat CABA.

### Alcance del MVP (1 mes de desarrollo)
- **Sucursal Piloto:** Belgrano (8 vendedoras)
- **Usuarios Piloto:** 5 personas (María, Laura, Patricia, Roberto, Javier)
- **Funcionalidades Core:** Pipeline de venta, carga masiva Excel, stock multi-sucursal, cotizador integrado, documentación digital, agenda test drives

### Inversión
| Concepto | Costo (USD) |
|----------|-------------|
| Desarrollo MVP (1 mes) | $8,000 |
| Carga Masiva Excel + Mapeo Inteligente | $1,500 |
| Cotizador Integrado + Documentación Digital | $1,500 |
| Implementación y Training | $1,000 |
| Hosting/Infraestructura + Soporte (año 1) | $1,500 |
| **TOTAL AÑO 1** | **$12,500** |

---

## 2. ARQUITECTURA TÉCNICA

### 2.1 Stack Tecnológico

| Capa | Tecnología | Versión | Justificación |
|------|------------|---------|---------------|
| **Backend** | Spring Boot | 4.0.2 | Enterprise-grade, reutilizable de base existente |
| **Lenguaje** | Java | 21 | LTS, performance moderna |
| **Base de Datos** | PostgreSQL | 15+ | Multi-tenant, robusto |
| **Frontend** | React + TypeScript | 18.x | Componentes reutilizables |
| **UI Framework** | Tailwind CSS | 3.x | Desarrollo rápido |
| **API** | REST + OpenAPI | 3.0 | Documentación automática |
| **Seguridad** | JWT + Spring Security | - | Tokens stateless, roles granulares |
| **Excel Processing** | Apache POI | 5.x | Lectura .xlsx/.xls nativa |
| **WhatsApp** | WhatsApp Business API | - | Notificaciones test drive |
| **Hosting** | Railway | - | Serverless, escalable |
| **Storage** | AWS S3 / B2 | - | Archivos, documentación digital |

### 2.2 Arquitectura Multi-Tenant

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENTE (Browser/App)                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        RAILWAY (Cloud)                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Spring Boot Application                                │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │   │
│  │  │ Auth Layer  │ │ Tenant      │ │ Business Logic  │   │   │
│  │  │ (JWT)       │ │ Context     │ │ (Leads/Sales)   │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────────┘   │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │   │
│  │  │ Excel       │ │ Cotizador   │ │ Test Drive      │   │   │
│  │  │ Processor   │ │ Engine      │ │ Scheduler       │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────────┘   │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │   │
│  │  │ Documentos  │ │ WhatsApp    │ │ Stock VIN       │   │   │
│  │  │ Digitales   │ │ Notif.      │ │ Multi-Sucursal  │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  PostgreSQL (Multi-Tenant per Schema)                   │   │
│  │  ├── tenant_belgrano (pilot)                            │   │
│  │  ├── tenant_mataderos (future)                          │   │
│  │  ├── tenant_alsina (future)                             │   │
│  │  └── tenant_pavon (future)                              │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 Modelo de Datos - Entidades Principales

```
┌──────────────────────────────────────────────────────────────────┐
│  TENANT (Sucursal)                                               │
│  ├── id: Long (PK)                                               │
│  ├── name: String (Belgrano, Mataderos, Alsina, Pavon)           │
│  ├── status: Enum (ACTIVE, INACTIVE)                             │
│  ├── createdAt: DateTime                                          │
│  └── users: List<User>                                           │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│  USER (Vendedora/Gerente/Admin)                                  │
│  ├── id: Long (PK)                                               │
│  ├── tenantId: Long (FK)                                         │
│  ├── email: String (único por tenant)                            │
│  ├── name: String                                                 │
│  ├── lastname: String                                            │
│  ├── role: Enum (GERENTE, SUPERVISOR, VENDEDORA, PLANES, ADMIN) │
│  ├── sucursal: String (preferencia trabajo)                      │
│  ├── isActive: Boolean                                          │
│  └── password: String (bcrypt)                                   │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│  LEAD (Cliente Potencial)                                        │
│  ├── id: Long (PK)                                               │
│  ├── tenantId: Long (FK)                                         │
│  ├── assignedTo: Long (FK → User, nullable)                      │
│  │                                                                 │
│  │  # Datos de Contacto                                          │
│  ├── firstName: String                                           │
│  ├── lastName: String                                            │
│  ├── phone: String (normalizado +54 9 11...)                     │
│  ├── email: String (nullable)                                    │
│  ├── dni: String (para consulta Plan)                             │
│  │                                                                 │
│  │  # Origen y Tracking                                           │
│  ├── source: Enum (WHATSAPP, WEB, FERIA, REFERIDO, CARGA_EXCEL) │
│  ├── sourceDetail: String (ej: "Feria Belgrano Marzo 2025")      │
│  ├── createdAt: DateTime                                         │
│  ├── updatedAt: DateTime                                         │
│  ├── lastContactAt: DateTime (nullable)                        │
│  │                                                                 │
│  │  # Interés                                                    │
│  ├── vehicleInterest: String (ej: "Fiat Cronos Drive 1.3")     │
│  ├── vehicleType: Enum (KM0, PLAN, USADO)                        │
│  │                                                                 │
│  │  # Estado del Pipeline                                        │
│  ├── status: Enum                                                │
│  │   ├── NUEVO (lead recién entrado)                             │
│  │   ├── CONTACTADO (vendedora llamó/mandó msg)                 │
│  │   ├── COTIZADO (precio enviado)                              │
│  │   ├── TEST_DRIVE_AGENDADO (turno programado)                │
│  │   ├── TEST_DRIVE_COMPLETADO (probó auto)                     │
│  │   ├── NEGOCIACION (discutiendo términos)                      │
│  │   ├── DOCUMENTACION_PENDIENTE (faltan papeles)               │
│  │   ├── DOCUMENTACION_COMPLETA (listo para reserva)             │
│  │   ├── RESERVADO (seña pagada, unidad bloqueada)              │
│  │   ├── ENTREGADO (venta completada)                            │
│  │   ├── CANCELADO (lead perdido)                               │
│  │   └── NO_CONTESTA (3 intentos sin respuesta)                 │
│  │                                                                 │
│  │  # Relaciones                                                 │
│  ├── notes: List<LeadNote>                                        │
│  ├── activities: List<Activity>                                  │
│  ├── quotations: List<Quotation>                                 │
│  ├── testDrives: List<TestDrive>                                 │
│  ├── documents: List<LeadDocument>                               │
│  └── sale: Sale (nullable, si convirtió)                        │
└──────────────────────────────────────────────────────────────────┘
```

### 2.4 Entidades Adicionales

```
┌──────────────────────────────────────────────────────────────────┐
│  VEHICLE (Stock de Unidades)                                     │
│  ├── id: Long (PK)                                               │
│  ├── tenantId: Long (FK)                                         │
│  ├── sucursal: String (ubicación física)                         │
│  ├── vin: String (único nacional)                                │
│  ├── modelo: String (ej: "Fiat Cronos Drive 1.3")                │
│  ├── color: String                                               │
│  ├── año: Integer                                                │
│  ├── precioLista: BigDecimal                                     │
│  ├── precioContado: BigDecimal                                   │
│  ├── status: Enum (DISPONIBLE, RESERVADO, VENDIDO, TRANSITO)    │
│  ├── reservadoPor: Long (FK → Lead, nullable)                   │
│  ├── fechaReserva: DateTime (nullable)                           │
│  └── isActive: Boolean                                           │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│  QUOTATION (Cotización Generada)                                   │
│  ├── id: Long (PK)                                               │
│  ├── leadId: Long (FK)                                             │
│  ├── vehicleModel: String                                          │
│  ├── tipoVenta: Enum (CONTADO, FINANCIADO, PLAN)                 │
│  ├── precioVehiculo: BigDecimal                                  │
│  ├── entregaMinima: BigDecimal                                   │
│  ├── cuotas: Integer (si aplica)                                 │
│  ├── montoCuota: BigDecimal (si aplica)                          │
│  ├── tasaInteres: Decimal (si aplica)                            │
│  ├── totalFinanciado: BigDecimal                                 │
│  ├── fechaCreacion: DateTime                                     │
│  ├── enviadaAlCliente: Boolean                                   │
│  ├── fechaEnvio: DateTime (nullable)                             │
│  └── estado: Enum (BORRADOR, ENVIADA, ACEPTADA, RECHAZADA)       │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│  TEST_DRIVE (Agenda de Prueba de Manejo)                          │
│  ├── id: Long (PK)                                               │
│  ├── leadId: Long (FK)                                             │
│  ├── vehicleId: Long (FK, nullable - unidad específica)          │
│  ├── vehicleModel: String (modelo solicitado)                    │
│  ├── fechaHora: DateTime                                         │
│  ├── sucursal: String (dónde se hace)                            │
│  ├── vendedoraId: Long (FK - quién agenda)                       │
│  ├── estado: Enum (AGENDADO, CONFIRMADO, COMPLETADO, CANCELADO) │
│  ├── notasResultado: String (qué opinó el cliente)               │
│  ├── recordatorioEnviado: Boolean                                │
│  ├── createdAt: DateTime                                         │
│  └── updatedAt: DateTime                                         │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│  LEAD_DOCUMENT (Documentación del Cliente)                        │
│  ├── id: Long (PK)                                               │
│  ├── leadId: Long (FK)                                             │
│  ├── tipo: Enum (DNI_FRENTE, DNI_DORSO, CUIL, RECIBO_SUELDO,    │
│  │               SERVICIO, GARANTE_DNI, OTRA)                   │
│  ├── nombreArchivo: String                                         │
│  ├── storageUrl: String (URL en S3/B2)                           │
│  ├── tamañoBytes: Long                                           │
│  ├── mimeType: String                                            │
│  ├── subidoPor: Long (FK → User)                                 │
│  ├── fechaSubida: DateTime                                       │
│  ├── verificado: Boolean                                         │
│  └── notas: String                                               │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│  EXCEL_UPLOAD (Registro de Cargas Masivas)                       │
│  ├── id: Long (PK)                                               │
│  ├── tenantId: Long (FK)                                         │
│  ├── uploadedBy: Long (FK → User)                                │
│  ├── filename: String                                            │
│  ├── fileSize: Long (bytes)                                      │
│  ├── totalRows: Integer                                          │
│  ├── validRows: Integer                                          │
│  ├── invalidRows: Integer                                        │
│  ├── duplicateRows: Integer (detectados)                         │
│  ├── status: Enum (UPLOADED, PROCESSING, COMPLETED, ERROR)      │
│  ├── columnMapping: JSON (mapeo de columnas → campos)            │
│  ├── errors: JSON (filas con error, descripción)                 │
│  ├── createdAt: DateTime                                         │
│  └── processedAt: DateTime (nullable)                            │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│  ACTIVITY (Log de Actividades)                                   │
│  ├── id: Long (PK)                                               │
│  ├── leadId: Long (FK)                                           │
│  ├── userId: Long (FK, quién hizo la acción)                     │
│  ├── type: Enum (LLAMADA, WHATSAPP, EMAIL, COTIZACION,          │
│  │                TEST_DRIVE, RESERVA, DOCUMENTO_SUBIDO, NOTE)   │
│  ├── description: String                                         │
│  ├── metadata: JSON (datos adicionales)                          │
│  └── createdAt: DateTime                                         │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│  SALE (Venta Concretada)                                         │
│  ├── id: Long (PK)                                               │
│  ├── leadId: Long (FK)                                           │
│  ├── tenantId: Long (FK)                                         │
│  ├── vendedoraId: Long (FK → User)                               │
│  ├── quotationId: Long (FK → Quotation aceptada)                 │
│  ├── vin: String (número de chasis)                              │
│  ├── modelo: String                                              │
│  ├── color: String                                               │
│  ├── tipoVenta: Enum (CONTADO, FINANCIADO, PLAN)                 │
│  ├── montoSeña: BigDecimal                                       │
│  ├── montoTotal: BigDecimal                                      │
│  ├── comisionVendedora: BigDecimal (calculado)                   │
│  ├── fechaReserva: DateTime                                      │
│  ├── fechaEntrega: DateTime                                      │
│  └── isActive: Boolean                                           │
└──────────────────────────────────────────────────────────────────┘
```

---

## 3. FUNCIONALIDADES DETALLADAS

### 3.1 Módulo: Carga Masiva Excel

#### 3.1.1 Requerimientos Funcionales

| ID | Requerimiento | Prioridad | Criterios de Aceptación |
|----|---------------|-----------|------------------------|
| CM-01 | Soportar formatos .xlsx, .xls, .csv | Alta | Archivos de ejemplo procesados correctamente |
| CM-02 | Preview de 10 filas antes de confirmar | Alta | Usuario ve datos mapeados antes de cargar |
| CM-03 | Normalización automática de teléfonos | Alta | "15 6677 8899" → "+5491166778899" |
| CM-04 | Detección de duplicados | Alta | Alerta si teléfono/email ya existe |
| CM-05 | Mapeo flexible de columnas | Media | UI para asignar columnas del Excel |
| CM-06 | Máximo 10,000 filas por archivo | Media | Rechazar archivos mayores |
| CM-07 | Asignación automática post-carga | Alta | Distribución equitativa a vendedoras |
| CM-08 | Reporte de errores descargable | Media | Excel con filas que fallaron |
| CM-09 | Soporte formato estándar CRM | Alta | Template para importación genérica |

#### 3.1.2 Template Excel Estándar

| Columna | Tipo | Requerido | Ejemplo |
|-----------|------|-----------|---------|
| Nombre | String | Sí | Juan |
| Apellido | String | Sí | Pérez |
| Telefono | String | Sí | 1155667788 |
| Email | String | No | juan@email.com |
| DNI | String | No | 30123456 |
| Modelo_Interes | String | No | Fiat Cronos |
| Tipo_Vehiculo | String | No | KM0/PLAN/USADO |
| Origen | String | No | WhatsApp/Facebook/Feria |
| Notas | String | No | Tiene auto para permuta |
| Vendedora_Asignada | String | No | María González |

---

### 3.2 Módulo: Cotizador Inteligente

#### 3.2.1 Requerimientos Funcionales

| ID | Requerimiento | Prioridad | Criterios de Aceptación |
|----|---------------|-----------|------------------------|
| CI-01 | Comparar 3 opciones en una pantalla | Alta | Contado, Financiado, Plan |
| CI-02 | Cálculo automático de cuotas | Alta | Ingresar entrega mínima, calcular cuotas |
| CI-03 | Tasas configurables por admin | Media | Roberto actualiza tasas Santander |
| CI-04 | Guardar historial de cotizaciones | Alta | Ver cotizaciones previas por lead |
| CI-05 | Enviar cotización por WhatsApp/Email | Alta | Botón "Enviar al cliente" |
| CI-06 | Marcar cotización como aceptada | Media | Transición a estado NEGOCIACION |

#### 3.2.2 Flujo del Cotizador

```
┌─────────────────────────────────────────────────────────────────┐
│  PASO 1: SELECCIÓN DE VEHÍCULO                                  │
│  - Buscar en stock por modelo/color                             │
│  - O ingresar modelo manualmente si no está en stock            │
│                                                                 │
│  PASO 2: SELECCIÓN DE TIPO DE VENTA                             │
│  ┌─────────────┐ ┌──────────────┐ ┌──────────────┐             │
│  │  CONTADO    │ │ FINANCIADO   │ │ PLAN FIAT    │             │
│  │             │ │ Santander    │ │ 4 opciones   │             │
│  │ Precio      │ │              │ │              │             │
│  │ de lista    │ │ Entrega      │ │ Cuotas       │             │
│  │             │ │ mínima       │ │ fijas        │             │
│  └─────────────┘ └──────────────┘ └──────────────┘             │
│                                                                 │
│  PASO 3: CÁLCULO Y COMPARACIÓN                                  │
│  - Si Contado: mostrar precio final                             │
│  - Si Financiado: slider de entrega → calcula cuotas           │
│  - Si Plan: mostrar 4 opciones de planes disponibles           │
│                                                                 │
│  PASO 4: GUARDAR Y ENVIAR                                       │
│  - Guardar cotización en historial del lead                     │
│  - Botón "Enviar al cliente" (WhatsApp/Email)                │
│  - Botón "Cliente interesado" → cambia estado a COTIZADO       │
└─────────────────────────────────────────────────────────────────┘
```

#### 3.2.3 Fórmulas de Cálculo

**Financiación Santander (ejemplo configurable):**
```
Precio Vehículo: $18.500.000
Entrega Mínima (30%): $5.550.000
Monto a Financiar: $12.950.000
Tasa Anual: 25%
Plazo: 48 meses
Cuota Estimada: $415.250/mes
```

**Planes Fiat (consumidos desde API externa o manual):**
- Plan 100%: 84 cuotas de $220.238
- Plan 70/30: 60 cuotas de $258.333 + entrega 30%
- Plan 50/50: 24 cuotas de $385.417 + entrega 50%
- Adjudicación: 25 cuotas de $740.000

---

### 3.3 Módulo: Agenda Test Drive

#### 3.3.1 Requerimientos Funcionales

| ID | Requerimiento | Prioridad | Criterios de Aceptación |
|----|---------------|-----------|------------------------|
| TD-01 | Agendar turno con fecha/hora/sucursal | Alta | Calendario interactivo |
| TD-02 | Asignar vehículo específico disponible | Media | Seleccionar VIN del stock |
| TD-03 | Recordatorio automático 24hs antes | Alta | WhatsApp al cliente |
| TD-04 | Confirmación de asistencia | Media | Cliente confirma/rechaza |
| TD-05 | Registro de resultado | Alta | Notas post-test drive |
| TD-06 | Dashboard de turnos por sucursal | Media | Vista calendario semanal |

#### 3.3.2 Estados del Test Drive

```
AGENDADO → CONFIRMADO → COMPLETADO
    ↓           ↓            ↓
 Recordatorio  Check-in     Notas resultado
 automático    en sucursal  (cliente interesado?)
```

#### 3.3.3 Notificaciones WhatsApp

| Momento | Mensaje |
|---------|---------|
| Al agendar | "✅ Tu test drive del Fiat [Modelo] está agendado para el [Fecha] a las [Hora] en [Sucursal]. Dirección: [Dirección]. Traer DNI y licencia de conducir." |
| 24hs antes | "⏰ Recordatorio: Mañana tenés agendado el test drive del Fiat [Modelo] a las [Hora]. Te esperamos en [Sucursal]. Confirmar asistencia: [Link]" |
| Post-test | "🚗 Gracias por probar el Fiat [Modelo]. ¿Te gustaría recibir una cotización personalizada? Respondé SI y tu vendedora [Nombre] te contactará." |

---

### 3.4 Módulo: Documentación Digital

#### 3.4.1 Requerimientos Funcionales

| ID | Requerimiento | Prioridad | Criterios de Aceptación |
|----|---------------|-----------|------------------------|
| DD-01 | Subida de documentos por tipo | Alta | DNI, CUIL, recibos, etc. |
| DD-02 | Storage seguro en S3/B2 | Alta | URLs privadas, expiración |
| DD-03 | Checklist de documentación | Alta | Ver qué falta por lead |
| DD-04 | Notificación doc pendiente | Media | Alerta a vendedora |
| DD-05 | Verificación por supervisor | Baja | Check de documento válido |
| DD-06 | Bloqueo de reserva sin docs | Alta | Validar docs completos |

#### 3.4.2 Tipos de Documentos

| Tipo | Obligatorio | Descripción |
|------|-------------|-------------|
| DNI_FRENTE | Sí | Frente del DNI |
| DNI_DORSO | Sí | Dorso del DNI |
| CUIL_CUIT | Sí | Constancia CUIL/CUIT |
| RECIBO_SUELDO_1 | Sí | Último recibo |
| RECIBO_SUELDO_2 | Sí | Penúltimo recibo |
| RECIBO_SUELDO_3 | No | Antepenúltimo recibo |
| SERVICIO | No | Factura de servicio a nombre |
| GARANTE_DNI | Condicional | Si necesita garante |

#### 3.4.3 Flujo de Documentación

```
┌────────────────────────────────────────────────────────────────┐
│  LEAD EN NEGOCIACIÓN                                          │
│  Vendedora solicita documentación                             │
│         ↓                                                      │
│  ┌────────────────────────────────────────────────────────┐   │
│  │  CHECKLIST DIGITAL                                    │   │
│  │  ☑️ DNI Frente  ☑️ DNI Dorso  ☑️ CUIL                │   │
│  │  ⏳ Recibo 1    ⏳ Recibo 2   ⬜ Servicio            │   │
│  │                                                        │   │
│  │  Falta: Recibo 2                                       │   │
│  └────────────────────────────────────────────────────────┘   │
│         ↓                                                      │
│  Sistema envía WhatsApp al cliente:                           │
│  "Para avanzar con tu reserva, necesitamos que subas:         │
│   1. Foto de tu último recibo de sueldo                       │
│   Link seguro: [concessio.com/docs/abc123]"                 │
│         ↓                                                      │
│  Cliente sube documento → Sistema verifica tipo               │
│         ↓                                                      │
│  CHECKLIST ACTUALIZADO: ☑️ Recibo 2                          │
│         ↓                                                      │
│  TODOS LOS CHECKS ✅ → Estado: DOCUMENTACION_COMPLETA        │
│  → Habilitado para reservar                                   │
└────────────────────────────────────────────────────────────────┘
```

---

### 3.5 Módulo: Pipeline de Venta (Actualizado)

#### 3.5.1 Estados y Transiciones

```
                    ┌─────────────┐
         Entrada →  │   NUEVO     │
         (Excel,    └──────┬──────┘
          Manual)          │
                            │ Contacto
                            ▼
                    ┌─────────────┐
                    │ CONTACTADO  │◄──────┐
                    └──────┬──────┘       │
                           │ Cotización   │ No responde
                           ▼              │
                    ┌─────────────┐       │
              ┌────│  COTIZADO   │───────┘
              │    └──────┬──────┘
              │             │ Interés confirmado
              │             ▼
              │    ┌─────────────┐
              │    │TEST_DRIVE   │
              │    │_AGENDADO    │
              │    └──────┬──────┘
              │             │ Asiste
              │             ▼
              │    ┌─────────────┐
              └────│TEST_DRIVE   │
                   │_COMPLETADO  │
                   └──────┬──────┘
                          │ Negociación
                          ▼
                   ┌─────────────┐
                   │ NEGOCIACION │
                   └──────┬──────┘
                          │ Solicita docs
                          ▼
              ┌─────────────┴─────────────┐
              ▼                           ▼
    ┌──────────────────┐      ┌──────────────────┐
    │ DOCUMENTACION    │      │ DOCUMENTACION   │
    │ _PENDIENTE        │─────▶│ _COMPLETA       │
    └──────────────────┘      └────────┬─────────┘
                                       │ Reserva (seña)
                                       ▼
                            ┌────────────▼────────────┐
                            │       RESERVADO       │
                            │     (VIN bloqueado)   │
                            └───────────┬───────────┘
                                        │ Entrega
                                        ▼
                            ┌────────────▼────────────┐
                            │       ENTREGADO         │
                            │    (venta completada)   │
                            └─────────────────────────┘
```

#### 3.5.2 Reglas de Negocio Actualizadas

| Regla | Descripción | Implementación |
|-------|-------------|----------------|
| R-01 | Solo vendedoras pueden cambiar estado de sus leads | Validación en service layer |
| R-02 | Supervisor puede reasignar leads entre vendedoras | Endpoint especial con permiso |
| R-03 | Reserva requiere: docs completos, seña ≥ $100.000, VIN disponible | Validación síncrona |
| R-04 | Test drive requiere vehículo disponible o similar | Booking de unidad 2h |
| R-05 | Cotización enviada = estado COTIZADO automático | Trigger post-envío |
| R-06 | Documentación completa habilita botón "Reservar" | UI conditional |

---

## 4. CRONOGRAMA DE DESARROLLO (1 mes)

### Semana 1: Fundamentos y Carga Masiva

| Día | Tarea | Entregable |
|-----|-------|------------|
| L 21/04 | Setup proyecto, Railway/PostgreSQL | Repo inicial, CI/CD básico |
| M 22/04 | Entidades core: Tenant, User, Lead, Vehicle | Modelo de datos v1 |
| X 23/04 | Autenticación JWT + roles | Login funcional |
| J 24/04 | Carga Masiva Excel: Upload + Analysis | Endpoint analyze |
| V 25/04 | Carga Masiva: Mapping + Preview + Process | Procesamiento completo |

**Demo Semana 1:** Login, carga Excel, crear leads

### Semana 2: Pipeline y Cotizador

| Día | Tarea | Entregable |
|-----|-------|------------|
| L 28/04 | CRUD Leads + filtros + asignación | Dashboard vendedora |
| M 29/04 | Workflow estados básicos | Pipeline NUEVO→CONTACTADO→COTIZADO |
| X 30/04 | **Cotizador Inteligente** | Comparador 3 opciones |
| J 01/05 | Historial cotizaciones + envío | Cotización por WhatsApp/Email |
| V 02/05 | Stock VIN multi-sucursal | Inventario tiempo real |

**Demo Semana 2:** Crear lead → Cotizar → Ver stock → Comparar opciones

### Semana 3: Test Drive y Documentación

| Día | Tarea | Entregable |
|-----|-------|------------|
| L 05/05 | **Agenda Test Drive** | Calendario turnos |
| M 06/05 | Notificaciones test drive | WhatsApp recordatorios |
| X 07/05 | **Documentación Digital** | Upload por tipo, checklist |
| J 08/05 | Estados DOCUMENTACION_PENDIENTE/COMPLETA | Flujo documental |
| V 09/05 | Validación docs para reserva | Bloqueo sin docs completos |

**Demo Semana 3:** Agendar test drive → Subir documentos → Reservar con VIN

### Semana 4: Comisiones, Dashboards y Deploy

| Día | Tarea | Entregable |
|-----|-------|------------|
| L 12/05 | Dashboard comisiones automático | Reporte Roberto |
| M 13/05 | Dashboard gerencial (KPIs, funnel) | Panel Javier |
| X 14/05 | Testing integración + UX refinements | Suite de tests |
| J 15/05 | Training usuarios piloto | Sesión con María, Laura, Patricia |
| V 16/05 | Deploy a producción Belgrano | Concessio Live |

---

## 5. ROLES Y PERMISOS (RBAC)

| Funcionalidad | GERENTE | SUPERVISOR | VENDEDORA | PLANES | ADMIN |
|---------------|:-------:|:----------:|:---------:|:------:|:-----:|
| Ver todos los leads | ✅ | ✅ (sucursal) | ❌ (solo propios) | ❌ | ✅ |
| Cargar Excel masivo | ✅ | ✅ | ❌ | ❌ | ✅ |
| Crear cotización | ✅ | ✅ | ✅ | ❌ | ✅ |
| Enviar cotización cliente | ✅ | ✅ | ✅ | ❌ | ✅ |
| Agendar test drive | ✅ | ✅ | ✅ | ❌ | ✅ |
| Ver documentos lead | ✅ | ✅ | ✅ (propios) | ❌ | ✅ |
| Subir documentos | ✅ | ✅ | ✅ (propios) | ✅ | ✅ |
| Reservar unidad | ✅ | ✅ | ✅ | ❌ | ✅ |
| Ver dashboard comisiones | ✅ | ✅ | ✅ (propia) | ❌ | ✅ |
| Configurar tasas cotizador | ✅ | ❌ | ❌ | ❌ | ✅ |

---

## 6. INTEGRACIONES

### 6.1 Carga de Leads Multi-Fuente

**Opciones de importación:**
1. **Excel estándar:** Template definido con columnas fijas
2. **Pilot (webhook):** Si Concessio decide integrar en el futuro
3. **CRM genérico:** Formato estándar de exportación (HubSpot, Salesforce, etc.)

**Endpoint genérico:**
```http
POST /api/v1/leads/import
Content-Type: multipart/form-data
source: EXCEL | PILOT | GENERIC_CRM
file: <archivo>
mapping: { "columnMapping": { ... } }
```

### 6.2 WhatsApp Business API

**Alcance MVP:**
- Recordatorios test drive (24hs antes)
- Envío de cotizaciones (con PDF adjunto)
- Solicitud de documentación (link seguro)
- Confirmaciones de turnos

### 6.3 Storage de Documentos (S3/Backblaze B2)

**Estructura:**
```
bucket-concessio/
├── documents/
│   ├── {tenantId}/
│   │   ├── {leadId}/
│   │   │   ├── dni_frente_abc123.pdf
│   │   │   ├── recibo_sueldo_def456.jpg
│   │   │   └── ...
```

**Seguridad:**
- URLs presignadas con expiración (15 minutos)
- Acceso solo autenticado
- Encriptación en tránsito y reposo

---

## 7. SEGURIDAD Y COMPLIANCE

### 7.1 Medidas de Seguridad

| Capa | Medida |
|------|--------|
| **Autenticación** | JWT tokens con expiración 24h, refresh tokens 7 días |
| **Autorización** | RBAC granulado por rol y tenant |
| **Contraseñas** | BCrypt con salt, mínimo 8 caracteres |
| **Datos** | Aislamiento por tenant en PostgreSQL |
| **Archivos** | URLs presignadas, validación tipo MIME |
| **Transporte** | HTTPS obligatorio, TLS 1.3 |

### 7.2 Protección de Datos Personales (Argentina)

- Consentimiento explícito al subir documentos
- Retención configurable (auto-eliminación post-venta)
- Acceso restringido: solo vendedora asignada + supervisores
- Logs de acceso a documentos sensibles

---

## 8. INFRAESTRUCTURA

### 8.1 Railway (Producción)

| Servicio | Plan | Costo Mensual |
|----------|------|---------------|
| Web Service (Spring Boot) | Pro | $20 |
| PostgreSQL | Pro | $15 |
| Storage B2 (documentos) | - | ~$3 |
| **Total estimado** | | **~$40 USD/mes** |

### 8.2 Variables de Entorno

```bash
# Base de datos
DB_HOST=postgres.railway.internal
DB_PORT=5432
DB_NAME=railway
DB_USER=postgres
DB_PASSWORD=[generado]

# Seguridad
JWT_SECRET=[random 256 bits]
JWT_EXPIRATION=86400000

# Storage
B2_BUCKET_NAME=concessio-docs
B2_KEY_ID=[backblaze]
B2_APPLICATION_KEY=[backblaze]

# WhatsApp (Phase 2)
WA_API_KEY=[twilio/business api]
WA_PHONE_NUMBER_ID=[meta]

# Cotizador
TASA_SANTANDER_DEFAULT=25.0
PLAN_FIAT_API_URL=[si aplica]
```

---

## 9. KPIs Y MÉTRICAS DE ÉXITO

### Métricas Técnicas

| Métrica | Target |
|---------|--------|
| Uptime | 99.5% |
| Tiempo respuesta API | < 500ms |
| Carga masiva 1.000 filas | < 60 segundos |

### Métricas de Negocio

| KPI | Actual | Target 3 Meses |
|-----|--------|----------------|
| Tiempo lead → cotización | 30-120 min | < 15 min |
| % leads con test drive | ~20% | > 40% |
| Tiempo documentación completa | 3-5 días | < 24 horas |
| Leads perdidos/mes | ~150 | < 30 |
| Tiempo reporte comisiones | 3 días | < 1 hora |

---

## 10. DOCUMENTACIÓN ENTREGABLE

| Documento | Formato |
|-----------|---------|
| User Guide Vendedora | PDF + Video |
| Manual Cotizador y Test Drive | PDF |
| API Documentation | Swagger/OpenAPI |
| Código fuente | GitHub (privado) |
| Template Excel Estándar | .xlsx |

---

**Documento versión 3.0 - Abril 2026**  
**QLSoftware Studio**
