/**
 * productosAdapter.js
 * ───────────────────
 * Adapter para el consumo de productos desde el POS.
 * Encapsula la integración con el módulo Catálogo.
 *
 * Usa la API real de productos (/restful/productos) a través de catalogoService.
 *
 * ¡NINGÚN otro archivo del POS debe contener datos simulados!
 */
import { productosService } from '@/admin/catalogo/services/catalogoService';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

const USE_MOCK = false;

/* ═══════════════════════════════════════════════════════════
 *  MOCK DATA – Estructura idéntica a la entidad Productos
 * ═══════════════════════════════════════════════════════════ */
const MOCK_PRODUCTOS = [
  {
    id: 1,
    negocio: { id: 1 },
    sku: 'LIC-001',
    nombre: 'Cerveza Cusqueña Dorada 620ml',
    slug: 'cerveza-cusquena-dorada-620ml',
    descripcion: 'Cerveza premium peruana tipo lager, cuerpo medio',
    urlImagen: null,
    categoria: { id: 1, nombre: 'Cervezas' },
    marca: { id: 1, nombre: 'Cusqueña' },
    unidadMedida: { id: 1, codigo: 'UND', nombre: 'Unidad', abreviatura: 'und' },
    gradoAlcoholico: 5.0,
    precioCompra: 3.50,
    precioVenta: 7.00,
    precioVentaMinimo: 6.00,
    precioMayorista: 5.50,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 120,
    fechaVencimiento: '2026-12-31',
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 2,
    negocio: { id: 1 },
    sku: 'LIC-002',
    nombre: 'Pisco Quebranta Tabernero 750ml',
    slug: 'pisco-quebranta-tabernero-750ml',
    descripcion: 'Pisco puro de uva quebranta, ideal para pisco sour',
    urlImagen: null,
    categoria: { id: 2, nombre: 'Piscos' },
    marca: { id: 2, nombre: 'Tabernero' },
    unidadMedida: { id: 2, codigo: 'BOT', nombre: 'Botella', abreviatura: 'bot' },
    gradoAlcoholico: 42.0,
    precioCompra: 28.00,
    precioVenta: 55.00,
    precioVentaMinimo: 48.00,
    precioMayorista: 45.00,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 35,
    fechaVencimiento: null,
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 3,
    negocio: { id: 1 },
    sku: 'LIC-003',
    nombre: 'Vino Tinto Reserva Tacama 750ml',
    slug: 'vino-tinto-reserva-tacama-750ml',
    descripcion: 'Vino tinto de reserva, uvas seleccionadas de Ica',
    urlImagen: null,
    categoria: { id: 3, nombre: 'Vinos' },
    marca: { id: 3, nombre: 'Tacama' },
    unidadMedida: { id: 2, codigo: 'BOT', nombre: 'Botella', abreviatura: 'bot' },
    gradoAlcoholico: 13.5,
    precioCompra: 22.00,
    precioVenta: 42.00,
    precioVentaMinimo: 38.00,
    precioMayorista: 35.00,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 25,
    fechaVencimiento: null,
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 4,
    negocio: { id: 1 },
    sku: 'LIC-004',
    nombre: 'Ron Cartavio Black 750ml',
    slug: 'ron-cartavio-black-750ml',
    descripcion: 'Ron añejo premium, envejecido en barricas de roble',
    urlImagen: null,
    categoria: { id: 4, nombre: 'Rones' },
    marca: { id: 4, nombre: 'Cartavio' },
    unidadMedida: { id: 2, codigo: 'BOT', nombre: 'Botella', abreviatura: 'bot' },
    gradoAlcoholico: 40.0,
    precioCompra: 32.00,
    precioVenta: 65.00,
    precioVentaMinimo: 58.00,
    precioMayorista: 52.00,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 18,
    fechaVencimiento: null,
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 5,
    negocio: { id: 1 },
    sku: 'LIC-005',
    nombre: 'Whisky Johnnie Walker Red Label 750ml',
    slug: 'whisky-johnnie-walker-red-label-750ml',
    descripcion: 'Whisky escocés blended, sabor intenso y especiado',
    urlImagen: null,
    categoria: { id: 5, nombre: 'Whiskys' },
    marca: { id: 5, nombre: 'Johnnie Walker' },
    unidadMedida: { id: 2, codigo: 'BOT', nombre: 'Botella', abreviatura: 'bot' },
    gradoAlcoholico: 40.0,
    precioCompra: 55.00,
    precioVenta: 95.00,
    precioVentaMinimo: 85.00,
    precioMayorista: 80.00,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 12,
    fechaVencimiento: null,
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 6,
    negocio: { id: 1 },
    sku: 'LIC-006',
    nombre: 'Vodka Absolut Original 750ml',
    slug: 'vodka-absolut-original-750ml',
    descripcion: 'Vodka sueco premium, perfecto para cócteles',
    urlImagen: null,
    categoria: { id: 6, nombre: 'Vodkas' },
    marca: { id: 6, nombre: 'Absolut' },
    unidadMedida: { id: 2, codigo: 'BOT', nombre: 'Botella', abreviatura: 'bot' },
    gradoAlcoholico: 40.0,
    precioCompra: 42.00,
    precioVenta: 78.00,
    precioVentaMinimo: 70.00,
    precioMayorista: 65.00,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 20,
    fechaVencimiento: null,
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 7,
    negocio: { id: 1 },
    sku: 'LIC-007',
    nombre: 'Cerveza Pilsen Callao 630ml',
    slug: 'cerveza-pilsen-callao-630ml',
    descripcion: 'Cerveza lager clásica peruana',
    urlImagen: null,
    categoria: { id: 1, nombre: 'Cervezas' },
    marca: { id: 7, nombre: 'Pilsen' },
    unidadMedida: { id: 1, codigo: 'UND', nombre: 'Unidad', abreviatura: 'und' },
    gradoAlcoholico: 5.0,
    precioCompra: 3.00,
    precioVenta: 6.50,
    precioVentaMinimo: 5.50,
    precioMayorista: 5.00,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 200,
    fechaVencimiento: '2026-10-15',
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 8,
    negocio: { id: 1 },
    sku: 'LIC-008',
    nombre: 'Tequila José Cuervo Especial 750ml',
    slug: 'tequila-jose-cuervo-especial-750ml',
    descripcion: 'Tequila gold, mezcla de reposado y joven',
    urlImagen: null,
    categoria: { id: 7, nombre: 'Tequilas' },
    marca: { id: 8, nombre: 'José Cuervo' },
    unidadMedida: { id: 2, codigo: 'BOT', nombre: 'Botella', abreviatura: 'bot' },
    gradoAlcoholico: 38.0,
    precioCompra: 38.00,
    precioVenta: 72.00,
    precioVentaMinimo: 65.00,
    precioMayorista: 60.00,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 15,
    fechaVencimiento: null,
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 9,
    negocio: { id: 1 },
    sku: 'MIX-001',
    nombre: 'Gaseosa Coca-Cola 1.5L',
    slug: 'gaseosa-coca-cola-1-5l',
    descripcion: 'Gaseosa para mezcla y consumo directo',
    urlImagen: null,
    categoria: { id: 8, nombre: 'Bebidas sin alcohol' },
    marca: { id: 9, nombre: 'Coca-Cola' },
    unidadMedida: { id: 1, codigo: 'UND', nombre: 'Unidad', abreviatura: 'und' },
    gradoAlcoholico: 0,
    precioCompra: 3.50,
    precioVenta: 7.00,
    precioVentaMinimo: 6.00,
    precioMayorista: 5.50,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 80,
    fechaVencimiento: '2026-08-20',
    permiteDescuento: true,
    estaActivo: true,
  },
  {
    id: 10,
    negocio: { id: 1 },
    sku: 'ACC-001',
    nombre: 'Hielo en bolsa 3kg',
    slug: 'hielo-en-bolsa-3kg',
    descripcion: 'Bolsa de hielo para enfriar bebidas',
    urlImagen: null,
    categoria: { id: 9, nombre: 'Accesorios' },
    marca: { id: 10, nombre: 'Genérico' },
    unidadMedida: { id: 3, codigo: 'BOL', nombre: 'Bolsa', abreviatura: 'bol' },
    gradoAlcoholico: 0,
    precioCompra: 2.00,
    precioVenta: 5.00,
    precioVentaMinimo: 4.00,
    precioMayorista: 3.50,
    tasaImpuesto: 18.00,
    impuestoIncluido: true,
    stock: 50,
    fechaVencimiento: null,
    permiteDescuento: false,
    estaActivo: true,
  },
];

