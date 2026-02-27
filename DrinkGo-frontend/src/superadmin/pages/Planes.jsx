import { useState, useMemo } from 'react';
import {
  Plus,
  Eye,
  Edit,
  Power,
  Search,
  LayoutGrid,
  List,
  Check,
  CheckCircle,
  XCircle,
} from 'lucide-react';
import { usePlanes } from '../hooks/usePlanes';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Table } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { PlanForm } from '../components/forms/PlanForm';
import { PlanDetail } from '../components/modals/PlanDetail';
import { formatCurrency } from '@/shared/utils/formatters';

/* ---------- Tarjeta de plan para vista de comparación (RF-PLT-005) ---------- */
const PlanCard = ({ plan, onEdit, onView, onToggle, isToggling }) => {
  const activo = plan.estaActivo !== false;
  const PERIODO = { mensual: '/mes', anual: '/año', trimestral: '/trim.' };
  const features = [
    plan.maxSedes ? `Hasta ${plan.maxSedes} sede(s)` : null,
    plan.maxUsuariosPorSede ? `${plan.maxUsuariosPorSede} usuarios/sede` : null,
    plan.maxProductos ? `${plan.maxProductos} productos` : null,
    plan.incluyeStorefront ? 'Storefront online' : null,
    plan.incluyeReportes ? 'Reportes avanzados' : null,
  ].filter(Boolean);

  return (
    <div
      className={`relative flex flex-col border-2 rounded-xl p-5 transition-all ${
        activo ? 'border-blue-200 bg-white' : 'border-gray-200 bg-gray-50 opacity-70'
      }`}
    >
      {/* Estado badge */}
      <div className="absolute top-3 right-3">
        <Badge variant={activo ? 'success' : 'error'}>{activo ? 'Activo' : 'Inactivo'}</Badge>
      </div>

      {/* Nombre */}
      <h3 className="text-lg font-bold text-gray-900 pr-20">{plan.nombre}</h3>
      {plan.descripcion && (
        <p className="text-xs text-gray-500 mt-1 line-clamp-2">{plan.descripcion}</p>
      )}

      {/* Precio */}
      <div className="mt-4 mb-4">
        <span className="text-3xl font-extrabold text-blue-600">
          {formatCurrency(plan.precio)}
        </span>
        <span className="text-sm text-gray-500 ml-1">
          {PERIODO[plan.periodoFacturacion] || ''}
        </span>
      </div>

      {/* Features */}
      <ul className="space-y-1.5 flex-1 mb-4">
        {features.map((f) => (
          <li key={f} className="flex items-center gap-2 text-sm text-gray-600">
            <Check size={14} className="text-green-500 shrink-0" />
            {f}
          </li>
        ))}
        {features.length === 0 && (
          <li className="text-xs text-gray-400">Sin límites configurados</li>
        )}
      </ul>

      {/* Actions */}
      <div className="flex gap-2 pt-2 border-t border-gray-100">
        <button
          title="Ver detalles"
          onClick={() => onView(plan)}
          className="flex-1 flex justify-center items-center py-1.5 rounded-lg border border-gray-200 hover:bg-gray-50 text-gray-500 hover:text-gray-700 text-xs gap-1 transition-colors"
        >
          <Eye size={13} /> Ver
        </button>
        <button
          title="Editar"
          onClick={() => onEdit(plan)}
          className="flex-1 flex justify-center items-center py-1.5 rounded-lg border border-gray-200 hover:bg-gray-50 text-gray-500 hover:text-gray-700 text-xs gap-1 transition-colors"
        >
          <Edit size={13} /> Editar
        </button>
        <button
          title={activo ? 'Desactivar plan' : 'Activar plan'}
          onClick={() => onToggle(plan)}
          disabled={isToggling}
          className={`flex-1 flex justify-center items-center py-1.5 rounded-lg border text-xs gap-1 transition-colors disabled:opacity-50 ${
            activo
              ? 'border-red-200 hover:bg-red-50 text-red-500 hover:text-red-700'
              : 'border-green-200 hover:bg-green-50 text-green-600 hover:text-green-700'
          }`}
        >
          <Power size={13} />
          {activo ? 'Bajar' : 'Subir'}
        </button>
      </div>
    </div>
  );
};

