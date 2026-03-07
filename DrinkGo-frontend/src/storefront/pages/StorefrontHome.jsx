import { Link, useOutletContext } from 'react-router-dom';
import { ArrowRight, ShoppingBag, Truck, Clock } from 'lucide-react';
import { useStorefrontProducts } from '../hooks/useStorefrontProducts';
import { useStorefrontCategories } from '../hooks/useStorefrontCategories';
import { ProductCard } from '../components/products/ProductCard';
import { SedeSelector } from '../components/layout/SedeSelector';

export const StorefrontHome = () => {
  const { slug, config, sedes, selectedSede, setSelectedSede } = useOutletContext();

  const { productos, isLoading } = useStorefrontProducts(slug, {
    sedeId: selectedSede?.id,
    limit: 8,
  });

  const { categorias } = useStorefrontCategories(slug);

  const storeName = config?.nombreTienda || 'Tienda';

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 sm:py-24">
          <div className="text-center max-w-2xl mx-auto">
            <h1 className="text-3xl sm:text-5xl font-bold mb-4">
              Bienvenido a{' '}
              <span className="text-amber-400">{storeName}</span>
            </h1>
            <p className="text-gray-300 text-lg mb-8">
              Encuentra los mejores productos con la mejor calidad. Haz tu pedido
              en línea y recíbelo donde quieras.
            </p>
            <Link
              to={`/tienda/${slug}/catalogo`}
              className="inline-flex items-center gap-2 px-6 py-3 bg-amber-500 hover:bg-amber-600 text-white font-semibold rounded-xl transition-colors"
            >
              Ver Catálogo
              <ArrowRight size={20} />
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-8">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5 flex items-center gap-4">
            <div className="p-3 rounded-lg bg-amber-100 text-amber-600">
              <Truck size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-gray-900 text-sm">Delivery</h3>
              <p className="text-xs text-gray-500">Envío a tu zona</p>
            </div>
          </div>
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5 flex items-center gap-4">
            <div className="p-3 rounded-lg bg-green-100 text-green-600">
              <ShoppingBag size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-gray-900 text-sm">Recojo en Tienda</h3>
              <p className="text-xs text-gray-500">Retira tu pedido</p>
            </div>
          </div>
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5 flex items-center gap-4">
            <div className="p-3 rounded-lg bg-blue-100 text-blue-600">
              <Clock size={24} />
            </div>
            <div>
              <h3 className="font-semibold text-gray-900 text-sm">Rápido y Seguro</h3>
              <p className="text-xs text-gray-500">Compra con confianza</p>
            </div>
          </div>
        </div>
      </section>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-10">
        {/* Sede selector */}
        {sedes.length > 1 && (
          <SedeSelector
            sedes={sedes}
            selectedSede={selectedSede}
            onSedeChange={setSelectedSede}
          />
        )}

        {/* Categories */}
        {categorias.length > 0 && (
          <section>
            <h2 className="text-xl font-bold text-gray-900 mb-4">Categorías</h2>
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
              {categorias.slice(0, 8).map((cat) => (
                <Link
                  key={cat.id}
                  to={`/tienda/${slug}/catalogo?categoriaId=${cat.id}`}
                  className="bg-white border border-gray-200 rounded-xl p-4 text-center hover:border-amber-400 hover:shadow-sm transition-all"
                >
                  <h3 className="font-medium text-gray-900 text-sm">{cat.nombre}</h3>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Featured products */}
        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-gray-900">Productos Destacados</h2>
            <Link
              to={`/tienda/${slug}/catalogo`}
              className="text-amber-600 hover:text-amber-700 text-sm font-medium flex items-center gap-1"
            >
              Ver todos <ArrowRight size={16} />
            </Link>
          </div>
          {isLoading ? (
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="bg-white rounded-xl border border-gray-200 animate-pulse">
                  <div className="aspect-square bg-gray-200 rounded-t-xl" />
                  <div className="p-4 space-y-3">
                    <div className="h-3 bg-gray-200 rounded w-1/3" />
                    <div className="h-4 bg-gray-200 rounded w-3/4" />
                    <div className="h-5 bg-gray-200 rounded w-1/2" />
                  </div>
                </div>
              ))}
            </div>
          ) : productos.length > 0 ? (
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
              {productos.slice(0, 8).map((product) => (
                <ProductCard key={product.id} product={product} slug={slug} />
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-gray-500">
              <ShoppingBag size={48} className="mx-auto mb-3 text-gray-300" />
              <p>No hay productos disponibles en esta sede</p>
            </div>
          )}
        </section>
      </div>
    </div>
  );
};
