/**
 * ZonaDeliveryForm.jsx
 * ────────────────────
 * Formulario para crear/editar zonas de delivery.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { zonaDeliverySchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

export const ZonaDeliveryForm = ({ initialData, sedes = [], onSubmit, onCancel, isLoading }) => {
  const isEditing = !!initialData?.id;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(zonaDeliverySchema),
    defaultValues: {
      nombre: '',
      descripcion: '',
      sedeId: '',
      tarifaDelivery: 0,
      montoMinimoPedido: 0,
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        nombre: initialData.nombre || '',
        descripcion: initialData.descripcion || '',
        sedeId: String(initialData.sede?.id || ''),
        tarifaDelivery: initialData.tarifaDelivery ?? 0,
        montoMinimoPedido: initialData.montoMinimoPedido ?? 0,
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
        label="Nombre de la zona"
        required
        {...register('nombre')}
        error={errors.nombre?.message}
        placeholder="Zona Centro - Lima"
      />

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
        placeholder="Área de cobertura, referencias, límites geográficos..."
        rows={2}
      />

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Input
          label="Tarifa de Delivery (S/)"
          required
          type="number"
          step="0.01"
          {...register('tarifaDelivery', { valueAsNumber: true })}
          error={errors.tarifaDelivery?.message}
          placeholder="5.00"
        />
        <Input
          label="Monto Mínimo de Pedido (S/)"
          type="number"
          step="0.01"
          {...register('montoMinimoPedido', { valueAsNumber: true })}
          error={errors.montoMinimoPedido?.message}
          placeholder="30.00"
        />
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEditing ? 'Actualizar Zona' : 'Crear Zona'}
        </Button>
      </div>
    </form>
  );
};
