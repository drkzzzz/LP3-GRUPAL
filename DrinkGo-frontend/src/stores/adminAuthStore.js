import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAdminAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      negocio: null,
      /** Sede asignada al usuario (viene directa del login) */
      sede: null,
      /**
       * Lista de permisos del usuario. Cada elemento es un objeto:
       * { codigo: 'm.ventas.pos', alcance: 'completo' | 'caja_asignada' }
       */
      permisos: [],
      /** Caja asignada al usuario (cajero). { id, nombreCaja, codigo } o null */
      cajaAsignada: null,
      /**
       * true cuando el usuario es un programador de plataforma que ingresó al panel
       * admin seleccionando un negocio desde su vista de programador.
       */
      esProgramador: false,

      login: (user, token, negocio, permisos = [], sede = null, cajaAsignada = null) =>
        set({ user, token, negocio, permisos, sede, cajaAsignada, esProgramador: false }),

      /** Ingreso especial de programador: configura el contexto de negocio/sede sin cambiar el token base */
      loginProgramador: (user, token, negocio, sede, modulos = []) =>
        set({
          user, token, negocio, sede,
          permisos: modulos.map((m) => (typeof m === 'string' ? { codigo: m, alcance: 'completo' } : m)),
          cajaAsignada: null,
          esProgramador: true,
        }),

      /** Actualiza el objeto negocio (p.ej. tras guardar configuración) */
      setNegocio: (negocio) => set({ negocio }),

      logout: () => {
        set({ user: null, token: null, negocio: null, sede: null, permisos: [], cajaAsignada: null, esProgramador: false });
      },

      isAuthenticated: () => !!get().token,

      /**
       * Devuelve true si el usuario tiene el permiso dado.
       * Soporta herencia: si 'm.ventas' está en permisos, 'm.ventas.pos' también es accesible.
       */
      hasPermiso: (codigo) => {
        const permisos = get().permisos;
        if (!permisos || permisos.length === 0) return false;
        // Extraer códigos (soporta formato antiguo string[] y nuevo objeto[])
        const codigos = permisos.map((p) => (typeof p === 'string' ? p : p.codigo));
        // Coincidencia exacta
        if (codigos.includes(codigo)) return true;
        // Herencia de módulo padre: 'm.ventas' habilita 'm.ventas.pos', 'm.ventas.cajas', etc.
        const parts = codigo.split('.');
        for (let i = parts.length - 1; i > 0; i--) {
          const parentCode = parts.slice(0, i).join('.');
          if (codigos.includes(parentCode)) return true;
        }
        return false;
      },

      /**
       * Devuelve el alcance de un permiso: 'completo' | 'caja_asignada'.
       * Si no se encuentra el permiso exacto, busca en padres (herencia).
       * Default: 'completo'.
       */
      getAlcance: (codigo) => {
        const permisos = get().permisos;
        if (!permisos || permisos.length === 0) return 'completo';
        // Buscar coincidencia exacta primero
        const exacto = permisos.find((p) =>
          (typeof p === 'string' ? p : p.codigo) === codigo,
        );
        if (exacto) return typeof exacto === 'string' ? 'completo' : (exacto.alcance || 'completo');
        // Buscar por herencia (padre más específico)
        const parts = codigo.split('.');
        for (let i = parts.length - 1; i > 0; i--) {
          const parentCode = parts.slice(0, i).join('.');
          const parent = permisos.find((p) =>
            (typeof p === 'string' ? p : p.codigo) === parentCode,
          );
          if (parent) return typeof parent === 'string' ? 'completo' : (parent.alcance || 'completo');
        }
        return 'completo';
      },
    }),
    {
      name: 'admin-auth-storage',
    },
  ),
);
