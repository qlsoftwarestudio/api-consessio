# 💼 Análisis de Negocio SaaS - 10 Tenants Starter

**Escenario:** Plataforma QL Trading Bot con **10 clientes (tenants)** suscritos al plan **STARTER**.

---

## 📊 Resumen del Negocio

| Métrica | Valor |
|---------|-------|
| Tenants activos | 10 |
| Plan | STARTER ($29/mes) |
| Usuarios totales | 10 (1 por tenant) |
| Bots activos | 10 |
| **MRR** (Monthly Recurring Revenue) | **$290/mes** |
| **ARR** (Annual Recurring Revenue) | **$3,480/año** |

---

## 💰 Ingresos Proyectados

### Por Tenant (Plan Starter $29/mes)

| Concepto | Mensual | Anual |
|----------|---------|-------|
| Suscripción Starter | $29 | $348 |
| 
| **TOTAL 10 Tenants** | **$290** | **$3,480** |

### Escenarios de Crecimiento (12 meses)

| Mes | Tenants | Churn* | Nuevos | Total | MRR | ARR Acumulado |
|-----|---------|--------|--------|-------|-----|---------------|
| 1 | 10 | 0 | 2 | 12 | $348 | $348 |
| 3 | 12 | 1 | 5 | 16 | $464 | $1,218 |
| 6 | 16 | 2 | 8 | 22 | $638 | $2,844 |
| 9 | 22 | 3 | 12 | 31 | $899 | $5,100 |
| 12 | 31 | 4 | 15 | **42** | **$1,218** | **$10,440** |

*Churn mensual estimado: 5% (industria SaaS promedio)
*Adquisición mensual: 2-15 nuevos tenants según marketing

---

## 💸 Costos de Operación

### 1. Infraestructura AWS (10 tenants)

| Servicio | Configuración | Costo Mensual |
|----------|---------------|---------------|
| **EC2 (App Server)** | t3.medium (2 vCPU, 4GB) | $30 |
| **RDS PostgreSQL** | db.t3.micro (1 instance) | $15 |
| **Redis (ElastiCache)** | cache.t3.micro | $12 |
| **Binance API** | WebSocket + REST calls | $0 (incluido) |
| **Telegram Bot API** | Webhooks | $0 |
| **Load Balancer** | ALB | $16 |
| **CloudWatch/Logs** | Monitoreo básico | $10 |
| **Data Transfer** | ~500GB/mes | $45 |
| 
| **TOTAL INFRAESTRUCTURA** | | **$128/mes** |

### 2. Costos Fijos Operativos

| Concepto | Mensual | Anual |
|----------|---------|-------|
| Dominio + SSL | $2 | $24 |
| Stripe (payment processing)** | ~3% del MRR | $9 |
| Email service (SendGrid/AWS SES) | $5 | $60 |
| Backup storage (S3) | $3 | $36 |
| Seguros/Legal | $10 | $120 |
| 
| **TOTAL OPERATIVOS** | **$29** | **$348** |

**Stripe cobra 2.9% + $0.30 por transacción. Para $290 MRR = ~$9

### 3. Costos Variables por Tenant

| Concepto | Costo/tenant/mes |
|----------|------------------|
| WebSocket connections | $0.50 |
| DB storage (~100MB/tenant) | $0.20 |
| API calls Binance (rate limit) | $0 (gratis) |
| Notificaciones push | $0.10 |
| Logs retention | $0.50 |
| 
| **TOTAL por tenant** | **$1.30** |
| **10 tenants** | **$13/mes** |

---

## 📈 Estado de Resultados (P&L)

### Mes 1-6: 10 Tenants Starter

| Concepto | Mensual | Anual |
|----------|---------|-------|
| **INGRESOS** | | |
| Suscripciones (10 × $29) | $290 | $3,480 |
| 
| **COSTOS** | | |
| Infraestructura AWS | $128 | $1,536 |
| Operativos fijos | $29 | $348 |
| Variables por tenant (10 × $1.30) | $13 | $156 |
| 
| **TOTAL COSTOS** | **$170** | **$2,040** |
| 
| **GANANCIA BRUTA** | **$120** | **$1,440** |
| 
| **Margen de beneficio** | **41.4%** | **41.4%** |

