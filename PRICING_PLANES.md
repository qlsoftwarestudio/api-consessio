# 💰 Planes y Precios - QL Trading Bot

**Fecha:** Abril 2026  
**Modelo:** SaaS B2C/B2B - Suscripción mensual + Setup inicial

---

## 📊 Resumen de Planes

| Plan | Precio/mes | Setup Fee | Pares de Trading | Usuarios/Bots | Capital Máx |
|------|-----------|-----------|------------------|---------------|-------------|
| **Starter** | $29 | $49 | 1 par | 1 | $500 |
| **Pro** | $79 | $99 | 2 pares + AUTO_BEST | 1 | $5,000 |
| **Enterprise** | $199 | $199 | 3 pares + MULTI_PARALLEL | 3 | Ilimitado |

---

## 🥉 Plan Starter - $29/mes

### Ideal para:
- Usuarios individuales que quieren probar el bot
- Capital inicial de $100-$500
- Primerizos en trading automatizado

### Características:
- ✅ **1 par de trading** (ej: BTCUSDT)
- ✅ **1 usuario/bot** por cuenta
- ✅ **Modo FIXED** (operar solo el par configurado)
- ✅ Estrategias: Range, Trend, Scalping
- ✅ Auto-switch de estrategias
- ✅ Risk management básico
- ✅ Notificaciones Telegram/Email
- ✅ Testnet (paper trading) ilimitado
- ✅ Soporte: Email (48h respuesta)
- ✅ Dashboard web básico

### Límites:
- ❌ Sin modo AUTO_BEST (no escanea otros pares)
- ❌ Sin operación multi-paralelo
- ❌ Máximo $500 de capital operativo
- ❌ 1 operación abierta máximo
- ❌ Sin API de acceso

### Precio:
- **Mensual:** $29/mes
- **Anual:** $290/año (2 meses gratis = 17% off)
- **Setup fee:** $49 (único, primera vez)

---

## 🥈 Plan Pro - $79/mes

### Ideal para:
- Traders serios con capital $1,000-$5,000
- Quieren maximizar oportunidades con multi-par
- Necesitan operar cuando hay movimiento en cualquier crypto

### Características (todo Starter +):
- ✅ **2 pares de trading** simultáneos
- ✅ **Modo AUTO_BEST** (destacado 🌟)
  - Bot escanea ambos pares cada 5 minutos
  - Opera el que tenga mejor condición (ADX más alto)
  - Si BTC está lateral → Switch automático a ETH
  - Maximiza operaciones: siempre hay movimiento en alguna crypto
- ✅ **Capital máximo:** $5,000
- ✅ **2 operaciones abiertas** simultáneas
- ✅ Soporte: Email + Chat (24h respuesta)
- ✅ Dashboard avanzado con analytics
- ✅ Webhooks para integraciones
- ✅ Exportar historial a Excel/CSV

### Ejemplo Modo AUTO_BEST:
```
09:00 - BTC (ADX 35, tendencia) → Opera BTCUSDT
11:30 - BTC (ADX 15, rango) → Cierra BTC, abre ETH
11:31 - ETH (ADX 38, tendencia fuerte) → Opera ETHUSDT
15:00 - ETH (ADX 12, lateral) → Vuelve a BTC
Resultado: 3 operaciones en el día vs 1 en Starter
```

### Precio:
- **Mensual:** $79/mes
- **Anual:** $790/año (2 meses gratis)
- **Setup fee:** $99 (único)

### Value Prop:
> "Con Pro pagas $50 más pero puedes operar el doble de veces porque el bot siempre encuentra el par en movimiento. Con $1,000 de capital, eso puede significar $200-400 más de ganancia mensual."

---

## 🥇 Plan Enterprise - $199/mes

### Ideal para:
- Empresas de trading/Prop firms
- Gestores de capital que operan múltiples cuentas
- Teams de 2-3 traders que quieren compartir costos
- Capital >$5,000

### Características (todo Pro +):
- ✅ **3 pares de trading** simultáneos
- ✅ **Modo MULTI_PARALLEL** (destacado 🚀)
  - Opera 3 pares al mismo tiempo con capital distribuido
  - Ej: $10,000 → $3,333 en BTC, $3,333 en ETH, $3,333 en SOL
  - Diversificación de riesgo por activo
  - Estrategias independientes por par
  - 3x más oportunidades de operación
