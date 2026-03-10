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

  /**
   * Obtiene todas las cajas registradoras de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de cajas del negocio
   * @example
   * const cajas = await cajasService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/cajas/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene una caja registradora por ID.
   * @param {number} cajaId - ID de la caja
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Object>} Caja encontrada
   * @example
   * const caja = await cajasService.getById(1, 5);
   */
  getById: async (cajaId, negocioId) => {
    const { data } = await adminApi.get(`/pos/cajas/${cajaId}/negocio/${negocioId}`);
    return data;
  },

  /**
   * Crea una nueva caja registradora.
   * @param {Object} cajaData - Datos de la caja a crear
   * @returns {Promise<Object>} Caja creada
   * @example
   * const nuevaCaja = await cajasService.crear({ nombre: 'Caja 1', negocioId: 5 });
   */
  crear: async (cajaData) => {
    const { data } = await adminApi.post('/pos/cajas', cajaData);
    return data;
  },

  /**
   * Actualiza una caja registradora existente.
   * @param {Object} cajaData - Datos de la caja a actualizar
   * @returns {Promise<Object>} Caja actualizada
   * @example
   * const actualizada = await cajasService.actualizar({ cajaId: 1, nombre: 'Caja Principal' });
   */
  actualizar: async (cajaData) => {
    const { data } = await adminApi.put('/pos/cajas', cajaData);
    return data;
  },

  /**
   * Habilita o deshabilita una caja registradora.
   * @param {number} cajaId - ID de la caja
   * @returns {Promise<Object>} Caja con estado actualizado
   * @example
   * await cajasService.toggleHabilitada(1);
   */
  toggleHabilitada: async (cajaId) => {
    const { data } = await adminApi.patch(`/pos/cajas/${cajaId}/toggle-habilitada`);
    return data;
  },

  /* ═══ SESIONES DE CAJA (TURNOS) ═══ */

  /**
   * Abre una sesión de caja (turno).
   * @param {Object} request - Datos de apertura
   * @param {number} request.cajaId - ID de la caja
   * @param {number} request.usuarioId - ID del usuario
   * @param {number} request.montoInicial - Monto inicial en caja
   * @returns {Promise<Object>} Sesión de caja creada
   * @example
   * const sesion = await cajasService.abrirCaja({ cajaId: 1, usuarioId: 3, montoInicial: 100.00 });
   */
  abrirCaja: async (request) => {
    const { data } = await adminApi.post('/pos/sesiones/abrir', request);
    return data;
  },

  /**
   * Cierra una sesión de caja (turno).
   * @param {Object} request - Datos de cierre
   * @param {number} request.sesionCajaId - ID de la sesión
   * @param {number} request.usuarioId - ID del usuario que cierra
   * @param {number} request.montoFinal - Monto final en caja
   * @returns {Promise<Object>} Sesión de caja cerrada
   * @example
   * const cerrada = await cajasService.cerrarCaja({ sesionCajaId: 10, usuarioId: 3, montoFinal: 500.00 });
   */
  cerrarCaja: async (request) => {
    const { data } = await adminApi.post('/pos/sesiones/cerrar', request);
    return data;
  },

  /**
   * Obtiene la sesión de caja activa de un usuario.
   * @param {number} usuarioId - ID del usuario
   * @returns {Promise<Object|null>} Sesión activa o null
   * @example
   * const sesionActiva = await cajasService.getSesionActiva(3);
   */
  getSesionActiva: async (usuarioId) => {
    const { data } = await adminApi.get(`/pos/sesiones/activa/usuario/${usuarioId}`);
    return data;
  },

  /**
   * Obtiene todas las sesiones de caja de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de sesiones del negocio
   * @example
   * const sesiones = await cajasService.getSesionesByNegocio(5);
   */
  getSesionesByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/sesiones/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene todas las sesiones de una caja específica.
   * @param {number} cajaId - ID de la caja
   * @returns {Promise<Array>} Lista de sesiones de la caja
   * @example
   * const sesiones = await cajasService.getSesionesByCaja(1);
   */
  getSesionesByCaja: async (cajaId) => {
    const { data } = await adminApi.get(`/pos/sesiones/caja/${cajaId}`);
    return toArray(data);
  },

  /**
   * Obtiene el resumen de una sesión de caja (ventas, egresos, ingresos).
   * @param {number} sesionCajaId - ID de la sesión de caja
   * @returns {Promise<Object>} Resumen del turno
   * @example
   * const resumen = await cajasService.getResumenTurno(10);
   */
  getResumenTurno: async (sesionCajaId) => {
    const { data } = await adminApi.get(`/pos/sesiones/${sesionCajaId}/resumen`);
    return data;
  },

  /* ═══ MOVIMIENTOS DE CAJA ═══ */

  /**
   * Registra un movimiento de efectivo (ingreso o egreso).
   * @param {Object} request - Datos del movimiento
   * @param {number} request.sesionCajaId - ID de la sesión activa
   * @param {string} request.tipoMovimiento - INGRESO o EGRESO
   * @param {number} request.monto - Monto del movimiento
   * @param {string} request.descripcion - Descripción del movimiento
   * @returns {Promise<Object>} Movimiento registrado
   * @example
   * const movimiento = await cajasService.registrarMovimiento({
   *   sesionCajaId: 10,
   *   tipoMovimiento: 'EGRESO',
   *   monto: 50.00,
   *   descripcion: 'Pago a proveedor'
   * });
   */
  registrarMovimiento: async (request) => {
    const { data } = await adminApi.post('/pos/movimientos', request);
    return data;
  },

  /**
   * Obtiene los movimientos de una sesión de caja.
   * @param {number} sesionCajaId - ID de la sesión de caja
   * @returns {Promise<Array>} Lista de movimientos
   * @example
   * const movimientos = await cajasService.getMovimientos(10);
   */
  getMovimientos: async (sesionCajaId) => {
    const { data } = await adminApi.get(`/pos/movimientos/sesion/${sesionCajaId}`);
    return toArray(data);
  },

  /**
   * Devuelve un egreso registrado (lo revierte).
   * @param {Object} params - Parámetros de devolución
   * @param {number} params.movimientoId - ID del movimiento a devolver
   * @param {number} params.monto - Monto a devolver
   * @param {string} params.motivo - Motivo de la devolución
   * @returns {Promise<Object>} Resultado de la devolución
   * @example
   * await cajasService.devolverEgreso({ movimientoId: 5, monto: 50.00, motivo: 'Error' });
   */
  devolverEgreso: async ({ movimientoId, monto, motivo }) => {
    const { data } = await adminApi.post(`/pos/movimientos/${movimientoId}/devolver`, {
      monto,
      motivo,
    });
    return data;
  },

  /**
   * Obtiene movimientos de caja de un negocio con filtros opcionales.
   * @param {number} negocioId - ID del negocio
   * @param {Object} [filters] - Filtros opcionales
   * @param {number} [filters.cajaId] - Filtrar por caja
   * @param {string} [filters.desde] - Fecha inicial (YYYY-MM-DD)
   * @param {string} [filters.hasta] - Fecha final (YYYY-MM-DD)
   * @returns {Promise<Array>} Lista de movimientos filtrados
   * @example
   * const movimientos = await cajasService.getMovimientosByNegocio(5, {
   *   cajaId: 1,
   *   desde: '2024-01-01',
   *   hasta: '2024-12-31'
   * });
   */
  getMovimientosByNegocio: async (negocioId, { cajaId, desde, hasta } = {}) => {
    const params = {};
    if (cajaId) params.cajaId = cajaId;
    if (desde) params.desde = desde;
    if (hasta) params.hasta = hasta;
    const { data } = await adminApi.get(`/pos/movimientos/negocio/${negocioId}`, { params });
    return toArray(data);
  },

  /* ═══ CATEGORÍAS DE GASTO ═══ */

  /**
   * Obtiene las categorías de gasto de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de categorías de gasto
   * @example
   * const categorias = await cajasService.getCategoriasByNegocio(5);
   */
  getCategoriasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/categorias-gasto/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Crea una nueva categoría de gasto.
   * @param {Object} categoriaData - Datos de la categoría
   * @returns {Promise<Object>} Categoría creada
   * @example
   * const categoria = await cajasService.crearCategoria({ nombre: 'Servicios', negocioId: 5 });
   */
  crearCategoria: async (categoriaData) => {
    const { data } = await adminApi.post('/pos/categorias-gasto', categoriaData);
    return data;
  },

  /**
   * Actualiza una categoría de gasto existente.
   * @param {number} id - ID de la categoría
   * @param {Object} categoriaData - Datos a actualizar
   * @returns {Promise<Object>} Categoría actualizada
   * @example
   * const actualizada = await cajasService.actualizarCategoria(1, { nombre: 'Servicios públicos' });
   */
  actualizarCategoria: async (id, categoriaData) => {
    const { data } = await adminApi.put(`/pos/categorias-gasto/${id}`, categoriaData);
    return data;
  },

  /**
   * Elimina una categoría de gasto.
   * @param {number} id - ID de la categoría a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await cajasService.eliminarCategoria(1);
   */
  eliminarCategoria: async (id) => {
    const { data } = await adminApi.delete(`/pos/categorias-gasto/${id}`);
    return data;
  },
};
