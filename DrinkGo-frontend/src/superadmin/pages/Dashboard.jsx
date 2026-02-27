import {
  Building2,
  CreditCard,
  FileText,
  TrendingUp,
  Users,
  AlertCircle,
  CheckCircle,
  XCircle,
  DollarSign,
} from 'lucide-react';
import { useDashboard } from '../hooks/useDashboard';
import { StatCard } from '../components/ui/StatCard';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';

const ESTADO_COLORS = {
  activo: 'success',
  pendiente: 'warning',
  suspendido: 'warning',
  cancelado: 'error',
};

const FACTURA_COLORS = {
  pagada: 'success',
  pendiente: 'warning',
  fallida: 'error',
  borrador: 'info',
  anulada: 'error',
  reembolsada: 'warning',
};

/* ---------- Mini KPI badge ---------- */
const KpiBadge = ({ label, value, color }) => {
  const colors = {
    green: 'bg-green-50 text-green-700 border-green-200',
    yellow: 'bg-yellow-50 text-yellow-700 border-yellow-200',
    red: 'bg-red-50 text-red-700 border-red-200',
    blue: 'bg-blue-50 text-blue-700 border-blue-200',
    gray: 'bg-gray-50 text-gray-600 border-gray-200',
  };
  return (
    <div className={`flex flex-col items-center px-4 py-3 rounded-lg border ${colors[color]}`}>
      <span className="text-xs font-medium mb-0.5">{label}</span>
      <span className="text-xl font-bold">{value}</span>
    </div>
  );
};

