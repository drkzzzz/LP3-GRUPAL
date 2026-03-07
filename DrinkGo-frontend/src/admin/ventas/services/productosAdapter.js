/**
 * productosAdapter.js
 * ───────────────────
 * Adapter para el consumo de productos, combos y promociones desde el POS.
 * Encapsula la integración con los endpoints POS/catálogo,
 * retornando items normalizados para búsqueda y carrito.
 *
 * ¡NINGÚN otro archivo del POS debe contener datos simulados!
 */
import { adminApi } from '@/admin/services/adminApi';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ═══════════════════════════════════════════════════════════
 *  CACHE
 * ═══════════════════════════════════════════════════════════ */

let cachedItems = null;      // productos + combos normalizados
let cachedPromos = null;      // promociones vigentes
let cacheNegocioId = null;

/* ═══════════════════════════════════════════════════════════
 *  FETCH & NORMALIZE
 * ═══════════════════════════════════════════════════════════ */

/**
 * Carga productos, combos y promociones del negocio actual.
 * Normaliza todo en una lista unificada para búsqueda.
 */
const fetchAllItems = async () => {
  const negocio = useAdminAuthStore.getState().negocio;
  const negocioId = negocio?.id;

  if (cachedItems && cacheNegocioId === negocioId) {
    return { items: cachedItems, promos: cachedPromos };
  }

  if (!negocioId) {
    return { items: [], promos: [] };
  }

  // Obtener datos en paralelo
  const [productosRes, combosRes, detalleCombosRes, promosRes, condicionesRes] = await Promise.all([
    adminApi.get(`/pos/productos/negocio/${negocioId}`),
    adminApi.get(`/combos/negocio/${negocioId}`),
    adminApi.get(`/detalle-combos/negocio/${negocioId}`),
    adminApi.get(`/promociones/negocio/${negocioId}`),
    adminApi.get('/condiciones-promocion'),
  ]);

  const productos = Array.isArray(productosRes.data) ? productosRes.data : [];
  const combos = Array.isArray(combosRes.data) ? combosRes.data : [];
  const detalleCombos = Array.isArray(detalleCombosRes.data) ? detalleCombosRes.data : [];
  const promociones = Array.isArray(promosRes.data) ? promosRes.data : [];
  const condiciones = Array.isArray(condicionesRes.data) ? condicionesRes.data : [];

  const ahora = new Date();

  // ── Filtrar promociones vigentes ──
  const promosVigentes = promociones.filter((p) => {
    if (p.estaActivo === false) return false;
    if (p.validoDesde && new Date(p.validoDesde) > ahora) return false;
    if (p.validoHasta && new Date(p.validoHasta) < ahora) return false;
    if (p.maxUsos != null && p.usosActuales >= p.maxUsos) return false;
    return true;
  });

  // Mapear condiciones por promoción
  const condicionesByPromo = {};
  for (const c of condiciones) {
    const promoId = c.promocion?.id;
    if (!promoId) continue;
    if (!condicionesByPromo[promoId]) condicionesByPromo[promoId] = [];
    condicionesByPromo[promoId].push(c);
  }

  // ── Aplicar promociones a productos ──
  const productosActivos = productos.filter((p) => p.estaActivo !== false);

  for (const prod of productosActivos) {
    const promoAplicable = encontrarMejorPromocion(prod, promosVigentes, condicionesByPromo);
    if (promoAplicable) {
      const precioOriginal = Number(prod.precioVenta || 0);
      const precioConDescuento = calcularPrecioPromo(precioOriginal, promoAplicable);
      prod._promocion = {
        id: promoAplicable.id,
        nombre: promoAplicable.nombre,
        codigo: promoAplicable.codigo,
        tipoDescuento: promoAplicable.tipoDescuento,
        valorDescuento: Number(promoAplicable.valorDescuento),
        precioOriginal,
        precioConDescuento,
      };
      prod._precioOriginal = precioOriginal;
      prod.precioVenta = precioConDescuento;
    }
    prod._tipo = 'producto';
  }

  // ── Normalizar combos como items vendibles ──
  const combosActivos = combos.filter((c) => {
    if (c.estaActivo === false) return false;
    if (c.visiblePos === false) return false;
    if (c.fechaInicio && new Date(c.fechaInicio) > ahora) return false;
    if (c.fechaFin && new Date(c.fechaFin) < ahora) return false;
    return true;
  });

  const combosNormalizados = combosActivos.map((combo) => {
    const detalles = detalleCombos.filter((d) => d.combo?.id === combo.id);

    // Calcular stock del combo = mínimo de (stock del producto / cantidad requerida)
    let stockCombo = Infinity;
    const productosDelCombo = [];
    for (const d of detalles) {
      const prodEnCache = productosActivos.find((p) => p.id === d.producto?.id);
      const stockProd = prodEnCache?.stock ?? 0;
      const cantidadRequerida = d.cantidad || 1;
      stockCombo = Math.min(stockCombo, Math.floor(stockProd / cantidadRequerida));
      productosDelCombo.push({
        productoId: d.producto?.id,
        nombre: prodEnCache?.nombre || d.producto?.nombre || '—',
        sku: prodEnCache?.sku || '',
        cantidad: cantidadRequerida,
        precioUnitario: Number(d.precioUnitario || 0),
        precioVenta: Number(prodEnCache?._precioOriginal || prodEnCache?.precioVenta || d.precioUnitario || 0),
      });
    }
    if (!isFinite(stockCombo)) stockCombo = 0;

    return {
      id: `combo-${combo.id}`,
      _comboId: combo.id,
      _tipo: 'combo',
      nombre: combo.nombre,
      descripcion: combo.descripcion,
      sku: null,
      categoria: null,
      marca: null,
      urlImagen: combo.urlImagen,
      precioVenta: Number(combo.precioCombo || 0),
      _precioRegular: Number(combo.precioRegular || 0),
      _porcentajeDescuento: Number(combo.porcentajeDescuento || 0),
      stock: stockCombo,
      _productosCombo: productosDelCombo,
      estaActivo: true,
    };
  });

  cachedItems = [...productosActivos, ...combosNormalizados];
  cachedPromos = promosVigentes;
  cacheNegocioId = negocioId;

  return { items: cachedItems, promos: cachedPromos };
};

