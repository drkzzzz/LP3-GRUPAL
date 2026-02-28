/**
 * CategoriaForm.jsx
 * ─────────────────
 * Formulario para crear/editar una Categoría.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { categoriaSchema } from '../../validations/catalogoSchemas';
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

export const CategoriaForm = ({
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
    resolver: zodResolver(categoriaSchema),
    defaultValues: {
      nombre: initialData?.nombre || '',
      slug: initialData?.slug || '',
      descripcion: initialData?.descripcion || '',
      esAlcoholica: initialData?.esAlcoholica ?? false,
      visibleTiendaOnline: initialData?.visibleTiendaOnline ?? true,
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
      esAlcoholica: formData.esAlcoholica,
      visibleTiendaOnline: formData.visibleTiendaOnline,
      estaActivo: formData.estaActivo,
    };
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Nombre"
          required
          {...register('nombre')}
          onBlur={handleNombreBlur}
          error={errors.nombre?.message}
          placeholder="Ej: Vinos Tintos"
        />
        <Input
          label="Slug"
          required
          {...register('slug')}
          error={errors.slug?.message}
          placeholder="vinos-tintos"
        />
      </div>

      <Textarea
        label="Descripción"
        {...register('descripcion')}
        error={errors.descripcion?.message}
      />

      <div className="flex flex-wrap gap-4">
        <label className="flex items-center gap-2 text-sm text-gray-700">
          <input type="checkbox" {...register('esAlcoholica')} className="rounded border-gray-300 text-amber-600 focus:ring-amber-500" />
          Categoría con alcohol
        </label>
        <label className="flex items-center gap-2 text-sm text-gray-700">
          <input type="checkbox" {...register('visibleTiendaOnline')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
          Visible en tienda online
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
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear categoría'}
        </Button>
      </div>
    </form>
  );
};
