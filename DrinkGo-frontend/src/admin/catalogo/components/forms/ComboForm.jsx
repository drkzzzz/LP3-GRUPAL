/**
 * ComboForm.jsx
 * ─────────────
 * Formulario para crear/editar un Combo promocional.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { comboSchema } from '../../validations/catalogoSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

const generateSlug = (name) =>
  name
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '');

export const ComboForm = ({
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
    setValue,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(comboSchema),
    defaultValues: {
      nombre: initialData?.nombre || '',
      slug: initialData?.slug || '',
      descripcion: initialData?.descripcion || '',
      urlImagen: initialData?.urlImagen || '',
      precioRegular: String(initialData?.precioRegular ?? ''),
      precioCombo: String(initialData?.precioCombo ?? ''),
      porcentajeDescuento: String(initialData?.porcentajeDescuento ?? ''),
      fechaInicio: initialData?.fechaInicio || '',
      fechaFin: initialData?.fechaFin || '',
      visiblePos: initialData?.visiblePos ?? true,
      visibleTiendaOnline: initialData?.visibleTiendaOnline ?? false,
      esDestacado: initialData?.esDestacado ?? false,
      estaActivo: initialData?.estaActivo ?? true,
    },
  });

  const nombre = watch('nombre');
  const handleNombreBlur = () => {
    const currentSlug = watch('slug');
    if (!currentSlug || !isEdit) {
      setValue('slug', generateSlug(nombre));
    }
  };

  const handleFormSubmit = (formData) => {
    const payload = {
      ...(isEdit && { id: initialData.id }),
      negocio: { id: negocioId },
      nombre: formData.nombre,
      slug: formData.slug,
      descripcion: formData.descripcion || null,
      urlImagen: formData.urlImagen || null,
      precioRegular: Number(formData.precioRegular) || 0,
      precioCombo: Number(formData.precioCombo) || 0,
      porcentajeDescuento: formData.porcentajeDescuento ? Number(formData.porcentajeDescuento) : null,
      fechaInicio: formData.fechaInicio || null,
      fechaFin: formData.fechaFin || null,
      visiblePos: formData.visiblePos,
      visibleTiendaOnline: formData.visibleTiendaOnline,
      esDestacado: formData.esDestacado,
      estaActivo: formData.estaActivo,
    };
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Nombre del combo"
          required
          {...register('nombre')}
          onBlur={handleNombreBlur}
          error={errors.nombre?.message}
          placeholder="Ej: Pack Fiesta"
        />
        <Input
          label="Slug"
          required
          {...register('slug')}
          error={errors.slug?.message}
          placeholder="pack-fiesta"
        />
      </div>

      <Input
        label="URL Imagen"
        {...register('urlImagen')}
        error={errors.urlImagen?.message}
        placeholder="https://..."
      />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Input
          label="Precio regular (S/)"
          required
          type="number"
          step="0.01"
          {...register('precioRegular')}
          error={errors.precioRegular?.message}
        />
        <Input
          label="Precio combo (S/)"
          required
          type="number"
          step="0.01"
          {...register('precioCombo')}
          error={errors.precioCombo?.message}
        />
        <Input
          label="% Descuento"
          type="number"
          step="0.01"
          {...register('porcentajeDescuento')}
          error={errors.porcentajeDescuento?.message}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Fecha inicio"
          type="date"
          {...register('fechaInicio')}
          error={errors.fechaInicio?.message}
        />
        <Input
          label="Fecha fin"
          type="date"
          {...register('fechaFin')}
          error={errors.fechaFin?.message}
        />
      </div>

      <Textarea
        label="Descripción"
        {...register('descripcion')}
        error={errors.descripcion?.message}
      />

      <div className="flex flex-wrap gap-4">
        <label className="flex items-center gap-2 text-sm text-gray-700">
          <input type="checkbox" {...register('visiblePos')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
          Visible en POS
        </label>
        <label className="flex items-center gap-2 text-sm text-gray-700">
          <input type="checkbox" {...register('visibleTiendaOnline')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
          Visible en tienda online
        </label>
        <label className="flex items-center gap-2 text-sm text-gray-700">
          <input type="checkbox" {...register('esDestacado')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
          Destacado
        </label>
        <label className="flex items-center gap-2 text-sm text-gray-700">
          <input type="checkbox" {...register('estaActivo')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
          Activo
        </label>
      </div>

      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear combo'}
        </Button>
      </div>
    </form>
  );
};
