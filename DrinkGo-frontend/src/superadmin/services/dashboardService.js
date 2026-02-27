import { api } from '@/config/api';

/**
 * Dashboard data is computed client-side from existing endpoints.
 * This service aggregates the calls.
 */
export const dashboardService = {
  getStats: async () => {
    const [negocios, planes, facturas, suscripciones] = await Promise.all([
      api.get('/negocios').then((r) => r.data),
      api.get('/planes-suscripcion').then((r) => r.data),
      api.get('/facturas-suscripcion').then((r) => r.data),
      api.get('/suscripciones').then((r) => r.data),
    ]);
    return { negocios, planes, facturas, suscripciones };
  },
};
