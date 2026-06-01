# 🤖 QL-Agent V2 - Optimizado para Latencia y Coste

**Versión:** 2.0 (Optimización máxima)  
**Enfoque:** Sub-100ms respuestas, costo mínimo, calidad mantenida

---

## 📊 Benchmarks Objetivo

| Métrica | V1 (Básico) | V2 (Optimizado) | Mejora |
|---------|-------------|-----------------|--------|
| **Latencia promedio** | 2-5 segundos | 100-300ms | **15x más rápido** |
| **Costo mensual** | $20-50 (APIs) | $0-5 (local) | **90% ahorro** |
| **Throughput** | 10 req/min | 100 req/min | **10x más** |
| **Uso de RAM** | 8GB (codellama:13b) | 2GB (mix especializado) | **75% menos** |

---

## 🧠 Arquitectura de Modelos Especializados

### Enfoque: "Small Specialists > Large Generalist"

En lugar de 1 modelo grande (13B), usamos **3 modelos pequeños especializados** que corren en paralelo según la tarea:

```yaml
# Configuración de Modelos Optimizada
models:
  # 1. Code Qwen 2.5-Coder: 1.5B - Para código Java/Spring
  coder:
    model: qwen2.5-coder:1.5b
    ram: 600MB
    use_for: ["generar_codigo", "refactor", "tests", "debug"]
    latency: 50-150ms
    
  # 2. Llama 3.2: 3B - Para consultas de negocio/documentación  
  business:
    model: llama3.2:3b
    ram: 1.2GB
    use_for: ["pricing", "estimaciones", "soporte_cliente"]
    latency: 80-200ms
    
  # 3. Phi-3 Mini: 3.8B - Para análisis complejo
  analyst:
    model: phi3:mini
    ram: 1.5GB
    use_for: ["analisis_arquitectura", "seguridad", "code_review"]
    latency: 100-250ms
    
  # 4. Embedding local (opcional) - Para búsqueda semántica
  embedder:
    model: nomic-embed-text
    ram: 200MB
    use_for: ["busqueda_documentacion", "similaridad_codigo"]
```

**Total RAM requerida:** ~3.5GB (vs 8GB de un solo modelo 13B)

---

## ⚡ Estrategia de Routing Inteligente

### Clasificador de Intenciones (Ultra-rápido)

```python
# router.py - Clasificación en <10ms sin LLM
import re

INTENT_PATTERNS = {
    "CODIGO": r"\b(clase|método|function|import|spring|entity|repository|@)\b",
    "NEGOCIO": r"\b(precio|costo|plan|cotizar|sucursal|vendedora|lead)\b", 
    "ANALISIS": r"\b(arquitectura|seguridad|optimizar|review|performance)\b",
    "DOCUMENTACION": r"\b(donde|ubicado|está|archivo|readme|doc)\b"
}

def classify_intent(query: str) -> str:
    query_lower = query.lower()
    
    for intent, pattern in INTENT_PATTERNS.items():
        if re.search(pattern, query_lower):
            return intent
    
    return "DEFAULT"  # Llama 3.2:3b para casos mixtos

# Ejemplo:
# "generar repository para Lead" → CODIGO → qwen2.5-coder:1.5b
# "cuanto cuesta enterprise" → NEGOCIO → llama3.2:3b  
# "revisa la seguridad de TenantContext" → ANALISIS → phi3:mini
```

### Resultado: 0ms de overhead en routing

---

## 🚀 Optimización de Contexto (RAG Lite)

### Problema V1: Mandar documentos enteros al prompt
- API_DOC_TRADING_BOT.md = 50KB de contexto
- Tiempo de procesamiento: +2 segundos
- Costo: Alto (tokens de entrada)

### Solución V2: Vector DB local + Chunking

```python
# knowledge_indexer.py - Corre una vez al iniciar
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np

class KnowledgeIndex:
    def __init__(self):
        self.encoder = SentenceTransformer('all-MiniLM-L6-v2')  # 80MB
        self.index = faiss.IndexFlatL2(384)  # FAISS para búsqueda rápida
        self.chunks = []
        
    def index_documents(self, docs_path: str):
        """Indexa documentos markdown del proyecto"""
        for doc in glob(f"{docs_path}/*.md"):
            chunks = self.chunk_document(doc)  # Por secciones/párrafos
            embeddings = self.encoder.encode(chunks)
            
            self.index.add(np.array(embeddings))
            self.chunks.extend(chunks)
    
    def search(self, query: str, k: int = 3) -> list:
        """Búsqueda semántica en <50ms"""
        query_vec = self.encoder.encode([query])
        distances, indices = self.index.search(query_vec, k)
        
        return [self.chunks[i] for i in indices[0]]

# Uso:
# query = "¿Cuántos pares soporta Pro?"
# relevant_chunks = index.search(query, k=2)
# # Devuelve: ["Plan Pro: $79/mes, 2 pares...", "Modo AUTO_BEST: disponible en Pro..."]
# context = "\n".join(relevant_chunks)  # Solo 2KB de contexto relevante
```

