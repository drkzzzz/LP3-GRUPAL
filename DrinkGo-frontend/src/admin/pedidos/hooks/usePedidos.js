/**
 * Hook para gestión de pedidos con React Query
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { message } from '@/shared/utils/notifications';
import {
  getPedidos,
  getPedidoById,
  createPedido,
  updatePedido,
  deletePedido,
} from '../services/pedidosApi';

/**
 * Hook para obtener lista de pedidos
 */
export const usePedidos = () => {
  return useQuery({
    queryKey: ['pedidos'],
    queryFn: getPedidos,
    staleTime: 0, // 🔄 Cambiado a 0 para que siempre pida datos frescos
    refetchOnWindowFocus: true, // Recargar al volver a la pestaña
  });
};

/**
 * Hook para obtener pedido por ID
 */
export const usePedidoById = (id) => {
  return useQuery({
    queryKey: ['pedido', id],
    queryFn: () => getPedidoById(id),
    enabled: !!id,
    staleTime: 1000 * 30,
  });
};

/**
 * Hook para crear pedido
 */
export const useCreatePedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createPedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Pedido creado exitosamente');
    },
    onError: (error) => {
      console.error('Error al crear pedido:', error);
      message.error('Error al crear el pedido');
    },
  });
};

/**
 * Hook para actualizar pedido
 */
export const useUpdatePedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updatePedido,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      queryClient.invalidateQueries({ queryKey: ['pedido', data.id] });
      message.success('Pedido actualizado exitosamente');
    },
    onError: (error) => {
      console.error('Error al actualizar pedido:', error);
      message.error('Error al actualizar el pedido');
    },
  });
};

/**
 * Hook para eliminar pedido
 */
export const useDeletePedido = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deletePedido,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Pedido eliminado exitosamente');
    },
    onError: (error) => {
      console.error('Error al eliminar pedido:', error);
      message.error('Error al eliminar el pedido');
    },
  });
};
