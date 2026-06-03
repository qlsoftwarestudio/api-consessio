#!/usr/bin/env bash
# Test rapido de los endpoints del Copiloto Comercial
set -euo pipefail

BASE_URL="http://localhost:8080"
TS=$(date +%s)
RAND=$(shuf -i 1000-9999 -n 1)
BUSINESS="Copilot Test ${RAND}"
TENANT_CODE="${RAND}"
ADMIN_EMAIL="copilot-admin-${TS}@giamma.com"

green() { echo -e "\033[0;32m$1\033[0m"; }
red() { echo -e "\033[0;31m$1\033[0m"; }

echo "=== TEST COPILOTO ==="

# 1. Onboarding
TOKEN=$(curl -s -X POST "$BASE_URL/auth/onboarding" -H "Content-Type: application/json" \
  -d "{\"businessName\":\"$BUSINESS\",\"adminName\":\"Ger\",\"adminLastname\":\"Test\",\"adminEmail\":\"$ADMIN_EMAIL\",\"password\":\"admin123\"}" \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
[ -n "$TOKEN" ] && green "Onboarding OK" || { red "Onboarding FALLO"; exit 1; }

AUTH="Authorization: Bearer $TOKEN"
CT="Content-Type: application/json"

# 2. Crear leads en distintos estados
create_lead() {
  curl -s -X POST "$BASE_URL/api/leads" -H "$AUTH" -H "$CT" \
    -d "{\"firstName\":\"$1\",\"lastName\":\"Test\",\"phone\":\"$2\",\"source\":\"WEB\",\"vehicleInterest\":\"Cronos Drive\",\"status\":\"$3\"}" \
    | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2
}

L1=$(create_lead "Juan" "111" "NEGOCIACION")
L2=$(create_lead "Maria" "222" "TEST_DRIVE_COMPLETADO")
L3=$(create_lead "Pedro" "333" "NUEVO")
L4=$(create_lead "Ana" "444" "COTIZADO")
green "Leads creados: $L1 $L2 $L3 $L4"

# 3. Daily summary
echo ""; echo "--- /daily-summary ---"
curl -s "$BASE_URL/api/copilot/daily-summary" -H "$AUTH"; echo ""

# 4. Hot leads
echo ""; echo "--- /hot-leads ---"
curl -s "$BASE_URL/api/copilot/hot-leads" -H "$AUTH"; echo ""

# 5. Abandoned leads (days=0 para incluir todos)
echo ""; echo "--- /abandoned-leads?days=0 ---"
curl -s "$BASE_URL/api/copilot/abandoned-leads?days=0" -H "$AUTH"; echo ""

# 6. Next actions
echo ""; echo "--- /next-actions ---"
curl -s "$BASE_URL/api/copilot/next-actions" -H "$AUTH"; echo ""

# 7. Ranking
echo ""; echo "--- /ranking ---"
curl -s "$BASE_URL/api/copilot/ranking" -H "$AUTH"; echo ""

green "=== TEST COPILOTO COMPLETADO ==="
