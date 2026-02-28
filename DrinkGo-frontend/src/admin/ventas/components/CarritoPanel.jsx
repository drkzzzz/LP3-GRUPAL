/**
 * CarritoPanel.jsx
 * ────────────────
 * Lista de ítems del carrito POS con edición inline.
 */
import { Trash2, Minus, Plus } from 'lucide-react';
import { useCartStore } from '../stores/cartStore';
import { formatCurrency } from '@/shared/utils/formatters';
import { Button } from '@/admin/components/ui/Button';

export const CarritoPanel = () => {
  const items = useCartStore((s) => s.items);
  const removeItem = useCartStore((s) => s.removeItem);
  const updateQuantity = useCartStore((s) => s.updateQuantity);

  if (items.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-12 text-gray-400">
        <p className="text-sm">El carrito está vacío</p>
        <p className="text-xs mt-1">Busca y agrega productos arriba</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col">
      {/* Header */}
      <div className="grid grid-cols-12 gap-2 px-3 py-2 text-xs font-semibold text-gray-500 uppercase border-b border-gray-200">
        <span className="col-span-5">Producto</span>
        <span className="col-span-2 text-center">Precio</span>
        <span className="col-span-3 text-center">Cant.</span>
        <span className="col-span-1 text-right">Total</span>
        <span className="col-span-1" />
      </div>

      {/* Items */}
      <div className="divide-y divide-gray-100 max-h-[400px] overflow-y-auto">
        {items.map(({ producto, cantidad }) => {
          const lineTotal = producto.precioVenta * cantidad;
          return (
            <div
              key={producto.id}
              className="grid grid-cols-12 gap-2 px-3 py-2.5 items-center hover:bg-gray-50 transition-colors"
            >
              {/* Nombre */}
              <div className="col-span-5 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {producto.nombre}
                </p>
                <p className="text-xs text-gray-400">{producto.sku}</p>
              </div>

              {/* Precio unitario */}
              <div className="col-span-2 text-center text-sm text-gray-600">
                {formatCurrency(producto.precioVenta)}
              </div>

              {/* Cantidad */}
              <div className="col-span-3 flex items-center justify-center gap-1">
                <Button
                  variant="outline"
                  size="icon"
                  className="w-7 h-7 p-0"
                  onClick={() => updateQuantity(producto.id, cantidad - 1)}
                  disabled={cantidad <= 1}
                >
                  <Minus size={14} />
                </Button>
                <input
                  type="number"
                  min={1}
                  max={producto.stock}
                  value={cantidad}
                  onChange={(e) =>
                    updateQuantity(producto.id, parseInt(e.target.value) || 1)
                  }
                  className="w-12 text-center border border-gray-300 rounded text-sm py-0.5
                             focus:outline-none focus:ring-1 focus:ring-green-500"
                />
                <Button
                  variant="outline"
                  size="icon"
                  className="w-7 h-7 p-0"
                  onClick={() => updateQuantity(producto.id, cantidad + 1)}
                  disabled={cantidad >= producto.stock}
                >
                  <Plus size={14} />
                </Button>
              </div>

              {/* Total línea */}
              <div className="col-span-1 text-right text-sm font-semibold text-gray-900">
                {formatCurrency(lineTotal)}
              </div>

              {/* Eliminar */}
              <div className="col-span-1 flex justify-end">
                <button
                  onClick={() => removeItem(producto.id)}
                  className="p-1 text-red-400 hover:text-red-600 transition-colors"
                  title="Quitar"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
