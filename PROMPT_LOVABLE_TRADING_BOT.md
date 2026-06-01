# TRADING BOT DASHBOARD - VUE 3 + TYPESCRIPT + TAILWIND CSS

## REQUIREMENTS (R)

### Propósito
Dashboard web para un trading bot de criptomonedas (Binance) con modo multi-tenant SaaS. Los usuarios configuran sus API keys, ven su portfolio en tiempo real, y reciben notificaciones de cambios de estrategia.

### Features Core
1. **Authentication**: Login JWT con selección de tenant (multi-tenant)
2. **Dashboard Principal**: 
   - Portfolio actual (USD)
   - P&L diario/semanal/mensual
   - Gráfica de equity curve
   - Estado actual del bot (activo/pausado)
   - Estrategia actual (Range/Trend/Scalping)
3. **Trading View**:
   - Lista de trades abiertos/cerrados
   - Señales generadas (BUY/SELL/HOLD)
   - Historial de operaciones con filtros
4. **Market Data**:
   - Precio actual del par (BTCUSDT)
   - Indicadores en tiempo real (ADX, RSI, EMAs)
   - Estado del mercado detectado (Ranging/Trend Up/Trend Down)
5. **Configuration**:
   - Formulario API Keys Binance (encriptado)
   - Selección de estrategia (auto-switch o manual)
   - Risk settings (max position size, daily loss limit)
   - Notificaciones Telegram (chat ID)
6. **Admin Panel** (solo ADMIN role):
   - Lista de tenants/usuarios
   - Suscripciones por plan (Starter/Pro/Enterprise)
   - Métricas de uso global

### Stack Tecnológico Obligatorio
- Vue 3.4+ con Composition API (`<script setup>`)
- TypeScript 5.0+ (strict mode)
- Vue Router 4 (hash mode)
- Pinia 2 (stores)
- Tailwind CSS 3.4
- shadcn-vue (componentes UI)
- Recharts o Chart.js (gráficas)
- Axios + interceptores JWT
- Vite (build tool)

## OBJECTIVES (O)

### Métricas de Éxito del Frontend
1. First Contentful Paint < 1.5s
2. Time to Interactive < 3s
3. 100% type-safe (no `any` implícitos)
4. Responsive: Mobile-first, desktop-enhanced
5. WebSocket reconnect automático para precios en tiempo real

### Estructura de Carpetas (Screaming Architecture)
```
src/
├── features/
│   ├── auth/
│   │   ├── components/LoginForm.vue
│   │   ├── stores/authStore.ts
│   │   └── types/AuthTypes.ts
│   ├── dashboard/
│   │   ├── components/PortfolioCard.vue
│   │   ├── components/StrategyStatus.vue
│   │   ├── components/EquityChart.vue
│   │   └── pages/DashboardPage.vue
│   ├── trades/
│   │   ├── components/TradeTable.vue
│   │   ├── components/TradeFilters.vue
│   │   └── types/Trade.ts
│   ├── market/
│   │   ├── components/PriceTicker.vue
│   │   ├── components/IndicatorPanel.vue
│   │   └── composables/useMarketData.ts
│   └── config/
│       ├── components/ApiKeyForm.vue
│       ├── components/RiskSettings.vue
│       └── stores/configStore.ts
├── shared/
│   ├── api/client.ts (axios instance)
│   ├── api/interceptors.ts (JWT refresh)
│   ├── components/ui/ (shadcn base)
│   └── composables/useWebSocket.ts
└── App.vue
```

### Paleta de Colores (Dark Trading Theme)
- Background: `#0B0E11` (Binance dark)
- Surface: `#1E2329` (card backgrounds)
- Primary: `#F0B90B` (Binance yellow - CTAs)
- Success: `#0ECB81` (profit)
- Danger: `#F6465D` (loss)
- Text Primary: `#EAECEF`
- Text Secondary: `#848E9C`

## CONSTRAINTS (C)

### Restricciones Técnicas
1. **Vue 3 Composition API obligatorio** - no Options API
2. **TypeScript strict** - todas las props tipadas, stores tipados
3. **Pinia para estado** - no Vuex
4. **shadcn-vue componentes** - usar donde sea posible (Button, Card, Dialog, Input, Select, Badge, Table, Tabs)
5. **Tailwind solo** - no CSS modules, no styled-components
6. **WebSocket**: Reconexión automática con backoff exponencial
7. **Mobile-responsive**: Debe funcionar en 375px+

