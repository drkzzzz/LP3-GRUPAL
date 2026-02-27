import { Truck } from 'lucide-react';

export const Compras = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Proveedores y Compras</h1>
        <p className="text-gray-600 mt-1">
          Gestión de proveedores, órdenes de compra, recepción de mercancía y devoluciones
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Truck size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Proveedores y Compras
        </h2>
        <p className="text-gray-500 max-w-sm">
          Registro de proveedores, creación y aprobación de órdenes de compra, recepción de
          mercancía, devoluciones y cuentas por pagar. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
