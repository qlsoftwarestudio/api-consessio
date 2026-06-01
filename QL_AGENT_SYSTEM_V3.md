# 🤖 QL-Agent System V3 - Multi-Agent Orchestration Platform

**Versión:** 3.0 - Enterprise Multi-Agent Architecture  
**Propósito:** Sistema agentico completo para QLSoftware Studio con proceso comercial end-to-end  
**Stack:** React (Atomic Design) + Node.js/Python + MCP + Multi-Agent Orchestration

---

## 🎯 Visión Ejecutiva

Transformar QLSoftware Studio en una **"AI-Native Software Agency"** donde:
- **Sub-agentes especializados** manejan cada fase del negocio
- **Skills MCP** conectan con herramientas externas (calendarios, email, CRM)
- **Código generado** sigue SOLID, OOP, Clean Code automáticamente
- **Proceso comercial** desde prospección hasta cierre está automatizado/assistido

---

## 🏗️ Arquitectura de Sistema Multi-Agente

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         QL-AGENT ORCHESTRATOR                               │
│                    (Coordinador Maestro - GPT-4o/Claude)                      │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
           ┌──────────────────────────┼──────────────────────────┐
           │                          │                          │
           ▼                          ▼                          ▼
┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
│   SALES AGENT       │  │   TECHNICAL AGENT   │  │   DELIVERY AGENT    │
│   (Prospección)     │  │   (Arquitectura)    │  │   (Ejecución)       │
├─────────────────────┤  ├─────────────────────┤  ├─────────────────────┤
│ • Prospector        │  │ • Architect         │  │ • Developer         │
│ • Qualifier         │  │ • Analyst           │  │ • Tester            │
│ • Presenter         │  │ • Estimator         │  │ • DevOps            │
│ • Negotiator        │  │ • Reviewer          │  │ • Deployer          │
│ • Closer            │  │ • Documenter        │  │ • Monitor           │
└─────────────────────┘  └─────────────────────┘  └─────────────────────┘
```

---

## 👥 Sub-Agentes Especializados (12 Agentes)

### 🎯 SALES DIVISION (5 Agentes)

#### 1. **ProspectorAgent** - "Cazador de Oportunidades"
```typescript
interface ProspectorConfig {
  name: "ProspectorAgent";
  role: SystemRole.LEAD_GENERATION;
  objective: "Encontrar y captar leads calificados para QLSoftware Studio";
  
  skills: [
    "linkedin_scraping",           // MCP: LinkedIn API
    "website_analysis",              // MCP: Web scraping
    "crm_integration",               // MCP: Concessio CRM API
    "email_outreach",                // MCP: SendGrid/Resend
    "social_listening"               // MCP: Twitter/X API
  ];
  
  knowledge_base: [
    "PRICING_PLANES.md",
    "PROPUESTA_TECNICA_GIAMMA_360_v3.md",
    "portfolio_qlstudio.json"
  ];
  
  prompts: {
    system: `Eres un prospector senior de QLSoftware Studio. 
            Identificas concesionarias Fiat que necesitan CRM (Concessio) 
            y traders que necesitan automatización (Trading Bot).
            Mensajes personalizados, no spam. Datos de contacto verificados.`;
    
    outreach_template: `
      Hola {nombre}, vi que {concesionaria} está expandiendo a {sucursal}.
      Concessio CRM ayudó a AutoX a reducir 40% tiempo de venta.
      ¿Te gustaría ver una demo de 15 min?
      - Emilio, QLSoftware Studio
    `;
  };
}
```

**Acciones:**
- Busca concesionarias nuevas en LinkedIn/Instagram
- Identifica señales de compra (expansión, malas reviews, tecnología antigua)
- Envía primer contacto personalizado (no masivo)
- Califica leads (BANT: Budget, Authority, Need, Timeline)
- Crea lead en Concessio CRM automáticamente

#### 2. **QualifierAgent** - "Calificador de Oportunidades"
```typescript
interface QualifierConfig {
  name: "QualifierAgent";
  role: SystemRole.LEAD_QUALIFICATION;
  
