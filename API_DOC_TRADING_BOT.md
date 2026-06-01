# 📚 API Documentation - QL Trading Bot

**Versión:** 1.0  
**Base URL:** `https://api.qltradingbot.com/api/v1`  
**WebSocket:** `wss://api.qltradingbot.com/ws`  
**Formato:** JSON  
**Autenticación:** JWT Bearer Token + Header `X-Tenant-ID`  

---

## 📋 ÍNDICE

1. [Arquitectura](#-arquitectura)
2. [Autenticación](#-autenticación)
3. [Usuarios y Bots](#-usuarios-y-bots)
4. [Portfolio](#-portfolio)
5. [Trades](#-trades)
6. [Market Data](#-market-data)
7. [Configuración](#-configuración)
8. [Estrategias](#-estrategias)
9. [Notificaciones](#-notificaciones)
10. [Admin](#-admin)
11. [WebSocket](#-websocket)
12. [Errores](#-errores)

---

## 🏗️ ARQUITECTURA

### Modelo Tenant → Usuario → Bot

```
TENANT (Cuenta que paga la suscripción)
├── ID: 42
├── Plan: ENTERPRISE ($199/mes)
├── Status: ACTIVE
│
├── USUARIO 1 (Admin del tenant)
│   ├── ID: 101
│   ├── Email: admin@empresa.com
│   ├── API Keys Binance: [encriptadas]
│   ├── Bot Status: ACTIVE
│   └── Capital: $5,000 USDT
│
├── USUARIO 2 (Trader junior)
│   ├── ID: 102
│   ├── Email: junior@empresa.com
│   ├── API Keys Binance: [encriptadas]
│   ├── Bot Status: PAUSED
│   └── Capital: $1,000 USDT
│
└── USUARIO 3 (Invitado)
    ├── ID: 103
    ├── Email: guest@empresa.com
    ├── API Keys Binance: [encriptadas]
    ├── Bot Status: ACTIVE
    └── Capital: $2,000 USDT
```

### Planes y Límites

| Plan | Máx Usuarios (Bots) | Máx Pares de Trading | Modo de Pares | Capital por Usuario | Capital Total Tenant |
|------|---------------------|---------------------|---------------|---------------------|---------------------|
| **STARTER** | 1 | **1** | FIXED (1 par fijo) | $500 | $500 |
| **PRO** | 1 | **2** | AUTO_BEST (elige el mejor) | $10,000 | $10,000 |
| **ENTERPRISE** | 3 | **3** | MULTI_PARALLEL (todos juntos) | Ilimitado | Ilimitado |

**Cada usuario = 1 Bot independiente con sus propias API Keys**  
**Multi-Pair:** Un bot puede operar hasta N pares según el plan (distribución de capital por pesos)

### Multi-Pair Trading (Feature Diferenciador)

```
┌─────────────────────────────────────────────────────────────────┐
│                    BOT CON MULTI-PAIR (PRO/Enterprise)        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Pares Configurados: BTCUSDT (40%) | ETHUSDT (35%) | SOL (25%)│
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐│
│  │  BTCUSDT        │  │  ETHUSDT        │  │  SOLUSDT       ││
│  │  ADX: 32 ↑      │  │  ADX: 18 →      │  │  ADX: 12 →     ││
│  │  Cond: TREND_UP │  │  Cond: RANGING  │  │  Cond: NEUTRAL ││
│  │                 │  │                 │  │                ││
│  │  Modo AUTO:     │  │                 │  │  Inactivo      ││
│  │  ✅ OPERANDO    │  │  ⏸️ Standby     │  │  ⏸️ Standby    ││
│  │                 │  │                 │  │                ││
│  │  Capital: $400  │  │  Capital: $350  │  │  Capital: $250 ││
│  │  Trade Open: 1  │  │  Trade Open: 0  │  │  Trade Open: 0 ││
│  └─────────────────┘  └─────────────────┘  └────────────────┘│
│                                                                 │
│  Resultado: Siempre hay 1 par operando el mejor mercado        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Modos de Operación:**

1. **FIXED** (Starter): Opera 1 par fijo. Si BTC está lateral → bot espera.
2. **AUTO_BEST** (Pro): Escanea 2 pares, opera el que tenga mejor ADX/condición. Siempre hay movimiento.
3. **MULTI_PARALLEL** (Enterprise): Opera 3 pares al mismo tiempo con capital distribuido. Máxima exposición.

**Beneficio:** Con AUTO_BEST, duplicas las oportunidades de trade porque rara vez las 2 cryptos están laterales al mismo tiempo.

### Flujo de Autenticación Multi-Usuario

1. Login con email/password
2. Si el tenant tiene múltiples usuarios → mostrar selección de "bot activo"
3. Todas las operaciones se filtran por `tenantId` + `userId`
4. Los usuarios solo ven sus propios trades, portfolio y configuración

---

## 🔐 AUTENTICACIÓN

### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "trader@example.com",
  "password": "SecurePass123"
}
```

**Response 200 OK (1 usuario - Starter/Pro):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "email": "trader@example.com",
  "role": "TRADER",
  "tenantId": 1,
  "userId": 1,
  "plan": "PRO",
  "usersInTenant": 1,
  "selectedUserId": 1,
  "telegramConfigured": true,
  "requiresUserSelection": false,
  "expiresAt": "2026-04-29T12:00:00Z"
}
```

**Response 200 OK (múltiples usuarios - Enterprise):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "email": "admin@empresa.com",
  "role": "ADMIN",
  "tenantId": 42,
  "userId": 101,
  "plan": "ENTERPRISE",
  "usersInTenant": 3,
  "availableUsers": [
    {
      "userId": 101,
      "email": "admin@empresa.com",
      "name": "Administrador",
      "botActive": true,
      "capital": 5000.00
    },
    {
      "userId": 102,
      "email": "junior@empresa.com",
      "name": "Trader Junior",
      "botActive": false,
      "capital": 1000.00
    },
    {
      "userId": 103,
      "email": "guest@empresa.com",
      "name": "Invitado",
      "botActive": true,
      "capital": 2000.00
    }
  ],
  "requiresUserSelection": true,
  "selectedUserId": 101,
  "telegramConfigured": true,
  "expiresAt": "2026-04-29T12:00:00Z"
}
```

**Response 401 Unauthorized:**
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "Email o contraseña incorrectos"
}
```

### Refresh Token
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresAt": "2026-04-29T12:00:00Z"
}
```

### Registro (Onboarding SaaS)
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "nuevo@example.com",
  "password": "SecurePass123",
  "confirmPassword": "SecurePass123",
  "plan": "STARTER",
  "telegramChatId": "123456789"
}
```

**Response 201 Created:**
```json
{
  "userId": 42,
  "tenantId": 42,
  "email": "nuevo@example.com",
  "plan": "STARTER",
  "message": "Verifica tu email para activar la cuenta"
}
```

### Headers Requeridos (todos los endpoints protegidos)
```
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
X-User-ID: {userId}  # Opcional, si no se envía usa el usuario del token
Content-Type: application/json
```

**Nota:** Si un ADMIN de Enterprise quiere ver datos de otro usuario del mismo tenant, debe enviar `X-User-ID`. Si no se envía, se usa el usuario autenticado.

### Seleccionar Usuario Activo (Enterprise)
```http
POST /api/v1/auth/select-user
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "userId": 102,
  "reason": "Switching to junior account"
}
```

**Response 200 OK:**
```json
{
  "message": "Usuario seleccionado correctamente",
  "previousUserId": 101,
  "selectedUserId": 102,
  "newToken": "eyJhbGciOiJIUzI1NiIs...",  # Token actualizado con nuevo userId
  "user": {
    "id": 102,
    "email": "junior@empresa.com",
    "name": "Trader Junior",
    "botActive": false,
    "apiKeysConfigured": true
  }
}
```

**Response 403 Forbidden (usuario de otro tenant):**
```json
{
  "error": "FORBIDDEN",
  "message": "No puedes seleccionar un usuario de otro tenant"
}
```

---

## 👥 USUARIOS Y BOTS

### Listar Usuarios del Tenant
```http
GET /api/v1/users
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "tenantId": 42,
  "plan": "ENTERPRISE",
  "maxUsers": 3,
  "currentUsers": 3,
  "users": [
    {
      "id": 101,
      "email": "admin@empresa.com",
      "name": "Administrador",
      "role": "ADMIN",
      "botActive": true,
      "apiKeysConfigured": true,
      "telegramConfigured": true,
      "capital": 5000.00,
      "dailyPnL": 12.45,
      "createdAt": "2026-04-01T00:00:00Z",
      "lastActivityAt": "2026-04-29T12:00:00Z"
    },
    {
      "id": 102,
      "email": "junior@empresa.com",
      "name": "Trader Junior",
      "role": "TRADER",
      "botActive": false,
      "apiKeysConfigured": true,
      "telegramConfigured": false,
      "capital": 1000.00,
      "dailyPnL": 0.00,
      "createdAt": "2026-04-15T00:00:00Z",
      "lastActivityAt": "2026-04-28T18:00:00Z"
    }
  ]
}
```

### Crear Usuario (Enterprise - Solo Admin)
```http
POST /api/v1/users
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "email": "nuevo@empresa.com",
  "password": "SecurePass123",
  "name": "Nuevo Trader",
  "role": "TRADER",
  "telegramChatId": "987654321"
}
```

**Response 201 Created:**
```json
{
  "message": "Usuario creado correctamente",
  "userId": 104,
  "email": "nuevo@empresa.com",
  "tenantId": 42,
  "role": "TRADER",
  "botStatus": "PENDING_SETUP",  # Espera configuración de API keys
  "apiKeysConfigured": false,
  "createdAt": "2026-04-29T12:30:00Z"
}
```

**Response 403 Forbidden (límite de usuarios):**
```json
{
  "error": "USER_LIMIT_EXCEEDED",
  "message": "Plan Enterprise permite máximo 3 usuarios. Actualiza tu plan o elimina un usuario existente."
}
```

### Obtener Usuario por ID
```http
GET /api/v1/users/{userId}
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "id": 102,
  "email": "junior@empresa.com",
  "name": "Trader Junior",
  "role": "TRADER",
  "botActive": false,
  "apiKeysConfigured": true,
  "telegramConfigured": false,
  "telegramChatId": null,
  "capital": 1000.00,
  "totalTrades": 45,
  "winRate": 68.5,
  "totalPnL": 25.30,
  "currentStrategy": "RANGE",
  "tradingPair": "BTCUSDT",
  "createdAt": "2026-04-15T00:00:00Z",
  "lastActivityAt": "2026-04-28T18:00:00Z",
  "lastTradeAt": "2026-04-28T16:30:00Z"
}
```

### Actualizar Usuario
```http
PUT /api/v1/users/{userId}
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "name": "Trader Junior Actualizado",
  "telegramChatId": "987654321",
  "role": "TRADER"
}
```

**Permisos:**
- ADMIN puede editar cualquier usuario del tenant
- TRADER solo puede editar su propio usuario

### Eliminar Usuario (Enterprise - Solo Admin)
```http
DELETE /api/v1/users/{userId}
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "message": "Usuario eliminado correctamente",
  "userId": 103,
  "deletedAt": "2026-04-29T12:35:00Z",
  "note": "Los trades históricos se mantienen para auditoría pero se marcan como eliminados"
}
```

**Restricciones:**
- No se puede eliminar el último usuario del tenant
- No se puede eliminar el usuario autenticado

---

## 💰 PORTFOLIO

### Obtener Portfolio Actual
```http
GET /api/v1/portfolio
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "totalBalance": 312.45,
  "availableBalance": 150.20,
  "lockedBalance": 162.25,
  "dailyPnL": 12.45,
  "dailyPnLPercent": 4.15,
  "weeklyPnL": 25.30,
  "weeklyPnLPercent": 8.82,
  "monthlyPnL": 45.60,
  "monthlyPnLPercent": 17.08,
  "totalTrades": 24,
  "winningTrades": 18,
  "losingTrades": 6,
  "winRate": 75.0,
  "averageProfit": 5.2,
  "averageLoss": -2.1,
  "profitFactor": 2.47,
  "openPositions": 1,
  "currency": "USDT",
  "timestamp": "2026-04-29T12:00:00Z"
}
```

### Historial Equity Curve
```http
GET /api/v1/portfolio/equity?from=2026-04-01&to=2026-04-29&interval=daily
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Parámetros Query:**
- `from` (ISO 8601): Fecha inicio
- `to` (ISO 8601): Fecha fin  
- `interval`: `hourly`, `daily`, `weekly`

**Response 200 OK:**
```json
{
  "data": [
    {
      "timestamp": "2026-04-01T00:00:00Z",
      "balance": 300.00,
      "pnl": 0.00
    },
    {
      "timestamp": "2026-04-29T12:00:00Z",
      "balance": 312.45,
      "pnl": 12.45
    }
  ],
  "interval": "daily",
  "totalPoints": 29
}
```

### Balance por Símbolo
```http
GET /api/v1/portfolio/balances
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "balances": [
    {
      "asset": "USDT",
      "free": 150.20,
      "locked": 0.00,
      "total": 150.20
    },
    {
      "asset": "BTC",
      "free": 0.0015,
      "locked": 0.0005,
      "total": 0.0020,
      "btcValue": 162.25
    }
  ],
  "totalUsdtValue": 312.45
}
```

---

## 📊 TRADES

### Listar Trades (Paginado)
```http
GET /api/v1/trades?page=0&size=20&status=OPEN&symbol=BTCUSDT&strategy=TREND
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Parámetros Query:**
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 20, max: 100)
- `status` (opcional): `OPEN`, `CLOSED`, `CANCELLED`
- `symbol` (opcional): `BTCUSDT`, `ETHUSDT`, etc.
- `strategy` (opcional): `RANGE`, `TREND`, `SCALPING`
- `from`/`to` (opcional): Rango de fechas ISO 8601

**Response 200 OK:**
```json
{
  "content": [
    {
      "id": 152,
      "symbol": "BTCUSDT",
      "type": "BUY",
      "status": "OPEN",
      "entryPrice": 64500.50,
      "currentPrice": 65100.00,
      "exitPrice": null,
      "quantity": 0.002,
      "investedAmount": 129.00,
      "pnl": 1.10,
      "pnlPercent": 0.85,
      "stopLoss": 63500.00,
      "takeProfit": 67000.00,
      "strategyUsed": "TREND",
      "marketCondition": "TREND_UP",
      "entryTime": "2026-04-29T10:30:00Z",
      "exitTime": null,
      "exitReason": null,
      "duration": "2h 30m"
    },
    {
      "id": 151,
      "symbol": "BTCUSDT",
      "type": "SELL",
      "status": "CLOSED",
      "entryPrice": 63800.00,
      "exitPrice": 64500.00,
      "quantity": 0.0015,
      "investedAmount": 95.70,
      "pnl": 1.05,
      "pnlPercent": 1.10,
      "stopLoss": 63000.00,
      "takeProfit": 65000.00,
      "strategyUsed": "RANGE",
      "marketCondition": "RANGING",
      "entryTime": "2026-04-29T08:15:00Z",
      "exitTime": "2026-04-29T09:45:00Z",
      "exitReason": "TAKE_PROFIT",
      "duration": "1h 30m"
    }
  ],
  "totalElements": 152,
  "totalPages": 8,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

### Obtener Trade por ID
```http
GET /api/v1/trades/{id}
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "id": 152,
  "symbol": "BTCUSDT",
  "type": "BUY",
  "status": "OPEN",
  "entryPrice": 64500.50,
  "currentPrice": 65100.00,
  "exitPrice": null,
  "quantity": 0.002,
  "investedAmount": 129.00,
  "pnl": 1.10,
  "pnlPercent": 0.85,
  "fees": 0.26,
  "stopLoss": 63500.00,
  "takeProfit": 67000.00,
  "strategyUsed": "TREND",
  "marketCondition": "TREND_UP",
  "signal": {
    "rsi": 55.2,
    "adx": 32.5,
    "ema9": 64400.00,
    "ema21": 63800.00,
    "support": null,
    "resistance": null
  },
  "entryTime": "2026-04-29T10:30:00Z",
  "exitTime": null,
  "exitReason": null,
  "binanceOrderId": "1234567890",
  "duration": "2h 30m"
}
```

### Estadísticas de Trading
```http
GET /api/v1/trades/statistics?from=2026-04-01&to=2026-04-29
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "period": {
    "from": "2026-04-01",
    "to": "2026-04-29"
  },
  "totalTrades": 45,
  "winningTrades": 32,
  "losingTrades": 13,
  "winRate": 71.11,
  "totalPnL": 45.60,
  "totalPnLPercent": 17.08,
  "averageWin": 2.85,
  "averageLoss": -1.45,
  "profitFactor": 2.47,
  "maxDrawdown": -5.20,
  "maxDrawdownPercent": -2.15,
  "bestTrade": {
    "id": 142,
    "pnl": 8.50,
    "pnlPercent": 5.2
  },
  "worstTrade": {
    "id": 138,
    "pnl": -3.20,
    "pnlPercent": -2.1
  },
  "byStrategy": [
    {
      "strategy": "TREND",
      "trades": 20,
      "winRate": 75.0,
      "pnl": 28.50
    },
    {
      "strategy": "RANGE",
      "trades": 15,
      "winRate": 73.3,
      "pnl": 12.10
    },
    {
      "strategy": "SCALPING",
      "trades": 10,
      "winRate": 60.0,
      "pnl": 5.00
    }
  ]
}
```

---

## 📈 MARKET DATA

### Estado Actual del Mercado
```http
GET /api/v1/market/current?symbol=BTCUSDT
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "symbol": "BTCUSDT",
  "currentPrice": 65100.50,
  "priceChange24h": 1200.50,
  "priceChangePercent24h": 1.88,
  "high24h": 65500.00,
  "low24h": 63800.00,
  "volume24h": 28500.50000000,
  "marketCondition": "TREND_UP",
  "indicators": {
    "adx": 32.5,
    "rsi": 58.3,
    "rsi14": 55.2,
    "ema9": 64400.00,
    "ema21": 63800.00,
    "ema50": 62500.00,
    "atr": 850.00,
    "bollinger": {
      "upper": 66800.00,
      "middle": 65000.00,
      "lower": 63200.00
    }
  },
  "levels": {
    "support": [64500.00, 63800.00, 62500.00],
    "resistance": [65500.00, 66800.00, 67500.00]
  },
  "timestamp": "2026-04-29T12:00:00Z"
}
```

### Velas (Candlesticks)
```http
GET /api/v1/market/candles?symbol=BTCUSDT&interval=5m&limit=100
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Parámetros Query:**
- `symbol` (requerido): Par de trading
- `interval`: `1m`, `5m`, `15m`, `1h`, `4h`, `1d` (default: `5m`)
- `limit`: Cantidad de velas (default: 100, max: 1000)
- `from`/`to`: Timestamps en ms (opcional)

**Response 200 OK:**
```json
{
  "symbol": "BTCUSDT",
  "interval": "5m",
  "candles": [
    {
      "openTime": 1714392000000,
      "open": 65000.00,
      "high": 65100.00,
      "low": 64950.00,
      "close": 65080.00,
      "volume": 45.20000000,
      "closeTime": 1714392299999
    }
  ]
}
```

### Símbolos Disponibles
```http
GET /api/v1/market/symbols
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "symbols": [
    {
      "symbol": "BTCUSDT",
      "baseAsset": "BTC",
      "quoteAsset": "USDT",
      "minQty": 0.0001,
      "tickSize": 0.01,
      "status": "TRADING"
    },
    {
      "symbol": "ETHUSDT",
      "baseAsset": "ETH",
      "quoteAsset": "USDT",
      "minQty": 0.001,
      "tickSize": 0.01,
      "status": "TRADING"
    }
  ]
}
```

---

## ⚙️ CONFIGURACIÓN

### Obtener Configuración del Bot
```http
GET /api/v1/config
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "general": {
    "botActive": true,
    "mode": "AUTO_SWITCH",
    "selectedStrategy": "TREND",
    "pairSelectionMode": "AUTO_BEST",
    "tradingPairs": [
      {
        "symbol": "BTCUSDT",
        "active": true,
        "weight": 40,
        "currentCondition": "TREND_UP",
        "adx": 32.5
      },
      {
        "symbol": "ETHUSDT",
        "active": true,
        "weight": 35,
        "currentCondition": "RANGING",
        "adx": 18.2
      },
      {
        "symbol": "SOLUSDT",
        "active": false,
        "weight": 25,
        "currentCondition": "NEUTRAL",
        "adx": 12.0
      }
    ],
    "maxActivePairs": 3,
    "currentActivePair": "BTCUSDT",
    "testMode": false
  },
  "risk": {
    "maxPositionSizePercent": 5.0,
    "maxDailyLossPercent": 10.0,
    "stopLossPercent": 3.0,
    "takeProfitPercent": 5.0,
    "maxOpenPositions": 2,
    "maxOpenPositionsPerPair": 1,
    "trailingStop": true
  },
  "strategies": {
    "range": {
      "enabled": true,
      "pairs": ["BTCUSDT", "ETHUSDT"],
      "rsiPeriod": 14,
      "rsiOversold": 35,
      "rsiOverbought": 65,
      "lookbackPeriod": 20
    },
    "trend": {
      "enabled": true,
      "pairs": ["BTCUSDT", "ETHUSDT", "SOLUSDT"],
      "emaFast": 9,
      "emaSlow": 21,
      "adxThreshold": 25
    },
    "scalping": {
      "enabled": true,
      "pairs": ["BTCUSDT"],
      "rsiPeriod": 7,
      "rsiOversold": 25,
      "rsiOverbought": 75,
      "bbPeriod": 20,
      "bbDeviation": 2.0
    }
  },
  "notifications": {
    "emailEnabled": true,
    "telegramEnabled": true,
    "telegramChatId": "123456789",
    "onStrategySwitch": true,
    "onTradeOpen": true,
    "onTradeClose": true,
    "onDailySummary": true,
    "onRiskAlert": true,
    "onPairSwitch": true
  },
  "apiKeys": {
    "configured": true,
    "testnet": false,
    "lastValidated": "2026-04-29T10:00:00Z"
  },
  "planLimits": {
    "maxPairsAllowed": 3,
    "currentPairsConfigured": 3,
    "canAddMorePairs": false
  }
}
```

### Modos de Selección de Pares (`pairSelectionMode`)

| Modo | Descripción | Plan Requerido |
|------|-------------|----------------|
| `FIXED` | Opera solo el par principal configurado | Starter, Pro, Enterprise |
| `AUTO_BEST` | Escanea todos los pares y opera el que tenga mejor condición de mercado (ADX más alto) | Pro, Enterprise |
| `MULTI_PARALLEL` | Opera múltiples pares simultáneamente con capital distribuido | Enterprise |

**Ejemplo modo AUTO_BEST:**
- Pares configurados: BTCUSDT, ETHUSDT, SOLUSDT
- ADX actual: BTC=32, ETH=18, SOL=12
- Bot opera: **BTCUSDT** (tendencia más fuerte)
- Si BTC entra en rango y ETH sube a ADX 35 → Switch automático a ETHUSDT

**Ejemplo modo MULTI_PARALLEL:**
- Capital: $3,000
- Pares activos: 3
- Capital por par: $1,000 cada uno
- Cada par opera independiente con su propia estrategia

### Actualizar Configuración
```http
PUT /api/v1/config
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "general": {
    "mode": "AUTO_SWITCH",
    "selectedStrategy": "TREND",
    "pairSelectionMode": "AUTO_BEST",
    "tradingPairs": [
      {"symbol": "BTCUSDT", "active": true, "weight": 40},
      {"symbol": "ETHUSDT", "active": true, "weight": 35}
    ]
  },
  "risk": {
    "maxPositionSizePercent": 5.0,
    "maxDailyLossPercent": 10.0,
    "stopLossPercent": 3.0,
    "maxOpenPositions": 2,
    "maxOpenPositionsPerPair": 1
  },
  "notifications": {
    "telegramEnabled": true,
    "telegramChatId": "123456789",
    "onPairSwitch": true
  }
}
```

**Response 400 (límite de plan excedido):**
```json
{
  "error": "PLAN_LIMIT_EXCEEDED",
  "message": "Tu plan Starter permite solo 1 par. Actualiza a Pro (2 pares) o Enterprise (3 pares).",
  "currentPlan": "STARTER",
  "maxPairsAllowed": 1,
  "pairsRequested": 2,
  "upgradeUrl": "https://bot.qlstudio.com/upgrade"
}
```

### Gestionar Pares de Trading

#### Listar Pares Disponibles
```http
GET /api/v1/config/pairs/available
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "availablePairs": [
    {"symbol": "BTCUSDT", "name": "Bitcoin", "minQty": 0.0001, "quoteAsset": "USDT"},
    {"symbol": "ETHUSDT", "name": "Ethereum", "minQty": 0.001, "quoteAsset": "USDT"},
    {"symbol": "SOLUSDT", "name": "Solana", "minQty": 0.01, "quoteAsset": "USDT"},
    {"symbol": "ADAUSDT", "name": "Cardano", "minQty": 1.0, "quoteAsset": "USDT"},
    {"symbol": "DOTUSDT", "name": "Polkadot", "minQty": 0.1, "quoteAsset": "USDT"}
  ],
  "planLimits": {
    "maxPairs": 3,
    "currentPairs": 2,
    "canAddMore": true
  }
}
```

#### Agregar Par de Trading
```http
POST /api/v1/config/pairs
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "symbol": "SOLUSDT",
  "weight": 25,
  "active": true
}
```

#### Eliminar Par de Trading
```http
DELETE /api/v1/config/pairs/{symbol}
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Nota:** No se puede eliminar el último par activo. Debe haber al menos 1.

