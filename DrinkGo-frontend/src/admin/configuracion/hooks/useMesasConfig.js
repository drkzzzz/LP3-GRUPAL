/**
 * useMesasConfig.js
 * ─────────────────
 * Hook de React Query para el CRUD de Mesas.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { mesasService } from '../services/configuracionService';
import { message } from '@/shared/utils/notifications';

export const useMesasConfig = (negocioId, sedes = []) => {
  const queryClient = useQueryClient();

  /* IDs de sedes del negocio para filtrar */
  const sedeIds = sedes.map((s) => s.id);

  const mesasQuery = useQuery({
    queryKey: ['mesas-config', negocioId],
    queryFn: async () => {
      const all = await mesasService.getAll();
      return sedeIds.length > 0
        ? all.filter((m) => sedeIds.includes(m.sede?.id))
        : all;
    },
    enabled: !!negocioId && sedeIds.length > 0,
  });

  const createMesa = useMutation({
    mutationFn: mesasService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mesas-config'] });
      message.success('Mesa creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear mesa');
    },
  });

  const updateMesa = useMutation({
    mutationFn: mesasService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mesas-config'] });
      message.success('Mesa actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar mesa');
    },
  });

  const deleteMesa = useMutation({
    mutationFn: mesasService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mesas-config'] });
      message.success('Mesa eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar mesa');
    },
  });

  return {
    mesas: mesasQuery.data || [],
    isLoading: mesasQuery.isLoading,
    isError: mesasQuery.isError,

    createMesa: createMesa.mutateAsync,
    updateMesa: updateMesa.mutateAsync,
    deleteMesa: deleteMesa.mutateAsync,

    isCreating: createMesa.isPending,
    isUpdating: updateMesa.isPending,
    isDeleting: deleteMesa.isPending,
  };
};
