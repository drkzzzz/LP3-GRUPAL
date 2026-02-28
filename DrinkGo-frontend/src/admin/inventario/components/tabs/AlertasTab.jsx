/**
 * AlertasTab.jsx
 * ──────────────
 * Pestaña de Alertas de Inventario: stock bajo, stock crítico,
 * productos próximos a vencer y productos vencidos.
 * Cálculo 100% client-side (umbrales configurables).
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  AlertTriangle,
  XCircle,
  CalendarClock,
  Ban,
  ShieldAlert,
  Package,
} from 'lucide-react';
import { useStockInventario } from '../../hooks/useStockInventario';
import { useLotesInventario } from '../../hooks/useLotesInventario';
import { formatDate, formatCurrency } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';

/* Umbrales de alerta */
const STOCK_MINIMO = 10;
const STOCK_CRITICO = 3;
const DIAS_ALERTA_VENCIMIENTO = 30;

/* Pestañas internas */
const ALERT_TABS = [
  { key: 'stock_bajo', label: 'Stock Bajo', icon: AlertTriangle },
  { key: 'stock_critico', label: 'Stock Crítico', icon: XCircle },
  { key: 'por_vencer', label: 'Próximos a Vencer', icon: CalendarClock },
  { key: 'vencidos', label: 'Vencidos', icon: Ban },
];