  evaluation_criteria: {
    budget: { min: 5000, ideal: 15000, enterprise: 50000 };
    timeline: { fast: "1-2 meses", normal: "3-6 meses", slow: "6+ meses" };
    decision_makers: ["gerente_comercial", "it_manager", "c_level"];
    fit_product: ["concessio", "trading_bot", "custom_dev"];
  };
  
  skills: [
    "discovery_call",                // MCP: Calendly + Zoom
    "questionnaire_analysis",        // Internal
    "competitor_research",           // MCP: Web search
    "scoring_algorithm"              // Internal
  ];
}
```

**Proceso de Calificación:**
```
Input: Lead de ProspectorAgent
↓
1. Envia cuestionario automático (3-5 preguntas clave)
2. Analiza respuestas + research online
3. Score: 0-100 (fit + intención + capacidad)
4. Si score > 70 → Escalate a ValidatorAgent
5. Si score 40-70 → Nurture (email sequence)
6. Si score < 40 → Descartar o re-evaluar en 6 meses
```

#### 3. **ValidatorAgent** - "Validador de Propuestas"
```typescript
interface ValidatorConfig {
  name: "ValidatorAgent";
  role: SystemRole.PROPOSAL_VALIDATION;
  
  validation_framework: {
    technical_feasibility: boolean;    // ¿Podemos construirlo?
    commercial_viability: boolean;       // ¿Es rentable?
    resource_availability: boolean;    // ¿Tenemos capacidad?
    risk_assessment: RiskMatrix;
  };
  
  skills: [
    "technical_spike",                 // MCP: GitHub Codespaces
    "effort_estimation",               // Internal (historical data)
    "market_rate_analysis",            // MCP: Glassdoor/LinkedIn salaries
    "risk_calculator"                  // Internal
  ];
}
```

**Proceso de Validación:**
```
Input: Lead calificado (score >70)
↓
1. Entrevista técnica con lead (15 min calendly)
2. Estima esfuerzo: Low (40h) / Medium (120h) / High (300h+)
3. Calcula precio basado en:
   - Esfuerzo estimado × Rate $50-100/h
   - Valor para cliente
   - Precios de competencia
4. Genera propuesta técnica preliminar (2-3 páginas)
5. Validación interna con Emilio (si >$10K)
6. Si aprobado → PresenterAgent
```

#### 4. **PresenterAgent** - "Presentador/Demo"
```typescript
interface PresenterConfig {
  name: "PresenterAgent";
  role: SystemRole.PROPOSAL_PRESENTATION;
  
  presentation_types: [
    "live_demo",                     // Mostrar Concessio/Trading Bot funcionando
    "technical_proposal",            // Documento PDF detallado
    "roi_calculator",                  // Spreadsheet interactivo
    "case_study_matching"              // Cliente similar exitoso
  ];
  
  skills: [
    "demo_environment",              // MCP: Railway preview deploys
    "slide_generation",                // MCP: Google Slides API
    "roi_modeling",                  // MCP: Excel/Google Sheets
    "video_pitch"                    // MCP: Loom API
  ];
}
```

#### 5. **NegotiatorCloserAgent** - "Negociador y Cerrador"
```typescript
interface NegotiatorConfig {
  name: "NegotiatorCloserAgent";
  role: SystemRole.NEGOTIATION_CLOSING;
  
  negotiation_params: {
    discount_max: 15;                 // % máximo descuento
    payment_terms: ["100_upfront", "50_50", "monthly_3"];
    scope_creep_handling: "change_request_pricing";
    contract_templates: ["saas_standard", "custom_dev", "maintenance"];
  };
  