### Modelos de Datos (Backend API Contracts)
```typescript
// Trade
interface Trade {
  id: number;
  symbol: string;
  type: 'BUY' | 'SELL';
  entryPrice: number;
  exitPrice?: number;
  quantity: number;
  pnl?: number;
  pnlPercent?: number;
  strategyUsed: 'RANGE' | 'TREND' | 'SCALPING';
  status: 'OPEN' | 'CLOSED' | 'CANCELLED';
  entryTime: string; // ISO 8601
  exitTime?: string;
}

// Portfolio
interface PortfolioSnapshot {
  totalBalance: number;
  availableBalance: number;
  dailyPnL: number;
  dailyPnLPercent: number;
  weeklyPnL: number;
  monthlyPnL: number;
  openPositions: number;
}

// Market Condition
interface MarketCondition {
  condition: 'RANGING' | 'TREND_UP' | 'TREND_DOWN' | 'SCALPING_OPPORTUNITY' | 'NEUTRAL';
  adx: number;
  rsi: number;
  ema9: number;
  ema21: number;
  currentPrice: number;
  timestamp: string;
}

// Strategy Configuration
interface StrategyConfig {
  mode: 'AUTO_SWITCH' | 'MANUAL';
  selectedStrategy?: 'RANGE' | 'TREND' | 'SCALPING';
  riskSettings: {
    maxPositionSizePercent: number; // 5 default
    maxDailyLossPercent: number; // 10 default
    stopLossPercent: number; // 3 default
  };
}

// User/Tenant
interface BotUser {
  id: number;
  email: string;
  plan: 'STARTER' | 'PRO' | 'ENTERPRISE';
  telegramChatId?: string;
  active: boolean;
}
```

### API Endpoints (Base: `/api/v1`)
- POST `/auth/login` → JWT + tenant_id
- GET `/portfolio` → PortfolioSnapshot
- GET `/trades?status=&page=&size=` → Paginated<Trade>
- GET `/market/current` → MarketCondition (WebSocket preferido)
- GET `/config` → StrategyConfig
- PUT `/config` → StrategyConfig
- POST `/config/apikeys` → {encryptedApiKey, encryptedSecret}
- WebSocket `/ws/market` → stream precios en tiempo real

## EXAMPLES (E)

### Ejemplo: Dashboard Layout
```vue
<!-- DashboardPage.vue -->
<template>
  <div class="min-h-screen bg-[#0B0E11] text-[#EAECEF]">
    <TopNav :user="authStore.user" />
    
    <main class="container mx-auto p-4 space-y-6">
      <!-- Portfolio Overview -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <PortfolioCard 
          title="Balance Total" 
          :value="portfolio.totalBalance" 
          format="currency"
          :change="portfolio.dailyPnLPercent"
        />
        <PortfolioCard 
          title="P&L Diario" 
          :value="portfolio.dailyPnL" 
          format="currency"
          :change="portfolio.dailyPnLPercent"
          :positive="portfolio.dailyPnL >= 0"
        />
        <StrategyStatus 
          :condition="market.condition"
          :strategy="config.selectedStrategy"
          :is-auto="config.mode === 'AUTO_SWITCH'"
        />
        <BotControl 
          :is-active="botActive"
          @toggle="toggleBot"
        />
      </div>
      
      <!-- Charts -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <EquityChart class="lg:col-span-2" :data="equityHistory" />
        <MarketIndicators :indicators="market" />
      </div>
      
      <!-- Recent Trades -->
      <TradeTable :trades="recentTrades" />
    </main>
  </div>
</template>

<script setup lang="ts">
import { usePortfolioStore } from '@/features/dashboard/stores/portfolioStore'
import { useMarketStore } from '@/features/market/stores/marketStore'
import { useConfigStore } from '@/features/config/stores/configStore'

const portfolioStore = usePortfolioStore()
const marketStore = useMarketStore()
const configStore = useConfigStore()

// WebSocket connection for real-time data
const { data: marketData } = useWebSocket('/ws/market')

const portfolio = computed(() => portfolioStore.snapshot)
const market = computed(() => marketStore.currentCondition)
const config = computed(() => configStore.config)
</script>
```

