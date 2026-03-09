/**
 * Modal para verificar el pago de un pedido antes de confirmarlo.
 * Muestra el método de pago, monto, referencia e imagen de comprobante (si aplica).
 * El admin puede aprobar el pago y luego confirmar el pedido.
 */
import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { X, CheckCircle, AlertTriangle, Clock, Image, CreditCard, Banknote, Smartphone, RefreshCw } from 'lucide-react';
import { getPagosPorPedido, aprobarPago } from '../../services/pedidosApi';
import { message } from '@/shared/utils/notifications';

const TIPOS_DIGITALES = ['yape', 'plin', 'billetera_digital', 'qr', 'transferencia_bancaria'];

const icono = (tipo) => {
  if (['yape', 'plin', 'billetera_digital', 'qr'].includes(tipo)) return <Smartphone size={18} className="text-purple-600" />;
  if (tipo === 'efectivo') return <Banknote size={18} className="text-green-600" />;
  return <CreditCard size={18} className="text-blue-600" />;
};

const badgeEstado = (estado) => {
  const cfg = {
    pagado:      'bg-green-100 text-green-700 border-green-200',
    pendiente:   'bg-yellow-100 text-yellow-700 border-yellow-200',
    fallido:     'bg-red-100 text-red-700 border-red-200',
    reembolsado: 'bg-gray-100 text-gray-600 border-gray-200',
  };
  const labels = { pagado: 'Pagado', pendiente: 'Pendiente', fallido: 'Fallido', reembolsado: 'Reembolsado' };
  return (
    <span className={`px-2 py-0.5 rounded border text-xs font-semibold ${cfg[estado] ?? cfg.pendiente}`}>
      {labels[estado] ?? estado}
    </span>
  );
};