### Beneficios:
- **Latencia:** Búsqueda <50ms vs leer archivo 500ms
- **Contexto:** Solo información relevante (2KB vs 50KB)
- **Calidad:** Respuestas más precisas (menos ruido)

---

## 💾 Caching Estratificado

### Nivel 1: Cache de Consultas Frecuentes (Redis/Memoria)

```python
from functools import lru_cache
import hashlib

class QueryCache:
    def __init__(self):
        self.cache = {}  # En producción: Redis local
        self.ttl = 3600  # 1 hora para consultas de negocio
        
    def get_or_compute(self, query: str, intent: str, compute_fn):
        # Hash de la query normalizada
        key = hashlib.md5(query.lower().strip().encode()).hexdigest()
        
        # Cache por tipo de intención
        cache_key = f"{intent}:{key}"
        
        if cache_key in self.cache:
            return self.cache[cache_key]
            
        result = compute_fn()
        self.cache[cache_key] = result
        return result

# Consultas que se cachean (no cambian seguido):
# - "precio plan Pro" → Cache 24h
# - "max usuarios Enterprise" → Cache 24h  
# - "estructura QuotationCalculation" → Cache 1h (código cambia)
```

### Nivel 2: Cache de Embeddings

```python
# Los embeddings de documentos se calculan una vez
# y se guardan en disco (~5MB total)

# Inicio del agente:
if os.path.exists("embeddings_cache.pkl"):
    index.load("embeddings_cache.pkl")  # <1 segundo
else:
    index.index_documents("/docs")  # 30 segundos una vez
    index.save("embeddings_cache.pkl")
```

### Hit Rate Esperado: 60-80% de consultas cacheadas

---

## 🔧 Pipeline Optimizado de Respuesta

```
┌─────────────────────────────────────────────────────────────────┐
│                    PIPELINE QL-AGENT V2 (<300ms total)           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. Ruteo (0-10ms)                                              │
│     └── Regex classifier → Selecciona modelo                    │
│                                                                 │
│  2. Cache Check (0-5ms)                                         │
│     └── ¿Consulta frecuente? → Retorna inmediato                │
│                                                                 │
│  3. Context Retrieval (20-50ms)                                 │
│     └── FAISS search → Top 3 chunks relevantes                │
│                                                                 │
│  4. Inference (50-200ms dependiendo modelo)                     │
│     └── Modelo especializado con contexto mínimo                │
│                                                                 │
│  5. Post-processing (0-10ms)                                    │
│     └── Formateo, validación, guardar en cache                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Tiempo total: 100-300ms** (vs 2000-5000ms de V1)

---

## 💰 Estrategia de Costo Cero (Ollama 100%)

### Escenarios y Routing de Coste

| Escenario | Técnica | Costo | Latencia |
|-----------|---------|-------|----------|
| **Desarrollo local** | Ollama (Qwen 1.5B) | $0 | 100ms |
| **Consulta documentación** | Vector search + Llama 3.2:3B | $0 | 150ms |
| **Análisis complejo** | Phi-3 Mini local | $0 | 200ms |
| **Emergencia (fuera de casa)** | GPT-4o-mini API | $0.01/query | 1s |
| **Batch processing (noche)** | Ollama con modelos grandes | $0 | sin prisa |

### API solo como Fallback

```python
class CostOptimizedRouter:
    def __init__(self):
        self.local_models = {
            'CODIGO': 'qwen2.5-coder:1.5b',
            'NEGOCIO': 'llama3.2:3b',
            'ANALISIS': 'phi3:mini'
        }
        self.api_client = OpenAI(api_key=os.getenv("OPENAI_KEY"))
        self.api_budget_daily = 1.0  # $1 USD máximo por día
        self.api_spent_today = 0.0
        
    async def query(self, text: str, priority: str = "normal"):
        intent = classify_intent(text)
        
        # 99% de queries: local (gratis)
        if priority == "normal":
            model = self.local_models.get(intent, 'llama3.2:3b')
            return await self.ollama_query(model, text)
        
        # 1% urgente/fuera de casa: API (pago)
        elif priority == "urgent" and self.api_spent_today < self.api_budget_daily:
            cost = await self.api_query(text)
            self.api_spent_today += cost
            return cost
            
        # Si se acabó budget: usa modelo local más grande
        else:
            return await self.ollama_query('llama3.1:8b', text)  # Más lento pero gratis
