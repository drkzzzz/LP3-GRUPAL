import { adminApi } from '@/admin/services/adminApi';

export const perfilAdminService = {
  getPerfil: async () => {
    const { data } = await adminApi.get('/admin/perfil');
    return data;
  },

  updatePerfil: async (datos) => {
    const { data } = await adminApi.patch('/admin/perfil', datos);
    return data;
  },

  changePassword: async (contrasenaActual, nuevaContrasena, confirmarContrasena) => {
    const { data } = await adminApi.post('/admin/perfil/cambiar-contrasena', {
      contrasenaActual,
      nuevaContrasena,
      confirmarContrasena,
    });
    return data;
  },
};
