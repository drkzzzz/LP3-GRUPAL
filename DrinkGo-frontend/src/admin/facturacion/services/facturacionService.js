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

  /**
   * Obtiene las series de comprobantes de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de series (facturas, boletas)
   * @example
   * const series = await facturacionService.getSeries(5);
   */
  getSeries: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/series/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene una serie por su ID.
   * @param {number} id - ID de la serie
   * @returns {Promise<Object>} Serie encontrada
   * @example
   * const serie = await facturacionService.getSerieById(1);
   */
  getSerieById: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/series/${id}`);
    return data;
  },

  /**
   * Crea una nueva serie de comprobantes.
   * @param {Object} serieData - Datos de la serie (tipo, prefijo, numeración)
   * @returns {Promise<Object>} Serie creada
   * @example
   * const serie = await facturacionService.crearSerie({ tipo: 'FACTURA', prefijo: 'F001' });
   */
  crearSerie: async (serieData) => {
    const { data } = await adminApi.post('/admin/facturacion/series', serieData);
    return data;
  },

  /**
   * Actualiza una serie existente.
   * @param {number} id - ID de la serie
   * @param {Object} serieData - Datos a actualizar
   * @returns {Promise<Object>} Serie actualizada
   * @example
   * const serie = await facturacionService.actualizarSerie(1, { prefijo: 'F002' });
   */
  actualizarSerie: async (id, serieData) => {
    const { data } = await adminApi.put(`/admin/facturacion/series/${id}`, serieData);
    return data;
  },

  /**
   * Elimina una serie.
   * @param {number} id - ID de la serie a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await facturacionService.eliminarSerie(1);
   */
  eliminarSerie: async (id) => {
    const { data } = await adminApi.delete(`/admin/facturacion/series/${id}`);
    return data;
  },

  /* ═══ COMPROBANTES ═══ */

  /**
   * Obtiene los comprobantes de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de comprobantes emitidos
   * @example
   * const comprobantes = await facturacionService.getComprobantes(5);
   */
  getComprobantes: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene un comprobante por su ID.
   * @param {number} id - ID del comprobante
   * @returns {Promise<Object>} Comprobante encontrado
   * @example
   * const comprobante = await facturacionService.getComprobanteById(100);
   */
  getComprobanteById: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/${id}`);
    return data;
  },

  /**
   * Cambia el estado de un comprobante (anulado, aceptado, etc.).
   * @param {number} id - ID del comprobante
   * @param {string} estado - Nuevo estado
   * @param {string} [motivoAnulacion] - Motivo si se anula
   * @returns {Promise<Object>} Comprobante actualizado
   * @example
   * await facturacionService.cambiarEstadoComprobante(100, 'ANULADO', 'Error en datos');
   */
  cambiarEstadoComprobante: async (id, estado, motivoAnulacion, usuarioId) => {
    const payload = { estado };
    if (motivoAnulacion) payload.motivoAnulacion = motivoAnulacion;
    if (usuarioId) payload.usuarioId = String(usuarioId);
    const { data } = await adminApi.patch(`/admin/facturacion/comprobantes/${id}/estado`, payload);
    return data;
  },

  /* ═══ NOTAS DE CRÉDITO / DÉBITO ═══ */

  /**
   * Emite una nota de crédito o débito.
   * @param {Object} payload - Datos de la nota (tipo, comprobanteId, motivo, items)
   * @returns {Promise<Object>} Nota emitida
   * @example
   * const nota = await facturacionService.emitirNotaCreditoDebito({
   *   tipo: 'CREDITO',
   *   comprobanteId: 100,
   *   motivo: '02',
   *   items: [...]
   * });
   */
  emitirNotaCreditoDebito: async (payload) => {
    const { data } = await adminApi.post('/admin/facturacion/comprobantes/nota-credito-debito', payload);
    return data;
  },

  /**
   * Obtiene ítems, notas existentes y acumulado NC para un comprobante.
   * @param {number} id - ID del comprobante
   * @returns {Promise<Object>} Detalle de ítems y notas asociadas
   * @example
   * const items = await facturacionService.getItemsComprobante(100);
   */
  getItemsComprobante: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/${id}/items`);
    return data;
  },

  /* ═══ REEMISIÓN DE COMPROBANTE (TRAS NC MOTIVO 02) ═══ */

  /**
   * Reemite un comprobante tras una nota de crédito motivo 02.
   * @param {number} ncId - ID de la nota de crédito
   * @param {Object} payload - Datos del nuevo comprobante
   * @returns {Promise<Object>} Comprobante reemitido
   * @example
   * const reemitido = await facturacionService.reemitirComprobante(50, { ... });
   */
  reemitirComprobante: async (ncId, payload) => {
    const { data } = await adminApi.post(
      `/admin/facturacion/comprobantes/${ncId}/reemitir`,
      payload
    );
    return data;
  },

  /* ═══ PSE (Proveedor de Servicios Electrónicos) ═══ */

  /**
   * Obtiene la configuración del PSE de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Object>} Configuración PSE (proveedor, apiToken, etc.)
   * @example
   * const config = await facturacionService.getConfiguracionPse(5);
   */
  getConfiguracionPse: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/pse/configuracion/${negocioId}`);
    return data;
  },

  /**
   * Guarda la configuración del PSE.
   * @param {number} negocioId - ID del negocio
   * @param {Object} configData - Datos de configuración PSE
   * @returns {Promise<Object>} Configuración guardada
   * @example
   * await facturacionService.guardarConfiguracionPse(5, { proveedor: 'SUNAT', apiToken: '...' });
   */
  guardarConfiguracionPse: async (negocioId, configData) => {
    const { data } = await adminApi.put(`/admin/facturacion/pse/configuracion/${negocioId}`, configData);
    return data;
  },

  /**
   * Prueba la conexión con el PSE usando el token.
   * @param {Object} params - Parámetros de prueba
   * @param {number} params.negocioId - ID del negocio
   * @param {string} params.apiToken - Token de API del PSE
   * @returns {Promise<Object>} Resultado de la prueba
   * @throws {Error} Si el token es inválido
   * @example
   * await facturacionService.probarConexionPse({ negocioId: 5, apiToken: '...' });
   */
  probarConexionPse: async ({ negocioId, apiToken }) => {
    const { data } = await adminApi.post(`/admin/facturacion/pse/probar-conexion/${negocioId}`, { apiToken });
    if (data.success === false) throw new Error(data.message || 'Token inválido');
    return data;
  },

  /**
   * Activa o desactiva el PSE de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Object>} Estado actualizado del PSE
   * @example
   * await facturacionService.togglePse(5);
   */
  togglePse: async (negocioId) => {
    const { data } = await adminApi.patch(`/admin/facturacion/pse/toggle/${negocioId}`);
    return data;
  },

  /**
   * Obtiene la bandeja de documentos PSE (pendientes, enviados, rechazados).
   * @param {number} negocioId - ID del negocio
   * @param {string} [estado] - Filtrar por estado (opcional)
   * @returns {Promise<Array>} Lista de documentos PSE
   * @example
   * const pendientes = await facturacionService.getBandejaPse(5, 'PENDIENTE');
   */
  getBandejaPse: async (negocioId, estado) => {
    const params = {};
    if (estado) params.estado = estado;
    const { data } = await adminApi.get(`/admin/facturacion/pse/bandeja/${negocioId}`, { params });
    return toArray(data);
  },

  /**
   * Envía un documento al PSE.
   * @param {number} documentoId - ID del documento
   * @returns {Promise<Object>} Resultado del envío
   * @example
   * await facturacionService.enviarDocumentoPse(100);
   */
  enviarDocumentoPse: async (documentoId) => {
    const { data } = await adminApi.post(`/admin/facturacion/pse/enviar/${documentoId}`);
    return data;
  },

  /**
   * Reenvía un documento al PSE.
   * @param {number} documentoId - ID del documento
   * @returns {Promise<Object>} Resultado del reenvío
   * @example
   * await facturacionService.reenviarDocumentoPse(100);
   */
  reenviarDocumentoPse: async (documentoId) => {
    const { data } = await adminApi.post(`/admin/facturacion/pse/reenviar/${documentoId}`);
    return data;
  },

  /* ═══ PSE – HISTORIAL DE COMUNICACIONES ═══ */

  /**
   * Obtiene el historial de comunicaciones con el PSE.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de comunicaciones (envíos, respuestas)
   * @example
   * const historial = await facturacionService.getHistorialPse(5);
   */
  getHistorialPse: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/pse/historial/${negocioId}`);
    return toArray(data);
  },

};
