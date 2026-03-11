/**
 * cartStore.js
 * ────────────
 * Estado del carrito POS usando Zustand.
 * Soporta productos individuales y combos.
 * Persiste items y descuentos en sessionStorage para sobrevivir recargas accidentales.
 * El storage se aísla por negocio: cada negocio guarda/lee su propia clave.
 */
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { invalidarCacheProductos, buscarProductos } from '../services/productosAdapter';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/** Estado inicial limpio del carrito (para reseteos). */
const EMPTY_CART = {
  items: [],
  descuentoGlobal: 0,
  razonDescuento: '',
  stockDesactualizado: false,
  _lastCapped: null,
  suspendedCart: null,
  mesaContext: null,
};

/**
 * Devuelve el negocioId actual del auth store.
 */
const getNegocioId = () => useAdminAuthStore.getState().negocio?.id ?? null;

/**
 * Storage wrapper que usa una key dinámica basada en el negocioId actual.
 * Cada negocio tiene su propio carrito aislado en sessionStorage.
 */
const negocioScopedStorage = {
  getItem: (_name) => {
    const nid = getNegocioId();
    const key = nid ? `pos-cart-n${nid}` : 'pos-cart';
    return sessionStorage.getItem(key);
  },
  setItem: (_name, value) => {
    const nid = getNegocioId();
    const key = nid ? `pos-cart-n${nid}` : 'pos-cart';
    sessionStorage.setItem(key, value);
  },
  removeItem: (_name) => {
    const nid = getNegocioId();
    const key = nid ? `pos-cart-n${nid}` : 'pos-cart';
    sessionStorage.removeItem(key);
  },
};

