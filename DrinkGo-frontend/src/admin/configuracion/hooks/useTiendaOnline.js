/**
 * useTiendaOnline.js
 * ──────────────────
 * Hook de React Query para la Configuración de Tienda Online.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { tiendaOnlineService } from '../services/configuracionService';
import { message } from '@/shared/utils/notifications';

export const useTiendaOnline = (negocioId) => {
  const queryClient = useQueryClient();

  const tiendaQuery = useQuery({
    queryKey: ['tienda-online', negocioId],
    queryFn: async () => {
      const all = await tiendaOnlineService.getAll();
      const found = negocioId
        ? all.find((t) => t.negocio?.id === negocioId)
        : all[0];
      return found || null;
    },
    enabled: !!negocioId,
  });

  const createTienda = useMutation({
    mutationFn: tiendaOnlineService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tienda-online'] });
      message.success('Configuración de tienda online creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear configuración de tienda');
    },
  });

  const updateTienda = useMutation({
    mutationFn: tiendaOnlineService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tienda-online'] });
      message.success('Configuración de tienda online actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar configuración de tienda');
    },
  });

  return {
    tiendaConfig: tiendaQuery.data,
    isLoading: tiendaQuery.isLoading,
    isError: tiendaQuery.isError,

    createTienda: createTienda.mutateAsync,
    updateTienda: updateTienda.mutateAsync,

    isCreating: createTienda.isPending,
    isUpdating: updateTienda.isPending,
  };
};
