/**
 * useFacturacion.js
 * ─────────────────
 * React Query hooks para el módulo de Facturación.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { facturacionService } from '../services/facturacionService';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

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
    mutationFn: ({ negocioId, apiToken }) => facturacionService.probarConexionPse({ negocioId, apiToken }),
  });
};

export const useTogglePse = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (negocioId) => facturacionService.togglePse(negocioId),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['facturacion', 'pse'] });
      queryClient.refetchQueries({ queryKey: ['facturacion', 'pse', 'config'] });
      // Actualizar tienePse en el store para que el sidebar reaccione de inmediato
      const { negocio, setNegocio } = useAdminAuthStore.getState();
      if (negocio && data?.tienePse !== undefined) {
        setNegocio({ ...negocio, tienePse: data.tienePse });
      }
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
    // Auto-polling cada 5 segundos mientras haya documentos en estado "enviado"
    refetchInterval: (query) => {
      const data = query.state.data;
      if (Array.isArray(data) && data.some((d) => d.estadoDocumento === 'enviado')) {
        return 5000;
      }
      return false;
    },
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


