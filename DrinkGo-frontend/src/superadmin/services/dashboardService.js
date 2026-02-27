import { api } from '@/config/api';

/**
 * Dashboard data is computed client-side from existing endpoints.
 * Uses allSettled so one failing endpoint doesn't crash everything.
 */
const safeGet = async (path) => {
  try {
    const r = await api.get(path);
    const data = r.data;
    if (Array.isArray(data)) return data;
    if (data && Array.isArray(data.content)) return data.content;
    if (data && Array.isArray(data.data)) return data.data;
    return Array.isArray(data) ? data : [];
  } catch {
    return [];
  }
};

export const dashboardService = {
  getStats: async () => {
    const [negocios, planes, facturas, suscripciones] = await Promise.all([
      safeGet('/negocios'),
      safeGet('/planes-suscripcion'),
      safeGet('/facturas-suscripcion'),
      safeGet('/suscripciones'),
    ]);
    return { negocios, planes, facturas, suscripciones };
  },
};
