import { Users } from 'lucide-react';

export const Usuarios = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Usuarios y Clientes</h1>
        <p className="text-gray-600 mt-1">
          Gestión de usuarios del negocio, roles, permisos y base de clientes
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Users size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Usuarios y Clientes
        </h2>
        <p className="text-gray-500 max-w-sm">
          Registro de usuarios del negocio, asignación de roles y sedes, gestión de
          clientes y comunicaciones. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
