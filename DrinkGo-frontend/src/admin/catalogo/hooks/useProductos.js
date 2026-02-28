/**
 * useProductos.js
 * ───────────────
 * Hook de React Query para el CRUD de Productos.
 * Patrón: queries + mutations con invalidación de cache y toasts.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productosService } from '../services/catalogoService';
import { message } from '@/shared/utils/notifications';

export const useProductos = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todos los productos ─── */
  const productosQuery = useQuery({
    queryKey: ['productos', negocioId],
    queryFn: () => productosService.getAll(negocioId),
    enabled: !!negocioId,
  });

  /* ─── Mutation: crear producto ─── */
  const createProducto = useMutation({
    mutationFn: productosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos'] });
      message.success('Producto creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear producto');
    },
  });

  /* ─── Mutation: actualizar producto ─── */
  const updateProducto = useMutation({
    mutationFn: productosService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos'] });
      message.success('Producto actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar producto');
    },
  });

  /* ─── Mutation: eliminar (soft delete) producto ─── */
  const deleteProducto = useMutation({
    mutationFn: productosService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos'] });
      message.success('Producto eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar producto');
    },
  });

  return {
    productos: productosQuery.data || [],
    isLoading: productosQuery.isLoading,
    isError: productosQuery.isError,

    createProducto: createProducto.mutateAsync,
    updateProducto: updateProducto.mutateAsync,
    deleteProducto: deleteProducto.mutateAsync,

    isCreating: createProducto.isPending,
    isUpdating: updateProducto.isPending,
    isDeleting: deleteProducto.isPending,
  };
};
