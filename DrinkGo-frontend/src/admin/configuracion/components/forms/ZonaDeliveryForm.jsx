/**
 * ZonaDeliveryForm.jsx
 * ────────────────────
 * Formulario para crear/editar zonas de delivery con múltiples distritos.
 */
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';
import { MultiDistritoInput } from './MultiDistritoInput';
import { CalculadoraMontoMinimo } from './CalculadoraMontoMinimo';

// Schema actualizado con distritos
const zonaDeliverySchema = z.object({
  nombre: z.string().min(1, 'El nombre es requerido'),
  descripcion: z.string().optional(),
  sedeId: z.string().min(1, 'Debe seleccionar una sede'),
  tarifaDelivery: z.number().min(0, 'La tarifa debe ser mayor o igual a 0'),
  montoMinimoPedido: z.number().min(0, 'El monto mínimo debe ser mayor o igual a 0'),
});

export const ZonaDeliveryForm = ({ initialData, sedesOptions = [], onSubmit, onCancel, isLoading }) => {
  const isEditing = !!initialData?.id;

  // Estado para distritos (fuera de react-hook-form)
  const [distritos, setDistritos] = useState([]);
  const [distritosError, setDistritosError] = useState('');

  // Buscar sede principal - CORREGIDO
  const sedeDefaultId = sedesOptions.length > 0 ? sedesOptions[0].value : '';

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(zonaDeliverySchema),
    defaultValues: {
      nombre: '',
      descripcion: '',
      sedeId: sedeDefaultId, // ✅ Sede predeterminada
      tarifaDelivery: 0,
      montoMinimoPedido: 0,
    },
  });

  // Observar cambios en tarifa para calculadora
  const tarifaActual = watch('tarifaDelivery');

  const handleAplicarMontoSugerido = (montoSugerido) => {
    setValue('montoMinimoPedido', montoSugerido, { shouldValidate: true });
  };

  useEffect(() => {
    if (initialData) {
      // Modo edición
      reset({
        nombre: initialData.nombre || '',
        descripcion: initialData.descripcion || '',
        sedeId: String(initialData.sede?.id || initialData.sedeId || ''),
        tarifaDelivery: initialData.tarifaDelivery ?? 0,
        montoMinimoPedido: initialData.montoMinimoPedido ?? 0,
      });
      
      // Parsear distritos desde JSON string
      try {
        const distritosArray = initialData.distritos 
          ? JSON.parse(initialData.distritos)
          : [];
        setDistritos(Array.isArray(distritosArray) ? distritosArray : []);
      } catch (e) {
        console.error('Error parseando distritos:', e);
        setDistritos([]);
      }
    } else {
      // Modo creación - asignar sede principal
      reset({
        nombre: '',
        descripcion: '',
        sedeId: sedeDefaultId,
        tarifaDelivery: 0,
        montoMinimoPedido: 0,
      });
      setDistritos([]);
    }
  }, [initialData, reset, sedeDefaultId]);

  const handleFormSubmit = (data) => {
    // Validar que haya al menos un distrito
    if (distritos.length === 0) {
      setDistritosError('Debe agregar al menos un distrito');
      return;
    }

    setDistritosError('');

    const payload = {
      ...data,
      sedeId: Number(data.sedeId),
      distritos: JSON.stringify(distritos), // Convertir a JSON string para backend
    };

    // Debug: log para verificar datos antes de enviar
    console.log('📤 Guardando zona delivery:', {
      ...payload,
      distritosArray: distritos, // Ver el array original
      distritosJSON: payload.distritos, // Ver el JSON string
    });

    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <Input
        label="Nombre de la zona"
        required
        {...register('nombre')}
        error={errors.nombre?.message}
        placeholder="Zona Centro - Lima"
      />

      <Select
        label="Sede"
        required
        options={sedesOptions}
        placeholder="Seleccione una sede"
        {...register('sedeId')}
        error={errors.sedeId?.message}
      />

      {/* Componente de múltiples distritos */}
      <MultiDistritoInput
        value={distritos}
        onChange={setDistritos}
        error={distritosError}
        placeholder="Escribe un distrito (ej: Surco, Miraflores...)"
      />

      <Textarea
        label="Descripción (opcional)"
        {...register('descripcion')}
        error={errors.descripcion?.message}
        placeholder="Información adicional sobre la zona de cobertura..."
        rows={2}
      />

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Input
          label="Tarifa de Delivery (S/)"
          required
          type="number"
          step="0.01"
          {...register('tarifaDelivery', { valueAsNumber: true })}
          error={errors.tarifaDelivery?.message}
          placeholder="9.00"
        />
        <Input
          label="Monto Mínimo de Pedido (S/)"
          type="number"
          step="0.01"
          {...register('montoMinimoPedido', { valueAsNumber: true })}
          error={errors.montoMinimoPedido?.message}
          placeholder="30.00"
        />
      </div>

      {/* Calculadora inteligente de monto mínimo */}
      <CalculadoraMontoMinimo
        tarifaDelivery={tarifaActual || 0}
        onSugerirMonto={handleAplicarMontoSugerido}
      />

      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEditing ? 'Actualizar Zona' : 'Crear Zona'}
        </Button>
      </div>
    </form>
  );
};
