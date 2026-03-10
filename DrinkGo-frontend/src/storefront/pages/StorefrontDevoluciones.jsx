import { useState, useMemo, useCallback } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  RotateCcw, Loader2, PackageX, Plus, Eye, ChevronDown,
  ChevronUp, Package, AlertCircle, X, Check, Minus,
} from 'lucide-react';
import toast from 'react-hot-toast';
import { useStorefrontAuthStore } from '../stores/storefrontAuthStore';
import { storefrontService } from '../services/storefrontService';

/* ─── Constantes ─── */
const ESTADO_COLORS = {
  solicitada: 'bg-yellow-100 text-yellow-700 border-yellow-300',
  aprobada: 'bg-blue-100 text-blue-700 border-blue-300',
  rechazada: 'bg-red-100 text-red-700 border-red-300',
  completada: 'bg-green-100 text-green-700 border-green-300',
  procesando: 'bg-indigo-100 text-indigo-700 border-indigo-300',
};

const ESTADO_LABELS = {
  solicitada: 'Solicitada',
  aprobada: 'Aprobada',
  rechazada: 'Rechazada',
  completada: 'Completada',
  procesando: 'Procesando',
};

const CATEGORIA_OPTIONS = [
  { value: 'defectuoso', label: 'Producto defectuoso' },
  { value: 'articulo_incorrecto', label: 'Artículo incorrecto' },
  { value: 'cambio_cliente', label: 'Cambio solicitado' },
  { value: 'vencido', label: 'Producto vencido' },
  { value: 'danado', label: 'Producto dañado' },
  { value: 'otro', label: 'Otro motivo' },
];

const METODO_REEMBOLSO_OPTIONS = [
  { value: 'pago_original', label: 'Método de pago original' },
  { value: 'efectivo', label: 'Efectivo' },
  { value: 'credito_tienda', label: 'Crédito en tienda' },
  { value: 'transferencia_bancaria', label: 'Transferencia bancaria' },
];

const CATEGORIA_LABELS = Object.fromEntries(CATEGORIA_OPTIONS.map((o) => [o.value, o.label]));
const REEMBOLSO_LABELS = Object.fromEntries(METODO_REEMBOLSO_OPTIONS.map((o) => [o.value, o.label]));

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('es-PE', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
};

const formatCurrency = (amount) => {
  if (amount == null) return '-';
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(amount);
};

/* ═══════════════════════════════════════════════════
   Componente principal
   ═══════════════════════════════════════════════════ */
export const StorefrontDevoluciones = () => {
  const { slug } = useOutletContext();
  const navigate = useNavigate();
  const { isAuthenticated } = useStorefrontAuthStore();

  if (!isAuthenticated()) {
    navigate(`/tienda/${slug}/login`, { replace: true });
    return null;
  }

  return <DevolucionesContent slug={slug} />;
};

/* ═══════════════════════════════════════════════════
   Contenido principal: Lista + Crear + Detalle
   ═══════════════════════════════════════════════════ */
