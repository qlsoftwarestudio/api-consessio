# 📱 Notificaciones Telegram - QL Trading Bot

## Tipos de Notificaciones

El bot envía notificaciones automatizadas vía Telegram Bot API cuando ocurren eventos importantes:

1. **Cambio de Estrategia** - Cuando el bot detecta un cambio de condición de mercado
2. **Trade Abierto** - Cuando se ejecuta una orden de compra
3. **Trade Cerrado** - Cuando se cierra una posición (profit/loss)
4. **Resumen Diario** - Reporte de fin de día
5. **Alertas de Riesgo** - Cuando se alcanzan límites de seguridad

---

## 1. Cambio de Estrategia (Strategy Switch)

**Cuándo se envía:** El bot detecta un cambio en la condición del mercado y cambia automáticamente de estrategia.

```
🔄 **Estrategia Cambiada - QL Trading Bot**

📅 2026-04-29 14:35:22 UTC-3
👤 Usuario: admin@empresa.com

━━━━━━━━━━━━━━━━━━━━━

📊 **Análisis del Mercado**

🔸 Condición anterior: RANGO
🔸 Nueva condición: TENDENCIA ALCISTA

🔸 Estrategia anterior: Range Trading
🔸 Nueva estrategia: Trend Following

🔸 ADX: 32.5 (tendencia fuerte)
🔸 EMA9: $64,400 > EMA21: $63,800
🔸 Volumen 24h: +15% sobre promedio

━━━━━━━━━━━━━━━━━━━━━

💰 **Portfolio al cambio**

Balance: $312.45 USD (+4.15%)
P&L del día: +$12.45 (+4.15%)
Operaciones abiertas: 0 (cerradas al cambiar)

━━━━━━━━━━━━━━━━━━━━━

🎯 **Próximas acciones:**

→ Buscar compras en pullbacks a EMA9
→ Stop dinámico: EMA21
→ Target: +5% por operación
→ Máximo 2 posiciones abiertas

⚠️ Risk Management:
Max 5% por trade ($15 USD)
Daily loss limit: -$30 USD

━━━━━━━━━━━━━━━━━━━━━

🔗 Dashboard: https://bot.qlstudio.com
🔕 Silenciar: /silenciar
📊 Status: /status
```

---

## 2. Trade Abierto (Trade Opened)

**Cuándo se envía:** El bot ejecuta una orden de compra exitosamente.

```
🟢 **Trade Abierto - COMPRA BTC**

📅 2026-04-29 10:30:15 UTC-3
👤 Usuario: admin@empresa.com

━━━━━━━━━━━━━━━━━━━━━

📈 **Detalles de la Operación**

🔸 Par: BTCUSDT
🔸 Tipo: BUY (LARGO)
🔸 Estrategia: Trend Following
🔸 Condición: TREND_UP

💰 **Entrada**
Precio: $64,500.50
Cantidad: 0.002 BTC
Inversión: $129.00 USDT

🎯 **Targets**

✅ Take Profit: $67,725.52 (+5.0%)
⛔ Stop Loss: $62,565.49 (-3.0%)
Ratio Riesgo/Beneficio: 1.67

━━━━━━━━━━━━━━━━━━━━━

📊 **Señal Técnica**

RSI: 58.3 (neutral)
ADX: 32.5 (tendencia fuerte)
EMA9: $64,400.00
EMA21: $63,800.00

━━━━━━━━━━━━━━━━━━━━━

💼 **Portfolio actual**

Balance Total: $312.45
Balance Disponible: $183.45
En operación: $129.00
Posiciones abiertas: 1/2

━━━━━━━━━━━━━━━━━━━━━

⚠️ Gestión de Riesgo
Tamaño posición: 41.3% del balance disponible
Riesgo máx. permitido: 5% ($15.62)
Trade dentro de parámetros: ✅

━━━━━━━━━━━━━━━━━━━━━

🔗 Ver en dashboard: https://bot.qlstudio.com/trades/152
```

---

## 3. Trade Cerrado con Ganancia (Trade Closed - Profit)

**Cuándo se envía:** Una posición se cierra en take profit o manualmente con ganancia.

```
✅ **Trade Cerrado - GANANCIA**

📅 2026-04-29 14:45:22 UTC-3
👤 Usuario: admin@empresa.com

━━━━━━━━━━━━━━━━━━━━━

📈 **Resultado de la Operación**

🔸 Par: BTCUSDT
🔸 Tipo: BUY (LARGO)
🔸 Estrategia: Range Trading

💰 **Performance**

Entrada: $63,800.00
Salida: $67,000.00

🟢 Ganancia: $3.20 USDT
🟢 ROI: +5.0%

Duración: 4h 15m

━━━━━━━━━━━━━━━━━━━━━

🎯 **¿Por qué se cerró?**

✅ TAKE PROFIT alcanzado
El precio tocó el objetivo de +5%

━━━━━━━━━━━━━━━━━━━━━

💼 **Portfolio actualizado**

Balance Total: $315.65 (+$3.20)
P&L del día: +$15.65 (+5.2%)

📊 Estadísticas del día:
Trades: 2 (2 ganadores, 0 perdedores)
Win rate: 100%
Mejor trade: +5.0%

━━━━━━━━━━━━━━━━━━━━━

🏆 **Streak actual:** 3 ganancias consecutivas

🔗 Ver detalle: https://bot.qlstudio.com/trades/151
📊 Ver historial: https://bot.qlstudio.com/trades
```

