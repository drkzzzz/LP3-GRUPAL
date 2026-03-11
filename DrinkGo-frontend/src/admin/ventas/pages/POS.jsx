/**
 * POS.jsx
 * ───────
 * Página principal del Punto de Venta.
 * Si no hay turno abierto redirige a Cajas para aperturar.
 */
import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Banknote, Power, ArrowDownUp, ShoppingBag, AlertCircle, LogIn, Settings, FileText, Pause, Play, X } from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { ProductoSearch } from '../components/ProductoSearch';
import { CarritoPanel } from '../components/CarritoPanel';
import { ResumenVenta } from '../components/ResumenVenta';
import { PagoModal } from '../components/PagoModal';
import { MovimientoCajaModal } from '../components/MovimientoCajaModal';
import { ComprobanteVenta } from '../components/ComprobanteVenta';
import { useCartStore } from '../stores/cartStore';
import { useSesionActiva, useResumenTurno, useMovimientos, useCategoriasGasto } from '../hooks/useCajas';
import { useCrearVenta, useMetodosPago } from '../hooks/useVentas';
import { useSeries } from '@/admin/facturacion/hooks/useFacturacion';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { SinCajaAsignada } from '../components/SinCajaAsignada';
import { cuentasMesaService } from '../services/cuentasMesaService';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';

