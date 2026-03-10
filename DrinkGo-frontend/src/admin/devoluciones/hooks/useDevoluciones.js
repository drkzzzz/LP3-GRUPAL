/**
 * useDevoluciones.js
 * ──────────────────
 * Hook de React Query para CRUD de devoluciones.
 * Consulta por negocioId (multi-tenant) y filtra client-side
 * por origen: 'clientes' (venta_id) o 'proveedores' (pedido_id / compra).
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { devolucionesService } from '../services/devolucionesService';
import { message } from '@/shared/utils/notifications';

export const useDevoluciones = (negocioId, origen = 'todos') => {
  const queryClient = useQueryClient();

  /* ─── Query: listar devoluciones por negocio ─── */
  const devolucionesQuery = useQuery({
    queryKey: ['devoluciones', negocioId, origen],
    queryFn: () => devolucionesService.getByNegocio(negocioId),
    enabled: !!negocioId,
    select: (data) => {
      let filtered = data;

      if (origen === 'clientes') {
        filtered = filtered.filter((d) => d.venta || d.ventaId || d.cliente || d.clienteId);
      } else if (origen === 'proveedores') {
        filtered = filtered.filter((d) => !d.venta && !d.ventaId);
      }

      return filtered;
    },
  });

  /* ─── Mutation: crear devolución ─── */
  const createDevolucion = useMutation({
    mutationFn: devolucionesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['devoluciones'] });
      message.success('Devolución registrada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al registrar devolución');
    },
  });

  /* ─── Mutation: actualizar devolución ─── */
  const updateDevolucion = useMutation({
    mutationFn: devolucionesService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['devoluciones'] });
      message.success('Devolución actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar devolución');
    },
  });

  /* ─── Mutation: eliminar devolución ─── */
  const deleteDevolucion = useMutation({
    mutationFn: devolucionesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['devoluciones'] });
      message.success('Devolución eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar devolución');
    },
  });

  /* ─── Mutation: aprobar devolución (solo admin) ─── */
  const aprobarDevolucion = useMutation({
    mutationFn: ({ id, usuarioId }) => devolucionesService.aprobar(id, usuarioId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['devoluciones'] });
      queryClient.invalidateQueries({ queryKey: ['ventas'] });
      queryClient.invalidateQueries({ queryKey: ['ventas-sesion'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'comprobantes'] });
      queryClient.invalidateQueries({ queryKey: ['resumen-turno'] });
      queryClient.invalidateQueries({ queryKey: ['movimientos'] });
      queryClient.invalidateQueries({ queryKey: ['lotes-inventario'] });
      queryClient.invalidateQueries({ queryKey: ['stock-inventario'] });
      message.success('Devolución aprobada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al aprobar devolución');
    },
  });

  /* ─── Mutation: rechazar devolución (solo admin) ─── */
  const rechazarDevolucion = useMutation({
    mutationFn: ({ id, usuarioId, razon }) =>
      devolucionesService.rechazar(id, usuarioId, razon),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['devoluciones'] });
      message.success('Devolución rechazada');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al rechazar devolución');
    },
  });

  return {
    devoluciones: devolucionesQuery.data || [],
    isLoading: devolucionesQuery.isLoading,
    isError: devolucionesQuery.isError,
    error: devolucionesQuery.error,
    refetch: devolucionesQuery.refetch,

    createDevolucion: createDevolucion.mutateAsync,
    isCreating: createDevolucion.isPending,

    updateDevolucion: updateDevolucion.mutateAsync,
    isUpdating: updateDevolucion.isPending,

    deleteDevolucion: deleteDevolucion.mutateAsync,
    isDeleting: deleteDevolucion.isPending,

    aprobarDevolucion: aprobarDevolucion.mutateAsync,
    isAprobando: aprobarDevolucion.isPending,

    rechazarDevolucion: rechazarDevolucion.mutateAsync,
    isRechazando: rechazarDevolucion.isPending,
  };
};
