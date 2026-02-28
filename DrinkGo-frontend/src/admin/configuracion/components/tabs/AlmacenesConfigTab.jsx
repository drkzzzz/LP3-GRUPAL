/**
 * AlmacenesConfigTab.jsx
 * ──────────────────────
 * CRUD de almacenes del módulo Configuración.
 */
import { useState, useMemo } from 'react';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  Warehouse,
  CheckCircle,
  Star,
} from 'lucide-react';
import { useAlmacenesConfig } from '../../hooks/useAlmacenesConfig';
import { useSedesConfig } from '../../hooks/useSedesConfig';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { AlmacenForm } from '../forms/AlmacenForm';

export const AlmacenesConfigTab = ({ context }) => {
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
  const { sedes } = useSedesConfig(negocioId);
  const {
    almacenes,
    isLoading,
    createAlmacen,
    updateAlmacen,
    deleteAlmacen,
    isCreating,
    isUpdating,
    isDeleting,
  } = useAlmacenesConfig(negocioId);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return almacenes;
    const q = debouncedSearch.toLowerCase();
    return almacenes.filter(
      (a) =>
        a.nombre?.toLowerCase().includes(q) ||
        a.codigo?.toLowerCase().includes(q) ||
        a.sede?.nombre?.toLowerCase().includes(q),
    );
  }, [almacenes, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: almacenes.length,
    activos: almacenes.filter((a) => a.estaActivo !== false).length,
    predeterminados: almacenes.filter((a) => a.esPredeterminado).length,
  }), [almacenes]);

  /* ─── Handlers ─── */
  const openCreate = () => { setEditing(null); setIsFormOpen(true); };
  const openEdit = (item) => { setEditing(item); setIsFormOpen(true); };
  const openDelete = (item) => { setSelected(item); setIsDeleteOpen(true); };

  const handleSubmit = async (formData) => {
    const payload = {
      negocio: { id: negocioId },
      sede: { id: formData.sedeId },
      codigo: formData.codigo.trim(),
      nombre: formData.nombre.trim(),
      descripcion: formData.descripcion?.trim() || null,
      esPredeterminado: formData.esPredeterminado,
    };

    if (editing) {
      await updateAlmacen({ ...payload, id: editing.id });
    } else {
      await createAlmacen(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteAlmacen(selected.id);
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
      width: '110px',
      render: (_, row) => (
        <span className="text-sm font-mono font-medium text-gray-900">{row.codigo}</span>
      ),
    },
    {
      key: 'nombre',
      title: 'Nombre',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.nombre}</p>
          {row.descripcion && <p className="text-xs text-gray-500 truncate">{row.descripcion}</p>}
        </div>
      ),
    },
    {
      key: 'sede',
      title: 'Sede',
      width: '150px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.sede?.nombre || '—'}</span>
      ),
    },
    {
      key: 'predeterminado',
      title: 'Predet.',
      width: '90px',
      align: 'center',
      render: (_, row) =>
        row.esPredeterminado ? (
          <Star size={16} className="text-yellow-500 mx-auto" fill="currentColor" />
        ) : (
          <span className="text-gray-300">—</span>
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
          <button title="Editar" onClick={() => openEdit(row)} className="p-1.5 rounded hover:bg-blue-50 text-blue-500 hover:text-blue-700 transition-colors">
            <Pencil size={15} />
          </button>
          <button title="Eliminar" onClick={() => openDelete(row)} className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors">
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
          <h1 className="text-2xl font-bold text-gray-900">Almacenes</h1>
          <p className="text-gray-600 mt-1">Gestión de almacenes y bodegas</p>
        </div>
        <button onClick={openCreate} className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
          <Plus size={18} /> Nuevo Almacén
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard title="Total almacenes" value={stats.total} icon={Warehouse} />
        <StatCard title="Activos" value={stats.activos} icon={CheckCircle} className="border-l-4 border-l-green-500" />
        <StatCard title="Predeterminados" value={stats.predeterminados} icon={Star} className="border-l-4 border-l-yellow-500" />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-72">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por código o nombre..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>
        <Table columns={columns} data={paginatedData} loading={isLoading} pagination={{ current: page, pageSize, total: filtered.length, onChange: (p, s) => { setPage(p); setPageSize(s); } }} />
      </Card>

      {/* Modal: Crear / Editar */}
      <Modal isOpen={isFormOpen} onClose={() => { setIsFormOpen(false); setEditing(null); }} title={editing ? 'Editar Almacén' : 'Nuevo Almacén'} size="md">
        <AlmacenForm initialData={editing} sedes={sedes} onSubmit={handleSubmit} onCancel={() => { setIsFormOpen(false); setEditing(null); }} isLoading={isSaving} />
      </Modal>

      {/* Modal: Confirmar eliminación */}
      <Modal isOpen={isDeleteOpen} onClose={() => { setIsDeleteOpen(false); setSelected(null); }} title="Eliminar Almacén" size="sm">
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar el almacén <strong className="text-gray-900">{selected?.nombre}</strong>? Esta acción no se puede deshacer.
          </p>
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => { setIsDeleteOpen(false); setSelected(null); }}>Cancelar</Button>
            <Button variant="danger" onClick={handleDelete} loading={isDeleting}>Eliminar</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
