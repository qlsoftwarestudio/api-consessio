# Plan de Pruebas - Concessio CRM API

## Credenciales de Test

| Rol        | Email              | Password       | Tenant |
|------------|--------------------|----------------|--------|
| GERENTE    | admin@giamma.com   | admin123       | DEM    |
| SUPERVISOR | carlos@giamma.com  | supervisor123  | DEM    |
| VENDEDORA  | maria@giamma.com   | vendedora123   | DEM    |
| PLANES     | lucia@giamma.com   | planes123      | DEM    |

## Variable para usar en todos los tests

```bash
# Loguearse y guardar el token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"tenantCode":"DEM","email":"admin@giamma.com","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "Token: $TOKEN"
```

---

## 1. Autenticacion (Sin Token)

### 1.1 Onboarding
```bash
curl -X POST http://localhost:8080/auth/onboarding \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Test Auto",
    "adminName": "Juan",
    "adminLastname": "Perez",
    "adminEmail": "juan@testauto.com",
    "password": "test123"
  }'
```
**Esperado:** 200 OK con token

### 1.2 Login exitoso
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"tenantCode":"DEM","email":"admin@giamma.com","password":"admin123"}'
```
**Esperado:** 200 OK con token

### 1.3 Login fallido - password incorrecta
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"tenantCode":"DEM","email":"admin@giamma.com","password":"wrong"}'
```
**Esperado:** 401 Unauthorized

### 1.4 Login usuario inactivo (soft-delete)
```bash
# Primero borrar un usuario con GERENTE, luego intentar login con ese usuario
curl -X DELETE http://localhost:8080/users/2 \
  -H "Authorization: Bearer $TOKEN"

# Luego intentar login con el usuario borrado
```
**Esperado:** 401 "User not found or inactive"

### 1.5 Register nuevo usuario (requiere token de GERENTE/SUPERVISOR/ADMIN_SISTEMA)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Pedro",
    "lastname": "Lopez",
    "email": "pedro@giamma.com",
    "password": "pedro123",
    "role": "VENDEDORA",
    "isActive": true
  }'
```
**Esperado:** 200 OK con token del nuevo usuario

---

## 2. Usuarios

### 2.1 GET /users/me (todos los roles)
**GERENTE:**
```bash
curl -X GET http://localhost:8080/users/me \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```
**Esperado:** 200 OK con datos del usuario autenticado

### 2.2 GET /users (todos leen)
**VENDEDORA:**
```bash
curl -X GET "http://localhost:8080/users?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```
**Esperado:** 200 OK con lista de usuarios activos

### 2.3 POST /users (solo GERENTE/SUPERVISOR/ADMIN_SISTEMA)
**GERENTE - debe funcionar:**
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d '{
    "name": "Ana",
    "lastname": "Garcia",
    "email": "ana@giamma.com",
    "password": "ana12345",
    "role": "VENDEDORA",
    "isActive": true
  }'
```
**Esperado:** 201/200 OK

**VENDEDORA - debe fallar:**
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -d '{...}'
```
**Esperado:** 403 Forbidden

### 2.4 DELETE /users/{id} (soft delete)
**GERENTE:**
```bash
curl -X DELETE http://localhost:8080/users/3 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```
**Esperado:** 204 No Content. El usuario debe desaparecer de GET /users.

**VENDEDORA - debe fallar:**
```bash
curl -X DELETE http://localhost:8080/users/3 \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```
**Esperado:** 403 Forbidden

---

## 3. Leads (todos los roles)

### 3.1 GET /api/leads
```bash
curl -X GET "http://localhost:8080/api/leads?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```
**Esperado:** 200 OK

### 3.2 POST /api/leads
```bash
curl -X POST http://localhost:8080/api/leads \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -d '{
    "name": "Cliente Test",
    "email": "cliente@test.com",
    "phone": "1112223333",
    "source": "WEB",
    "status": "NEW"
  }'
```
**Esperado:** 200 OK

### 3.3 GET /api/leads/{id}
```bash
curl -X GET http://localhost:8080/api/leads/1 \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 3.4 GET /api/leads/my-leads
```bash
curl -X GET "http://localhost:8080/api/leads/my-leads?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 3.5 GET /api/leads/unassigned
```bash
curl -X GET "http://localhost:8080/api/leads/unassigned?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 3.6 GET /api/leads/search?query=Juan
```bash
curl -X GET "http://localhost:8080/api/leads/search?query=Juan&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 3.7 PUT /api/leads/{id}/status
```bash
curl -X PUT http://localhost:8080/api/leads/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -d '"CONTACTED"'
```

