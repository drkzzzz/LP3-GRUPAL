/**
 * useZonasDelivery.js
 * ───────────────────
 * Hook de React Query para el CRUD de Zonas de Delivery.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { zonasDeliveryService } from '../services/configuracionService';
import { message } from '@/shared/utils/notifications';

export const useZonasDelivery = (negocioId) => {
  const queryClient = useQueryClient();

  const zonasQuery = useQuery({
    queryKey: ['zonas-delivery', negocioId],
    queryFn: async () => {
      const all = await zonasDeliveryService.getAll();
      return negocioId
        ? all.filter((z) => z.negocio?.id === negocioId)
        : all;
    },
    enabled: !!negocioId,
  });

  const createZona = useMutation({
    mutationFn: zonasDeliveryService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['zonas-delivery'] });
      message.success('Zona de delivery creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear zona de delivery');
    },
  });

  const updateZona = useMutation({
    mutationFn: zonasDeliveryService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['zonas-delivery'] });
      message.success('Zona de delivery actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar zona de delivery');
    },
  });

  const deleteZona = useMutation({
    mutationFn: zonasDeliveryService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['zonas-delivery'] });
      message.success('Zona de delivery eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar zona');
    },
  });

  return {
    zonas: zonasQuery.data || [],
    isLoading: zonasQuery.isLoading,
    isError: zonasQuery.isError,

    createZona: createZona.mutateAsync,
    updateZona: updateZona.mutateAsync,
    deleteZona: deleteZona.mutateAsync,

    isCreating: createZona.isPending,
    isUpdating: updateZona.isPending,
    isDeleting: deleteZona.isPending,
  };
};
