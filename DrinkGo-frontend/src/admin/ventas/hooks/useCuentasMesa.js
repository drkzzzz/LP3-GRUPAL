/**
 * useCuentasMesa.js
 * ─────────────────
 * TanStack Query hooks para el módulo de Gestión de Mesas.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cuentasMesaService } from '../services/cuentasMesaService';
import { message } from '@/shared/utils/notifications';

/* ═══ LEER CUENTAS ABIERTAS POR SEDE ═══ */

export const useCuentasAbiertasPorSede = (sedeId) => {
  const query = useQuery({
    queryKey: ['cuentas-mesa-abiertas', sedeId],
    queryFn: () => cuentasMesaService.getAbiertasPorSede(sedeId),
    enabled: !!sedeId,
    staleTime: 1000 * 30, // 30 segundos (datos bastante dinámicos)
    refetchInterval: 1000 * 60, // refrescar automáticamente cada minuto
  });

  return {
    cuentas: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,
    refetch: query.refetch,
  };
};

/* ═══ LEER DETALLES DE UNA CUENTA ═══ */

export const useCuentaDetalle = (cuentaId) => {
  const query = useQuery({
    queryKey: ['cuenta-mesa-detalle', cuentaId],
    queryFn: () => cuentasMesaService.getDetalles(cuentaId),
    enabled: !!cuentaId,
    staleTime: 1000 * 15,
  });

  return {
    detalles: query.data || [],
    isLoading: query.isLoading,
    refetch: query.refetch,
  };
};

/* ═══ MUTACIONES ═══ */

export const useCuentasMesaMutations = (sedeId) => {
  const qc = useQueryClient();

  const invalidar = () => {
    qc.invalidateQueries({ queryKey: ['cuentas-mesa-abiertas', sedeId] });
  };

  const invalidarDetalle = (cuentaId) => {
    qc.invalidateQueries({ queryKey: ['cuenta-mesa-detalle', cuentaId] });
  };

  /* Abrir cuenta */
  const abrirCuenta = useMutation({
    mutationFn: cuentasMesaService.abrir,
    onSuccess: () => {
      message.success('Cuenta abierta exitosamente');
      invalidar();
    },
    onError: (err) => {
      message.error(err?.response?.data?.message || 'Error al abrir la cuenta');
    },
  });

  /* Agregar producto */
  const agregarProducto = useMutation({
    mutationFn: ({ cuentaId, producto }) =>
      cuentasMesaService.agregarProducto(cuentaId, producto),
    onSuccess: (_, { cuentaId }) => {
      message.success('Producto agregado');
      invalidarDetalle(cuentaId);
      invalidar();
    },
    onError: (err) => {
      message.error(err?.response?.data?.message || 'Error al agregar producto');
    },
  });

  /* Remover producto */
  const removerProducto = useMutation({
    mutationFn: ({ detalleId, cuentaId }) =>
      cuentasMesaService.removerProducto(detalleId).then((r) => ({ ...r, cuentaId })),
    onSuccess: (data) => {
      message.success('Producto eliminado');
      if (data?.cuentaId) invalidarDetalle(data.cuentaId);
      invalidar();
    },
    onError: (err) => {
      message.error(err?.response?.data?.message || 'Error al eliminar producto');
    },
  });

  /* Transferir a otra mesa */
  const transferirMesa = useMutation({
    mutationFn: ({ cuentaId, nuevaMesaId }) =>
      cuentasMesaService.transferirMesa(cuentaId, nuevaMesaId),
    onSuccess: () => {
      message.success('Cuenta transferida a nueva mesa');
      invalidar();
    },
    onError: (err) => {
      message.error(err?.response?.data?.message || 'Error al transferir la mesa');
    },
  });

  /* Transferir productos seleccionados */
  const transferirProductos = useMutation({
    mutationFn: ({ cuentaOrigenId, cuentaDestinoId, detalleIds }) =>
      cuentasMesaService.transferirProductos(cuentaOrigenId, cuentaDestinoId, detalleIds),
    onSuccess: () => {
      message.success('Productos transferidos');
      invalidar();
    },
    onError: (err) => {
      message.error(err?.response?.data?.message || 'Error al transferir productos');
    },
  });

  /* Cerrar cuenta */
  const cerrarCuenta = useMutation({
    mutationFn: ({ cuentaId, usuarioId }) =>
      cuentasMesaService.cerrarCuenta(cuentaId, usuarioId),
    onSuccess: () => {
      message.success('Cuenta cerrada y mesa liberada');
      invalidar();
      qc.invalidateQueries({ queryKey: ['ventas'] });
      qc.invalidateQueries({ queryKey: ['ventas-sesion'] });
      qc.invalidateQueries({ queryKey: ['facturacion', 'comprobantes'] });
      qc.invalidateQueries({ queryKey: ['resumen-turno'] });
      qc.invalidateQueries({ queryKey: ['movimientos'] });
    },
    onError: (err) => {
      message.error(err?.response?.data?.message || 'Error al cerrar la cuenta');
    },
  });

  return {
    abrirCuenta,
    agregarProducto,
    removerProducto,
    transferirMesa,
    transferirProductos,
    cerrarCuenta,
  };
};
