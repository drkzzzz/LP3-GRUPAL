/**
 * useCategorias.js
 * ────────────────
 * Hook de React Query para el CRUD de Categorías.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { categoriasService } from '../services/catalogoService';
import { message } from '@/shared/utils/notifications';

export const useCategorias = (negocioId) => {
  const queryClient = useQueryClient();

  const categoriasQuery = useQuery({
    queryKey: ['categorias', negocioId],
    queryFn: () => categoriasService.getAll(negocioId),
    enabled: !!negocioId,
  });

  const createCategoria = useMutation({
    mutationFn: categoriasService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categorias'] });
      message.success('Categoría creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear categoría');
    },
  });

  const updateCategoria = useMutation({
    mutationFn: categoriasService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categorias'] });
      message.success('Categoría actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar categoría');
    },
  });

  const deleteCategoria = useMutation({
    mutationFn: categoriasService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categorias'] });
      message.success('Categoría eliminada exitosamente');
    },
    onError: (err) => {
      const msg = err.response?.data?.message || err.response?.data || 'Error al eliminar categoría';
      message.error(msg);
    },
  });

  return {
    categorias: categoriasQuery.data || [],
    isLoading: categoriasQuery.isLoading,
    isError: categoriasQuery.isError,

    createCategoria: createCategoria.mutateAsync,
    updateCategoria: updateCategoria.mutateAsync,
    deleteCategoria: deleteCategoria.mutateAsync,

    isCreating: createCategoria.isPending,
    isUpdating: updateCategoria.isPending,
    isDeleting: deleteCategoria.isPending,
  };
};
