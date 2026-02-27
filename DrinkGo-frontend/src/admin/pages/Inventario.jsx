import { Warehouse } from 'lucide-react';

export const Inventario = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Inventario</h1>
        <p className="text-gray-600 mt-1">
          Control de stock, lotes FIFO, movimientos, ajustes y alertas de inventario
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Warehouse size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Gestión de Inventario
        </h2>
        <p className="text-gray-500 max-w-sm">
          Stock consolidado, lotes con vencimiento, kardex de movimientos, ajustes manuales,
          transferencias entre almacenes e inventario físico. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
