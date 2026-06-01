# 🎨 LOVABLE AI - FRONTEND SPECIFICATION

## Concessio CRM - Concesionario Multimarca

**Versión:** 2.0  
**Stack Tecnológico:** React + TypeScript + Tailwind CSS + shadcn/ui + Zustand + TanStack Query  
**Estilo Visual:** Premium Automotriz - Inspirado en Concessio Jeep (Dark & Bold)
**Arquitectura:** Screaming Architecture (Feature-Based) + Atomic Design

---

## 🎯 OBJETIVO

Crear una aplicación web premium para CRM de concesionario de autos, con arquitectura escalable, diseño atómico y estética automotriz moderna tipo Concessio Jeep (dark theme, acentos dorados/naranja, tipografía bold).

---

## 🎨 DISEÑO UI/UX

### Paleta de Colores - Concessio Premium

| Color | Hex | Uso |
|-------|-----|-----|
| **Primary** | `#D97706` | Acento Jeep (ámbar/dorado) - CTAs principales |
| **Primary Dark** | `#B45309` | Hover states dorado |
| **Secondary** | `#1F2937` | Gris oscuro - Navbar, headers |
| **Background** | `#0F172A` | Slate 900 - Fondo general dark |
| **Surface** | `#1E293B` | Slate 800 - Cards, modales |
| **Surface Elevated** | `#334155` | Slate 700 - Hover, elevated cards |
| **Text Primary** | `#F8FAFC` | Slate 50 - Texto principal (blanco) |
| **Text Secondary** | `#94A3B8` | Slate 400 - Texto secundario |
| **Text Muted** | `#64748B` | Slate 500 - Placeholder, disabled |
| **Success** | `#22C55E` | Leads entregados, éxito |
| **Warning** | `#F59E0B` | Alertas, pendientes (ámbar) |
| **Danger** | `#EF4444` | Errores, eliminar, descartados |
| **Info** | `#3B82F6` | Informativo, links |
| **Border** | `#334155` | Slate 700 - Bordes sutiles |
| **Border Focus** | `#D97706` | Ámbar - Focus states |

### Estados de Lead (Dark Theme)

| Estado | Color | Badge Style |
|--------|-------|-------------|
| NUEVO | `#3B82F6` | `bg-blue-500/20 text-blue-400 border-blue-500/30` |
| CONTACTADO | `#F59E0B` | `bg-amber-500/20 text-amber-400 border-amber-500/30` |
| EN_SEGUIMIENTO | `#8B5CF6` | `bg-violet-500/20 text-violet-400 border-violet-500/30` |
| COTIZADO | `#D97706` | `bg-amber-600/20 text-amber-500 border-amber-600/30` |
| TEST_DRIVE | `#EC4899` | `bg-pink-500/20 text-pink-400 border-pink-500/30` |
| NEGOCIACION | `#6366F1` | `bg-indigo-500/20 text-indigo-400 border-indigo-500/30` |
| ENTREGADO | `#22C55E` | `bg-green-500/20 text-green-400 border-green-500/30` |
| DESCARTADO | `#64748B` | `bg-slate-500/20 text-slate-400 border-slate-500/30` |

### Tipografía

- **Headings:** Inter / Roboto Condensed (Bold, uppercase para títulos de sección)
- **Body:** Inter (Regular, 400-500)
- **Monospace (precios):** Tabular nums, font-weight 600
- **Títulos Sección:** Uppercase, tracking-wider, font-bold, text-amber-500

### Efectos Visuales

```css
/* Glassmorphism cards */
.glass-card {
  @apply bg-slate-800/80 backdrop-blur-lg border border-slate-700/50;
}

/* Amber glow para CTAs */
.btn-primary {
  @apply bg-amber-600 hover:bg-amber-700 text-white;
  @apply shadow-lg shadow-amber-600/25;
}

/* Gradient backgrounds */
.page-gradient {
  @apply bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900;
}
```

### Componentes shadcn/ui Requeridos

```bash
npx shadcn add button card table badge dialog input select tabs avatar dropdown-menu toast pagination calendar date-picker skeleton
```

---