**Response 200 OK:**
```json
{
  "message": "Configuración actualizada",
  "updatedAt": "2026-04-29T12:05:00Z"
}
```

### Configurar API Keys Binance
```http
POST /api/v1/config/apikeys
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "apiKey": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "apiSecret": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "testnet": true
}
```

**Response 200 OK:**
```json
{
  "message": "API Keys guardadas y validadas",
  "testnet": true,
  "validated": true,
  "permissions": ["SPOT", "TRADE"]
}
```

**Response 400 Bad Request (keys inválidas):**
```json
{
  "error": "INVALID_API_KEYS",
  "message": "Las API keys no son válidas para Binance"
}
```

### Test Connection
```http
POST /api/v1/config/test-connection
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "success": true,
  "latency": 45,
  "accountType": "SPOT",
  "balances": [
    {"asset": "USDT", "free": "300.00", "locked": "0.00"}
  ]
}
```

### Activar/Desactivar Bot
```http
POST /api/v1/config/toggle
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}

{
  "active": true
}
```

**Response 200 OK:**
```json
{
  "botActive": true,
  "message": "Bot activado",
  "activatedAt": "2026-04-29T12:10:00Z"
}
```

---

## 🎯 ESTRATEGIAS

### Listar Estrategias Disponibles
```http
GET /api/v1/strategies
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "strategies": [
    {
      "id": "RANGE",
      "name": "Range Trading",
      "description": "Opera entre soportes y resistencias detectadas automáticamente",
      "conditions": ["RANGING"],
      "parameters": {
        "rsiPeriod": 14,
        "rsiOversold": 35,
        "rsiOverbought": 65
      }
    },
    {
      "id": "TREND",
      "name": "Trend Following",
      "description": "Sigue la tendencia usando EMAs y ADX",
      "conditions": ["TREND_UP", "TREND_DOWN"],
      "parameters": {
        "emaFast": 9,
        "emaSlow": 21,
        "adxThreshold": 25
      }
    },
    {
      "id": "SCALPING",
      "name": "Scalping",
      "description": "Operaciones rápidas usando RSI y Bollinger Bands",
      "conditions": ["SCALPING_OPPORTUNITY"],
      "parameters": {
        "rsiPeriod": 7,
        "bbPeriod": 20
      }
    }
  ],
  "autoSwitchEnabled": true
}
```