export const Dashboard = () => {
  const {
    isLoading,
    totalNegocios,
    negociosActivos,
    negociosPendientes,
    negociosSuspendidos,
    ingresosMensuales,
    suscripcionesActivas,
    facturasPendientes,
    totalFacturas,
    facturasPagadas,
    recentNegocios,
    recentFacturas,
  } = useDashboard();

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-1">Resumen de la plataforma DrinkGo</p>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div
              key={i}
              className="bg-white rounded-lg border border-gray-200 shadow-sm p-6 animate-pulse"
            >
              <div className="h-4 bg-gray-200 rounded w-1/2 mb-3" />
              <div className="h-8 bg-gray-200 rounded w-1/3" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  const negociosCancelados = Math.max(
    0,
    totalNegocios - negociosActivos - negociosPendientes - negociosSuspendidos,
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-1">Resumen de la plataforma DrinkGo</p>
      </div>

      {/* Stats Row */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Negocios Activos"
          value={negociosActivos}
          icon={Building2}
        />
        <StatCard
          title="Suscripciones Activas"
          value={suscripcionesActivas}
          icon={Users}
        />
        <StatCard
          title="Ingresos Totales"
          value={formatCurrency(ingresosMensuales)}
          icon={TrendingUp}
        />
        <StatCard
          title="Facturas Pendientes"
          value={facturasPendientes}
          icon={CreditCard}
        />
      </div>

      {/* Estado de negocios (KPI row) */}
      <Card>
        <h2 className="text-sm font-semibold text-gray-700 mb-3 uppercase tracking-wide">
          Distribución de Negocios
        </h2>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <KpiBadge label="Activos" value={negociosActivos} color="green" />
          <KpiBadge label="Pendientes" value={negociosPendientes} color="yellow" />
          <KpiBadge label="Suspendidos" value={negociosSuspendidos} color="red" />
          <KpiBadge label="Cancelados" value={negociosCancelados} color="gray" />
        </div>
        {totalNegocios > 0 && (
          <div className="mt-3 h-2.5 bg-gray-100 rounded-full overflow-hidden flex">
            <div
              className="bg-green-500 h-full"
              style={{ width: `${(negociosActivos / totalNegocios) * 100}%` }}
            />
            <div
              className="bg-yellow-400 h-full"
              style={{ width: `${(negociosPendientes / totalNegocios) * 100}%` }}
            />
            <div
              className="bg-red-400 h-full"
              style={{ width: `${(negociosSuspendidos / totalNegocios) * 100}%` }}
            />
            <div
              className="bg-gray-300 h-full"
              style={{ width: `${(negociosCancelados / totalNegocios) * 100}%` }}
            />
          </div>
        )}
      </Card>

      {/* Facturación summary */}
      <Card>
        <h2 className="text-sm font-semibold text-gray-700 mb-3 uppercase tracking-wide">
          Resumen de Facturación
        </h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          <div className="flex items-center gap-3 bg-green-50 rounded-lg p-3">
            <CheckCircle size={20} className="text-green-600 shrink-0" />
            <div>
              <p className="text-xs text-gray-500">Pagadas</p>
              <p className="text-lg font-bold text-green-600">{facturasPagadas}</p>
            </div>
          </div>
          <div className="flex items-center gap-3 bg-yellow-50 rounded-lg p-3">
            <FileText size={20} className="text-yellow-600 shrink-0" />
            <div>
              <p className="text-xs text-gray-500">Pendientes</p>
              <p className="text-lg font-bold text-yellow-600">{facturasPendientes}</p>
            </div>
          </div>
          <div className="flex items-center gap-3 bg-blue-50 rounded-lg p-3 col-span-2 sm:col-span-1">
            <DollarSign size={20} className="text-blue-600 shrink-0" />
            <div>
              <p className="text-xs text-gray-500">Ingresos cobrados</p>
              <p className="text-lg font-bold text-blue-600">{formatCurrency(ingresosMensuales)}</p>
            </div>
          </div>
        </div>
      </Card>

      {/* Alert cards */}
      {(negociosPendientes > 0 || negociosSuspendidos > 0) && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {negociosPendientes > 0 && (
            <div className="flex items-center gap-3 bg-yellow-50 border border-yellow-200 rounded-lg p-4">
              <AlertCircle size={20} className="text-yellow-600 flex-shrink-0" />
              <p className="text-sm text-yellow-800">
                <span className="font-medium">{negociosPendientes}</span> negocio(s) pendiente(s) de aprobación
              </p>
            </div>
          )}
          {negociosSuspendidos > 0 && (
            <div className="flex items-center gap-3 bg-red-50 border border-red-200 rounded-lg p-4">
              <AlertCircle size={20} className="text-red-600 flex-shrink-0" />
              <p className="text-sm text-red-800">
                <span className="font-medium">{negociosSuspendidos}</span> negocio(s) suspendido(s)
              </p>
            </div>
          )}
        </div>
      )}

      {/* Tables row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Negocios */}
        <Card>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Negocios Recientes
          </h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-500 uppercase">
                    Negocio
                  </th>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-500 uppercase">
                    Estado
                  </th>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-500 uppercase">
                    Fecha
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {recentNegocios.length === 0 ? (
                  <tr>
                    <td colSpan={3} className="px-4 py-4 text-center text-sm text-gray-500">
                      Sin negocios registrados
                    </td>
                  </tr>
                ) : (
                  recentNegocios.map((n) => (
                    <tr key={n.id} className="hover:bg-gray-50">
                      <td className="px-4 py-2 text-sm text-gray-900">
                        {n.razonSocial}
                      </td>
                      <td className="px-4 py-2">
                        <Badge variant={ESTADO_COLORS[n.estado] || 'info'}>
                          {n.estado?.toUpperCase()}
                        </Badge>
                      </td>
                      <td className="px-4 py-2 text-sm text-gray-500">
                        {formatDate(n.creadoEn)}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>

        {/* Recent Facturas */}
        <Card>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Facturas Recientes
          </h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-500 uppercase">
                    Nro. Factura
                  </th>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-500 uppercase">
                    Total
                  </th>
                  <th className="px-4 py-2 text-left text-xs font-semibold text-gray-500 uppercase">
                    Estado
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {recentFacturas.length === 0 ? (
                  <tr>
                    <td colSpan={3} className="px-4 py-4 text-center text-sm text-gray-500">
                      Sin facturas registradas
                    </td>
                  </tr>
                ) : (
                  recentFacturas.map((f) => (
                    <tr key={f.id} className="hover:bg-gray-50">
                      <td className="px-4 py-2 text-sm text-gray-900">
                        {f.numeroFactura}
                      </td>
                      <td className="px-4 py-2 text-sm text-gray-900">
                        {formatCurrency(f.total)}
                      </td>
                      <td className="px-4 py-2">
                        <Badge variant={FACTURA_COLORS[f.estado] || 'info'}>
                          {f.estado?.toUpperCase()}
                        </Badge>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      </div>
    </div>
  );
};

