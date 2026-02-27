import { useState } from 'react';
import { Building2, MapPin, CreditCard, KeyRound } from 'lucide-react';
import { clsx } from 'clsx';
import { Modal } from '../ui/Modal';
import { NegocioInfo } from './NegocioInfo';
import { SedesPanel } from './SedesPanel';
import { PlanSelector } from './PlanSelector';
import { CredencialesPanel } from './CredencialesPanel';

const TABS = [
  { key: 'info', label: 'Información', icon: Building2 },
  { key: 'sedes', label: 'Sedes', icon: MapPin },
  { key: 'plan', label: 'Suscripción', icon: CreditCard },
  { key: 'credenciales', label: 'Credenciales', icon: KeyRound },
];

export const NegocioDetailModal = ({
  isOpen,
  onClose,
  negocio,
  sedes = [],
  suscripcion,
  planes = [],
  onCreateSede,
  onUpdateSede,
  onDeleteSede,
  onChangePlan,
  isCreatingSede,
  isUpdatingSede,
  isDeletingSede,
  isUpdatingSuscripcion,
  onUpdateNegocio,
  isUpdatingNegocio,
  usuarios = [],
  onCreateUsuario,
  onUpdateUsuario,
  isCreatingUsuario,
  isUpdatingUsuario,
}) => {
  const [activeTab, setActiveTab] = useState('info');

  const handleClose = () => {
    setActiveTab('info');
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={negocio?.razonSocial || 'Detalle del Negocio'}
      size="xl"
    >
      {negocio && (
        <>
          {/* Subtitle */}
          {negocio.nombreComercial && (
            <p className="text-sm text-gray-500 -mt-2 mb-4">
              {negocio.nombreComercial}
            </p>
          )}

          {/* Tab bar */}
          <div className="flex gap-1 border-b border-gray-200 -mt-2 mb-4">
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

          {/* Tab content */}
          <div className="overflow-y-auto" style={{ maxHeight: '60vh' }}>
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
                onUpdateNegocio={onUpdateNegocio}
                isUpdatingNegocio={isUpdatingNegocio}
              />
            )}

            {activeTab === 'credenciales' && (
              <CredencialesPanel
                negocio={negocio}
                onCreateUsuario={onCreateUsuario}
                onUpdateUsuario={onUpdateUsuario}
                isCreating={isCreatingUsuario}
                isUpdating={isUpdatingUsuario}
              />
            )}
          </div>
        </>
      )}
    </Modal>
  );
};
