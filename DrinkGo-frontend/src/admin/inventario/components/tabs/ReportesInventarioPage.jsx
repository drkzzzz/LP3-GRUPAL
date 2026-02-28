/**
 * ReportesInventarioPage.jsx
 * ──────────────────────────
 * Sub-módulo unificado que agrupa Stock Consolidado, Movimientos y Alertas
 * en pestañas internas. Mismo patrón que ClasificacionesPage del Catálogo.
 */
import { useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Package, History, AlertTriangle } from 'lucide-react';
import { StockTab } from './StockTab';
import { MovimientosTab } from './MovimientosTab';
import { AlertasTab } from './AlertasTab';

const TABS = [
  { key: 'stock', label: 'Stock Consolidado', icon: Package },
  { key: 'movimientos', label: 'Movimientos', icon: History },
  { key: 'alertas', label: 'Alertas', icon: AlertTriangle },
];

export const ReportesInventarioPage = () => {
  const outletContext = useOutletContext();
  const [activeTab, setActiveTab] = useState('stock');

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Reportes de Inventario</h1>
        <p className="text-gray-600 mt-1">
          Visualización de stock consolidado, historial de movimientos y alertas del inventario
        </p>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex gap-6" aria-label="Pestañas de reportes de inventario">
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
        {activeTab === 'stock' && <StockTab />}
        {activeTab === 'movimientos' && <MovimientosTab />}
        {activeTab === 'alertas' && <AlertasTab />}
      </div>
    </div>
  );
};
