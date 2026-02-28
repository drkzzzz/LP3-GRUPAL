/**
 * useStockInventario.js
 * ─────────────────────
 * Hook de React Query para consultar el stock consolidado.
 * Filtra client-side por negocioId (multi-tenant).
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { stockInventarioService } from '../services/inventarioService';
import { message } from '@/shared/utils/notifications';

export const useStockInventario = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todo el stock ─── */
  const stockQuery = useQuery({
    queryKey: ['stock-inventario', negocioId],
    queryFn: stockInventarioService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((s) => s.negocio?.id === negocioId),
  });

  /* ─── Mutation: crear registro de stock ─── */
  const createStock = useMutation({
    mutationFn: stockInventarioService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Stock registrado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al registrar stock');
    },
  });

  /* ─── Mutation: actualizar stock ─── */
  const updateStock = useMutation({
    mutationFn: stockInventarioService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Stock actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar stock');
    },
  });

  /* ─── Mutation: eliminar stock ─── */
  const deleteStock = useMutation({
    mutationFn: stockInventarioService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Stock eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar stock');
    },
  });

  return {
    stock: stockQuery.data || [],
    isLoading: stockQuery.isLoading,
    isError: stockQuery.isError,

    createStock: createStock.mutateAsync,
    updateStock: updateStock.mutateAsync,
    deleteStock: deleteStock.mutateAsync,

    isCreating: createStock.isPending,
    isUpdating: updateStock.isPending,
    isDeleting: deleteStock.isPending,
  };
};
