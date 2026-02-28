/**
 * useAlmacenesConfig.js
 * ─────────────────────
 * Hook de React Query para el CRUD de Almacenes (módulo Configuración).
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { almacenesConfigService } from '../services/configuracionService';
import { message } from '@/shared/utils/notifications';

export const useAlmacenesConfig = (negocioId) => {
  const queryClient = useQueryClient();

  const almacenesQuery = useQuery({
    queryKey: ['almacenes-config', negocioId],
    queryFn: async () => {
      const all = await almacenesConfigService.getAll();
      return negocioId
        ? all.filter((a) => a.negocio?.id === negocioId)
        : all;
    },
    enabled: !!negocioId,
  });

  const createAlmacen = useMutation({
    mutationFn: almacenesConfigService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['almacenes-config'] });
      message.success('Almacén creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear almacén');
    },
  });

  const updateAlmacen = useMutation({
    mutationFn: almacenesConfigService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['almacenes-config'] });
      message.success('Almacén actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar almacén');
    },
  });

  const deleteAlmacen = useMutation({
    mutationFn: almacenesConfigService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['almacenes-config'] });
      message.success('Almacén eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar almacén');
    },
  });

  return {
    almacenes: almacenesQuery.data || [],
    isLoading: almacenesQuery.isLoading,
    isError: almacenesQuery.isError,

    createAlmacen: createAlmacen.mutateAsync,
    updateAlmacen: updateAlmacen.mutateAsync,
    deleteAlmacen: deleteAlmacen.mutateAsync,

    isCreating: createAlmacen.isPending,
    isUpdating: updateAlmacen.isPending,
    isDeleting: deleteAlmacen.isPending,
  };
};
