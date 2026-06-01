# 🧠 Modelo Mental: QL-Agent System

**Concepto:** Un asistente técnico ultra-rápido que "conoce" tu negocio y stack tecnológico.

---

## 🎯 Analogía: El "Segundo Cerebro" de tu Estudio

### Imaginá esto:

**Vos sos el founder/dev principal.** Tenés 2 productos complejos:
- Concessio CRM (multi-tenant, leads, cotizaciones)
- QL Trading Bot (multi-pair, estrategias, risk management)

**El problema:** Tu conocimiento está disperso en:
- 50 archivos Java dispersos
- 10 documentos Markdown
- Tu memoria (olvidadizo)
- Notas random en Telegram

**QL-Agent es como tener un "co-fundador virtual" que:**
- ✅ Leó TODO tu código y documentación
- ✅ Lo indexó en su "cerebro" (vector DB)
- ✅ Sabe qué modelo usar según la pregunta
- ✅ Responde en 200ms, no en 5 minutos de búsqueda

---

## 🏗️ Arquitectura de 3 Capas (Modelo Conceptual)

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA 1: INTERFAZ (Vos)                      │
│                                                                 │
│  • Terminal: comando 'ql'                                       │
│  • VS Code: atajo de teclado                                    │
│  • Telegram: mensaje cuando estás afuera                        │
│                                                                 │
│  Ejemplo: "generar repository para Lead"                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              CAPA 2: ORQUESTADOR (El "Router")                  │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │  CLASIFICAR │  │   CACHE     │  │   RAG/CTX   │            │
│  │             │  │             │  │             │            │
│  │ ¿Código?    │  │ ¿Ya         │  │ Buscar en   │            │
│  │ ¿Negocio?   │→ │ respondí    │→ │ vector DB   │            │
│  │ ¿Análisis?  │  │ esto antes? │  │ chunks      │            │
│  └─────────────┘  │             │  │ relevantes  │            │
│        (1ms)      │ Hit? →     │  │             │            │
│                   │ Retorna    │  │ (20-50ms)   │            │
│                   │ inmediato  │  └─────────────┘            │
│                   └─────────────┘                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│           CAPA 3: MODELOS ESPECIALIZADOS (El "Experto")         │
│                                                                 │
│  ┌─────────────────┐                                            │
│  │ qwen-coder:1.5b │ ← "Soy experto en Spring Boot/Java"       │
│  │     600MB       │                                           │
│  │  50-150ms       │    Si query tiene: class, method, @       │
│  └─────────────────┘                                           │
│                                                                 │
│  ┌─────────────────┐                                            │
│  │ llama3.2:3b     │ ← "Soy experto en negocio QL"             │
│  │     1.2GB       │                                           │
│  │  80-200ms       │    Si query tiene: precio, plan, lead     │
│  └─────────────────┘                                           │
│                                                                 │
│  ┌─────────────────┐                                            │
│  │ phi3:mini       │ ← "Soy analista de arquitectura"          │
│  │     1.5GB       │                                           │
│  │  100-250ms      │    Si query tiene: seguridad, review      │
│  └─────────────────┘                                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo de una Consulta (End-to-End)

### Escenario: "¿Cuánto cuesta el plan Pro?"

**Paso 0: Preparación (una vez)**
```
Tus documentos (.md) → Chunks → Embeddings → FAISS Index
  50KB              →  200 chunks →  Vectores 384-dim  →  DB local 5MB

Esto corre UNA SOLA VEZ al iniciar el agente.
Luego queda en memoria/disco.
```

**Paso 1: Input (0ms)**
```
Vos escribís: "¿Cuánto cuesta el plan Pro?"
```

**Paso 2: Clasificación (1ms)**
```
Router detecta keywords: "cuanto", "cuesta", "plan", "Pro"
→ Clasifica: NEGOCIO
→ Selecciona modelo: llama3.2:3b
```

**Paso 3: Cache Check (0ms)**
```
¿Respondí esto antes?
Hash de "cuanto cuesta plan pro" → Lookup
→ MISS (primera vez)
→ Continuar

(Si fuera HIT → Retorna inmediato, 0ms LLM)
```

**Paso 4: Context Retrieval (30ms)**
```
Query: "plan Pro precio"
↓
FAISS busca en índice vectorial:
→ Top 3 chunks más similares:
   1. "Plan Pro: $79/mes, 2 pares, modo AUTO_BEST..."
   2. "Setup fee: $99 para Pro..."
   3. "Comparativa: Starter $29, Pro $79, Enterprise $199..."

Contexto final (2KB): Junta estos 3 chunks
```

**Paso 5: Inference (150ms)**
```
Llama3.2:3b recibe:

---
Contexto:
Plan Pro: $79/mes, 2 pares de trading, modo AUTO_BEST
Setup fee: $99 (único)
Anual: $790 (2 meses gratis)

Pregunta: ¿Cuánto cuesta el plan Pro?
---

Modelo genera:
"Plan Pro: $79/mes + $99 setup fee (único).
Anual: $790 (ahorras $158).
Incluye 2 pares y modo AUTO_BEST."
```

