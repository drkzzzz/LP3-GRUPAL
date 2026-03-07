import { Link, useOutletContext } from 'react-router-dom';
import { Trash2, Minus, Plus, ShoppingBag, ArrowRight } from 'lucide-react';
import { useCartStore } from '../stores/cartStore';
import { formatCurrency } from '@/shared/utils/formatters';

export const StorefrontCart = () => {
  const { slug } = useOutletContext();
  const { items, updateQuantity, removeItem, clearCart, getTotal } = useCartStore();
  const total = getTotal();

  if (items.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 text-center">
        <ShoppingBag size={64} className="mx-auto mb-4 text-gray-300" />
        <h2 className="text-xl font-bold text-gray-900 mb-2">Tu carrito está vacío</h2>
        <p className="text-gray-500 mb-6">Agrega productos para comenzar tu pedido</p>
        <Link
          to={`/tienda/${slug}/catalogo`}
          className="inline-flex items-center gap-2 px-6 py-3 bg-amber-500 hover:bg-amber-600 text-white font-semibold rounded-xl transition-colors"
        >
          Explorar Catálogo
          <ArrowRight size={18} />
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Carrito de Compras</h1>
        <button
          onClick={clearCart}
          className="text-sm text-red-600 hover:text-red-700"
        >
          Vaciar carrito
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Items list */}
        <div className="lg:col-span-2 space-y-4">
          {items.map(({ product, quantity }) => (
            <div
              key={product.id}
              className="bg-white rounded-xl border border-gray-200 p-4 flex gap-4"
            >
              {/* Image */}
              <div className="w-20 h-20 rounded-lg bg-gray-100 overflow-hidden shrink-0">
                {product.urlImagen ? (
                  <img
                    src={product.urlImagen}
                    alt={product.nombre}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-gray-300">
                    <ShoppingBag size={24} />
                  </div>
                )}
              </div>

              {/* Info */}
              <div className="flex-1 min-w-0">
                <h3 className="font-medium text-gray-900 text-sm truncate">
                  {product.nombre}
                </h3>
                {product.categoria?.nombre && (
                  <p className="text-xs text-gray-500">{product.categoria.nombre}</p>
                )}
                <p className="text-amber-600 font-semibold mt-1">
                  {formatCurrency(product.precioVenta)}
                </p>
              </div>

              {/* Quantity + actions */}
              <div className="flex flex-col items-end justify-between">
                <button
                  onClick={() => removeItem(product.id)}
                  className="text-gray-400 hover:text-red-500 transition-colors"
                  title="Eliminar"
                >
                  <Trash2 size={16} />
                </button>
                <div className="flex items-center border border-gray-200 rounded-lg">
                  <button
                    onClick={() => updateQuantity(product.id, quantity - 1)}
                    className="p-1.5 hover:bg-gray-50 rounded-l-lg"
                  >
                    <Minus size={14} />
                  </button>
                  <span className="px-3 text-sm font-medium min-w-[32px] text-center">
                    {quantity}
                  </span>
                  <button
                    onClick={() => updateQuantity(product.id, quantity + 1)}
                    className="p-1.5 hover:bg-gray-50 rounded-r-lg"
                  >
                    <Plus size={14} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Summary */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-xl border border-gray-200 p-6 sticky top-24">
            <h3 className="font-semibold text-gray-900 mb-4">Resumen del Pedido</h3>

            <div className="space-y-2 text-sm border-b border-gray-200 pb-4 mb-4">
              <div className="flex justify-between text-gray-600">
                <span>Subtotal ({items.length} producto{items.length !== 1 ? 's' : ''})</span>
                <span>{formatCurrency(total)}</span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Delivery</span>
                <span className="text-gray-400">Por calcular</span>
              </div>
            </div>

            <div className="flex justify-between font-bold text-gray-900 text-lg mb-6">
              <span>Total</span>
              <span>{formatCurrency(total)}</span>
            </div>

            <Link
              to={`/tienda/${slug}/checkout`}
              className="block w-full text-center py-3 bg-amber-500 hover:bg-amber-600 text-white font-semibold rounded-xl transition-colors"
            >
              Continuar al Checkout
            </Link>

            <Link
              to={`/tienda/${slug}/catalogo`}
              className="block text-center text-sm text-amber-600 hover:text-amber-700 mt-3"
            >
              Seguir comprando
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