## 🏗️ ARQUITECTURA ORIENTADA A GRITOS (SCREAMING)

La estructura grita qué hace la aplicación, no qué tecnologías usa.

```
src/
├── features/                    ← 🎯 FEATURES (el corazón)
│   ├── auth/
│   │   ├── components/          # LoginForm, OnboardingForm
│   │   ├── hooks/               # useAuth, useLogin
│   │   ├── stores/              # authStore.ts (Zustand)
│   │   ├── api/                 # authApi.ts
│   │   ├── types/               # AuthTypes.ts
│   │   └── pages/               # LoginPage.tsx
│   ├── leads/
│   │   ├── components/          # LeadCard, LeadTable, LeadFilters
│   │   ├── organisms/             # LeadList, LeadDetail (Atomic)
│   │   ├── hooks/               # useLeads, useLeadMutations
│   │   ├── stores/              # leadStore.ts
│   │   ├── api/                 # leadsApi.ts
│   │   ├── types/               # Lead.ts
│   │   └── pages/               # LeadsPage, LeadDetailPage
│   ├── vehicles/
│   │   ├── components/          # VehicleCard, VehicleGrid
│   │   ├── organisms/             # VehicleCatalog, VehicleDetail
│   │   ├── hooks/               # useVehicles, useVehicleMutations
│   │   ├── stores/              # vehicleStore.ts
│   │   ├── api/                 # vehiclesApi.ts
│   │   ├── types/               # Vehicle.ts
│   │   └── pages/               # VehiclesPage, VehicleDetailPage
│   ├── quotations/
│   │   ├── components/          # QuotationForm, Calculator
│   │   ├── organisms/             # QuotationList, QuotationWizard
│   │   ├── hooks/               # useQuotations, useCalculate
│   │   ├── stores/              # quotationStore.ts
│   │   ├── api/                 # quotationsApi.ts
│   │   ├── types/               # Quotation.ts, QuotationType.ts
│   │   └── pages/               # QuotationsPage, NewQuotationPage
│   ├── test-drives/
│   │   ├── components/          # TestDriveCalendar, TestDriveCard
│   │   ├── organisms/             # CalendarView, BookingForm
│   │   ├── hooks/               # useTestDrives, useCalendar
│   │   ├── stores/              # testDriveStore.ts
│   │   ├── api/                 # testDrivesApi.ts
│   │   ├── types/               # TestDrive.ts
│   │   └── pages/               # TestDrivesPage, CalendarPage
│   ├── dashboard/
│   │   ├── components/          # KPICard, PipelineChart
│   │   ├── organisms/             # StatsGrid, RecentActivity
│   │   ├── hooks/               # useDashboard, useStats
│   │   ├── api/                 # dashboardApi.ts
│   │   └── pages/               # DashboardPage
│   └── activities/
│       ├── components/          # ActivityItem, Timeline
│       ├── organisms/             # ActivityFeed, TimelineView
│       ├── hooks/               # useActivities
│       ├── api/                 # activitiesApi.ts
│       └── types/               # Activity.ts
│
├── atomic-design/               ← ⚛️ ATOMIC DESIGN SYSTEM
│   ├── atoms/                   # Componentes más básicos
│   │   ├── Button/
│   │   │   ├── Button.tsx
│   │   │   ├── Button.types.ts
│   │   │   └── Button.test.tsx
│   │   ├── Input/
│   │   ├── Label/
│   │   ├── Badge/
│   │   ├── Avatar/
│   │   ├── Icon/
│   │   └── Price/
│   ├── molecules/               # Combinación de átomos
│   │   ├── LeadStatusBadge/
│   │   ├── SearchBar/
│   │   ├── FormField/
│   │   ├── VehiclePrice/
│   │   ├── UserInfo/
│   │   └── DateDisplay/
│   ├── organisms/               # Grupos de moléculas
│   │   ├── DataTable/
│   │   ├── FilterBar/
│   │   ├── Pagination/
│   │   ├── CardGrid/
│   │   └── FormSection/
│   ├── templates/               # Layouts de página
│   │   ├── DashboardLayout/
│   │   ├── DetailLayout/
│   │   ├── SplitLayout/
│   │   └── AuthLayout/
│   └── pages/                   # Páginas completas (raramente aquí)
│
├── shared/                      ← 🛠️ SHARED/COMMON
│   ├── api/                     # Config base de API
│   │   ├── client.ts            # Axios instance
│   │   ├── interceptors.ts      # JWT, error handling
│   │   └── types.ts             # ApiResponse, Paginated
│   ├── hooks/                   # Hooks genéricos
│   │   ├── usePagination.ts
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   └── useMediaQuery.ts
│   ├── utils/                   # Utilidades puras
│   │   ├── formatters.ts        # currency, date, phone
│   │   ├── validators.ts        # zod schemas
│   │   └── helpers.ts
│   ├── constants/               # Constantes globales
│   │   ├── routes.ts
│   │   ├── lead-status.ts
│   │   └── api-endpoints.ts
│   └── types/                   # Types compartidos
│       ├── pagination.ts
│       └── api.ts
│
├── providers/                   # 🌐 PROVIDERS
│   ├── QueryProvider.tsx        # TanStack Query
│   ├── ThemeProvider.tsx         # NextThemes
│   └── AuthProvider.tsx         # Auth context
│
├── lib/                         # 📚 LIBRARIES CONFIG
│   ├── utils.ts                 # cn() helper
│   └── formatter.ts             # date-fns config
│
├── App.tsx
├── main.tsx
└── index.css
```

