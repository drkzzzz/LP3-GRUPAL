/**
 * TransferenciasTab.jsx
 * ─────────────────────
 * Pestaña de Transferencias entre Almacenes: permite registrar transferencias
 * y muestra el historial de movimientos de tipo "transferencia".
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Plus,
  Search,
  Eye,
  ArrowRightLeft,
  Warehouse,
  Package,
  ArrowRight,
} from 'lucide-react';
import { useMovimientosInventario } from '../../hooks/useMovimientosInventario';
import { useAlmacenes } from '../../hooks/useAlmacenes';
import { useProductosInventario } from '../../hooks/useProductosInventario';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatDateTime } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { StatCard } from '@/admin/components/ui/StatCard';
import { TransferenciaForm } from '../forms/TransferenciaForm';

export const TransferenciasTab = () => {
  const { negocioId } = useOutletContext();
  const user = useAdminAuthStore((s) => s.user);

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const { movimientos, isLoading, createMovimiento, isCreating } = useMovimientosInventario(negocioId);
  const { almacenes } = useAlmacenes(negocioId);
  const { productos } = useProductosInventario(negocioId);

  /* ─── Filtrar solo transferencias ─── */
  const transferencias = useMemo(() => {
    return movimientos
      .filter((m) => m.tipoMovimiento === 'transferencia')
      .sort((a, b) => new Date(b.fechaMovimiento) - new Date(a.fechaMovimiento));
  }, [movimientos]);

  const filtered = useMemo(() => {
    if (!debouncedSearch) return transferencias;
    const q = debouncedSearch.toLowerCase();
    return transferencias.filter(
      (m) =>
        m.producto?.nombre?.toLowerCase().includes(q) ||
        m.almacenOrigen?.nombre?.toLowerCase().includes(q) ||
        m.almacenDestino?.nombre?.toLowerCase().includes(q) ||
        m.motivoMovimiento?.toLowerCase().includes(q),
    );
  }, [transferencias, debouncedSearch]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const total = transferencias.length;
    const almacenesInvolucrados = new Set();
    transferencias.forEach((t) => {
      if (t.almacenOrigen?.id) almacenesInvolucrados.add(t.almacenOrigen.id);
      if (t.almacenDestino?.id) almacenesInvolucrados.add(t.almacenDestino.id);
    });
    const productosTransferidos = new Set(transferencias.map((t) => t.producto?.id)).size;
    const unidadesTotales = transferencias.reduce((acc, t) => acc + Number(t.cantidad || 0), 0);
    return { total, almacenes: almacenesInvolucrados.size, productosTransferidos, unidadesTotales };
  }, [transferencias]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    await createMovimiento(data);
    setIsCreateOpen(false);
  };

  const handleView = (item) => {
    setSelected(item);
    setIsDetailOpen(true);
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
      key: 'fecha',
      title: 'Fecha',
      width: '140px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{formatDateTime(row.fechaMovimiento)}</span>
      ),
    },
    {
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <p className="text-sm font-medium text-gray-900 truncate">{row.producto?.nombre || '—'}</p>
      ),
    },
    {
      key: 'origen',
      title: 'Origen',
      width: '140px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.almacenOrigen?.nombre || '—'}</span>
      ),
    },
    {
      key: 'arrow',
      title: '',
      width: '40px',
      align: 'center',
      render: () => <ArrowRight size={16} className="text-gray-400 mx-auto" />,
    },
    {
      key: 'destino',
      title: 'Destino',
      width: '140px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.almacenDestino?.nombre || '—'}</span>
      ),
    },
    {
      key: 'cantidad',
      title: 'Cantidad',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm font-semibold text-blue-700">
          {Number(row.cantidad || 0).toLocaleString()}
        </span>
      ),
    },
    {
      key: 'usuario',
      title: 'Usuario',
      width: '120px',
      render: (_, row) => (
        <span className="text-sm text-gray-500">{row.usuario?.nombre || '—'}</span>
      ),
    },
    {
      key: 'actions',
      title: '',
      width: '60px',
      align: 'center',
      render: (_, row) => (
        <button
          title="Ver detalles"
          onClick={() => handleView(row)}
          className="p-1.5 rounded hover:bg-blue-50 text-blue-500 hover:text-blue-700 transition-colors"
        >
          <Eye size={16} />
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Transferencias entre Almacenes</h1>
        <p className="text-gray-600 mt-1">
          Registro y seguimiento de traslados de productos entre almacenes
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total transferencias" value={stats.total} icon={ArrowRightLeft} />
        <StatCard
          title="Almacenes involucrados"
          value={stats.almacenes}
          icon={Warehouse}
          className="border-l-4 border-l-blue-500"
        />
        <StatCard
          title="Productos distintos"
          value={stats.productosTransferidos}
          icon={Package}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Unidades totales"
          value={stats.unidadesTotales.toLocaleString()}
          icon={ArrowRightLeft}
          className="border-l-4 border-l-purple-500"
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search
              size={16}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <input
              type="text"
              placeholder="Buscar por producto, almacén..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nueva Transferencia
          </Button>
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

      {/* ─── Modal: Crear transferencia ─── */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Nueva Transferencia"
        size="lg"
      >
        <TransferenciaForm
          productos={productos}
          almacenes={almacenes}
          negocioId={negocioId}
          userId={user?.id}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* ─── Modal: Detalle ─── */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title="Detalle de Transferencia"
        size="md"
      >
        {selected && (
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-gray-900">{selected.producto?.nombre}</h3>

            <div className="flex items-center gap-3 bg-gray-50 rounded-lg p-4">
              <div className="text-center flex-1">
                <p className="text-xs text-gray-500 mb-1">Origen</p>
                <p className="font-semibold text-gray-900">{selected.almacenOrigen?.nombre || '—'}</p>
              </div>
              <ArrowRight size={20} className="text-gray-400 flex-shrink-0" />
              <div className="text-center flex-1">
                <p className="text-xs text-gray-500 mb-1">Destino</p>
                <p className="font-semibold text-gray-900">{selected.almacenDestino?.nombre || '—'}</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Fecha</p>
                <p className="font-medium">{formatDateTime(selected.fechaMovimiento)}</p>
              </div>
              <div>
                <p className="text-gray-500">Cantidad</p>
                <p className="font-medium">{Number(selected.cantidad || 0).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-gray-500">Usuario</p>
                <p className="font-medium">{selected.usuario?.nombre || '—'}</p>
              </div>
            </div>

            {selected.motivoMovimiento && (
              <div>
                <p className="text-sm text-gray-500">Motivo</p>
                <p className="text-sm text-gray-700 mt-1">{selected.motivoMovimiento}</p>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};