**Paso 6: Post-processing (10ms)**
```
- Guarda en cache (para próxima vez)
- Formatea salida
- Retorna a Vos
```

**Total: ~200ms** (vs 2-5 segundos si buscás manual en los docs)

---

## 🧩 Componentes Clave Explicados

### 1. **FAISS (Facebook AI Similarity Search)**

**Qué es:** Una biblioteca de Facebook para buscar vectores rápido.

**Analogía:** 
- Imaginá que cada documento es una ciudad
- Cada chunk es una calle de esa ciudad
- Cada embedding es la "dirección GPS" de esa calle
- FAISS es Google Maps: te encuentra las 3 calles más cercanas a tu consulta

**Por qué es rápido:**
- Usa índices especiales (IVF, HNSW) que hacen búsqueda O(log n)
- 10,000 chunks → Búsqueda <10ms en CPU común

### 2. **Embeddings (MiniLM)**

**Qué es:** Convertir texto en vectores numéricos (384 dimensiones).

**Analogía:**
- "Perro" → [0.2, -0.5, 0.8, ...] (384 números)
- "Gato" → [0.1, -0.4, 0.9, ...] (similar a perro)
- "Avión" → [0.9, 0.2, -0.1, ...] (diferente)

**Cálculo de similitud:**
```
Cosine similarity(query, chunk) = 0.92 → Muy relevante
Cosine similarity(query, chunk) = 0.12 → No relevante
```

### 3. **Router por Regex (no por LLM)**

**Concepto clave:** No usamos un LLM para decidir qué LLM usar.

**Por qué:**
- LLM para clasificar: 500ms + costo
- Regex para clasificar: 1ms + gratis

**Implementación:**
```python
# Ultra-rápido, determinístico
if re.search(r'\b(código|clase|método|@|import)\b', query):
    return 'CODER'
elif re.search(r'\b(precio|plan|costo|vendedora)\b', query):
    return 'BUSINESS'
else:
    return 'ANALYST'
```

### 4. **Modelos Especializados (vs Generalista)**

**Concepto:** 3 modelos chicos especializados > 1 modelo grande generalista.

**Comparación mental:**

| Generalista (13B) | Especialistas (3× 1.5-3B) |
|-------------------|---------------------------|
| "Sé un poco de todo" | "Sé MUCHO de mi área" |
| 8GB RAM, 3s latencia | 3.5GB RAM, 0.2s latencia |
| Prompt engineering complejo | Prompt simple, directo |
| Puede confundirse | Preciso en su dominio |

**Analogía médica:**
- Generalista = Médico clínico (sabe de todo, te deriva)
- Especialista = Cardiólogo (sabe TODO de corazones)

Tu caso:
- Coder = Cardiólogo de Java/Spring
- Business = Cardiólogo de pricing/planes
- Analyst = Cardiólogo de arquitectura/seguridad

---

## 🎯 Decisiones de Diseño Clave

### 1. **Por qué Local > Cloud?**

| Factor | Local (Ollama) | Cloud (GPT-4) |
|--------|---------------|---------------|
| **Latencia** | 100-300ms | 2000-5000ms |
| **Costo** | $0 | $20-50/mes |
| **Privacidad** | Código nunca sale de tu PC | OpenAI lo procesa |
| **Offline** | ✅ Funciona en avión | ❌ Necesita internet |
| **Setup** | 2 horas inicial | 0 (listo) |

**Trade-off aceptado:** 2 horas de setup → Ahorro perpetuo de $500+/año.

### 2. **Por qué 3 Modelos > 1 Modelo?**

**Problema con 1 modelo grande:**
- Si es coder, responde mal sobre negocio
- Si es negocio, genera código mediocre
- Contexto contaminado entre dominios

**Solución 3 especialistas:**
- Coder entrenado específicamente en código
- Business "sabe" PRICING_PLANES.md de memoria
- Analyst enfocado en arquitectura

**Eficiencia:** Cada uno es pequeño y rápido en su nicho.

### 3. **Por qué RAG > Fine-tuning?**

**Fine-tuning:**
- Reentrenar modelo con tus datos
- Toma horas, requiere GPU potente
- Modelo "olvida" conocimiento general
- Costoso ($$$)

**RAG (Retrieval Augmented Generation):**
- Mantiene modelo base (rápido de cargar)
- Inyecta contexto relevante en cada query
- Actualizar docs = reindexar (5 minutos, no horas)
- Gratis

**Analogía:**
- Fine-tuning = Memorizar un libro (toma tiempo)
- RAG = Tener el libro abierto y buscar la página (instantáneo)

### 4. **Por qué Cache es Crítico?**

**Patrón Pareto:**
- 80% de tus consultas son el 20% de tipos de preguntas

