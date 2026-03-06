/**
 * Hook para gestión de detalles de pedidos (items) con React Query
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { message } from '@/shared/utils/notifications';
import {
  getDetallePedidos,
  getDetallePedidoById,
  createDetallePedido,
  updateDetallePedido,
  deleteDetallePedido,
} from '../services/pedidosApi';

/**
 * Hook para obtener lista de detalles de pedidos
 */
export const useDetallePedidos = () => {
  return useQuery({
    queryKey: ['detalle-pedidos'],
    queryFn: getDetallePedidos,
    staleTime: 1000 * 30,
  });
};

/**
 * Hook para obtener detalle de pedido por ID
 */
export const useDetallePedidoById = (id) => {
  return useQuery({
    queryKey: ['detalle-pedido', id],
    queryFn: () => getDetallePedidoById(id),
    enabled: !!id,
    staleTime: 1000 * 30,
  });
};

/**
 * Hook para crear detalle de pedido
 */
export const useCreateDetallePedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createDetallePedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-pedidos'] });
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
    },
    onError: (error) => {
      console.error('Error al crear detalle de pedido:', error);
      message.error('Error al agregar item al pedido');
    },
  });
};

/**
 * Hook para actualizar detalle de pedido
 */
export const useUpdateDetallePedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateDetallePedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-pedidos'] });
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
    },
    onError: (error) => {
      console.error('Error al actualizar detalle de pedido:', error);
      message.error('Error al actualizar item del pedido');
    },
  });
};

/**
 * Hook para eliminar detalle de pedido
 */
export const useDeleteDetallePedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteDetallePedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-pedidos'] });
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
    },
    onError: (error) => {
      console.error('Error al eliminar detalle de pedido:', error);
      message.error('Error al eliminar item del pedido');
    },
  });
};
