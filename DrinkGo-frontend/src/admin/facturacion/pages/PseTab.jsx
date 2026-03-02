/**
 * PseTab.jsx
 * ──────────
 * PSE – Facturación Electrónica.
 * 3 pestañas: Bandeja Electrónica, Configuración del Proveedor,
 * Historial de Comunicaciones.
 * Permite enviar comprobantes a SUNAT vía PSE simulado.
 */
import { useState, useMemo, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Send, RefreshCw, CheckCircle, Clock, AlertCircle, XCircle,
  Search, FileText, X, Eye, Zap, History, Shield, Wifi, WifiOff,
  Save, Key, Building2, Power,
} from 'lucide-react';
import {
  useBandejaPse,
  useEnviarDocumentoPse,
  useReenviarDocumentoPse,
  useHistorialPse,
  useConfiguracionPse,
  useGuardarConfiguracionPse,
  useProbarConexionPse,
} from '../hooks/useFacturacion';

/* ─── Mapas de estado PSE ─── */
const ESTADO_CONFIG = {
  pendiente_envio: { label: 'Pendiente', badge: 'bg-yellow-100 text-yellow-700 border-yellow-300' },
  enviado:         { label: 'Enviado',   badge: 'bg-blue-100 text-blue-700 border-blue-300' },
  aceptado:        { label: 'Aceptado',  badge: 'bg-green-100 text-green-700 border-green-300' },
  observado:       { label: 'Observado', badge: 'bg-orange-100 text-orange-700 border-orange-300' },
  rechazado:       { label: 'Rechazado', badge: 'bg-red-100 text-red-700 border-red-300' },
  anulado:         { label: 'Anulado',   badge: 'bg-gray-100 text-gray-600 border-gray-300' },
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

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('es-PE', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
};

const formatCurrency = (amount) => {
  if (amount == null) return 'S/ 0.00';
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(amount);
};

/* ─── Opciones de configuración ─── */
const PROVEEDOR_OPTIONS = [
  { value: 'nubefact', label: 'Nubefact' },
  { value: 'efact', label: 'Efact' },
  { value: 'digiflow', label: 'Digiflow' },
];

const ENTORNO_OPTIONS = [
  { value: 'beta', label: 'Beta / Homologación' },
  { value: 'produccion', label: 'Producción' },
];

const URL_TEMPLATES = {
  nubefact_beta: 'https://demo.nubefact.com/pse/v1',
  nubefact_produccion: 'https://api.nubefact.com/pse/v1',
  efact_beta: 'https://demo.efact.pe/pse/v1',
  efact_produccion: 'https://api.efact.pe/pse/v1',
  digiflow_beta: 'https://demo.digiflow.pe/pse/v1',
  digiflow_produccion: 'https://api.digiflow.pe/pse/v1',
};

const TABS_PSE = [
  { id: 'bandeja', label: 'Bandeja Electrónica', icon: FileText },
  { id: 'configuracion', label: 'Configuración del Proveedor', icon: Shield },
  { id: 'historial', label: 'Historial de Comunicaciones', icon: History },
];

/* ═══════════════════════════════════════════════════════════
   COMPONENTE PRINCIPAL
   ═══════════════════════════════════════════════════════════ */
export const PseTab = () => {
  const { negocioId } = useOutletContext();

  /* ─── Data hooks ─── */
  const { data: documentos = [], isLoading: loadingBandeja } = useBandejaPse(negocioId);
  const { data: historial = [], isLoading: loadingHistorial } = useHistorialPse(negocioId);
  const { data: config, isLoading: loadingConfig } = useConfiguracionPse(negocioId);
  const enviarDoc = useEnviarDocumentoPse();
  const reenviarDoc = useReenviarDocumentoPse();
  const guardarConfig = useGuardarConfiguracionPse();
  const probarConexion = useProbarConexionPse();
  /* ─── Estado local ─── */
  const [activeTab, setActiveTab] = useState('bandeja');
  const [selected, setSelected] = useState(new Set());
  const [search, setSearch] = useState('');
  const [filterEstado, setFilterEstado] = useState('');
  const [sending, setSending] = useState(false);
  const [selectedDoc, setSelectedDoc] = useState(null);
  const [testingConn, setTestingConn] = useState(false);
  const [connResult, setConnResult] = useState(null);
  const [togglingPse, setTogglingPse] = useState(false);
  const [lastTestDate, setLastTestDate] = useState(null);
  const [connectionTested, setConnectionTested] = useState(false);
  const [showDeactivateModal, setShowDeactivateModal] = useState(false);

  /* ─── Formulario de configuración ─── */
  const [configForm, setConfigForm] = useState({
    proveedor: '',
    entorno: '',
    rucEmisor: '',
    apiToken: '',
    urlServicio: '',
  });

  /* ─── Sincronizar config del backend ─── */
  useEffect(() => {
    if (config) {
      const prov = config.proveedor || '';
      const ent = config.entorno || '';
      const urlKey = `${prov}_${ent}`;
      setConfigForm({
        proveedor: prov,
        entorno: ent,
        rucEmisor: config.rucEmisor || '',
        apiToken: config.apiToken || '',
        urlServicio: config.urlServicio || URL_TEMPLATES[urlKey] || '',
      });
      /* Si ya tiene token guardado, considerar conexión testeada */
      if (config.apiToken && config.estaActivo) {
        setConnectionTested(true);
      }
    }
  }, [config]);

  /* ─── Valores derivados ─── */
  const pseActivo = config?.estaActivo ?? false;
  const pseConfigurado = pseActivo && !!config?.apiToken;

  /* ─── Forzar pestaña configuración si PSE no está configurado ─── */
  useEffect(() => {
    if (!loadingConfig && !pseConfigurado) {
      setActiveTab('configuracion');
    }
  }, [pseConfigurado, loadingConfig]);
  const proveedorLabel =
    PROVEEDOR_OPTIONS.find((p) => p.value === (config?.proveedor || configForm.proveedor))?.label ||
    'Sin configurar';
  const entornoLabel =
    ENTORNO_OPTIONS.find((e) => e.value === (config?.entorno || configForm.entorno))?.label || '-';

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const total = documentos.length;
    const localesSinEnviar = documentos.filter(
      (d) => d.modoEmision === 'LOCAL' && d.estadoDocumento !== 'aceptado'
    ).length;
    const pendientes = documentos.filter((d) => d.estadoDocumento === 'pendiente_envio').length;
    const aceptados = documentos.filter((d) => d.estadoDocumento === 'aceptado').length;
    const rechazados = documentos.filter((d) => d.estadoDocumento === 'rechazado').length;
    return { total, localesSinEnviar, pendientes, aceptados, rechazados };
  }, [documentos]);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    return documentos.filter((d) => {
      if (search) {
        const q = search.toLowerCase();
        if (
          !d.numeroDocumento?.toLowerCase().includes(q) &&
          !d.razonSocialReceptor?.toLowerCase().includes(q)
        )
          return false;
      }
      if (filterEstado && d.estadoDocumento !== filterEstado) return false;
      return true;
    });
  }, [documentos, search, filterEstado]);

  /* ─── Selección ─── */
  const canSend = (doc) =>
    doc.estadoDocumento === 'pendiente_envio' || doc.estadoDocumento === 'rechazado';

  const toggleSelect = (id) => {
    setSelected((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const toggleSelectAll = () => {
    const sendable = filtered.filter(canSend);
    if (selected.size === sendable.length && sendable.length > 0) {
      setSelected(new Set());
    } else {
      setSelected(new Set(sendable.map((d) => d.id)));
    }
  };

  /* ─── Handlers ─── */
  const handleEnviarSeleccionados = async () => {
    if (selected.size === 0) return;
    setSending(true);
    for (const id of [...selected]) {
      try {
        const doc = documentos.find((d) => d.id === id);
        if (doc?.estadoDocumento === 'rechazado') await reenviarDoc.mutateAsync(id);
        else await enviarDoc.mutateAsync(id);
      } catch {
        /* error manejado por mutation */
      }
    }
    setSelected(new Set());
    setSending(false);
  };

  const handleEnviar = async (doc) => {
    try {
      if (doc.estadoDocumento === 'rechazado') await reenviarDoc.mutateAsync(doc.id);
      else await enviarDoc.mutateAsync(doc.id);
    } catch {
      /* error manejado por mutation */
    }
  };

  const handleTogglePse = async () => {
    setTogglingPse(true);
    try {
      await guardarConfig.mutateAsync({
        negocioId,
        data: { estaActivo: !pseActivo },
      });
    } catch {
      /* error manejado por mutation */
    } finally {
      setTogglingPse(false);
    }
  };

  const handleProbarConexion = async () => {
    if (!configForm.apiToken) {
      setConnResult({ ok: false, msg: 'Error en la conexión: Debe ingresar un API Token válido' });
      setConnectionTested(false);
      return;
    }
    setTestingConn(true);
    setConnResult(null);
    try {
      await probarConexion.mutateAsync(negocioId);
      setConnResult({ ok: true, msg: 'Conectado a PSE — Conexión verificada exitosamente' });
      setConnectionTested(true);
      setLastTestDate(new Date());
    } catch (err) {
      setConnResult({
        ok: false,
        msg: err?.response?.data?.message || 'Error en la conexión con el PSE',
      });
      setConnectionTested(false);
    } finally {
      setTestingConn(false);
    }
  };

  const handleGuardarConfig = async () => {
    try {
      await guardarConfig.mutateAsync({ negocioId, data: { ...configForm, estaActivo: true } });
      setActiveTab('bandeja');
    } catch {
      /* error manejado por mutation */
    }
  };

  const handleDesactivarPse = async () => {
    setTogglingPse(true);
    try {
      await guardarConfig.mutateAsync({
        negocioId,
        data: { ...configForm, estaActivo: false },
      });
      setShowDeactivateModal(false);
      setConnectionTested(false);
      setConnResult(null);
    } catch {
      /* error manejado por mutation */
    } finally {
      setTogglingPse(false);
    }
  };

  const updateConfigField = (field, value) => {
    /* Reset connection test when changing critical fields */
    if (['proveedor', 'entorno', 'apiToken', 'urlServicio'].includes(field)) {
      setConnectionTested(false);
      setConnResult(null);
    }
    setConfigForm((prev) => {
      const next = { ...prev, [field]: value };
      if (field === 'proveedor' || field === 'entorno') {
        const prov = field === 'proveedor' ? value : prev.proveedor;
        const ent = field === 'entorno' ? value : prev.entorno;
        if (prov && ent) {
          const key = `${prov}_${ent}`;
          next.urlServicio = URL_TEMPLATES[key] || '';
        } else {
          next.urlServicio = '';
        }
      }
      return next;
    });
  };

  /* ═══════════════════════════════════════════════════════════
     RENDER
     ═══════════════════════════════════════════════════════════ */
  return (
    <div className="space-y-6">
      {/* ─── HEADER CON ESTADO + BOTÓN DESACTIVAR ─── */}
      {pseConfigurado && (
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 px-3 py-1.5 bg-green-50 border border-green-200 rounded-full">
              <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse" />
              <span className="text-sm font-medium text-green-700">PSE Activo</span>
            </div>
            <span className="text-sm text-gray-500">
              {proveedorLabel} — {entornoLabel}
            </span>
          </div>
          <button
            onClick={() => setShowDeactivateModal(true)}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-red-600 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
          >
            <Power size={16} />
            Desactivar PSE
          </button>
        </div>
      )}

      {/* ─── TAB NAVIGATION ─── */}
      {pseConfigurado && (
        <div className="border-b border-gray-200">
          <nav className="flex gap-6">
            {TABS_PSE.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 pb-3 px-1 text-sm font-medium border-b-2 transition-colors ${
                  activeTab === tab.id
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                }`}
              >
                <tab.icon size={16} />
                {tab.label}
              </button>
            ))}
          </nav>
        </div>
      )}

      {/* ═══════════════════════════════════════════════════════
          TAB 1: BANDEJA ELECTRÓNICA
          ═══════════════════════════════════════════════════════ */}
      {pseConfigurado && activeTab === 'bandeja' && (
        <div className="space-y-5">
          {/* Stats Cards (5) */}
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
            <div className="bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3">
              <div className="p-2 bg-blue-50 rounded-lg">
                <FileText size={20} className="text-blue-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Total</p>
                <p className="text-xl font-bold text-gray-900">{stats.total}</p>
              </div>
            </div>
            <div className="bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3">
              <div className="p-2 bg-orange-50 rounded-lg">
                <AlertCircle size={20} className="text-orange-500" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Locales sin enviar</p>
                <p className="text-xl font-bold text-gray-900">{stats.localesSinEnviar}</p>
              </div>
            </div>
            <div className="bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3">
              <div className="p-2 bg-yellow-50 rounded-lg">
                <Clock size={20} className="text-yellow-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Pendientes envío</p>
                <p className="text-xl font-bold text-gray-900">{stats.pendientes}</p>
              </div>
            </div>
            <div className="bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3">
              <div className="p-2 bg-green-50 rounded-lg">
                <CheckCircle size={20} className="text-green-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Aceptados</p>
                <p className="text-xl font-bold text-gray-900">{stats.aceptados}</p>
              </div>
            </div>
            <div className="bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3">
              <div className="p-2 bg-red-50 rounded-lg">
                <XCircle size={20} className="text-red-500" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Rechazados</p>
                <p className="text-xl font-bold text-gray-900">{stats.rechazados}</p>
              </div>
            </div>
          </div>

          {/* Bandeja Table */}
          <div className="bg-white rounded-xl border border-gray-200 p-6">
            {/* Toolbar */}
            <div className="flex flex-wrap items-center gap-3 mb-5">
              <div className="relative flex-1 min-w-[200px] max-w-xs">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type="text"
                  placeholder="Buscar por número o receptor..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <select
                value={filterEstado}
                onChange={(e) => setFilterEstado(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Todos los estados</option>
                {Object.entries(ESTADO_CONFIG).map(([key, cfg]) => (
                  <option key={key} value={key}>
                    {cfg.label}
                  </option>
                ))}
              </select>
              {selected.size > 0 && (
                <button
                  onClick={handleEnviarSeleccionados}
                  disabled={sending}
                  className="ml-auto flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {sending ? (
                    <RefreshCw size={16} className="animate-spin" />
                  ) : (
                    <Send size={16} />
                  )}
                  Enviar {selected.size} documento{selected.size > 1 ? 's' : ''}
                </button>
              )}
            </div>

            {/* Table */}
            {loadingBandeja ? (
              <div className="text-center py-12 text-gray-500">Cargando documentos...</div>
            ) : filtered.length === 0 ? (
              <div className="text-center py-12 text-gray-400">
                No hay documentos en la bandeja PSE
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-y border-gray-200 bg-gray-50/50">
                      <th className="py-3 px-3 w-[40px]">
                        <input
                          type="checkbox"
                          onChange={toggleSelectAll}
                          checked={
                            selected.size > 0 &&
                            selected.size === filtered.filter(canSend).length
                          }
                          className="rounded border-gray-300"
                        />
                      </th>
                      <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                        Número
                      </th>
                      <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                        Tipo
                      </th>
                      <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                        Receptor
                      </th>
                      <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                        Fecha
                      </th>
                      <th className="text-right py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                        Total
                      </th>
                      <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                        Modo
                      </th>
                      <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                        Estado
                      </th>
                      <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[100px]">
                        Detalle
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((doc) => {
                      const cfg =
                        ESTADO_CONFIG[doc.estadoDocumento] || ESTADO_CONFIG.pendiente_envio;
                      const sendable = canSend(doc);
                      return (
                        <tr
                          key={doc.id}
                          className="border-b border-gray-100 hover:bg-gray-50/50"
                        >
                          <td className="py-3 px-3">
                            {sendable ? (
                              <input
                                type="checkbox"
                                checked={selected.has(doc.id)}
                                onChange={() => toggleSelect(doc.id)}
                                className="rounded border-gray-300"
                              />
                            ) : (
                              <span className="text-gray-300">—</span>
                            )}
                          </td>
                          <td className="py-3 px-3 font-mono text-xs font-medium">
                            {doc.numeroDocumento}
                          </td>
                          <td className="py-3 px-3">
                            {TIPO_DOC_LABELS[doc.tipoDocumento] || doc.tipoDocumento}
                          </td>
                          <td className="py-3 px-3 text-gray-700">
                            {doc.razonSocialReceptor || '-'}
                          </td>
                          <td className="py-3 px-3 text-gray-600">
                            {formatDate(doc.fechaEmision || doc.creadoEn)}
                          </td>
                          <td className="py-3 px-3 text-right font-semibold">
                            {formatCurrency(doc.total)}
                          </td>
                          <td className="py-3 px-3 text-center">
                            <span
                              className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                                doc.modoEmision === 'PSE'
                                  ? 'bg-blue-50 text-blue-700'
                                  : 'bg-gray-100 text-gray-600'
                              }`}
                            >
                              {doc.modoEmision || 'LOCAL'}
                            </span>
                          </td>
                          <td className="py-3 px-3 text-center">
                            <span
                              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${cfg.badge}`}
                            >
                              {cfg.label}
                            </span>
                          </td>
                          <td className="py-3 px-3">
                            <div className="flex justify-center gap-2">
                              <button
                                title="Ver detalles"
                                onClick={() => setSelectedDoc(doc)}
                                className="text-blue-500 hover:text-blue-700"
                              >
                                <Eye size={16} />
                              </button>
                              {sendable && (
                                <button
                                  title={
                                    doc.estadoDocumento === 'rechazado'
                                      ? 'Reenviar'
                                      : 'Enviar a SUNAT'
                                  }
                                  onClick={() => handleEnviar(doc)}
                                  className="text-green-500 hover:text-green-700"
                                >
                                  {doc.estadoDocumento === 'rechazado' ? (
                                    <RefreshCw size={16} />
                                  ) : (
                                    <Send size={16} />
                                  )}
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
          </div>
        </div>
      )}

      {/* ═══════════════════════════════════════════════════════
          TAB 2: CONFIGURACIÓN DEL PROVEEDOR
          ═══════════════════════════════════════════════════════ */}
      {(!pseConfigurado || activeTab === 'configuracion') && (
        <div className="space-y-6">
          {/* Formulario de configuración */}
          <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-6">
            {/* Sección: Proveedor y Entorno */}
            <div>
              <h3 className="text-sm font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <Building2 size={16} className="text-gray-500" />
                Proveedor y Entorno
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-600 mb-1">
                    Proveedor PSE
                  </label>
                  <select
                    value={configForm.proveedor}
                    onChange={(e) => updateConfigField('proveedor', e.target.value)}
                    className={`w-full rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 bg-white ${
                      configForm.proveedor
                        ? 'border-2 border-green-500 focus:ring-green-500'
                        : 'border border-gray-300 focus:ring-blue-500'
                    }`}
                  >
                    <option value="" disabled>Seleccione su PSE</option>
                    {PROVEEDOR_OPTIONS.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1">Entorno</label>
                  <select
                    value={configForm.entorno}
                    onChange={(e) => updateConfigField('entorno', e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                  >
                    <option value="" disabled>Seleccione su entorno</option>
                    {ENTORNO_OPTIONS.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1">
                    RUC Emisor
                  </label>
                  <input
                    type="text"
                    value={configForm.rucEmisor}
                    onChange={(e) => updateConfigField('rucEmisor', e.target.value)}
                    placeholder="20123456789"
                    maxLength={11}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            </div>

            <hr className="border-gray-100" />

            {/* Sección: Credenciales */}
            <div>
              <h3 className="text-sm font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <Key size={16} className="text-gray-500" />
                Credenciales – {PROVEEDOR_OPTIONS.find((p) => p.value === configForm.proveedor)?.label || 'PSE'}
              </h3>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-600 mb-1">
                    API Key / Token <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    value={configForm.apiToken}
                    onChange={(e) => updateConfigField('apiToken', e.target.value)}
                    placeholder="Ingrese su API Token"
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  <p className="text-xs text-gray-400 mt-1">
                    Proporcionado por {PROVEEDOR_OPTIONS.find((p) => p.value === configForm.proveedor)?.label || 'su proveedor'} al registrarte
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-600 mb-1">
                    URL del Servicio
                  </label>
                  <input
                    type="text"
                    value={configForm.urlServicio}
                    onChange={(e) => updateConfigField('urlServicio', e.target.value)}
                    placeholder="https://..."
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            </div>

            {/* Resultado de prueba de conexión */}
            {connResult && (
              <div
                className={`flex items-center gap-2 p-3 rounded-lg text-sm ${
                  connResult.ok
                    ? 'bg-green-50 text-green-700 border border-green-200'
                    : 'bg-red-50 text-red-700 border border-red-200'
                }`}
              >
                {connResult.ok ? <CheckCircle size={16} /> : <XCircle size={16} />}
                {connResult.msg}
              </div>
            )}

            {/* Botones */}
            <div className="flex items-center justify-between pt-4">
              <button
                onClick={handleProbarConexion}
                disabled={testingConn}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {testingConn ? (
                  <RefreshCw size={16} className="animate-spin" />
                ) : (
                  <Wifi size={16} />
                )}
                Probar conexión
              </button>
              <button
                onClick={handleGuardarConfig}
                disabled={guardarConfig.isPending || !connectionTested}
                title={!connectionTested ? 'Primero debe probar la conexión exitosamente' : ''}
                className={`flex items-center gap-2 px-5 py-2.5 text-sm font-medium rounded-lg disabled:opacity-50 disabled:cursor-not-allowed ${
                  connectionTested
                    ? 'bg-green-600 text-white hover:bg-green-700'
                    : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                }`}
              >
                {guardarConfig.isPending ? (
                  <RefreshCw size={16} className="animate-spin" />
                ) : (
                  <Save size={16} />
                )}
                Guardar credenciales
              </button>
            </div>
            {/* Última prueba */}
            {lastTestDate && (
              <p className="text-xs text-gray-400">
                Última prueba exitosa: {formatDateTime(lastTestDate)}
              </p>
            )}
          </div>

          {/* Mensaje informativo si no está configurado */}
          {!pseConfigurado && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 flex items-start gap-3">
              <AlertCircle size={20} className="text-amber-500 mt-0.5 shrink-0" />
              <div>
                <p className="text-sm font-medium text-amber-800">Configure su PSE para continuar</p>
                <p className="text-xs text-amber-600 mt-1">
                  Complete los campos de proveedor y credenciales, luego haga clic en "Probar conexión" para verificar.
                  Una vez conectado, presione "Guardar credenciales" para habilitar la Bandeja Electrónica e Historial.
                </p>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ═══════════════════════════════════════════════════════
          TAB 3: HISTORIAL DE COMUNICACIONES
          ═══════════════════════════════════════════════════════ */}
      {pseConfigurado && activeTab === 'historial' && (
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <h2 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
            <History size={20} className="text-gray-500" />
            Historial de comunicaciones PSE
          </h2>

          {loadingHistorial ? (
            <div className="text-center py-12 text-gray-500">Cargando historial...</div>
          ) : historial.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              No hay registros de comunicación con el PSE
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-y border-gray-200 bg-gray-50/50">
                    <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                      Fecha
                    </th>
                    <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                      Operación
                    </th>
                    <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                      Documento
                    </th>
                    <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                      Proveedor
                    </th>
                    <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                      Resultado
                    </th>
                    <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                      Código
                    </th>
                    <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">
                      Mensaje
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {historial.map((h) => (
                    <tr
                      key={h.id}
                      className="border-b border-gray-100 hover:bg-gray-50/50"
                    >
                      <td className="py-3 px-3 text-gray-600 text-xs">
                        {formatDateTime(h.creadoEn)}
                      </td>
                      <td className="py-3 px-3">
                        <span className="px-2 py-0.5 rounded bg-blue-50 text-blue-700 text-xs font-medium">
                          {h.tipoOperacion}
                        </span>
                      </td>
                      <td className="py-3 px-3 font-mono text-xs">
                        {h.numeroDocumento}
                      </td>
                      <td className="py-3 px-3 text-gray-600 text-xs">
                        {h.proveedor || '-'}
                      </td>
                      <td className="py-3 px-3 text-center">
                        {h.exitoso ? (
                          <span className="inline-flex items-center gap-1 text-green-600 text-xs font-medium">
                            <CheckCircle size={14} /> Exitoso
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 text-red-500 text-xs font-medium">
                            <AlertCircle size={14} /> Fallido
                          </span>
                        )}
                      </td>
                      <td className="py-3 px-3 text-gray-500 text-xs">
                        {h.codigoRespuesta || '-'}
                      </td>
                      <td
                        className="py-3 px-3 text-gray-600 text-xs max-w-[200px] truncate"
                        title={h.mensajeRespuesta}
                      >
                        {h.mensajeRespuesta || '-'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* ═══════════════════════════════════════════════════════
          MODAL: DETALLE DEL COMPROBANTE
          ═══════════════════════════════════════════════════════ */}
      {selectedDoc && (() => {
        const estadoCfg = ESTADO_CONFIG[selectedDoc.estadoDocumento] || ESTADO_CONFIG.pendiente_envio;
        const isAceptado = selectedDoc.estadoDocumento === 'aceptado';
        const isRechazado = selectedDoc.estadoDocumento === 'rechazado';
        return (
          <div
            className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
            onClick={() => setSelectedDoc(null)}
          >
            <div
              className="bg-white rounded-lg max-w-md w-full shadow-xl max-h-[85vh] flex flex-col"
              onClick={(e) => e.stopPropagation()}
            >
              {/* Header */}
              <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200">
                <h2 className="text-sm font-bold text-gray-900">Detalle del comprobante</h2>
                <button
                  onClick={() => setSelectedDoc(null)}
                  className="text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <X size={16} />
                </button>
              </div>

              {/* Body */}
              <div className="px-4 py-2 overflow-y-auto flex-1 text-xs">
                {/* Info grid — 2 columnas compactas */}
                <div className="divide-y divide-gray-100">
                  {[
                    ['Número', <span className="font-mono">{selectedDoc.numeroDocumento}</span>],
                    ['Tipo', TIPO_DOC_LABELS[selectedDoc.tipoDocumento] || selectedDoc.tipoDocumento],
                    ['Receptor', selectedDoc.razonSocialReceptor || '-'],
                    ['Fecha emisión', formatDate(selectedDoc.fechaEmision || selectedDoc.creadoEn)],
                    ['Fecha envío', selectedDoc.fechaEnvio ? formatDateTime(selectedDoc.fechaEnvio) : '-'],
                    ['Total', formatCurrency(selectedDoc.total)],
                  ].map(([label, value], i) => (
                    <div key={i} className="flex items-center justify-between py-1.5">
                      <span className="text-gray-500">{label}</span>
                      <span className="font-medium text-gray-900">{value}</span>
                    </div>
                  ))}
                </div>

                {/* SUNAT section */}
                <div className="mt-2 pt-2 border-t border-gray-200 divide-y divide-gray-100">
                  <div className="flex items-center justify-between py-1.5">
                    <span className="text-gray-500">Intentos</span>
                    <span className="font-medium text-gray-900">{selectedDoc.intentosEnvio || 0}</span>
                  </div>
                  <div className="flex items-center justify-between py-1.5">
                    <span className="text-gray-500">Código SUNAT</span>
                    <span className="font-medium text-gray-900">{selectedDoc.codigoRespuestaSunat ?? '-'}</span>
                  </div>
                  <div className="flex items-center justify-between py-1.5">
                    <span className="text-gray-500">Estado SUNAT</span>
                    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-semibold border ${estadoCfg.badge}`}>
                      {estadoCfg.label}
                    </span>
                  </div>
                </div>

                {/* Hash CDR */}
                {selectedDoc.hashSunat && (
                  <div className="mt-2 pt-2 border-t border-gray-200">
                    <span className="text-gray-500 text-[10px] uppercase tracking-wider">Hash CDR (SHA-256)</span>
                    <p className="font-mono text-[10px] text-gray-700 break-all select-all bg-gray-50 rounded px-2 py-1 mt-1">
                      {selectedDoc.hashSunat}
                    </p>
                  </div>
                )}

                {/* Respuesta SUNAT */}
                {selectedDoc.respuestaSunat && (
                  <div className="mt-2 pt-2 border-t border-gray-200">
                    <span className="text-gray-500 text-[10px] uppercase tracking-wider">Respuesta SUNAT</span>
                    <div className={`mt-1 rounded px-2 py-1.5 text-xs border ${
                      isAceptado
                        ? 'bg-green-50 border-green-200 text-green-800'
                        : isRechazado
                        ? 'bg-red-50 border-red-200 text-red-800'
                        : 'bg-gray-50 border-gray-200 text-gray-700'
                    }`}>
                      {isAceptado && <CheckCircle size={12} className="inline mr-1 -mt-0.5 text-green-600" />}
                      {isRechazado && <XCircle size={12} className="inline mr-1 -mt-0.5 text-red-600" />}
                      {selectedDoc.respuestaSunat}
                    </div>
                  </div>
                )}

                {/* XML UBL 2.1 */}
                {selectedDoc.xmlDocumento && (
                  <div className="mt-2 pt-2 border-t border-gray-200">
                    <span className="text-gray-500 text-[10px] uppercase tracking-wider">XML UBL 2.1</span>
                    <div className="mt-1 rounded overflow-hidden border border-gray-800" style={{ backgroundColor: '#000' }}>
                      <div className="flex items-center gap-1 px-2 py-1 border-b border-gray-800" style={{ backgroundColor: '#111' }}>
                        <span className="w-1.5 h-1.5 rounded-full bg-red-500" />
                        <span className="w-1.5 h-1.5 rounded-full bg-yellow-500" />
                        <span className="w-1.5 h-1.5 rounded-full bg-green-500" />
                        <span className="text-[9px] text-gray-500 ml-1 font-mono">invoice.xml</span>
                      </div>
                      <div className="px-2 py-1.5 max-h-32 overflow-y-auto overflow-x-auto" style={{ backgroundColor: '#000' }}>
                        <pre className="text-[10px] font-mono whitespace-pre leading-relaxed" style={{ color: '#4ade80' }}>
{selectedDoc.xmlDocumento}
                        </pre>
                      </div>
                    </div>
                  </div>
                )}
              </div>

              {/* Footer */}
              <div className="px-4 py-2 border-t border-gray-200 flex items-center justify-between bg-gray-50/50 rounded-b-lg">
                {canSend(selectedDoc) ? (
                  <button
                    onClick={() => { handleEnviar(selectedDoc); setSelectedDoc(null); }}
                    className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors"
                  >
                    <Send size={12} />
                    {selectedDoc.estadoDocumento === 'rechazado' ? 'Reenviar a SUNAT' : 'Enviar a SUNAT'}
                  </button>
                ) : (
                  <div />
                )}
                <button
                  onClick={() => setSelectedDoc(null)}
                  className="px-3 py-1.5 text-xs font-medium text-gray-700 bg-white border border-gray-200 rounded-md hover:bg-gray-100 transition-colors"
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        );
      })()}
      {/* ═══════════════════════════════════════════════════════
          MODAL: CONFIRMAR DESACTIVAR PSE
          ═══════════════════════════════════════════════════════ */}
      {showDeactivateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full mx-4 p-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2 bg-red-100 rounded-full">
                <AlertCircle size={24} className="text-red-600" />
              </div>
              <h3 className="text-lg font-bold text-gray-900">Desactivar PSE</h3>
            </div>
            <p className="text-sm text-gray-600 mb-2">
              ¿Está seguro que desea desactivar el PSE?
            </p>
            <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 mb-6">
              <p className="text-sm text-amber-800">
                <strong>Advertencia:</strong> Al desactivar el PSE, no podrá seguir enviando comprobantes electrónicos a SUNAT a través del proveedor. Para volver a enviar, deberá conectarse nuevamente al PSE.
              </p>
            </div>
            <div className="flex justify-end gap-3">
              <button
                onClick={() => setShowDeactivateModal(false)}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={handleDesactivarPse}
                disabled={togglingPse}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 disabled:opacity-50 transition-colors"
              >
                {togglingPse ? (
                  <RefreshCw size={16} className="animate-spin" />
                ) : (
                  <Power size={16} />
                )}
                Sí, desactivar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
