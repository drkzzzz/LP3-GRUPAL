/**
 * ProductoForm.jsx
 * ────────────────
 * Formulario para crear/editar un Producto.
 * Usa React Hook Form + Zod. Recibe categorías, marcas y unidades como props.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { productoSchema } from '../../validations/catalogoSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

/**
 * Genera un slug a partir de un nombre.
 * Ejemplo: "Ron Cartavio XO" → "ron-cartavio-xo"
 */
const generateSlug = (name) =>
  name
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '');

export const ProductoForm = ({
  initialData,
  categorias = [],
  marcas = [],
  unidades = [],
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
    resolver: zodResolver(productoSchema),
    defaultValues: {
      nombre: initialData?.nombre || '',
      sku: initialData?.sku || '',
      slug: initialData?.slug || '',
      descripcion: initialData?.descripcion || '',
      urlImagen: initialData?.urlImagen || '',
      categoriaId: String(initialData?.categoria?.id || initialData?.categoriaId || ''),
      marcaId: String(initialData?.marca?.id || initialData?.marcaId || ''),
      unidadMedidaId: String(initialData?.unidadMedida?.id || initialData?.unidadMedidaId || ''),
      gradoAlcoholico: String(initialData?.gradoAlcoholico ?? ''),
      precioCompra: String(initialData?.precioCompra ?? '0'),
      precioVenta: String(initialData?.precioVenta ?? ''),
      precioVentaMinimo: String(initialData?.precioVentaMinimo ?? ''),
      precioMayorista: String(initialData?.precioMayorista ?? ''),
      tasaImpuesto: String(initialData?.tasaImpuesto ?? '18'),
      impuestoIncluido: initialData?.impuestoIncluido ?? true,
      permiteDescuento: initialData?.permiteDescuento ?? true,
      estaActivo: initialData?.estaActivo ?? true,
    },
  });

  /* Auto-generar slug cuando cambia el nombre */
  const nombre = watch('nombre');
  const handleNombreBlur = () => {
    const currentSlug = watch('slug');
    if (!currentSlug || (!isEdit && currentSlug === generateSlug(nombre))) {
      setValue('slug', generateSlug(nombre));
    }
  };

  /* Preparar datos antes de enviar al backend */
  const handleFormSubmit = (formData) => {
    const payload = {
      ...(isEdit && { id: initialData.id }),
      negocio: { id: negocioId },
      nombre: formData.nombre,
      sku: formData.sku,
      slug: formData.slug,
      descripcion: formData.descripcion || null,
      urlImagen: formData.urlImagen || null,
      categoria: formData.categoriaId ? { id: Number(formData.categoriaId) } : null,
      marca: formData.marcaId ? { id: Number(formData.marcaId) } : null,
      unidadMedida: formData.unidadMedidaId ? { id: Number(formData.unidadMedidaId) } : null,
      gradoAlcoholico: formData.gradoAlcoholico ? Number(formData.gradoAlcoholico) : null,
      precioCompra: Number(formData.precioCompra) || 0,
      precioVenta: Number(formData.precioVenta),
      precioVentaMinimo: formData.precioVentaMinimo ? Number(formData.precioVentaMinimo) : null,
      precioMayorista: formData.precioMayorista ? Number(formData.precioMayorista) : null,
      tasaImpuesto: Number(formData.tasaImpuesto) || 18,
      impuestoIncluido: formData.impuestoIncluido,
      permiteDescuento: formData.permiteDescuento,
      estaActivo: formData.estaActivo,
    };
    onSubmit(payload);
  };

  /* Opciones para selects */
  const categoriaOptions = categorias.map((c) => ({ value: String(c.id), label: c.nombre }));
  const marcaOptions = marcas.map((m) => ({ value: String(m.id), label: m.nombre }));
  const unidadOptions = unidades.map((u) => ({ value: String(u.id), label: `${u.nombre} (${u.abreviatura})` }));

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      {/* Fila 1: Nombre + SKU */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Nombre del producto"
          required
          {...register('nombre')}
          onBlur={handleNombreBlur}
          error={errors.nombre?.message}
          placeholder="Ej: Ron Cartavio XO"
        />
        <Input
          label="SKU"
          required
          {...register('sku')}
          error={errors.sku?.message}
          placeholder="Ej: RON-CART-XO-750"
        />
      </div>

      {/* Fila 2: Slug + URL Imagen */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Slug"
          required
          {...register('slug')}
          error={errors.slug?.message}
          placeholder="ron-cartavio-xo"
        />
        <Input
          label="URL Imagen"
          {...register('urlImagen')}
          error={errors.urlImagen?.message}
          placeholder="https://..."
        />
      </div>

      {/* Fila 3: Categoría + Marca + Unidad */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Select
          label="Categoría"
          placeholder="Seleccionar..."
          options={categoriaOptions}
          {...register('categoriaId')}
          error={errors.categoriaId?.message}
        />
        <Select
          label="Marca"
          placeholder="Seleccionar..."
          options={marcaOptions}
          {...register('marcaId')}
          error={errors.marcaId?.message}
        />
        <Select
          label="Unidad de medida"
          placeholder="Seleccionar..."
          options={unidadOptions}
          {...register('unidadMedidaId')}
          error={errors.unidadMedidaId?.message}
        />
      </div>

      {/* Fila 4: Precios */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Input
          label="Precio compra (S/)"
          type="number"
          step="0.01"
          {...register('precioCompra')}
          error={errors.precioCompra?.message}
        />
        <Input
          label="Precio venta (S/)"
          required
          type="number"
          step="0.01"
          {...register('precioVenta')}
          error={errors.precioVenta?.message}
        />
        <Input
          label="Precio mínimo (S/)"
          type="number"
          step="0.01"
          {...register('precioVentaMinimo')}
          error={errors.precioVentaMinimo?.message}
        />
        <Input
          label="Precio mayorista (S/)"
          type="number"
          step="0.01"
          {...register('precioMayorista')}
          error={errors.precioMayorista?.message}
        />
      </div>

      {/* Fila 5: Grado + Impuesto */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Input
          label="Grado alcohólico (%)"
          type="number"
          step="0.01"
          {...register('gradoAlcoholico')}
          error={errors.gradoAlcoholico?.message}
          placeholder="Ej: 40.00"
        />
        <Input
          label="Tasa impuesto (%)"
          type="number"
          step="0.01"
          {...register('tasaImpuesto')}
          error={errors.tasaImpuesto?.message}
        />
        <div className="flex flex-col justify-end gap-2">
          <label className="flex items-center gap-2 text-sm text-gray-700">
            <input type="checkbox" {...register('impuestoIncluido')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
            Impuesto incluido en precio
          </label>
          <label className="flex items-center gap-2 text-sm text-gray-700">
            <input type="checkbox" {...register('permiteDescuento')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
            Permite descuento
          </label>
        </div>
      </div>

      {/* Descripción */}
      <Textarea
        label="Descripción"
        {...register('descripcion')}
        error={errors.descripcion?.message}
        placeholder="Descripción del producto..."
      />

      {/* Botones */}
      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear producto'}
        </Button>
      </div>
    </form>
  );
};
