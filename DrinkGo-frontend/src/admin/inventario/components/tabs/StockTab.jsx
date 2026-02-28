/**
 * StockTab.jsx
 * ────────────
 * Pestaña de Stock Consolidado: tabla con búsqueda, alertas visuales y estadísticas.
 * Vista de solo lectura del stock actual por producto/almacén.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Search,
  Package,
  AlertTriangle,
  CheckCircle,
  XCircle,
  Eye,
  Warehouse,
} from 'lucide-react';
import { useStockInventario } from '../../hooks/useStockInventario';
import { useAlmacenes } from '../../hooks/useAlmacenes';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';

/* Umbrales de alerta (configurables) */
const STOCK_MINIMO = 10;
const STOCK_CRITICO = 3;

export const StockTab = () => {
  const { negocioId } = useOutletContext();

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selected, setSelected] = useState(null);
  const [filterAlmacen, setFilterAlmacen] = useState('');

  /* ─── Data hooks ─── */
  const { stock, isLoading } = useStockInventario(negocioId);
  const { almacenes } = useAlmacenes(negocioId);

  /* ─── Filtrado client-side ─── */
  const filtered = useMemo(() => {
    let result = stock;
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (s) =>
          s.producto?.nombre?.toLowerCase().includes(q) ||
          s.producto?.sku?.toLowerCase().includes(q) ||
          s.almacen?.nombre?.toLowerCase().includes(q),
      );
    }
    if (filterAlmacen) {
      result = result.filter((s) => String(s.almacen?.id) === filterAlmacen);
    }
    return result;
  }, [stock, debouncedSearch, filterAlmacen]);

  /* ─── Paginación client-side ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const total = stock.length;
    const stockBajo = stock.filter((s) => Number(s.cantidadActual) <= STOCK_MINIMO && Number(s.cantidadActual) > STOCK_CRITICO).length;
    const stockCritico = stock.filter((s) => Number(s.cantidadActual) <= STOCK_CRITICO).length;
    const valorTotal = stock.reduce((acc, s) => acc + (Number(s.cantidadActual) * Number(s.costoPromedio || 0)), 0);
    return { total, stockBajo, stockCritico, valorTotal };
  }, [stock]);

  /* ─── Helpers ─── */
  const getStockBadge = (cantidad) => {
    const qty = Number(cantidad);
    if (qty <= STOCK_CRITICO) return <Badge variant="error">Crítico</Badge>;
    if (qty <= STOCK_MINIMO) return <Badge variant="warning">Bajo</Badge>;
    return <Badge variant="success">Normal</Badge>;
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
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.producto?.nombre || '—'}</p>
          <p className="text-xs text-gray-500 truncate">SKU: {row.producto?.sku || '—'}</p>
        </div>
      ),
    },
    {
      key: 'almacen',
      title: 'Almacén',
      width: '150px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.almacen?.nombre || '—'}</span>
      ),
    },
    {
      key: 'cantidadActual',
      title: 'Stock Actual',
      width: '110px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm font-semibold text-gray-900">
          {Number(row.cantidadActual || 0).toLocaleString()}
        </span>
      ),
    },
    {
      key: 'cantidadReservada',
      title: 'Reservado',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm text-gray-500">
          {Number(row.cantidadReservada || 0).toLocaleString()}
        </span>
      ),
    },
    {
      key: 'cantidadDisponible',
      title: 'Disponible',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm font-medium text-green-700">
          {Number(row.cantidadDisponible || 0).toLocaleString()}
        </span>
      ),
    },
    {
      key: 'costoPromedio',
      title: 'Costo Prom.',
      width: '110px',
      align: 'right',
      render: (_, row) => (
        <span className="text-sm text-gray-600">
          {formatCurrency(Number(row.costoPromedio || 0))}
        </span>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '100px',
      align: 'center',
      render: (_, row) => getStockBadge(row.cantidadActual),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '80px',
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
      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total registros" value={stats.total} icon={Package} />
        <StatCard
          title="Stock bajo"
          value={stats.stockBajo}
          icon={AlertTriangle}
          className="border-l-4 border-l-yellow-500"
        />
        <StatCard
          title="Stock crítico"
          value={stats.stockCritico}
          icon={XCircle}
          className="border-l-4 border-l-red-500"
        />
        <StatCard
          title="Valor total"
          value={formatCurrency(stats.valorTotal)}
          icon={Warehouse}
          className="border-l-4 border-l-blue-500"
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
            <div className="relative w-full sm:w-72">
              <Search
                size={16}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <input
                type="text"
                placeholder="Buscar por producto, SKU..."
                value={searchTerm}
                onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
                className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
            {almacenes.length > 1 && (
              <select
                value={filterAlmacen}
                onChange={(e) => { setFilterAlmacen(e.target.value); setPage(1); }}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                <option value="">Todos los almacenes</option>
                {almacenes.map((a) => (
                  <option key={a.id} value={String(a.id)}>{a.nombre}</option>
                ))}
              </select>
            )}
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

      {/* ─── Modal: Detalle de stock ─── */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title="Detalle de Stock"
        size="md"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-green-100 flex items-center justify-center text-green-700 font-bold text-lg">
                {selected.producto?.nombre?.charAt(0)?.toUpperCase() || 'P'}
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{selected.producto?.nombre}</h3>
                <p className="text-sm text-gray-500">SKU: {selected.producto?.sku || '—'}</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Almacén</p>
                <p className="font-medium">{selected.almacen?.nombre || '—'}</p>
              </div>
              <div>
                <p className="text-gray-500">Estado</p>
                {getStockBadge(selected.cantidadActual)}
              </div>
              <div>
                <p className="text-gray-500">Stock actual</p>
                <p className="font-medium">{Number(selected.cantidadActual || 0).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-gray-500">Reservado</p>
                <p className="font-medium">{Number(selected.cantidadReservada || 0).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-gray-500">Disponible</p>
                <p className="font-medium text-green-700">{Number(selected.cantidadDisponible || 0).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-gray-500">Costo promedio</p>
                <p className="font-medium">{formatCurrency(Number(selected.costoPromedio || 0))}</p>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
