# Caso de Uso: Documentación del Comprador (Lead Documents)

Especificación completa del módulo de documentos del comprador para implementación en el frontend React.

---

## 1. Entidades

### DocumentType (enum del backend)

```typescript
type DocumentType =
  | 'DNI_FRENTE'
  | 'DNI_DORSO'
  | 'CUIL_CUIT'
  | 'RECIBO_SUELDO_1'
  | 'RECIBO_SUELDO_2'
  | 'RECIBO_SUELDO_3'
  | 'SERVICIO'
  | 'GARANTE_DNI_FRENTE'
  | 'GARANTE_DNI_DORSO'
  | 'GARANTE_CUIL'
  | 'GARANTE_RECIBO_1'
  | 'CONTRATO_RESERVA'
  | 'ORDEN_COMPRA'
  | 'OTRO';
```

### LeadDocument (respuesta de la API)

```typescript
interface LeadDocument {
  id: number;
  type: DocumentType;
  fileName: string;           // nombre original ej: "dni_juan.jpg"
  fileSize: number;           // bytes
  mimeType: string;           // ej: "image/jpeg", "application/pdf"
  uploadedAt: string;         // ISO 8601
  uploadedBy: { id: number; name: string; lastname: string };
  verified: boolean;
  verifiedAt?: string;        // ISO 8601
  verifiedBy?: { id: number; name: string; lastname: string };
  notes?: string;
}
```

### DocumentStats

```typescript
interface DocumentStats {
  totalDocs: number;
  verifiedDocs: number;
  pendingDocs: number;
}
```

---

## 2. Endpoints

| Método | URL | Descripción | Auth |
|--------|-----|-------------|------|
| `POST` | `/api/documents` | Subir documento (multipart) | Bearer |
| `GET` | `/api/documents/lead/{leadId}` | Listar documentos del lead | Bearer |
| `GET` | `/api/documents/lead/{leadId}/type/{type}` | Filtrar por tipo | Bearer |
| `GET` | `/api/documents/lead/{leadId}/verified` | Solo verificados | Bearer |
| `GET` | `/api/documents/lead/{leadId}/checklist` | Tipos ya verificados | Bearer |
| `GET` | `/api/documents/lead/{leadId}/stats` | Estadísticas | Bearer |
| `GET` | `/api/documents/{id}/download` | Descargar archivo | Bearer |
| `PUT` | `/api/documents/{id}/verify` | Verificar documento | Bearer |
| `DELETE` | `/api/documents/{id}` | Eliminar documento | Bearer |

---

## 3. Request/Response Detallados

### Subir documento (`POST /api/documents`)

**Content-Type:** `multipart/form-data`

```typescript
const formData = new FormData();
formData.append('document', JSON.stringify({
  lead: { id: 42 },
  type: 'DNI_FRENTE',
  notes: 'Foto con buena iluminacion'
}));
formData.append('file', file); // File object del <input type="file">
```

**Response:** `200 OK` -> `LeadDocument`

### Verificar documento (`PUT /api/documents/{id}/verify`)

**Response:** `200 OK` -> `LeadDocument` (con `verified: true`, `verifiedAt`, `verifiedBy`)

### Stats (`GET /api/documents/lead/{leadId}/stats`)

**Response:**

```json
{ "totalDocs": 5, "verifiedDocs": 3, "pendingDocs": 2 }
```

---

## 4. Flujos de Pantalla (React)

### 4.1 Vista del Lead -> Tab "Documentos"

```
+------------------------------------------+
| Documentos del comprador                 |
|                                          |
| [Subir documento v] [Ver checklist]      |
|                                          |
| Stats: 3/5 verificados | 2 pendientes    |
| ##########....................  60%       |
|                                          |
| Tipo           | Estado  | Subido por   |
| ---------------------------------------- |
| DNI Frente     | OK      | Maria G.     |
| DNI Dorso      | OK      | Maria G.     |
| Recibo 1       | Pend.   | Maria G.     |
| Recibo 2       | Pend.   | Maria G.     |
| CUIL           | OK      | Maria G.     |
|                                          |
| [Ver] [Verificar] [Eliminar]             |
+------------------------------------------+
```

### 4.2 Modal de Subida

- Dropdown: seleccionar `DocumentType`
- `<input type="file" accept=".pdf,.jpg,.jpeg,.png">`
- Preview del archivo (thumbnail)
- Campo opcional: `notes`
- Boton "Subir"

### 4.3 Checklist Visual

Muestra los tipos obligatorios para una operacion financiada:

```
[ ] DNI Frente
[ ] DNI Dorso
[ ] CUIL/CUIT
[ ] Recibo sueldo 1
[ ] Recibo sueldo 2
[ ] Recibo sueldo 3
[ ] Factura servicio
[ ] Garante DNI (si aplica)
```

El endpoint `/checklist` devuelve los que YA estan verificados para tacharlos.

---

## 5. Reglas de Negocio

| Regla | Implementacion en React |
|-------|------------------------|
| Solo GERENTE/SUPERVISOR pueden verificar | Ocultar boton "Verificar" si rol !== GERENTE/SUPERVISOR |
| Cualquier usuario autenticado puede subir | Mostrar "Subir" a VENDEDORA/PLANES tambien |
| Archivos: PDF, JPG, PNG, DOC | Validar `accept` en `<input>` y mostrar error |
| Maximo tamano: 10MB | Validar antes de enviar |
| No duplicados del mismo tipo? | Backend no lo valida; frontend puede advertir |
| Descarga con `Content-Disposition` | Abrir en nueva pestana o usar `window.open` con token |

---

## 6. Estados de un Documento

```typescript
function getDocumentStatus(doc: LeadDocument): 'pending' | 'verified' | 'rejected' {
  if (doc.verified) return 'verified';
  return 'pending';
  // Nota: no hay "rejected" en el backend aun, solo verified=true/false
}
```

---

## 7. Dependencias por Rol

| Rol | Puede subir | Puede verificar | Puede eliminar |
|-----|-------------|-----------------|----------------|
| GERENTE | Si | Si | Si |
| SUPERVISOR | Si | Si | Si |
| VENDEDORA | Si | No | No |
| PLANES | Si | No | No |

---

## 8. Documentacion Real que Necesita un Comprador de 0km (Argentina)

### Para compra CONTADO:
- DNI (frente + dorso)
- CUIL/CUIT

### Para compra FINANCIADA:
- Todo lo anterior +
- 3 ultimos recibos de sueldo
- Factura de servicio a nombre (prueba de domicilio)
- Si aplica: DNI + recibo del garante

### Para la operacion misma (generada por la concesionaria):
- Contrato de reserva firmado
- Orden de compra

---

## 9. Posibles Tipos de Documento Faltantes

| Faltante | Impacto |
|----------|---------|
| LICENCIA_CONDUCIR | Obligatoria para test drive y entrega |
| DATOS_BANCARIOS | Si paga contado (CBU/alias) |
| FORMULARIO_08 (DNRPA) | Para inscripcion del auto |
| CEDULA_VEHICULO | Si es usado (parte de pago) |
| COTIZACION_SEGURO | Obligatorio para 0km |

---

*Especificacion basada en el backend Concessio CRM*
