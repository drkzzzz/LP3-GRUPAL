/**
 * Servicio API para zonas de delivery
 * Endpoints: /restful/zonas-delivery
 */
import { adminApi } from '@/admin/services/adminApi';

/**
 * ==========================================
 * ZONAS DELIVERY - CRUD y búsqueda
 * ==========================================
 */

/**
 * Obtener todas las zonas de delivery activas
 * GET /restful/zonas-delivery
 */
export const getZonasDelivery = async () => {
  const response = await adminApi.get('/zonas-delivery');
  return response.data;
};

/**
 * Buscar zona por distrito
 * @param {string} distrito - Nombre del distrito
 * @returns {Promise<Object|null>} -Zona encontrada o null
 */
export const buscarZonaPorDistrito = async (distrito) => {
  if (!distrito || distrito.trim().length === 0) {
    return null;
  }
  
  const response = await adminApi.get('/zonas-delivery');
  const zonas = response.data;
  
  const distritoNormalizado = distrito.toLowerCase().trim();
  
  // Buscar zona que contenga ese distrito en su array JSON
  const zonaEncontrada = zonas.find(zona => {
    if (zona.estaActivo === false) return false;
    
    // Parsear distritos JSON
    let distritosZona = [];
    if (typeof zona.distritos === 'string') {
      try {
        distritosZona = JSON.parse(zona.distritos);
      } catch {
        distritosZona = [];
      }
    } else if (Array.isArray(zona.distritos)) {
      distritosZona = zona.distritos;
    }
    
    // Verificar si el distrito está en la zona
    return distritosZona.some(d => 
      d.toLowerCase().trim() === distritoNormalizado
    );
  });
  
  return zonaEncontrada || null;
};

/**
 * Obtener zona por ID
 * GET /restful/zonas-delivery/:id
 */
export const getZonaDeliveryById = async (id) => {
  const response = await adminApi.get(`/zonas-delivery/${id}`);
  return response.data;
};

/**
 * Crear nueva zona de delivery
 * POST /restful/zonas-delivery
 */
export const createZonaDelivery = async (zonaData) => {
  const response = await adminApi.post('/zonas-delivery', zonaData);
  return response.data;
};

/**
 * Actualizar zona de delivery
 * PUT /restful/zonas-delivery
 */
export const updateZonaDelivery = async (zonaData) => {
  const response = await adminApi.put('/zonas-delivery', zonaData);
  return response.data;
};

/**
 * Eliminar zona de delivery (soft delete)
 * DELETE /restful/zonas-delivery/:id
 */
export const deleteZonaDelivery = async (id) => {
  const response = await adminApi.delete(`/zonas-delivery/${id}`);
  return response.data;
};

/**
 * Calcular costo de delivery según distrito
 * @param {string} distrito - Distrito del cliente
 * @returns {Promise<{zona: Object, costo: number}>}
 */
export const calcularCostoDelivery = async (distrito) => {
  const zona = await buscarZonaPorDistrito(distrito);
  
  if (!zona) {
    return {
      zona: null,
      costo: 0,
      mensaje: `No hay cobertura de delivery para el distrito: ${distrito}`,
    };
  }
  
  return {
    zona: zona,
    costo: parseFloat(zona.tarifaDelivery || 0),
    montoMinimo: parseFloat(zona.montoMinimoPedido || 0),
    mensaje: `Delivery: S/ ${zona.tarifaDelivery} - Mínimo: S/ ${zona.montoMinimoPedido}`,
  };
};
