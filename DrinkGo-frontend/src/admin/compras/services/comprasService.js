/**
 * comprasService.js
 * ─────────────────
 * Servicio centralizado del módulo Proveedores y Compras (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /proveedores, /productos-proveedor, /ordenes-compra, /detalle-ordenes-compra
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
 *  PROVEEDORES
 * ================================================================ */
export const proveedoresService = {
  /**
   * Obtiene todos los proveedores del negocio.
   * @returns {Promise<Array>} Lista de proveedores
   * @example
   * const proveedores = await proveedoresService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/proveedores');
    return toArray(data);
  },

  /**
   * Obtiene un proveedor por su ID.
   * @param {number} id - ID del proveedor
   * @returns {Promise<Object>} Proveedor encontrado
   * @example
   * const proveedor = await proveedoresService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/proveedores/${id}`);
    return data;
  },

  /**
   * Crea un nuevo proveedor.
   * @param {Object} proveedor - Datos del proveedor a crear
   * @returns {Promise<Object>} Proveedor creado
   * @example
   * const nuevo = await proveedoresService.create({
   *   nombre: 'Distribuidora ABC',
   *   ruc: '20123456789',
   *   contacto: 'Juan Perez'
   * });
   */
  create: async (proveedor) => {
    const { data } = await adminApi.post('/proveedores', proveedor);
    return data;
  },

  /**
   * Actualiza un proveedor existente.
   * @param {Object} proveedor - Datos del proveedor a actualizar
   * @returns {Promise<Object>} Proveedor actualizado
   * @example
   * const actualizado = await proveedoresService.update({ proveedorId: 1, contacto: 'Maria Lopez' });
   */
  update: async (proveedor) => {
    const { data } = await adminApi.put('/proveedores', proveedor);
    return data;
  },

  /**
   * Elimina un proveedor.
   * @param {number} id - ID del proveedor a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await proveedoresService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/proveedores/${id}`);
    return data;
  },
};

/* ================================================================
 *  PRODUCTOS POR PROVEEDOR
 * ================================================================ */
export const productosProveedorService = {
  /**
   * Obtiene todos los productos asociados a proveedores.
   * @returns {Promise<Array>} Lista de productos por proveedor
   * @example
   * const productosProveedor = await productosProveedorService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/productos-proveedor');
    return toArray(data);
  },

  /**
   * Obtiene un producto-proveedor por su ID.
   * @param {number} id - ID del producto-proveedor
   * @returns {Promise<Object>} Producto-proveedor encontrado
   * @example
   * const productoProveedor = await productosProveedorService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/productos-proveedor/${id}`);
    return data;
  },

  /**
   * Crea una nueva relación producto-proveedor.
   * @param {Object} productoProveedor - Datos del producto-proveedor a crear
   * @returns {Promise<Object>} Producto-proveedor creado
   * @example
   * const nuevo = await productosProveedorService.create({
   *   productoId: 10,
   *   proveedorId: 2,
   *   precioProveedor: 12.50
   * });
   */
  create: async (productoProveedor) => {
    const { data } = await adminApi.post('/productos-proveedor', productoProveedor);
    return data;
  },

  /**
   * Actualiza una relación producto-proveedor existente.
   * @param {Object} productoProveedor - Datos del producto-proveedor a actualizar
   * @returns {Promise<Object>} Producto-proveedor actualizado
   * @example
   * const actualizado = await productosProveedorService.update({ id: 1, precioProveedor: 13.00 });
   */
  update: async (productoProveedor) => {
    const { data } = await adminApi.put('/productos-proveedor', productoProveedor);
    return data;
  },

  /**
   * Elimina una relación producto-proveedor.
   * @param {number} id - ID del producto-proveedor a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await productosProveedorService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/productos-proveedor/${id}`);
    return data;
  },
};

/* ================================================================
 *  ÓRDENES DE COMPRA
 * ================================================================ */
export const ordenesCompraService = {
  /**
   * Obtiene todas las órdenes de compra del negocio.
   * @returns {Promise<Array>} Lista de órdenes de compra
   * @example
   * const ordenes = await ordenesCompraService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/ordenes-compra');
    return toArray(data);
  },

  /**
   * Obtiene una orden de compra por su ID.
   * @param {number} id - ID de la orden de compra
   * @returns {Promise<Object>} Orden de compra encontrada
   * @example
   * const orden = await ordenesCompraService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/ordenes-compra/${id}`);
    return data;
  },

  /**
   * Crea una nueva orden de compra.
   * @param {Object} orden - Datos de la orden a crear
   * @returns {Promise<Object>} Orden de compra creada
   * @example
   * const nueva = await ordenesCompraService.create({
   *   proveedorId: 2,
   *   fechaOrden: '2024-03-10',
   *   total: 500.00
   * });
   */
  create: async (orden) => {
    const { data } = await adminApi.post('/ordenes-compra', orden);
    return data;
  },

  /**
   * Actualiza una orden de compra existente.
   * @param {Object} orden - Datos de la orden a actualizar
   * @returns {Promise<Object>} Orden de compra actualizada
   * @example
   * const actualizada = await ordenesCompraService.update({ ordenCompraId: 1, total: 550.00 });
   */
  update: async (orden) => {
    const { data } = await adminApi.put('/ordenes-compra', orden);
    return data;
  },

  /**
   * Elimina una orden de compra.
   * @param {number} id - ID de la orden a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await ordenesCompraService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/ordenes-compra/${id}`);
    return data;
  },

  /**
   * Recibe una orden de compra de forma transaccional en el backend.
   * Crea los lotes, actualiza el stock con CPP y marca la orden como recibida.
   * @param {number} ordenId - ID de la orden de compra
   * @param {number} usuarioId - ID del usuario que recibe la orden
   * @param {Array} items - Lista de items recibidos [{ detalleId, cantidadRecibida, numeroLote, fechaVencimiento }]
   * @returns {Promise<Object>} Resultado de la recepción
   * @example
   * const resultado = await ordenesCompraService.recibir(5, 3, [
   *   { detalleId: 10, cantidadRecibida: 50, numeroLote: 'L001', fechaVencimiento: '2025-12-31' }
   * ]);
   */
  recibir: async (ordenId, usuarioId, items) => {
    const { data } = await adminApi.post(`/ordenes-compra/${ordenId}/recibir`, {
      usuarioId,
      items,
    });
    return data;
  },
};

/* ================================================================
 *  DETALLE DE ÓRDENES DE COMPRA
 * ================================================================ */
export const detalleOrdenesCompraService = {
  /**
   * Obtiene todos los detalles de órdenes de compra.
   * @returns {Promise<Array>} Lista de detalles de órdenes
   * @example
   * const detalles = await detalleOrdenesCompraService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/detalle-ordenes-compra');
    return toArray(data);
  },

  /**
   * Obtiene los detalles de una orden de compra específica.
   * @param {number} ordenCompraId - ID de la orden de compra
   * @returns {Promise<Array>} Lista de detalles de la orden
   * @example
   * const detalles = await detalleOrdenesCompraService.getByOrden(5);
   */
  getByOrden: async (ordenCompraId) => {
    const { data } = await adminApi.get(`/detalle-ordenes-compra/orden/${ordenCompraId}`);
    return toArray(data);
  },

  /**
   * Obtiene un detalle de orden de compra por su ID.
   * @param {number} id - ID del detalle de orden
   * @returns {Promise<Object>} Detalle de orden encontrado
   * @example
   * const detalle = await detalleOrdenesCompraService.getById(10);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/detalle-ordenes-compra/${id}`);
    return data;
  },

  /**
   * Crea un nuevo detalle de orden de compra.
   * @param {Object} detalle - Datos del detalle a crear
   * @returns {Promise<Object>} Detalle de orden creado
   * @example
   * const nuevo = await detalleOrdenesCompraService.create({
   *   ordenCompraId: 5,
   *   productoId: 10,
   *   cantidad: 50,
   *   precioUnitario: 12.50
   * });
   */
  create: async (detalle) => {
    const { data } = await adminApi.post('/detalle-ordenes-compra', detalle);
    return data;
  },

  /**
   * Actualiza un detalle de orden de compra existente.
   * @param {Object} detalle - Datos del detalle a actualizar
   * @returns {Promise<Object>} Detalle de orden actualizado
   * @example
   * const actualizado = await detalleOrdenesCompraService.update({ detalleId: 10, cantidad: 60 });
   */
  update: async (detalle) => {
    const { data } = await adminApi.put('/detalle-ordenes-compra', detalle);
    return data;
  },

  /**
   * Elimina un detalle de orden de compra.
   * @param {number} id - ID del detalle a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await detalleOrdenesCompraService.delete(10);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/detalle-ordenes-compra/${id}`);
    return data;
  },
};

/* ================================================================
 *  SEDES (solo lectura, para selects)
 * ================================================================ */
export const sedesComprasService = {
  /**
   * Obtiene todas las sedes del negocio.
   * Usado para poblar selects en formularios de compras.
   * @returns {Promise<Array>} Lista de sedes
   * @example
   * const sedes = await sedesComprasService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/sedes');
    return toArray(data);
  },
};

/* ================================================================
 *  ALMACENES (solo lectura, para selects)
 * ================================================================ */
export const almacenesComprasService = {
  /**
   * Obtiene todos los almacenes del negocio.
   * Usado para poblar selects en formularios de compras.
   * @returns {Promise<Array>} Lista de almacenes
   * @example
   * const almacenes = await almacenesComprasService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/almacenes');
    return toArray(data);
  },
};

/* ================================================================
 *  PRODUCTOS (solo lectura, para selects en órdenes)
 * ================================================================ */
export const productosComprasService = {
  /**
   * Obtiene todos los productos, opcionalmente filtrados por negocio.
   * Usado para poblar selects en formularios de órdenes de compra.
   * @param {number} [negocioId] - ID del negocio (opcional)
   * @returns {Promise<Array>} Lista de productos
   * @example
   * const productos = await productosComprasService.getAll();
   * const productosPorNegocio = await productosComprasService.getAll(5);
   */
  getAll: async (negocioId) => {
    const url = negocioId ? `/productos/negocio/${negocioId}` : '/productos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
};
