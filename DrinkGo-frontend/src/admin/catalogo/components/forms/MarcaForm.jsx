/**
 * MarcaForm.jsx
 * ─────────────
 * Formulario para crear/editar una Marca.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { marcaSchema } from '../../validations/catalogoSchemas';
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

export const MarcaForm = ({
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
    resolver: zodResolver(marcaSchema),
    defaultValues: {
      nombre: initialData?.nombre || '',
      slug: initialData?.slug || '',
      paisOrigen: initialData?.paisOrigen || '',
      urlLogo: initialData?.urlLogo || '',
      descripcion: initialData?.descripcion || '',
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
      paisOrigen: formData.paisOrigen || null,
      urlLogo: formData.urlLogo || null,
      descripcion: formData.descripcion || null,
      estaActivo: formData.estaActivo,
    };
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Nombre de la marca"
          required
          {...register('nombre')}
          onBlur={handleNombreBlur}
          error={errors.nombre?.message}
          placeholder="Ej: Cartavio"
        />
        <Input
          label="Slug"
          required
          {...register('slug')}
          error={errors.slug?.message}
          placeholder="cartavio"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="País de origen"
          {...register('paisOrigen')}
          error={errors.paisOrigen?.message}
          placeholder="Ej: Perú"
        />
        <Input
          label="URL del logo"
          {...register('urlLogo')}
          error={errors.urlLogo?.message}
          placeholder="https://..."
        />
      </div>

      <Textarea
        label="Descripción"
        {...register('descripcion')}
        error={errors.descripcion?.message}
      />

      <label className="flex items-center gap-2 text-sm text-gray-700">
        <input type="checkbox" {...register('estaActivo')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
        Marca activa
      </label>

      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear marca'}
        </Button>
      </div>
    </form>
  );
};
