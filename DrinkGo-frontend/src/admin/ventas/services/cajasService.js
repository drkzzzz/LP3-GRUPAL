/**
 * cajasService.js
 * ───────────────
 * Servicio para gestión de cajas registradoras y sesiones (turnos).
 * Consume los endpoints REST del PosController.
 */
import { adminApi } from '@/admin/services/adminApi';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
};

export const cajasService = {
  /* ═══ CAJAS REGISTRADORAS ═══ */

  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/cajas/negocio/${negocioId}`);
    return toArray(data);
  },

  getById: async (cajaId, negocioId) => {
    const { data } = await adminApi.get(`/pos/cajas/${cajaId}/negocio/${negocioId}`);
    return data;
  },

  crear: async (cajaData) => {
    const { data } = await adminApi.post('/pos/cajas', cajaData);
    return data;
  },

  actualizar: async (cajaData) => {
    const { data } = await adminApi.put('/pos/cajas', cajaData);
    return data;
  },

  /* ═══ SESIONES DE CAJA (TURNOS) ═══ */

  abrirCaja: async (request) => {
    const { data } = await adminApi.post('/pos/sesiones/abrir', request);
    return data;
  },

  cerrarCaja: async (request) => {
    const { data } = await adminApi.post('/pos/sesiones/cerrar', request);
    return data;
  },

  getSesionActiva: async (usuarioId) => {
    const { data } = await adminApi.get(`/pos/sesiones/activa/usuario/${usuarioId}`);
    return data;
  },

  getSesionesByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/sesiones/negocio/${negocioId}`);
    return toArray(data);
  },

  getSesionesByCaja: async (cajaId) => {
    const { data } = await adminApi.get(`/pos/sesiones/caja/${cajaId}`);
    return toArray(data);
  },

  getResumenTurno: async (sesionCajaId) => {
    const { data } = await adminApi.get(`/pos/sesiones/${sesionCajaId}/resumen`);
    return data;
  },

  /* ═══ MOVIMIENTOS DE CAJA ═══ */

  registrarMovimiento: async (request) => {
    const { data } = await adminApi.post('/pos/movimientos', request);
    return data;
  },

  getMovimientos: async (sesionCajaId) => {
    const { data } = await adminApi.get(`/pos/movimientos/sesion/${sesionCajaId}`);
    return toArray(data);
  },
};
