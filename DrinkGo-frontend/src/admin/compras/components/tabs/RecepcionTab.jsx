/**
 * RecepcionTab.jsx
 * ────────────────
 * Pantalla de recepción de mercadería.
 * Muestra órdenes pendientes y permite registrar cantidades recibidas
 * e ítems (detalle), además de marcar la orden como recibida.
 * 
 * INTEGRACIÓN CON INVENTARIO:
 * Al marcar una orden como recibida, automáticamente crea:
 * - Lotes de inventario con número de lote y fecha de vencimiento
 * - Actualiza el stock consolidado (cantidad + costo promedio)
 */
import { useState, useMemo, useCallback } from 'react';
import { useOutletContext } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
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
import { useLotesInventario } from '@/admin/inventario/hooks/useLotesInventario';
import { useStockInventario } from '@/admin/inventario/hooks/useStockInventario';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import { message } from '@/shared/utils/notifications';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
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
  const queryClient = useQueryClient();

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
    changeEstado,
  } = useOrdenesCompra(negocioId);

  const {
    detalles,
    getDetallesForOrden,
    updateDetalle,
    createDetalle,
  } = useDetalleOrdenesCompra(negocioId);

  /* ─── Inventario hooks ─── */
  const { createLote } = useLotesInventario(negocioId);
  const { stock, createStock, updateStock } = useStockInventario(negocioId);
  const { usuario } = useAdminAuthStore();

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

  /* ─── Calcular total desde detalles si el total de la orden es 0 ─── */
  const totalCalculado = useMemo(() => {
    if (!selectedOrden) return 0;
    // Si el total de la orden es mayor a 0, usarlo
    if (selectedOrden.total > 0) return selectedOrden.total;
    // Sino, calcular desde los detalles
    return ordenDetalles.reduce((sum, d) => sum + (Number(d.total) || 0), 0);
  }, [selectedOrden, ordenDetalles]);

  /* ─── Handlers ─── */
  const openRecepcion = (orden) => {
    setSelectedOrden(orden);
    const items = getDetallesForOrden(orden.id);
    const initialCantidades = {};
    items.forEach((d) => {
      // Conservar los datos si ya existen en el estado, sino inicializar vacíos
      const existingData = cantidades[d.id];
      initialCantidades[d.id] = {
        cantidadRecibida: existingData?.cantidadRecibida ?? d.cantidadRecibida ?? 0,
        numeroLote: existingData?.numeroLote ?? '',
        fechaVencimiento: existingData?.fechaVencimiento ?? '',
      };
    });
    setCantidades(initialCantidades);
    setIsRecepcionOpen(true);
  };

  const handleCantidadChange = useCallback((detalleId, value) => {
    setCantidades((prev) => ({
      ...prev,
      [detalleId]: {
        ...prev[detalleId],
        cantidadRecibida: Math.max(0, Number(value) || 0),
      },
    }));
  }, []);

  const handleLoteChange = useCallback((detalleId, field, value) => {
    setCantidades((prev) => ({
      ...prev,
      [detalleId]: {
        ...prev[detalleId],
        [field]: value,
      },
    }));
  }, []);

  const handleGuardarRecepcion = async () => {
    if (!selectedOrden || ordenDetalles.length === 0) return;

    setIsSaving(true);
    try {
      // Update each detail with the received quantity
      for (const detalle of ordenDetalles) {
        const data = cantidades[detalle.id];
        const cantidadRecibida = data?.cantidadRecibida ?? 0;
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

    // Validar que la orden tenga almacén asignado
    if (!selectedOrden.almacen?.id) {
      message.error('La orden debe tener un almacén asignado para poder registrar lotes');
      return;
    }

    // Validar que todos los productos con cantidadRecibida > 0 tengan número de lote
    const errores = [];
    for (const detalle of ordenDetalles) {
      const data = cantidades[detalle.id];
      const cantRecibida = data?.cantidadRecibida ?? 0;
      if (cantRecibida > 0 && !data?.numeroLote?.trim()) {
        errores.push(`${detalle.producto?.nombre || 'Producto'}: falta número de lote`);
      }
    }

    if (errores.length > 0) {
      message.error(`Por favor complete:\n${errores.join('\n')}`);
      return;
    }

    setIsSaving(true);
    try {
      // 1. Actualizar cantidades recibidas en los detalles
      for (const detalle of ordenDetalles) {
        const data = cantidades[detalle.id];
        const cantidadRecibida = data?.cantidadRecibida ?? 0;
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

      // 2. Crear lotes y sincronizar stock para productos recibidos
      for (const detalle of ordenDetalles) {
        const data = cantidades[detalle.id];
        const cantRecibida = data?.cantidadRecibida ?? 0;
        
        if (cantRecibida > 0 && detalle.producto) {
          // Crear lote en inventario
          const loteData = {
            negocio: { id: negocioId },
            producto: { id: detalle.producto.id },
            almacen: { id: selectedOrden.almacen.id },
            numeroLote: data.numeroLote.trim(),
            fechaIngreso: new Date().toISOString().split('T')[0],
            fechaVencimiento: data.fechaVencimiento || null,
            cantidadInicial: cantRecibida,
            cantidadActual: cantRecibida,
            costoUnitario: detalle.precioUnitario,
          };

          // Solo agregar creadoPor si tenemos el usuario
          if (usuario?.id) {
            loteData.creadoPor = { id: usuario.id };
          }

          await createLote(loteData);

          // Sincronizar stock: buscar si existe registro para producto+almacén
          const stockExistente = stock.find(
            (s) => s.producto?.id === detalle.producto.id && 
                   s.almacen?.id === selectedOrden.almacen.id
          );

          const costoUnitario = Number(detalle.precioUnitario);

          if (stockExistente) {
            // Actualizar stock existente (recalcular costo promedio)
            const cantidadAnterior = Number(stockExistente.cantidadActual || 0);
            const costoAnterior = Number(stockExistente.costoPromedio || 0);
            const nuevaCantidad = cantidadAnterior + cantRecibida;
            const nuevoCostoPromedio = (
              (cantidadAnterior * costoAnterior + cantRecibida * costoUnitario) /
              nuevaCantidad
            );

            await updateStock({
              id: stockExistente.id,
              negocio: { id: negocioId },
              producto: { id: detalle.producto.id },
              almacen: { id: selectedOrden.almacen.id },
              cantidadActual: nuevaCantidad,
              cantidadDisponible: nuevaCantidad,
              cantidadReservada: Number(stockExistente.cantidadReservada || 0),
              costoPromedio: nuevoCostoPromedio,
            });
          } else {
            // Crear nuevo registro de stock
            await createStock({
              negocio: { id: negocioId },
              producto: { id: detalle.producto.id },
              almacen: { id: selectedOrden.almacen.id },
              cantidadActual: cantRecibida,
              cantidadDisponible: cantRecibida,
              cantidadReservada: 0,
              costoPromedio: costoUnitario,
            });
          }
        }
      }

      // 3. Marcar orden como recibida
      await changeEstado({
        orden: selectedOrden,
        nuevoEstado: 'recibida',
      });

      // 4. Invalidar todas las queries relacionadas para refrescar la UI
      await queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      await queryClient.invalidateQueries({ queryKey: ['detalle-ordenes-compra'] });
      await queryClient.invalidateQueries({ queryKey: ['lotes-inventario'] });
      await queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });

      message.success('Orden recibida, lotes creados y stock actualizado exitosamente');
      setIsRecepcionOpen(false);
      setSelectedOrden(null);
      setCantidades({});
    } catch (err) {
      console.error('Error al procesar recepción:', err);
      message.error(err.response?.data?.message || 'Error al marcar la orden como recibida');
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
          <div className="space-y-2.5">
            {/* Info de la orden - compacta en línea */}
            <div className="flex items-center gap-4 text-xs bg-gray-50 p-2 rounded">
              <div className="flex items-center gap-1.5">
                <span className="text-gray-500 font-medium">Proveedor:</span>
                <span className="text-gray-900 font-semibold">{selectedOrden.proveedor?.razonSocial || '—'}</span>
              </div>
              <div className="h-3 w-px bg-gray-300"></div>
              <div className="flex items-center gap-1.5">
                <span className="text-gray-500 font-medium">Almacén:</span>
                <span className="text-gray-900">{selectedOrden.almacen?.nombre || '—'}</span>
              </div>
              <div className="h-3 w-px bg-gray-300"></div>
              <div className="flex items-center gap-1.5 ml-auto">
                <span className="text-gray-500 font-medium">Total:</span>
                <span className="text-gray-900 font-bold">{formatCurrency(totalCalculado)}</span>
                {selectedOrden.total === 0 && totalCalculado > 0 && (
                  <span className="text-orange-600 text-[10px] italic">(calculado)</span>
                )}
              </div>
            </div>

            {/* Ayuda compacta */}
            <div className="bg-blue-50 border border-blue-200 rounded p-2 text-[11px] text-blue-800">
              <strong>💡 Tip:</strong> Complete <strong>cantidad recibida</strong> y <strong>N° lote</strong> (requerido si cantidad &gt; 0). Fecha vencimiento opcional.
            </div>

            {/* Tabla de ítems */}
            {ordenDetalles.length === 0 ? (
              <div className="text-center py-6">
                <p className="text-gray-500 text-xs italic">
                  No hay ítems en esta orden. Agrega ítems desde la pestaña "Órdenes de Compra".
                </p>
              </div>
            ) : (
              <div className="border rounded-lg overflow-x-auto max-h-[300px] overflow-y-auto">
                <table className="w-full text-[11px]">
                  <thead className="bg-gray-50 text-[10px] uppercase text-gray-500 sticky top-0">
                    <tr>
                      <th className="px-2 py-1 text-left">Producto</th>
                      <th className="px-2 py-1 text-center w-16">Sol.</th>
                      <th className="px-2 py-1 text-center w-16">Rec.</th>
                      <th className="px-2 py-1 text-center w-24">N° Lote</th>
                      <th className="px-2 py-1 text-center w-28">Vencimiento</th>
                      <th className="px-2 py-1 text-right w-20">P.Unit</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {ordenDetalles.map((d) => {
                      const data = cantidades[d.id] || { cantidadRecibida: 0, numeroLote: '', fechaVencimiento: '' };
                      const recibida = data.cantidadRecibida;
                      return (
                        <tr key={d.id} className="hover:bg-gray-50">
                          <td className="px-2 py-1">
                            <span className="text-[11px] font-medium">{d.producto?.nombre || '—'}</span>
                          </td>
                          <td className="px-2 py-1 text-center text-[11px] font-medium text-gray-600">
                            {d.cantidadSolicitada}
                          </td>
                          <td className="px-2 py-1">
                            <input
                              type="number"
                              min="0"
                              value={recibida}
                              onChange={(e) => handleCantidadChange(d.id, e.target.value)}
                              className="w-14 border border-gray-300 rounded px-1.5 py-0.5 text-center text-[11px] focus:outline-none focus:ring-1 focus:ring-green-500"
                            />
                          </td>
                          <td className="px-2 py-1">
                            <input
                              type="text"
                              placeholder="LT-001"
                              value={data.numeroLote}
                              onChange={(e) => handleLoteChange(d.id, 'numeroLote', e.target.value)}
                              className="w-full border border-gray-300 rounded px-1.5 py-0.5 text-[11px] focus:outline-none focus:ring-1 focus:ring-green-500"
                              disabled={recibida === 0}
                            />
                          </td>
                          <td className="px-2 py-1">
                            <input
                              type="date"
                              value={data.fechaVencimiento}
                              onChange={(e) => handleLoteChange(d.id, 'fechaVencimiento', e.target.value)}
                              className="w-full border border-gray-300 rounded px-1.5 py-0.5 text-[11px] focus:outline-none focus:ring-1 focus:ring-green-500"
                              disabled={recibida === 0}
                            />
                          </td>
                          <td className="px-2 py-1 text-right text-[11px] font-medium">
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
            <div className="flex justify-end gap-2 pt-2 border-t">
              <Button
                variant="secondary"
                onClick={() => {
                  setIsRecepcionOpen(false);
                  setSelectedOrden(null);
                }}
                className="text-[11px] px-3 py-1.5"
              >
                Cancelar
              </Button>
              {ordenDetalles.length > 0 && (
                <>
                  <Button
                    variant="secondary"
                    onClick={handleGuardarRecepcion}
                    disabled={isSaving}
                    className="flex items-center gap-1 text-[11px] px-3 py-1.5"
                  >
                    <Save size={13} />
                    {isSaving ? 'Guardando...' : 'Guardar'}
                  </Button>
                  <Button
                    onClick={handleMarcarRecibida}
                    disabled={isSaving}
                    className="flex items-center gap-1 bg-green-600 hover:bg-green-700 text-[11px] px-3 py-1.5"
                  >
                    <PackageCheck size={13} />
                    {isSaving ? 'Procesando...' : 'Marcar recibida'}
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
