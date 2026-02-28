/**
 * LoteEntradaForm.jsx
 * ───────────────────
 * Formulario para registrar la entrada de un lote de mercancía.
 * Usa React Hook Form + Zod. Crea un registro de lote + movimiento tipo "entrada".
 */
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loteEntradaSchema } from '../../validations/inventarioSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

export const LoteEntradaForm = ({
  productos = [],
  almacenes = [],
  negocioId,
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(loteEntradaSchema),
    defaultValues: {
      productoId: '',
      almacenId: '',
      numeroLote: '',
      cantidadInicial: '',
      costoUnitario: '',
      fechaVencimiento: '',
      observaciones: '',
    },
  });

  /* Preparar payload para el backend */
  const handleFormSubmit = (formData) => {
    const payload = {
      negocio: { id: negocioId },
      producto: { id: Number(formData.productoId) },
      almacen: { id: Number(formData.almacenId) },
      numeroLote: formData.numeroLote,
      cantidadInicial: Number(formData.cantidadInicial),
      cantidadActual: Number(formData.cantidadInicial),
      costoUnitario: Number(formData.costoUnitario),
      fechaIngreso: new Date().toISOString().split('T')[0],
      fechaVencimiento: formData.fechaVencimiento || null,
      estaActivo: true,
      observaciones: formData.observaciones || null,
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
      {/* Producto y Almacén */}
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
          label="Almacén destino"
          required
          placeholder="Seleccionar almacén..."
          options={almacenOptions}
          {...register('almacenId')}
          error={errors.almacenId?.message}
        />
      </div>

      {/* Número de lote y Cantidad */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Input
          label="Número de lote"
          required
          {...register('numeroLote')}
          error={errors.numeroLote?.message}
          placeholder="Ej: LOT-2025-001"
        />
        <Input
          label="Cantidad recibida"
          required
          type="number"
          step="0.01"
          min="0"
          {...register('cantidadInicial')}
          error={errors.cantidadInicial?.message}
          placeholder="Ej: 100"
        />
        <Input
          label="Costo unitario (S/)"
          required
          type="number"
          step="0.01"
          min="0"
          {...register('costoUnitario')}
          error={errors.costoUnitario?.message}
          placeholder="Ej: 25.50"
        />
      </div>

      {/* Fecha de vencimiento */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Fecha de vencimiento"
          type="date"
          {...register('fechaVencimiento')}
          error={errors.fechaVencimiento?.message}
        />
      </div>

      {/* Observaciones */}
      <Textarea
        label="Observaciones"
        {...register('observaciones')}
        error={errors.observaciones?.message}
        placeholder="Observaciones adicionales sobre la recepción..."
      />

      {/* Botones */}
      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Registrando...' : 'Registrar Entrada'}
        </Button>
      </div>
    </form>
  );
};
