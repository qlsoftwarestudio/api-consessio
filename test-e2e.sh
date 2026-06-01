#!/bin/bash

BASE_URL="http://localhost:8080"
TIMESTAMP=$(date +%s)

echo ""
echo "========================================"
echo "  TEST E2E - Concessio CRM"
echo "========================================"
echo ""

# ========== COLORES ==========
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

success() { echo -e "${GREEN}✓${NC} $1" >&2; }
error()   { echo -e "${RED}✗${NC} $1" >&2; }
info()    { echo -e "${YELLOW}→${NC} $1" >&2; }

json_value() {
    python3 -c "import sys,json; print(json.loads(sys.stdin.read()).get('$2',''))" <<< "$1"
}

# Variables unicas por ejecucion
RAND_CODE=$(shuf -i 100-999 -n 1)
BUSINESS_NAME="Giamma Test ${RAND_CODE}"
ADMIN_EMAIL="e2e-admin-${TIMESTAMP}@giamma.com"
VENDEDORA_EMAIL="e2e-vendedora-${TIMESTAMP}@giamma.com"
SUPERVISOR_EMAIL="e2e-supervisor-${TIMESTAMP}@giamma.com"
PLANES_EMAIL="e2e-planes-${TIMESTAMP}@giamma.com"
TEMP_EMAIL="e2e-temp-${TIMESTAMP}@giamma.com"

# Tenant code: backend lo genera de la ultima palabra del business name
TENANT_CODE=$(printf '%03d' "$RAND_CODE")
info "Tenant code esperado: $TENANT_CODE"

# ========== 1. ONBOARDING ==========
info "1. Creando tenant y admin via onboarding..."

ADMIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/onboarding" \
  -H "Content-Type: application/json" \
  -d "{\"businessName\":\"$BUSINESS_NAME\",\"adminName\":\"Gerente\",\"adminLastname\":\"Test\",\"adminEmail\":\"$ADMIN_EMAIL\",\"password\":\"admin123\"}")

HTTP_CODE=$(echo "$ADMIN_RESPONSE" | tail -n1)
BODY=$(echo "$ADMIN_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
    success "Onboarding OK (HTTP $HTTP_CODE)"
    ADMIN_TOKEN=$(json_value "$BODY" token)
    echo "   Token: ${ADMIN_TOKEN:0:50}..." >&2
else
    error "Onboarding falló (HTTP $HTTP_CODE)"
    echo "$BODY" >&2
    exit 1
fi

# ========== 2. LOGIN ADMIN ==========
info "2. Login como admin..."

LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"tenantCode\":\"$TENANT_CODE\",\"email\":\"$ADMIN_EMAIL\",\"password\":\"admin123\"}")

HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)
LOGIN_BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')
if [ "$HTTP_CODE" = "200" ]; then
    success "Login admin OK"
    ADMIN_TOKEN=$(json_value "$LOGIN_BODY" token)
else
    error "Login admin falló (HTTP $HTTP_CODE)"
    echo "   Body: $LOGIN_BODY" >&2
    exit 1
fi

# ========== 3. CREAR USUARIOS ==========
info "3. Creando usuarios de distintos roles..."

create_user() {
    local name=$1
    local email=$2
    local password=$3
    local role=$4

    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -d "{\"name\":\"$name\",\"lastname\":\"Test\",\"email\":\"$email\",\"password\":\"$password\",\"role\":\"$role\",\"isActive\":true}")

    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    if [ "$HTTP_CODE" = "200" ]; then
        success "Usuario $role creado: $email"
        json_value "$(echo "$RESPONSE" | sed '$d')" token
    else
        error "Error creando $role (HTTP $HTTP_CODE)"
        echo "$RESPONSE" | sed '$d' >&2
        return 1
    fi
}

VENDEDORA_TOKEN=$(create_user "Maria" "$VENDEDORA_EMAIL" "vendedora123" "VENDEDORA")
SUPERVISOR_TOKEN=$(create_user "Carlos" "$SUPERVISOR_EMAIL" "supervisor123" "SUPERVISOR")
PLANES_TOKEN=$(create_user "Lucia" "$PLANES_EMAIL" "planes123" "PLANES")

# ========== 4. GET /users/me ==========
info "4. Probando GET /users/me con cada rol..."

for TOKEN in "$ADMIN_TOKEN" "$VENDEDORA_TOKEN" "$SUPERVISOR_TOKEN" "$PLANES_TOKEN"; do
    ROLE=$(curl -s -X GET "$BASE_URL/users/me" -H "Authorization: Bearer $TOKEN" | grep -o '"role":"[^"]*"' | cut -d'"' -f4)
    if [ -n "$ROLE" ]; then
        success "/users/me OK para rol: $ROLE"
    else
        error "/users/me falló para un token"
    fi
