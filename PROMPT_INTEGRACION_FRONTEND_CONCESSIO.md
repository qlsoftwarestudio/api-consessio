# Prompt para integrar el frontend con la API Concessio

Usá este prompt en el proyecto frontend para revisar el código existente e implementar la integración completa con el backend Concessio.

```text
Actuá como senior frontend engineer. Necesito que revises el código existente del frontend y lo integres con el backend Concessio usando la documentación de API disponible.

Contexto de backend:
- Backend Spring Boot desplegable en Railway.
- Base URL de producción esperada: https://api.consessio.com o la URL temporal de Railway.
- Frontend productivo permitido por CORS:
  - https://consessio-front.vercel.app
  - https://www.consessio.com
- Autenticación JWT vía header Authorization: Bearer <token>.
- Login multi-tenant: el endpoint de login requiere tenantCode + email + password.
- Los documentos se descargan vía backend autenticado, no como URLs públicas.

Objetivos:
1. Analizar la estructura actual del frontend.
2. Identificar dónde se maneja autenticación, llamadas HTTP, rutas protegidas, formularios y descarga de archivos.
3. Crear o ajustar un cliente API centralizado.
4. Implementar login con tenantCode, email y password.
5. Guardar el JWT de forma consistente con la arquitectura existente.
6. Agregar el header Authorization en todas las requests protegidas.
7. Manejar 401/403 cerrando sesión o redirigiendo al login.
8. Integrar endpoints principales según la documentación del backend:
   - /auth/login
   - /auth/onboarding
   - /api/leads/**
   - /api/vehicles/**
   - /api/quotations/**
   - /api/test-drives/**
   - /api/documents/**
   - /api/activities/**
   - /api/stats/**
9. Implementar descarga autenticada de documentos usando blob/arrayBuffer y respetando Content-Disposition.
10. Configurar la variable de entorno del frontend para la API:
    - VITE_API_URL=https://api.consessio.com si usa Vite
    - NEXT_PUBLIC_API_URL=https://api.consessio.com si usa Next.js
11. Revisar paginación si la API devuelve Page<T> de Spring.
12. Mantener el estilo visual y arquitectura actual del proyecto.
13. No hardcodear tokens, claves ni URLs de producción en componentes.
14. Agregar manejo de errores claro para el usuario.
15. Proponer una checklist final de pruebas manuales.

Contrato de autenticación esperado:
POST /auth/login
Body:
{
  "tenantCode": "BEL",
  "email": "admin@consessio.com",
  "password": "password"
}

Respuesta esperada:
{
  "token": "jwt...",
  "message": "Login successful"
}

Descarga de documentos:
GET /api/documents/{id}/download
Headers:
Authorization: Bearer <token>

Implementación sugerida:
- Hacer fetch/axios con responseType blob.
- Leer Content-Disposition para obtener filename.
- Crear URL temporal con URL.createObjectURL(blob).
- Disparar descarga con un anchor temporal.
- Revocar la URL temporal.

Documentación backend disponible en el repo:
- API-COMPLETA.md
- API-DOC.md
- FRONTEND-PAGINATION-GUIDE.md
- TELEGRAM_NOTIFICATIONS.md si aplica

Antes de implementar:
- Mostrame un resumen de archivos relevantes encontrados.
- Indicame qué variables de entorno usa el frontend actualmente.
- Confirmá si el proyecto usa Vite, Next.js u otro framework.

Después de implementar:
- Ejecutá build/lint/test si existen scripts.
- Mostrame un resumen de cambios.
- Listá endpoints integrados y pendientes.
- Listá pruebas manuales recomendadas.
```

## Checklist manual esperada

- Login con `tenantCode`, email y password.
- Redirección correcta luego del login.
- Persistencia de sesión al refrescar.
- Logout limpia token.
- Requests protegidas envían `Authorization`.
- 401/403 se manejan correctamente.
- Listado de leads carga.
- Creación/edición de entidades funciona.
- Upload de documentos funciona.
- Descarga de documentos funciona con archivo real.
- Error de documento inexistente se muestra de forma amigable.
- Frontend apunta a `https://api.consessio.com` en producción.
