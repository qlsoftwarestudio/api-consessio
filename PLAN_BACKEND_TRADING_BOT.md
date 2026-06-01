# 🚀 Plan de Acción Backend - QL Trading Bot

**Análisis basado en:** Proyecto Concessio CRM existente (Spring Boot, PostgreSQL, Railway, Docker)

**Duración estimada:** 4 semanas (1 desarrollador full-time)

---

## 📊 Análisis del Proyecto Base (Concessio CRM)

### ✅ Componentes REUTILIZABLES (Cópialos tal cual)

| Componente | Ubicación Actual | Reutilización |
|------------|------------------|---------------|
| **TenantContext** | `com.concessio.crm.tenant.TenantContext` | ✅ 100% - ThreadLocal multi-tenant |
| **TenantContextFilter** | `com.concessio.crm.auth.security.TenantContextFilter` | ✅ 100% - JWT + tenant isolation |
| **JwtService** | `com.concessio.crm.auth.service.JwtService` | ✅ 95% - Agregar `userId` al JWT |
| **SecurityConfig** | `com.concessio.crm.config.SecurityConfig` | ✅ 80% - Adaptar endpoints Trading |
| **Strategy Pattern** | `com.concessio.crm.quotation.calculation` | ✅ 90% - Base para trading strategies |
| **Dockerfile** | `/Dockerfile` | ✅ 100% - Multi-stage build |
| **docker-compose** | `/docker-compose.yml` | ✅ 90% - Cambiar nombre DB |
| **run.sh** | `/run.sh` | ✅ 90% - Cambiar nombres |

### ❌ Componentes NUEVOS (Crear desde cero)

| Componente | Razón |
|------------|-------|
| Binance API Client | No existe en Concessio |
| Trading Strategies (3) | Lógica de trading específica |
| Market Data Service | WebSocket + REST Binance |
| Trade Engine | Ejecutar órdenes, gestionar positions |
| Risk Manager | Lógica de stops, límites diarios |
| Notification Service | Telegram Bot API |
| User-Bot Model | Extensión de User con campos de trading |
| Subscription/Plans | Stripe integration, límites por plan |

---

## 🗓️ Cronograma de 4 Semanas

### Semana 1: Foundation + Autenticación Multi-Usuario

#### Día 1-2: Setup Proyecto

```bash
# 1. Copiar estructura base
cp -r giamma-360-backend/ trading-bot-backend/
cd trading-bot-backend

# 2. Renombrar package base
# com.concessio.crm → com.ql.tradingbot

# 3. Actualizar build.gradle
# Cambiar nombre, descripción, group
```

**Tareas:**
- [ ] Renombrar packages a `com.ql.tradingbot`
- [ ] Actualizar `application.yml` (nombre, puertos)
- [ ] Adaptar `Dockerfile` (mismo, solo cambiar labels)
- [ ] Adaptar `docker-compose.yml` (nombre DB: `tradingbot`)
- [ ] Crear nuevo `run.sh`

**Archivos a modificar:**
- `build.gradle` - Group: `com.ql`, nombre: `trading-bot`
- `settings.gradle` - `rootProject.name = 'trading-bot'`
- `application.yml` - `spring.application.name: tradingbot`

#### Día 3-4: Modelo de Datos Base

**Extender el modelo Tenant actual:**

```java
// Nuevo: User (reemplaza User de Concessio)
@Entity
@Table(name = "bot_users")
public class BotUser {
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    
    private String email;
    private String password;
    private String name;
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // ADMIN, TRADER
    
    // Trading specific
    private boolean apiKeysConfigured;
    private String encryptedApiKey;
    private String encryptedApiSecret;
    private boolean testnet;
    
    private boolean botActive;
    private String tradingPair;
    private String currentStrategy;
    private String mode; // AUTO_SWITCH, MANUAL
    
    private String telegramChatId;
    private boolean notificationsEnabled;
    
    // Plan limits
    private BigDecimal maxCapital;
    private BigDecimal currentCapital;
}
```

**Nuevo: Trade Entity**
```java
@Entity
@Table(name = "trades")
public class Trade {
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private BotUser user;
    
    private Long tenantId; // Para queries rápidas
    
    private String symbol;
    private TradeType type;
    private TradeStatus status;
    
    private BigDecimal entryPrice;
    private BigDecimal exitPrice;
    private BigDecimal quantity;
    private BigDecimal investedAmount;
    
    private BigDecimal pnl;
    private BigDecimal pnlPercent;
    
    private String strategyUsed;
    private String marketCondition;
    
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
}
```

