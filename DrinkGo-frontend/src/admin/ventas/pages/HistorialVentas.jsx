/**
 * HistorialVentas.jsx
 * ───────────────────
 * Historial de ventas del negocio con filtro, detalle y anulación.
 */
import { useState, useMemo } from 'react';
import { Eye, Ban, Search, Receipt, DollarSign, XCircle } from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { Modal } from '@/admin/components/ui/Modal';
import { StatCard } from '@/admin/components/ui/StatCard';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { useVentas, useVentaDetalle, useAnularVenta } from '../hooks/useVentas';
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
  const { user } = useAdminAuthStore();
  const { ventas, isLoading, refetch } = useVentas();
  const { anularVenta, isAnulando } = useAnularVenta();

  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [page, setPage] = useState(1);
  const pageSize = 10;

  /* Detalle modal */
  const [selectedVentaId, setSelectedVentaId] = useState(null);
  const [showDetalle, setShowDetalle] = useState(false);
  const { venta: ventaDetail, detalle, pagos, isLoading: loadingDetalle } =
    useVentaDetalle(showDetalle ? selectedVentaId : null);

  /* Anulación */
  const [anularTarget, setAnularTarget] = useState(null);

  /* Filtrar ventas */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return ventas;
    const q = debouncedSearch.toLowerCase();
    return ventas.filter(
      (v) =>
        (v.numeroVenta || '').toLowerCase().includes(q) ||
        (v.estado || '').toLowerCase().includes(q) ||
        (v.tipoComprobante || '').toLowerCase().includes(q),
    );
  }, [ventas, debouncedSearch]);

  /* Paginación del lado del cliente */
  const paginated = filtered.slice((page - 1) * pageSize, page * pageSize);

  /* Stats */
  const totalCompletadas = ventas.filter((v) => v.estado === 'completada').length;
  const totalAnuladas = ventas.filter((v) => v.estado === 'anulada').length;
  const montoTotal = ventas
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
      razonCancelacion: 'Anulación manual desde historial',
    });
    setAnularTarget(null);
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
          icon={DollarSign}
        />
        <StatCard
          title="Anuladas"
          value={totalAnuladas}
          icon={XCircle}
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex justify-between items-center mb-4">
          <div className="relative w-96">
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
                    {detalle.map((d, i) => (
                      <tr key={i}>
                        <td className="px-3 py-2">
                          {d.producto?.nombre || '—'}
                        </td>
                        <td className="px-3 py-2 text-center">{d.cantidad}</td>
                        <td className="px-3 py-2 text-right">
                          {formatCurrency(d.precioUnitario)}
                        </td>
                        <td className="px-3 py-2 text-right font-medium">
                          {formatCurrency(d.total || d.subtotal)}
                        </td>
                      </tr>
                    ))}
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
                      {p.metodoPago?.nombre ||
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

      {/* Confirmar anulación */}
      <ConfirmDialog
        isOpen={!!anularTarget}
        onClose={() => setAnularTarget(null)}
        onConfirm={handleAnular}
        title="Anular Venta"
        message={`¿Está seguro de anular la venta ${anularTarget?.numeroVenta}? Se revertirá el stock y se registrará un egreso en caja.`}
        confirmText="Anular Venta"
        isLoading={isAnulando}
      />
    </div>
  );
};
