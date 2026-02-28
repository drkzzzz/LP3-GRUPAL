/**
 * SedesTab.jsx
 * ────────────
 * CRUD de sedes del negocio: listado, creación, edición y eliminación.
 */
import { useState, useMemo } from 'react';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  MapPin,
  CheckCircle,
  Truck,
  ShoppingBag,
  Star,
} from 'lucide-react';
import { useSedesConfig } from '../../hooks/useSedesConfig';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { SedeForm } from '../forms/SedeForm';

export const SedesTab = ({ context }) => {
  const { negocioId } = context;

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selected, setSelected] = useState(null);

  /* ─── Data ─── */
  const {
    sedes,
    isLoading,
    createSede,
    updateSede,
    deleteSede,
    isCreating,
    isUpdating,
    isDeleting,
  } = useSedesConfig(negocioId);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return sedes;
    const q = debouncedSearch.toLowerCase();
    return sedes.filter(
      (s) =>
        s.nombre?.toLowerCase().includes(q) ||
        s.codigo?.toLowerCase().includes(q) ||
        s.ciudad?.toLowerCase().includes(q),
    );
  }, [sedes, debouncedSearch]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: sedes.length,
    activas: sedes.filter((s) => s.estaActivo !== false).length,
    delivery: sedes.filter((s) => s.deliveryHabilitado).length,
    recojo: sedes.filter((s) => s.recojoHabilitado).length,
  }), [sedes]);

  /* ─── Handlers ─── */
  const openCreate = () => {
    setEditing(null);
    setIsFormOpen(true);
  };

  const openEdit = (item) => {
    setEditing(item);
    setIsFormOpen(true);
  };

  const openDelete = (item) => {
    setSelected(item);
    setIsDeleteOpen(true);
  };

  const handleSubmit = async (formData) => {
    const payload = {
      negocio: { id: negocioId },
      codigo: formData.codigo.trim(),
      nombre: formData.nombre.trim(),
      direccion: formData.direccion.trim(),
      ciudad: formData.ciudad?.trim() || null,
      departamento: formData.departamento?.trim() || null,
      pais: formData.pais?.trim() || 'PE',
      codigoPostal: formData.codigoPostal?.trim() || null,
      telefono: formData.telefono?.trim() || null,
      esPrincipal: formData.esPrincipal,
      deliveryHabilitado: formData.deliveryHabilitado,
      recojoHabilitado: formData.recojoHabilitado,
    };

    if (editing) {
      await updateSede({ ...payload, id: editing.id });
    } else {
      await createSede(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteSede(selected.id);
    setIsDeleteOpen(false);
    setSelected(null);
  };

  /* ─── Columnas ─── */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => (
        <span className="text-gray-400 text-xs">{(page - 1) * pageSize + i + 1}</span>
      ),
    },
    {
      key: 'codigo',
      title: 'Código',
      width: '100px',
      render: (_, row) => (
        <span className="text-sm font-mono font-medium text-gray-900">{row.codigo}</span>
      ),
    },
    {
      key: 'nombre',
      title: 'Nombre',
      render: (_, row) => (
        <div className="min-w-0">
          <div className="flex items-center gap-1.5">
            <p className="text-sm font-medium text-gray-900 truncate">{row.nombre}</p>
            {row.esPrincipal && (
              <Star size={14} className="text-yellow-500 flex-shrink-0" fill="currentColor" />
            )}
          </div>
          <p className="text-xs text-gray-500 truncate">{row.direccion || '—'}</p>
        </div>
      ),
    },
    {
      key: 'ciudad',
      title: 'Ciudad',
      width: '120px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.ciudad || '—'}</span>
      ),
    },
    {
      key: 'servicios',
      title: 'Servicios',
      width: '140px',
      render: (_, row) => (
        <div className="flex items-center gap-1.5">
          {row.deliveryHabilitado && (
            <span className="inline-flex items-center gap-1 bg-blue-50 text-blue-600 text-xs px-2 py-0.5 rounded-full">
              <Truck size={12} /> Delivery
            </span>
          )}
          {row.recojoHabilitado && (
            <span className="inline-flex items-center gap-1 bg-purple-50 text-purple-600 text-xs px-2 py-0.5 rounded-full">
              <ShoppingBag size={12} /> Recojo
            </span>
          )}
          {!row.deliveryHabilitado && !row.recojoHabilitado && (
            <span className="text-gray-400 text-xs">—</span>
          )}
        </div>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '100px',
      align: 'center',
      render: (_, row) =>
        row.estaActivo !== false ? (
          <Badge variant="success">Activo</Badge>
        ) : (
          <Badge variant="error">Inactivo</Badge>
        ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <div className="flex items-center justify-center gap-1">
          <button
            title="Editar"
            onClick={() => openEdit(row)}
            className="p-1.5 rounded hover:bg-blue-50 text-blue-500 hover:text-blue-700 transition-colors"
          >
            <Pencil size={15} />
          </button>
          <button
            title="Eliminar"
            onClick={() => openDelete(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
          >
            <Trash2 size={15} />
          </button>
        </div>
      ),
    },
  ];

  const isSaving = isCreating || isUpdating;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Sedes</h1>
          <p className="text-gray-600 mt-1">
            Gestiona las sedes y sucursales de tu negocio
          </p>
        </div>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
        >
          <Plus size={18} />
          Nueva Sede
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total sedes" value={stats.total} icon={MapPin} />
        <StatCard title="Activas" value={stats.activas} icon={CheckCircle} className="border-l-4 border-l-green-500" />
        <StatCard title="Con delivery" value={stats.delivery} icon={Truck} className="border-l-4 border-l-blue-500" />
        <StatCard title="Con recojo" value={stats.recojo} icon={ShoppingBag} className="border-l-4 border-l-purple-500" />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-72">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por código, nombre o ciudad..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>

        <Table
          columns={columns}
          data={paginatedData}
          loading={isLoading}
          pagination={{
            current: page,
            pageSize,
            total: filtered.length,
            onChange: (newPage, newSize) => { setPage(newPage); setPageSize(newSize); },
          }}
        />
      </Card>

      {/* Modal: Crear / Editar */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => { setIsFormOpen(false); setEditing(null); }}
        title={editing ? 'Editar Sede' : 'Nueva Sede'}
        size="lg"
      >
        <SedeForm
          initialData={editing}
          onSubmit={handleSubmit}
          onCancel={() => { setIsFormOpen(false); setEditing(null); }}
          isLoading={isSaving}
        />
      </Modal>

      {/* Modal: Confirmar eliminación */}
      <Modal
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setSelected(null); }}
        title="Eliminar Sede"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar la sede{' '}
            <strong className="text-gray-900">{selected?.nombre}</strong>?
            Esta acción no se puede deshacer.
          </p>
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => { setIsDeleteOpen(false); setSelected(null); }}>
              Cancelar
            </Button>
            <Button variant="danger" onClick={handleDelete} loading={isDeleting}>
              Eliminar
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