**Nuevo: Tenant con Planes**
```java
// Extender Tenant actual
@Entity
public class Tenant {
    // ... campos existentes ...
    
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan plan; // STARTER, PRO, ENTERPRISE
    
    private Integer maxUsers;
    private BigDecimal monthlyFee;
    
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    
    private boolean active;
    private LocalDateTime nextBillingDate;
}
```

**Archivos a crear:**
- `model/BotUser.java`
- `model/Trade.java`
- `model/MarketCondition.java`
- `model/StrategySwitch.java`
- `model/Notification.java`
- `model/RiskEvent.java`
- `enums/SubscriptionPlan.java`
- `enums/TradeType.java`, `TradeStatus.java`, etc.

#### Día 5: Autenticación Multi-Usuario

**Extender JwtService:**
```java
public String generateToken(String email, Long tenantId, Long userId) {
    return Jwts.builder()
        .subject(email)
        .claim("tenantId", tenantId)
        .claim("userId", userId)  // NUEVO
        .claim("plan", plan)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
}
```

**Nuevo endpoint: POST /auth/select-user**
```java
@PostMapping("/select-user")
public ResponseEntity<?> selectUser(@RequestBody SelectUserRequest request) {
    // Validar que el usuario pertenece al tenant
    // Generar nuevo token con userId específico
    // Para Enterprise: permitir seleccionar entre múltiples usuarios
}
```

**Tareas:**
- [ ] Extender JwtService con `userId`
- [ ] Crear UserSelectionService
- [ ] Actualizar AuthController
- [ ] Tests de autenticación multi-usuario

---

### Semana 2: Binance Integration + Trading Engine Core

#### Día 6-8: Binance API Client

**Nuevo: Servicio de Conexión Binance**
```java
@Service
public class BinanceApiService {
    
    @Value("${binance.api.url}")
    private String baseUrl;
    
    // REST API
    public AccountInfo getAccountInfo(String apiKey, String apiSecret) {
        // Firma HMAC SHA256
        // GET /api/v3/account
    }
    
    public OrderResponse placeOrder(String apiKey, String apiSecret, OrderRequest order) {
        // POST /api/v3/order
    }
    
    public TickerPrice getPrice(String symbol) {
        // GET /api/v3/ticker/price?symbol=BTCUSDT
    }
    
    public KlineData getKlines(String symbol, String interval, int limit) {
        // GET /api/v3/klines
    }
}
```

**Nuevo: WebSocket Client para Market Data**
```java
@Component
public class BinanceWebSocketClient {
    
    private WebSocketClient client;
    
    @EventListener(ApplicationReadyEvent.class)
    public void connect() {
        // Conectar a wss://stream.binance.com:9443/ws/btcusdt@kline_1m
        // Reconexión automática
    }
    
    @OnMessage
    public void onMessage(String message) {
        // Parsear JSON
        // Actualizar MarketDataCache
    }
}
```

**Configuración en `application.yml`:**
```yaml
binance:
  api:
    url: https://api.binance.com
    testnet-url: https://testnet.binance.vision
  websocket:
    url: wss://stream.binance.com:9443/ws
```

**Tareas:**
- [ ] Implementar firma HMAC para API privada
- [ ] WebSocket client con reconexión
- [ ] Manejo de rate limits (1200 req/min)
- [ ] Tests con Testnet

#### Día 9-10: Trading Strategies (Pattern Strategy)

**Reutilizar estructura de Concessio:**
```java
// Base: QuotationCalculationStrategy → TradingStrategy
public interface TradingStrategy {
    String getName(); // "RANGE", "TREND", "SCALPING"
    
    boolean shouldEnter(MarketData data);
    boolean shouldExit(Trade trade, MarketData data);
    
    BigDecimal calculatePositionSize(AccountInfo account, BigDecimal riskPercent);
    BigDecimal calculateStopLoss(MarketData data);
    BigDecimal calculateTakeProfit(MarketData data);
}

@Component
public class RangeStrategy implements TradingStrategy {
    @Override
    public String getName() { return "RANGE"; }
    
    @Override
    public boolean shouldEnter(MarketData data) {
        // RSI < 35 (oversold) y precio cerca de soporte
        return data.getRsi() < 35 && data.isNearSupport();
    }
    // ... resto de métodos
}
```

