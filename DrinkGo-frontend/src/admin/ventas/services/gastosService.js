import { adminApi } from '@/admin/services/adminApi';

export const gastosService = {
  /**
   * Obtiene todos los gastos externos de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de gastos del negocio
   * @example
   * const gastos = await gastosService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/gastos/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  /**
   * Crea un nuevo gasto externo.
   * @param {Object} gasto - Datos del gasto a crear
   * @param {number} gasto.negocioId - ID del negocio
   * @param {number} gasto.categoriaId - ID de la categoría
   * @param {number} gasto.monto - Monto del gasto
   * @param {string} gasto.descripcion - Descripción del gasto
   * @returns {Promise<Object>} Gasto creado
   * @example
   * const nuevoGasto = await gastosService.create({
   *   negocioId: 5,
   *   categoriaId: 2,
   *   monto: 150.00,
   *   descripcion: 'Factura de luz'
   * });
   */
  create: async (gasto) => {
    const { data } = await adminApi.post('/gastos', gasto);
    return data;
  },

  /**
   * Actualiza un gasto externo existente.
   * @param {Object} gasto - Datos del gasto a actualizar
   * @param {number} gasto.gastoId - ID del gasto
   * @returns {Promise<Object>} Gasto actualizado
   * @example
   * const actualizado = await gastosService.update({ gastoId: 1, monto: 200.00 });
   */
  update: async (gasto) => {
    const { data } = await adminApi.put('/gastos', gasto);
    return data;
  },

  /**
   * Elimina un gasto externo.
   * @param {number} id - ID del gasto a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await gastosService.remove(1);
   */
  remove: async (id) => {
    const { data } = await adminApi.delete(`/gastos/${id}`);
    return data;
  },

  /**
   * Marca un gasto como pagado.
   * @param {Object} params - Parámetros del pago
   * @param {number} params.id - ID del gasto
   * @param {string} [params.metodoPago] - Método de pago
   * @param {string} [params.referencia] - Referencia del pago
   * @returns {Promise<Object>} Gasto actualizado
   * @example
   * await gastosService.marcarPagado({ id: 1, metodoPago: 'TRANSFERENCIA', referencia: 'TRX-123' });
   */
  marcarPagado: async ({ id, metodoPago, referencia }) => {
    const params = {};
    if (metodoPago) params.metodoPago = metodoPago;
    if (referencia) params.referencia = referencia;
    const { data } = await adminApi.patch(`/gastos/${id}/pagar`, null, { params });
    return data;
  },

  /**
   * Sube un comprobante de pago para un gasto.
   * @param {Object} params - Parámetros de la subida
   * @param {number} params.id - ID del gasto
   * @param {File} params.archivo - Archivo del comprobante
   * @returns {Promise<Object>} Gasto con comprobante subido
   * @example
   * const archivo = new File(['...'], 'comprobante.pdf');
   * await gastosService.subirComprobante({ id: 1, archivo });
   */
  subirComprobante: async ({ id, archivo, metodoPago, referenciaPago }) => {
    const formData = new FormData();
    formData.append('archivo', archivo);
    if (metodoPago) formData.append('metodoPago', metodoPago);
    if (referenciaPago) formData.append('referenciaPago', referenciaPago);
    const { data } = await adminApi.postForm(`/gastos/${id}/comprobante`, formData);
    return data;
  },

  /**
   * Elimina el comprobante de pago de un gasto.
   * @param {number} id - ID del gasto
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await gastosService.eliminarComprobante(1);
   */
  eliminarComprobante: async (id) => {
    const { data } = await adminApi.delete(`/gastos/${id}/comprobante`);
    return data;
  },

  /* ─── Categorías de gasto ─── */
  /**
   * Obtiene las categorías de gasto de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de categorías
   * @example
   * const categorias = await gastosService.getCategoriasByNegocio(5);
   */
  getCategoriasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/categorias-gasto/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  /**
   * Crea una nueva categoría de gasto.
   * @param {Object} categoria - Datos de la categoría
   * @returns {Promise<Object>} Categoría creada
   * @example
   * const nueva = await gastosService.createCategoria({ nombre: 'Servicios', negocioId: 5 });
   */
  createCategoria: async (categoria) => {
    const { data } = await adminApi.post('/categorias-gasto', categoria);
    return data;
  },

  /**
   * Actualiza una categoría de gasto existente.
   * @param {Object} categoria - Datos de la categoría a actualizar
   * @returns {Promise<Object>} Categoría actualizada
   * @example
   * const actualizada = await gastosService.updateCategoria({ categoriaId: 1, nombre: 'Suministros' });
   */
  updateCategoria: async (categoria) => {
    const { data } = await adminApi.put('/categorias-gasto', categoria);
    return data;
  },

  /**
   * Elimina una categoría de gasto.
   * @param {number} id - ID de la categoría a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await gastosService.deleteCategoria(1);
   */
  deleteCategoria: async (id) => {
    const { data } = await adminApi.delete(`/categorias-gasto/${id}`);
    return data;
  },

  /* ─── Facturas de suscripción del negocio (cobros del sistema) ─── */
  /**
   * Obtiene las facturas de suscripción de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de facturas de suscripción
   * @example
   * const facturas = await gastosService.getFacturasByNegocio(5);
   */
  getFacturasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/facturas-suscripcion/por-negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  /**
   * Registra el pago de una factura de suscripción.
   * @param {Object} params - Parámetros del pago
   * @param {number} params.id - ID de la factura
   * @param {string} [params.metodoPago] - Método de pago
   * @param {string} [params.referencia] - Referencia del pago
   * @returns {Promise<Object>} Factura actualizada
   * @example
   * await gastosService.pagarFacturaSuscripcion({ id: 1, metodoPago: 'YAPE', referencia: 'YPE-123' });
   */
  pagarFacturaSuscripcion: async ({ id, metodoPago, referencia }) => {
    const params = {};
    if (metodoPago) params.metodoPago = metodoPago;
    if (referencia) params.referencia = referencia;
    const { data } = await adminApi.patch(`/facturas-suscripcion/${id}/pagar`, null, { params });
    return data;
  },
};
