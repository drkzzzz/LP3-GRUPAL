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
  /**
   * Obtiene todos los usuarios del sistema.
   * @returns {Promise<Array>} Lista de usuarios
   * @example
   * const usuarios = await usuariosService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/usuarios');
    return toArray(data);
  },

  /**
   * Obtiene los usuarios de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de usuarios del negocio
   * @example
   * const usuarios = await usuariosService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/usuarios/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene un usuario por su ID.
   * @param {number} id - ID del usuario
   * @returns {Promise<Object>} Usuario encontrado
   * @example
   * const usuario = await usuariosService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/usuarios/${id}`);
    return data;
  },

  /**
   * Crea un nuevo usuario.
   * @param {Object} usuario - Datos del usuario a crear
   * @returns {Promise<Object>} Usuario creado
   * @example
   * const nuevo = await usuariosService.create({
   *   nombreUsuario: 'jdoe',
   *   contrasenya: 'Pass123',
   *   email: 'jdoe@example.com'
   * });
   */
  create: async (usuario) => {
    const { data } = await adminApi.post('/usuarios', usuario);
    return data;
  },

  /**
   * Actualiza un usuario existente.
   * @param {Object} usuario - Datos del usuario a actualizar
   * @returns {Promise<Object>} Usuario actualizado
   * @example
   * const actualizado = await usuariosService.update({ id: 1, ... });
   */
  update: async (usuario) => {
    const { data } = await adminApi.put('/usuarios', usuario);
    return data;
  },

  /**
   * Elimina un usuario.
   * @param {number} id - ID del usuario a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await usuariosService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/usuarios/${id}`);
    return data;
  },
};

/* ================================================================
 *  CLIENTES
 * ================================================================ */