**Nuevo: StrategyContext**
```java
@Component
public class StrategyContext {
    
    private final Map<String, TradingStrategy> strategies;
    
    public StrategyContext(List<TradingStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                TradingStrategy::getName,
                Function.identity()
            ));
    }
    
    public TradingStrategy getStrategy(String name) {
        return strategies.get(name);
    }
    
    public String detectMarketCondition(MarketData data) {
        // Lógica de detección ADX, EMA, RSI
        // Devuelve: RANGING, TREND_UP, TREND_DOWN, SCALPING_OPPORTUNITY
    }
}
```

**Tareas:**
- [ ] Implementar RangeStrategy
- [ ] Implementar TrendStrategy
- [ ] Implementar ScalpingStrategy
- [ ] Tests unitarios con datos históricos

---

### Semana 3: Trade Engine + Risk Management

#### Día 11-13: Trade Engine Core

**Nuevo: TradeEngineService**
```java
@Service
public class TradeEngineService {
    
    private final BinanceApiService binanceApi;
    private final StrategyContext strategyContext;
    private final TradeRepository tradeRepository;
    private final RiskManager riskManager;
    
    @Scheduled(fixedDelay = 60000) // Cada 1 minuto
    public void scanForOpportunities() {
        // Para cada usuario con botActive=true:
        // 1. Obtener market data
        // 2. Evaluar estrategia activa
        // 3. Si hay señal, validar con RiskManager
        // 4. Ejecutar orden si pasa validaciones
    }
    
    @Scheduled(fixedDelay = 30000) // Cada 30 segundos
    public void monitorOpenTrades() {
        // Para cada trade OPEN:
        // 1. Obtener precio actual
        // 2. Verificar si tocó SL o TP
        // 3. Cerrar trade si es necesario
    }
    
    public Trade executeEntry(BotUser user, String strategy, MarketData data) {
        // 1. Calcular tamaño de posición
        // 2. Calcular SL y TP
        // 3. Enviar orden a Binance
        // 4. Guardar trade en DB
        // 5. Notificar al usuario
    }
    
    public Trade executeExit(Trade trade, ExitReason reason) {
        // 1. Enviar orden de venta a Binance
        // 2. Calcular P&L final
        // 3. Actualizar trade
        // 4. Notificar al usuario
    }
}
```

**Nuevo: RiskManager**
```java
@Service
public class RiskManager {
    
    public boolean canOpenTrade(BotUser user, BigDecimal positionSize) {
        // Verificar:
        // - Daily loss limit no alcanzado
        // - Max positions no excedido
        // - Position size <= maxPositionSizePercent del balance
        // - API keys válidas
        return true; // o false con razón
    }
    
    public boolean shouldPauseBot(BotUser user) {
        // Verificar si se alcanzó daily loss limit
        BigDecimal dailyLoss = calculateDailyLoss(user);
        return dailyLoss.compareTo(user.getMaxDailyLoss()) >= 0;
    }
}
```

**Tareas:**
- [ ] Implementar ciclo de vida del trade
- [ ] Manejo de errores de Binance (insufficient balance, API error)
- [ ] Audit logging de todas las operaciones
- [ ] Tests de integración con Testnet

#### Día 14-15: Notificaciones Telegram

**Nuevo: TelegramNotificationService**
```java
@Service
public class TelegramNotificationService {
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    public void sendTradeNotification(BotUser user, Trade trade, String type) {
        String message = buildTradeMessage(trade, type);
        sendMessage(user.getTelegramChatId(), message);
    }
    
    public void sendStrategySwitch(BotUser user, String oldStrategy, String newStrategy) {
        String message = String.format(
            "🔄 **Estrategia Cambiada**\n\n" +
            "De: %s\n" +
            "A: %s\n" +
            "Hora: %s",
            oldStrategy, newStrategy, LocalDateTime.now()
        );
        sendMessage(user.getTelegramChatId(), message);
    }
    
    private void sendMessage(String chatId, String message) {
        // POST https://api.telegram.org/bot{token}/sendMessage
    }
}
```

**Tareas:**
- [ ] Crear bot en BotFather
- [ ] Implementar templates de mensajes
- [ ] Webhook para recibir comandos (/status, /balance)
- [ ] Cola async para no bloquear (Redis/Simple Queue)