/* ═══════════════════════════════════════════════════════════
 *  ADAPTER API – idéntico para mock y real
 * ═══════════════════════════════════════════════════════════ */

/** Cache local de productos para búsqueda rápida en el POS */
let cachedProductos = null;
let cacheNegocioId = null;

/**
 * Obtiene todos los productos del negocio actual y los filtra localmente.
 * Se cachean en memoria para evitar llamadas repetidas durante la sesión.
 */
const fetchProductosNegocio = async () => {
  const negocio = useAdminAuthStore.getState().negocio;
  const negocioId = negocio?.id;

  // Si ya están cacheados para este negocio, retornar cache
  if (cachedProductos && cacheNegocioId === negocioId) {
    return cachedProductos;
  }

  const todos = await productosService.getAll();
  // Filtrar solo los del negocio actual y activos
  cachedProductos = todos.filter(
    (p) => p.estaActivo !== false && (!negocioId || p.negocio?.id === negocioId),
  );
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
  if (USE_MOCK) {
    const q = (query || '').toLowerCase().trim();
    if (!q) return MOCK_PRODUCTOS.filter((p) => p.estaActivo);
    return MOCK_PRODUCTOS.filter(
      (p) =>
        p.estaActivo &&
        (p.nombre.toLowerCase().includes(q) ||
          p.sku.toLowerCase().includes(q) ||
          p.categoria?.nombre?.toLowerCase().includes(q) ||
          p.marca?.nombre?.toLowerCase().includes(q)),
    );
  }

  // Integración real con Catálogo API
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
  if (USE_MOCK) {
    return MOCK_PRODUCTOS.find((p) => p.id === id) || null;
  }

  // Intentar desde cache primero
  if (cachedProductos) {
    const found = cachedProductos.find((p) => p.id === id);
    if (found) return found;
  }

  // Fallback: llamar API directamente
  try {
    return await productosService.getById(id);
  } catch {
    return null;
  }
};
