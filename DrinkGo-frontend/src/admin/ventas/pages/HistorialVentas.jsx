/**
 * HistorialVentas.jsx
 * ───────────────────
 * Historial de ventas del negocio con filtro, detalle y anulación.
 */
import { useState, useMemo, useCallback } from 'react';
import { Eye, Ban, Search, Receipt, Banknote, XCircle, Monitor, Gift, Calendar, RotateCcw, RefreshCw, AlertTriangle } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { Modal } from '@/admin/components/ui/Modal';
import { StatCard } from '@/admin/components/ui/StatCard';
import { useVentas, useVentaDetalle, useAnularVenta } from '../hooks/useVentas';
import { SinCajaAsignada } from '../components/SinCajaAsignada';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { devolucionesService, detalleDevolucionesService } from '@/admin/devoluciones/services/devolucionesService';

const ESTADO_MAP = {
  completada: { label: 'Completada', variant: 'success' },
  pendiente: { label: 'Pendiente', variant: 'warning' },
  anulada: { label: 'Anulada', variant: 'error' },
  cancelada: { label: 'Cancelada', variant: 'error' },
  reembolsada: { label: 'Reembolsada', variant: 'info' },
  parcialmente_pagada: { label: 'Parcial', variant: 'warning' },
  devuelta: { label: 'Devuelta', variant: 'info' },
};

const TIPO_VENTA_MAP = {
  pos: { label: 'POS', className: 'bg-blue-100 text-blue-700' },
  tienda_online: { label: 'Tienda Online', className: 'bg-amber-100 text-amber-700' },
  mesa: { label: 'Mesa', className: 'bg-purple-100 text-purple-700' },
  telefono: { label: 'Teléfono', className: 'bg-green-100 text-green-700' },
  otro: { label: 'Otro', className: 'bg-gray-100 text-gray-600' },
};

