import { BarChart3 } from 'lucide-react';

export const Reportes = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Reportes</h1>
        <p className="text-gray-600 mt-1">
          Informes de ventas, inventario, compras, clientes y rendimiento del negocio
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <BarChart3 size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Reportes
        </h2>
        <p className="text-gray-500 max-w-sm">
          Reportes detallados de ventas, compras, inventario, rentabilidad y
          comportamiento de clientes con exportación a Excel/PDF. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
