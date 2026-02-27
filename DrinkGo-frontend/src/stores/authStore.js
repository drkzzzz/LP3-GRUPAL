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
    }),
    {
      name: 'auth-storage',
    },
  ),
);