### Historial de Cambios de Estrategia
```http
GET /api/v1/strategies/switches?page=0&size=20
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "id": 15,
      "previousStrategy": "RANGE",
      "newStrategy": "TREND",
      "previousCondition": "RANGING",
      "newCondition": "TREND_UP",
      "reason": "ADX 32.5 > 25, EMA9 cruzó sobre EMA21",
      "portfolioValueAtSwitch": 310.35,
      "tradesClosed": 0,
      "timestamp": "2026-04-29T10:30:00Z",
      "notificationSent": true
    }
  ],
  "totalElements": 15,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### Forzar Cambio de Estrategia (Manual)
```http
POST /api/v1/strategies/switch
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "strategy": "TREND",
  "reason": "Cambio manual por usuario"
}
```

**Response 200 OK:**
```json
{
  "message": "Estrategia cambiada a TREND",
  "previousStrategy": "RANGE",
  "newStrategy": "TREND",
  "switchedAt": "2026-04-29T12:15:00Z",
  "notificationSent": true
}
```

---

## 📱 NOTIFICACIONES

### Obtener Configuración de Notificaciones
```http
GET /api/v1/notifications/config
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

### Actualizar Configuración Telegram
```http
PUT /api/v1/notifications/telegram
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "enabled": true,
  "chatId": "123456789"
}
```

