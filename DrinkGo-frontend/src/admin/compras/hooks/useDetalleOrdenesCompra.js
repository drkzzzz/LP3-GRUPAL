/**
 * useDetalleOrdenesCompra.js
 * ──────────────────────────
 * Hook de React Query para CRUD de detalle de órdenes de compra.
 * Maneja los ítems individuales de cada orden.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  detalleOrdenesCompraService,
  productosComprasService,
} from '../services/comprasService';
import { message } from '@/shared/utils/notifications';

export const useDetalleOrdenesCompra = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todos los detalles ─── */
  const detallesQuery = useQuery({
    queryKey: ['detalle-ordenes-compra', negocioId],
    queryFn: detalleOrdenesCompraService.getAll,
    enabled: !!negocioId,
  });

  /* ─── Query: productos para selects ─── */
  const productosQuery = useQuery({
    queryKey: ['productos-compras', negocioId],
    queryFn: () => productosComprasService.getAll(negocioId),
    enabled: !!negocioId,
  });

  /* ─── Mutation: crear detalle ─── */
  const createDetalle = useMutation({
    mutationFn: detalleOrdenesCompraService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-ordenes-compra'] });
      queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      message.success('Producto agregado a la orden');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al agregar producto a la orden');
    },
  });

  /* ─── Mutation: actualizar detalle (recepción, cantidades) ─── */
  const updateDetalle = useMutation({
    mutationFn: detalleOrdenesCompraService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-ordenes-compra'] });
      queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      message.success('Detalle actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar detalle');
    },
  });

  /* ─── Mutation: eliminar detalle ─── */
  const deleteDetalle = useMutation({
    mutationFn: detalleOrdenesCompraService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['detalle-ordenes-compra'] });
      queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      message.success('Producto removido de la orden');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al remover producto de la orden');
    },
  });

  /* ─── Helper: obtener detalles de una orden específica ─── */
  const getDetallesForOrden = (ordenId) => {
    if (!detallesQuery.data || !ordenId) return [];
    return detallesQuery.data.filter(
      (d) => (d.ordenCompra?.id ?? d.ordenCompraId) === ordenId,
    );
  };

  return {
    detalles: detallesQuery.data || [],
    productos: productosQuery.data || [],
    isLoading: detallesQuery.isLoading,
    isLoadingProductos: productosQuery.isLoading,

    getDetallesForOrden,

    createDetalle: createDetalle.mutateAsync,
    updateDetalle: updateDetalle.mutateAsync,
    deleteDetalle: deleteDetalle.mutateAsync,

    isCreating: createDetalle.isPending,
    isUpdating: updateDetalle.isPending,
    isDeleting: deleteDetalle.isPending,
  };
};
