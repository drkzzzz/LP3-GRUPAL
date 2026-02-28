/**
 * AjustesTab.jsx
 * ──────────────
 * Pestaña de Ajustes de Inventario: permite crear ajustes manuales
 * (ajuste_positivo, ajuste_negativo, merma, devolucion).
 * Los ajustes se muestran como movimientos filtrados por tipo.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Plus,
  Search,
  Eye,
  SlidersHorizontal,
  TrendingUp,
  TrendingDown,
  RotateCcw,
} from 'lucide-react';
import { useMovimientosInventario } from '../../hooks/useMovimientosInventario';
import { useAlmacenes } from '../../hooks/useAlmacenes';
import { useProductosInventario } from '../../hooks/useProductosInventario';
import { useStockInventario } from '../../hooks/useStockInventario';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatDateTime } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { StatCard } from '@/admin/components/ui/StatCard';
import { AjusteInventarioForm } from '../forms/AjusteInventarioForm';

const TIPOS_AJUSTE = ['ajuste_positivo', 'ajuste_negativo', 'merma', 'devolucion'];

const TIPO_LABELS = {
  ajuste_positivo: { label: 'Ajuste (+)', variant: 'success' },
  ajuste_negativo: { label: 'Ajuste (−)', variant: 'error' },
  merma:           { label: 'Merma',       variant: 'error' },
  devolucion:      { label: 'Devolución',  variant: 'warning' },
};

export const AjustesTab = () => {
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
  const { productos } = useProductosInventario(negocioId);  const { stock, createStock, updateStock } = useStockInventario(negocioId);
  /* ─── Filtrar solo ajustes ─── */
  const ajustes = useMemo(() => {
    return movimientos
      .filter((m) => TIPOS_AJUSTE.includes(m.tipoMovimiento))
      .sort((a, b) => new Date(b.fechaMovimiento) - new Date(a.fechaMovimiento));
  }, [movimientos]);

  const filtered = useMemo(() => {
    if (!debouncedSearch) return ajustes;
    const q = debouncedSearch.toLowerCase();
    return ajustes.filter(
      (m) =>
        m.producto?.nombre?.toLowerCase().includes(q) ||
        m.motivoMovimiento?.toLowerCase().includes(q) ||
        m.referenciaDocumento?.toLowerCase().includes(q),
    );
  }, [ajustes, debouncedSearch]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: ajustes.length,
    positivos: ajustes.filter((m) => m.tipoMovimiento === 'ajuste_positivo').length,
    negativos: ajustes.filter((m) => m.tipoMovimiento === 'ajuste_negativo').length,
    mermas: ajustes.filter((m) => m.tipoMovimiento === 'merma').length,
  }), [ajustes]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    try {
      // 1. Crear el movimiento
      await createMovimiento(data);

      // 2. Sincronizar stock
      const almacenAfectado = data.almacenOrigen || data.almacenDestino;
      const stockExistente = stock.find(
        (s) => s.producto?.id === data.producto.id && s.almacen?.id === almacenAfectado.id
      );

      const cantidad = Number(data.cantidad);
      const isPositive = ['ajuste_positivo', 'devolucion'].includes(data.tipoMovimiento);
      const deltaStock = isPositive ? cantidad : -cantidad;

      if (stockExistente) {
        // Actualizar stock existente
        const nuevaCantidad = Math.max(0, Number(stockExistente.cantidadActual || 0) + deltaStock);
        await updateStock({
          id: stockExistente.id,
          negocio: data.negocio,
          producto: data.producto,
          almacen: almacenAfectado,
          cantidadActual: nuevaCantidad,
          cantidadDisponible: nuevaCantidad,
          cantidadReservada: Number(stockExistente.cantidadReservada || 0),
          costoPromedio: Number(stockExistente.costoPromedio || 0),
        });
      } else if (isPositive) {
        // Solo crear stock si es ajuste positivo
        await createStock({
          negocio: data.negocio,
          producto: data.producto,
          almacen: almacenAfectado,
          cantidadActual: cantidad,
          cantidadDisponible: cantidad,
          cantidadReservada: 0,
          costoPromedio: 0,
        });
      }

      setIsCreateOpen(false);
    } catch (error) {
      console.error('Error al crear ajuste o actualizar stock:', error);
    }
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
      width: '120px',
      align: 'center',
      render: (_, row) => {
        const config = TIPO_LABELS[row.tipoMovimiento] || { label: row.tipoMovimiento, variant: 'info' };
        return <Badge variant={config.variant}>{config.label}</Badge>;
      },
    },
    {
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <p className="text-sm font-medium text-gray-900 truncate">{row.producto?.nombre || '—'}</p>
      ),
    },
    {
      key: 'cantidad',
      title: 'Cantidad',
      width: '100px',
      align: 'center',
      render: (_, row) => {
        const isPositive = row.tipoMovimiento === 'ajuste_positivo' || row.tipoMovimiento === 'devolucion';
        return (
          <span className={`text-sm font-semibold ${isPositive ? 'text-green-700' : 'text-red-600'}`}>
            {isPositive ? '+' : '−'}{Number(row.cantidad || 0).toLocaleString()}
          </span>
        );
      },
    },
    {
      key: 'motivo',
      title: 'Motivo',
      width: '180px',
      render: (_, row) => (
        <span className="text-sm text-gray-600 truncate block">{row.motivoMovimiento || '—'}</span>
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
        <h1 className="text-2xl font-bold text-gray-900">Ajustes de Inventario</h1>
        <p className="text-gray-600 mt-1">
          Ajustes manuales: correcciones de conteo, mermas, devoluciones y muestras
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total ajustes" value={stats.total} icon={SlidersHorizontal} />
        <StatCard
          title="Ajustes (+)"
          value={stats.positivos}
          icon={TrendingUp}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Ajustes (−)"
          value={stats.negativos}
          icon={TrendingDown}
          className="border-l-4 border-l-red-500"
        />
        <StatCard
          title="Mermas"
          value={stats.mermas}
          icon={RotateCcw}
          className="border-l-4 border-l-yellow-500"
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
              placeholder="Buscar por producto, motivo..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nuevo Ajuste
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

      {/* ─── Modal: Crear ajuste ─── */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Nuevo Ajuste de Inventario"
        size="lg"
      >
        <AjusteInventarioForm
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
        title="Detalle del Ajuste"
        size="md"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-900">{selected.producto?.nombre}</h3>
              <Badge variant={TIPO_LABELS[selected.tipoMovimiento]?.variant || 'info'}>
                {TIPO_LABELS[selected.tipoMovimiento]?.label || selected.tipoMovimiento}
              </Badge>
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
                <p className="text-gray-500">Almacén</p>
                <p className="font-medium">{selected.almacenOrigen?.nombre || selected.almacenDestino?.nombre || '—'}</p>
              </div>
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
