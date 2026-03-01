/**
 * useFacturacion.js
 * ─────────────────
 * React Query hooks para el módulo de Facturación.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { facturacionService } from '../services/facturacionService';

/* ═══ SERIES ═══ */

export const useSeries = (negocioId) => {
  return useQuery({
    queryKey: ['facturacion', 'series', negocioId],
    queryFn: () => facturacionService.getSeries(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 5,
  });
};

export const useCrearSerie = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: facturacionService.crearSerie,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'series'] });
    },
  });
};

export const useActualizarSerie = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }) => facturacionService.actualizarSerie(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'series'] });
    },
  });
};

export const useEliminarSerie = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: facturacionService.eliminarSerie,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'series'] });
    },
  });
};

/* ═══ COMPROBANTES ═══ */

export const useComprobantes = (negocioId) => {
  return useQuery({
    queryKey: ['facturacion', 'comprobantes', negocioId],
    queryFn: () => facturacionService.getComprobantes(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 2,
  });
};

export const useCambiarEstadoComprobante = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, estado }) => facturacionService.cambiarEstadoComprobante(id, estado),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'comprobantes'] });
    },
  });
};

/* ═══ MÉTODOS DE PAGO ═══ */

export const useMetodosPago = (negocioId) => {
  return useQuery({
    queryKey: ['facturacion', 'metodos-pago', negocioId],
    queryFn: () => facturacionService.getMetodosPago(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 5,
  });
};

export const useCrearMetodoPago = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: facturacionService.crearMetodoPago,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'metodos-pago'] });
    },
  });
};

export const useActualizarMetodoPago = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }) => facturacionService.actualizarMetodoPago(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'metodos-pago'] });
    },
  });
};

export const useEliminarMetodoPago = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: facturacionService.eliminarMetodoPago,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'metodos-pago'] });
    },
  });
};
