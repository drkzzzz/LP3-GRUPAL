import { useNavigate } from 'react-router-dom';
import {
  TrendingUp, ShoppingCart, AlertTriangle, Users,
  RotateCcw, RefreshCw, ArrowRight, Warehouse,
  ArrowUpRight, ArrowDownRight,
} from 'lucide-react';
import {
  ResponsiveContainer, LineChart, Line, XAxis, YAxis,
  CartesianGrid, Tooltip, PieChart, Pie, Cell,
} from 'recharts';
import { useDashboardAdmin } from '@/admin/hooks/useDashboardAdmin';

const formatCurrency = (v) =>
  new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(v ?? 0);

const ESTADO_COLORS = {
  pendiente: '#f59e0b',
  confirmado: '#3b82f6',
  preparando: '#8b5cf6',
  en_camino: '#06b6d4',
  entregado: '#22c55e',
  cancelado: '#ef4444',
};

const ESTADO_LABELS = {
  pendiente: 'Pendiente',
  confirmado: 'Confirmado',
  preparando: 'Preparando',
  en_camino: 'En camino',
  entregado: 'Entregado',
  cancelado: 'Cancelado',
};

const estadoBadgeClass = (estado) => ({
  pendiente: 'bg-amber-100 text-amber-700',
  confirmado: 'bg-blue-100 text-blue-700',
  preparando: 'bg-purple-100 text-purple-700',
  en_camino: 'bg-cyan-100 text-cyan-700',
  entregado: 'bg-green-100 text-green-700',
  cancelado: 'bg-red-100 text-red-700',
}[estado] || 'bg-gray-100 text-gray-600');

/* ── Sub-componentes ─────────────────────────────────── */

function KpiCard({ title, value, icon: Icon, colorClass, iconBg, iconColor, trend }) {
  const isPositive = trend == null || trend >= 0;
  return (
    <div className={`bg-white rounded-xl border border-gray-100 border-l-4 ${colorClass} p-5 shadow-sm`}>
      <div className="flex items-start justify-between gap-2">
        <div className="min-w-0 flex-1">
          <p className="text-xs font-medium text-gray-500 uppercase tracking-wide truncate">{title}</p>
          <p className="text-2xl font-bold text-gray-900 mt-1 truncate">{value}</p>
          {trend != null && (
            <p className={`text-xs mt-1 flex items-center gap-0.5 ${isPositive ? 'text-green-600' : 'text-red-500'}`}>
              {isPositive
                ? <ArrowUpRight size={12} />
                : <ArrowDownRight size={12} />}
              {isPositive ? '+' : ''}{trend.toFixed(1)}% vs mes anterior
            </p>
          )}
        </div>
        <div className={`w-10 h-10 rounded-lg ${iconBg} flex items-center justify-center shrink-0`}>
          <Icon size={20} className={iconColor} />
        </div>
      </div>
    </div>
  );
}

function SectionCard({ title, children, action }) {
  return (
    <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-semibold text-gray-700">{title}</h3>
        {action}
      </div>
      {children}
    </div>
  );
}

const LineTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-white border border-gray-200 rounded-lg shadow-sm px-3 py-2">
      <p className="text-xs text-gray-500 mb-0.5">{label}</p>
      <p className="text-sm font-semibold text-gray-900">{formatCurrency(payload[0].value)}</p>
    </div>
  );
};

/* ── Componente principal ─────────────────────────────── */

