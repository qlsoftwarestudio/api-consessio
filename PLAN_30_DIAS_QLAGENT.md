e# 🚀 Plan 30 Días: De 0 a Empresa de Software con QL-Agent

**Situación:** Primera empresa de desarrollo + Primera vez usando agentes IA  
**Objetivo:** Generar ingresos inmediatos + Establecer base sólida  
**Ventaja:** QL-Agent como multiplicador de productividad (3-5x)  
**Meta Mes 1:** $2,000-5,000 USD en ingresos + 2-3 clientes activos

---

## 🎯 Filosofía del Plan

### "Crawl, Walk, Run" con Agentes

| Fase | Días | Enfoque | Ingresos Meta |
|------|------|---------|---------------|
| **CRAWL** | 1-7 | Setup + Quick Wins (servicios) | $500-1,000 |
| **WALK** | 8-14 | Productización + Ventas activas | $1,000-2,000 |
| **RUN** | 15-21 | Delivery + Referencias | $2,000-3,500 |
| **FLY** | 22-30 | Escalado + Automatización | $3,500-5,000 |

### Principios No Negociables

1. **Ingresos > Perfección:** Mejor un MVP vendido que un producto perfecto en local
2. **Agentes desde Día 1:** Usar QL-Agent para TODO (prospección, código, documentación)
3. **Un cliente feliz > 10 leads fríos:** Calidad sobre cantidad inicialmente
4. **Reutilizar > Crear desde 0:** Concessio como base para proyectos similares
5. **Documentar > Recordar:** Todo proceso va a QL-Agent knowledge base

---

## 📅 SEMANA 1: CRAWL - Setup + Quick Wins (Días 1-7)

**Objetivo:** Tener sistema funcionando + Primeros ingresos + Validar modelo

### Día 1: Setup Mínimo Viable (4-6 horas)

#### Mañana (2-3h): Infraestructura
```bash
# 1. Setup QL-Agent (básico, no el sistema completo aún)
curl -fsSL https://ollama.com/install.sh | sh
ollama pull llama3.2:3b          # Negocio/Código
ollama pull qwen2.5-coder:1.5b   # Desarrollo

# 2. Crear comando 'ql'
cat > /usr/local/bin/ql << 'EOF'
#!/bin/bash
query="$*"
if echo "$query" | grep -qiE "(código|clase|método|function)"; then
    model="qwen2.5-coder:1.5b"
else
    model="llama3.2:3b"
fi
curl -s http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d "{\"model\": \"$model\", \"prompt\": \"$query\", \"stream\": false, \"options\": {\"temperature\": 0.2}}" | jq -r '.response'
EOF
chmod +x /usr/local/bin/ql

# 3. Setup GitHub + Railway
# - Crear repo qlsoftware-studio
# - Conectar Railway a GitHub
# - Setup PostgreSQL en Railway
```

#### Tarde (2-3h): Productos Listos para Vender
```
Tienes 2 productos documentados:
1. Concessio CRM (Concesionarias Fiat)
2. QL Trading Bot (Traders crypto)

Acciones:
□ Subir Concessio a Railway (deploy de demo)
□ Crear landing page simple (HTML + Tailwind, 1 página)
□ Preparar pitch deck (3 slides)
```

**Comando útil:**
```bash
# Generar landing page con QL-Agent
ql "generar HTML landing page para Concessio CRM, incluir: hero, features, pricing CTA, formulario contacto. Estilo moderno, Tailwind CSS"
```

**Output esperado:** `landing-concessio.html` funcional

---

### Día 2: Prospección Masiva (4-6 horas)

#### Mañana: Identificar Targets
```bash
# Crear lista de prospectos (100 concesionarias)
# Fuentes: Google Maps "Concesionaria Fiat Buenos Aires", LinkedIn, Instagram

# Estructura CSV:
# nombre, dirección, teléfono, email, sitio_web, red_social, notas
```

**QL-Agent te ayuda:**
```bash
# Analizar cada prospecto
ql "analizar si concesionaria 'AutoSur Fiat' necesita CRM. Señales: sitio web antiguo, no tienen sistema de leads online, solo WhatsApp. Puntuar 1-10"
```

