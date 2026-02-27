---
applyTo: "DrinkGo-frontend/**/*.ts,DrinkGo-frontend/**/*.tsx,DrinkGo-frontend/**/*.jsx,DrinkGo-frontend/**/*.css,DrinkGo-frontend/package.json,DrinkGo-frontend/index.html,DrinkGo-frontend/vite.config.js"
---

# Frontend Context: DrinkGo - React 19 + Vite + Tailwind CSS

## CRITICAL RULES (Read First)

**Multi-Tenant Architecture:** DrinkGo is a SaaS platform with 3 isolated scopes. See [Architecture](#architecture) for routing rules.

**Package Manager:** pnpm ONLY (NOT npm/yarn)
```bash
✅ pnpm dev
✅ pnpm build
✅ pnpm add axios
❌ npm install  # NEVER
```

**Path Aliases (MANDATORY):**
```tsx
✅ import { api } from '@/services/api';
✅ import { Button } from '@/components/ui/Button';
✅ import type { Product } from '@/types';
❌ import { api } from '../../../../services/api';  // NO deep relative paths
```

**UI Stack (STRICT):**
- **Styling:** Tailwind CSS ONLY
- **Icons:** Lucide React (`lucide-react`) ONLY
- **Components:** Custom components in `src/[scope]/components/ui/`
- **Design:** Clean, minimalist, professional (inspired by Pegasus)

**Language:**
- Code/variables/functions: **English**
- ALL user-facing text (UI/messages): **Spanish**

**Quality:**
- Code MUST compile without errors
- Run `pnpm run build` before submitting
- NO unused imports
- NO console.log in production

**Never Use:**
- ❌ Native browser alerts (`alert()`, `confirm()`, `prompt()`)
- ❌ Inline styles (use Tailwind classes)
- ❌ CSS modules (use Tailwind)
- ❌ Any/unknown without proper typing

---

## 1. Tech Stack

```json
{
  "core": "React 19 + TypeScript + Vite 7",
  "router": "React Router DOM v7",
  "state": {
    "server": "TanStack Query v5 (MANDATORY for API)",
    "client": "Zustand (auth, cart, sidebar)"
  },
  "http": "Axios (configured in services/api.js)",
  "styling": "Tailwind CSS 3.4",
  "forms": "React Hook Form + Zod",
  "icons": "Lucide React",
  "dates": "date-fns"
}
```

**Environment Variables:**
```bash
# .env
VITE_API_BASE_URL=http://localhost:8080/api  # Spring Boot backend
VITE_ENV=development
VITE_ENABLE_LOGS=true
```

**Path Aliases Config:**
```javascript
// vite.config.js
export default defineConfig({
  resolve: {
    alias: {
      '@': '/src',
    },
  },
});

// jsconfig.json or tsconfig.json
{
  "compilerOptions": {
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

**Required Package Installation:**
```bash
pnpm add @tanstack/react-query zustand react-hook-form zod
```

---

<a name="architecture"></a>
## 2. Architecture: Multi-Tenant Scopes

DrinkGo has **3 isolated application scopes**. Each scope has its own layouts, components, pages, and services.

### A. Scope Definitions

**1. SuperAdmin (`/superadmin/*`)**
- **Purpose:** Platform management (SaaS control panel)
- **Users:** Platform administrators
- **Features:** Manage tenants (negocios), subscription plans, billing, platform analytics
- **Root:** `src/superadmin/`

**2. Admin (`/admin/*`)**
- **Purpose:** Business operations (liquor store management)
- **Users:** Store owners, managers, employees
- **Features:** POS, inventory, catalog, orders, reports, settings
- **Root:** `src/admin/`

**3. Storefront (`/tienda/*`)**
- **Purpose:** Public e-commerce (customer-facing)
- **Users:** End customers
- **Features:** Product catalog, shopping cart, checkout, order tracking
- **Root:** `src/storefront/`

### B. Isolation Rules (CRITICAL)

**NEVER mix scopes:**
```tsx
// ❌ WRONG: Admin importing from SuperAdmin
import { NegocioService } from '@/superadmin/services/negociosService';

// ✅ CORRECT: Create shared utilities
import { formatCurrency } from '@/shared/utils/formatters';
```

**Shared Resources:**
- ✅ `src/shared/` - Common utilities, types, hooks
- ✅ `src/config/` - API client, query client
- ✅ `src/types/` - Global TypeScript interfaces
- ❌ Direct imports between `superadmin/`, `admin/`, `storefront/`

### C. Directory Structure

```
src/
├── config/              # API client, query client
│   ├── api.js           # Axios instance
│   └── queryClient.js   # TanStack Query client
├── types/               # Global TypeScript types
│   └── index.ts
├── shared/              # Shared across ALL scopes
│   ├── components/      # Universal components (ErrorBoundary, etc.)
│   ├── hooks/           # useDebounce, useLocalStorage
│   └── utils/           # formatCurrency, validateRUC, etc.
├── superadmin/          # SCOPE 1: Platform Management
│   ├── components/
│   │   ├── forms/       # NegocioForm, PlanForm
│   │   ├── modals/      # FacturaDetail, NegocioDetail
│   │   └── ui/          # Badge, Button, Card, Modal, Table
│   ├── layouts/
│   │   └── SuperAdminLayout.jsx
│   ├── pages/
│   │   ├── Dashboard.jsx
│   │   ├── Negocios.jsx
│   │   ├── Planes.jsx
│   │   └── Facturacion.jsx
│   ├── routes/
│   │   └── SuperAdminRoutes.jsx
│   └── services/
│       ├── authService.js
│       ├── negociosService.js
│       ├── planesService.js
│       └── facturacionService.js
├── admin/               # SCOPE 2: Business Operations
│   ├── components/
│   ├── layouts/
│   ├── pages/
│   ├── routes/
│   └── services/
└── storefront/          # SCOPE 3: E-commerce
    ├── components/
    ├── layouts/
    ├── pages/
    ├── routes/
    └── services/
```

### D. Module Anatomy (Standard Pattern)

**Example:** `superadmin/pages/Negocios.jsx`

```
Negocios Module:
├── pages/Negocios.jsx          # Route entry point (list page)
├── components/
│   └── forms/NegocioForm.jsx   # Create/Edit form
│   └── modals/NegocioDetail.jsx # View details modal
└── services/negociosService.js # API calls (getNegocios, createNegocio)
```

**File Responsibilities:**
- **Page:** Layout, orchestrates components
- **Component:** Reusable UI, receives props
- **Service:** Pure API calls, returns Promise
- **Hook:** Business logic, state management

---

## 3. Pattern: Service → Hook → Component → Page

### A. Service Layer (API Calls)

**Location:** `[scope]/services/{entity}Service.js`

```javascript
// superadmin/services/negociosService.js
import { api } from '@/config/api';

export const negociosService = {
  getAll: async (page = 0, size = 10, search = '') => {
    const params = { page, size };
    if (search) params.search = search;
    const { data } = await api.get('/superadmin/negocios', { params });
    return data; // PageResponse<Negocio>
  },

  getById: async (id) => {
    const { data } = await api.get(`/superadmin/negocios/${id}`);
    return data;
  },

  create: async (negocio) => {
    const { data } = await api.post('/superadmin/negocios', negocio);
    return data;
  },

  update: async (id, negocio) => {
    const { data } = await api.put(`/superadmin/negocios/${id}`, negocio);
    return data;
  },

  delete: async (id) => {
    const { data } = await api.delete(`/superadmin/negocios/${id}`);
    return data;
  },

  changeStatus: async (id, status) => {
    const { data } = await api.patch(`/superadmin/negocios/${id}/status`, { status });
    return data;
  },
};
```

### B. Hook (Business Logic)

**Location:** `[scope]/hooks/use{Entity}.js` or inline in component

```javascript
// superadmin/hooks/useNegocios.js
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { negociosService } from '../services/negociosService';
import { message } from '@/shared/utils/notifications'; // Custom notification wrapper

export const useNegocios = (page = 0, size = 10, search = '') => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['negocios', page, size, search],
    queryFn: () => negociosService.getAll(page, size, search),
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  const createMutation = useMutation({
    mutationFn: negociosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['negocios'] });
      message.success('Negocio creado exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al crear negocio');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => negociosService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['negocios'] });
      message.success('Negocio actualizado exitosamente');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: negociosService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['negocios'] });
      message.success('Negocio eliminado exitosamente');
    },
  });

  return {
    // Query data
    negocios: query.data?.content || [],
    totalPages: query.data?.totalPages || 0,
    totalElements: query.data?.totalElements || 0,
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,

    // Mutations
    createNegocio: createMutation.mutateAsync,
    updateNegocio: updateMutation.mutateAsync,
    deleteNegocio: deleteMutation.mutateAsync,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};
```

### C. Component (UI Only)

**Location:** `[scope]/components/{ComponentName}.jsx`

```jsx
// superadmin/components/forms/NegocioForm.jsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';

const negocioSchema = z.object({
  ruc: z.string().length(11, 'RUC debe tener 11 dígitos').regex(/^\d+$/, 'Solo números'),
  razonSocial: z.string().min(3, 'Razón social muy corta'),
  email: z.string().email('Email inválido'),
  telefono: z.string().regex(/^9\d{8}$/, 'Teléfono inválido (formato: 9XXXXXXXX)'),
});

export const NegocioForm = ({ initialData, onSubmit, onCancel, isLoading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(negocioSchema),
    defaultValues: initialData,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700">
          RUC <span className="text-red-500">*</span>
        </label>
        <Input {...register('ruc')} placeholder="20123456789" maxLength={11} />
        {errors.ruc && <p className="text-sm text-red-500">{errors.ruc.message}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">
          Razón Social <span className="text-red-500">*</span>
        </label>
        <Input {...register('razonSocial')} placeholder="Licorería El Buen Gusto S.A.C." />
        {errors.razonSocial && <p className="text-sm text-red-500">{errors.razonSocial.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Email</label>
          <Input {...register('email')} type="email" placeholder="contacto@ejemplo.com" />
          {errors.email && <p className="text-sm text-red-500">{errors.email.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">Teléfono</label>
          <Input {...register('telefono')} placeholder="987654321" maxLength={9} />
          {errors.telefono && <p className="text-sm text-red-500">{errors.telefono.message}</p>}
        </div>
      </div>

      <div className="flex justify-end gap-2 pt-4">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : 'Guardar'}
        </Button>
      </div>
    </form>
  );
};
```

### D. Page (Layout + Orchestration)

**Location:** `[scope]/pages/{PageName}.jsx`

```jsx
// superadmin/pages/Negocios.jsx
import { useState } from 'react';
import { Plus, Eye, Edit, Trash2 } from 'lucide-react';
import { useNegocios } from '../hooks/useNegocios';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Table } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { NegocioForm } from '../components/forms/NegocioForm';
import { NegocioDetail } from '../components/modals/NegocioDetail';
import { useDebounce } from '@/shared/hooks/useDebounce';

export const Negocios = () => {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 500);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedNegocio, setSelectedNegocio] = useState(null);

  const {
    negocios,
    totalElements,
    isLoading,
    createNegocio,
    updateNegocio,
    deleteNegocio,
    isCreating,
  } = useNegocios(page, pageSize, debouncedSearch);

  const handleCreate = async (data) => {
    await createNegocio(data);
    setIsCreateModalOpen(false);
  };

  const handleView = (negocio) => {
    setSelectedNegocio(negocio);
    setIsDetailModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este negocio?')) {
      await deleteNegocio(id);
    }
  };

  const columns = [
    {
      key: 'index',
      title: '#',
      width: '60px',
      render: (_, __, index) => page * pageSize + index + 1,
    },
    {
      key: 'ruc',
      title: 'RUC',
      dataIndex: 'ruc',
    },
    {
      key: 'razonSocial',
      title: 'Razón Social',
      dataIndex: 'razonSocial',
    },
    {
      key: 'plan',
      title: 'Plan',
      dataIndex: ['plan', 'nombre'],
    },
    {
      key: 'estado',
      title: 'Estado',
      dataIndex: 'estado',
      render: (estado) => (
        <Badge
          variant={
            estado === 'ACTIVO' ? 'success' :
            estado === 'SUSPENDIDO' ? 'warning' :
            'error'
          }
        >
          {estado}
        </Badge>
      ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, record) => (
        <div className="flex justify-center gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => handleView(record)}
            title="Ver detalles"
          >
            <Eye size={16} />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {/* handle edit */}}
            title="Editar"
          >
            <Edit size={16} />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => handleDelete(record.id)}
            title="Eliminar"
            className="text-red-500 hover:text-red-700"
          >
            <Trash2 size={16} />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Negocios</h1>
        <p className="text-gray-600 mt-1">
          Gestión de licorerías registradas en la plataforma
        </p>
      </div>

      {/* Card Container */}
      <Card>
        {/* Search + Create */}
        <div className="flex justify-between items-center mb-4">
          <input
            type="text"
            placeholder="Buscar por RUC, razón social o email..."
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setPage(0);
            }}
            className="border border-gray-300 rounded-lg px-4 py-2 w-96 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <Button onClick={() => setIsCreateModalOpen(true)}>
            <Plus size={18} className="mr-2" />
            Nuevo Negocio
          </Button>
        </div>

        {/* Table */}
        <Table
          columns={columns}
          data={negocios}
          loading={isLoading}
          pagination={{
            current: page + 1, // UI is 1-based, backend is 0-based
            pageSize,
            total: totalElements,
            onChange: (newPage, newPageSize) => {
              setPage(newPage - 1);
              setPageSize(newPageSize);
            },
          }}
        />
      </Card>

      {/* Create Modal */}
      <Modal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Crear Nuevo Negocio"
      >
        <NegocioForm
          onSubmit={handleCreate}
          onCancel={() => setIsCreateModalOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* Detail Modal */}
      <Modal
        isOpen={isDetailModalOpen}
        onClose={() => setIsDetailModalOpen(false)}
        title="Detalles del Negocio"
        size="lg"
      >
        {selectedNegocio && <NegocioDetail negocio={selectedNegocio} />}
      </Modal>
    </div>
  );
};
```

---

## 4. UI/UX Guidelines (CRITICAL)

### A. Language Rules

```jsx
✅ <Button>Guardar</Button>              // Spanish UI
✅ const userName = 'John';              // English code
✅ message.success('Producto creado');   // Spanish messages
❌ <Button>Save</Button>                 // NO English in UI
❌ const nombreUsuario = 'John';         // NO Spanish in code
```

### B. Modal vs Alert Rules

**NEVER use native alerts:**
```javascript
❌ alert('Operación exitosa');
❌ const confirmed = confirm('¿Eliminar?');
❌ const input = prompt('Ingrese valor');
```

**ALWAYS use custom modals:**
```jsx
✅ <Modal title="Confirmar" onConfirm={handleDelete}>¿Eliminar?</Modal>
✅ message.success('Operación exitosa');
✅ <Drawer title="Crear Producto">...</Drawer>
```

**When to use Modal vs Drawer:**
- **Modal (centered):** Small forms (≤8 fields), confirmations, quick actions
- **Drawer (side panel):** Large forms (>8 fields), complex workflows, maintain context

### C. Design System

**Colors:**
```javascript
// Tailwind classes (semantic colors)
const COLORS = {
  // Status
  success: 'bg-green-100 text-green-700 border-green-300',
  warning: 'bg-yellow-100 text-yellow-700 border-yellow-300',
  error: 'bg-red-100 text-red-700 border-red-300',
  info: 'bg-blue-100 text-blue-700 border-blue-300',

  // Backgrounds
  bgPrimary: 'bg-white',
  bgSecondary: 'bg-gray-50',
  bgHover: 'hover:bg-gray-100',

  // Text
  textPrimary: 'text-gray-900',
  textSecondary: 'text-gray-600',
  textMuted: 'text-gray-400',
};
```

**Typography:**
```jsx
<h1 className="text-2xl font-bold text-gray-900">Page Title</h1>
<h2 className="text-xl font-semibold text-gray-800">Section Title</h2>
<p className="text-sm text-gray-600">Description text</p>
<span className="text-xs text-gray-400">Helper text</span>
```

**Spacing:**
```jsx
// Use Tailwind spacing scale (4px increments)
<div className="space-y-4">    // 16px vertical gap
<div className="gap-2">        // 8px gap
<div className="p-6">          // 24px padding
<div className="mb-4">         // 16px margin bottom
```

**Shadows & Borders:**
```jsx
<Card className="shadow-sm rounded-lg border border-gray-200">
<Button className="rounded-md shadow-sm">
```

### D. Table Structure (MANDATORY)

**ALL CRUD tables MUST include:**

1. **Header Section** (outside table)
   - Page title (h1)
   - Description (paragraph)

2. **Search + Action Bar**
   - Search input (left, max-width 400px)
   - Create button (right, primary color)

3. **Table Columns** (5-7 max)
   - First column: `#` (row number, width 60px)
   - Last column: Actions (icons only, width 120px, fixed right)
   - Status: Use Badge component with semantic colors

