/**
 * MultiDistritoInput.jsx
 * ─────────────────────
 * Input con chips/tags para agregar múltiples distritos a una zona
 * Permite: agregar, eliminar, sugerencias
 */
import { useState, useRef } from 'react';
import { X, Plus } from 'lucide-react';

// Lista de distritos de Lima (43 distritos)
const DISTRITOS_LIMA = [
  'Surco', 'Miraflores', 'San Isidro', 'Barranco', 'Chorrillos',
  'San Borja', 'La Molina', 'San Juan de Miraflores', 'Villa María del Triunfo',
  'Los Olivos', 'San Martín de Porres', 'Independencia', 'Comas',
  'Callao', 'Ventanilla', 'Bellavista', 'La Perla', 'Carmen de la Legua',
  'Ate', 'Santa Anita', 'El Agustino', 'San Luis', 'La Victoria',
  'Rímac', 'Cercado de Lima', 'Breña', 'Jesús María', 'Lince',
  'Magdalena', 'Pueblo Libre', 'San Miguel',
  'Villa El Salvador', 'Lurín', 'Pachacámac', 'Punta Hermosa', 'Pucusana',
  'San Juan de Lurigancho', 'Lurigancho-Chosica', 'Chaclacayo', 'Ate Vitarte',
  'Puente Piedra', 'Ancón', 'Santa Rosa', 'Carabayllo'
].sort();

export const MultiDistritoInput = ({ value = [], onChange, error, placeholder = "Escribe un distrito y presiona Enter" }) => {
  const [input, setInput] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const inputRef = useRef(null);

  // Filtrar sugerencias basadas en input
  const suggestions = input.trim()
    ? DISTRITOS_LIMA.filter(
        (d) =>
          d.toLowerCase().includes(input.toLowerCase()) &&
          !value.includes(d)
      )
    : [];

  const handleAddDistrito = (distrito) => {
    const trimmed = distrito.trim();
    if (trimmed && !value.includes(trimmed)) {
      onChange([...value, trimmed]);
      setInput('');
      setShowSuggestions(false);
      inputRef.current?.focus();
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (suggestions.length > 0) {
        handleAddDistrito(suggestions[0]);
      } else if (input.trim()) {
        handleAddDistrito(input);
      }
    } else if (e.key === 'Backspace' && !input && value.length > 0) {
      // Eliminar último distrito si backspace en campo vacío
      onChange(value.slice(0, -1));
    }
  };

  const handleRemoveDistrito = (distritoToRemove) => {
    onChange(value.filter((d) => d !== distritoToRemove));
  };

  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-gray-700">
        Distritos cubiertos <span className="text-red-500">*</span>
      </label>

      {/* Chips de distritos seleccionados */}
      {value.length > 0 && (
        <div className="flex flex-wrap gap-2 p-3 bg-gray-50 rounded-lg border border-gray-200">
          {value.map((distrito) => (
            <div
              key={distrito}
              className="inline-flex items-center gap-1.5 bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-medium"
            >
              <span>{distrito}</span>
              <button
                type="button"
                onClick={() => handleRemoveDistrito(distrito)}
                className="hover:bg-green-200 rounded-full p-0.5 transition-colors"
                title={`Eliminar ${distrito}`}
              >
                <X size={14} />
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Input para agregar distrito */}
      <div className="relative">
        <div className="flex gap-2">
          <input
            ref={inputRef}
            type="text"
            value={input}
            onChange={(e) => {
              setInput(e.target.value);
              setShowSuggestions(true);
            }}
            onKeyDown={handleKeyDown}
            onFocus={() => setShowSuggestions(true)}
            onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
            placeholder={placeholder}
            className={`flex-1 px-3 py-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500 transition-colors ${
              error ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {input.trim() && (
            <button
              type="button"
              onClick={() => handleAddDistrito(input)}
              className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center gap-1 text-sm font-medium"
            >
              <Plus size={16} /> Agregar
            </button>
          )}
        </div>

        {/* Sugerencias desplegables */}
        {showSuggestions && suggestions.length > 0 && (
          <div className="absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-48 overflow-y-auto">
            {suggestions.map((distrito) => (
              <button
                key={distrito}
                type="button"
                onClick={() => handleAddDistrito(distrito)}
                className="w-full px-4 py-2 text-left text-sm hover:bg-green-50 transition-colors flex items-center gap-2"
              >
                <Plus size={14} className="text-green-600" />
                <span>{distrito}</span>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Mensaje de ayuda */}
      <p className="text-xs text-gray-500">
        {value.length === 0
          ? 'Agrega los distritos que cubre esta zona. Presiona Enter o haz clic en Agregar.'
          : `${value.length} distrito${value.length === 1 ? '' : 's'} seleccionado${value.length === 1 ? '' : 's'}`}
      </p>

      {/* Error */}
      {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  );
};
