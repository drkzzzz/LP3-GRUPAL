/**
 * Modal de detalle del pedido
 * Muestra información completa, timeline de estados, productos y seguimiento
 */
import { useMemo } from 'react';
import { X, Package, User, MapPin, Clock, Truck, Phone, ShoppingCart, CheckCircle2, CreditCard, MessageSquare } from 'lucide-react';
import {
  ESTADOS_PEDIDO,
  getConfigEstado,
  formatearFechaPedido,
} from '../../utils/estadosPedido';

export const DetallePedidoModal = ({ pedido, detalles = [], onClose, onAvanzarEstado }) => {
  if (!pedido) return null;

  const configEstado = getConfigEstado(pedido.estado);

  // Timeline de estados
  const estadosTimeline = [
    { key: ESTADOS_PEDIDO.PENDIENTE, label: 'Pendiente', icon: Clock },
    { key: ESTADOS_PEDIDO.CONFIRMADO, label: 'Confirmado', icon: CheckCircle2 },
    { key: ESTADOS_PEDIDO.PREPARANDO, label: 'Preparando', icon: Package },
    { key: ESTADOS_PEDIDO.LISTO, label: 'Listo', icon: ShoppingCart },
    { key: ESTADOS_PEDIDO.EN_CAMINO, label: 'En Camino', icon: Truck },
    { key: ESTADOS_PEDIDO.ENTREGADO, label: 'Entregado', icon: CheckCircle2 },
  ];

  // Calcular índice del estado actual
  const estadoActualIndex = useMemo(() => {
    if (pedido.estado === ESTADOS_PEDIDO.CANCELADO) return -1;
    return estadosTimeline.findIndex((e) => e.key === pedido.estado);
  }, [pedido.estado]);

  // Calcular subtotal de detalles
  const subtotal = useMemo(() => {
    return detalles.reduce((sum, detalle) => sum + (detalle.subtotal || 0), 0);
  }, [detalles]);

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b bg-gradient-to-r from-green-50 to-emerald-50">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <Package className="text-green-600" size={24} />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">
                Pedido #{pedido.numeroPedido || pedido.id}
              </h2>
              <p className="text-sm text-gray-600">
                {formatearFechaPedido(pedido.creado_en || pedido.fechaPedido)}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-white/50 rounded-lg transition-colors"
          >
            <X size={20} className="text-gray-500" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {/* Timeline de Estados */}
          {pedido.estado !== ESTADOS_PEDIDO.CANCELADO ? (
            <div className="bg-gray-50 rounded-lg p-4">
              <h3 className="text-sm font-semibold text-gray-700 mb-4">Progreso del Pedido</h3>
              <div className="relative">
                {/* Línea de progreso */}
                <div className="absolute top-5 left-0 right-0 h-1 bg-gray-200" />
                <div
                  className="absolute top-5 left-0 h-1 bg-green-500 transition-all duration-500"
                  style={{
                    width: `${(estadoActualIndex / (estadosTimeline.length - 1)) * 100}%`,
                  }}
                />

                {/* Estados */}
                <div className="relative flex justify-between">
                  {estadosTimeline.map((estado, index) => {
                    const isCompleted = index <= estadoActualIndex;
                    const isCurrent = index === estadoActualIndex;
                    const IconComponent = estado.icon;

                    return (
                      <div key={estado.key} className="flex flex-col items-center">
                        <div
                          className={`w-10 h-10 rounded-full flex items-center justify-center transition-all duration-300 ${
                            isCompleted
                              ? 'bg-green-500 text-white shadow-lg'
                              : 'bg-white border-2 border-gray-300 text-gray-400'
                          } ${isCurrent ? 'ring-4 ring-green-200 scale-110' : ''}`}
                        >
                          <IconComponent size={18} />
                        </div>
                        <span
                          className={`text-xs mt-2 font-medium text-center ${
                            isCompleted ? 'text-green-700' : 'text-gray-400'
                          }`}
                        >
                          {estado.label}
                        </span>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          ) : (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-center gap-3">
              <div className="p-2 bg-red-100 rounded-full">
                <X className="text-red-600" size={20} />
              </div>
              <div>
                <p className="font-semibold text-red-800">Pedido Cancelado</p>
                <p className="text-sm text-red-600">Este pedido fue cancelado</p>
              </div>
            </div>
          )}

          {/* Información del Pedido */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Cliente */}
            <div className="bg-white border border-gray-200 rounded-lg p-4">
              <div className="flex items-center gap-2 mb-3">
                <User size={18} className="text-gray-500" />
                <h3 className="font-semibold text-gray-700">Cliente</h3>
              </div>
              <p className="text-gray-900 font-medium">
                {pedido.cliente?.nombres || pedido.clienteNombre || 'N/A'}{' '}
                {pedido.cliente?.apellidos || pedido.clienteApellido || ''}
              </p>
              {(pedido.cliente?.telefono || pedido.clienteTelefono) && (
                <div className="flex items-center gap-2 mt-2 text-sm text-gray-600">
                  <Phone size={14} />
                  <span>{pedido.cliente?.telefono || pedido.clienteTelefono}</span>
                </div>
              )}
            </div>

            {/* Tipo de Pedido */}
            <div className="bg-white border border-gray-200 rounded-lg p-4">
              <div className="flex items-center gap-2 mb-3">
                <Truck size={18} className="text-gray-500" />
                <h3 className="font-semibold text-gray-700">Tipo de Entrega</h3>
              </div>
              <div className="flex items-center gap-2">
                {pedido.tipoPedido === 'delivery' ? (
                  <>
                    <Truck size={16} className="text-blue-500" />
                    <span className="text-gray-900 font-medium">Delivery</span>
                  </>
                ) : (
                  <>
                    <ShoppingCart size={16} className="text-green-500" />
                    <span className="text-gray-900 font-medium">Recojo en Tienda</span>
                  </>
                )}
              </div>
              <p className="text-xs text-gray-500 mt-1">
                Origen: {pedido.origenPedido || 'tienda_online'}
              </p>
            </div>

            {/* Método de Pago */}
            <div className="bg-white border border-gray-200 rounded-lg p-4">
              <div className="flex items-center gap-2 mb-3">
                <CreditCard size={18} className="text-gray-500" />
                <h3 className="font-semibold text-gray-700">Método de Pago</h3>
              </div>
              <div className="flex items-center gap-2">
                <span className="px-3 py-1 bg-blue-50 text-blue-700 border border-blue-100 rounded-full text-sm font-semibold uppercase tracking-wider">
                  {pedido.metodoPago || 'No especificado'}
                </span>
              </div>
              <p className="text-xs text-gray-400 mt-2 italic">
                Verificado en caja
              </p>
            </div>

            {/* Notas / Observaciones */}
            <div className="bg-white border border-gray-200 rounded-lg p-4">
              <div className="flex items-center gap-2 mb-3">
                <MessageSquare size={18} className="text-gray-500" />
                <h3 className="font-semibold text-gray-700">Notas del Pedido</h3>
              </div>
              <p className="text-sm text-gray-600 line-clamp-2">
                {pedido.observaciones || 'Sin observaciones adicionales.'}
              </p>
            </div>

            {/* Dirección */}
            {pedido.direccionEntrega && (
              <div className="bg-white border border-gray-200 rounded-lg p-4 md:col-span-2">
                <div className="flex items-center gap-2 mb-3">
                  <MapPin size={18} className="text-gray-500" />
                  <h3 className="font-semibold text-gray-700">Dirección de Entrega</h3>
                </div>
                <div className="space-y-2">
                  <p className="text-gray-900 font-medium">{pedido.direccionEntrega}</p>
                  {(pedido.distrito || pedido.provincia || pedido.departamento) && (
                    <p className="text-sm text-gray-600">
                      {[pedido.distrito, pedido.provincia, pedido.departamento]
                        .filter(Boolean)
                        .join(', ')}
                    </p>
                  )}
                  {pedido.referencia && (
                    <p className="text-sm text-gray-500 italic">
                      📍 {pedido.referencia}
                    </p>
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Productos */}
          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <h3 className="font-semibold text-gray-700 mb-4 flex items-center gap-2">
              <Package size={18} className="text-gray-500" />
              Productos del Pedido
            </h3>

            {detalles.length > 0 ? (
              <div className="space-y-3">
                {detalles.map((detalle, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between py-3 border-b last:border-b-0"
                  >
                    <div className="flex-1">
                      <p className="font-medium text-gray-900">
                        {detalle.producto?.nombre || detalle.combo?.nombre || 'Producto'}
                      </p>
                      <p className="text-sm text-gray-500">
                        Cantidad: {detalle.cantidad} x S/ {detalle.precioUnitario?.toFixed(2)}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-semibold text-gray-900">
                        S/ {detalle.subtotal?.toFixed(2)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-sm">No hay detalles disponibles</p>
            )}
          </div>

          {/* Resumen de Costos */}
          <div className="bg-gradient-to-br from-green-50 to-emerald-50 border border-green-200 rounded-lg p-4">
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Subtotal:</span>
                <span className="text-gray-900 font-medium">
                  S/ {(pedido.subtotal || subtotal)?.toFixed(2)}
                </span>
              </div>
              {(pedido.impuestos > 0) && (
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">IGV (18%):</span>
                  <span className="text-gray-900 font-medium">
                    S/ {pedido.impuestos?.toFixed(2)}
                  </span>
                </div>
              )}
              {pedido.costoDelivery > 0 && (
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Costo de Delivery:</span>
                  <span className="text-gray-900 font-medium">
                    S/ {pedido.costoDelivery?.toFixed(2)}
                  </span>
                </div>
              )}
              <div className="flex justify-between text-lg font-bold pt-2 border-t border-green-300">
                <span className="text-gray-800">Total a Pagar:</span>
                <span className="text-green-700">S/ {pedido.total?.toFixed(2)}</span>
              </div>
            </div>
          </div>

          {/* Estado Actual */}
          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-semibold text-gray-700 mb-1">Estado Actual</h3>
                <span
                  className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-full text-sm font-medium border ${configEstado.color}`}
                >
                  <span>{configEstado.icon}</span>
                  {configEstado.label}
                </span>
              </div>

              {/* Botón Avanzar Estado */}
              {onAvanzarEstado &&
                pedido.estado !== ESTADOS_PEDIDO.ENTREGADO &&
                pedido.estado !== ESTADOS_PEDIDO.CANCELADO && (
                  <button
                    onClick={() => onAvanzarEstado(pedido)}
                    className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium transition-colors flex items-center gap-2"
                  >
                    <CheckCircle2 size={18} />
                    Avanzar Estado
                  </button>
                )}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="border-t px-6 py-4 bg-gray-50 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors font-medium"
          >
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
};
