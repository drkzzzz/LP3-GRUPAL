/**
 * MetodosPagoTab.jsx
 * ──────────────────
 * CRUD de métodos de pago del negocio.
 */
import { useState, useMemo } from 'react';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  CreditCard,
  CheckCircle,
  Monitor,
  ShoppingCart,
} from 'lucide-react';
import { useMetodosPago } from '../../hooks/useMetodosPago';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { MetodoPagoForm } from '../forms/MetodoPagoForm';

const TIPO_LABELS = {
  efectivo: 'Efectivo',
  tarjeta_credito: 'Tarjeta Crédito',
  tarjeta_debito: 'Tarjeta Débito',
  transferencia_bancaria: 'Transferencia',
  billetera_digital: 'Billetera Digital',
  yape: 'Yape',
  plin: 'Plin',
  qr: 'QR',
  otro: 'Otro',
};

const TIPO_COLORS = {
  efectivo: 'success',
  tarjeta_credito: 'info',
  tarjeta_debito: 'info',
  transferencia_bancaria: 'warning',
  billetera_digital: 'info',
  yape: 'info',
  plin: 'info',
  qr: 'warning',
  otro: 'info',
};

export const MetodosPagoTab = ({ context }) => {
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
    metodosPago,
    isLoading,
    createMetodo,
    updateMetodo,
    deleteMetodo,
    isCreating,
    isUpdating,
    isDeleting,
  } = useMetodosPago(negocioId);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return metodosPago;
    const q = debouncedSearch.toLowerCase();
    return metodosPago.filter(
      (m) =>
        m.nombre?.toLowerCase().includes(q) ||
        m.codigo?.toLowerCase().includes(q) ||
        m.tipo?.toLowerCase().includes(q),
    );
  }, [metodosPago, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: metodosPago.length,
    activos: metodosPago.filter((m) => m.estaActivo !== false).length,
    pos: metodosPago.filter((m) => m.disponiblePos).length,
    online: metodosPago.filter((m) => m.disponibleTiendaOnline).length,
  }), [metodosPago]);

  /* ─── Handlers ─── */
  const openCreate = () => { setEditing(null); setIsFormOpen(true); };
  const openEdit = (item) => { setEditing(item); setIsFormOpen(true); };
  const openDelete = (item) => { setSelected(item); setIsDeleteOpen(true); };

  const handleSubmit = async (formData) => {
    const payload = {
      negocio: { id: negocioId },
      nombre: formData.nombre.trim(),
      codigo: formData.codigo.trim(),
      tipo: formData.tipo,
      disponiblePos: formData.disponiblePos,
      disponibleTiendaOnline: formData.disponibleTiendaOnline,
      orden: formData.orden,
    };

    if (editing) {
      await updateMetodo({ ...payload, id: editing.id });
    } else {
      await createMetodo(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteMetodo(selected.id);
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
          <p className="text-xs text-gray-500 font-mono">{row.codigo}</p>
        </div>
      ),
    },
    {
      key: 'tipo',
      title: 'Tipo',
      width: '150px',
      render: (_, row) => (
        <Badge variant={TIPO_COLORS[row.tipo] || 'info'}>
          {TIPO_LABELS[row.tipo] || row.tipo}
        </Badge>
      ),
    },
    {
      key: 'canales',
      title: 'Canales',
      width: '160px',
      render: (_, row) => (
        <div className="flex items-center gap-1.5">
          {row.disponiblePos && (
            <span className="inline-flex items-center gap-1 bg-green-50 text-green-600 text-xs px-2 py-0.5 rounded-full">
              <Monitor size={12} /> POS
            </span>
          )}
          {row.disponibleTiendaOnline && (
            <span className="inline-flex items-center gap-1 bg-blue-50 text-blue-600 text-xs px-2 py-0.5 rounded-full">
              <ShoppingCart size={12} /> Online
            </span>
          )}
        </div>
      ),
    },
    {
      key: 'orden',
      title: 'Orden',
      width: '70px',
      align: 'center',
      render: (_, row) => <span className="text-sm text-gray-600">{row.orden ?? 0}</span>,
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
          <h1 className="text-2xl font-bold text-gray-900">Métodos de Pago</h1>
          <p className="text-gray-600 mt-1">Configura los métodos de pago aceptados por tu negocio</p>
        </div>
        <button onClick={openCreate} className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
          <Plus size={18} /> Nuevo Método
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total métodos" value={stats.total} icon={CreditCard} />
        <StatCard title="Activos" value={stats.activos} icon={CheckCircle} className="border-l-4 border-l-green-500" />
        <StatCard title="Disponible POS" value={stats.pos} icon={Monitor} className="border-l-4 border-l-blue-500" />
        <StatCard title="Disponible Online" value={stats.online} icon={ShoppingCart} className="border-l-4 border-l-purple-500" />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-72">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre, código o tipo..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>
        <Table columns={columns} data={paginatedData} loading={isLoading} pagination={{ current: page, pageSize, total: filtered.length, onChange: (p, s) => { setPage(p); setPageSize(s); } }} />
      </Card>

      {/* Modal: Crear / Editar */}
      <Modal isOpen={isFormOpen} onClose={() => { setIsFormOpen(false); setEditing(null); }} title={editing ? 'Editar Método de Pago' : 'Nuevo Método de Pago'} size="md">
        <MetodoPagoForm initialData={editing} onSubmit={handleSubmit} onCancel={() => { setIsFormOpen(false); setEditing(null); }} isLoading={isSaving} />
      </Modal>

      {/* Modal: Confirmar eliminación */}
      <Modal isOpen={isDeleteOpen} onClose={() => { setIsDeleteOpen(false); setSelected(null); }} title="Eliminar Método de Pago" size="sm">
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar <strong className="text-gray-900">{selected?.nombre}</strong>? Esta acción no se puede deshacer.
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