### 3.8 PUT /api/leads/{id}/assign/{userId}
```bash
curl -X PUT http://localhost:8080/api/leads/1/assign/2 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### 3.9 DELETE /api/leads/{id}
```bash
curl -X DELETE http://localhost:8080/api/leads/1 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### 3.10 GET /api/leads/stats/by-status
```bash
curl -X GET http://localhost:8080/api/leads/stats/by-status \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

---

## 4. Vehicles

### 4.1 GET /api/vehicles (todos)
```bash
curl -X GET "http://localhost:8080/api/vehicles?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```
**Esperado:** 200 OK

### 4.2 POST /api/vehicles (solo GERENTE/SUPERVISOR/ADMIN_SISTEMA)
```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d '{
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2024,
    "price": 25000000,
    "vin": "1HGBH41JXMN109186",
    "status": "AVAILABLE"
  }'
```
**Esperado GERENTE:** 200 OK

**VENDEDORA - debe fallar:**
```bash
curl -X POST http://localhost:8080/api/vehicles ... \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```
**Esperado:** 403 Forbidden

### 4.3 PUT /api/vehicles/{id}
```bash
curl -X PUT http://localhost:8080/api/vehicles/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d '{
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2024,
    "price": 26000000,
    "vin": "1HGBH41JXMN109186",
    "status": "AVAILABLE"
  }'
```

### 4.4 DELETE /api/vehicles/{id}
```bash
curl -X DELETE http://localhost:8080/api/vehicles/1 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### 4.5 PUT /api/vehicles/{id}/reserve
```bash
curl -X PUT http://localhost:8080/api/vehicles/1/reserve \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 4.6 PUT /api/vehicles/{id}/sell
```bash
curl -X PUT http://localhost:8080/api/vehicles/1/sell \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### 4.7 GET /api/vehicles/available
```bash
curl -X GET "http://localhost:8080/api/vehicles/available?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 4.8 GET /api/vehicles/search?model=Corolla
```bash
curl -X GET "http://localhost:8080/api/vehicles/search?model=Corolla&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

---

## 5. Quotations (todos los roles)

### 5.1 GET /api/quotations
```bash
curl -X GET "http://localhost:8080/api/quotations?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_PLANES"
```

### 5.2 POST /api/quotations
```bash
curl -X POST http://localhost:8080/api/quotations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_PLANES" \
  -d '{
    "leadId": 1,
    "vehicleId": 1,
    "type": "CASH",
    "amount": 25000000,
    "status": "DRAFT"
  }'
```

### 5.3 PUT /api/quotations/{id}
```bash
curl -X PUT http://localhost:8080/api/quotations/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_PLANES" \
  -d '{...}'
```

### 5.4 POST /api/quotations/{id}/send
```bash
curl -X POST http://localhost:8080/api/quotations/1/send \
  -H "Authorization: Bearer $TOKEN_PLANES"
```

### 5.5 DELETE /api/quotations/{id}
```bash
curl -X DELETE http://localhost:8080/api/quotations/1 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### 5.6 GET /api/quotations/stats/by-type
```bash
curl -X GET http://localhost:8080/api/quotations/stats/by-type \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 5.7 GET /api/quotations/valid
```bash
curl -X GET "http://localhost:8080/api/quotations/valid?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

---

## 6. Test Drives (todos los roles)

### 6.1 GET /api/test-drives
```bash
curl -X GET http://localhost:8080/api/test-drives \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 6.2 POST /api/test-drives
```bash
curl -X POST http://localhost:8080/api/test-drives \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -d '{
    "leadId": 1,
    "vehicleId": 1,
    "scheduledAt": "2026-06-15T10:00:00",
    "status": "SCHEDULED"
  }'
```

### 6.3 PUT /api/test-drives/{id}/confirm
```bash
curl -X PUT http://localhost:8080/api/test-drives/1/confirm \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 6.4 PUT /api/test-drives/{id}/complete
```bash
curl -X PUT http://localhost:8080/api/test-drives/1/complete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -d '"Test drive exitoso, cliente interesado"'
```

### 6.5 PUT /api/test-drives/{id}/cancel
```bash
curl -X PUT http://localhost:8080/api/test-drives/1/cancel \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 6.6 GET /api/test-drives/calendar
```bash
curl -X GET "http://localhost:8080/api/test-drives/calendar?start=2026-06-01T00:00:00&end=2026-06-30T23:59:59" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

---

## 7. Documents (todos los roles)

### 7.1 GET /api/documents/lead/{leadId}
```bash
curl -X GET http://localhost:8080/api/documents/lead/1 \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 7.2 POST /api/documents (upload)
```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -F "document={\"leadId\":1,\"type\":\"DNI\"};type=application/json" \
  -F "file=@/ruta/a/documento.pdf"