---

### Semana 4: API REST + Testing + Deployment

#### Día 16-18: API Controllers

**Nuevo: Controllers según API_DOC_TRADING_BOT.md**

```java
@RestController
@RequestMapping("/api/v1")
public class PortfolioController {
    
    @GetMapping("/portfolio")
    public PortfolioResponse getPortfolio(
        @RequestHeader("X-Tenant-ID") Long tenantId,
        @RequestHeader("X-User-ID") Long userId
    ) {
        // Usar TenantContext para aislamiento
        TenantContext.setCurrentTenant(tenantId);
        // ... lógica
    }
}

@RestController
@RequestMapping("/api/v1")
public class TradeController {
    
    @GetMapping("/trades")
    public Page<Trade> getTrades(
        @RequestHeader("X-Tenant-ID") Long tenantId,
        @RequestParam(required = false) TradeStatus status,
        Pageable pageable
    ) {
        // Filtrar por tenantId + userId
    }
    
    @PostMapping("/trades/{id}/close")
    public Trade closeTradeManually(@PathVariable Long id) {
        // Cerrar trade manualmente
    }
}

@RestController
@RequestMapping("/api/v1")
public class ConfigController {
    
    @PostMapping("/config/apikeys")
    public void configureApiKeys(@RequestBody ApiKeysRequest request) {
        // Validar keys contra Binance
        // Guardar encriptadas (AES-256)
    }
    
    @PostMapping("/config/toggle")
    public BotStatus toggleBot(@RequestBody ToggleRequest request) {
        // Activar/desactivar bot
    }
}
```

**Tareas:**
- [ ] Implementar todos los endpoints de API_DOC
- [ ] Validaciones con Jakarta Validation
- [ ] Manejo de errores global (@ControllerAdvice)
- [ ] Rate limiting por tenant (Bucket4j)

#### Día 19-20: Testing + Deploy Railway

**Tests:**
```java
@SpringBootTest
@AutoConfigureMockMvc
public class TradingIntegrationTest {
    
    @Test
    void testCompleteTradeLifecycle() {
        // 1. Crear usuario con API keys testnet
        // 2. Activar bot
        // 3. Simular señal de entrada
        // 4. Verificar orden enviada a Binance testnet
        // 5. Simular SL/TP
        // 6. Verificar cierre
    }
}
```

**Railway Deployment:**
```bash
# 1. Crear railway.json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "Dockerfile"
  },
  "deploy": {
    "healthcheckPath": "/actuator/health",
    "healthcheckTimeout": 100,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}

# 2. Variables de entorno en Railway
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.DATABASE_USERNAME}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.DATABASE_PASSWORD}}
JWT_SECRET=${{secret(JWT_SECRET)}}
BINANCE_API_KEY=${{secret(BINANCE_API_KEY)}}
TELEGRAM_BOT_TOKEN=${{secret(TELEGRAM_BOT_TOKEN)}}
```

**Tareas:**
- [ ] Tests unitarios (strategies, risk)
- [ ] Tests de integración (Binance testnet)
- [ ] Configurar Railway project
- [ ] Crear Postgres en Railway
- [ ] Deploy automático desde GitHub
- [ ] Health check endpoint

---

## 🏗️ Estructura de Carpetas Final

```
trading-bot-backend/
├── src/main/java/com/ql/tradingbot/
│   ├── TradingBotApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java          (de Concessio, adaptado)
│   │   ├── WebSocketConfig.java         (nuevo)
│   │   └── SchedulerConfig.java         (nuevo)
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   ├── service/JwtService.java      (extendido con userId)
│   │   ├── service/AuthService.java
│   │   └── security/TenantContextFilter.java  (de Concessio)
│   ├── tenant/
│   │   ├── model/Tenant.java            (extendido con plan)
│   │   ├── repository/TenantRepository.java
│   │   ├── service/TenantService.java
│   │   └── TenantContext.java           (de Concessio)
│   ├── user/
│   │   ├── model/BotUser.java           (nuevo)
│   │   ├── repository/UserRepository.java
│   │   └── service/UserService.java
│   ├── trading/
│   │   ├── controller/
│   │   │   ├── PortfolioController.java
│   │   │   ├── TradeController.java
│   │   │   └── ConfigController.java
│   │   ├── service/
│   │   │   ├── TradeEngineService.java
│   │   │   ├── RiskManager.java
│   │   │   ├── PortfolioService.java
│   │   │   └── MarketDataService.java
│   │   ├── binance/
│   │   │   ├── BinanceApiService.java
│   │   │   ├── BinanceWebSocketClient.java
│   │   │   └── dto/*.java
│   │   ├── strategy/
│   │   │   ├── TradingStrategy.java
│   │   │   ├── StrategyContext.java     (pattern de Concessio)
│   │   │   ├── RangeStrategy.java
│   │   │   ├── TrendStrategy.java
│   │   │   └── ScalpingStrategy.java
│   │   └── model/
│   │       ├── Trade.java
│   │       ├── MarketCondition.java
│   │       └── StrategySwitch.java
│   ├── notification/
│   │   └── TelegramNotificationService.java
│   └── exception/
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-prod.yml
├── Dockerfile                           (de Concessio)
├── docker-compose.yml                   (adaptado)
├── railway.json                         (nuevo)
├── run.sh                               (adaptado)
└── build.gradle                         (adaptado)
```

