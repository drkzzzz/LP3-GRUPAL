import { api } from '@/config/api';

/**
 * Servicios para el rol programador de plataforma.
 * Utiliza el token JWT del superadmin login para autenticarse.
 */
export const programadorService = {
  /**
   * Devuelve todos los negocios activos con sus sedes anidadas.
   * Usado en la pantalla de selección previa al panel admin.
   */
  getNegociosConSedes: async () => {
    const { data } = await api.get('/superadmin/programador/negocios-con-sedes');
    return data; // Array de { id, uuid, nombreComercial, razonSocial, ruc, estado, urlLogo, sedes[] }
  },

  /**
   * Actualiza los módulos asignados a un usuario programador.
   * Solo superadmin puede llamar este endpoint.
   * @param {number} usuarioId - ID del usuario programador
   * @param {string[]} modulos - Array de códigos de módulo, ej: ['m.catalogo', 'm.ventas']
   */
  actualizarModulos: async (usuarioId, modulos) => {
    const { data } = await api.patch(
      `/superadmin/usuarios-plataforma/${usuarioId}/modulos`,
      { modulosAsignados: JSON.stringify(modulos) },
    );
    return data;
  },
};