4. **Pagination**
   - Show page size selector
   - Show total count
   - Convert backend 0-based to UI 1-based

**Column Width Guidelines:**
```javascript
{
  '#': '60px',           // Row number
  'RUC': '120px',        // Fixed identifiers
  'Nombre': null,        // Flexible text
  'Estado': '100px',     // Badges
  'Teléfono': '140px',   // Phone (formatted)
  'Email': '200px',      // Email
  'Acciones': '120px',   // Actions (fixed right)
}
```

**Table Example:**
```jsx
<Table
  columns={[
    { key: 'index', title: '#', width: '60px', render: (_, __, i) => i + 1 },
    { key: 'codigo', title: 'Código', dataIndex: 'codigo' },
    { key: 'nombre', title: 'Nombre', dataIndex: 'nombre' },
    { key: 'categoria', title: 'Categoría', dataIndex: ['categoria', 'nombre'] },
    { key: 'precio', title: 'Precio', render: (r) => formatCurrency(r.precio) },
    { key: 'stock', title: 'Stock', dataIndex: 'stock' },
    { key: 'estado', title: 'Estado', render: (r) => <Badge>{r.estado}</Badge> },
    { key: 'actions', title: 'Acciones', width: '120px', render: ActionsCell },
  ]}
  data={productos}
  loading={isLoading}
  pagination={{
    current: page + 1,
    pageSize,
    total: totalElements,
    showSizeChanger: true,
    showTotal: (total) => `Total: ${total} productos`,
  }}
/>
```