#### Tarde: Primer Contacto (10-20 mensajes)
```
Template mensaje (personalizar con QL-Agent):

"Hola [nombre], soy Emilio de QLSoftware Studio.
Veo que [concesionaria] está creciendo fuerte.
Desarrollamos Concessio CRM, específico para concesionarias Fiat.
AutoX redujo 40% tiempo de venta y aumentó 25% conversión.
¿Te interesaría una demo de 15 min sin compromiso?
Saludos,
Emilio"
```

**Acciones concretas:**
- Enviar 10 mensajes LinkedIn
- Enviar 10 emails (buscar en sitios web)
- Llamar 5 concesionarias (pitch de 30 segundos)

**Meta:** 3 respuestas interesadas

---

### Día 3-4: Seguimiento + Propuestas (6-8 horas)

#### Responder Interesados
```
Por cada respuesta positiva:
1. Agendar demo 15 min (Calendly)
2. Preparar demo personalizada (usar Concessio deployado)
3. Preparar propuesta preliminar
```

**QL-Agent genera propuesta:**
```bash
ql "generar propuesta técnica para Concessio CRM. Cliente: [nombre concesionaria], 1 sucursal, 5 vendedores, necesitan: leads, cotizaciones, test drives. Formato: 1 página, precio $8,000 ARS/mes o $600 USD setup + $100/mes"
```

**Output:** `propuesta-{cliente}.pdf` (generar con HTML → PDF)

#### Primer Cierre (meta: 1 cliente esta semana)
```
Estrategía de cierre rápido:
- Descuento 20% por "cliente fundador" (primero)
- Pago 50% upfront, 50% al entregar
- Scope reducido: MVP esencial (leads + cotizaciones)
- Timeline: 2 semanas
```

**Precio Quick Win:** $400-800 USD (setup) + $50-100/mes

---

### Día 5-6: Delivery del Primer Cliente (8-10 horas)

#### Setup Concessio para Cliente
```bash
# 1. Clonar Concessio base
git clone [repo-concessio] cliente-1
cd cliente-1

# 2. Personalizar (con ayuda de QL-Agent)
ql "adaptar Concessio para concesionaria 'AutoSur'. Cambiar: logo, colores a azul #0066CC, nombre tenant, quitar feature X si no la necesitan"

# 3. Deploy a Railway
railway login
railway init
railway up
```

#### Configuración Esencial
- [ ] Tenant configurado (AutoSur)
- [ ] Usuario admin creado
- [ ] 3 vendedores dados de alta
- [ ] 5 vehículos de ejemplo cargados
- [ ] Formulario de leads en su sitio web

**Time to first value:** 2-3 días (cliente ve leads entrando)

---

### Día 7: Review + Documentar (4 horas)

#### Retro Semana 1
```
¿Qué funcionó?
- Canales de prospección más efectivos
- Objetiones comunes
- Tiempo real de implementación

¿Qué no funcionó?
- Ajustar pitch
- Mejorar propuesta
```

#### Documentar en QL-Agent Knowledge Base
```bash
# Indexar todo lo aprendido
# - Propuestas enviadas
# - Código generado
# - Procesos que funcionaron

# Script de indexación (para futura referencia)
python index-docs.py
```

**Meta Semana 1:**
- ✅ 1 cliente pagando $400-800 setup
- ✅ Sistema QL-Agent funcionando
- ✅ Pipeline de prospección establecido
- ✅ Landing page + pitch deck listos

---

## 📅 SEMANA 2: WALK - Ventas Activas (Días 8-14)

**Objetivo:** 2-3 leads calificados + 1-2 propuestas enviadas + Sistema agentes mejorado

### Día 8-9: Mejorar QL-Agent (6-8 horas)

#### Agregar Especialista "Prospector"
```bash
# Crear Modelfile para prospección
cat > ~/prospector.modelfile << 'EOF'
FROM llama3.2:3b

SYSTEM """Eres ProspectorAgent de QLSoftware Studio.
Tarea: Generar mensajes de outreach personalizados para concesionarias Fiat.

INPUT: Nombre concesionaria, sitio web, señales de necesidad
OUTPUT: Mensaje personalizado 3-4 oraciones, tono profesional pero cercano

REGLAS:
- Mencionar señal específica de su sitio/social media
- Incluir beneficio cuantificado (ej: 40% más rápido)
- CTA claro pero no agresivo
- Max 100 palabras"""
EOF

ollama create prospector -f ~/prospector.modelfile
```

