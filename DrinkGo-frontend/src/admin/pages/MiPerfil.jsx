import { UserCircle } from 'lucide-react';

export const MiPerfil = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Mi Perfil</h1>
        <p className="text-gray-600 mt-1">Información personal y configuración de cuenta</p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <UserCircle size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">Mi Perfil</h2>
        <p className="text-gray-500 max-w-sm">
          Gestión de datos personales, cambio de contraseña y preferencias de cuenta.
          Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
