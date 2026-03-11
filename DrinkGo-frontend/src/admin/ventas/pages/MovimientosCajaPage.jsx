/**
 * MovimientosCajaPage.jsx
 * Control de ingresos y egresos del turno activo + historial por caja.
 *
 * Selector de caja arriba:
 *   - Caja del turno activo  -> vista completa (StatCards, desglose, tabla, registrar)
 *   - Otra caja              -> lista de sesiones, clic en una -> vista completa de esa sesion
 */
import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Plus,
  Minus,
  ArrowDownUp,
  TrendingUp,
  TrendingDown,
  Wallet,
  AlertCircle,
  LogIn,
  Filter,
  ShoppingCart,
  Banknote,
  Clock,
  CheckCircle,
  AlertTriangle,
  ArrowLeft,
  ChevronRight,
  Search,
  Calendar,
  User,
  Monitor,
  Undo2,
  Eye,
} from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Input } from '@/admin/components/ui/Input';
import { MovimientoCajaModal } from '../components/MovimientoCajaModal';
import {
  useCajas,
  useSesionActiva,
  useResumenTurno,
  useMovimientos,
  useSesionesByCaja,
} from '../hooks/useCajas';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { SinCajaAsignada } from '../components/SinCajaAsignada';

/* -------------------------------------------------- */
/*  Constantes                                        */
/* -------------------------------------------------- */

const TIPO_CONFIG = {
  ingreso_venta: { label: 'Venta', variant: 'success', sign: '+' },
  ingreso_otro: { label: 'Ingreso manual', variant: 'info', sign: '+' },
  ingreso_manual: { label: 'Ingreso', variant: 'info', sign: '+' },
  egreso_gasto: { label: 'Gasto', variant: 'error', sign: '-' },
  egreso_otro: { label: 'Egreso otro', variant: 'warning', sign: '-' },
  egreso_manual: { label: 'Egreso', variant: 'error', sign: '-' },
  apertura: { label: 'Apertura', variant: 'info', sign: '' },
  cierre: { label: 'Cierre', variant: 'info', sign: '' },
};

const FILTRO_OPTIONS = [
  { value: 'todos', label: 'Todos' },
  { value: 'ingresos', label: 'Solo Ingresos' },
  { value: 'egresos', label: 'Solo Egresos' },
  { value: 'ingreso_venta', label: 'Ventas' },
  { value: 'manuales_ingreso', label: 'Ingresos manuales' },
  { value: 'manuales_egreso', label: 'Egresos manuales' },
];

const ESTADO_SESION = {
  abierta: { label: 'Abierta', variant: 'info' },
  cerrada: { label: 'Cerrada', variant: 'success' },
  con_diferencia: { label: 'Con diferencia', variant: 'warning' },
};

/* -------------------------------------------------- */
/*  Sub-componente: Vista completa de una sesion       */
/*  (reutilizado tanto para turno activo como histor.) */
/* -------------------------------------------------- */

