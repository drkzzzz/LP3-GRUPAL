/**
 * MetodoPagoForm.jsx
 * ──────────────────
 * Formulario para crear/editar métodos de pago.
 * - Al crear: combo con métodos predefinidos; si elige "Otro" puede escribir nombre.
 * - Al editar: formulario clásico.
 */
import { useEffect, useState, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { metodoPagoSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';

/* ─── Métodos de pago predefinidos ─── */
const METODOS_PREDEFINIDOS = [
  { key: 'efectivo',               nombre: 'Efectivo',               codigo: 'efectivo',               tipo: 'efectivo' },
  { key: 'tarjeta_credito',        nombre: 'Tarjeta de Crédito',     codigo: 'tarjeta_credito',        tipo: 'tarjeta_credito' },
  { key: 'tarjeta_debito',         nombre: 'Tarjeta de Débito',      codigo: 'tarjeta_debito',         tipo: 'tarjeta_debito' },
  { key: 'transferencia_bancaria', nombre: 'Transferencia Bancaria', codigo: 'transferencia_bancaria', tipo: 'transferencia_bancaria' },
  { key: 'yape',                   nombre: 'Yape',                   codigo: 'yape',                   tipo: 'billetera_digital' },
  { key: 'plin',                   nombre: 'Plin',                   codigo: 'plin',                   tipo: 'billetera_digital' },
  { key: 'qr',                     nombre: 'QR',                     codigo: 'qr',                     tipo: 'qr' },
];

const TIPO_OPTIONS = [
  { value: 'efectivo', label: 'Efectivo' },
  { value: 'tarjeta_credito', label: 'Tarjeta de Crédito' },
  { value: 'tarjeta_debito', label: 'Tarjeta de Débito' },
  { value: 'transferencia_bancaria', label: 'Transferencia Bancaria' },
  { value: 'billetera_digital', label: 'Billetera Digital' },
  { value: 'yape', label: 'Yape' },
  { value: 'plin', label: 'Plin' },
  { value: 'qr', label: 'QR' },
  { value: 'otro', label: 'Otro' },
];

/** Genera un código slug a partir de un nombre libre */
const slugify = (text) =>
  text
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '_')
    .replace(/^_|_$/g, '');

export const MetodoPagoForm = ({ initialData, onSubmit, onCancel, isLoading, existingCodigos = [], existingMetodos = [] }) => {
  const isEditing = !!initialData?.id;

  /* ─── Estado del selector (solo para creación) ─── */
  const [selectedPreset, setSelectedPreset] = useState('');

  const isOtro = selectedPreset === 'otro';
  const presetData = useMemo(
    () => METODOS_PREDEFINIDOS.find((m) => m.key === selectedPreset),
    [selectedPreset],
  );

  /* ─── Opciones del combo filtrando los ya existentes ─── */
  const comboOptions = useMemo(() => {
    const existingSet = new Set(existingCodigos.map((c) => c.toLowerCase()));
    const options = METODOS_PREDEFINIDOS
      .filter((m) => !existingSet.has(m.codigo))
      .map((m) => ({ value: m.key, label: m.nombre }));
    options.push({ value: 'otro', label: 'Otro (personalizado)' });
    return options;
  }, [existingCodigos]);

  /* ─── React Hook Form ─── */
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(metodoPagoSchema),
    defaultValues: {
      nombre: '',
      codigo: '',
      tipo: 'efectivo',
      disponiblePos: true,
      disponibleTiendaOnline: false,
      orden: 0,
    },
  });

  /* ─── Orden helpers ─── */
  const nextOrden = useMemo(() => {
    if (!existingMetodos.length) return 0;
    const max = Math.max(...existingMetodos.map((m) => m.orden ?? 0));
    return max + 1;
  }, [existingMetodos]);

  const ordenValue = watch('orden');
  const ordenConflict = useMemo(() => {
    if (ordenValue === undefined || ordenValue === null || ordenValue === '') return null;
    const num = Number(ordenValue);
    const afectados = existingMetodos.filter(
      (m) => m.orden >= num && (!isEditing || m.id !== initialData?.id),
    );
    return afectados.length > 0 ? afectados : null;
  }, [ordenValue, existingMetodos, isEditing, initialData]);

  /* Rellenar campos cuando se elige un preset */
  useEffect(() => {
    if (isEditing) return;
    if (presetData) {
      setValue('nombre', presetData.nombre);
      setValue('codigo', presetData.codigo);
      setValue('tipo', presetData.tipo);
      setValue('orden', nextOrden);
    } else if (isOtro) {
      setValue('nombre', '');
      setValue('codigo', '');
      setValue('tipo', 'otro');
      setValue('orden', nextOrden);
    }
  }, [presetData, isOtro, isEditing, setValue, nextOrden]);

  /* Auto-generar código cuando se escribe nombre personalizado */
  const nombreValue = watch('nombre');
  useEffect(() => {
    if (!isEditing && isOtro && nombreValue) {
      setValue('codigo', slugify(nombreValue));
    }
  }, [nombreValue, isOtro, isEditing, setValue]);

  /* Cargar datos al editar */
  useEffect(() => {
    if (initialData) {
      reset({
        nombre: initialData.nombre || '',
        codigo: initialData.codigo || '',
        tipo: initialData.tipo || 'efectivo',
        disponiblePos: initialData.disponiblePos ?? true,
        disponibleTiendaOnline: initialData.disponibleTiendaOnline ?? false,
        orden: initialData.orden ?? 0,
      });
    }
  }, [initialData, reset]);

  const handleFormSubmit = (data) => {
    onSubmit(data);
  };

  /* ─── Render ─── */
  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-5">

      {/* ── Selector de método predefinido (solo al crear) ── */}
      {!isEditing && (
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Método de pago <span className="text-red-500">*</span>
          </label>
          <select
            value={selectedPreset}
            onChange={(e) => setSelectedPreset(e.target.value)}
            className="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm focus:border-green-500 focus:ring-1 focus:ring-green-500 outline-none bg-white"
          >
            <option value="" disabled>Seleccione un método de pago</option>
            {comboOptions.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </div>
      )}

      {/* ── Campo nombre personalizado (solo si eligió "Otro") ── */}
      {!isEditing && isOtro && (
        <Input
          label="Nombre del método de pago"
          required
          {...register('nombre')}
          error={errors.nombre?.message}
          placeholder="Ej: Billetera MiPago"
        />
      )}

      {/* ── Campos visibles al editar ── */}
      {isEditing && (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Nombre"
              required
              {...register('nombre')}
              error={errors.nombre?.message}
              placeholder="Efectivo"
            />
            <Input
              label="Código"
              required
              {...register('codigo')}
              error={errors.codigo?.message}
              placeholder="efectivo_sol"
              disabled
            />
          </div>
          <Select
            label="Tipo"
            required
            options={TIPO_OPTIONS}
            {...register('tipo')}
            error={errors.tipo?.message}
          />
        </>
      )}

      {/* ── Disponible en (checkboxes) ── */}
      {(isEditing || selectedPreset) && (
        <div>
          <p className="text-sm font-medium text-gray-700 mb-2">Disponible en</p>
          <div className="flex items-center gap-6">
            <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
              <input
                type="checkbox"
                {...register('disponiblePos')}
                className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
              />
              POS (tienda física)
            </label>
            <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
              <input
                type="checkbox"
                {...register('disponibleTiendaOnline')}
                className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
              />
              Tienda online
            </label>
          </div>
        </div>
      )}

      {/* ── Orden de aparición ── */}
      {(isEditing || selectedPreset) && (
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Orden de aparición</label>
          <input
            type="number"
            {...register('orden', { valueAsNumber: true })}
            className="w-28 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-green-500 focus:ring-1 focus:ring-green-500 outline-none"
            placeholder="0"
            min={0}
          />
          {errors.orden?.message && (
            <p className="text-xs text-red-500 mt-1">{errors.orden.message}</p>
          )}
          <p className="text-xs text-gray-400 mt-1">Menor número = aparece primero</p>
        </div>
      )}

      {/* ── Botones ── */}
      <div className="flex justify-end gap-3 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button
          type="submit"
          disabled={isLoading || (!isEditing && !selectedPreset)}
        >
          {isLoading ? 'Guardando...' : isEditing ? 'Guardar' : 'Guardar'}
        </Button>
      </div>
    </form>
  );
};