const DevolucionesContent = ({ slug }) => {
  const queryClient = useQueryClient();
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedDev, setSelectedDev] = useState(null);
  const [filterEstado, setFilterEstado] = useState('todos');

  /* ── Devoluciones ── */
  const { data: devoluciones = [], isLoading, isError } = useQuery({
    queryKey: ['storefront-devoluciones', slug],
    queryFn: () => storefrontService.getMisDevoluciones(slug),
    staleTime: 1000 * 60 * 2,
  });

  /* ── Pedidos entregados (para el form de solicitud) ── */
  const { data: pedidos = [] } = useQuery({
    queryKey: ['storefront-pedidos', slug],
    queryFn: () => storefrontService.getMisPedidos(slug),
    enabled: isFormOpen,
    select: (data) => {
      const pedidoIdsConDevolucion = new Set(
        devoluciones.map((d) => d.pedido?.id).filter(Boolean)
      );
      return data.filter(
        (p) => p.estadoPedido === 'entregado' && !pedidoIdsConDevolucion.has(p.id)
      );
    },
  });

  /* ── Mutation crear devolución ── */
  const crearMutation = useMutation({
    mutationFn: (devData) => storefrontService.solicitarDevolucion(slug, devData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['storefront-devoluciones'] });
      toast.success('¡Solicitud de devolución enviada!');
      setIsFormOpen(false);
    },
    onError: (err) => {
      toast.error(err.response?.data?.message || 'Error al solicitar devolución');
    },
  });

  /* ── Filtrado ── */
  const filtered = useMemo(() => {
    if (filterEstado === 'todos') return devoluciones;
    return devoluciones.filter((d) => d.estado?.toLowerCase() === filterEstado);
  }, [devoluciones, filterEstado]);

  const stats = useMemo(() => ({
    total: devoluciones.length,
    solicitadas: devoluciones.filter((d) => d.estado === 'solicitada').length,
    aprobadas: devoluciones.filter((d) => d.estado === 'aprobada' || d.estado === 'completada').length,
    rechazadas: devoluciones.filter((d) => d.estado === 'rechazada').length,
  }), [devoluciones]);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <Loader2 size={32} className="animate-spin text-amber-500" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Mis Devoluciones</h1>
          <p className="text-gray-500 text-sm mt-1">Historial de solicitudes de devolución</p>
        </div>
        <button
          onClick={() => setIsFormOpen(true)}
          className="flex items-center gap-2 px-4 py-2.5 bg-amber-600 text-white rounded-xl text-sm font-semibold hover:bg-amber-700 transition-colors shadow-sm"
        >
          <Plus size={18} />
          Solicitar Devolución
        </button>
      </div>

      {/* Stats resumidos */}
      {devoluciones.length > 0 && (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <StatMini label="Total" value={stats.total} color="gray" />
          <StatMini label="Solicitadas" value={stats.solicitadas} color="yellow" />
          <StatMini label="Aprobadas" value={stats.aprobadas} color="green" />
          <StatMini label="Rechazadas" value={stats.rechazadas} color="red" />
        </div>
      )}

      {/* Filtro de estado */}
      {devoluciones.length > 0 && (
        <div className="flex gap-2 flex-wrap">
          {['todos', 'solicitada', 'aprobada', 'completada', 'rechazada'].map((estado) => (
            <button
              key={estado}
              onClick={() => setFilterEstado(estado)}
              className={`px-3 py-1.5 rounded-full text-xs font-medium border transition-colors ${
                filterEstado === estado
                  ? 'bg-amber-600 text-white border-amber-600'
                  : 'bg-white text-gray-600 border-gray-300 hover:border-amber-400'
              }`}
            >
              {estado === 'todos' ? 'Todos' : ESTADO_LABELS[estado] || estado}
            </button>
          ))}
        </div>
      )}

      {/* Error */}
      {isError && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-4 text-red-700 text-sm">
          No se pudieron cargar las devoluciones. Intente más tarde.
        </div>
      )}

      {/* Empty state */}
      {filtered.length === 0 && !isError ? (
        <div className="bg-white rounded-2xl border border-gray-200 p-12 text-center shadow-sm">
          <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <PackageX size={28} className="text-gray-400" />
          </div>
          <h3 className="text-lg font-semibold text-gray-800 mb-2">
            {devoluciones.length === 0 ? 'Sin devoluciones' : 'Sin resultados'}
          </h3>
          <p className="text-gray-500 text-sm">
            {devoluciones.length === 0
              ? 'No tienes solicitudes de devolución registradas.'
              : 'No hay devoluciones con el filtro seleccionado.'}
          </p>
          {devoluciones.length === 0 && (
            <button
              onClick={() => setIsFormOpen(true)}
              className="mt-4 inline-flex items-center gap-2 px-4 py-2 bg-amber-600 text-white rounded-xl text-sm font-semibold hover:bg-amber-700 transition-colors"
            >
              <Plus size={16} />
              Solicitar Devolución
            </button>
          )}
        </div>
      ) : (
        <div className="space-y-4">
          {filtered.map((devolucion) => (
            <DevolucionCard
              key={devolucion.id}
              devolucion={devolucion}
              onView={() => setSelectedDev(devolucion)}
            />
          ))}
        </div>
      )}

      {/* Modal: Crear solicitud */}
      {isFormOpen && (
        <SolicitarDevolucionModal
          pedidos={pedidos}
          isSubmitting={crearMutation.isPending}
          onSubmit={(data) => crearMutation.mutateAsync(data)}
          onClose={() => setIsFormOpen(false)}
        />
      )}

      {/* Modal: Detalle */}
      {selectedDev && (
        <DetalleDevolucionModal
          devolucion={selectedDev}
          onClose={() => setSelectedDev(null)}
        />
      )}
    </div>
  );
};