  skills: [
    "objection_handling",            // Knowledge base
    "pricing_tactics",               // Internal
    "contract_generation",           // MCP: DocuSign/HelloSign
    "stripe_billing",                // MCP: Stripe API
    "escalation_detection"           // Internal
  ];
}
```

**Proceso de Negociación:**
```
Input: Cliente interesado, objeciones surgen
↓
Objectiones comunes manejadas:
• "Es caro" → Comparativa ROI vs competencia
• "Necesito pensarlo" → Urgencia legítima (case study, deadline)
• "Competidor X cobra menos" → Diferenciación valor
• "No tenemos budget ahora" → Flexible payment terms
• "Quiero feature Y extra" → Scope negotiation / Change request pricing
↓
Output: Contrato firmado o Escalado a Emilio (casos complejos)
```

---

### 🔧 TECHNICAL DIVISION (4 Agentes)

#### 6. **ArchitectAgent** - "Arquitecto de Soluciones"
```typescript
interface ArchitectConfig {
  name: "ArchitectAgent";
  role: SystemRole.TECHNICAL_ARCHITECTURE;
  
  design_principles: ["SOLID", "DDD", "CleanArchitecture", "Microservices"];
  
  stack_expertise: {
    backend: ["SpringBoot", "Node.js", "Python/FastAPI"];
    frontend: ["React", "Vue", "Next.js"];
    database: ["PostgreSQL", "MongoDB", "Redis"];
    infrastructure: ["Railway", "AWS", "Docker", "Kubernetes"];
  };
  
  output_formats: {
    adr: "Architecture Decision Records";
    diagrams: "C4Model / PlantUML / Mermaid";
    api_spec: "OpenAPI 3.0";
    data_model: "ER Diagrams + Migrations";
  };
  
  skills: [
    "c4_modeling",                   // MCP: PlantUML/Structurizr
    "openapi_generation",            // Internal
    "database_design",               // Internal
    "security_audit",                // MCP: OWASP/ZAP
    "scalability_analysis"           // Internal formulas
  ];
}
```

**Proceso de Arquitectura:**
```
Input: Requerimientos del cliente + Propuesta aprobada
↓
1. Analiza requerimientos funcionales
2. Define arquitectura (Monolito vs Microservicios)
3. Selecciona stack tecnológico
4. Diseña modelo de datos
5. Define API contracts (OpenAPI)
6. Identifica riesgos técnicos
7. Genera ADRs (Architecture Decision Records)
8. Output: Documento de arquitectura + Diagramas
↓
Handoff a: DeveloperAgent
```

**Ejemplo de output (ADR):**
```markdown
## ADR-001: Selección de Base de Datos

Contexto: Cliente necesita multi-tenancy con aislamiento de datos

Opciones consideradas:
- PostgreSQL con schemas por tenant
- PostgreSQL con row-level security
- MongoDB con collections por tenant

Decisión: PostgreSQL con schemas por tenant

Justificación:
- Concessio ya usa este patrón (reutilizable)
- Mejor aislamiento que RLS
- Backup/restore por tenant facilitado

Consecuencias:
- Migration strategy más compleja
- Mayor overhead de conexiones
```

#### 7. **EstimatorAgent** - "Estimador de Esfuerzo"
```typescript
interface EstimatorConfig {
  name: "EstimatorAgent";
  role: SystemRole.EFFORT_ESTIMATION;
  
  estimation_methods: [
    "story_points",                  // Fibonacci: 1,2,3,5,8,13,21
    "function_points",               // IFPUG standard
    "analogous",                     // Basado en proyectos similares
    "parametric"                     // Fórmulas (loc × complexity)
  ];
  
  factors: {
    complexity: ["simple", "medium", "complex", "very_complex"];
    uncertainty: ["low", "medium", "high"];
    team_velocity: number;            // Story points/sprint
    buffer: 1.3;                      // 30% buffer para imprevistos
  };
  
  skills: [
    "historical_analysis",           // Proyectos pasados QLStudio
    "task_breakdown",                // Decomposition
    "risk_adjustment",               // Monte Carlo simulation
    "confidence_intervals"           // P50, P80, P90 estimates
  ];
}
```

#### 8. **AnalystAgent** - "Analista de Requerimientos"
```typescript
interface AnalystConfig {
  name: "AnalystAgent";
  role: SystemRole.REQUIREMENTS_ANALYSIS;
  