### Test Notificación Telegram
```http
POST /api/v1/notifications/test
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "channel": "TELEGRAM"
}
```

**Response 200 OK:**
```json
{
  "sent": true,
  "message": "🔔 Test notification - QL Trading Bot",
  "timestamp": "2026-04-29T12:20:00Z"
}
```

### Historial de Notificaciones
```http
GET /api/v1/notifications/history?page=0&size=50
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "id": 152,
      "type": "STRATEGY_SWITCH",
      "channel": "TELEGRAM",
      "title": "🔄 Estrategia Cambiada",
      "message": "Modo: TENDENCIA ALCISTA...",
      "sent": true,
      "read": true,
      "timestamp": "2026-04-29T10:30:00Z"
    }
  ]
}
```

---

## 👤 USER PROFILE

### Obtener Perfil
```http
GET /api/v1/user/profile
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "email": "trader@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "TRADER",
  "plan": "PRO",
  "tenantId": 1,
  "telegramChatId": "123456789",
  "telegramConfigured": true,
  "createdAt": "2026-04-01T00:00:00Z",
  "lastLoginAt": "2026-04-29T08:00:00Z",
  "active": true
}
```

### Actualizar Perfil
```http
PUT /api/v1/user/profile
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "firstName": "Juan",
  "lastName": "Pérez",
  "telegramChatId": "123456789"
}
```

