/**
 * POS.jsx
 * ───────
 * Página principal del Punto de Venta.
 * Si no hay turno abierto redirige a Cajas para aperturar.
 */
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DollarSign, Power, ArrowDownUp, ShoppingBag, AlertCircle, LogIn } from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { ProductoSearch } from '../components/ProductoSearch';
import { CarritoPanel } from '../components/CarritoPanel';
import { ResumenVenta } from '../components/ResumenVenta';
import { PagoModal } from '../components/PagoModal';
import { MovimientoCajaModal } from '../components/MovimientoCajaModal';
import { useCartStore } from '../stores/cartStore';
import { useSesionActiva, useResumenTurno, useMovimientos, useCategoriasGasto } from '../hooks/useCajas';
import { useCrearVenta, useMetodosPago } from '../hooks/useVentas';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';

export const POS = () => {
  const navigate = useNavigate();
  const { user, negocio } = useAdminAuthStore();
  const { sesion, hasSesion, isLoading: loadingSesion } = useSesionActiva();
  const { resumen } = useResumenTurno(sesion?.id);
  const { registrar: registrarMovimiento, isRegistrando } = useMovimientos(sesion?.id);
  const { categorias } = useCategoriasGasto();
  const { crearVenta, isCreating } = useCrearVenta();
  const { metodosPago } = useMetodosPago();

  const items = useCartStore((s) => s.items);
  const getTotal = useCartStore((s) => s.getTotal);
  const clearCart = useCartStore((s) => s.clearCart);
  const descuentoGlobal = useCartStore((s) => s.descuentoGlobal);
  const razonDescuento = useCartStore((s) => s.razonDescuento);

  const [showPagoModal, setShowPagoModal] = useState(false);
  const [showMovimientoModal, setShowMovimientoModal] = useState(false);

  /* ─── Registrar movimiento ─── */
  const handleMovimiento = async (data) => {
    await registrarMovimiento(data);
    setShowMovimientoModal(false);
  };

  /* ─── Procesar venta ─── */
  const handlePago = async ({ pagos, tipoComprobante, docClienteNumero, docClienteNombre }) => {
    const ventaData = {
      negocioId: negocio?.id,
      sedeId: sesion?.caja?.sede?.id || null,
      sesionCajaId: sesion?.id,
      usuarioId: user?.id,
      clienteId: null,
      descuentoGlobal,
      razonDescuento: razonDescuento || null,
      tipoComprobante,
      docClienteNumero,
      docClienteNombre,
      items: items.map((i) => ({
        productoId: i.producto.id,
        nombreProducto: i.producto.nombre,
        precioUnitario: i.producto.precioVenta,
        cantidad: i.cantidad,
        descuento: i.descuento || 0,
      })),
      pagos,
    };

    await crearVenta(ventaData);
    setShowPagoModal(false);
    clearCart();
  };

  /* ─── LOADING ─── */
  if (loadingSesion) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-4 border-green-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  /* Si no hay sesión, mostrar aviso claro */
  if (!hasSesion) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Punto de Venta</h1>
          <p className="text-gray-600 mt-1">Registra ventas rápidas desde el mostrador</p>
        </div>

        <div className="flex flex-col items-center justify-center bg-white rounded-xl border border-gray-200 shadow-sm py-16 px-6">
          <div className="w-20 h-20 bg-amber-100 rounded-full flex items-center justify-center mb-6">
            <AlertCircle size={40} className="text-amber-500" />
          </div>

          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            No tienes una caja abierta
          </h2>

          <p className="text-gray-500 text-center max-w-md mb-2">
            Para poder realizar ventas, primero debes aperturar una caja registradora.
            Esto permite llevar un control preciso del dinero en tu turno.
          </p>

          <p className="text-sm text-gray-400 text-center max-w-sm mb-8">
            Ve a la sección de Cajas, selecciona una caja disponible e ingresa el monto
            con el que inicias tu turno.
          </p>

          <Button
            onClick={() => navigate('/admin/ventas/cajas')}
            className="px-6 py-3 text-base"
          >
            <LogIn size={20} className="mr-2" />
            Ir a Aperturar Caja
          </Button>
        </div>
      </div>
    );
  }

  const total = getTotal();

  return (
    <div className="space-y-4">
      {/* Header con info de turno */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Punto de Venta</h1>
          <div className="flex items-center gap-2 mt-1 flex-wrap">
            <Badge variant="success">Turno abierto</Badge>
            <span className="text-sm text-gray-500">
              Caja: {sesion?.caja?.nombreCaja || '—'} · Desde: {formatDateTime(sesion?.fechaApertura)}
            </span>
          </div>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowMovimientoModal(true)}
          >
            <ArrowDownUp size={16} className="mr-1" />
            Movimiento
          </Button>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => navigate('/admin/ventas/cajas')}
          >
            <Power size={16} className="mr-1" />
            Ir a Cajas
          </Button>
        </div>
      </div>

      {/* POS Layout: 2 columnas */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Columna izquierda: búsqueda + carrito */}
        <div className="lg:col-span-2 space-y-4">
          <Card className="!p-4">
            <ProductoSearch />
          </Card>

          <Card className="!p-0">
            <div className="px-4 py-3 border-b border-gray-200 flex items-center gap-2">
              <ShoppingBag size={18} className="text-green-600" />
              <h2 className="font-semibold text-gray-900">Carrito</h2>
              <span className="text-sm text-gray-400">
                ({items.length} {items.length === 1 ? 'producto' : 'productos'})
              </span>
            </div>
            {items.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-12 text-gray-400">
                <ShoppingBag size={40} className="mb-2 opacity-50" />
                <p className="text-sm">Busque y agregue productos al carrito</p>
              </div>
            ) : (
              <CarritoPanel />
            )}
          </Card>
        </div>

        {/* Columna derecha: resumen + pagar */}
        <div className="space-y-4">
          <Card>
            <h2 className="font-semibold text-gray-900 mb-4">Resumen de Venta</h2>
            <ResumenVenta />

            <div className="mt-6">
              <Button
                className="w-full py-3 text-base"
                onClick={() => setShowPagoModal(true)}
                disabled={items.length === 0}
              >
                <DollarSign size={20} className="mr-2" />
                Cobrar {formatCurrency(total)}
              </Button>
            </div>
          </Card>

          {/* Mini resumen del turno */}
          {resumen && (
            <Card className="!p-4">
              <h3 className="text-sm font-semibold text-gray-700 mb-2">
                Resumen del turno
              </h3>
              <div className="space-y-1 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-500">Ventas</span>
                  <span className="font-medium">{resumen.totalVentasCompletadas ?? resumen.cantidadVentas ?? 0}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Efectivo</span>
                  <span className="font-medium">{formatCurrency(resumen.totalEfectivo ?? 0)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Digital</span>
                  <span className="font-medium">
                    {formatCurrency(
                      (resumen.totalTarjeta ?? 0) +
                      (resumen.totalYape ?? 0) +
                      (resumen.totalPlin ?? 0) +
                      (resumen.totalOtros ?? 0),
                    )}
                  </span>
                </div>
              </div>
            </Card>
          )}
        </div>
      </div>

      {/* Modales */}
      <PagoModal
        isOpen={showPagoModal}
        onClose={() => setShowPagoModal(false)}
        onConfirm={handlePago}
        total={total}
        metodosPago={metodosPago}
        isLoading={isCreating}
      />

      <MovimientoCajaModal
        isOpen={showMovimientoModal}
        onClose={() => setShowMovimientoModal(false)}
        onConfirm={handleMovimiento}
        sesionCajaId={sesion?.id}
        isLoading={isRegistrando}
        categorias={categorias}
      />
    </div>
  );
};