- ✅ **3 usuarios/bots** por cuenta
  - Cada usuario tiene sus propias API keys
  - Capital separado por usuario
  - Admin puede ver todo, traders solo su cuenta
- ✅ **Capital ilimitado**
- ✅ **3 operaciones abiertas por usuario** (9 total)
- ✅ **Soporte prioritario:** Chat + WhatsApp (4h respuesta)
- ✅ API completa de acceso
- ✅ White-label opcional (logo propio)
- ✅ Onboarding personalizado 1:1
- ✅ Estrategias custom disponibles (addon)

### Caso de Uso típico Enterprise:
```
Empresa: "CryptoTraders Argentina"
Tenant: crypto-traders-ar

Usuario 1 (Admin):  
- Email: admin@cryptotraders.ar  
- Capital: $10,000  
- Pares: BTC, ETH, SOL

Usuario 2 (Trader Junior):
- Email: junior@cryptotraders.ar
- Capital: $5,000
- Pares: BTC, ETH

Usuario 3 (Trader Invitado):
- Email: guest@cryptotraders.ar
- Capital: $3,000
- Par: BTC

Total gestionado: $18,000
Costo mensual: $199 (vs $237 si fueran 3 cuentas Pro)
Ahorro: 16% + centralización de gestión
```

### Precio:
- **Mensual:** $199/mes
- **Anual:** $1,990/año (2 meses gratis)
- **Setup fee:** $199 (único)

---

## 💳 Setup Fee (Cargo Único de Activación)

| Plan | Setup Fee | Qué incluye |
|------|-----------|-------------|
| **Starter** | $49 | Onboarding guiado + Configuración inicial + Test de API keys |
| **Pro** | $99 | Todo Starter + Configuración de 2 pares + Tutorial modo AUTO_BEST |
| **Enterprise** | $199 | Todo Pro + Onboarding 1:1 (30 min) + Configuración de 3 usuarios + Setup Telegram prioritario |

### ¿Por qué cobramos setup fee?
1. **Costo real:** Tiempo de soporte para configuración inicial (~30-60 min)
2. **Filtra usuarios:** Reduce churn de usuarios que prueban y abandonan
3. **Mejora retención:** Usuarios con inversión inicial están más comprometidos
4. **Cubre costos:** API calls de testnet, validación de keys, etc.

### Promociones de Setup Fee:
- **50% OFF** setup fee en Black Friday / Cyber Monday
- **WAIVED** (gratis) si pagas anual upfront
- **WAIVED** para referrals (el que refiere y el nuevo)

---

## 🎁 Descuentos y Promociones

### Anual (2 meses gratis)
| Plan | Mensual × 12 | Anual | Ahorro |
|------|--------------|-------|--------|
| Starter | $348 | $290 | $58 (17%) |
| Pro | $948 | $790 | $158 (17%) |
| Enterprise | $2,388 | $1,990 | $398 (17%) |

### Programa de Referidos
- Refiere un amigo → Ambos obtienen **1 mes gratis**
- Refiere 3 amigos → **3 meses gratis** + upgrade temporal a Pro

### Upgrade de Plan
- Starter → Pro: Primer mes Pro a **$39** (50% off)
- Pro → Enterprise: Setup fee waived + **2 meses al precio de Pro**

---

## 📈 Comparativa ROI por Plan

Supongamos capital de $2,000 y win rate 65%:

| Métrica | Starter (1 par) | Pro (2 pares AUTO) | Enterprise (3 pares) |
|---------|-----------------|-------------------|---------------------|
| Operaciones/día | 4 | 8 | 12 |
| P&L mensual est. | $120 (6%) | $280 (14%) | $420 (21%) |
| Costo mensual | $29 | $79 | $199 |
| **ROI neto** | **$91** | **$201** | **$221** |
| ROI % neto | 4.5% | 10% | 11% |

### Breakeven por Plan
| Plan | Costo mensual | Capital mínimo recomendado | Breakeven (P&L necesario) |
|------|---------------|---------------------------|---------------------------|
| Starter | $29 | $500 | $29/mes (5.8% del capital) |
| Pro | $79 | $1,000 | $79/mes (7.9% del capital) |
| Enterprise | $199 | $5,000 | $199/mes (4% del capital) |

