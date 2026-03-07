/**
 * useClientes.js
 * ──────────────
 * Hook TanStack Query para CRUD de Clientes.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { clientesService } from '../services/usuariosClientesService';
import { message } from '@/shared/utils/notifications';

export const useClientes = (negocioId) => {
  const queryClient = useQueryClient();

  const clientesQuery = useQuery({
    queryKey: ['clientes', negocioId],
    queryFn: () =>
      negocioId
        ? clientesService.getByNegocio(negocioId)
        : clientesService.getAll(),
    staleTime: 1000 * 60 * 3,
    enabled: !!negocioId,
  });

  const createCliente = useMutation({
    mutationFn: clientesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes', negocioId] });
      message.success('Cliente registrado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al registrar cliente');
    },
  });

  const updateCliente = useMutation({
    mutationFn: clientesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes', negocioId] });
      message.success('Cliente actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar cliente');
    },
  });

  const deleteCliente = useMutation({
    mutationFn: clientesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes', negocioId] });
      message.success('Cliente eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar cliente');
    },
  });

  return {
    clientes: clientesQuery.data || [],
    isLoading: clientesQuery.isLoading,
    isError: clientesQuery.isError,
    refetch: clientesQuery.refetch,

    createCliente: createCliente.mutateAsync,
    updateCliente: updateCliente.mutateAsync,
    deleteCliente: deleteCliente.mutateAsync,

    isCreating: createCliente.isPending,
    isUpdating: updateCliente.isPending,
    isDeleting: deleteCliente.isPending,
  };
};
