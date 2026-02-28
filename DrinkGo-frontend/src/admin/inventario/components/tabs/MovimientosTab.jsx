/**
 * MovimientosTab.jsx
 * ──────────────────
 * Pestaña de Movimientos de Inventario (Kardex): historial completo,
 * filtros por tipo de movimiento, producto y almacén.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Search,
  Eye,
  ArrowDownCircle,
  ArrowUpCircle,
  ArrowRightLeft,
  History,
  TrendingUp,
  TrendingDown,
} from 'lucide-react';
import { useMovimientosInventario } from '../../hooks/useMovimientosInventario';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatDateTime, formatCurrency } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';

/* Etiquetas y colores por tipo de movimiento */
const TIPO_CONFIG = {
  entrada:          { label: 'Entrada',          variant: 'success', icon: ArrowDownCircle },
  salida:           { label: 'Salida',           variant: 'error',   icon: ArrowUpCircle },
  transferencia:    { label: 'Transferencia',    variant: 'info',    icon: ArrowRightLeft },
  ajuste_positivo:  { label: 'Ajuste (+)',       variant: 'success', icon: TrendingUp },
  ajuste_negativo:  { label: 'Ajuste (−)',       variant: 'error',   icon: TrendingDown },
  devolucion:       { label: 'Devolución',       variant: 'warning', icon: ArrowDownCircle },
  merma:            { label: 'Merma',            variant: 'error',   icon: TrendingDown },
};

const TIPO_OPTIONS = [
  { value: '', label: 'Todos los tipos' },
  { value: 'entrada', label: 'Entrada' },
  { value: 'salida', label: 'Salida' },
  { value: 'transferencia', label: 'Transferencia' },
  { value: 'ajuste_positivo', label: 'Ajuste positivo' },
  { value: 'ajuste_negativo', label: 'Ajuste negativo' },
  { value: 'devolucion', label: 'Devolución' },
  { value: 'merma', label: 'Merma' },
];

export const MovimientosTab = () => {
  const { negocioId } = useOutletContext();

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [filterTipo, setFilterTipo] = useState('');
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const { movimientos, isLoading } = useMovimientosInventario(negocioId);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    let result = [...movimientos];

    /* Más recientes primero */
    result.sort((a, b) => new Date(b.fechaMovimiento) - new Date(a.fechaMovimiento));

    if (filterTipo) {
      result = result.filter((m) => m.tipoMovimiento === filterTipo);
    }
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (m) =>
          m.producto?.nombre?.toLowerCase().includes(q) ||
          m.motivoMovimiento?.toLowerCase().includes(q) ||
          m.referenciaDocumento?.toLowerCase().includes(q) ||
          m.usuario?.nombre?.toLowerCase().includes(q),
      );
    }
    return result;
  }, [movimientos, debouncedSearch, filterTipo]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const total = movimientos.length;
    const entradas = movimientos.filter((m) => m.tipoMovimiento === 'entrada' || m.tipoMovimiento === 'ajuste_positivo' || m.tipoMovimiento === 'devolucion').length;
    const salidas = movimientos.filter((m) => m.tipoMovimiento === 'salida' || m.tipoMovimiento === 'ajuste_negativo' || m.tipoMovimiento === 'merma').length;
    const transferencias = movimientos.filter((m) => m.tipoMovimiento === 'transferencia').length;
    return { total, entradas, salidas, transferencias };
  }, [movimientos]);

  /* ─── Helpers ─── */
  const getTipoBadge = (tipo) => {
    const config = TIPO_CONFIG[tipo] || { label: tipo, variant: 'info' };
    return <Badge variant={config.variant}>{config.label}</Badge>;
  };

  const getAlmacenInfo = (mov) => {
    if (mov.tipoMovimiento === 'transferencia') {
      return `${mov.almacenOrigen?.nombre || '—'} → ${mov.almacenDestino?.nombre || '—'}`;
    }
    return mov.almacenOrigen?.nombre || mov.almacenDestino?.nombre || '—';
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
      key: 'tipo',
      title: 'Tipo',
      width: '130px',
      align: 'center',
      render: (_, row) => getTipoBadge(row.tipoMovimiento),
    },
    {
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.producto?.nombre || '—'}</p>
        </div>
      ),
    },
    {
      key: 'cantidad',
      title: 'Cantidad',
      width: '100px',
      align: 'center',
      render: (_, row) => {
        const isEntrada = ['entrada', 'ajuste_positivo', 'devolucion'].includes(row.tipoMovimiento);
        return (
          <span className={`text-sm font-semibold ${isEntrada ? 'text-green-700' : 'text-red-600'}`}>
            {isEntrada ? '+' : '−'}{Number(row.cantidad || 0).toLocaleString()}
          </span>
        );
      },
    },
    {
      key: 'almacen',
      title: 'Almacén',
      width: '160px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{getAlmacenInfo(row)}</span>
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
      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total movimientos" value={stats.total} icon={History} />
        <StatCard
          title="Entradas"
          value={stats.entradas}
          icon={ArrowDownCircle}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Salidas"
          value={stats.salidas}
          icon={ArrowUpCircle}
          className="border-l-4 border-l-red-500"
        />
        <StatCard
          title="Transferencias"
          value={stats.transferencias}
          icon={ArrowRightLeft}
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
                placeholder="Buscar por producto, motivo, doc..."
                value={searchTerm}
                onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
                className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
            <select
              value={filterTipo}
              onChange={(e) => { setFilterTipo(e.target.value); setPage(1); }}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              {TIPO_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
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

      {/* ─── Modal: Detalle ─── */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title="Detalle del Movimiento"
        size="md"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-900">{selected.producto?.nombre}</h3>
              {getTipoBadge(selected.tipoMovimiento)}
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
                <p className="text-gray-500">Costo unitario</p>
                <p className="font-medium">{formatCurrency(Number(selected.costoUnitario || 0))}</p>
              </div>
              <div>
                <p className="text-gray-500">Monto total</p>
                <p className="font-medium">{formatCurrency(Number(selected.montoTotal || 0))}</p>
              </div>
              {selected.almacenOrigen && (
                <div>
                  <p className="text-gray-500">Almacén origen</p>
                  <p className="font-medium">{selected.almacenOrigen.nombre}</p>
                </div>
              )}
              {selected.almacenDestino && (
                <div>
                  <p className="text-gray-500">Almacén destino</p>
                  <p className="font-medium">{selected.almacenDestino.nombre}</p>
                </div>
              )}
              <div>
                <p className="text-gray-500">Usuario</p>
                <p className="font-medium">{selected.usuario?.nombre || '—'}</p>
              </div>
              <div>
                <p className="text-gray-500">Referencia</p>
                <p className="font-medium">{selected.referenciaDocumento || '—'}</p>
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
