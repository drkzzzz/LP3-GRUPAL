/**
 * useSedesConfig.js
 * ─────────────────
 * Hook de React Query para el CRUD de Sedes.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { sedesService } from '../services/configuracionService';
import { message } from '@/shared/utils/notifications';

export const useSedesConfig = (negocioId) => {
  const queryClient = useQueryClient();

  const sedesQuery = useQuery({
    queryKey: ['sedes-config', negocioId],
    queryFn: async () => {
      const all = await sedesService.getAll();
      return negocioId
        ? all.filter((s) => s.negocio?.id === negocioId)
        : all;
    },
    enabled: !!negocioId,
  });

  const createSede = useMutation({
    mutationFn: sedesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sedes-config'] });
      message.success('Sede creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear sede');
    },
  });

  const updateSede = useMutation({
    mutationFn: sedesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sedes-config'] });
      message.success('Sede actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar sede');
    },
  });

  const deleteSede = useMutation({
    mutationFn: sedesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sedes-config'] });
      message.success('Sede desactivada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al desactivar sede');
    },
  });

  return {
    sedes: sedesQuery.data || [],
    isLoading: sedesQuery.isLoading,
    isError: sedesQuery.isError,

    createSede: createSede.mutateAsync,
    updateSede: updateSede.mutateAsync,
    deleteSede: deleteSede.mutateAsync,

    isCreating: createSede.isPending,
    isUpdating: updateSede.isPending,
    isDeleting: deleteSede.isPending,
  };
};
