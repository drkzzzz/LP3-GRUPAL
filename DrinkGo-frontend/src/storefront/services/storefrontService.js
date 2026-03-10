import axios from 'axios';
import { useStorefrontAuthStore } from '../stores/storefrontAuthStore';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/restful';

/* ── API pública (sin token requerido) ── */
export const publicApi = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

/* ── API autenticada (cliente con token) ── */
export const customerApi = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

customerApi.interceptors.request.use((config) => {
  const token = useStorefrontAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

customerApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const slug = useStorefrontAuthStore.getState().slug;
      const isAuthPage =
        window.location.pathname.includes('/login') ||
        window.location.pathname.includes('/registro');
      if (!isAuthPage && slug) {
        useStorefrontAuthStore.getState().logout();
      }
    }
    return Promise.reject(error);
  },
);

/* ── Helpers ── */
const toArray = (d) => (Array.isArray(d) ? d : d?.content || []);

/* ── URL helper para imágenes/logos ── */
export const UPLOADS_BASE = (import.meta.env.VITE_API_BASE_URL || '/restful').replace('/restful', '');

export const getImageUrl = (path) => {
  if (!path) return null;
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('/')) return path;
  return `${UPLOADS_BASE}/${path}`;
};

/* ══════════════════════════════════════════════════════════
   NOTA PARA BACKEND:
   Los endpoints /tienda/public/* deben ser públicos (permitAll)
   en SecurityFilterChain. Los endpoints /tienda/{slug}/* 
   requieren token de cliente.
   ══════════════════════════════════════════════════════════ */

export const storefrontService = {
  /* ── Configuración de tienda (público) ── */
  getConfigBySlug: async (slug) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}`);
    return data;
  },

  /* ── Sedes activas (público) ── */
  getSedesBySlug: async (slug) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/sedes`);
    return toArray(data);
  },

  /* ── Categorías visibles en tienda (público) ── */
  getCategoriasBySlug: async (slug) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/categorias`);
    return toArray(data);
  },

  /* ── Productos visibles en tienda (público) ── */
  getProductosBySlug: async (slug, params = {}) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/productos`, { params });
    return data;
  },

  getProductoById: async (slug, productoId) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/productos/${productoId}`);
    return data;
  },

  /* ── Métodos de pago habilitados para tienda online (público) ── */
  getMetodosPago: async (slug) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/metodos-pago`);
    return toArray(data);
  },

  /* ── Zonas de delivery por sede (público) ── */
  getZonasDelivery: async (slug, sedeId) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/zonas-delivery`, {
      params: { sedeId },
    });
    return toArray(data);
  },

  /* ── Autenticación de cliente ── */
  login: async (credentials) => {
    const { data } = await publicApi.post('/tienda/auth/login', credentials);
    return data;
  },

  register: async (clienteData) => {
    const { data } = await publicApi.post('/tienda/auth/registro', clienteData);
    return data;
  },

  /* ── Pedidos (requiere token de cliente) ── */
  createPedido: async (slug, pedido) => {
    const { data } = await customerApi.post(`/tienda/${slug}/pedidos`, pedido);
    return data;
  },

  getMisPedidos: async (slug) => {
    const { data } = await customerApi.get(`/tienda/${slug}/mis-pedidos`);
    return toArray(data);
  },

  getPedidoById: async (slug, pedidoId) => {
    const { data } = await customerApi.get(`/tienda/${slug}/mis-pedidos/${pedidoId}`);
    return data;
  },

  getPedidoPago: async (slug, pedidoId) => {
    const { data } = await customerApi.get(`/tienda/${slug}/pedidos/${pedidoId}/pago`);
    return data;
  },

  subirComprobante: async (slug, pedidoId, archivo) => {
    const formData = new FormData();
    formData.append('archivo', archivo);
    const { data } = await customerApi.post(
      `/tienda/${slug}/pedidos/${pedidoId}/comprobante`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
    return data;
  },

  /* ── Consulta DNI/CE (público) ── */
  consultarDni: async (numero) => {
    const { data } = await publicApi.get('/consulta/dni', { params: { numero } });
    return data;
  },

  /* ── Perfil del cliente (requiere token) ── */
  getProfile: async (slug) => {
    const { data } = await customerApi.get(`/tienda/${slug}/mi-perfil`);
    return data;
  },

  updateProfile: async (slug, profileData) => {
    const { data } = await customerApi.put(`/tienda/${slug}/mi-perfil`, profileData);
    return data;
  },

  changePassword: async (slug, passwordData) => {
    const { data } = await customerApi.put(`/tienda/${slug}/mi-perfil/password`, passwordData);
    return data;
  },

  /* ── Devoluciones del cliente (requiere token) ── */
  getMisDevoluciones: async (slug) => {
    const { data } = await customerApi.get(`/tienda/${slug}/mis-devoluciones`);
    return toArray(data);
  },

  solicitarDevolucion: async (slug, devolucionData) => {
    const { data } = await customerApi.post(`/tienda/${slug}/solicitar-devolucion`, devolucionData);
    return data;
  },

  /* ── Combos visibles en tienda (público) ── */
  getCombosBySlug: async (slug, sedeId) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/combos`, {
      params: sedeId ? { sedeId } : {},
    });
    return toArray(data);
  },

  /* ── Promociones vigentes (público) ── */
  getPromocionesActivasBySlug: async (slug, sedeId) => {
    const { data } = await publicApi.get(`/tienda/public/${slug}/promociones`, {
      params: sedeId ? { sedeId } : {},
    });
    return toArray(data);
  },
};
