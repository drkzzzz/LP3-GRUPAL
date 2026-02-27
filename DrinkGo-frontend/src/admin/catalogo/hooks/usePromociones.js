/**
 * usePromociones.js
 * ─────────────────
 * Hook de React Query para el CRUD de Promociones y Condiciones.
 * NOTA: En el backend, los "Descuentos" se modelan como Promociones
 * con tipoDescuento = 'porcentaje' | 'monto_fijo'.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  promocionesService,
  condicionesPromocionService,
} from '../services/catalogoService';
import { message } from '@/shared/utils/notifications';

export const usePromociones = () => {
  const queryClient = useQueryClient();

  /* ─── Queries ─── */
  const promocionesQuery = useQuery({
    queryKey: ['promociones'],
    queryFn: promocionesService.getAll,
  });

  const condicionesQuery = useQuery({
    queryKey: ['condiciones-promocion'],
    queryFn: condicionesPromocionService.getAll,
  });

  /* ─── Promociones Mutations ─── */
  const createPromocion = useMutation({
    mutationFn: promocionesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['promociones'] });
      message.success('Promoción creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear promoción');
    },
  });

  const updatePromocion = useMutation({
    mutationFn: promocionesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['promociones'] });
      message.success('Promoción actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar promoción');
    },
  });

  const deletePromocion = useMutation({
    mutationFn: promocionesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['promociones'] });
      queryClient.invalidateQueries({ queryKey: ['condiciones-promocion'] });
      message.success('Promoción eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar promoción');
    },
  });

  /* ─── Condiciones Mutations ─── */
  const createCondicion = useMutation({
    mutationFn: condicionesPromocionService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['condiciones-promocion'] });
      message.success('Condición agregada');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear condición');
    },
  });

  const deleteCondicion = useMutation({
    mutationFn: condicionesPromocionService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['condiciones-promocion'] });
      message.success('Condición eliminada');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar condición');
    },
  });

  /* ─── Helpers ─── */
  const getCondicionesForPromocion = (promocionId) => {
    if (!condicionesQuery.data || !promocionId) return [];
    return condicionesQuery.data.filter(
      (c) => (c.promocion?.id ?? c.promocionId) === promocionId,
    );
  };

  return {
    promociones: promocionesQuery.data || [],
    condiciones: condicionesQuery.data || [],
    isLoading: promocionesQuery.isLoading,
    isLoadingCondiciones: condicionesQuery.isLoading,

    getCondicionesForPromocion,

    createPromocion: createPromocion.mutateAsync,
    updatePromocion: updatePromocion.mutateAsync,
    deletePromocion: deletePromocion.mutateAsync,
    createCondicion: createCondicion.mutateAsync,
    deleteCondicion: deleteCondicion.mutateAsync,

    isCreating: createPromocion.isPending,
    isUpdating: updatePromocion.isPending,
    isDeleting: deletePromocion.isPending,
    isCreatingCondicion: createCondicion.isPending,
    isDeletingCondicion: deleteCondicion.isPending,
  };
};
