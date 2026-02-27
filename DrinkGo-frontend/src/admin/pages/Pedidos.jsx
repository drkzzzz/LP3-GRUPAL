import { ShoppingCart } from 'lucide-react';

export const Pedidos = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Pedidos</h1>
        <p className="text-gray-600 mt-1">
          Gestión de pedidos online, delivery, recojo en tienda y seguimiento
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <ShoppingCart size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Pedidos
        </h2>
        <p className="text-gray-500 max-w-sm">
          Gestión de pedidos recibidos por la tienda online, control de estados,
          seguimiento de delivery y recojo en local. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
