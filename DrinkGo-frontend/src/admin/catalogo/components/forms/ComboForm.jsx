/**
 * ComboForm.jsx
 * ─────────────
 * Formulario para crear/editar un Combo promocional.
 * Incluye sección de productos del combo (Producto, Cantidad, Precio Unitario, Subtotal).
 */
import { useState, useMemo, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { comboSchema } from '../../validations/catalogoSchemas';
import { Plus, Trash2, Package, ShoppingCart } from 'lucide-react';
import { formatCurrency } from '@/shared/utils/formatters';
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
  initialDetalles = [],
  productos = [],
  negocioId,
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const isEdit = !!initialData?.id;

  /* ─── React Hook Form ─── */
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

  /* ─── Detalles (productos del combo) ─── */
  const [detalles, setDetalles] = useState(() =>
    initialDetalles.map((d) => ({
      _key: d.id || crypto.randomUUID(),
      id: d.id || null,
      productoId: d.producto?.id ?? d.productoId ?? '',
      cantidad: d.cantidad ?? 1,
      precioUnitario: d.precioUnitario ?? 0,
    })),
  );

  const addDetalle = useCallback(() => {
    setDetalles((prev) => [
      ...prev,
      { _key: crypto.randomUUID(), id: null, productoId: '', cantidad: 1, precioUnitario: 0 },
    ]);
  }, []);

  const removeDetalle = useCallback((key) => {
    setDetalles((prev) => prev.filter((d) => d._key !== key));
  }, []);

  const updateDetalle = useCallback((key, field, value) => {
    setDetalles((prev) =>
      prev.map((d) => (d._key === key ? { ...d, [field]: value } : d)),
    );
  }, []);

  const handleProductoChange = useCallback(
    (key, productoId) => {
      const producto = productos.find((p) => p.id === Number(productoId));
      setDetalles((prev) =>
        prev.map((d) =>
          d._key === key
            ? {
                ...d,
                productoId: Number(productoId) || '',
                precioUnitario: 0,
              }
            : d,
        ),
      );
    },
    [productos],
  );

  /* IDs de productos ya seleccionados (para excluirlos del dropdown) */
  const usedProductIds = useMemo(
    () => new Set(detalles.map((d) => Number(d.productoId)).filter(Boolean)),
    [detalles],
  );

  /* Totales */
  const totalRegular = useMemo(
    () => detalles.reduce((sum, d) => sum + (d.precioUnitario ?? 0) * (d.cantidad ?? 0), 0),
    [detalles],
  );

  /* ─── Slug ─── */
  const nombre = watch('nombre');
  const handleNombreBlur = () => {
    const currentSlug = watch('slug');
    if (!currentSlug || !isEdit) {
      setValue('slug', generateSlug(nombre));
    }
  };

  /* ─── Submit ─── */
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
      detalles: detalles
        .filter((d) => d.productoId)
        .map((d) => ({
          ...(d.id && { id: d.id }),
          productoId: Number(d.productoId),
          cantidad: Number(d.cantidad) || 1,
          precioUnitario: Number(d.precioUnitario) || 0,
        })),
    };
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-5">
      {/* ─── Datos generales ─── */}
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

      {/* ─── Productos del combo ─── */}
      <div className="border-t pt-5">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <ShoppingCart size={18} className="text-pink-600" />
            <h3 className="text-sm font-semibold text-gray-800">Productos del combo</h3>
          </div>
          <button
            type="button"
            onClick={addDetalle}
            className="flex items-center gap-1 text-sm text-green-600 hover:text-green-700 font-medium transition-colors"
          >
            <Plus size={16} />
            Agregar producto
          </button>
        </div>

        {detalles.length === 0 ? (
          <div className="border-2 border-dashed border-gray-200 rounded-lg py-8 text-center">
            <Package size={32} className="mx-auto text-gray-300 mb-2" />
            <p className="text-sm text-gray-400">No hay productos en este combo</p>
            <button
              type="button"
              onClick={addDetalle}
              className="mt-2 text-sm text-green-600 hover:text-green-700 font-medium"
            >
              + Agregar primer producto
            </button>
          </div>
        ) : (
          <div className="space-y-0">
            {/* Encabezado tabla */}
            <div className="grid grid-cols-12 gap-2 px-3 py-2 bg-gray-50 rounded-t-lg text-xs font-medium text-gray-500 uppercase tracking-wider">
              <div className="col-span-5">Producto</div>
              <div className="col-span-2 text-center">Cantidad</div>
              <div className="col-span-2 text-right">Precio Unit.</div>
              <div className="col-span-2 text-right">Subtotal</div>
              <div className="col-span-1" />
            </div>

            {/* Filas */}
            <div className="border border-gray-200 rounded-b-lg divide-y divide-gray-100">
              {detalles.map((det) => {
                const subtotal = (det.precioUnitario ?? 0) * (det.cantidad ?? 0);
                return (
                  <div key={det._key} className="grid grid-cols-12 gap-2 px-3 py-2.5 items-center">
                    {/* Producto */}
                    <div className="col-span-5">
                      <select
                        value={det.productoId}
                        onChange={(e) => handleProductoChange(det._key, e.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-2.5 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                      >
                        <option value="">Seleccionar producto...</option>
                        {productos.map((p) => (
                          <option
                            key={p.id}
                            value={p.id}
                            disabled={usedProductIds.has(p.id) && Number(det.productoId) !== p.id}
                          >
                            {p.nombre}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Cantidad */}
                    <div className="col-span-2">
                      <input
                        type="number"
                        min="1"
                        value={det.cantidad}
                        onChange={(e) => updateDetalle(det._key, 'cantidad', Number(e.target.value) || 1)}
                        className="w-full border border-gray-300 rounded-lg px-2.5 py-1.5 text-sm text-center focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                    </div>

                    {/* Precio Unitario */}
                    <div className="col-span-2">
                      <input
                        type="number"
                        min="0"
                        step="0.01"
                        value={det.precioUnitario}
                        onChange={(e) => updateDetalle(det._key, 'precioUnitario', Number(e.target.value) || 0)}
                        className="w-full border border-gray-300 rounded-lg px-2.5 py-1.5 text-sm text-right focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                    </div>

                    {/* Subtotal */}
                    <div className="col-span-2 text-right">
                      <span className="text-sm font-medium text-gray-700">
                        {formatCurrency(subtotal)}
                      </span>
                    </div>

                    {/* Eliminar */}
                    <div className="col-span-1 flex justify-center">
                      <button
                        type="button"
                        onClick={() => removeDetalle(det._key)}
                        className="p-1 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
                        title="Quitar producto"
                      >
                        <Trash2 size={15} />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Total precio regular calculado */}
            <div className="flex justify-end mt-2 pr-14">
              <div className="text-sm text-gray-500">
                Suma productos:{' '}
                <span className="font-semibold text-gray-800">{formatCurrency(totalRegular)}</span>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* ─── Acciones ─── */}
      <div className="flex justify-end gap-2 pt-2 border-t">
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
