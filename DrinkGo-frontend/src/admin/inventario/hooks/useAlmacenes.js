/**
 * useAlmacenes.js
 * ───────────────
 * Hook de React Query para CRUD de almacenes.
 * Se usa tanto en el CRUD de AlmacenesTab como en selects de otros formularios.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { almacenesService, sedesService } from '../services/inventarioService';
import { message } from '@/shared/utils/notifications';

export const useAlmacenes = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Queries ─── */
  const almacenesQuery = useQuery({
    queryKey: ['almacenes', negocioId],
    queryFn: almacenesService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((a) => a.negocio?.id === negocioId),
  });

  const sedesQuery = useQuery({
    queryKey: ['sedes', negocioId],
    queryFn: sedesService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((s) => s.negocio?.id === negocioId),
  });

  /* ─── Mutations ─── */
  const createMutation = useMutation({
    mutationFn: almacenesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['almacenes'] });
      message.success('Almacén creado correctamente');
    },
    onError: () => message.error('Error al crear el almacén'),
  });

  const updateMutation = useMutation({
    mutationFn: almacenesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['almacenes'] });
      message.success('Almacén actualizado correctamente');
    },
    onError: () => message.error('Error al actualizar el almacén'),
  });

  const deleteMutation = useMutation({
    mutationFn: almacenesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['almacenes'] });
      message.success('Almacén eliminado correctamente');
    },
    onError: () => message.error('Error al eliminar el almacén'),
  });

  return {
    almacenes: almacenesQuery.data || [],
    sedes: sedesQuery.data || [],
    isLoading: almacenesQuery.isLoading,
    isError: almacenesQuery.isError,
    createAlmacen: createMutation.mutate,
    updateAlmacen: updateMutation.mutate,
    deleteAlmacen: deleteMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};
