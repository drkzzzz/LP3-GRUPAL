import { useEffect, useState, useRef } from 'react';
import { useOutletContext, useNavigate, Link } from 'react-router-dom';
import {
  Package, Clock, CheckCircle, XCircle, Truck, Store,
  ShoppingBag, Loader2, Receipt, Upload, Image, X, MapPin,
  AlertTriangle, ChevronRight, Download,
} from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useStorefrontAuthStore } from '../stores/storefrontAuthStore';
import { storefrontService } from '../services/storefrontService';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import toast from 'react-hot-toast';

/* â”€â”€â”€ Config de estados â”€â”€â”€ */
const STATUS_CONFIG = {
  pendiente:  { label: 'Pendiente',    icon: Clock,         color: 'bg-yellow-100 text-yellow-700 border-yellow-200' },
  confirmado: { label: 'Confirmado',   icon: CheckCircle,   color: 'bg-blue-100 text-blue-700 border-blue-200' },
  preparando: { label: 'Preparando',   icon: Package,       color: 'bg-purple-100 text-purple-700 border-purple-200' },
  listo:      { label: 'Listo',        icon: Package,       color: 'bg-teal-100 text-teal-700 border-teal-200' },
  en_camino:  { label: 'En Camino',    icon: Truck,         color: 'bg-indigo-100 text-indigo-700 border-indigo-200' },
  entregado:  { label: 'Entregado',    icon: CheckCircle,   color: 'bg-green-100 text-green-700 border-green-200' },
  cancelado:  { label: 'Cancelado',    icon: XCircle,       color: 'bg-red-100 text-red-700 border-red-200' },
};

const TIMELINE_STEPS = ['pendiente', 'confirmado', 'preparando', 'listo', 'en_camino', 'entregado'];

const METODOS_DIGITALES = ['yape', 'plin'];
const esMetodoDigital = (nombre) => {
  if (!nombre) return false;
  const lower = nombre.toLowerCase();
  return METODOS_DIGITALES.some((m) => lower.includes(m));
};

const getComprobanteUrl = (path) => {
  if (!path) return null;
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('/')) return path;
  return `/uploads/${path}`;
};

