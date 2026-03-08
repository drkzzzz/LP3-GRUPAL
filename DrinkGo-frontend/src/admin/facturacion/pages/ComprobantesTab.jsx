/**
 * ComprobantesTab.jsx
 * ───────────────────
 * Comprobantes electrónicos - facturación LOCAL.
 * Muestra los comprobantes generados por ventas POS.
 * Estados locales: Confirmado (aceptado) y Anulado.
 * NO tiene interacción con SUNAT — eso se hace en PSE.
 */
import { useState, useMemo, useCallback } from 'react';
import { useOutletContext, useNavigate } from 'react-router-dom';
import { Eye, XCircle, Search, CheckCircle, AlertCircle, Hash, Monitor, FileText, Info } from 'lucide-react';
import { useComprobantes, useCambiarEstadoComprobante, useEmitirNotaCreditoDebito, useItemsComprobante } from '../hooks/useFacturacion';
import { ComprobanteViewModal } from '../components/ComprobanteViewModal';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { SinCajaAsignada } from '@/admin/ventas/components/SinCajaAsignada';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { message } from '@/shared/utils/notifications';

/* ─── Mapeo de estados internos a etiquetas locales ─── */
const getEstadoLocal = (estadoDocumento) => {
  if (estadoDocumento === 'anulado') {
    return { label: 'Anulado', badge: 'bg-red-50 text-red-700 border-red-200' };
  }
  // aceptado, observado, enviado, pendiente_envio, borrador → "Confirmado"
  return { label: 'Confirmado', badge: 'bg-green-50 text-green-700 border-green-200' };
};

const TIPO_DOC_LABELS = {
  boleta: 'Boleta',
  factura: 'Factura',
  nota_credito: 'Nota de Crédito',
  nota_debito: 'Nota de Débito',
};

/* ─── Motivos SUNAT para Notas de Crédito ─── */
const MOTIVOS_NC = [
  { codigo: '01', label: 'Anulación de la operación' },
  { codigo: '02', label: 'Anulación por error en el RUC' },
  { codigo: '03', label: 'Corrección por error en la descripción' },
  { codigo: '04', label: 'Descuento global' },
  { codigo: '06', label: 'Devolución total' },
  { codigo: '07', label: 'Devolución por ítem' },
  { codigo: '09', label: 'Disminución en el valor' },
];

/* ─── Motivos SUNAT para Notas de Débito ─── */
const MOTIVOS_ND = [
  { codigo: '01', label: 'Intereses por mora' },
  { codigo: '02', label: 'Aumento en el valor' },
  { codigo: '03', label: 'Penalidades / otros conceptos' },
];

/* ─── Helpers ─── */
const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('es-PE', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  });
};

const formatCurrency = (amount) => {
  if (amount == null) return 'S/ 0.00';
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(amount);
};

