import { useState } from 'react';
import { X, Building2, MapPin, CreditCard } from 'lucide-react';
import { clsx } from 'clsx';
import { NegocioInfo } from './NegocioInfo';
import { SedesPanel } from './SedesPanel';
import { PlanSelector } from './PlanSelector';

const TABS = [
  { key: 'info', label: 'Información', icon: Building2 },
  { key: 'sedes', label: 'Sedes', icon: MapPin },
  { key: 'plan', label: 'Suscripción', icon: CreditCard },
];

export const NegocioDrawer = ({
  isOpen,
  onClose,
  negocio,
  sedes,
  suscripcion,
  planes,
  onCreateSede,
  onUpdateSede,
  onDeleteSede,
  onChangePlan,
  isCreatingSede,
  isUpdatingSede,
  isDeletingSede,
  isUpdatingSuscripcion,
}) => {
  const [activeTab, setActiveTab] = useState('info');

  if (!isOpen || !negocio) return null;

  return (
    <div className="fixed inset-0 z-50 flex justify-end">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/50 transition-opacity"
        onClick={onClose}
      />

      {/* Drawer panel */}
      <div className="relative bg-white shadow-xl w-full max-w-3xl h-full flex flex-col animate-slide-in-right">
        {/* Header */}
        <div className="flex-shrink-0 border-b border-gray-200">
          <div className="flex items-center justify-between px-6 py-4">
            <div className="min-w-0">
              <h2 className="text-lg font-semibold text-gray-900 truncate">
                {negocio.razonSocial}
              </h2>
              {negocio.nombreComercial && (
                <p className="text-sm text-gray-500 truncate">
                  {negocio.nombreComercial}
                </p>
              )}
            </div>
            <button
              onClick={onClose}
              className="ml-4 text-gray-400 hover:text-gray-600 transition-colors flex-shrink-0"
            >
              <X size={20} />
            </button>
          </div>

          {/* Tabs */}
          <div className="flex px-6 gap-1">
            {TABS.map((tab) => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.key;
              return (
                <button
                  key={tab.key}
                  onClick={() => setActiveTab(tab.key)}
                  className={clsx(
                    'flex items-center gap-2 px-4 py-2.5 text-sm font-medium rounded-t-lg transition-colors',
                    isActive
                      ? 'bg-blue-50 text-blue-700 border-b-2 border-blue-600'
                      : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50',
                  )}
                >
                  <Icon size={16} />
                  {tab.label}
                  {tab.key === 'sedes' && (
                    <span
                      className={clsx(
                        'ml-1 text-xs px-1.5 py-0.5 rounded-full',
                        isActive
                          ? 'bg-blue-100 text-blue-700'
                          : 'bg-gray-100 text-gray-500',
                      )}
                    >
                      {sedes.length}
                    </span>
                  )}
                </button>
              );
            })}
          </div>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto px-6 py-5">
          {activeTab === 'info' && <NegocioInfo negocio={negocio} />}

          {activeTab === 'sedes' && (
            <SedesPanel
              negocioId={negocio.id}
              sedes={sedes}
              onCreateSede={onCreateSede}
              onUpdateSede={onUpdateSede}
              onDeleteSede={onDeleteSede}
              isCreating={isCreatingSede}
              isUpdating={isUpdatingSede}
              isDeleting={isDeletingSede}
            />
          )}

          {activeTab === 'plan' && (
            <PlanSelector
              negocio={negocio}
              suscripcion={suscripcion}
              planes={planes}
              onChangePlan={onChangePlan}
              isUpdating={isUpdatingSuscripcion}
            />
          )}
        </div>
      </div>
    </div>
  );
};
