/**
 * useLotesInventario.js
 * ─────────────────────
 * Hook de React Query para CRUD de lotes de inventario.
 * Soporta FIFO/FEFO a través de fechaIngreso y fechaVencimiento.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { lotesInventarioService } from '../services/inventarioService';
import { message } from '@/shared/utils/notifications';

export const useLotesInventario = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todos los lotes ─── */
  const lotesQuery = useQuery({
    queryKey: ['lotes-inventario', negocioId],
    queryFn: lotesInventarioService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((l) => l.negocio?.id === negocioId),
  });

  /* ─── Mutation: crear lote (entrada de mercancía) ─── */
  const createLote = useMutation({
    mutationFn: lotesInventarioService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lotes-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['movimientos-inventario'] });
      message.success('Lote registrado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al registrar lote');
    },
  });

  /* ─── Mutation: actualizar lote ─── */
  const updateLote = useMutation({
    mutationFn: lotesInventarioService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lotes-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Lote actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar lote');
    },
  });

  /* ─── Mutation: eliminar lote ─── */
  const deleteLote = useMutation({
    mutationFn: lotesInventarioService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lotes-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Lote eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar lote');
    },
  });

  return {
    lotes: lotesQuery.data || [],
    isLoading: lotesQuery.isLoading,
    isError: lotesQuery.isError,

    createLote: createLote.mutateAsync,
    updateLote: updateLote.mutateAsync,
    deleteLote: deleteLote.mutateAsync,

    isCreating: createLote.isPending,
    isUpdating: updateLote.isPending,
    isDeleting: deleteLote.isPending,
  };
};