export const ComprobantesTab = () => {
  const { negocioId } = useOutletContext();
  const { user, getAlcance, cajaAsignada } = useAdminAuthStore();
  const navigate = useNavigate();
  const alcanceComprobantes = getAlcance('m.facturacion.comprobantes');
  const esSoloCaja = alcanceComprobantes === 'caja_asignada';

  const [noSerieError, setNoSerieError] = useState(null);

  const { data: comprobantesRaw = [], isLoading } = useComprobantes(negocioId);
  const cambiarEstado = useCambiarEstadoComprobante();
  const emitirNota = useEmitirNotaCreditoDebito({
    onErrorCallback: (msg) => setNoSerieError(msg),
  });

  /* Filtrar comprobantes: solo los generados por el cajero + su caja + hoy */
  const comprobantes = useMemo(() => {
    if (!esSoloCaja || !cajaAsignada) return comprobantesRaw;
    const hoy = new Date().toISOString().slice(0, 10);
    return comprobantesRaw.filter(
      (c) =>
        c.ventaCajaId === cajaAsignada.id &&
        c.usuarioId === user?.id &&
        (c.fechaEmision || c.creadoEn)?.slice(0, 10) === hoy,
    );
  }, [comprobantesRaw, esSoloCaja, cajaAsignada, user?.id]);

  /* ─── Estado de filtros ─── */
  const [search, setSearch] = useState('');
  const [filterTipo, setFilterTipo] = useState('');
  const [filterEstado, setFilterEstado] = useState('');
  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');

  /* ─── Paginación ─── */
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  /* ─── Modal detalle ─── */
  const [selectedDoc, setSelectedDoc] = useState(null);

  /* ─── Modal anulación ─── */
  const [anularTarget, setAnularTarget] = useState(null);
  const [razonAnulacion, setRazonAnulacion] = useState('');

  /* ─── Modal Nota de Crédito / Débito ─── */
  const [ncTarget, setNcTarget] = useState(null);
  const [ncTipoNota, setNcTipoNota] = useState('nota_credito');
  const [ncCodigoMotivo, setNcCodigoMotivo] = useState('');
  const [ncDescripcion, setNcDescripcion] = useState('');
  // Motivo 07: items seleccionados { [productoId|comboId]: { checked, cantidad, maxCantidad, nombre, precioUnit } }
  const [ncItems, setNcItems] = useState({});
  // Motivos 04/09 (NC) y todos ND: monto libre
  const [ncMonto, setNcMonto] = useState('');

  /* Items del comprobante de referencia (carga dinámica) */
  const { data: itemsData, isLoading: itemsLoading } = useItemsComprobante(ncTarget?.id);

  /* Si es cajero con alcance caja_asignada pero sin caja asignada -> bloquear */
  if (esSoloCaja && !cajaAsignada) {
    return <SinCajaAsignada titulo="Comprobantes Electrónicos" />;
  }

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const total = comprobantes.length;
    const anulados = comprobantes.filter((c) => c.estadoDocumento === 'anulado').length;
    const confirmados = total - anulados;
    return { total, confirmados, anulados };
  }, [comprobantes]);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    return comprobantes.filter((c) => {
      if (search) {
        const q = search.toLowerCase();
        const matchSearch =
          c.numeroDocumento?.toLowerCase().includes(q) ||
          c.clienteNombre?.toLowerCase().includes(q) ||
          c.receptor?.toLowerCase().includes(q) ||
          c.razonSocialReceptor?.toLowerCase().includes(q);
        if (!matchSearch) return false;
      }
      if (filterTipo && c.tipoDocumento !== filterTipo) return false;
      if (filterEstado) {
        const esAnulado = c.estadoDocumento === 'anulado';
        if (filterEstado === 'confirmado' && esAnulado) return false;
        if (filterEstado === 'anulado' && !esAnulado) return false;
      }
      if (fechaDesde) {
        const desde = new Date(fechaDesde);
        const fecha = new Date(c.fechaEmision || c.creadoEn);
        if (fecha < desde) return false;
      }
      if (fechaHasta) {
        const hasta = new Date(fechaHasta);
        hasta.setHours(23, 59, 59, 999);
        const fecha = new Date(c.fechaEmision || c.creadoEn);
        if (fecha > hasta) return false;
      }
      return true;
    });
  }, [comprobantes, search, filterTipo, filterEstado, fechaDesde, fechaHasta]);

  /* ─── Paginación calculada ─── */
  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const paginated = filtered.slice(page * pageSize, (page + 1) * pageSize);

  /* ─── Acciones ─── */
  const handleConfirmAnular = async () => {
    if (!anularTarget) return;
    try {
      await cambiarEstado.mutateAsync({
        id: anularTarget.id,
        estado: 'anulado',
        motivoAnulacion: razonAnulacion.trim() || 'Anulación manual',
      });
    } catch {
      // Error manejado por mutation
    } finally {
      setAnularTarget(null);
      setRazonAnulacion('');
    }
  };

  /* ─── Constantes de motivos por categoría ─── */
  const MOTIVOS_ANULACION_COMPLETA = new Set(['01', '02', '03', '06']);
  const MOTIVO_DEVOLUCION_ITEM = '07';
  const MOTIVOS_DESCUENTO = new Set(['04', '09']);

  const handleOpenNcNd = (doc) => {
    setNcTarget(doc);
    setNcTipoNota('nota_credito');
    setNcCodigoMotivo('');
    setNcDescripcion('');
    setNcItems({});
    setNcMonto('');
    setNoSerieError(null);
  };

  const handleCloseNcNd = useCallback(() => {
    setNcTarget(null);
    setNcCodigoMotivo('');
    setNcDescripcion('');
    setNcItems({});
    setNcMonto('');
    setNoSerieError(null);
  }, []);

  /* Cuando llegan los items del backend, inicializar ncItems para motivo 07 */
  const itemsOriginal = itemsData?.items || [];
  const notasExistentes = itemsData?.notasExistentes || [];
  const totalNcAcumulado = itemsData?.totalNcAcumulado ?? 0;
  const totalComprobante = itemsData?.totalComprobante ?? ncTarget?.total ?? 0;
  const saldoDisponibleNc = Math.max(0, totalComprobante - totalNcAcumulado);

  /* Categoría del motivo actual */
  const esAnulacionCompleta = ncTipoNota === 'nota_credito' && MOTIVOS_ANULACION_COMPLETA.has(ncCodigoMotivo);
  const esDevolucionItem = ncTipoNota === 'nota_credito' && ncCodigoMotivo === MOTIVO_DEVOLUCION_ITEM;
  const esDescuento = ncTipoNota === 'nota_credito' && MOTIVOS_DESCUENTO.has(ncCodigoMotivo);
  const esNotaDebito = ncTipoNota === 'nota_debito';

  /* Cálculo del total de la nota según motivo */
  const ncTotalCalculado = useMemo(() => {
    if (esAnulacionCompleta) return totalComprobante;
    if (esDevolucionItem) {
      return Object.values(ncItems).reduce((sum, it) => {
        if (!it.checked || !it.cantidad) return sum;
        return sum + it.cantidad * it.precioUnit;
      }, 0);
    }
    if (esDescuento || esNotaDebito) {
      return parseFloat(ncMonto) || 0;
    }
    return 0;
  }, [esAnulacionCompleta, esDevolucionItem, esDescuento, esNotaDebito, totalComprobante, ncItems, ncMonto]);

  /* Validación del formulario NC/ND */
  const ncFormValid = useMemo(() => {
    if (!ncCodigoMotivo) return false;
    if (esAnulacionCompleta) return true;
    if (esDevolucionItem) {
      const hayItemsSeleccionados = Object.values(ncItems).some((it) => it.checked && it.cantidad > 0);
      return hayItemsSeleccionados && ncTotalCalculado > 0 && ncTotalCalculado <= saldoDisponibleNc;
    }
    if (esDescuento) {
      const monto = parseFloat(ncMonto);
      return monto > 0 && monto <= saldoDisponibleNc;
    }
    if (esNotaDebito) {
      return parseFloat(ncMonto) > 0;
    }
    return false;
  }, [ncCodigoMotivo, esAnulacionCompleta, esDevolucionItem, esDescuento, esNotaDebito, ncItems, ncMonto, ncTotalCalculado, saldoDisponibleNc]);

  /* Toggle item seleccionado (motivo 07) */
  const handleToggleItem = useCallback((key, item) => {
    setNcItems((prev) => {
      const current = prev[key];
      if (current?.checked) {
        return { ...prev, [key]: { ...current, checked: false, cantidad: 0 } };
      }
      return {
        ...prev,
        [key]: {
          checked: true,
          cantidad: item.maxCantidad || item.cantidad || 1,
          maxCantidad: item.maxCantidad || item.cantidad || 1,
          nombre: item.nombre,
          precioUnit: item.precioUnit,
          productoId: item.productoId || null,
          comboId: item.comboId || null,
        },
      };
    });
  }, []);

  /* Cambiar cantidad de un item seleccionado */
  const handleItemCantidad = useCallback((key, cant) => {
    setNcItems((prev) => {
      const current = prev[key];
      if (!current) return prev;
      const cantidad = Math.max(0, Math.min(cant, current.maxCantidad));
      return { ...prev, [key]: { ...current, cantidad, checked: cantidad > 0 } };
    });
  }, []);

  /* Inicializar items cuando cambia motivo a 07 y hay items cargados */
  const initItemsForDevolucion = useCallback(() => {
    if (!itemsOriginal.length) return;
    const initial = {};
    itemsOriginal.forEach((it) => {
      const key = it.productoId ? `p_${it.productoId}` : `c_${it.comboId}`;
      initial[key] = {
        checked: false,
        cantidad: 0,
        maxCantidad: it.cantidad,
        nombre: it.nombreProducto || it.nombreCombo || 'Producto',
        precioUnit: it.subtotal / it.cantidad,
        productoId: it.productoId || null,
        comboId: it.comboId || null,
      };
    });
    setNcItems(initial);
  }, [itemsOriginal]);

  const handleConfirmNcNd = async () => {
    if (!ncTarget || !ncFormValid) return;

    const payload = {
      documentoReferenciaId: ncTarget.id,
      tipoNota: ncTipoNota,
      codigoMotivo: ncCodigoMotivo,
      descripcionMotivo: ncDescripcion.trim() || undefined,
      usuarioId: user?.id,
    };

    // Motivo 07: agregar items seleccionados
    if (esDevolucionItem) {
      payload.items = Object.values(ncItems)
        .filter((it) => it.checked && it.cantidad > 0)
        .map((it) => ({
          productoId: it.productoId,
          comboId: it.comboId,
          cantidad: it.cantidad,
        }));
    }

    // Motivos 04/09 (descuento) y ND (monto libre)
    if (esDescuento || esNotaDebito) {
      payload.monto = parseFloat(ncMonto);
    }

    try {
      await emitirNota.mutateAsync(payload);
      handleCloseNcNd();
    } catch {
      // Error manejado por mutation
    }
  };

  /** ¿Puede emitir NC/ND sobre este documento? Solo boletas/facturas no anuladas */
  const puedeEmitirNcNd = (doc) => {
    if (doc.estadoDocumento === 'anulado') return false;
    return doc.tipoDocumento === 'boleta' || doc.tipoDocumento === 'factura';
  };

  return (
    <div className="space-y-6">
      {/* ─── Header ─── */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Comprobantes Electrónicos</h1>
        <p className="text-gray-600 mt-1">Consulta y gestión de boletas, facturas y notas de crédito</p>
      </div>

      {/* Banner alcance restringido */}
      {esSoloCaja && cajaAsignada && (
        <div className="flex items-center gap-2 bg-amber-50 border border-amber-200 rounded-lg px-4 py-3">
          <Monitor size={16} className="text-amber-600 shrink-0" />
          <p className="text-sm text-amber-700">
            Mostrando solo los comprobantes del día de tu caja: <strong>{cajaAsignada.nombreCaja}</strong>
          </p>
        </div>
      )}

      {/* ─── Stats Cards (3 cards: Total, Confirmados, Anulados) ─── */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center gap-4">
          <div className="p-2.5 bg-blue-50 rounded-lg"><Hash size={22} className="text-blue-600" /></div>
          <div><p className="text-sm text-gray-500">Total emitidos</p><p className="text-2xl font-bold text-gray-900">{stats.total}</p></div>
        </div>
        <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center gap-4">
          <div className="p-2.5 bg-green-50 rounded-lg"><CheckCircle size={22} className="text-green-600" /></div>
          <div><p className="text-sm text-gray-500">Confirmados</p><p className="text-2xl font-bold text-gray-900">{stats.confirmados}</p></div>
        </div>
        <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center gap-4">
          <div className="p-2.5 bg-red-50 rounded-lg"><AlertCircle size={22} className="text-red-500" /></div>
          <div><p className="text-sm text-gray-500">Anulados</p><p className="text-2xl font-bold text-gray-900">{stats.anulados}</p></div>
        </div>
      </div>

      {/* ─── Filtros + Tabla ─── */}
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        <div className="flex flex-wrap items-center gap-3 mb-5">
          {/* Search */}
          <div className="relative flex-1 min-w-[200px] max-w-xs">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por número, receptor..."
              value={search}
              onChange={(e) => { setSearch(e.target.value); setPage(0); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Tipo */}
          <select
            value={filterTipo}
            onChange={(e) => { setFilterTipo(e.target.value); setPage(0); }}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
          >
            <option value="">Todos los tipos</option>
            {Object.entries(TIPO_DOC_LABELS).map(([key, label]) => (
              <option key={key} value={key}>{label}</option>
            ))}
          </select>

          {/* Estado */}
          <select
            value={filterEstado}
            onChange={(e) => { setFilterEstado(e.target.value); setPage(0); }}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
          >
            <option value="">Todos los estados</option>
            <option value="confirmado">Confirmado</option>
            <option value="anulado">Anulado</option>
          </select>

          {/* Fecha desde */}
          <input
            type="date"
            value={fechaDesde}
            onChange={(e) => { setFechaDesde(e.target.value); setPage(0); }}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Fecha hasta */}
          <input
            type="date"
            value={fechaHasta}
            onChange={(e) => { setFechaHasta(e.target.value); setPage(0); }}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* ─── Tabla ─── */}
        {isLoading ? (
          <div className="text-center py-12 text-gray-500">Cargando comprobantes...</div>
        ) : filtered.length === 0 ? (
          <div className="text-center py-12 text-gray-400">No se encontraron comprobantes</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-y border-gray-200 bg-gray-50/50">
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[50px]">#</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Número</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Tipo</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Receptor</th>
                  <th className="text-right py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Total</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Estado</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Fecha</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[130px]">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((doc, idx) => {
                  const estado = getEstadoLocal(doc.estadoDocumento);
                  const esAnulado = doc.estadoDocumento === 'anulado';
                  return (
                    <tr key={doc.id} className="border-b border-gray-100 hover:bg-gray-50/50">
                      <td className="py-3 px-3 text-gray-400">{page * pageSize + idx + 1}</td>
                      <td className="py-3 px-3 font-mono text-xs font-medium">{doc.numeroDocumento}</td>
                      <td className="py-3 px-3">{TIPO_DOC_LABELS[doc.tipoDocumento] || doc.tipoDocumento}</td>
                      <td className="py-3 px-3 text-gray-700">{doc.clienteNombre || doc.razonSocialReceptor || doc.receptor || '-'}</td>
                      <td className="py-3 px-3 text-right font-semibold">{formatCurrency(doc.total)}</td>
                      <td className="py-3 px-3 text-center">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${estado.badge}`}>
                          {estado.label}
                        </span>
                      </td>
                      <td className="py-3 px-3 text-gray-600">{formatDate(doc.fechaEmision || doc.creadoEn)}</td>
                      <td className="py-3 px-3">
                        <div className="flex justify-center gap-2">
                          <button title="Ver detalles" onClick={() => setSelectedDoc(doc)} className="text-blue-500 hover:text-blue-700">
                            <Eye size={16} />
                          </button>
                          {puedeEmitirNcNd(doc) && (
                            <button title="Emitir Nota de Crédito/Débito" onClick={() => handleOpenNcNd(doc)} className="text-amber-600 hover:text-amber-800">
                              <FileText size={16} />
                            </button>
                          )}
                          {!esAnulado && !(doc.modoEmision === 'PSE' && ['enviado', 'aceptado', 'observado'].includes(doc.estadoDocumento)) && (
                            <button title="Anular" onClick={() => setAnularTarget(doc)} className="text-red-500 hover:text-red-700">
                              <XCircle size={16} />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* ─── Paginación ─── */}
        <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-100 text-sm text-gray-600">
          <div className="flex items-center gap-3">
            <span>Filas por página:</span>
            <select
              value={pageSize}
              onChange={(e) => { setPageSize(Number(e.target.value)); setPage(0); }}
              className="border border-gray-300 rounded-md px-2 py-1 text-sm"
            >
              {[10, 25, 50].map((n) => <option key={n} value={n}>{n}</option>)}
            </select>
            <span>Total: {filtered.length} registros</span>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={page === 0}
              className="px-2 py-1 rounded hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              &lt;
            </button>
            <span>Página {page + 1} de {totalPages}</span>
            <button
              onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
              disabled={page >= totalPages - 1}
              className="px-2 py-1 rounded hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              &gt;
            </button>
          </div>
        </div>
      </div>

      {/* ─── Modal comprobante completo ─── */}
      {selectedDoc && (
        <ComprobanteViewModal
          doc={selectedDoc}
          onClose={() => setSelectedDoc(null)}
        />
      )}

      {/* ─── Modal anulación con razón ─── */}
      <Modal
        isOpen={!!anularTarget}
        onClose={() => { setAnularTarget(null); setRazonAnulacion(''); }}
        title="Anular Comprobante"
        size="sm"
        footer={
          <>
            <Button variant="outline" onClick={() => { setAnularTarget(null); setRazonAnulacion(''); }}>
              Cancelar
            </Button>
            <Button variant="danger" onClick={handleConfirmAnular} disabled={cambiarEstado.isPending}>
              {cambiarEstado.isPending ? 'Anulando...' : 'Confirmar Anulación'}
            </Button>
          </>
        }
      >
        <div className="space-y-3">
          <p className="text-sm text-gray-600">
            ¿Está seguro de anular el comprobante <strong>{anularTarget?.numeroDocumento}</strong>?
          </p>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Motivo de anulación
            </label>
            <textarea
              value={razonAnulacion}
              onChange={(e) => setRazonAnulacion(e.target.value)}
              placeholder="Ingrese el motivo de la anulación..."
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>
        </div>
      </Modal>

      {/* ─── Modal Nota de Crédito / Débito ─── */}
      <Modal
        isOpen={!!ncTarget}
        onClose={handleCloseNcNd}
        title={ncTipoNota === 'nota_credito' ? 'Emitir Nota de Crédito' : 'Emitir Nota de Débito'}
        size={esDevolucionItem ? 'lg' : 'md'}
        footer={
          <>
            <Button variant="outline" onClick={handleCloseNcNd}>
              Cancelar
            </Button>
            <Button
              onClick={handleConfirmNcNd}
              disabled={emitirNota.isPending || !ncFormValid}
            >
              {emitirNota.isPending ? 'Emitiendo...' : 'Emitir Nota'}
            </Button>
          </>
        }
      >
        <div className="space-y-4">
          {/* Documento de referencia */}
          <div className="bg-gray-50 rounded-lg p-3 border border-gray-200">
            <p className="text-xs text-gray-500 mb-1">Documento de referencia</p>
            <p className="font-mono font-semibold text-sm">{ncTarget?.numeroDocumento}</p>
            <p className="text-sm text-gray-600 mt-1">
              {TIPO_DOC_LABELS[ncTarget?.tipoDocumento]} — {formatCurrency(ncTarget?.total)}
            </p>
            <p className="text-sm text-gray-500">
              Cliente: {ncTarget?.clienteNombre || '-'}
            </p>
          </div>

          {/* Info de notas existentes y saldo */}
          {ncTarget && totalNcAcumulado > 0 && (
            <div className="bg-blue-50 border border-blue-200 rounded-lg px-3 py-2">
              <div className="flex items-start gap-2">
                <Info size={14} className="text-blue-600 mt-0.5 shrink-0" />
                <div className="text-xs text-blue-700 space-y-1">
                  <p>NC emitidas previamente: <strong>{formatCurrency(totalNcAcumulado)}</strong></p>
                  <p>Saldo disponible para NC: <strong>{formatCurrency(saldoDisponibleNc)}</strong></p>
                  {notasExistentes.length > 0 && (
                    <details className="mt-1">
                      <summary className="cursor-pointer hover:underline">
                        Ver {notasExistentes.length} nota(s) existente(s)
                      </summary>
                      <ul className="mt-1 space-y-0.5 pl-2">
                        {notasExistentes.map((n) => (
                          <li key={n.id} className="text-xs">
                            {n.numeroDocumento} — {formatCurrency(n.total)} — {n.estadoDocumento}
                          </li>
                        ))}
                      </ul>
                    </details>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Saldo agotado → bloquear NC */}
          {ncTarget && ncTipoNota === 'nota_credito' && saldoDisponibleNc <= 0 && totalNcAcumulado > 0 && (
            <div className="bg-red-50 border border-red-200 rounded-lg px-3 py-2">
              <p className="text-xs text-red-700 font-medium">
                Este comprobante ya fue cubierto completamente por notas de crédito. No se pueden emitir más.
              </p>
            </div>
          )}

          {/* Tipo de nota */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tipo de Nota <span className="text-red-500">*</span>
            </label>
            <select
              value={ncTipoNota}
              onChange={(e) => {
                setNcTipoNota(e.target.value);
                setNcCodigoMotivo('');
                setNcItems({});
                setNcMonto('');
              }}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            >
              <option value="nota_credito">Nota de Crédito</option>
              <option value="nota_debito">Nota de Débito</option>
            </select>
          </div>

          {/* Motivo SUNAT */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Motivo SUNAT <span className="text-red-500">*</span>
            </label>
            <select
              value={ncCodigoMotivo}
              onChange={(e) => {
                setNcCodigoMotivo(e.target.value);
                setNcItems({});
                setNcMonto('');
                // Si selecciona motivo 07, inicializar items
                if (e.target.value === MOTIVO_DEVOLUCION_ITEM) {
                  initItemsForDevolucion();
                }
              }}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            >
              <option value="">Seleccione un motivo...</option>
              {(ncTipoNota === 'nota_credito' ? MOTIVOS_NC : MOTIVOS_ND).map((m) => (
                <option key={m.codigo} value={m.codigo}>
                  {m.codigo} - {m.label}
                </option>
              ))}
            </select>
          </div>

          {/* ─── Sección dinámica según motivo ─── */}

          {/* Anulación completa (01/02/03/06): solo info */}
          {esAnulacionCompleta && (
            <div className="bg-amber-50 border border-amber-200 rounded-lg px-3 py-2">
              <p className="text-xs text-amber-700">
                Se emitirá una Nota de Crédito por el <strong>total</strong> del comprobante ({formatCurrency(totalComprobante)}).
                Los ítems y montos se copiarán automáticamente. El comprobante original quedará marcado como <strong>anulado</strong>.
              </p>
            </div>
          )}

          {/* Devolución por ítem (07): tabla de selección */}
          {esDevolucionItem && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Seleccione los ítems a devolver
              </label>
              {itemsLoading ? (
                <div className="text-sm text-gray-400 py-4 text-center">Cargando ítems...</div>
              ) : itemsOriginal.length === 0 ? (
                <div className="text-sm text-gray-400 py-4 text-center">No se encontraron ítems en el comprobante</div>
              ) : (
                <div className="border border-gray-200 rounded-lg overflow-hidden">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="bg-gray-50 border-b border-gray-200">
                        <th className="w-10 px-3 py-2"></th>
                        <th className="text-left px-3 py-2 text-xs font-semibold text-gray-500 uppercase">Producto</th>
                        <th className="text-center px-3 py-2 text-xs font-semibold text-gray-500 uppercase w-24">Cantidad</th>
                        <th className="text-right px-3 py-2 text-xs font-semibold text-gray-500 uppercase w-28">P. Unit.</th>
                        <th className="text-right px-3 py-2 text-xs font-semibold text-gray-500 uppercase w-28">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                      {itemsOriginal.map((it) => {
                        const key = it.productoId ? `p_${it.productoId}` : `c_${it.comboId}`;
                        const sel = ncItems[key] || { checked: false, cantidad: 0 };
                        const precioUnit = it.subtotal / it.cantidad;
                        return (
                          <tr key={key} className={`border-b border-gray-100 ${sel.checked ? 'bg-blue-50/50' : ''}`}>
                            <td className="px-3 py-2 text-center">
                              <input
                                type="checkbox"
                                checked={sel.checked}
                                onChange={() => handleToggleItem(key, { ...it, maxCantidad: it.cantidad, nombre: it.nombreProducto || it.nombreCombo, precioUnit })}
                                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                              />
                            </td>
                            <td className="px-3 py-2 text-gray-700">
                              {it.nombreProducto || it.nombreCombo || '-'}
                            </td>
                            <td className="px-3 py-2 text-center">
                              {sel.checked ? (
                                <input
                                  type="number"
                                  min={1}
                                  max={it.cantidad}
                                  value={sel.cantidad}
                                  onChange={(e) => handleItemCantidad(key, parseInt(e.target.value) || 0)}
                                  className="w-16 text-center border border-gray-300 rounded px-1 py-0.5 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                                />
                              ) : (
                                <span className="text-gray-400">{it.cantidad}</span>
                              )}
                              <span className="text-xs text-gray-400 ml-1">/ {it.cantidad}</span>
                            </td>
                            <td className="px-3 py-2 text-right text-gray-600">{formatCurrency(precioUnit)}</td>
                            <td className="px-3 py-2 text-right font-medium">
                              {sel.checked && sel.cantidad > 0
                                ? formatCurrency(sel.cantidad * precioUnit)
                                : <span className="text-gray-300">—</span>}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {/* Descuento (04/09): monto */}
          {esDescuento && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Monto del descuento <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">S/</span>
                <input
                  type="number"
                  min="0.01"
                  max={saldoDisponibleNc}
                  step="0.01"
                  value={ncMonto}
                  onChange={(e) => setNcMonto(e.target.value)}
                  placeholder="0.00"
                  className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <p className="text-xs text-gray-400 mt-1">
                Máximo: {formatCurrency(saldoDisponibleNc)}
              </p>
            </div>
          )}

          {/* Nota de Débito: monto */}
          {esNotaDebito && ncCodigoMotivo && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Monto adicional <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">S/</span>
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={ncMonto}
                  onChange={(e) => setNcMonto(e.target.value)}
                  placeholder="0.00"
                  className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
          )}

          {/* Descripción adicional */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descripción adicional
            </label>
            <textarea
              value={ncDescripcion}
              onChange={(e) => setNcDescripcion(e.target.value)}
              placeholder="Detalle del motivo (opcional)..."
              rows={2}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Resumen de totales */}
          {ncCodigoMotivo && ncTotalCalculado > 0 && (
            <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
              <p className="text-xs text-gray-500 mb-2 font-medium uppercase tracking-wider">Resumen de la nota</p>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Total de la nota:</span>
                <span className="font-bold text-gray-900">{formatCurrency(ncTotalCalculado)}</span>
              </div>
              {ncTipoNota === 'nota_credito' && (
                <div className="flex justify-between text-xs mt-1">
                  <span className="text-gray-400">Saldo restante después:</span>
                  <span className="text-gray-500">{formatCurrency(saldoDisponibleNc - ncTotalCalculado)}</span>
                </div>
              )}
            </div>
          )}

          {/* Validación: NC excede saldo */}
          {ncTipoNota === 'nota_credito' && ncTotalCalculado > saldoDisponibleNc && saldoDisponibleNc > 0 && (
            <div className="bg-red-50 border border-red-200 rounded-lg px-3 py-2">
              <p className="text-xs text-red-700">
                El monto de la nota ({formatCurrency(ncTotalCalculado)}) excede el saldo disponible ({formatCurrency(saldoDisponibleNc)}).
              </p>
            </div>
          )}

          {/* Error: sin serie configurada */}
          {noSerieError && (
            <div className="bg-amber-50 border border-amber-300 rounded-lg px-4 py-3 flex items-start gap-3">
              <AlertCircle size={16} className="text-amber-600 shrink-0 mt-0.5" />
              <div className="flex-1 text-sm text-amber-800">
                <p className="font-semibold mb-1">Serie no configurada</p>
                <p className="text-xs leading-relaxed">{noSerieError}</p>
                <button
                  type="button"
                  onClick={() => { handleCloseNcNd(); navigate('/admin/facturacion/series'); }}
                  className="mt-2 text-xs text-amber-700 underline hover:text-amber-900 font-medium"
                >
                  Ir a Facturación → Series →
                </button>
              </div>
            </div>
          )}
        </div>
      </Modal>
    </div>
  );
};
