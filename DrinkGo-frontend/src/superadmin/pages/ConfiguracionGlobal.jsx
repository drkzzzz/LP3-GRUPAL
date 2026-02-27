import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Settings, Save, RotateCcw, CheckCircle, AlertCircle } from 'lucide-react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { configService } from '../services/configService';

/* ---------- Zod schema ---------- */
const configSchema = z.object({
  nombrePlataforma: z.string().min(2, 'Mínimo 2 caracteres'),
  monedaDefecto: z.enum(['PEN', 'USD'], { errorMap: () => ({ message: 'Seleccione una moneda' }) }),
  impuestoPorDefecto: z.coerce
    .number()
    .min(0, 'Mínimo 0')
    .max(100, 'Máximo 100'),
  tiempoExpiracionSesion: z.coerce.number().min(1, 'Mínimo 1 hora').max(720, 'Máximo 720 horas'),
  maxIntentosSesion: z.coerce.number().int().min(1, 'Mínimo 1').max(20, 'Máximo 20'),
  diasGraciaFactura: z.coerce.number().int().min(0, 'Mínimo 0').max(30, 'Máximo 30'),
  notificacionEmail: z.boolean(),
  alertasFacturacion: z.boolean(),
  alertasAuditoria: z.boolean(),
  respaldoAutomatico: z.boolean(),
});

/* ---------- Toggle Switch ---------- */
const ToggleSwitch = ({ checked, onChange, disabled }) => (
  <button
    type="button"
    onClick={() => !disabled && onChange(!checked)}
    className={`relative w-11 h-6 rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
      checked ? 'bg-blue-600' : 'bg-gray-300'
    } ${disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}`}
    aria-pressed={checked}
  >
    <span
      className={`absolute top-0.5 h-5 w-5 bg-white rounded-full shadow transition-transform ${
        checked ? 'translate-x-5' : 'translate-x-0.5'
      }`}
    />
  </button>
);

/* ---------- Section title ---------- */
const SectionTitle = ({ children }) => (
  <h2 className="text-base font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-100">
    {children}
  </h2>
);

/* ---------- Field wrapper ---------- */
const Field = ({ label, error, required, children }) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-1">
      {label}
      {required && <span className="text-red-500 ml-0.5">*</span>}
    </label>
    {children}
    {error && <p className="text-xs text-red-500 mt-1">{error}</p>}
  </div>
);