```

---

## 🎯 Configuración de Ollama Optimizada

### Modelfile de Performance

```dockerfile
# qwen-coder-fast.modelfile
FROM qwen2.5-coder:1.5b

# Parámetros optimizados para velocidad
PARAMETER temperature 0.1      # Más determinista, más rápido
PARAMETER top_p 0.5            # Muestreo conservador
PARAMETER num_ctx 2048         # Contexto pequeño = rápido
PARAMETER num_predict 512      # Respuestas cortas y concisas

# GPU aceleración si disponible
PARAMETER num_gpu 999          # Usa toda la GPU disponible
PARAMETER num_thread 4         # Ajustar a tus cores CPU

SYSTEM """Eres el asistente de código de QLSoftware Studio.
Stack: Spring Boot, Java 21, PostgreSQL.

REGLAS DE VELOCIDAD:
1. Respuestas concisas (<100 palabras)
2. Mostrar solo código esencial
3. No explicar lo obvio
4. Usar bullet points, no párrafos

FORMATO:
- Archivo: path
- Cambio: diff breve
- Comando: uno solo"""
```

### Script de Inicio Optimizado

```bash
#!/bin/bash
# start-ql-agent.sh

echo "🚀 Iniciando QL-Agent V2 (Optimizado)"

# 1. Precargar modelos en RAM (evita cold start)
echo "📦 Cargando modelos especializados..."
ollama run qwen2.5-coder:1.5b "hello" &>/dev/null &
ollama run llama3.2:3b "hello" &>/dev/null &
ollama run phi3:mini "hello" &>/dev/null &

# 2. Precargar embeddings en memoria
echo "📚 Indexando conocimiento..."
python3 -c "
from knowledge_indexer import KnowledgeIndex
index = KnowledgeIndex()
index.load('embeddings_cache.pkl')
print('✅ Knowledge base cargada')
" &

# 3. Iniciar API del agente
echo "🌐 Iniciando API..."
uvicorn ql_agent_api:app --host 0.0.0.0 --port 11434 --workers 1
```

---

## 📱 Integración Ultra-rápida con tu Workflow

### Opción 1: Extension VS Code (Local)

```typescript
// ql-agent-extension.ts
// Comunicación directa con Ollama local (localhost:11434)

class QLAgentExtension {
    private ollamaUrl = 'http://localhost:11434/api/generate';
    
    async onAskCommand() {
        const editor = vscode.window.activeTextEditor;
        const selectedCode = editor.document.getText(editor.selection);
        
        // 1. Detectar contexto del archivo abierto
        const fileContext = this.detectContext(editor.document.fileName);
        
        // 2. Query optimizada (<100 tokens de contexto)
        const prompt = this.buildPrompt(selectedCode, fileContext);
        
        // 3. Llamada directa a Ollama
        const response = await fetch(this.ollamaUrl, {
            method: 'POST',
            body: JSON.stringify({
                model: 'qwen2.5-coder:1.5b',
                prompt: prompt,
                stream: false,
                options: { temperature: 0.1, num_predict: 200 }
            })
        });
        
        // 4. Mostrar inline (<1s total)
        this.showInlineSuggestion(response.text);
    }
    
    buildPrompt(code: string, context: string): string {
        return `
Archivo: ${context.file}
Tipo: ${context.type}

Código:
${code.slice(0, 500)}

Instrucción: ${this.getUserIntent()}

Respuesta corta (solo código o bullets):
`;
    }
}
```

**Tiempo de respuesta: 200-400ms** (parecido a GitHub Copilot)

### Opción 2: CLI Tool (Terminal)

```bash
# ql (comando personalizado)
#!/bin/bash

query="$*"

# Clasificar y rutear
if echo "$query" | grep -qi "\b(código\|clase\|método\|function\|import)\b"; then
    model="qwen2.5-coder:1.5b"
