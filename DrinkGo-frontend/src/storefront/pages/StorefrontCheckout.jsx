import { useState, useEffect } from 'react';
import { useNavigate, useOutletContext, Link } from 'react-router-dom';
import { MapPin, Store, CreditCard, ArrowLeft, Loader2, ShoppingBag } from 'lucide-react';
import { useCartStore } from '../stores/cartStore';
import { useStorefrontAuthStore } from '../stores/storefrontAuthStore';
import { storefrontService } from '../services/storefrontService';
import { formatCurrency } from '@/shared/utils/formatters';
import { useQuery, useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';

export const StorefrontCheckout = () => {
  const { slug, selectedSede } = useOutletContext();
  const navigate = useNavigate();
  const isAuthenticated = useStorefrontAuthStore((s) => s.isAuthenticated());
  const { items, getTotal, clearCart } = useCartStore();
  const subtotal = getTotal();

  const [tipoPedido, setTipoPedido] = useState('recojo_tienda');
  const [metodoPagoId, setMetodoPagoId] = useState(null);
  const [zonaDeliveryId, setZonaDeliveryId] = useState(null);
  const [direccion, setDireccion] = useState('');
  const [departamento, setDepartamento] = useState('');
  const [provincia, setProvincia] = useState('');
  const [distrito, setDistrito] = useState('');
  const [referencia, setReferencia] = useState('');
  const [observaciones, setObservaciones] = useState('');

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      navigate(`/tienda/${slug}/login?redirect=checkout`, { replace: true });
    }
  }, [isAuthenticated, slug, navigate]);

  // Redirect if cart is empty
  useEffect(() => {
    if (items.length === 0) {
      navigate(`/tienda/${slug}/carrito`, { replace: true });
    }
  }, [items, slug, navigate]);

  const { data: metodosPago = [] } = useQuery({
    queryKey: ['storefront-metodos-pago', slug],
    queryFn: () => storefrontService.getMetodosPago(slug),
    enabled: !!slug,
  });

  const { data: zonasDelivery = [] } = useQuery({
    queryKey: ['storefront-zonas-delivery', slug, selectedSede?.id],
    queryFn: () => storefrontService.getZonasDelivery(slug, selectedSede?.id),
    enabled: !!slug && !!selectedSede?.id && tipoPedido === 'delivery',
  });

  const selectedZona = zonasDelivery.find((z) => z.id === zonaDeliveryId);
  const costoDelivery = tipoPedido === 'delivery' && selectedZona ? selectedZona.tarifaDelivery : 0;
  const total = subtotal + costoDelivery;

  const createPedidoMutation = useMutation({
    mutationFn: (pedido) => storefrontService.createPedido(slug, pedido),
    onSuccess: (data) => {
      clearCart();
      toast.success('¡Pedido realizado exitosamente!');
      navigate(`/tienda/${slug}/mis-pedidos`, { replace: true });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error al realizar el pedido');
    },
  });

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!metodoPagoId) {
      toast.error('Selecciona un método de pago');
      return;
    }

    if (tipoPedido === 'delivery' && !zonaDeliveryId) {
      toast.error('Selecciona una zona de delivery');
      return;
    }

    if (tipoPedido === 'delivery' && !direccion.trim()) {
      toast.error('Ingresa tu dirección de entrega');
      return;
    }

    const pedido = {
      sedeId: selectedSede?.id,
      tipoPedido,
      origenPedido: 'tienda_online',
      metodoPagoId,
      observaciones: observaciones.trim() || null,
      detalles: items.map((item) => ({
        productoId: item.product.id,
        cantidad: item.quantity,
        precioUnitario: item.product.precioVenta,
      })),
      ...(tipoPedido === 'delivery' && {
        zonaDeliveryId,
        direccionEntrega: direccion.trim(),
        departamento: departamento.trim() || null,
        provincia: provincia.trim() || null,
        distrito: distrito.trim() || null,
        referencia: referencia.trim() || null,
      }),
    };

    createPedidoMutation.mutate(pedido);
  };

  if (!isAuthenticated || items.length === 0) return null;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link
        to={`/tienda/${slug}/carrito`}
        className="inline-flex items-center gap-1 text-amber-600 hover:text-amber-700 text-sm mb-6"
      >
        <ArrowLeft size={16} /> Volver al carrito
      </Link>

      <h1 className="text-2xl font-bold text-gray-900 mb-8">Finalizar Pedido</h1>

      <form onSubmit={handleSubmit}>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left: Options */}
          <div className="lg:col-span-2 space-y-6">
            {/* Tipo de pedido */}
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <h2 className="font-semibold text-gray-900 mb-4">Tipo de Entrega</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <label
                  className={`flex items-center gap-3 p-4 border-2 rounded-xl cursor-pointer transition-colors ${
                    tipoPedido === 'recojo_tienda'
                      ? 'border-amber-500 bg-amber-50'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="tipoPedido"
                    value="recojo_tienda"
                    checked={tipoPedido === 'recojo_tienda'}
                    onChange={(e) => setTipoPedido(e.target.value)}
                    className="sr-only"
                  />
                  <Store size={24} className={tipoPedido === 'recojo_tienda' ? 'text-amber-600' : 'text-gray-400'} />
                  <div>
                    <p className="font-medium text-gray-900">Recojo en Tienda</p>
                    <p className="text-sm text-gray-500">Ven a recoger tu pedido</p>
                  </div>
                </label>

                {selectedSede?.deliveryHabilitado && (
                  <label
                    className={`flex items-center gap-3 p-4 border-2 rounded-xl cursor-pointer transition-colors ${
                      tipoPedido === 'delivery'
                        ? 'border-amber-500 bg-amber-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <input
                      type="radio"
                      name="tipoPedido"
                      value="delivery"
                      checked={tipoPedido === 'delivery'}
                      onChange={(e) => setTipoPedido(e.target.value)}
                      className="sr-only"
                    />
                    <MapPin size={24} className={tipoPedido === 'delivery' ? 'text-amber-600' : 'text-gray-400'} />
                    <div>
                      <p className="font-medium text-gray-900">Delivery</p>
                      <p className="text-sm text-gray-500">Recibe en tu dirección</p>
                    </div>
                  </label>
                )}
              </div>
            </div>

            {/* Delivery address */}
            {tipoPedido === 'delivery' && (
              <div className="bg-white rounded-xl border border-gray-200 p-6">
                <h2 className="font-semibold text-gray-900 mb-4">Dirección de Entrega</h2>

                {zonasDelivery.length > 0 && (
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Zona de Delivery <span className="text-red-500">*</span>
                    </label>
                    <select
                      value={zonaDeliveryId || ''}
                      onChange={(e) => setZonaDeliveryId(Number(e.target.value) || null)}
                      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                    >
                      <option value="">Selecciona tu zona</option>
                      {zonasDelivery.map((z) => (
                        <option key={z.id} value={z.id}>
                          {z.nombre} - {formatCurrency(z.tarifaDelivery)}
                          {z.montoMinimoPedido > 0 && ` (mín. ${formatCurrency(z.montoMinimoPedido)})`}
                        </option>
                      ))}
                    </select>
                  </div>
                )}

                <div className="space-y-3">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Dirección <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      value={direccion}
                      onChange={(e) => setDireccion(e.target.value)}
                      placeholder="Av. Principal 123, Dpto. 4B"
                      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                  </div>
                  <div className="grid grid-cols-3 gap-3">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Departamento</label>
                      <input
                        type="text"
                        value={departamento}
                        onChange={(e) => setDepartamento(e.target.value)}
                        placeholder="Lima"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Provincia</label>
                      <input
                        type="text"
                        value={provincia}
                        onChange={(e) => setProvincia(e.target.value)}
                        placeholder="Lima"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Distrito</label>
                      <input
                        type="text"
                        value={distrito}
                        onChange={(e) => setDistrito(e.target.value)}
                        placeholder="Miraflores"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                      />
                    </div>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Referencia</label>
                    <input
                      type="text"
                      value={referencia}
                      onChange={(e) => setReferencia(e.target.value)}
                      placeholder="A dos cuadras del parque"
                      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                  </div>
                </div>
              </div>
            )}

            {/* Método de pago */}
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <h2 className="font-semibold text-gray-900 mb-4">Método de Pago</h2>
              {metodosPago.length === 0 ? (
                <p className="text-sm text-gray-500">No hay métodos de pago disponibles.</p>
              ) : (
                <div className="space-y-2">
                  {metodosPago.map((mp) => (
                    <label
                      key={mp.id}
                      className={`flex items-center gap-3 p-3 border-2 rounded-xl cursor-pointer transition-colors ${
                        metodoPagoId === mp.id
                          ? 'border-amber-500 bg-amber-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <input
                        type="radio"
                        name="metodoPago"
                        value={mp.id}
                        checked={metodoPagoId === mp.id}
                        onChange={() => setMetodoPagoId(mp.id)}
                        className="sr-only"
                      />
                      <CreditCard
                        size={20}
                        className={metodoPagoId === mp.id ? 'text-amber-600' : 'text-gray-400'}
                      />
                      <span className="font-medium text-gray-900 text-sm">{mp.nombre}</span>
                    </label>
                  ))}
                </div>
              )}
            </div>

            {/* Observaciones */}
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <h2 className="font-semibold text-gray-900 mb-4">Observaciones</h2>
              <textarea
                value={observaciones}
                onChange={(e) => setObservaciones(e.target.value)}
                rows={3}
                placeholder="Instrucciones especiales para tu pedido..."
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500 resize-none"
              />
            </div>
          </div>

          {/* Right: Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl border border-gray-200 p-6 sticky top-24">
              <h3 className="font-semibold text-gray-900 mb-4">Resumen del Pedido</h3>

              <div className="space-y-3 border-b border-gray-200 pb-4 mb-4 max-h-64 overflow-y-auto">
                {items.map(({ product, quantity }) => (
                  <div key={product.id} className="flex justify-between text-sm">
                    <div className="flex-1 min-w-0 pr-2">
                      <p className="text-gray-700 truncate">{product.nombre}</p>
                      <p className="text-xs text-gray-400">×{quantity}</p>
                    </div>
                    <p className="font-medium text-gray-900 shrink-0">
                      {formatCurrency(product.precioVenta * quantity)}
                    </p>
                  </div>
                ))}
              </div>

              <div className="space-y-2 text-sm border-b border-gray-200 pb-4 mb-4">
                <div className="flex justify-between text-gray-600">
                  <span>Subtotal</span>
                  <span>{formatCurrency(subtotal)}</span>
                </div>
                {tipoPedido === 'delivery' && (
                  <div className="flex justify-between text-gray-600">
                    <span>Delivery</span>
                    <span>
                      {selectedZona ? formatCurrency(costoDelivery) : 'Selecciona zona'}
                    </span>
                  </div>
                )}
              </div>

              <div className="flex justify-between font-bold text-gray-900 text-lg mb-6">
                <span>Total</span>
                <span>{formatCurrency(total)}</span>
              </div>

              {selectedSede && (
                <div className="bg-gray-50 rounded-lg p-3 text-xs text-gray-600 mb-4">
                  <p className="font-medium text-gray-700">Sede: {selectedSede.nombre}</p>
                  {selectedSede.direccion && <p>{selectedSede.direccion}</p>}
                </div>
              )}

              <button
                type="submit"
                disabled={createPedidoMutation.isPending}
                className="w-full py-3 bg-amber-500 hover:bg-amber-600 disabled:bg-amber-300 text-white font-semibold rounded-xl transition-colors flex items-center justify-center gap-2"
              >
                {createPedidoMutation.isPending ? (
                  <>
                    <Loader2 size={18} className="animate-spin" />
                    Procesando...
                  </>
                ) : (
                  <>
                    <ShoppingBag size={18} />
                    Realizar Pedido
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
};
