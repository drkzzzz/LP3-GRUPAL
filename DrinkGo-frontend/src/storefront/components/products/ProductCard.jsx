import { ShoppingCart } from 'lucide-react';
import { formatCurrency } from '@/shared/utils/formatters';
import { useCartStore } from '../../stores/cartStore';
import toast from 'react-hot-toast';

export const ProductCard = ({ product, slug }) => {
  const addItem = useCartStore((s) => s.addItem);

  const handleAddToCart = () => {
    addItem(product, 1);
    toast.success('Producto agregado al carrito');
  };

  const imageUrl = product.urlImagen || null;
  const hasStock = product.stock == null || product.stock > 0;

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm hover:shadow-md transition-shadow group">
      {/* Image */}
      <div className="aspect-square bg-gray-100 relative overflow-hidden">
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={product.nombre}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            loading="lazy"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-gray-300">
            <ShoppingCart size={48} />
          </div>
        )}
        {!hasStock && (
          <div className="absolute inset-0 bg-black/50 flex items-center justify-center">
            <span className="bg-red-600 text-white px-3 py-1 rounded-full text-sm font-medium">
              Agotado
            </span>
          </div>
        )}
        {product.gradoAlcoholico > 0 && (
          <span className="absolute top-2 right-2 bg-slate-900/80 text-white text-xs px-2 py-0.5 rounded-full">
            {product.gradoAlcoholico}° Alc.
          </span>
        )}
      </div>

      {/* Info */}
      <div className="p-4">
        {product.categoria?.nombre && (
          <span className="text-xs text-amber-600 font-medium uppercase tracking-wide">
            {product.categoria.nombre}
          </span>
        )}
        <h3 className="font-medium text-gray-900 mt-1 line-clamp-2 text-sm leading-tight">
          {product.nombre}
        </h3>
        {product.marca?.nombre && (
          <p className="text-xs text-gray-500 mt-0.5">{product.marca.nombre}</p>
        )}

        <div className="flex items-center justify-between mt-3">
          <span className="text-lg font-bold text-gray-900">
            {formatCurrency(product.precioVenta)}
          </span>
          <button
            onClick={handleAddToCart}
            disabled={!hasStock}
            className="p-2 rounded-lg bg-amber-500 text-white hover:bg-amber-600 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
            title="Agregar al carrito"
          >
            <ShoppingCart size={18} />
          </button>
        </div>
      </div>
    </div>
  );
};
