/**
 * Hook para gestión de seguimiento de pedidos (timeline de estados) con React Query
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { message } from '@/shared/utils/notifications';
import {
  getSeguimientoPedidos,
  getSeguimientoPedidoById,
  createSeguimientoPedido,
  updateSeguimientoPedido,
  deleteSeguimientoPedido,
} from '../services/pedidosApi';

/**
 * Hook para obtener lista de seguimientos
 */
export const useSeguimientoPedidos = () => {
  return useQuery({
    queryKey: ['seguimiento-pedidos'],
    queryFn: getSeguimientoPedidos,
    staleTime: 1000 * 30,
  });
};

/**
 * Hook para obtener seguimiento por ID
 */
export const useSeguimientoPedidoById = (id) => {
  return useQuery({
    queryKey: ['seguimiento-pedido', id],
    queryFn: () => getSeguimientoPedidoById(id),
    enabled: !!id,
    staleTime: 1000 * 30,
  });
};

/**
 * Hook para crear seguimiento (cambio de estado)
 */
export const useCreateSeguimientoPedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createSeguimientoPedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['seguimiento-pedidos'] });
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Estado del pedido actualizado');
    },
    onError: (error) => {
      console.error('Error al actualizar estado:', error);
      message.error('Error al cambiar el estado del pedido');
    },
  });
};

/**
 * Hook para actualizar seguimiento
 */
export const useUpdateSeguimientoPedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateSeguimientoPedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['seguimiento-pedidos'] });
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
    },
    onError: (error) => {
      console.error('Error al actualizar seguimiento:', error);
      message.error('Error al actualizar el seguimiento');
    },
  });
};

/**
 * Hook para eliminar seguimiento
 */
export const useDeleteSeguimientoPedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteSeguimientoPedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['seguimiento-pedidos'] });
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
    },
    onError: (error) => {
      console.error('Error al eliminar seguimiento:', error);
      message.error('Error al eliminar el seguimiento');
    },
  });
};
