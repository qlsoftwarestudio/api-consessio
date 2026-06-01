# Plan de Acción - Trading Bot Binance

**Proyecto:** QL Trading Bot SaaS  
**Arquitectura Base:** Concessio CRM (GIAMMA 360)  
**Modelo de Negocio:** Multi-tenant SaaS ($29/$79/$199 mensuales)  
**Fecha:** Abril 2026  
**Estimación Total:** 4 semanas (56 horas)

---

## Fase 0: Preparación (Pre-kickoff)

### Entregables Previos
- [ ] Aprobar plan de acción
- [ ] Crear repositorio Git privado
- [ ] Configurar Binance Testnet API Keys
- [ ] Provisionar VPS (AWS/GCP 4CPU/8GB)
- [ ] Configurar PostgreSQL + Redis en VPS
- [ ] Configurar bot de Telegram (BotFather)

---

## Semana 1: Infraestructura + Multi-Tenant Core

### Días 1-2: Setup Proyecto
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| Crear proyecto Spring Boot 3.3 | 2 | `build.gradle` con WebSocket, TA4J, Redis, Telegram deps |
| Estructura feature-based | 1 | `marketdata/`, `strategy/`, `risk/`, `execution/`, `tenant/`, `portfolio/`, `notification/` |
| Configurar PostgreSQL + JPA | 2 | `application.yml`, entidades base |
| Configurar Redis | 1 | `RedisConfig.java`, `CacheConfig.java` |
| Setup Docker Compose | 2 | `docker-compose.yml` (app, postgres, redis) |

**Reutilizable de Concessio:**
- Estructura de paquetes feature-based
- Configuración base de Spring Boot
- `TenantContext` (ThreadLocal) - copiar y adaptar
- JWT filter pattern

### Días 3-5: Multi-Tenant Core + Seguridad
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `TenantContext` + `TenantAwareEntity` | 3 | `@MappedSuperclass` con `tenant_id` |
| `TenantJwtFilter` | 2 | Extraer `tenant_id` del JWT |
| `TenantAwareRepository` | 2 | Base repository con filtro automático |
| Encriptación API Keys (AES-256) | 3 | `SecureCredentialsService.java` |
| `BotUser` + `BinanceCredentials` | 2 | Entidades tenant-aware |

**Reutilizable de Concessio:**
- `@/home/emilio/Escritorio/quilodranEmilioBackUp/emilioquilodran/qlsoftwarestudio/giamma-360-backend/src/main/java/com/concessio/crm/tenant/TenantContext.java:1-18`
- Patrón de JWT filter de `TenantContextFilter.java`

### Días 6-7: Binance Connector
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `BinanceRestClient` | 3 | REST API wrapper (account, orders, klines) |
| `BinanceWebSocketClient` | 4 | WebSocket streaming (price, klines) |
| `MarketDataService` | 2 | Agregar/quitar símbolos, cache de precios |
| `CandlestickRepository` | 2 | Guardar candles en PostgreSQL |

**Nuevo desarrollo:** WebSocket no existe en Concessio

**Entregable Semana 1:**
- Proyecto base corriendo en Docker
- Multi-tenant funcional (JWT + tenant_id)
- Conexión a Binance Testnet establecida
- Datos de mercado guardándose en DB

---

## Semana 2: Strategy Engine + Indicadores

### Días 8-10: Strategy Pattern Engine
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `TradingStrategy` interface | 2 | `generateSignal()`, `onActivate()`, `onDeactivate()` |
| `StrategyContext` | 4 | Map de estrategias, auto-switch logic |
| `MarketConditionDetector` | 4 | ADX, EMAs, RSI, ATR calculations |
| `MarketCondition` enum | 1 | `RANGING`, `TREND_UP`, `TREND_DOWN`, `SCALPING_OPPORTUNITY`, `NEUTRAL` |
| `Signal` class | 1 | `BUY`, `SELL`, `HOLD` con stop-loss/take-profit |

**Reutilizable de Concessio:**
- Patrón Strategy de `QuotationCalculationStrategy`
- `@/home/emilio/Escritorio/quilodranEmilioBackUp/emilioquilodran/qlsoftwarestudio/giamma-360-backend/src/main/java/com/concessio/crm/quotation/calculation/QuotationCalculationStrategy.java:9-31`
- `@/home/emilio/Escritorio/quilodranEmilioBackUp/emilioquilodran/qlsoftwarestudio/giamma-360-backend/src/main/java/com/concessio/crm/quotation/calculation/QuotationCalculationContext.java:1-92`

### Días 11-13: Estrategias Implementadas
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `RangeTradingStrategy` | 3 | Soporte/Resistencia + RSI |
| `TrendFollowingStrategy` | 3 | EMA crossover + ADX |
| `ScalpingStrategy` | 3 | RSI rápido + Bollinger Bands |
| `TechnicalIndicatorCalculator` | 3 | TA4J wrapper para indicadores |
| `SupportResistanceDetector` | 2 | Detección automática de niveles |

### Días 14: Testing Estrategias
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| Unit tests estrategias | 2 | Mock candles, verificar señales |
| Backtesting simple | 2 | `BacktestService.java` con histórico |

**Entregable Semana 2:**
- 3 estrategias funcionando
- Auto-detección de condición de mercado
- Tests de estrategias pasando

