/**
 * usuariosClientesService.js
 * ───────────────────────────
 * Servicio centralizado del módulo Usuarios y Clientes (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /usuarios, /usuarios/por-negocio/:id
 *   /clientes
 *   /roles
 *   /consulta/dni, /consulta/ruc
 */
import { adminApi } from '@/admin/services/adminApi';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
};

/* ================================================================
 *  USUARIOS
 * ================================================================ */
export const usuariosService = {
  getAll: async () => {
    const { data } = await adminApi.get('/usuarios');
    return toArray(data);
  },

  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/usuarios/por-negocio/${negocioId}`);
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/usuarios/${id}`);
    return data;
  },

  create: async (usuario) => {
    const { data } = await adminApi.post('/usuarios', usuario);
    return data;
  },

  update: async (usuario) => {
    const { data } = await adminApi.put('/usuarios', usuario);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/usuarios/${id}`);
    return data;
  },
};

/* ================================================================
 *  CLIENTES
 * ================================================================ */
export const clientesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/clientes');
    return toArray(data);
  },

  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/clientes/por-negocio/${negocioId}`);
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/clientes/${id}`);
    return data;
  },

  create: async (cliente) => {
    const { data } = await adminApi.post('/clientes', cliente);
    return data;
  },

  update: async (cliente) => {
    const { data } = await adminApi.put('/clientes', cliente);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/clientes/${id}`);
    return data;
  },
};

/* ================================================================
 *  ROLES
 * ================================================================ */
export const rolesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/roles');
    return toArray(data);
  },

  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/roles/por-negocio/${negocioId}`);
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/roles/${id}`);
    return data;
  },

  create: async (rol) => {
    const { data } = await adminApi.post('/roles', rol);
    return data;
  },

  update: async (rol) => {
    const { data } = await adminApi.put('/roles', rol);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/roles/${id}`);
    return data;
  },
};

/* ================================================================
 *  PERMISOS DEL SISTEMA
 * ================================================================ */
export const permisosSistemaService = {
  getAll: async () => {
    const { data } = await adminApi.get('/permisos-sistema');
    return toArray(data);
  },
};

/* ================================================================
 *  ROLES ↔ PERMISOS (asignación)
 * ================================================================ */
export const rolesPermisosService = {
  getAll: async () => {
    const { data } = await adminApi.get('/roles-permisos');
    return toArray(data);
  },

  getByRolId: async (rolId) => {
    const { data } = await adminApi.get(`/roles-permisos/por-rol/${rolId}`);
    return toArray(data);
  },

  /**
   * Asigna un permiso a un rol.
   * @param {{ rol: { id }, permiso: { id }, alcance?: string }} payload
   */
  assign: async (payload) => {
    const { data } = await adminApi.post('/roles-permisos', payload);
    return data;
  },

  /**
   * Actualiza un rol-permiso existente (ej: cambiar alcance).
   * @param {{ id, rol: { id }, permiso: { id }, alcance: string }} payload
   */
  update: async (payload) => {
    const { data } = await adminApi.put('/roles-permisos', payload);
    return data;
  },

  revoke: async (id) => {
    const { data } = await adminApi.delete(`/roles-permisos/${id}`);
    return data;
  },
};

/* ================================================================
 *  USUARIOS ↔ ROLES (asignación)
 * ================================================================ */
export const usuariosRolesService = {
  /**
   * Devuelve los registros UsuariosRoles para un usuario dado.
   * Cada objeto incluye: { id, rol: { id, nombre, ... }, asignadoEn }
   */
  getByUsuarioId: async (usuarioId) => {
    const { data } = await adminApi.get(`/usuarios/${usuarioId}/roles`);
    return Array.isArray(data) ? data : [];
  },

  /**
   * Asigna un rol a un usuario.
   * @param {{ usuario: { id }, rol: { id } }} payload
   */
  assign: async (payload) => {
    const { data } = await adminApi.post('/usuarios-roles', payload);
    return data;
  },

  /**
   * Revoca un rol (elimina el registro usuarios_roles por su ID).
   */
  revoke: async (id) => {
    const { data } = await adminApi.delete(`/usuarios-roles/${id}`);
    return data;
  },
};

/* ================================================================
 *  USUARIOS ↔ SEDES (asignación de sede al usuario)
 * ================================================================ */
export const usuariosSedesService = {
  /**
   * Asigna una sede a un usuario.
   * @param {{ usuario: { id }, sede: { id }, esPredeterminado?: boolean }} payload
   */
  assign: async (payload) => {
    const { data } = await adminApi.post('/usuarios-sedes', payload);
    return data;
  },

  revoke: async (id) => {
    const { data } = await adminApi.delete(`/usuarios-sedes/${id}`);
    return data;
  },
};

/* ================================================================
 *  CONSULTA DE DOCUMENTOS (DNI / RUC via PeruDevs)
 * ================================================================ */
export const consultaDocumentoService = {
  /**
   * Consulta DNI en RENIEC (via backend proxy).
   * Retorna: { nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, ... }
   */
  consultarDni: async (numero) => {
    const { data } = await adminApi.get(`/consulta/dni?numero=${numero}`);
    return data;
  },

  /**
   * Consulta RUC en SUNAT (via backend proxy).
   * Retorna: { razonSocial, nombreComercial, ... }
   */
  consultarRuc: async (numero) => {
    const { data } = await adminApi.get(`/consulta/ruc?numero=${numero}`);
    return data;
  },
};
