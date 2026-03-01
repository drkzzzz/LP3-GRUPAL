/**
 * SesionDetalleModal.jsx
 * ──────────────────────
 * Modal de detalle de una sesión de caja (abierta o cerrada).
 * Muestra resumen financiero completo y tabla de movimientos.
 * Usado desde el Historial de Cajas para auditoría.
 */
import { useMemo } from 'react';
import {
  Wallet,
  TrendingUp,
  TrendingDown,
  DollarSign,
  CreditCard,
  ShoppingCart,
  ArrowDownUp,
  Clock,
  User,
  AlertTriangle,
  CheckCircle,
  XCircle,
} from 'lucide-react';
import { Modal } from '@/admin/components/ui/Modal';
import { Card } from '@/admin/components/ui/Card';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { StatCard } from '@/admin/components/ui/StatCard';
import { useResumenTurno, useMovimientos } from '../hooks/useCajas';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';

/* Mapa de tipo → etiqueta + variante + signo */
const TIPO_CONFIG = {
  ingreso_venta: { label: 'Venta', variant: 'success', sign: '+' },
  ingreso_otro: { label: 'Ingreso manual', variant: 'info', sign: '+' },
  egreso_gasto: { label: 'Gasto', variant: 'error', sign: '-' },
  egreso_otro: { label: 'Egreso otro', variant: 'warning', sign: '-' },
  apertura: { label: 'Apertura', variant: 'info', sign: '' },
  cierre: { label: 'Cierre', variant: 'info', sign: '' },
};

const ESTADO_CONFIG = {
  abierta: { label: 'Abierta', variant: 'info', icon: Clock },
  cerrada: { label: 'Cerrada', variant: 'success', icon: CheckCircle },
  con_diferencia: { label: 'Con diferencia', variant: 'warning', icon: AlertTriangle },
};

