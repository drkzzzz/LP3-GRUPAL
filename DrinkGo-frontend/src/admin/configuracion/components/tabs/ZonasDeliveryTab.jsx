/**
 * ZonasDeliveryTab.jsx
 * ────────────────────
 * CRUD de zonas de delivery del negocio.
 */
import { useState, useMemo } from 'react';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  Truck,
  CheckCircle,
  MapPin,
} from 'lucide-react';
import { useZonasDelivery } from '../../hooks/useZonasDelivery';
import { useSedesConfig } from '../../hooks/useSedesConfig';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { ZonaDeliveryForm } from '../forms/ZonaDeliveryForm';
import { formatCurrency } from '@/shared/utils/formatters';

export const ZonasDeliveryTab = ({ context }) => {
  const { negocioId } = context;

  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selected, setSelected] = useState(null);

  const {
    zonas,
    isLoading,
    createZona,
    updateZona,
    deleteZona,
    isCreating,
    isUpdating,
    isDeleting,
  } = useZonasDelivery(negocioId);

  const { sedes } = useSedesConfig(negocioId);

  /* Sedes activas para el Select */
  const sedesOptions = useMemo(
    () =>
      sedes
        .filter((s) => s.estaActivo !== false)
        .map((s) => ({ value: String(s.id), label: `${s.nombre} – ${s.ciudad || ''}` })),
    [sedes],
  );

  /* Mapa id → sede para render */
  const sedesMap = useMemo(() => {
    const m = {};
    sedes.forEach((s) => { m[s.id] = s; });
    return m;
  }, [sedes]);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return zonas;
    const q = debouncedSearch.toLowerCase();
    return zonas.filter(
      (z) =>
        z.nombre?.toLowerCase().includes(q) ||
        z.descripcion?.toLowerCase().includes(q) ||
        sedesMap[z.sede?.id]?.nombre?.toLowerCase().includes(q),
    );
  }, [zonas, debouncedSearch, sedesMap]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: zonas.length,
    activas: zonas.filter((z) => z.estaActivo !== false).length,
    tarifaPromedio:
      zonas.length > 0
        ? zonas.reduce((acc, z) => acc + (z.tarifaDelivery || 0), 0) / zonas.length
        : 0,
  }), [zonas]);

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
      negocio: { id: negocioId },
      sede: { id: Number(formData.sedeId) },
      nombre: formData.nombre.trim(),
      descripcion: formData.descripcion?.trim() || '',
      tarifaDelivery: formData.tarifaDelivery,
      montoMinimoPedido: formData.montoMinimoPedido,
    };

    if (editing) {
      await updateZona({ ...payload, id: editing.id });
    } else {
      await createZona(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteZona(selected.id);
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
      width: '180px',
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
      key: 'tarifa',
      title: 'Tarifa delivery',
      width: '130px',
      align: 'right',
      render: (_, row) => (
        <span className="text-sm font-semibold text-gray-800">{formatCurrency(row.tarifaDelivery || 0)}</span>
      ),
    },
    {
      key: 'montoMinimo',
      title: 'Monto mínimo',
      width: '130px',
      align: 'right',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{formatCurrency(row.montoMinimoPedido || 0)}</span>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '100px',
      align: 'center',
      render: (_, row) =>
        row.estaActivo !== false ? <Badge variant="success">Activo</Badge> : <Badge variant="error">Inactivo</Badge>,
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
          <h1 className="text-2xl font-bold text-gray-900">Zonas de Delivery</h1>
          <p className="text-gray-600 mt-1">Administra las zonas de cobertura y tarifas de delivery</p>
        </div>
        <button onClick={openCreate} className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
          <Plus size={18} /> Nueva Zona
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard title="Total zonas" value={stats.total} icon={MapPin} />
        <StatCard title="Activas" value={stats.activas} icon={CheckCircle} className="border-l-4 border-l-green-500" />
        <StatCard title="Tarifa promedio" value={formatCurrency(stats.tarifaPromedio)} icon={Truck} className="border-l-4 border-l-blue-500" />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-72">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre, descripción o sede..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>
        <Table columns={columns} data={paginatedData} loading={isLoading} pagination={{ current: page, pageSize, total: filtered.length, onChange: (p, s) => { setPage(p); setPageSize(s); } }} />
      </Card>

      {/* Modal: Crear / Editar */}
      <Modal isOpen={isFormOpen} onClose={() => { setIsFormOpen(false); setEditing(null); }} title={editing ? 'Editar Zona de Delivery' : 'Nueva Zona de Delivery'} size="md">
        <ZonaDeliveryForm sedesOptions={sedesOptions} initialData={editing} onSubmit={handleSubmit} onCancel={() => { setIsFormOpen(false); setEditing(null); }} isLoading={isSaving} />
      </Modal>

      {/* Modal: Confirmar eliminación */}
      <Modal isOpen={isDeleteOpen} onClose={() => { setIsDeleteOpen(false); setSelected(null); }} title="Eliminar Zona de Delivery" size="sm">
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar la zona <strong className="text-gray-900">{selected?.nombre}</strong>? Esta acción no se puede deshacer.
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
