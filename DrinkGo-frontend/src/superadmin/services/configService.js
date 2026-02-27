/**
 * Config Service — RF-CGL-001
 * Persiste la configuración global en localStorage hasta que exista un endpoint backend.
 */

const CONFIG_KEY = 'drinkgo_platform_config';

const DEFAULTS = {
  nombrePlataforma: 'DrinkGo',
  version: '1.0.0',
  monedaDefecto: 'PEN',
  impuestoPorDefecto: 18,
  tiempoExpiracionSesion: 24,
  maxIntentosSesion: 5,
  notificacionEmail: true,
  alertasFacturacion: true,
  alertasAuditoria: false,
  respaldoAutomatico: true,
  diasGraciaFactura: 3,
  urlSoporte: 'soporte@drinkgo.com',
};

export const configService = {
  get: async () => {
    try {
      const stored = localStorage.getItem(CONFIG_KEY);
      return stored ? { ...DEFAULTS, ...JSON.parse(stored) } : { ...DEFAULTS };
    } catch {
      return { ...DEFAULTS };
    }
  },

  save: async (config) => {
    localStorage.setItem(CONFIG_KEY, JSON.stringify(config));
    return config;
  },

  reset: async () => {
    localStorage.removeItem(CONFIG_KEY);
    return { ...DEFAULTS };
  },
};
