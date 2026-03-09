/**
 * useVentas.js
 * ────────────
 * Hook para operaciones de ventas POS.
 * TanStack Query para cache, mutations para crear/anular.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ventasService } from '../services/ventasService';
import { invalidarCacheProductos } from '../services/productosAdapter';
import { message } from '@/shared/utils/notifications';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ═══ LISTAR VENTAS POR NEGOCIO ═══ */

export const useVentas = () => {
  const { negocio, sede, user, getAlcance, cajaAsignada } = useAdminAuthStore();
  const negocioId = negocio?.id;
  const sedeId = sede?.id;
  const userId = user?.id;
  const alcanceHistorial = getAlcance('m.ventas.historial');

  const query = useQuery({
    queryKey: ['ventas', negocioId, sedeId],
    queryFn: () => ventasService.getByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 2,
    select: (data) => {
      let filtered = data;
      if (sedeId) {
        filtered = filtered.filter((v) => v.sede?.id === sedeId);
      }
      // Si alcance es caja_asignada, mostrar solo ventas propias del cajero + del día
      if (alcanceHistorial === 'caja_asignada' && cajaAsignada) {
        const hoy = new Date().toISOString().slice(0, 10);
        filtered = filtered.filter(
          (v) =>
            v.sesionCaja?.caja?.id === cajaAsignada.id &&
            v.usuario?.id === userId &&
            (v.fechaVenta || v.creadoEn)?.slice(0, 10) === hoy,
        );
      }
      return filtered;
    },
  });

  return {
    ventas: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,
    refetch: query.refetch,
    alcanceHistorial,
  };
};

/* ═══ VENTAS POR SESIÓN ═══ */

export const useVentasBySesion = (sesionCajaId) => {
  const query = useQuery({
    queryKey: ['ventas-sesion', sesionCajaId],
    queryFn: () => ventasService.getBySesion(sesionCajaId),
    enabled: !!sesionCajaId,
    staleTime: 1000 * 60,
  });

  return {
    ventas: query.data || [],
    isLoading: query.isLoading,
  };
};

/* ═══ DETALLE DE UNA VENTA ═══ */

export const useVentaDetalle = (ventaId) => {
  const { negocio } = useAdminAuthStore();

  const ventaQuery = useQuery({
    queryKey: ['venta', ventaId],
    queryFn: () => ventasService.getById(ventaId, negocio?.id),
    enabled: !!ventaId && !!negocio?.id,
  });

  const detalleQuery = useQuery({
    queryKey: ['venta-detalle', ventaId],
    queryFn: () => ventasService.getDetalle(ventaId),
    enabled: !!ventaId,
  });

  const pagosQuery = useQuery({
    queryKey: ['venta-pagos', ventaId],
    queryFn: () => ventasService.getPagos(ventaId),
    enabled: !!ventaId,
  });

  return {
    venta: ventaQuery.data || null,
    detalle: detalleQuery.data || [],
    pagos: pagosQuery.data || [],
    isLoading: ventaQuery.isLoading || detalleQuery.isLoading || pagosQuery.isLoading,
  };
};

/* ═══ CREAR VENTA POS ═══ */

export const useCrearVenta = () => {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: ventasService.crear,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ventas'] });
      queryClient.invalidateQueries({ queryKey: ['ventas-sesion'] });
      queryClient.invalidateQueries({ queryKey: ['resumen-turno'] });
      queryClient.invalidateQueries({ queryKey: ['movimientos'] });
      // Refrescar comprobantes en módulo de facturación sin recargar la página
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'comprobantes'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse', 'bandeja'] });
      // Invalidar cache de productos para reflejar el nuevo stock en el POS
      invalidarCacheProductos();
      queryClient.invalidateQueries({ queryKey: ['productos-pos'] });
      message.success('Venta registrada exitosamente');
    },
    onError: async (err) => {
      const errorMsg = err.response?.data?.error || 'Error al registrar venta';

      // Si el error es por stock insuficiente, refrescar stock automáticamente
      if (errorMsg.toLowerCase().includes('stock insuficiente')) {
        const { useCartStore } = await import('../stores/cartStore');
        await useCartStore.getState().refreshStock();
        message.error(errorMsg + '. El stock se ha actualizado.');
      } else {
        message.error(errorMsg);
      }
    },
  });

  return {
    crearVenta: mutation.mutateAsync,
    isCreating: mutation.isPending,
  };
};

/* ═══ ANULAR VENTA ═══ */

export const useAnularVenta = () => {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: ventasService.anular,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ventas'] });
      queryClient.invalidateQueries({ queryKey: ['ventas-sesion'] });
      queryClient.invalidateQueries({ queryKey: ['resumen-turno'] });
      queryClient.invalidateQueries({ queryKey: ['movimientos'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'comprobantes'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse', 'bandeja'] });
      queryClient.invalidateQueries({ queryKey: ['devoluciones'] });
      invalidarCacheProductos();
      queryClient.invalidateQueries({ queryKey: ['productos-pos'] });
      message.success('Venta anulada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al anular venta');
    },
  });

  return {
    anularVenta: mutation.mutateAsync,
    isAnulando: mutation.isPending,
  };
};

/* ═══ MÉTODOS DE PAGO ═══ */

export const useMetodosPago = () => {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;

  const query = useQuery({
    queryKey: ['metodos-pago-pos', negocioId],
    queryFn: () => ventasService.getMetodosPago(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 30,
    retry: 3,
    retryDelay: 1000,
  });

  if (query.isError) {
    console.error('[useMetodosPago] Error al cargar métodos de pago:', query.error);
  }

  return {
    metodosPago: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,
    refetch: query.refetch,
  };
};