export function VerificarPagoModal({ pedido, onClose, onConfirmarPedido }) {
  const queryClient = useQueryClient();
  const [imagenAmpliada, setImagenAmpliada] = useState(null);

  const { data: pagos = [], isLoading, refetch } = useQuery({
    queryKey: ['pagos-pedido', pedido.id],
    queryFn: () => getPagosPorPedido(pedido.id),
    staleTime: 0,
  });

  const { mutateAsync: aprobar, isPending: aprobando } = useMutation({
    mutationFn: aprobarPago,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pagos-pedido', pedido.id] });
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Pago aprobado');
      refetch();
    },
    onError: () => message.error('Error al aprobar el pago'),
  });

  const pagoPrincipal = pagos[0] ?? null;
  const yaAprobado = pagoPrincipal?.estadoPago === 'pagado';
  const rawUrl = pagoPrincipal?.urlComprobante;
  const comprobanteUrl = rawUrl
    ? (rawUrl.startsWith('http://') || rawUrl.startsWith('https://') || rawUrl.startsWith('/'))
      ? rawUrl
      : `/uploads/${rawUrl}`
    : null;
  const tieneComprobante = !!comprobanteUrl;
  const esDigital = TIPOS_DIGITALES.includes(pagoPrincipal?.metodoPagoTipo);

  const handleAprobarYConfirmar = async () => {
    if (!yaAprobado && pagoPrincipal) {
      await aprobar(pagoPrincipal.id);
    }
    onConfirmarPedido();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-lg">
        {/* Header */}
        <div className="flex items-center justify-between px-5 py-4 border-b">
          <div>
            <h2 className="text-base font-semibold text-gray-800">Verificar Pago</h2>
            <p className="text-xs text-gray-500 mt-0.5">
              Pedido <span className="font-medium text-blue-600">#{pedido.numeroPedido || pedido.id}</span>
              {' — '}
              <span className="font-medium">
                {pedido.cliente?.nombres || pedido.clienteNombre} {pedido.cliente?.apellidos || pedido.clienteApellido}
              </span>
            </p>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-md hover:bg-gray-100 text-gray-500">
            <X size={18} />
          </button>
        </div>

        {/* Cuerpo */}
        <div className="px-5 py-4 space-y-4">
          {isLoading ? (
            <div className="flex items-center justify-center py-8 text-gray-400">
              <RefreshCw size={20} className="animate-spin mr-2" />
              Cargando información de pago...
            </div>
          ) : pagos.length === 0 ? (
            /* Sin registro de pago */
            <div className="flex flex-col items-center py-6 text-center">
              <div className="p-3 bg-yellow-100 rounded-full mb-3">
                <AlertTriangle size={24} className="text-yellow-600" />
              </div>
              <p className="font-medium text-gray-700">Sin registro de pago</p>
              <p className="text-sm text-gray-500 mt-1">
                Este pedido no tiene un pago registrado todavía.<br />
                Puedes confirmar el pedido de todas formas si el pago fue verificado por otro medio.
              </p>
            </div>
          ) : (
            /* Información del pago */
            <div className="space-y-3">
              {/* Método y estado */}
              <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border">
                <div className="flex items-center gap-2">
                  {icono(pagoPrincipal.metodoPagoTipo)}
                  <div>
                    <p className="text-sm font-medium text-gray-800">
                      {pagoPrincipal.metodoPagoNombre || pagoPrincipal.metodoPagoTipo || 'Desconocido'}
                    </p>
                    {pagoPrincipal.numeroReferencia && (
                      <p className="text-xs text-gray-500">Ref: {pagoPrincipal.numeroReferencia}</p>
                    )}
                  </div>
                </div>
                {badgeEstado(pagoPrincipal.estadoPago)}
              </div>

              {/* Monto */}
              <div className="flex justify-between items-center px-1">
                <span className="text-sm text-gray-500">Monto registrado</span>
                <span className="text-lg font-bold text-green-700">
                  S/ {Number(pagoPrincipal.monto || 0).toFixed(2)}
                </span>
              </div>

              {/* Total del pedido vs monto pagado */}
              {Number(pagoPrincipal.monto) < Number(pedido.total) && (
                <div className="flex items-center gap-2 p-2 bg-yellow-50 border border-yellow-200 rounded-lg text-xs text-yellow-700">
                  <AlertTriangle size={14} />
                  El monto pagado (S/ {Number(pagoPrincipal.monto).toFixed(2)}) es menor al total del pedido (S/ {Number(pedido.total).toFixed(2)})
                </div>
              )}

              {/* Comprobante imagen (Yape, Plin, transferencia, etc.) */}
              {esDigital && (
                <div>
                  <p className="text-xs font-medium text-gray-500 mb-2 uppercase tracking-wide">
                    Comprobante de pago
                  </p>
                  {tieneComprobante ? (
                    <div
                      className="relative cursor-pointer rounded-lg overflow-hidden border border-gray-200 bg-gray-50"
                      onClick={() => setImagenAmpliada(comprobanteUrl)}
                    >
                      <img
                        src={comprobanteUrl}
                        alt="Comprobante de pago"
                        className="w-full max-h-52 object-contain"
                      />
                      <div className="absolute inset-0 bg-black/0 hover:bg-black/10 transition-colors flex items-center justify-center">
                        <span className="opacity-0 hover:opacity-100 text-white text-xs font-medium bg-black/50 px-2 py-1 rounded">
                          Ver ampliado
                        </span>
                      </div>
                    </div>
                  ) : (
                    <div className="flex items-center gap-2 p-3 bg-gray-50 border border-dashed border-gray-300 rounded-lg text-sm text-gray-500">
                      <Image size={16} />
                      El cliente aún no ha subido el comprobante
                    </div>
                  )}
                </div>
              )}

              {/* Fecha de pago */}
              {pagoPrincipal.fechaPago && (
                <div className="flex items-center gap-2 text-xs text-gray-500">
                  <Clock size={12} />
                  Pago registrado el {new Date(pagoPrincipal.fechaPago).toLocaleString('es-PE')}
                </div>
              )}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between px-5 py-4 border-t bg-gray-50 rounded-b-xl">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors"
          >
            Cancelar
          </button>
          <div className="flex gap-2">
            {/* Si hay pago pero no está aprobado, mostrar botón solo de aprobar */}
            {pagoPrincipal && !yaAprobado && (
              <button
                onClick={() => aprobar(pagoPrincipal.id)}
                disabled={aprobando}
                className="px-4 py-2 text-sm text-purple-700 border border-purple-300 bg-purple-50 rounded-lg hover:bg-purple-100 transition-colors disabled:opacity-50"
              >
                {aprobando ? 'Aprobando...' : 'Solo aprobar pago'}
              </button>
            )}
            {/* Botón principal: aprobar + confirmar pedido */}
            <button
              onClick={handleAprobarYConfirmar}
              disabled={aprobando}
              className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50"
            >
              <CheckCircle size={16} />
              {yaAprobado || pagos.length === 0
                ? 'Confirmar Pedido'
                : 'Aprobar pago y Confirmar Pedido'}
            </button>
          </div>
        </div>
      </div>

      {/* Imagen ampliada */}
      {imagenAmpliada && (
        <div
          className="fixed inset-0 z-60 flex items-center justify-center bg-black/80 p-4"
          onClick={() => setImagenAmpliada(null)}
        >
          <img
            src={imagenAmpliada}
            alt="Comprobante ampliado"
            className="max-w-full max-h-full rounded-lg shadow-2xl"
          />
        </div>
      )}
    </div>
  );
}
