import { useState, useMemo } from 'react';
import { useOutletContext, useSearchParams } from 'react-router-dom';
import { Search, SlidersHorizontal, ShoppingBag } from 'lucide-react';
import { useStorefrontProducts } from '../hooks/useStorefrontProducts';
import { useStorefrontCategories } from '../hooks/useStorefrontCategories';
import { ProductCard } from '../components/products/ProductCard';
import { CategoryFilter } from '../components/products/CategoryFilter';

export const StorefrontCatalog = () => {
  const { slug, selectedSede } = useOutletContext();
  const [searchParams, setSearchParams] = useSearchParams();

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategoriaId, setSelectedCategoriaId] = useState(
    searchParams.get('categoriaId') ? Number(searchParams.get('categoriaId')) : null,
  );

  const { productos, isLoading } = useStorefrontProducts(slug, {
    sedeId: selectedSede?.id,
    categoriaId: selectedCategoriaId,
    search: searchTerm || undefined,
  });

  const { categorias } = useStorefrontCategories(slug);

  const handleCategoryChange = (catId) => {
    setSelectedCategoriaId(catId);
    if (catId) {
      setSearchParams({ categoriaId: catId });
    } else {
      setSearchParams({});
    }
  };

  const filteredProducts = useMemo(() => {
    if (!searchTerm.trim()) return productos;
    const term = searchTerm.toLowerCase();
    return productos.filter(
      (p) =>
        p.nombre?.toLowerCase().includes(term) ||
        p.sku?.toLowerCase().includes(term) ||
        p.marca?.nombre?.toLowerCase().includes(term),
    );
  }, [productos, searchTerm]);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Catálogo</h1>
        <p className="text-gray-600 mt-1">
          Explora nuestra selección de productos
          {selectedSede && <span className="text-amber-600"> — {selectedSede.nombre}</span>}
        </p>
      </div>

      {/* Search */}
      <div className="mb-6">
        <div className="relative max-w-md">
          <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Buscar productos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-300 focus:border-amber-500 focus:ring-2 focus:ring-amber-200 outline-none text-sm"
          />
        </div>
      </div>

      {/* Category filter */}
      {categorias.length > 0 && (
        <div className="mb-6">
          <CategoryFilter
            categorias={categorias}
            selectedId={selectedCategoriaId}
            onSelect={handleCategoryChange}
          />
        </div>
      )}

      {/* Products grid */}
      {isLoading ? (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {[...Array(8)].map((_, i) => (
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
      ) : filteredProducts.length > 0 ? (
        <>
          <p className="text-sm text-gray-500 mb-4">
            {filteredProducts.length} producto{filteredProducts.length !== 1 ? 's' : ''} encontrado{filteredProducts.length !== 1 ? 's' : ''}
          </p>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {filteredProducts.map((product) => (
              <ProductCard key={product.id} product={product} slug={slug} />
            ))}
          </div>
        </>
      ) : (
        <div className="text-center py-16">
          <ShoppingBag size={64} className="mx-auto mb-4 text-gray-300" />
          <h3 className="text-lg font-medium text-gray-900 mb-1">No se encontraron productos</h3>
          <p className="text-gray-500 text-sm">
            {searchTerm
              ? 'Intenta con otro término de búsqueda'
              : 'No hay productos disponibles en esta categoría'}
          </p>
        </div>
      )}
    </div>
  );
};