  analysis_techniques: [
    "user_story_mapping",
    "event_storming", 
    "domain_modeling",
    "use_case_modeling"
  ];
  
  deliverables: {
    prd: "Product Requirements Document";
    user_stories: "Gherkin format (Given-When-Then)";
    acceptance_criteria: "AC + Definition of Done";
    wireframes: "Lo-fi descriptions for UI/UX";
  };
  
  skills: [
    "interview_conduction",          // MCP: Calendly + Recording
    "requirement_elicitation",       // Active listening prompts
    "ambiguity_detection",           // NLP analysis
    "traceability_matrix"            // Req → Story → Test
  ];
}
```

#### 9. **ReviewerAgent** - "Reviewer de Código"
```typescript
interface ReviewerConfig {
  name: "ReviewerAgent";
  role: SystemRole.CODE_REVIEW;
  
  review_focus: [
    "solid_principles",
    "design_patterns",
    "security_vulnerabilities",
    "performance_issues",
    "test_coverage",
    "documentation_completeness"
  ];
  
  quality_gates: {
    coverage_min: 80;               // % cobertura tests
    complexity_max: 10;             // Cyclomatic complexity
    duplications_max: 3;            // % código duplicado
    security_issues: 0;             // Críticos: 0
  };
  
  skills: [
    "static_analysis",               // MCP: SonarQube/SonarCloud
    "dependency_check",              // MCP: OWASP Dependency Check
    "security_scan",                 // MCP: Snyk/Semgrep
    "pattern_matching",              // Internal regex patterns
    "diff_analysis"                  // Git diff review
  ];
}
```

**Checklist de Review Automático:**
```
✅ SOLID Principles:
   - S: ¿Clase tiene una sola responsabilidad?
   - O: ¿Está abierto a extensión, cerrado a modificación?
   - L: ¿Cumple Liskov Substitution?
   - I: ¿Interfaces son pequeñas y cohesivas?
   - D: ¿Depende de abstracciones?

✅ Clean Code:
   - Nombres descriptivos (no a, b, tmp)
   - Funciones <20 líneas
   - No código duplicado (DRY)
   - Comentarios solo si explican "por qué", no "qué"

✅ Testing:
   - Tests unitarios presentes
   - Tests de integración para endpoints
   - Cobertura >80%
   - Mocks/stubs apropiados

✅ Security:
   - No secrets hardcodeados
   - SQL injection protegido (parametrizado)
   - XSS/CSRF protegido
   - Input validation
```

---

### 🚚 DELIVERY DIVISION (3 Agentes)

#### 10. **DeveloperAgent** - "Desarrollador Full-Stack"
```typescript
interface DeveloperConfig {
  name: "DeveloperAgent";
  role: SystemRole.SOFTWARE_DEVELOPMENT;
  
  coding_standards: {
    style_guide: "Google Java Style / Airbnb JavaScript";
    patterns: ["Strategy", "Factory", "Repository", "UnitOfWork"];
    principles: ["SOLID", "DRY", "KISS", "YAGNI"];
    testing: "TDD preferred, minimum 80% coverage";
  };
  
  tech_stack: {
    backend: "Spring Boot 3.x (Java 21) / Node.js / Python";
    frontend: "React 18 + TypeScript + Atomic Design";
    database: "PostgreSQL + JPA/Hibernate";
    testing: "JUnit + Mockito + Playwright";
  };
  
