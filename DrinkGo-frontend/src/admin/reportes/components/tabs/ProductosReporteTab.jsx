/**
 * ProductosReporteTab.jsx
 * ───────────────────────
 * RF-ECO-019: Reporte de Productos Más Vendidos
 * Ranking de productos por volumen de ventas y monto generado.
 */
import { useState, useMemo } from 'react';
import {
  Award,
  TrendingUp,
  Package,
  BarChart3,
  Search,
  Calendar,
  Download,
} from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { Button } from '@/admin/components/ui/Button';
import { useReporteDetalleVentas } from '../../hooks/useReportes';
import { formatCurrency } from '@/shared/utils/formatters';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { exportToCSV } from '../../utils/exportUtils';

export const ProductosReporteTab = () => {
  const { data: detalles = [], isLoading } = useReporteDetalleVentas();

  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');
  const [page, setPage] = useState(1);
  const pageSize = 10;

  /* Filtrar detalles de ventas completadas en rango */
  const filteredDetalles = useMemo(() => {
    let result = detalles.filter((d) => d.ventaEstado === 'completada');
    if (fechaDesde) {
      const desde = new Date(fechaDesde);
      result = result.filter((d) => new Date(d.ventaCreadoEn || d.creadoEn) >= desde);
    }
    if (fechaHasta) {
      const hasta = new Date(fechaHasta);
      hasta.setHours(23, 59, 59, 999);
      result = result.filter((d) => new Date(d.ventaCreadoEn || d.creadoEn) <= hasta);
    }
    return result;
  }, [detalles, fechaDesde, fechaHasta]);

  /* Armar ranking de productos directamente desde detalles */
  const productosRanking = useMemo(() => {
    const map = {};
    filteredDetalles.forEach((d) => {
      const nombre = d.nombreProducto || `Producto #${d.productoId || '?'}`;
      const key = d.productoId || nombre;
      if (!map[key]) {
        map[key] = {
          productoId: d.productoId,
          nombre,
          cantidadVendida: 0,
          montoTotal: 0,
          vecesEnVentas: new Set(),
        };
      }
      map[key].cantidadVendida += Number(d.cantidad) || 0;
      map[key].montoTotal += Number(d.subtotal) || (Number(d.precioUnitario) * (Number(d.cantidad) || 0));
      map[key].vecesEnVentas.add(d.ventaId);
    });

    let ranking = Object.values(map)
      .map((p) => ({ ...p, vecesEnVentas: p.vecesEnVentas.size }))
      .sort((a, b) => b.cantidadVendida - a.cantidadVendida);

    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      ranking = ranking.filter((p) => p.nombre.toLowerCase().includes(q));
    }

    return ranking;
  }, [filteredDetalles, debouncedSearch]);

  /* Stats */
  const stats = useMemo(() => {
    const totalProductosUnicos = productosRanking.length;
    const totalUnidadesVendidas = productosRanking.reduce((acc, p) => acc + p.cantidadVendida, 0);
    const montoTotalProductos = productosRanking.reduce((acc, p) => acc + p.montoTotal, 0);
    const topProducto = productosRanking[0]?.nombre || '—';
    return { totalProductosUnicos, totalUnidadesVendidas, montoTotalProductos, topProducto };
  }, [productosRanking]);

  /* Paginación */
  const paginated = productosRanking.slice((page - 1) * pageSize, page * pageSize);

  /* Exportar */
  const handleExport = () => {
    const rows = productosRanking.map((p, i) => ({
      'Posición': i + 1,
      'Producto': p.nombre,
      'Unidades Vendidas': p.cantidadVendida,
      'Monto Total': p.montoTotal,
      'Apariciones en Ventas': p.vecesEnVentas,
    }));
    exportToCSV(rows, 'reporte_productos_mas_vendidos');
  };

  /* Columnas */
  const columns = [
    {
      key: 'rank', title: '#', width: '60px',
      render: (_, __, i) => {
        const pos = (page - 1) * pageSize + i + 1;
        if (pos <= 3) {
          const colors = ['text-yellow-500', 'text-gray-400', 'text-amber-600'];
          return <span className={`font-bold ${colors[pos - 1]}`}>{pos}</span>;
        }
        return pos;
      },
    },
    { key: 'nombre', title: 'Producto', dataIndex: 'nombre' },
    {
      key: 'cantidadVendida', title: 'Unidades Vendidas',
      render: (_, row) => <span className="font-semibold">{row.cantidadVendida}</span>,
    },
    {
      key: 'montoTotal', title: 'Monto Total',
      render: (_, row) => <span className="font-semibold">{formatCurrency(row.montoTotal)}</span>,
    },
    { key: 'vecesEnVentas', title: 'En N° Ventas', dataIndex: 'vecesEnVentas' },
    {
      key: 'ticketPromedio', title: 'Precio Prom.',
      render: (_, row) => formatCurrency(row.cantidadVendida > 0 ? row.montoTotal / row.cantidadVendida : 0),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Productos Más Vendidos</h1>
        <p className="text-gray-600 mt-1">
          Ranking de productos por volumen de ventas, identificación de bestsellers y alta rotación
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Productos únicos vendidos" value={stats.totalProductosUnicos} icon={Package} />
        <StatCard title="Unidades vendidas" value={stats.totalUnidadesVendidas} icon={BarChart3} />
        <StatCard title="Monto total productos" value={formatCurrency(stats.montoTotalProductos)} icon={TrendingUp} />
        <StatCard title="Producto estrella" value={stats.topProducto} icon={Award} />
      </div>

      {/* Top 5 visual */}
      {productosRanking.length > 0 && (
        <Card>
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Top 5 Productos</h2>
          <div className="space-y-3">
            {productosRanking.slice(0, 5).map((p, i) => {
              const maxQty = productosRanking[0]?.cantidadVendida || 1;
              const pct = Math.round((p.cantidadVendida / maxQty) * 100);
              const barColors = ['bg-green-500', 'bg-green-400', 'bg-green-300', 'bg-green-200', 'bg-green-100'];
              return (
                <div key={p.productoId || i} className="flex items-center gap-3">
                  <span className="w-6 text-sm font-bold text-gray-500">{i + 1}</span>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-sm font-medium text-gray-700 truncate">{p.nombre}</span>
                      <span className="text-sm text-gray-500 ml-2">{p.cantidadVendida} uds.</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-2">
                      <div
                        className={`h-2 rounded-full transition-all ${barColors[i] || 'bg-green-100'}`}
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>
                  <span className="text-sm font-semibold text-gray-700 w-24 text-right">
                    {formatCurrency(p.montoTotal)}
                  </span>
                </div>
              );
            })}
          </div>
        </Card>
      )}

      {/* Tabla detallada */}
      <Card>
        <div className="flex flex-wrap items-center gap-3 mb-4">
          <div className="relative flex-1 min-w-[200px] max-w-sm">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              placeholder="Buscar producto..."
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
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
          <Button variant="outline" size="sm" onClick={handleExport}>
            <Download size={16} />
            Exportar CSV
          </Button>
        </div>

        <Table
          columns={columns}
          data={paginated}
          loading={isLoading}
          emptyText="No hay productos vendidos en el período seleccionado"
          pagination={{
            current: page,
            pageSize,
            total: productosRanking.length,
            onChange: (newPage) => setPage(newPage),
          }}
        />
      </Card>
    </div>
  );
};