elif echo "$query" | grep -qi "\b(precio\|plan\|costo\|cotizar)\b"; then
    model="llama3.2:3b"
else
    model="phi3:mini"
fi

# Query a Ollama
curl -s http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d "{
    \"model\": \"$model\",
    \"prompt\": \"$query\",
    \"stream\": false,
    \"options\": { \"temperature\": 0.1, \"num_predict\": 300 }
  }" | jq -r '.response'

# Uso:
# $ ql "generar repository para Lead"
# $ ql "cuanto cuesta Enterprise"
# $ ql "revisar seguridad TenantContext"
```

### Opción 3: Telegram Bot (Móvil)

```python
# Cuando estás afuera y necesitas consultar algo
# El bot usa el agente local (si PC prendida) o API fallback

@bot.message_handler(func=lambda msg: True)
def handle_message(message):
    query = message.text
    
    # Intentar local primero (más rápido, gratis)
    try:
        response = requests.post(
            "http://TU_IP_LOCAL:11434/api/generate",
            json={"model": "llama3.2:3b", "prompt": query},
            timeout=2  # 2 segundos máximo
        )
        return response.json()['response']
    except:
        # Fallback a API (lento, pago, pero funciona)
        return openai_client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[{"role": "user", "content": query}]
        )
```

---

## 📊 Comparativa: V1 vs V2 vs Copilot

| Feature | QL-Agent V1 | QL-Agent V2 | GitHub Copilot |
|---------|-------------|-------------|----------------|
| **Costo mensual** | $20-50 | **$0-2** | $10-20 |
| **Latencia código** | 3-5s | **100-200ms** | 300-800ms |
| **Conocimiento QL** | ✅ Total | ✅ Total | ❌ Ninguno |
| **Privacidad código** | ✅ 100% local | ✅ 100% local | ❌ Nube |
| **Offline** | ✅ Sí | ✅ Sí | ❌ No |
| **Personalización** | ✅ Modelfile | ✅ Modelfile | ❌ Limitada |

**V2 gana en todo excepto en "requiere setup inicial"**

---

## 🚀 Plan de Implementación (2 horas)

### Paso 1: Setup Base (30 min)
```bash
# Instalar Ollama

curl -fsSL https://ollama.com/install.sh | sh

# Descargar modelos livianos
ollama pull qwen2.5-coder:1.5b
ollama pull llama3.2:3b
ollama pull phi3:mini
ollama pull nomic-embed-text

# Verificar instalación
ollama list
# Debería mostrar 4 modelos, ~3GB total
```

### Paso 2: Crear Modelfiles (20 min)
```bash
# Crear modelfiles optimizados
mkdir -p ~/.ollama/models/ql-studio

# Código: ultra-rápido
cat > ~/.ollama/models/ql-studio/coder.modelfile << 'EOF'
FROM qwen2.5-coder:1.5b
PARAMETER temperature 0.1
PARAMETER num_ctx 2048
PARAMETER num_predict 400
PARAMETER num_thread 4
SYSTEM "Asistente código QLSoftware. Respuestas ultra-concisas. Solo código relevante."
EOF

# Negocio: conversacional pero directo  
cat > ~/.ollama/models/ql-studio/business.modelfile << 'EOF'
FROM llama3.2:3b
PARAMETER temperature 0.3
PARAMETER num_ctx 4096
PARAMETER num_predict 200
SYSTEM "Asistente negocio QLSoftware. Precio en USD. Directo al punto. Datos de PRICING_PLANES.md y PROPUESTA_TECNICA."
EOF

ollama create ql-coder -f ~/.ollama/models/ql-studio/coder.modelfile
ollama create ql-business -f ~/.ollama/models/ql-studio/business.modelfile
```

### Paso 3: Indexar Documentos (30 min)
```bash
# Instalar dependencias
pip install sentence-transformers faiss-cpu numpy

# Crear script de indexación
cat > ~/index-ql-docs.py << 'PYEOF'
import os
from sentence_transformers import SentenceTransformer
import faiss
import pickle
from glob import glob