done

# ========== 5. CREAR LEAD (VENDEDORA) ==========
info "5. Creando lead como VENDEDORA..."

LEAD_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/leads" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $VENDEDORA_TOKEN" \
  -d '{
    "firstName": "Juan",
    "lastName": "Perez",
    "email": "juan@cliente.com",
    "phone": "1112223333",
    "source": "WEB"
  }')

HTTP_CODE=$(echo "$LEAD_RESPONSE" | tail -n1)
BODY=$(echo "$LEAD_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
    LEAD_ID=$(json_value "$BODY" id)
    success "Lead creado con ID: $LEAD_ID"
else
    error "Error creando lead (HTTP $HTTP_CODE)"
    echo "$BODY" >&2
    exit 1
fi

# ========== 6. LISTAR LEADS ==========
info "6. Listando leads..."

LEADS=$(curl -s -X GET "$BASE_URL/api/leads?page=0&size=10" \
  -H "Authorization: Bearer $VENDEDORA_TOKEN")

if echo "$LEADS" | grep -q "content"; then
    success "Listado de leads OK"
else
    error "Listado de leads falló"
fi

# ========== 7. CREAR VEHICULO (ADMIN) ==========
info "7. Creando vehiculo como GERENTE..."

VEHICLE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/vehicles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{\"vin\":\"VIN${TIMESTAMP}123\",\"model\":\"Toyota Corolla\",\"color\":\"Rojo\",\"year\":2024,\"priceList\":25000000}")

HTTP_CODE=$(echo "$VEHICLE_RESPONSE" | tail -n1)
BODY=$(echo "$VEHICLE_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
    VEHICLE_ID=$(json_value "$BODY" id)
    success "Vehiculo creado con ID: $VEHICLE_ID"
else
    error "Error creando vehiculo (HTTP $HTTP_CODE)"
    echo "$BODY" >&2
    exit 1
fi

# ========== 8. VENDEDORA INTENTA CREAR VEHICULO (403) ==========
info "8. Verificando que VENDEDORA no puede crear vehiculo..."

FORBIDDEN=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/vehicles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $VENDEDORA_TOKEN" \
  -d "{\"vin\":\"FORBIDDEN${TIMESTAMP}\",\"model\":\"Ford Focus\",\"year\":2023,\"priceList\":20000000}")

if [ "$FORBIDDEN" = "403" ]; then
    success "403 Forbidden correcto para VENDEDORA creando vehiculo"
else
    error "Esperado 403, recibido $FORBIDDEN"
fi

# ========== 9. CREAR COTIZACION (PLANES) ==========
info "9. Creando cotizacion como PLANES..."

QUOTE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/quotations" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PLANES_TOKEN" \
  -d "{\"lead\":{\"id\":$LEAD_ID},\"vehicleModel\":\"Toyota Corolla\",\"type\":\"CONTADO\",\"priceList\":25000000,\"notes\":\"Cotizacion de prueba\"}")

HTTP_CODE=$(echo "$QUOTE_RESPONSE" | tail -n1)
BODY=$(echo "$QUOTE_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
    QUOTE_ID=$(json_value "$BODY" id)
    success "Cotizacion creada con ID: $QUOTE_ID"
else
    error "Error creando cotizacion (HTTP $HTTP_CODE)"
    echo "$BODY" >&2
fi

# ========== 10. CREAR TEST DRIVE (VENDEDORA) ==========
info "10. Creando test drive como VENDEDORA..."

TD_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/test-drives" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $VENDEDORA_TOKEN" \
  -d "{\"lead\":{\"id\":$LEAD_ID},\"vehicle\":{\"id\":$VEHICLE_ID},\"scheduledAt\":\"2026-06-15T10:00:00\",\"vehicleModel\":\"Toyota Corolla\",\"location\":\"Sucursal Test\"}")

HTTP_CODE=$(echo "$TD_RESPONSE" | tail -n1)
BODY=$(echo "$TD_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
    TD_ID=$(json_value "$BODY" id)
    success "Test drive creado con ID: $TD_ID"
else
    error "Error creando test drive (HTTP $HTTP_CODE)"
    echo "$BODY" >&2
fi

# ========== 11. CREAR ACTIVIDAD (VENDEDORA) ==========
info "11. Creando actividad como VENDEDORA..."

ACT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/activities" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $VENDEDORA_TOKEN" \
  -d "{\"lead\":{\"id\":$LEAD_ID},\"type\":\"LLAMADA\",\"description\":\"Llamada de seguimiento\",\"metadata\":\"{\\\"resultado\\\": \\\"interesado\\\"}\"}")

HTTP_CODE=$(echo "$ACT_RESPONSE" | tail -n1)
if [ "$HTTP_CODE" = "200" ]; then
    success "Actividad creada OK"
else
    error "Error creando actividad (HTTP $HTTP_CODE)"
    echo "$ACT_RESPONSE" | sed '$d' >&2
fi

# ========== 12. ASIGNAR LEAD (ADMIN) ==========
info "12. Asignando lead a VENDEDORA como GERENTE..."

# Primero obtener el ID de la vendedora
VENDEDORA_ID=$(json_value "$(curl -s -X GET "$BASE_URL/users/me" -H "Authorization: Bearer $VENDEDORA_TOKEN")" id)

ASSIGN_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/api/leads/$LEAD_ID/assign/$VENDEDORA_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

ASSIGN_CODE=$(echo "$ASSIGN_RESPONSE" | tail -n1)
if [ "$ASSIGN_CODE" = "200" ]; then
    success "Lead asignado a VENDEDORA OK"
else
    error "Error asignando lead (HTTP $ASSIGN_CODE)"
fi

# ========== 13. SOFT DELETE DE USUARIO ==========
info "13. Probando soft delete de usuario..."

# Crear un usuario temporal para borrar
TEMP_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{\"name\":\"Temp\",\"lastname\":\"User\",\"email\":\"$TEMP_EMAIL\",\"password\":\"temp123\",\"role\":\"VENDEDORA\",\"isActive\":true}")

TEMP_ID=$(json_value "$(echo "$TEMP_RESPONSE" | sed '$d')" id)
info "Usuario temporal creado con ID: $TEMP_ID"

# Borrar (soft delete)
DELETE_RESPONSE=$(curl -s -w "%{http_code}" -X DELETE "$BASE_URL/users/$TEMP_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

if [ "$DELETE_RESPONSE" = "204" ] || [ "$DELETE_RESPONSE" = "200" ]; then
    success "Soft delete OK (HTTP $DELETE_RESPONSE)"
else
    error "Soft delete falló (HTTP $DELETE_RESPONSE)"
fi

# Verificar que no aparece en el listado
USERS_LIST=$(curl -s -X GET "$BASE_URL/users?page=0&size=100" -H "Authorization: Bearer $ADMIN_TOKEN")
if echo "$USERS_LIST" | grep -q "$TEMP_EMAIL"; then
    error "Usuario borrado sigue apareciendo en el listado!"
else
    success "Usuario borrado correctamente oculto del listado"
fi

# Intentar login con usuario borrado
LOGIN_DELETED=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"tenantCode\":\"$TENANT_CODE\",\"email\":\"$TEMP_EMAIL\",\"password\":\"temp123\"}")

LOGIN_DEL_CODE=$(echo "$LOGIN_DELETED" | tail -n1)
if [ "$LOGIN_DEL_CODE" = "401" ]; then
    success "Login de usuario borrado rechazado correctamente (401)"
else
    error "Esperado 401 para login de usuario borrado, recibido $LOGIN_DEL_CODE"
fi

# ========== 14. STATS ==========
info "14. Verificando endpoints de stats..."

LEAD_STATS=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/leads/stats/by-status" -H "Authorization: Bearer $ADMIN_TOKEN")
if echo "$LEAD_STATS" | grep -q "200$"; then
    success "Lead stats OK"
else
    error "Lead stats falló"
fi

QUOTE_STATS=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/quotations/stats/by-type" -H "Authorization: Bearer $PLANES_TOKEN")
if echo "$QUOTE_STATS" | grep -q "200$"; then
    success "Quotation stats OK"
else
    error "Quotation stats falló"
fi

# ========== 15. EXCEL UPLOAD (ADMIN solo) ==========
info "15. Verificando permisos de Excel upload..."

EXCEL_403=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/excel/template" -H "Authorization: Bearer $VENDEDORA_TOKEN")
if [ "$EXCEL_403" = "403" ]; then
    success "Excel template bloqueado para VENDEDORA (403)"
else
    error "Esperado 403 para Excel, recibido $EXCEL_403"
fi

EXCEL_OK=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/excel/template" -H "Authorization: Bearer $ADMIN_TOKEN" -o /dev/null)
if [ "$EXCEL_OK" = "200" ]; then
    success "Excel template accesible para GERENTE (200)"
else
    error "Excel template falló para GERENTE (HTTP $EXCEL_OK)"
fi

echo ""
echo "========================================"
echo "  TEST E2E COMPLETADO"
echo "========================================"
echo ""
