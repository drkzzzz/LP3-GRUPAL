import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { planesService } from '../services/planesService';
import { message } from '@/shared/utils/notifications';

export const usePlanes = () => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['planes'],
    queryFn: planesService.getAll,
  });

  const createMutation = useMutation({
    mutationFn: planesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['planes'] });
      message.success('Plan creado exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al crear plan');
    },
  });

  const updateMutation = useMutation({
    mutationFn: planesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['planes'] });
      message.success('Plan actualizado exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al actualizar plan');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: planesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['planes'] });
      message.success('Plan eliminado exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al eliminar plan');
    },
  });

  const deactivateMutation = useMutation({
    mutationFn: planesService.deactivate,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['planes'] });
      message.success('Plan desactivado exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al desactivar plan');
    },
  });

  const activateMutation = useMutation({
    mutationFn: planesService.activate,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['planes'] });
      message.success('Plan activado exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al activar plan');
    },
  });

  return {
    planes: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,

    createPlan: createMutation.mutateAsync,
    updatePlan: updateMutation.mutateAsync,
    deletePlan: deleteMutation.mutateAsync,
    deactivatePlan: deactivateMutation.mutateAsync,
    activatePlan: activateMutation.mutateAsync,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
    isDeactivating: deactivateMutation.isPending,
    isActivating: activateMutation.isPending,
  };
};