### E. Action Icons (Lucide React)

**CRITICAL:** Actions column uses ONLY icons (NO text).

```jsx
import { Eye, Edit, Trash2, Power, Plus, Download } from 'lucide-react';

// ✅ CORRECT: Icon-only buttons with title attribute
<div className="flex justify-center gap-2">
  <button title="Ver detalles" className="text-blue-500 hover:text-blue-700">
    <Eye size={16} />
  </button>
  <button title="Editar" className="text-gray-500 hover:text-gray-700">
    <Edit size={16} />
  </button>
  <button title="Eliminar" className="text-red-500 hover:text-red-700">
    <Trash2 size={16} />
  </button>
</div>

// ❌ WRONG: Text labels cause overflow
<button>Editar Producto</button>
```

**Common Icons:**
- View: `<Eye />`
- Edit: `<Edit />`
- Delete: `<Trash2 />`
- Toggle status: `<Power />`
- Add: `<Plus />`
- Download: `<Download />`
- Search: `<Search />`
- Filter: `<Filter />`
- Settings: `<Settings />`

### F. Form Layout

**RULE:** Use grid layout. NEVER stack all fields vertically.

```jsx
// ✅ CORRECT: Grid layout (varied widths)
<form className="space-y-4">
  <div className="grid grid-cols-2 gap-4">
    <div>
      <label>Usuario *</label>
      <Input {...register('username')} />
    </div>
    <div>
      <label>Email *</label>
      <Input {...register('email')} />
    </div>
  </div>

  <div className="grid grid-cols-3 gap-4">
    <div>
      <label>Tipo Documento</label>
      <Select {...register('docType')} />
    </div>
    <div>
      <label>Número Documento</label>
      <Input {...register('docNumber')} />
    </div>
    <div>
      <label>Teléfono</label>
      <Input {...register('phone')} />
    </div>
  </div>

  <div>
    <label>Dirección Completa</label>
    <Input {...register('address')} />
  </div>
</form>

// ❌ WRONG: All fields stacked (wastes space)
<form className="space-y-4">
  <Input label="Usuario" />
  <Input label="Email" />
  <Select label="Tipo Documento" />
  <Input label="Número Documento" />
  {/* ... monotonous */}
</form>
```