  skills: [
    "code_generation",               // Template-based + LLM refinement
    "refactoring",                   // Automated refactoring
    "test_generation",               // Mutation testing
    "documentation",                 // JavaDoc + Markdown
    "atomic_design_implementation"   // Component hierarchy
  ];
}
```

**Proceso de Desarrollo (Clean Code):**
```
Input: Arquitectura aprobada + User Stories
↓
1. Genera estructura de proyecto (boilerplate SOLID)
2. Implementa entities (JPA, validaciones)
3. Implementa repositories (Spring Data)
4. Implementa services (business logic, Strategy pattern)
5. Implementa controllers (REST, DTOs, validation)
6. Genera tests unitarios (JUnit + Mockito)
7. Genera tests de integración (TestContainers)
8. Implementa frontend (React + Atomic Design)
   - Atoms: Button, Input, Label
   - Molecules: SearchBar, FormField
   - Organisms: LeadCard, LeadForm
   - Templates: DashboardLayout
   - Pages: LeadsPage, DashboardPage
9. Documenta API (OpenAPI annotations)
10. Commit estructurado (conventional commits)
↓
Handoff a: ReviewerAgent → TesterAgent → DevOpsAgent
```

**Ejemplo: Generación SOLID Automática**
```java
// DeveloperAgent genera estructura base:

// 1. ENTITY (S - Single Responsibility)
@Entity
@Table(name = "leads")
public class Lead {
    @Id @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre es obligatorio")
    private String firstName;
    
    // ... campos con validaciones
}

// 2. REPOSITORY INTERFACE (DIP - Dependency Inversion)
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByStatusAndTenantId(LeadStatus status, Long tenantId);
}

// 3. SERVICE INTERFACE + IMPL (OCP - Open/Closed)
public interface LeadService {
    LeadDTO createLead(CreateLeadRequest request, Long tenantId);
}

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {
    private final LeadRepository repository;  // Inyección por constructor
    private final LeadMapper mapper;
    private final TenantContext tenantContext;
    
    @Override
    @Transactional
    public LeadDTO createLead(CreateLeadRequest request, Long tenantId) {
        // Validaciones
        // Lógica de negocio
        // Persistencia
    }
}

// 4. STRATEGY PATTERN para cálculos (Extensible)
public interface LeadScoringStrategy {
    int calculateScore(Lead lead);
}

@Component
public class DefaultLeadScoringStrategy implements LeadScoringStrategy {
    // Implementación por defecto
}

// 5. CONTROLLER (Thin Controller)
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {
    private final LeadService leadService;
    
    @PostMapping
    public ResponseEntity<LeadDTO> create(@Valid @RequestBody CreateLeadRequest request) {
        return ResponseEntity.ok(leadService.createLead(request, getCurrentTenantId()));
    }
}
```

#### 11. **TesterAgent** - "QA Engineer"
```typescript
interface TesterConfig {
  name: "TesterAgent";
  role: SystemRole.QUALITY_ASSURANCE;
  
  testing_levels: [
    "unit",                          // JUnit + Mockito
    "integration",                   // TestContainers + SpringBootTest
    "e2e",                           // Playwright / Cypress
    "performance",                   // k6 / JMeter
    "security"                       // OWASP ZAP
  ];
  
  coverage_requirements: {
    line_coverage: 80;
    branch_coverage: 70;
    mutation_score: 60;
  };
  
  skills: [
    "test_case_generation",            // BDD/Gherkin
    "mutation_testing",                // PIT
    "boundary_testing",                // Equivalence partitioning
    "exploratory_testing",             // Randomized inputs
    "regression_detection"             // Diff-based testing
  ];
}
```

#### 12. **DevOpsAgent** - "DevOps Engineer"
```typescript
interface DevOpsConfig {
  name: "DevOpsAgent";
  role: SystemRole.DEVOPS_DEPLOYMENT;
  
  infrastructure: {
    platforms: ["Railway", "AWS", "Vercel", "DigitalOcean"];
    containers: "Docker + Docker Compose";
    cicd: "GitHub Actions / GitLab CI";
    iac: "Terraform / Pulumi";
  };
  
  deployment_strategies: [
    "blue_green",
    "canary",
    "rolling_update"
  ];
  
