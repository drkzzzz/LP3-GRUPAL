import { LayoutDashboard } from 'lucide-react';

export const Dashboard = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-1">Resumen general del negocio</p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
          <LayoutDashboard size={32} className="text-green-600" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Dashboard del Negocio
        </h2>
        <p className="text-gray-500 max-w-sm">
          Aquí se mostrarán las métricas y resumen de ventas, stock, pedidos y más.
          Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
