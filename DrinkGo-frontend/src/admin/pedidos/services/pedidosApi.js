/**
 * Servicio API para gestión de pedidos
 * Endpoints: /restful/pedidos, /restful/detalle-pedidos, /restful/seguimiento-pedidos
 */
import { adminApi } from '@/admin/services/adminApi';

/**
 * Mapper para normalizar datos de pedidos (soporta camelCase y snake_case)
 */
const mapPedidoFromBackend = (pedido) => {
  if (!pedido) return null;
  
  return {
    ...pedido,
    // Mapear estado (priorizar camelCase del DTO, fallback a snake_case)
    estado: pedido.estadoPedido || pedido.estado_pedido || pedido.estado,
    // Mantener estadoPedido para compatibilidad
    estadoPedido: pedido.estadoPedido || pedido.estado_pedido || pedido.estado,
    
    // Mapear tipo de pedido
    tipoPedido: pedido.tipoPedido || pedido.tipo_pedido,
    
    // Mapear número de pedido
    numeroPedido: pedido.numeroPedido || pedido.numero_pedido,
    
    // Mapear origen del pedido
    origenPedido: pedido.origenPedido || pedido.origen_pedido,
    
    // Mapear fecha_pedido
    fechaPedido: pedido.fechaPedido || pedido.fecha_pedido,
    
    // Mapear creado_en (priorizar camelCase)
    creado_en: pedido.creadoEn || pedido.creado_en || pedido.fechaPedido || pedido.fecha_pedido,
    creadoEn: pedido.creadoEn || pedido.creado_en || pedido.fechaPedido || pedido.fecha_pedido,
    
    // Mapear actualizado_en
    actualizado_en: pedido.actualizadoEn || pedido.actualizado_en,
    actualizadoEn: pedido.actualizadoEn || pedido.actualizado_en,
    
    // Mapear costo delivery
    costoDelivery: pedido.costoEnvio || pedido.costo_delivery || pedido.costoDelivery || pedido.tarifa_delivery || pedido.tarifaDelivery,

    // Mapear dirección completa (SIN latitud/longitud - eliminados)
    direccionEntrega: pedido.direccionEntrega || pedido.direccion_entrega,
    departamento: pedido.departamento,
    provincia: pedido.provincia,
    distrito: pedido.distrito,
    referencia: pedido.referencia,
    
    // Cliente (puede venir como objeto o ID)
    clienteId: pedido.clienteId || pedido.cliente_id || pedido.cliente?.id,
    clienteNombre: pedido.cliente?.nombres || pedido.clienteNombre,
    clienteApellido: pedido.cliente?.apellidos || pedido.clienteApellido,
    clienteTelefono: pedido.cliente?.telefono || pedido.clienteTelefono,
    cliente: pedido.cliente, // Mantener objeto completo
    
    // Observaciones (contiene método de pago)
    observaciones: pedido.observaciones,
  };
};

/**
 * ==========================================
 * PEDIDOS - CRUD Principal
 * ==========================================
 */

/**
 * Obtener todos los pedidos del negocio
 * GET /restful/pedidos
 */
export const getPedidos = async () => {
  const response = await adminApi.get('/pedidos');
  // Mapear cada pedido para convertir nombres de campos
  return response.data.map(mapPedidoFromBackend);
};

/**
 * Obtener pedido por ID
 * GET /restful/pedidos/:id
 */
export const getPedidoById = async (id) => {
  const response = await adminApi.get(`/pedidos/${id}`);
  return mapPedidoFromBackend(response.data);
};

/**
 * Crear nuevo pedido
 * POST /restful/pedidos
 */
export const createPedido = async (pedidoData) => {
  const response = await adminApi.post('/pedidos', pedidoData);
  return response.data;
};

/**
 * Actualizar pedido
 * PUT /restful/pedidos
 */
export const updatePedido = async (pedidoData) => {
  const response = await adminApi.put('/pedidos', pedidoData);
  return response.data;
};

/**
 * Eliminar pedido (soft delete)
 * DELETE /restful/pedidos/:id
 */
export const deletePedido = async (id) => {
  const response = await adminApi.delete(`/pedidos/${id}`);
  return response.data;
};

/**
 * ==========================================
 * DETALLE PEDIDOS - Items del pedido
 * ==========================================
 */

/**
 * Obtener todos los detalles de pedidos
 * GET /restful/detalle-pedidos
 */
export const getDetallePedidos = async () => {
  const response = await adminApi.get('/detalle-pedidos');
  return response.data;
};

/**
 * Obtener detalle de pedido por ID
 * GET /restful/detalle-pedidos/:id
 */
export const getDetallePedidoById = async (id) => {
  const response = await adminApi.get(`/detalle-pedidos/${id}`);
  return response.data;
};

/**
 * Crear detalle de pedido
 * POST /restful/detalle-pedidos
 */
export const createDetallePedido = async (detalleData) => {
  const response = await adminApi.post('/detalle-pedidos', detalleData);
  return response.data;
};

/**
 * Actualizar detalle de pedido
 * PUT /restful/detalle-pedidos
 */
export const updateDetallePedido = async (detalleData) => {
  const response = await adminApi.put('/detalle-pedidos', detalleData);
  return response.data;
};

/**
 * Eliminar detalle de pedido
 * DELETE /restful/detalle-pedidos/:id
 */
export const deleteDetallePedido = async (id) => {
  const response = await adminApi.delete(`/detalle-pedidos/${id}`);
  return response.data;
};

/**
 * ==========================================
 * SEGUIMIENTO PEDIDOS - Timeline de estados
 * ==========================================
 */

/**
 * Obtener todos los seguimientos
 * GET /restful/seguimiento-pedidos
 */
export const getSeguimientoPedidos = async () => {
  const response = await adminApi.get('/seguimiento-pedidos');
  return response.data;
};

/**
 * Obtener seguimiento por ID
 * GET /restful/seguimiento-pedidos/:id
 */
export const getSeguimientoPedidoById = async (id) => {
  const response = await adminApi.get(`/seguimiento-pedidos/${id}`);
  return response.data;
};

/**
 * Crear seguimiento de pedido
 * POST /restful/seguimiento-pedidos
 */
export const createSeguimientoPedido = async (seguimientoData) => {
  const response = await adminApi.post('/seguimiento-pedidos', seguimientoData);
  return response.data;
};

/**
 * Actualizar seguimiento
 * PUT /restful/seguimiento-pedidos
 */
export const updateSeguimientoPedido = async (seguimientoData) => {
  const response = await adminApi.put('/seguimiento-pedidos', seguimientoData);
  return response.data;
};

/**
 * Eliminar seguimiento
 * DELETE /restful/seguimiento-pedidos/:id
 */
export const deleteSeguimientoPedido = async (id) => {
  const response = await adminApi.delete(`/seguimiento-pedidos/${id}`);
  return response.data;
};
