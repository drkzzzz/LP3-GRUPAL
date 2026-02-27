/**
 * PromocionForm.jsx
 * ─────────────────
 * Formulario para crear/editar una Promoción / Descuento.
 * En el backend, los descuentos se modelan como Promociones con
 * tipoDescuento = 'porcentaje' | 'monto_fijo'.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { promocionSchema } from '../../validations/catalogoSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';

const TIPO_DESCUENTO_OPTIONS = [
  { value: 'porcentaje', label: 'Porcentaje (%)' },
  { value: 'monto_fijo', label: 'Monto fijo (S/)' },
];

const APLICA_A_OPTIONS = [
  { value: 'todo', label: 'Todo el catálogo' },
  { value: 'categoria', label: 'Categoría específica' },
  { value: 'producto', label: 'Producto específico' },
];

export const PromocionForm = ({
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
    resolver: zodResolver(promocionSchema),
    defaultValues: {
      nombre: initialData?.nombre || '',
      codigo: initialData?.codigo || '',
      tipoDescuento: initialData?.tipoDescuento || '',
      valorDescuento: String(initialData?.valorDescuento ?? ''),
      montoMinimoCompra: String(initialData?.montoMinimoCompra ?? ''),
      maxUsos: String(initialData?.maxUsos ?? ''),
      aplicaA: initialData?.aplicaA || 'todo',
      validoDesde: initialData?.validoDesde
        ? initialData.validoDesde.substring(0, 16) // formato datetime-local
        : '',
      validoHasta: initialData?.validoHasta
        ? initialData.validoHasta.substring(0, 16)
        : '',
      estaActivo: initialData?.estaActivo ?? true,
    },
  });

  const handleFormSubmit = (formData) => {
    const payload = {
      ...(isEdit && { id: initialData.id }),
      negocio: { id: negocioId },
      nombre: formData.nombre,
      codigo: formData.codigo || null,
      tipoDescuento: formData.tipoDescuento,
      valorDescuento: Number(formData.valorDescuento),
      montoMinimoCompra: formData.montoMinimoCompra ? Number(formData.montoMinimoCompra) : null,
      maxUsos: formData.maxUsos ? Number(formData.maxUsos) : null,
      usosActuales: initialData?.usosActuales ?? 0,
      aplicaA: formData.aplicaA,
      validoDesde: formData.validoDesde,
      validoHasta: formData.validoHasta,
      estaActivo: formData.estaActivo,
    };
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Nombre de la promoción"
          required
          {...register('nombre')}
          error={errors.nombre?.message}
          placeholder="Ej: Descuento de verano"
        />
        <Input
          label="Código promocional"
          {...register('codigo')}
          error={errors.codigo?.message}
          placeholder="Ej: VERANO2026"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Select
          label="Tipo de descuento"
          required
          placeholder="Seleccionar..."
          options={TIPO_DESCUENTO_OPTIONS}
          {...register('tipoDescuento')}
          error={errors.tipoDescuento?.message}
        />
        <Input
          label="Valor del descuento"
          required
          type="number"
          step="0.01"
          {...register('valorDescuento')}
          error={errors.valorDescuento?.message}
          placeholder="Ej: 15"
        />
        <Select
          label="Aplica a"
          required
          options={APLICA_A_OPTIONS}
          {...register('aplicaA')}
          error={errors.aplicaA?.message}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Monto mínimo de compra (S/)"
          type="number"
          step="0.01"
          {...register('montoMinimoCompra')}
          error={errors.montoMinimoCompra?.message}
          placeholder="Opcional"
        />
        <Input
          label="Máximo de usos"
          type="number"
          {...register('maxUsos')}
          error={errors.maxUsos?.message}
          placeholder="Sin límite si se deja vacío"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Válido desde"
          required
          type="datetime-local"
          {...register('validoDesde')}
          error={errors.validoDesde?.message}
        />
        <Input
          label="Válido hasta"
          required
          type="datetime-local"
          {...register('validoHasta')}
          error={errors.validoHasta?.message}
        />
      </div>

      <label className="flex items-center gap-2 text-sm text-gray-700">
        <input type="checkbox" {...register('estaActivo')} className="rounded border-gray-300 text-green-600 focus:ring-green-500" />
        Promoción activa
      </label>

      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear promoción'}
        </Button>
      </div>
    </form>
  );
};
