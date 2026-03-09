/**
 * useRoles.js
 * ───────────
 * Hook TanStack Query para CRUD de Roles del negocio.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { rolesService } from '../services/usuariosClientesService';
import { message } from '@/shared/utils/notifications';

export const useRoles = (negocioId) => {
  const queryClient = useQueryClient();

  const rolesQuery = useQuery({
    queryKey: ['roles', negocioId],
    queryFn: () => negocioId ? rolesService.getByNegocio(negocioId) : rolesService.getAll(),
    staleTime: 1000 * 60 * 5,
    enabled: !!negocioId,
  });

  const createRol = useMutation({
    mutationFn: rolesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['roles', negocioId] });
      message.success('Rol creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear rol');
    },
  });

  const updateRol = useMutation({
    mutationFn: rolesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['roles', negocioId] });
      message.success('Rol actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar rol');
    },
  });

  const deleteRol = useMutation({
    mutationFn: rolesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['roles', negocioId] });
      message.success('Rol eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar rol');
    },
  });

  return {
    roles: rolesQuery.data || [],
    isLoading: rolesQuery.isLoading,
    isError: rolesQuery.isError,

    createRol: createRol.mutateAsync,
    updateRol: updateRol.mutateAsync,
    deleteRol: deleteRol.mutateAsync,

    isCreating: createRol.isPending,
    isUpdating: updateRol.isPending,
    isDeleting: deleteRol.isPending,
  };
};
