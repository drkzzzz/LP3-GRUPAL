/**
 * Hook para crear pedidos manuales (tienda/teléfono)
 */
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createPedido } from '@/admin/pedidos/services/pedidosApi';
import { message } from '@/shared/utils/notifications';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/**
 * Hook para crear pedido manual completo
 * Incluye validación de datos y creación del pedido con sus detalles
 */
export function useCrearPedidoManual() {
  const queryClient = useQueryClient();
  const { negocio, sede } = useAdminAuthStore();
  
  return useMutation({
    mutationFn: async (pedidoData) => {
      /**
       * pedidoData structure:
       * {
       *   // Datos del pedido
       *   clienteId: number,
       *   tipoPedido: 'delivery' | 'recojo_tienda' | 'consumo_local',
       *   origenPedido: 'telefono' | 'whatsapp' | 'pos' | 'otro',
       *   
       *   // Dirección (solo si es delivery)
       *   direccionEntrega: string,
       *   departamento: string,
       *   provincia: string,
       *   distrito: string,
       *   referencia: string,
       *   zonaDeliveryId: number,
       *   
       *   // Items del pedido
       *   items: [{
       *     productoId: number,
       *     nombreProducto: string,
       *     skuProducto: string,
       *     cantidad: number,
       *     precioUnitario: number,
       *   }],
       *   
       *   // Montos
       *   subtotal: number,
       *   costoDelivery: number,
       *   total: number,
       *   
       *   // Observaciones y método de pago
       *   observaciones: string,
       *   metodoPago: string,
       * }
       */
      
      // Validar datos mínimos
      if (!pedidoData.clienteId) {
        throw new Error('Debe seleccionar un cliente');
      }
      
      if (!pedidoData.items || pedidoData.items.length === 0) {
        throw new Error('Debe agregar al menos un producto');
      }
      
      if (pedidoData.tipoPedido === 'delivery' && !pedidoData.distrito) {
        throw new Error('Debe ingresar el distrito para delivery');
      }
      
      // Preparar payload para el backend
      const payload = {
        negocio: { id: negocio?.id || 1 }, // ✅ Obtener del store
        sede: { id: sede?.id || 1 }, // ✅ Obtener del store
        cliente: { id: pedidoData.clienteId }, // ✅ Objeto con ID (NO clienteId)
        tipoPedido: pedidoData.tipoPedido,
        origenPedido: pedidoData.origenPedido || 'telefono',
        
        // Dirección (solo delivery)
        direccionEntrega: pedidoData.direccionEntrega || null,
        departamento: pedidoData.departamento || null,
        provincia: pedidoData.provincia || null,
        distrito: pedidoData.distrito || null,
        referencia: pedidoData.referencia || null,
        zonaDelivery: pedidoData.zonaDeliveryId ? { id: pedidoData.zonaDeliveryId } : null,
        
        // Montos
        subtotal: pedidoData.subtotal,
        montoDescuento: 0,
        impuestos: pedidoData.impuestos || 0, // IGV calculado en modal
        costoDelivery: pedidoData.costoDelivery || 0,
        total: pedidoData.total,
        moneda: 'PEN',
        
        // Campo para el método de pago (si existe en la entidad)
        metodoPago: pedidoData.metodoPago || 'Efectivo',
        
        // Estado inicial
        estadoPedido: 'pendiente',
        
        // Observaciones puras
        observaciones: pedidoData.observaciones || null,
        
        // Usuario que registra (TODO: obtener del contexto)
        usuario: null, // Cambiar a objeto cuando tengamos auth
        
        // Fechas
        fechaPedido: new Date().toISOString(),
        fechaEntregaEstimada: null, // TODO: Calcular según tipo
        
        // Items del pedido (backend espera esto en payload o endpoint separado)
        detalles: pedidoData.items.map(item => {
          const itemSubtotal = item.precioUnitario * item.cantidad;
          const itemImpuesto = pedidoData.incluirIGV ? (itemSubtotal * 0.18) : 0;
          const itemTotal = itemSubtotal + itemImpuesto;
          
          return {
            producto: { id: item.productoId }, // ✅ Objeto con ID
            nombreProducto: item.nombreProducto,
            skuProducto: item.skuProducto,
            cantidad: item.cantidad,
            precioUnitario: item.precioUnitario,
            montoDescuento: 0,
            tasaImpuesto: pedidoData.incluirIGV ? 18.00 : 0,
            impuesto: itemImpuesto,
            subtotal: itemSubtotal,
            total: itemTotal,
            notas: null,
          };
        }),
      };
      
      console.log('📦 Creando pedido manual:', payload);
      
      // Crear pedido en el backend
      const response = await createPedido(payload);
      return response;
    },
    onSuccess: (data) => {
      // ✅ Invalidación agresiva y forzada
      queryClient.invalidateQueries({ 
        queryKey: ['pedidos'],
        exact: true,
        refetchType: 'all' 
      });
      
      // Forzar recarga inmediata desde el servidor
      queryClient.refetchQueries({ queryKey: ['pedidos'] });
      
      message.success(`✅ Pedido ${data.numeroPedido || 'creado'} registrado correctamente`);
      return data;
    },
    onError: (error) => {
      console.error('❌ Error al crear pedido:', error);
      const errorMessage = error.message || 'Error al crear el pedido';
      message.error(errorMessage);
      throw error;
    },
  });
}
