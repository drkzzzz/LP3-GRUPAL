import { useParams, useOutletContext, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ShoppingCart, ArrowLeft, Minus, Plus, Loader2 } from 'lucide-react';
import { useState } from 'react';
import { storefrontService } from '../services/storefrontService';
import { useCartStore } from '../stores/cartStore';
import { formatCurrency } from '@/shared/utils/formatters';
import toast from 'react-hot-toast';

export const StorefrontProductDetail = () => {
  const { productoId } = useParams();
  const { slug } = useOutletContext();
  const [quantity, setQuantity] = useState(1);
  const addItem = useCartStore((s) => s.addItem);

  const { data: product, isLoading } = useQuery({
    queryKey: ['storefront-producto', slug, productoId],
    queryFn: () => storefrontService.getProductoById(slug, productoId),
    enabled: !!slug && !!productoId,
  });

  const handleAddToCart = () => {
    if (product) {
      addItem(product, quantity);
      toast.success(`${quantity}x ${product.nombre} agregado al carrito`);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <Loader2 size={32} className="animate-spin text-amber-500" />
      </div>
    );
  }

  if (!product) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 text-center">
        <h2 className="text-xl font-bold text-gray-900 mb-2">Producto no encontrado</h2>
        <Link to={`/tienda/${slug}/catalogo`} className="text-amber-600 hover:text-amber-700">
          Volver al catálogo
        </Link>
      </div>
    );
  }

  const hasStock = product.stock == null || product.stock > 0;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Back link */}
      <Link
        to={`/tienda/${slug}/catalogo`}
        className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-6"
      >
        <ArrowLeft size={16} />
        Volver al catálogo
      </Link>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Image */}
        <div className="aspect-square bg-gray-100 rounded-2xl overflow-hidden relative">
          {product.urlImagen ? (
            <img
              src={product.urlImagen}
              alt={product.nombre}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-gray-300">
              <ShoppingCart size={80} />
            </div>
          )}
          {product.gradoAlcoholico > 0 && (
            <span className="absolute top-4 right-4 bg-slate-900/80 text-white text-sm px-3 py-1 rounded-full">
              {product.gradoAlcoholico}° Alc.
            </span>
          )}
        </div>

        {/* Details */}
        <div>
          {product.categoria?.nombre && (
            <span className="text-sm text-amber-600 font-medium uppercase tracking-wide">
              {product.categoria.nombre}
            </span>
          )}
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 mt-1">
            {product.nombre}
          </h1>
          {product.marca?.nombre && (
            <p className="text-gray-500 mt-1">{product.marca.nombre}</p>
          )}

          <div className="mt-6">
            <span className="text-3xl font-bold text-gray-900">
              {formatCurrency(product.precioVenta)}
            </span>
            {product.impuestoIncluido && (
              <span className="text-sm text-gray-500 ml-2">IGV incluido</span>
            )}
          </div>

          {product.descripcion && (
            <div className="mt-6">
              <h3 className="font-medium text-gray-900 mb-2">Descripción</h3>
              <p className="text-gray-600 text-sm leading-relaxed">{product.descripcion}</p>
            </div>
          )}

          {/* Specs */}
          <div className="mt-6 grid grid-cols-2 gap-3 text-sm">
            {product.sku && (
              <div className="bg-gray-50 rounded-lg p-3">
                <span className="text-gray-500">SKU</span>
                <p className="font-medium text-gray-900">{product.sku}</p>
              </div>
            )}
            {product.unidadMedida?.nombre && (
              <div className="bg-gray-50 rounded-lg p-3">
                <span className="text-gray-500">Presentación</span>
                <p className="font-medium text-gray-900">{product.unidadMedida.nombre}</p>
              </div>
            )}
          </div>

          {/* Add to cart */}
          <div className="mt-8 flex items-center gap-4">
            <div className="flex items-center border border-gray-300 rounded-xl">
              <button
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                className="p-3 hover:bg-gray-50 transition-colors rounded-l-xl"
                disabled={!hasStock}
              >
                <Minus size={16} />
              </button>
              <span className="px-5 font-medium text-gray-900 min-w-[48px] text-center">
                {quantity}
              </span>
              <button
                onClick={() => setQuantity(quantity + 1)}
                className="p-3 hover:bg-gray-50 transition-colors rounded-r-xl"
                disabled={!hasStock}
              >
                <Plus size={16} />
              </button>
            </div>
            <button
              onClick={handleAddToCart}
              disabled={!hasStock}
              className="flex-1 flex items-center justify-center gap-2 py-3 px-6 bg-amber-500 hover:bg-amber-600 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-semibold rounded-xl transition-colors"
            >
              <ShoppingCart size={20} />
              {hasStock ? 'Agregar al Carrito' : 'Agotado'}
            </button>
          </div>

          {!hasStock && (
            <p className="mt-3 text-sm text-red-600">
              Este producto no está disponible actualmente.
            </p>
          )}
        </div>
      </div>
    </div>
  );
};
