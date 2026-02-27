import { useState } from 'react';
import {
  Crown,
  ArrowRight,
  Check,
  MapPin,
  Users,
  Package,
  ShieldCheck,
  AlertCircle,
} from 'lucide-react';
import { clsx } from 'clsx';
import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { formatCurrency } from '@/shared/utils/formatters';

const PLAN_COLORS = [
  'from-gray-50 to-gray-100 border-gray-200',
  'from-blue-50 to-indigo-50 border-blue-200',
  'from-purple-50 to-pink-50 border-purple-200',
  'from-amber-50 to-orange-50 border-amber-200',
];

export const ActivarConPlanModal = ({
  isOpen,
  onClose,
  negocio,
  planes,
  onConfirm,
  isLoading,
}) => {
  const [selectedPlanId, setSelectedPlanId] = useState(null);

  const activePlanes = (planes || []).filter((p) => p.estaActivo !== false);

  const handleConfirm = () => {
    if (!selectedPlanId) return;
    onConfirm(selectedPlanId);
  };

  const handleClose = () => {
    setSelectedPlanId(null);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Activar Negocio" size="lg">
      <div className="space-y-5">
        {/* Warning banner */}
        <div className="flex items-start gap-3 bg-amber-50 border border-amber-200 rounded-lg p-4">
          <AlertCircle size={20} className="text-amber-500 flex-shrink-0 mt-0.5" />
          <div>
            <p className="text-sm font-semibold text-amber-800">
              Se requiere un plan de suscripción
            </p>
            <p className="text-xs text-amber-600 mt-1">
              Para activar <span className="font-medium">"{negocio?.razonSocial}"</span> es
              necesario asignar un plan. Selecciona uno de los planes disponibles.
            </p>
          </div>
        </div>

        {/* Plan grid */}
        <div className="space-y-2">
          <h4 className="text-sm font-semibold text-gray-700">
            Seleccionar Plan
          </h4>

          {activePlanes.length === 0 ? (
            <p className="text-sm text-gray-500 italic py-4 text-center">
              No hay planes disponibles. Cree un plan de suscripción primero.
            </p>
          ) : (
            <div className="grid grid-cols-1 gap-3 max-h-80 overflow-y-auto pr-1">
              {activePlanes.map((plan, index) => {
                const isSelected = selectedPlanId === plan.id;
                return (
                  <button
                    key={plan.id}
                    type="button"
                    onClick={() => setSelectedPlanId(plan.id)}
                    className={clsx(
                      'text-left rounded-xl p-4 border-2 transition-all',
                      isSelected
                        ? 'bg-blue-50 border-blue-500 ring-2 ring-blue-200 shadow-sm'
                        : 'bg-gradient-to-r hover:shadow-md cursor-pointer border-gray-200 hover:border-blue-300',
                      !isSelected && PLAN_COLORS[index % PLAN_COLORS.length],
                    )}
                  >
                    <div className="flex items-start justify-between">
                      <div>
                        <div className="flex items-center gap-2">
                          <Crown
                            size={16}
                            className={isSelected ? 'text-blue-600' : 'text-gray-400'}
                          />
                          <h5 className="text-sm font-bold text-gray-900">
                            {plan.nombre}
                          </h5>
                          {isSelected && <Badge variant="info">Seleccionado</Badge>}
                        </div>
                        <p className="text-lg font-bold text-gray-900 mt-1">
                          {formatCurrency(plan.precio)}
                          <span className="text-xs font-normal text-gray-500 ml-1">
                            / {plan.periodoFacturacion || 'mes'}
                          </span>
                        </p>
                        {plan.descripcion && (
                          <p className="text-xs text-gray-500 mt-1 line-clamp-2">
                            {plan.descripcion}
                          </p>
                        )}
                      </div>
                      {isSelected ? (
                        <div className="w-6 h-6 rounded-full bg-blue-500 flex items-center justify-center flex-shrink-0 mt-1">
                          <Check size={14} className="text-white" />
                        </div>
                      ) : (
                        <ArrowRight
                          size={18}
                          className="text-gray-400 mt-1 flex-shrink-0"
                        />
                      )}
                    </div>

                    {/* Plan limits */}
                    <div className="flex flex-wrap gap-3 mt-3">
                      {plan.maxSedes != null && (
                        <span className="inline-flex items-center gap-1 text-xs text-gray-600">
                          <MapPin size={11} />
                          {plan.maxSedes} sedes
                        </span>
                      )}
                      {plan.maxUsuarios != null && (
                        <span className="inline-flex items-center gap-1 text-xs text-gray-600">
                          <Users size={11} />
                          {plan.maxUsuarios} usuarios
                        </span>
                      )}
                      {plan.maxProductos != null && (
                        <span className="inline-flex items-center gap-1 text-xs text-gray-600">
                          <Package size={11} />
                          {plan.maxProductos} productos
                        </span>
                      )}
                      {plan.permitePos && (
                        <span className="inline-flex items-center gap-1 text-xs text-green-600">
                          <ShieldCheck size={11} />
                          POS
                        </span>
                      )}
                    </div>
                  </button>
                );
              })}
            </div>
          )}
        </div>

        {/* Actions */}
        <div className="flex justify-end gap-2 pt-3 border-t border-gray-200">
          <Button variant="outline" onClick={handleClose}>
            Cancelar
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={!selectedPlanId || isLoading}
          >
            {isLoading ? 'Activando...' : 'Asignar Plan y Activar'}
          </Button>
        </div>
      </div>
    </Modal>
  );
};
