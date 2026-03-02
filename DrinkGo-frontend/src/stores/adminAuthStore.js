import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAdminAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      negocio: null,
      /** Lista de códigos de permiso del usuario, e.g. ['m.dashboard', 'm.ventas.pos'] */
      permisos: [],

      login: (user, token, negocio, permisos = []) =>
        set({ user, token, negocio, permisos }),

      logout: () => {
        set({ user: null, token: null, negocio: null, permisos: [] });
      },

      isAuthenticated: () => !!get().token,

      /** Devuelve true si el usuario tiene el permiso dado, o si tiene acceso completo (Administrador). */
      hasPermiso: (codigo) => {
        const permisos = get().permisos;
        if (!permisos || permisos.length === 0) return false;
        // Si tiene TODOS los permisos de admin (≥10 módulos padre) se considera superusuario
        const tieneTodos = permisos.some((p) => p === 'm.dashboard') &&
          permisos.some((p) => p === 'm.ventas') &&
          permisos.some((p) => p === 'm.reportes');
        if (tieneTodos) return true;
        return permisos.includes(codigo);
      },
    }),
    {
      name: 'admin-auth-storage',
    },
  ),
);
