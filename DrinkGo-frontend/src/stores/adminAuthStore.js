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
      /** Lista de códigos de permiso del usuario, e.g. ['m.dashboard', 'm.ventas.pos'] */
      permisos: [],
      /**
       * true cuando el usuario es un programador de plataforma que ingresó al panel
       * admin seleccionando un negocio desde su vista de programador.
       */
      esProgramador: false,

      login: (user, token, negocio, permisos = [], sede = null) =>
        set({ user, token, negocio, permisos, sede, esProgramador: false }),

      /** Ingreso especial de programador: configura el contexto de negocio/sede sin cambiar el token base */
      loginProgramador: (user, token, negocio, sede, modulos = []) =>
        set({ user, token, negocio, sede, permisos: modulos, esProgramador: true }),

      /** Actualiza el objeto negocio (p.ej. tras guardar configuración) */
      setNegocio: (negocio) => set({ negocio }),

      logout: () => {
        set({ user: null, token: null, negocio: null, sede: null, permisos: [], esProgramador: false });
      },

      isAuthenticated: () => !!get().token,

      /**
       * Devuelve true si el usuario tiene el permiso dado.
       * Soporta herencia: si 'm.ventas' está en permisos, 'm.ventas.pos' también es accesible.
       */
      hasPermiso: (codigo) => {
        const permisos = get().permisos;
        if (!permisos || permisos.length === 0) return false;
        // Coincidencia exacta
        if (permisos.includes(codigo)) return true;
        // Herencia de módulo padre: 'm.ventas' habilita 'm.ventas.pos', 'm.ventas.cajas', etc.
        const parts = codigo.split('.');
        for (let i = parts.length - 1; i > 0; i--) {
          const parentCode = parts.slice(0, i).join('.');
          if (permisos.includes(parentCode)) return true;
        }
        return false;
      },
    }),
    {
      name: 'admin-auth-storage',
    },
  ),
);
