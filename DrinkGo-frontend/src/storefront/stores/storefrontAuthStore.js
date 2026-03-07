import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useStorefrontAuthStore = create(
  persist(
    (set, get) => ({
      customer: null,
      token: null,
      slug: null,
      negocioId: null,

      login: (customer, token, slug, negocioId) =>
        set({ customer, token, slug, negocioId }),

      setSlug: (slug) => set({ slug }),
      setNegocioId: (negocioId) => set({ negocioId }),

      logout: () => set({ customer: null, token: null }),

      isAuthenticated: () => !!get().token,
    }),
    { name: 'storefront-auth-storage' },
  ),
);