**Grid Patterns:**
- `grid-cols-2`: Paired fields (username/email, firstName/lastName)
- `grid-cols-3`: Triple fields (docType/docNumber/phone)
- `grid-cols-4`: Quadruple fields (dates, small codes)
- Full width: Address, descriptions, long text

### G. Badge Component (Status Indicators)

```jsx
// components/ui/Badge.jsx
const VARIANTS = {
  success: 'bg-green-100 text-green-700 border-green-300',
  warning: 'bg-yellow-100 text-yellow-700 border-yellow-300',
  error: 'bg-red-100 text-red-700 border-red-300',
  info: 'bg-blue-100 text-blue-700 border-blue-300',
};

export const Badge = ({ children, variant = 'info' }) => (
  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${VARIANTS[variant]}`}>
    {children}
  </span>
);

// Usage
<Badge variant="success">ACTIVO</Badge>
<Badge variant="warning">PENDIENTE</Badge>
<Badge variant="error">INACTIVO</Badge>
```

**Semantic Color Mapping:**
- **Green (success):** Activo, Entregado, Aprobado, Completado
- **Yellow (warning):** Pendiente, En Proceso, Bajo Stock
- **Red (error):** Inactivo, Cancelado, Rechazado, Sin Stock
- **Blue (info):** En Tránsito, Revisión, Borrador

---

## 5. State Management

### A. Server State (TanStack Query)

**MANDATORY for ALL API calls.**

```javascript
// config/queryClient.js
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

