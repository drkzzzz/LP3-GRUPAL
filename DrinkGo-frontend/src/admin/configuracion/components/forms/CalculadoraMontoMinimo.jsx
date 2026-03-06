/**
 * CalculadoraMontoMinimo.jsx
 * ──────────────────────────
 * Calculadora inteligente para sugerir monto mínimo de pedido
 * basado en costos operativos reales
 */
import { useState } from 'react';
import { Calculator, Info, TrendingUp, DollarSign } from 'lucide-react';

export const CalculadoraMontoMinimo = ({ tarifaDelivery = 0, onSugerirMonto }) => {
  const [mostrarCalculadora, setMostrarCalculadora] = useState(false);
  const [costos, setCostos] = useState({
    gasolina: 5.0,        // Costo promedio gasolina por viaje
    tiempoMotorista: 30,  // Minutos
    pagoHora: 15.0,       // S/ por hora del motorista
    margenGanancia: 30,   // % ganancia deseada
  });

  const calcularMontoSugerido = () => {
    // Costo total del delivery
    const costoGasolina = parseFloat(costos.gasolina) || 0;
    const costoMotorista = (parseFloat(costos.pagoHora) || 0) * (parseFloat(costos.tiempoMotorista) || 0) / 60;
    const costoTotalDelivery = costoGasolina + costoMotorista;

    // Si hay tarifa de delivery configurada, usarla como base
    const costoBase = tarifaDelivery > 0 ? tarifaDelivery : costoTotalDelivery;

    // Margen de ganancia
    const margen = parseFloat(costos.margenGanancia) || 30;
    
    // Fórmula: Monto Mínimo = (Costo Delivery × 3.5) × (1 + Margen/100)
    // Factor 3.5 = cubrir delivery + costo producto + ganancia + seguridad
    const montoSugerido = costoBase * 3.5 * (1 + margen / 100);

    return Math.ceil(montoSugerido); // Redondear hacia arriba
  };

  const handleAplicarSugerencia = () => {
    const monto = calcularMontoSugerido();
    onSugerirMonto(monto);
    setMostrarCalculadora(false);
  };

  const montoCalculado = calcularMontoSugerido();
  const costoMotorista = (parseFloat(costos.pagoHora) || 0) * (parseFloat(costos.tiempoMotorista) || 0) / 60;
  const costoTotal = (parseFloat(costos.gasolina) || 0) + costoMotorista;

  return (
    <div className="space-y-2">
      {/* Botón para abrir calculadora */}
      <button
        type="button"
        onClick={() => setMostrarCalculadora(!mostrarCalculadora)}
        className="flex items-center gap-2 text-sm text-blue-600 hover:text-blue-700 font-medium transition-colors"
      >
        <Calculator size={16} />
        {mostrarCalculadora ? 'Ocultar' : 'Calcular monto ideal'}
      </button>

      {/* Panel de calculadora */}
      {mostrarCalculadora && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 space-y-4">
          <div className="flex items-start gap-2">
            <Info size={18} className="text-blue-600 mt-0.5 flex-shrink-0" />
            <div className="text-sm text-blue-800">
              <p className="font-semibold mb-1">Calculadora de Monto Mínimo</p>
              <p className="text-xs">Ingresa tus costos reales para obtener una sugerencia inteligente</p>
            </div>
          </div>

          {/* Inputs de costos */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-medium text-gray-700 mb-1">
                Gasolina por viaje (S/)
              </label>
              <input
                type="number"
                step="0.5"
                value={costos.gasolina}
                onChange={(e) => setCostos({ ...costos, gasolina: e.target.value })}
                className="w-full px-3 py-1.5 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-700 mb-1">
                Tiempo delivery (min)
              </label>
              <input
                type="number"
                step="5"
                value={costos.tiempoMotorista}
                onChange={(e) => setCostos({ ...costos, tiempoMotorista: e.target.value })}
                className="w-full px-3 py-1.5 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-700 mb-1">
                Pago motorista (S/hora)
              </label>
              <input
                type="number"
                step="1"
                value={costos.pagoHora}
                onChange={(e) => setCostos({ ...costos, pagoHora: e.target.value })}
                className="w-full px-3 py-1.5 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-700 mb-1">
                Margen ganancia (%)
              </label>
              <input
                type="number"
                step="5"
                value={costos.margenGanancia}
                onChange={(e) => setCostos({ ...costos, margenGanancia: e.target.value })}
                className="w-full px-3 py-1.5 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Desglose de cálculo */}
          <div className="bg-white rounded-lg p-3 space-y-2 border border-blue-100">
            <p className="text-xs font-semibold text-gray-700 flex items-center gap-1">
              <TrendingUp size={14} className="text-blue-600" />
              Desglose del cálculo:
            </p>
            <div className="space-y-1 text-xs text-gray-600">
              <div className="flex justify-between">
                <span>• Gasolina:</span>
                <span className="font-medium">S/ {parseFloat(costos.gasolina || 0).toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>• Motorista ({costos.tiempoMotorista} min):</span>
                <span className="font-medium">S/ {costoMotorista.toFixed(2)}</span>
              </div>
              <div className="flex justify-between border-t pt-1">
                <span className="font-semibold">Costo total delivery:</span>
                <span className="font-semibold text-red-600">S/ {costoTotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>• Tarifa cobrada al cliente:</span>
                <span className="font-medium text-green-600">S/ {tarifaDelivery.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>• Factor cobertura (× 3.5):</span>
                <span className="text-gray-500">Cubre costos + ganancia</span>
              </div>
              <div className="flex justify-between">
                <span>• Margen adicional:</span>
                <span className="font-medium">{costos.margenGanancia}%</span>
              </div>
            </div>
          </div>

          {/* Resultado y acciones */}
          <div className="flex items-center justify-between bg-gradient-to-r from-green-50 to-emerald-50 border border-green-200 rounded-lg p-3">
            <div>
              <p className="text-xs text-gray-600 mb-0.5">Monto mínimo sugerido:</p>
              <p className="text-2xl font-bold text-green-700 flex items-center gap-1">
                <DollarSign size={20} />
                {montoCalculado.toFixed(2)}
              </p>
            </div>
            <button
              type="button"
              onClick={handleAplicarSugerencia}
              className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
            >
              Usar este monto
            </button>
          </div>

          {/* Explicación */}
          <div className="text-xs text-gray-500 bg-gray-50 rounded p-2">
            <p className="font-semibold mb-1">💡 ¿Por qué este monto?</p>
            <p>
              Con pedidos de <strong>S/ {montoCalculado.toFixed(2)}</strong> o más, cubres 
              el costo del delivery (S/ {costoTotal.toFixed(2)}), el costo del producto (~30-40% del monto), 
              y generas una ganancia del {costos.margenGanancia}%. 
              Pedidos menores podrían generar pérdidas.
            </p>
          </div>
        </div>
      )}
    </div>
  );
};