/* ═══════════════════════════════════════════════════
   Stat mini
   ═══════════════════════════════════════════════════ */
const StatMini = ({ label, value, color }) => {
  const colors = {
    gray: 'bg-gray-50 border-gray-200 text-gray-700',
    yellow: 'bg-yellow-50 border-yellow-200 text-yellow-700',
    green: 'bg-green-50 border-green-200 text-green-700',
    red: 'bg-red-50 border-red-200 text-red-700',
  };
  return (
    <div className={`rounded-xl border p-3 text-center ${colors[color]}`}>
      <p className="text-2xl font-bold">{value}</p>
      <p className="text-xs mt-0.5 opacity-80">{label}</p>
    </div>
  );
};

/* ═══════════════════════════════════════════════════
   Card de devolución
   ═══════════════════════════════════════════════════ */
const DevolucionCard = ({ devolucion, onView }) => {
  const estado = devolucion.estado?.toString().toLowerCase();
  const colorClass = ESTADO_COLORS[estado] || 'bg-gray-100 text-gray-700 border-gray-300';
  const estadoLabel = ESTADO_LABELS[estado] || devolucion.estado || '-';

  return (
    <div
      className="bg-white rounded-2xl border border-gray-200 p-5 shadow-sm hover:shadow-md transition-shadow cursor-pointer"
      onClick={onView}
    >
      {/* Cabecera */}
      <div className="flex items-start justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-amber-100 rounded-full flex items-center justify-center shrink-0">
            <RotateCcw size={18} className="text-amber-600" />
          </div>
          <div>
            <p className="font-semibold text-gray-900 text-sm">
              {devolucion.numeroDevolucion || `#DEV-${devolucion.id}`}
            </p>
            <p className="text-xs text-gray-500 mt-0.5">
              {formatDate(devolucion.solicitadoEn || devolucion.creadoEn)}
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${colorClass}`}>
            {estadoLabel}
          </span>
          <button
            onClick={(e) => { e.stopPropagation(); onView(); }}
            className="p-1.5 text-gray-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
            title="Ver detalle"
          >
            <Eye size={16} />
          </button>
        </div>
      </div>

      {/* Info */}
      <div className="mt-4 grid grid-cols-2 sm:grid-cols-4 gap-3">
        <InfoField label="Tipo" value={devolucion.tipoDevolucion === 'total' ? 'Total' : 'Parcial'} />
        <InfoField label="Categoría" value={CATEGORIA_LABELS[devolucion.categoriaMotivo] || devolucion.categoriaMotivo || '-'} />
        <InfoField label="Total" value={formatCurrency(devolucion.total)} bold />
        <InfoField label="Reembolso" value={REEMBOLSO_LABELS[devolucion.metodoReembolso] || devolucion.metodoReembolso || '-'} />
      </div>

      {/* Pedido asociado */}
      {devolucion.pedido && (
        <div className="mt-3 pt-3 border-t border-gray-100 flex items-center gap-2">
          <Package size={14} className="text-gray-400" />
          <span className="text-xs text-gray-500">
            Pedido: <span className="font-medium text-gray-700">{devolucion.pedido.numeroPedido || `#${devolucion.pedido.id}`}</span>
          </span>
        </div>
      )}

      {/* Motivo */}
      {devolucion.detalleMotivo && (
        <div className="mt-2 pt-2 border-t border-gray-100">
          <p className="text-xs text-gray-400">Motivo</p>
          <p className="text-sm text-gray-600 line-clamp-2">{devolucion.detalleMotivo}</p>
        </div>
      )}

      {/* Razón de rechazo */}
      {estado === 'rechazada' && devolucion.razonRechazo && (
        <div className="mt-2 p-3 bg-red-50 border border-red-200 rounded-xl">
          <p className="text-xs font-medium text-red-600 mb-1">Razón del rechazo</p>
          <p className="text-sm text-red-700">{devolucion.razonRechazo}</p>
        </div>
      )}
    </div>
  );
};

