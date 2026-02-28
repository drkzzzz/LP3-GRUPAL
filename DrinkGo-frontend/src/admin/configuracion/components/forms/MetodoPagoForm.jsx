/**
 * MetodoPagoForm.jsx
 * ──────────────────
 * Formulario para crear/editar métodos de pago.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { metodoPagoSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';

const TIPO_OPTIONS = [
  { value: 'efectivo', label: 'Efectivo' },
  { value: 'tarjeta_credito', label: 'Tarjeta de Crédito' },
  { value: 'tarjeta_debito', label: 'Tarjeta de Débito' },
  { value: 'transferencia_bancaria', label: 'Transferencia Bancaria' },
  { value: 'billetera_digital', label: 'Billetera Digital' },
  { value: 'yape', label: 'Yape' },
  { value: 'plin', label: 'Plin' },
  { value: 'qr', label: 'QR' },
  { value: 'otro', label: 'Otro' },
];

export const MetodoPagoForm = ({ initialData, onSubmit, onCancel, isLoading }) => {
  const isEditing = !!initialData?.id;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(metodoPagoSchema),
    defaultValues: {
      nombre: '',
      codigo: '',
      tipo: 'efectivo',
      disponiblePos: true,
      disponibleTiendaOnline: false,
      orden: 0,
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        nombre: initialData.nombre || '',
        codigo: initialData.codigo || '',
        tipo: initialData.tipo || 'efectivo',
        disponiblePos: initialData.disponiblePos ?? true,
        disponibleTiendaOnline: initialData.disponibleTiendaOnline ?? false,
        orden: initialData.orden ?? 0,
      });
    }
  }, [initialData, reset]);

  const handleFormSubmit = (data) => {
    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Input
          label="Nombre"
          required
          {...register('nombre')}
          error={errors.nombre?.message}
          placeholder="Efectivo"
        />
        <Input
          label="Código"
          required
          {...register('codigo')}
          error={errors.codigo?.message}
          placeholder="efectivo_sol"
          disabled={isEditing}
        />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Select
          label="Tipo"
          required
          options={TIPO_OPTIONS}
          {...register('tipo')}
          error={errors.tipo?.message}
        />
        <Input
          label="Orden de visualización"
          type="number"
          {...register('orden', { valueAsNumber: true })}
          error={errors.orden?.message}
          placeholder="0"
        />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
        <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
          <input
            type="checkbox"
            {...register('disponiblePos')}
            className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
          />
          Disponible en POS (punto de venta)
        </label>
        <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
          <input
            type="checkbox"
            {...register('disponibleTiendaOnline')}
            className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
          />
          Disponible en tienda online
        </label>
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEditing ? 'Actualizar Método' : 'Crear Método'}
        </Button>
      </div>
    </form>
  );
};
