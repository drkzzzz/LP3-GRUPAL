import { api } from '@/config/api';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  if (Array.isArray(data.data)) return data.data;
  return [];
};

export const negociosService = {
  /**
   * Obtiene todos los negocios del sistema.
   * @returns {Promise<Array>} Lista de negocios
   * @example
   * const negocios = await negociosService.getAll();
   */
  getAll: async () => {
    const { data } = await api.get('/negocios');
    return toArray(data);
  },

  /**
   * Obtiene un negocio por su ID.
   * @param {number} id - ID del negocio
   * @returns {Promise<Object>} Negocio encontrado
   * @example
   * const negocio = await negociosService.getById(1);
   */
  getById: async (id) => {
    const { data } = await api.get(`/negocios/${id}`);
    return data;
  },

  /**
   * Crea un nuevo negocio.
   * @param {Object} negocio - Datos del negocio a crear
   * @returns {Promise<Object>} Negocio creado
   * @example
   * const nuevo = await negociosService.create({ nombre: 'Licorería Cruz del Sur' });
   */
  create: async (negocio) => {
    const { data } = await api.post('/negocios', negocio);
    return data;
  },

  /**
   * Actualiza un negocio existente.
   * @param {Object} negocio - Datos del negocio a actualizar
   * @returns {Promise<Object>} Negocio actualizado
   * @example
   * const actualizado = await negociosService.update({ negocioId: 1, nombre: 'Licorería Star' });
   */
  update: async (negocio) => {
    const { data } = await api.put('/negocios', negocio);
    return data;
  },

  /**
   * Elimina un negocio.
   * @param {number} id - ID del negocio a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await negociosService.delete(1);
   */
  delete: async (id) => {
    const { data } = await api.delete(`/negocios/${id}`);
    return data;
  },

  /* ─── Sedes CRUD ─── */
  /**
   * Obtiene todas las sedes del sistema.
   * @returns {Promise<Array>} Lista de sedes
   * @example
   * const sedes = await negociosService.getAllSedes();
   */
  getAllSedes: async () => {
    const { data } = await api.get('/sedes');
    return toArray(data);
  },

  /**
   * Obtiene las sedes de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de sedes del negocio
   * @example
   * const sedes = await negociosService.getSedesByNegocio(1);
   */
  getSedesByNegocio: async (negocioId) => {
    const { data } = await api.get(`/sedes/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Crea una nueva sede.
   * @param {Object} sede - Datos de la sede a crear
   * @returns {Promise<Object>} Sede creada
   * @example
   * const nueva = await negociosService.createSede({ nombre: 'Sede Central', negocioId: 1 });
   */
  createSede: async (sede) => {
    const { data } = await api.post('/sedes', sede);
    return data;
  },

  /**
   * Actualiza una sede existente.
   * @param {Object} sede - Datos de la sede a actualizar
   * @returns {Promise<Object>} Sede actualizada
   * @example
   * const actualizada = await negociosService.updateSede({ sedeId: 1, nombre: 'Sede Norte' });
   */
  updateSede: async (sede) => {
    const { data } = await api.put('/sedes', sede);
    return data;
  },

  /**
   * Elimina una sede.
   * @param {number} id - ID de la sede a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await negociosService.deleteSede(1);
   */
  deleteSede: async (id) => {
    const { data } = await api.delete(`/sedes/${id}`);
    return data;
  },

  /* ─── Asignación usuario ↔ sede ─── */
  /**
   * Asigna un usuario a una sede.
   * @param {number} usuarioId - ID del usuario
   * @param {number} sedeId - ID de la sede
   * @returns {Promise<Object>} Asignación creada
   * @example
   * const asignacion = await negociosService.assignUsuarioSede(5, 2);
   */
  assignUsuarioSede: async (usuarioId, sedeId) => {
    const { data } = await api.post('/usuarios-sedes', {
      usuario: { id: usuarioId },
      sede: { id: sedeId },
      esPredeterminado: true,
    });
    return data;
  },

  /**
   * Obtiene todas las asignaciones usuario-sede.
   * @returns {Promise<Array>} Lista de asignaciones
   * @example
   * const asignaciones = await negociosService.getUsuariosSedes();
   */
  getUsuariosSedes: async () => {
    const { data } = await api.get('/usuarios-sedes');
    return toArray(data);
  },

  /**
   * Obtiene las asignaciones usuario-sede de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de asignaciones del negocio
   * @example
   * const asignaciones = await negociosService.getUsuariosSedesByNegocio(1);
   */
  getUsuariosSedesByNegocio: async (negocioId) => {
    const { data } = await api.get(`/usuarios-sedes/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Elimina una asignación usuario-sede.
   * @param {number} id - ID de la asignación
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await negociosService.deleteUsuarioSede(1);
   */
  deleteUsuarioSede: async (id) => {
    const { data } = await api.delete(`/usuarios-sedes/${id}`);
    return data;
  },

  /* ─── Suscripciones ─── */
  /**
   * Obtiene todas las suscripciones del sistema.
   * @returns {Promise<Array>} Lista de suscripciones
   * @example
   * const suscripciones = await negociosService.getAllSuscripciones();
   */
  getAllSuscripciones: async () => {
    const { data } = await api.get('/suscripciones');
    return toArray(data);
  },

  /**
   * Crea una nueva suscripción.
   * @param {Object} suscripcion - Datos de la suscripción a crear
   * @returns {Promise<Object>} Suscripción creada
   * @example
   * const nueva = await negociosService.createSuscripcion({ negocioId: 1, planId: 2 });
   */
  createSuscripcion: async (suscripcion) => {
    const { data } = await api.post('/suscripciones', suscripcion);
    return data;
  },

  /**
   * Actualiza una suscripción existente.
   * @param {Object} suscripcion - Datos de la suscripción a actualizar
   * @returns {Promise<Object>} Suscripción actualizada
   * @example
   * const actualizada = await negociosService.updateSuscripcion({ suscripcionId: 1, planId: 3 });
   */
  updateSuscripcion: async (suscripcion) => {
    const { data } = await api.put('/suscripciones', suscripcion);
    return data;
  },

  /* ─── Planes de suscripción ─── */
  /**
   * Obtiene todos los planes de suscripción disponibles.
   * @returns {Promise<Array>} Lista de planes de suscripción
   * @example
   * const planes = await negociosService.getAllPlanes();
   */
  getAllPlanes: async () => {
    const { data } = await api.get('/planes-suscripcion');
    return toArray(data);
  },

  /* ─── Usuarios del negocio ─── */
  /**
   * Obtiene todos los usuarios del sistema.
   * @returns {Promise<Array>} Lista de usuarios
   * @example
   * const usuarios = await negociosService.getAllUsuarios();
   */
  getAllUsuarios: async () => {
    const { data } = await api.get('/usuarios');
    return toArray(data);
  },

  /**
   * Obtiene los usuarios de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de usuarios del negocio
   * @example
   * const usuarios = await negociosService.getUsuariosByNegocio(1);
   */
  getUsuariosByNegocio: async (negocioId) => {
    const { data } = await api.get(`/usuarios/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Crea un nuevo usuario.
   * @param {Object} usuario - Datos del usuario a crear
   * @returns {Promise<Object>} Usuario creado
   * @example
   * const nuevo = await negociosService.createUsuario({ username: 'admin', password: '123' });
   */
  createUsuario: async (usuario) => {
    const { data } = await api.post('/usuarios', usuario);
    return data;
  },

  /**
   * Actualiza un usuario existente.
   * @param {Object} usuario - Datos del usuario a actualizar
   * @returns {Promise<Object>} Usuario actualizado
   * @example
   * const actualizado = await negociosService.updateUsuario({ usuarioId: 1, email: 'nuevo@mail.com' });
   */
  updateUsuario: async (usuario) => {
    const { data } = await api.put('/usuarios', usuario);
    return data;
  },

  /* ─── Facturas de suscripción ─── */
  /**
   * Obtiene todas las facturas de suscripción del sistema.
   * @returns {Promise<Array>} Lista de facturas
   * @example
   * const facturas = await negociosService.getAllFacturas();
   */
  getAllFacturas: async () => {
    const { data } = await api.get('/facturas-suscripcion');
    return Array.isArray(data) ? data : [];
  },

  /**
   * Obtiene las facturas de suscripción de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de facturas del negocio
   * @example
   * const facturas = await negociosService.getFacturasByNegocio(1);
   */
  getFacturasByNegocio: async (negocioId) => {
    const { data } = await api.get(`/facturas-suscripcion/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Genera una nueva factura para una suscripción.
   * @param {number} suscripcionId - ID de la suscripción
   * @returns {Promise<Object>} Factura generada
   * @example
   * const factura = await negociosService.generarFactura(1);
   */
  generarFactura: async (suscripcionId) => {
    const { data } = await api.post(`/facturas-suscripcion/generar/${suscripcionId}`);
    return data;
  },

  /**
   * Registra el pago de una factura de suscripción.
   * @param {Object} params - Parámetros del pago
   * @param {number} params.id - ID de la factura
   * @param {string} [params.metodoPago] - Método de pago utilizado
   * @param {string} [params.referencia] - Referencia del pago
   * @returns {Promise<Object>} Factura actualizada
   * @example
   * const resultado = await negociosService.pagarFactura({ id: 1, metodoPago: 'TARJETA', referencia: 'REF123' });
   */
  pagarFactura: async ({ id, metodoPago, referencia }) => {
    const params = new URLSearchParams();
    if (metodoPago) params.append('metodoPago', metodoPago);
    if (referencia) params.append('referencia', referencia);
    const { data } = await api.patch(`/facturas-suscripcion/${id}/pagar?${params}`);
    return data;
  },

  /**
   * Cancela una factura de suscripción.
   * @param {number} id - ID de la factura a cancelar
   * @returns {Promise<Object>} Factura cancelada
   * @example
   * const resultado = await negociosService.cancelarFactura(1);
   */
  cancelarFactura: async (id) => {
    const { data } = await api.patch(`/facturas-suscripcion/${id}/cancelar`);
    return data;
  },
};
