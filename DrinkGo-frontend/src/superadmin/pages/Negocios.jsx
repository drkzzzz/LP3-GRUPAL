import { useState, useMemo, useCallback } from 'react';
import {
  Plus,
  Eye,
  Edit,
  Search,
  Building2,
  CheckCircle,
  Clock,
  AlertTriangle,
  MapPin,
  Filter,
  Power,
  ChevronDown,
} from 'lucide-react';
import { useNegocios } from '../hooks/useNegocios';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Table } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { StatCard } from '../components/ui/StatCard';
import { Modal } from '../components/ui/Modal';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { EstadoDropdown } from '../components/negocios/EstadoDropdown';
import { NegocioForm } from '../components/forms/NegocioForm';
import { NegocioDetailModal } from '../components/negocios/NegocioDetailModal';
import { WizardNuevoNegocio } from '../components/negocios/WizardNuevoNegocio';
import { ActivarConPlanModal } from '../components/negocios/ActivarConPlanModal';
import { formatPhone } from '@/shared/utils/formatters';

const ESTADO_COLORS = {
  activo: 'success',
  pendiente: 'warning',
  suspendido: 'warning',
  cancelado: 'error',
};

const ESTADO_OPTIONS = [
  { value: '', label: 'Todos los estados' },
  { value: 'activo', label: 'Activo' },
  { value: 'pendiente', label: 'Pendiente' },
  { value: 'suspendido', label: 'Suspendido' },
  { value: 'cancelado', label: 'Cancelado' },
];

