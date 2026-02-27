import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';

const schema = z.object({
  codigo: z.string().min(2, 'Código requerido (mín. 2 caracteres)'),
  nombre: z.string().min(2, 'Nombre requerido'),
  direccion: z.string().min(3, 'Dirección requerida'),
  ciudad: z.string().optional(),
  departamento: z.string().optional(),
  pais: z.string().optional(),
  codigoPostal: z.string().optional(),
  telefono: z
    .string()
    .regex(/^9\d{8}$/, 'Formato: 9XXXXXXXX')
    .optional()
    .or(z.literal('')),
  esPrincipal: z.boolean().optional(),
  deliveryHabilitado: z.boolean().optional(),
  recojoHabilitado: z.boolean().optional(),
});

export const SedeForm = ({ initialData, onSubmit, onCancel, isLoading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      codigo: '',
      nombre: '',
      direccion: '',
      ciudad: '',
      departamento: '',
      pais: 'PE',
      codigoPostal: '',
      telefono: '',
      esPrincipal: false,
      deliveryHabilitado: false,
      recojoHabilitado: false,
      ...initialData,
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {/* Row 1: Código + Nombre */}
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Código <span className="text-red-500">*</span>
          </label>
          <Input
            {...register('codigo')}
            placeholder="SEDE-001"
            error={errors.codigo?.message}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Nombre <span className="text-red-500">*</span>
          </label>
          <Input
            {...register('nombre')}
            placeholder="Sede Principal"
            error={errors.nombre?.message}
          />
        </div>
      </div>

      {/* Row 2: Dirección */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Dirección <span className="text-red-500">*</span>
        </label>
        <Input
          {...register('direccion')}
          placeholder="Av. Principal 123"
          error={errors.direccion?.message}
        />
      </div>

      {/* Row 3: Ciudad + Departamento */}
      <div className="grid grid-cols-2 gap-4">
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
      </div>

      {/* Row 4: Teléfono + Código Postal */}
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Teléfono
          </label>
          <Input
            {...register('telefono')}
            placeholder="987654321"
            maxLength={9}
            error={errors.telefono?.message}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Código Postal
          </label>
          <Input {...register('codigoPostal')} placeholder="15001" />
        </div>
      </div>

      {/* Row 5: Toggles */}
      <div className="bg-gray-50 rounded-lg p-4 space-y-3">
        <p className="text-sm font-medium text-gray-700">Configuración</p>
        <label className="flex items-center gap-3 cursor-pointer">
          <input
            type="checkbox"
            {...register('esPrincipal')}
            className="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
          />
          <div>
            <span className="text-sm text-gray-700">Sede Principal</span>
            <p className="text-xs text-gray-500">
              Marcar como sede principal del negocio
            </p>
          </div>
        </label>
        <label className="flex items-center gap-3 cursor-pointer">
          <input
            type="checkbox"
            {...register('deliveryHabilitado')}
            className="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
          />
          <div>
            <span className="text-sm text-gray-700">Delivery habilitado</span>
            <p className="text-xs text-gray-500">
              Permitir entregas a domicilio desde esta sede
            </p>
          </div>
        </label>
        <label className="flex items-center gap-3 cursor-pointer">
          <input
            type="checkbox"
            {...register('recojoHabilitado')}
            className="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
          />
          <div>
            <span className="text-sm text-gray-700">Recojo en tienda</span>
            <p className="text-xs text-gray-500">
              Permitir recojo de pedidos en esta sede
            </p>
          </div>
        </label>
      </div>

      {/* Actions */}
      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : 'Guardar Sede'}
        </Button>
      </div>
    </form>
  );
};