> **Insight:** Enterprise es más eficiente con capitales grandes porque el costo fijo $199 representa menor % del capital operado.

---

## 🛒 Add-ons y Servicios Extras

| Servicio | Precio | Descripción |
|----------|--------|-------------|
| **Estrategia Custom** | $299 (one-time) | Desarrollamos una estrategia a tu medida ( Enterprise only) |
| **Coaching 1:1** | $49/sesión | 30 min con experto para optimizar tu configuración |
| **Priority Support** | $19/mes | Respuesta < 2 horas (separado del plan) |
| **API Access** | $39/mes | Acceso completo a API REST (Pro/Enterprise incluye) |
| **White Label** | $99/mes | Tu logo, tu dominio, tu marca (Enterprise + addon) |
| **Extra User** | $49/mes/mes | Usuario adicional en Enterprise (máx 5 total) |
| **Extra Pair** | $19/mes/mes | Par de trading adicional (máx 5 total) |

---

## 💼 Comparativa con Competidores

| Producto | Precio/mes | Pares | Multi-User | Nuestra Ventaja |
|----------|-----------|-------|------------|-----------------|
| **QL Trading Bot** | $29-$199 | 1-3 | ✅ Enterprise | Multi-pair AUTO_BEST, multi-user real |
| 3Commas | $29-$99 | 1 | ❌ | Nosotros tenemos multi-user nativo |
| Cryptohopper | $19-$99 | 1 | ❌ | Nuestras estrategias son más simples y efectivas |
| Pionex | Gratis (0.05% fee) | 1 | ❌ | Nosotros no cobramos fee por trade |
| TradeSanta | $12-$30 | 1 | ❌ | Mejor soporte y multi-par en planes altos |
| **Ventaja QL:** | | | | **Únicos con modo AUTO_BEST y multi-paralelo** |

---

## 🎯 Recomendaciones por Perfil

### "Quiero probar, tengo $300"
→ **Starter mensual** ($29 + $49 setup = $78 inicial)

### "Tengo $1,500 y quiero resultados"
→ **Pro anual** ($790 + $99 setup = $889, luego $0 por 12 meses)
→ El modo AUTO_BEST pagará el costo extra en 2-3 semanas

### "Somos 3 amigos que invertimos juntos"
→ **Enterprise mensual** compartido ($199/3 = $66 por persona)
→ Cada uno con su cuenta y capital separado

### "Tengo una prop firm de $50,000"
→ **Enterprise anual + Estrategia Custom**
→ $1,990 + $299 = $2,289 año 1
→ ROI esperado: $7,500-15,000/año (con multi-paralelo)

---

## 📋 Checklist de Decisión

- [ ] Capital < $500 → Starter
- [ ] Capital $500-$5,000 → Pro (AUTO_BEST vale la pena)
- [ ] Capital > $5,000 → Enterprise (multi-paralelo)
- [ ] Somos 2+ personas → Enterprise (costo compartido)
- [ ] Quiero probar primero → Starter mensual (no anual)
- [ ] Voy en serio largo plazo → Cualquier plan anual (17% off)

---

## 📞 Contacto para Enterprise

Para empresas >$50,000 capital o >10 usuarios:

**Email:** enterprise@qltradingbot.com  
**WhatsApp:** +54 11-XXXX-XXXX  
**Calendly:** https://calendly.com/ql-trading-enterprise

**Custom pricing disponible:**
- Volumen >$100K: 20% off Enterprise
- White-label completo: Desde $499/mes
- API dedicada: Precio custom

---

## ⚖️ Términos y Condiciones

- Sin contratos de permanencia. Cancela cuando quieras.
- Primeros 7 días: Money back garantizado (menos setup fee)
- Testnet ilimitado gratis (no necesitas suscripción)
- Para operar real (live) con dinero: Plan activo requerido
- Precios en USD. Cobro vía Stripe (tarjeta) o crypto (USDT)

---

*Última actualización: Abril 2026*  
*Los precios pueden cambiar. Los usuarios actuales mantienen su tarifa por 12 meses.*
