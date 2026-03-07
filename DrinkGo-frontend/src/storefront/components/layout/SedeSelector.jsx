import { MapPin } from 'lucide-react';

export const SedeSelector = ({ sedes, selectedSede, onSedeChange }) => {
  if (!sedes || sedes.length <= 1) return null;

  return (
    <div className="bg-amber-50 border border-amber-200 rounded-xl p-6">
      <div className="flex items-center gap-2 mb-4">
        <MapPin size={20} className="text-amber-600" />
        <h3 className="font-semibold text-gray-900">Selecciona una sede</h3>
      </div>
      <p className="text-sm text-gray-600 mb-4">
        Elige la sucursal desde donde deseas realizar tu pedido
      </p>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        {sedes.map((sede) => (
          <button
            key={sede.id}
            onClick={() => onSedeChange(sede)}
            className={`text-left p-4 rounded-lg border-2 transition-all ${
              selectedSede?.id === sede.id
                ? 'border-amber-500 bg-amber-50 ring-2 ring-amber-200'
                : 'border-gray-200 bg-white hover:border-amber-300 hover:shadow-sm'
            }`}
          >
            <div className="font-medium text-gray-900">{sede.nombre}</div>
            {sede.direccion && (
              <div className="text-xs text-gray-500 mt-1">{sede.direccion}</div>
            )}
            <div className="flex gap-3 mt-2">
              {sede.deliveryHabilitado && (
                <span className="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full">
                  Delivery
                </span>
              )}
              {sede.recojoHabilitado && (
                <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
                  Recojo en tienda
                </span>
              )}
            </div>
          </button>
        ))}
      </div>
    </div>
  );
};
