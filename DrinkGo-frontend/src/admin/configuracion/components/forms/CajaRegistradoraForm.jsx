/**
 * CajaRegistradoraForm.jsx
 * ────────────────────────
 * Formulario para crear/editar cajas registradoras.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { cajaRegistradoraSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';

export const CajaRegistradoraForm = ({ initialData, sedes = [], onSubmit, onCancel, isLoading }) => {
  const isEditing = !!initialData?.id;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(cajaRegistradoraSchema),
    defaultValues: {
      codigo: '',
      nombreCaja: '',
      sedeId: '',
      montoAperturaDefecto: 0,
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        codigo: initialData.codigo || '',
        nombreCaja: initialData.nombreCaja || '',
        sedeId: String(initialData.sede?.id || ''),
        montoAperturaDefecto: initialData.montoAperturaDefecto ?? 0,
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
          placeholder="CAJA-01"
          disabled={isEditing}
        />
        <Input
          label="Nombre"
          required
          {...register('nombreCaja')}
          error={errors.nombreCaja?.message}
          placeholder="Caja Principal"
        />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Select
          label="Sede"
          required
          options={sedeOptions}
          placeholder="Seleccione una sede"
          {...register('sedeId')}
          error={errors.sedeId?.message}
        />
        <Input
          label="Monto de Apertura por Defecto (S/)"
          type="number"
          step="0.01"
          {...register('montoAperturaDefecto', { valueAsNumber: true })}
          error={errors.montoAperturaDefecto?.message}
          placeholder="100.00"
        />
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEditing ? 'Actualizar Caja' : 'Crear Caja'}
        </Button>
      </div>
    </form>
  );
};