```

### 7.3 GET /api/documents/{id}/download
```bash
curl -X GET http://localhost:8080/api/documents/1/download \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  --output documento.pdf
```

### 7.4 PUT /api/documents/{id}/verify
```bash
curl -X PUT http://localhost:8080/api/documents/1/verify \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### 7.5 DELETE /api/documents/{id}
```bash
curl -X DELETE http://localhost:8080/api/documents/1 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

## 8. Activities (todos los roles)

### 8.1 GET /api/activities/lead/{leadId}
```bash
curl -X GET http://localhost:8080/api/activities/lead/1 \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 8.2 POST /api/activities
```bash
curl -X POST http://localhost:8080/api/activities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -d '{
    "leadId": 1,
    "type": "CALL",
    "description": "Llamada de seguimiento",
    "notes": "Cliente interesado en financiacion"
  }'
```

### 8.3 GET /api/activities/my-activities
```bash
curl -X GET http://localhost:8080/api/activities/my-activities \
  -H "Authorization: Bearer $TOKEN_VENDEDORA"
```

### 8.4 GET /api/activities/stats
```bash
curl -X GET "http://localhost:8080/api/activities/stats?start=2026-06-01T00:00:00&end=2026-06-30T23:59:59" \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

## 9. Excel Upload (solo GERENTE/SUPERVISOR/ADMIN_SISTEMA)

### 9.1 GET /api/excel/template (todos? - verificar)
```bash
curl -X GET http://localhost:8080/api/excel/template \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  --output template.xlsx
```

### 9.2 POST /api/excel/upload
```bash
curl -X POST http://localhost:8080/api/excel/upload \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -F "file=@/ruta/a/leads.xlsx"
```
**VENDEDORA - debe fallar con 403:**
```bash
curl -X POST http://localhost:8080/api/excel/upload \
  -H "Authorization: Bearer $TOKEN_VENDEDORA" \
  -F "file=@/ruta/a/leads.xlsx"
```

### 9.3 GET /api/excel/uploads
```bash
curl -X GET http://localhost:8080/api/excel/uploads \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

## 10. Matriz de Permisos Resumida

| Endpoint                         | GERENTE | SUPERVISOR | VENDEDORA | PLANES | ADMIN_SISTEMA |
|----------------------------------|---------|------------|-----------|--------|---------------|
| GET /users/**                    |   OK    |     OK     |    OK     |   OK   |      OK       |
| POST/PUT/DELETE /users/**        |   OK    |     OK     |   403     |  403   |      OK       |
| /api/leads/**                    |   OK    |     OK     |    OK     |   OK   |      OK       |
| GET /api/vehicles/**             |   OK    |     OK     |    OK     |   OK   |      OK       |
| POST/PUT/DELETE /api/vehicles/** |   OK    |     OK     |   403     |  403   |      OK       |
| /api/quotations/**               |   OK    |     OK     |    OK     |   OK   |      OK       |
| /api/test-drives/**              |   OK    |     OK     |    OK     |   OK   |      OK       |
| /api/documents/**                |   OK    |     OK     |    OK     |   OK   |      OK       |
| /api/activities/**               |   OK    |     OK     |    OK     |   OK   |      OK       |
| /api/excel/**                    |   OK    |     OK     |   403     |  403   |      OK       |
| /api/stats/**                    |   OK    |     OK     |    OK     |   OK   |      OK       |

---

## 11. Flujo End-to-End Recomendado

1. **Onboarding** -> Crear tenant y admin (GERENTE)
2. **Login** como GERENTE -> Obtener token
3. **POST /auth/register** -> Crear VENDEDORA, SUPERVISOR, PLANES
4. **Login** como cada rol -> Obtener tokens
5. **POST /api/leads** -> Crear lead (VENDEDORA)
6. **GET /api/leads** -> Ver leads (cualquiera)
7. **PUT /api/leads/{id}/assign** -> Asignar lead (GERENTE)
8. **POST /api/vehicles** -> Crear vehiculo (GERENTE)
9. **POST /api/quotations** -> Crear cotizacion (PLANES)
10. **POST /api/test-drives** -> Crear test drive (VENDEDORA)
11. **POST /api/activities** -> Loguear actividad (VENDEDORA)
12. **POST /api/documents** -> Subir documento (VENDEDORA)
13. **GET /api/activities/lead/{id}** -> Ver timeline (SUPERVISOR)
14. **DELETE /users/{id}** -> Soft delete de un usuario (GERENTE)
15. **GET /users** -> Verificar que el usuario borrado no aparece
16. **Login** con usuario borrado -> Verificar 401
