/**
 * useMarcas.js
 * ────────────
 * Hook de React Query para el CRUD de Marcas.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { marcasService } from '../services/catalogoService';
import { message } from '@/shared/utils/notifications';

export const useMarcas = () => {
  const queryClient = useQueryClient();

  const marcasQuery = useQuery({
    queryKey: ['marcas'],
    queryFn: marcasService.getAll,
  });

  const createMarca = useMutation({
    mutationFn: marcasService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['marcas'] });
      message.success('Marca creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear marca');
    },
  });

  const updateMarca = useMutation({
    mutationFn: marcasService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['marcas'] });
      message.success('Marca actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar marca');
    },
  });

  const deleteMarca = useMutation({
    mutationFn: marcasService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['marcas'] });
      message.success('Marca eliminada exitosamente');
    },
    onError: (err) => {
      const msg = err.response?.data?.message || err.response?.data || 'Error al eliminar marca';
      message.error(msg);
    },
  });

  return {
    marcas: marcasQuery.data || [],
    isLoading: marcasQuery.isLoading,
    isError: marcasQuery.isError,

    createMarca: createMarca.mutateAsync,
    updateMarca: updateMarca.mutateAsync,
    deleteMarca: deleteMarca.mutateAsync,

    isCreating: createMarca.isPending,
    isUpdating: updateMarca.isPending,
    isDeleting: deleteMarca.isPending,
  };
};
