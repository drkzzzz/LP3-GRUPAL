/**
 * productosAdapter.js
 * ───────────────────
 * Adapter para el consumo de productos desde el POS.
 * Encapsula la integración con el endpoint POS de productos,
 * que retorna productos enriquecidos con stock y precio de venta.
 *
 * Usa la API POS (/restful/pos/productos/negocio/{id}) que retorna
 * productos con campos `precioVenta` y `stock` poblados desde inventario.
 *
 * ¡NINGÚN otro archivo del POS debe contener datos simulados!
 */
import { adminApi } from '@/admin/services/adminApi';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ═══════════════════════════════════════════════════════════
 *  ADAPTER API
 * ═══════════════════════════════════════════════════════════ */

/** Cache local de productos para búsqueda rápida en el POS */
let cachedProductos = null;
let cacheNegocioId = null;

/**
 * Obtiene todos los productos del negocio actual con stock y precios reales.
 * Usa el endpoint POS dedicado que enriquece los productos con:
 * - precioVenta (desde el campo precio_venta del producto)
 * - stock (calculado desde stock_inventario.cantidad_disponible)
 */
const fetchProductosNegocio = async () => {
  const negocio = useAdminAuthStore.getState().negocio;
  const negocioId = negocio?.id;

  // Si ya están cacheados para este negocio, retornar cache
  if (cachedProductos && cacheNegocioId === negocioId) {
    return cachedProductos;
  }

  if (!negocioId) {
    return [];
  }

  const { data } = await adminApi.get(`/pos/productos/negocio/${negocioId}`);
  const productos = Array.isArray(data) ? data : [];

  // Filtrar solo activos
  cachedProductos = productos.filter((p) => p.estaActivo !== false);
  cacheNegocioId = negocioId;
  return cachedProductos;
};

/** Invalida la cache (llamar si se actualiza el catálogo) */
export const invalidarCacheProductos = () => {
  cachedProductos = null;
  cacheNegocioId = null;
};

/**
 * Busca productos por nombre, SKU o categoría.
 * @param {string} query - Texto de búsqueda
 * @returns {Promise<Array>} Lista de productos filtrados
 */
export const buscarProductos = async (query) => {
  const productos = await fetchProductosNegocio();
  const q = (query || '').toLowerCase().trim();
  if (!q) return productos;
  return productos.filter(
    (p) =>
      p.nombre?.toLowerCase().includes(q) ||
      p.sku?.toLowerCase().includes(q) ||
      p.categoria?.nombre?.toLowerCase().includes(q) ||
      p.marca?.nombre?.toLowerCase().includes(q),
  );
};

/**
 * Obtiene un producto por su ID.
 * @param {number} id - ID del producto
 * @returns {Promise<Object|null>} Producto encontrado o null
 */
export const obtenerProductoPorId = async (id) => {
  // Intentar desde cache primero
  if (cachedProductos) {
    const found = cachedProductos.find((p) => p.id === id);
    if (found) return found;
  }

  // Fallback: invalidate cache and refetch
  try {
    invalidarCacheProductos();
    const productos = await fetchProductosNegocio();
    return productos.find((p) => p.id === id) || null;
  } catch {
    return null;
  }
};
