/**
 * AjusteInventarioForm.jsx
 * ────────────────────────
 * Formulario para registrar un ajuste manual de inventario.
 * Crea un movimiento de tipo ajuste_positivo o ajuste_negativo.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ajusteInventarioSchema } from '../../validations/inventarioSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

const TIPOS_AJUSTE = [
  { value: 'ajuste_positivo', label: 'Ajuste positivo (entrada)' },
  { value: 'ajuste_negativo', label: 'Ajuste negativo (salida)' },
  { value: 'merma', label: 'Merma' },
  { value: 'devolucion', label: 'Devolución' },
];

const MOTIVOS = [
  { value: 'error_conteo', label: 'Error de conteo' },
  { value: 'merma', label: 'Merma / deterioro' },
  { value: 'robo', label: 'Robo / pérdida' },
  { value: 'muestra', label: 'Muestra / degustación' },
  { value: 'donacion', label: 'Donación' },
  { value: 'vencimiento', label: 'Producto vencido' },
  { value: 'rotura', label: 'Rotura / daño' },
  { value: 'inventario_fisico', label: 'Ajuste por inventario físico' },
  { value: 'otro', label: 'Otro' },
];

export const AjusteInventarioForm = ({
  productos = [],
  almacenes = [],
  negocioId,
  userId,
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(ajusteInventarioSchema),
    defaultValues: {
      productoId: '',
      almacenId: '',
      tipoAjuste: '',
      cantidad: '',
      motivoMovimiento: '',
      referenciaDocumento: '',
    },
  });

  /* Preparar payload para el backend (entidad MovimientosInventario) */
  const handleFormSubmit = (formData) => {
    const isPositive = formData.tipoAjuste === 'ajuste_positivo' || formData.tipoAjuste === 'devolucion';

    const payload = {
      negocio: { id: negocioId },
      producto: { id: Number(formData.productoId) },
      tipoMovimiento: formData.tipoAjuste,
      cantidad: Number(formData.cantidad),
      costoUnitario: 0,
      montoTotal: 0,
      motivoMovimiento: MOTIVOS.find((m) => m.value === formData.motivoMovimiento)?.label || formData.motivoMovimiento,
      referenciaDocumento: formData.referenciaDocumento || null,
      estaActivo: true,
      ...(userId && { usuario: { id: userId } }),
    };

    /* Asignar almacén según tipo */
    if (isPositive) {
      payload.almacenDestino = { id: Number(formData.almacenId) };
      payload.almacenOrigen = null;
    } else {
      payload.almacenOrigen = { id: Number(formData.almacenId) };
      payload.almacenDestino = null;
    }

    onSubmit(payload);
  };

  /* Opciones para selects */
  const productoOptions = productos.map((p) => ({
    value: String(p.id),
    label: `${p.nombre} (SKU: ${p.sku || '—'})`,
  }));
  const almacenOptions = almacenes.map((a) => ({
    value: String(a.id),
    label: `${a.nombre} ${a.esPredeterminado ? '(Principal)' : ''}`,
  }));

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      {/* Producto y Tipo de ajuste */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Select
          label="Producto"
          required
          placeholder="Seleccionar producto..."
          options={productoOptions}
          {...register('productoId')}
          error={errors.productoId?.message}
        />
        <Select
          label="Tipo de ajuste"
          required
          placeholder="Seleccionar tipo..."
          options={TIPOS_AJUSTE}
          {...register('tipoAjuste')}
          error={errors.tipoAjuste?.message}
        />
      </div>

      {/* Almacén y Cantidad */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Select
          label="Almacén"
          required
          placeholder="Seleccionar almacén..."
          options={almacenOptions}
          {...register('almacenId')}
          error={errors.almacenId?.message}
        />
        <Input
          label="Cantidad"
          required
          type="number"
          step="0.01"
          min="0"
          {...register('cantidad')}
          error={errors.cantidad?.message}
          placeholder="Ej: 10"
        />
      </div>

      {/* Motivo y Referencia */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Select
          label="Motivo"
          required
          placeholder="Seleccionar motivo..."
          options={MOTIVOS}
          {...register('motivoMovimiento')}
          error={errors.motivoMovimiento?.message}
        />
        <Input
          label="Documento de referencia"
          {...register('referenciaDocumento')}
          error={errors.referenciaDocumento?.message}
          placeholder="Ej: ACTA-001"
        />
      </div>

      {/* Botones */}
      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Registrando...' : 'Registrar Ajuste'}
        </Button>
      </div>
    </form>
  );
};