export const SesionDetalleModal = ({ isOpen, onClose, sesion }) => {
  const sesionId = sesion?.id;
  const { resumen, isLoading: loadingResumen } = useResumenTurno(sesionId);
  const { movimientos, isLoading: loadingMov } = useMovimientos(sesionId);

  /* ─── Cálculos del lado cliente como fallback ─── */
  const totalIngresos = useMemo(() => {
    return movimientos
      .filter((m) => m.tipoMovimiento?.startsWith('ingreso'))
      .reduce((sum, m) => sum + (m.monto || 0), 0);
  }, [movimientos]);

  const totalEgresos = useMemo(() => {
    return movimientos
      .filter((m) => m.tipoMovimiento?.startsWith('egreso'))
      .reduce((sum, m) => sum + (m.monto || 0), 0);
  }, [movimientos]);

  const montoApertura = sesion?.montoApertura ?? resumen?.sesion?.montoApertura ?? 0;
  const montoCierre = sesion?.montoCierre ?? 0;
  const saldoTeorico = montoApertura + totalIngresos - totalEgresos;
  const diferencia = sesion?.diferenciaEsperadoReal ?? (montoCierre - saldoTeorico);
  const estadoSesion = sesion?.estadoSesion || 'abierta';
  const estadoInfo = ESTADO_CONFIG[estadoSesion] || ESTADO_CONFIG.abierta;
  const EstadoIcon = estadoInfo.icon;
  const esCerrada = estadoSesion !== 'abierta';

  /* ─── Columnas de movimientos ─── */
  const columns = [
    { key: 'index', title: '#', width: '50px', render: (_, __, i) => i + 1 },
    {
      key: 'fecha',
      title: 'Fecha / Hora',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{formatDateTime(row.fechaMovimiento)}</span>
      ),
    },
    {
      key: 'tipo',
      title: 'Tipo',
      render: (_, row) => {
        const config = TIPO_CONFIG[row.tipoMovimiento] || { label: row.tipoMovimiento, variant: 'info' };
        return <Badge variant={config.variant}>{config.label}</Badge>;
      },
    },
    {
      key: 'categoria',
      title: 'Categoría',
      render: (_, row) => {
        if (row.categoriaGasto?.nombre) return row.categoriaGasto.nombre;
        if (row.tipoMovimiento === 'ingreso_venta') return 'Venta POS';
        return '—';
      },
    },
    {
      key: 'descripcion',
      title: 'Descripción',
      render: (_, row) => (
        <span className="text-sm text-gray-700 truncate max-w-[200px] block" title={row.descripcion}>
          {row.descripcion || '—'}
        </span>
      ),
    },
    {
      key: 'monto',
      title: 'Monto',
      align: 'right',
      render: (_, row) => {
        const config = TIPO_CONFIG[row.tipoMovimiento] || { sign: '' };
        const isNeg = config.sign === '-';
        const isPos = config.sign === '+';
        return (
          <span className={`font-semibold ${isNeg ? 'text-red-600' : isPos ? 'text-green-600' : 'text-gray-700'}`}>
            {config.sign}{formatCurrency(row.monto)}
          </span>
        );
      },
    },
  ];

  if (!sesion) return null;

  const nombreCaja = sesion.caja?.nombreCaja || sesion.cajaNombre || '—';
  const nombreUsuario = sesion.usuario
    ? `${sesion.usuario.nombres || ''} ${sesion.usuario.apellidos || ''}`.trim()
    : '—';

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Detalle de Sesión de Caja" size="xl">
      <div className="space-y-5">
        {/* ─── Header con info de sesión ─── */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 bg-gray-50 rounded-lg p-4 border border-gray-200">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <h3 className="text-lg font-semibold text-gray-900">{nombreCaja}</h3>
              <Badge variant={estadoInfo.variant}>
                <EstadoIcon size={12} className="mr-1" />
                {estadoInfo.label}
              </Badge>
            </div>
            <div className="flex items-center gap-4 text-sm text-gray-500">
              <span className="flex items-center gap-1">
                <User size={14} />
                {nombreUsuario}
              </span>
              <span className="flex items-center gap-1">
                <Clock size={14} />
                {formatDateTime(sesion.fechaApertura)}
                {sesion.fechaCierre && ` → ${formatDateTime(sesion.fechaCierre)}`}
              </span>
            </div>
          </div>
          <div className="text-right">
            <p className="text-xs text-gray-400">Movimientos totales</p>
            <p className="text-xl font-bold text-gray-900">{movimientos.length}</p>
          </div>
        </div>

        {/* ─── Resumen financiero ─── */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
          <StatCard title="Monto Apertura" value={formatCurrency(montoApertura)} icon={Wallet} />
          <StatCard
            title="Total Ingresos"
            value={formatCurrency(totalIngresos)}
            icon={TrendingUp}
            className="!border-green-200"
          />
          <StatCard
            title="Total Egresos"
            value={formatCurrency(totalEgresos)}
            icon={TrendingDown}
            className="!border-red-200"
          />
          <StatCard
            title="Saldo Teórico"
            value={formatCurrency(saldoTeorico)}
            icon={DollarSign}
            className="!border-blue-200"
          />
        </div>

        {/* ─── Si está cerrada: datos de cierre ─── */}
        {esCerrada && (
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <Card className="!p-3 text-center">
              <p className="text-xs text-gray-500 mb-1">Monto Contado (cierre)</p>
              <p className="text-lg font-bold text-gray-900">{formatCurrency(montoCierre)}</p>
            </Card>
            <Card className="!p-3 text-center">
              <p className="text-xs text-gray-500 mb-1">Saldo Teórico (esperado)</p>
              <p className="text-lg font-bold text-gray-900">{formatCurrency(saldoTeorico)}</p>
            </Card>
            <Card className={`!p-3 text-center ${diferencia !== 0 ? '!border-amber-300 !bg-amber-50' : '!border-green-300 !bg-green-50'}`}>
              <p className="text-xs text-gray-500 mb-1">Diferencia</p>
              <p className={`text-lg font-bold ${diferencia > 0 ? 'text-green-600' : diferencia < 0 ? 'text-red-600' : 'text-gray-900'}`}>
                {diferencia > 0 ? '+' : ''}{formatCurrency(diferencia)}
              </p>
              {diferencia !== 0 && (
                <p className="text-xs text-amber-600 mt-0.5">
                  {diferencia > 0 ? 'Sobrante' : 'Faltante'}
                </p>
              )}
            </Card>
          </div>
        )}

        {/* ─── Desglose por medio de pago (del resumen del backend) ─── */}
        {resumen && (
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <ShoppingCart size={14} className="text-green-500" />
                <span className="text-xs font-medium text-gray-500">Ventas</span>
              </div>
              <p className="text-lg font-bold text-gray-900">
                {formatCurrency(resumen.totalVentas ?? 0)}
              </p>
              <p className="text-xs text-gray-400">{resumen.totalVentasCompletadas ?? 0} completadas</p>
            </Card>
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <DollarSign size={14} className="text-green-600" />
                <span className="text-xs font-medium text-gray-500">Efectivo</span>
              </div>
              <p className="text-lg font-bold text-gray-900">
                {formatCurrency(resumen.totalEfectivo ?? 0)}
              </p>
            </Card>
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <CreditCard size={14} className="text-blue-500" />
                <span className="text-xs font-medium text-gray-500">Tarjeta</span>
              </div>
              <p className="text-lg font-bold text-gray-900">
                {formatCurrency(resumen.totalTarjeta ?? 0)}
              </p>
            </Card>
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <ArrowDownUp size={14} className="text-purple-500" />
                <span className="text-xs font-medium text-gray-500">Otros medios</span>
              </div>
              <p className="text-lg font-bold text-gray-900">
                {formatCurrency((resumen.totalYape ?? 0) + (resumen.totalPlin ?? 0) + (resumen.totalOtros ?? 0))}
              </p>
            </Card>
          </div>
        )}

        {/* ─── Observaciones ─── */}
        {sesion.observaciones && (
          <Card className="!p-3 !bg-yellow-50 !border-yellow-200">
            <p className="text-xs text-yellow-600 font-medium mb-1">Observaciones de cierre</p>
            <p className="text-sm text-gray-700">{sesion.observaciones}</p>
          </Card>
        )}

        {/* ─── Tabla de movimientos ─── */}
        <div>
          <h4 className="text-sm font-semibold text-gray-700 mb-2">
            Movimientos ({movimientos.length})
          </h4>
          <Table
            columns={columns}
            data={movimientos}
            loading={loadingMov || loadingResumen}
            pagination={{
              current: 1,
              pageSize: 50,
              total: movimientos.length,
            }}
          />
        </div>
      </div>
    </Modal>
  );
};
