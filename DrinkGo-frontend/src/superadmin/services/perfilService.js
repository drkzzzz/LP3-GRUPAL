import { api } from '@/config/api';

export const perfilService = {
  getPerfil: async () => {
    const { data } = await api.get('/superadmin/perfil');
    return data;
  },

  updatePerfil: async (datos) => {
    const { data } = await api.patch('/superadmin/perfil', datos);
    return data;
  },

  changePassword: async (contrasenaActual, nuevaContrasena, confirmarContrasena) => {
    const { data } = await api.post('/superadmin/perfil/cambiar-contrasena', {
      contrasenaActual,
      nuevaContrasena,
      confirmarContrasena,
    });
    return data;
  },
};
