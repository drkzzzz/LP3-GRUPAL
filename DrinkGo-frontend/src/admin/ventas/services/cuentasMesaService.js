/**
 * cuentasMesaService.js
 * ─────────────────────
 * Servicio para el módulo de Gestión de Mesas (RF-VTA-011 a 014).
 * Consume los endpoints del CuentasMesaController.
 */
import { adminApi } from '@/admin/services/adminApi';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
};

export const cuentasMesaService = {
  /* ─── RF-VTA-011: Abrir cuenta ─── */
  abrir: async ({ negocioId, mesaId, meseroId, clienteId, numComensales }) => {
    const payload = {
      negocio: { id: negocioId },
      mesa: { id: mesaId },
      mesero: { id: meseroId },
      numComensales: numComensales || 1,
    };
    if (clienteId) payload.cliente = { id: clienteId };
    const { data } = await adminApi.post('/cuentas-mesa', payload);
    return data;
  },

  /* ─── Consultas ─── */
  getAbiertasPorSede: async (sedeId) => {
    const { data } = await adminApi.get(`/cuentas-mesa/por-sede/${sedeId}`);
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/cuentas-mesa/${id}`);
    return data;
  },

  getDetalles: async (cuentaId) => {
    const { data } = await adminApi.get(`/cuentas-mesa/${cuentaId}/detalles`);
    return toArray(data);
  },

  /* ─── RF-VTA-012: Agregar / quitar productos ─── */
  agregarProducto: async (cuentaId, { productoId, nombreProductoSnapshot, cantidad, precioUnitario, notas }) => {
    const payload = { cantidad, precioUnitario };
    if (productoId) payload.producto = { id: productoId };
    if (nombreProductoSnapshot) payload.nombreProductoSnapshot = nombreProductoSnapshot;
    if (notas) payload.notas = notas;
    const { data } = await adminApi.post(`/cuentas-mesa/${cuentaId}/productos`, payload);
    return data;
  },

  removerProducto: async (detalleId) => {
    const { data } = await adminApi.delete(`/cuentas-mesa/detalle/${detalleId}`);
    return data;
  },

  /* ─── RF-VTA-013: Transferir ─── */
  transferirProductos: async (cuentaOrigenId, cuentaDestinoId, detalleIds) => {
    const { data } = await adminApi.post(
      `/cuentas-mesa/${cuentaOrigenId}/transferir-productos`,
      { cuentaDestinoId, detalleIds },
    );
    return data;
  },

  transferirMesa: async (cuentaId, nuevaMesaId) => {
    const { data } = await adminApi.post(`/cuentas-mesa/${cuentaId}/transferir-mesa`, {
      nuevaMesaId,
    });
    return data;
  },

  /* ─── RF-VTA-014: Cerrar cuenta ─── */
  dividirPorPersonas: async (cuentaId, personas) => {
    const { data } = await adminApi.get(`/cuentas-mesa/${cuentaId}/dividir/${personas}`);
    return toArray(data);
  },

  cerrarCuenta: async (cuentaId, usuarioId) => {
    const { data } = await adminApi.post(`/cuentas-mesa/${cuentaId}/cerrar`, { usuarioId });
    return data;
  },
};
