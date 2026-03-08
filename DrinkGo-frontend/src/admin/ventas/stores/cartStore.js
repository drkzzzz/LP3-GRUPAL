/**
 * cartStore.js
 * ────────────
 * Estado del carrito POS usando Zustand.
 * Soporta productos individuales y combos.
 * Persiste items y descuentos en sessionStorage para sobrevivir recargas accidentales.
 */
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { invalidarCacheProductos, buscarProductos } from '../services/productosAdapter';

export const useCartStore = create(
  persist(
    (set, get) => ({
  /* ═══ ESTADO ═══ */
  items: [],               // [{ producto, cantidad, descuento }]
  descuentoGlobal: 0,      // monto fijo de descuento global
  razonDescuento: '',
  stockDesactualizado: false, // true cuando se detectó error de stock

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
    if (cantidad < 1) return;
    set({
      items: get().items.map((i) =>
        i.producto.id === productoId
          ? { ...i, cantidad: Math.min(cantidad, i.producto.stock ?? 0) }
          : i,
      ),
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

  /** Cantidad total de unidades */
  getTotalItems: () => {
    return get().items.reduce((acc, i) => acc + i.cantidad, 0);
  },
}),
    {
      name: 'pos-cart',
      storage: createJSONStorage(() => sessionStorage),
      partialize: (state) => ({
        items: state.items,
        descuentoGlobal: state.descuentoGlobal,
        razonDescuento: state.razonDescuento,
      }),
    },
  ),
);