const SesionFullView = ({ sesionData, columns, isActive = false }) => {
  const { movimientos, isLoading } = useMovimientos(sesionData?.id);
  const [filtroTipo, setFiltroTipo] = useState('todos');

  const movFiltrados = useMemo(() => {
    if (filtroTipo === 'todos') return movimientos;
    if (filtroTipo === 'ingresos') return movimientos.filter((m) => m.tipoMovimiento?.startsWith('ingreso'));
    if (filtroTipo === 'egresos') return movimientos.filter((m) => m.tipoMovimiento?.startsWith('egreso'));
    if (filtroTipo === 'manuales_ingreso') return movimientos.filter((m) => m.tipoMovimiento === 'ingreso_otro' || m.tipoMovimiento === 'ingreso_manual');
    if (filtroTipo === 'manuales_egreso') return movimientos.filter((m) => m.tipoMovimiento === 'egreso_otro' || m.tipoMovimiento === 'egreso_manual' || m.tipoMovimiento === 'egreso_gasto');
    return movimientos.filter((m) => m.tipoMovimiento === filtroTipo);
  }, [movimientos, filtroTipo]);

  const totalIngresos = useMemo(() => {
    return movimientos.filter((m) => m.tipoMovimiento?.startsWith('ingreso')).reduce((s, m) => s + (m.monto || 0), 0);
  }, [movimientos]);

  const totalEgresos = useMemo(() => {
    return movimientos.filter((m) => m.tipoMovimiento?.startsWith('egreso')).reduce((s, m) => s + (m.monto || 0), 0);
  }, [movimientos]);

  const montoAp = sesionData?.montoApertura ?? 0;
  const saldo = montoAp + totalIngresos - totalEgresos;

  const estadoCfg = ESTADO_SESION[sesionData?.estadoSesion] || ESTADO_SESION.cerrada;
  const usuario = sesionData?.usuario
    ? `${sesionData.usuario.nombres || ''} ${sesionData.usuario.apellidos || ''}`.trim()
    : '\u2014';

  return (
    <>
      {/* Banner de sesion */}
      {isActive ? (
        <div className="flex items-center gap-2 text-sm text-gray-500 bg-green-50 border border-green-200 rounded-lg px-4 py-2">
          <Clock size={16} className="text-green-600" />
          <span>
            <span className="font-medium text-green-700">Turno activo</span> &middot; Caja: {sesionData?.caja?.nombreCaja || '\u2014'} &middot; Desde: {formatDateTime(sesionData?.fechaApertura)}
          </span>
        </div>
      ) : (
        <div className="flex items-center gap-2 text-sm bg-gray-50 border border-gray-200 rounded-lg px-4 py-2">
          <Clock size={16} className="text-gray-400" />
          <span className="text-gray-600">
            <span className="font-medium text-gray-800">Sesion historica</span>
            <Badge variant={estadoCfg.variant} className="ml-2">{estadoCfg.label}</Badge>
            <span className="ml-2">&middot; {formatDateTime(sesionData?.fechaApertura)}</span>
            {sesionData?.fechaCierre && <span> &rarr; {formatDateTime(sesionData.fechaCierre)}</span>}
            <span className="ml-2">&middot; Usuario: {usuario}</span>
          </span>
        </div>
      )}

      {/* StatCards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Monto Apertura" value={formatCurrency(montoAp)} icon={Wallet} />
        <StatCard title="Total Ingresos" value={formatCurrency(totalIngresos)} icon={TrendingUp} className="!border-green-200" />
        <StatCard title="Total Egresos" value={formatCurrency(totalEgresos)} icon={TrendingDown} className="!border-red-200" />
        <StatCard title="Saldo Actual" value={formatCurrency(saldo)} icon={Banknote} className="!border-blue-200" />
      </div>

      {/* Desglose */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        <Card className="!p-3">
          <div className="flex items-center gap-2 mb-1">
            <ShoppingCart size={14} className="text-green-500" />
            <span className="text-xs font-medium text-gray-500">Ventas</span>
          </div>
          <p className="text-lg font-bold text-gray-900">
            {formatCurrency(movimientos.filter((m) => m.tipoMovimiento === 'ingreso_venta').reduce((s, m) => s + (m.monto || 0), 0))}
          </p>
          <p className="text-xs text-gray-400">
            {movimientos.filter((m) => m.tipoMovimiento === 'ingreso_venta').length} completadas
          </p>
        </Card>
        <Card className="!p-3">
          <div className="flex items-center gap-2 mb-1">
            <Plus size={14} className="text-blue-500" />
            <span className="text-xs font-medium text-gray-500">Ingresos manuales</span>
          </div>
          <p className="text-lg font-bold text-gray-900">
            {formatCurrency(movimientos.filter((m) => m.tipoMovimiento === 'ingreso_otro').reduce((s, m) => s + (m.monto || 0), 0))}
          </p>
        </Card>
        <Card className="!p-3">
          <div className="flex items-center gap-2 mb-1">
            <Minus size={14} className="text-red-500" />
            <span className="text-xs font-medium text-gray-500">Egresos / Gastos</span>
          </div>
          <p className="text-lg font-bold text-gray-900">
            {formatCurrency(totalEgresos)}
          </p>
        </Card>
        <Card className="!p-3">
          <div className="flex items-center gap-2 mb-1">
            <ArrowDownUp size={14} className="text-purple-500" />
            <span className="text-xs font-medium text-gray-500">Total movimientos</span>
          </div>
          <p className="text-lg font-bold text-gray-900">{movimientos.length}</p>
        </Card>
      </div>

      {/* Tabla de movimientos */}
      <Card>
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
          <div className="flex items-center gap-2">
            <Filter size={16} className="text-gray-400" />
            <select
              value={filtroTipo}
              onChange={(e) => setFiltroTipo(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              {FILTRO_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
            <span className="text-sm text-gray-400">{movFiltrados.length} de {movimientos.length}</span>
          </div>
        </div>
        <Table columns={columns} data={movFiltrados} loading={isLoading} pagination={{ current: 1, pageSize: 50, total: movFiltrados.length }} />
      </Card>
    </>
  );
};

/* -------------------------------------------------- */
/*  Componente principal                              */
/* -------------------------------------------------- */

export const MovimientosCajaPage = () => {
  const navigate = useNavigate();
  const { user } = useAdminAuthStore();

  /* --- Datos base --- */
  const { cajas, isLoading: loadingCajas, alcanceCajas, cajaAsignada } = useCajas();
  const esSoloCaja = alcanceCajas === 'caja_asignada';

  const { sesion, hasSesion, isLoading: loadingSesion } = useSesionActiva();
  const { resumen } = useResumenTurno(sesion?.id);
  const { movimientos, isLoading: loadingMov, registrar, isRegistrando, devolverEgreso, isDevolviendo } = useMovimientos(sesion?.id);

  /* --- Selector de caja --- */
  const activeCajaId = sesion?.caja?.id;
  const [selectedCajaId, setSelectedCajaId] = useState(null);

  // Si no se ha seleccionado nada, usar la caja activa (o la primera disponible)
  const effectiveCajaId = selectedCajaId ?? activeCajaId ?? (cajas.length > 0 ? cajas[0].id : null);
  const isViewingActiveCaja = hasSesion && effectiveCajaId === activeCajaId;

  /* --- Sesiones historicas de la caja seleccionada --- */
  const { sesiones: sesionesCaja, isLoading: loadingSesiones } = useSesionesByCaja(effectiveCajaId);

  // Filtrar sesiones: excluir la sesión activa actual, y si es cajero solo mostrar sus propias sesiones
  const sesionesHistoricas = useMemo(() => {
    let filtered = sesionesCaja.filter((s) => s.id !== sesion?.id);
    // Si alcance es caja_asignada, solo mostrar las sesiones del propio usuario
    if (esSoloCaja && user?.id) {
      filtered = filtered.filter((s) => s.usuario?.id === user.id);
    }
    return filtered;
  }, [sesionesCaja, sesion?.id, esSoloCaja, user?.id]);

  /* --- Sesion historica seleccionada (vista completa) --- */
  const [selectedSesionId, setSelectedSesionId] = useState(null);
  const selectedSesion = useMemo(() => {
    if (!selectedSesionId) return null;
    return sesionesHistoricas.find((s) => s.id === selectedSesionId) || null;
  }, [selectedSesionId, sesionesHistoricas]);

  /* --- UI --- */
  const [showRegistrarModal, setShowRegistrarModal] = useState(false);
  const [showSesionesAnteriores, setShowSesionesAnteriores] = useState(false);
  const [descripcionModal, setDescripcionModal] = useState(null);
  const [filtroTipo, setFiltroTipo] = useState('todos');
  const [devolverTarget, setDevolverTarget] = useState(null);
  const [devolverMonto, setDevolverMonto] = useState('');
  const [devolverMotivo, setDevolverMotivo] = useState('');

  /* --- Filtros historial de sesiones --- */
  const [filtroEstadoHist, setFiltroEstadoHist] = useState('todos');
  const [filtroBusquedaHist, setFiltroBusquedaHist] = useState('');
  const [filtroFechaDesdeHist, setFiltroFechaDesdeHist] = useState('');
  const [filtroFechaHastaHist, setFiltroFechaHastaHist] = useState('');
  const [paginaHist, setPaginaHist] = useState(1);
  const PAGE_SIZE_HIST = 8;

  /* Si es cajero con alcance caja_asignada pero sin caja asignada -> bloquear */
  if (esSoloCaja && !cajaAsignada) {
    return <SinCajaAsignada titulo="Movimientos de Caja" />;
  }

  /* --- Filtrado de sesiones historicas --- */
  const sesionesHistoricasFiltradas = useMemo(() => {
    let result = sesionesHistoricas;
    if (filtroEstadoHist !== 'todos') {
      result = result.filter((s) => s.estadoSesion === filtroEstadoHist);
    }
    if (filtroBusquedaHist.trim()) {
      const q = filtroBusquedaHist.toLowerCase();
      result = result.filter((s) =>
        `${s.usuario?.nombres || ''} ${s.usuario?.apellidos || ''}`.toLowerCase().includes(q),
      );
    }
    if (filtroFechaDesdeHist) {
      const desde = new Date(filtroFechaDesdeHist);
      desde.setHours(0, 0, 0, 0);
      result = result.filter((s) => new Date(s.fechaApertura) >= desde);
    }
    if (filtroFechaHastaHist) {
      const hasta = new Date(filtroFechaHastaHist);
      hasta.setHours(23, 59, 59, 999);
      result = result.filter((s) => new Date(s.fechaApertura) <= hasta);
    }
    return result;
  }, [sesionesHistoricas, filtroEstadoHist, filtroBusquedaHist, filtroFechaDesdeHist, filtroFechaHastaHist]);

  const sesionesHistPage = useMemo(() => {
    const start = (paginaHist - 1) * PAGE_SIZE_HIST;
    return sesionesHistoricasFiltradas.slice(start, start + PAGE_SIZE_HIST);
  }, [sesionesHistoricasFiltradas, paginaHist]);

  const totalPaginasHist = Math.max(1, Math.ceil(sesionesHistoricasFiltradas.length / PAGE_SIZE_HIST));

  /* --- Filtrar movimientos del turno activo --- */
  const movimientosFiltrados = useMemo(() => {
    if (filtroTipo === 'todos') return movimientos;
    if (filtroTipo === 'ingresos') return movimientos.filter((m) => m.tipoMovimiento?.startsWith('ingreso'));
    if (filtroTipo === 'egresos') return movimientos.filter((m) => m.tipoMovimiento?.startsWith('egreso'));
    if (filtroTipo === 'manuales_ingreso') return movimientos.filter((m) => m.tipoMovimiento === 'ingreso_otro' || m.tipoMovimiento === 'ingreso_manual');
    if (filtroTipo === 'manuales_egreso') return movimientos.filter((m) => m.tipoMovimiento === 'egreso_otro' || m.tipoMovimiento === 'egreso_manual' || m.tipoMovimiento === 'egreso_gasto');
    return movimientos.filter((m) => m.tipoMovimiento === filtroTipo);
  }, [movimientos, filtroTipo]);

  /* --- Calculos turno activo --- */
  const totalIngresos = useMemo(() => {
    return movimientos.filter((m) => m.tipoMovimiento?.startsWith('ingreso')).reduce((s, m) => s + (m.monto || 0), 0);
  }, [movimientos]);

  const totalEgresos = useMemo(() => {
    return movimientos.filter((m) => m.tipoMovimiento?.startsWith('egreso')).reduce((s, m) => s + (m.monto || 0), 0);
  }, [movimientos]);

  const montoApertura = resumen?.sesion?.montoApertura ?? 0;
  const saldoActual = montoApertura + totalIngresos - totalEgresos;

  const handleRegistrar = async (data) => {
    await registrar(data);
    setShowRegistrarModal(false);
  };

  const handleDevolver = async () => {
    if (!devolverTarget || !devolverMonto) return;
    try {
      await devolverEgreso({ movimientoId: devolverTarget.id, monto: parseFloat(devolverMonto), motivo: devolverMotivo });
      setDevolverTarget(null);
    } catch (err) {
      console.error('Error devolviendo egreso:', err);
    }
  };

  const maxDevolucion = devolverTarget ? (devolverTarget.monto - (devolverTarget.montoDevuelto || 0)) : 0;

  /* --- Columnas de la tabla (reutilizable) --- */
  const columns = [
    { key: 'index', title: '#', width: '50px', render: (_, __, i) => i + 1 },
    {
      key: 'fecha',
      title: 'Fecha / Hora',
      render: (_, row) => <span className="text-sm text-gray-600">{formatDateTime(row.fechaMovimiento)}</span>,
    },
    {
      key: 'tipo',
      title: 'Tipo',
      render: (_, row) => {
        const c = TIPO_CONFIG[row.tipoMovimiento] || { label: row.tipoMovimiento, variant: 'info' };
        return <Badge variant={c.variant}>{c.label}</Badge>;
      },
    },
    {
      key: 'categoria',
      title: 'Categoria',
      render: (_, row) => {
        if (row.categoriaGasto?.nombre) return row.categoriaGasto.nombre;
        if (row.tipoMovimiento === 'ingreso_venta') return 'Venta POS';
        if (row.descripcion?.startsWith('Devoluci\u00f3n:') || row.descripcion?.startsWith('Devolucion:')) return 'Devoluci\u00f3n';
        if (row.tipoMovimiento?.startsWith('ingreso')) return 'Ingreso';
        if (row.tipoMovimiento === 'egreso_manual' || row.tipoMovimiento === 'egreso_gasto' || row.tipoMovimiento === 'egreso_otro') return 'Egreso';
        if (row.tipoMovimiento === 'apertura') return 'Apertura';
        if (row.tipoMovimiento === 'cierre') return 'Cierre';
        return '\u2014';
      },
    },
    {
      key: 'descripcion',
      title: 'Descripcion',
      render: (_, row) => {
        const desc = row.descripcion?.replace(/^Devoluci\u00f3n:\s*/i, '').replace(/^Devolucion:\s*/i, '') || null;
        return desc ? (
          <button
            onClick={(e) => { e.stopPropagation(); setDescripcionModal(row); }}
            className="text-sm text-gray-700 truncate max-w-[220px] block text-left hover:text-green-700 hover:underline transition-colors cursor-pointer"
          >
            {desc}
          </button>
        ) : (
          <span className="text-sm text-gray-400">&mdash;</span>
        );
      },
    },
    {
      key: 'monto',
      title: 'Monto',
      align: 'right',
      render: (_, row) => {
        const c = TIPO_CONFIG[row.tipoMovimiento] || { sign: '' };
        const neg = c.sign === '-';
        const pos = c.sign === '+';
        return (
          <span className={`font-semibold whitespace-nowrap ${neg ? 'text-red-600' : pos ? 'text-green-600' : 'text-gray-700'}`}>
            {c.sign}{formatCurrency(row.monto)}
          </span>
        );
      },
    },
    ...(isViewingActiveCaja ? [{
      key: 'acciones',
      title: '',
      width: '90px',
      align: 'center',
      render: (_, row) => {
        const esEgreso = row.tipoMovimiento?.startsWith('egreso');
        if (!esEgreso) return null;
        if (row.estadoEgreso === 'devuelto') {
          return <Badge variant="info" className="text-[10px] whitespace-nowrap">Devuelto</Badge>;
        }
        if (row.estadoEgreso === 'parcial') {
          return <Badge variant="warning" className="text-[10px] whitespace-nowrap">Parcial</Badge>;
        }
        if (row.venta) return null;
        return (
          <button
            onClick={() => { setDevolverTarget(row); setDevolverMonto(''); setDevolverMotivo(''); }}
            className="inline-flex items-center gap-1 px-2 py-1 text-xs text-amber-700 bg-amber-50 hover:bg-amber-100 border border-amber-200 rounded-md transition-colors"
            title="Devolver egreso"
          >
            <Undo2 size={12} />
            Devolver
          </button>
        );
      },
    }] : []),
  ];

  /* --- Nombre de la caja seleccionada --- */
  const selectedCajaName = useMemo(() => {
    const c = cajas.find((x) => x.id === effectiveCajaId);
    return c?.nombreCaja || '';
  }, [cajas, effectiveCajaId]);

  /* -------------------------------------------------- */
  /*  LOADING                                           */
  /* -------------------------------------------------- */

  if (loadingSesion || loadingCajas) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-4 border-green-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  /* -------------------------------------------------- */
  /*  SIN CAJAS REGISTRADAS                             */
  /* -------------------------------------------------- */

  if (cajas.length === 0) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Movimientos de Caja</h1>
          <p className="text-gray-600 mt-1">Control de ingresos y egresos del turno</p>
        </div>
        <div className="flex flex-col items-center justify-center bg-white rounded-xl border border-gray-200 shadow-sm py-16 px-6">
          <div className="w-20 h-20 bg-amber-100 rounded-full flex items-center justify-center mb-6">
            <AlertCircle size={40} className="text-amber-500" />
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">No hay cajas registradas</h2>
          <p className="text-gray-500 text-center max-w-md mb-6">
            Primero debes crear una caja registradora para poder ver movimientos.
          </p>
          <Button onClick={() => navigate('/admin/ventas/cajas')} className="px-6 py-3">
            <LogIn size={20} className="mr-2" />
            Ir a Cajas
          </Button>
        </div>
      </div>
    );
  }

  /* -------------------------------------------------- */
  /*  RENDER                                            */
  /* -------------------------------------------------- */

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Movimientos de Caja</h1>
          <p className="text-gray-600 mt-1">Control de ingresos y egresos del turno</p>
        </div>

      </div>

      {/* Banner alcance restringido */}
      {esSoloCaja && cajaAsignada && (
        <div className="flex items-center gap-2 bg-amber-50 border border-amber-200 rounded-lg px-4 py-3">
          <Monitor size={16} className="text-amber-600 shrink-0" />
          <p className="text-sm text-amber-700">
            Mostrando solo los movimientos de tu caja: <strong>{cajaAsignada.nombreCaja}</strong>
          </p>
        </div>
      )}

      {/* Selector de caja */}
      <Card className="!p-4">
        <div className="flex items-center gap-3">
          <label className="text-sm font-medium text-gray-700 whitespace-nowrap">
            Ver movimientos de:
          </label>
          <select
            value={effectiveCajaId || ''}
            onChange={(e) => {
              setSelectedCajaId(Number(e.target.value));
              setSelectedSesionId(null);
              setShowSesionesAnteriores(false);
              setFiltroTipo('todos');
              setFiltroEstadoHist('todos');
              setFiltroBusquedaHist('');
              setFiltroFechaDesdeHist('');
              setFiltroFechaHastaHist('');
            }}
            disabled={esSoloCaja}
            className="flex-1 max-w-sm border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
          >
            {cajas.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombreCaja}
                {hasSesion && c.id === activeCajaId ? ' (Turno activo)' : ''}
              </option>
            ))}
          </select>
          {isViewingActiveCaja && sesionesHistoricas.length > 0 && (
            <Button
              size="sm"
              variant="outline"
              onClick={() => { setShowSesionesAnteriores((v) => !v); setSelectedSesionId(null); }}
            >
              <Clock size={16} className="mr-1" />
              {showSesionesAnteriores ? 'Ocultar sesiones' : 'Ver sesiones anteriores'}
            </Button>
          )}
        </div>
      </Card>

      {/* ================================================= */}
      {/*  VISTA: TURNO ACTIVO DE LA CAJA                    */}
      {/* ================================================= */}
      {isViewingActiveCaja && (
        <>
          {!showSesionesAnteriores && (<>
          {/* Info turno */}
          <div className="flex items-center gap-2 text-sm text-gray-500 bg-green-50 border border-green-200 rounded-lg px-4 py-2">
            <Clock size={16} className="text-green-600" />
            <span>
              <span className="font-medium text-green-700">Turno activo</span> &middot; Caja: {sesion?.caja?.nombreCaja || '\u2014'} &middot; Desde: {formatDateTime(sesion?.fechaApertura)}
            </span>
          </div>

          {/* Resumen Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard title="Monto Apertura" value={formatCurrency(montoApertura)} icon={Wallet} />
            <StatCard title="Total Ingresos" value={formatCurrency(totalIngresos)} icon={TrendingUp} className="!border-green-200" />
            <StatCard title="Total Egresos" value={formatCurrency(totalEgresos)} icon={TrendingDown} className="!border-red-200" />
            <StatCard title="Saldo Actual" value={formatCurrency(saldoActual)} icon={Banknote} className="!border-blue-200" />
          </div>

          {/* Desglose rapido */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <ShoppingCart size={14} className="text-green-500" />
                <span className="text-xs font-medium text-gray-500">Ventas</span>
              </div>
              <p className="text-lg font-bold text-gray-900">{formatCurrency(resumen?.totalEfectivo ?? 0)}</p>
              <p className="text-xs text-gray-400">{resumen?.totalVentasCompletadas ?? 0} completadas</p>
            </Card>
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <Plus size={14} className="text-blue-500" />
                <span className="text-xs font-medium text-gray-500">Ingresos manuales</span>
              </div>
              <p className="text-lg font-bold text-gray-900">{formatCurrency(resumen?.totalIngresos ?? 0)}</p>
            </Card>
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <Minus size={14} className="text-red-500" />
                <span className="text-xs font-medium text-gray-500">Egresos / Gastos</span>
              </div>
              <p className="text-lg font-bold text-gray-900">{formatCurrency(resumen?.totalEgresos ?? 0)}</p>
            </Card>
            <Card className="!p-3">
              <div className="flex items-center gap-2 mb-1">
                <ArrowDownUp size={14} className="text-purple-500" />
                <span className="text-xs font-medium text-gray-500">Total movimientos</span>
              </div>
              <p className="text-lg font-bold text-gray-900">{movimientos.length}</p>
            </Card>
          </div>

          {/* Tabla movimientos */}
          <Card>
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
              <div className="flex items-center gap-2">
                <Filter size={16} className="text-gray-400" />
                <select
                  value={filtroTipo}
                  onChange={(e) => setFiltroTipo(e.target.value)}
                  className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  {FILTRO_OPTIONS.map((opt) => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
                <span className="text-sm text-gray-400">{movimientosFiltrados.length} de {movimientos.length}</span>
              </div>
              <div className="flex items-center gap-2">
                <Button size="sm" onClick={() => setShowRegistrarModal(true)}>
                  <Plus size={16} className="mr-1" />
                  Registrar Movimiento
                </Button>
              </div>
            </div>
            <Table columns={columns} data={movimientosFiltrados} loading={loadingMov} pagination={{ current: 1, pageSize: 50, total: movimientosFiltrados.length }} />
          </Card>
          </>)}

          {/* --- Sesiones anteriores de esta misma caja --- */}
          {showSesionesAnteriores && (
            <>
              <div className="flex items-center justify-between">
                <h3 className="text-base font-semibold text-gray-700 flex items-center gap-2">
                  <Clock size={16} className="text-gray-400" />
                  Sesiones anteriores — {selectedCajaName}
                </h3>
                {selectedSesion && (
                  <button
                    onClick={() => setSelectedSesionId(null)}
                    className="inline-flex items-center gap-1.5 text-sm text-gray-600 hover:text-gray-900 transition-colors"
                  >
                    <ArrowLeft size={16} />
                    Volver al listado de sesiones
                  </button>
                )}
              </div>
              {selectedSesion ? (
                <>
                  <SesionFullView sesionData={selectedSesion} columns={columns} />
                </>
              ) : (
                <>
                  {/* Stats */}
                  <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
                    <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                      <p className="text-xs text-gray-500">Total sesiones</p>
                      <p className="text-xl font-bold text-gray-900">{sesionesHistoricasFiltradas.length}</p>
                    </div>
                    <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                      <div className="flex items-center gap-1 mb-0.5">
                        <Clock size={12} className="text-blue-500" />
                        <p className="text-xs text-gray-500">Abiertas</p>
                      </div>
                      <p className="text-xl font-bold text-blue-600">
                        {sesionesHistoricasFiltradas.filter((s) => s.estadoSesion === 'abierta').length}
                      </p>
                    </div>
                    <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                      <div className="flex items-center gap-1 mb-0.5">
                        <CheckCircle size={12} className="text-green-500" />
                        <p className="text-xs text-gray-500">Cerradas OK</p>
                      </div>
                      <p className="text-xl font-bold text-green-600">
                        {sesionesHistoricasFiltradas.filter((s) => s.estadoSesion === 'cerrada').length}
                      </p>
                    </div>
                    <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                      <div className="flex items-center gap-1 mb-0.5">
                        <AlertTriangle size={12} className="text-amber-500" />
                        <p className="text-xs text-gray-500">Con diferencia</p>
                      </div>
                      <p className="text-xl font-bold text-amber-600">
                        {sesionesHistoricasFiltradas.filter((s) => s.estadoSesion === 'con_diferencia').length}
                      </p>
                    </div>
                  </div>

                  {/* Filtros */}
                  <Card className="!p-4">
                    <div className="flex flex-col lg:flex-row lg:items-end gap-3">
                      <div className="flex-1 min-w-[150px]">
                        <label className="block text-xs font-medium text-gray-500 mb-1">
                          <User size={12} className="inline mr-1" />Buscar usuario
                        </label>
                        <div className="relative">
                          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                          <input
                            type="text"
                            value={filtroBusquedaHist}
                            onChange={(e) => setFiltroBusquedaHist(e.target.value)}
                            placeholder="Nombre..."
                            className="w-full border border-gray-300 rounded-lg pl-8 pr-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                          />
                        </div>
                      </div>
                      <div className="flex-1 min-w-[130px]">
                        <label className="block text-xs font-medium text-gray-500 mb-1">
                          <Calendar size={12} className="inline mr-1" />Desde
                        </label>
                        <input
                          type="date"
                          value={filtroFechaDesdeHist}
                          onChange={(e) => setFiltroFechaDesdeHist(e.target.value)}
                          className="w-full border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                        />
                      </div>
                      <div className="flex-1 min-w-[130px]">
                        <label className="block text-xs font-medium text-gray-500 mb-1">
                          <Calendar size={12} className="inline mr-1" />Hasta
                        </label>
                        <input
                          type="date"
                          value={filtroFechaHastaHist}
                          onChange={(e) => setFiltroFechaHastaHist(e.target.value)}
                          className="w-full border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                        />
                      </div>
                      <div className="flex-1 min-w-[130px]">
                        <label className="block text-xs font-medium text-gray-500 mb-1">Estado</label>
                        <select
                          value={filtroEstadoHist}
                          onChange={(e) => setFiltroEstadoHist(e.target.value)}
                          className="w-full border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                        >
                          <option value="todos">Todos</option>
                          <option value="abierta">Abierta</option>
                          <option value="cerrada">Cerrada</option>
                          <option value="con_diferencia">Con diferencia</option>
                        </select>
                      </div>
                    </div>
                  </Card>

                  {loadingSesiones ? (
                    <div className="flex items-center justify-center h-32">
                      <div className="w-6 h-6 border-4 border-green-500 border-t-transparent rounded-full animate-spin" />
                    </div>
                  ) : sesionesHistoricasFiltradas.length === 0 ? (
                    <Card className="!py-10 text-center">
                      <AlertCircle size={28} className="text-gray-300 mx-auto mb-2" />
                      <p className="text-gray-500 text-sm">No hay sesiones anteriores para esta caja.</p>
                    </Card>
                  ) : (
                    <div className="space-y-3">
                      {sesionesHistoricasFiltradas.map((ses) => {
                        const ec = ESTADO_SESION[ses.estadoSesion] || ESTADO_SESION.cerrada;
                        const usr = ses.usuario
                          ? `${ses.usuario.nombres || ''} ${ses.usuario.apellidos || ''}`.trim()
                          : '—';
                        return (
                          <Card
                            key={ses.id}
                            className="!p-0 overflow-hidden cursor-pointer hover:border-green-300 hover:shadow-md transition-all"
                            onClick={() => setSelectedSesionId(ses.id)}
                          >
                            <div className="flex items-center justify-between px-4 py-3">
                              <div className="flex items-center gap-2">
                                <Clock size={16} className="text-gray-400" />
                                <div>
                                  <p className="text-sm font-medium text-gray-900">
                                    {formatDateTime(ses.fechaApertura)}
                                    {ses.fechaCierre && (
                                      <span className="text-gray-400"> &rarr; {formatDateTime(ses.fechaCierre)}</span>
                                    )}
                                  </p>
                                  <p className="text-xs text-gray-500">
                                    Usuario: {usr} &middot; Apertura: {formatCurrency(ses.montoApertura ?? 0)}
                                    {ses.fechaCierre && (
                                      <span> &middot; Cierre: {formatCurrency(ses.montoCierre ?? 0)}</span>
                                    )}
                                  </p>
                                </div>
                              </div>
                              <div className="flex items-center gap-3">
                                <Badge variant={ec.variant}>{ec.label}</Badge>
                                <ChevronRight size={16} className="text-gray-400" />
                              </div>
                            </div>
                          </Card>
                        );
                      })}
                    </div>
                  )}
                </>
              )}
            </>
          )}
        </>
      )}

      {/* ================================================= */}
      {/*  VISTA: SESIONES HISTORICAS DE LA CAJA              */}
      {/* ================================================= */}
      {!isViewingActiveCaja && (
        <>
          {/* --- Vista completa de una sesion seleccionada --- */}
          {selectedSesion ? (
            <>
              <button
                onClick={() => setSelectedSesionId(null)}
                className="inline-flex items-center gap-1.5 text-sm text-gray-600 hover:text-gray-900 transition-colors"
              >
                <ArrowLeft size={16} />
                Volver a sesiones de {selectedCajaName}
              </button>
              <SesionFullView sesionData={selectedSesion} columns={columns} />
            </>
          ) : (
            /* --- Lista de sesiones (clickeables) --- */
            <>
              {/* Stats de sesiones */}
              <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
                <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                  <p className="text-xs text-gray-500">Total sesiones</p>
                  <p className="text-xl font-bold text-gray-900">{sesionesHistoricasFiltradas.length}</p>
                </div>
                <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                  <div className="flex items-center gap-1 mb-0.5">
                    <Clock size={12} className="text-blue-500" />
                    <p className="text-xs text-gray-500">Abiertas</p>
                  </div>
                  <p className="text-xl font-bold text-blue-600">
                    {sesionesHistoricasFiltradas.filter((s) => s.estadoSesion === 'abierta').length}
                  </p>
                </div>
                <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                  <div className="flex items-center gap-1 mb-0.5">
                    <CheckCircle size={12} className="text-green-500" />
                    <p className="text-xs text-gray-500">Cerradas OK</p>
                  </div>
                  <p className="text-xl font-bold text-green-600">
                    {sesionesHistoricasFiltradas.filter((s) => s.estadoSesion === 'cerrada').length}
                  </p>
                </div>
                <div className="bg-gray-50 rounded-lg p-3 border border-gray-100">
                  <div className="flex items-center gap-1 mb-0.5">
                    <AlertTriangle size={12} className="text-amber-500" />
                    <p className="text-xs text-gray-500">Con diferencia</p>
                  </div>
                  <p className="text-xl font-bold text-amber-600">
                    {sesionesHistoricasFiltradas.filter((s) => s.estadoSesion === 'con_diferencia').length}
                  </p>
                </div>
              </div>

              {/* Filtros */}
              <Card className="!p-4">
                <div className="flex flex-col lg:flex-row lg:items-end gap-3">
                  <div className="flex-1 min-w-[150px]">
                    <label className="block text-xs font-medium text-gray-500 mb-1">
                      <User size={12} className="inline mr-1" />Buscar usuario
                    </label>
                    <div className="relative">
                      <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                      <input
                        type="text"
                        value={filtroBusquedaHist}
                      onChange={(e) => { setFiltroBusquedaHist(e.target.value); setPaginaHist(1); }}
                        placeholder="Nombre..."
                        className="w-full border border-gray-300 rounded-lg pl-8 pr-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                    </div>
                  </div>
                  <div className="flex-1 min-w-[130px]">
                    <label className="block text-xs font-medium text-gray-500 mb-1">
                      <Calendar size={12} className="inline mr-1" />Desde
                    </label>
                    <input
                      type="date"
                      value={filtroFechaDesdeHist}
                      onChange={(e) => { setFiltroFechaDesdeHist(e.target.value); setPaginaHist(1); }}
                      className="w-full border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                    />
                  </div>
                  <div className="flex-1 min-w-[130px]">
                    <label className="block text-xs font-medium text-gray-500 mb-1">
                      <Calendar size={12} className="inline mr-1" />Hasta
                    </label>
                    <input
                      type="date"
                      value={filtroFechaHastaHist}
                      onChange={(e) => { setFiltroFechaHastaHist(e.target.value); setPaginaHist(1); }}
                      className="w-full border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                    />
                  </div>
                  <div className="flex-1 min-w-[130px]">
                    <label className="block text-xs font-medium text-gray-500 mb-1">Estado</label>
                    <select
                      value={filtroEstadoHist}
                      onChange={(e) => { setFiltroEstadoHist(e.target.value); setPaginaHist(1); }}
                      className="w-full border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                    >
                      <option value="todos">Todos</option>
                      <option value="abierta">Abierta</option>
                      <option value="cerrada">Cerrada</option>
                      <option value="con_diferencia">Con diferencia</option>
                    </select>
                  </div>
                  {(filtroEstadoHist !== 'todos' || filtroBusquedaHist || filtroFechaDesdeHist || filtroFechaHastaHist) && (
                    <button
                      onClick={() => {
                        setFiltroEstadoHist('todos');
                        setFiltroBusquedaHist('');
                        setFiltroFechaDesdeHist('');
                        setFiltroFechaHastaHist('');
                        setPaginaHist(1);
                      }}
                      className="text-xs text-gray-500 hover:text-gray-800 whitespace-nowrap underline"
                    >
                      Limpiar filtros
                    </button>
                  )}
                </div>
              </Card>

              {loadingSesiones ? (
                <div className="flex items-center justify-center h-32">
                  <div className="w-6 h-6 border-4 border-green-500 border-t-transparent rounded-full animate-spin" />
                </div>
              ) : sesionesHistoricasFiltradas.length === 0 ? (
                <Card className="!py-12 text-center">
                  <AlertCircle size={32} className="text-gray-300 mx-auto mb-3" />
                  <p className="text-gray-500">No hay sesiones para los filtros aplicados.</p>
                  <p className="text-sm text-gray-400 mt-1">
                    Las sesiones aparecen aquí cuando se cierra un turno en esta caja.
                  </p>
                </Card>
              ) : (
                <div className="space-y-3">
                  {sesionesHistPage.map((ses) => {
                    const ec = ESTADO_SESION[ses.estadoSesion] || ESTADO_SESION.cerrada;
                    const usr = ses.usuario
                      ? `${ses.usuario.nombres || ''} ${ses.usuario.apellidos || ''}`.trim()
                      : '\u2014';
                    return (
                      <Card
                        key={ses.id}
                        className="!p-0 overflow-hidden cursor-pointer hover:border-green-300 hover:shadow-md transition-all"
                        onClick={() => setSelectedSesionId(ses.id)}
                      >
                        <div className="flex items-center justify-between px-4 py-3">
                          <div className="flex items-center gap-4">
                            <div className="flex items-center gap-2">
                              <Clock size={16} className="text-gray-400" />
                              <div>
                                <p className="text-sm font-medium text-gray-900">
                                  {formatDateTime(ses.fechaApertura)}
                                  {ses.fechaCierre && (
                                    <span className="text-gray-400"> &rarr; {formatDateTime(ses.fechaCierre)}</span>
                                  )}
                                </p>
                                <p className="text-xs text-gray-500">
                                  Usuario: {usr} &middot; Apertura: {formatCurrency(ses.montoApertura ?? 0)}
                                  {ses.fechaCierre && (
                                    <span> &middot; Cierre: {formatCurrency(ses.montoCierre ?? 0)}</span>
                                  )}
                                </p>
                              </div>
                            </div>
                            <Badge variant={ec.variant}>{ec.label}</Badge>
                          </div>
                          <span className="text-xs text-gray-400">Click para ver detalle &rarr;</span>
                        </div>
                      </Card>
                    );
                  })}

                  {/* Controles de paginación */}
                  {totalPaginasHist > 1 && (
                    <div className="flex items-center justify-between pt-2">
                      <span className="text-sm text-gray-500">
                        Página {paginaHist} de {totalPaginasHist} &middot; {sesionesHistoricasFiltradas.length} sesiones
                      </span>
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => setPaginaHist((p) => Math.max(1, p - 1))}
                          disabled={paginaHist === 1}
                          className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50 transition-colors"
                        >
                          ← Anterior
                        </button>
                        {Array.from({ length: totalPaginasHist }, (_, i) => i + 1)
                          .filter((p) => p === 1 || p === totalPaginasHist || Math.abs(p - paginaHist) <= 1)
                          .reduce((acc, p, idx, arr) => {
                            if (idx > 0 && p - arr[idx - 1] > 1) acc.push('...');
                            acc.push(p);
                            return acc;
                          }, [])
                          .map((p, idx) =>
                            p === '...' ? (
                              <span key={`ellipsis-${idx}`} className="px-1 text-gray-400">…</span>
                            ) : (
                              <button
                                key={p}
                                onClick={() => setPaginaHist(p)}
                                className={`px-3 py-1.5 text-sm border rounded-lg transition-colors ${
                                  p === paginaHist
                                    ? 'bg-green-600 text-white border-green-600'
                                    : 'border-gray-300 hover:bg-gray-50'
                                }`}
                              >
                                {p}
                              </button>
                            ),
                          )}
                        <button
                          onClick={() => setPaginaHist((p) => Math.min(totalPaginasHist, p + 1))}
                          disabled={paginaHist === totalPaginasHist}
                          className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50 transition-colors"
                        >
                          Siguiente →
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </>
          )}
        </>
      )}

      {/* Modales */}
      {hasSesion && (
        <MovimientoCajaModal
          isOpen={showRegistrarModal}
          onClose={() => setShowRegistrarModal(false)}
          onConfirm={handleRegistrar}
          sesionCajaId={sesion?.id}
          isLoading={isRegistrando}
        />
      )}

      {/* Modal Devolver Egreso */}
      <Modal
        isOpen={!!devolverTarget}
        onClose={() => setDevolverTarget(null)}
        title="Devolver Egreso"
        size="sm"
      >
        {devolverTarget && (
          <div className="space-y-4">
            <div className="bg-gray-50 rounded-lg p-3 text-sm space-y-1">
              <p><span className="text-gray-500">Tipo:</span> {TIPO_CONFIG[devolverTarget.tipoMovimiento]?.label || devolverTarget.tipoMovimiento}</p>
              <p><span className="text-gray-500">Monto original:</span> <strong className="text-red-600">{formatCurrency(devolverTarget.monto)}</strong></p>
              {devolverTarget.montoDevuelto > 0 && (
                <p><span className="text-gray-500">Ya devuelto:</span> {formatCurrency(devolverTarget.montoDevuelto)}</p>
              )}
              <p><span className="text-gray-500">Máximo a devolver:</span> {formatCurrency(maxDevolucion)}</p>
              {devolverTarget.descripcion && (
                <p><span className="text-gray-500">Descripción:</span> {devolverTarget.descripcion}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Monto a devolver *</label>
              <Input
                type="number"
                step="0.01"
                min="0.01"
                max={maxDevolucion}
                value={devolverMonto}
                onChange={(e) => setDevolverMonto(e.target.value)}
                placeholder={`Máx ${formatCurrency(maxDevolucion)}`}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Motivo</label>
              <textarea
                value={devolverMotivo}
                onChange={(e) => setDevolverMotivo(e.target.value)}
                rows={2}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                placeholder="Motivo de la devolución (opcional)"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button variant="outline" size="sm" onClick={() => setDevolverTarget(null)}>Cancelar</Button>
              <Button
                size="sm"
                onClick={handleDevolver}
                disabled={!devolverMonto || parseFloat(devolverMonto) <= 0 || parseFloat(devolverMonto) > maxDevolucion || isDevolviendo}
              >
                {isDevolviendo ? 'Devolviendo...' : 'Confirmar Devolución'}
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Modal Descripcion */}
      <Modal
        isOpen={!!descripcionModal}
        onClose={() => setDescripcionModal(null)}
        title="Detalle del movimiento"
        size="sm"
      >
        {descripcionModal && (
          <div className="space-y-3">
            <div className="bg-gray-50 rounded-lg p-3 text-sm space-y-1">
              <p><span className="text-gray-500">Tipo:</span> {TIPO_CONFIG[descripcionModal.tipoMovimiento]?.label || descripcionModal.tipoMovimiento}</p>
              <p><span className="text-gray-500">Monto:</span> <strong>{formatCurrency(descripcionModal.monto)}</strong></p>
              {descripcionModal.categoriaGasto?.nombre && (
                <p><span className="text-gray-500">Categoría:</span> {descripcionModal.categoriaGasto.nombre}</p>
              )}
              <p><span className="text-gray-500">Fecha:</span> {formatDateTime(descripcionModal.fecha)}</p>
            </div>
            <div>
              <p className="text-xs font-medium text-gray-500 mb-1">Descripción</p>
              <p className="text-sm text-gray-800 whitespace-pre-wrap bg-white border border-gray-200 rounded-lg p-3">
                {descripcionModal.descripcion}
              </p>
            </div>
            <div className="flex justify-end pt-1">
              <Button variant="outline" size="sm" onClick={() => setDescripcionModal(null)}>Cerrar</Button>
            </div>
          </div>
        )}
      </Modal>

    </div>
  );
};