// main.jsx
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './config/queryClient';

root.render(
  <QueryClientProvider client={queryClient}>
    <App />
  </QueryClientProvider>
);
```

**Query Pattern:**
```javascript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

// Fetch data
const { data, isLoading, error } = useQuery({
  queryKey: ['products', page, search],
  queryFn: () => productService.getAll(page, 10, search),
});

// Mutate data
const queryClient = useQueryClient();
const mutation = useMutation({
  mutationFn: productService.create,
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['products'] });
  },
});
```

### B. Client State (Zustand)

**Use ONLY for:**
- Authentication state
- Shopping cart
- UI state (sidebar open/closed, theme)

```javascript
// stores/authStore.js
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set) => ({
      user: null,
      token: null,
      login: (user, token) => set({ user, token }),
      logout: () => set({ user: null, token: null }),
      isAuthenticated: () => !!get().token,
    }),
    {
      name: 'auth-storage',
    }
  )
);
```

**❌ WRONG: Using useState for API data**
```javascript
const [products, setProducts] = useState([]);
useEffect(() => {
  fetch('/api/products').then(res => res.json()).then(setProducts);
}, []);
```

**✅ CORRECT: Using TanStack Query**
```javascript
const { data: products } = useQuery({
  queryKey: ['products'],
  queryFn: productService.getAll,
});
```

---

## 6. API Integration (Axios)

### A. API Client Configuration

```javascript
// config/api.js
import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor (add token)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor (handle errors)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### B. Service Pattern (CRUD)

```javascript
// services/productService.js
import { api } from '@/config/api';

export const productService = {
  // List with pagination
  getAll: async (page = 0, size = 10, search = '') => {
    const params = { page, size };
    if (search) params.search = search;
    const { data } = await api.get('/admin/productos', { params });
    return data; // { content: [], totalPages, totalElements }
  },

  // Get by ID
  getById: async (id) => {
    const { data } = await api.get(`/admin/productos/${id}`);
    return data;
  },

  // Create
  create: async (product) => {
    const { data } = await api.post('/admin/productos', product);
    return data;
  },

  // Update
  update: async (id, product) => {
    const { data } = await api.put(`/admin/productos/${id}`, product);
    return data;
  },

  // Delete
  delete: async (id) => {
    const { data } = await api.delete(`/admin/productos/${id}`);
    return data;
  },

  // Custom actions
  updateStock: async (id, quantity) => {
    const { data } = await api.patch(`/admin/productos/${id}/stock`, { quantity });
    return data;
  },
};
```

### C. Error Handling

```javascript
// In mutations
const createProduct = async (formData) => {
  try {
    await productService.create(formData);
    message.success('Producto creado exitosamente');
    navigate('/admin/productos');
  } catch (error) {
    const errorMessage = error.response?.data?.message || 'Error al crear producto';
    message.error(errorMessage);
    console.error('Create product error:', error);
  }
};

// In queries (handled by TanStack Query)
const { data, isLoading, error } = useQuery({
  queryKey: ['products'],
  queryFn: productService.getAll,
});

if (error) {
  return <ErrorMessage message={error.message} />;
}
```

---

## 7. Forms & Validation

### A. React Hook Form + Zod

```javascript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

// Schema
const productSchema = z.object({
  nombre: z.string().min(3, 'Nombre muy corto'),
  codigo: z.string().regex(/^[A-Z0-9-]+$/, 'Solo letras mayúsculas, números y guiones'),
  precio: z.number().positive('Precio debe ser mayor a 0'),
  stock: z.number().int().min(0, 'Stock no puede ser negativo'),
  categoriaId: z.number({ required_error: 'Seleccione una categoría' }),
});

// Form component
export const ProductForm = ({ initialData, onSubmit }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(productSchema),
    defaultValues: initialData,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700">
          Nombre <span className="text-red-500">*</span>
        </label>
        <input
          {...register('nombre')}
          className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
        />
        {errors.nombre && (
          <p className="text-sm text-red-500 mt-1">{errors.nombre.message}</p>
        )}
      </div>

      <button type="submit" className="btn-primary">
        Guardar
      </button>
    </form>
  );
};
```

### B. Validation Rules (Peru Context)