  skills: [
    "dockerfile_generation",         // Optimized multi-stage
    "pipeline_configuration",        // CI/CD YAML
    "infrastructure_provisioning",   // Terraform
    "monitoring_setup",              // Datadog / Sentry
    "ssl_cert_management",           // Let's Encrypt
    "backup_automation"              // Scheduled backups
  ];
}
```

---

## 🔌 MCP (Model Context Protocol) Integrations

### Skills y Herramientas Externas

```typescript
interface MCPServerRegistry {
  // Comunicación
  email: {
    provider: "resend" | "sendgrid";
    actions: ["send", "schedule", "template"];
  };
  
  calendar: {
    provider: "calendly" | "google_calendar";
    actions: ["schedule", "check_availability", "send_reminder"];
  };
  
  // Development
  database: {
    provider: "postgresql" | "mongodb";
    actions: ["query", "migrate", "backup", "seed"];
  };
  
  git: {
    provider: "github" | "gitlab";
    actions: ["create_pr", "review", "merge", "tag"];
  };
  
  // Deploy
  hosting: {
    providers: ["railway", "aws", "vercel"];
    actions: ["deploy", "rollback", "scale", "logs"];
  };
  
  // CRM & Sales
  crm: {
    internal: "concessio_api";
    external: ["hubspot", "salesforce"];
    actions: ["create_lead", "update_opportunity", "track_interaction"];
  };
  
  // Comunicación
  messaging: {
    providers: ["telegram", "whatsapp_business", "slack"];
    actions: ["send", "broadcast", "automation"];
  };
  
  // Documentos
  documents: {
    providers: ["google_docs", "docusign", "notion"];
    actions: ["generate", "sign", "share"];
  };
}
```

---

## 🎨 Frontend Architecture - Atomic Design

### Estructura de Componentes

```
src/
├── atoms/                    # Elementos indivisibles
│   ├── Button/
│   │   ├── Button.tsx
│   │   ├── Button.test.tsx
│   │   ├── Button.styles.ts
│   │   └── Button.stories.tsx
│   ├── Input/
│   ├── Label/
│   ├── Icon/
│   └── Typography/
│
├── molecules/                # Grupos de atoms
│   ├── SearchBar/           # Input + Button + Icon
│   ├── FormField/           # Label + Input + Error
│   ├── LeadCardHeader/      # Avatar + Name + Status
│   └── PaginationControls/  # Button + Button + Text
│
├── organisms/                # Componentes complejos
│   ├── LeadCard/            # Header + Body + Actions
│   ├── LeadForm/            # Multiple FormFields + Buttons
│   ├── Navigation/          # Logo + Links + UserMenu
│   ├── DataTable/           # Table + Pagination + Filters
│   └── DashboardStats/      # Charts + KPIs + Trends
│
├── templates/                # Layouts de página
│   ├── DashboardLayout/     # Sidebar + Header + Content
│   ├── AuthLayout/        # Centered card + Background
│   └── CrudLayout/        # List + Detail split view
│
├── pages/                    # Rutas de la app
│   ├── LeadsPage/
│   ├── LeadDetailPage/
│   ├── DashboardPage/
│   └── SettingsPage/
│
└── shared/                   # Utils, hooks, types
    ├── hooks/
    ├── utils/
    ├── types/
    └── api/
