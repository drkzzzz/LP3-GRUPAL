/**
 * Hooks de React Query para gestión de clientes
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getClientes,
  buscarClientes,
  getClienteById,
  createClienteRapido,
  updateCliente,
  deleteCliente,
} from '@/admin/services/clientesApi';
import { message } from '@/shared/utils/notifications';

/**
 * Hook para obtener todos los clientes
 */
export function useClientes() {
  return useQuery({
    queryKey: ['clientes'],
    queryFn: getClientes,
    staleTime: 2 * 60 * 1000, // 2 minutos
  });
}

/**
 * Hook para buscar clientes por término
 * @param {string} termino - Término de búsqueda
 * @param {boolean} enabled - Si la búsqueda debe ejecutarse
 */
export function useBuscarClientes(termino, enabled = true) {
  return useQuery({
    queryKey: ['clientes-busqueda', termino],
    queryFn: () => buscarClientes(termino),
    enabled: enabled && termino && termino.trim().length >= 2,
    staleTime: 30 * 1000, // 30 segundos
  });
}

/**
 * Hook para obtener un cliente por ID
 * @param {number} id - ID del cliente
 */
export function useClienteById(id) {
  return useQuery({
    queryKey: ['clientes', id],
    queryFn: () => getClienteById(id),
    enabled: !!id,
  });
}

/**
 * Hook para crear cliente rápido (mutación)
 */
export function useCrearClienteRapido() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: createClienteRapido,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      message.success('Cliente creado correctamente');
      return data;
    },
    onError: (error) => {
      console.error('Error al crear cliente:', error);
      message.error('Error al crear cliente');
    },
  });
}

/**
 * Hook para actualizar cliente
 */
export function useUpdateCliente() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: updateCliente,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      message.success('Cliente actualizado correctamente');
    },
    onError: (error) => {
      console.error('Error al actualizar cliente:', error);
      message.error('Error al actualizar cliente');
    },
  });
}

/**
 * Hook para eliminar cliente
 */
export function useDeleteCliente() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: deleteCliente,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      message.success('Cliente eliminado correctamente');
    },
    onError: (error) => {
      console.error('Error al eliminar cliente:', error);
      message.error('Error al eliminar cliente');
    },
  });
}
