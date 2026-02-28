/**
 * TransferenciaForm.jsx
 * ─────────────────────
 * Formulario para registrar una transferencia entre almacenes.
 * Crea un movimiento de tipo "transferencia" con almacenOrigen y almacenDestino.
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { transferenciaSchema } from '../../validations/inventarioSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

export const TransferenciaForm = ({
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
    resolver: zodResolver(transferenciaSchema),
    defaultValues: {
      productoId: '',
      almacenOrigenId: '',
      almacenDestinoId: '',
      cantidad: '',
      motivoMovimiento: '',
    },
  });

  /* Preparar payload para el backend (MovimientosInventario) */
  const handleFormSubmit = (formData) => {
    const payload = {
      negocio: { id: negocioId },
      producto: { id: Number(formData.productoId) },
      almacenOrigen: { id: Number(formData.almacenOrigenId) },
      almacenDestino: { id: Number(formData.almacenDestinoId) },
      tipoMovimiento: 'transferencia',
      cantidad: Number(formData.cantidad),
      costoUnitario: 0,
      montoTotal: 0,
      motivoMovimiento: formData.motivoMovimiento || 'Transferencia entre almacenes',
      referenciaDocumento: null,
      estaActivo: true,
      ...(userId && { usuario: { id: userId } }),
    };
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
      {/* Producto */}
      <Select
        label="Producto"
        required
        placeholder="Seleccionar producto..."
        options={productoOptions}
        {...register('productoId')}
        error={errors.productoId?.message}
      />

      {/* Almacenes */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Select
          label="Almacén de origen"
          required
          placeholder="Seleccionar origen..."
          options={almacenOptions}
          {...register('almacenOrigenId')}
          error={errors.almacenOrigenId?.message}
        />
        <Select
          label="Almacén de destino"
          required
          placeholder="Seleccionar destino..."
          options={almacenOptions}
          {...register('almacenDestinoId')}
          error={errors.almacenDestinoId?.message}
        />
      </div>

      {/* Cantidad */}
      <Input
        label="Cantidad a transferir"
        required
        type="number"
        step="0.01"
        min="0"
        {...register('cantidad')}
        error={errors.cantidad?.message}
        placeholder="Ej: 50"
      />

      {/* Motivo */}
      <Textarea
        label="Motivo de transferencia"
        {...register('motivoMovimiento')}
        error={errors.motivoMovimiento?.message}
        placeholder="Motivo o detalle de la transferencia..."
      />

      {/* Botones */}
      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Transfiriendo...' : 'Registrar Transferencia'}
        </Button>
      </div>
    </form>
  );
};