/**
 * Encuentra la mejor promoción aplicable a un producto.
 */
function encontrarMejorPromocion(producto, promos, condicionesByPromo) {
  let mejor = null;
  let mejorDescuento = 0;

  for (const promo of promos) {
    const conds = condicionesByPromo[promo.id] || [];

    let aplica = false;

    if (promo.aplicaA === 'todo') {
      aplica = true;
    } else if (conds.length > 0) {
      for (const c of conds) {
        if (c.tipoEntidad === 'producto' && c.entidadId === producto.id) {
          aplica = true;
          break;
        }
        if (c.tipoEntidad === 'categoria' && producto.categoria?.id === c.entidadId) {
          aplica = true;
          break;
        }
        if (c.tipoEntidad === 'marca' && producto.marca?.id === c.entidadId) {
          aplica = true;
          break;
        }
      }
    }

    if (!aplica) continue;

    const precio = Number(producto.precioVenta || 0);
    const descuento = promo.tipoDescuento === 'porcentaje'
      ? precio * (Number(promo.valorDescuento) / 100)
      : Number(promo.valorDescuento);

    if (descuento > mejorDescuento) {
      mejorDescuento = descuento;
      mejor = promo;
    }
  }

  return mejor;
}

/**
 * Calcula precio después de aplicar la promoción.
 */
function calcularPrecioPromo(precioOriginal, promo) {
  if (promo.tipoDescuento === 'porcentaje') {
    const desc = precioOriginal * (Number(promo.valorDescuento) / 100);
    return Math.max(0, +(precioOriginal - desc).toFixed(2));
  }
  return Math.max(0, +(precioOriginal - Number(promo.valorDescuento)).toFixed(2));
}

/* ═══════════════════════════════════════════════════════════
 *  API PÚBLICA
 * ═══════════════════════════════════════════════════════════ */

/** Invalida la cache (llamar si se actualiza el catálogo) */
export const invalidarCacheProductos = () => {
  cachedItems = null;
  cachedPromos = null;
  cacheNegocioId = null;
};

/**
 * Busca productos y combos por nombre, SKU o categoría.
 * @param {string} query - Texto de búsqueda
 * @returns {Promise<Array>} Lista de items filtrados
 */
export const buscarProductos = async (query) => {
  const { items } = await fetchAllItems();
  const q = (query || '').toLowerCase().trim();
  if (!q) return items;
  return items.filter(
    (p) =>
      p.nombre?.toLowerCase().includes(q) ||
      p.sku?.toLowerCase().includes(q) ||
      p.categoria?.nombre?.toLowerCase().includes(q) ||
      p.marca?.nombre?.toLowerCase().includes(q) ||
      p.descripcion?.toLowerCase().includes(q),
  );
};

/**
 * Obtiene un item (producto o combo) por su ID.
 * @param {number|string} id - ID del item
 * @returns {Promise<Object|null>} Item encontrado o null
 */
export const obtenerProductoPorId = async (id) => {
  const { items } = await fetchAllItems();
  const found = items.find((p) => p.id === id);
  if (found) return found;

  // Fallback: invalidate cache and refetch
  try {
    invalidarCacheProductos();
    const { items: fresh } = await fetchAllItems();
    return fresh.find((p) => p.id === id) || null;
  } catch {
    return null;
  }
};
