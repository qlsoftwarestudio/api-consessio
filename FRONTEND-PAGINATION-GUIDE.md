# Guía de Paginación para Frontend (Lovable IA)

## Resumen

Todos los endpoints de listado ahora están **paginados** para mejor performance. Se eliminaron los endpoints `/all` que devolvían listas completas.

---

## Endpoints Paginados

### Leads (`/api/leads`)

| Endpoint | Descripción |
|----------|-------------|
| `GET /api/leads` | Lista todos los leads |
| `GET /api/leads/status/{status}` | Filtra por estado (NUEVO, CONTACTADO, etc) |
| `GET /api/leads/my-leads` | Leads asignados al usuario actual |
| `GET /api/leads/unassigned` | Leads sin asignar |
| `GET /api/leads/search?query={texto}` | Búsqueda de leads |

### Vehículos (`/api/vehicles`)

| Endpoint | Descripción |
|----------|-------------|
| `GET /api/vehicles` | Lista todos los vehículos (paginado) |
| `GET /api/vehicles/available` | Solo vehículos disponibles |
| `GET /api/vehicles/search?model={texto}` | Búsqueda por modelo |

### Cotizaciones (`/api/quotations`)

| Endpoint | Descripción |
|----------|-------------|
| `GET /api/quotations` | Lista todas las cotizaciones (paginado) |
| `GET /api/quotations/lead/{leadId}` | Cotizaciones de un lead específico |
| `GET /api/quotations/type/{type}` | Por tipo (CONTADO, FINANCIADO, PLAN_FIAT) |
| `GET /api/quotations/valid` | Cotizaciones vigentes |

---

## Parámetros de Paginación

Spring Boot acepta estos parámetros estándar:

| Parámetro | Descripción | Ejemplo |
|-----------|-------------|---------|
| `page` | Número de página (0-based) | `?page=0` |
| `size` | Cantidad de elementos por página | `?size=20` |
| `sort` | Campo y dirección de ordenamiento | `?sort=createdAt,desc` |

### Ejemplos de URLs

```
GET /api/leads?page=0&size=10&sort=createdAt,desc
GET /api/vehicles?size=50&sort=priceCash,asc
GET /api/quotations/lead/123?page=2&size=5
```

---

## Respuesta Paginada (JSON)

Todas las respuestas siguen este formato:

```json
{
  "content": [
    { "id": 1, "name": "...", ... },
    { "id": 2, "name": "...", ... }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 150,
  "totalPages": 8,
  "last": false,
  "first": true,
  "numberOfElements": 20,
  "empty": false
}
```

### Campos útiles para el UI:

| Campo | Descripción |
|-------|-------------|
| `content` | Array con los datos de la página |
| `totalElements` | Total de registros en la base |
| `totalPages` | Total de páginas disponibles |
| `number` | Página actual (0-based) |
| `size` | Tamaño de la página |
| `first` | ¿Es la primera página? |
| `last` | ¿Es la última página? |

---

## Recomendaciones para UI

### Paginación básica
```javascript
// Ejemplo con fetch
const fetchLeads = async (page = 0, size = 10) => {
  const res = await fetch(`/api/leads?page=${page}&size=${size}`);
  return res.json();
};
```

### Tabla con paginación
- Mostrar `totalElements` como "Total: X registros"
- Botones "Anterior/Siguiente" usando `first` y `last`
- Select para cambiar `size` (10, 20, 50)

### Scroll infinito
- Detectar cuando `last === false` para cargar más
- Acumular `content` en un array local

---

## Notas

- **Se eliminaron** los endpoints `/api/vehicles/all` y `/api/quotations/all`
- Usar siempre los endpoints base (`/api/vehicles`, `/api/quotations`) con paginación
- Si no se envían parámetros, Spring usa defaults: `page=0, size=20`
