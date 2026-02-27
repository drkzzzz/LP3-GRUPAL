import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Button } from '../ui/Button';

const schema = z.object({
  razonSocial: z.string().min(3, 'Razón social muy corta'),
  nombreComercial: z.string().optional(),
  ruc: z
    .string()
    .length(11, 'RUC debe tener 11 dígitos')
    .regex(/^\d+$/, 'Solo números'),
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
  estado: z.string().optional(),
});

const DOC_TYPES = [
  { value: 'RUC', label: 'RUC' },
  { value: 'DNI', label: 'DNI' },
  { value: 'CE', label: 'Carnet de Extranjería' },
  { value: 'OTRO', label: 'Otro' },
];

const ESTADOS = [
  { value: 'activo', label: 'Activo' },
  { value: 'pendiente', label: 'Pendiente' },
  { value: 'suspendido', label: 'Suspendido' },
  { value: 'cancelado', label: 'Cancelado' },
];

const TIPOS_NEGOCIO = [
  { value: 'licoreria', label: 'Licorería' },
  { value: 'bar', label: 'Bar' },
  { value: 'restaurante', label: 'Restaurante' },
  { value: 'distribuidor', label: 'Distribuidor' },
  { value: 'otro', label: 'Otro' },
];

export const NegocioForm = ({ initialData, onSubmit, onCancel, isLoading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(schema),
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
      estado: 'activo',
      ...initialData,
    },
  });

  // Fuerza solo dígitos: bloquea letras y sanitiza pegar
  const numericField = (field) => ({
    ...field,
    inputMode: 'numeric',
    onChange: (e) => {
      e.target.value = e.target.value.replace(/\D/g, '');
      field.onChange(e);
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {/* Row 1 */}
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Razón Social <span className="text-red-500">*</span>
          </label>
          <Input
            {...register('razonSocial')}
            placeholder="Licorería El Buen Gusto S.A.C."
            error={errors.razonSocial?.message}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Nombre Comercial
          </label>
          <Input
            {...register('nombreComercial')}
            placeholder="El Buen Gusto"
          />
        </div>
      </div>

      {/* Row 2 */}
      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Tipo Documento <span className="text-red-500">*</span>
          </label>
          <Select
            {...register('tipoDocumentoFiscal')}
            options={DOC_TYPES}
            error={errors.tipoDocumentoFiscal?.message}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            RUC <span className="text-red-500">*</span>
          </label>
          <Input
            {...numericField(register('ruc'))}
            placeholder="20123456789"
            maxLength={11}
            error={errors.ruc?.message}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Tipo de Negocio
          </label>
          <Select {...register('tipoNegocio')} options={TIPOS_NEGOCIO} />
        </div>
      </div>

      {/* Row 3 */}
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Representante Legal
          </label>
          <Input
            {...register('representanteLegal')}
            placeholder="Juan Pérez"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Documento Representante
          </label>
          <Input
            {...numericField(register('documentoRepresentante'))}
            placeholder="12345678"
            maxLength={8}
          />
        </div>
      </div>

      {/* Row 4 */}
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email <span className="text-red-500">*</span>
          </label>
          <Input
            {...register('email')}
            type="email"
            placeholder="contacto@ejemplo.com"
            error={errors.email?.message}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Teléfono
          </label>
          <Input
            {...numericField(register('telefono'))}
            placeholder="987654321"
            maxLength={9}
            error={errors.telefono?.message}
          />
        </div>
      </div>

      {/* Row 5 */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Dirección Fiscal
        </label>
        <Input {...register('direccion')} placeholder="Av. Principal 123" />
      </div>

      {/* Row 6 */}
      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Ciudad
          </label>
          <Input {...register('ciudad')} placeholder="Lima" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Departamento
          </label>
          <Input {...register('departamento')} placeholder="Lima" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Estado
          </label>
          <Select {...register('estado')} options={ESTADOS} />
        </div>
      </div>

      {/* Actions */}
      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading
            ? 'Guardando...'
            : initialData
              ? 'Actualizar Negocio'
              : 'Crear Negocio'}
        </Button>
      </div>
    </form>
  );
};