```javascript
// shared/utils/validators.js
import { z } from 'zod';

export const validators = {
  // RUC: 11 digits
  ruc: z.string().length(11, 'RUC debe tener 11 dígitos').regex(/^\d+$/, 'Solo números'),

  // DNI: 8 digits
  dni: z.string().length(8, 'DNI debe tener 8 dígitos').regex(/^\d+$/, 'Solo números'),

  // Carnet de Extranjería: 9 alphanumeric
  ce: z.string().length(9, 'CE debe tener 9 caracteres').regex(/^[A-Z0-9]+$/, 'Solo letras y números'),

  // Phone: 9 digits starting with 9
  phone: z.string().regex(/^9\d{8}$/, 'Teléfono inválido (formato: 9XXXXXXXX)'),

  // Email
  email: z.string().email('Email inválido'),

  // Currency (positive decimal, max 2 decimals)
  currency: z.number().positive('Monto debe ser mayor a 0').multipleOf(0.01, 'Máximo 2 decimales'),

  // Percentage (0-100)
  percentage: z.number().min(0, 'Mínimo 0%').max(100, 'Máximo 100%'),

  // Document type
  docType: z.enum(['DNI', 'CE', 'RUC'], { errorMap: () => ({ message: 'Tipo de documento inválido' }) }),
};

// Usage
const customerSchema = z.object({
  tipoDocumento: validators.docType,
  numeroDocumento: z.string().min(1, 'Requerido'),
  telefono: validators.phone.optional(),
  email: validators.email.optional(),
});
```

---

## 8. Routing (React Router DOM v7)

### A. Route Structure

```javascript
// App.jsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { SuperAdminRoutes } from './superadmin/routes/SuperAdminRoutes';
import { AdminRoutes } from './admin/routes/AdminRoutes';
import { StorefrontRoutes } from './storefront/routes/StorefrontRoutes';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/superadmin/*" element={<SuperAdminRoutes />} />
        <Route path="/admin/*" element={<AdminRoutes />} />
        <Route path="/tienda/*" element={<StorefrontRoutes />} />
        <Route path="/" element={<Navigate to="/superadmin/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
```

### B. Scope Routes

```javascript
// superadmin/routes/SuperAdminRoutes.jsx
import { Routes, Route, Navigate } from 'react-router-dom';
import { SuperAdminLayout } from '../layouts/SuperAdminLayout';
import { Dashboard } from '../pages/Dashboard';
import { Negocios } from '../pages/Negocios';
import { Planes } from '../pages/Planes';
import { Facturacion } from '../pages/Facturacion';
import { ConfiguracionGlobal } from '../pages/ConfiguracionGlobal';
import { AuditoriaLogs } from '../pages/AuditoriaLogs';

export const SuperAdminRoutes = () => {
  return (
    <Routes>
      <Route element={<SuperAdminLayout />}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="negocios" element={<Negocios />} />
        <Route path="planes" element={<Planes />} />
        <Route path="facturacion" element={<Facturacion />} />
        <Route path="configuracion" element={<ConfiguracionGlobal />} />
        <Route path="auditoria" element={<AuditoriaLogs />} />
      </Route>
    </Routes>
  );
};
```

### C. Protected Routes

```javascript
// components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/authStore';

export const ProtectedRoute = ({ children, requiredRole }) => {
  const { user, token } = useAuthStore();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user?.role !== requiredRole) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

// Usage
<Route
  path="dashboard"
  element={
    <ProtectedRoute requiredRole="SUPERADMIN">
      <Dashboard />
    </ProtectedRoute>
  }
/>
```

---

## 9. Shared Utilities

### A. Formatters

```javascript
// shared/utils/formatters.js

/**
 * Format currency for Peru (Soles)
 * @param {number} amount
 * @returns {string} "S/ 1,234.56"
 */
export const formatCurrency = (amount) => {
  return new Intl.NumberFormat('es-PE', {
    style: 'currency',
    currency: 'PEN',
  }).format(amount);
};

/**
 * Format date to DD/MM/YYYY
 * @param {string|Date} date
 * @returns {string} "24/02/2026"
 */
export const formatDate = (date) => {
  return new Date(date).toLocaleDateString('es-PE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
};

/**
 * Format datetime to DD/MM/YYYY HH:mm
 * @param {string|Date} datetime
 * @returns {string} "24/02/2026 14:30"
 */
export const formatDateTime = (datetime) => {
  return new Date(datetime).toLocaleDateString('es-PE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * Format phone number
 * @param {string} phone - "987654321"
 * @returns {string} "987 654 321"
 */
export const formatPhone = (phone) => {
  return phone.replace(/(\d{3})(\d{3})(\d{3})/, '$1 $2 $3');
};

/**
 * Format RUC
 * @param {string} ruc - "20123456789"
 * @returns {string} "20-12345678-9"
 */
export const formatRUC = (ruc) => {
  return ruc.replace(/(\d{2})(\d{8})(\d{1})/, '$1-$2-$3');
};

/**
 * Truncate text
 * @param {string} text
 * @param {number} maxLength
 * @returns {string}
 */
export const truncate = (text, maxLength = 50) => {
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
};
```

### B. Custom Hooks

