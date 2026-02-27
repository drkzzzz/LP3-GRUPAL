/**
 * WizardNuevoNegocio — RF-PLT-001
 * Wizard de 3 pasos para registrar un nuevo negocio (tenant) en la plataforma.
 * Paso 1: Datos del negocio
 * Paso 2: Seleccionar plan de suscripción
 * Paso 3: Confirmación y envío
 */
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Building2,
  CreditCard,
  CheckCircle,
  ChevronRight,
  ChevronLeft,
  Loader2,
  Check,
  KeyRound,
  Eye,
  EyeOff,
  Percent,
} from 'lucide-react';
import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { formatCurrency } from '@/shared/utils/formatters';

/* ---------- Schema Paso 1 ---------- */
const negocioSchema = z.object({
  razonSocial: z.string().min(3, 'Razón social muy corta'),
  nombreComercial: z.string().optional(),
  ruc: z.string().length(11, 'RUC debe tener 11 dígitos').regex(/^\d+$/, 'Solo números'),
  tipoDocumentoFiscal: z.string().min(1, 'Seleccione un tipo'),
  tipoNegocio: z.string().optional(),
  representanteLegal: z.string().optional(),
  documentoRepresentante: z
    .string()
    .max(8, 'Máximo 8 dígitos')
    .regex(/^\d*$/, 'Solo números')
    .optional()
    .or(z.literal('')),
  email: z.string().email('Email inválido'),
  telefono: z
    .string()
    .regex(/^9\d{8}$/, 'Formato: 9XXXXXXXX')
    .optional()
    .or(z.literal('')),
  direccion: z.string().optional(),
  ciudad: z.string().optional(),
  departamento: z.string().optional(),
  // Cuenta de administrador (opcional)
  adminNombres: z.string().optional().or(z.literal('')),
  adminApellidos: z.string().optional().or(z.literal('')),
  adminEmail: z
    .string()
    .email('Email inválido')
    .optional()
    .or(z.literal('')),
  adminPassword: z
    .string()
    .min(6, 'Mínimo 6 caracteres')
    .optional()
    .or(z.literal('')),
  // Configuración fiscal
  aplicaIgv: z.boolean().optional(),
  porcentajeIgv: z.coerce.number().min(0).max(100).optional().or(z.literal('')),
});

const DOC_TYPES = [
  { value: 'RUC', label: 'RUC' },
  { value: 'DNI', label: 'DNI' },
  { value: 'CE', label: 'Carnet de Extranjería' },
  { value: 'OTRO', label: 'Otro' },
];

const TIPOS_NEGOCIO = [
  { value: 'licoreria', label: 'Licorería' },
  { value: 'bar', label: 'Bar' },
  { value: 'restaurante', label: 'Restaurante' },
  { value: 'distribuidor', label: 'Distribuidor' },
  { value: 'otro', label: 'Otro' },
];

/* ---------- Step indicator ---------- */
const STEPS = [
  { number: 1, label: 'Datos del Negocio', icon: Building2 },
  { number: 2, label: 'Plan de Suscripción', icon: CreditCard },
  { number: 3, label: 'Confirmación', icon: CheckCircle },
];

const StepIndicator = ({ current }) => (
  <div className="flex items-center justify-center mb-6">
    {STEPS.map((step, idx) => {
      const Icon = step.icon;
      const isDone = step.number < current;
      const isActive = step.number === current;
      return (
        <div key={step.number} className="flex items-center">
          <div className="flex flex-col items-center">
            <div
              className={`w-9 h-9 rounded-full flex items-center justify-center text-sm font-semibold border-2 transition-colors ${
                isDone
                  ? 'bg-green-500 border-green-500 text-white'
                  : isActive
                  ? 'bg-blue-600 border-blue-600 text-white'
                  : 'bg-white border-gray-300 text-gray-400'
              }`}
            >
              {isDone ? <Check size={16} /> : <Icon size={16} />}
            </div>
            <span
              className={`text-xs mt-1 font-medium whitespace-nowrap ${
                isActive ? 'text-blue-600' : isDone ? 'text-green-600' : 'text-gray-400'
              }`}
            >
              {step.label}
            </span>
          </div>
          {idx < STEPS.length - 1 && (
            <div
              className={`h-0.5 w-16 mx-2 mb-4 ${isDone ? 'bg-green-400' : 'bg-gray-200'}`}
            />
          )}
        </div>
      );
    })}
  </div>
);