export const Planes = () => {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const [viewMode, setViewMode] = useState('table'); // 'table' | 'cards'
  const [showInactive, setShowInactive] = useState(false);
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isToggleOpen, setIsToggleOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  const {
    planes,
    isLoading,
    createPlan,
    updatePlan,
    deactivatePlan,
    activatePlan,
    isCreating,
    isUpdating,
    isDeactivating,
    isActivating,
  } = usePlanes();

  const isToggling = isDeactivating || isActivating;

  /* ---------- client-side search + filter ---------- */
  const filtered = useMemo(() => {
    let result = planes;
    if (!showInactive) {
      result = result.filter((p) => p.estaActivo !== false);
    }
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (p) =>
          p.nombre?.toLowerCase().includes(q) ||
          p.descripcion?.toLowerCase().includes(q),
      );
    }
    return result;
  }, [planes, debouncedSearch, showInactive]);

  /* ---------- client-side pagination ---------- */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ---------- handlers ---------- */
  const handleCreate = async (data) => {
    await createPlan(data);
    setIsCreateOpen(false);
  };

  const handleEdit = (plan) => {
    setSelected(plan);
    setIsEditOpen(true);
  };

  const handleUpdate = async (data) => {
    await updatePlan({ ...selected, ...data });
    setIsEditOpen(false);
    setSelected(null);
  };

  const handleView = (plan) => {
    setSelected(plan);
    setIsDetailOpen(true);
  };

  const handleToggleClick = (plan) => {
    setSelected(plan);
    setIsToggleOpen(true);
  };

  const handleToggleConfirm = async () => {
    if (!selected) return;
    const isCurrentlyActive = selected.estaActivo !== false;
    if (isCurrentlyActive) {
      await deactivatePlan(selected);
    } else {
      await activatePlan(selected);
    }
    setIsToggleOpen(false);
    setSelected(null);
  };

  /* ---------- columns ---------- */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '60px',
      render: (_, __, i) => (page - 1) * pageSize + i + 1,
    },
    { key: 'nombre', title: 'Nombre', dataIndex: 'nombre' },
    {
      key: 'precio',
      title: 'Precio',
      width: '130px',
      render: (_, row) => formatCurrency(row.precio),
    },
    {
      key: 'periodo',
      title: 'Periodo',
      dataIndex: 'periodoFacturacion',
      width: '120px',
      render: (val) => <Badge variant="info">{val?.toUpperCase()}</Badge>,
    },
    {
      key: 'maxSedes',
      title: 'Máx. Sedes',
      dataIndex: 'maxSedes',
      width: '100px',
      align: 'center',
    },
    {
      key: 'maxUsuarios',
      title: 'Máx. Usuarios',
      dataIndex: 'maxUsuariosPorSede',
      width: '110px',
      align: 'center',
    },
    {
      key: 'maxProductos',
      title: 'Máx. Productos',
      dataIndex: 'maxProductos',
      width: '120px',
      align: 'center',
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '100px',
      render: (_, row) => {
        const activo = row.estaActivo !== false;
        return (
          <Badge variant={activo ? 'success' : 'error'}>
            {activo ? 'Activo' : 'Inactivo'}
          </Badge>
        );
      },
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, row) => {
        const activo = row.estaActivo !== false;
        return (
          <div className="flex justify-center gap-1">
            <button
              title="Ver detalles"
              onClick={() => handleView(row)}
              className="p-1.5 rounded hover:bg-gray-100 text-blue-500 hover:text-blue-700"
            >
              <Eye size={16} />
            </button>
            <button
              title="Editar"
              onClick={() => handleEdit(row)}
              className="p-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-gray-700"
            >
              <Edit size={16} />
            </button>
            <button
              title={activo ? 'Desactivar plan (RF-PLT-004)' : 'Activar plan'}
              onClick={() => handleToggleClick(row)}
              className={`p-1.5 rounded hover:bg-gray-100 ${
                activo
                  ? 'text-red-400 hover:text-red-600'
                  : 'text-green-500 hover:text-green-700'
              }`}
            >
              <Power size={16} />
            </button>
          </div>
        );
      },
    },
  ];

  const isActive = selected?.estaActivo !== false;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Planes de Suscripción</h1>
          <p className="text-gray-600 mt-1">
            Gestión de planes ofrecidos a las licorerías
          </p>
        </div>
        <div className="flex items-center gap-2 shrink-0">
          {/* View mode toggle */}
          <div className="flex border border-gray-300 rounded-lg overflow-hidden">
            <button
              onClick={() => setViewMode('table')}
              className={`p-2 ${viewMode === 'table' ? 'bg-blue-50 text-blue-600' : 'text-gray-500 hover:bg-gray-50'}`}
              title="Vista tabla"
            >
              <List size={16} />
            </button>
            <button
              onClick={() => setViewMode('cards')}
              className={`p-2 ${viewMode === 'cards' ? 'bg-blue-50 text-blue-600' : 'text-gray-500 hover:bg-gray-50'}`}
              title="Vista comparación"
            >
              <LayoutGrid size={16} />
            </button>
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nuevo Plan
          </Button>
        </div>
      </div>

      {/* Stats resumen */}
      <div className="grid grid-cols-3 gap-4">
        <div className="bg-white border border-gray-200 rounded-lg p-4 text-center">
          <p className="text-sm text-gray-500">Total Planes</p>
          <p className="text-2xl font-bold text-gray-900">{planes.length}</p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4 text-center">
          <p className="text-sm text-gray-500">Planes Activos</p>
          <p className="text-2xl font-bold text-green-600">
            {planes.filter((p) => p.estaActivo !== false).length}
          </p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4 text-center">
          <p className="text-sm text-gray-500">Planes Inactivos</p>
          <p className="text-2xl font-bold text-red-500">
            {planes.filter((p) => p.estaActivo === false).length}
          </p>
        </div>
      </div>

      {/* Card */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="flex gap-3 items-center w-full sm:w-auto">
            <div className="relative flex-1 sm:w-80">
              <Search
                size={16}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <input
                type="text"
                placeholder="Buscar por nombre o descripción..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setPage(1);
                }}
                className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <label className="flex items-center gap-2 text-sm text-gray-600 cursor-pointer whitespace-nowrap">
              <input
                type="checkbox"
                checked={showInactive}
                onChange={(e) => setShowInactive(e.target.checked)}
                className="rounded accent-blue-600"
              />
              Mostrar inactivos
            </label>
          </div>
        </div>

        {/* Vista tabla */}
        {viewMode === 'table' && (
          <Table
            columns={columns}
            data={paginatedData}
            loading={isLoading}
            pagination={{
              current: page,
              pageSize,
              total: filtered.length,
              onChange: (newPage, newSize) => {
                setPage(newPage);
                setPageSize(newSize);
              },
            }}
          />
        )}

        {/* Vista tarjetas comparación (RF-PLT-005) */}
        {viewMode === 'cards' && (
          <div>
            {isLoading ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {Array.from({ length: 3 }).map((_, i) => (
                  <div key={i} className="border-2 border-gray-100 rounded-xl p-5 animate-pulse">
                    <div className="h-5 bg-gray-200 rounded w-2/3 mb-3" />
                    <div className="h-8 bg-gray-200 rounded w-1/2 mb-4" />
                    <div className="space-y-2">
                      {[1, 2, 3].map((j) => (
                        <div key={j} className="h-3 bg-gray-100 rounded w-3/4" />
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            ) : filtered.length === 0 ? (
              <div className="text-center py-10 text-gray-400">
                No hay planes que coincidan con la búsqueda
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {filtered.map((plan) => (
                  <PlanCard
                    key={plan.id}
                    plan={plan}
                    onEdit={handleEdit}
                    onView={handleView}
                    onToggle={handleToggleClick}
                    isToggling={isToggling}
                  />
                ))}
              </div>
            )}
          </div>
        )}
      </Card>

      {/* Create Modal */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Crear Nuevo Plan"
        size="xl"
      >
        <PlanForm
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* Edit Modal */}
      <Modal
        isOpen={isEditOpen}
        onClose={() => { setIsEditOpen(false); setSelected(null); }}
        title="Editar Plan"
        size="xl"
      >
        {selected && (
          <PlanForm
            initialData={selected}
            onSubmit={handleUpdate}
            onCancel={() => { setIsEditOpen(false); setSelected(null); }}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* Detail Modal */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title="Detalles del Plan"
        size="lg"
      >
        {selected && <PlanDetail plan={selected} />}
      </Modal>

      {/* Toggle Confirm (RF-PLT-004) */}
      <ConfirmDialog
        isOpen={isToggleOpen}
        onClose={() => { setIsToggleOpen(false); setSelected(null); }}
        onConfirm={handleToggleConfirm}
        title={isActive ? 'Desactivar Plan' : 'Activar Plan'}
        message={
          isActive
            ? `¿Desactivar el plan "${selected?.nombre}"? Los negocios actualmente suscritos conservarán sus condiciones, pero no se permitirán nuevas suscripciones a este plan.`
            : `¿Activar el plan "${selected?.nombre}"? Volverá a estar disponible para nuevas suscripciones.`
        }
        confirmText={isActive ? 'Desactivar' : 'Activar'}
        isLoading={isToggling}
      />
    </div>
  );
};