```javascript
// shared/hooks/useDebounce.js
import { useState, useEffect } from 'react';

export const useDebounce = (value, delay = 500) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => clearTimeout(handler);
  }, [value, delay]);

  return debouncedValue;
};

// shared/hooks/useLocalStorage.js
import { useState, useEffect } from 'react';

export const useLocalStorage = (key, initialValue) => {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(error);
      return initialValue;
    }
  });

  const setValue = (value) => {
    try {
      setStoredValue(value);
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error(error);
    }
  };

  return [storedValue, setValue];
};

// shared/hooks/useClickOutside.js
import { useEffect, useRef } from 'react';

export const useClickOutside = (handler) => {
  const ref = useRef();

  useEffect(() => {
    const listener = (event) => {
      if (!ref.current || ref.current.contains(event.target)) {
        return;
      }
      handler(event);
    };

    document.addEventListener('mousedown', listener);
    document.addEventListener('touchstart', listener);

    return () => {
      document.removeEventListener('mousedown', listener);
      document.removeEventListener('touchstart', listener);
    };
  }, [handler]);

  return ref;
};
```

### C. Notification System

```javascript
// shared/utils/notifications.js
// Simple toast notification system (can be replaced with a library like react-hot-toast)

const notifications = {
  success: (message) => {
    // Implement custom toast or use a library
    console.log('✅ Success:', message);
    // Example: toast.success(message);
  },

  error: (message) => {
    console.error('❌ Error:', message);
    // Example: toast.error(message);
  },

  warning: (message) => {
    console.warn('⚠️ Warning:', message);
    // Example: toast.warning(message);
  },

  info: (message) => {
    console.info('ℹ️ Info:', message);
    // Example: toast.info(message);
  },
};

export const message = notifications;
```

---

## 10. Coding Standards

### A. File Naming

```
✅ ProductList.jsx          // Component (PascalCase)
✅ useProducts.js           // Hook (camelCase)
✅ productService.js        // Service (camelCase)
✅ formatters.js            // Utility (camelCase)
✅ PRODUCT_STATUSES.js      // Constants (UPPER_SNAKE_CASE)

❌ product-list.jsx
❌ UseProducts.js
❌ ProductService.js
```

### B. Component Structure

```jsx
// 1. Imports (grouped)
import { useState, useEffect } from 'react'; // React
import { useNavigate } from 'react-router-dom'; // External libraries
import { useProducts } from '../hooks/useProducts'; // Internal hooks
import { Button } from '../components/ui/Button'; // Components
import { formatCurrency } from '@/shared/utils/formatters'; // Utilities

// 2. Component
export const ProductList = () => {
  // 3. Hooks (top of component)
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const { products, isLoading } = useProducts(page);

  // 4. Event handlers
  const handleCreate = () => {
    navigate('/admin/productos/nuevo');
  };

  // 5. Effects
  useEffect(() => {
    // ...
  }, []);

  // 6. Render conditions
  if (isLoading) return <LoadingSpinner />;

  // 7. JSX
  return (
    <div>
      {/* Content */}
    </div>
  );
};
```

### C. TypeScript (if using .tsx)

```typescript
// Define interfaces
interface Product {
  id: number;
  nombre: string;
  precio: number;
  stock: number;
  categoria: Category;
}

interface Category {
  id: number;
  nombre: string;
}

// Component props
interface ProductListProps {
  initialPage?: number;
  onProductSelect?: (product: Product) => void;
}

// Component with typed props
export const ProductList: React.FC<ProductListProps> = ({
  initialPage = 0,
  onProductSelect,
}) => {
  // ...
};

// Avoid 'any'
❌ const data: any = await api.get('/products');
✅ const data: Product[] = await api.get('/products');
```

### D. Constants

```javascript
// superadmin/constants/NEGOCIO_ESTADOS.js
export const NEGOCIO_ESTADOS = {
  ACTIVO: 'ACTIVO',
  SUSPENDIDO: 'SUSPENDIDO',
  MOROSO: 'MOROSO',
  CANCELADO: 'CANCELADO',
};

export const NEGOCIO_ESTADO_LABELS = {
  [NEGOCIO_ESTADOS.ACTIVO]: 'Activo',
  [NEGOCIO_ESTADOS.SUSPENDIDO]: 'Suspendido',
  [NEGOCIO_ESTADOS.MOROSO]: 'Moroso',
  [NEGOCIO_ESTADOS.CANCELADO]: 'Cancelado',
};

export const NEGOCIO_ESTADO_COLORS = {
  [NEGOCIO_ESTADOS.ACTIVO]: 'success',
  [NEGOCIO_ESTADOS.SUSPENDIDO]: 'warning',
  [NEGOCIO_ESTADOS.MOROSO]: 'warning',
  [NEGOCIO_ESTADOS.CANCELADO]: 'error',
};
```

---

## 11. Performance & Optimization

### A. Code Splitting

```javascript
// Lazy load routes
import { lazy, Suspense } from 'react';

const Dashboard = lazy(() => import('./pages/Dashboard'));
const Productos = lazy(() => import('./pages/Productos'));

// Wrap with Suspense
<Suspense fallback={<LoadingSpinner />}>
  <Routes>
    <Route path="dashboard" element={<Dashboard />} />
    <Route path="productos" element={<Productos />} />
  </Routes>
</Suspense>
```

### B. Memoization

