/**
 * RecepcionTab.jsx
 * ────────────────
 * Pantalla de recepción de mercadería.
 * Muestra órdenes pendientes y permite registrar cantidades recibidas
 * e ítems (detalle), además de marcar la orden como recibida.
 */
import { useState, useMemo, useCallback } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Search,
  PackageCheck,
  Clock,
  CheckCircle,
  Eye,
  TruckIcon,
  Save,
} from 'lucide-react';
import { useOrdenesCompra } from '../../hooks/useOrdenesCompra';
import { useDetalleOrdenesCompra } from '../../hooks/useDetalleOrdenesCompra';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import { message } from '@/shared/utils/notifications';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';

const ESTADO_MAP = {
  pendiente: { label: 'Pendiente', variant: 'warning' },
  recibida: { label: 'Recibida', variant: 'success' },
  cancelada: { label: 'Cancelada', variant: 'error' },
};

export const RecepcionTab = () => {
  const { negocioId } = useOutletContext();

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [isRecepcionOpen, setIsRecepcionOpen] = useState(false);
  const [selectedOrden, setSelectedOrden] = useState(null);
  const [cantidades, setCantidades] = useState({});
  const [isSaving, setIsSaving] = useState(false);

  /* ─── Data hooks ─── */
  const {
    ordenes,
    isLoading,
    updateOrden,
  } = useOrdenesCompra(negocioId);

  const {
    detalles,
    getDetallesForOrden,
    updateDetalle,
    createDetalle,
  } = useDetalleOrdenesCompra(negocioId);

  /* ─── Solo pendientes ─── */
  const ordenesPendientes = useMemo(
    () => ordenes.filter((o) => o.estado === 'pendiente'),
    [ordenes],
  );

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return ordenesPendientes;
    const q = debouncedSearch.toLowerCase();
    return ordenesPendientes.filter(
      (o) =>
        o.numeroOrden?.toLowerCase().includes(q) ||
        o.proveedor?.razonSocial?.toLowerCase().includes(q),
    );
  }, [ordenesPendientes, debouncedSearch]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const pendientes = ordenesPendientes.length;
    const recibidas = ordenes.filter((o) => o.estado === 'recibida').length;
    const montoEsperando = ordenesPendientes.reduce(
      (sum, o) => sum + (Number(o.total) || 0),
      0,
    );
    return { pendientes, recibidas, montoEsperando };
  }, [ordenes, ordenesPendientes]);

  /* ─── Detail items for selected order ─── */
  const ordenDetalles = useMemo(() => {
    if (!selectedOrden) return [];
    return getDetallesForOrden(selectedOrden.id);
  }, [selectedOrden, detalles, getDetallesForOrden]);

  /* ─── Handlers ─── */
  const openRecepcion = (orden) => {
    setSelectedOrden(orden);
    const items = getDetallesForOrden(orden.id);
    const initialCantidades = {};
    items.forEach((d) => {
      initialCantidades[d.id] = d.cantidadRecibida ?? 0;
    });
    setCantidades(initialCantidades);
    setIsRecepcionOpen(true);
  };

  const handleCantidadChange = useCallback((detalleId, value) => {
    setCantidades((prev) => ({
      ...prev,
      [detalleId]: Math.max(0, Number(value) || 0),
    }));
  }, []);

  const handleGuardarRecepcion = async () => {
    if (!selectedOrden || ordenDetalles.length === 0) return;

    setIsSaving(true);
    try {
      // Update each detail with the received quantity
      for (const detalle of ordenDetalles) {
        const cantidadRecibida = cantidades[detalle.id] ?? 0;
        await updateDetalle({
          id: detalle.id,
          ordenCompra: { id: selectedOrden.id },
          producto: detalle.producto ? { id: detalle.producto.id } : null,
          cantidadSolicitada: detalle.cantidadSolicitada,
          cantidadRecibida,
          precioUnitario: detalle.precioUnitario,
          subtotal: detalle.subtotal,
          impuesto: detalle.impuesto,
          total: detalle.total,
          estaActivo: detalle.estaActivo,
        });
      }
      message.success('Cantidades recibidas guardadas correctamente');
    } catch (err) {
      message.error('Error al guardar cantidades recibidas');
    } finally {
      setIsSaving(false);
    }
  };

  const handleMarcarRecibida = async () => {
    if (!selectedOrden) return;

    setIsSaving(true);
    try {
      // First save quantities
      for (const detalle of ordenDetalles) {
        const cantidadRecibida = cantidades[detalle.id] ?? 0;
        await updateDetalle({
          id: detalle.id,
          ordenCompra: { id: selectedOrden.id },
          producto: detalle.producto ? { id: detalle.producto.id } : null,
          cantidadSolicitada: detalle.cantidadSolicitada,
          cantidadRecibida,
          precioUnitario: detalle.precioUnitario,
          subtotal: detalle.subtotal,
          impuesto: detalle.impuesto,
          total: detalle.total,
          estaActivo: detalle.estaActivo,
        });
      }

      // Then mark order as received
      await updateOrden({
        id: selectedOrden.id,
        negocio: { id: negocioId },
        proveedor: selectedOrden.proveedor ? { id: selectedOrden.proveedor.id } : null,
        sede: selectedOrden.sede ? { id: selectedOrden.sede.id } : null,
        almacen: selectedOrden.almacen ? { id: selectedOrden.almacen.id } : null,
        estado: 'recibida',
        notas: selectedOrden.notas,
      });

      message.success('Orden marcada como recibida exitosamente');
      setIsRecepcionOpen(false);
      setSelectedOrden(null);
    } catch (err) {
      message.error('Error al marcar la orden como recibida');
    } finally {
      setIsSaving(false);
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
      key: 'numeroOrden',
      title: 'N° Orden',
      width: '130px',
      render: (_, row) => (
        <span className="text-sm font-mono font-medium text-gray-900">
          {row.numeroOrden || `#${row.id}`}
        </span>
      ),
    },
    {
      key: 'proveedor',
      title: 'Proveedor',
      render: (_, row) => (
        <span className="text-sm text-gray-900 truncate">
          {row.proveedor?.razonSocial || '—'}
        </span>
      ),
    },
    {
      key: 'sede',
      title: 'Sede / Almacén',
      width: '180px',
      render: (_, row) => (
        <div>
          <p className="text-sm text-gray-900">{row.sede?.nombre || '—'}</p>
          <p className="text-xs text-gray-500">{row.almacen?.nombre || ''}</p>
        </div>
      ),
    },
    {
      key: 'fecha',
      title: 'Fecha',
      width: '130px',
      render: (_, row) => (
        <span className="text-sm text-gray-700">
          {formatDateTime(row.fechaOrden || row.creadoEn)}
        </span>
      ),
    },
    {
      key: 'total',
      title: 'Total',
      width: '120px',
      align: 'right',
      render: (_, row) => (
        <span className="text-sm font-medium text-gray-900">
          {formatCurrency(row.total)}
        </span>
      ),
    },
    {
      key: 'actions',
      title: 'Recepción',
      width: '110px',
      align: 'center',
      render: (_, row) => (
        <button
          onClick={() => openRecepcion(row)}
          className="flex items-center gap-1 px-3 py-1.5 text-xs font-medium bg-green-50 text-green-700 rounded-lg hover:bg-green-100 transition-colors mx-auto"
        >
          <PackageCheck size={14} />
          Recibir
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Recepción de Mercadería</h1>
        <p className="text-gray-600 mt-1">
          Registra la recepción de productos de las órdenes de compra pendientes
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard
          title="Órdenes pendientes"
          value={stats.pendientes}
          icon={Clock}
          className="border-l-4 border-l-yellow-500"
        />
        <StatCard
          title="Órdenes recibidas"
          value={stats.recibidas}
          icon={CheckCircle}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Monto en espera"
          value={formatCurrency(stats.montoEsperando)}
          icon={TruckIcon}
          className="border-l-4 border-l-blue-500"
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="mb-4">
          <div className="relative w-full sm:w-72">
            <Search
              size={16}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <input
              type="text"
              placeholder="Buscar orden o proveedor..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPage(1);
              }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>

        {ordenesPendientes.length === 0 ? (
          <div className="text-center py-12">
            <CheckCircle size={48} className="text-green-300 mx-auto mb-3" />
            <p className="text-gray-500 text-sm">No hay órdenes pendientes de recepción</p>
          </div>
        ) : (
          <Table
            columns={columns}
            data={paginatedData}
            loading={isLoading}
            pagination={{
              current: page,
              pageSize,
              total: filtered.length,
              onChange: (newPage, newSize) => {
                setPage(newPage);
                setPageSize(newSize);
              },
            }}
          />
        )}
      </Card>

      {/* ─── Modal: Recepción de Orden ─── */}
      <Modal
        isOpen={isRecepcionOpen}
        onClose={() => {
          setIsRecepcionOpen(false);
          setSelectedOrden(null);
        }}
        title={`Recepción — ${selectedOrden?.numeroOrden || `#${selectedOrden?.id}`}`}
        size="lg"
      >
        {selectedOrden && (
          <div className="space-y-4">
            {/* Info de la orden */}
            <div className="bg-gray-50 p-4 rounded-lg grid grid-cols-2 sm:grid-cols-4 gap-3 text-sm">
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Proveedor</p>
                <p className="text-gray-900">{selectedOrden.proveedor?.razonSocial || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Sede</p>
                <p className="text-gray-900">{selectedOrden.sede?.nombre || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Almacén</p>
                <p className="text-gray-900">{selectedOrden.almacen?.nombre || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Total</p>
                <p className="text-gray-900 font-medium">{formatCurrency(selectedOrden.total)}</p>
              </div>
            </div>

            {/* Tabla de ítems */}
            {ordenDetalles.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-gray-500 text-sm italic">
                  No hay ítems en esta orden. Agrega ítems desde la pestaña "Órdenes de Compra".
                </p>
              </div>
            ) : (
              <div className="border rounded-lg overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="bg-gray-50 text-xs uppercase text-gray-500">
                    <tr>
                      <th className="px-3 py-2 text-left">Producto</th>
                      <th className="px-3 py-2 text-center">Solicitado</th>
                      <th className="px-3 py-2 text-center w-32">Recibido</th>
                      <th className="px-3 py-2 text-center">Diferencia</th>
                      <th className="px-3 py-2 text-right">P. Unit.</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {ordenDetalles.map((d) => {
                      const recibida = cantidades[d.id] ?? 0;
                      const diff = recibida - (d.cantidadSolicitada || 0);
                      return (
                        <tr key={d.id} className="hover:bg-gray-50">
                          <td className="px-3 py-2">{d.producto?.nombre || '—'}</td>
                          <td className="px-3 py-2 text-center font-medium">
                            {d.cantidadSolicitada}
                          </td>
                          <td className="px-3 py-2 text-center">
                            <input
                              type="number"
                              min="0"
                              value={recibida}
                              onChange={(e) => handleCantidadChange(d.id, e.target.value)}
                              className="w-20 border border-gray-300 rounded px-2 py-1 text-center text-sm focus:outline-none focus:ring-2 focus:ring-green-500 mx-auto"
                            />
                          </td>
                          <td className="px-3 py-2 text-center">
                            <span
                              className={`text-sm font-medium ${
                                diff === 0
                                  ? 'text-green-600'
                                  : diff < 0
                                  ? 'text-red-600'
                                  : 'text-blue-600'
                              }`}
                            >
                              {diff > 0 ? `+${diff}` : diff}
                            </span>
                          </td>
                          <td className="px-3 py-2 text-right">
                            {formatCurrency(d.precioUnitario)}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}

            {/* Acciones */}
            <div className="flex flex-col sm:flex-row justify-end gap-3 pt-2 border-t">
              <Button
                variant="secondary"
                onClick={() => {
                  setIsRecepcionOpen(false);
                  setSelectedOrden(null);
                }}
              >
                Cancelar
              </Button>
              {ordenDetalles.length > 0 && (
                <>
                  <Button
                    variant="secondary"
                    onClick={handleGuardarRecepcion}
                    disabled={isSaving}
                    className="flex items-center gap-2"
                  >
                    <Save size={16} />
                    {isSaving ? 'Guardando...' : 'Guardar cantidades'}
                  </Button>
                  <Button
                    onClick={handleMarcarRecibida}
                    disabled={isSaving}
                    className="flex items-center gap-2 bg-green-600 hover:bg-green-700"
                  >
                    <PackageCheck size={16} />
                    {isSaving ? 'Procesando...' : 'Marcar como recibida'}
                  </Button>
                </>
              )}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