export const clientesService = {
  /**
   * Obtiene todos los clientes del sistema.
   * @returns {Promise<Array>} Lista de clientes
   * @example
   * const clientes = await clientesService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/clientes');
    return toArray(data);
  },

  /**
   * Obtiene los clientes de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de clientes del negocio
   * @example
   * const clientes = await clientesService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/clientes/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene un cliente por su ID.
   * @param {number} id - ID del cliente
   * @returns {Promise<Object>} Cliente encontrado
   * @example
   * const cliente = await clientesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/clientes/${id}`);
    return data;
  },

  /**
   * Crea un nuevo cliente.
   * @param {Object} cliente - Datos del cliente a crear
   * @returns {Promise<Object>} Cliente creado
   * @example
   * const nuevo = await clientesService.create({
   *   nombres: 'Juan',
   *   apellidos: 'Pérez',
   *   tipoDocumento: 'DNI',
   *   numeroDocumento: '12345678'
   * });
   */
  create: async (cliente) => {
    const { data } = await adminApi.post('/clientes', cliente);
    return data;
  },

  /**
   * Actualiza un cliente existente.
   * @param {Object} cliente - Datos del cliente a actualizar
   * @returns {Promise<Object>} Cliente actualizado
   * @example
   * const actualizado = await clientesService.update({ id: 1, ... });
   */
  update: async (cliente) => {
    const { data } = await adminApi.put('/clientes', cliente);
    return data;
  },

  /**
   * Elimina un cliente.
   * @param {number} id - ID del cliente a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await clientesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/clientes/${id}`);
    return data;
  },
};

/* ================================================================
 *  ROLES
 * ================================================================ */
export const rolesService = {
  /**
   * Obtiene todos los roles del sistema.
   * @returns {Promise<Array>} Lista de roles
   * @example
   * const roles = await rolesService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/roles');
    return toArray(data);
  },

  /**
   * Obtiene los roles de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de roles del negocio
   * @example
   * const roles = await rolesService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/roles/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene un rol por su ID.
   * @param {number} id - ID del rol
   * @returns {Promise<Object>} Rol encontrado
   * @example
   * const rol = await rolesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/roles/${id}`);
    return data;
  },

  /**
   * Crea un nuevo rol.
   * @param {Object} rol - Datos del rol a crear
   * @returns {Promise<Object>} Rol creado
   * @example
   * const nuevoRol = await rolesService.create({
   *   nombre: 'Cajero',
   *   descripcion: 'Acceso al POS'
   * });
   */
  create: async (rol) => {
    const { data } = await adminApi.post('/roles', rol);
    return data;
  },

  /**
   * Actualiza un rol existente.
   * @param {Object} rol - Datos del rol a actualizar
   * @returns {Promise<Object>} Rol actualizado
   * @example
   * const actualizado = await rolesService.update({ id: 1, ... });
   */
  update: async (rol) => {
    const { data } = await adminApi.put('/roles', rol);
    return data;
  },

  /**
   * Elimina un rol.
   * @param {number} id - ID del rol a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await rolesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/roles/${id}`);
    return data;
  },
};

/* ================================================================
 *  PERMISOS DEL SISTEMA
 * ================================================================ */
export const permisosSistemaService = {
  /**
   * Obtiene todos los permisos disponibles en el sistema.
   * @returns {Promise<Array>} Lista de permisos (lectura, escritura, etc.)
   * @example
   * const permisos = await permisosSistemaService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/permisos-sistema');
    return toArray(data);
  },
};

/* ================================================================
 *  ROLES ↔ PERMISOS (asignación)
 * ================================================================ */
export const rolesPermisosService = {
  /**
   * Obtiene todas las asignaciones rol-permiso.
   * @returns {Promise<Array>} Lista de asignaciones
   * @example
   * const asignaciones = await rolesPermisosService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/roles-permisos');
    return toArray(data);
  },

  /**
   * Obtiene los permisos asignados a un rol específico.
   * @param {number} rolId - ID del rol
   * @returns {Promise<Array>} Lista de permisos del rol
   * @example
   * const permisos = await rolesPermisosService.getByRolId(1);
   */
  getByRolId: async (rolId) => {
    const { data } = await adminApi.get(`/roles-permisos/por-rol/${rolId}`);
    return toArray(data);
  },

  /**
   * Asigna un permiso a un rol.
   * @param {Object} payload - Datos de la asignación
   * @param {Object} payload.rol - Objeto con {id} del rol
   * @param {Object} payload.permiso - Objeto con {id} del permiso
   * @param {string} [payload.alcance] - Alcance del permiso (opcional)
   * @returns {Promise<Object>} Asignación creada
   * @example
   * await rolesPermisosService.assign({ rol: {id: 1}, permiso: {id: 2}, alcance: 'GLOBAL' });
   */
  assign: async (payload) => {
    const { data } = await adminApi.post('/roles-permisos', payload);
    return data;
  },

  /**
   * Actualiza un rol-permiso existente (ej: cambiar alcance).
   * @param {Object} payload - Datos de la asignación a actualizar
   * @param {number} payload.id - ID de la asignación
   * @param {Object} payload.rol - Objeto con {id} del rol
   * @param {Object} payload.permiso - Objeto con {id} del permiso
   * @param {string} payload.alcance - Nuevo alcance del permiso
   * @returns {Promise<Object>} Asignación actualizada
   * @example
   * await rolesPermisosService.update({ id: 1, rol: {id: 1}, permiso: {id: 2}, alcance: 'SEDE' });
   */
  update: async (payload) => {
    const { data } = await adminApi.put('/roles-permisos', payload);
    return data;
  },

  /**
   * Revoca un permiso de un rol (elimina la asignación).
   * @param {number} id - ID de la asignación a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await rolesPermisosService.revoke(1);
   */
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
   * @param {number} usuarioId - ID del usuario
   * @returns {Promise<Array>} Lista de roles asignados al usuario
   * @example
   * const roles = await usuariosRolesService.getByUsuarioId(1);
   */
  getByUsuarioId: async (usuarioId) => {
    const { data } = await adminApi.get(`/usuarios/${usuarioId}/roles`);
    return Array.isArray(data) ? data : [];
  },

  /**
   * Asigna un rol a un usuario.
   * @param {Object} payload - Datos de la asignación
   * @param {Object} payload.usuario - Objeto con {id} del usuario
   * @param {Object} payload.rol - Objeto con {id} del rol
   * @returns {Promise<Object>} Asignación creada
   * @example
   * await usuariosRolesService.assign({ usuario: {id: 1}, rol: {id: 2} });
   */
  assign: async (payload) => {
    const { data } = await adminApi.post('/usuarios-roles', payload);
    return data;
  },

  /**
   * Revoca un rol (elimina el registro usuarios_roles por su ID).
   * @param {number} id - ID de la asignación a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await usuariosRolesService.revoke(1);
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
   * @param {Object} payload - Datos de la asignación
   * @param {Object} payload.usuario - Objeto con {id} del usuario
   * @param {Object} payload.sede - Objeto con {id} de la sede
   * @param {boolean} [payload.esPredeterminado] - Si es sede predeterminada
   * @returns {Promise<Object>} Asignación creada
   * @example
   * await usuariosSedesService.assign({ usuario: {id: 1}, sede: {id: 2}, esPredeterminado: true });
   */
  assign: async (payload) => {
    const { data } = await adminApi.post('/usuarios-sedes', payload);
    return data;
  },

  /**
   * Revoca la asignación de una sede a un usuario.
   * @param {number} id - ID de la asignación a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await usuariosSedesService.revoke(1);
   */
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
   * @param {string} numero - Número de DNI (8 dígitos)
   * @returns {Promise<Object>} Datos de la persona
   * @example
   * const datos = await consultaDocumentoService.consultarDni('12345678');
   */
  consultarDni: async (numero) => {
    const { data } = await adminApi.get(`/consulta/dni?numero=${numero}`);
    return data;
  },

  /**
   * Consulta RUC en SUNAT (via backend proxy).
   * Retorna: { razonSocial, nombreComercial, ... }
   * @param {string} numero - Número de RUC (11 dígitos)
   * @returns {Promise<Object>} Datos de la empresa
   * @example
   * const datos = await consultaDocumentoService.consultarRuc('12345678901');
   */
  consultarRuc: async (numero) => {
    const { data } = await adminApi.get(`/consulta/ruc?numero=${numero}`);
    return data;
  },
};