export const ConfiguracionGlobal = () => {
  /* ---- Data ---- */
  const { data: savedConfig, isLoading } = useQuery({
    queryKey: ['config-global'],
    queryFn: configService.get,
  });

  /* ---- Mutations ---- */
  const saveMutation = useMutation({
    mutationFn: configService.save,
    onSuccess: () => {},
  });

  const resetMutation = useMutation({
    mutationFn: configService.reset,
    onSuccess: (defaults) => {
      reset(defaults);
    },
  });

  /* ---- Form ---- */
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    reset,
    formState: { errors, isDirty },
  } = useForm({
    resolver: zodResolver(configSchema),
    defaultValues: configService.get(),
  });

  // Sync once data is loaded
  useEffect(() => {
    if (savedConfig) {
      reset(savedConfig);
    }
  }, [savedConfig, reset]);

  const onSubmit = async (data) => {
    await saveMutation.mutateAsync(data);
  };

  const handleReset = async () => {
    await resetMutation.mutateAsync();
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="h-8 bg-gray-200 rounded w-48 animate-pulse" />
        <div className="h-64 bg-gray-100 rounded animate-pulse" />
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-blue-50 rounded-lg">
            <Settings size={24} className="text-blue-600" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Configuración Global</h1>
            <p className="text-gray-600 mt-0.5">
              Ajustes generales de la plataforma DrinkGo
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Button
            type="button"
            variant="outline"
            onClick={handleReset}
            disabled={resetMutation.isPending || saveMutation.isPending}
          >
            <RotateCcw size={16} className="mr-1.5" />
            Restablecer
          </Button>
          <Button
            type="submit"
            disabled={saveMutation.isPending || !isDirty}
          >
            <Save size={16} className="mr-1.5" />
            {saveMutation.isPending ? 'Guardando...' : 'Guardar Cambios'}
          </Button>
        </div>
      </div>

      {/* Success banner */}
      {saveMutation.isSuccess && (
        <div className="flex items-center gap-2 bg-green-50 border border-green-200 rounded-lg px-4 py-3 text-sm text-green-700">
          <CheckCircle size={16} />
          Configuración guardada correctamente.
        </div>
      )}

      {/* Error banner */}
      {saveMutation.isError && (
        <div className="flex items-center gap-2 bg-red-50 border border-red-200 rounded-lg px-4 py-3 text-sm text-red-700">
          <AlertCircle size={16} />
          Error al guardar. Intente nuevamente.
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* ── Sección: General ── */}
        <Card>
          <SectionTitle>General</SectionTitle>
          <div className="space-y-4">
            <Field
              label="Nombre de la Plataforma"
              required
              error={errors.nombrePlataforma?.message}
            >
              <Input {...register('nombrePlataforma')} placeholder="DrinkGo" />
            </Field>

            <Field label="Moneda por Defecto" required error={errors.monedaDefecto?.message}>
              <select
                {...register('monedaDefecto')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="PEN">PEN - Sol Peruano</option>
                <option value="USD">USD - Dólar Americano</option>
              </select>
            </Field>

            <Field
              label="Impuesto por Defecto (%)"
              required
              error={errors.impuestoPorDefecto?.message}
            >
              <Input
                {...register('impuestoPorDefecto')}
                type="number"
                min={0}
                max={100}
                step={0.01}
                placeholder="18"
              />
            </Field>

            <Field
              label="Días de Gracia por Factura"
              required
              error={errors.diasGraciaFactura?.message}
            >
              <Input
                {...register('diasGraciaFactura')}
                type="number"
                min={0}
                max={30}
                placeholder="3"
              />
            </Field>
          </div>
        </Card>

        {/* ── Sección: Seguridad ── */}
        <Card>
          <SectionTitle>Seguridad</SectionTitle>
          <div className="space-y-4">
            <Field
              label="Expiración de Sesión (horas)"
              required
              error={errors.tiempoExpiracionSesion?.message}
            >
              <Input
                {...register('tiempoExpiracionSesion')}
                type="number"
                min={1}
                max={720}
                placeholder="24"
              />
            </Field>

            <Field
              label="Intentos Máximos de Login"
              required
              error={errors.maxIntentosSesion?.message}
            >
              <Input
                {...register('maxIntentosSesion')}
                type="number"
                min={1}
                max={20}
                placeholder="5"
              />
            </Field>

            <div className="bg-blue-50 rounded-lg p-3 text-xs text-blue-700 border border-blue-100">
              Tras superar los intentos máximos, la cuenta quedará bloqueada temporalmente.
            </div>
          </div>
        </Card>

        {/* ── Sección: Notificaciones ── */}
        <Card>
          <SectionTitle>Notificaciones</SectionTitle>
          <div className="space-y-5">
            {[
              {
                key: 'notificacionEmail',
                label: 'Notificaciones por email',
                desc: 'Enviar correos al registrar un nuevo negocio',
              },
              {
                key: 'alertasFacturacion',
                label: 'Alertas de facturación',
                desc: 'Notificar cuando facturas estén próximas a vencer',
              },
              {
                key: 'alertasAuditoria',
                label: 'Alertas de auditoría',
                desc: 'Enviar alertas por acciones críticas en el sistema',
              },
            ].map(({ key, label, desc }) => (
              <div key={key} className="flex items-center justify-between gap-4">
                <div>
                  <p className="text-sm font-medium text-gray-700">{label}</p>
                  <p className="text-xs text-gray-500">{desc}</p>
                </div>
                <ToggleSwitch
                  checked={!!watch(key)}
                  onChange={(val) => setValue(key, val, { shouldDirty: true })}
                />
              </div>
            ))}
          </div>
        </Card>

        {/* ── Sección: Respaldo ── */}
        <Card>
          <SectionTitle>Respaldo de Datos</SectionTitle>
          <div className="space-y-5">
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm font-medium text-gray-700">Respaldo automático</p>
                <p className="text-xs text-gray-500">
                  Respaldar la base de datos diariamente
                </p>
              </div>
              <ToggleSwitch
                checked={!!watch('respaldoAutomatico')}
                onChange={(val) => setValue('respaldoAutomatico', val, { shouldDirty: true })}
              />
            </div>

            <div className="bg-yellow-50 rounded-lg p-3 text-xs text-yellow-700 border border-yellow-100">
              Los respaldos se almacenan de forma segura. La restauración requiere 
              intervención del equipo de soporte técnico.
            </div>
          </div>
        </Card>
      </div>

      {/* Bottom save bar */}
      {isDirty && (
        <div className="sticky bottom-4 bg-white border border-gray-200 rounded-xl shadow-lg px-5 py-3 flex items-center justify-between">
          <p className="text-sm text-gray-600">
            Tienes cambios sin guardar.
          </p>
          <div className="flex gap-2">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => reset(savedConfig)}
              disabled={saveMutation.isPending}
            >
              Descartar
            </Button>
            <Button type="submit" size="sm" disabled={saveMutation.isPending}>
              <Save size={14} className="mr-1.5" />
              {saveMutation.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
          </div>
        </div>
      )}
    </form>
  );
};