const InfoField = ({ label, value, bold }) => (
  <div>
    <p className="text-xs text-gray-400 mb-0.5">{label}</p>
    <p className={`text-sm ${bold ? 'font-semibold text-gray-900' : 'font-medium text-gray-700'}`}>{value}</p>
  </div>
);

/* ═══════════════════════════════════════════════════
   Modal: Detalle de Devolución
   ═══════════════════════════════════════════════════ */
const DetalleDevolucionModal = ({ devolucion, onClose }) => {
  const estado = devolucion.estado?.toString().toLowerCase();
  const colorClass = ESTADO_COLORS[estado] || 'bg-gray-100 text-gray-700 border-gray-300';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50" onClick={onClose}>
      <div
        className="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between p-5 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-amber-100 rounded-full flex items-center justify-center">
              <RotateCcw size={18} className="text-amber-600" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-gray-900">
                {devolucion.numeroDevolucion || `#DEV-${devolucion.id}`}
              </h2>
              <p className="text-xs text-gray-500">{formatDate(devolucion.solicitadoEn || devolucion.creadoEn)}</p>
            </div>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-full transition-colors">
            <X size={20} className="text-gray-500" />
          </button>
        </div>

        {/* Body */}
        <div className="p-5 space-y-4">
          {/* Estado */}
          <div className="flex justify-center">
            <span className={`inline-flex items-center px-4 py-1.5 rounded-full text-sm font-semibold border ${colorClass}`}>
              {ESTADO_LABELS[estado] || devolucion.estado}
            </span>
          </div>

          {/* Info grid */}
          <div className="grid grid-cols-2 gap-4">
            <InfoField label="Tipo de Devolución" value={devolucion.tipoDevolucion === 'total' ? 'Total' : 'Parcial'} />
            <InfoField label="Categoría Motivo" value={CATEGORIA_LABELS[devolucion.categoriaMotivo] || devolucion.categoriaMotivo || '-'} />
            <InfoField label="Método Reembolso" value={REEMBOLSO_LABELS[devolucion.metodoReembolso] || devolucion.metodoReembolso || '-'} />
            <InfoField label="Total" value={formatCurrency(devolucion.total)} bold />
          </div>

          {/* Pedido asociado */}
          {devolucion.pedido && (
            <div className="p-3 bg-gray-50 rounded-xl border border-gray-200">
              <p className="text-xs text-gray-400 mb-1">Pedido Asociado</p>
              <div className="flex items-center gap-2">
                <Package size={16} className="text-gray-500" />
                <span className="text-sm font-semibold text-gray-800">
                  {devolucion.pedido.numeroPedido || `#${devolucion.pedido.id}`}
                </span>
                {devolucion.pedido.total != null && (
                  <span className="text-xs text-gray-500 ml-auto">
                    Total pedido: {formatCurrency(devolucion.pedido.total)}
                  </span>
                )}
              </div>
            </div>
          )}

          {/* Motivo */}
          {devolucion.detalleMotivo && (
            <div>
              <p className="text-xs text-gray-400 mb-1">Detalle del Motivo</p>
              <p className="text-sm text-gray-700 bg-gray-50 rounded-xl p-3 border border-gray-200">
                {devolucion.detalleMotivo}
              </p>
            </div>
          )}

          {/* Notas */}
          {devolucion.notas && (
            <div>
              <p className="text-xs text-gray-400 mb-1">Notas Adicionales</p>
              <p className="text-sm text-gray-600 bg-gray-50 rounded-xl p-3 border border-gray-200">
                {devolucion.notas}
              </p>
            </div>
          )}

          {/* Razón rechazo */}
          {estado === 'rechazada' && devolucion.razonRechazo && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-xl">
              <p className="text-xs font-medium text-red-600 mb-1">Razón del Rechazo</p>
              <p className="text-sm text-red-700">{devolucion.razonRechazo}</p>
            </div>
          )}

          {/* Fechas */}
          <div className="pt-3 border-t border-gray-200 space-y-1.5">
            <p className="text-xs text-gray-400 font-medium mb-2">Historial de Fechas</p>
            <DateRow label="Solicitado" date={devolucion.solicitadoEn} />
            {devolucion.aprobadoEn && <DateRow label="Aprobado" date={devolucion.aprobadoEn} />}
            {devolucion.completadoEn && <DateRow label="Completado" date={devolucion.completadoEn} />}
            {devolucion.rechazadoEn && <DateRow label="Rechazado" date={devolucion.rechazadoEn} />}
          </div>
        </div>

        {/* Footer */}
        <div className="p-5 border-t border-gray-200">
          <button
            onClick={onClose}
            className="w-full py-2.5 px-4 bg-gray-100 text-gray-700 rounded-xl text-sm font-semibold hover:bg-gray-200 transition-colors"
          >
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
};

