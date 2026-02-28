/**
 * useProductosInventario.js
 * ─────────────────────────
 * Hook de React Query para obtener productos (solo lectura).
 * Se usa en selects de formularios de inventario.
 */
import { useQuery } from '@tanstack/react-query';
import { productosInventarioService } from '../services/inventarioService';

export const useProductosInventario = (negocioId) => {
  const productosQuery = useQuery({
    queryKey: ['productos-inventario', negocioId],
    queryFn: () => productosInventarioService.getAll(negocioId),
    enabled: !!negocioId,
  });

  return {
    productos: productosQuery.data || [],
    isLoading: productosQuery.isLoading,
    isError: productosQuery.isError,
  };
};
