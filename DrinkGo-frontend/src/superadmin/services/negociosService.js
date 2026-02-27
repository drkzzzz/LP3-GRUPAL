import { api } from '@/config/api';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  if (Array.isArray(data.data)) return data.data;
  return [];
};

export const negociosService = {
  /* ─── Negocios CRUD ─── */
  getAll: async () => {
    const { data } = await api.get('/negocios');
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await api.get(`/negocios/${id}`);
    return data;
  },

  create: async (negocio) => {
    const { data } = await api.post('/negocios', negocio);
    return data;
  },

  update: async (negocio) => {
    const { data } = await api.put('/negocios', negocio);
    return data;
  },

  delete: async (id) => {
    const { data } = await api.delete(`/negocios/${id}`);
    return data;
  },

  /* ─── Sedes CRUD ─── */
  getAllSedes: async () => {
    const { data } = await api.get('/sedes');
    return toArray(data);
  },

  createSede: async (sede) => {
    const { data } = await api.post('/sedes', sede);
    return data;
  },

  updateSede: async (sede) => {
    const { data } = await api.put('/sedes', sede);
    return data;
  },

  deleteSede: async (id) => {
    const { data } = await api.delete(`/sedes/${id}`);
    return data;
  },

  /* ─── Suscripciones ─── */
  getAllSuscripciones: async () => {
    const { data } = await api.get('/suscripciones');
    return toArray(data);
  },

  createSuscripcion: async (suscripcion) => {
    const { data } = await api.post('/suscripciones', suscripcion);
    return data;
  },

  updateSuscripcion: async (suscripcion) => {
    const { data } = await api.put('/suscripciones', suscripcion);
    return data;
  },

  /* ─── Planes de suscripción ─── */
  getAllPlanes: async () => {
    const { data } = await api.get('/planes-suscripcion');
    return toArray(data);
  },

  /* ─── Usuarios del negocio ─── */
  getAllUsuarios: async () => {
    const { data } = await api.get('/usuarios');
    return toArray(data);
  },

  getUsuariosByNegocio: async (negocioId) => {
    const { data } = await api.get(`/usuarios/por-negocio/${negocioId}`);
    return toArray(data);
  },

  createUsuario: async (usuario) => {
    const { data } = await api.post('/usuarios', usuario);
    return data;
  },

  updateUsuario: async (usuario) => {
    const { data } = await api.put('/usuarios', usuario);
    return data;
  },
};
