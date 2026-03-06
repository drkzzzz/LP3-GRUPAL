/**
 * TransferenciaForm.jsx
 * ─────────────────────
 * Formulario para registrar una transferencia entre almacenes.
 * Crea un movimiento de tipo "transferencia" con almacenOrigen y almacenDestino.
 */
import { useMemo } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { transferenciaSchema } from '../../validations/inventarioSchemas';
import { message } from '@/shared/utils/notifications';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

export const TransferenciaForm = ({
  productos = [],
  almacenes = [],
  stock = [], // NUEVO: recibir stock disponible
  negocioId,
  userId,
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const {
    register,
    handleSubmit,
    control,
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

  // Observar valores seleccionados en tiempo real
  const watchedProductoId = useWatch({ control, name: 'productoId' });
  const watchedAlmacenOrigenId = useWatch({ control, name: 'almacenOrigenId' });
  const watchedCantidad = useWatch({ control, name: 'cantidad' });

  // Buscar stock disponible en el almacén origen para el producto seleccionado
  const stockDisponible = useMemo(() => {
    if (!watchedProductoId || !watchedAlmacenOrigenId) return null;
    
    const stockEncontrado = stock.find(
      (s) => 
        String(s.producto?.id) === String(watchedProductoId) && 
        String(s.almacen?.id) === String(watchedAlmacenOrigenId)
    );
    
    return stockEncontrado ? Number(stockEncontrado.cantidadActual || 0) : 0;
  }, [stock, watchedProductoId, watchedAlmacenOrigenId]);

  /* Preparar payload para el backend (MovimientosInventario) */
  const handleFormSubmit = (formData) => {
    const cantidadATransferir = Number(formData.cantidad);
    
    // VALIDACIÓN: Verificar que haya suficiente stock en el origen
    if (stockDisponible !== null && cantidadATransferir > stockDisponible) {
      message.error(
        `Stock insuficiente. Disponible en origen: ${stockDisponible} unidades. No puedes transferir ${cantidadATransferir} unidades.`
      );
      return;
    }

    // Si el stock es 0 y se intenta transferir
    if (stockDisponible === 0) {
      message.error('No hay stock disponible en el almacén de origen para este producto.');
      return;
    }

    const payload = {
      negocio: { id: negocioId },
      producto: { id: Number(formData.productoId) },
      almacenOrigen: { id: Number(formData.almacenOrigenId) },
      almacenDestino: { id: Number(formData.almacenDestinoId) },
      tipoMovimiento: 'transferencia',
      cantidad: cantidadATransferir,
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
    label: a.nombre,
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
      <div>
        <Input
          label="Cantidad a transferir"
          required
          type="number"
          step="0.01"
          min="0"
          max={stockDisponible !== null ? stockDisponible : undefined}
          {...register('cantidad')}
          error={errors.cantidad?.message}
          placeholder="Ej: 50"
        />
        
        {/* Indicador de stock disponible */}
        {stockDisponible !== null && watchedProductoId && watchedAlmacenOrigenId && (
          <div className={`mt-2 p-3 rounded-lg border ${
            stockDisponible === 0 
              ? 'bg-red-50 border-red-200' 
              : Number(watchedCantidad) > stockDisponible
              ? 'bg-orange-50 border-orange-200'
              : 'bg-blue-50 border-blue-200'
          }`}>
            <div className="flex items-center gap-2">
              <span className={`text-sm font-medium ${
                stockDisponible === 0 
                  ? 'text-red-700' 
                  : Number(watchedCantidad) > stockDisponible
                  ? 'text-orange-700'
                  : 'text-blue-700'
              }`}>
                {stockDisponible === 0 ? '⚠️' : '📦'} Stock disponible en origen: 
                <span className="font-bold ml-1">{stockDisponible} unidades</span>
              </span>
            </div>
            {Number(watchedCantidad) > stockDisponible && stockDisponible > 0 && (
              <p className="text-xs text-orange-600 mt-1">
                ⚠️ La cantidad ingresada ({watchedCantidad}) excede el stock disponible
              </p>
            )}
            {stockDisponible === 0 && (
              <p className="text-xs text-red-600 mt-1">
                ❌ No hay stock disponible en este almacén para transferir
              </p>
            )}
          </div>
        )}
      </div>

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
