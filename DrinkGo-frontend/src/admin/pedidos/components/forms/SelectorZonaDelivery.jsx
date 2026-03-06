/**
 * Selector de zona de delivery con auto-cálculo de costo
 * Busca la zona por distrito y muestra el costo automáticamente
 */
import { useEffect, useState } from 'react';
import { useCalcularCostoDelivery } from '@/admin/hooks/useZonasDelivery';
import { MapPin, Truck, AlertCircle, CheckCircle, Loader2 } from 'lucide-react';

// Distritos de Lima (mismo array que en MultiDistritoInput)
const DISTRITOS_LIMA = [
  'Surco', 'Miraflores', 'San Isidro', 'Barranco', 'Chorrillos', 'San Borja',
  'La Molina', 'San Miguel', 'Jesús María', 'Lince', 'Magdalena', 'Pueblo Libre',
  'Breña', 'Lima Cercado', 'Rímac', 'San Juan de Miraflores', 'Villa María del Triunfo',
  'Villa El Salvador', 'Lurín', 'Pachacámac', 'San Juan de Lurigancho', 'El Agustino',
  'Santa Anita', 'Ate', 'La Victoria', 'San Luis', 'Comas', 'Carabayllo', 'Puente Piedra',
  'Santa Rosa', 'Ancón', 'Los Olivos', 'Independencia', 'San Martín de Porres',
  'Callao', 'Bellavista', 'La Perla', 'La Punta', 'Carmen de la Legua', 'Ventanilla',
  'Surquillo', 'Miraflores', 'Santiago de Surco'
].sort();

export function SelectorZonaDelivery({ 
  distrito, 
  onDistritoChange, 
  direccion, 
  onDireccionChange,
  referencia,
  onReferenciaChange,
  onCostoDeliveryCalculado 
}) {
  const [distritoSeleccionado, setDistritoSeleccionado] = useState(distrito || '');
  const [mostrarSugerencias, setMostrarSugerencias] = useState(false);
  
  // Calcular costo de delivery automáticamente
  const { data: calculoDelivery, isLoading: calculandoCosto } = 
    useCalcularCostoDelivery(distritoSeleccionado, distritoSeleccionado.trim().length > 0);
  
  // Filtrar distritos según búsqueda
  const distritosFiltrados = DISTRITOS_LIMA.filter(d =>
    d.toLowerCase().includes(distritoSeleccionado.toLowerCase())
  );
  
  // Actualizar costo cuando cambie el cálculo
  useEffect(() => {
    if (calculoDelivery) {
      onCostoDeliveryCalculado({
        costo: calculoDelivery.costo || 0,
        zona: calculoDelivery.zona,
        montoMinimo: calculoDelivery.montoMinimo || 0,
        mensaje: calculoDelivery.mensaje,
      });
    }
  }, [calculoDelivery, onCostoDeliveryCalculado]);
  
  const handleSeleccionarDistrito = (distrito) => {
    setDistritoSeleccionado(distrito);
    onDistritoChange(distrito);
    setMostrarSugerencias(false);
  };
  
  const handleInputDistrito = (e) => {
    const valor = e.target.value;
    setDistritoSeleccionado(valor);
    onDistritoChange(valor);
    setMostrarSugerencias(true);
  };
  
  return (
    <div className="space-y-4">
      {/* Selector de distrito */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Distrito *
        </label>
        <div className="relative">
          <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="Ej: Surco, Miraflores, San Isidro..."
            value={distritoSeleccionado}
            onChange={handleInputDistrito}
            onFocus={() => setMostrarSugerencias(true)}
            className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            required
          />
          
          {/* Sugerencias de distritos */}
          {mostrarSugerencias && distritosFiltrados.length > 0 && distritoSeleccionado.trim().length > 0 && (
            <div className="absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
              {distritosFiltrados.slice(0, 10).map((distrito) => (
                <button
                  key={distrito}
                  type="button"
                  onClick={() => handleSeleccionarDistrito(distrito)}
                  className="w-full text-left px-4 py-2 hover:bg-gray-50 border-b border-gray-100 last:border-b-0"
                >
                  <p className="text-gray-900">{distrito}</p>
                </button>
              ))}
            </div>
          )}
        </div>
      </div>
      
      {/* Resultado del cálculo de delivery */}
      {distritoSeleccionado.trim().length > 0 && (
        <div>
          {calculandoCosto ? (
            <div className="flex items-center gap-2 text-sm text-gray-600">
              <Loader2 className="w-4 h-4 animate-spin" />
              Verificando cobertura...
            </div>
          ) : calculoDelivery?.zona ? (
            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <div className="flex items-start gap-3">
                <div className="bg-green-100 p-2 rounded-full">
                  <CheckCircle className="w-5 h-5 text-green-600" />
                </div>
                <div className="flex-1">
                  <p className="font-medium text-green-900">
                    ✅ Zona: {calculoDelivery.zona.nombre}
                  </p>
                  <div className="mt-2 space-y-1 text-sm text-green-800">
                    <div className="flex items-center gap-2">
                      <Truck className="w-4 h-4" />
                      <span>Costo de delivery: <strong>S/ {calculoDelivery.costo.toFixed(2)}</strong></span>
                    </div>
                    <div className="flex items-center gap-2">
                      <AlertCircle className="w-4 h-4" />
                      <span>Monto mínimo de pedido: <strong>S/ {calculoDelivery.montoMinimo.toFixed(2)}</strong></span>
                    </div>
                  </div>
                  {calculoDelivery.zona.descripcion && (
                    <p className="text-xs text-green-700 mt-2">
                      {calculoDelivery.zona.descripcion}
                    </p>
                  )}
                </div>
              </div>
            </div>
          ) : (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
              <div className="flex items-start gap-3">
                <div className="bg-red-100 p-2 rounded-full">
                  <AlertCircle className="w-5 h-5 text-red-600" />
                </div>
                <div>
                  <p className="font-medium text-red-900">
                    Sin cobertura de delivery
                  </p>
                  <p className="text-sm text-red-700 mt-1">
                    {calculoDelivery?.mensaje || `El distrito "${distritoSeleccionado}" no tiene cobertura de delivery configurada.`}
                  </p>
                  <p className="text-xs text-red-600 mt-2">
                    💡 Puedes configurar zonas de delivery en: Configuración → Operaciones → Zonas Delivery
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
      
      {/* Dirección completa */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Dirección Completa *
        </label>
        <input
          type="text"
          placeholder="Ej: Av. Los Precursores 123, Dpto. 401"
          value={direccion}
          onChange={(e) => onDireccionChange(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          required
        />
      </div>
      
      {/* Referencia */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Referencia (Opcional)
        </label>
        <textarea
          placeholder="Ej: Casa blanca con reja verde, timbre rojo, cerca al parque..."
          value={referencia}
          onChange={(e) => onReferenciaChange(e.target.value)}
          rows={2}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
        />
        <p className="text-xs text-gray-500 mt-1">
          Agrega referencias para facilitar la entrega
        </p>
      </div>
    </div>
  );
}