### Escenario Escalado: 42 Tenants (Mes 12)

| Concepto | Mensual | Anual Proyectado |
|----------|---------|------------------|
| **INGRESOS** | | |
| Starter (30 × $29) | $870 | $10,440 |
| Pro (8 × $79) | $632 | $7,584 |
| Enterprise (4 × $199) | $796 | $9,552 |
| **TOTAL MRR** | **$2,298** | **$27,576** |
| 
| **COSTOS** | | |
| Infraestructura escalada* | $350 | $4,200 |
| Operativos fijos | $50 | $600 |
| Variables (42 × $1.30) | $55 | $660 |
| Stripe fees (3%) | $69 | $828 |
| **TOTAL COSTOS** | **$524** | **$6,288** |
| 
| **GANANCIA BRUTA** | **$1,774** | **$21,288** |
| 
| **Margen de beneficio** | **77.2%** | **77.2%** |

*EC2 upgraded a t3.large, RDS a db.t3.small, ElastiCache a cache.t3.small

---

## 📊 Métricas SaaS Clave (10 Tenants)

| Métrica | Valor | Benchmark Industria |
|---------|-------|---------------------|
| **CAC** (Customer Acquisition Cost) | ~$50-100* | $50-200 (B2B SaaS) |
| **LTV** (Lifetime Value) | $174 ($29 × 6 meses avg) | Depende de churn |
| **LTV/CAC Ratio** | 1.74 - 3.48 | >3 es ideal |
| **Churn Rate** | 5%/mes estimado | <5% es bueno |
| **Payback Period** | 2-4 meses | <12 meses es bueno |
| **ARR** | $3,480 | Early stage |
| **MRR** | $290 | Pre-seed/seed |
| **Net Revenue Retention** | 100%+ con upgrades | >100% es excelente |

*Estimado con marketing orgánico + ads mínimo

---

## 🎯 Estrategia de Monetización Adicional

### 1. Upsell a Planes Superiores

| Upgrade | Probabilidad | Impacto en MRR |
|---------|--------------|----------------|
| Starter → Pro ($29→$79) | 20% de tenants | +$50 por upgrade |
| Starter → Enterprise ($29→$199) | 5% de tenants | +$170 por upgrade |
| 
| **Proyección:** De 10 tenants Starter, 2 suben a Pro en mes 6: | | |
| MRR se convierte en: (8×$29) + (2×$79) = **$390** | | |

### 2. Revenue por Trading Volume (Opcional)

Algunos SaaS de trading cobran comisión por volumen:

| Volumen mensual/tenant | Comisión % | Ingreso extra/mes |
|--------------------------|------------|-------------------|
| $10,000 (promedio) | 0% (nuestro caso) | $0 |
| Si cobráramos 0.1% | - | $10/tenant = $100 extra |

*Decisión de negocio: No cobrar comisión para diferenciarse de la competencia

### 3. Servicios Adicionales (Addons)

| Servicio | Precio | Demand estimate |
|----------|--------|-----------------|
| Setup personalizado | $99 one-time | 10% |
| Estrategia custom | $299 one-time | 5% |
| Coaching/mes | $49/mes | 5% |
| Priority support | $19/mes | 15% |

---

## 🚀 Escenarios de Escalado

### Escenario 1: Crecimiento Orgánico (12 meses)

```
Mes 1:  10 tenants  → MRR $290  → Profit $120
Mes 3:  16 tenants  → MRR $464  → Profit $250
Mes 6:  25 tenants  → MRR $725  → Profit $450
Mes 9:  35 tenants  → MRR $1,015 → Profit $700
Mes 12: 50 tenants  → MRR $1,450 → Profit $1,100

Año 1 Total Profit: ~$5,000
```

### Escenario 2: Crecimiento con Marketing ($500/mes ads)

```
Mes 1:  10 tenants  → MRR $290  → Profit -$380 (inversión)
Mes 3:  30 tenants  → MRR $870  → Profit $120
Mes 6:  75 tenants  → MRR $2,175 → Profit $900
Mes 12: 200 tenants → MRR $5,800 → Profit $3,200

CAC: $30 por tenant (muy eficiente)
Año 1 Total Profit: ~$15,000
```

