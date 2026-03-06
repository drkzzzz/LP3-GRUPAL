/**
 * Hooks de React Query para productos del catálogo
 */
import { useQuery } from '@tanstack/react-query';
import {
  getProductos,
  getProductosConStock,
  buscarProductos,
  getProductoById,
  getProductosPorCategoria,
} from '@/admin/services/productosApi';

/**
 * Hook para obtener todos los productos activos
 */
export function useProductos() {
  return useQuery({
    queryKey: ['productos'],
    queryFn: getProductos,
    staleTime: 5 * 60 * 1000, // 5 minutos
  });
}

/**
 * Hook para obtener productos con stock disponible
 */
export function useProductosConStock() {
  return useQuery({
    queryKey: ['productos-con-stock'],
    queryFn: getProductosConStock,
    staleTime: 2 * 60 * 1000, // 2 minutos
  });
}

/**
 * Hook para buscar productos por término
 * @param {string} termino - Término de búsqueda
 * @param {boolean} enabled - Si la búsqueda debe ejecutarse
 */
export function useBuscarProductos(termino, enabled = true) {
  return useQuery({
    queryKey: ['productos-busqueda', termino],
    queryFn: () => buscarProductos(termino),
    enabled: enabled && termino && termino.trim().length >= 2,
    staleTime: 30 * 1000, // 30 segundos
  });
}

/**
 * Hook para obtener un producto por ID
 * @param {number} id - ID del producto
 */
export function useProductoById(id) {
  return useQuery({
    queryKey: ['productos', id],
    queryFn: () => getProductoById(id),
    enabled: !!id,
  });
}

/**
 * Hook para obtener productos por categoría
 * @param {number} categoriaId - ID de la categoría
 */
export function useProductosPorCategoria(categoriaId) {
  return useQuery({
    queryKey: ['productos-categoria', categoriaId],
    queryFn: () => getProductosPorCategoria(categoriaId),
    enabled: !!categoriaId,
    staleTime: 3 * 60 * 1000, // 3 minutos
  });
}