---

## 4. Trade Cerrado con Pérdida (Trade Closed - Loss)

**Cuándo se envía:** Una posición se cierra en stop loss o manualmente con pérdida.

```
🔴 **Trade Cerrado - PÉRDIDA**

📅 2026-04-29 16:20:10 UTC-3
👤 Usuario: junior@empresa.com

━━━━━━━━━━━━━━━━━━━━━

📉 **Resultado de la Operación**

🔸 Par: BTCUSDT
🔸 Tipo: BUY (LARGO)
🔸 Estrategia: Scalping

💰 **Performance**

Entrada: $65,200.00
Salida: $63,244.00

🔴 Pérdida: -$1.96 USDT
🔴 ROI: -3.0%

Duración: 1h 45m

━━━━━━━━━━━━━━━━━━━━━

🎯 **¿Por qué se cerró?**

⛔ STOP LOSS ejecutado
El precio alcanzó el límite de -3%
Riesgo controlado ✅

━━━━━━━━━━━━━━━━━━━━━

💼 **Portfolio actualizado**

Balance Total: $998.04 (-$1.96)
P&L del día: -$1.96 (-0.2%)

📊 Estadísticas del día:
Trades: 1 (0 ganadores, 1 perdedor)
Win rate: 0%

⚠️ **Risk Alert:**
Pérdida diaria acumulada: -0.2% / -10.0% límite
Remaining risk budget: $98.04

━━━━━━━━━━━━━━━━━━━━━

📝 **Lección:** El stop loss funcionó correctamente, 
protegiendo el capital. La pérdida estaba dentro del 
riesgo planificado.

🔗 Ver detalle: https://bot.qlstudio.com/trades/148
```

---

## 5. Resumen Diario (Daily Summary)

**Cuándo se envía:** Todos los días a las 23:59 UTC (o al desactivar el bot).

```
📊 **Resumen Diario - QL Trading Bot**

📅 29 de Abril, 2026
👤 Usuario: admin@empresa.com

━━━━━━━━━━━━━━━━━━━━━

💰 **Performance del Día**

Balance inicial: $300.00
Balance final: $312.45

🟢 Ganancia Neta: +$12.45
🟢 ROI: +4.15%

━━━━━━━━━━━━━━━━━━━━━

📈 **Actividad de Trading**

Trades ejecutados: 4
✅ Ganadores: 3 (75%)
🔴 Perdedores: 1 (25%)

Mejor trade: +5.0% ($3.20)
Peor trade: -3.0% (-$1.96)
Profit factor: 2.47

⏱️ Tiempo en mercado: 6h 30m
Estrategia más usada: Trend Following (3 trades)

━━━━━━━━━━━━━━━━━━━━━

📊 **Estadísticas Acumuladas (Mes)**

Total trades: 45
Win rate: 71.1%
P&L mensual: +$45.60 (+17.08%)

Max drawdown: -5.2%
Profit factor: 2.47

━━━━━━━━━━━━━━━━━━━━━

🎯 **Estado del Bot**

Bot: 🟢 ACTIVO
Estrategia actual: Trend Following
Condición: TREND_UP
Operaciones abiertas: 0

━━━━━━━━━━━━━━━━━━━━━

🏆 **Logros**

✨ Día rentable consecutivo: 5
✨ Mejor racha del mes: 7 ganancias seguidas

━━━━━━━━━━━━━━━━━━━━━

🔗 Dashboard completo: https://bot.qlstudio.com
📈 Gráficas: https://bot.qlstudio.com/analytics

¡Buen trabajo hoy! 🚀
```

---

## 6. Alerta de Riesgo - Límite Diario (Risk Alert)

**Cuándo se envía:** Cuando el bot se acerca o alcanza el límite de pérdida diaria.

