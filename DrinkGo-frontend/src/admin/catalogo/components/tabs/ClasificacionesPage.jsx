/**
 * ClasificacionesPage.jsx
 * ───────────────────────
 * Sub-módulo unificado que agrupa Categorías, Marcas y Unidades de Medida
 * en pestañas internas. Por defecto muestra la pestaña Categorías.
 */
import { useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { FolderTree, Award, Ruler } from 'lucide-react';
import { CategoriasTab } from './CategoriasTab';
import { MarcasTab } from './MarcasTab';
import { UnidadesMedidaTab } from './UnidadesMedidaTab';

const TABS = [
  { key: 'categorias', label: 'Categorías', icon: FolderTree },
  { key: 'marcas', label: 'Marcas', icon: Award },
  { key: 'unidades', label: 'Unidades de Medida', icon: Ruler },
];

export const ClasificacionesPage = () => {
  const outletContext = useOutletContext();
  const [activeTab, setActiveTab] = useState('categorias');

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Clasificaciones</h1>
        <p className="text-gray-600 mt-1">
          Gestión de categorías, marcas y unidades de medida para organizar el catálogo de productos
        </p>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex gap-6" aria-label="Pestañas de clasificaciones">
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
        {activeTab === 'categorias' && <CategoriasTab context={outletContext} />}
        {activeTab === 'marcas' && <MarcasTab context={outletContext} />}
        {activeTab === 'unidades' && <UnidadesMedidaTab context={outletContext} />}
      </div>
    </div>
  );
};
