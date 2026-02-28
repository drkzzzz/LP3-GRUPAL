/**
 * NegocioYSedesPage.jsx
 * ─────────────────────
 * Sub-módulo que agrupa Mi Negocio, Sedes, Almacenes y Tienda Online
 * en pestañas internas. Por defecto muestra la pestaña Mi Negocio.
 */
import { useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Building2, MapPin, Warehouse, Globe } from 'lucide-react';
import { NegocioTab } from './NegocioTab';
import { SedesTab } from './SedesTab';
import { AlmacenesConfigTab } from './AlmacenesConfigTab';
import { TiendaOnlineTab } from './TiendaOnlineTab';

const TABS = [
  { key: 'negocio', label: 'Mi Negocio', icon: Building2 },
  { key: 'sedes', label: 'Sedes', icon: MapPin },
  { key: 'almacenes', label: 'Almacenes', icon: Warehouse },
  { key: 'tienda-online', label: 'Tienda Online', icon: Globe },
];

export const NegocioYSedesPage = () => {
  const outletContext = useOutletContext();
  const [activeTab, setActiveTab] = useState('negocio');

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Negocio y Sedes</h1>
        <p className="text-gray-600 mt-1">
          Configuración general del negocio, sedes, almacenes y tienda online
        </p>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex gap-6" aria-label="Pestañas de negocio y sedes">
          {TABS.map((tab) => {
            const isActive = activeTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`flex items-center gap-2 py-3 px-1 border-b-2 text-sm font-medium transition-colors ${
                  isActive
                    ? 'border-green-600 text-green-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <tab.icon size={16} />
                {tab.label}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Tab Content */}
      <div>
        {activeTab === 'negocio' && <NegocioTab context={outletContext} />}
        {activeTab === 'sedes' && <SedesTab context={outletContext} />}
        {activeTab === 'almacenes' && <AlmacenesConfigTab context={outletContext} />}
        {activeTab === 'tienda-online' && <TiendaOnlineTab context={outletContext} />}
      </div>
    </div>
  );
};