const DateRow = ({ label, date }) => (
  <div className="flex justify-between text-xs">
    <span className="text-gray-500">{label}</span>
    <span className="text-gray-700 font-medium">{formatDate(date)}</span>
  </div>
);

/* ═══════════════════════════════════════════════════
   Modal: Solicitar Devolución
   ═══════════════════════════════════════════════════ */
const SolicitarDevolucionModal = ({ pedidos, isSubmitting, onSubmit, onClose }) => {
  const [step, setStep] = useState(1); // 1: seleccionar pedido, 2: formulario
  const [selectedPedido, setSelectedPedido] = useState(null);
  const [tipoDevolucion, setTipoDevolucion] = useState('parcial');
  const [categoriaMotivo, setCategoriaMotivo] = useState('');
  const [detalleMotivo, setDetalleMotivo] = useState('');
  const [metodoReembolso, setMetodoReembolso] = useState('pago_original');
  const [notas, setNotas] = useState('');
  const [productosSeleccionados, setProductosSeleccionados] = useState({});
  const [errors, setErrors] = useState({});

  const detalles = selectedPedido?.detalles || [];

  /* Cuando selecciona un pedido, pasar al paso 2 */
  const handleSelectPedido = (pedido) => {
    setSelectedPedido(pedido);
    setProductosSeleccionados({});
    setStep(2);
  };

  /* Cuando cambia tipo de devolución */
  const handleTipoChange = useCallback((tipo) => {
    setTipoDevolucion(tipo);
    if (tipo === 'total') {
      const sel = {};
      detalles.forEach((d) => { sel[d.id] = Number(d.cantidad); });
      setProductosSeleccionados(sel);
    } else {
      setProductosSeleccionados({});
    }
  }, [detalles]);

  /* Toggle selección de producto */
  const toggleProducto = (detalleId, maxQty) => {
    if (tipoDevolucion === 'total') return;
    setProductosSeleccionados((prev) => {
      const copy = { ...prev };
      if (copy[detalleId]) { delete copy[detalleId]; } else { copy[detalleId] = 1; }
      return copy;
    });
  };

  /* Cambiar cantidad */
  const updateCantidad = (detalleId, newQty, maxQty) => {
    if (tipoDevolucion === 'total') return;
    const qty = Math.max(1, Math.min(Number(newQty), maxQty));
    setProductosSeleccionados((prev) => ({ ...prev, [detalleId]: qty }));
  };

  /* Calcular total */
  const totalDevolucion = useMemo(() => {
    let total = 0;
    for (const [detalleId, qty] of Object.entries(productosSeleccionados)) {
      const item = detalles.find((d) => String(d.id) === String(detalleId));
      if (item) total += Number(item.precioUnitario) * qty;
    }
    return total;
  }, [productosSeleccionados, detalles]);

  const selectedCount = Object.keys(productosSeleccionados).length;

  /* Validar y enviar */
  const handleSubmit = async () => {
    const errs = {};
    if (!categoriaMotivo) errs.categoriaMotivo = 'Seleccione la categoría del motivo';
    if (!detalleMotivo || detalleMotivo.length < 5) errs.detalleMotivo = 'Describa el motivo (mínimo 5 caracteres)';
    if (selectedCount === 0) errs.productos = 'Seleccione al menos un producto';
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }

    setErrors({});
    const data = {
      pedidoId: selectedPedido.id,
      tipoDevolucion,
      categoriaMotivo,
      detalleMotivo,
      metodoReembolso,
      notas: notas || null,
      productos: Object.entries(productosSeleccionados).map(([detallePedidoId, cantidad]) => ({
        detallePedidoId: Number(detallePedidoId),
        cantidad,
      })),
    };
    await onSubmit(data);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50" onClick={onClose}>
      <div
        className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between p-5 border-b border-gray-200">
          <div>
            <h2 className="text-lg font-bold text-gray-900">Solicitar Devolución</h2>
            <p className="text-xs text-gray-500 mt-0.5">
              {step === 1 ? 'Paso 1: Seleccione el pedido' : 'Paso 2: Complete los datos de la devolución'}
            </p>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-full transition-colors">
            <X size={20} className="text-gray-500" />
          </button>
        </div>

        <div className="p-5">
          {/* ── PASO 1: Seleccionar pedido ── */}
          {step === 1 && (
            <div className="space-y-3">
              {pedidos.length === 0 ? (
                <div className="text-center py-8">
                  <div className="w-14 h-14 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-3">
                    <Package size={24} className="text-gray-400" />
                  </div>
                  <p className="text-sm font-medium text-gray-700">No tiene pedidos entregados</p>
                  <p className="text-xs text-gray-500 mt-1">Solo se pueden devolver pedidos con estado &quot;Entregado&quot;</p>
                </div>
              ) : (
                pedidos.map((pedido) => (
                  <button
                    key={pedido.id}
                    onClick={() => handleSelectPedido(pedido)}
                    className="w-full text-left p-4 bg-gray-50 border border-gray-200 rounded-xl hover:border-amber-400 hover:bg-amber-50/30 transition-all group"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 bg-white border border-gray-200 rounded-lg flex items-center justify-center group-hover:border-amber-300">
                          <Package size={16} className="text-gray-500 group-hover:text-amber-600" />
                        </div>
                        <div>
                          <p className="text-sm font-semibold text-gray-900">
                            {pedido.numeroPedido || `Pedido #${pedido.id}`}
                          </p>
                          <p className="text-xs text-gray-500">
                            {formatDate(pedido.fechaPedido || pedido.creadoEn)}
                            {' · '}
                            {pedido.detalles?.length || 0} producto{(pedido.detalles?.length || 0) !== 1 ? 's' : ''}
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-bold text-gray-900">{formatCurrency(pedido.total)}</p>
                        <p className="text-xs text-green-600 font-medium">Entregado</p>
                      </div>
                    </div>
                    {/* Preview de productos */}
                    {pedido.detalles && pedido.detalles.length > 0 && (
                      <div className="mt-2 pt-2 border-t border-gray-200 flex flex-wrap gap-1">
                        {pedido.detalles.slice(0, 3).map((d, i) => (
                          <span key={i} className="text-[11px] text-gray-500 bg-white px-2 py-0.5 rounded-md border border-gray-200">
                            {d.producto?.nombre || `Producto ${d.productoId}`} ×{Number(d.cantidad)}
                          </span>
                        ))}
                        {pedido.detalles.length > 3 && (
                          <span className="text-[11px] text-gray-400">+{pedido.detalles.length - 3} más</span>
                        )}
                      </div>
                    )}
                  </button>
                ))
              )}
            </div>
          )}

          {/* ── PASO 2: Formulario ── */}
          {step === 2 && selectedPedido && (
            <div className="space-y-5">
              {/* Pedido seleccionado */}
              <div className="flex items-center justify-between p-3 bg-amber-50 border border-amber-200 rounded-xl">
                <div className="flex items-center gap-2">
                  <Package size={16} className="text-amber-600" />
                  <span className="text-sm font-semibold text-amber-800">
                    {selectedPedido.numeroPedido || `Pedido #${selectedPedido.id}`}
                  </span>
                  <span className="text-xs text-amber-600">{formatCurrency(selectedPedido.total)}</span>
                </div>
                <button
                  onClick={() => { setStep(1); setSelectedPedido(null); setProductosSeleccionados({}); }}
                  className="text-xs text-amber-700 hover:text-amber-900 font-medium underline"
                >
                  Cambiar
                </button>
              </div>

              {/* Tipo de devolución */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Tipo de Devolución</label>
                <div className="grid grid-cols-2 gap-3">
                  {[
                    { value: 'parcial', label: 'Parcial', desc: 'Seleccionar productos' },
                    { value: 'total', label: 'Total', desc: 'Todos los productos' },
                  ].map(({ value, label, desc }) => (
                    <button
                      key={value}
                      onClick={() => handleTipoChange(value)}
                      className={`p-3 rounded-xl border-2 text-left transition-all ${
                        tipoDevolucion === value
                          ? 'border-amber-500 bg-amber-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <p className={`text-sm font-semibold ${tipoDevolucion === value ? 'text-amber-700' : 'text-gray-800'}`}>{label}</p>
                      <p className="text-xs text-gray-500 mt-0.5">{desc}</p>
                    </button>
                  ))}
                </div>
              </div>

              {/* Productos a devolver */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Productos a Devolver
                  {selectedCount > 0 && (
                    <span className="text-amber-600 ml-2">({selectedCount} seleccionado{selectedCount !== 1 ? 's' : ''})</span>
                  )}
                </label>
                {errors.productos && <p className="text-xs text-red-500 mb-2">{errors.productos}</p>}
                <div className="space-y-2 max-h-48 overflow-y-auto">
                  {detalles.map((d) => {
                    const isSelected = !!productosSeleccionados[d.id];
                    const maxQty = Number(d.cantidad);
                    const currentQty = productosSeleccionados[d.id] || 0;
                    const nombre = d.producto?.nombre || `Producto ${d.productoId}`;

                    return (
                      <div
                        key={d.id}
                        className={`flex items-center gap-3 p-3 rounded-xl border transition-all ${
                          isSelected ? 'border-amber-400 bg-amber-50/50' : 'border-gray-200 bg-gray-50'
                        }`}
                      >
                        {/* Checkbox */}
                        <button
                          onClick={() => toggleProducto(d.id, maxQty)}
                          disabled={tipoDevolucion === 'total'}
                          className={`w-5 h-5 rounded border-2 flex items-center justify-center shrink-0 transition-colors ${
                            isSelected
                              ? 'bg-amber-500 border-amber-500 text-white'
                              : 'border-gray-300 hover:border-amber-400'
                          } ${tipoDevolucion === 'total' ? 'cursor-not-allowed opacity-60' : 'cursor-pointer'}`}
                        >
                          {isSelected && <Check size={12} />}
                        </button>

                        {/* Producto info */}
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-gray-800 truncate">{nombre}</p>
                          <p className="text-xs text-gray-500">
                            {formatCurrency(d.precioUnitario)} × {maxQty}
                          </p>
                        </div>

                        {/* Cantidad selector */}
                        {isSelected && tipoDevolucion === 'parcial' && (
                          <div className="flex items-center gap-1.5">
                            <button
                              onClick={() => updateCantidad(d.id, currentQty - 1, maxQty)}
                              className="w-7 h-7 rounded-lg border border-gray-300 flex items-center justify-center hover:bg-gray-100 transition-colors"
                            >
                              <Minus size={12} />
                            </button>
                            <span className="w-8 text-center text-sm font-semibold text-gray-800">{currentQty}</span>
                            <button
                              onClick={() => updateCantidad(d.id, currentQty + 1, maxQty)}
                              className="w-7 h-7 rounded-lg border border-gray-300 flex items-center justify-center hover:bg-gray-100 transition-colors"
                            >
                              <Plus size={12} />
                            </button>
                          </div>
                        )}

                        {/* Subtotal */}
                        {isSelected && (
                          <span className="text-sm font-semibold text-gray-900 ml-2 shrink-0">
                            {formatCurrency(Number(d.precioUnitario) * currentQty)}
                          </span>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Categoría motivo */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Categoría del Motivo <span className="text-red-500">*</span>
                </label>
                <select
                  value={categoriaMotivo}
                  onChange={(e) => setCategoriaMotivo(e.target.value)}
                  className={`w-full px-3 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none ${
                    errors.categoriaMotivo ? 'border-red-300' : 'border-gray-300'
                  }`}
                >
                  <option value="">Seleccione un motivo...</option>
                  {CATEGORIA_OPTIONS.map((o) => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                  ))}
                </select>
                {errors.categoriaMotivo && <p className="text-xs text-red-500 mt-1">{errors.categoriaMotivo}</p>}
              </div>

              {/* Detalle motivo */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Describa el Motivo <span className="text-red-500">*</span>
                </label>
                <textarea
                  value={detalleMotivo}
                  onChange={(e) => setDetalleMotivo(e.target.value)}
                  rows={3}
                  maxLength={1000}
                  placeholder="Explique con detalle el motivo de la devolución..."
                  className={`w-full px-3 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none resize-none ${
                    errors.detalleMotivo ? 'border-red-300' : 'border-gray-300'
                  }`}
                />
                <div className="flex justify-between mt-1">
                  {errors.detalleMotivo && <p className="text-xs text-red-500">{errors.detalleMotivo}</p>}
                  <p className="text-xs text-gray-400 ml-auto">{detalleMotivo.length}/1000</p>
                </div>
              </div>

              {/* Método de reembolso */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">Método de Reembolso</label>
                <select
                  value={metodoReembolso}
                  onChange={(e) => setMetodoReembolso(e.target.value)}
                  className="w-full px-3 py-2.5 border border-gray-300 rounded-xl text-sm focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none"
                >
                  {METODO_REEMBOLSO_OPTIONS.map((o) => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                  ))}
                </select>
              </div>

              {/* Notas */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">Notas Adicionales <span className="text-gray-400">(opcional)</span></label>
                <textarea
                  value={notas}
                  onChange={(e) => setNotas(e.target.value)}
                  rows={2}
                  maxLength={1000}
                  placeholder="Información adicional..."
                  className="w-full px-3 py-2.5 border border-gray-300 rounded-xl text-sm focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none resize-none"
                />
              </div>

              {/* Resumen total */}
              {selectedCount > 0 && (
                <div className="p-4 bg-amber-50 border border-amber-200 rounded-xl flex items-center justify-between">
                  <div>
                    <p className="text-xs text-amber-600">Total a Devolver</p>
                    <p className="text-xl font-bold text-amber-800">{formatCurrency(totalDevolucion)}</p>
                  </div>
                  <div className="text-right text-xs text-amber-600">
                    {selectedCount} producto{selectedCount !== 1 ? 's' : ''}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-5 border-t border-gray-200 flex gap-3">
          {step === 2 && (
            <button
              onClick={() => { setStep(1); setSelectedPedido(null); setProductosSeleccionados({}); }}
              className="px-4 py-2.5 bg-gray-100 text-gray-700 rounded-xl text-sm font-semibold hover:bg-gray-200 transition-colors"
            >
              Atrás
            </button>
          )}
          <button
            onClick={onClose}
            className="px-4 py-2.5 bg-gray-100 text-gray-700 rounded-xl text-sm font-semibold hover:bg-gray-200 transition-colors ml-auto"
          >
            Cancelar
          </button>
          {step === 2 && (
            <button
              onClick={handleSubmit}
              disabled={isSubmitting || selectedCount === 0}
              className="px-6 py-2.5 bg-amber-600 text-white rounded-xl text-sm font-semibold hover:bg-amber-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              {isSubmitting ? (
                <>
                  <Loader2 size={16} className="animate-spin" />
                  Enviando...
                </>
              ) : (
                <>
                  <RotateCcw size={16} />
                  Solicitar Devolución
                </>
              )}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