---

## ⚛️ ATOMIC DESIGN SYSTEM

### 1. ÁTOMOS (Atoms)

Componentes más básicos, no se descomponen más.

```tsx
// atomic-design/atoms/Button/Button.tsx
interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  children: React.ReactNode;
}

export const Button = ({ variant = 'primary', size = 'md', isLoading, children }: ButtonProps) => {
  const variants = {
    primary: 'bg-amber-600 hover:bg-amber-700 text-white shadow-lg shadow-amber-600/25',
    secondary: 'bg-slate-700 hover:bg-slate-600 text-slate-100 border border-slate-600',
    danger: 'bg-red-600 hover:bg-red-700 text-white',
    ghost: 'hover:bg-slate-800 text-slate-300 hover:text-white',
  };

  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  };

  return (
    <button className={cn('rounded-lg font-semibold transition-all', variants[variant], sizes[size])}>
      {isLoading ? <Spinner /> : children}
    </button>
  );
};
```

#### Lista de Átomos

| Átomo | Props | Descripción |
|-------|-------|-------------|
| `Button` | variant, size, isLoading | Botón con variantes amber/slate |
| `Input` | type, error, icon | Input dark theme con icono opcional |
| `Label` | required, children | Label uppercase tracking-wider |
| `Badge` | status, size | Badge para estados de lead |
| `Avatar` | src, fallback, size | Avatar usuario con fallback |
| `Price` | value, currency | Display de precio formateado |
| `Icon` | name, size, color | Wrapper de Lucide icons |

### 2. MOLÉCULAS (Molecules)

Combinación de átomos que forman componentes funcionales.

```tsx
// atomic-design/molecules/LeadStatusBadge/LeadStatusBadge.tsx
import { Badge } from '@/atomic-design/atoms/Badge';
import { LeadStatus } from '@/shared/constants/lead-status';

interface LeadStatusBadgeProps {
  status: LeadStatus;
}

export const LeadStatusBadge = ({ status }: LeadStatusBadgeProps) => {
  const styles = {
    NUEVO: 'bg-blue-500/20 text-blue-400 border-blue-500/30',
    CONTACTADO: 'bg-amber-500/20 text-amber-400 border-amber-500/30',
    COTIZADO: 'bg-amber-600/20 text-amber-500 border-amber-600/30',
    ENTREGADO: 'bg-green-500/20 text-green-400 border-green-500/30',
    DESCARTADO: 'bg-slate-500/20 text-slate-400 border-slate-500/30',
    // ... etc
  };

  return (
    <Badge className={cn('border', styles[status])}>
      {status.replace('_', ' ')}
    </Badge>
  );
};
```

#### Lista de Moléculas

