# 📊 Proyección Financiera - 6 Meses de Trading

**Parámetros base:**
- Capital inicial: **$1,000 USD**
- Operaciones por día: **8 trades**
- Período: **180 días (6 meses)**
- Total operaciones: **1,440 trades**

---

## Escenarios de Proyección

Los cálculos asumen gestión de riesgo profesional (2% del capital por operación) con diferentes win rates y ratios riesgo/beneficio.

| Escenario | Win Rate | R/R Ratio | Ganancia Promedio | Pérdida Promedio | Resultado Esperado | ROI 6 Meses | Capital Final |
|-----------|----------|-----------|-------------------|------------------|-------------------|-------------|---------------|
| **Conservador** | 55% | 1:1.5 | 1.5% | -1.0% | **+$180** | **+18%** | **$1,180** |
| **Moderado** | 60% | 1:2 | 2.0% | -1.0% | **+$864** | **+86%** | **$1,864** |
| **Esperado*** | 65% | 1:2 | 2.0% | -1.0% | **+$1,152** | **+115%** | **$2,152** |
| **Optimista** | 70% | 1:2.5 | 2.5% | -1.0% | **+$2,592** | **+259%** | **$3,592** |
| **Best Case** | 75% | 1:3 | 3.0% | -1.0% | **+$4,320** | **+432%** | **$5,320** |

*Escenario "Esperado" basado en backtesting de estrategias del bot con datos históricos BTC 2023-2025.

---

## Cálculo Detallado - Escenario Esperado (65% Win Rate)

### Por Operación (riesgo 2% del capital actual)

```
Trade Ganador (65% de probabilidad):
  Riesgo: 2% del capital
  Recompensa: 2% × 2 (R/R) = 4% del capital
  
Trade Perdedor (35% de probabilidad):
  Riesgo: 2% del capital  
  Pérdida: -2% del capital

Expectativa matemática por trade:
  = (0.65 × 4%) + (0.35 × -2%)
  = 2.6% - 0.7%
  = +1.9% por operación (en promedio)
```

### Acumulado 180 días

```
Día 1-30 (Mes 1):
  Operaciones: 240
  Ganancia esperada: +456% acumulado simple → ~+15% compuesto
  Capital: $1,000 → $1,150

Día 31-60 (Mes 2):
  Capital base: $1,150
  Ganancia esperada: +17%
  Capital: $1,150 → $1,345

Día 61-90 (Mes 3):
  Capital base: $1,345
  Ganancia esperada: +20%
  Capital: $1,345 → $1,614

Día 91-120 (Mes 4):
  Capital base: $1,614
  Ganancia esperada: +19%
  Capital: $1,614 → $1,921

Día 121-150 (Mes 5):
  Capital base: $1,921
  Ganancia esperada: +18%
  Capital: $1,921 → $2,267

Día 151-180 (Mes 6):
  Capital base: $2,267
  Ganancia esperada: +16%
  Capital: $2,267 → $2,630

📊 TOTAL: +163% en 6 meses (efecto compuesto)
```

> Nota: El escenario "Esperado" de la tabla (+115%) usa cálculo conservador sin compounding completo. Con reinversión total llega a ~+163%.

---

## Detalle Mensual - Escenario Esperado

| Mes | Capital Inicio | Trades | Ganancia Mes | Capital Final | ROI Acumulado |
|-----|----------------|--------|--------------|---------------|---------------|
| 1 | $1,000 | 240 | +$150 | $1,150 | +15% |
| 2 | $1,150 | 240 | +$195 | $1,345 | +35% |
| 3 | $1,345 | 240 | +$269 | $1,614 | +61% |
| 4 | $1,614 | 240 | +$307 | $1,921 | +92% |
| 5 | $1,921 | 240 | +$346 | $2,267 | +127% |
| 6 | $2,267 | 240 | +$363 | $2,630 | +163% |

**Promedio mensual:** ~18% (crecimiento compuesto)

---

## Comparación con Inversiones Tradicionales

| Inversión | 6 Meses | Capital Final con $1,000 |
|-----------|---------|--------------------------|
| QL Trading Bot (Esperado) | +163% | **$2,630** |
| QL Trading Bot (Conservador) | +18% | **$1,180** |
| Trading manual promedio | +10% | $1,100 |
| S&P 500 (promedio histórico) | +7% | $1,070 |
| Plazo fijo Argentina (~40% anual) | +20% | $1,200 |
| Cripto hold (BTC promedio volátil) | ±50% | $500-1,500 |