#### Agregar Especialista "Estimator"
```bash
# Para estimar proyectos rápido
cat > ~/estimator.modelfile << 'EOF'
FROM qwen2.5-coder:1.5b

SYSTEM """Eres EstimatorAgent de QLSoftware Studio.
Calculas esfuerzo de desarrollo en horas.

ESTIMACIÓN:
- Entidad simple (CRUD): 4h (entity + repo + service + controller + tests)
- Feature con lógica: 8-16h
- Integración API externa: 8-12h
- UI Component (React): 2-4h
- Testing completo: 30% del desarrollo

OUTPUT:
- Total horas estimadas
- Precio (rate $30-50/h)
- Timeline semanas
- Riesgos identificados"""
EOF

ollama create estimator -f ~/estimator.modelfile
```

---

### Día 10-11: Prospección Escala (6-8 horas)

#### Campaña LinkedIn + Email (50 contactos)
```bash
# Usar ProspectorAgent para personalizar cada mensaje

for prospect in lista-prospectos.csv; do
    ql -m prospector "generar mensaje para $prospect, señal: sitio web no tiene formulario de contacto, solo teléfono"
done
```

#### Estrategía "Landed" (Contactos cálidos)
```
- Preguntar a amigos/familia si conocen concesionarios
- Postear en grupos de Facebook de emprendedores
- Comentar en posts de concesionarias con valor agregado
- Ofrecer "auditoría gratuita de CRM" (15 min call)
```

**Meta:** 5-10 calls agendadas para esta semana

---

### Día 12-13: Demos + Propuestas (8-10 horas)

#### Estructura Demo 15 Minutos
```
1. Hook (1 min): "AutoSur aumentó ventas 25% en 3 meses"
2. Problema (2 min): "Mostrar situación actual del cliente"
3. Solución (8 min): Demo Concessio en vivo
   - Crear lead
   - Asignar a vendedor
   - Cotizar vehículo
   - Ver dashboard
4. Precio (2 min): "Setup $600, $80/mes, ROI en 2 meses"
5. Cierre (2 min): "¿Empezamos la próxima semana?"
```

**QL-Agent prepara cada demo:**
```bash
ql "preparar demo personalizada para [concesionaria]. Research: tienen 2 sucursales, 8 vendedores, problema: leads se pierden en WhatsApp. Enfocar demo en: asignación automática, seguimiento estructurado, notificaciones"
```

#### Propuestas Enviadas
- Meta: 2-3 propuestas formales
- Incluye: Scope, Timeline, Precio, Términos
- QL-Agent genera el documento

---

### Día 14: Cierre + Onboarding (6-8 horas)

#### Negociación
```
Tácticas de cierre:
- "Precio especial fundador" (15% off si cierran esta semana)
- Pago flexible: 50% inicio, 25% demo, 25% final
- Garantía: 30 días money-back si no ven valor
```

**Meta:** Cerrar 2do cliente ($600-1,000 setup)

#### Onboarding Automatizado
```bash
# Checklist para nuevo cliente
□ Contrato firmado (DocuSign)
□ Pago recibido (Stripe)
□ Tenant creado en Concessio
□ Usuarios configurados
□ Training call agendada (30 min)
□ Documentación enviada
```

**QL-Agent genera:**
- Bienvenida email personalizado
- Guía de inicio rápido (PDF)
- Video Loom explicando sistema

---

## 📅 SEMANA 3: RUN - Delivery + Referencias (Días 15-21)

**Objetivo:** Entregar proyectos + Generar referencias + Upsells

### Día 15-17: Delivery Intensivo (10-12 horas)

#### Cliente 1: MVP Completo
```
Features a entregar:
✅ Leads pipeline (Nuevo → Contactado → Cotizado → Cerrado)
✅ Asignación automática a vendedores
✅ Cotizaciones con PDF
✅ Dashboard básico
✅ Notificaciones email
```