| Molécula | Átomos Usados | Descripción |
|----------|---------------|-------------|
| `LeadStatusBadge` | Badge | Badge con color según estado |
| `SearchBar` | Input, Icon, Button | Barra de búsqueda con icono |
| `FormField` | Label, Input, Text | Campo de form con error |
| `VehiclePrice` | Price, Badge | Precio + badge de estado |
| `UserInfo` | Avatar, Text | Avatar + nombre + rol |
| `DateDisplay` | Text, Icon | Fecha formateada con icono |

### 3. ORGANISMOS (Organisms)

Grupos de moléculas que forman secciones complejas.

```tsx
// atomic-design/organisms/DataTable/DataTable.tsx
interface DataTableProps<T> {
  data: T[];
  columns: ColumnDef<T>[];
  pagination: PaginationState;
  onPageChange: (page: number) => void;
  isLoading?: boolean;
}

export function DataTable<T>({ data, columns, pagination, onPageChange, isLoading }: DataTableProps<T>) {
  return (
    <div className="glass-card rounded-xl overflow-hidden">
      <Table>
        <TableHeader className="bg-slate-800/50">
          {/* ... */}
        </TableHeader>
        <TableBody>
          {isLoading ? <SkeletonRows /> : data.map(renderRow)}
        </TableBody>
      </Table>
      <Pagination {...pagination} onChange={onPageChange} />
    </div>
  );
}
```

#### Lista de Organismos

| Organismo | Moléculas | Descripción |
|-----------|-----------|-------------|
| `DataTable` | SearchBar, Pagination, Badge | Tabla completa con filtros |
| `FilterBar` | Input, Select, Button | Barra de filtros avanzados |
| `Pagination` | Button, Text | Controles de paginación |
| `CardGrid` | VehicleCard (molecule) | Grid de cards responsive |
| `FormSection` | FormField, Button | Sección de formulario |

### 4. TEMPLATES (Templates)

Layouts de página que definen la estructura.

```tsx
// atomic-design/templates/DashboardLayout/DashboardLayout.tsx
export const DashboardLayout = ({ children }: { children: React.ReactNode }) => {
  return (
    <div className="min-h-screen bg-slate-900">
      {/* Header */}
      <header className="h-16 bg-slate-800/50 backdrop-blur-lg border-b border-slate-700/50 fixed top-0 w-full z-50">
        <div className="flex items-center justify-between h-full px-6">
          <Logo />
          <div className="flex items-center gap-4">
            <Notifications />
            <UserMenu />
          </div>
        </div>
      </header>

      {/* Sidebar */}
      <aside className="fixed left-0 top-16 w-64 h-[calc(100vh-4rem)] bg-slate-800/30 border-r border-slate-700/50">
        <Navigation />
      </aside>

      {/* Main Content */}
      <main className="ml-64 pt-16 min-h-screen">
        <div className="p-6">
          {children}
        </div>
      </main>
    </div>
  );
};
```

#### Lista de Templates

| Template | Uso | Descripción |
|----------|-----|-------------|
| `DashboardLayout` | Todas las páginas internas | Layout con sidebar + header |
| `AuthLayout` | Login, Onboarding | Layout centrado minimalista |
| `DetailLayout` | LeadDetail, VehicleDetail | Split view: info + sidebar |
| `SplitLayout` | Cotización, Test Drive | Dos columnas 60/40 |

---

## 📱 PÁGINAS Y VISTAS

### 1. LOGIN PAGE (`features/auth/pages/LoginPage.tsx`)

Diseño premium dark, centrado, efecto glassmorphism.

```tsx
export const LoginPage = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4">
      <div className="glass-card w-full max-w-md p-8 rounded-2xl">
        <div className="text-center mb-8">
          <img src="/logo-Concessio.svg" alt="Concessio" className="h-16 mx-auto mb-4" />
          <h1 className="text-2xl font-bold text-white tracking-tight">Concessio</h1>
          <p className="text-slate-400 mt-2">Sistema de Gestión de Concesionarios</p>
        </div>
        <LoginForm />
      </div>
    </div>
  );
};
```

### 2. DASHBOARD (`features/dashboard/pages/DashboardPage.tsx`)

