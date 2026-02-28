/**
 * MesasTab.jsx
 * ────────────
 * CRUD de mesas agrupadas por sede.
 */
import { useState, useMemo } from 'react';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  LayoutGrid,
  CheckCircle,
  Users,
  AlertTriangle,
} from 'lucide-react';
import { useMesasConfig } from '../../hooks/useMesasConfig';
import { useSedesConfig } from '../../hooks/useSedesConfig';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { MesaForm } from '../forms/MesaForm';

const ESTADO_VARIANT = {
  disponible: 'success',
  ocupada: 'warning',
  reservada: 'info',
  mantenimiento: 'error',
};

const ESTADO_LABEL = {
  disponible: 'Disponible',
  ocupada: 'Ocupada',
  reservada: 'Reservada',
  mantenimiento: 'Mantenimiento',
};

export const MesasTab = ({ context }) => {
  const { negocioId } = context;

  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selected, setSelected] = useState(null);

  const { sedes } = useSedesConfig(negocioId);

  const {
    mesas,
    isLoading,
    createMesa,
    updateMesa,
    deleteMesa,
    isCreating,
    isUpdating,
    isDeleting,
  } = useMesasConfig(negocioId, sedes);

  /* Sedes activas para el Select */
  const sedesOptions = useMemo(
    () =>
      sedes
        .filter((s) => s.estaActivo !== false)
        .map((s) => ({ value: String(s.id), label: `${s.nombre} – ${s.ciudad || ''}` })),
    [sedes],
  );

  /* Mapa id → sede */
  const sedesMap = useMemo(() => {
    const m = {};
    sedes.forEach((s) => { m[s.id] = s; });
    return m;
  }, [sedes]);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return mesas;
    const q = debouncedSearch.toLowerCase();
    return mesas.filter(
      (m) =>
        m.nombre?.toLowerCase().includes(q) ||
        sedesMap[m.sede?.id]?.nombre?.toLowerCase().includes(q) ||
        m.estado?.toLowerCase().includes(q),
    );
  }, [mesas, debouncedSearch, sedesMap]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: mesas.length,
    disponibles: mesas.filter((m) => m.estado === 'disponible').length,
    ocupadas: mesas.filter((m) => m.estado === 'ocupada').length,
    reservadas: mesas.filter((m) => m.estado === 'reservada').length,
  }), [mesas]);

  /* ─── Handlers ─── */
  const openCreate = () => { setEditing(null); setIsFormOpen(true); };
  const openEdit = (item) => {
    setEditing({
      ...item,
      sedeId: item.sede?.id ? String(item.sede.id) : '',
    });
    setIsFormOpen(true);
  };
  const openDelete = (item) => { setSelected(item); setIsDeleteOpen(true); };

  const handleSubmit = async (formData) => {
    const payload = {
      sede: { id: Number(formData.sedeId) },
      nombre: formData.nombre.trim(),
      capacidad: formData.capacidad,
      estado: formData.estado,
    };

    if (editing) {
      await updateMesa({ ...payload, id: editing.id });
    } else {
      await createMesa(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteMesa(selected.id);
    setIsDeleteOpen(false);
    setSelected(null);
  };

  /* ─── Columnas ─── */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => <span className="text-gray-400 text-xs">{(page - 1) * pageSize + i + 1}</span>,
    },
    {
      key: 'nombre',
      title: 'Mesa',
      render: (_, row) => <span className="text-sm font-medium text-gray-900">{row.nombre}</span>,
    },
    {
      key: 'sede',
      title: 'Sede',
      width: '200px',
      render: (_, row) => {
        const sede = sedesMap[row.sede?.id];
        return sede ? (
          <span className="text-sm text-gray-700">{sede.nombre}</span>
        ) : (
          <span className="text-sm text-gray-400 italic">Sin sede</span>
        );
      },
    },
    {
      key: 'capacidad',
      title: 'Capacidad',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="inline-flex items-center gap-1 text-sm text-gray-700">
          <Users size={14} className="text-gray-400" /> {row.capacidad}
        </span>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '130px',
      align: 'center',
      render: (_, row) => (
        <Badge variant={ESTADO_VARIANT[row.estado] || 'info'}>
          {ESTADO_LABEL[row.estado] || row.estado}
        </Badge>
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
          <h1 className="text-2xl font-bold text-gray-900">Mesas</h1>
          <p className="text-gray-600 mt-1">Gestiona las mesas de tus sedes para atención en local</p>
        </div>
        <button onClick={openCreate} className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
          <Plus size={18} /> Nueva Mesa
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total mesas" value={stats.total} icon={LayoutGrid} />
        <StatCard title="Disponibles" value={stats.disponibles} icon={CheckCircle} className="border-l-4 border-l-green-500" />
        <StatCard title="Ocupadas" value={stats.ocupadas} icon={Users} className="border-l-4 border-l-yellow-500" />
        <StatCard title="Reservadas" value={stats.reservadas} icon={AlertTriangle} className="border-l-4 border-l-blue-500" />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-72">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre, sede o estado..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>
        <Table columns={columns} data={paginatedData} loading={isLoading} pagination={{ current: page, pageSize, total: filtered.length, onChange: (p, s) => { setPage(p); setPageSize(s); } }} />
      </Card>

      {/* Modal: Crear / Editar */}
      <Modal isOpen={isFormOpen} onClose={() => { setIsFormOpen(false); setEditing(null); }} title={editing ? 'Editar Mesa' : 'Nueva Mesa'} size="md">
        <MesaForm sedesOptions={sedesOptions} initialData={editing} onSubmit={handleSubmit} onCancel={() => { setIsFormOpen(false); setEditing(null); }} isLoading={isSaving} />
      </Modal>

      {/* Modal: Confirmar eliminación */}
      <Modal isOpen={isDeleteOpen} onClose={() => { setIsDeleteOpen(false); setSelected(null); }} title="Eliminar Mesa" size="sm">
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar la mesa <strong className="text-gray-900">{selected?.nombre}</strong>? Esta acción no se puede deshacer.
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
