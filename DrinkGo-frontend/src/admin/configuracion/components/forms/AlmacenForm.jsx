/**
 * AlmacenForm.jsx
 * ───────────────
 * Formulario para crear/editar almacenes.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { almacenSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

export const AlmacenForm = ({ initialData, sedes = [], onSubmit, onCancel, isLoading }) => {
  const isEditing = !!initialData?.id;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(almacenSchema),
    defaultValues: {
      codigo: '',
      nombre: '',
      descripcion: '',
      sedeId: '',
      esPredeterminado: false,
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        codigo: initialData.codigo || '',
        nombre: initialData.nombre || '',
        descripcion: initialData.descripcion || '',
        sedeId: String(initialData.sede?.id || ''),
        esPredeterminado: initialData.esPredeterminado ?? false,
      });
    }
  }, [initialData, reset]);

  const sedeOptions = sedes.map((s) => ({ value: String(s.id), label: s.nombre }));

  const handleFormSubmit = (data) => {
    onSubmit({
      ...data,
      sedeId: Number(data.sedeId),
    });
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Input
          label="Código"
          required
          {...register('codigo')}
          error={errors.codigo?.message}
          placeholder="ALM-001"
          disabled={isEditing}
        />
        <Input
          label="Nombre"
          required
          {...register('nombre')}
          error={errors.nombre?.message}
          placeholder="Almacén Principal"
        />
      </div>

      <Select
        label="Sede"
        required
        options={sedeOptions}
        placeholder="Seleccione una sede"
        {...register('sedeId')}
        error={errors.sedeId?.message}
      />

      <Textarea
        label="Descripción"
        {...register('descripcion')}
        error={errors.descripcion?.message}
        placeholder="Descripción del almacén (opcional)"
        rows={2}
      />

      <div className="flex items-center gap-2">
        <input
          type="checkbox"
          id="esPredeterminado"
          {...register('esPredeterminado')}
          className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
        />
        <label htmlFor="esPredeterminado" className="text-sm text-gray-700">
          Marcar como almacén predeterminado
        </label>
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEditing ? 'Actualizar Almacén' : 'Crear Almacén'}
        </Button>
      </div>
    </form>
  );
};
