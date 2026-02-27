import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { suscripcionesService } from '../services/suscripcionesService';
import { message } from '@/shared/utils/notifications';

export const useSuscripciones = () => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['suscripciones'],
    queryFn: suscripcionesService.getAll,
  });

  const createMutation = useMutation({
    mutationFn: suscripcionesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['suscripciones'] });
      message.success('Suscripción creada exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al crear suscripción');
    },
  });

  const updateMutation = useMutation({
    mutationFn: suscripcionesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['suscripciones'] });
      message.success('Suscripción actualizada exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al actualizar suscripción');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: suscripcionesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['suscripciones'] });
      message.success('Suscripción eliminada exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al eliminar suscripción');
    },
  });

  return {
    suscripciones: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,

    createSuscripcion: createMutation.mutateAsync,
    updateSuscripcion: updateMutation.mutateAsync,
    deleteSuscripcion: deleteMutation.mutateAsync,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};