export const POS = () => {
  const navigate = useNavigate();
  const { user, negocio, getAlcance, cajaAsignada } = useAdminAuthStore();
  const esSoloCaja = getAlcance('m.ventas.pos') === 'caja_asignada';

  const { sesion, hasSesion, isLoading: loadingSesion } = useSesionActiva();
  const { resumen } = useResumenTurno(sesion?.id);
  const { registrar: registrarMovimiento, isRegistrando } = useMovimientos(sesion?.id);
  const { categorias } = useCategoriasGasto();
  const { crearVenta, isCreating } = useCrearVenta();
  const { metodosPago, isLoading: loadingMetodos } = useMetodosPago();
  const { data: series = [], isLoading: loadingSeries } = useSeries(negocio?.id);

  const items = useCartStore((s) => s.items);
  const getTotal = useCartStore((s) => s.getTotal);
  const clearCart = useCartStore((s) => s.clearCart);
  const descuentoGlobal = useCartStore((s) => s.descuentoGlobal);
  const razonDescuento = useCartStore((s) => s.razonDescuento);
  const setIgvConfig = useCartStore((s) => s.setIgvConfig);
  const suspendedCart = useCartStore((s) => s.suspendedCart);
  const suspendCart = useCartStore((s) => s.suspendCart);
  const recoverCart = useCartStore((s) => s.recoverCart);
  const discardSuspendedCart = useCartStore((s) => s.discardSuspendedCart);
  const mesaContext = useCartStore((s) => s.mesaContext);
  const clearMesaContext = useCartStore((s) => s.clearMesaContext);

  /* ─── Sincronizar carrito con el negocio actual ─── */
  const syncNegocio = useCartStore((s) => s.syncNegocio);
  useEffect(() => {
    if (negocio?.id) {
      syncNegocio(negocio.id);
    }
  }, [negocio?.id, syncNegocio]);

  /* ─── Sincronizar config IGV del negocio al carrito ─── */
  useEffect(() => {
    if (negocio) {
      setIgvConfig(negocio.aplicaIgv, negocio.porcentajeIgv);
    }
  }, [negocio, setIgvConfig]);

  const [showPagoModal, setShowPagoModal] = useState(false);
  const [showMovimientoModal, setShowMovimientoModal] = useState(false);
  const [showDiscardModal, setShowDiscardModal] = useState(false);

  /* ─── Estado del comprobante ─── */
  const [receiptData, setReceiptData] = useState(null);

  /* ─── Valores derivados (antes de early returns para no romper hooks) ─── */
  const faltaMetodosPago = !loadingMetodos && metodosPago.length === 0;
  const faltaSeries = !loadingSeries && series.length === 0;
  const faltaConfiguracion = faltaMetodosPago || faltaSeries;
  const total = getTotal();

  /* ─── Atajos de teclado ─── */
  const handleKeyDown = useCallback((e) => {
    // ESC → cerrar modales
    if (e.key === 'Escape') {
      if (receiptData) { setReceiptData(null); return; }
      if (showPagoModal) { setShowPagoModal(false); return; }
      if (showMovimientoModal) { setShowMovimientoModal(false); return; }
    }
  }, [receiptData, showPagoModal, showMovimientoModal]);

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [handleKeyDown]);

  /* Si es cajero con alcance caja_asignada pero sin caja asignada -> bloquear */
  if (esSoloCaja && !cajaAsignada) {
    return <SinCajaAsignada titulo="Punto de Venta" />;
  }

  /* ─── Registrar movimiento ─── */
  const handleMovimiento = async (data) => {
    await registrarMovimiento(data);
    setShowMovimientoModal(false);
  };

  /* ─── Suspender / Recuperar venta ─── */
  const handleSuspend = () => {
    suspendCart();
  };

  const handleRecover = () => {
    recoverCart();
  };

  const handleDiscard = () => {
    discardSuspendedCart();
    setShowDiscardModal(false);
  };

  /* ─── Procesar venta ─── */
  const handlePago = async ({ pagos, tipoComprobante, docClienteNumero, docClienteNombre, docClienteDireccion }) => {
    /* Snapshot de los ítems ANTES de limpiar el carrito */
    const itemsSnapshot = items.map((i) => ({ ...i, producto: { ...i.producto } }));

    /* Construir ítems expandiendo combos en productos componentes */
    const ventaItems = [];
    for (const i of items) {
      if (i.producto._tipo === 'combo' && i.producto._productosCombo?.length) {
        const comps = i.producto._productosCombo;
        const comboId = i.producto._comboId;
        const comboPrice = i.producto.precioVenta;
        const comboQty = i.cantidad;
        const lineDiscount = i.descuento || 0;

        /* Sumatoria de precios regulares de los componentes */
        const regularSum = comps.reduce((s, c) => s + c.precioVenta * c.cantidad, 0);

        /* Descuento del combo = diferencia entre precios catálogo y precio combo */
        const comboDiscountTotal = +(regularSum - comboPrice).toFixed(2) * comboQty;

        /* Distribuir descuento proporcionalmente — último absorbe residual */
        let cumComboDisc = 0;
        let cumLineDisc = 0;
        comps.forEach((comp, idx) => {
          const isLast = idx === comps.length - 1;
          const proportion = regularSum > 0
            ? (comp.precioVenta * comp.cantidad) / regularSum
            : 1 / comps.length;

          const realQty = comp.cantidad * comboQty;

          /* Descuento combo proporcional */
          const compComboDisc = isLast
            ? +(comboDiscountTotal - cumComboDisc).toFixed(2)
            : +(comboDiscountTotal * proportion).toFixed(2);
          if (!isLast) cumComboDisc += compComboDisc;

          /* Descuento manual de línea proporcional */
          const compLineDisc = isLast
            ? +(lineDiscount * comboQty - cumLineDisc).toFixed(2)
            : +(lineDiscount * proportion * comboQty).toFixed(2);
          if (!isLast) cumLineDisc += compLineDisc;

          ventaItems.push({
            productoId: comp.productoId,
            comboId,
            nombreCombo: i.producto.nombre,
            nombreProducto: comp.nombre,
            precioUnitario: comp.precioVenta,
            cantidad: realQty,
            descuento: +(compComboDisc + compLineDisc).toFixed(2),
          });
        });
      } else {
        ventaItems.push({
          productoId: i.producto.id,
          nombreProducto: i.producto.nombre,
          precioUnitario: i.producto.precioVenta,
          cantidad: i.cantidad,
          descuento: i.descuento || 0,
        });
      }
    }

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
      docClienteDireccion: docClienteDireccion || null,
      items: ventaItems,
      pagos,
    };

    try {
      const ventaResponse = await crearVenta(ventaData);

      /* Guardar datos para el comprobante */
      setReceiptData({
        venta: ventaResponse,
        items: itemsSnapshot,
        pagos,
      });

      setShowPagoModal(false);
      clearCart();

      /* ─ Post-venta: si viene de una mesa, remover detalles y/o cerrar cuenta ─ */
      if (mesaContext) {
        const ctx = mesaContext;
        clearMesaContext();
        try {
          for (const id of (ctx.detalleIds ?? [])) {
            await cuentasMesaService.removerProducto(id);
          }
          if (ctx.closeOnSuccess) {
            await cuentasMesaService.cerrarCuenta(ctx.cuentaId, user?.id);
          }
        } catch { /* best-effort: venta ya confirmada */ }
      }
    } catch (err) {
      // Cerrar modal de pago para que el cajero vea el carrito actualizado.
      setShowPagoModal(false);
    }
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

  return (
    <div className="space-y-4">
      {/* Banner de mesa activa */}
      {mesaContext && (
        <div className="flex items-center justify-between bg-green-50 border border-green-200 rounded-lg px-4 py-2 text-sm">
          <span className="text-green-800 font-medium">
            🍽️ Cobrando {mesaContext.mesaNombre} — {mesaContext.numeroCuenta}
            {!mesaContext.closeOnSuccess && ' (cobro parcial)'}
          </span>
          <button
            onClick={() => { clearMesaContext(); navigate('/admin/ventas/mesas'); }}
            className="text-xs text-green-600 hover:text-green-800 underline"
          >
            Volver a mesas
          </button>
        </div>
      )}

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

      {/* ─── Alerta de configuración pendiente ─── */}
      {faltaConfiguracion && (
        <div className="bg-amber-50 border border-amber-300 rounded-xl p-5 shadow-sm">
          <div className="flex items-start gap-3">
            <div className="w-10 h-10 bg-amber-100 rounded-full flex items-center justify-center shrink-0 mt-0.5">
              <AlertCircle size={22} className="text-amber-600" />
            </div>
            <div className="flex-1">
              <h3 className="text-base font-semibold text-amber-800 mb-1">
                Configuración pendiente para realizar ventas
              </h3>
              <p className="text-sm text-amber-700 mb-3">
                Antes de poder registrar ventas, necesitas configurar los siguientes elementos:
              </p>
              <div className="space-y-2">
                {faltaMetodosPago && (
                  <div className="flex items-center gap-2 text-sm text-amber-800">
                    <Settings size={16} className="text-amber-600 shrink-0" />
                    <span>
                      <strong>Métodos de Pago:</strong> No hay métodos de pago configurados.
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => navigate('/admin/configuracion')}
                      className="ml-auto text-xs shrink-0"
                    >
                      Ir a Configuración
                    </Button>
                  </div>
                )}
                {faltaSeries && (
                  <div className="flex items-center gap-2 text-sm text-amber-800">
                    <FileText size={16} className="text-amber-600 shrink-0" />
                    <span>
                      <strong>Series de Comprobantes:</strong> No hay series configuradas para emitir comprobantes.
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => navigate('/admin/facturacion/series')}
                      className="ml-auto text-xs shrink-0"
                    >
                      Ir a Facturación
                    </Button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

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

          {/* Panel de venta suspendida (debajo del carrito) */}
          {suspendedCart && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl px-5 py-4 flex items-center justify-between shadow-sm">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-amber-100 rounded-full flex items-center justify-center">
                  <Pause size={20} className="text-amber-600" />
                </div>
                <div>
                  <p className="font-semibold text-amber-800">Venta suspendida</p>
                  <p className="text-sm text-amber-600">
                    {suspendedCart.items.length} {suspendedCart.items.length === 1 ? 'producto' : 'productos'}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleRecover}
                  className="border-amber-300 text-amber-700 hover:bg-amber-100"
                >
                  <Play size={16} className="mr-1" />
                  Recuperar
                </Button>
                <button
                  onClick={() => setShowDiscardModal(true)}
                  className="p-1.5 text-amber-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  title="Descartar venta suspendida"
                >
                  <X size={18} />
                </button>
              </div>
            </div>
          )}
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
                disabled={items.length === 0 || faltaConfiguracion || isCreating}
                title={faltaConfiguracion ? 'Debe configurar métodos de pago y series de comprobantes antes de vender' : undefined}
              >
                <Banknote size={20} className="mr-2" />
                Cobrar {formatCurrency(total)}
              </Button>
              {faltaConfiguracion && items.length > 0 && (
                <p className="text-xs text-amber-600 mt-2 text-center flex items-center justify-center gap-1">
                  <AlertCircle size={12} />
                  Configure métodos de pago y series para poder cobrar
                </p>
              )}
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
        onSuspend={!suspendedCart ? handleSuspend : undefined}
        total={total}
        metodosPago={metodosPago}
        series={series}
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

      {/* Comprobante de venta */}
      {receiptData && (
        <ComprobanteVenta
          venta={receiptData.venta}
          items={receiptData.items}
          pagos={receiptData.pagos}
          negocio={negocio}
          sede={sesion?.caja?.sede}
          metodosPago={metodosPago}
          onClose={() => setReceiptData(null)}
        />
      )}

      {/* Modal de confirmación para descartar venta suspendida */}
      <Modal
        isOpen={showDiscardModal}
        onClose={() => setShowDiscardModal(false)}
        title="Descartar venta suspendida"
        size="sm"
        footer={
          <>
            <Button variant="outline" onClick={() => setShowDiscardModal(false)}>
              Cancelar
            </Button>
            <Button
              className="bg-red-600 hover:bg-red-700 text-white"
              onClick={handleDiscard}
            >
              Sí, descartar
            </Button>
          </>
        }
      >
        <div className="space-y-3">
          <p className="text-gray-700">
            Se eliminará el carrito suspendido con{' '}
            <strong>
              {suspendedCart?.items.length}{' '}
              {suspendedCart?.items.length === 1 ? 'producto' : 'productos'}
            </strong>.
          </p>
          <p className="text-sm text-gray-500">
            Esta acción no se puede deshacer.
          </p>
        </div>
      </Modal>
    </div>
  );
};
