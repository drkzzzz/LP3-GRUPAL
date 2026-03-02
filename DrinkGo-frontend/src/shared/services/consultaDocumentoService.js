/**
 * consultaDocumentoService.js
 * ───────────────────────────
 * Servicio para consultar RUC (SUNAT) y DNI (RENIEC)
 * a través del backend proxy que consume la API PeruDevs.
 */
import { adminApi } from '@/admin/services/adminApi';

/**
 * Consultar datos de empresa por RUC (SUNAT).
 * @param {string} numero - RUC de 11 dígitos
 * @returns {Promise<{
 *   razon_social: string,
 *   condicion: string,
 *   nombre_comercial: string,
 *   tipo: string,
 *   fecha_inscripcion: string,
 *   estado: string,
 *   direccion: string,
 *   sistema_emision: string,
 *   actividad_exterior: string,
 *   sistema_contabilidad: string
 * }>}
 */
export const consultarRuc = async (numero) => {
  const { data } = await adminApi.get('/consulta/ruc', { params: { numero } });
  return data;
};

/**
 * Consultar datos de persona por DNI (RENIEC).
 * @param {string} numero - DNI de 8 dígitos
 * @returns {Promise<{
 *   id: string,
 *   nombres: string,
 *   apellido_paterno: string,
 *   apellido_materno: string,
 *   nombre_completo: string,
 *   genero: string,
 *   fecha_nacimiento: string,
 *   codigo_verificacion: string
 * }>}
 */
export const consultarDni = async (numero) => {
  const { data } = await adminApi.get('/consulta/dni', { params: { numero } });
  return data;
};
