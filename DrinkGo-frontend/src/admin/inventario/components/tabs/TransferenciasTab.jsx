/**
 * TransferenciasTab.jsx
 * ─────────────────────
 * Pestaña de Transferencias entre Almacenes: permite registrar transferencias
 * y muestra el historial de movimientos de tipo "transferencia".
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
import {
  Plus,
  Search,
  Eye,
  ArrowRightLeft,
  Warehouse,
  Package,
  ArrowRight,
  XCircle,
} from 'lucide-react';
import { useMovimientosInventario } from '../../hooks/useMovimientosInventario';
import { useAlmacenes } from '../../hooks/useAlmacenes';
import { useProductosInventario } from '../../hooks/useProductosInventario';
import { useStockInventario } from '../../hooks/useStockInventario';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatDateTime } from '@/shared/utils/formatters';
import { message } from '@/shared/utils/notifications';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { StatCard } from '@/admin/components/ui/StatCard';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { TransferenciaForm } from '../forms/TransferenciaForm';

export const TransferenciasTab = () => {
  const { negocioId } = useOutletContext();
  const user = useAdminAuthStore((s) => s.user);
  const queryClient = useQueryClient();

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isCancelOpen, setIsCancelOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const { movimientos, isLoading, createMovimiento, deleteMovimiento, isCreating } = useMovimientosInventario(negocioId);
  const { almacenes } = useAlmacenes(negocioId);
  const { productos } = useProductosInventario(negocioId);  const { stock, createStock, updateStock } = useStockInventario(negocioId);
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
    try {
      // VALIDACIÓN DE SEGURIDAD: Verificar stock antes de procesar
      const stockOrigen = stock.find(
        (s) => s.producto?.id === data.producto.id && s.almacen?.id === data.almacenOrigen.id
      );

      const cantidad = Number(data.cantidad);
      const stockDisponible = stockOrigen ? Number(stockOrigen.cantidadActual || 0) : 0;

      // Si no hay suficiente stock, abortar
      if (!stockOrigen || stockDisponible < cantidad) {
        console.error('Stock insuficiente para realizar la transferencia');
        return;
      }

      // 1. Crear el movimiento de transferencia
      await createMovimiento(data);

      // 2. Sincronizar stock en almacén ORIGEN (restar)
      const nuevaCantidadOrigen = stockDisponible - cantidad;
      await updateStock({
        id: stockOrigen.id,
        negocio: data.negocio,
        producto: data.producto,
        almacen: data.almacenOrigen,
        cantidadActual: nuevaCantidadOrigen,
        cantidadDisponible: nuevaCantidadOrigen,
        cantidadReservada: Number(stockOrigen.cantidadReservada || 0),
        costoPromedio: Number(stockOrigen.costoPromedio || 0),
      });

      // 3. Sincronizar stock en almacén DESTINO (sumar)
      const stockDestino = stock.find(
        (s) => s.producto?.id === data.producto.id && s.almacen?.id === data.almacenDestino.id
      );

      if (stockDestino) {
        const nuevaCantidadDestino = Number(stockDestino.cantidadActual || 0) + cantidad;
        await updateStock({
          id: stockDestino.id,
          negocio: data.negocio,
          producto: data.producto,
          almacen: data.almacenDestino,
          cantidadActual: nuevaCantidadDestino,
          cantidadDisponible: nuevaCantidadDestino,
          cantidadReservada: Number(stockDestino.cantidadReservada || 0),
          costoPromedio: Number(stockDestino.costoPromedio || 0),
        });
      } else {
        // Crear stock en destino si no existe
        await createStock({
          negocio: data.negocio,
          producto: data.producto,
          almacen: data.almacenDestino,
          cantidadActual: cantidad,
          cantidadDisponible: cantidad,
          cantidadReservada: 0,
          costoPromedio: stockOrigen ? Number(stockOrigen.costoPromedio || 0) : 0,
        });
      }

      // 4. Invalidar queries para actualizar reportes en tiempo real
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['stock-inventario'] }),
        queryClient.invalidateQueries({ queryKey: ['movimientos-inventario'] }),
      ]);

      setIsCreateOpen(false);
    } catch (error) {
      console.error('Error al crear transferencia o actualizar stock:', error);
    }
  };

  const handleView = (item) => {
    setSelected(item);
    setIsDetailOpen(true);
  };

  const handleCancelClick = (item) => {
    setSelected(item);
    setIsCancelOpen(true);
  };

  const handleCancelTransferencia = async () => {
    if (!selected) return;

    try {
      const cantidad = Number(selected.cantidad || 0);

      // 1. Buscar stock en almacén ORIGEN (devolver la cantidad)
      const stockOrigen = stock.find(
        (s) => s.producto?.id === selected.producto?.id && s.almacen?.id === selected.almacenOrigen?.id
      );

      if (stockOrigen) {
        const nuevaCantidadOrigen = Number(stockOrigen.cantidadActual || 0) + cantidad;
        await updateStock({
          id: stockOrigen.id,
          negocio: { id: negocioId },
          producto: { id: selected.producto.id },
          almacen: { id: selected.almacenOrigen.id },
          cantidadActual: nuevaCantidadOrigen,
          cantidadDisponible: nuevaCantidadOrigen,
          cantidadReservada: Number(stockOrigen.cantidadReservada || 0),
          costoPromedio: Number(stockOrigen.costoPromedio || 0),
        });
      } else {
        // Si no existe stock en origen, crearlo
        await createStock({
          negocio: { id: negocioId },
          producto: { id: selected.producto.id },
          almacen: { id: selected.almacenOrigen.id },
          cantidadActual: cantidad,
          cantidadDisponible: cantidad,
          cantidadReservada: 0,
          costoPromedio: 0,
        });
      }

      // 2. Buscar stock en almacén DESTINO (restar la cantidad)
      const stockDestino = stock.find(
        (s) => s.producto?.id === selected.producto?.id && s.almacen?.id === selected.almacenDestino?.id
      );

      if (stockDestino) {
        const nuevaCantidadDestino = Math.max(0, Number(stockDestino.cantidadActual || 0) - cantidad);
        await updateStock({
          id: stockDestino.id,
          negocio: { id: negocioId },
          producto: { id: selected.producto.id },
          almacen: { id: selected.almacenDestino.id },
          cantidadActual: nuevaCantidadDestino,
          cantidadDisponible: nuevaCantidadDestino,
          cantidadReservada: Number(stockDestino.cantidadReservada || 0),
          costoPromedio: Number(stockDestino.costoPromedio || 0),
        });
      }

      // 3. Marcar la transferencia como cancelada (soft delete)
      await deleteMovimiento(selected.id);

      // 4. Invalidar TODAS las queries relacionadas para actualizar reportes y alertas
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['stock-inventario'] }),
        queryClient.invalidateQueries({ queryKey: ['lotes-inventario'] }),
        queryClient.invalidateQueries({ queryKey: ['movimientos-inventario'] }),
        queryClient.invalidateQueries({ queryKey: ['productos-inventario'] }),
      ]);

      message.success('Transferencia cancelada exitosamente. Los productos han regresado al almacén de origen.');
      setIsCancelOpen(false);
      setSelected(null);
    } catch (error) {
      console.error('Error al cancelar transferencia:', error);
      message.error('Error al cancelar la transferencia. Por favor, intenta nuevamente.');
    }
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
      title: 'Acciones',
      width: '120px',
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
            title="Cancelar transferencia"
            onClick={() => handleCancelClick(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-500 hover:text-red-700 transition-colors"
          >
            <XCircle size={16} />
          </button>
        </div>
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
          stock={stock}
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

      {/* ─── Modal: Confirmar Cancelación ─── */}
      <ConfirmDialog
        isOpen={isCancelOpen}
        onClose={() => { setIsCancelOpen(false); setSelected(null); }}
        onConfirm={handleCancelTransferencia}
        title="Cancelar Transferencia"
        message={
          <>
            <p className="mb-3"><strong>¿Está seguro de cancelar esta transferencia?</strong></p>
            {selected && (
              <div className="bg-orange-50 border border-orange-200 rounded-lg p-3 mb-3">
                <p className="text-sm"><strong>Producto:</strong> {selected.producto?.nombre}</p>
                <p className="text-sm"><strong>Cantidad:</strong> {Number(selected.cantidad || 0).toLocaleString()} unidades</p>
                <p className="text-sm"><strong>Origen:</strong> {selected.almacenOrigen?.nombre}</p>
                <p className="text-sm"><strong>Destino:</strong> {selected.almacenDestino?.nombre}</p>
                <p className="text-sm"><strong>Fecha:</strong> {formatDateTime(selected.fechaMovimiento)}</p>
              </div>
            )}
            <p className="text-sm text-gray-600 mb-2">Esta acción:</p>
            <ul className="list-disc list-inside text-xs text-gray-600 space-y-1">
              <li>Devolverá {selected?.cantidad || 0} unidades al almacén <strong>{selected?.almacenOrigen?.nombre}</strong></li>
              <li>Restará {selected?.cantidad || 0} unidades del almacén <strong>{selected?.almacenDestino?.nombre}</strong></li>
              <li>Los stocks se actualizarán automáticamente</li>
            </ul>
          </>
        }
        confirmText="Cancelar Transferencia"
        cancelText="No, mantener"
      />
    </div>
  );
};
