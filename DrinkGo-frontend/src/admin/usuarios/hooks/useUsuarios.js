/**
 * useUsuarios.js
 * ──────────────
 * Hook TanStack Query para CRUD de Usuarios del negocio.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { usuariosService } from '../services/usuariosClientesService';
import { message } from '@/shared/utils/notifications';

export const useUsuarios = (negocioId) => {
  const queryClient = useQueryClient();

  const usuariosQuery = useQuery({
    queryKey: ['usuarios', negocioId],
    queryFn: () =>
      negocioId
        ? usuariosService.getByNegocio(negocioId)
        : usuariosService.getAll(),
    enabled: true,
    staleTime: 1000 * 60 * 3,
  });

  const createUsuario = useMutation({
    mutationFn: usuariosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear usuario');
    },
  });

  const updateUsuario = useMutation({
    mutationFn: usuariosService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar usuario');
    },
  });

  const deleteUsuario = useMutation({
    mutationFn: usuariosService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario desactivado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar usuario');
    },
  });

  return {
    usuarios: usuariosQuery.data || [],
    isLoading: usuariosQuery.isLoading,
    isError: usuariosQuery.isError,

    createUsuario: createUsuario.mutateAsync,
    updateUsuario: updateUsuario.mutateAsync,
    deleteUsuario: deleteUsuario.mutateAsync,

    isCreating: createUsuario.isPending,
    isUpdating: updateUsuario.isPending,
    isDeleting: deleteUsuario.isPending,
  };
};
