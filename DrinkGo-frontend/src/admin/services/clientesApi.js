/**
 * Servicio API para gestión de clientes
 * Endpoints: /restful/clientes
 */
import { adminApi } from './adminApi';

/**
 * ==========================================
 * CLIENTES - CRUD y búsqueda
 * ==========================================
 */

/**
 * Obtener todos los clientes del negocio
 * GET /restful/clientes
 */
export const getClientes = async () => {
  const response = await adminApi.get('/clientes');
  return response.data;
};

/**
 * Buscar clientes por nombre, teléfono o documento
 * @param {string} termino - Término de búsqueda
 * @returns {Promise<Array>} - Lista de clientes que coinciden
 */
export const buscarClientes = async (termino) => {
  if (!termino || termino.trim().length < 2) {
    return [];
  }
  
  const response = await adminApi.get('/clientes');
  const clientes = response.data;
  
  // Filtrar localmente (si el backend no tiene endpoint de búsqueda)
  const terminoBusqueda = termino.toLowerCase().trim();
  return clientes.filter(cliente => {
    const nombre = `${cliente.nombres || ''} ${cliente.apellidos || ''}`.toLowerCase();
    const telefono = (cliente.telefono || '').toString();
    const documento = (cliente.numeroDocumento || '').toString();
    
    return nombre.includes(terminoBusqueda) ||
           telefono.includes(terminoBusqueda) ||
           documento.includes(terminoBusqueda);
  });
};

/**
 * Obtener cliente por ID
 * GET /restful/clientes/:id
 */
export const getClienteById = async (id) => {
  const response = await adminApi.get(`/clientes/${id}`);
  return response.data;
};

/**
 * Crear nuevo cliente rápido
 * POST /restful/clientes
 */
export const createClienteRapido = async (clienteData) => {
  // Obtener negocio del store (debe estar disponible en sesión admin)
  const { useAdminAuthStore } = await import('@/stores/adminAuthStore');
  const negocioId = useAdminAuthStore.getState().negocio?.id;
  
  if (!negocioId) {
    throw new Error('No se encontró el negocio en la sesión');
  }
  
  const payload = {
    negocio: { id: negocioId },
    tipoDocumento: clienteData.numeroDocumento ? 'DNI' : 'OTRO',
    numeroDocumento: clienteData.numeroDocumento || null,
    nombres: clienteData.nombres,
    apellidos: clienteData.apellidos || null,
    telefono: clienteData.telefono,
    email: clienteData.email || null,
    direccion: clienteData.direccion || null,
    estaActivo: true,
  };
  
  const response = await adminApi.post('/clientes', payload);
  return response.data;
};

/**
 * Actualizar cliente
 * PUT /restful/clientes
 */
export const updateCliente = async (clienteData) => {
  const response = await adminApi.put('/clientes', clienteData);
  return response.data;
};

/**
 * Eliminar cliente (soft delete)
 * DELETE /restful/clientes/:id
 */
export const deleteCliente = async (id) => {
  const response = await adminApi.delete(`/clientes/${id}`);
  return response.data;
};
