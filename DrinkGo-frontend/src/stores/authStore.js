import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,

      login: (user, token) => set({ user, token }),

      logout: () => {
        set({ user: null, token: null });
      },

      isAuthenticated: () => !!get().token,

      /** Devuelve true si el usuario logueado es un programador */
      esProgramador: () => get().user?.rol === 'programador',

      /** Retorna los módulos asignados al programador como array de strings */
      getModulosAsignados: () => {
        const user = get().user;
        if (!user || user.rol !== 'programador') return [];
        const raw = user.modulosAsignados;
        if (!raw) return [];
        try {
          const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw;
          return Array.isArray(parsed) ? parsed : [];
        } catch {
          return [];
        }
      },
    }),
    {
      name: 'auth-storage',
    },
  ),
);