export const Dashboard = () => {
  const navigate = useNavigate();
  const {
    isLoading, refetch,
    totalVentasMes, variacionVentas,
    cantidadPedidosMes, variacionPedidos,
    clientesNuevosMes,
    cantidadAlertasStock,
    valorInventario,
    devolucionesPendientes,
    donutData,
    chartVentas,
    pedidosRecientes,
    stockAlerts,
    lotesVencidos,
    lotesProximos,
    topProductos,
    negocioNombre,
  } = useDashboardAdmin();

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            {negocioNombre ? `${negocioNombre} · ` : ''}Métricas y estadísticas en tiempo real
          </p>
        </div>
        <button
          onClick={refetch}
          disabled={isLoading}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-600 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 shrink-0"
        >
          <RefreshCw size={15} className={isLoading ? 'animate-spin' : ''} />
          Actualizar
        </button>
      </div>

      {/* KPI Row 1 */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <KpiCard
          title="Ventas del Mes"
          value={formatCurrency(totalVentasMes)}
          icon={TrendingUp}
          colorClass="border-l-green-500"
          iconBg="bg-green-100"
          iconColor="text-green-600"
          trend={variacionVentas}
        />
        <KpiCard
          title="Clientes Nuevos"
          value={`${clientesNuevosMes} clientes`}
          icon={Users}
          colorClass="border-l-blue-500"
          iconBg="bg-blue-100"
          iconColor="text-blue-600"
        />
        <KpiCard
          title="Productos con Stock Bajo"
          value={`${cantidadAlertasStock} productos`}
          icon={AlertTriangle}
          colorClass="border-l-amber-500"
          iconBg="bg-amber-100"
          iconColor="text-amber-600"
        />
      </div>

      {/* KPI Row 2 */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <KpiCard
          title="Pedidos del Mes"
          value={`${cantidadPedidosMes} órdenes`}
          icon={ShoppingCart}
          colorClass="border-l-indigo-500"
          iconBg="bg-indigo-100"
          iconColor="text-indigo-600"
          trend={variacionPedidos}
        />
        <KpiCard
          title="Devoluciones Pendientes"
          value={`${devolucionesPendientes} RMAs`}
          icon={RotateCcw}
          colorClass="border-l-teal-500"
          iconBg="bg-teal-100"
          iconColor="text-teal-600"
        />
        <KpiCard
          title="Valor del Inventario"
          value={formatCurrency(valorInventario)}
          icon={Warehouse}
          colorClass="border-l-violet-500"
          iconBg="bg-violet-100"
          iconColor="text-violet-600"
        />
      </div>

      {/* Gráfico de ventas 30 días */}
      <SectionCard title="Ventas de los Últimos 30 Días">
        <div className="h-56">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartVentas} margin={{ top: 4, right: 16, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f3f4f6" vertical={false} />
              <XAxis
                dataKey="label"
                tick={{ fontSize: 10, fill: '#9ca3af' }}
                tickLine={false}
                axisLine={false}
                interval={4}
              />
              <YAxis
                tick={{ fontSize: 10, fill: '#9ca3af' }}
                tickLine={false}
                axisLine={false}
                tickFormatter={(v) => v >= 1000 ? `${(v / 1000).toFixed(0)}k` : v}
                width={40}
              />
              <Tooltip content={<LineTooltip />} />
              <Line
                type="monotone"
                dataKey="total"
                stroke="#22c55e"
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 4, fill: '#16a34a', strokeWidth: 0 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </SectionCard>

      {/* Donut + Top Productos */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <SectionCard title="Pedidos por Estado">
          {donutData.length === 0 ? (
            <p className="text-sm text-gray-400 text-center py-10">Sin datos de pedidos</p>
          ) : (
            <div className="flex items-center gap-6">
              <div className="shrink-0 w-44 h-44">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={donutData}
                      cx="50%"
                      cy="50%"
                      innerRadius={42}
                      outerRadius={68}
                      paddingAngle={2}
                      dataKey="value"
                      startAngle={90}
                      endAngle={-270}
                    >
                      {donutData.map((entry) => (
                        <Cell
                          key={entry.name}
                          fill={ESTADO_COLORS[entry.name] || '#9ca3af'}
                        />
                      ))}
                    </Pie>
                  </PieChart>
                </ResponsiveContainer>
              </div>
              <div className="flex-1 space-y-2.5">
                {donutData.map((entry) => (
                  <div key={entry.name} className="flex items-center justify-between gap-2">
                    <div className="flex items-center gap-2 min-w-0">
                      <span
                        className="w-2.5 h-2.5 rounded-full shrink-0"
                        style={{ background: ESTADO_COLORS[entry.name] || '#9ca3af' }}
                      />
                      <span className="text-sm text-gray-600 truncate">
                        {ESTADO_LABELS[entry.name] || entry.name}
                      </span>
                    </div>
                    <span className="text-sm font-semibold text-gray-900 shrink-0">{entry.value}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </SectionCard>

        <SectionCard title="Top 5 Productos Más Vendidos">
          {topProductos.length === 0 ? (
            <p className="text-sm text-gray-400 text-center py-10">Sin datos de movimientos este mes</p>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100">
                  <th className="pb-2.5 text-left text-xs font-medium text-gray-400 w-6">#</th>
                  <th className="pb-2.5 text-left text-xs font-medium text-gray-400">Producto</th>
                  <th className="pb-2.5 text-center text-xs font-medium text-gray-400">SKU</th>
                  <th className="pb-2.5 text-right text-xs font-medium text-gray-400">Cantidad</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {topProductos.map((p) => (
                  <tr key={p.rank}>
                    <td className="py-2.5 text-xs font-bold text-gray-300">{p.rank}</td>
                    <td className="py-2.5 font-medium text-gray-900 truncate max-w-[140px] pr-2">{p.nombre}</td>
                    <td className="py-2.5 text-center text-xs text-gray-400">{p.sku || '—'}</td>
                    <td className="py-2.5 text-right font-semibold text-gray-900">{p.cantidad} uds</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </SectionCard>
      </div>

      {/* Pedidos Recientes + Alertas Stock */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <SectionCard
          title="Pedidos Recientes"
          action={
            <button
              onClick={() => navigate('/admin/pedidos')}
              className="flex items-center gap-1 text-xs font-medium text-green-600 hover:text-green-700 transition-colors"
            >
              Ver todos <ArrowRight size={12} />
            </button>
          }
        >
          {pedidosRecientes.length === 0 ? (
            <p className="text-sm text-gray-400 text-center py-10">Sin pedidos recientes</p>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-100">
                  <th className="pb-2.5 text-left text-xs font-medium text-gray-400">Nº Orden</th>
                  <th className="pb-2.5 text-left text-xs font-medium text-gray-400">Cliente</th>
                  <th className="pb-2.5 text-right text-xs font-medium text-gray-400">Total</th>
                  <th className="pb-2.5 text-center text-xs font-medium text-gray-400">Estado</th>
                  <th className="pb-2.5 text-right text-xs font-medium text-gray-400">Fecha</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {pedidosRecientes.map((p) => (
                  <tr key={p.id}>
                    <td className="py-2.5 text-xs font-medium text-blue-600">{p.numeroPedido}</td>
                    <td className="py-2.5 text-xs text-gray-700 truncate max-w-[80px]">
                      {p.clienteNombre
                        ? `${p.clienteNombre} ${p.clienteApellido || ''}`.trim()
                        : '—'}
                    </td>
                    <td className="py-2.5 text-right text-xs font-semibold text-gray-900">
                      {formatCurrency(p.total)}
                    </td>
                    <td className="py-2.5 text-center">
                      <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${estadoBadgeClass(p.estadoPedido)}`}>
                        {ESTADO_LABELS[p.estadoPedido] || p.estadoPedido}
                      </span>
                    </td>
                    <td className="py-2.5 text-right text-xs text-gray-400">
                      {p.creadoEn
                        ? new Date(p.creadoEn).toLocaleDateString('es-PE', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' })
                        : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </SectionCard>

        <SectionCard
          title="Alertas de Inventario"
          action={
            <button
              onClick={() => navigate('/admin/inventario/reportes')}
              className="flex items-center gap-1 text-xs font-medium text-green-600 hover:text-green-700 transition-colors"
            >
              Ver detalles <ArrowRight size={12} />
            </button>
          }
        >
          {cantidadAlertasStock === 0 ? (
            <p className="text-sm text-gray-400 text-center py-10">Sin alertas de inventario 🎉</p>
          ) : (
            <div className="space-y-3">
              <div className="flex items-center gap-3 bg-amber-50 border border-amber-100 rounded-lg px-3 py-2.5">
                <AlertTriangle size={15} className="text-amber-600 shrink-0" />
                <p className="text-sm text-amber-800">
                  <strong>{cantidadAlertasStock} alertas</strong> de inventario que requieren atención
                </p>
              </div>

              {/* Lotes vencidos */}
              {lotesVencidos.length > 0 && (
                <div className="space-y-1">
                  <p className="text-xs font-semibold text-red-600 uppercase tracking-wide">Vencidos</p>
                  {lotesVencidos.map(l => (
                    <div key={l.id} className="flex items-center justify-between py-2 border-b border-gray-50">
                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-medium text-gray-900 truncate">
                          {l.producto?.nombre || '—'}
                        </p>
                        <p className="text-xs text-gray-400">
                          Lote: {l.numeroLote} · Vencido: {l.fechaVencimiento}
                        </p>
                      </div>
                      <span className="text-xs font-bold text-red-600 shrink-0 ml-3">
                        {Number(l.cantidadActual)} uds
                      </span>
                    </div>
                  ))}
                </div>
              )}

              {/* Lotes próximos a vencer */}
              {lotesProximos.length > 0 && (
                <div className="space-y-1">
                  <p className="text-xs font-semibold text-orange-600 uppercase tracking-wide">Próximos a vencer (30 días)</p>
                  {lotesProximos.map(l => (
                    <div key={l.id} className="flex items-center justify-between py-2 border-b border-gray-50">
                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-medium text-gray-900 truncate">
                          {l.producto?.nombre || '—'}
                        </p>
                        <p className="text-xs text-gray-400">
                          Lote: {l.numeroLote} · Vence: {l.fechaVencimiento}
                        </p>
                      </div>
                      <span className="text-xs font-bold text-orange-600 shrink-0 ml-3">
                        {Number(l.cantidadActual)} uds
                      </span>
                    </div>
                  ))}
                </div>
              )}

              {/* Stock bajo */}
              {stockAlerts.length > 0 && (
                <div className="space-y-1">
                  <p className="text-xs font-semibold text-amber-600 uppercase tracking-wide">Stock bajo</p>
                  {stockAlerts.map(s => (
                    <div key={s.id} className="flex items-center justify-between py-2 border-b border-gray-50">
                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-medium text-gray-900 truncate">{s.producto?.nombre || '—'}</p>
                        <p className="text-xs text-gray-400">SKU: {s.producto?.sku || '—'}</p>
                      </div>
                      <div className="text-right shrink-0 ml-3">
                        <p className={`text-sm font-bold ${Number(s.cantidadDisponible) === 0 ? 'text-red-600' : 'text-amber-600'}`}>
                          {Number(s.cantidadDisponible)} uds
                        </p>
                        <p className="text-xs text-gray-400">disponible</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </SectionCard>
      </div>
    </div>
  );
};