**Con ayuda de QL-Agent:**
```bash
# Generar feature faltante
ql "generar servicio de asignación automática de leads en Spring Boot. Reglas: round-robin entre vendedores activos, balancear carga, notificar por email"

# Generar tests
ql "generar tests de integración para LeadAssignmentService. Usar TestContainers, probar asignación round-robin, probar notificaciones"
```

#### Cliente 2: Setup Inicial
- Reutilizar 80% del código del Cliente 1
- Personalizar branding
- Migrar datos si tienen Excel

---

### Día 18-19: Generar Referencias (6-8 horas)

#### Estrategía "Case Study"
```
Pedir a Cliente 1:
- Testimonial de 2-3 oraciones
- Permiso para usar logo en landing
- Video testimonial de 1 min (ofrecer 1 mes gratis)

Ofrecer:
- 1 mes gratis por referral que cierre
- Feature prioritario gratis
- Soporte prioritario
```

#### Mejorar Landing con Social Proof
```bash
ql "actualizar landing page Concessio con: testimonial de AutoSur, logo de cliente, estadísticas reales (25% más ventas), case study breve"
```

---

### Día 20-21: Upsell + Nuevo Producto (8-10 horas)

#### Upsells a Clientes Existentes
```
Ofrecer a Cliente 1:
- Módulo WhatsApp Business ($200 setup)
- Módulo Test Drive ($150 setup)
- Módulo Documentos Digitales ($150 setup)
- Integración con sistema contable ($300)

Total upsell potencial: $500-800
```

#### Preparar QL Trading Bot para Venta
```
Parallel track: Empezar a prospectar traders
- Grupos de Facebook/Discord de trading
- Instagram traders con poca tecnología
- Foros de inversión

Pitch diferente:
"Bot de trading automatizado, 65% win rate promedio, 
multi-pair, notificaciones Telegram. 
Setup $99, $29/mes."
```

---

## 📅 SEMANA 4: FLY - Optimización + Escalado (Días 22-30)

**Objetivo:** Sistematizar + Documentar + Preparar mes 2

### Día 22-24: Automatizar con QL-Agent V2 (8-10 horas)

#### Implementar 3 Agentes Clave
```
1. ProspectorAgent (automatizado)
   - Scrapea LinkedIn diariamente
   - Genera mensajes personalizados
   - Agenda follow-ups

2. EstimatorAgent (instantáneo)
   - Recibe requerimientos
   - Genera estimación en 2 minutos
   - Crea propuesta automática

3. DeveloperAgent (asistido)
   - Genera boilerplate SOLID
   - Sugiere refactorings
   - Crea tests automáticos
```

**Setup básico (no el sistema completo):**
```bash
# Crear scripts Python simples

# prospector.py
# - Lee CSV de prospectos
# - Llama Ollama para generar mensajes
# - Guarda en output/

# estimator.py
# - Recibe descripción de feature
# - Calcula horas basado en templates
# - Genera propuesta HTML

# developer.py
# - Recibe "generar entity X"
# - Llama qwen-coder
# - Guarda archivos en estructura correcta
```

---

### Día 25-27: Documentar Procesos (6-8 horas)

#### Playbooks de QL-Agent
```
Crear 3 documentos maestros:

1. PLAYBOOK_VENTAS.md
   - Scripts de prospección
   - Respuestas a objeciones
   - Proceso demo
   - Tácticas de cierre

2. PLAYBOOK_DELIVERY.md
   - Checklist de implementación
   - Templates de código
   - Testing checklist
   - Deploy process

3. PLAYBOOK_AGENTES.md
   - Prompts optimizados
   - Comandos útiles
   - Troubleshooting
```

#### Indexar Todo en QL-Agent
```bash
# Toda la documentación ahora es "memoria" del agente
python index-docs.py --include playbooks/ --include propuestas/ --include codigo-generado/
```

---

### Día 28-30: Planificación Mes 2 (6-8 horas)

#### Métricas del Mes 1
```
Revisar:
- Total ingresos: $____
- Clientes nuevos: ____
- Leads generados: ____
- Conversion rate: ____%
- Horas trabajadas: ____
- Hourly rate efectivo: $____
```