```
⚠️ **ALERTA DE RIESGO - BOT PAUSADO**

🛑 El bot ha sido pausado automáticamente

📅 2026-04-29 18:30:00 UTC-3
👤 Usuario: admin@empresa.com

━━━━━━━━━━━━━━━━━━━━━

🔴 **Límite de Pérdida Diaria Alcanzado**

Pérdida acumulada hoy: -$30.00
Límite configurado: -$30.00 (10% del balance)

Balance inicial: $300.00
Balance actual: $270.00 (-10.0%)

━━━━━━━━━━━━━━━━━━━━━

📊 **Análisis del Día**

Trades: 8
✅ Ganadores: 2
🔴 Perdedores: 6
Win rate: 25%

El mercado ha estado en contra de nuestras 
estrategias hoy.

━━━━━━━━━━━━━━━━━━━━━

🛡️ **Protección Activada**

✅ Bot pausado automáticamente
✅ Operaciones abiertas cerradas en market
✅ No se abrirán nuevas posiciones hoy
✅ Capital preservado: $270.00

━━━━━━━━━━━━━━━━━━━━━

📝 **Acciones Recomendadas**

1. Revisar el dashboard para analizar qué pasó
2. Considerar si las estrategias actuales se 
   ajustan al mercado de hoy
3. El bot se reactivará automáticamente mañana a las 00:00 UTC

O puedes reactivar manualmente con:
👉 /activar (no recomendado hoy)

━━━━━━━━━━━━━━━━━━━━━

⚠️ Recuerda: Esto es parte del trading. Los días 
perdedores ocurren. Lo importante es que el 
sistema protegió tu capital.

🔗 Dashboard: https://bot.qlstudio.com
📊 Análisis: https://bot.qlstudio.com/risk
```

---

## 7. Alerta de Máximo de Posiciones (Max Positions)

**Cuándo se envía:** Cuando se alcanza el límite de operaciones abiertas.

```
⚠️ **Alerta - Máximo de Posiciones Alcanzado**

📅 2026-04-29 11:15:33 UTC-3
👤 Usuario: junior@empresa.com

━━━━━━━━━━━━━━━━━━━━━

📊 **Estado del Portfolio**

Operaciones abiertas: 2/2 (MÁXIMO)

Trade #151: BTCUSDT BUY - +$1.20 (en curso)
Trade #152: BTCUSDT BUY - +$0.85 (en curso)

Capital comprometido: $258.00 (86% del portfolio)
Capital disponible: $42.00 (14%)

━━━━━━━━━━━━━━━━━━━━━

🚫 **Nueva Señal Rechazada**

El bot detectó una señal de compra válida:
• Condición: TREND_UP
• Precio: $64,500
• Confianza: 78%

❌ No se ejecutó - Límite de posiciones alcanzado

━━━━━━━━━━━━━━━━━━━━━

💡 **Gestión de Riesgo**

El sistema mantiene máximo 2 posiciones para:
✅ No sobre-exponer el capital
✅ Permitir promediar si es necesario
✅ Mantener margen de maniobra

━━━━━━━━━━━━━━━━━━━━━

⏳ **Cuándo se reanudará:**

El bot abrirá nuevas posiciones cuando:
→ Se cierre alguna operación actual, O
→ Se libere capital suficiente

Notificaciones seguirán llegando para las 
operaciones abiertas.
```

---

## 8. Notificación de Test / Configuración

**Cuándo se envía:** Cuando el usuario configura Telegram por primera vez.

```
🔔 **Test de Notificación - QL Trading Bot**

✅ Tus notificaciones de Telegram están configuradas correctamente.

👤 Usuario: admin@empresa.com
Tenant: Empresa Pérez Trading
Plan: ENTERPRISE

━━━━━━━━━━━━━━━━━━━━━

📱 **Tipos de notificaciones que recibirás:**

🟢 Trade abierto
🔴 Trade cerrado
🔄 Cambio de estrategia
📊 Resumen diario
⚠️ Alertas de riesgo
📈 Reportes semanales (opcional)

━━━━━━━━━━━━━━━━━━━━━

🔕 **Para silenciar temporalmente:**
Envía /silenciar

📊 **Para ver status actual:**
Envía /status

⚙️ **Para ver configuración:**
Ingresa al dashboard: https://bot.qlstudio.com/config

━━━━━━━━━━━━━━━━━━━━━

🚀 El bot está listo para operar.

Próxima notificación: Cuando ocurra la primera operación.
```

---

## Comandos de Telegram Disponibles

Los usuarios pueden interactuar con el bot enviando comandos:

| Comando | Descripción |
|---------|-------------|
| `/start` | Iniciar bot y ver mensaje de bienvenida |
| `/status` | Ver estado actual del bot y portfolio |
| `/balance` | Ver balance y posiciones abiertas |
| `/trades` | Ver últimos trades |
| `/silenciar` | Pausar notificaciones por 4 horas |
| `/activar` | Reactivar notificaciones |
| `/pause` | Pausar el bot (cierra trades al final del día) |
| `/resume` | Reactivar el bot |
| `/help` | Ver lista de comandos |

---

## Formato de Mensajes Técnicos

### Características del diseño:
- **Emojis**: Para categorizar visualmente el tipo de mensaje
- **Separadores**: Líneas de `━` para organizar secciones
- **Negrita**: Para datos importantes (precios, P&L)
- **Códigos cortos**: Fechas ISO 8601 en UTC-3
- **Links**: URLs cortas al dashboard para acción

### Limitaciones de Telegram:
- Mensaje máximo: 4096 caracteres
- Markdown básico: negrita, cursiva, código
- No se soportan tablas complejas
- Las imágenes/gráficas se envían como archivos separados

---

*QL Trading Bot v1.0*
