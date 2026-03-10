/**
 * inventarioService.js
 * ────────────────────
 * Servicio centralizado del módulo Inventario (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /stock-inventario, /lotes-inventario, /movimientos-inventario, /almacenes
 */
import { adminApi } from '@/admin/services/adminApi';

/* ─── Helper: normaliza respuesta a arreglo ─── */
const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  if (Array.isArray(data.data)) return data.data;
  return [];
};

/* ================================================================
 *  STOCK INVENTARIO
 * ================================================================ */
export const stockInventarioService = {
  /**
   * Obtiene todos los registros de stock de inventario.
   * @returns {Promise<Array>} Lista de stock de inventario
   * @example
   * const stocks = await stockInventarioService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/stock-inventario');
    return toArray(data);
  },

  /**
   * Obtiene un registro de stock por su ID.
   * @param {number} id - ID del stock de inventario
   * @returns {Promise<Object>} Stock de inventario encontrado
   * @example
   * const stock = await stockInventarioService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/stock-inventario/${id}`);
    return data;
  },

  /**
   * Crea un nuevo registro de stock de inventario.
   * @param {Object} stock - Datos del stock a crear
   * @param {number} stock.productoId - ID del producto
   * @param {number} stock.almacenId - ID del almacén
   * @param {number} stock.cantidadActual - Cantidad en stock
   * @returns {Promise<Object>} Stock creado
   * @example
   * const nuevoStock = await stockInventarioService.create({
   *   productoId: 10,
   *   almacenId: 2,
   *   cantidadActual: 100
   * });
   */
  create: async (stock) => {
    const { data } = await adminApi.post('/stock-inventario', stock);
    return data;
  },

  /**
   * Actualiza un registro de stock de inventario existente.
   * @param {Object} stock - Datos del stock a actualizar
   * @param {number} stock.stockInventarioId - ID del stock
   * @returns {Promise<Object>} Stock actualizado
   * @example
   * const actualizado = await stockInventarioService.update({
   *   stockInventarioId: 5,
   *   cantidadActual: 150
   * });
   */
  update: async (stock) => {
    const { data } = await adminApi.put('/stock-inventario', stock);
    return data;
  },

  /**
   * Elimina un registro de stock de inventario.
   * @param {number} id - ID del stock a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await stockInventarioService.delete(5);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/stock-inventario/${id}`);
    return data;
  },
};

/* ================================================================
 *  LOTES DE INVENTARIO
 * ================================================================ */
export const lotesInventarioService = {
  /**
   * Obtiene todos los lotes de inventario.
   * @returns {Promise<Array>} Lista de lotes de inventario
   * @example
   * const lotes = await lotesInventarioService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/lotes-inventario');
    return toArray(data);
  },

  /**
   * Obtiene un lote de inventario por su ID.
   * @param {number} id - ID del lote de inventario
   * @returns {Promise<Object>} Lote de inventario encontrado
   * @example
   * const lote = await lotesInventarioService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/lotes-inventario/${id}`);
    return data;
  },

  /**
   * Crea un nuevo lote de inventario.
   * @param {Object} lote - Datos del lote a crear
   * @param {number} lote.productoId - ID del producto
   * @param {string} lote.numeroLote - Número de lote
   * @param {Date} lote.fechaVencimiento - Fecha de vencimiento
   * @param {number} lote.cantidadInicial - Cantidad inicial del lote
   * @returns {Promise<Object>} Lote creado
   * @example
   * const nuevoLote = await lotesInventarioService.create({
   *   productoId: 10,
   *   numeroLote: 'L2024-001',
   *   fechaVencimiento: '2025-12-31',
   *   cantidadInicial: 50
   * });
   */
  create: async (lote) => {
    const { data } = await adminApi.post('/lotes-inventario', lote);
    return data;
  },

  /**
   * Actualiza un lote de inventario existente.
   * @param {Object} lote - Datos del lote a actualizar
   * @param {number} lote.loteInventarioId - ID del lote
   * @returns {Promise<Object>} Lote actualizado
   * @example
   * const actualizado = await lotesInventarioService.update({
   *   loteInventarioId: 3,
   *   fechaVencimiento: '2026-01-31'
   * });
   */
  update: async (lote) => {
    const { data } = await adminApi.put('/lotes-inventario', lote);
    return data;
  },

  /**
   * Elimina un lote de inventario.
   * @param {number} id - ID del lote a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await lotesInventarioService.delete(3);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/lotes-inventario/${id}`);
    return data;
  },
};

/* ================================================================
 *  MOVIMIENTOS DE INVENTARIO
 * ================================================================ */
export const movimientosInventarioService = {
  /**
   * Obtiene todos los movimientos de inventario.
   * @returns {Promise<Array>} Lista de movimientos de inventario
   * @example
   * const movimientos = await movimientosInventarioService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/movimientos-inventario');
    return toArray(data);
  },

  /**
   * Obtiene un movimiento de inventario por su ID.
   * @param {number} id - ID del movimiento de inventario
   * @returns {Promise<Object>} Movimiento de inventario encontrado
   * @example
   * const movimiento = await movimientosInventarioService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/movimientos-inventario/${id}`);
    return data;
  },

  /**
   * Crea un nuevo movimiento de inventario.
   * @param {Object} movimiento - Datos del movimiento a crear
   * @param {number} movimiento.loteId - ID del lote afectado
   * @param {string} movimiento.tipoMovimiento - Tipo (ENTRADA, SALIDA, AJUSTE)
   * @param {number} movimiento.cantidad - Cantidad del movimiento
   * @param {string} movimiento.motivo - Motivo del movimiento
   * @returns {Promise<Object>} Movimiento creado
   * @example
   * const nuevoMovimiento = await movimientosInventarioService.create({
   *   loteId: 5,
   *   tipoMovimiento: 'AJUSTE',
   *   cantidad: -2,
   *   motivo: 'Merma por rotura'
   * });
   */
  create: async (movimiento) => {
    const { data } = await adminApi.post('/movimientos-inventario', movimiento);
    return data;
  },

  /**
   * Actualiza un movimiento de inventario existente.
   * @param {Object} movimiento - Datos del movimiento a actualizar
   * @param {number} movimiento.movimientoInventarioId - ID del movimiento
   * @returns {Promise<Object>} Movimiento actualizado
   * @example
   * const actualizado = await movimientosInventarioService.update({
   *   movimientoInventarioId: 3,
   *   motivo: 'Ajuste de inventario corregido'
   * });
   */
  update: async (movimiento) => {
    const { data } = await adminApi.put('/movimientos-inventario', movimiento);
    return data;
  },

  /**
   * Elimina un movimiento de inventario.
   * @param {number} id - ID del movimiento a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await movimientosInventarioService.delete(3);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/movimientos-inventario/${id}`);
    return data;
  },
};

/* ================================================================
 *  ALMACENES
 * ================================================================ */
export const almacenesService = {
  /**
   * Obtiene todos los almacenes del negocio.
   * @returns {Promise<Array>} Lista de almacenes
   * @example
   * const almacenes = await almacenesService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/almacenes');
    return toArray(data);
  },

  /**
   * Obtiene un almacén por su ID.
   * @param {number} id - ID del almacén
   * @returns {Promise<Object>} Almacén encontrado
   * @example
   * const almacen = await almacenesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/almacenes/${id}`);
    return data;
  },

  /**
   * Crea un nuevo almacén.
   * @param {Object} almacen - Datos del almacén a crear
   * @param {string} almacen.nombre - Nombre del almacén
   * @param {number} almacen.sedeId - ID de la sede asociada
   * @param {string} almacen.descripcion - Descripción del almacén
   * @returns {Promise<Object>} Almacén creado
   * @example
   * const nuevoAlmacen = await almacenesService.create({
   *   nombre: 'Almacén Principal',
   *   sedeId: 1,
   *   descripcion: 'Almacén de productos de alta rotación'
   * });
   */
  create: async (almacen) => {
    const { data } = await adminApi.post('/almacenes', almacen);
    return data;
  },

  /**
   * Actualiza un almacén existente.
   * @param {Object} almacen - Datos del almacén a actualizar
   * @param {number} almacen.almacenId - ID del almacén
   * @returns {Promise<Object>} Almacén actualizado
   * @example
   * const actualizado = await almacenesService.update({
   *   almacenId: 2,
   *   nombre: 'Almacén Secundario'
   * });
   */
  update: async (almacen) => {
    const { data } = await adminApi.put('/almacenes', almacen);
    return data;
  },

  /**
   * Elimina un almacén.
   * @param {number} id - ID del almacén a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await almacenesService.delete(2);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/almacenes/${id}`);
    return data;
  },
};

/* ================================================================
 *  SEDES (solo lectura, para selects de almacenes)
 * ================================================================ */
export const sedesService = {
  /**
   * Obtiene todas las sedes del negocio.
   * Usado para poblar selects en formularios de almacenes.
   * @returns {Promise<Array>} Lista de sedes
   * @example
   * const sedes = await sedesService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/sedes');
    return toArray(data);
  },
};

/* ================================================================
 *  PRODUCTOS (solo lectura, para selects de inventario)
 * ================================================================ */
export const productosInventarioService = {
  /**
   * Obtiene todos los productos, opcionalmente filtrados por negocio.
   * Usado para poblar selects en formularios de inventario.
   * @param {number} [negocioId] - ID del negocio (opcional)
   * @returns {Promise<Array>} Lista de productos
   * @example
   * // Obtener todos los productos
   * const productos = await productosInventarioService.getAll();
   * 
   * // Obtener productos de un negocio específico
   * const productosPorNegocio = await productosInventarioService.getAll(5);
   */
  getAll: async (negocioId) => {
    const url = negocioId ? `/productos/negocio/${negocioId}` : '/productos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
};