def chunk_markdown(file_path, chunk_size=500):
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Split por secciones (##)
    sections = content.split('\n## ')
    chunks = []
    
    for section in sections:
        if len(section) > chunk_size:
            # Split further por párrafos
            paragraphs = section.split('\n\n')
            current_chunk = ""
            for p in paragraphs:
                if len(current_chunk) + len(p) < chunk_size:
                    current_chunk += p + "\n\n"
                else:
                    chunks.append(current_chunk)
                    current_chunk = p + "\n\n"
            if current_chunk:
                chunks.append(current_chunk)
        else:
            chunks.append(section)
    
    return chunks

# Indexar
docs_path = "/home/emilio/Escritorio/quilodranEmilioBackUp/emilioquilodran/qlsoftwarestudio/giamma-360-backend"
encoder = SentenceTransformer('all-MiniLM-L6-v2')

all_chunks = []
all_embeddings = []

for doc in glob(f"{docs_path}/*.md"):
    print(f"Indexando: {os.path.basename(doc)}")
    chunks = chunk_markdown(doc)
    embeddings = encoder.encode(chunks)
    
    all_chunks.extend(chunks)
    all_embeddings.extend(embeddings)

# Guardar índice FAISS
dimension = len(all_embeddings[0])
index = faiss.IndexFlatL2(dimension)
index.add(np.array(all_embeddings))

faiss.write_index(index, "ql_docs.index")
with open("ql_chunks.pkl", "wb") as f:
    pickle.dump(all_chunks, f)

print(f"✅ Indexados {len(all_chunks)} chunks de {len(glob(f'{docs_path}/*.md'))} documentos")
PYEOF

python3 ~/index-ql-docs.py
```

### Paso 4: CLI Tool (20 min)
```bash
# Crear comando 'ql'
cat > /usr/local/bin/ql << 'EOF'
#!/bin/bash
QUERY="$*"

# Clasificación simple
if echo "$QUERY" | grep -qiE "(código|clase|método|function|import|spring|entity|repository|@|test)"; then
    MODEL="ql-coder"
elif echo "$QUERY" | grep -qiE "(precio|plan|costo|cotizar|sucursal|vendedora|lead|cliente|venta)"; then
    MODEL="ql-business"
else
    MODEL="llama3.2:3b"
fi

echo "🤖 Modelo: $MODEL"
echo "---"

curl -s http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d "{
    \"model\": \"$MODEL\",
    \"prompt\": \"$QUERY\",
    \"stream\": false,
    \"options\": { \"temperature\": 0.2, \"num_predict\": 400 }
  }" | jq -r '.response' 2>/dev/null || echo "❌ Error: ¿Ollama corriendo?"
EOF

chmod +x /usr/local/bin/ql

# Probar
ql "generar entity Lead con campos basicos"
ql "precio plan Pro"
```

### Paso 5: Test de Performance (20 min)
```bash
# Benchmark simple
echo "Test 1: Código (generar método)"
time ql "generar método findByEmail en UserRepository"

echo -e "\nTest 2: Negocio (pricing)"
time ql "cuanto cuesta Enterprise con 3 usuarios"

echo -e "\nTest 3: Análisis (arquitectura)"
time ql "explicar flujo multi-tenant en Concessio"
```

**Resultados esperados:**
- Código: 100-200ms
- Negocio: 80-150ms  
- Análisis: 150-300ms

---

## ✅ Checklist Final

- [ ] Ollama instalado y corriendo
- [ ] 4 modelos descargados (~3GB)
- [ ] Modelfiles personalizados creados
- [ ] Documentos indexados (FAISS)
- [ ] Comando `ql` funciona en terminal
- [ ] Tiempo de respuesta <300ms promedio
- [ ] Costo mensual: $0

**¡Listo para usar!**

---

## 🎓 Tips de Uso Diario

### 1. Contexto Implícito
```bash
# Desde carpeta del proyecto
cd /proyectos/concessio-crm
ql "agregar campo telefono al lead"
# El agente detecta el contexto y genera:
# - Migration SQL
# - Entity field
# - DTO update
# - Validation
```

### 2. Chaining de Consultas
```bash
# Paso 1: Entender
ql "explicar StrategyPattern en QuotationCalculation"

# Paso 2: Extender  
ql "crear nueva estrategia DiscountStrategy usando ese patrón"

# Paso 3: Testear
ql "generar test unitario para DiscountStrategy"
```

### 3. Atajos de Keyboard (VS Code)
```json
// keybindings.json
{
  "key": "ctrl+shift+q",
  "command": "workbench.action.terminal.sendSequence",
  "args": { "text": "ql \"${selectedText}\"\n" }
}
```

**Seleccionás código → Ctrl+Shift+Q → Respuesta inmediata**

---

*Optimizado para: Velocidad > Costo > Calidad (en ese orden)*