### Escenario 3: Viral/Referidos

```
Cada tenant refiere 0.5 nuevos tenants (recompensa $10)

Mes 1:  10 tenants (base) + 5 referidos = 15
Mes 2:  15 + 7 referidos = 22
Mes 3:  22 + 11 referidos = 33
...
Mes 6:  100+ tenants → MRR $2,900 → Profit $1,800

Costo adquisición efectivo: $10 (solo el referido)
```

---

## 💡 Break-Even Analysis

### Punto de equilibrio (con infraestructura actual)

```
Costos fijos mensuales: $128 (infra) + $29 (operativos) = $157
Costo variable por tenant: $1.30

Fórmula: Ingreso = Costos
$29 × n = $157 + $1.30 × n
$29n - $1.30n = $157
$27.70n = $157
n = 5.67 tenants

📍 BREAK-EVEN: 6 tenants Starter
```

**Con 10 tenants estás 67% arriba del punto de equilibrio.**

---

## ⚠️ Riesgos del Modelo SaaS

### Riesgos Operativos

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Saturación API Binance | Media | Alto | Rate limiting, múltiples API keys |
| AWS downtime | Baja | Alto | Multi-AZ, failover |
| Churn alto (>10%) | Media | Alto | Mejor onboarding, support |
| Chargebacks/fraud | Media | Medio | Stripe radar, 3D Secure |

### Riesgos del Mercado Cripto

| Riesgo | Impacto en Negocio |
|--------|-------------------|
| Bear market prolongado | Menor actividad → menos interés en bot |
| Exchange hackeado | Pérdida de confianza general |
| Regulación | Posible freeze de cuentas/pagos |
| Competencia | Margin squeeze, price war |

---

## 📋 Recomendaciones para Escalar a 10→100 Tenants

### Fase 1: Optimización (10-20 tenants)

1. **Automatizar onboarding** → Reduce support tickets
2. **Implementar referidos** → LTV/CAC más eficiente
3. **A/B test pricing** → Starter $29 vs $39
4. **Monitor churn** → Entender por qué se van

### Fase 2: Crecimiento (20-50 tenants)

1. **Contratar support part-time** → $500/mes
2. **Marketing de contenido** → Blog, YouTube
3. **Partnerships** → Influencers cripto (affiliates 20%)
4. **Plan anual con descuento** → Mejora cash flow

### Fase 3: Scale (50-200 tenants)

1. **Infra auto-scaling** → Kubernetes, serverless
2. **Equipo dedicado** → CTO, Support, Marketing
3. **Multi-exchange** → Binance, Bybit, OKX
4. **Mobile app** → Diferenciador competitivo
5. **White-label** → Revendedores

---

## 🎯 Conclusión: Viabilidad del Negocio

### Con 10 Tenants Starter:

```
✅ MRR: $290/mes ($3,480/año)
✅ Costos: $170/mes ($2,040/año)
✅ Ganancia: $120/mes ($1,440/año) - 41% margen
✅ Break-even: 6 tenants (ya lo superaste)
✅ Escalable: Infra soporta 50+ tenants sin upgrade
```

**Veredicto:** El modelo SaaS es **viable y rentable** desde los 10 tenants.

### Proyección a 12 meses (escenario conservador):

```
Tenants finales: 30-50
MRR final: $870-$1,450
Ganancia anual: $5,000-$15,000
Margen: 55-65%

Esto es un negocio de:
→ $5K-15K/año en auto-piloto (side income)
→ Potencial de $50K+/año si le dedicas tiempo
```

### Para llegar a $50K/año necesitas:

- **150-200 tenants** activos
- **Mix de planes:** 60% Starter, 30% Pro, 10% Enterprise
- **MRR:** ~$6,000/mes
- **Gastos:** ~$1,500/mes (equipo + infra + marketing)
- **Profit:** ~$4,500/mes = **$54,000/año**

---

*Análisis basado en costos AWS us-east-1 a Abril 2026. Los costos pueden variar según optimización de infraestructura y volumen de uso.*
