/**
 * LotesTab.jsx
 * ────────────
 * Pestaña de Lotes de Inventario: CRUD completo, ordenamiento FEFO,
 * alertas de vencimiento y registro de entrada de lotes.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Plus,
  Search,
  Eye,
  Edit,
  Trash2,
  Package,
  AlertTriangle,
  CalendarClock,
  Layers,
} from 'lucide-react';
import { useLotesInventario } from '../../hooks/useLotesInventario';
import { useAlmacenes } from '../../hooks/useAlmacenes';
import { useProductosInventario } from '../../hooks/useProductosInventario';
import { useStockInventario } from '../../hooks/useStockInventario';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatDate, formatCurrency } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { StatCard } from '@/admin/components/ui/StatCard';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { LoteEntradaForm } from '../forms/LoteEntradaForm';

/* Días para considerar "próximo a vencer" */
const DIAS_ALERTA_VENCIMIENTO = 30;

export const LotesTab = () => {
  const { negocioId } = useOutletContext();

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const { lotes, isLoading, createLote, deleteLote, isCreating, isDeleting } = useLotesInventario(negocioId);
  const { almacenes } = useAlmacenes(negocioId);
  const { productos } = useProductosInventario(negocioId);
  const { stock, createStock, updateStock } = useStockInventario(negocioId);

  /* ─── Helpers de vencimiento ─── */
  const diasParaVencer = (fechaVencimiento) => {
    if (!fechaVencimiento) return null;
    const hoy = new Date();
    const vence = new Date(fechaVencimiento);
    return Math.ceil((vence - hoy) / (1000 * 60 * 60 * 24));
  };

  const getVencimientoBadge = (fechaVencimiento) => {
    const dias = diasParaVencer(fechaVencimiento);
    if (dias === null) return <span className="text-xs text-gray-400">Sin vencimiento</span>;
    if (dias < 0) return <Badge variant="error">Vencido</Badge>;
    if (dias <= DIAS_ALERTA_VENCIMIENTO) return <Badge variant="warning">Próximo ({dias}d)</Badge>;
    return <Badge variant="success">{dias} días</Badge>;
  };

  /* ─── Filtrado y ordenamiento FEFO ─── */
  const filtered = useMemo(() => {
    let result = [...lotes];

    /* Ordenar por fecha de vencimiento (FEFO: primero los que vencen antes) */
    result.sort((a, b) => {
      if (!a.fechaVencimiento && !b.fechaVencimiento) return 0;
      if (!a.fechaVencimiento) return 1;
      if (!b.fechaVencimiento) return -1;
      return new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento);
    });

    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (l) =>
          l.producto?.nombre?.toLowerCase().includes(q) ||
          l.numeroLote?.toLowerCase().includes(q) ||
          l.almacen?.nombre?.toLowerCase().includes(q),
      );
    }
    return result;
  }, [lotes, debouncedSearch]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const total = lotes.length;
    const activos = lotes.filter((l) => Number(l.cantidadActual) > 0).length;
    const hoy = new Date();
    const proximosVencer = lotes.filter((l) => {
      const dias = diasParaVencer(l.fechaVencimiento);
      return dias !== null && dias >= 0 && dias <= DIAS_ALERTA_VENCIMIENTO;
    }).length;
    const vencidos = lotes.filter((l) => {
      const dias = diasParaVencer(l.fechaVencimiento);
      return dias !== null && dias < 0;
    }).length;
    return { total, activos, proximosVencer, vencidos };
  }, [lotes]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    try {
      // 1. Crear el lote
      await createLote(data);

      // 2. Sincronizar stock: buscar si existe registro para producto+almacén
      const stockExistente = stock.find(
        (s) => s.producto?.id === data.producto.id && s.almacen?.id === data.almacen.id
      );

      const cantidadLote = Number(data.cantidadInicial);
      const costoUnitario = Number(data.costoUnitario);

      if (stockExistente) {
        // Actualizar stock existente
        const nuevaCantidad = Number(stockExistente.cantidadActual || 0) + cantidadLote;
        const nuevoCostoPromedio = (
          (Number(stockExistente.cantidadActual || 0) * Number(stockExistente.costoPromedio || 0) +
            cantidadLote * costoUnitario) /
          nuevaCantidad
        );

        await updateStock({
          id: stockExistente.id,
          negocio: data.negocio,
          producto: data.producto,
          almacen: data.almacen,
          cantidadActual: nuevaCantidad,
          cantidadDisponible: nuevaCantidad,
          cantidadReservada: Number(stockExistente.cantidadReservada || 0),
          costoPromedio: nuevoCostoPromedio,
        });
      } else {
        // Crear nuevo registro de stock
        await createStock({
          negocio: data.negocio,
          producto: data.producto,
          almacen: data.almacen,
          cantidadActual: cantidadLote,
          cantidadDisponible: cantidadLote,
          cantidadReservada: 0,
          costoPromedio: costoUnitario,
        });
      }

      setIsCreateOpen(false);
    } catch (error) {
      console.error('Error al crear lote o actualizar stock:', error);
    }
  };

  const handleView = (lote) => {
    setSelected(lote);
    setIsDetailOpen(true);
  };

  const handleDeleteClick = (lote) => {
    setSelected(lote);
    setIsDeleteOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (selected) await deleteLote(selected.id);
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
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.producto?.nombre || '—'}</p>
          <p className="text-xs text-gray-500 truncate">Lote: {row.numeroLote || '—'}</p>
        </div>
      ),
    },
    {
      key: 'almacen',
      title: 'Almacén',
      width: '130px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.almacen?.nombre || '—'}</span>
      ),
    },
    {
      key: 'cantidadActual',
      title: 'Cant. Actual',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm font-semibold text-gray-900">
          {Number(row.cantidadActual || 0).toLocaleString()}
        </span>
      ),
    },
    {
      key: 'costoUnitario',
      title: 'Costo Unit.',
      width: '100px',
      align: 'right',
      render: (_, row) => (
        <span className="text-sm text-gray-600">
          {formatCurrency(Number(row.costoUnitario || 0))}
        </span>
      ),
    },
    {
      key: 'fechaIngreso',
      title: 'Ingreso',
      width: '100px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{formatDate(row.fechaIngreso)}</span>
      ),
    },
    {
      key: 'vencimiento',
      title: 'Vencimiento',
      width: '130px',
      align: 'center',
      render: (_, row) => getVencimientoBadge(row.fechaVencimiento),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '100px',
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
            title="Eliminar"
            onClick={() => handleDeleteClick(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
          >
            <Trash2 size={16} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Lotes de Inventario</h1>
        <p className="text-gray-600 mt-1">
          Control de lotes con ordenamiento FEFO (primero en vencer, primero en salir)
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total lotes" value={stats.total} icon={Layers} />
        <StatCard
          title="Lotes con stock"
          value={stats.activos}
          icon={Package}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Próximos a vencer"
          value={stats.proximosVencer}
          icon={CalendarClock}
          className="border-l-4 border-l-yellow-500"
        />
        <StatCard
          title="Vencidos"
          value={stats.vencidos}
          icon={AlertTriangle}
          className="border-l-4 border-l-red-500"
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
              placeholder="Buscar por producto, lote, almacén..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Registrar Entrada
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

      {/* ─── Modal: Crear lote ─── */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Registrar Entrada de Lote"
        size="lg"
      >
        <LoteEntradaForm
          productos={productos}
          almacenes={almacenes}
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* ─── Modal: Detalle ─── */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title="Detalle del Lote"
        size="md"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-green-100 flex items-center justify-center text-green-700 font-bold text-lg">
                {selected.producto?.nombre?.charAt(0)?.toUpperCase() || 'L'}
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{selected.producto?.nombre}</h3>
                <p className="text-sm text-gray-500">Lote: {selected.numeroLote}</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Almacén</p>
                <p className="font-medium">{selected.almacen?.nombre || '—'}</p>
              </div>
              <div>
                <p className="text-gray-500">Vencimiento</p>
                <div className="mt-0.5">{getVencimientoBadge(selected.fechaVencimiento)}</div>
              </div>
              <div>
                <p className="text-gray-500">Cantidad inicial</p>
                <p className="font-medium">{Number(selected.cantidadInicial || 0).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-gray-500">Cantidad actual</p>
                <p className="font-medium">{Number(selected.cantidadActual || 0).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-gray-500">Costo unitario</p>
                <p className="font-medium">{formatCurrency(Number(selected.costoUnitario || 0))}</p>
              </div>
              <div>
                <p className="text-gray-500">Fecha de ingreso</p>
                <p className="font-medium">{formatDate(selected.fechaIngreso)}</p>
              </div>
              <div>
                <p className="text-gray-500">Fecha de vencimiento</p>
                <p className="font-medium">{selected.fechaVencimiento ? formatDate(selected.fechaVencimiento) : 'Sin vencimiento'}</p>
              </div>
            </div>
          </div>
        )}
      </Modal>

      {/* ─── Confirmar eliminar ─── */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setSelected(null); }}
        onConfirm={handleDeleteConfirm}
        title="Eliminar Lote"
        message={`¿Está seguro de eliminar el lote "${selected?.numeroLote}" del producto "${selected?.producto?.nombre}"?`}
        confirmText="Eliminar"
        isLoading={isDeleting}
      />
    </div>
  );
};