export const AlertasTab = () => {
  const { negocioId } = useOutletContext();
  const [activeAlert, setActiveAlert] = useState('stock_bajo');
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  /* ─── Data hooks ─── */
  const { stock } = useStockInventario(negocioId);
  const { lotes } = useLotesInventario(negocioId);

  /* ─── Helpers ─── */
  const diasParaVencer = (fecha) => {
    if (!fecha) return null;
    return Math.ceil((new Date(fecha) - new Date()) / (1000 * 60 * 60 * 24));
  };

  /* ─── Datos calculados ─── */
  const alertData = useMemo(() => {
    const stockBajo = stock.filter(
      (s) => Number(s.cantidadActual) <= STOCK_MINIMO && Number(s.cantidadActual) > STOCK_CRITICO,
    );
    const stockCritico = stock.filter(
      (s) => Number(s.cantidadActual) <= STOCK_CRITICO,
    );
    const porVencer = lotes.filter((l) => {
      const dias = diasParaVencer(l.fechaVencimiento);
      return dias !== null && dias >= 0 && dias <= DIAS_ALERTA_VENCIMIENTO && Number(l.cantidadActual) > 0;
    }).sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));

    const vencidos = lotes.filter((l) => {
      const dias = diasParaVencer(l.fechaVencimiento);
      return dias !== null && dias < 0 && Number(l.cantidadActual) > 0;
    }).sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));

    return { stock_bajo: stockBajo, stock_critico: stockCritico, por_vencer: porVencer, vencidos };
  }, [stock, lotes]);

  const currentData = alertData[activeAlert] || [];

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return currentData.slice(start, start + pageSize);
  }, [currentData, page, pageSize]);

  /* ─── Columnas para stock bajo/crítico ─── */
  const stockColumns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => (
        <span className="text-gray-400 text-xs">{(page - 1) * pageSize + i + 1}</span>
      ),
    },
    {
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.producto?.nombre || '—'}</p>
          <p className="text-xs text-gray-500">SKU: {row.producto?.sku || '—'}</p>
        </div>
      ),
    },
    {
      key: 'almacen',
      title: 'Almacén',
      width: '140px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.almacen?.nombre || '—'}</span>
      ),
    },
    {
      key: 'cantidadActual',
      title: 'Stock Actual',
      width: '110px',
      align: 'center',
      render: (_, row) => {
        const qty = Number(row.cantidadActual || 0);
        return (
          <span className={`text-sm font-bold ${qty <= STOCK_CRITICO ? 'text-red-600' : 'text-yellow-600'}`}>
            {qty.toLocaleString()}
          </span>
        );
      },
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '110px',
      align: 'center',
      render: (_, row) => {
        const qty = Number(row.cantidadActual);
        if (qty <= STOCK_CRITICO) return <Badge variant="error">Crítico</Badge>;
        return <Badge variant="warning">Bajo</Badge>;
      },
    },
  ];

  /* ─── Columnas para lotes próximos a vencer / vencidos ─── */
  const lotesColumns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => (
        <span className="text-gray-400 text-xs">{(page - 1) * pageSize + i + 1}</span>
      ),
    },
    {
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.producto?.nombre || '—'}</p>
          <p className="text-xs text-gray-500">Lote: {row.numeroLote || '—'}</p>
        </div>
      ),
    },
    {
      key: 'almacen',
      title: 'Almacén',
      width: '130px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.almacen?.nombre || '—'}</span>
      ),
    },
    {
      key: 'cantidadActual',
      title: 'Cantidad',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm font-semibold text-gray-900">
          {Number(row.cantidadActual || 0).toLocaleString()}
        </span>
      ),
    },
    {
      key: 'fechaVencimiento',
      title: 'Vencimiento',
      width: '120px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{formatDate(row.fechaVencimiento)}</span>
      ),
    },
    {
      key: 'dias',
      title: 'Estado',
      width: '120px',
      align: 'center',
      render: (_, row) => {
        const dias = diasParaVencer(row.fechaVencimiento);
        if (dias < 0) return <Badge variant="error">Vencido ({Math.abs(dias)}d)</Badge>;
        return <Badge variant="warning">{dias} días</Badge>;
      },
    },
  ];

  const isStockAlert = activeAlert === 'stock_bajo' || activeAlert === 'stock_critico';
  const columns = isStockAlert ? stockColumns : lotesColumns;

  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Stock bajo"
          value={alertData.stock_bajo.length}
          icon={AlertTriangle}
          className="border-l-4 border-l-yellow-500"
        />
        <StatCard
          title="Stock crítico"
          value={alertData.stock_critico.length}
          icon={XCircle}
          className="border-l-4 border-l-red-500"
        />
        <StatCard
          title="Próximos a vencer"
          value={alertData.por_vencer.length}
          icon={CalendarClock}
          className="border-l-4 border-l-orange-500"
        />
        <StatCard
          title="Vencidos"
          value={alertData.vencidos.length}
          icon={Ban}
          className="border-l-4 border-l-red-700"
        />
      </div>

      {/* Sub-tabs */}
      <div className="flex gap-1 bg-gray-100 rounded-lg p-1">
        {ALERT_TABS.map((tab) => {
          const isActive = activeAlert === tab.key;
          const count = alertData[tab.key]?.length || 0;
          return (
            <button
              key={tab.key}
              onClick={() => { setActiveAlert(tab.key); setPage(1); }}
              className={`flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium transition-colors flex-1 justify-center ${
                isActive
                  ? 'bg-white text-gray-900 shadow-sm'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              <tab.icon size={16} />
              <span className="hidden sm:inline">{tab.label}</span>
              {count > 0 && (
                <span className={`text-xs px-1.5 py-0.5 rounded-full ${
                  isActive ? 'bg-red-100 text-red-700' : 'bg-gray-200 text-gray-600'
                }`}>
                  {count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Tabla */}
      <Card>
        {currentData.length === 0 ? (
          <div className="py-12 flex flex-col items-center justify-center text-center">
            <div className="w-16 h-16 bg-green-50 rounded-full flex items-center justify-center mb-4">
              <ShieldAlert size={32} className="text-green-500" />
            </div>
            <h3 className="text-base font-semibold text-gray-800 mb-1">Sin alertas</h3>
            <p className="text-sm text-gray-500">
              No hay alertas de {ALERT_TABS.find((t) => t.key === activeAlert)?.label?.toLowerCase()} en este momento.
            </p>
          </div>
        ) : (
          <Table
            columns={columns}
            data={paginatedData}
            pagination={{
              current: page,
              pageSize,
              total: currentData.length,
              onChange: (newPage, newSize) => { setPage(newPage); setPageSize(newSize); },
            }}
          />
        )}
      </Card>

      {/* Info de umbrales */}
      <Card>
        <div className="flex items-start gap-3">
          <Package size={20} className="text-gray-400 mt-0.5 flex-shrink-0" />
          <div className="text-sm text-gray-500">
            <p className="font-medium text-gray-700 mb-1">Umbrales configurados</p>
            <ul className="list-disc list-inside space-y-1">
              <li><strong>Stock bajo:</strong> ≤ {STOCK_MINIMO} unidades</li>
              <li><strong>Stock crítico:</strong> ≤ {STOCK_CRITICO} unidades</li>
              <li><strong>Próximo a vencer:</strong> ≤ {DIAS_ALERTA_VENCIMIENTO} días para vencimiento</li>
            </ul>
          </div>
        </div>
      </Card>
    </div>
  );
};
