import { useState, useEffect, useMemo } from 'react';
import { useNavigate, useOutletContext, Link } from 'react-router-dom';
import { MapPin, Store, CreditCard, ArrowLeft, Loader2, ShoppingBag, Receipt, User, Search, CheckCircle, XCircle } from 'lucide-react';
import { useConsultarRuc } from '@/shared/hooks/useConsultaDocumento';
import { useCartStore } from '../stores/cartStore';
import { useStorefrontAuthStore } from '../stores/storefrontAuthStore';
import { storefrontService } from '../services/storefrontService';
import { formatCurrency } from '@/shared/utils/formatters';
import { useQuery, useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';

export const StorefrontCheckout = () => {
  const { slug, selectedSede } = useOutletContext();
  const navigate = useNavigate();
  const customer = useStorefrontAuthStore((s) => s.customer);
  const isAuthenticated = useStorefrontAuthStore((s) => s.isAuthenticated());
  const { items, getTotal, clearCart } = useCartStore();
  const subtotal = getTotal();

  const [tipoPedido, setTipoPedido] = useState('recojo_tienda');
  const [metodoPagoId, setMetodoPagoId] = useState(null);
  const [direccion, setDireccion] = useState('');
  const [departamento, setDepartamento] = useState('');
  const [provincia, setProvincia] = useState('');
  const [distrito, setDistrito] = useState('');
  const [referencia, setReferencia] = useState('');
  const [observaciones, setObservaciones] = useState('');

  // Campos adicionales por tipo de pago
  const [banco, setBanco] = useState('');
  const [ultimosCuatro, setUltimosCuatro] = useState('');
  const [nombreTitular, setNombreTitular] = useState('');
  const [numeroReferencia, setNumeroReferencia] = useState('');

  // Comprobante
  const rucLookup = useConsultarRuc();
  const [tipoComprobante, setTipoComprobante] = useState('boleta');
  const [docNumero, setDocNumero] = useState('');
  const [docNombre, setDocNombre] = useState('');
  const [docDireccion, setDocDireccion] = useState('');

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

  // Pre-fill comprobante (boleta) with customer data
  useEffect(() => {
    if (customer) {
      const nombreCompleto = [customer.nombres, customer.apellidos].filter(Boolean).join(' ');
      setDocNombre(nombreCompleto);
      if (customer.tipoDocumento === 'DNI' || customer.tipoDocumento === 'CE') {
        setDocNumero(customer.numeroDocumento || '');
      }
    }
  }, [customer]);

  const { data: metodosPago = [] } = useQuery({
    queryKey: ['storefront-metodos-pago', slug],
    queryFn: () => storefrontService.getMetodosPago(slug),
    enabled: !!slug,
  });

  const { data: zonasDelivery = [] } = useQuery({
    queryKey: ['storefront-zonas-delivery', slug, selectedSede?.id],
    queryFn: () => storefrontService.getZonasDelivery(slug, selectedSede?.id),
    enabled: !!slug && !!selectedSede?.id,
  });

  // Auto-detectar zona por distrito ingresado (matching client-side)
  const zonaDetectada = useMemo(() => {
    if (!distrito.trim() || zonasDelivery.length === 0) return null;
    const distLower = distrito.trim().toLowerCase();
    return zonasDelivery.find((zona) => {
      if (zona.estaActivo === false) return false;
      let distritosZona = [];
      if (typeof zona.distritos === 'string') {
        try { distritosZona = JSON.parse(zona.distritos); } catch { distritosZona = []; }
      } else if (Array.isArray(zona.distritos)) {
        distritosZona = zona.distritos;
      }
      return distritosZona.some((d) => d.toLowerCase().trim() === distLower);
    }) || null;
  }, [distrito, zonasDelivery]);

  const zonaDeliveryId = zonaDetectada?.id ?? null;
  const costoDelivery = tipoPedido === 'delivery' && zonaDetectada ? parseFloat(zonaDetectada.tarifaDelivery || 0) : 0;
  const total = subtotal + costoDelivery;

  const metodoPagoSeleccionado = metodosPago.find((mp) => mp.id === metodoPagoId);
  // Detectar tipo usando tipo, nombre o codigo como fallback (por si tipo es null en BD)
  const tipoPagoSeleccionado = (
    metodoPagoSeleccionado?.tipo ||
    metodoPagoSeleccionado?.nombre ||
    metodoPagoSeleccionado?.codigo ||
    ''
  ).toLowerCase();
  const esTarjeta = tipoPagoSeleccionado.includes('tarjeta');
  const esTransferencia = tipoPagoSeleccionado.includes('transferencia');

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

    if (tipoPedido === 'delivery' && zonasDelivery.length > 0) {
      if (!distrito.trim()) {
        toast.error('Ingresa tu distrito para verificar cobertura de delivery');
        return;
      }
      if (!zonaDetectada) {
        toast.error(`No hay cobertura de delivery para "${distrito}". Verifica el nombre del distrito.`);
        return;
      }
      const minimo = parseFloat(zonaDetectada.montoMinimoPedido || 0);
      if (minimo > 0 && subtotal < minimo) {
        toast.error(`El pedido mínimo para ${zonaDetectada.nombre} es ${formatCurrency(minimo)}. Tu subtotal es ${formatCurrency(subtotal)}.`);
        return;
      }
    }

    if (tipoPedido === 'delivery' && !direccion.trim()) {
      toast.error('Ingresa tu dirección de entrega');
      return;
    }

    if (tipoComprobante === 'factura') {
      if (!docNumero.trim() || docNumero.trim().length !== 11) {
        toast.error('Ingresa un RUC válido (11 dígitos)');
        return;
      }
      if (!docNombre.trim()) {
        toast.error('Ingresa la razón social para la factura');
        return;
      }
    }

    const pedido = {
      sedeId: selectedSede?.id,
      tipoPedido,
      origenPedido: 'tienda_online',
      metodoPagoId,
      observaciones: observaciones.trim() || null,
      tipoComprobante,
      docClienteNumero: docNumero.trim() || null,
      docClienteNombre: docNombre.trim() || null,
      docClienteDireccion: tipoComprobante === 'factura' ? (docDireccion.trim() || null) : null,
      // Campos de pago adicionales por tipo
      banco: banco.trim() || null,
      ultimosCuatroDigitos: ultimosCuatro.trim() || null,
      nombreTitular: nombreTitular.trim() || null,
      numeroReferencia: numeroReferencia.trim() || null,
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
              </div>
            </div>

            {/* Delivery address */}
            {tipoPedido === 'delivery' && (
              <div className="bg-white rounded-xl border border-gray-200 p-6">
                <h2 className="font-semibold text-gray-900 mb-4">Dirección de Entrega</h2>

                <div className="space-y-3">
                  {/* Departamento + Provincia */}
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Departamento</label>
                      <input
                        type="text"
                        value={departamento}
                        onChange={(e) => setDepartamento(e.target.value)}
                        placeholder="San Martín"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Provincia</label>
                      <input
                        type="text"
                        value={provincia}
                        onChange={(e) => setProvincia(e.target.value)}
                        placeholder="San Martín"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                      />
                    </div>
                  </div>

                  {/* Distrito con auto-detección de zona */}
                  {zonasDelivery.length > 0 ? (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Distrito <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={distrito}
                        onChange={(e) => setDistrito(e.target.value)}
                        placeholder="Escribe tu distrito..."
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                      />
                      {distrito.trim() && (
                        zonaDetectada ? (
                          <div className={`mt-2 p-3 rounded-lg border flex items-start gap-2 ${
                            parseFloat(zonaDetectada.montoMinimoPedido || 0) > 0 && subtotal < parseFloat(zonaDetectada.montoMinimoPedido)
                              ? 'bg-orange-50 border-orange-300'
                              : 'bg-green-50 border-green-200'
                          }`}>
                            <CheckCircle size={16} className={`flex-shrink-0 mt-0.5 ${
                              parseFloat(zonaDetectada.montoMinimoPedido || 0) > 0 && subtotal < parseFloat(zonaDetectada.montoMinimoPedido)
                                ? 'text-orange-500' : 'text-green-600'
                            }`} />
                            <div>
                              <p className="text-sm font-medium text-gray-800">{zonaDetectada.nombre}</p>
                              <p className="text-xs text-gray-600">
                                Costo de delivery: {formatCurrency(zonaDetectada.tarifaDelivery)}
                                {parseFloat(zonaDetectada.montoMinimoPedido) > 0 &&
                                  ` · Pedido mínimo: ${formatCurrency(zonaDetectada.montoMinimoPedido)}`}
                              </p>
                              {parseFloat(zonaDetectada.montoMinimoPedido || 0) > 0 && subtotal < parseFloat(zonaDetectada.montoMinimoPedido) && (
                                <p className="text-xs text-orange-700 font-medium mt-0.5">
                                  ⚠ Tu subtotal ({formatCurrency(subtotal)}) no alcanza el mínimo requerido.
                                </p>
                              )}
                            </div>
                          </div>
                        ) : (
                          <div className="mt-2 p-3 bg-red-50 border border-red-200 rounded-lg flex items-start gap-2">
                            <XCircle size={16} className="text-red-500 flex-shrink-0 mt-0.5" />
                            <p className="text-sm text-red-700">
                              No hay cobertura de delivery para <strong>{distrito}</strong>. Verifica el nombre o elige recojo en tienda.
                            </p>
                          </div>
                        )
                      )}
                    </div>
                  ) : (
                    <>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Distrito</label>
                        <input
                          type="text"
                          value={distrito}
                          onChange={(e) => setDistrito(e.target.value)}
                          placeholder="Tarapoto"
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                        />
                      </div>
                      <div className="p-3 bg-amber-50 border border-amber-200 rounded-lg">
                        <p className="text-sm text-amber-700">El costo de delivery se coordinará con el negocio al confirmar el pedido.</p>
                      </div>
                    </>
                  )}

                  {/* Dirección (jirón/av) */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Dirección <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      value={direccion}
                      onChange={(e) => setDireccion(e.target.value)}
                      placeholder="Jr. Shapaja 456, Dpto. 2A"
                      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                  </div>

                  {/* Referencia */}
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
                        onChange={() => {
                          setMetodoPagoId(mp.id);
                          setBanco('');
                          setUltimosCuatro('');
                          setNombreTitular('');
                          setNumeroReferencia('');
                        }}
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

              {/* Campos adicionales por tipo de pago */}
              {metodoPagoId && (esTarjeta || esTransferencia) && (
                <div className="mt-4 space-y-3 border-t border-gray-100 pt-4">
                  <p className="text-xs font-medium text-gray-600 uppercase tracking-wide">
                    {esTarjeta ? 'Datos de la tarjeta' : 'Datos de la transferencia'}
                  </p>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      {esTarjeta ? 'Banco / Emisor' : 'Banco de origen'}
                    </label>
                    <input
                      type="text"
                      value={banco}
                      onChange={(e) => setBanco(e.target.value)}
                      placeholder={esTarjeta ? 'Ej: BCP, Visa, Mastercard...' : 'Ej: BCP, Interbank, BBVA...'}
                      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                  </div>
                  {esTarjeta && (
                    <>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Últimos 4 dígitos</label>
                        <input
                          type="text"
                          value={ultimosCuatro}
                          onChange={(e) => setUltimosCuatro(e.target.value.replace(/\D/g, '').slice(0, 4))}
                          placeholder="1234"
                          maxLength={4}
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Nombre del titular</label>
                        <input
                          type="text"
                          value={nombreTitular}
                          onChange={(e) => setNombreTitular(e.target.value)}
                          placeholder="Como aparece en la tarjeta"
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                        />
                      </div>
                    </>
                  )}
                  {esTransferencia && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Número de operación / referencia</label>
                      <input
                        type="text"
                        value={numeroReferencia}
                        onChange={(e) => setNumeroReferencia(e.target.value)}
                        placeholder="Nro. de operación bancaria"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                      />
                    </div>
                  )}
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

            {/* Comprobante */}
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <h2 className="font-semibold text-gray-900 mb-1 flex items-center gap-2">
                <Receipt size={18} className="text-amber-600" />
                Comprobante de Pago
              </h2>
              <p className="text-xs text-gray-500 mb-4">
                Selecciona el tipo de comprobante que deseas recibir
              </p>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
                <label
                  className={`flex items-center gap-3 p-4 border-2 rounded-xl cursor-pointer transition-colors ${
                    tipoComprobante === 'boleta'
                      ? 'border-amber-500 bg-amber-50'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="tipoComprobante"
                    value="boleta"
                    checked={tipoComprobante === 'boleta'}
                    onChange={(e) => {
                      setTipoComprobante(e.target.value);
                      if (customer) {
                        const nombre = [customer.nombres, customer.apellidos].filter(Boolean).join(' ');
                        setDocNombre(nombre);
                        if (customer.tipoDocumento === 'DNI' || customer.tipoDocumento === 'CE') {
                          setDocNumero(customer.numeroDocumento || '');
                        } else {
                          setDocNumero('');
                        }
                        setDocDireccion('');
                      }
                    }}
                    className="sr-only"
                  />
                  <div className={`w-4 h-4 rounded-full border-2 flex-shrink-0 ${tipoComprobante === 'boleta' ? 'border-amber-500 bg-amber-500' : 'border-gray-400'}`} />
                  <div>
                    <p className="font-medium text-gray-900 text-sm">Boleta</p>
                    <p className="text-xs text-gray-500">DNI / CE</p>
                  </div>
                </label>
                <label
                  className={`flex items-center gap-3 p-4 border-2 rounded-xl cursor-pointer transition-colors ${
                    tipoComprobante === 'factura'
                      ? 'border-amber-500 bg-amber-50'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="tipoComprobante"
                    value="factura"
                    checked={tipoComprobante === 'factura'}
                    onChange={(e) => {
                      setTipoComprobante(e.target.value);
                      setDocNumero('');
                      setDocNombre('');
                      setDocDireccion('');
                    }}
                    className="sr-only"
                  />
                  <div className={`w-4 h-4 rounded-full border-2 flex-shrink-0 ${tipoComprobante === 'factura' ? 'border-amber-500 bg-amber-500' : 'border-gray-400'}`} />
                  <div>
                    <p className="font-medium text-gray-900 text-sm">Factura</p>
                    <p className="text-xs text-gray-500">RUC (empresa)</p>
                  </div>
                </label>
              </div>

              {tipoComprobante === 'boleta' && (
                <div className="flex items-center gap-3 p-3 bg-amber-50 rounded-lg border border-amber-200">
                  <User size={16} className="text-amber-600 shrink-0" />
                  <div className="min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">{docNombre || '—'}</p>
                    {docNumero && (
                      <p className="text-xs text-gray-500">
                        {customer?.tipoDocumento || 'Doc'}: {docNumero}
                      </p>
                    )}
                  </div>
                  <span className="ml-auto text-xs text-gray-400 shrink-0">Datos de tu cuenta</span>
                </div>
              )}

              {tipoComprobante === 'factura' && (
                <div className="space-y-3">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        RUC <span className="text-red-500">*</span>
                      </label>
                      <div className="relative">
                        <input
                          type="text"
                          value={docNumero}
                          onChange={(e) => {
                            const val = e.target.value.replace(/\D/g, '');
                            setDocNumero(val);
                            rucLookup.reset();
                            setDocNombre('');
                            setDocDireccion('');
                            if (val.length === 11) {
                              rucLookup.buscar(val).then((data) => {
                                if (data) {
                                  setDocNombre(data.razon_social || '');
                                  setDocDireccion(data.direccion || '');
                                }
                              });
                            }
                          }}
                          placeholder="20123456789"
                          maxLength={11}
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 pr-9 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                        />
                        <div className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">
                          {rucLookup.isLoading
                            ? <Loader2 size={14} className="animate-spin" />
                            : <Search size={14} />}
                        </div>
                      </div>
                      {rucLookup.error && (
                        <p className="text-xs text-red-500 mt-1">{rucLookup.error}</p>
                      )}
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Razón Social <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={docNombre}
                        onChange={(e) => setDocNombre(e.target.value)}
                        placeholder={rucLookup.isLoading ? 'Buscando...' : 'Mi Empresa S.A.C.'}
                        disabled={rucLookup.isLoading}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500 disabled:bg-gray-50 disabled:text-gray-500"
                      />
                    </div>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Dirección Fiscal
                    </label>
                    <input
                      type="text"
                      value={docDireccion}
                      onChange={(e) => setDocDireccion(e.target.value)}
                      placeholder="Av. Industrial 456, Lima"
                      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                  </div>
                </div>
              )}
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
                      {zonaDetectada ? formatCurrency(costoDelivery) : 'Escribe tu distrito'}
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
