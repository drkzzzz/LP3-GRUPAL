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
  Percent,
  LayoutGrid,
  Save,
  RotateCcw,
  Pencil,
} from 'lucide-react';
import { clsx } from 'clsx';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { ConfirmDialog } from '../ui/ConfirmDialog';
import { Input } from '../ui/Input';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';

const ADMIN_MODULES = [
  { key: 'dashboard',      label: 'Dashboard' },
  { key: 'configuracion', label: 'Configuración' },
  { key: 'usuarios',      label: 'Usuarios' },
  { key: 'catalogo',      label: 'Catálogo' },
  { key: 'inventario',    label: 'Inventario' },
  { key: 'compras',       label: 'Compras' },
  { key: 'ventas',        label: 'Ventas' },
  { key: 'facturacion',   label: 'Facturación' },
  { key: 'gastos',        label: 'Gastos' },
  { key: 'reportes',      label: 'Reportes' },
];

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
  onUpdateNegocio,
  isUpdatingNegocio,
}) => {
  const [selectedPlanId, setSelectedPlanId] = useState(null);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);

  // ── Módulos personalizados ──
  const planModules = (() => {
    if (!suscripcion?.plan?.modulosHabilitados) return null;
    try { return JSON.parse(suscripcion.plan.modulosHabilitados); } catch { return null; }
  })();
  const [editingModules, setEditingModules] = useState(false);
  const [tempModules, setTempModules] = useState(null);
  const [isSavingModules, setIsSavingModules] = useState(false);
  const customModules = (() => {
    if (!suscripcion?.modulosPersonalizados) return null;
    try { return JSON.parse(suscripcion.modulosPersonalizados); } catch { return null; }
  })();

  const startEditModules = () => {
    setTempModules(
      customModules ?? planModules ?? ADMIN_MODULES.map((m) => m.key)
    );
    setEditingModules(true);
  };
  const toggleTempModule = (key) =>
    setTempModules((prev) =>
      prev.includes(key) ? prev.filter((k) => k !== key) : [...prev, key]
    );
  const handleSaveModules = async () => {
    if (!suscripcion) return;
    setIsSavingModules(true);
    try {
      await onChangePlan({
        id: suscripcion.id,
        negocio: { id: negocio.id },
        plan: { id: suscripcion.plan?.id },
        estado: suscripcion.estado,
        inicioPeriodoActual: suscripcion.inicioPeriodoActual,
        finPeriodoActual: suscripcion.finPeriodoActual,
        autoRenovar: suscripcion.autoRenovar ?? true,
        modulosPersonalizados: JSON.stringify(tempModules),
      });
      setEditingModules(false);
    } finally {
      setIsSavingModules(false);
    }
  };
  const handleResetModules = async () => {
    if (!suscripcion) return;
    setIsSavingModules(true);
    try {
      await onChangePlan({
        id: suscripcion.id,
        negocio: { id: negocio.id },
        plan: { id: suscripcion.plan?.id },
        estado: suscripcion.estado,
        inicioPeriodoActual: suscripcion.inicioPeriodoActual,
        finPeriodoActual: suscripcion.finPeriodoActual,
        autoRenovar: suscripcion.autoRenovar ?? true,
        modulosPersonalizados: null,
      });
      setEditingModules(false);
    } finally {
      setIsSavingModules(false);
    }
  };

  // ── IGV ──
  const [editingIgv, setEditingIgv] = useState(false);
  const [localAplicaIgv, setLocalAplicaIgv] = useState(negocio?.aplicaIgv !== false);
  const [localPorcentajeIgv, setLocalPorcentajeIgv] = useState(negocio?.porcentajeIgv ?? 18);
  const [isSavingIgv, setIsSavingIgv] = useState(false);

  const handleSaveIgv = async () => {
    setIsSavingIgv(true);
    try {
      await onUpdateNegocio?.({
        ...negocio,
        aplicaIgv: localAplicaIgv,
        porcentajeIgv: Number(localPorcentajeIgv),
      });
      setEditingIgv(false);
    } finally {
      setIsSavingIgv(false);
    }
  };

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

      {/* Módulos personalizados */}
      {suscripcion && (
        <div className="border border-gray-200 rounded-xl p-4 space-y-3">
          <div className="flex items-center justify-between">
            <h4 className="text-sm font-semibold text-gray-800 flex items-center gap-2">
              <LayoutGrid size={15} className="text-indigo-500" />
              Módulos habilitados
            </h4>
            <div className="flex gap-2">
              {customModules && !editingModules && (
                <button
                  onClick={handleResetModules}
                  disabled={isSavingModules}
                  className="text-xs text-gray-400 hover:text-gray-600 flex items-center gap-1"
                >
                  <RotateCcw size={11} /> Restablecer al plan
                </button>
              )}
              {!editingModules ? (
                <button
                  onClick={startEditModules}
                  className="text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1"
                >
                  <Pencil size={11} /> Personalizar
                </button>
              ) : (
                <div className="flex gap-2">
                  <button
                    onClick={() => setEditingModules(false)}
                    className="text-xs text-gray-400 hover:text-gray-600"
                  >
                    Cancelar
                  </button>
                  <button
                    onClick={handleSaveModules}
                    disabled={isSavingModules}
                    className="text-xs text-green-600 hover:text-green-800 flex items-center gap-1 font-medium"
                  >
                    <Save size={11} /> {isSavingModules ? 'Guardando...' : 'Guardar'}
                  </button>
                </div>
              )}
            </div>
          </div>

          {!editingModules ? (
            <div className="grid grid-cols-2 gap-1.5">
              {ADMIN_MODULES.map(({ key, label }) => {
                const active = customModules
                  ? customModules.includes(key)
                  : planModules
                  ? planModules.includes(key)
                  : true;
                return (
                  <div key={key} className={clsx('flex items-center gap-1.5 text-xs', active ? 'text-gray-700' : 'text-gray-300')}>
                    {active
                      ? <Check size={12} className="text-green-500 shrink-0" />
                      : <span className="w-3 h-3 rounded-full border border-gray-200 shrink-0 inline-block" />}
                    {label}
                    {customModules && !planModules?.includes(key) && active && (
                      <span className="text-xs text-amber-500">(extra)</span>
                    )}
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="space-y-2">
              <p className="text-xs text-gray-500">Selecciona los módulos que este negocio puede usar:</p>
              <div className="grid grid-cols-2 gap-1.5">
                {ADMIN_MODULES.map(({ key, label }) => (
                  <label key={key} className="flex items-center gap-2 text-xs text-gray-700 cursor-pointer select-none">
                    <input
                      type="checkbox"
                      checked={tempModules?.includes(key) ?? true}
                      onChange={() => toggleTempModule(key)}
                      className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                    />
                    {label}
                  </label>
                ))}
              </div>
            </div>
          )}

          {!customModules && !editingModules && (
            <p className="text-xs text-gray-400">
              Usando configuración del plan{planModules ? ` (${planModules.length} módulos)` : ' (todos)'}
            </p>
          )}
        </div>
      )}

      {/* IGV */}
      <div className="border border-gray-200 rounded-xl p-4 space-y-3">
        <div className="flex items-center justify-between">
          <h4 className="text-sm font-semibold text-gray-800 flex items-center gap-2">
            <Percent size={15} className="text-orange-500" />
            Configuración IGV
          </h4>
          {!editingIgv ? (
            <button
              onClick={() => { setLocalAplicaIgv(negocio?.aplicaIgv !== false); setLocalPorcentajeIgv(negocio?.porcentajeIgv ?? 18); setEditingIgv(true); }}
              className="text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1"
            >
              <Pencil size={11} /> Editar
            </button>
          ) : (
            <div className="flex gap-2">
              <button onClick={() => setEditingIgv(false)} className="text-xs text-gray-400 hover:text-gray-600">
                Cancelar
              </button>
              <button
                onClick={handleSaveIgv}
                disabled={isSavingIgv || !onUpdateNegocio}
                className="text-xs text-green-600 hover:text-green-800 flex items-center gap-1 font-medium"
              >
                <Save size={11} /> {isSavingIgv ? 'Guardando...' : 'Guardar'}
              </button>
            </div>
          )}
        </div>

        {!editingIgv ? (
          <div className="flex items-center gap-4 text-sm">
            <span className={clsx('px-2 py-0.5 rounded-full text-xs font-medium', negocio?.aplicaIgv !== false ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500')}>
              {negocio?.aplicaIgv !== false ? 'Aplica IGV' : 'Sin IGV'}
            </span>
            {negocio?.aplicaIgv !== false && (
              <span className="text-gray-700 font-semibold">{negocio?.porcentajeIgv ?? 18}%</span>
            )}
          </div>
        ) : (
          <div className="space-y-2">
            <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
              <input
                type="checkbox"
                checked={localAplicaIgv}
                onChange={(e) => setLocalAplicaIgv(e.target.checked)}
                className="rounded border-gray-300 text-orange-500 focus:ring-orange-400"
              />
              Este negocio aplica IGV en sus ventas
            </label>
            {localAplicaIgv && (
              <div className="flex items-center gap-2">
                <label className="text-xs text-gray-500">Porcentaje:</label>
                <Input
                  type="number"
                  step="0.01"
                  min="0"
                  max="100"
                  value={localPorcentajeIgv}
                  onChange={(e) => setLocalPorcentajeIgv(e.target.value)}
                  className="w-24 text-sm"
                />
                <span className="text-sm text-gray-500">%</span>
              </div>
            )}
          </div>
        )}
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
