/**
 * ComprobantesReporteTab.jsx
 * ──────────────────────────
 * RF-ECO-023 / RF-ECO-024: Reporte de Boletas y Facturas
 * Pestañas: Boletas | Facturas | Análisis SUNAT
 */
import { useState, useMemo } from 'react';
import {
  FileText, CheckCircle, AlertTriangle, Search,
  Calendar, Download, DollarSign, Users, BarChart2,
} from 'lucide-react';
import { Card }     from '@/admin/components/ui/Card';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Badge }    from '@/admin/components/ui/Badge';
import { Table }    from '@/admin/components/ui/Table';
import { Button }   from '@/admin/components/ui/Button';
import { useReporteComprobantes } from '../../hooks/useReportes';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { exportToCSV, exportToPDF } from '../../utils/exportUtils';

const getTipo      = (c) => (c.tipoDocumento || c.tipoComprobante || c.tipo || '').toUpperCase();
const getEstado    = (c) => (c.estadoDocumento || c.estado || '').toUpperCase();
const getCliente   = (c) => c.clienteNombre || c.cliente?.razonSocial || c.cliente?.nombre || '—';
const getDocumento = (c) => c.clienteNumeroDocumento || c.clienteDocumento || c.cliente?.documento || '';
const getTotal     = (c) => Number(c.total || c.montoTotal || 0);
const getFecha     = (c) => c.fechaEmision || c.creadoEn;
const getSerie     = (c) => c.serie || (c.numeroDocumento ? c.numeroDocumento.split('-')[0] : null);
const getNumero    = (c) => c.numero || (c.numeroDocumento ? c.numeroDocumento.split('-').slice(1).join('-') : null);

const ESTADO_MAP = {
  BORRADOR:        { label: 'Borrador',    variant: 'info'    },
  PENDIENTE_ENVIO: { label: 'Pend. envío', variant: 'warning' },
  ENVIADO:         { label: 'Enviado',     variant: 'info'    },
  ACEPTADO:        { label: 'Aceptado',    variant: 'success' },
  OBSERVADO:       { label: 'Observado',   variant: 'warning' },
  RECHAZADO:       { label: 'Rechazado',   variant: 'error'   },
  ANULADO:         { label: 'Anulado',     variant: 'error'   },
  // aliases de compatibilidad
  EMITIDO:         { label: 'Emitido',     variant: 'success' },
  PENDIENTE:       { label: 'Pendiente',   variant: 'warning' },
};

const INNER_TABS = [
  { key: 'boletas',  label: 'Boletas',       icon: FileText    },
  { key: 'facturas', label: 'Facturas',       icon: Users       },
  { key: 'sunat',    label: 'Análisis SUNAT', icon: BarChart2   },
];

const DateBar = ({ fechaDesde, onDesde, fechaHasta, onHasta, onCSV, onPDF }) => (
  <div className="flex flex-wrap items-center gap-3">
    <div className="flex items-center gap-2">
      <Calendar size={15} className="text-gray-400" />
      <input type="date" value={fechaDesde} onChange={(e) => onDesde(e.target.value)}
        className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
      <span className="text-gray-400 text-sm">–</span>
      <input type="date" value={fechaHasta} onChange={(e) => onHasta(e.target.value)}
        className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
    </div>
    {onCSV && <Button variant="outline" size="sm" onClick={onCSV}><Download size={14} /> CSV</Button>}
    {onPDF && <Button variant="outline" size="sm" onClick={onPDF}><Download size={14} /> PDF</Button>}
  </div>
);

