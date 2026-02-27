import { useState } from 'react';
import {
  CreditCard,
  Calendar,
  Check,
  ArrowRight,
  Crown,
  Users,
  MapPin,
  Package,
  ShieldCheck,
} from 'lucide-react';
import { clsx } from 'clsx';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { ConfirmDialog } from '../ui/ConfirmDialog';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';

const SUSCRIPCION_COLORS = {
  activa: 'success',
  vencida: 'error',
  suspendida: 'warning',
  cancelada: 'error',
  expirada: 'error',
};

const PLAN_COLORS = [
  'from-gray-50 to-gray-100 border-gray-200',
  'from-blue-50 to-indigo-50 border-blue-200',
  'from-purple-50 to-pink-50 border-purple-200',
  'from-amber-50 to-orange-50 border-amber-200',
];

export const PlanSelector = ({
  negocio,
  suscripcion,
  planes,
  onChangePlan,
  isUpdating,
}) => {
  const [selectedPlanId, setSelectedPlanId] = useState(null);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);

  const currentPlan = suscripcion?.plan || null;
  const activePlanes = planes.filter((p) => p.estaActivo !== false);

  const handlePlanSelect = (planId) => {
    if (planId === currentPlan?.id) return;
    setSelectedPlanId(planId);
    setIsConfirmOpen(true);
  };

  const handleConfirmChange = async () => {
    const selectedPlan = planes.find((p) => p.id === selectedPlanId);
    if (!selectedPlan) return;

    if (suscripcion) {
      // Update existing subscription
      await onChangePlan({
        id: suscripcion.id,
        negocio: { id: negocio.id },
        plan: { id: selectedPlanId },
        estado: suscripcion.estado,
        inicioPeriodoActual: suscripcion.inicioPeriodoActual,
        finPeriodoActual: suscripcion.finPeriodoActual,
        autoRenovar: suscripcion.autoRenovar ?? true,
      });
    } else {
      // Create new subscription
      const today = new Date().toISOString().split('T')[0];
      const nextMonth = new Date();
      nextMonth.setMonth(nextMonth.getMonth() + 1);

      await onChangePlan({
        negocio: { id: negocio.id },
        plan: { id: selectedPlanId },
        estado: 'activa',
        inicioPeriodoActual: today,
        finPeriodoActual: nextMonth.toISOString().split('T')[0],
        autoRenovar: true,
      });
    }

    setIsConfirmOpen(false);
    setSelectedPlanId(null);
  };

  const selectedPlanForConfirm = planes.find((p) => p.id === selectedPlanId);

  return (
    <div className="space-y-6">
      {/* Current subscription info */}
      {suscripcion ? (
        <div className="bg-gradient-to-r from-green-50 to-emerald-50 rounded-xl p-5 border border-green-200">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-xl bg-green-600 flex items-center justify-center shadow-sm">
                <Crown size={22} className="text-white" />
              </div>
              <div>
                <p className="text-xs font-medium text-green-600 uppercase tracking-wide">
                  Plan Actual
                </p>
                <h3 className="text-lg font-bold text-gray-900">
                  {currentPlan?.nombre || 'Sin plan'}
                </h3>
                <p className="text-sm text-gray-600">
                  {currentPlan?.precio != null
                    ? `${formatCurrency(currentPlan.precio)} / ${currentPlan.periodoFacturacion || 'mes'}`
                    : '—'}
                </p>
              </div>
            </div>
            <Badge
              variant={SUSCRIPCION_COLORS[suscripcion.estado] || 'info'}
            >
              {suscripcion.estado?.toUpperCase()}
            </Badge>
          </div>

          {/* Subscription details */}
          <div className="mt-4 grid grid-cols-3 gap-4">
            <div className="bg-white/60 rounded-lg p-3">
              <p className="text-xs text-gray-500 flex items-center gap-1">
                <Calendar size={12} />
                Inicio
              </p>
              <p className="text-sm font-medium text-gray-900 mt-0.5">
                {formatDate(suscripcion.inicioPeriodoActual)}
              </p>
            </div>
            <div className="bg-white/60 rounded-lg p-3">
              <p className="text-xs text-gray-500 flex items-center gap-1">
                <Calendar size={12} />
                Vencimiento
              </p>
              <p className="text-sm font-medium text-gray-900 mt-0.5">
                {formatDate(suscripcion.finPeriodoActual)}
              </p>
            </div>
            <div className="bg-white/60 rounded-lg p-3">
              <p className="text-xs text-gray-500 flex items-center gap-1">
                <CreditCard size={12} />
                Auto-renovar
              </p>
              <p className="text-sm font-medium text-gray-900 mt-0.5">
                {suscripcion.autoRenovar ? 'Sí' : 'No'}
              </p>
            </div>
          </div>
        </div>
      ) : (
        <div className="bg-yellow-50 rounded-xl p-5 border border-yellow-200">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-xl bg-yellow-500 flex items-center justify-center shadow-sm">
              <CreditCard size={22} className="text-white" />
            </div>
            <div>
              <p className="text-sm font-semibold text-yellow-800">
                Sin suscripción activa
              </p>
              <p className="text-xs text-yellow-600 mt-0.5">
                Selecciona un plan para asignar a este negocio
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Available plans */}
      <div>
        <h4 className="text-sm font-semibold text-gray-800 mb-3">
          {suscripcion ? 'Cambiar Plan' : 'Seleccionar Plan'}
        </h4>
        <div className="grid grid-cols-1 gap-3">
          {activePlanes.map((plan, index) => {
            const isCurrent = currentPlan?.id === plan.id;
            return (
              <button
                key={plan.id}
                onClick={() => handlePlanSelect(plan.id)}
                disabled={isCurrent}
                className={clsx(
                  'text-left rounded-xl p-4 border-2 transition-all',
                  isCurrent
                    ? 'bg-blue-50 border-blue-400 ring-2 ring-blue-200 cursor-default'
                    : 'bg-gradient-to-r hover:shadow-md cursor-pointer border-gray-200 hover:border-blue-300',
                  !isCurrent && PLAN_COLORS[index % PLAN_COLORS.length],
                )}
              >
                <div className="flex items-start justify-between">
                  <div>
                    <div className="flex items-center gap-2">
                      <h5 className="text-sm font-bold text-gray-900">
                        {plan.nombre}
                      </h5>
                      {isCurrent && (
                        <Badge variant="info">Actual</Badge>
                      )}
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
                  {!isCurrent && (
                    <ArrowRight size={18} className="text-gray-400 mt-1 flex-shrink-0" />
                  )}
                  {isCurrent && (
                    <Check size={18} className="text-blue-600 mt-1 flex-shrink-0" />
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
      </div>

      {/* Confirm change dialog */}
      <ConfirmDialog
        isOpen={isConfirmOpen}
        onClose={() => {
          setIsConfirmOpen(false);
          setSelectedPlanId(null);
        }}
        onConfirm={handleConfirmChange}
        title="Cambiar Plan de Suscripción"
        message={
          suscripcion
            ? `¿Cambiar de "${currentPlan?.nombre || 'Sin plan'}" a "${selectedPlanForConfirm?.nombre}"? El cambio se aplicará inmediatamente.`
            : `¿Asignar el plan "${selectedPlanForConfirm?.nombre}" a este negocio?`
        }
        confirmText={suscripcion ? 'Cambiar Plan' : 'Asignar Plan'}
        variant="primary"
        isLoading={isUpdating}
      />
    </div>
  );
};
