/**
 * ProductoSearch.jsx
 * ──────────────────
 * Buscador de productos para el POS.
 * Usa productosAdapter (mock o real según USE_MOCK).
 */
import { useState, useRef, useEffect, useCallback } from 'react';
import { Search, Plus, Package } from 'lucide-react';
import { buscarProductos } from '../services/productosAdapter';
import { useCartStore } from '../stores/cartStore';
import { formatCurrency } from '@/shared/utils/formatters';
import { useDebounce } from '@/shared/hooks/useDebounce';

export const ProductoSearch = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isSearching, setIsSearching] = useState(false);
  const debouncedQuery = useDebounce(query, 300);
  const wrapperRef = useRef(null);
  const inputRef = useRef(null);
  const addItem = useCartStore((s) => s.addItem);

  /* Buscar productos al cambiar el query */
  useEffect(() => {
    const doSearch = async () => {
      if (!debouncedQuery || debouncedQuery.length < 1) {
        setResults([]);
        setIsOpen(false);
        return;
      }
      setIsSearching(true);
      try {
        const data = await buscarProductos(debouncedQuery);
        setResults(data);
        setIsOpen(data.length > 0);
      } catch {
        setResults([]);
      } finally {
        setIsSearching(false);
      }
    };
    doSearch();
  }, [debouncedQuery]);

  /* Cerrar dropdown al hacer click fuera */
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = useCallback(
    (producto) => {
      addItem(producto);
      setQuery('');
      setResults([]);
      setIsOpen(false);
      inputRef.current?.focus();
    },
    [addItem],
  );

  return (
    <div ref={wrapperRef} className="relative w-full">
      {/* Input de búsqueda */}
      <div className="relative">
        <Search
          size={18}
          className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
        />
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Buscar producto por nombre, SKU o categoría..."
          className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg text-sm
                     focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
          autoComplete="off"
        />
        {isSearching && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2">
            <div className="w-4 h-4 border-2 border-green-500 border-t-transparent rounded-full animate-spin" />
          </div>
        )}
      </div>

      {/* Dropdown de resultados */}
      {isOpen && (
        <div className="absolute z-30 top-full mt-1 w-full bg-white border border-gray-200 rounded-lg shadow-lg max-h-80 overflow-y-auto">
          {results.map((p) => (
            <button
              key={p.id}
              onClick={() => handleSelect(p)}
              className="w-full flex items-center gap-3 px-4 py-3 hover:bg-green-50 transition-colors text-left border-b border-gray-100 last:border-b-0"
            >
              <div className="p-2 bg-gray-100 rounded-lg shrink-0">
                <Package size={18} className="text-gray-500" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {p.nombre}
                </p>
                <p className="text-xs text-gray-500">
                  {p.sku} · {p.categoria?.nombre || '—'} · Stock: {p.stock}
                </p>
              </div>
              <div className="text-right shrink-0">
                <p className="text-sm font-bold text-green-700">
                  {formatCurrency(p.precioVenta)}
                </p>
              </div>
              <Plus size={18} className="text-green-600 shrink-0" />
            </button>
          ))}
        </div>
      )}
    </div>
  );
};