```tsx
export const DashboardPage = () => {
  const { stats } = useDashboard();

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white uppercase tracking-wider">
          <span className="text-amber-500">Dashboard</span>
        </h1>
        <span className="text-slate-400">{formatDate(new Date())}</span>
      </div>

      {/* KPIs Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KPICard title="Leads Activos" value={stats.leads.total} icon={Users} trend={+12} />
        <KPICard title="Vehículos" value={stats.vehicles.available} icon={Car} trend={-3} />
        <KPICard title="Cotizaciones" value={stats.quotations.thisMonth} icon={DollarSign} trend={+25} />
        <KPICard title="Test Drives" value={stats.testDrives.pending} icon={Calendar} trend={+5} />
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <PipelineChart data={stats.pipeline} />
        <RecentActivityList activities={stats.recentActivity} />
      </div>
    </div>
  );
};
```

### 3. LEADS PAGE (`features/leads/pages/LeadsPage.tsx`)

```tsx
export const LeadsPage = () => {
  const { leads, pagination, filters, setFilters } = useLeads();

  return (
    <div className="space-y-6">
      {/* Header con acciones */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white uppercase tracking-wider">
            <span className="text-amber-500">Gestión de</span> Leads
          </h1>
          <p className="text-slate-400 mt-1">Administre sus prospectos y seguimientos</p>
        </div>
        <div className="flex gap-3">
          <Button variant="secondary" icon={Upload}>Importar Excel</Button>
          <Button variant="primary" icon={Plus}>Nuevo Lead</Button>
        </div>
      </div>

      {/* Filters Bar */}
      <FilterBar
        filters={[
          { key: 'search', type: 'search', placeholder: 'Buscar por nombre, teléfono...' },
          { key: 'status', type: 'select', options: LEAD_STATUS_OPTIONS, placeholder: 'Estado' },
          { key: 'source', type: 'select', options: SOURCE_OPTIONS, placeholder: 'Fuente' },
          { key: 'assigned', type: 'select', options: users, placeholder: 'Asignado a' },
        ]}
        onChange={setFilters}
        values={filters}
      />

      {/* Leads Table */}
      <DataTable
        data={leads}
        columns={leadColumns}
        pagination={pagination}
        onPageChange={pagination.setPage}
      />
    </div>
  );
};
```

### 4. LEAD DETAIL PAGE (`features/leads/pages/LeadDetailPage.tsx`)

```tsx
export const LeadDetailPage = () => {
  const { leadId } = useParams();
  const { lead, activities, quotations, testDrives } = useLeadDetail(leadId);

  return (
    <DetailLayout
      header={<LeadDetailHeader lead={lead} />}
      sidebar={<LeadActionsSidebar lead={lead} />}
    >
      <Tabs defaultValue="general">
        <TabsList className="bg-slate-800/50 border border-slate-700/50">
          <TabsTrigger value="general">General</TabsTrigger>
          <TabsTrigger value="cotizaciones">Cotizaciones ({quotations.length})</TabsTrigger>
          <TabsTrigger value="test-drives">Test Drives ({testDrives.length})</TabsTrigger>
          <TabsTrigger value="actividad">Actividad</TabsTrigger>
          <TabsTrigger value="documentos">Documentos</TabsTrigger>
        </TabsList>

        <TabsContent value="general" className="mt-6">
          <LeadInfoCard lead={lead} />
        </TabsContent>

        <TabsContent value="cotizaciones" className="mt-6">
          <QuotationList quotations={quotations} />
        </TabsContent>

        <TabsContent value="actividad" className="mt-6">
          <Timeline activities={activities} />
        </TabsContent>
      </Tabs>
    </DetailLayout>
  );
};
```

### 5. VEHICLES PAGE (`features/vehicles/pages/VehiclesPage.tsx`)

```tsx
export const VehiclesPage = () => {
  const { vehicles, pagination, viewMode, setViewMode } = useVehicles();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white uppercase tracking-wider">
          <span className="text-amber-500">Catálogo de</span> Vehículos
        </h1>
        <div className="flex gap-3">
          <ViewToggle view={viewMode} onChange={setViewMode} />
          <Button variant="primary" icon={Plus}>Nuevo Vehículo</Button>
        </div>
      </div>

      {viewMode === 'grid' ? (
        <VehicleGrid vehicles={vehicles} />
      ) : (
        <DataTable data={vehicles} columns={vehicleColumns} />
      )}
    </div>
  );
};
```