### Cambiar Password
```http
POST /api/v1/user/change-password
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "currentPassword": "oldpass123",
  "newPassword": "newpass123",
  "confirmPassword": "newpass123"
}
```

---

## 🔧 ADMIN (Super Admin - Plataforma)

### Listar Tenants
```http
GET /api/v1/admin/tenants?page=0&size=20
Authorization: Bearer {token}
X-Tenant-ID: {adminTenantId}
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "id": 42,
      "name": "Empresa Pérez Trading",
      "adminEmail": "admin@empresa.com",
      "plan": "ENTERPRISE",
      "maxUsers": 3,
      "active": true,
      "createdAt": "2026-04-01T00:00:00Z",
      "lastActivityAt": "2026-04-29T12:00:00Z",
      "users": [
        {
          "userId": 101,
          "email": "admin@empresa.com",
          "botActive": true,
          "capital": 5000.00,
          "dailyPnL": 12.45
        },
        {
          "userId": 102,
          "email": "junior@empresa.com",
          "botActive": false,
          "capital": 1000.00,
          "dailyPnL": 0.00
        }
      ],
      "stats": {
        "totalUsers": 3,
        "activeBots": 2,
        "totalTrades": 245,
        "totalPnL": 58.90,
        "totalCapital": 8000.00
      }
    }
  ]
}
```

### Obtener Detalle de Tenant
```http
GET /api/v1/admin/tenants/{tenantId}
Authorization: Bearer {token}
X-Tenant-ID: {adminTenantId}
```

**Response 200 OK:**
```json
{
  "id": 42,
  "name": "Empresa Pérez Trading",
  "adminEmail": "admin@empresa.com",
  "plan": "ENTERPRISE",
  "maxUsers": 3,
  "active": true,
  "createdAt": "2026-04-01T00:00:00Z",
  "billing": {
    "plan": "ENTERPRISE",
    "monthlyFee": 199.00,
    "nextBillingDate": "2026-05-01",
    "paymentMethod": "Stripe",
    "status": "ACTIVE"
  },
  "users": [
    {
      "userId": 101,
      "email": "admin@empresa.com",
      "name": "Administrador",
      "role": "ADMIN",
      "botActive": true,
      "apiKeysConfigured": true,
      "capital": 5000.00,
      "totalTrades": 150,
      "winRate": 75.0,
      "totalPnL": 45.60,
      "createdAt": "2026-04-01T00:00:00Z",
      "lastActivityAt": "2026-04-29T12:00:00Z"
    },
    {
      "userId": 102,
      "email": "junior@empresa.com",
      "name": "Trader Junior",
      "role": "TRADER",
      "botActive": false,
      "apiKeysConfigured": true,
      "capital": 1000.00,
      "totalTrades": 45,
      "winRate": 68.5,
      "totalPnL": 8.20,
      "createdAt": "2026-04-15T00:00:00Z",
      "lastActivityAt": "2026-04-28T18:00:00Z"
    },
    {
      "userId": 103,
      "email": "guest@empresa.com",
      "name": "Invitado",
      "role": "TRADER",
      "botActive": true,
      "apiKeysConfigured": true,
      "capital": 2000.00,
      "totalTrades": 50,
      "winRate": 72.0,
      "totalPnL": 5.10,
      "createdAt": "2026-04-20T00:00:00Z",
      "lastActivityAt": "2026-04-29T10:00:00Z"
    }
  ],
  "aggregatedStats": {
    "totalUsers": 3,
    "activeBots": 2,
    "totalTrades": 245,
    "totalPnL": 58.90,
    "totalCapital": 8000.00,
    "avgWinRate": 71.8
  }
}
```