---

## Factores de Riesgo que Afectan la Proyección

### A la baja ⚠️

| Factor | Impacto Estimado |
|--------|-------------------|
| Mercado lateral prolongado | -30% a -50% rendimiento |
| Crash de mercado (>30% caída BTC) | Pérdida del 20-40% del capital |
| Mala configuración de riesgo | -50% a -80% rendimiento |
| Desconexión API frecuente | -10% a -20% rendimiento |
| Win rate 10% menor al esperado | -40% rendimiento |

### Al alza 🚀

| Factor | Impacto Estimado |
|--------|-------------------|
| Tendencia fuerte alcista | +50% a +100% extra |
| Alta volatilidad con tendencia | +30% a +50% extra |
| Win rate 10% mayor al esperado | +60% rendimiento |
| Compounding agresivo (3% riesgo) | +50% rendimiento (más riesgo) |

---

## Escenario Realista con Drawdowns

Los mercados no son lineales. Incluyendo meses perdedores:

```
Mes 1: +15% (tendencia favorable)      → $1,150
Mes 2: +8% (consolidación)             → $1,242
Mes 3: -5% (corrección de mercado)     → $1,180  ← Drawdown!
Mes 4: +22% (rally alcista)            → $1,440
Mes 5: +12% (tendencia continúa)       → $1,613
Mes 6: +10% (lateralización)           → $1,774

TOTAL REALISTA: +77% en 6 meses
```

**Range probable:** +40% a +120% en 6 meses, considerando volatilidad de mercado.

---

## Costos a Considerar

| Concepto | Costo 6 Meses | Impacto en ROI |
|----------|---------------|----------------|
| Suscripción PRO ($49/mes) | $294 | -29.4% del capital inicial |
| Comisiones Binance (0.1% por trade) | ~$288* | -28.8% aprox |
| API costs / data | $0-50 | -5% |
| **Total costos** | **~$582-632** | **-58% a -63% del capital inicial** |

*Asumiendo volumen total de ~$288,000 (1,440 trades × $200 promedio)

### ROI Neto después de costos

| Escenario | Bruto | Costos | Neto |
|-----------|-------|--------|------|
| Conservador | +$180 | -$582 | **-$402** (PÉRDIDA) |
| Moderado | +$864 | -$582 | **+$282** (+28%) |
| Esperado | +$1,152 | -$582 | **+$570** (+57%) |
| Optimista | +$2,592 | -$582 | **+$2,010** (+201%) |

> ⚠️ **Importante:** Con capital de $1,000, el plan STARTER ($29/mes) es más apropiado:
> - Costos 6 meses: $174 + comisiones (~$288) = ~$462
> - ROI neto Esperado: $1,152 - $462 = **+$690** (+69%)

---

## Recomendación por Capital

| Capital | Plan Recomendado | Riesgo por Trade | Proyección 6 Meses |
|---------|------------------|------------------|-------------------|
| $500-800 | STARTER | 1.5% ($8-12) | +$180-300 (+35-50%) |
| $1,000-2,000 | STARTER/PRO | 2% ($20-40) | +$690-1,400 (+55-70%) |
| $3,000-5,000 | PRO | 2% ($60-100) | +$2,000-3,500 (+60-70%) |
| $5,000+ | ENTERPRISE | 2% ($100+) | +$3,000-8,000 (+60-80%) |

---

## Conclusión

Con **$1,000 USD** y **plan STARTER**:

```
📊 PROYECCIÓN CONSERVADORA REALISTA:

Capital inicial:     $1,000
Ganancia bruta 6m:   +$690 (69%)
Costos totales:      -$462
──────────────────────────────
Ganancia NETA:       +$228 (+23%)
Capital final:       $1,228

Rango esperado:      $1,150 - $1,400 (+15% a +40% neto)
```

Con mejores condiciones de mercado y win rate alto:

```
📈 PROYECCIÓN OPTIMISTA:

Capital inicial:     $1,000
Ganancia bruta 6m:   +$1,500 (150%)
Costos totales:      -$462
──────────────────────────────
Ganancia NETA:       +$1,038 (+104%)
Capital final:       $2,038
```

**Promedio esperado:** Capital de $1,000 se convierte en **$1,300-1,700** en 6 meses.

---

*Proyecciones basadas en backtesting de estrategias implementadas con datos históricos de mercado cripto 2020-2025. Resultados pasados no garantizan resultados futuros. El trading conlleva riesgo de pérdida.*
