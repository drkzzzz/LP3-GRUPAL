/**
 * CuentaDetailPanel.jsx
 * ─────────────────────
 * RF-VTA-012 / 013: Panel lateral de gestión de una cuenta abierta.
 * - Buscar y agregar productos
 * - Remover productos (soft delete)
 * - Acciones: Cerrar cuenta, Transferir
 */
import { useState, useEffect, useRef, useCallback } from 'react';
import { Search, Trash2, X, CreditCard, ArrowRightLeft, Clock, Users } from 'lucide-react';
import { useCuentaDetalle, useCuentasMesaMutations } from '../hooks/useCuentasMesa';
import { buscarProductos } from '../services/productosAdapter';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Button } from '@/admin/components/ui/Button';
import { formatCurrency } from '@/shared/utils/formatters';

export const CuentaDetailPanel = ({ cuenta, sedeId, onClose, onCerrar, onTransferir }) => {
  const { detalles, isLoading } = useCuentaDetalle(cuenta?.id);
  const { agregarProducto, removerProducto } = useCuentasMesaMutations(sedeId);

  /* ── Búsqueda de productos ── */
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const debouncedQuery = useDebounce(query, 300);
  const wrapperRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    const doSearch = async () => {
      if (!debouncedQuery || debouncedQuery.length < 1) {
        setResults([]);
        setIsDropdownOpen(false);
        return;
      }
      setIsSearching(true);
      try {
        const data = await buscarProductos(debouncedQuery);
        setResults(data);
        setIsDropdownOpen(data.length > 0);
      } catch {
        setResults([]);
      } finally {
        setIsSearching(false);
      }
    };
    doSearch();
  }, [debouncedQuery]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelectProduct = useCallback(
    (producto) => {
      const esCombo = producto._tipo === 'combo';
      agregarProducto.mutate({
        cuentaId: cuenta.id,
        producto: {
          productoId: esCombo ? null : producto.id,
          nombreProductoSnapshot: esCombo ? producto.nombre : null,
          cantidad: 1,
          precioUnitario: producto.precioVenta ?? producto.precio ?? 0,
        },
      });
      setQuery('');
      setResults([]);
      setIsDropdownOpen(false);
      inputRef.current?.focus();
    },
    [agregarProducto, cuenta?.id],
  );

  const handleRemove = (detalleId) => {
    removerProducto.mutate({ detalleId, cuentaId: cuenta.id });
  };

  if (!cuenta) return null;

  return (
    <div className="flex flex-col h-full bg-white border-l border-gray-200 w-full max-w-sm shadow-xl">
      {/* ── Header ── */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200 bg-gray-50">
        <div>
          <p className="text-xs text-gray-500 uppercase tracking-wide">Cuenta</p>
          <p className="font-bold text-gray-800 text-lg">{cuenta.numeroCuenta}</p>
          <p className="text-sm text-gray-600">{cuenta.mesa?.nombre}</p>
        </div>
        <div className="text-right">
          <div className="flex items-center gap-1 text-gray-500 text-xs mb-1">
            <Users size={12} />
            <span>{cuenta.numComensales} comensales</span>
          </div>
          <div className="flex items-center gap-1 text-gray-500 text-xs">
            <Clock size={12} />
            <span>{cuenta.abiertoEn ? new Date(cuenta.abiertoEn).toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }) : ''}</span>
          </div>
        </div>
        <button onClick={onClose} className="ml-2 p-1 rounded hover:bg-gray-200 text-gray-500">
          <X size={18} />
        </button>
      </div>

      {/* ── Buscador de productos ── */}
      <div className="px-4 pt-3 pb-2">
        <div ref={wrapperRef} className="relative">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            ref={inputRef}
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Agregar producto..."
            className="w-full pl-9 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            autoComplete="off"
          />
          {isSearching && (
            <div className="absolute right-3 top-1/2 -translate-y-1/2">
              <div className="w-3.5 h-3.5 border-2 border-green-500 border-t-transparent rounded-full animate-spin" />
            </div>
          )}
          {isDropdownOpen && (
            <div className="absolute z-30 top-full mt-1 w-full bg-white border border-gray-200 rounded-lg shadow-lg max-h-56 overflow-y-auto">
              {results.map((p) => (
                <button
                  key={p.id}
                  onMouseDown={() => handleSelectProduct(p)}
                  className="w-full text-left px-3 py-2 hover:bg-green-50 flex items-center justify-between gap-2"
                >
                  <span className="text-sm font-medium text-gray-800 truncate">{p.nombre}</span>
                  <span className="text-sm text-green-700 font-semibold whitespace-nowrap">
                    {formatCurrency(p.precioVenta ?? p.precio ?? 0)}
                  </span>
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* ── Lista de productos ── */}
      <div className="flex-1 overflow-y-auto px-4 pb-2">
        {isLoading ? (
          <p className="text-center text-gray-400 text-sm py-6">Cargando...</p>
        ) : detalles.length === 0 ? (
          <p className="text-center text-gray-400 text-sm py-6">Sin productos agregados</p>
        ) : (
          <ul className="divide-y divide-gray-100">
            {detalles.map((d) => (
              <li key={d.id} className="flex items-center gap-2 py-2">
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-800 truncate">{d.nombreProductoSnapshot}</p>
                  <p className="text-xs text-gray-500">
                    {d.cantidad} × {formatCurrency(d.precioUnitario)}
                  </p>
                  {d.notas && <p className="text-xs text-gray-400 italic">{d.notas}</p>}
                </div>
                <span className="text-sm font-semibold text-gray-800 whitespace-nowrap">
                  {formatCurrency(d.subtotal ?? d.total ?? 0)}
                </span>
                <button
                  onClick={() => handleRemove(d.id)}
                  className="p-1 rounded text-red-400 hover:bg-red-50 hover:text-red-600"
                  title="Quitar producto"
                >
                  <Trash2 size={15} />
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* ── Total ── */}
      <div className="px-4 py-3 border-t border-gray-200 bg-gray-50">
        <div className="flex justify-between items-center mb-3">
          <span className="text-sm text-gray-600">Total</span>
          <span className="text-xl font-bold text-gray-900">{formatCurrency(cuenta.total ?? 0)}</span>
        </div>

        {/* ── Acciones ── */}
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={onTransferir} className="flex-1 flex items-center gap-1 justify-center">
            <ArrowRightLeft size={14} />
            Transferir
          </Button>
          <Button variant="primary" size="sm" onClick={onCerrar} className="flex-1 flex items-center gap-1 justify-center">
            <CreditCard size={14} />
            Cerrar cuenta
          </Button>
        </div>
      </div>
    </div>
  );
};