export const HistorialVentas = () => {
  const { user, cajaAsignada, getAlcance } = useAdminAuthStore();
  const { ventas, isLoading, refetch, alcanceHistorial } = useVentas();
  const { anularVenta, isAnulando } = useAnularVenta();
  const esSoloCaja = alcanceHistorial === 'caja_asignada';
  const esAdmin = getAlcance('m.ventas') === 'completo';
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = useCallback(async () => {
    setIsRefreshing(true);
    await refetch();
    setTimeout(() => setIsRefreshing(false), 600);
  }, [refetch]);

  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [page, setPage] = useState(1);
  const pageSize = 10;

  /* Filtros de fecha y estado */
  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');
  const [estadoFiltro, setEstadoFiltro] = useState('');

  /* Detalle modal */
  const [selectedVentaId, setSelectedVentaId] = useState(null);
  const [showDetalle, setShowDetalle] = useState(false);
  const { venta: ventaDetail, detalle, pagos, isLoading: loadingDetalle } =
    useVentaDetalle(showDetalle ? selectedVentaId : null);

  /* Devolución asociada a la venta (solo cuando es "devuelta") */
  const esDevuelta = ventaDetail?.estado === 'devuelta';
  const devolucionQuery = useQuery({
    queryKey: ['devoluciones-venta', selectedVentaId],
    queryFn: () => devolucionesService.getByVenta(selectedVentaId),
    enabled: !!selectedVentaId && showDetalle && esDevuelta,
    select: (data) => data.find((d) => d.estado === 'aprobada' || d.estado === 'completada') || data[0],
  });
  const devolucion = devolucionQuery.data || null;

  const detalleDevQuery = useQuery({
    queryKey: ['detalle-devolucion-venta', devolucion?.id],
    queryFn: () => detalleDevolucionesService.getByDevolucion(devolucion.id),
    enabled: !!devolucion?.id && esDevuelta,
  });
  const detalleDevolucion = detalleDevQuery.data || [];

  /* Anulación */
  const [anularTarget, setAnularTarget] = useState(null);
  const [razonAnulacion, setRazonAnulacion] = useState('');

  /* Si es cajero con alcance caja_asignada pero sin caja asignada -> bloquear */
  if (esSoloCaja && !cajaAsignada) {
    return <SinCajaAsignada titulo="Historial de Ventas" />;
  }

  /* Filtrar ventas */
  const filtered = useMemo(() => {
    let result = ventas;
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (v) =>
          (v.numeroVenta || '').toLowerCase().includes(q) ||
          (v.estado || '').toLowerCase().includes(q) ||
          (v.tipoComprobante || '').toLowerCase().includes(q),
      );
    }
    if (fechaDesde) {
      const desde = new Date(fechaDesde);
      result = result.filter((v) => new Date(v.creadoEn) >= desde);
    }
    if (fechaHasta) {
      const hasta = new Date(fechaHasta);
      hasta.setHours(23, 59, 59, 999);
      result = result.filter((v) => new Date(v.creadoEn) <= hasta);
    }
    if (estadoFiltro) {
      result = result.filter((v) => v.estado === estadoFiltro);
    }
    return result;
  }, [ventas, debouncedSearch, fechaDesde, fechaHasta, estadoFiltro]);


  /* Paginación del lado del cliente */
  const paginated = filtered.slice((page - 1) * pageSize, page * pageSize);

  /* Stats (basados en las ventas filtradas) */
  const totalCompletadas = filtered.filter((v) => v.estado === 'completada').length;
  const totalAnuladas = filtered.filter((v) => v.estado === 'anulada').length;
  const montoTotal = filtered
    .filter((v) => v.estado === 'completada')
    .reduce((acc, v) => acc + (v.total || 0), 0);

  /* Handlers */
  const handleVerDetalle = (ventaId) => {
    setSelectedVentaId(ventaId);
    setShowDetalle(true);
  };

  const handleAnular = async () => {
    if (!anularTarget) return;
    await anularVenta({
      ventaId: anularTarget.id,
      usuarioId: user?.id,
      razonCancelacion: razonAnulacion.trim() || 'Anulación manual desde historial',
    });
    setAnularTarget(null);
    setRazonAnulacion('');
    refetch();
  };

  /* Columnas */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '60px',
      render: (_, __, i) => (page - 1) * pageSize + i + 1,
    },
    {
      key: 'tipo',
      title: 'Tipo',
      render: (_, row) => {
        const info = TIPO_VENTA_MAP[row.tipoVenta] || { label: row.tipoVenta || '—', className: 'bg-gray-100 text-gray-600' };
        return (
          <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${info.className}`}>
            {info.label}
          </span>
        );
      },
    },
    {
      key: 'numero',
      title: 'N° Venta',
      dataIndex: 'numeroVenta',
    },
    {
      key: 'fecha',
      title: 'Fecha',
      render: (_, row) => formatDateTime(row.creadoEn),
    },
    {
      key: 'comprobante',
      title: 'Comprobante',
      render: (_, row) => (row.tipoComprobante || '—').replace(/_/g, ' '),
    },
    {
      key: 'total',
      title: 'Total',
      render: (_, row) => (
        <span className="font-semibold">{formatCurrency(row.total)}</span>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      render: (_, row) => {
        const info = ESTADO_MAP[row.estado] || { label: row.estado, variant: 'info' };
        return <Badge variant={info.variant}>{info.label}</Badge>;
      },
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '100px',
      align: 'center',
      render: (_, row) => {
        const puedeAnular = row.estado === 'completada' &&
          (esAdmin || row.sesionCaja?.estadoSesion === 'abierta');
        return (
          <div className="flex justify-center items-center gap-3">
            <button
              title="Ver detalle"
              onClick={() => handleVerDetalle(row.id)}
              className="text-blue-500 hover:text-blue-700"
            >
              <Eye size={16} />
            </button>
            {puedeAnular ? (
              <button
                title="Anular"
                onClick={() => setAnularTarget(row)}
                className="text-red-500 hover:text-red-700"
              >
                <Ban size={16} />
              </button>
            ) : (
              <span className="text-gray-300 opacity-40 cursor-default inline-flex">
                <Ban size={16} />
              </span>
            )}
          </div>
        );
      },
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Historial de Ventas</h1>
        <p className="text-gray-600 mt-1">
          Consulta y gestión de todas las ventas realizadas
        </p>
      </div>

      {/* Banner alcance restringido */}
      {esSoloCaja && cajaAsignada && (
        <div className="flex items-center gap-2 bg-amber-50 border border-amber-200 rounded-lg px-4 py-3">
          <Monitor size={16} className="text-amber-600 shrink-0" />
          <p className="text-sm text-amber-700">
            Mostrando solo las ventas del día de tu caja: <strong>{cajaAsignada.nombreCaja}</strong>
          </p>
        </div>
      )}

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard
          title="Ventas completadas"
          value={totalCompletadas}
          icon={Receipt}
        />
        <StatCard
          title="Monto total"
          value={formatCurrency(montoTotal)}
          icon={Banknote}
        />
        <StatCard
          title="Anuladas"
          value={totalAnuladas}
          icon={XCircle}
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-wrap items-center gap-3 mb-4">
          <div className="relative flex-1 min-w-[200px] max-w-sm">
            <Search
              size={16}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPage(1);
              }}
              placeholder="Buscar por N° venta, estado..."
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm
                         focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <select
            value={estadoFiltro}
            onChange={(e) => { setEstadoFiltro(e.target.value); setPage(1); }}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
          >
            <option value="">Todos los estados</option>
            <option value="completada">Completada</option>
            <option value="anulada">Anulada</option>
            <option value="pendiente">Pendiente</option>
            <option value="devuelta">Devuelta</option>
          </select>
          <div className="flex items-center gap-2">
            <Calendar size={16} className="text-gray-400" />
            <input
              type="date"
              value={fechaDesde}
              onChange={(e) => { setFechaDesde(e.target.value); setPage(1); }}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
            <span className="text-gray-400 text-sm">a</span>
            <input
              type="date"
              value={fechaHasta}
              onChange={(e) => { setFechaHasta(e.target.value); setPage(1); }}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button variant="outline" size="sm" onClick={handleRefresh} disabled={isRefreshing}>
            <RefreshCw size={14} className={`mr-1.5 ${isRefreshing ? 'animate-spin' : ''}`} />
            {isRefreshing ? 'Actualizando...' : 'Actualizar'}
          </Button>
        </div>

        <Table
          columns={columns}
          data={paginated}
          loading={isLoading}
          pagination={{
            current: page,
            pageSize,
            total: filtered.length,
            onChange: (newPage) => setPage(newPage),
          }}
        />
      </Card>

      {/* Modal Detalle */}
      <Modal
        isOpen={showDetalle}
        onClose={() => setShowDetalle(false)}
        title={`Detalle de Venta ${ventaDetail?.numeroVenta || ''}`}
        size="lg"
      >
        {loadingDetalle ? (
          <div className="flex justify-center py-8">
            <div className="w-6 h-6 border-2 border-green-500 border-t-transparent rounded-full animate-spin" />
          </div>
        ) : (
          <div className="space-y-5">
            {/* Info general */}
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="text-gray-500">Fecha:</span>{' '}
                <span className="font-medium">
                  {formatDateTime(ventaDetail?.creadoEn)}
                </span>
              </div>
              <div>
                <span className="text-gray-500">Estado:</span>{' '}
                <Badge
                  variant={
                    ESTADO_MAP[ventaDetail?.estado]?.variant || 'info'
                  }
                >
                  {ESTADO_MAP[ventaDetail?.estado]?.label || ventaDetail?.estado}
                </Badge>
              </div>
              <div>
                <span className="text-gray-500">Tipo de venta:</span>{' '}
                {(() => {
                  const info = TIPO_VENTA_MAP[ventaDetail?.tipoVenta] || { label: ventaDetail?.tipoVenta || '—', className: 'bg-gray-100 text-gray-600' };
                  return (
                    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${info.className}`}>
                      {info.label}
                    </span>
                  );
                })()}
              </div>
              <div>
                <span className="text-gray-500">Comprobante:</span>{' '}
                <span className="font-medium">
                  {(ventaDetail?.tipoComprobante || '—').replace(/_/g, ' ')}
                </span>
              </div>
            </div>

            {/* Razón de anulación */}
            {ventaDetail?.estado === 'anulada' && (
              <div className="rounded-lg border border-red-200 overflow-hidden">
                {/* Header */}
                <div className="flex items-center gap-2 bg-red-600 px-4 py-2.5">
                  <AlertTriangle size={15} className="text-white shrink-0" />
                  <span className="text-sm font-semibold text-white tracking-wide">Venta Anulada</span>
                </div>
                {/* Body */}
                <div className="bg-red-50 px-4 py-3">
                  <div className="flex items-start gap-3">
                    <div className="flex-1">
                      <p className="text-xs font-semibold text-red-500 uppercase tracking-wider mb-1">Motivo de anulación</p>
                      <p className="text-sm text-gray-800 font-medium">
                        {ventaDetail.razonCancelacion || <span className="text-gray-400 italic">Sin motivo especificado</span>}
                      </p>
                    </div>
                  </div>
                  {(ventaDetail.anuladoEn || ventaDetail.anuladoPor?.nombre) && (
                    <div className="mt-3 pt-3 border-t border-red-200 flex flex-wrap gap-4 text-xs text-red-600">
                      {ventaDetail.anuladoEn && (
                        <span>📅 {formatDateTime(ventaDetail.anuladoEn)}</span>
                      )}
                      {ventaDetail.anuladoPor?.nombre && (
                        <span>👤 {ventaDetail.anuladoPor.nombre}</span>
                      )}
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Detalle de ítems */}
            {(() => {
              const esParcial = esDevuelta && devolucion?.tipoDevolucion === 'parcial';
              const esTotal = esDevuelta && devolucion?.tipoDevolucion === 'total';

              /* Helper: renderiza la tabla de productos vendidos */
              const renderProductosVendidos = (titulo) => (
                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-2">{titulo}</h3>
                  <div className="border border-gray-200 rounded-lg overflow-hidden">
                    <table className="min-w-full divide-y divide-gray-200 text-sm">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">Producto</th>
                          <th className="px-3 py-2 text-center text-xs font-semibold text-gray-500">Cant.</th>
                          <th className="px-3 py-2 text-right text-xs font-semibold text-gray-500">P. Unit.</th>
                          <th className="px-3 py-2 text-right text-xs font-semibold text-gray-500">Total</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-100">
                        {(() => {
                          const comboGroups = {};
                          const standalone = [];
                          (detalle || []).forEach((d) => {
                            if (d.comboId) {
                              if (!comboGroups[d.comboId]) {
                                comboGroups[d.comboId] = { nombre: d.nombreCombo || `Combo #${d.comboId}`, items: [], totalCombo: 0 };
                              }
                              comboGroups[d.comboId].items.push(d);
                              comboGroups[d.comboId].totalCombo += Number(d.subtotal || 0);
                            } else {
                              standalone.push(d);
                            }
                          });
                          const rows = [];
                          Object.entries(comboGroups).forEach(([cId, group]) => {
                            rows.push(
                              <tr key={`combo-header-${cId}`} className="bg-purple-50">
                                <td className="px-3 py-2 font-semibold text-purple-700" colSpan={3}>
                                  <span className="flex items-center gap-1.5"><Gift size={14} className="text-purple-500" /> 📦 {group.nombre}</span>
                                </td>
                                <td className="px-3 py-2 text-right font-semibold text-purple-700">{formatCurrency(group.totalCombo)}</td>
                              </tr>,
                            );
                            group.items.forEach((d, i) =>
                              rows.push(
                                <tr key={`combo-${cId}-${i}`} className="bg-purple-50/50">
                                  <td className="px-3 py-1.5 pl-7 text-gray-500 text-xs">└ {d.nombreProducto || d.producto?.nombre || '—'}</td>
                                  <td className="px-3 py-1.5 text-center text-gray-500 text-xs">{d.cantidad}</td>
                                  <td className="px-3 py-1.5 text-right text-gray-500 text-xs">{formatCurrency(d.precioUnitario)}</td>
                                  <td className="px-3 py-1.5 text-right text-gray-500 text-xs"></td>
                                </tr>,
                              ),
                            );
                          });
                          standalone.forEach((d, i) =>
                            rows.push(
                              <tr key={`std-${i}`}>
                                <td className="px-3 py-2">{d.nombreProducto || d.producto?.nombre || '—'}</td>
                                <td className="px-3 py-2 text-center">{d.cantidad}</td>
                                <td className="px-3 py-2 text-right">{formatCurrency(d.precioUnitario)}</td>
                                <td className="px-3 py-2 text-right font-medium">{formatCurrency(d.subtotal)}</td>
                              </tr>,
                            ),
                          );
                          return rows;
                        })()}
                      </tbody>
                    </table>
                  </div>
                </div>
              );

              /* Helper: renderiza la tabla de productos devueltos */
              const renderProductosDevueltos = () => (
                <div>
                  <h3 className="text-sm font-semibold text-red-600 mb-2 flex items-center gap-1.5">
                    <RotateCcw size={14} />
                    Productos Devueltos
                    {devolucion?.numeroDevolucion && (
                      <span className="text-xs font-normal text-gray-400 ml-1">({devolucion.numeroDevolucion})</span>
                    )}
                  </h3>
                  {detalleDevQuery.isLoading ? (
                    <div className="flex items-center justify-center py-4">
                      <div className="w-5 h-5 border-2 border-red-400 border-t-transparent rounded-full animate-spin" />
                      <span className="ml-2 text-sm text-gray-500">Cargando...</span>
                    </div>
                  ) : detalleDevolucion.length === 0 ? (
                    <p className="text-sm text-gray-400 bg-gray-50 p-3 rounded-lg">No se encontraron productos devueltos</p>
                  ) : (
                    <div className="border border-red-200 rounded-lg overflow-hidden">
                      <table className="min-w-full divide-y divide-red-100 text-sm">
                        <thead className="bg-red-50">
                          <tr>
                            <th className="px-3 py-2 text-left text-xs font-semibold text-red-600">Producto</th>
                            <th className="px-3 py-2 text-center text-xs font-semibold text-red-600">Cant.</th>
                            <th className="px-3 py-2 text-right text-xs font-semibold text-red-600">P. Unit.</th>
                            <th className="px-3 py-2 text-right text-xs font-semibold text-red-600">Total</th>
                            <th className="px-3 py-2 text-center text-xs font-semibold text-red-600">Condición</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-red-50">
                          {detalleDevolucion.map((det, i) => (
                            <tr key={det.id || i}>
                              <td className="px-3 py-2">{det.producto?.nombre || det.nombreProducto || '—'}</td>
                              <td className="px-3 py-2 text-center">{det.cantidad}</td>
                              <td className="px-3 py-2 text-right">{formatCurrency(det.precioUnitario)}</td>
                              <td className="px-3 py-2 text-right font-medium">{formatCurrency(det.total)}</td>
                              <td className="px-3 py-2 text-center">
                                <Badge variant={det.estadoCondicion === 'bueno' ? 'success' : 'warning'}>
                                  {det.estadoCondicion || 'N/A'}
                                </Badge>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                        <tfoot className="bg-red-50 font-medium">
                          <tr>
                            <td colSpan={3} className="px-3 py-2 text-right text-red-700">Total devuelto:</td>
                            <td className="px-3 py-2 text-right text-red-700">
                              {formatCurrency(detalleDevolucion.reduce((s, d) => s + Number(d.total || 0), 0))}
                            </td>
                            <td></td>
                          </tr>
                        </tfoot>
                      </table>
                    </div>
                  )}
                </div>
              );

              /* Renderizado condicional */
              if (esTotal) {
                /* Devolución total: solo tabla de devueltos */
                return renderProductosDevueltos();
              }

              if (esParcial) {
                /* Devolución parcial: tabla de vendidos + tabla de devueltos */
                return (
                  <>
                    {renderProductosVendidos('Productos Vendidos')}
                    {renderProductosDevueltos()}
                  </>
                );
              }

              /* Venta normal (no devuelta): tabla de productos original */
              return renderProductosVendidos('Productos');
            })()}

            {/* Subtotales */}
            <div className="flex justify-end mt-2 pr-1">
              <div className="w-56 space-y-1 text-sm">
                {parseFloat(ventaDetail?.subtotal || 0) > 0 && (
                  <div className="flex justify-between text-gray-500">
                    <span>Subtotal</span>
                    <span>{formatCurrency(ventaDetail.subtotal)}</span>
                  </div>
                )}
                {parseFloat(ventaDetail?.montoDescuento || 0) > 0 && (
                  <div className="flex justify-between text-red-500">
                    <span>Descuento</span>
                    <span>-{formatCurrency(ventaDetail.montoDescuento)}</span>
                  </div>
                )}
                {parseFloat(ventaDetail?.costoEnvio || 0) > 0 && (
                  <div className="flex justify-between text-gray-500">
                    <span>Delivery</span>
                    <span>{formatCurrency(ventaDetail.costoEnvio)}</span>
                  </div>
                )}
                {parseFloat(ventaDetail?.montoImpuesto || 0) > 0 && (
                  <div className="flex justify-between text-gray-500">
                    <span>IGV (18%)</span>
                    <span>{formatCurrency(ventaDetail.montoImpuesto)}</span>
                  </div>
                )}
                <div className="flex justify-between font-bold text-gray-900 border-t pt-1">
                  <span>Total</span>
                  <span className="text-green-700">{formatCurrency(ventaDetail?.total)}</span>
                </div>
              </div>
            </div>

            {/* Pagos */}
            <div>
              <h3 className="text-sm font-semibold text-gray-700 mb-2">
                Pagos
              </h3>
              <div className="space-y-1">
                {pagos.map((p, i) => (
                  <div
                    key={i}
                    className="flex justify-between text-sm bg-gray-50 px-3 py-2 rounded"
                  >
                    <span className="text-gray-600">
                      {p.metodoPagoNombre ||
                        p.metodoPago?.nombre ||
                        p.metodoPago?.tipoMetodoPago?.replace(/_/g, ' ') ||
                        'Método'}
                    </span>
                    <span className="font-medium">
                      {formatCurrency(p.monto)}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </Modal>

      {/* Modal anulación con razón */}
      <Modal
        isOpen={!!anularTarget}
        onClose={() => { setAnularTarget(null); setRazonAnulacion(''); }}
        title="Anular Venta"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            {'¿Está seguro de anular la venta '}
            <strong>{anularTarget?.numeroVenta}</strong>
            {'? Se revertirá el stock.'}
          </p>

          {/* Advertencia según estado de la sesión de caja */}
          {anularTarget?.sesionCaja?.estadoSesion === 'abierta' ? (
            <div className="bg-green-50 border border-green-200 rounded-lg px-3 py-2">
              <p className="text-xs text-green-700">
                La caja está abierta. Si la venta tiene pagos en efectivo, se registrará un egreso de caja automáticamente.
              </p>
            </div>
          ) : (
            <div className="bg-amber-50 border border-amber-200 rounded-lg px-3 py-2">
              <p className="text-xs text-amber-700">
                La caja asociada a esta venta ya fue cerrada. La anulación se registrará contablemente pero <strong>no se generará movimiento de caja</strong>. La devolución de dinero deberá realizarse manualmente.
              </p>
            </div>
          )}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Razón de anulación <span className="text-red-500">*</span>
            </label>
            <textarea
              value={razonAnulacion}
              onChange={(e) => setRazonAnulacion(e.target.value)}
              placeholder="Describa el motivo de la anulación..."
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => { setAnularTarget(null); setRazonAnulacion(''); }}>
              Cancelar
            </Button>
            <Button
              className="bg-red-600 hover:bg-red-700 text-white disabled:opacity-50 disabled:cursor-not-allowed"
              onClick={handleAnular}
              disabled={isAnulando || razonAnulacion.trim() === ''}
            >
              {isAnulando ? 'Anulando...' : 'Anular Venta'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
