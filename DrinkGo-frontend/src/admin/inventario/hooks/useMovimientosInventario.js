/**
 * useMovimientosInventario.js
 * ───────────────────────────
 * Hook de React Query para CRUD de movimientos de inventario (Kardex).
 * Tipos: entrada, salida, transferencia, ajuste_positivo, ajuste_negativo, devolucion, merma.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { movimientosInventarioService } from '../services/inventarioService';
import { message } from '@/shared/utils/notifications';

export const useMovimientosInventario = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todos los movimientos ─── */
  const movimientosQuery = useQuery({
    queryKey: ['movimientos-inventario', negocioId],
    queryFn: movimientosInventarioService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((m) => m.negocio?.id === negocioId),
  });

  /* ─── Mutation: crear movimiento ─── */
  const createMovimiento = useMutation({
    mutationFn: movimientosInventarioService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['movimientos-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['lotes-inventario'] });
      message.success('Movimiento registrado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al registrar movimiento');
    },
  });

  /* ─── Mutation: actualizar movimiento ─── */
  const updateMovimiento = useMutation({
    mutationFn: movimientosInventarioService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['movimientos-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Movimiento actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar movimiento');
    },
  });

  /* ─── Mutation: eliminar movimiento ─── */
  const deleteMovimiento = useMutation({
    mutationFn: movimientosInventarioService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['movimientos-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Movimiento eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar movimiento');
    },
  });

  return {
    movimientos: movimientosQuery.data || [],
    isLoading: movimientosQuery.isLoading,
    isError: movimientosQuery.isError,

    createMovimiento: createMovimiento.mutateAsync,
    updateMovimiento: updateMovimiento.mutateAsync,
    deleteMovimiento: deleteMovimiento.mutateAsync,

    isCreating: createMovimiento.isPending,
    isUpdating: updateMovimiento.isPending,
    isDeleting: deleteMovimiento.isPending,
  };
};
