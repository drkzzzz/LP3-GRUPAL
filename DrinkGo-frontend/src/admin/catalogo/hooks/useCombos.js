/**
 * useCombos.js
 * ────────────
 * Hook de React Query para el CRUD de Combos y su Detalle.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { combosService, detalleCombosService } from '../services/catalogoService';
import { message } from '@/shared/utils/notifications';

export const useCombos = () => {
  const queryClient = useQueryClient();

  /* ─── Queries ─── */
  const combosQuery = useQuery({
    queryKey: ['combos'],
    queryFn: combosService.getAll,
  });

  const detallesQuery = useQuery({
    queryKey: ['detalle-combos'],
    queryFn: detalleCombosService.getAll,
  });

  /* ─── Combo Mutations ─── */
  const createCombo = useMutation({
    mutationFn: combosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['combos'] });
      message.success('Combo creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear combo');
    },
  });

  const updateCombo = useMutation({
    mutationFn: combosService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['combos'] });
      message.success('Combo actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar combo');
    },
  });

  const deleteCombo = useMutation({
    mutationFn: combosService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['combos'] });
      queryClient.invalidateQueries({ queryKey: ['detalle-combos'] });
      message.success('Combo eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar combo');
    },
  });

  /* ─── Detalle Combo Mutations ─── */
  const createDetalle = useMutation({
    mutationFn: detalleCombosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-combos'] });
      message.success('Producto agregado al combo');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al agregar producto al combo');
    },
  });

  const updateDetalle = useMutation({
    mutationFn: detalleCombosService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-combos'] });
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar producto del combo');
    },
  });

  const deleteDetalle = useMutation({
    mutationFn: detalleCombosService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-combos'] });
      message.success('Producto removido del combo');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al remover producto del combo');
    },
  });

  /* ─── Helpers ─── */
  const getDetallesForCombo = (comboId) => {
    if (!detallesQuery.data || !comboId) return [];
    return detallesQuery.data.filter(
      (d) => (d.combo?.id ?? d.comboId) === comboId,
    );
  };

  return {
    combos: combosQuery.data || [],
    detalles: detallesQuery.data || [],
    isLoading: combosQuery.isLoading,
    isLoadingDetalles: detallesQuery.isLoading,

    getDetallesForCombo,

    createCombo: createCombo.mutateAsync,
    updateCombo: updateCombo.mutateAsync,
    deleteCombo: deleteCombo.mutateAsync,
    createDetalle: createDetalle.mutateAsync,
    updateDetalle: updateDetalle.mutateAsync,
    deleteDetalle: deleteDetalle.mutateAsync,

    isCreating: createCombo.isPending,
    isUpdating: updateCombo.isPending,
    isDeleting: deleteCombo.isPending,
    isCreatingDetalle: createDetalle.isPending,
    isUpdatingDetalle: updateDetalle.isPending,
    isDeletingDetalle: deleteDetalle.isPending,
  };
};
