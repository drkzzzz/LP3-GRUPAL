import { CreditCard } from 'lucide-react';

export const Ventas = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Ventas / POS</h1>
        <p className="text-gray-600 mt-1">
          Punto de venta, cajas registradoras, sesiones de caja y pagos
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <CreditCard size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Ventas / POS
        </h2>
        <p className="text-gray-500 max-w-sm">
          Sistema de punto de venta, gestión de cajas registradoras, sesiones y turnos,
          historial de ventas y pagos mixtos. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