### 6. QUOTATIONS PAGE (`features/quotations/pages/QuotationsPage.tsx`)

```tsx
export const QuotationsPage = () => {
  const { quotations, pagination } = useQuotations();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white uppercase tracking-wider">
          <span className="text-amber-500">Cotizaciones</span>
        </h1>
        <Button variant="primary" icon={Calculator}>Nueva Cotización</Button>
      </div>

      <DataTable data={quotations} columns={quotationColumns} />
    </div>
  );
};
```

### 7. NEW QUOTATION WIZARD (`features/quotations/pages/NewQuotationPage.tsx`)

```tsx
export const NewQuotationPage = () => {
  const [step, setStep] = useState(1);
  const [quotationData, setQuotationData] = useState({});

  const steps = [
    { id: 1, title: 'Tipo', component: QuotationTypeSelector },
    { id: 2, title: 'Lead & Vehículo', component: LeadVehicleStep },
    { id: 3, title: 'Cálculo', component: QuotationCalculator },
    { id: 4, title: 'Resumen', component: QuotationSummary },
  ];

  return (
    <div className="max-w-4xl mx-auto">
      <Stepper steps={steps} current={step} />
      <div className="glass-card mt-6 p-6">
        <CurrentStepComponent data={quotationData} onUpdate={setQuotationData} />
        <div className="flex justify-between mt-6">
          <Button variant="ghost" onClick={() => setStep(s => s - 1)}>Anterior</Button>
          <Button variant="primary" onClick={() => setStep(s => s + 1)}>Siguiente</Button>
        </div>
      </div>
    </div>
  );
};
```

---

## 🔌 INTEGRACIÓN API

### Configuración Base

```typescript
// shared/api/client.ts
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// JWT Interceptor
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Error Interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      toast.error('Sesión expirada');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### TanStack Query Hooks

```typescript
// features/leads/hooks/useLeads.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

const LEADS_QUERY_KEY = 'leads';

export const useLeads = (page: number, size: number, filters?: LeadFilters) => {
  return useQuery({
    queryKey: [LEADS_QUERY_KEY, page, size, filters],
    queryFn: () => leadsApi.getAll({ page, size, ...filters }),
    keepPreviousData: true,
  });
};

export const useCreateLead = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: leadsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [LEADS_QUERY_KEY] });
      toast.success('Lead creado exitosamente');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error al crear lead');
    },
  });
};

export const useUpdateLeadStatus = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: LeadStatus }) =>
      leadsApi.updateStatus(id, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [LEADS_QUERY_KEY] });
    },
  });
};
```

### Zustand Store Example

```typescript
// features/leads/stores/leadStore.ts
import { create } from 'zustand';

interface LeadStore {
  selectedLead: Lead | null;
  filters: LeadFilters;
  setSelectedLead: (lead: Lead | null) => void;
  setFilters: (filters: LeadFilters) => void;
  resetFilters: () => void;
}

export const useLeadStore = create<LeadStore>((set) => ({
  selectedLead: null,
  filters: {},
  setSelectedLead: (lead) => set({ selectedLead: lead }),
  setFilters: (filters) => set((state) => ({ filters: { ...state.filters, ...filters } })),
  resetFilters: () => set({ filters: {} }),
}));
```

---

## 🧮 UTILIDADES Y FORMATEO

```typescript
// shared/utils/formatters.ts

export const formatCurrency = (value: number | string): string => {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    minimumFractionDigits: 0,
  }).format(Number(value));
};

