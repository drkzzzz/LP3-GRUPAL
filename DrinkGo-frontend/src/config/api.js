import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/restful',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor — attach JWT token
// Reads from Zustand in-memory state (avoids localStorage timing race condition)
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// Response interceptor — handle 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Only redirect if not already on login page AND we have no token in memory
      const isLoginPage = window.location.pathname.includes('/login');
      if (!isLoginPage) {
        const currentToken = useAuthStore.getState().token;
        if (!currentToken) {
          localStorage.removeItem('auth-storage');
          window.location.href = '/superadmin/login';
        }
      }
    }
    return Promise.reject(error);
  },
);
