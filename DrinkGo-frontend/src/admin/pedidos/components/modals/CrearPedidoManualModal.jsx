/**
 * Modal principal para crear pedidos manuales
 * Integra todos los componentes: cliente, productos, dirección, totales
 */
import { useState, useMemo, useEffect } from 'react';
import { X, ShoppingCart, Loader2, Store, Truck } from 'lucide-react';
import { BuscadorCliente } from '../forms/BuscadorCliente';
import { SelectorProductos } from '../forms/SelectorProductos';
import { SelectorZonaDelivery } from '../forms/SelectorZonaDelivery';
import { ResumenTotales } from '../forms/ResumenTotales';
import { useCrearPedidoManual } from '../../hooks/useCrearPedidoManual';
import { useMetodosPago } from '@/admin/configuracion/hooks/useMetodosPago';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { message } from '@/shared/utils/notifications';

export function CrearPedidoManualModal({ isOpen, onClose, onPedidoCreado }) {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;
  
  // Estados del formulario
  const [clienteSeleccionado, setClienteSeleccionado] = useState(null);
  const [items, setItems] = useState([]);
  const [tipoPedido, setTipoPedido] = useState('delivery');
  const [origenPedido, setOrigenPedido] = useState('telefono');
  const [tipoPedidoAnterior, setTipoPedidoAnterior] = useState('delivery');
  
  // Datos de dirección (solo para delivery)
  const [distrito, setDistrito] = useState('');
  const [direccion, setDireccion] = useState('');
  const [referencia, setReferencia] = useState('');
  const [deliveryInfo, setDeliveryInfo] = useState({
    costo: 0,
    zona: null,
    montoMinimo: 0,
  });
  
  // Observaciones y método de pago
  const [observaciones, setObservaciones] = useState('');
  const { metodosPago, isLoading: cargandoMetodos } = useMetodosPago(negocioId);
  const [metodoPago, setMetodoPago] = useState('');
  const [incluirIGV, setIncluirIGV] = useState(true);

  // Efecto para seleccionar el primer método de pago por defecto
  useEffect(() => {
    if (metodosPago.length > 0 && !metodoPago) {
      setMetodoPago(metodosPago[0].nombre);
    }
  }, [metodosPago, metodoPago]);
  
  // Hook de creación
  const { mutateAsync: crearPedido, isPending: creandoPedido } = useCrearPedidoManual();
  
  // Efecto: Limpiar carrito al cambiar tipo de pedido
  useEffect(() => {
    if (tipoPedido !== tipoPedidoAnterior) {
      // Limpiar items
      setItems([]);
      // Limpiar datos de delivery
      setDistrito('');
      setDireccion('');
      setReferencia('');
      setDeliveryInfo({ costo: 0, zona: null, montoMinimo: 0 });
      // Actualizar tipo anterior
      setTipoPedidoAnterior(tipoPedido);
    }
  }, [tipoPedido, tipoPedidoAnterior]);
  
  // Calcular subtotal
  const subtotal = useMemo(() => {
    return items.reduce((total, item) => total + (item.precioUnitario * item.cantidad), 0);
  }, [items]);
  
  // Validar formulario
  const puedeGuardar = () => {
    if (!clienteSeleccionado) return false;
    if (items.length === 0) return false;
    
    if (tipoPedido === 'delivery') {
      if (!distrito || !direccion) return false;
      if (!deliveryInfo.zona) return false;
      
      // Verificar monto mínimo
      const total = subtotal + (subtotal * 0.18) + deliveryInfo.costo;
      if (deliveryInfo.montoMinimo > 0 && total < deliveryInfo.montoMinimo) {
        return false;
      }
    }
    
    return true;
  };
  
  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!puedeGuardar()) {
      message.error('Por favor completa todos los campos requeridos');
      return;
    }
    
    try {
      const igv = incluirIGV ? subtotal * 0.18 : 0;
      const costoDeliveryFinal = tipoPedido === 'delivery' ? deliveryInfo.costo : 0;
      const total = subtotal + igv + costoDeliveryFinal;
      
      const pedidoData = {
        clienteId: clienteSeleccionado.id,
        tipoPedido,
        origenPedido,
        items,
        subtotal,
        impuestos: igv,
        incluirIGV,
        costoDelivery: costoDeliveryFinal,
        total,
        observaciones,
        metodoPago,
      };
      
      // Agregar datos de dirección si es delivery
      if (tipoPedido === 'delivery') {
        pedidoData.direccionEntrega = direccion;
        pedidoData.departamento = 'Lima';
        pedidoData.provincia = 'Lima';
        pedidoData.distrito = distrito;
        pedidoData.referencia = referencia;
        pedidoData.zonaDeliveryId = deliveryInfo.zona?.id;
      }
      
      const pedidoCreado = await crearPedido(pedidoData);
      
      // Callback y cerrar antes de limpiar para evitar parpadeos
      if (onPedidoCreado) {
        onPedidoCreado(pedidoCreado);
      }
      
      // Limpiar formulario
      setClienteSeleccionado(null);
      setItems([]);
      setDistrito('');
      setDireccion('');
      setReferencia('');
      setObservaciones('');
      setMetodoPago('Efectivo');
      
      onClose();
    } catch (error) {
      console.error('Error al crear pedido:', error);
    }
  };
  
  // Resetear formulario al cerrar
  const handleClose = () => {
    if (!creandoPedido) {
      setClienteSeleccionado(null);
      setItems([]);
      setDistrito('');
      setDireccion('');
      setReferencia('');
      setObservaciones('');
      setMetodoPago('Efectivo');
      onClose();
    }
  };
  
  if (!isOpen) return null;
  
  return (
    <div className="fixed inset-0 z-50 flex items-start justify-center bg-black/50 overflow-y-auto py-8">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-6xl mx-4">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <div className="bg-blue-100 p-2 rounded-lg">
              <ShoppingCart className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">Crear Pedido Manual</h2>
              <p className="text-sm text-gray-600">Registro de pedido por teléfono o tienda</p>
            </div>
          </div>
          <button
            type="button"
            onClick={handleClose}
            disabled={creandoPedido}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors disabled:opacity-50"
          >
            <X className="w-6 h-6 text-gray-600" />
          </button>
        </div>
        
        {/* Formulario */}
        <form onSubmit={handleSubmit} className="p-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Columna izquierda - Datos del pedido */}
            <div className="lg:col-span-2 space-y-6">
              {/* Tipo y Origen de Pedido */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tipo de Pedido *
                  </label>
                  <div className="grid grid-cols-2 gap-3">
                    <button
                      type="button"
                      onClick={() => setTipoPedido('delivery')}
                      className={`p-4 border-2 rounded-lg flex flex-col items-center gap-2 transition-all ${
                        tipoPedido === 'delivery'
                          ? 'border-blue-500 bg-blue-50 text-blue-700'
                          : 'border-gray-300 hover:border-gray-400'
                      }`}
                    >
                      <Truck className="w-6 h-6" />
                      <span className="text-sm font-medium">Delivery</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => setTipoPedido('recojo_tienda')}
                      className={`p-4 border-2 rounded-lg flex flex-col items-center gap-2 transition-all ${
                        tipoPedido === 'recojo_tienda'
                          ? 'border-blue-500 bg-blue-50 text-blue-700'
                          : 'border-gray-300 hover:border-gray-400'
                      }`}
                    >
                      <Store className="w-6 h-6" />
                      <span className="text-sm font-medium">Recojo en Tienda</span>
                    </button>
                  </div>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Origen del Pedido *
                  </label>
                  <select
                    value={origenPedido}
                    onChange={(e) => setOrigenPedido(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="telefono">Teléfono</option>
                    <option value="whatsapp">WhatsApp</option>
                    <option value="pos">POS (tienda)</option>
                    <option value="otro">Otro</option>
                  </select>
                </div>
              </div>
              
              {/* Buscador de Cliente */}
              <div className="bg-gray-50 p-4 rounded-lg">
                <BuscadorCliente
                  clienteSeleccionado={clienteSeleccionado}
                  onClienteSeleccionado={setClienteSeleccionado}
                />
              </div>
              
              {/* Selector de Productos */}
              <div className="bg-gray-50 p-4 rounded-lg">
                <SelectorProductos
                  items={items}
                  onItemsChange={setItems}
                />
              </div>
              
              {/* Datos de Delivery (solo si es delivery) */}
              {tipoPedido === 'delivery' && (
                <div className="bg-blue-50 p-4 rounded-lg">
                  <h3 className="font-medium text-gray-900 mb-4 flex items-center gap-2">
                    <Truck className="w-5 h-5 text-blue-600" />
                    Datos de Entrega
                  </h3>
                  <SelectorZonaDelivery
                    distrito={distrito}
                    onDistritoChange={setDistrito}
                    direccion={direccion}
                    onDireccionChange={setDireccion}
                    referencia={referencia}
                    onReferenciaChange={setReferencia}
                    onCostoDeliveryCalculado={setDeliveryInfo}
                  />
                </div>
              )}
              
              {/* Método de Pago y Observaciones */}
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Método de Pago
                    </label>
                    <select
                      value={metodoPago}
                      onChange={(e) => setMetodoPago(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                      disabled={cargandoMetodos}
                    >
                      {cargandoMetodos ? (
                        <option>Cargando métodos...</option>
                      ) : metodosPago.length > 0 ? (
                        metodosPago.map((m) => (
                          <option key={m.id} value={m.nombre}>
                            {m.nombre}
                          </option>
                        ))
                      ) : (
                        <>
                          <option value="Efectivo">Efectivo</option>
                          <option value="Transferencia">Transferencia</option>
                          <option value="Yape/Plin">Yape/Plin</option>
                        </>
                      )}
                    </select>
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Opciones de Facturación
                    </label>
                    <label className="flex items-center gap-2 px-4 py-2.5 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                      <input
                        type="checkbox"
                        checked={incluirIGV}
                        onChange={(e) => setIncluirIGV(e.target.checked)}
                        className="w-4 h-4 text-blue-600 rounded focus:ring-2 focus:ring-blue-500"
                      />
                      <span className="text-sm text-gray-700">Incluir IGV (18%)</span>
                    </label>
                  </div>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Observaciones
                  </label>
                  <textarea
                    value={observaciones}
                    onChange={(e) => setObservaciones(e.target.value)}
                    placeholder="Notas adicionales..."
                    rows={2}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 resize-none"
                  />
                </div>
              </div>
            </div>
            
            {/* Columna derecha - Resumen de Totales */}
            <div className="lg:col-span-1">
              <div className="sticky top-6">
                <ResumenTotales
                  subtotal={subtotal}
                  costoDelivery={deliveryInfo.costo}
                  montoMinimo={deliveryInfo.montoMinimo}
                  tipoPedido={tipoPedido}
                  incluirIGV={incluirIGV}
                />
              </div>
            </div>
          </div>
          
          {/* Footer con botones */}
          <div className="flex items-center justify-end gap-3 mt-6 pt-6 border-t border-gray-200">
            <button
              type="button"
              onClick={handleClose}
              disabled={creandoPedido}
              className="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={!puedeGuardar() || creandoPedido}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              {creandoPedido ? (
                <>
                  <Loader2 className="w-5 h-5 animate-spin" />
                  Creando Pedido...
                </>
              ) : (
                <>
                  <ShoppingCart className="w-5 h-5" />
                  Crear Pedido
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
