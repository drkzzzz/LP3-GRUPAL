/**
 * cartStore.js
 * ────────────
 * Estado del carrito POS usando Zustand.
 * NO persiste en localStorage (se limpia al recargar).
 */
import { create } from 'zustand';

export const useCartStore = create((set, get) => ({
  /* ═══ ESTADO ═══ */
  items: [],               // [{ producto, cantidad, descuento }]
  descuentoGlobal: 0,      // monto fijo de descuento global
  razonDescuento: '',

  /* ═══ ACCIONES ═══ */

  addItem: (producto) => {
    const { items } = get();
    const existing = items.find((i) => i.producto.id === producto.id);

    if (existing) {
      if (existing.cantidad >= producto.stock) return; // sin stock
      set({
        items: items.map((i) =>
          i.producto.id === producto.id
            ? { ...i, cantidad: i.cantidad + 1 }
            : i,
        ),
      });
    } else {
      if (producto.stock <= 0) return;
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
          ? { ...i, cantidad: Math.min(cantidad, i.producto.stock) }
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
    set({ items: [], descuentoGlobal: 0, razonDescuento: '' });
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
   * Impuesto (IGV 18%).
   * Se calcula SOBRE la base imponible → IGV = base × 0.18
   * El total a pagar será base + IGV.
   */
  getImpuesto: () => {
    const base = get().getBaseImponible();
    return +(base * 0.18).toFixed(2);
  },

  /** Total a pagar = base imponible + IGV */
  getTotal: () => {
    return +(get().getBaseImponible() + get().getImpuesto()).toFixed(2);
  },

  /** Cantidad total de unidades */
  getTotalItems: () => {
    return get().items.reduce((acc, i) => acc + i.cantidad, 0);
  },
}));
