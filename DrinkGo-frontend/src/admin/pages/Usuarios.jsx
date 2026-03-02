/**
 * Usuarios.jsx
 * ────────────
 * Módulo 3: Seguridad, Usuarios y Clientes
 *
 * Tabs:
 *  - Usuarios del negocio (CRUD)
 *  - Clientes (CRUD + búsqueda DNI/RUC + validación mayoría de edad)
 *  - Roles personalizados (CRUD)
 */
import { useState } from 'react';
import { Users, UserCheck, Shield } from 'lucide-react';
import { UsuariosTab } from '@/admin/usuarios/components/tabs/UsuariosTab';
import { ClientesTab } from '@/admin/usuarios/components/tabs/ClientesTab';
import { RolesTab } from '@/admin/usuarios/components/tabs/RolesTab';

const TABS = [
  { key: 'usuarios', label: 'Usuarios', icon: Users },
  { key: 'clientes', label: 'Clientes', icon: UserCheck },
  { key: 'roles', label: 'Roles', icon: Shield },
];

export const Usuarios = () => {
  const [activeTab, setActiveTab] = useState('usuarios');

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Usuarios y Clientes</h1>
        <p className="text-gray-600 mt-1">
          Gestión de usuarios del negocio, base de clientes y roles personalizados
        </p>
      </div>

      {/* Tabs de navegación */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex gap-1">
          {TABS.map(({ key, label, icon: Icon }) => (
            <button
              key={key}
              onClick={() => setActiveTab(key)}
              className={[
                'flex items-center gap-2 px-4 py-3 text-sm font-medium border-b-2 transition-colors',
                activeTab === key
                  ? 'border-green-600 text-green-700'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
              ].join(' ')}
            >
              <Icon size={16} />
              {label}
            </button>
          ))}
        </nav>
      </div>

      {/* Contenido del tab activo */}
      {activeTab === 'usuarios' && <UsuariosTab />}
      {activeTab === 'clientes' && <ClientesTab />}
      {activeTab === 'roles' && <RolesTab />}
    </div>
  );
};
