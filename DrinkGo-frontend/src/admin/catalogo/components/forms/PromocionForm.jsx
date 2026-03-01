/**
 * PromocionForm.jsx
 * ─────────────────
 * Formulario para crear/editar una Promoción / Descuento.
 * En el backend, los descuentos se modelan como Promociones con
 * tipoDescuento = 'porcentaje' | 'monto_fijo'.
 *
 * Cuando "Aplica a" es 'categoria' o 'producto', se muestra un
 * buscador tipo autocomplete (como en Punto de Venta) para
 * seleccionar la entidad.
 */
import { useState, useMemo, useRef, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { promocionSchema } from '../../validations/catalogoSchemas';
import { Search, Tag, Package, X } from 'lucide-react';
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

/**
 * Buscador autocomplete estilo POS.
 * Escribe → filtra → click para seleccionar → muestra chip.
 */
const EntitySearch = ({ label, required, items, selectedId, onSelect, onClear, placeholder, icon: Icon, error }) => {
  const [query, setQuery] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const wrapperRef = useRef(null);
  const inputRef = useRef(null);

  const selectedItem = useMemo(
    () => items.find((i) => String(i.id) === String(selectedId)),
    [items, selectedId],
  );

  const results = useMemo(() => {
    if (!query || query.length < 1) return [];
    const q = query.toLowerCase();
    return items.filter((i) => i.searchText.toLowerCase().includes(q));
  }, [items, query]);

  useEffect(() => {
    setIsOpen(results.length > 0 && query.length > 0);
  }, [results, query]);

  /* Cerrar al hacer click fuera */
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (item) => {
    onSelect(String(item.id));
    setQuery('');
    setIsOpen(false);
  };

  /* Si ya hay selección, mostrar chip en vez del buscador */
  if (selectedItem) {
    return (
      <div className="w-full">
        {label && (
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {label}
            {required && <span className="text-red-500 ml-0.5">*</span>}
          </label>
        )}
        <div className="flex items-center gap-2 border border-green-300 bg-green-50 rounded-lg px-3 py-2">
          <Icon size={16} className="text-green-600 shrink-0" />
          <span className="text-sm font-medium text-green-800 flex-1 truncate">{selectedItem.label}</span>
          <button
            type="button"
            onClick={() => { onClear(); inputRef.current?.focus(); }}
            className="p-0.5 rounded hover:bg-green-200 text-green-600 transition-colors"
          >
            <X size={14} />
          </button>
        </div>
      </div>
    );
  }

  return (
    <div ref={wrapperRef} className="relative w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-1">
          {label}
          {required && <span className="text-red-500 ml-0.5">*</span>}
        </label>
      )}
      <div className="relative">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder={placeholder}
          className="w-full pl-9 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
          autoComplete="off"
        />
      </div>

      {/* Dropdown de resultados */}
      {isOpen && (
        <div className="absolute z-30 top-full mt-1 w-full bg-white border border-gray-200 rounded-lg shadow-lg max-h-48 overflow-y-auto">
          {results.map((item) => (
            <button
              key={item.id}
              type="button"
              onClick={() => handleSelect(item)}
              className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-green-50 transition-colors text-left border-b border-gray-100 last:border-b-0"
            >
              <Icon size={16} className="text-gray-400 shrink-0" />
              <span className="text-sm text-gray-900 truncate">{item.label}</span>
              {item.extra && <span className="text-xs text-gray-500 shrink-0 ml-auto">{item.extra}</span>}
            </button>
          ))}
        </div>
      )}

      {error && <p className="text-sm text-red-500 mt-1">{error}</p>}
    </div>
  );
};

export const PromocionForm = ({
  initialData,
  negocioId,
  categorias = [],
  productos = [],
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const isEdit = !!initialData?.id;

  const {
    register,
    handleSubmit,
    watch,
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

  const aplicaA = watch('aplicaA');

  /* ─── Estado del buscador dependiente ─── */
  const [selectedEntidadId, setSelectedEntidadId] = useState(
    initialData?._entidadId ? String(initialData._entidadId) : '',
  );

  const categoriaItems = useMemo(
    () => categorias.map((c) => ({ id: c.id, label: c.nombre, searchText: c.nombre })),
    [categorias],
  );

  const productoItems = useMemo(
    () => productos.map((p) => ({
      id: p.id,
      label: p.nombre,
      extra: p.sku || '',
      searchText: `${p.nombre} ${p.sku || ''}`,
    })),
    [productos],
  );

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
      /* Dato extra para crear condición (Tab lo usa para POST a condiciones-promocion) */
      _entidadId: (formData.aplicaA === 'categoria' || formData.aplicaA === 'producto') && selectedEntidadId
        ? Number(selectedEntidadId)
        : null,
      _tipoEntidad: formData.aplicaA === 'categoria' ? 'categoria' : formData.aplicaA === 'producto' ? 'producto' : null,
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

      {/* ─── Buscador dependiente: Categoría ─── */}
      {aplicaA === 'categoria' && (
        <EntitySearch
          label="Categoría"
          required
          items={categoriaItems}
          selectedId={selectedEntidadId}
          onSelect={setSelectedEntidadId}
          onClear={() => setSelectedEntidadId('')}
          placeholder="Buscar categoría por nombre..."
          icon={Tag}
          error={aplicaA === 'categoria' && !selectedEntidadId ? 'Debe seleccionar una categoría' : undefined}
        />
      )}

      {/* ─── Buscador dependiente: Producto ─── */}
      {aplicaA === 'producto' && (
        <EntitySearch
          label="Producto"
          required
          items={productoItems}
          selectedId={selectedEntidadId}
          onSelect={setSelectedEntidadId}
          onClear={() => setSelectedEntidadId('')}
          placeholder="Buscar producto por nombre o SKU..."
          icon={Package}
          error={aplicaA === 'producto' && !selectedEntidadId ? 'Debe seleccionar un producto' : undefined}
        />
      )}

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