export const formatDate = (date: string | Date): string => {
  return new Intl.DateTimeFormat('es-AR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(date));
};

export const formatDateTime = (date: string | Date): string => {
  return new Intl.DateTimeFormat('es-AR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(date));
};

export const formatPhone = (phone: string): string => {
  if (phone.startsWith('+54')) {
    return phone.replace(/(\+54\s)(\d{2})(\d{4})(\d{4})/, '$1 $2 $3-$4');
  }
  return phone;
};
```

---

## 📝 VALIDACIÓN CON ZOD

```typescript
// shared/utils/validators.ts
import { z } from 'zod';

export const LeadSchema = z.object({
  firstName: z.string().min(2, 'Nombre es requerido'),
  lastName: z.string().min(2, 'Apellido es requerido'),
  phone: z.string().min(10, 'Teléfono inválido'),
  email: z.string().email('Email inválido').optional(),
  dni: z.string().optional(),
  status: z.enum(['NUEVO', 'CONTACTADO', 'EN_SEGUIMIENTO', 'COTIZADO', 'TEST_DRIVE', 'NEGOCIACION', 'ENTREGADO', 'DESCARTADO']),
  source: z.enum(['WEB', 'REFERIDO', 'CARTEL', 'REDES_SOCIALES', 'EXCEL', 'TELEFONO', 'VISITA', 'OTRO']),
});

export const QuotationSchema = z.object({
  type: z.enum(['CONTADO', 'FINANCIADO', 'PLAN_FIAT']),
  vehicleModel: z.string().min(1, 'Vehículo requerido'),
  priceList: z.number().positive('Precio debe ser positivo'),
  discount: z.number().min(0).default(0),
  priceFinal: z.number().positive(),
  // Condicionales según tipo...
});

export type LeadFormData = z.infer<typeof LeadSchema>;
export type QuotationFormData = z.infer<typeof QuotationSchema>;
```

---

## 🛣️ RUTAS

```typescript
// shared/constants/routes.ts
export const ROUTES = {
  // Auth
  LOGIN: '/login',
  ONBOARDING: '/onboarding',

  // Main
  DASHBOARD: '/',

  // Leads
  LEADS: '/leads',
  LEAD_DETAIL: (id: number) => `/leads/${id}`,
  NEW_LEAD: '/leads/new',

  // Vehicles
  VEHICLES: '/vehicles',
  VEHICLE_DETAIL: (id: number) => `/vehicles/${id}`,

  // Quotations
  QUOTATIONS: '/quotations',
  NEW_QUOTATION: '/quotations/new',

  // Test Drives
  TEST_DRIVES: '/test-drives',
  CALENDAR: '/test-drives/calendar',

  // Users
  USERS: '/users',
} as const;
```

---

## 🚀 COMANDOS DE INSTALACIÓN

```bash
# 1. Crear proyecto con Vite + React + TS
npm create vite@latest concessio-frontend -- --template react-ts
cd concessio-frontend

# 2. Instalar dependencias core
npm install @tanstack/react-query zustand axios react-router-dom
npm install lucide-react framer-motion date-fns zod @hookform/resolvers
npm install tailwindcss-animate class-variance-authority clsx tailwind-merge

# 3. Inicializar shadcn/ui
npx shadcn@latest init

# 4. Agregar componentes shadcn
npx shadcn add button card table badge dialog input select tabs avatar dropdown-menu toast pagination calendar skeleton

# 5. Configurar Tailwind (ya viene con shadcn)
# Agregar colores custom en tailwind.config.js:
colors: {
  amber: {
    500: '#D97706',
    600: '#B45309',
  },
  slate: {
    800: '#1E293B',
    900: '#0F172A',
  }
}

# 6. Configurar path aliases en tsconfig.json
"paths": {
  "@/*": ["./src/*"],
  "@/features/*": ["./src/features/*"],
  "@/shared/*": ["./src/shared/*"],
  "@/atomic-design/*": ["./src/atomic-design/*"]
}

# 7. Iniciar desarrollo
npm run dev
```

---

## 📋 CHECKLIST PARA LOVABLE AI

### ✅ Antes de empezar
- [ ] Entender que NO es un CRM tipo Salesforce
- [ ] Es un CRM automotriz premium estilo Concessio Jeep
- [ ] Dark theme con acentos ámbar/dorado
- [ ] Tipografía bold, uppercase para títulos
- [ ] Glassmorphism en cards

### ✅ Arquitectura
- [ ] Crear carpetas por feature (Screaming Architecture)
- [ ] Usar Atomic Design (atoms → molecules → organisms → templates → pages)
- [ ] Cada feature tiene: components/, hooks/, stores/, api/, types/, pages/
- [ ] Shared para código reutilizable

### ✅ Componentes Base (Átomos)
- [ ] Button con variantes amber, slate, ghost
- [ ] Input dark con iconos
- [ ] Badge para estados de lead
- [ ] Price display formateado

### ✅ Páginas Principales
- [ ] Login (centrado, glassmorphism, dark)
- [ ] Dashboard (KPIs, gráficos, actividad reciente)
- [ ] Leads (tabla, filtros, acciones)
- [ ] Lead Detail (tabs, info, cotizaciones, actividad)
- [ ] Vehículos (grid + tabla toggle)
- [ ] Cotizaciones (wizard de 4 pasos)
- [ ] Test Drives (calendario)

### ✅ Integración
- [ ] Configurar axios con interceptors
- [ ] TanStack Query para server state
- [ ] Zustand para client state
- [ ] React Router para navegación

### ✅ Estilo Visual
- [ ] Background: slate-900
- [ ] Cards: slate-800/80 con backdrop-blur
- [ ] Primary: amber-500/600
- [ ] Text: white/slate-300
- [ ] Borders: slate-700/50
- [ ] Sombras: shadow-lg shadow-amber-600/25 para CTAs

---

## 🎨 REFERENCIA VISUAL

### Dashboard Layout
```
┌──────────────────────────────────────────────────────────────┐
│  [LOGO]              👤 Usuario    🔔    ⚙️    [SALIR]        │  ← Header slate-800/50
├──────────────────────────────────────────────────────────────┤
│  ┌─────────┐                                                 │
│  │  📊     │  ┌────────────────────────────────────────────┐  │
│  │ DASH    │  │  <span class="text-amber-500">DASHBOARD</span>              [Fecha]      │  │
│  │         │  ├────────────────────────────────────────────┤  │
│  │ 📋 LEADS│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ │  │
│  │ 🚗 AUTOS│  │  │ Leads  │ │Autos   │ │Cotiz   │ │Test    │ │  │
│  │ 💰 COTIZ│  │  │  150   │ │  45    │ │  32    │ │  12    │ │  │
│  │         │  │  │  ↑12%   │ │ ↓3%    │ │ ↑25%   │ │ ↑5%    │ │  │
│  │         │  │  └────────┘ └────────┘ └────────┘ └────────┘ │  │
│  │         │  │                                              │  │
│  │         │  │  ┌─────────────────────┐ ┌───────────────┐  │  │
│  │         │  │  │ Pipeline Chart      │ │ Próximos Test │  │  │
│  │         │  │  │                     │ │               │  │  │
│  │         │  │  │ [Gráfico embudo]    │ │ • 14:00 Juan  │  │  │
│  │         │  │  │                     │ │ • 16:30 María │  │  │
│  └─────────┘  │  └─────────────────────┘ └───────────────┘  │  │
└───────────────┘
```

### Leads Table
```
┌─────────────────────────────────────────────────────────────────────────┐
│  <span class="text-amber-500">GESTIÓN DE</span> LEADS        [Importar] [+ Nuevo Lead]                    │
├─────────────────────────────────────────────────────────────────────────┤
│  🔍 Buscar...    [Estado ▼] [Fuente ▼] [Asignado ▼]                    │
├─────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐│
│  │ Nombre      │ Teléfono    │ Estado       │ Asignado    │ Acciones  ││
│  ├─────────────┼─────────────┼──────────────┼─────────────┼───────────┤│
│  │ Juan Pérez  │ 11 1234...  │ NUEVO       │ María G.    │ 👁 ✏ 🗑   ││
│  │ Ana López   │ 11 8765...  │ COTIZADO    │ Juan P.     │ 👁 ✏ 🗑   ││
│  └─────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────┘
```

---

**Generado para:** Lovable AI  
**Proyecto:** Concessio CRM  
**Arquitectura:** Screaming + Atomic Design  
**Estilo:** Premium Automotriz Dark Theme  
**Fecha:** Abril 2026
