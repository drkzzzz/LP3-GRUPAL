/**
 * useOrdenesCompra.js
 * ───────────────────
 * Hook de React Query para CRUD de órdenes de compra.
 * Incluye creación de orden con detalles (multi-step) y cambio de estado.
 * Filtra client-side por negocioId (multi-tenant).
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  ordenesCompraService,
  detalleOrdenesCompraService,
  proveedoresService,
  sedesComprasService,
  almacenesComprasService,
  productosComprasService,
} from '../services/comprasService';
import { message } from '@/shared/utils/notifications';

/**
 * Generates a sequential order number: OC-YYYYMMDD-NNN
 */
const generateNumeroOrden = (existingOrdenes) => {
  const today = new Date();
  const dateStr =
    String(today.getFullYear()) +
    String(today.getMonth() + 1).padStart(2, '0') +
    String(today.getDate()).padStart(2, '0');
  const prefix = `OC-${dateStr}-`;
  const todayOrdenes = existingOrdenes.filter(
    (o) => o.numeroOrden && o.numeroOrden.startsWith(prefix),
  );
  const nextNum = todayOrdenes.length + 1;
  return `${prefix}${String(nextNum).padStart(3, '0')}`;
};

const IGV_RATE = 0.18;

export const useOrdenesCompra = (negocioId) => {
  const queryClient = useQueryClient();

  /* ─── Query: listar todas las órdenes de compra ─── */
  const ordenesQuery = useQuery({
    queryKey: ['ordenes-compra', negocioId],
    queryFn: ordenesCompraService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((o) => o.negocio?.id === negocioId),
  });

  /* ─── Queries auxiliares para selects ─── */
  const proveedoresQuery = useQuery({
    queryKey: ['proveedores', negocioId],
    queryFn: proveedoresService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((p) => p.negocio?.id === negocioId && p.estaActivo !== false),
  });

  const sedesQuery = useQuery({
    queryKey: ['sedes-compras', negocioId],
    queryFn: sedesComprasService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((s) => s.negocio?.id === negocioId),
  });

  const almacenesQuery = useQuery({
    queryKey: ['almacenes-compras', negocioId],
    queryFn: almacenesComprasService.getAll,
    enabled: !!negocioId,
    select: (data) => data.filter((a) => a.negocio?.id === negocioId),
  });

  const productosQuery = useQuery({
    queryKey: ['productos-compras', negocioId],
    queryFn: () => productosComprasService.getAll(negocioId),
    enabled: !!negocioId,
  });

  /* ─── Mutation: crear orden de compra CON detalles ─── */
  const createOrdenWithDetalles = useMutation({
    mutationFn: async ({ headerPayload, items }) => {
      // 1. Create the order header
      const createdOrder = await ordenesCompraService.create(headerPayload);
      const ordenId = createdOrder.id;

      // 2. Create each detail item
      for (const item of items) {
        const qty = Number(item.cantidadSolicitada);
        const price = Number(item.precioUnitario);
        const subtotal = qty * price;
        const impuesto = subtotal * IGV_RATE;
        const total = subtotal + impuesto;

        await detalleOrdenesCompraService.create({
          ordenCompra: { id: ordenId },
          producto: { id: Number(item.productoId) },
          cantidadSolicitada: qty,
          cantidadRecibida: 0,
          precioUnitario: price,
          subtotal: Number(subtotal.toFixed(2)),
          impuesto: Number(impuesto.toFixed(2)),
          total: Number(total.toFixed(2)),
          estaActivo: true,
        });
      }

      return createdOrder;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      queryClient.invalidateQueries({ queryKey: ['detalle-ordenes-compra'] });
      message.success('Orden de compra creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || err.message || 'Error al crear orden de compra');
    },
  });

  /* ─── Mutation: actualizar orden de compra (solo header) ─── */
  const updateOrden = useMutation({
    mutationFn: ordenesCompraService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      message.success('Orden de compra actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar orden de compra');
    },
  });

  /* ─── Mutation: cambiar estado de orden (recibida / cancelada) ─── */
  const changeEstado = useMutation({
    mutationFn: async ({ orden, nuevoEstado }) => {
      // Asegurar que todos los campos required del backend están presentes
      const payload = {
        id: orden.id,
        negocio: { id: orden.negocio?.id || negocioId },
        numeroOrden: orden.numeroOrden,
        proveedor: { id: orden.proveedor?.id },
        sede: { id: orden.sede?.id },
        almacen: { id: orden.almacen?.id },
        estado: nuevoEstado,
        subtotal: orden.subtotal || 0,
        impuestos: orden.impuestos || 0,
        total: orden.total || 0,
        fechaOrden: orden.fechaOrden,
        notas: orden.notas || '',
      };

      // Agregar campos opcionales si existen
      if (orden.usuario?.id) {
        payload.usuario = { id: orden.usuario.id };
      }
      if (orden.creadoPor?.id) {
        payload.creadoPor = { id: orden.creadoPor.id };
      }

      return ordenesCompraService.update(payload);
    },
    onSuccess: (_, { nuevoEstado }) => {
      queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      queryClient.invalidateQueries({ queryKey: ['detalle-ordenes-compra'] });
      const label = nuevoEstado === 'cancelada' ? 'cancelada' : 'recibida';
      message.success(`Orden ${label} exitosamente`);
    },
    onError: (err) => {
      console.error('Error al cambiar estado:', err.response?.data);
      message.error(err.response?.data?.message || 'Error al cambiar estado de la orden');
    },
  });

  /* ─── Mutation: eliminar orden de compra ─── */
  const deleteOrden = useMutation({
    mutationFn: ordenesCompraService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ordenes-compra'] });
      queryClient.invalidateQueries({ queryKey: ['detalle-ordenes-compra'] });
      message.success('Orden de compra eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar orden de compra');
    },
  });

  return {
    ordenes: ordenesQuery.data || [],
    proveedores: proveedoresQuery.data || [],
    sedes: sedesQuery.data || [],
    almacenes: almacenesQuery.data || [],
    productos: productosQuery.data || [],
    isLoading: ordenesQuery.isLoading,

    generateNumeroOrden: () =>
      generateNumeroOrden(ordenesQuery.data || []),

    createOrdenWithDetalles: createOrdenWithDetalles.mutateAsync,
    updateOrden: updateOrden.mutateAsync,
    changeEstado: changeEstado.mutateAsync,
    deleteOrden: deleteOrden.mutateAsync,

    isCreating: createOrdenWithDetalles.isPending,
    isUpdating: updateOrden.isPending,
    isChangingEstado: changeEstado.isPending,
    isDeleting: deleteOrden.isPending,
  };
};