```

### Principios de Atomic Design Implementados

1. **Atoms:** No dependen de otros componentes
   ```typescript
   // Button.tsx - Props explícitas, estilos aislados
   interface ButtonProps {
     variant: 'primary' | 'secondary' | 'danger';
     size: 'sm' | 'md' | 'lg';
     loading?: boolean;
     disabled?: boolean;
     onClick: () => void;
     children: React.ReactNode;
   }
   ```

2. **Molecules:** Componen atoms, sin lógica de negocio
   ```typescript
   // SearchBar.tsx - Solo UI composition
   export const SearchBar: React.FC<SearchBarProps> = ({ onSearch }) => {
     const [query, setQuery] = useState('');
     return (
       <Flex gap={2}>
         <Input value={query} onChange={setQuery} placeholder="Buscar..." />
         <Button onClick={() => onSearch(query)} icon={<SearchIcon />} />
       </Flex>
     );
   };
   ```

3. **Organisms:** Lógica de negocio, conectan con API
   ```typescript
   // LeadCard.tsx - Sabe de leads, usa molecules
   export const LeadCard: React.FC<LeadCardProps> = ({ leadId }) => {
     const { data: lead, isLoading } = useLead(leadId);  // Hook conecta API
     
     if (isLoading) return <SkeletonCard />;
     
     return (
       <Card>
         <LeadCardHeader lead={lead} />
         <LeadCardBody lead={lead} />
         <LeadCardActions leadId={lead.id} />
       </Card>
     );
   };
   ```

---

## 🔄 Proceso Comercial End-to-End (Workflow)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         PROCESO DE VENTAS QL-AGENT                       │
└─────────────────────────────────────────────────────────────────────────┘

FASE 1: PROSPECCIÓN (Semana 1)
├── ProspectorAgent escanea LinkedIn/Instagram
├── Identifica concesionarias Fiat sin CRM o con tecnología antigua
├── Envía primer contacto personalizado vía LinkedIn/Email
└── Crea Lead en Concessio CRM con tag "PROSPECTO_CALIENTE"

FASE 2: CALIFICACIÓN (Día 2-3)
├── QualifierAgent envía cuestionario automático (Calendly)
├── Analiza respuestas + research de competencia online
├── Calcula BANT Score (Budget, Authority, Need, Timeline)
└── Si Score >70 → Escalate a ValidatorAgent

FASE 3: VALIDACIÓN (Día 4-7)
├── ValidatorAgent entrevista técnica (15 min)
├── ArchitectAgent diseña solución preliminar (30 min)
├── EstimatorAgent calcula esfuerzo y precio
├── Genera propuesta técnica 2-3 páginas (PDF auto-generado)
└── Emilio revisa y aprueba (si >$10K)

FASE 4: PRESENTACIÓN (Semana 2)
├── PresenterAgent agenda demo (Calendly + Zoom)
├── Prepara environment demo en Railway (preview)
├── Muestra Concessio con datos de ejemplo
├── Presenta ROI calculator (Google Sheets)
└── Responde preguntas técnicas en vivo

FASE 5: NEGOCIACIÓN (Semana 2-3)
├── NegotiatorCloserAgent detecta objeciones
├── Genera propuesta final con 3 opciones de pricing
├── Envía contrato (DocuSign)
├── Configura billing en Stripe
└── Cierra deal o escala a Emilio si objeción compleja

FASE 6: DELIVERY (Semana 3-8)
├── AnalystAgent entrevista detallada de requerimientos
├── ArchitectAgent diseña arquitectura completa (ADR)
├── DeveloperAgent genera código SOLID/Clean
├── ReviewerAgent revisa código (SonarQube + manual)
├── TesterAgent ejecuta suite de tests
├── DevOpsAgent deploya a producción (Railway/AWS)
└── Handoff a Soporte + Documentación

FASE 7: POST-VENTA (Continuo)
├── MonitorAgent verifica uptime y errores (Sentry)
├── SuccessAgent checkea satisfacción (NPS survey)
├── UpsellAgent identifica oportunidades (nuevos módulos)
└── ReferralAgent pide referrals a clientes satisfechos
```

---

## 📋 Implementation Roadmap

### Fase 1: Foundation (Semanas 1-2)
- [ ] Setup MCP servers (Calendly, Resend, Railway, GitHub)
- [ ] Implementar Orchestrator base (Node.js/Python)
- [ ] Crear estructura de Sub-Agentes (carpetas/interfaces)
- [ ] Implementar ProspectorAgent + QualifierAgent

### Fase 2: Sales Automation (Semanas 3-4)
- [ ] ValidatorAgent con entrevistas automáticas
- [ ] PresenterAgent con demo environments
- [ ] NegotiatorCloserAgent con templates de contratos
- [ ] Integración completa con Concessio CRM