---

## Semana 3: Auto-Switch + Risk + Notificaciones

### Días 15-16: Auto-Switch + Scheduler
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `@Scheduled` evaluación | 2 | Cada 1 minuto analizar mercado |
| Lógica de switch | 3 | Cambiar estrategia si condición cambia |
| Cierre de operaciones | 2 | `closeOpenPositions()` al cambiar |
| `StrategySwitch` entity | 2 | Guardar historial de cambios |
| Registro de switches | 1 | Auditar cambios de estrategia |

### Días 17-18: Risk Management
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `RiskManager` | 3 | Validar trades contra reglas |
| `PositionSizer` | 2 | Calcular tamaño basado en riesgo |
| `StopLossCalculator` | 2 | Stops dinámicos por estrategia |
| Reglas hardcoded | 2 | Max 5% posición, 10% daily loss, 2 posiciones max |
| Pausar bot | 1 | Stop trading si se alcanza límite |

### Días 19-20: Notificaciones
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `NotificationService` interface | 1 | `sendAlert()`, `sendTradeNotification()` |
| `TelegramBot` | 3 | Bot API integration |
| `StrategySwitchAlert` | 2 | Mensaje formateado de cambio |
| `TradeNotification` | 2 | Mensaje de trade completado |
| Comandos Telegram | 2 | `/status`, `/balance`, `/pause`, `/resume` |

**Nuevo desarrollo:** Telegram Bot no existe en Concessio

### Día 21: Execution Engine
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `OrderExecutor` | 3 | Ejecutar órdenes en Binance |
| `Trade` entity | 2 | Guardar trades en DB |
| `TradeRepository` | 1 | Queries tenant-aware |
| WebSocket monitoreo | 2 | Seguimiento de precios para SL/TP |

**Entregable Semana 3:**
- Auto-switch funcionando
- Risk management hardcoded
- Notificaciones Telegram activas
- Órdenes ejecutándose en Binance Testnet

---

## Semana 4: Portfolio + Dashboard API + Deploy

### Días 22-23: Portfolio Service
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `PortfolioService` | 3 | Calcular balance, P&L, equity curve |
| `PerformanceTracker` | 2 | Estadísticas por estrategia |
| `TradeRepository` queries | 2 | P&L diario, semanal, mensual |
| `PortfolioSnapshot` | 1 | DTO para dashboard |

### Días 24-25: API REST para Frontend
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| `AuthController` | 2 | Login JWT + tenant |
| `PortfolioController` | 2 | GET /api/v1/portfolio |
| `TradeController` | 2 | GET /api/v1/trades (paginado) |
| `MarketController` | 2 | GET /api/v1/market/current |
| `ConfigController` | 2 | GET/PUT /api/v1/config |
| `WebSocketController` | 2 | `/ws/market` streaming |

Ver detalle completo en `API_DOC_TRADING_BOT.md`

### Día 26-27: Testing + Paper Trading
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| Integration tests | 3 | End-to-end con Testnet |
| Paper trading | 2 | Validar estrategias con fondos virtuales |
| Load testing | 1 | JMeter para WebSocket |

### Día 28: Deploy + Documentación
| Tarea | Horas | Archivos/Código |
|-------|-------|-----------------|
| Deploy VPS | 2 | Docker Compose en producción |
| SSL + Nginx | 2 | HTTPS + reverse proxy |
| Monitoreo | 1 | Logs + alertas |
| README + Runbook | 2 | Documentación operación |

**Entregable Semana 4:**
- API REST completa documentada
- Bot operando en Testnet
- Dashboard API lista para frontend
- Deploy en VPS productivo

---

## Resumen de Esfuerzo

| Componente | Horas | Reutilización Concessio |
|------------|-------|------------------------|
| Infra + Multi-tenant | 12 | 70% (TenantContext, JWT) |
| Strategy Engine | 16 | 40% (Patrón Strategy) |
| Binance Connector | 10 | 0% (nuevo) |
| Risk + Execution | 10 | 10% (base services) |
| Notificaciones | 6 | 0% (nuevo) |
| Portfolio + API | 8 | 30% (repositorios) |
| Testing + Deploy | 8 | 50% (Docker, tests) |
| **TOTAL** | **56h** | **~35% promedio** |

**Inversión Setup:** $3,160 USD (56h × $60/$50 mix)
**Mensualidad:** $140 USD/mes (VPS + Infra)

---

## Checklist Pre-Código

- [ ] Plan de acción aprobado
- [ ] API_DOC_TRADING_BOT.md creada
- [ ] Repositorio Git inicializado
- [ ] VPS provisionado
- [ ] Binance Testnet API Keys listas
- [ ] Bot de Telegram creado (BotFather)
- [ ] Especificación frontend lista (PROMPT_LOVABLE_TRADING_BOT.md)

---

## Post-Código: Integración Frontend

| Tarea | Responsable | Dependencia |
|-------|-------------|-------------|
| Frontend Vue implementado | Lovable AI | API_DOC lista |
| Integración API | Desarrollador | Backend deployado |
| Testing end-to-end | QA | Frontend + Backend |
| Go-live Producción | DevOps | Testing OK |

---

*QL Software Studio*  
*"Arquitectura sólida, trading inteligente"*
