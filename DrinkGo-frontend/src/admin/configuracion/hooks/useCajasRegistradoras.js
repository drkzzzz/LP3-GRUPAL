/**
 * useCajasRegistradoras.js
 * ────────────────────────
 * Hook de React Query para el CRUD de Cajas Registradoras.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cajasRegistradorasService } from '../services/configuracionService';
import { message } from '@/shared/utils/notifications';

export const useCajasRegistradoras = (negocioId) => {
  const queryClient = useQueryClient();

  const cajasQuery = useQuery({
    queryKey: ['cajas-registradoras', negocioId],
    queryFn: async () => {
      const all = await cajasRegistradorasService.getAll();
      return negocioId
        ? all.filter((c) => c.negocio?.id === negocioId)
        : all;
    },
    enabled: !!negocioId,
  });

  const createCaja = useMutation({
    mutationFn: cajasRegistradorasService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cajas-registradoras'] });
      message.success('Caja registradora creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear caja registradora');
    },
  });

  const updateCaja = useMutation({
    mutationFn: cajasRegistradorasService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cajas-registradoras'] });
      message.success('Caja registradora actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar caja registradora');
    },
  });

  const deleteCaja = useMutation({
    mutationFn: cajasRegistradorasService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cajas-registradoras'] });
      message.success('Caja registradora eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar caja registradora');
    },
  });

  return {
    cajas: cajasQuery.data || [],
    isLoading: cajasQuery.isLoading,
    isError: cajasQuery.isError,

    createCaja: createCaja.mutateAsync,
    updateCaja: updateCaja.mutateAsync,
    deleteCaja: deleteCaja.mutateAsync,

    isCreating: createCaja.isPending,
    isUpdating: updateCaja.isPending,
    isDeleting: deleteCaja.isPending,
  };
};
