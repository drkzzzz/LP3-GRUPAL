import { Settings } from 'lucide-react';

export const Configuracion = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Configuración</h1>
        <p className="text-gray-600 mt-1">
          Configuración del negocio, sedes, storefront y parámetros del sistema
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Settings size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Configuración del Negocio
        </h2>
        <p className="text-gray-500 max-w-sm">
          Datos del negocio, sedes, almacenes, métodos de pago, horarios, zonas de delivery
          y configuración de la tienda online. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
