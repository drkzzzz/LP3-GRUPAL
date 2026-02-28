/**
 * useProveedores.js
 * ─────────────────
 * Hook de React Query para CRUD de proveedores.
 * Filtra client-side por negocioId (multi-tenant).
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { proveedoresService } from '../services/comprasService';
import { message } from '@/shared/utils/notifications';

export const useProveedores = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todos los proveedores ─── */
  const proveedoresQuery = useQuery({
    queryKey: ['proveedores', negocioId],
    queryFn: proveedoresService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((p) => p.negocio?.id === negocioId),
  });

  /* ─── Mutation: crear proveedor ─── */
  const createProveedor = useMutation({
    mutationFn: proveedoresService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['proveedores'] });
      message.success('Proveedor creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear proveedor');
    },
  });

  /* ─── Mutation: actualizar proveedor ─── */
  const updateProveedor = useMutation({
    mutationFn: proveedoresService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['proveedores'] });
      message.success('Proveedor actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar proveedor');
    },
  });

  /* ─── Mutation: eliminar (soft delete) proveedor ─── */
  const deleteProveedor = useMutation({
    mutationFn: proveedoresService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['proveedores'] });
      message.success('Proveedor eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar proveedor');
    },
  });

  return {
    proveedores: proveedoresQuery.data || [],
    isLoading: proveedoresQuery.isLoading,
    isError: proveedoresQuery.isError,

    createProveedor: createProveedor.mutateAsync,
    updateProveedor: updateProveedor.mutateAsync,
    deleteProveedor: deleteProveedor.mutateAsync,

    isCreating: createProveedor.isPending,
    isUpdating: updateProveedor.isPending,
    isDeleting: deleteProveedor.isPending,
  };
};
