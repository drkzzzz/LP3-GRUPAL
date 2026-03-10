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
 * Obtiene todos los pedidos del negocio.
 * Mapea los campos snake_case del backend a camelCase.
 * @returns {Promise<Array>} Lista de pedidos normalizados
 * @example
 * const pedidos = await getPedidos();
 */
export const getPedidos = async () => {
  const response = await adminApi.get('/pedidos');
  // Mapear cada pedido para convertir nombres de campos
  return response.data.map(mapPedidoFromBackend);
};

/**
 * Obtiene un pedido por su ID.
 * @param {number} id - ID del pedido
 * @returns {Promise<Object>} Pedido normalizado
 * @example
 * const pedido = await getPedidoById(100);
 */
export const getPedidoById = async (id) => {
  const response = await adminApi.get(`/pedidos/${id}`);
  return mapPedidoFromBackend(response.data);
};

/**
 * Crea un nuevo pedido.
 * @param {Object} pedidoData - Datos del pedido (cliente, items, dirección)
 * @returns {Promise<Object>} Pedido creado
 * @example
 * const pedido = await createPedido({ clienteId: 1, tipoPedido: 'DELIVERY', ... });
 */
export const createPedido = async (pedidoData) => {
  const response = await adminApi.post('/pedidos', pedidoData);
  return response.data;
};

/**
 * Actualiza un pedido existente.
 * @param {Object} pedidoData - Datos del pedido a actualizar
 * @returns {Promise<Object>} Pedido actualizado
 * @example
 * await updatePedido({ id: 100, estado: 'EN_PREPARACION' });
 */
export const updatePedido = async (pedidoData) => {
  const response = await adminApi.put('/pedidos', pedidoData);
  return response.data;
};

/**
 * Cambia solo el estado de un pedido.
 * @param {Object} params - Parámetros
 * @param {number} params.id - ID del pedido
 * @param {string} params.nuevoEstado - Nuevo estado (PENDIENTE, EN_PREPARACION, EN_CAMINO, ENTREGADO, etc.)
 * @returns {Promise<void>}
 * @example
 * await cambiarEstadoPedido({ id: 100, nuevoEstado: 'ENTREGADO' });
 */
export const cambiarEstadoPedido = async ({ id, nuevoEstado }) => {
  await adminApi.patch(`/pedidos/${id}/estado`, { estado: nuevoEstado });
  // El backend devuelve { id, estado, ok } — la tabla se refresca por invalidateQueries
};

/**
 * Elimina (soft delete) un pedido.
 * @param {number} id - ID del pedido a eliminar
 * @returns {Promise<Object>} Resultado de la operación
 * @example
 * await deletePedido(100);
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
 * Obtiene todos los detalles de pedidos (items).
 * @returns {Promise<Array>} Lista de detalles de todos los pedidos
 * @example
 * const detalles = await getDetallePedidos();
 */
export const getDetallePedidos = async () => {
  const response = await adminApi.get('/detalle-pedidos');
  return response.data;
};

/**
 * Obtiene un detalle de pedido por su ID.
 * @param {number} id - ID del detalle
 * @returns {Promise<Object>} Detalle del pedido
 * @example
 * const detalle = await getDetallePedidoById(1);
 */
export const getDetallePedidoById = async (id) => {
  const response = await adminApi.get(`/detalle-pedidos/${id}`);
  return response.data;
};

/**
 * Crea un nuevo detalle de pedido (item del pedido).
 * @param {Object} detalleData - Datos del ítem (pedidoId, productoId, cantidad, precio)
 * @returns {Promise<Object>} Detalle creado
 * @example
 * const detalle = await createDetallePedido({ pedidoId: 100, productoId: 10, cantidad: 2 });
 */
export const createDetallePedido = async (detalleData) => {
  const response = await adminApi.post('/detalle-pedidos', detalleData);
  return response.data;
};

/**
 * Actualiza un detalle de pedido existente.
 * @param {Object} detalleData - Datos del detalle a actualizar
 * @returns {Promise<Object>} Detalle actualizado
 * @example
 * await updateDetallePedido({ id: 1, cantidad: 3 });
 */
export const updateDetallePedido = async (detalleData) => {
  const response = await adminApi.put('/detalle-pedidos', detalleData);
  return response.data;
};

/**
 * Elimina un detalle de pedido.
 * @param {number} id - ID del detalle a eliminar
 * @returns {Promise<Object>} Resultado de la operación
 * @example
 * await deleteDetallePedido(1);
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
 * Obtiene todos los seguimientos de pedidos (timeline de estados).
 * @returns {Promise<Array>} Lista de seguimientos
 * @example
 * const seguimientos = await getSeguimientoPedidos();
 */
export const getSeguimientoPedidos = async () => {
  const response = await adminApi.get('/seguimiento-pedidos');
  return response.data;
};

/**
 * Obtiene un seguimiento por su ID.
 * @param {number} id - ID del seguimiento
 * @returns {Promise<Object>} Seguimiento encontrado
 * @example
 * const seguimiento = await getSeguimientoPedidoById(1);
 */
export const getSeguimientoPedidoById = async (id) => {
  const response = await adminApi.get(`/seguimiento-pedidos/${id}`);
  return response.data;
};

/**
 * Crea un nuevo seguimiento de pedido.
 * @param {Object} seguimientoData - Datos del seguimiento (pedidoId, estado, comentario)
 * @returns {Promise<Object>} Seguimiento creado
 * @example
 * const seguimiento = await createSeguimientoPedido({ pedidoId: 100, estado: 'EN_CAMINO' });
 */
export const createSeguimientoPedido = async (seguimientoData) => {
  const response = await adminApi.post('/seguimiento-pedidos', seguimientoData);
  return response.data;
};

/**
 * Actualiza un seguimiento existente.
 * @param {Object} seguimientoData - Datos del seguimiento a actualizar
 * @returns {Promise<Object>} Seguimiento actualizado
 * @example
 * await updateSeguimientoPedido({ id: 1, comentario: 'Actualización' });
 */
export const updateSeguimientoPedido = async (seguimientoData) => {
  const response = await adminApi.put('/seguimiento-pedidos', seguimientoData);
  return response.data;
};

/**
 * Elimina un seguimiento.
 * @param {number} id - ID del seguimiento a eliminar
 * @returns {Promise<Object>} Resultado de la operación
 * @example
 * await deleteSeguimientoPedido(1);
 */
export const deleteSeguimientoPedido = async (id) => {
  const response = await adminApi.delete(`/seguimiento-pedidos/${id}`);
  return response.data;
};

/**
 * ==========================================
 * PAGOS DE PEDIDO
 * ==========================================
 */

/**
 * Obtiene los pagos de un pedido específico.
 * @param {number} pedidoId - ID del pedido
 * @returns {Promise<Array>} Lista de pagos del pedido
 * @example
 * const pagos = await getPagosPorPedido(100);
 */
export const getPagosPorPedido = async (pedidoId) => {
  const response = await adminApi.get(`/pagos-pedido/por-pedido/${pedidoId}`);
  return response.data;
};

/**
 * Aprueba un pago (marcarlo como pagado).
 * @param {number} pagoId - ID del pago
 * @returns {Promise<Object>} Pago aprobado
 * @example
 * await aprobarPago(1);
 */
export const aprobarPago = async (pagoId) => {
  const response = await adminApi.patch(`/pagos-pedido/${pagoId}/aprobar`);
  return response.data;
};
