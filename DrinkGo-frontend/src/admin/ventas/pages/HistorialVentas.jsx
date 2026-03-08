/**
 * HistorialVentas.jsx
 * ───────────────────
 * Historial de ventas del negocio con filtro, detalle y anulación.
 */
import { useState, useMemo } from 'react';
import { Eye, Ban, Search, Receipt, Banknote, XCircle, Monitor, Gift, Calendar } from 'lucide-react';
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

const ESTADO_MAP = {
  completada: { label: 'Completada', variant: 'success' },
  pendiente: { label: 'Pendiente', variant: 'warning' },
  anulada: { label: 'Anulada', variant: 'error' },
  cancelada: { label: 'Cancelada', variant: 'error' },
  reembolsada: { label: 'Reembolsada', variant: 'info' },
  parcialmente_pagada: { label: 'Parcial', variant: 'warning' },
};

export const HistorialVentas = () => {
  const { user, cajaAsignada, getAlcance } = useAdminAuthStore();
  const { ventas, isLoading, refetch, alcanceHistorial } = useVentas();
  const { anularVenta, isAnulando } = useAnularVenta();
  const esSoloCaja = alcanceHistorial === 'caja_asignada';
  const esAdmin = getAlcance('m.ventas') === 'completo';

  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [page, setPage] = useState(1);
  const pageSize = 10;

  /* Filtros de fecha */
  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');

  /* Detalle modal */
  const [selectedVentaId, setSelectedVentaId] = useState(null);
  const [showDetalle, setShowDetalle] = useState(false);
  const { venta: ventaDetail, detalle, pagos, isLoading: loadingDetalle } =
    useVentaDetalle(showDetalle ? selectedVentaId : null);

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
    return result;
  }, [ventas, debouncedSearch, fechaDesde, fechaHasta]);

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
      render: (_, row) => (
        <div className="flex justify-center gap-2">
          <button
            title="Ver detalle"
            onClick={() => handleVerDetalle(row.id)}
            className="text-blue-500 hover:text-blue-700"
          >
            <Eye size={16} />
          </button>
          {row.estado === 'completada' && (
            // Admin: puede anular cualquier venta
            // Cajero: solo si la sesión de caja sigue abierta
            esAdmin || row.sesionCaja?.estadoSesion === 'abierta'
          ) && (
            <button
              title="Anular"
              onClick={() => setAnularTarget(row)}
              className="text-red-500 hover:text-red-700"
            >
              <Ban size={16} />
            </button>
          )}
        </div>
      ),
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
          <Button variant="outline" size="sm" onClick={refetch}>
            Actualizar
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
                <span className="text-gray-500">Comprobante:</span>{' '}
                <span className="font-medium">
                  {(ventaDetail?.tipoComprobante || '—').replace(/_/g, ' ')}
                </span>
              </div>
              <div>
                <span className="text-gray-500">Total:</span>{' '}
                <span className="font-bold text-green-700">
                  {formatCurrency(ventaDetail?.total)}
                </span>
              </div>
            </div>

            {/* Detalle de ítems */}
            <div>
              <h3 className="text-sm font-semibold text-gray-700 mb-2">
                Productos
              </h3>
              <div className="border border-gray-200 rounded-lg overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200 text-sm">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">
                        Producto
                      </th>
                      <th className="px-3 py-2 text-center text-xs font-semibold text-gray-500">
                        Cant.
                      </th>
                      <th className="px-3 py-2 text-right text-xs font-semibold text-gray-500">
                        P. Unit.
                      </th>
                      <th className="px-3 py-2 text-right text-xs font-semibold text-gray-500">
                        Total
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {(() => {
                      /* Agrupar items que pertenecen a un combo */
                      const comboGroups = {};
                      const standalone = [];

                      (detalle || []).forEach((d) => {
                        if (d.comboId) {
                          if (!comboGroups[d.comboId]) {
                            comboGroups[d.comboId] = {
                              nombre: d.nombreCombo || `Combo #${d.comboId}`,
                              items: [],
                              totalCombo: 0,
                            };
                          }
                          comboGroups[d.comboId].items.push(d);
                          comboGroups[d.comboId].totalCombo += Number(d.total || d.subtotal || 0);
                        } else {
                          standalone.push(d);
                        }
                      });

                      const rows = [];

                      /* Renderizar combos agrupados */
                      Object.entries(comboGroups).forEach(([cId, group]) => {
                        rows.push(
                          <tr key={`combo-header-${cId}`} className="bg-purple-50">
                            <td className="px-3 py-2 font-semibold text-purple-700" colSpan={3}>
                              <span className="flex items-center gap-1.5">
                                <Gift size={14} className="text-purple-500" />
                                📦 {group.nombre}
                              </span>
                            </td>
                            <td className="px-3 py-2 text-right font-semibold text-purple-700">
                              {formatCurrency(group.totalCombo)}
                            </td>
                          </tr>,
                        );
                        group.items.forEach((d, i) =>
                          rows.push(
                            <tr key={`combo-${cId}-${i}`} className="bg-purple-50/50">
                              <td className="px-3 py-1.5 pl-7 text-gray-500 text-xs">
                                └ {d.nombreProducto || d.producto?.nombre || '—'}
                              </td>
                              <td className="px-3 py-1.5 text-center text-gray-500 text-xs">{d.cantidad}</td>
                              <td className="px-3 py-1.5 text-right text-gray-500 text-xs">
                                {formatCurrency(d.precioUnitario)}
                              </td>
                              <td className="px-3 py-1.5 text-right text-gray-500 text-xs">
                                {formatCurrency(d.total || d.subtotal)}
                              </td>
                            </tr>,
                          ),
                        );
                      });

                      /* Renderizar productos sueltos */
                      standalone.forEach((d, i) =>
                        rows.push(
                          <tr key={`std-${i}`}>
                            <td className="px-3 py-2">
                              {d.nombreProducto || d.producto?.nombre || '—'}
                            </td>
                            <td className="px-3 py-2 text-center">{d.cantidad}</td>
                            <td className="px-3 py-2 text-right">
                              {formatCurrency(d.precioUnitario)}
                            </td>
                            <td className="px-3 py-2 text-right font-medium">
                              {formatCurrency(d.total || d.subtotal)}
                            </td>
                          </tr>,
                        ),
                      );

                      return rows;
                    })()}
                  </tbody>
                </table>
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
            {'? Se revertirá el stock y se registrará un egreso en caja.'}
          </p>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Razón de anulación
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
              className="bg-red-600 hover:bg-red-700 text-white"
              onClick={handleAnular}
              disabled={isAnulando}
            >
              {isAnulando ? 'Anulando...' : 'Anular Venta'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
