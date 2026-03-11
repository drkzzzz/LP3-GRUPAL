/**
 * ComprasGastosReporteTab.jsx
 * ───────────────────────────
 * RF-ECO-022: Reporte de Compras y Gastos
 * Consolidación de compras a proveedores y gastos operativos.
 */
import { useState, useMemo } from 'react';
import {
  Truck,
  Receipt,
  DollarSign,
  TrendingDown,
  Search,
  Calendar,
  Download,
} from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { Button } from '@/admin/components/ui/Button';
import { useReporteCompras, useReporteGastos } from '../../hooks/useReportes';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { exportToCSV, exportToPDF } from '../../utils/exportUtils';

const ESTADO_OC_MAP = {
  pendiente: { label: 'Pendiente', variant: 'warning' },
  recibida: { label: 'Recibida', variant: 'success' },
  cancelada: { label: 'Cancelada', variant: 'error' },
  parcial: { label: 'Parcial', variant: 'info' },
};

const ESTADO_GASTO_MAP = {
  pendiente: { label: 'Pendiente', variant: 'warning' },
  pagado: { label: 'Pagado', variant: 'success' },
  anulado: { label: 'Anulado', variant: 'error' },
};

export const ComprasGastosReporteTab = () => {
  const { data: compras = [], isLoading: loadingCompras } = useReporteCompras();
  const { data: gastos = [], isLoading: loadingGastos } = useReporteGastos();
  const isLoading = loadingCompras || loadingGastos;

  const [activeSubTab, setActiveSubTab] = useState('compras');
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');
  const [page, setPage] = useState(1);
  const pageSize = 10;

  /* Filtrar compras */
  const filteredCompras = useMemo(() => {
    let result = [...compras];
    if (fechaDesde) {
      const desde = new Date(fechaDesde);
      result = result.filter((c) => new Date(c.fechaOrden || c.creadoEn) >= desde);
    }
    if (fechaHasta) {
      const hasta = new Date(fechaHasta);
      hasta.setHours(23, 59, 59, 999);
      result = result.filter((c) => new Date(c.fechaOrden || c.creadoEn) <= hasta);
    }
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter((c) =>
        (c.numeroOrden || '').toLowerCase().includes(q) ||
        (c.proveedorNombre || c.proveedor?.razonSocial || '').toLowerCase().includes(q),
      );
    }
    return result;
  }, [compras, fechaDesde, fechaHasta, debouncedSearch]);

  /* Filtrar gastos */
  const filteredGastos = useMemo(() => {
    let result = [...gastos];
    if (fechaDesde) {
      const desde = new Date(fechaDesde);
      result = result.filter((g) => new Date(g.fechaGasto || g.creadoEn) >= desde);
    }
    if (fechaHasta) {
      const hasta = new Date(fechaHasta);
      hasta.setHours(23, 59, 59, 999);
      result = result.filter((g) => new Date(g.fechaGasto || g.creadoEn) <= hasta);
    }
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter((g) =>
        (g.descripcion || '').toLowerCase().includes(q) ||
        (g.categoriaNombre || g.categoriaGasto?.nombre || '').toLowerCase().includes(q),
      );
    }
    return result;
  }, [gastos, fechaDesde, fechaHasta, debouncedSearch]);

  /* Stats */
  const stats = useMemo(() => {
    const totalCompras = filteredCompras.reduce((acc, c) => acc + (c.total || c.montoTotal || 0), 0);
    const comprasRecibidas = filteredCompras.filter((c) => c.estado === 'recibida').length;
    const totalGastos = filteredGastos.reduce((acc, g) => acc + (g.monto || 0), 0);
    const gastosPagados = filteredGastos.filter((g) => g.estado === 'pagado').length;
    return { totalCompras, comprasRecibidas, totalGastos, gastosPagados, granTotal: totalCompras + totalGastos };
  }, [filteredCompras, filteredGastos]);

  /* Resumen por proveedor */
  const resumenProveedores = useMemo(() => {
    const map = {};
    filteredCompras.forEach((c) => {
      const nombre = c.proveedorNombre || c.proveedor?.razonSocial || 'Sin proveedor';
      if (!map[nombre]) map[nombre] = { nombre, totalCompras: 0, cantidadOrdenes: 0 };
      map[nombre].totalCompras += c.total || c.montoTotal || 0;
      map[nombre].cantidadOrdenes++;
    });
    return Object.values(map).sort((a, b) => b.totalCompras - a.totalCompras);
  }, [filteredCompras]);

  /* Resumen por categoría de gasto */
  const resumenCategorias = useMemo(() => {
    const map = {};
    filteredGastos.forEach((g) => {
      const nombre = g.categoriaNombre || g.categoriaGasto?.nombre || 'Sin categoría';
      if (!map[nombre]) map[nombre] = { nombre, totalGastos: 0, cantidad: 0 };
      map[nombre].totalGastos += g.monto || 0;
      map[nombre].cantidad++;
    });
    return Object.values(map).sort((a, b) => b.totalGastos - a.totalGastos);
  }, [filteredGastos]);

  const currentData = activeSubTab === 'compras' ? filteredCompras : filteredGastos;
  const paginated = currentData.slice((page - 1) * pageSize, page * pageSize);

  /* Exportar */
  const buildComprasRows = () =>
    filteredCompras.map((c) => ({
      'N° Orden': c.numeroOrden || '',
      'Proveedor': c.proveedorNombre || c.proveedor?.razonSocial || '',
      'Fecha': formatDate(c.fechaOrden || c.creadoEn),
      'Total': formatCurrency(c.total || c.montoTotal || 0),
      'Estado': ESTADO_OC_MAP[c.estado]?.label || c.estado || '',
    }));

  const buildGastosRows = () =>
    filteredGastos.map((g) => ({
      'Descripción': g.descripcion || '',
      'Categoría': g.categoriaNombre || g.categoriaGasto?.nombre || '',
      'Fecha': formatDate(g.fechaGasto || g.creadoEn),
      'Monto': formatCurrency(g.monto || 0),
      'Estado': ESTADO_GASTO_MAP[g.estado]?.label || g.estado || '',
    }));

  const handleExport = () => {
    if (activeSubTab === 'compras') {
      exportToCSV(
        filteredCompras.map((c) => ({
          'N° Orden': c.numeroOrden || '',
          'Proveedor': c.proveedorNombre || c.proveedor?.razonSocial || '',
          'Fecha': formatDate(c.fechaOrden || c.creadoEn),
          'Total': c.total || c.montoTotal || 0,
          'Estado': ESTADO_OC_MAP[c.estado]?.label || c.estado || '',
        })),
        'reporte_compras',
      );
    } else {
      exportToCSV(
        filteredGastos.map((g) => ({
          'Descripción': g.descripcion || '',
          'Categoría': g.categoriaNombre || g.categoriaGasto?.nombre || '',
          'Fecha': formatDate(g.fechaGasto || g.creadoEn),
          'Monto': g.monto || 0,
          'Estado': ESTADO_GASTO_MAP[g.estado]?.label || g.estado || '',
        })),
        'reporte_gastos',
      );
    }
  };

  const handleExportPDF = () => {
    if (activeSubTab === 'compras') {
      exportToPDF('Reporte de Compras', buildComprasRows());
    } else {
      exportToPDF('Reporte de Gastos', buildGastosRows());
    }
  };

  /* Columnas compras */
  const comprasColumns = [
    { key: 'index', title: '#', width: '60px', render: (_, __, i) => (page - 1) * pageSize + i + 1 },
    { key: 'numero', title: 'N° Orden', render: (_, row) => row.numeroOrden || `OC-${row.id}` },
    { key: 'proveedor', title: 'Proveedor', render: (_, row) => row.proveedorNombre || row.proveedor?.razonSocial || '—' },
    { key: 'fecha', title: 'Fecha', render: (_, row) => formatDate(row.fechaOrden || row.creadoEn) },
    { key: 'total', title: 'Total', render: (_, row) => <span className="font-semibold">{formatCurrency(row.total || row.montoTotal)}</span> },
    {
      key: 'estado', title: 'Estado',
      render: (_, row) => {
        const info = ESTADO_OC_MAP[row.estado] || { label: row.estado, variant: 'info' };
        return <Badge variant={info.variant}>{info.label}</Badge>;
      },
    },
  ];

  /* Columnas gastos */
  const gastosColumns = [
    { key: 'index', title: '#', width: '60px', render: (_, __, i) => (page - 1) * pageSize + i + 1 },
    { key: 'descripcion', title: 'Descripción', dataIndex: 'descripcion' },
    { key: 'categoria', title: 'Categoría', render: (_, row) => row.categoriaNombre || row.categoriaGasto?.nombre || '—' },
    { key: 'fecha', title: 'Fecha', render: (_, row) => formatDate(row.fechaGasto || row.creadoEn) },
    { key: 'monto', title: 'Monto', render: (_, row) => <span className="font-semibold">{formatCurrency(row.monto)}</span> },
    {
      key: 'estado', title: 'Estado',
      render: (_, row) => {
        const info = ESTADO_GASTO_MAP[row.estado] || { label: row.estado, variant: 'info' };
        return <Badge variant={info.variant}>{info.label}</Badge>;
      },
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Compras y Gastos</h1>
        <p className="text-gray-600 mt-1">
          Consolidación de compras a proveedores y gastos operativos del negocio
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total compras" value={formatCurrency(stats.totalCompras)} icon={Truck} />
        <StatCard title="Compras recibidas" value={stats.comprasRecibidas} icon={Receipt} />
        <StatCard title="Total gastos" value={formatCurrency(stats.totalGastos)} icon={TrendingDown} />
        <StatCard title="Egresos totales" value={formatCurrency(stats.granTotal)} icon={DollarSign} />
      </div>

      {/* Resúmenes */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Top proveedores */}
        <Card>
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Top Proveedores</h2>
          {resumenProveedores.length === 0 ? (
            <p className="text-sm text-gray-500">Sin datos de compras</p>
          ) : (
            <div className="space-y-2">
              {resumenProveedores.slice(0, 5).map((p, i) => (
                <div key={i} className="flex items-center justify-between py-2 border-b border-gray-100 last:border-0">
                  <div>
                    <span className="text-sm font-medium text-gray-700">{p.nombre}</span>
                    <span className="text-xs text-gray-400 ml-2">({p.cantidadOrdenes} órdenes)</span>
                  </div>
                  <span className="text-sm font-semibold">{formatCurrency(p.totalCompras)}</span>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Distribución de gastos */}
        <Card>
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Gastos por Categoría</h2>
          {resumenCategorias.length === 0 ? (
            <p className="text-sm text-gray-500">Sin datos de gastos</p>
          ) : (
            <div className="space-y-2">
              {resumenCategorias.slice(0, 5).map((c, i) => {
                const totalGastos = stats.totalGastos || 1;
                const pct = Math.round((c.totalGastos / totalGastos) * 100);
                return (
                  <div key={i} className="space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="text-sm font-medium text-gray-700">{c.nombre}</span>
                      <span className="text-sm font-semibold">{formatCurrency(c.totalGastos)} ({pct}%)</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-2">
                      <div className="h-2 rounded-full bg-red-400 transition-all" style={{ width: `${pct}%` }} />
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </Card>
      </div>

      {/* Tabla detallada */}
      <Card>
        {/* Sub-tab compras/gastos */}
        <div className="flex items-center gap-4 mb-4 border-b border-gray-200 pb-3">
          <button
            onClick={() => { setActiveSubTab('compras'); setPage(1); setSearchTerm(''); }}
            className={`text-sm font-medium pb-1 border-b-2 transition-colors ${
              activeSubTab === 'compras' ? 'border-green-600 text-green-600' : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            Órdenes de Compra ({filteredCompras.length})
          </button>
          <button
            onClick={() => { setActiveSubTab('gastos'); setPage(1); setSearchTerm(''); }}
            className={`text-sm font-medium pb-1 border-b-2 transition-colors ${
              activeSubTab === 'gastos' ? 'border-green-600 text-green-600' : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            Gastos ({filteredGastos.length})
          </button>
        </div>

        <div className="flex flex-wrap items-center gap-3 mb-4">
          <div className="relative flex-1 min-w-[200px] max-w-sm">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              placeholder={activeSubTab === 'compras' ? 'Buscar por N° orden, proveedor...' : 'Buscar por descripción, categoría...'}
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
            CSV
          </Button>
          <Button variant="outline" size="sm" onClick={handleExportPDF}>
            <Download size={16} />
            PDF
          </Button>
        </div>

        <Table
          columns={activeSubTab === 'compras' ? comprasColumns : gastosColumns}
          data={paginated}
          loading={isLoading}
          emptyText={`No hay ${activeSubTab === 'compras' ? 'órdenes de compra' : 'gastos'} en el período seleccionado`}
          pagination={{
            current: page,
            pageSize,
            total: currentData.length,
            onChange: (newPage) => setPage(newPage),
          }}
        />
      </Card>
    </div>
  );
};
