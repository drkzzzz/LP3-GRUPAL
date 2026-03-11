/**
 * InventarioReporteTab.jsx
 * ────────────────────────
 * RF-ECO-020: Reporte de Inventario
 * Estado del inventario con márgenes de ganancia, costo vs precio de venta,
 * análisis de capital de trabajo.
 */
import { useState, useMemo } from 'react';
import {
  Package,
  DollarSign,
  TrendingUp,
  AlertTriangle,
  Search,
  Download,
} from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { Button } from '@/admin/components/ui/Button';
import { useReporteStock, useReporteLotes } from '../../hooks/useReportes';
import { formatCurrency } from '@/shared/utils/formatters';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { exportToCSV, exportToPDF } from '../../utils/exportUtils';

export const InventarioReporteTab = () => {
  const { data: stock = [], isLoading: loadingStock } = useReporteStock();
  const { data: lotes = [], isLoading: loadingLotes } = useReporteLotes();
  const isLoading = loadingStock || loadingLotes;

  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [page, setPage] = useState(1);
  const pageSize = 10;

  /* Construir reporte de inventario con márgenes */
  const inventarioReport = useMemo(() => {
    const lotesMap = {};
    lotes.forEach((lote) => {
      const key = lote.productoId || lote.producto?.id;
      if (!key) return;
      if (!lotesMap[key]) lotesMap[key] = [];
      lotesMap[key].push(lote);
    });

    let items = stock.map((s) => {
      const productId = s.productoId || s.producto?.id;
      const nombre = s.productoNombre || s.producto?.nombre || `Producto #${productId}`;
      const almacen = s.almacenNombre || s.almacen?.nombre || '—';
      const cantidadActual = s.cantidadActual ?? s.cantidad ?? 0;
      const stockMinimo = s.stockMinimo ?? 0;

      /* Calcular costo promedio ponderado desde lotes */
      const lotesProducto = lotesMap[productId] || [];
      let costoTotal = 0;
      let cantidadLotes = 0;
      lotesProducto.forEach((l) => {
        const qty = l.cantidadActual ?? l.cantidad ?? 0;
        const cost = l.costoUnitario ?? l.precioCompra ?? 0;
        costoTotal += qty * cost;
        cantidadLotes += qty;
      });
      const costoPromedio = cantidadLotes > 0 ? costoTotal / cantidadLotes : 0;

      const precioVenta = s.precioVenta || s.producto?.precioVenta || 0;
      const margenUnitario = precioVenta - costoPromedio;
      const margenPorcentaje = precioVenta > 0 ? ((margenUnitario / precioVenta) * 100) : 0;
      const valorInventario = cantidadActual * costoPromedio;
      const valorVentaPotencial = cantidadActual * precioVenta;

      return {
        id: s.id,
        productoId: productId,
        nombre,
        almacen,
        cantidadActual,
        stockMinimo,
        costoPromedio,
        precioVenta,
        margenUnitario,
        margenPorcentaje,
        valorInventario,
        valorVentaPotencial,
        bajStock: cantidadActual <= stockMinimo && stockMinimo > 0,
      };
    });

    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      items = items.filter((i) =>
        i.nombre.toLowerCase().includes(q) || i.almacen.toLowerCase().includes(q),
      );
    }

    return items;
  }, [stock, lotes, debouncedSearch]);

  /* Stats */
  const stats = useMemo(() => {
    const totalItems = inventarioReport.length;
    const valorTotal = inventarioReport.reduce((acc, i) => acc + i.valorInventario, 0);
    const valorVenta = inventarioReport.reduce((acc, i) => acc + i.valorVentaPotencial, 0);
    const bajoStock = inventarioReport.filter((i) => i.bajStock).length;
    return { totalItems, valorTotal, valorVenta, bajoStock };
  }, [inventarioReport]);

  /* Paginación */
  const paginated = inventarioReport.slice((page - 1) * pageSize, page * pageSize);

  /* Exportar */
  const buildRows = () =>
    inventarioReport.map((i) => ({
      'Producto': i.nombre,
      'Almacén': i.almacen,
      'Stock Actual': i.cantidadActual,
      'Stock Mínimo': i.stockMinimo,
      'Costo Promedio': formatCurrency(i.costoPromedio),
      'Precio Venta': formatCurrency(i.precioVenta),
      'Margen (%)': i.margenPorcentaje.toFixed(1) + '%',
      'Valor Inventario': formatCurrency(i.valorInventario),
    }));

  const handleExport = () => exportToCSV(
    inventarioReport.map((i) => ({
      'Producto': i.nombre,
      'Almacén': i.almacen,
      'Stock Actual': i.cantidadActual,
      'Stock Mínimo': i.stockMinimo,
      'Costo Promedio': i.costoPromedio,
      'Precio Venta': i.precioVenta,
      'Margen (%)': i.margenPorcentaje.toFixed(1),
      'Valor Inventario': i.valorInventario,
    })),
    'reporte_inventario',
  );
  const handleExportPDF = () => exportToPDF('Reporte de Inventario', buildRows());

  /* Columnas */
  const columns = [
    { key: 'index', title: '#', width: '60px', render: (_, __, i) => (page - 1) * pageSize + i + 1 },
    { key: 'nombre', title: 'Producto', dataIndex: 'nombre' },
    { key: 'almacen', title: 'Almacén', dataIndex: 'almacen' },
    {
      key: 'stock', title: 'Stock',
      render: (_, row) => (
        <div className="flex items-center gap-2">
          <span className={row.bajStock ? 'text-red-600 font-semibold' : ''}>{row.cantidadActual}</span>
          {row.bajStock && <Badge variant="error">Bajo</Badge>}
        </div>
      ),
    },
    { key: 'costoPromedio', title: 'Costo Prom.', render: (_, row) => formatCurrency(row.costoPromedio) },
    { key: 'precioVenta', title: 'Precio Venta', render: (_, row) => formatCurrency(row.precioVenta) },
    {
      key: 'margen', title: 'Margen',
      render: (_, row) => (
        <Badge variant={row.margenPorcentaje > 30 ? 'success' : row.margenPorcentaje > 15 ? 'warning' : 'error'}>
          {row.margenPorcentaje.toFixed(1)}%
        </Badge>
      ),
    },
    {
      key: 'valorInv', title: 'Valor Inventario',
      render: (_, row) => <span className="font-semibold">{formatCurrency(row.valorInventario)}</span>,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Reporte de Inventario</h1>
        <p className="text-gray-600 mt-1">
          Estado del inventario, márgenes de ganancia y capital de trabajo invertido
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Productos en inventario" value={stats.totalItems} icon={Package} />
        <StatCard title="Valor al costo" value={formatCurrency(stats.valorTotal)} icon={DollarSign} />
        <StatCard title="Valor venta potencial" value={formatCurrency(stats.valorVenta)} icon={TrendingUp} />
        <StatCard title="Productos bajo stock" value={stats.bajoStock} icon={AlertTriangle} />
      </div>

      {/* Tabla detallada */}
      <Card>
        <div className="flex flex-wrap items-center gap-3 mb-4">
          <div className="relative flex-1 min-w-[200px] max-w-sm">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              placeholder="Buscar producto o almacén..."
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button variant="outline" size="sm" onClick={handleExport}>
            <Download size={16} />
            CSV
          </Button>
          <Button variant="outline" size="sm" onClick={handleExportPDF}>
            <Download size={16} />
            PDF
          </Button>
        </div>

        <Table
          columns={columns}
          data={paginated}
          loading={isLoading}
          emptyText="No hay datos de inventario"
          pagination={{
            current: page,
            pageSize,
            total: inventarioReport.length,
            onChange: (newPage) => setPage(newPage),
          }}
        />
      </Card>
    </div>
  );
};
