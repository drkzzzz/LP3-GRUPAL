/**
 * ComprobantesTab.jsx
 * ───────────────────
 * Comprobantes electrónicos - facturación LOCAL.
 * Muestra los comprobantes generados por ventas POS.
 * Estados locales: Confirmado (aceptado) y Anulado.
 * NO tiene interacción con SUNAT — eso se hace en PSE.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Eye, XCircle, Search, CheckCircle, AlertCircle, Hash, X } from 'lucide-react';
import { useComprobantes, useCambiarEstadoComprobante } from '../hooks/useFacturacion';

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
  const { data: comprobantes = [], isLoading } = useComprobantes(negocioId);
  const cambiarEstado = useCambiarEstadoComprobante();

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
  const handleAnular = async (id) => {
    try {
      await cambiarEstado.mutateAsync({ id, estado: 'anulado' });
    } catch {
      // Error manejado por mutation
    }
  };

  return (
    <div className="space-y-6">
      {/* ─── Header ─── */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Comprobantes Electrónicos</h1>
        <p className="text-gray-600 mt-1">Consulta y gestión de boletas, facturas y notas de crédito</p>
      </div>

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
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[100px]">Acciones</th>
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
                      <td className="py-3 px-3 text-gray-700">{doc.razonSocialReceptor || doc.receptor || '-'}</td>
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
                          {!esAnulado && (
                            <button title="Anular" onClick={() => handleAnular(doc.id)} className="text-red-500 hover:text-red-700">
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

      {/* ─── Modal detalle ─── */}
      {selectedDoc && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setSelectedDoc(null)}>
          <div className="bg-white rounded-xl max-w-lg w-full p-6 space-y-4" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-bold text-gray-900">Detalle del Comprobante</h2>
              <button onClick={() => setSelectedDoc(null)} className="text-gray-400 hover:text-gray-600">
                <X size={20} />
              </button>
            </div>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500 text-xs mb-0.5">Número</p>
                <p className="font-mono font-medium">{selectedDoc.numeroDocumento}</p>
              </div>
              <div>
                <p className="text-gray-500 text-xs mb-0.5">Tipo</p>
                <p>{TIPO_DOC_LABELS[selectedDoc.tipoDocumento] || selectedDoc.tipoDocumento}</p>
              </div>
              <div>
                <p className="text-gray-500 text-xs mb-0.5">Fecha Emisión</p>
                <p>{formatDate(selectedDoc.fechaEmision || selectedDoc.creadoEn)}</p>
              </div>
              <div>
                <p className="text-gray-500 text-xs mb-0.5">Estado</p>
                {(() => {
                  const est = getEstadoLocal(selectedDoc.estadoDocumento);
                  return (
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${est.badge}`}>
                      {est.label}
                    </span>
                  );
                })()}
              </div>
              <div>
                <p className="text-gray-500 text-xs mb-0.5">Receptor</p>
                <p>{selectedDoc.razonSocialReceptor || selectedDoc.receptor || '-'}</p>
              </div>
              <div>
                <p className="text-gray-500 text-xs mb-0.5">Moneda</p>
                <p>{selectedDoc.moneda || 'PEN'}</p>
              </div>
              <div className="col-span-2 grid grid-cols-3 gap-4 pt-3 border-t border-gray-100">
                <div>
                  <p className="text-gray-500 text-xs mb-0.5">Subtotal</p>
                  <p className="font-medium">{formatCurrency(selectedDoc.subtotal)}</p>
                </div>
                <div>
                  <p className="text-gray-500 text-xs mb-0.5">IGV</p>
                  <p className="font-medium">{formatCurrency(selectedDoc.impuestos)}</p>
                </div>
                <div>
                  <p className="text-gray-500 text-xs mb-0.5">Total</p>
                  <p className="font-bold text-lg">{formatCurrency(selectedDoc.total)}</p>
                </div>
              </div>
            </div>
            <div className="flex justify-end pt-2">
              <button
                onClick={() => setSelectedDoc(null)}
                className="px-4 py-2 text-sm bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
