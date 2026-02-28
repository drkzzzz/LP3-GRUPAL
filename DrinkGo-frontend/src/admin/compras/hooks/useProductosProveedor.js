/**
 * useProductosProveedor.js
 * ────────────────────────
 * Hook de React Query para CRUD de productos por proveedor.
 * Filtra client-side por negocioId (multi-tenant).
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  productosProveedorService,
  productosComprasService,
} from '../services/comprasService';
import { message } from '@/shared/utils/notifications';

export const useProductosProveedor = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todos los productos-proveedor ─── */
  const productosProveedorQuery = useQuery({
    queryKey: ['productos-proveedor', negocioId],
    queryFn: productosProveedorService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((pp) => pp.negocio?.id === negocioId),
  });

  /* ─── Query: listar productos del catálogo (para selects) ─── */
  const productosQuery = useQuery({
    queryKey: ['productos-compras', negocioId],
    queryFn: () => productosComprasService.getAll(negocioId),
    enabled: !!negocioId,
  });

  /* ─── Mutation: crear relación producto-proveedor ─── */
  const createProductoProveedor = useMutation({
    mutationFn: productosProveedorService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos-proveedor'] });
      message.success('Producto asignado al proveedor');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al asignar producto al proveedor');
    },
  });

  /* ─── Mutation: actualizar relación ─── */
  const updateProductoProveedor = useMutation({
    mutationFn: productosProveedorService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos-proveedor'] });
      message.success('Producto del proveedor actualizado');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar producto del proveedor');
    },
  });

  /* ─── Mutation: eliminar relación ─── */
  const deleteProductoProveedor = useMutation({
    mutationFn: productosProveedorService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos-proveedor'] });
      message.success('Producto removido del proveedor');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al remover producto del proveedor');
    },
  });

  /* ─── Helper: obtener productos de un proveedor específico ─── */
  const getProductosForProveedor = (proveedorId) => {
    if (!productosProveedorQuery.data || !proveedorId) return [];
    return productosProveedorQuery.data.filter(
      (pp) => (pp.proveedor?.id ?? pp.proveedorId) === proveedorId,
    );
  };

  return {
    productosProveedor: productosProveedorQuery.data || [],
    productos: productosQuery.data || [],
    isLoading: productosProveedorQuery.isLoading,
    isLoadingProductos: productosQuery.isLoading,

    getProductosForProveedor,

    createProductoProveedor: createProductoProveedor.mutateAsync,
    updateProductoProveedor: updateProductoProveedor.mutateAsync,
    deleteProductoProveedor: deleteProductoProveedor.mutateAsync,

    isCreating: createProductoProveedor.isPending,
    isUpdating: updateProductoProveedor.isPending,
    isDeleting: deleteProductoProveedor.isPending,
  };
};
