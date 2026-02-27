import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAdminAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      negocio: null,

      login: (user, token, negocio) => set({ user, token, negocio }),

      logout: () => {
        set({ user: null, token: null, negocio: null });
      },

      isAuthenticated: () => !!get().token,
    }),
    {
      name: 'admin-auth-storage',
    },
  ),
);