#### Proyección Mes 2
```
Escenarios:

Conservador (replicar mes 1):
- 2 nuevos clientes Concessio: $1,200
- Upsells clientes actuales: $600
- Trading Bot 1 cliente: $400
- Total: $2,200

Moderado (mejorar):
- 3 nuevos clientes Concessio: $1,800
- Sistema QL-Agent vendido a 1 agencia: $2,000
- Trading Bot 3 clientes: $1,200
- Total: $5,000

Optimista (escalar):
- 4 nuevos clientes Concessio: $2,400
- Partnership con 1 concesionaria grande: $5,000
- Trading Bot 5 clientes: $2,000
- Total: $9,400
```

#### Acciones Mes 2
```
□ Implementar QL-Agent completo (12 sub-agentes)
□ Contratar/Partner para delivery (si volumen alto)
□ Lanzar QL Trading Bot oficialmente
□ Webinar gratuito: "Cómo digitalizar tu concesionaria"
□ Partnership con 2-3 agencias de autos usados
```

---

## 💰 Finanzas y Métricas

### Proyección de Ingresos (Mes 1)

| Semana | Ingresos | Acumulado | Fuente |
|--------|----------|-----------|--------|
| 1 | $500-800 | $500-800 | 1er cliente (setup) |
| 2 | $600-1,000 | $1,100-1,800 | 2do cliente |
| 3 | $200-400 | $1,300-2,200 | Upsells |
| 4 | $400-800 | $1,700-3,000 | 3er cliente + mantenimiento |

**Total Mes 1:** $1,700 - $3,000 USD

### Estructura de Costes

| Concepto | Costo |
|----------|-------|
| Infraestructura (Railway) | $20-50/mes |
| Ollama (local) | $0 |
| APIs (Resend, Calendly) | $10-30/mes |
| DocuSign/Stripe | Variable |
| **Total fijo** | **$30-80/mes** |

**Margen:** 90-95% (software + agentes locales)

---

## 🛠️ Herramientas y Recursos

### Stack Tecnológico (Mínimo)
```
Desarrollo:
- VS Code + Extensiones (Git, REST Client)
- GitHub (repositorios)
- Railway (deploy)
- PostgreSQL (base de datos)

Agentes IA:
- Ollama (local)
- llama3.2:3b (negocio)
- qwen2.5-coder:1.5b (código)

Ventas:
- LinkedIn (gratuito)
- Calendly (gratuito)
- Resend (100 emails/día gratis)
- Canva (presentaciones)

Productividad:
- Notion (documentación)
- Loom (videos)
- DocuSign (contratos)
- Stripe (pagos)
```

### Templates Generados por QL-Agent

#### 1. Propuesta Técnica
```html
<!-- Generado por: ql "generar propuesta HTML para Concessio" -->
<!DOCTYPE html>
<html>
<head><title>Propuesta Concessio - {cliente}</title></head>
<body>
  <h1>Propuesta: Implementación Concessio CRM</h1>
  <h2>Cliente: {nombre}</h2>
  
  <h3>Problema Actual</h3>
  <p>Leads se pierden, no hay seguimiento estructurado...</p>
  
  <h3>Solución Propuesta</h3>
  <ul>
    <li>Pipeline de leads automatizado</li>
    <li>Cotizaciones instantáneas</li>
    <li>Dashboard gerencial</li>
  </ul>
  
  <h3>Inversión</h3>
  <table>
    <tr><td>Setup</td><td>$600 USD</td></tr>
    <tr><td>Mensual</td><td>$80 USD</td></tr>
    <tr><td>ROI esperado</td><td>3 meses</td></tr>
  </table>
  
  <h3>Timeline</h3>
  <p>2 semanas desde aprobación</p>
  
  <button>Aceptar Propuesta</button>
</body>
</html>
```

#### 2. Email de Follow-up
```
Asunto: Siguiente pasos - Concessio CRM

Hola {nombre},

Gracias por la reunión de ayer.

Como acordamos, te envío:
1. Propuesta técnica adjunta
2. Demo grabada: [link Loom]
3. Case study de AutoSur (+25% ventas)

Timeline propuesto:
- Aprobación: Esta semana
- Inicio desarrollo: Lunes próximo
- Go-live: 2 semanas después

¿Tienes preguntas sobre la propuesta?
Estoy disponible mañana 10am o 3pm.

Saludos,
Emilio
QLSoftware Studio
```