export const ComprobantesReporteTab = () => {
  const { data: comprobantes = [], isLoading } = useReporteComprobantes();
  const [activeTab,  setActiveTab]  = useState('boletas');
  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [page, setPage] = useState(1);
  const PAGE_SIZE = 10;

  /* Filtrado por fecha compartido */
  const byDate = useMemo(() => {
    let r = comprobantes;
    if (fechaDesde) r = r.filter((c) => new Date(getFecha(c)) >= new Date(fechaDesde));
    if (fechaHasta) {
      const h = new Date(fechaHasta); h.setHours(23, 59, 59, 999);
      r = r.filter((c) => new Date(getFecha(c)) <= h);
    }
    return r;
  }, [comprobantes, fechaDesde, fechaHasta]);

  const boletas  = useMemo(() => byDate.filter((c) => getTipo(c) === 'BOLETA'),  [byDate]);
  const facturas = useMemo(() => byDate.filter((c) => getTipo(c) === 'FACTURA'), [byDate]);

  const stats = useMemo(() => ({
    total:     byDate.length,
    boletas:   boletas.length,
    facturas:  facturas.length,
    monto:     byDate.reduce((a, c) => a + getTotal(c), 0),
    pendientes: byDate.filter((c) => getEstado(c) === 'PENDIENTE_ENVIO').length,
    anulados:   byDate.filter((c) => getEstado(c) === 'ANULADO').length,
  }), [byDate, boletas, facturas]);

  /* ── BOLETAS ── */
  const filteredBoletas = useMemo(() => {
    if (!debouncedSearch) return boletas;
    const q = debouncedSearch.toLowerCase();
    return boletas.filter((c) =>
      (getSerie(c) || '').toLowerCase().includes(q) ||
      (getNumero(c) || '').toLowerCase().includes(q) ||
      getCliente(c).toLowerCase().includes(q),
    );
  }, [boletas, debouncedSearch]);

  const seriesBoletas = useMemo(() => {
    const g = {};
    boletas.forEach((c) => {
      const s = getSerie(c) || 'S/N';
      if (!g[s]) g[s] = { serie: s, cantidad: 0, monto: 0, ultimoNumero: '' };
      g[s].cantidad++;
      g[s].monto += getTotal(c);
      const num = getNumero(c) || '';
      if (!g[s].ultimoNumero || num > g[s].ultimoNumero)
        g[s].ultimoNumero = num || '—';
    });
    return Object.values(g).sort((a, b) => b.cantidad - a.cantidad);
  }, [boletas]);

  /* ── FACTURAS ── */
  const filteredFacturas = useMemo(() => {
    if (!debouncedSearch) return facturas;
    const q = debouncedSearch.toLowerCase();
    return facturas.filter((c) =>
      (getSerie(c) || '').toLowerCase().includes(q) ||
      (getNumero(c) || '').toLowerCase().includes(q) ||
      getCliente(c).toLowerCase().includes(q) ||
      getDocumento(c).toLowerCase().includes(q),
    );
  }, [facturas, debouncedSearch]);

  const topClientes = useMemo(() => {
    const g = {};
    facturas.forEach((c) => {
      const key = getDocumento(c) || getCliente(c);
      if (!g[key]) g[key] = { doc: getDocumento(c), name: getCliente(c), cantidad: 0, monto: 0 };
      g[key].cantidad++;
      g[key].monto += getTotal(c);
    });
    return Object.values(g).sort((a, b) => b.monto - a.monto).slice(0, 10);
  }, [facturas]);

  const pendientesEnvio = useMemo(
    () => facturas.filter((c) => getEstado(c) === 'PENDIENTE_ENVIO'),
  );

  /* ── SUNAT ── */
  const dailySummary = useMemo(() => {
    const g = {};
    byDate.forEach((c) => {
      const day = new Date(getFecha(c)).toLocaleDateString('es-PE', {
        day: '2-digit', month: '2-digit', year: 'numeric',
      });
      if (!g[day]) g[day] = { fecha: day, boletas: 0, facturas: 0, total: 0 };
      if (getTipo(c) === 'BOLETA')  g[day].boletas++;
      if (getTipo(c) === 'FACTURA') g[day].facturas++;
      g[day].total += getTotal(c);
    });
    return Object.values(g).sort((a, b) => (a.fecha < b.fecha ? 1 : -1));
  }, [byDate]);

  const peakHours = useMemo(() => {
    const counts = Array(24).fill(0);
    byDate.forEach((c) => { counts[new Date(getFecha(c)).getHours()]++; });
    const max = Math.max(...counts, 1);
    return counts.map((v, h) => ({
      hora: `${String(h).padStart(2, '0')}:00`, count: v, pct: Math.round((v / max) * 100),
    }));
  }, [byDate]);

  const paginated = useMemo(() => {
    const src = activeTab === 'boletas' ? filteredBoletas : filteredFacturas;
    return src.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);
  }, [activeTab, filteredBoletas, filteredFacturas, page]);

  /* Columnas comunes para ambas tablas de detalle */
  const commonCols = [
    { key:'#',  title:'#', width:'50px', render:(_,__,i) => (page-1)*PAGE_SIZE + i + 1 },
    { key:'sn', title:'Serie-Número', render:(_,r) => (
      <span className="font-mono text-sm">{getSerie(r)||'—'}-{getNumero(r)||'—'}</span>
    )},
    { key:'f',  title:'Fecha', render:(_,r) => formatDate(getFecha(r)) },
    { key:'cl', title:'Cliente', render:(_,r) => (
      <div>
        <p className="text-sm">{getCliente(r)}</p>
        <p className="text-xs text-gray-400">{getDocumento(r)}</p>
      </div>
    )},
    { key:'t',  title:'Total', render:(_,r) => (
      <span className="font-semibold">{formatCurrency(getTotal(r))}</span>
    )},
    { key:'e',  title:'Estado', render:(_,r) => {
      const info = ESTADO_MAP[getEstado(r)] || { label: r.estado, variant:'info' };
      return <Badge variant={info.variant}>{info.label}</Badge>;
    }},
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Comprobantes de Pago</h1>
        <p className="text-gray-600 mt-1">Boletas, facturas electrónicas emitidas y análisis SUNAT</p>
      </div>

      {/* Stats globales */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total comprobantes"    value={stats.total}                       icon={FileText}      />
        <StatCard title="Monto total"           value={formatCurrency(stats.monto)}        icon={DollarSign}    />
        <StatCard title="Boletas / Facturas"    value={`${stats.boletas} / ${stats.facturas}`} icon={CheckCircle} />
        <StatCard title="Pendientes de envío"   value={stats.pendientes}                   icon={AlertTriangle} />
      </div>

      {/* Pestañas internas */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex gap-6">
          {INNER_TABS.map((tab) => {
            const active = activeTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => { setActiveTab(tab.key); setPage(1); setSearchTerm(''); }}
                className={`flex items-center gap-2 py-3 px-1 border-b-2 text-sm font-medium transition-colors ${
                  active
                    ? 'border-green-600 text-green-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <tab.icon size={16} />
                {tab.label}
              </button>
            );
          })}
        </nav>
      </div>

      {/* ── TAB: Boletas ── */}
      {activeTab === 'boletas' && (
        <div className="space-y-4">
          <DateBar
            fechaDesde={fechaDesde} onDesde={setFechaDesde}
            fechaHasta={fechaHasta} onHasta={setFechaHasta}
            onCSV={() => exportToCSV(filteredBoletas.map((c) => ({
              'Serie-Número': `${getSerie(c)||''}-${getNumero(c)||''}`,
              Fecha: formatDate(getFecha(c)),
              Cliente: getCliente(c), Documento: getDocumento(c),
              Total: getTotal(c), Estado: ESTADO_MAP[getEstado(c)]?.label || c.estado || '',
            })), 'reporte_boletas')}
            onPDF={() => exportToPDF('Boletas de Venta', filteredBoletas.map((c) => ({
              'Serie-Número': `${getSerie(c)||''}-${getNumero(c)||''}`,
              Fecha: formatDate(getFecha(c)), Cliente: getCliente(c),
              Total: formatCurrency(getTotal(c)), Estado: ESTADO_MAP[getEstado(c)]?.label || c.estadoDocumento || '',
            })))}
          />

          <Card>
            <h2 className="text-base font-semibold text-gray-800 mb-4">Control de series de boletas</h2>
            <Table
              columns={[
                { key:'s',  title:'Serie',       dataIndex:'serie'        },
                { key:'c',  title:'Cantidad',     dataIndex:'cantidad'     },
                { key:'m',  title:'Monto Total',  render:(_,r) => <span className="font-semibold">{formatCurrency(r.monto)}</span> },
                { key:'ul', title:'Último N°',    dataIndex:'ultimoNumero' },
              ]}
              data={seriesBoletas} loading={isLoading} emptyText="Sin series de boletas"
            />
          </Card>

          <Card>
            <div className="flex flex-wrap items-center gap-3 mb-4">
              <div className="relative flex-1 min-w-[200px] max-w-sm">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                <input type="text" value={searchTerm}
                  onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
                  placeholder="Buscar serie, número, cliente..."
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>
            </div>
            <Table
              columns={commonCols} data={paginated} loading={isLoading} emptyText="Sin boletas"
              pagination={{ current: page, pageSize: PAGE_SIZE, total: filteredBoletas.length, onChange: setPage }}
            />
          </Card>
        </div>
      )}

      {/* ── TAB: Facturas ── */}
      {activeTab === 'facturas' && (
        <div className="space-y-4">
          <DateBar
            fechaDesde={fechaDesde} onDesde={setFechaDesde}
            fechaHasta={fechaHasta} onHasta={setFechaHasta}
            onCSV={() => exportToCSV(filteredFacturas.map((c) => ({
              'Serie-Número': `${getSerie(c)||''}-${getNumero(c)||''}`,
              Fecha: formatDate(getFecha(c)), Cliente: getCliente(c),
              RUC: getDocumento(c), Total: getTotal(c),
              Estado: ESTADO_MAP[getEstado(c)]?.label || c.estado || '',
            })), 'reporte_facturas')}
            onPDF={() => exportToPDF('Facturas de Venta', filteredFacturas.map((c) => ({
              'Serie-Número': `${getSerie(c)||''}-${getNumero(c)||''}`,
              Fecha: formatDate(getFecha(c)), Cliente: getCliente(c),
              RUC: getDocumento(c), Total: formatCurrency(getTotal(c)),
              Estado: ESTADO_MAP[getEstado(c)]?.label || c.estado || '',
            })))}
          />

          {pendientesEnvio.length > 0 && (
            <div className="bg-yellow-50 border border-yellow-300 rounded-xl p-4 flex items-start gap-3">
              <AlertTriangle size={20} className="text-yellow-600 shrink-0 mt-0.5" />
              <div>
                <p className="font-semibold text-yellow-800">
                  {pendientesEnvio.length} factura{pendientesEnvio.length > 1 ? 's' : ''} pendiente{pendientesEnvio.length > 1 ? 's' : ''} de envío a SUNAT
                </p>
                <p className="text-sm text-yellow-700 mt-0.5">
                  Revise y envíe los comprobantes antes del vencimiento.
                </p>
              </div>
            </div>
          )}

          {topClientes.length > 0 && (
            <Card>
              <h2 className="text-base font-semibold text-gray-800 mb-4">Top 10 clientes corporativos</h2>
              <Table
                columns={[
                  { key:'r', title:'#',         width:'50px', render:(_,__,i) => i+1 },
                  { key:'n', title:'Cliente',   dataIndex:'name'     },
                  { key:'d', title:'RUC',       dataIndex:'doc'      },
                  { key:'c', title:'Facturas',  dataIndex:'cantidad' },
                  { key:'m', title:'Monto Total', render:(_,r) => <span className="font-semibold">{formatCurrency(r.monto)}</span> },
                ]}
                data={topClientes} loading={isLoading} emptyText="Sin facturas en el período"
              />
            </Card>
          )}

          <Card>
            <div className="flex flex-wrap items-center gap-3 mb-4">
              <div className="relative flex-1 min-w-[200px] max-w-sm">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                <input type="text" value={searchTerm}
                  onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
                  placeholder="Buscar serie, número, cliente, RUC..."
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>
            </div>
            <Table
              columns={commonCols} data={paginated} loading={isLoading} emptyText="Sin facturas"
              pagination={{ current: page, pageSize: PAGE_SIZE, total: filteredFacturas.length, onChange: setPage }}
            />
          </Card>
        </div>
      )}

      {/* ── TAB: Análisis SUNAT ── */}
      {activeTab === 'sunat' && (
        <div className="space-y-4">
          <DateBar
            fechaDesde={fechaDesde} onDesde={setFechaDesde}
            fechaHasta={fechaHasta} onHasta={setFechaHasta}
            onCSV={() => exportToCSV(dailySummary.map((d) => ({
              Fecha: d.fecha, Boletas: d.boletas,
              Facturas: d.facturas, 'Total S/': d.total,
            })), 'resumen_diario_sunat')}
          />

          <Card>
            <h2 className="text-base font-semibold text-gray-800 mb-4">Resumen diario de comprobantes</h2>
            <Table
              columns={[
                { key:'f',  title:'Fecha',    dataIndex:'fecha'    },
                { key:'b',  title:'Boletas',  dataIndex:'boletas'  },
                { key:'fa', title:'Facturas', dataIndex:'facturas' },
                { key:'t',  title:'Total del día', render:(_,r) => <span className="font-semibold">{formatCurrency(r.total)}</span> },
              ]}
              data={dailySummary.slice(0, 30)} loading={isLoading}
              emptyText="Sin comprobantes en el período"
            />
          </Card>

          <Card>
            <h2 className="text-base font-semibold text-gray-800 mb-4">Horas pico de emisión</h2>
            <div className="space-y-1.5">
              {peakHours.filter((h) => h.count > 0).map((h) => (
                <div key={h.hora} className="flex items-center gap-3">
                  <span className="w-14 text-xs text-gray-500 text-right shrink-0">{h.hora}</span>
                  <div className="flex-1 bg-gray-100 rounded-full h-4">
                    <div className="bg-green-500 h-4 rounded-full transition-all" style={{ width: `${h.pct}%` }} />
                  </div>
                  <span className="w-8 text-xs text-gray-600 text-right shrink-0">{h.count}</span>
                </div>
              ))}
              {peakHours.every((h) => h.count === 0) && (
                <p className="text-sm text-gray-400 text-center py-4">Sin datos en el período seleccionado</p>
              )}
            </div>
          </Card>
        </div>
      )}
    </div>
  );
};