export const Negocios = () => {
  /* ---------- state ---------- */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const [estadoFilter, setEstadoFilter] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [isDeactivateOpen, setIsDeactivateOpen] = useState(false);
  const [isActivarPlanOpen, setIsActivarPlanOpen] = useState(false);
  const [pendingActivation, setPendingActivation] = useState(null);
  const [selected, setSelected] = useState(null);

  const {
    negocios,
    isLoading,
    stats,
    sedes,
    suscripciones,
    planes,
    getSedesForNegocio,
    getSuscripcionForNegocio,
    createNegocio,
    updateNegocio,
    deleteNegocio,
    isCreating,
    isUpdating,
    isDeleting,
    createSede,
    updateSede,
    deleteSede,
    isCreatingSede,
    isUpdatingSede,
    isDeletingSede,
    createSuscripcion,
    updateSuscripcion,
    isCreatingSuscripcion,
    isUpdatingSuscripcion,
    createUsuario,
    updateUsuario,
    getUsuariosForNegocio,
    isCreatingUsuario,
    isUpdatingUsuario,
  } = useNegocios();

  /* ---------- helpers: enrich negocios with plan & sede count ---------- */
  const enrichedNegocios = useMemo(() => {
    return negocios.map((n) => {
      const sus = getSuscripcionForNegocio(n.id);
      // Handle both nested (s.negocio?.id) and flat (s.negocioId) formats from backend
      const sedesCount = sedes.filter(
        (s) => (s.negocio?.id ?? s.negocioId) === n.id,
      ).length;
      // Handle both nested (sus.plan?.nombre) and flat (sus.planId) plan references
      const planNombre =
        sus?.plan?.nombre ||
        (sus?.planId ? planes.find((p) => p.id === sus.planId)?.nombre : null) ||
        null;
      return {
        ...n,
        _planNombre: planNombre,
        _sedesCount: sedesCount,
        _suscripcion: sus,
      };
    });
  }, [negocios, sedes, suscripciones, planes, getSedesForNegocio, getSuscripcionForNegocio]);

  /* ---------- client-side search + filter ---------- */
  const filtered = useMemo(() => {
    let result = enrichedNegocios;
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (n) =>
          n.razonSocial?.toLowerCase().includes(q) ||
          n.ruc?.includes(q) ||
          n.email?.toLowerCase().includes(q) ||
          n.nombreComercial?.toLowerCase().includes(q),
      );
    }
    if (estadoFilter) {
      result = result.filter((n) => n.estado === estadoFilter);
    }
    return result;
  }, [enrichedNegocios, debouncedSearch, estadoFilter]);

  /* ---------- client-side pagination ---------- */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ---------- handlers ---------- */
  const handleCreate = async ({ negocioData, selectedPlanId }) => {
    const newNegocio = await createNegocio(negocioData);
    if (selectedPlanId && newNegocio?.id) {
      await createSuscripcion({
        negocio: { id: newNegocio.id },
        plan: { id: selectedPlanId },
        estado: 'activa',
      });
    }
    if (negocioData.adminEmail && negocioData.adminPassword && newNegocio?.id) {
      await createUsuario({
        negocio: { id: newNegocio.id },
        email: negocioData.adminEmail,
        hashContrasena: negocioData.adminPassword,
        nombres: negocioData.adminNombres || 'Administrador',
        apellidos: negocioData.adminApellidos || 'Principal',
        estaActivo: 1,
      });
    }
    setIsCreateOpen(false);
  };

  const handleEdit = (negocio) => {
    setSelected(negocio);
    setIsEditOpen(true);
  };

  const handleUpdate = async (data) => {
    await updateNegocio({ ...selected, ...data });
    setIsEditOpen(false);
    setSelected(null);
  };

  const handleView = (negocio) => {
    setSelected(negocio);
    setIsDrawerOpen(true);
  };

  const handleDeactivateClick = (negocio) => {
    setSelected(negocio);
    setIsDeactivateOpen(true);
  };

  const handleDeactivateConfirm = async () => {
    await updateNegocio({ ...selected, estado: 'cancelado' });
    setIsDeactivateOpen(false);
    setSelected(null);
  };

  const handleEstadoChange = async (negocio, newEstado) => {
    if (newEstado === 'activo') {
      const sus = getSuscripcionForNegocio(negocio.id);
      const hasActivePlan = sus && sus.estado === 'activa';
      if (!hasActivePlan) {
        setPendingActivation(negocio);
        setIsActivarPlanOpen(true);
        return;
      }
    }
    await updateNegocio({ ...negocio, estado: newEstado });
  };

  const handleActivarConPlan = async (planId) => {
    if (!pendingActivation) return;
    const today = new Date().toISOString().split('T')[0];
    const nextMonth = new Date();
    nextMonth.setMonth(nextMonth.getMonth() + 1);

    await createSuscripcion({
      negocio: { id: pendingActivation.id },
      plan: { id: planId },
      estado: 'activa',
      inicioPeriodoActual: today,
      finPeriodoActual: nextMonth.toISOString().split('T')[0],
      autoRenovar: true,
    });
    await updateNegocio({ ...pendingActivation, estado: 'activo' });
    setIsActivarPlanOpen(false);
    setPendingActivation(null);
  };

  const handleChangePlan = useCallback(
    async (data) => {
      if (data.id) {
        await updateSuscripcion(data);
      } else {
        await createSuscripcion(data);
      }
    },
    [updateSuscripcion, createSuscripcion],
  );

  /* ---------- columns ---------- */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => (
        <span className="text-gray-400 text-xs">
          {(page - 1) * pageSize + i + 1}
        </span>
      ),
    },
    {
      key: 'negocio',
      title: 'Negocio',
      render: (_, row) => (
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-lg bg-blue-100 flex items-center justify-center text-blue-700 font-bold text-sm flex-shrink-0">
            {row.razonSocial?.charAt(0)?.toUpperCase() || 'N'}
          </div>
          <div className="min-w-0">
            <p className="text-sm font-medium text-gray-900 truncate">
              {row.razonSocial}
            </p>
            <p className="text-xs text-gray-500 truncate">
              RUC: {row.ruc || '—'}
            </p>
          </div>
        </div>
      ),
    },
    {
      key: 'email',
      title: 'Contacto',
      width: '200px',
      render: (_, row) => (
        <div>
          <p className="text-sm text-gray-700 truncate">{row.email || '—'}</p>
          <p className="text-xs text-gray-400">
            {row.telefono ? formatPhone(row.telefono) : '—'}
          </p>
        </div>
      ),
    },
    {
      key: 'plan',
      title: 'Plan',
      width: '130px',
      render: (_, row) =>
        row._planNombre ? (
          <Badge variant="info">{row._planNombre}</Badge>
        ) : (
          <span className="text-xs text-gray-400 italic">Sin plan</span>
        ),
    },
    {
      key: 'sedes',
      title: 'Sedes',
      width: '80px',
      align: 'center',
      render: (_, row) => (
        <div className="flex items-center justify-center gap-1">
          <MapPin size={13} className="text-gray-400" />
          <span className="text-sm font-medium text-gray-700">
            {row._sedesCount}
          </span>
        </div>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '150px',
      render: (_, row) => (
        <EstadoDropdown
          current={row.estado}
          onChange={(newEstado) => handleEstadoChange(row, newEstado)}
          isUpdating={isUpdating}
        />
      ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <div className="flex justify-center gap-1">
          <button
            title="Ver detalles"
            onClick={() => handleView(row)}
            className="p-1.5 rounded hover:bg-blue-50 text-blue-500 hover:text-blue-700 transition-colors"
          >
            <Eye size={16} />
          </button>
          <button
            title="Editar"
            onClick={() => handleEdit(row)}
            className="p-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition-colors"
          >
            <Edit size={16} />
          </button>
          <button
            title="Desactivar"
            onClick={() => handleDeactivateClick(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
            disabled={row.estado === 'cancelado'}
          >
            <Power size={16} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Negocios</h1>
          <p className="text-gray-500 mt-1 text-sm">
            Gestión de licorerías y negocios registrados en la plataforma
          </p>
        </div>
        <Button onClick={() => setIsCreateOpen(true)}>
          <Plus size={18} />
          Nuevo Negocio
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Negocios"
          value={stats.total}
          icon={Building2}
        />
        <StatCard
          title="Activos"
          value={stats.activos}
          icon={CheckCircle}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Pendientes"
          value={stats.pendientes}
          icon={Clock}
          className="border-l-4 border-l-yellow-500"
        />
        <StatCard
          title="Suspendidos"
          value={stats.suspendidos}
          icon={AlertTriangle}
          className="border-l-4 border-l-red-500"
        />
      </div>

      {/* Table Card */}
      <Card>
        {/* Search + Filters */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
            <div className="relative w-full sm:w-80">
              <Search
                size={16}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <input
                type="text"
                placeholder="Buscar por RUC, razón social o email..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setPage(1);
                }}
                className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="relative w-full sm:w-48">
              <Filter
                size={14}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <select
                value={estadoFilter}
                onChange={(e) => {
                  setEstadoFilter(e.target.value);
                  setPage(1);
                }}
                className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 appearance-none bg-white"
              >
                {ESTADO_OPTIONS.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <p className="text-xs text-gray-400 whitespace-nowrap">
            {filtered.length} resultado{filtered.length !== 1 ? 's' : ''}
          </p>
        </div>

        {/* Table */}
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
      </Card>

      {/* ─── Modals ─── */}

      {/* Create Wizard */}
      <WizardNuevoNegocio
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        planes={planes}
        onSubmit={handleCreate}
        isLoading={isCreating || isCreatingSuscripcion || isCreatingUsuario}
      />

      {/* Edit Modal */}
      <Modal
        isOpen={isEditOpen}
        onClose={() => {
          setIsEditOpen(false);
          setSelected(null);
        }}
        title="Editar Negocio"
        size="lg"
      >
        {selected && (
          <NegocioForm
            initialData={selected}
            onSubmit={handleUpdate}
            onCancel={() => {
              setIsEditOpen(false);
              setSelected(null);
            }}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* Detail Modal — full tabs: Info / Sedes / Plan / Credenciales */}
      <NegocioDetailModal
        isOpen={isDrawerOpen}
        onClose={() => {
          setIsDrawerOpen(false);
          setSelected(null);
        }}
        negocio={selected}
        sedes={selected ? getSedesForNegocio(selected.id) : []}
        suscripcion={selected ? getSuscripcionForNegocio(selected.id) : null}
        planes={planes}
        onCreateSede={createSede}
        onUpdateSede={updateSede}
        onDeleteSede={deleteSede}
        onChangePlan={handleChangePlan}
        isCreatingSede={isCreatingSede}
        isUpdatingSede={isUpdatingSede}
        isDeletingSede={isDeletingSede}
        isUpdatingSuscripcion={isUpdatingSuscripcion}
        onUpdateNegocio={updateNegocio}
        isUpdatingNegocio={isUpdating}
        usuarios={selected ? getUsuariosForNegocio(selected.id) : []}
        onCreateUsuario={createUsuario}
        onUpdateUsuario={updateUsuario}
        isCreatingUsuario={isCreatingUsuario}
        isUpdatingUsuario={isUpdatingUsuario}
      />

      {/* Activar con Plan */}
      <ActivarConPlanModal
        isOpen={isActivarPlanOpen}
        onClose={() => {
          setIsActivarPlanOpen(false);
          setPendingActivation(null);
        }}
        negocio={pendingActivation}
        planes={planes}
        onConfirm={handleActivarConPlan}
        isLoading={isCreatingSuscripcion || isUpdating}
      />

      {/* Deactivate Confirm */}
      <ConfirmDialog
        isOpen={isDeactivateOpen}
        onClose={() => {
          setIsDeactivateOpen(false);
          setSelected(null);
        }}
        onConfirm={handleDeactivateConfirm}
        title="Desactivar Negocio"
        message={`¿Está seguro de desactivar "${selected?.razonSocial}"? El negocio pasará a estado cancelado y no podrá operar.`}
        confirmText="Desactivar"
        isLoading={isUpdating}
      />
    </div>
  );
};