#### 3. Código Boilerplate (SOLID)
```java
// Generado por: ql "generar entity Lead con validaciones JPA"

@Entity
@Table(name = "leads")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String firstName;
    
    @Column(nullable = false)
    @NotBlank(message = "Apellido es obligatorio")
    private String lastName;
    
    @Column(nullable = false, unique = true)
    @Email(message = "Email inválido")
    private String email;
    
    @Column
    @Pattern(regexp = "^\\+?[0-9\\s-]{10,20}$")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status = LeadStatus.NUEVO;
    
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

---

## 🚨 Troubleshooting Común

### Problema: No hay respuestas a prospecting
```
Soluciones:
1. Mejorar targeting (más específico, no genérico)
2. Personalizar más (mencionar algo específico de ellos)
3. Cambiar canal (LinkedIn → Email → Llamada)
4. Ofrecer valor primero (auditoría gratuita)
5. Seguimiento 3-4 veces (80% de ventas en follow-up)
```

### Problema: Cliente dice "Es caro"
```
Respuestas:
- "Entiendo. Comparado con un empleado ($2,000/mes), esto es $80/mes"
- "Hagamos MVP por $400 y escalamos"
- "ROI es 2-3 meses, te muestro cálculo?"
- "Oferta fundador: 20% off si cierras esta semana"
```

### Problema: No tengo tiempo para implementar
```
Solución: Usar QL-Agent para acelerar 3-5x
- Código generado automáticamente
- Tests automáticos
- Documentación automática
- Personalización en horas, no días
```

### Problema: Ollama es lento
```
Optimizaciones:
1. Usar modelos más pequeños (1.5B en vez de 13B)
2. Mantener modelo cargado en RAM (keep_alive)
3. Paralelizar requests
4. Cachear respuestas frecuentes
5. Usar GPU si disponible
```

---

## 🎯 Checklist Diario (Mínimo Viable)

### Todos los días (30 min)
- [ ] Revisar leads entrantes
- [ ] 1 actividad de prospección (mensaje/llamada)
- [ ] Revisar métricas (pipeline, conversiones)
- [ ] Documentar aprendizaje en QL-Agent

### 3 veces por semana
- [ ] Demo con prospecto
- [ ] Propuesta enviada
- [ ] Código generado con QL-Agent

### Semanal
- [ ] Review de pipeline
- [ ] Actualizar documentación
- [ ] Mejorar prompts de agentes
- [ ] Planificar semana siguiente

---

## 🎓 Próximos Pasos (Mes 2+)

### Opción A: Escalar Concessio (Especialista)
- Target: 10 concesionarias Fiat
- Equipo: Tú + 1 developer junior + 1 soporte
- Meta: $8,000/mes recurrente

### Opción B: Diversificar (Agency Model)
- Concessio: 40%
- Trading Bot: 30%
- Custom dev: 30%
- Meta: $10,000/mes

### Opción C: Productizar (SaaS)
- Concessio multi-tenant auto-service
- Landing page → Sign up → Stripe → Deploy automático
- Meta: 50 clientes × $100/mes = $5,000 MRR

---

## 📞 Soporte y Recursos

### Cuando estás atascado
```
1. Preguntar a QL-Agent: "estoy atascado con X, qué opciones tengo"
2. Buscar en documentación indexada
3. Comunidad: IndieHackers, Reddit r/SaaS
4. Mentor: ADPList (gratis), experiencia emprendedor
```

### Recordatorio Final
```
"Perfecto es enemigo de hecho"

No esperes a tener:
- Website perfecto
- Producto 100% feature-complete
- QL-Agent sistema completo
- Experiencia de 10 años

EMPIEZA HOY con:
- Landing simple (1 página)
- Concessio funcional (70% listo)
- QL-Agent básico (2 modelos)
- 10 mensajes de prospección

Itera rápido, aprende, mejora.
```

---

*Plan creado por QL-Agent para Emilio - QLSoftware Studio*  
*Fecha: Mayo 2026*  
*Meta: $2,000-5,000 mes 1 → $5,000-10,000 mes 3*
