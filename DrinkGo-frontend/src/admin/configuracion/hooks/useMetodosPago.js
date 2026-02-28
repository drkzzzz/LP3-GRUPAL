/**
 * useMetodosPago.js
 * ─────────────────
 * Hook de React Query para el CRUD de Métodos de Pago.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { metodosPagoService } from '../services/configuracionService';
import { message } from '@/shared/utils/notifications';

export const useMetodosPago = (negocioId) => {
  const queryClient = useQueryClient();

  const metodosPagoQuery = useQuery({
    queryKey: ['metodos-pago', negocioId],
    queryFn: async () => {
      const all = await metodosPagoService.getAll();
      return negocioId
        ? all.filter((m) => m.negocio?.id === negocioId)
        : all;
    },
    enabled: !!negocioId,
  });

  const createMetodo = useMutation({
    mutationFn: metodosPagoService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['metodos-pago'] });
      message.success('Método de pago creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear método de pago');
    },
  });

  const updateMetodo = useMutation({
    mutationFn: metodosPagoService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['metodos-pago'] });
      message.success('Método de pago actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar método de pago');
    },
  });

  const deleteMetodo = useMutation({
    mutationFn: metodosPagoService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['metodos-pago'] });
      message.success('Método de pago eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar método de pago');
    },
  });

  return {
    metodosPago: metodosPagoQuery.data || [],
    isLoading: metodosPagoQuery.isLoading,
    isError: metodosPagoQuery.isError,

    createMetodo: createMetodo.mutateAsync,
    updateMetodo: updateMetodo.mutateAsync,
    deleteMetodo: deleteMetodo.mutateAsync,

    isCreating: createMetodo.isPending,
    isUpdating: updateMetodo.isPending,
    isDeleting: deleteMetodo.isPending,
  };
};
