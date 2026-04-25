#!/bin/bash

set -e

echo "🔌 Creando network si no existe..."
docker network create concessio-network >/dev/null 2>&1 || true

echo "🗄️ Levantando PostgreSQL para Concessio..."
docker rm -f concessio-postgres >/dev/null 2>&1 || true

docker run -d \
  --name concessio-postgres \
  --network concessio-network \
  -e POSTGRES_DB=concessio \
  -e POSTGRES_USER=concessio \
  -e POSTGRES_PASSWORD=concessio123 \
  -p 5433:5432 \
  -v concessio-postgres-data:/var/lib/postgresql/data \
  postgres:15-alpine

echo "⏳ Esperando a que Postgres inicie..."
sleep 10

echo "🏗️ Build de Concessio App..."
docker build -t concessio-app .

echo "🚀 Levantando Concessio..."
docker rm -f concessio-app >/dev/null 2>&1 || true

docker run -d \
  --name concessio-app \
  --network concessio-network \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://concessio-postgres:5432/concessio \
  -e SPRING_DATASOURCE_USERNAME=concessio \
  -e SPRING_DATASOURCE_PASSWORD=concessio123 \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -e JWT_SECRET=concessio-super-secret-key-for-jwt-tokens-2026 \
  -e JWT_EXPIRATION=86400000 \
  concessio-app

echo "✅ Concessio corriendo en http://localhost:8080"
echo "📊 PostgreSQL disponible en puerto 5433 (mapeado desde 5432)"
echo ""
echo "Endpoints disponibles:"
echo "  POST /auth/onboarding"
echo "  POST /auth/login"
echo "  GET  /api/excel/template"
echo "  GET  /api/leads"
