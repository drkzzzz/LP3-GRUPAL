/**
 * Reportes — RF-RPL-001
 * Dashboard ejecutivo de reportes consolidados de la plataforma.
 * Métricas: MRR, crecimiento de negocios, distribución por plan,
 * estado de facturas, y resumen de suscripciones.
 */
import { useCallback } from 'react';
import {
  TrendingUp,
  Building2,
  CreditCard,
  DollarSign,
  Users,
  FileText,
  BarChart3,
  Download,
  AlertCircle,
} from 'lucide-react';
import { useDashboard } from '../hooks/useDashboard';
import { Card } from '../components/ui/Card';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';

/* ---------- CSV export helper ---------- */
const buildCSV = (sections) => {
  const rows = [];
  sections.forEach(({ title, headers, data }) => {
    rows.push([title]);
    rows.push(headers);
    data.forEach((row) => rows.push(row));
    rows.push([]);
  });
  return rows
    .map((r) =>
      r
        .map((cell) => {
          const s = cell === null || cell === undefined ? '' : String(cell);
          return s.includes(',') || s.includes('"') || s.includes('\n')
            ? `"${s.replace(/"/g, '""')}"`
            : s;
        })
        .join(',')
    )
    .join('\n');
};

const downloadCSV = (content, filename) => {
  const bom = '\uFEFF'; // UTF-8 BOM para Excel
  const blob = new Blob([bom + content], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
};

/* ---------- Mini stat card para reportes ---------- */
const MetricCard = ({ label, value, sub, icon: Icon, color = 'blue' }) => {
  const COLOR_MAP = {
    blue: 'bg-blue-50 text-blue-600',
    green: 'bg-green-50 text-green-600',
    yellow: 'bg-yellow-50 text-yellow-600',
    red: 'bg-red-50 text-red-600',
    purple: 'bg-purple-50 text-purple-600',
  };
  return (
    <div className="bg-white border border-gray-200 rounded-lg p-5">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-gray-500">{label}</p>
          <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
          {sub && <p className="text-xs text-gray-400 mt-0.5">{sub}</p>}
        </div>
        <div className={`p-2.5 rounded-lg ${COLOR_MAP[color]}`}>
          <Icon size={20} />
        </div>
      </div>
    </div>
  );
};

/* ---------- Barra horizontal de porcentaje ---------- */
const BarRow = ({ label, count, total, color = 'bg-blue-500' }) => {
  const pct = total > 0 ? Math.round((count / total) * 100) : 0;
  return (
    <div className="flex items-center gap-3">
      <span className="text-sm text-gray-600 w-28 truncate shrink-0">{label}</span>
      <div className="flex-1 h-2.5 bg-gray-100 rounded-full overflow-hidden">
        <div
          className={`h-full rounded-full transition-all ${color}`}
          style={{ width: `${pct}%` }}
        />
      </div>
      <span className="text-sm font-medium text-gray-700 w-10 text-right">{count}</span>
      <span className="text-xs text-gray-400 w-10 text-right">{pct}%</span>
    </div>
  );
};

/* ---------- Layout placeholder cuando no hay datos ---------- */
const EmptyState = ({ message }) => (
  <div className="py-8 text-center text-sm text-gray-400">{message}</div>
);

export const Reportes = () => {
  const {
    isLoading,
    isError,
    totalNegocios,
    negociosActivos,
    negociosPendientes,
    negociosSuspendidos,
    totalFacturas,
    facturasPendientes,
    facturasPagadas,
    ingresosMensuales,
    suscripcionesActivas,
    allNegocios,
    allFacturas,
    allPlanes,
  } = useDashboard();

  const negociosCancelados = Math.max(
    0,
    totalNegocios - negociosActivos - negociosPendientes - negociosSuspendidos,
  );
  const facturasVencidas = Math.max(0, totalFacturas - facturasPendientes - facturasPagadas);
  const mrrEstimado = ingresosMensuales;

  /* ---------- Export handler ---------- */
  const handleExport = useCallback(() => {
    const now = new Date();
    const fecha = now.toLocaleDateString('es-PE');
    const hora = now.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });

    const csv = buildCSV([
      {
        title: `Reporte DrinkGo — ${fecha} ${hora}`,
        headers: ['Métrica', 'Valor'],
        data: [
          ['MRR Total (ingresos pagados)', mrrEstimado.toFixed(2)],
          ['Negocios totales', totalNegocios],
          ['Negocios activos', negociosActivos],
          ['Negocios pendientes', negociosPendientes],
          ['Negocios suspendidos', negociosSuspendidos],
          ['Negocios cancelados', negociosCancelados],
          ['Suscripciones activas', suscripcionesActivas],
          ['Facturas totales', totalFacturas],
          ['Facturas pagadas', facturasPagadas],
          ['Facturas pendientes', facturasPendientes],
          ['Facturas vencidas/otras', facturasVencidas],
          ['Tasa de actividad (%)', totalNegocios > 0 ? Math.round((negociosActivos / totalNegocios) * 100) : 0],
          ['Tasa de cobro (%)', totalFacturas > 0 ? Math.round((facturasPagadas / totalFacturas) * 100) : 0],
        ],
      },
      {
        title: 'Negocios',
        headers: ['ID', 'Razón Social', 'RUC', 'Estado', 'Email', 'Teléfono', 'Fecha Registro'],
        data: allNegocios.map((n) => [
          n.id,
          n.razonSocial,
          n.ruc,
          n.estado,
          n.email || '',
          n.telefono || '',
          n.creadoEn ? formatDate(n.creadoEn) : '',
        ]),
      },
      {
        title: 'Facturas',
        headers: ['ID', 'Nro. Factura', 'Negocio', 'Total', 'Estado', 'Fecha Emisión', 'Fecha Vencimiento'],
        data: allFacturas.map((f) => [
          f.id,
          f.numeroFactura || '',
          f.suscripcion?.negocio?.razonSocial || f.negocio?.razonSocial || '',
          Number(f.total || 0).toFixed(2),
          f.estado,
          f.fechaEmision ? formatDate(f.fechaEmision) : '',
          f.fechaVencimiento ? formatDate(f.fechaVencimiento) : '',
        ]),
      },
      {
        title: 'Planes de Suscripción',
        headers: ['ID', 'Nombre', 'Precio', 'Período', 'Max Sedes', 'Max Usuarios', 'Activo'],
        data: allPlanes.map((p) => [
          p.id,
          p.nombre,
          Number(p.precio || 0).toFixed(2),
          p.periodo || '',
          p.maxSedes || '',
          p.maxUsuarios || '',
          p.estaActivo !== false ? 'Sí' : 'No',
        ]),
      },
    ]);

    const filename = `drinkgo-reporte-${now.toISOString().slice(0, 10)}.csv`;
    downloadCSV(csv, filename);
  }, [allNegocios, allFacturas, allPlanes, mrrEstimado, totalNegocios, negociosActivos,
      negociosPendientes, negociosSuspendidos, negociosCancelados, suscripcionesActivas,
      totalFacturas, facturasPagadas, facturasPendientes, facturasVencidas]);

  if (isError) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Reportes de Plataforma</h1>
        </div>
        <div className="flex items-center gap-3 bg-red-50 border border-red-200 rounded-lg p-5">
          <AlertCircle size={22} className="text-red-500 shrink-0" />
          <div>
            <p className="font-medium text-red-800">No se pudieron cargar los datos</p>
            <p className="text-sm text-red-600 mt-0.5">
              Verifica que el backend esté activo y vuelve a intentarlo.
            </p>
          </div>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Reportes</h1>
          <p className="text-gray-600 mt-1">Cargando métricas de la plataforma...</p>
        </div>
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="bg-white rounded-lg border border-gray-200 p-5 animate-pulse">
              <div className="h-4 bg-gray-200 rounded w-3/4 mb-3" />
              <div className="h-7 bg-gray-200 rounded w-1/2" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Reportes de Plataforma</h1>
          <p className="text-gray-600 mt-1">
            Monitoreo y analítica de la plataforma DrinkGo — RF-RPL-001
          </p>
        </div>
        <button
          onClick={handleExport}
          disabled={isLoading || totalNegocios + totalFacturas === 0}
          className="flex items-center gap-2 text-sm text-gray-700 font-medium border border-gray-300 rounded-lg px-4 py-2 hover:bg-gray-50 active:bg-gray-100 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
          title="Descargar reporte en CSV (compatible con Excel)"
        >
          <Download size={16} />
          Exportar CSV
        </button>
      </div>

      {/* KPI Row */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          label="MRR Total"
          value={formatCurrency(mrrEstimado)}
          sub="Ingresos recurrentes acumulados"
          icon={DollarSign}
          color="green"
        />
        <MetricCard
          label="Negocios Activos"
          value={negociosActivos}
          sub={`De ${totalNegocios} registrados`}
          icon={Building2}
          color="blue"
        />
        <MetricCard
          label="Suscripciones Activas"
          value={suscripcionesActivas}
          sub="Suscripciones al corriente"
          icon={CreditCard}
          color="purple"
        />
        <MetricCard
          label="Facturas Emitidas"
          value={totalFacturas}
          sub={`${facturasPagadas} pagadas · ${facturasPendientes} pendientes`}
          icon={FileText}
          color="yellow"
        />
      </div>

      {/* Middle Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Distribución de Negocios por Estado */}
        <Card>
          <div className="flex items-center gap-2 mb-4">
            <div className="p-1.5 bg-blue-50 rounded">
              <Building2 size={16} className="text-blue-600" />
            </div>
            <h2 className="font-semibold text-gray-900">Negocios por Estado</h2>
          </div>
          {totalNegocios === 0 ? (
            <EmptyState message="Sin negocios registrados" />
          ) : (
            <div className="space-y-3">
              <BarRow
                label="Activos"
                count={negociosActivos}
                total={totalNegocios}
                color="bg-green-500"
              />
              <BarRow
                label="Pendientes"
                count={negociosPendientes}
                total={totalNegocios}
                color="bg-yellow-400"
              />
              <BarRow
                label="Suspendidos"
                count={negociosSuspendidos}
                total={totalNegocios}
                color="bg-orange-400"
              />
              <BarRow
                label="Cancelados"
                count={Math.max(0, negociosCancelados)}
                total={totalNegocios}
                color="bg-red-400"
              />
            </div>
          )}
          {/* Summary row */}
          {totalNegocios > 0 && (
            <div className="mt-4 pt-3 border-t border-gray-100 flex items-center gap-3 flex-wrap">
              <span className="text-xs px-2 py-0.5 rounded-full bg-green-100 text-green-700">
                {Math.round((negociosActivos / totalNegocios) * 100)}% activos
              </span>
              {negociosPendientes > 0 && (
                <span className="text-xs px-2 py-0.5 rounded-full bg-yellow-100 text-yellow-700">
                  {negociosPendientes} en espera
                </span>
              )}
            </div>
          )}
        </Card>

        {/* Distribución de Facturas por Estado */}
        <Card>
          <div className="flex items-center gap-2 mb-4">
            <div className="p-1.5 bg-yellow-50 rounded">
              <FileText size={16} className="text-yellow-600" />
            </div>
            <h2 className="font-semibold text-gray-900">Facturas por Estado</h2>
          </div>
          {totalFacturas === 0 ? (
            <EmptyState message="Sin facturas registradas" />
          ) : (
            <div className="space-y-3">
              <BarRow
                label="Pagadas"
                count={facturasPagadas}
                total={totalFacturas}
                color="bg-green-500"
              />
              <BarRow
                label="Pendientes"
                count={facturasPendientes}
                total={totalFacturas}
                color="bg-yellow-400"
              />
              <BarRow
                label="Vencidas/Otras"
                count={Math.max(0, facturasVencidas)}
                total={totalFacturas}
                color="bg-red-400"
              />
            </div>
          )}
          {totalFacturas > 0 && (
            <div className="mt-4 pt-3 border-t border-gray-100 flex items-center gap-3 flex-wrap">
              <span className="text-xs px-2 py-0.5 rounded-full bg-green-100 text-green-700">
                {Math.round((facturasPagadas / totalFacturas) * 100)}% cobradas
              </span>
              {facturasPendientes > 0 && (
                <span className="text-xs px-2 py-0.5 rounded-full bg-yellow-100 text-yellow-700">
                  {formatCurrency(0)} por cobrar
                </span>
              )}
            </div>
          )}
        </Card>
      </div>

      {/* Health indicators */}
      <Card>
        <div className="flex items-center gap-2 mb-4">
          <div className="p-1.5 bg-purple-50 rounded">
            <BarChart3 size={16} className="text-purple-600" />
          </div>
          <h2 className="font-semibold text-gray-900">Indicadores de Salud de la Plataforma</h2>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Tasa de Actividad */}
          <div className="border border-gray-200 rounded-lg p-4 text-center">
            <p className="text-sm text-gray-500 mb-1">Tasa de Actividad</p>
            <p className="text-3xl font-bold text-green-600">
              {totalNegocios > 0
                ? `${Math.round((negociosActivos / totalNegocios) * 100)}%`
                : '—'}
            </p>
            <p className="text-xs text-gray-400 mt-1">Negocios activos vs total</p>
          </div>

          {/* Tasa de Cobro */}
          <div className="border border-gray-200 rounded-lg p-4 text-center">
            <p className="text-sm text-gray-500 mb-1">Tasa de Cobro</p>
            <p className="text-3xl font-bold text-blue-600">
              {totalFacturas > 0
                ? `${Math.round((facturasPagadas / totalFacturas) * 100)}%`
                : '—'}
            </p>
            <p className="text-xs text-gray-400 mt-1">Facturas pagadas vs total</p>
          </div>

          {/* Negocios Pendientes */}
          <div className="border border-gray-200 rounded-lg p-4 text-center">
            <p className="text-sm text-gray-500 mb-1">Pendientes Aprobación</p>
            <p className={`text-3xl font-bold ${negociosPendientes > 0 ? 'text-yellow-600' : 'text-gray-400'}`}>
              {negociosPendientes}
            </p>
            <p className="text-xs text-gray-400 mt-1">Requieren acción</p>
          </div>

          {/* Suscripciones vs Negocios */}
          <div className="border border-gray-200 rounded-lg p-4 text-center">
            <p className="text-sm text-gray-500 mb-1">Conversión a Plan</p>
            <p className="text-3xl font-bold text-purple-600">
              {negociosActivos > 0
                ? `${Math.round((suscripcionesActivas / negociosActivos) * 100)}%`
                : '—'}
            </p>
            <p className="text-xs text-gray-400 mt-1">Activos con suscripción</p>
          </div>
        </div>
      </Card>

      {/* Nota informativa */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 flex items-start gap-3">
        <BarChart3 size={18} className="text-blue-500 shrink-0 mt-0.5" />
        <div>
          <p className="text-sm font-medium text-blue-900">Datos en tiempo real</p>
          <p className="text-xs text-blue-700 mt-0.5">
            Los reportes se calculan en tiempo real a partir de los datos de la plataforma.
            Para exportar en Excel/PDF, utiliza el botón "Exportar" (disponible próximamente).
          </p>
        </div>
      </div>
    </div>
  );
};