/* ---------- Step 1: Datos del Negocio ---------- */
const Step1Form = ({ onNext }) => {
  const [showAdminPass, setShowAdminPass] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(negocioSchema),
    defaultValues: {
      razonSocial: '',
      nombreComercial: '',
      ruc: '',
      tipoDocumentoFiscal: 'RUC',
      tipoNegocio: 'licoreria',
      representanteLegal: '',
      documentoRepresentante: '',
      email: '',
      telefono: '',
      direccion: '',
      ciudad: '',
      departamento: '',
        adminNombres: '',
        adminApellidos: '',
        adminEmail: '',
        adminPassword: 'Admin123!',
        aplicaIgv: true,
        porcentajeIgv: 18,
    },
  });

  const numericField = (field) => ({
    ...field,
    inputMode: 'numeric',
    onChange: (e) => {
      e.target.value = e.target.value.replace(/\D/g, '');
      field.onChange(e);
    },
  });

  return (
    <form onSubmit={handleSubmit(onNext)} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="col-span-2 sm:col-span-1">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Razón Social <span className="text-red-500">*</span>
          </label>
          <Input {...register('razonSocial')} placeholder="Licorería El Buen Gusto S.A.C." />
          {errors.razonSocial && (
            <p className="text-xs text-red-500 mt-1">{errors.razonSocial.message}</p>
          )}
        </div>
        <div className="col-span-2 sm:col-span-1">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Nombre Comercial
          </label>
          <Input {...register('nombreComercial')} placeholder="Licorería BG" />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            RUC <span className="text-red-500">*</span>
          </label>
          <Input
            {...numericField(register('ruc'))}
            placeholder="20123456789"
            maxLength={11}
          />
          {errors.ruc && <p className="text-xs text-red-500 mt-1">{errors.ruc.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Tipo Documento Fiscal <span className="text-red-500">*</span>
          </label>
          <Select {...register('tipoDocumentoFiscal')} options={DOC_TYPES} />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email <span className="text-red-500">*</span>
          </label>
          <Input
            {...register('email')}
            type="email"
            placeholder="contacto@licoreria.com"
          />
          {errors.email && (
            <p className="text-xs text-red-500 mt-1">{errors.email.message}</p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Teléfono
          </label>
          <Input
            {...numericField(register('telefono'))}
            placeholder="987654321"
            maxLength={9}
          />
          {errors.telefono && (
            <p className="text-xs text-red-500 mt-1">{errors.telefono.message}</p>
          )}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Representante Legal
          </label>
          <Input {...register('representanteLegal')} placeholder="Juan Pérez" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Documento Representante
          </label>
          <Input
            {...numericField(register('documentoRepresentante'))}
            placeholder="DNI (8 dígitos)"
            maxLength={8}
          />
        </div>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Tipo Negocio</label>
          <Select {...register('tipoNegocio')} options={TIPOS_NEGOCIO} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Ciudad</label>
          <Input {...register('ciudad')} placeholder="Lima" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Departamento</label>
          <Input {...register('departamento')} placeholder="Lima" />
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Dirección</label>
        <Input {...register('direccion')} placeholder="Av. Principal 123, Miraflores" />
      </div>

      {/* ─── Cuenta de Administrador ─── */}
      <div className="border-t border-gray-200 pt-4 space-y-3">
        <h4 className="text-sm font-semibold text-gray-700 flex items-center gap-2">
          <KeyRound size={14} className="text-indigo-600" />
          Cuenta de Administrador
          <span className="text-xs font-normal text-gray-400">(opcional)</span>
        </h4>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Nombres</label>
            <Input {...register('adminNombres')} placeholder="Juan" />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Apellidos</label>
            <Input {...register('adminApellidos')} placeholder="Pérez" />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Email administrador</label>
            <Input
              {...register('adminEmail')}
              type="email"
              placeholder="admin@negocio.com"
            />
            {errors.adminEmail && (
              <p className="text-xs text-red-500 mt-1">{errors.adminEmail.message}</p>
            )}
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Contraseña</label>
            <div className="relative">
              <Input
                {...register('adminPassword')}
                type={showAdminPass ? 'text' : 'password'}
                placeholder="Mín. 6 caracteres"
              />
              <button
                type="button"
                onClick={() => setShowAdminPass(!showAdminPass)}
                className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                {showAdminPass ? <EyeOff size={15} /> : <Eye size={15} />}
              </button>
            </div>
            {errors.adminPassword && (
              <p className="text-xs text-red-500 mt-1">{errors.adminPassword.message}</p>
            )}
          </div>
        </div>
      </div>

      {/* ─── Configuración de IGV ─── */}
      <div className="border-t border-gray-200 pt-4 space-y-3">
        <h4 className="text-sm font-semibold text-gray-700 flex items-center gap-2">
          <Percent size={14} className="text-blue-600" />
          Configuración de IGV
        </h4>
        <div className="flex items-center gap-6 flex-wrap">
          <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
            <input
              type="checkbox"
              {...register('aplicaIgv')}
              className="rounded border-gray-300 text-blue-600"
            />
            Este negocio aplica IGV en sus ventas
          </label>
          {watch('aplicaIgv') && (
            <div className="flex items-center gap-2">
              <label className="text-xs text-gray-500">Porcentaje:</label>
              <div className="flex items-center gap-1">
                <Input
                  {...register('porcentajeIgv')}
                  type="number"
                  step="0.01"
                  min="0"
                  max="100"
                  className="w-24 text-sm"
                />
                <span className="text-sm text-gray-500 font-medium">%</span>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="flex justify-end pt-2">
        <Button type="submit">
          Siguiente
          <ChevronRight size={16} className="ml-1" />
        </Button>
      </div>
    </form>
  );
};

/* ---------- Step 2: Seleccionar Plan ---------- */
const Step2Plan = ({ planes, selectedPlanId, onSelect, onBack, onNext }) => {
  const PERIODO_LABELS = { mensual: '/mes', anual: '/año', trimestral: '/trim.' };

  return (
    <div className="space-y-4">
      <p className="text-sm text-gray-600">
        Selecciona el plan de suscripción inicial para este negocio.
      </p>

      {planes.length === 0 ? (
        <div className="text-center py-8 text-gray-400">
          No hay planes disponibles. Crea un plan primero.
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 max-h-80 overflow-y-auto pr-1">
          {planes.filter((p) => p.estaActivo !== false).map((plan) => (
            <button
              key={plan.id}
              type="button"
              onClick={() => onSelect(plan.id)}
              className={`text-left p-4 rounded-lg border-2 transition-all ${
                selectedPlanId === plan.id
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-200 hover:border-blue-300 hover:bg-gray-50'
              }`}
            >
              <div className="flex items-start justify-between">
                <p className="font-semibold text-gray-900 text-sm">{plan.nombre}</p>
                {selectedPlanId === plan.id && (
                  <Check size={16} className="text-blue-600 mt-0.5 shrink-0" />
                )}
              </div>
              <p className="text-lg font-bold text-blue-600 mt-1">
                {formatCurrency(plan.precio)}
                <span className="text-xs font-normal text-gray-500">
                  {PERIODO_LABELS[plan.periodoFacturacion] || ''}
                </span>
              </p>
              <div className="mt-2 space-y-0.5 text-xs text-gray-500">
                {plan.maxSedes && <p>• Hasta {plan.maxSedes} sede(s)</p>}
                {plan.maxUsuariosPorSede && (
                  <p>• Hasta {plan.maxUsuariosPorSede} usuario(s)/sede</p>
                )}
                {plan.maxProductos && <p>• Hasta {plan.maxProductos} productos</p>}
              </div>
            </button>
          ))}
        </div>
      )}

      <div className="flex justify-between pt-2">
        <Button variant="outline" onClick={onBack}>
          <ChevronLeft size={16} className="mr-1" />
          Anterior
        </Button>
        <Button onClick={onNext} disabled={!selectedPlanId}>
          Siguiente
          <ChevronRight size={16} className="ml-1" />
        </Button>
      </div>
    </div>
  );
};

/* ---------- Step 3: Confirmación ---------- */
const Step3Confirm = ({ negocioData, planId, planes, onBack, onSubmit, isLoading }) => {
  const plan = planes.find((p) => p.id === planId);

  return (
    <div className="space-y-4">
      <div className="bg-green-50 border border-green-200 rounded-lg p-3 flex items-start gap-3">
        <CheckCircle size={18} className="text-green-600 shrink-0 mt-0.5" />
        <p className="text-sm text-green-800">
          Revisa los datos antes de crear el negocio. El sistema creará el negocio y
          asignará el plan de suscripción de forma automática.
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {/* Datos del negocio */}
        <div className="border border-gray-200 rounded-lg p-4 space-y-2">
          <h4 className="font-semibold text-gray-800 text-sm flex items-center gap-2">
            <Building2 size={14} className="text-blue-600" />
            Datos del Negocio
          </h4>
          <dl className="space-y-1 text-sm">
            <div className="flex gap-2">
              <dt className="text-gray-500 w-24 shrink-0">Razón Social:</dt>
              <dd className="text-gray-900 font-medium">{negocioData.razonSocial}</dd>
            </div>
            {negocioData.nombreComercial && (
              <div className="flex gap-2">
                <dt className="text-gray-500 w-24 shrink-0">Comercial:</dt>
                <dd className="text-gray-900">{negocioData.nombreComercial}</dd>
              </div>
            )}
            <div className="flex gap-2">
              <dt className="text-gray-500 w-24 shrink-0">RUC:</dt>
              <dd className="text-gray-900">{negocioData.ruc}</dd>
            </div>
            <div className="flex gap-2">
              <dt className="text-gray-500 w-24 shrink-0">Email:</dt>
              <dd className="text-gray-900 truncate">{negocioData.email}</dd>
            </div>
            {negocioData.ciudad && (
              <div className="flex gap-2">
                <dt className="text-gray-500 w-24 shrink-0">Ciudad:</dt>
                <dd className="text-gray-900">{negocioData.ciudad}</dd>
              </div>
            )}
            {negocioData.adminEmail && (
              <div className="flex gap-2">
                <dt className="text-gray-500 w-24 shrink-0">Admin:</dt>
                <dd className="text-indigo-700 font-medium truncate">{negocioData.adminEmail}</dd>
              </div>
            )}
            <div className="flex gap-2">
              <dt className="text-gray-500 w-24 shrink-0">IGV:</dt>
              <dd className="text-gray-900">
                {negocioData.aplicaIgv ? `Sí (${negocioData.porcentajeIgv ?? 18}%)` : 'No aplica'}
              </dd>
            </div>
          </dl>
        </div>

        {/* Plan seleccionado */}
        <div className="border border-blue-200 bg-blue-50 rounded-lg p-4 space-y-2">
          <h4 className="font-semibold text-gray-800 text-sm flex items-center gap-2">
            <CreditCard size={14} className="text-blue-600" />
            Plan Seleccionado
          </h4>
          {plan ? (
            <dl className="space-y-1 text-sm">
              <div className="flex gap-2">
                <dt className="text-gray-500 w-24 shrink-0">Nombre:</dt>
                <dd className="text-blue-700 font-semibold">{plan.nombre}</dd>
              </div>
              <div className="flex gap-2">
                <dt className="text-gray-500 w-24 shrink-0">Precio:</dt>
                <dd className="text-blue-700 font-bold">{formatCurrency(plan.precio)}</dd>
              </div>
              <div className="flex gap-2">
                <dt className="text-gray-500 w-24 shrink-0">Periodo:</dt>
                <dd className="text-gray-900 capitalize">{plan.periodoFacturacion}</dd>
              </div>
            </dl>
          ) : (
            <p className="text-sm text-gray-500">Sin plan (se puede asignar después)</p>
          )}
        </div>
      </div>

      <div className="flex justify-between pt-2">
        <Button variant="outline" onClick={onBack} disabled={isLoading}>
          <ChevronLeft size={16} className="mr-1" />
          Anterior
        </Button>
        <Button onClick={onSubmit} disabled={isLoading}>
          {isLoading ? (
            <>
              <Loader2 size={16} className="mr-2 animate-spin" />
              Creando negocio...
            </>
          ) : (
            <>
              <CheckCircle size={16} className="mr-2" />
              Crear Negocio
            </>
          )}
        </Button>
      </div>
    </div>
  );
};

/* ---------- Main Wizard ---------- */
export const WizardNuevoNegocio = ({
  isOpen,
  onClose,
  planes,
  onSubmit,
  isLoading,
}) => {
  const [step, setStep] = useState(1);
  const [negocioData, setNegocioData] = useState(null);
  const [selectedPlanId, setSelectedPlanId] = useState(null);

  const handleReset = () => {
    setStep(1);
    setNegocioData(null);
    setSelectedPlanId(null);
  };

  const handleClose = () => {
    handleReset();
    onClose();
  };

  const handleStep1Next = (data) => {
    setNegocioData(data);
    setStep(2);
  };

  const handleStep2Next = () => {
    setStep(3);
  };

  const handleStep3Submit = async () => {
    await onSubmit({ negocioData, selectedPlanId });
    handleReset();
  };

  const titles = ['Registrar Nuevo Negocio', 'Seleccionar Plan', 'Confirmar Registro'];

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={titles[step - 1]}
      size="xl"
    >
      <StepIndicator current={step} />

      {step === 1 && <Step1Form onNext={handleStep1Next} />}

      {step === 2 && (
        <Step2Plan
          planes={planes}
          selectedPlanId={selectedPlanId}
          onSelect={setSelectedPlanId}
          onBack={() => setStep(1)}
          onNext={handleStep2Next}
        />
      )}

      {step === 3 && (
        <Step3Confirm
          negocioData={negocioData}
          planId={selectedPlanId}
          planes={planes}
          onBack={() => setStep(2)}
          onSubmit={handleStep3Submit}
          isLoading={isLoading}
        />
      )}
    </Modal>
  );
};