### Ejemplo: Componente StrategyStatus
```vue
<!-- StrategyStatus.vue -->
<template>
  <Card class="bg-[#1E2329] border-[#2B3139]">
    <CardHeader class="pb-2">
      <CardDescription class="text-[#848E9C]">Estrategia Actual</CardDescription>
      <div class="flex items-center justify-between">
        <CardTitle class="text-lg text-[#EAECEF] flex items-center gap-2">
          <Badge 
            :variant="conditionVariant"
            class="text-xs"
          >
            {{ displayCondition }}
          </Badge>
          <span v-if="isAuto" class="text-xs text-[#848E9C]">(Auto)</span>
        </CardTitle>
        <Activity class="h-4 w-4 text-[#F0B90B]" v-if="isAuto" />
      </div>
    </CardHeader>
    <CardContent>
      <p class="text-sm text-[#848E9C]">{{ strategyDescription }}</p>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { Card, CardHeader, CardDescription, CardTitle, CardContent, Badge } from '@/shared/components/ui'
import { Activity } from 'lucide-vue-next'
import { computed } from 'vue'

const props = defineProps<{
  condition: MarketCondition['condition']
  strategy?: string
  isAuto: boolean
}>()

const conditionVariant = computed(() => ({
  'RANGING': 'secondary',
  'TREND_UP': 'success',
  'TREND_DOWN': 'destructive',
  'SCALPING_OPPORTUNITY': 'warning',
  'NEUTRAL': 'outline'
}[props.condition]))

const displayCondition = computed(() => ({
  'RANGING': 'Rango',
  'TREND_UP': 'Tendencia Alcista',
  'TREND_DOWN': 'Tendencia Bajista',
  'SCALPING_OPPORTUNITY': 'Scalping',
  'NEUTRAL': 'Neutral'
}[props.condition]))
</script>
```

### Ejemplo: Store Pinia tipado
```typescript
// features/dashboard/stores/portfolioStore.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/shared/api/client'

export const usePortfolioStore = defineStore('portfolio', () => {
  // State
  const snapshot = ref<PortfolioSnapshot | null>(null)
  const equityHistory = ref<EquityPoint[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const totalPnL = computed(() => snapshot.value?.dailyPnL ?? 0)
  const isPositive = computed(() => totalPnL.value >= 0)

  // Actions
  async function fetchPortfolio() {
    loading.value = true
    try {
      const { data } = await api.get<PortfolioSnapshot>('/portfolio')
      snapshot.value = data
    } catch (err) {
      error.value = 'Failed to load portfolio'
    } finally {
      loading.value = false
    }
  }

  return {
    snapshot,
    equityHistory,
    loading,
    error,
    totalPnL,
    isPositive,
    fetchPortfolio
  }
})
```

### Ejemplo: WebSocket composable
```typescript
// shared/composables/useWebSocket.ts
import { ref, onMounted, onUnmounted } from 'vue'

export function useWebSocket<T>(url: string) {
  const data = ref<T | null>(null)
  const connected = ref(false)
  const error = ref<string | null>(null)
  
  let ws: WebSocket | null = null
  let reconnectTimer: number | null = null
  let reconnectAttempts = 0
  const MAX_RECONNECT = 5

  const connect = () => {
    ws = new WebSocket(`${import.meta.env.VITE_WS_URL}${url}`)
    
    ws.onopen = () => {
      connected.value = true
      reconnectAttempts = 0
    }
    
    ws.onmessage = (event) => {
      data.value = JSON.parse(event.data)
    }
    
    ws.onclose = () => {
      connected.value = false
      if (reconnectAttempts < MAX_RECONNECT) {
        reconnectTimer = window.setTimeout(() => {
          reconnectAttempts++
          connect()
        }, Math.min(1000 * 2 ** reconnectAttempts, 30000))
      }
    }
    
    ws.onerror = (err) => {
      error.value = 'WebSocket error'
    }
  }

  onMounted(connect)
  onUnmounted(() => {
    ws?.close()
    if (reconnectTimer) clearTimeout(reconnectTimer)
  })

  return { data, connected, error }
}
```

---

## Instrucciones de Build para Lovable

1. Crear proyecto Vue 3 + TypeScript + Vite
2. Instalar dependencias: `npm install pinia vue-router axios tailwindcss @tailwindcss/forms`
3. Instalar shadcn-vue: `npx shadcn-vue@latest init`
4. Agregar componentes: `npx shadcn-vue@latest add button card badge dialog input select table tabs`
5. Configurar Tailwind con colores custom (Binance dark theme)
6. Implementar features en orden: auth → dashboard → market → trades → config
