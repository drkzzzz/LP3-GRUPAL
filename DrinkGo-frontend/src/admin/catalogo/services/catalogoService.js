/**
 * catalogoService.js
 * ──────────────────
 * Servicio centralizado del módulo Catálogo (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /productos, /categorias, /marcas, /unidades-medida,
 *   /combos, /detalle-combos, /promociones, /condiciones-promocion
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
 *  PRODUCTOS
 * ================================================================ */
export const productosService = {
  /**
   * Obtiene todos los productos, opcionalmente filtrados por sede.
   * @param {number} [sedeId] - ID de la sede (opcional)
   * @returns {Promise<Array>} Lista de productos
   * @example
   * const productos = await productosService.getAll();
   * const productosSede = await productosService.getAll(3);
   */
  getAll: async (sedeId) => {
    const url = sedeId ? `/productos/sede/${sedeId}` : '/productos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },

  /**
   * Obtiene un producto por su ID.
   * @param {number} id - ID del producto
   * @returns {Promise<Object>} Producto encontrado
   * @example
   * const producto = await productosService.getById(10);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/productos/${id}`);
    return data;
  },

  /**
   * Crea un nuevo producto.
   * @param {Object} producto - Datos del producto a crear
   * @returns {Promise<Object>} Producto creado
   * @example
   * const nuevo = await productosService.create({ nombre: 'Cerveza Corona', ... });
   */
  create: async (producto) => {
    const { data } = await adminApi.post('/productos', producto);
    return data;
  },

  /**
   * Actualiza un producto existente.
   * @param {Object} producto - Datos del producto a actualizar
   * @returns {Promise<Object>} Producto actualizado
   * @example
   * const actualizado = await productosService.update({ productoId: 10, precio: 15.00 });
   */
  update: async (producto) => {
    const { data } = await adminApi.put('/productos', producto);
    return data;
  },

  /**
   * Elimina un producto.
   * @param {number} id - ID del producto a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await productosService.delete(10);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/productos/${id}`);
    return data;
  },
};

/* ================================================================
 *  CATEGORÍAS
 * ================================================================ */
export const categoriasService = {
  /**
   * Obtiene todas las categorías, opcionalmente filtradas por negocio.
   * @param {number} [negocioId] - ID del negocio (opcional)
   * @returns {Promise<Array>} Lista de categorías
   * @example
   * const categorias = await categoriasService.getAll();
   * const categoriasPorNegocio = await categoriasService.getAll(5);
   */
  getAll: async (negocioId) => {
    const url = negocioId ? `/categorias/negocio/${negocioId}` : '/categorias';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },

  /**
   * Obtiene una categoría por su ID.
   * @param {number} id - ID de la categoría
   * @returns {Promise<Object>} Categoría encontrada
   * @example
   * const categoria = await categoriasService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/categorias/${id}`);
    return data;
  },

  /**
   * Crea una nueva categoría.
   * @param {Object} categoria - Datos de la categoría a crear
   * @returns {Promise<Object>} Categoría creada
   * @example
   * const nueva = await categoriasService.create({ nombre: 'Cervezas' });
   */
  create: async (categoria) => {
    const { data } = await adminApi.post('/categorias', categoria);
    return data;
  },

  /**
   * Actualiza una categoría existente.
   * @param {Object} categoria - Datos de la categoría a actualizar
   * @returns {Promise<Object>} Categoría actualizada
   * @example
   * const actualizada = await categoriasService.update({ categoriaId: 1, nombre: 'Vinos' });
   */
  update: async (categoria) => {
    const { data } = await adminApi.put('/categorias', categoria);
    return data;
  },

  /**
   * Elimina una categoría.
   * @param {number} id - ID de la categoría a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await categoriasService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/categorias/${id}`);
    return data;
  },
};

/* ================================================================
 *  MARCAS
 * ================================================================ */
export const marcasService = {
  /**
   * Obtiene todas las marcas, opcionalmente filtradas por negocio.
   * @param {number} [negocioId] - ID del negocio (opcional)
   * @returns {Promise<Array>} Lista de marcas
   * @example
   * const marcas = await marcasService.getAll();
   */
  getAll: async (negocioId) => {
    const url = negocioId ? `/marcas/negocio/${negocioId}` : '/marcas';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },

  /**
   * Obtiene una marca por su ID.
   * @param {number} id - ID de la marca
   * @returns {Promise<Object>} Marca encontrada
   * @example
   * const marca = await marcasService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/marcas/${id}`);
    return data;
  },

  /**
   * Crea una nueva marca.
   * @param {Object} marca - Datos de la marca a crear
   * @returns {Promise<Object>} Marca creada
   * @example
   * const nueva = await marcasService.create({ nombre: 'Cusqueña' });
   */
  create: async (marca) => {
    const { data } = await adminApi.post('/marcas', marca);
    return data;
  },

  /**
   * Actualiza una marca existente.
   * @param {Object} marca - Datos de la marca a actualizar
   * @returns {Promise<Object>} Marca actualizada
   * @example
   * const actualizada = await marcasService.update({ marcaId: 1, nombre: 'Pilsen' });
   */
  update: async (marca) => {
    const { data } = await adminApi.put('/marcas', marca);
    return data;
  },

  /**
   * Elimina una marca.
   * @param {number} id - ID de la marca a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await marcasService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/marcas/${id}`);
    return data;
  },
};

/* ================================================================
 *  UNIDADES DE MEDIDA
 * ================================================================ */
export const unidadesMedidaService = {
  /**
   * Obtiene todas las unidades de medida, opcionalmente filtradas por negocio.
   * @param {number} [negocioId] - ID del negocio (opcional)
   * @returns {Promise<Array>} Lista de unidades de medida
   * @example
   * const unidades = await unidadesMedidaService.getAll();
   */
  getAll: async (negocioId) => {
    const url = negocioId ? `/unidades-medida/negocio/${negocioId}` : '/unidades-medida';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },

  /**
   * Obtiene una unidad de medida por su ID.
   * @param {number} id - ID de la unidad de medida
   * @returns {Promise<Object>} Unidad de medida encontrada
   * @example
   * const unidad = await unidadesMedidaService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/unidades-medida/${id}`);
    return data;
  },

  /**
   * Crea una nueva unidad de medida.
   * @param {Object} unidad - Datos de la unidad a crear
   * @returns {Promise<Object>} Unidad de medida creada
   * @example
   * const nueva = await unidadesMedidaService.create({ nombre: 'Litro', abreviatura: 'L' });
   */
  create: async (unidad) => {
    const { data } = await adminApi.post('/unidades-medida', unidad);
    return data;
  },

  /**
   * Actualiza una unidad de medida existente.
   * @param {Object} unidad - Datos de la unidad a actualizar
   * @returns {Promise<Object>} Unidad de medida actualizada
   * @example
   * const actualizada = await unidadesMedidaService.update({ unidadMedidaId: 1, nombre: 'Mililitro' });
   */
  update: async (unidad) => {
    const { data } = await adminApi.put('/unidades-medida', unidad);
    return data;
  },

  /**
   * Elimina una unidad de medida.
   * @param {number} id - ID de la unidad a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await unidadesMedidaService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/unidades-medida/${id}`);
    return data;
  },
};

/* ================================================================
 *  COMBOS
 * ================================================================ */
export const combosService = {
  /**
   * Obtiene todos los combos, opcionalmente filtrados por sede.
   * @param {number} [sedeId] - ID de la sede (opcional)
   * @returns {Promise<Array>} Lista de combos
   * @example
   * const combos = await combosService.getAll();
   */
  getAll: async (sedeId) => {
    const url = sedeId ? `/combos/sede/${sedeId}` : '/combos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },

  /**
   * Obtiene todos los combos de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de combos del negocio
   * @example
   * const combos = await combosService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/combos/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene un combo por su ID.
   * @param {number} id - ID del combo
   * @returns {Promise<Object>} Combo encontrado
   * @example
   * const combo = await combosService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/combos/${id}`);
    return data;
  },

  /**
   * Crea un nuevo combo.
   * @param {Object} combo - Datos del combo a crear
   * @returns {Promise<Object>} Combo creado
   * @example
   * const nuevo = await combosService.create({ nombre: 'Combo Familiar', precio: 45.50 });
   */
  create: async (combo) => {
    const { data } = await adminApi.post('/combos', combo);
    return data;
  },

  /**
   * Actualiza un combo existente.
   * @param {Object} combo - Datos del combo a actualizar
   * @returns {Promise<Object>} Combo actualizado
   * @example
   * const actualizado = await combosService.update({ comboId: 1, precio: 50.00 });
   */
  update: async (combo) => {
    const { data } = await adminApi.put('/combos', combo);
    return data;
  },

  /**
   * Elimina un combo.
   * @param {number} id - ID del combo a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await combosService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/combos/${id}`);
    return data;
  },
};

/* ================================================================
 *  DETALLE COMBOS (productos dentro de un combo)
 * ================================================================ */
export const detalleCombosService = {
  /**
   * Obtiene todos los detalles de combos, opcionalmente filtrados por negocio.
   * @param {number} [negocioId] - ID del negocio (opcional)
   * @returns {Promise<Array>} Lista de detalles de combos
   * @example
   * const detalles = await detalleCombosService.getAll();
   */
  getAll: async (negocioId) => {
    const url = negocioId ? `/detalle-combos/negocio/${negocioId}` : '/detalle-combos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },

  /**
   * Obtiene un detalle de combo por su ID.
   * @param {number} id - ID del detalle de combo
   * @returns {Promise<Object>} Detalle de combo encontrado
   * @example
   * const detalle = await detalleCombosService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/detalle-combos/${id}`);
    return data;
  },

  /**
   * Crea un nuevo detalle de combo (producto dentro de un combo).
   * @param {Object} detalle - Datos del detalle a crear
   * @returns {Promise<Object>} Detalle de combo creado
   * @example
   * const nuevo = await detalleCombosService.create({ comboId: 1, productoId: 5, cantidad: 2 });
   */
  create: async (detalle) => {
    const { data } = await adminApi.post('/detalle-combos', detalle);
    return data;
  },

  /**
   * Actualiza un detalle de combo existente.
   * @param {Object} detalle - Datos del detalle a actualizar
   * @returns {Promise<Object>} Detalle de combo actualizado
   * @example
   * const actualizado = await detalleCombosService.update({ detalleComboId: 1, cantidad: 3 });
   */
  update: async (detalle) => {
    const { data } = await adminApi.put('/detalle-combos', detalle);
    return data;
  },

  /**
   * Elimina un detalle de combo.
   * @param {number} id - ID del detalle a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await detalleCombosService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/detalle-combos/${id}`);
    return data;
  },
};

/* ================================================================
 *  PROMOCIONES (también cubre "Descuentos")
 * ================================================================ */
export const promocionesService = {
  /**
   * Obtiene todas las promociones, opcionalmente filtradas por sede.
   * @param {number} [sedeId] - ID de la sede (opcional)
   * @returns {Promise<Array>} Lista de promociones
   * @example
   * const promociones = await promocionesService.getAll();
   */
  getAll: async (sedeId) => {
    const url = sedeId ? `/promociones/sede/${sedeId}` : '/promociones';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },

  /**
   * Obtiene una promoción por su ID.
   * @param {number} id - ID de la promoción
   * @returns {Promise<Object>} Promoción encontrada
   * @example
   * const promocion = await promocionesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/promociones/${id}`);
    return data;
  },

  /**
   * Crea una nueva promoción.
   * @param {Object} promocion - Datos de la promoción a crear
   * @returns {Promise<Object>} Promoción creada
   * @example
   * const nueva = await promocionesService.create({ nombre: '2x1 Cervezas', descuento: 50 });
   */
  create: async (promocion) => {
    const { data } = await adminApi.post('/promociones', promocion);
    return data;
  },

  /**
   * Actualiza una promoción existente.
   * @param {Object} promocion - Datos de la promoción a actualizar
   * @returns {Promise<Object>} Promoción actualizada
   * @example
   * const actualizada = await promocionesService.update({ promocionId: 1, descuento: 60 });
   */
  update: async (promocion) => {
    const { data } = await adminApi.put('/promociones', promocion);
    return data;
  },

  /**
   * Elimina una promoción.
   * @param {number} id - ID de la promoción a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await promocionesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/promociones/${id}`);
    return data;
  },
};

/* ================================================================
 *  CONDICIONES DE PROMOCIÓN
 * ================================================================ */
export const condicionesPromocionService = {
  /**
   * Obtiene todas las condiciones de promoción.
   * @returns {Promise<Array>} Lista de condiciones de promoción
   * @example
   * const condiciones = await condicionesPromocionService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/condiciones-promocion');
    return toArray(data);
  },

  /**
   * Obtiene una condición de promoción por su ID.
   * @param {number} id - ID de la condición
   * @returns {Promise<Object>} Condición de promoción encontrada
   * @example
   * const condicion = await condicionesPromocionService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/condiciones-promocion/${id}`);
    return data;
  },

  /**
   * Crea una nueva condición de promoción.
   * @param {Object} condicion - Datos de la condición a crear
   * @returns {Promise<Object>} Condición de promoción creada
   * @example
   * const nueva = await condicionesPromocionService.create({ descripcion: 'Compra mínima S/.100' });
   */
  create: async (condicion) => {
    const { data } = await adminApi.post('/condiciones-promocion', condicion);
    return data;
  },

  /**
   * Actualiza una condición de promoción existente.
   * @param {Object} condicion - Datos de la condición a actualizar
   * @returns {Promise<Object>} Condición de promoción actualizada
   * @example
   * const actualizada = await condicionesPromocionService.update({ condicionId: 1, ... });
   */
  update: async (condicion) => {
    const { data } = await adminApi.put('/condiciones-promocion', condicion);
    return data;
  },

  /**
   * Elimina una condición de promoción.
   * @param {number} id - ID de la condición a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await condicionesPromocionService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/condiciones-promocion/${id}`);
    return data;
  },
};
