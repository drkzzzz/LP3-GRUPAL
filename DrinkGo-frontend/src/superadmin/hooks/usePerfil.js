import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { perfilService } from '../services/perfilService';
import { message } from '@/shared/utils/notifications';

export const usePerfil = () => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['perfil'],
    queryFn: perfilService.getPerfil,
    staleTime: 1000 * 60 * 5,
  });

  const updateMutation = useMutation({
    mutationFn: perfilService.updatePerfil,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['perfil'] });
      message.success(data.message || 'Perfil actualizado exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.error || 'Error al actualizar el perfil');
    },
  });

  const changePasswordMutation = useMutation({
    mutationFn: ({ contrasenaActual, nuevaContrasena, confirmarContrasena }) =>
      perfilService.changePassword(contrasenaActual, nuevaContrasena, confirmarContrasena),
    onSuccess: (data) => {
      message.success(data.message || 'Contraseña actualizada exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.error || 'Error al cambiar la contraseña');
    },
  });

  return {
    perfil: query.data || null,
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
    refetch: query.refetch,

    updatePerfil: updateMutation.mutateAsync,
    isUpdating: updateMutation.isPending,

    changePassword: changePasswordMutation.mutateAsync,
    isChangingPassword: changePasswordMutation.isPending,
  };
};