### Fase 3: Technical Agents (Semanas 5-8)
- [ ] ArchitectAgent con ADR generation
- [ ] DeveloperAgent con code generation SOLID
- [ ] ReviewerAgent con SonarQube integration
- [ ] TesterAgent con test generation

### Fase 4: Frontend (Semanas 6-9)
- [ ] Setup React + Atomic Design structure
- [ ] Implementar Atoms (Button, Input, etc.)
- [ ] Implementar Molecules (SearchBar, FormField)
- [ ] Implementar Organisms (LeadCard, DataTable)
- [ ] Dashboard para Emilio (visualizar pipelines)

### Fase 5: Production (Semanas 9-10)
- [ ] DevOpsAgent con CI/CD pipelines
- [ ] Testing end-to-end
- [ ] Documentación completa
- [ ] Deploy a producción

---

## 🎓 Prompts de Sistema por Agente

### Orchestrator (Maestro)
```
Eres el Orchestrator de QL-Agent System.
Tu trabajo es recibir requests del usuario (Emilio) y decidir:
1. Qué sub-agente(s) deben actuar
2. En qué orden (pipeline)
3. Cuándo escalar a Emilio (casos complejos)

Reglas:
- Nunca hagas el trabajo directamente, delega
- Si múltiples agentes, orquesta secuencial o paralelo según dependencias
- Si output de un agente es input de otro, maneja el handoff
- Mantén contexto de conversación entre agentes
- Si el cliente pide algo fuera de scope, consulta a Emilio
```

### DeveloperAgent (Código SOLID)
```
Eres DeveloperAgent de QLSoftware Studio.
Generas código Java/Spring Boot y React siguiendo:

PRINCIPIOS SOLID:
S - Single Responsibility: Una clase = una razón para cambiar
O - Open/Closed: Extiende con nuevas clases, no modifiques existentes
L - Liskov Substitution: Implementaciones intercambiables
I - Interface Segregation: Interfaces pequeñas y específicas
D - Dependency Inversion: Depende de abstracciones

CLEAN CODE:
- Nombres descriptivos (no a, b, tmp)
- Funciones <20 líneas, hacen una cosa
- No comentarios obvios (el código debe ser self-documenting)
- Manejo de errores explícito
- DRY: No repitas código

ESTRUCTURA:
- Entities: JPA, validaciones Bean Validation
- Repositories: Interfaces Spring Data
- Services: Interfaces + Impl (Strategy pattern para variaciones)
- Controllers: Thin, solo routing y DTOs
- DTOs: Record classes (Java 21) o interfaces tipadas (TS)
- Tests: JUnit 5 + Mockito, cobertura >80%

ATOMIC DESIGN (React):
- Atoms: Props primitivas, estilos aislados
- Molecules: Composición de atoms, lógica simple
- Organisms: Conectan con API, lógica de negocio
- Templates: Layouts, no data fetching
- Pages: Routing + Data fetching

OUTPUT:
- Genera archivos completos, no snippets
- Incluye tests automáticamente
- Documenta con JavaDoc/OpenAPI
- Usa conventional commits
```

---

## 💰 Business Model Impact

### Antes (Sin Agente)
| Métrica | Valor |
|---------|-------|
| Leads generados/mes | 5-10 (manual) |
| Tiempo estimación | 4-8 horas |
| Tiempo desarrollo feature | 40-80 horas |
| Clientes activos máximo | 3-4 |

### Después (Con QL-Agent System)
| Métrica | Valor |
|---------|-------|
| Leads generados/mes | 30-50 (automático) |
| Tiempo estimación | 30 min (automático) |
| Tiempo desarrollo feature | 10-20 horas (50% generado) |
| Clientes activos máximo | 10-15 |

**ROI:** 3-5x capacidad de entrega con el mismo equipo (1 persona)

---

*Documento maestro para implementación QL-Agent System V3*
