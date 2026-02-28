/**
 * MesaForm.jsx
 * ────────────
 * Formulario para crear/editar mesas.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { mesaSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';

const ESTADO_OPTIONS = [
  { value: 'disponible', label: 'Disponible' },
  { value: 'ocupada', label: 'Ocupada' },
  { value: 'reservada', label: 'Reservada' },
  { value: 'mantenimiento', label: 'Mantenimiento' },
];

export const MesaForm = ({ initialData, sedes = [], onSubmit, onCancel, isLoading }) => {
  const isEditing = !!initialData?.id;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(mesaSchema),
    defaultValues: {
      nombre: '',
      sedeId: '',
      capacidad: 4,
      estado: 'disponible',
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        nombre: initialData.nombre || '',
        sedeId: String(initialData.sede?.id || ''),
        capacidad: initialData.capacidad ?? 4,
        estado: initialData.estado || 'disponible',
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
      <Input
        label="Nombre de la mesa"
        required
        {...register('nombre')}
        error={errors.nombre?.message}
        placeholder="Mesa 1"
      />

      <Select
        label="Sede"
        required
        options={sedeOptions}
        placeholder="Seleccione una sede"
        {...register('sedeId')}
        error={errors.sedeId?.message}
      />

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Input
          label="Capacidad (personas)"
          required
          type="number"
          {...register('capacidad', { valueAsNumber: true })}
          error={errors.capacidad?.message}
          placeholder="4"
        />
        <Select
          label="Estado"
          required
          options={ESTADO_OPTIONS}
          {...register('estado')}
          error={errors.estado?.message}
        />
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEditing ? 'Actualizar Mesa' : 'Crear Mesa'}
        </Button>
      </div>
    </form>
  );
};
