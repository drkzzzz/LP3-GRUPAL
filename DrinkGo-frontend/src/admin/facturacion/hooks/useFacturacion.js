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

/* ═══ PSE – CONFIGURACIÓN ═══ */

export const useConfiguracionPse = (negocioId) => {
  return useQuery({
    queryKey: ['facturacion', 'pse', 'config', negocioId],
    queryFn: () => facturacionService.getConfiguracionPse(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 5,
  });
};

export const useGuardarConfiguracionPse = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ negocioId, data }) => facturacionService.guardarConfiguracionPse(negocioId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse', 'config'] });
    },
  });
};

export const useProbarConexionPse = () => {
  return useMutation({
    mutationFn: (negocioId) => facturacionService.probarConexionPse(negocioId),
  });
};

export const useTogglePse = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (negocioId) => facturacionService.togglePse(negocioId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse'] });
      queryClient.refetchQueries({ queryKey: ['facturacion', 'pse', 'config'] });
    },
  });
};

/* ═══ PSE – BANDEJA DE ENVÍO ═══ */

export const useBandejaPse = (negocioId, estado) => {
  return useQuery({
    queryKey: ['facturacion', 'pse', 'bandeja', negocioId, estado],
    queryFn: () => facturacionService.getBandejaPse(negocioId, estado),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 2,
  });
};

export const useEnviarDocumentoPse = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: facturacionService.enviarDocumentoPse,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse', 'bandeja'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse', 'historial'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'comprobantes'] });
    },
  });
};

export const useReenviarDocumentoPse = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: facturacionService.reenviarDocumentoPse,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse', 'bandeja'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse', 'historial'] });
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'comprobantes'] });
    },
  });
};

/* ═══ PSE – HISTORIAL DE COMUNICACIONES ═══ */

export const useHistorialPse = (negocioId) => {
  return useQuery({
    queryKey: ['facturacion', 'pse', 'historial', negocioId],
    queryFn: () => facturacionService.getHistorialPse(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 2,
  });
};


