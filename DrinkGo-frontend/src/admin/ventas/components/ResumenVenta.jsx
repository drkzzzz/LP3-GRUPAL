/**
 * ResumenVenta.jsx
 * ────────────────
 * Resumen del carrito: subtotal, descuentos, impuesto, total.
 */
import { useCartStore } from '../stores/cartStore';
import { formatCurrency } from '@/shared/utils/formatters';

export const ResumenVenta = () => {
  const items = useCartStore((s) => s.items);
  const getSubtotal = useCartStore((s) => s.getSubtotal);
  const getTotalDescuento = useCartStore((s) => s.getTotalDescuento);
  const getImpuesto = useCartStore((s) => s.getImpuesto);
  const getTotal = useCartStore((s) => s.getTotal);
  const getTotalItems = useCartStore((s) => s.getTotalItems);

  const subtotal = getSubtotal();
  const descuento = getTotalDescuento();
  const impuesto = getImpuesto();
  const total = getTotal();
  const totalItems = getTotalItems();

  return (
    <div className="space-y-2 px-1">
      {/* Líneas de resumen */}
      <div className="flex justify-between text-sm text-gray-600">
        <span>Artículos ({totalItems})</span>
        <span>{formatCurrency(subtotal)}</span>
      </div>

      {descuento > 0 && (
        <div className="flex justify-between text-sm text-red-600">
          <span>Descuento</span>
          <span>-{formatCurrency(descuento)}</span>
        </div>
      )}

      <div className="flex justify-between text-xs text-gray-400">
        <span>IGV (18%)</span>
        <span>{formatCurrency(impuesto)}</span>
      </div>

      {/* Separador */}
      <div className="border-t border-gray-300 pt-2">
        <div className="flex justify-between text-lg font-bold text-gray-900">
          <span>TOTAL</span>
          <span className="text-green-700">{formatCurrency(total)}</span>
        </div>
      </div>
    </div>
  );
};
