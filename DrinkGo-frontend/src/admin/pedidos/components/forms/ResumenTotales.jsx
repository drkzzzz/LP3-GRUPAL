/**
 * Resumen de totales del pedido
 * Muestra subtotal, delivery, IGV y total
 */
import { Calculator } from 'lucide-react';

export function ResumenTotales({ 
  subtotal = 0, 
  costoDelivery = 0,
  montoMinimo = 0,
  tipoPedido = 'delivery',
  incluirIGV = true
}) {
  const IGV_RATE = 0.18; // 18%
  
  // Calcular IGV sobre subtotal (no sobre delivery) solo si está habilitado
  const igv = incluirIGV ? subtotal * IGV_RATE : 0;
  
  // Total = subtotal + IGV + delivery
  const total = subtotal + igv + (tipoPedido === 'delivery' ? costoDelivery : 0);
  
  // Verificar si cumple monto mínimo
  const cumpleMontoMinimo = tipoPedido !== 'delivery' || montoMinimo === 0 || total >= montoMinimo;
  
  return (
    <div className="bg-gray-50 rounded-lg p-6 space-y-4">
      <div className="flex items-center gap-2 text-gray-700 font-medium mb-4">
        <Calculator className="w-5 h-5" />
        <h3>Resumen del Pedido</h3>
      </div>
      
      {/* Subtotal */}
      <div className="flex items-center justify-between text-gray-700">
        <span>Subtotal (productos)</span>
        <span className="font-medium">S/ {subtotal.toFixed(2)}</span>
      </div>
      
      {/* Delivery (solo si es delivery) */}
      {tipoPedido === 'delivery' && (
        <div className="flex items-center justify-between text-gray-700">
          <span>Costo de delivery</span>
          <span className="font-medium">
            {costoDelivery > 0 ? `S/ ${costoDelivery.toFixed(2)}` : 'S/ 0.00'}
          </span>
        </div>
      )}
      
      {/* IGV (solo si está habilitado) */}
      {incluirIGV && (
        <div className="flex items-center justify-between text-gray-700">
          <span>IGV (18%)</span>
          <span className="font-medium">S/ {igv.toFixed(2)}</span>
        </div>
      )}
      
      {/* Línea divisoria */}
      <div className="border-t border-gray-300 pt-4">
        {/* Total */}
        <div className="flex items-center justify-between">
          <span className="text-lg font-bold text-gray-900">TOTAL</span>
          <span className="text-2xl font-bold text-gray-900">
            S/ {total.toFixed(2)}
          </span>
        </div>
      </div>
      
      {/* Verificación de monto mínimo */}
      {tipoPedido === 'delivery' && montoMinimo > 0 && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          {cumpleMontoMinimo ? (
            <div className="flex items-center gap-2 text-sm text-green-700 bg-green-50 px-3 py-2 rounded-lg">
              <span className="text-green-600">✓</span>
              <span>Cumple el monto mínimo de S/ {montoMinimo.toFixed(2)}</span>
            </div>
          ) : (
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-red-700 bg-red-50 px-3 py-2 rounded-lg">
                <span className="text-red-600">✗</span>
                <span>Monto mínimo requerido: S/ {montoMinimo.toFixed(2)}</span>
              </div>
              <p className="text-xs text-gray-600 px-3">
                Falta: <strong>S/ {(montoMinimo - total).toFixed(2)}</strong> para alcanzar el monto mínimo
              </p>
            </div>
          )}
        </div>
      )}
      
      {/* Desglose de impuestos */}
      <div className="text-xs text-gray-500 border-t border-gray-200 pt-3">
        {incluirIGV ? (
          <p>💡 El IGV (18%) se calcula sobre el subtotal de productos</p>
        ) : (
          <p>⚠️ Pedido sin IGV</p>
        )}
        {tipoPedido === 'delivery' && costoDelivery > 0 && (
          <p className="mt-1">🚚 El costo de delivery no incluye IGV adicional</p>
        )}
      </div>
    </div>
  );
}
