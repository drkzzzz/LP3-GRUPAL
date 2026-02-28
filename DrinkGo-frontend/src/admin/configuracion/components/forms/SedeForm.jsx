/**
 * SedeForm.jsx
 * ────────────
 * Formulario para crear/editar sedes.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { sedeSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Button } from '@/admin/components/ui/Button';

export const SedeForm = ({ initialData, onSubmit, onCancel, isLoading }) => {
  const isEditing = !!initialData?.id;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(sedeSchema),
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
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        codigo: initialData.codigo || '',
        nombre: initialData.nombre || '',
        direccion: initialData.direccion || '',
        ciudad: initialData.ciudad || '',
        departamento: initialData.departamento || '',
        pais: initialData.pais || 'PE',
        codigoPostal: initialData.codigoPostal || '',
        telefono: initialData.telefono || '',
        esPrincipal: initialData.esPrincipal ?? false,
        deliveryHabilitado: initialData.deliveryHabilitado ?? false,
        recojoHabilitado: initialData.recojoHabilitado ?? false,
      });
    }
  }, [initialData, reset]);

  const handleFormSubmit = (data) => {
    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Código"
          required
          {...register('codigo')}
          error={errors.codigo?.message}
          placeholder="SEDE-01"
          disabled={isEditing}
        />
        <Input
          label="Nombre"
          required
          {...register('nombre')}
          error={errors.nombre?.message}
          placeholder="Sede Principal - Lima"
        />
      </div>

      <Input
        label="Dirección"
        required
        {...register('direccion')}
        error={errors.direccion?.message}
        placeholder="Av. Los Incas 1234, Cercado de Lima"
      />

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Input
          label="Ciudad"
          {...register('ciudad')}
          error={errors.ciudad?.message}
          placeholder="Lima"
        />
        <Input
          label="Departamento"
          {...register('departamento')}
          error={errors.departamento?.message}
          placeholder="Lima"
        />
        <Input
          label="País"
          {...register('pais')}
          error={errors.pais?.message}
          placeholder="PE"
          maxLength={3}
        />
        <Input
          label="Teléfono"
          {...register('telefono')}
          error={errors.telefono?.message}
          placeholder="987654321"
          maxLength={9}
        />
      </div>

      {/* Opciones booleanas */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-2">
        <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
          <input
            type="checkbox"
            {...register('esPrincipal')}
            className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
          />
          Sede principal
        </label>
        <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
          <input
            type="checkbox"
            {...register('deliveryHabilitado')}
            className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
          />
          Delivery habilitado
        </label>
        <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
          <input
            type="checkbox"
            {...register('recojoHabilitado')}
            className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
          />
          Recojo en tienda
        </label>
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEditing ? 'Actualizar Sede' : 'Crear Sede'}
        </Button>
      </div>
    </form>
  );
};