**Ejemplo real:**
```
Semana tipo:
- "precio plan X" → 10 veces (cache hit 9)
- "generar repository" → 5 veces (cache hit 2)
- "análisis complejo" → 2 veces (cache hit 0)

Total: 17 consultas
Cache hits: 11 (65%)
LLM calls: 6 (35%)

Resultado: 65% de queries resueltas en 5ms
```

---

## 🔄 Ciclo de Vida de una Query (Visual)

```
Usuario: "generar repository para Lead"
    │
    ▼
┌────────────────────────────────────┐
│ 1. ROUTER                          │
│    Keywords: "generar", "repository"│
│    → Clasificación: CÓDIGO          │
│    Time: 1ms                       │
└────────────────────────────────────┘
    │
    ▼
┌────────────────────────────────────┐
│ 2. CACHE                           │
│    Hash: "generar_repository_lead"  │
│    → Miss (nueva query)             │
│    Time: 0ms                       │
└────────────────────────────────────┘
    │
    ▼
┌────────────────────────────────────┐
│ 3. RAG (Context Retrieval)         │
│    Query embedding → FAISS        │
│    Top chunks:                      │
│      - LeadRepository.java          │
│      - JpaRepository pattern docs   │
│    Time: 25ms                      │
└────────────────────────────────────┘
    │
    ▼
┌────────────────────────────────────┐
│ 4. LLM (Inference)                 │
│    Modelo: qwen-coder:1.5b        │
│    Input: 2KB context + query     │
│    Output: Código Java             │
│    Time: 120ms                     │
└────────────────────────────────────┘
    │
    ▼
┌────────────────────────────────────┐
│ 5. POST-PROCESS                    │
│    - Formatear output               │
│    - Guardar en cache               │
│    Time: 10ms                      │
└────────────────────────────────────┘
    │
    ▼
Usuario recibe respuesta en: ~160ms
```

---

## 🧠 Mentalidad de Uso

### No pensar: "Voy a usar el agente"
### Pensar: "Tengo un co-fundador que sabe todo"

**Ejemplos de mindset shift:**

| Antes | Ahora con QL-Agent |
|-------|-------------------|
| "¿Cómo era el Strategy Pattern?" → Buscar en Google | "@ql explicar StrategyPattern en Concessio" → 200ms |
| "¿Qué cobro por Enterprise?" → Abrir Excel | "@ql precio Enterprise 3 usuarios" → 100ms |
| "¿Dónde está el TenantContext?" → IDE search | "@ql ubicación TenantContext" → 150ms |
| "¿Este código es seguro?" → Code review manual | "@ql revisar seguridad de este método" → 200ms |

**Resultado:** Tu velocidad de desarrollo/análisis se multiplica por 5-10x.

---

## ⚠️ Limitaciones Conscientes

### 1. **No Reemplaza el Pensamiento Crítico**
```
❌ "@ql decide si debo pivotar el negocio"
✅ "@ql analiza métricas de churn, yo decido"
```

### 2. **Contexto Limitado por Query**
```
❌ "@ql refactorizá todo el sistema" (demasiado amplio)
✅ "@ql refactorizá QuotationCalculationContext" (específico)
```

### 3. **No Tiene Estado Entre Conversaciones**
```
Vos: "@ql generar entity Lead"
Agente: *genera*

Vos: "agregale campo email"  
Agente: "¿Agregar a qué?" (no recuerda contexto previo)

✅ Solución: Thread de conversación o contexto explícito
```

### 4. **Precisión > Creatividad**
```
El agente es EXCELENTE para:
- Recordar facts (precios, estructura)
- Generar código boilerplate
- Analizar patrones existentes

El agente es MALO para:
- Inventar nuevas estrategias de negocio
- Diseñar arquitecturas desde cero
- Debugging complejo de bugs esotéricos
```

---

## 🚀 Próximos Pasos Conceptuales

Ahora que entendés el modelo mental, la implementación es:

1. **Instalar Ollama** (el "motor" de LLMs)
2. **Descargar los 3 especialistas** (cerebros específicos)
3. **Indexar tus documentos** (memoria del sistema)
4. **Crear el comando 'ql'** (interfaz de uso)

**Cada paso es independiente y testeable.**

---

## 📝 TL;DR - Resumen Visual

```
┌─────────────────────────────────────────────────────────┐
│  VOS (Founder/Dev)                                      │
│  Escribís: "precio plan Pro"                           │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│  ROUTER (1ms)                                           │
│  Detecta: NEGOCIO → Modelo: llama3.2:3b               │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│  RAG (30ms)                                             │
│  Busca en FAISS: "Plan Pro: $79/mes..."               │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│  LLM (150ms)                                            │
│  Genera: "Plan Pro cuesta $79/mes + $99 setup"        │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│  VOS recibe respuesta en ~200ms                         │
│  (vs 5 minutos buscando manualmente)                   │
└─────────────────────────────────────────────────────────┘

COSTO: $0
PRIVACIDAD: 100% local
VELOCIDAD: 15x más rápido que buscar manual
```

---

**¿Te hace sentido el modelo? ¿Empezamos con la implementación práctica?**
