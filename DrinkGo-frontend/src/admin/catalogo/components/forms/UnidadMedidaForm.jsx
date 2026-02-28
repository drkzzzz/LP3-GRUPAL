/**
 * UnidadMedidaForm.jsx
 * ────────────────────
 * Formulario para crear/editar una Unidad de Medida.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { unidadMedidaSchema } from '../../validations/catalogoSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';

/* Opciones del enum TipoUnidad (backend) */
const TIPO_OPTIONS = [
  { value: 'volumen', label: 'Volumen' },
  { value: 'peso', label: 'Peso' },
  { value: 'unidad', label: 'Unidad' },
  { value: 'paquete', label: 'Paquete' },
  { value: 'otro', label: 'Otro' },
];

export const UnidadMedidaForm = ({
  initialData,
  negocioId,
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const isEdit = !!initialData?.id;

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(unidadMedidaSchema),
    defaultValues: {
      codigo: initialData?.codigo || '',
      nombre: initialData?.nombre || '',
      abreviatura: initialData?.abreviatura || '',
      tipo: initialData?.tipo || 'unidad',
      estaActivo: initialData?.estaActivo ?? true,
    },
  });

  const handleFormSubmit = (formData) => {
    const payload = {
      ...(isEdit && { id: initialData.id }),
      negocio: { id: negocioId },
      codigo: formData.codigo,
      nombre: formData.nombre,
      abreviatura: formData.abreviatura,
      tipo: formData.tipo,
      estaActivo: formData.estaActivo,
    };
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Input
          label="Código"
          required
          {...register('codigo')}
          error={errors.codigo?.message}
          placeholder="Ej: ML"
        />
        <Input
          label="Nombre"
          required
          {...register('nombre')}
          error={errors.nombre?.message}
          placeholder="Ej: Mililitros"
        />
        <Input
          label="Abreviatura"
          required
          {...register('abreviatura')}
          error={errors.abreviatura?.message}
          placeholder="Ej: ml"
        />
      </div>

      <Select
        label="Tipo"
        required
        options={TIPO_OPTIONS}
        {...register('tipo')}
        error={errors.tipo?.message}
      />

      <label className="flex items-center gap-2 text-sm text-gray-700">
        <input type="checkbox" {...register('estaActivo')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
        Unidad activa
      </label>

      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear unidad'}
        </Button>
      </div>
    </form>
  );
};