### Métricas Globales (Super Admin)
```http
GET /api/v1/admin/metrics
Authorization: Bearer {token}
X-Tenant-ID: {adminTenantId}
```

**Response 200 OK:**
```json
{
  "platform": {
    "tenants": {
      "total": 42,
      "active": 38,
      "byPlan": {
        "STARTER": 20,
        "PRO": 15,
        "ENTERPRISE": 7
      }
    },
    "users": {
      "total": 56,
      "byPlan": {
        "STARTER": 20,
        "PRO": 15,
        "ENTERPRISE": 21
      }
    },
    "bots": {
      "total": 56,
      "active": 48,
      "paused": 8
    }
  },
  "trading": {
    "totalTrades24h": 1250,
    "totalVolume24h": 125000.00,
    "totalCapitalManaged": 485000.00,
    "avgDailyPnL": 2.5,
    "topPerformers": [
      {
        "tenantId": 42,
        "userId": 101,
        "email": "admin@empresa.com",
        "dailyPnL": 12.45,
        "winRate": 75.0
      }
    ]
  },
  "system": {
    "apiCalls24h": 50000,
    "notificationsSent24h": 3500,
    "webSocketConnections": 48,
    "binanceApiCalls24h": 125000
  },
  "revenue": {
    "mrr": 5536.00,
    "arr": 66432.00,
    "byPlan": {
      "STARTER": 580.00,
      "PRO": 1185.00,
      "ENTERPRISE": 1393.00
    }
  }
}
```

### Métricas por Tenant (Admin del Tenant)
```http
GET /api/v1/admin/tenant-metrics
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
```

**Response 200 OK (Enterprise con múltiples usuarios):**
```json
{
  "tenantId": 42,
  "plan": "ENTERPRISE",
  "maxUsers": 3,
  "currentUsers": 3,
  "users": [
    {
      "userId": 101,
      "email": "admin@empresa.com",
      "name": "Administrador",
      "botActive": true,
      "capital": 5000.00,
      "dailyPnL": 12.45,
      "dailyPnLPercent": 0.25,
      "totalTrades": 150,
      "winRate": 75.0,
      "currentStrategy": "TREND",
      "lastTradeAt": "2026-04-29T11:30:00Z"
    },
    {
      "userId": 102,
      "email": "junior@empresa.com",
      "name": "Trader Junior",
      "botActive": false,
      "capital": 1000.00,
      "dailyPnL": 0.00,
      "dailyPnLPercent": 0.00,
      "totalTrades": 45,
      "winRate": 68.5,
      "currentStrategy": null,
      "lastTradeAt": "2026-04-28T16:30:00Z"
    }
  ],
  "aggregated": {
    "totalCapital": 8000.00,
    "totalDailyPnL": 15.30,
    "totalTrades": 245,
    "activeBots": 2,
    "avgWinRate": 71.8
  }
}
```

### Suspender/Reactivar Tenant
```http
POST /api/v1/admin/tenants/{tenantId}/toggle
Authorization: Bearer {token}
X-Tenant-ID: {tenantId}
Content-Type: application/json

{
  "active": false,
  "reason": "Suscripción vencida"
}
```

### Cambiar Plan de Tenant
```http
PUT /api/v1/admin/tenants/{tenantId}/plan
Authorization: Bearer {token}
X-Tenant-ID: {adminTenantId}
Content-Type: application/json

{
  "plan": "ENTERPRISE",
  "reason": "Upgrade solicitado",
  "immediate": true
}
```

**Response 200 OK:**
```json
{
  "message": "Plan actualizado correctamente",
  "tenantId": 42,
  "previousPlan": "PRO",
  "newPlan": "ENTERPRISE",
  "maxUsers": 3,
  "updatedAt": "2026-04-29T12:00:00Z",
  "effectiveDate": "2026-04-29T12:00:00Z",
  "nextBillingDate": "2026-05-01T00:00:00Z"
}
```

**Response 400 Bad Request (downgrade con más usuarios que el límite):**
```json
{
  "error": "DOWNGRADE_NOT_ALLOWED",
  "message": "El tenant tiene 3 usuarios pero el plan PRO permite solo 1. Elimina 2 usuarios antes de downgradear.",
  "currentUsers": 3,
  "maxAllowed": 1
}
```

---

## 🔌 WEBSOCKET

### Conexión
```
wss://api.qltradingbot.com/ws/market?token={jwt}&tenantId={tenantId}&userId={userId}
```

**Parámetros:**
- `token` (requerido): JWT del usuario autenticado
- `tenantId` (requerido): ID del tenant
- `userId` (opcional): Si no se envía, usa el userId del token. Los admins de Enterprise pueden especificar otro usuario del mismo tenant.

### Autenticación y Autorización WebSocket

Al conectar, el servidor valida:
1. Token JWT válido
2. Tenant ID coincide con el del token (o el admin tiene acceso)
3. Si se especifica `userId`, el usuario debe pertenecer al mismo tenant

**Mensaje de error (conexión rechazada):**
```json
{
  "type": "ERROR",
  "code": "UNAUTHORIZED",
  "message": "Token inválido o acceso denegado al usuario especificado",
  "timestamp": "2026-04-29T12:00:00Z"
}
```

### Mensajes del Servidor

#### Market Update (cada 1-5 segundos)
```json
{
  "type": "MARKET_UPDATE",
  "timestamp": "2026-04-29T12:00:00Z",
  "data": {
    "symbol": "BTCUSDT",
    "price": 65100.50,
    "change24h": 1.88,
    "marketCondition": "TREND_UP",
    "indicators": {
      "adx": 32.5,
      "rsi": 58.3,
      "ema9": 64400.00,
      "ema21": 63800.00
    }
  }
}
```

#### Trade Update (cuando hay cambios)
```json
{
  "type": "TRADE_UPDATE",
  "timestamp": "2026-04-29T12:00:00Z",
  "data": {
    "tradeId": 152,
    "status": "OPEN",
    "currentPnL": 1.10,
    "currentPnLPercent": 0.85,
    "currentPrice": 65100.00
  }
}
```