export const useCartStore = create(
  persist(
    (set, get) => ({
  /* ═══ ESTADO ═══ */
  _negocioId: null,        // negocio al que pertenece el carrito actual
  items: [],               // [{ producto, cantidad, descuento }]
  descuentoGlobal: 0,      // monto fijo de descuento global
  razonDescuento: '',
  stockDesactualizado: false, // true cuando se detectó error de stock
  _lastCapped: null,         // productoId si la última actualización recortó cantidad por stock
  suspendedCart: null,        // { items, descuentoGlobal, razonDescuento }

  /* ═══ ACCIONES ═══ */

  addItem: (producto) => {
    const { items } = get();
    const existing = items.find((i) => i.producto.id === producto.id);

    if (existing) {
      const maxStock = producto.stock ?? 0;
      if (existing.cantidad >= maxStock) return;
      set({
        items: items.map((i) =>
          i.producto.id === producto.id
            ? { ...i, cantidad: i.cantidad + 1 }
            : i,
        ),
      });
    } else {
      if ((producto.stock ?? 0) <= 0) return;
      set({
        items: [...items, { producto, cantidad: 1, descuento: 0 }],
      });
    }
  },

  removeItem: (productoId) => {
    set({ items: get().items.filter((i) => i.producto.id !== productoId) });
  },

  updateQuantity: (productoId, cantidad) => {
    const qty = Math.floor(cantidad); // solo enteros
    if (qty < 1) return;
    const { items } = get();
    const item = items.find((i) => i.producto.id === productoId);
    if (!item) return;
    const maxStock = item.producto.stock ?? 0;
    const capped = Math.min(qty, maxStock);
    set({
      items: items.map((i) =>
        i.producto.id === productoId
          ? { ...i, cantidad: capped }
          : i,
      ),
      // Señalizar si se recortó la cantidad (objeto nuevo cada vez para forzar re-render)
      _lastCapped: capped < qty ? { productoId, _ts: Date.now() } : null,
    });
  },

  updateDescuento: (productoId, descuento) => {
    set({
      items: get().items.map((i) =>
        i.producto.id === productoId ? { ...i, descuento: Math.max(0, descuento) } : i,
      ),
    });
  },

  setDescuentoGlobal: (monto, razon = '') => {
    set({ descuentoGlobal: Math.max(0, monto), razonDescuento: razon });
  },

  clearCart: () => {
    set({ items: [], descuentoGlobal: 0, razonDescuento: '', stockDesactualizado: false });
  },

  /**
   * Sincroniza el carrito con el negocio actual.
   * Si el negocioId cambió, limpia el estado en memoria y rehidrata
   * desde el storage del nuevo negocio (que usa su propia clave).
   */
  syncNegocio: (negocioId) => {
    const currentNid = get()._negocioId;
    if (currentNid === negocioId) return; // mismo negocio, no hacer nada

    // Limpiar el estado en memoria (para que no quede data del negocio anterior)
    set({ ...EMPTY_CART, _negocioId: negocioId });

    // Rehidratar desde el sessionStorage del nuevo negocio
    useCartStore.persist.rehydrate();
  },

  /* ═══ CONTEXTO DE MESA ═══ */
  mesaContext: null, // { cuentaId, numeroCuenta, mesaNombre, detalleIds, closeOnSuccess }

  setMesaContext: (ctx) => set({ mesaContext: ctx }),
  clearMesaContext: () => set({ mesaContext: null }),

  /**
   * Carga los ítems de una cuenta de mesa en el carrito y
   * guarda el contexto de la mesa (para cerrarla/limpiar detalles después del pago).
   * @param {Array} detalles  - array de DetalleCuentaMesa
   * @param {Object} ctx      - { cuentaId, numeroCuenta, mesaNombre, detalleIds, closeOnSuccess }
   */
  loadFromMesa: (detalles, ctx) => {
    const items = detalles.map((d) => ({
      producto: {
        id: d.productoId ?? `mesa-item-${d.id}`,
        nombre: d.nombreProducto,
        precioVenta: Number(d.precioUnitario ?? 0),
        stock: 99999, // sin límite de stock en cobro de mesa
        _tipo: 'producto',
        _fromMesa: true,
        _detalleId: d.id,
      },
      cantidad: Number(d.cantidad ?? 1),
      descuento: 0,
    }));
    set({
      items,
      descuentoGlobal: 0,
      razonDescuento: '',
      stockDesactualizado: false,
      mesaContext: ctx ?? null,
    });
  },

  /**
   * Re-obtiene el stock real del backend y actualiza cada ítem del carrito.
   * Si la cantidad seleccionada supera el stock nuevo, se marca stockDesactualizado.
   */
  refreshStock: async () => {
    invalidarCacheProductos();
    const productosActualizados = await buscarProductos('');
    const { items } = get();
    let hayProblema = false;

    const nuevosItems = items.map((item) => {
      const fresh = productosActualizados.find((p) => p.id === item.producto.id);
      if (!fresh) return item;
      const nuevoStock = fresh.stock ?? 0;
      if (item.cantidad > nuevoStock) hayProblema = true;
      return {
        ...item,
        producto: { ...item.producto, stock: nuevoStock },
      };
    });

    set({ items: nuevosItems, stockDesactualizado: hayProblema });
    return hayProblema;
  },

  /** Limpia la bandera de stock desactualizado */
  clearStockWarning: () => set({ stockDesactualizado: false }),

  /* ═══ SUSPENDER / RECUPERAR ═══ */

  /** Suspende la venta actual: guarda ítems y descuentos, luego limpia el carrito. */
  suspendCart: () => {
    const { items, descuentoGlobal, razonDescuento } = get();
    if (items.length === 0) return;
    set({
      suspendedCart: {
        items: items.map((i) => ({ ...i, producto: { ...i.producto } })),
        descuentoGlobal,
        razonDescuento,
      },
      items: [],
      descuentoGlobal: 0,
      razonDescuento: '',
      stockDesactualizado: false,
    });
  },

  /** Recupera la venta suspendida: restaura ítems y descuentos al carrito activo. */
  recoverCart: () => {
    const { suspendedCart } = get();
    if (!suspendedCart) return;
    set({
      items: suspendedCart.items,
      descuentoGlobal: suspendedCart.descuentoGlobal,
      razonDescuento: suspendedCart.razonDescuento,
      suspendedCart: null,
      stockDesactualizado: false,
    });
  },

  /** Descarta la venta suspendida sin recuperarla. */
  discardSuspendedCart: () => {
    set({ suspendedCart: null });
  },

  /* ═══ SELECTORES COMPUTADOS ═══ */

  /** Subtotal = Σ (precioVenta × cantidad) */
  getSubtotal: () => {
    return get().items.reduce(
      (acc, i) => acc + i.producto.precioVenta * i.cantidad,
      0,
    );
  },

  /** Descuento por ítems = Σ descuento por línea */
  getDescuentoItems: () => {
    return get().items.reduce((acc, i) => acc + i.descuento, 0);
  },

  /** Total descuento = descuento ítems + descuento global */
  getTotalDescuento: () => {
    return get().getDescuentoItems() + get().descuentoGlobal;
  },

  /** Base imponible (subtotal - descuentos) */
  getBaseImponible: () => {
    return Math.max(0, get().getSubtotal() - get().getTotalDescuento());
  },

  /**
   * Configuración IGV del negocio.
   * Se establece desde POS.jsx con los datos del negocio.
   */
  aplicaIgv: true,
  porcentajeIgv: 18,

  setIgvConfig: (aplicaIgv, porcentajeIgv) => {
    set({
      aplicaIgv: aplicaIgv !== false,
      porcentajeIgv: (porcentajeIgv != null && porcentajeIgv >= 0) ? Number(porcentajeIgv) : 18,
    });
  },

  /**
   * Impuesto (IGV dinámico).
   * Si aplicaIgv es false → 0.
   * Si aplicaIgv es true → base × (porcentajeIgv / 100).
   *
   * Se multiplica por el porcentaje entero (ej. 18) y se divide
   * entre 100 al final para evitar el error de representación
   * de 0.18 en IEEE 754, que causa discrepancias de 1 céntimo
   * frente al backend (BigDecimal HALF_UP).
   */
  getImpuesto: () => {
    const { aplicaIgv, porcentajeIgv } = get();
    if (!aplicaIgv) return 0;
    const base = get().getBaseImponible();
    const pct = porcentajeIgv ?? 18;
    return Math.round(base * pct) / 100;
  },

  /** Total a pagar = base imponible + IGV */
  getTotal: () => {
    return +(get().getBaseImponible() + get().getImpuesto()).toFixed(2);
  },

  /** Total redondeado al 0.10 más cercano (para pagos 100% efectivo) */
  getTotalEfectivo: () => {
    const total = get().getTotal();
    return +(Math.round(total * 10) / 10).toFixed(2);
  },

  /** Cantidad total de unidades */
  getTotalItems: () => {
    return get().items.reduce((acc, i) => acc + i.cantidad, 0);
  },
}),
    {
      name: 'pos-cart',
      storage: createJSONStorage(() => negocioScopedStorage),
      partialize: (state) => ({
        _negocioId: state._negocioId,
        items: state.items,
        descuentoGlobal: state.descuentoGlobal,
        razonDescuento: state.razonDescuento,
        suspendedCart: state.suspendedCart,
      }),
      // Validar datos rehidratados: si la estructura es inválida, descartar
      merge: (persisted, current) => {
        if (
          !persisted ||
          typeof persisted !== 'object' ||
          !Array.isArray(persisted.items) ||
          persisted.items.some((i) => !i?.producto?.id || typeof i.cantidad !== 'number')
        ) {
          // datos ausentes o corruptos → mantener estado limpio (no arrastrar data vieja)
          return { ...current, items: [], descuentoGlobal: 0, razonDescuento: '', suspendedCart: null };
        }
        return { ...current, ...persisted };
      },
    },
  ),
);
