#!/bin/bash

set -e

BASE_URL="http://localhost:8080"

echo "🚀 Seed de datos para Concessio CRM (local)"
echo "============================================"
echo ""

# 1. Onboarding - Crear tenant + admin
echo "1. Creando tenant y usuario admin..."
ADMIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/onboarding" \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Giamma Demo",
    "adminName": "Gerente",
    "adminLastname": "Demo",
    "adminEmail": "admin@giamma.com",
    "password": "admin123"
  }')

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "   ✅ Tenant creado (code: DEM)"
echo "   ✅ Admin: admin@giamma.com / admin123"
echo "   📝 Token admin: ${ADMIN_TOKEN:0:50}..."
echo ""

# 2. Login con el admin para confirmar
echo "2. Probando login del admin..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "tenantCode": "DEM",
    "email": "admin@giamma.com",
    "password": "admin123"
  }')

echo "   ✅ Login exitoso"
echo ""

# 3. Crear vendedora
echo "3. Creando usuario vendedora..."
curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "name": "María",
    "lastname": "González",
    "email": "maria@giamma.com",
    "password": "vendedora123",
    "role": "VENDEDORA",
    "isActive": true
  }' > /dev/null

echo "   ✅ Vendedora: maria@giamma.com / vendedora123"
echo ""

# 4. Crear supervisor
echo "4. Creando usuario supervisor..."
curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "name": "Carlos",
    "lastname": "Rodríguez",
    "email": "carlos@giamma.com",
    "password": "supervisor123",
    "role": "SUPERVISOR",
    "isActive": true
  }' > /dev/null

echo "   ✅ Supervisor: carlos@giamma.com / supervisor123"
echo ""

# 5. Crear usuario de planes
echo "5. Creando usuario planes..."
curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "name": "Lucía",
    "lastname": "Fernández",
    "email": "lucia@giamma.com",
    "password": "planes123",
    "role": "PLANES",
    "isActive": true
  }' > /dev/null

echo "   ✅ Planes: lucia@giamma.com / planes123"
echo ""

echo "============================================"
echo "✅ Seed completado!"
echo ""
echo "📋 Credenciales para testing manual:"
echo ""
echo "  Tenant Code: DEM"
echo ""
echo "  ┌─────────────────────────────────────────┐"
echo "  │  Rol        │  Email              │  Pass         │"
echo "  ├─────────────────────────────────────────┤"
echo "  │  GERENTE    │  admin@giamma.com   │  admin123     │"
echo "  │  SUPERVISOR │  carlos@giamma.com  │  supervisor123│"
echo "  │  VENDEDORA  │  maria@giamma.com   │  vendedora123 │"
echo "  │  PLANES     │  lucia@giamma.com   │  planes123    │"
echo "  └─────────────────────────────────────────┘"
echo ""
echo "🧪 Para testear desde terminal:"
echo ""
echo "  curl -X POST http://localhost:8080/auth/login \\"
echo "    -H 'Content-Type: application/json' \\"
echo "    -d '{\"tenantCode\":\"DEM\",\"email\":\"admin@giamma.com\",\"password\":\"admin123\"}' \\"
echo "    | jq -r .token"
echo ""