#### Strategy Switch
```json
{
  "type": "STRATEGY_SWITCH",
  "timestamp": "2026-04-29T12:00:00Z",
  "data": {
    "previousStrategy": "RANGE",
    "newStrategy": "TREND",
    "reason": "ADX 32.5 > 25",
    "marketCondition": "TREND_UP"
  }
}
```

#### Bot Status Change
```json
{
  "type": "BOT_STATUS",
  "timestamp": "2026-04-29T12:00:00Z",
  "data": {
    "active": true,
    "reason": "Activado por usuario",
    "pausedUntil": null
  }
}
```

#### Ping/Pong
Servidor envía ping cada 30s:
```json
{"type": "PING", "timestamp": "2026-04-29T12:00:00Z"}
```

Cliente debe responder:
```json
{"type": "PONG"}
```

### Reconexión
En caso de desconexión, el cliente debe reconectar con backoff exponencial:
- Intento 1: 1 segundo
- Intento 2: 2 segundos  
- Intento 3: 4 segundos
- Intento 4: 8 segundos
- Máximo: 30 segundos entre intentos
- Máximo intentos: 10

---

## ❌ ERRORES

### Códigos de Error HTTP

| Código | Significado | Ejemplo |
|--------|-------------|---------|
| 400 | Bad Request | Parámetros inválidos |
| 401 | Unauthorized | Token JWT inválido o expirado |
| 403 | Forbidden | Acceso a tenant no autorizado |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | Operación no permitida (ej: cambio de estrategia en modo auto) |
| 422 | Unprocessable Entity | Validación de negocio falló |
| 429 | Too Many Requests | Rate limit excedido |
| 500 | Internal Server Error | Error del servidor |

### Formatos de Error