---

## 📦 Dependencias Nuevas (build.gradle)

```gradle
dependencies {
    // Spring Boot base (ya existe en Concessio)
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // NUEVOS para Trading Bot
    implementation 'org.springframework.boot:spring-boot-starter-websocket'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'  // WebClient
    
    // Encryption (API keys)
    implementation 'org.springframework.security:spring-security-crypto'
    
    // Technical Analysis (opcional, o calcular manual)
    implementation 'org.ta4j:ta4j-core:0.15'
    
    // Rate Limiting
    implementation 'com.github.vladimir-bukhtoyarov:bucket4j-core:8.1.0'
    implementation 'com.github.vladimir-bukhtoyarov:bucket4j-jcache:8.1.0'
    
    // Database (ya existe)
    runtimeOnly 'org.postgresql:postgresql'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'io.projectreactor:reactor-test'
}
```

---

## 🔐 Security Checklist

- [ ] **API Keys encriptadas**: AES-256 con key derivada de JWT_SECRET
- [ ] **Validación firma Binance**: HMAC SHA256
- [ ] **Rate limiting**: 1200 req/min por IP
- [ ] **Tenant isolation**: Todas las queries filtran por tenantId
- [ ] **User isolation**: Users solo ven sus propios trades
- [ ] **HTTPS obligatorio**: Redirect HTTP→HTTPS en prod
- [ ] **CORS configurado**: Solo dominios permitidos
- [ ] **Input validation**: @Valid en todos los DTOs
- [ ] **Audit logging**: Todas las operaciones de trading logueadas

---

## 🚀 Comandos para Empezar

```bash
# 1. Clonar y preparar
cd /home/emilio/Escritorio/quilodranEmilioBackUp/emilioquilodran/qlsoftwarestudio/
cp -r giamma-360-backend trading-bot-backend
cd trading-bot-backend

# 2. Renombrar packages
find src -type f -name "*.java" -exec sed -i 's/com.concessio.crm/com.ql.tradingbot/g' {} +
find src -type d -name "concessio" -exec rename 's/concessio/tradingbot/' {} +

# 3. Actualizar build.gradle
sed -i 's/concessio/tradingbot/g' build.gradle settings.gradle

# 4. Actualizar application.yml
sed -i 's/concessio/tradingbot/g' src/main/resources/application.yml

# 5. Levantar local
docker-compose up -d

# 6. Test
./gradlew test

# 7. Deploy Railway (después de configurar proyecto)
railway login
railway link
railway up
```

---

## 📊 Estimación de Esfuerzo

| Componente | Días | Complejidad |
|------------|------|-------------|
| Setup + Auth multi-usuario | 3 | Media |
| Modelo de datos | 2 | Baja |
| Binance API + WebSocket | 3 | Alta |
| Trading Strategies | 2 | Alta |
| Trade Engine | 3 | Alta |
| Risk Management | 2 | Media |
| Notificaciones Telegram | 2 | Media |
| API REST | 2 | Media |
| Testing + Deploy | 3 | Media |
| **TOTAL** | **20 días** | **4 semanas** |

**Nota:** Con código de Concessio como base, ahorras ~40% del tiempo (ya tienes auth, tenant, JPA, Docker, etc.)

---

*Plan basado en análisis del proyecto Concessio CRM existente. Ajustar según requerimientos específicos.*
