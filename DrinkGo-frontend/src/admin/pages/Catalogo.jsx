import { Package } from 'lucide-react';

export const Catalogo = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Catálogo de Productos</h1>
        <p className="text-gray-600 mt-1">
          Gestión de productos, categorías, marcas, combos, descuentos y promociones
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Package size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Catálogo de Productos
        </h2>
        <p className="text-gray-500 max-w-sm">
          Administración completa del catálogo: productos, categorías, marcas, unidades de
          medida, combos, descuentos y promociones. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
