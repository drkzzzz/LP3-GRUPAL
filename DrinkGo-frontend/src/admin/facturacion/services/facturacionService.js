/**
 * facturacionService.js
 * ─────────────────────
 * Servicio para el módulo de Facturación (admin).
 * Consume los endpoints REST del AdminFacturacionController.
 */
import { adminApi } from '@/admin/services/adminApi';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
};

export const facturacionService = {
  /* ═══ SERIES ═══ */

  getSeries: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/series/negocio/${negocioId}`);
    return toArray(data);
  },

  getSerieById: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/series/${id}`);
    return data;
  },

  crearSerie: async (serieData) => {
    const { data } = await adminApi.post('/admin/facturacion/series', serieData);
    return data;
  },

  actualizarSerie: async (id, serieData) => {
    const { data } = await adminApi.put(`/admin/facturacion/series/${id}`, serieData);
    return data;
  },

  eliminarSerie: async (id) => {
    const { data } = await adminApi.delete(`/admin/facturacion/series/${id}`);
    return data;
  },

  /* ═══ COMPROBANTES ═══ */

  getComprobantes: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/negocio/${negocioId}`);
    return toArray(data);
  },

  getComprobanteById: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/${id}`);
    return data;
  },

  cambiarEstadoComprobante: async (id, estado, motivoAnulacion) => {
    const payload = { estado };
    if (motivoAnulacion) payload.motivoAnulacion = motivoAnulacion;
    const { data } = await adminApi.patch(`/admin/facturacion/comprobantes/${id}/estado`, payload);
    return data;
  },

  /* ═══ NOTAS DE CRÉDITO / DÉBITO ═══ */

  emitirNotaCreditoDebito: async (payload) => {
    const { data } = await adminApi.post('/admin/facturacion/comprobantes/nota-credito-debito', payload);
    return data;
  },

  /** Obtiene ítems, notas existentes y acumulado NC para un comprobante */
  getItemsComprobante: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/${id}/items`);
    return data;
  },

  /* ═══ REEMISIÓN DE COMPROBANTE (TRAS NC MOTIVO 02) ═══ */

  reemitirComprobante: async (ncId, payload) => {
    const { data } = await adminApi.post(
      `/admin/facturacion/comprobantes/${ncId}/reemitir`,
      payload
    );
    return data;
  },

  /* ═══ PSE (Proveedor de Servicios Electrónicos) ═══ */

  getConfiguracionPse: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/pse/configuracion/${negocioId}`);
    return data;
  },

  guardarConfiguracionPse: async (negocioId, configData) => {
    const { data } = await adminApi.put(`/admin/facturacion/pse/configuracion/${negocioId}`, configData);
    return data;
  },

  probarConexionPse: async ({ negocioId, apiToken }) => {
    const { data } = await adminApi.post(`/admin/facturacion/pse/probar-conexion/${negocioId}`, { apiToken });
    if (data.success === false) throw new Error(data.message || 'Token inválido');
    return data;
  },

  togglePse: async (negocioId) => {
    const { data } = await adminApi.patch(`/admin/facturacion/pse/toggle/${negocioId}`);
    return data;
  },

  getBandejaPse: async (negocioId, estado) => {
    const params = {};
    if (estado) params.estado = estado;
    const { data } = await adminApi.get(`/admin/facturacion/pse/bandeja/${negocioId}`, { params });
    return toArray(data);
  },

  enviarDocumentoPse: async (documentoId) => {
    const { data } = await adminApi.post(`/admin/facturacion/pse/enviar/${documentoId}`);
    return data;
  },

  reenviarDocumentoPse: async (documentoId) => {
    const { data } = await adminApi.post(`/admin/facturacion/pse/reenviar/${documentoId}`);
    return data;
  },

  /* ═══ PSE – HISTORIAL DE COMUNICACIONES ═══ */

  getHistorialPse: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/pse/historial/${negocioId}`);
    return toArray(data);
  },

};