```javascript
import { useMemo, useCallback } from 'react';

// useMemo for expensive calculations
const filteredProducts = useMemo(() => {
  return products.filter(p => p.nombre.includes(searchTerm));
}, [products, searchTerm]);

// useCallback for callbacks passed to children
const handleDelete = useCallback((id) => {
  deleteMutation.mutate(id);
}, [deleteMutation]);
```

### C. Image Optimization

```jsx
// Use proper image formats and sizes
<img
  src="/products/thumb-123.webp"
  alt="Producto"
  loading="lazy"
  className="w-20 h-20 object-cover"
/>
```

---

## 12. Error Recovery & Troubleshooting

### A. Common Issues

| Error | Cause | Solution |
|-------|-------|----------|
| Module not found '@/...' | Path alias not configured | Add to vite.config.js + jsconfig.json |
| QueryClient not found | Missing provider | Wrap App with QueryClientProvider |
| Axios 401 | Token expired | Check interceptor, clear storage |
| Tailwind classes not working | Missing import | Add to index.css: @tailwind directives |
| Icons not rendering | Missing import | Import from 'lucide-react' |

### B. Debug Checklist

**Before asking for help:**
1. ✅ Run `pnpm install`
2. ✅ Check `.env` file exists with VITE_API_BASE_URL
3. ✅ Verify backend is running (test with Postman)
4. ✅ Clear browser cache / localStorage
5. ✅ Check browser console for errors
6. ✅ Run `pnpm run build` to check compile errors

### C. Recovery Steps

**File doesn't exist error:**
```bash
# 1. Check file exists
ls src/config/api.js

# 2. Verify import path
# ❌ import { api } from '../config/api';
# ✅ import { api } from '@/config/api';

# 3. Check alias configuration in vite.config.js
```

**Network error:**
```bash
# 1. Verify backend is running
curl http://localhost:8080/api/health

# 2. Check VITE_API_BASE_URL in .env
echo $VITE_API_BASE_URL

# 3. Restart dev server
pnpm dev
```

**Build error:**
```bash
# 1. Clear cache
rm -rf node_modules .pnpm-store
pnpm install

# 2. Check for TypeScript errors
pnpm run build

# 3. Fix imports, unused variables
```

---

## 13. Quality Checklist

**Before submitting code:**

- [ ] Code compiles: `pnpm run build`
- [ ] No linter errors: `pnpm run lint`
- [ ] Path aliases used (NO `../../../../`)
- [ ] All UI text in Spanish
- [ ] All code/variables in English
- [ ] Tables have `#` column first
- [ ] Search + pagination implemented
- [ ] Loading states on buttons/tables
- [ ] Error handling with try-catch
- [ ] No `console.log` (use `console.error` only)
- [ ] No unused imports
- [ ] Icons from Lucide React ONLY
- [ ] Forms use React Hook Form + Zod
- [ ] API calls use TanStack Query
- [ ] Modals for confirmations (NO `alert()`)

---

## 14. Config Templates

### A. Vite Config

```javascript
// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

### B. Tailwind Config

```javascript
// tailwind.config.js
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          100: '#dbeafe',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
        },
      },
    },
  },
  plugins: [],
};
```

### C. ESLint Config

```javascript
// eslint.config.js
import js from '@eslint/js';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import globals from 'globals';

export default [
  { ignores: ['dist'] },
  {
    extends: [js.configs.recommended],
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': ['warn', { allowConstantExport: true }],
      'no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],
      'no-console': ['warn', { allow: ['error', 'warn'] }],
    },
  },
];
```

### D. Main Entry Point

```javascript
// main.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './config/queryClient';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  </React.StrictMode>
);
```

```css
/* index.css */
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  * {
    @apply border-gray-200;
  }
  body {
    @apply bg-gray-50 text-gray-900;
  }
}

@layer components {
  .btn-primary {
    @apply bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors;
  }
  .btn-secondary {
    @apply bg-gray-200 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-300 transition-colors;
  }
}
```

---

## 15. Quick Reference

### A. Common Commands

```bash
# Development
pnpm dev                    # Start dev server
pnpm build                  # Build for production
pnpm preview                # Preview production build
pnpm lint                   # Run ESLint

# Package management
pnpm add axios              # Add dependency
pnpm add -D @types/node     # Add dev dependency
pnpm remove axios           # Remove dependency
pnpm install                # Install all dependencies
```

### B. Folder Quick Access

```
SuperAdmin:    src/superadmin/
Admin:         src/admin/
Storefront:    src/storefront/
Shared:        src/shared/
Config:        src/config/
Types:         src/types/
Components:    src/[scope]/components/
Pages:         src/[scope]/pages/
Services:      src/[scope]/services/
```

### C. Import Patterns

```javascript
// Services
import { negociosService } from '@/superadmin/services/negociosService';

// Components
import { Button } from '@/superadmin/components/ui/Button';

// Hooks
import { useDebounce } from '@/shared/hooks/useDebounce';

// Utils
import { formatCurrency } from '@/shared/utils/formatters';

// Types
import type { Negocio } from '@/types';

// Config
import { api } from '@/config/api';
```

---

## END OF INSTRUCTIONS

**Remember:**
- ✅ pnpm ONLY
- ✅ Path aliases (@/)
- ✅ Spanish UI, English code
- ✅ TanStack Query for API
- ✅ Lucide React for icons
- ✅ Tailwind for styling
- ✅ No native alerts
- ❌ No deep relative paths
- ❌ No mixing scopes
- ❌ No console.log in production