**Error 400 - Bad Request:**
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Los datos proporcionados son inválidos",
  "details": {
    "email": "El formato de email es inválido",
    "password": "La contraseña debe tener al menos 8 caracteres"
  }
}
```

**Error 401 - Unauthorized:**
```json
{
  "error": "UNAUTHORIZED",
  "message": "Token JWT inválido o expirado"
}
```

**Error 403 - Forbidden (Cross-tenant access):**
```json
{
  "error": "FORBIDDEN",
  "message": "Access denied: Tenant 1 cannot access data of tenant 2"
}
```

**Error 422 - Business Validation:**
```json
{
  "error": "RISK_VIOLATION",
  "message": "Trade rechazado: excede el tamaño máximo de posición (5%)"
}
```

**Error 429 - Rate Limit:**
```json
{
  "error": "RATE_LIMIT_EXCEEDED",
  "message": "Límite de requests excedido. Intenta en 60 segundos.",
  "retryAfter": 60
}
```

### Rate Limits

| Endpoint | Límite | Ventana |
|----------|--------|---------|
| `/api/v1/auth/*` | 10 requests | 1 minuto |
| `/api/v1/market/*` | 60 requests | 1 minuto |
| `/api/v1/trades/*` | 30 requests | 1 minuto |
| `/api/v1/config/*` | 20 requests | 1 minuto |
| WebSocket | 1 conexión | por tenant |

---

## 📊 MODELOS DE DATOS COMPLETOS

### Enums

```typescript
// MarketCondition
enum MarketCondition {
  RANGING = "RANGING",
  TREND_UP = "TREND_UP", 
  TREND_DOWN = "TREND_DOWN",
  SCALPING_OPPORTUNITY = "SCALPING_OPPORTUNITY",
  NEUTRAL = "NEUTRAL"
}

// TradingStrategy
enum TradingStrategy {
  RANGE = "RANGE",
  TREND = "TREND",
  SCALPING = "SCALPING"
}

// TradeType
enum TradeType {
  BUY = "BUY",
  SELL = "SELL"
}

// TradeStatus
enum TradeStatus {
  OPEN = "OPEN",
  CLOSED = "CLOSED",
  CANCELLED = "CANCELLED"
}

// ExitReason
enum ExitReason {
  STOP_LOSS = "STOP_LOSS",
  TAKE_PROFIT = "TAKE_PROFIT",
  STRATEGY_SWITCH = "STRATEGY_SWITCH",
  MANUAL_CLOSE = "MANUAL_CLOSE",
  SYSTEM_CLOSE = "SYSTEM_CLOSE"
}

// UserRole
enum UserRole {
  TRADER = "TRADER",
  ADMIN = "ADMIN"
}

// NotificationType
enum NotificationType {
  STRATEGY_SWITCH = "STRATEGY_SWITCH",
  TRADE_OPENED = "TRADE_OPENED",
  TRADE_CLOSED = "TRADE_CLOSED",
  DAILY_SUMMARY = "DAILY_SUMMARY",
  RISK_ALERT = "RISK_ALERT",
  SYSTEM_ALERT = "SYSTEM_ALERT"
}

// NotificationChannel
enum NotificationChannel {
  EMAIL = "EMAIL",
  TELEGRAM = "TELEGRAM"
}

// PairSelectionMode - Modos de selección de pares
enum PairSelectionMode {
  FIXED = "FIXED",                    // Starter: 1 par fijo
  AUTO_BEST = "AUTO_BEST",            // Pro: Elige el par con mejor condición
  MULTI_PARALLEL = "MULTI_PARALLEL"   // Enterprise: Opera múltiples pares
}

// SubscriptionPlan - Planes de suscripción
enum SubscriptionPlan {
  STARTER = "STARTER",       // 1 par, 1 usuario, $29/mes
  PRO = "PRO",               // 2 pares, 1 usuario, $79/mes
  ENTERPRISE = "ENTERPRISE"  // 3 pares, 3 usuarios, $199/mes
}
```

### Entidades Principales

```typescript
// BotUser - Cada usuario = 1 Bot con Multi-Pair Trading
interface BotUser {
  id: number;
  tenantId: number;              // FK al tenant (aislamiento)
  email: string;                 // Login único
  password: string;              // BCrypt hash
  name: string;
  role: UserRole;                // ADMIN o TRADER
  
  // Bot Configuration
  apiKeysConfigured: boolean;
  encryptedApiKey?: string;      // AES-256
  encryptedApiSecret?: string;   // AES-256
  testnet: boolean;              // TRUE = paper trading
  
  // Multi-Pair Trading Configuration
  pairSelectionMode: 'FIXED' | 'AUTO_BEST' | 'MULTI_PARALLEL';
  maxActivePairs: number;        // 1, 2, o 3 según plan
  tradingPairs: TradingPairConfig[];
  currentActivePair?: string;    // En modo AUTO_BEST: el par operando ahora
  
  // Bot Status
  botActive: boolean;
  currentStrategy: TradingStrategy;
  mode: 'AUTO_SWITCH' | 'MANUAL';
  
  // Capital Management (por par)
  totalCapital: number;          // Capital total del bot
  capitalDistribution: Record<string, number>; // { "BTCUSDT": 0.4, "ETHUSDT": 0.6 }
  
  // Notifications
  telegramChatId?: string;
  notificationsEnabled: {
    strategySwitch: boolean;
    pairSwitch: boolean;         // Nuevo: notificar cambio de par
    tradeOpen: boolean;
    tradeClose: boolean;
    dailySummary: boolean;
    riskAlert: boolean;
  };
  
  // Timestamps
  createdAt: string;
  updatedAt: string;
  lastLoginAt?: string;
  lastActivityAt?: string;
  
  // Soft delete
  deleted: boolean;
  deletedAt?: string;
}

// TradingPairConfig - Configuración de un par de trading
interface TradingPairConfig {
  symbol: string;                // "BTCUSDT", "ETHUSDT", etc.
  active: boolean;               // Si está habilitado para operar
  weight: number;                // 0-100, distribución del capital
  strategy: TradingStrategy;     // Estrategia específica por par (opcional)
  
  // Indicadores actuales (cache, se actualiza cada 5 min)
  currentCondition?: MarketCondition;
  adx?: number;
  rsi?: number;
  lastUpdated?: string;
}

// Tenant - Contenedor de usuarios (plan de suscripción)
interface Tenant {
  id: number;
  name?: string;                 // Nombre opcional del tenant
  adminEmail: string;            // Email del creador/admin principal
  plan: SubscriptionPlan;
  maxUsers: number;              // 1, 1, o 3 según plan
  maxPairs: number;              // 1, 2, o 3 según plan (nuevo)
  
  // Billing
  stripeCustomerId?: string;
  stripeSubscriptionId?: string;
  monthlyFee: number;
  nextBillingDate?: string;
  
  // Status
  active: boolean;
  suspendedReason?: string;
  
  // Timestamps
  createdAt: string;
  updatedAt: string;
}

// Trade - Operación de trading con soporte Multi-Pair
interface Trade {
  id: number;
  tenantId: number;              // Para aislamiento de datos
  userId: number;                // FK al BotUser
  
  // Trade Details
  symbol: string;                // "BTCUSDT", "ETHUSDT", "SOLUSDT", etc.
  type: TradeType;               // BUY o SELL
  status: TradeStatus;           // OPEN, CLOSED, CANCELLED
  
  // Multi-Pair Tracking
  pairIndex: number;             // Índice del par en tradingPairs[] (0, 1, 2)
  
  // Prices
  entryPrice: number;
  exitPrice?: number;
  currentPrice?: number;        // Para trades abiertos
  
  // Quantities
  quantity: number;
  investedAmount: number;        // quantity * entryPrice
  
  // P&L
  pnl?: number;                  // Profit/Loss en USDT
  pnlPercent?: number;           // Porcentaje
  fees?: number;                 // Comisiones pagadas
  
  // Risk Management
  stopLoss: number;
  takeProfit: number;
  trailingStop?: boolean;
  
  // Strategy
  strategyUsed: TradingStrategy;
  marketCondition: MarketCondition;
  signalSnapshot: {
    rsi: number;
    adx: number;
    ema9: number;
    ema21: number;
    support?: number;
    resistance?: number;
  };
  
  // Execution
  entryTime: string;             // ISO 8601
  exitTime?: string;
  exitReason?: ExitReason;
  duration?: string;             // "2h 30m"
  
  // Binance
  binanceOrderId?: string;
  
  // Timestamps
  createdAt: string;
  updatedAt: string;
}

// MarketConditionHistory - Para análisis posterior
interface MarketConditionHistory {
  id: number;
  tenantId: number;
  userId: number;
  symbol: string;
  
  // Indicators
  adx: number;
  rsi: number;
  ema9: number;
  ema21: number;
  atr: number;
  
  // Detected
  marketCondition: MarketCondition;
  confidence: number;            // 0-100%
  
  timestamp: string;
}

// StrategySwitch - Auditar cambios de estrategia
interface StrategySwitch {
  id: number;
  tenantId: number;
  userId: number;
  
  previousStrategy: TradingStrategy;
  newStrategy: TradingStrategy;
  previousCondition: MarketCondition;
  newCondition: MarketCondition;
  
  reason: string;
  portfolioValueAtSwitch: number;
  tradesClosed: number;          // Cuántos trades se cerraron
  
  notificationSent: boolean;
  timestamp: string;
}

// Notification - Log de notificaciones enviadas
interface Notification {
  id: number;
  tenantId: number;
  userId: number;
  
  type: NotificationType;
  channel: NotificationChannel;
  title: string;
  message: string;
  
  sent: boolean;
  deliveredAt?: string;
  error?: string;
  
  read: boolean;
  readAt?: string;
  
  timestamp: string;
}

// RiskEvent - Log de eventos de riesgo
interface RiskEvent {
  id: number;
  tenantId: number;
  userId: number;
  
  eventType: 'DAILY_LIMIT_REACHED' | 'MAX_POSITIONS_REACHED' | 'BOT_PAUSED' | 'BOT_RESUMED';
  message: string;
  details: Record<string, any>;
  
  timestamp: string;
}
```

### Relaciones entre Entidades

```
┌─────────────────────────────────────────────────────────────┐
│                         TENANT                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  BotUser (1) - Admin del Tenant                       │  │
│  │  ├── API Keys propias                                 │  │
│  │  ├── Bot independiente                                │  │
│  │  └── Trades aislados (tenant_id + user_id)            │  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  BotUser (2) - Trader Junior                          │  │
│  │  ├── API Keys propias (diferentes)                    │  │
│  │  ├── Bot independiente                                │  │
│  │  └── Trades aislados                                  │  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  BotUser (3) - Invitado                               │  │
│  │  ├── API Keys propias                                 │  │
│  │  ├── Bot independiente                                │  │
│  │  └── Trades aislados                                  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘

Características del aislamiento:
• Cada BotUser tiene sus propias API Keys de Binance
• Cada BotUser opera su propio capital (no se mezcla)
• Los trades se filtran por tenant_id + user_id
• Los admins pueden ver datos de todos los usuarios de su tenant
• Los traders solo ven sus propios datos
```

---

## 🔗 RECURSOS ADICIONALES

### Postman Collection
Descargar colección completa: `QL_Trading_Bot_API.postman_collection.json`

### OpenAPI Spec
Especificación OpenAPI 3.0: `openapi-trading-bot.yaml`

### SDK Client (Generado)
```bash
# TypeScript/Axios
npx openapi-generator-cli generate \
  -i openapi-trading-bot.yaml \
  -g typescript-axios \
  -o ./sdk-typescript
```

---

*QL Software Studio*  
*Documentación v1.0 - Abril 2026*