/* â”€â”€â”€ Badge de estado â”€â”€â”€ */
const StatusBadge = ({ status }) => {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.pendiente;
  const Icon = cfg.icon;
  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold border ${cfg.color}`}>
      <Icon size={12} />
      {cfg.label}
    </span>
  );
};

/* â”€â”€â”€ Timeline â”€â”€â”€ */
const StatusTimeline = ({ estadoActual }) => {
  if (estadoActual === 'cancelado') {
    return (
      <div className="flex items-center gap-2 p-3 bg-red-50 border border-red-200 rounded-xl">
        <XCircle size={18} className="text-red-500 shrink-0" />
        <span className="text-sm font-medium text-red-700">Pedido cancelado</span>
      </div>
    );
  }

  const currentIdx = TIMELINE_STEPS.indexOf(estadoActual);

  return (
    <div className="relative flex items-start justify-between gap-0 overflow-x-auto pb-1">
      {TIMELINE_STEPS.map((step, idx) => {
        const cfg = STATUS_CONFIG[step];
        const done = idx < currentIdx;
        const active = idx === currentIdx;
        const Icon = cfg.icon;

        return (
          <div key={step} className="flex flex-col items-center flex-1 min-w-0 relative">
            {idx > 0 && (
              <div
                className={`absolute top-3.5 h-0.5 ${done || active ? 'bg-amber-400' : 'bg-gray-200'}`}
                style={{ left: 'calc(-50% + 14px)', right: 'calc(50% + 14px)' }}
              />
            )}
            <div
              className={`relative z-10 w-7 h-7 rounded-full flex items-center justify-center border-2 transition-all ${
                done
                  ? 'bg-amber-500 border-amber-500'
                  : active
                  ? 'bg-amber-50 border-amber-500 ring-2 ring-amber-200'
                  : 'bg-white border-gray-300'
              }`}
            >
              {done ? (
                <CheckCircle size={13} className="text-white" />
              ) : (
                <Icon size={11} className={active ? 'text-amber-600' : 'text-gray-400'} />
              )}
            </div>
            <span
              className={`mt-1.5 text-center leading-tight ${
                done || active ? 'text-gray-700 font-medium' : 'text-gray-400'
              }`}
              style={{ fontSize: '10px', maxWidth: '50px' }}
            >
              {cfg.label}
            </span>
          </div>
        );
      })}
    </div>
  );
};

/* â”€â”€â”€ Modal de detalle â”€â”€â”€ */
const PedidoDetalleModal = ({ pedido, slug, config, onClose }) => {
  const printRef = useRef(null);

  const handlePrint = () => {
    const negocio = config?.negocio || {};
    const nombreNegocio = negocio.nombreComercial || negocio.razonSocial || 'Mi Negocio';
    const rucNegocio = negocio.ruc || '';
    const telefonoNegocio = negocio.telefono || '';
    const direccionNegocio = [negocio.direccion, negocio.ciudad, negocio.departamento].filter(Boolean).join(', ');
    const tipoLabel = pedido.tipoComprobante === 'factura' ? 'FACTURA ELECTRÓNICA' : 'BOLETA DE VENTA ELECTRÓNICA';
    const esNotaVenta = !pedido.tipoComprobante || pedido.tipoComprobante === 'nota_venta';
    const numeroDoc = pedido.numeroComprobante || pedido.numeroPedido || String(pedido.id);
    const clienteNombre = pedido.docClienteNombre || pedido.clienteNombre || [pedido.cliente?.nombres, pedido.cliente?.apellidos].filter(Boolean).join(' ') || '—';
    const clienteDoc = pedido.docClienteNumero || pedido.clienteDocumento || '—';
    const metodoPago = pedido.metodoPago || '—';
    const costoDelivery = parseFloat(pedido.costoDelivery || 0);
    const subtotal = parseFloat(pedido.subtotal || 0);
    const total = parseFloat(pedido.total || 0);
    const fecha = pedido.fechaVenta || pedido.creadoEn || '';
    const fechaStr = fecha ? new Date(fecha).toLocaleDateString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric' }) : '—';
    const horaStr = fecha ? new Date(fecha).toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }) : '';
    const detalles = pedido.detalles || [];

    // Detalle de pago adicional desde pago consultado (banco, últimos 4 dígitos, titular)
    const pagoDetalle = (() => {
      if (!pago) return '';
      const partes = [];
      if (pago.banco) partes.push(pago.banco);
      if (pago.ultimosCuatroDigitos) partes.push(`****${pago.ultimosCuatroDigitos}`);
      if (pago.nombreTitular) partes.push(pago.nombreTitular);
      if (pago.numeroReferencia && !pago.ultimosCuatroDigitos) partes.push(`Op. ${pago.numeroReferencia}`);
      return partes.join(' · ');
    })();

    const itemsHtml = detalles.map((d) => {
      const nombre = d.producto?.nombre || d.nombreProducto || `Producto ${d.productoId}`;
      const precioUnit = parseFloat(d.precioUnitario || 0).toFixed(2);
      const subtotalItem = parseFloat(d.subtotal || d.precioUnitario * d.cantidad || 0).toFixed(2);
      return `<tr>
        <td style="padding:6px 10px;font-size:11px;border-bottom:1px solid #eee;color:#444">${d.cantidad}</td>
        <td style="padding:6px 10px;font-size:11px;border-bottom:1px solid #eee;color:#444">${nombre}</td>
        <td style="padding:6px 10px;font-size:11px;border-bottom:1px solid #eee;color:#444;text-align:right">S/ ${precioUnit}</td>
        <td style="padding:6px 10px;font-size:11px;border-bottom:1px solid #eee;color:#444;text-align:right">S/ ${subtotalItem}</td>
      </tr>`;
    }).join('');

    const html = `<!DOCTYPE html><html><head><meta charset="UTF-8"><title>${tipoLabel} ${numeroDoc}</title>
      <style>
        @page { size: A4; margin: 15mm 20mm; }
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Arial, sans-serif; color: #1a1a1a; font-size: 12px; line-height: 1.5; }
      </style>
      </head><body><div style="max-width:700px;margin:0 auto;padding:20px 0">

        <!-- Header: Emisor + Tipo -->
        <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:16px;padding-bottom:12px;border-bottom:2px solid #d97706">
          <div style="flex:1">
            <div style="font-size:18px;font-weight:700;color:#1a1a1a;margin-bottom:4px">${nombreNegocio}</div>
            <div style="font-size:11px;color:#555;line-height:1.7">
              ${direccionNegocio ? `${direccionNegocio}<br>` : ''}
              ${telefonoNegocio ? `Teléfono: ${telefonoNegocio}<br>` : ''}
              ${rucNegocio ? `RUC: ${rucNegocio}` : ''}
            </div>
          </div>
          <div style="text-align:right;border:2px solid #d97706;padding:10px 16px;border-radius:4px;min-width:220px">
            <div style="font-size:12px;font-weight:700;color:#d97706">${tipoLabel}</div>
            <div style="font-size:13px;font-weight:600;color:#333;margin-top:2px">${numeroDoc}</div>
          </div>
        </div>

        <!-- Datos cliente -->
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:4px 24px;font-size:11px;margin-bottom:12px">
          <div><span style="font-weight:600;color:#333">Cliente: </span><span style="color:#555">${clienteNombre}</span></div>
          <div><span style="font-weight:600;color:#333">Fecha: </span><span style="color:#555">${fechaStr}${horaStr ? ` ${horaStr}` : ''}</span></div>
          <div><span style="font-weight:600;color:#333">Documento: </span><span style="color:#555">${clienteDoc}</span></div>
          <div><span style="font-weight:600;color:#333">Método de Pago: </span><span style="color:#555">${metodoPago}</span></div>
          ${pagoDetalle ? `<div><span style="font-weight:600;color:#333">Detalle de Pago: </span><span style="color:#555">${pagoDetalle}</span></div>` : ''}
        </div>

        <!-- Tabla de ítems -->
        <table style="width:100%;border-collapse:collapse;margin:12px 0">
          <thead>
            <tr>
              <th style="background:#f9fafb;border-top:1px solid #ddd;border-bottom:1px solid #ddd;padding:8px 10px;text-align:left;font-size:11px;font-weight:600;color:#333;width:50px">Cant.</th>
              <th style="background:#f9fafb;border-top:1px solid #ddd;border-bottom:1px solid #ddd;padding:8px 10px;text-align:left;font-size:11px;font-weight:600;color:#333">Descripción</th>
              <th style="background:#f9fafb;border-top:1px solid #ddd;border-bottom:1px solid #ddd;padding:8px 10px;text-align:right;font-size:11px;font-weight:600;color:#333;width:90px">P. Unit.</th>
              <th style="background:#f9fafb;border-top:1px solid #ddd;border-bottom:1px solid #ddd;padding:8px 10px;text-align:right;font-size:11px;font-weight:600;color:#333;width:90px">Total</th>
            </tr>
          </thead>
          <tbody>${itemsHtml}</tbody>
        </table>

        <!-- Totales -->
        <div style="display:flex;justify-content:flex-end;margin-top:4px">
          <div style="width:240px">
            <div style="display:flex;justify-content:space-between;padding:3px 0;font-size:11px;color:#555">
              <span>Subtotal:</span><span>S/ ${subtotal.toFixed(2)}</span>
            </div>
            ${costoDelivery > 0 ? `<div style="display:flex;justify-content:space-between;padding:3px 0;font-size:11px;color:#555"><span>Delivery:</span><span>S/ ${costoDelivery.toFixed(2)}</span></div>` : ''}
            <div style="display:flex;justify-content:space-between;padding:6px 0 3px;font-size:13px;font-weight:700;color:#1a1a1a;border-top:2px solid #333">
              <span>TOTAL:</span><span>S/ ${total.toFixed(2)}</span>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div style="margin-top:24px;text-align:center;border-top:1px dashed #ccc;padding-top:12px">
          <p style="font-size:11px;color:#666">¡Gracias por su preferencia!</p>
          ${!esNotaVenta
            ? `<p style="font-weight:600;font-size:10px;color:#888;margin-top:4px">Este comprobante es válido como documento tributario</p>`
            : `<p style="font-weight:600;font-size:10px;color:#888;margin-top:4px">Documento interno — No es comprobante tributario</p>`
          }
        </div>

      </div></body></html>`;

    const win = window.open('', '_blank', 'width=800,height=600');
    win.document.write(html);
    win.document.close();
    win.focus();
    setTimeout(() => { win.print(); win.close(); }, 250);
  };
  const queryClient = useQueryClient();
  const [imagenAmpliada, setImagenAmpliada] = useState(null);
  const [archivoSeleccionado, setArchivoSeleccionado] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);

  const { data: pago, isLoading: loadingPago, refetch: refetchPago } = useQuery({
    queryKey: ['storefront-pago-pedido', pedido.id],
    queryFn: () => storefrontService.getPedidoPago(slug, pedido.id),
    staleTime: 0,
    enabled: esMetodoDigital(pedido.metodoPago),
  });

  const { mutateAsync: subirImg, isPending: subiendo } = useMutation({
    mutationFn: () => storefrontService.subirComprobante(slug, pedido.id, archivoSeleccionado),
    onSuccess: () => {
      toast.success('Â¡Comprobante enviado!');
      setArchivoSeleccionado(null);
      setPreviewUrl(null);
      refetchPago();
      queryClient.invalidateQueries({ queryKey: ['storefront-mis-pedidos', slug] });
    },
    onError: (err) => {
      toast.error(err.response?.data?.message || 'Error al subir el comprobante');
    },
  });

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    if (file.size > 5 * 1024 * 1024) {
      toast.error('La imagen no puede superar 5 MB');
      return;
    }
    setArchivoSeleccionado(file);
    setPreviewUrl(URL.createObjectURL(file));
  };

  const esDigital = esMetodoDigital(pedido.metodoPago);
  const comprobanteUrl = pago ? getComprobanteUrl(pago.urlComprobante) : null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 overflow-y-auto">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg my-4">
        {/* Header */}
        <div className="flex items-start justify-between p-5 border-b">
          <div>
            <h2 className="text-lg font-bold text-gray-900">
              Pedido #{pedido.numeroPedido || pedido.id}
            </h2>
            <p className="text-sm text-gray-500 mt-0.5">
              {pedido.creadoEn ? formatDateTime(pedido.creadoEn) : 'â€”'}
            </p>
          </div>
          <div className="flex items-center gap-3">
            <StatusBadge status={pedido.estadoPedido} />
            <button
              onClick={onClose}
              className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-gray-600"
            >
              <X size={20} />
            </button>
          </div>
        </div>

        {/* Cuerpo */}
        <div className="p-5 space-y-5 max-h-[70vh] overflow-y-auto">
          {/* Timeline */}
          <div>
            <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Estado del pedido
            </h3>
            <StatusTimeline estadoActual={pedido.estadoPedido} />
          </div>

          {/* Tipo de entrega */}
          <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-xl border border-gray-100">
            {pedido.tipoPedido === 'delivery' ? (
              <Truck size={20} className="text-blue-500 shrink-0" />
            ) : (
              <Store size={20} className="text-amber-500 shrink-0" />
            )}
            <div className="min-w-0">
              <p className="text-sm font-semibold text-gray-800">
                {pedido.tipoPedido === 'delivery' ? 'Delivery' : 'Recojo en tienda'}
              </p>
              {pedido.direccionEntrega && (
                <p className="text-xs text-gray-500 truncate mt-0.5">
                  <MapPin size={10} className="inline mr-1" />
                  {pedido.direccionEntrega}
                </p>
              )}
            </div>
          </div>

          {/* Productos */}
          <div>
            <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Productos</h3>
            <div className="divide-y divide-gray-100">
              {(pedido.detalles || []).map((d, i) => (
                <div key={i} className="flex justify-between items-center py-2.5">
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-800 truncate">
                      {d.producto?.nombre || d.nombreProducto || `Producto ${d.productoId}`}
                    </p>
                    <p className="text-xs text-gray-400">
                      {formatCurrency(d.precioUnitario)} Ã— {d.cantidad}
                    </p>
                  </div>
                  <p className="font-semibold text-gray-900 ml-3 shrink-0 text-sm">
                    {formatCurrency(d.subtotal)}
                  </p>
                </div>
              ))}
            </div>

            {/* Totales */}
            <div className="mt-3 pt-3 border-t space-y-1.5 text-sm">
              {pedido.costoDelivery > 0 && (
                <div className="flex justify-between text-gray-500">
                  <span>Costo de delivery</span>
                  <span>{formatCurrency(pedido.costoDelivery)}</span>
                </div>
              )}
              <div className="flex justify-between font-bold text-gray-900 text-base">
                <span>Total</span>
                <span>{formatCurrency(pedido.total)}</span>
              </div>
            </div>
          </div>

          {/* MÃ©todo de pago */}
          {pedido.metodoPago && (
            <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl border border-gray-100 text-sm">
              <span className="text-gray-500">MÃ©todo de pago</span>
              <span className="font-semibold text-gray-800">{pedido.metodoPago}</span>
            </div>
          )}

          {/* Comprobante Yape/Plin */}
          {esDigital && (
            <div>
              <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">
                Comprobante de Pago
              </h3>
              {loadingPago ? (
                <div className="flex items-center gap-2 text-sm text-gray-400 py-2">
                  <Loader2 size={14} className="animate-spin" /> Cargandoâ€¦
                </div>
              ) : comprobanteUrl ? (
                <div>
                  <div
                    className="rounded-xl overflow-hidden border border-gray-200 cursor-pointer"
                    onClick={() => setImagenAmpliada(comprobanteUrl)}
                  >
                    <img
                      src={comprobanteUrl}
                      alt="Comprobante"
                      className="w-full max-h-52 object-contain bg-gray-50"
                    />
                  </div>
                  <p className="text-xs text-gray-400 mt-1.5 text-center">
                    Toca para ampliar Â· Comprobante enviado âœ“
                  </p>
                </div>
              ) : (
                <div>
                  <div className="p-3 bg-amber-50 border border-amber-200 rounded-xl mb-3">
                    <div className="flex items-start gap-2">
                      <AlertTriangle size={16} className="text-amber-600 shrink-0 mt-0.5" />
                      <div className="text-sm">
                        <p className="font-medium text-amber-800">
                          Pagaste con {pedido.metodoPago}
                        </p>
                        <p className="text-amber-700 mt-0.5 text-xs">
                          Sube la captura de tu {pedido.metodoPago} para que podamos verificar tu pago y confirmar tu pedido mÃ¡s rÃ¡pido.
                        </p>
                      </div>
                    </div>
                  </div>

                  {previewUrl && (
                    <div className="rounded-xl overflow-hidden border border-gray-200 mb-3">
                      <img src={previewUrl} alt="Preview" className="w-full max-h-44 object-contain bg-gray-50" />
                    </div>
                  )}

                  <div className="flex gap-2">
                    <label className="flex-1 cursor-pointer">
                      <div className="flex items-center justify-center gap-2 px-4 py-2.5 border-2 border-dashed border-amber-400 rounded-xl text-amber-600 hover:bg-amber-50 transition-colors text-sm font-medium">
                        <Image size={16} />
                        {archivoSeleccionado ? 'Cambiar imagen' : 'Seleccionar captura'}
                      </div>
                      <input
                        type="file"
                        accept="image/jpeg,image/png,image/webp"
                        className="sr-only"
                        onChange={handleFileChange}
                      />
                    </label>
                    {archivoSeleccionado && (
                      <button
                        onClick={() => subirImg()}
                        disabled={subiendo}
                        className="px-4 py-2.5 bg-amber-500 hover:bg-amber-600 disabled:bg-amber-300 text-white rounded-xl font-medium text-sm flex items-center gap-2 transition-colors"
                      >
                        {subiendo ? (
                          <><Loader2 size={14} className="animate-spin" />Enviandoâ€¦</>
                        ) : (
                          <><Upload size={14} />Enviar</>
                        )}
                      </button>
                    )}
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Observaciones */}
          {pedido.observaciones && (
            <div>
              <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
                Observaciones
              </h3>
              <p className="text-sm text-gray-600 bg-gray-50 rounded-lg px-3 py-2">
                {pedido.observaciones}
              </p>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-5 py-4 border-t flex gap-3">
          {pedido.estadoPedido === 'entregado' && (
            <button
              onClick={handlePrint}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 text-sm font-medium text-white bg-green-600 hover:bg-green-700 rounded-xl transition-colors"
            >
              <Download size={15} />
              Descargar comprobante
            </button>
          )}
          <button
            onClick={onClose}
            className="flex-1 py-2.5 text-sm font-medium text-gray-600 hover:text-gray-800 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors"
          >
            Cerrar
          </button>
        </div>
      </div>

      {/* Imagen ampliada */}
      {imagenAmpliada && (
        <div
          className="fixed inset-0 z-60 flex items-center justify-center bg-black/90 p-4"
          onClick={() => setImagenAmpliada(null)}
        >
          <img
            src={imagenAmpliada}
            alt="Comprobante ampliado"
            className="max-w-full max-h-[90vh] object-contain rounded-lg"
          />
          <button
            className="absolute top-4 right-4 p-2 bg-white/20 rounded-full text-white hover:bg-white/30"
            onClick={() => setImagenAmpliada(null)}
          >
            <X size={24} />
          </button>
        </div>
      )}
    </div>
  );
};

/* â”€â”€â”€ PÃ¡gina principal â”€â”€â”€ */
export const StorefrontOrders = () => {
  const { slug, config } = useOutletContext();
  const navigate = useNavigate();
  const isAuthenticated = useStorefrontAuthStore((s) => s.isAuthenticated());
  const [selectedPedido, setSelectedPedido] = useState(null);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate(`/tienda/${slug}/login?redirect=mis-pedidos`, { replace: true });
    }
  }, [isAuthenticated, slug, navigate]);

  const { data: pedidos = [], isLoading } = useQuery({
    queryKey: ['storefront-mis-pedidos', slug],
    queryFn: () => storefrontService.getMisPedidos(slug),
    enabled: !!slug && isAuthenticated,
  });

  if (!isAuthenticated) return null;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Mis Pedidos</h1>

      {isLoading ? (
        <div className="text-center py-16">
          <Loader2 size={32} className="animate-spin text-amber-500 mx-auto" />
          <p className="text-gray-500 text-sm mt-3">Cargando pedidos...</p>
        </div>
      ) : pedidos.length === 0 ? (
        <div className="text-center py-16">
          <ShoppingBag size={48} className="mx-auto mb-4 text-gray-300" />
          <h2 className="text-lg font-bold text-gray-900 mb-2">Sin pedidos</h2>
          <p className="text-gray-500 mb-6">Comienza explorando nuestro catalogo</p>
          <Link
            to={`/tienda/${slug}/catalogo`}
            className="inline-flex items-center gap-2 px-6 py-3 bg-amber-500 hover:bg-amber-600 text-white font-semibold rounded-xl transition-colors"
          >
            Ver Catalogo
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {pedidos.map((pedido) => (
            <button
              key={pedido.id}
              onClick={() => setSelectedPedido(pedido)}
              className="w-full text-left bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md hover:border-amber-200 transition-all group"
            >
              <div className="flex items-start justify-between mb-3">
                <div>
                  <p className="font-bold text-gray-900 group-hover:text-amber-700 transition-colors">
                    Pedido #{pedido.numeroPedido || pedido.id}
                  </p>
                  <p className="text-xs text-gray-400 mt-0.5">
                    {pedido.creadoEn ? formatDateTime(pedido.creadoEn) : 'â€”'}
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  <StatusBadge status={pedido.estadoPedido} />
                  <ChevronRight size={16} className="text-gray-300 group-hover:text-amber-400 transition-colors" />
                </div>
              </div>

              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-3 text-gray-500">
                  <span className="inline-flex items-center gap-1">
                    {pedido.tipoPedido === 'delivery' ? (
                      <><Truck size={13} /> Delivery</>
                    ) : (
                      <><Store size={13} /> Recojo</>
                    )}
                  </span>
                  {pedido.detalles && (
                    <span>
                      {pedido.detalles.length} producto{pedido.detalles.length !== 1 ? 's' : ''}
                    </span>
                  )}
                  {pedido.metodoPago && esMetodoDigital(pedido.metodoPago) && (
                    <span className="inline-flex items-center gap-1 px-1.5 py-0.5 bg-purple-50 text-purple-600 rounded-full text-xs">
                      {pedido.metodoPago}
                    </span>
                  )}
                </div>
                <span className="font-bold text-gray-900">
                  {formatCurrency(pedido.total)}
                </span>
              </div>

              {/* Vista previa de productos */}
              {pedido.detalles && pedido.detalles.length > 0 && (
                <div className="mt-3 pt-3 border-t border-gray-100 flex flex-wrap gap-1.5">
                  {pedido.detalles.slice(0, 3).map((d, i) => (
                    <span key={i} className="text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded-md">
                      {d.producto?.nombre || d.nombreProducto || `Producto ${d.productoId}`} Ã—{d.cantidad}
                    </span>
                  ))}
                  {pedido.detalles.length > 3 && (
                    <span className="text-xs text-gray-400">+{pedido.detalles.length - 3} mÃ¡s</span>
                  )}
                </div>
              )}
            </button>
          ))}
        </div>
      )}

      {selectedPedido && (
        <PedidoDetalleModal
          pedido={selectedPedido}
          slug={slug}
          config={config}
          onClose={() => setSelectedPedido(null)}
        />
      )}
    </div>
  );
};

