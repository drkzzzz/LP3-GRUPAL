/**
 * Servicio API para productos del catálogo
 * Endpoints: /restful/productos
 */
import { adminApi } from './adminApi';

/**
 * Mapper para normalizar productos del backend
 */
const mapProductoFromBackend = (producto) => {
  if (!producto) return null;
  
  return {
    ...producto,
    // Normalizar precio (puede venir como precioVenta, precio_venta, precio)
    precioVenta: producto.precioVenta || producto.precio_venta || producto.precio || 0,
    // Normalizar stock (puede venir como stock, stockActual, stock_actual)
    stock: producto.stock !== undefined ? producto.stock : 
           (producto.stockActual !== undefined ? producto.stockActual : 
           (producto.stock_actual !== undefined ? producto.stock_actual : null)),
    // Asegurar que tenga estos campos
    sku: producto.sku || 'SIN-SKU',
    nombre: producto.nombre || 'Producto sin nombre',
  };
};

/**
 * ==========================================
 * PRODUCTOS - Obtener catálogo activo
 * ==========================================
 */

/**
 * Obtener todos los productos activos del negocio
 * GET /restful/productos
 */
export const getProductos = async () => {
  const response = await adminApi.get('/productos');
  return response.data.map(mapProductoFromBackend);
};

/**
 * Obtener productos activos con stock disponible
 * Filtra productos con stock > 0
 */
export const getProductosConStock = async () => {
  const response = await adminApi.get('/productos');
  const productos = response.data.map(mapProductoFromBackend);
  
  // Filtrar solo productos activos con stock
  return productos.filter(producto => {
    return producto.estaActivo && 
           (producto.stock === null || producto.stock === undefined || producto.stock > 0);
  });
};

/**
 * Buscar productos por nombre o SKU
 * @param {string} termino - Término de búsqueda
 * @returns {Promise<Array>} - Lista de productos que coinciden
 */
export const buscarProductos = async (termino) => {
  if (!termino || termino.trim().length < 2) {
    return [];
  }
  
  const response = await adminApi.get('/productos');
  const productos = response.data.map(mapProductoFromBackend);
  
  const terminoBusqueda = termino.toLowerCase().trim();
  return productos.filter(producto => {
    const nombre = (producto.nombre || '').toLowerCase();
    const sku = (producto.sku || '').toLowerCase();
    
    return producto.estaActivo && 
           (nombre.includes(terminoBusqueda) || sku.includes(terminoBusqueda));
  });
};

/**
 * Obtener producto por ID
 * GET /restful/productos/:id
 */
export const getProductoById = async (id) => {
  const response = await adminApi.get(`/productos/${id}`);
  return mapProductoFromBackend(response.data);
};

/**
 * Obtener productos por categoría
 * @param {number} categoriaId - ID de la categoría
 */
export const getProductosPorCategoria = async (categoriaId) => {
  const response = await adminApi.get('/productos');
  const productos = response.data.map(mapProductoFromBackend);
  
  return productos.filter(producto => 
    producto.estaActivo && producto.categoriaId === categoriaId
  );
};
