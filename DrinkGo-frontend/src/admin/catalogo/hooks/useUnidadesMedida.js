/**
 * useUnidadesMedida.js
 * ────────────────────
 * Hook de React Query para el CRUD de Unidades de Medida.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { unidadesMedidaService } from '../services/catalogoService';
import { message } from '@/shared/utils/notifications';

export const useUnidadesMedida = () => {
  const queryClient = useQueryClient();

  const unidadesQuery = useQuery({
    queryKey: ['unidades-medida'],
    queryFn: unidadesMedidaService.getAll,
  });

  const createUnidad = useMutation({
    mutationFn: unidadesMedidaService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['unidades-medida'] });
      message.success('Unidad de medida creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear unidad de medida');
    },
  });

  const updateUnidad = useMutation({
    mutationFn: unidadesMedidaService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['unidades-medida'] });
      message.success('Unidad de medida actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar unidad');
    },
  });

  const deleteUnidad = useMutation({
    mutationFn: unidadesMedidaService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['unidades-medida'] });
      message.success('Unidad de medida eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar unidad');
    },
  });

  return {
    unidades: unidadesQuery.data || [],
    isLoading: unidadesQuery.isLoading,
    isError: unidadesQuery.isError,

    createUnidad: createUnidad.mutateAsync,
    updateUnidad: updateUnidad.mutateAsync,
    deleteUnidad: deleteUnidad.mutateAsync,

    isCreating: createUnidad.isPending,
    isUpdating: updateUnidad.isPending,
    isDeleting: deleteUnidad.isPending,
  };
};
