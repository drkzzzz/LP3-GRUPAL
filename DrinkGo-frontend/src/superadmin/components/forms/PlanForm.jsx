import { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Button } from '../ui/Button';

const schema = z.object({
  nombre: z.string().min(3, 'Nombre muy corto'),
  descripcion: z.string().optional(),
  precio: z.coerce.number().min(0, 'Precio no puede ser negativo'),
  moneda: z.string().min(1, 'Seleccione moneda'),
  periodoFacturacion: z.string().min(1, 'Seleccione período'),
  maxSedes: z.coerce.number().int().min(1, 'Mínimo 1 sede'),
  maxUsuarios: z.coerce.number().int().min(1, 'Mínimo 1 usuario'),
  maxProductos: z.coerce.number().int().min(1, 'Mínimo 1 producto'),
  maxAlmacenesPorSede: z.coerce.number().int().min(1, 'Mínimo 1 almacén'),
  permitePos: z.boolean().optional(),
  permiteTiendaOnline: z.boolean().optional(),
  permiteDelivery: z.boolean().optional(),
  permiteFacturacionElectronica: z.boolean().optional(),
  permiteReportesAvanzados: z.boolean().optional(),
  orden: z.coerce.number().int().min(0).optional(),
});

const MONEDAS = [
  { value: 'PEN', label: 'Soles (PEN)' },
  { value: 'USD', label: 'Dólares (USD)' },
];

const PERIODOS = [
  { value: 'mensual', label: 'Mensual' },
  { value: 'anual', label: 'Anual' },
];

const ADMIN_MODULES = [
  { key: 'dashboard',      label: 'Dashboard',     description: 'Panel de control del negocio' },
  { key: 'configuracion', label: 'Configuración',  description: 'Negocio, sedes y storefront' },
  { key: 'usuarios',      label: 'Usuarios',       description: 'Seguridad, usuarios y clientes' },
  { key: 'catalogo',      label: 'Catálogo',       description: 'Productos, descuentos y promociones' },
  { key: 'inventario',    label: 'Inventario',     description: 'Gestión de inventario y almacenes' },
  { key: 'compras',       label: 'Compras',        description: 'Proveedores y órdenes de compra' },
  { key: 'ventas',        label: 'Ventas',         description: 'Ventas, POS y pedidos' },
  { key: 'facturacion',   label: 'Facturación',    description: 'Facturación electrónica (SUNAT)' },
  { key: 'gastos',        label: 'Gastos',         description: 'Gastos e ingresos' },
  { key: 'reportes',      label: 'Reportes',       description: 'Reportes y analítica avanzada' },
];

export const PlanForm = ({ initialData, onSubmit, onCancel, isLoading }) => {
  const [selectedModules, setSelectedModules] = useState(() => {
    try {
      return JSON.parse(initialData?.modulosHabilitados || '[]');
    } catch {
      return [];
    }
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      nombre: '',
      descripcion: '',
      precio: 0,
      moneda: 'PEN',
      periodoFacturacion: 'mensual',
      maxSedes: 1,
      maxUsuarios: 3,
      maxProductos: 500,
      maxAlmacenesPorSede: 1,
      permitePos: false,
      permiteTiendaOnline: false,
      permiteDelivery: false,
      permiteFacturacionElectronica: false,
      permiteReportesAvanzados: false,
      orden: 0,
      ...initialData,
    },
  });

  const toggleModule = (key) =>
    setSelectedModules((prev) =>
      prev.includes(key) ? prev.filter((k) => k !== key) : [...prev, key]
    );

  const handleFormSubmit = (data) => {
    onSubmit({
      ...data,
      modulosHabilitados: selectedModules.length > 0 ? JSON.stringify(selectedModules) : null,
    });
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      {/* Row 1: Nombre */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Nombre del Plan <span className="text-red-500">*</span>
        </label>
        <Input
          {...register('nombre')}
          placeholder="Plan Profesional"
          error={errors.nombre?.message}
        />
      </div>

      {/* Row 2 */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
        <textarea
          {...register('descripcion')}
          rows={3}
          className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Descripción del plan..."
        />
      </div>

      {/* Row 3: Precio, Moneda, Periodo */}
      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Precio <span className="text-red-500">*</span>
          </label>
          <Input
            {...register('precio')}
            type="number"
            step="0.01"
            min="0"
            error={errors.precio?.message}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Moneda</label>
          <Select {...register('moneda')} options={MONEDAS} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Período</label>
          <Select {...register('periodoFacturacion')} options={PERIODOS} />
        </div>
      </div>

      {/* Row 4: Limits */}
      <div className="grid grid-cols-4 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Máx. Sedes</label>
          <Input {...register('maxSedes')} type="number" min="1" error={errors.maxSedes?.message} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Máx. Usuarios</label>
          <Input {...register('maxUsuarios')} type="number" min="1" error={errors.maxUsuarios?.message} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Máx. Productos</label>
          <Input {...register('maxProductos')} type="number" min="1" error={errors.maxProductos?.message} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Almacenes/Sede</label>
          <Input
            {...register('maxAlmacenesPorSede')}
            type="number"
            min="1"
            error={errors.maxAlmacenesPorSede?.message}
          />
        </div>
      </div>

      {/* Row 5: Features checkboxes */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">Funcionalidades</label>
        <div className="grid grid-cols-2 gap-3">
          {[
            { field: 'permitePos', label: 'Punto de Venta (POS)' },
            { field: 'permiteTiendaOnline', label: 'Tienda Online' },
            { field: 'permiteDelivery', label: 'Delivery' },
            { field: 'permiteFacturacionElectronica', label: 'Facturación Electrónica' },
            { field: 'permiteReportesAvanzados', label: 'Reportes Avanzados' },
          ].map(({ field, label }) => (
            <label key={field} className="flex items-center gap-2 text-sm text-gray-700">
              <input
                type="checkbox"
                {...register(field)}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              {label}
            </label>
          ))}
        </div>
      </div>

      {/* Row 6: Módulos del Panel Admin */}
      <div className="border-t border-gray-100 pt-4">
        <div className="flex items-center justify-between mb-2">
          <label className="block text-sm font-medium text-gray-700">
            Módulos del Panel Admin
            <span className="text-xs font-normal text-gray-400 ml-2">
              (qué módulos puede usar el negocio)
            </span>
          </label>
          <div className="flex gap-3">
            <button
              type="button"
              onClick={() => setSelectedModules(ADMIN_MODULES.map((m) => m.key))}
              className="text-xs text-blue-600 hover:underline"
            >
              Todos
            </button>
            <button
              type="button"
              onClick={() => setSelectedModules([])}
              className="text-xs text-gray-500 hover:underline"
            >
              Ninguno
            </button>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-2">
          {ADMIN_MODULES.map(({ key, label, description }) => (
            <label
              key={key}
              className="flex items-start gap-2 text-sm text-gray-700 p-2 rounded-lg border border-gray-200 hover:bg-gray-50 cursor-pointer select-none"
            >
              <input
                type="checkbox"
                checked={selectedModules.includes(key)}
                onChange={() => toggleModule(key)}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500 mt-0.5 shrink-0"
              />
              <div>
                <p className="font-medium leading-tight">{label}</p>
                <p className="text-xs text-gray-400 leading-tight mt-0.5">{description}</p>
              </div>
            </label>
          ))}
        </div>
        {selectedModules.length === 0 && (
          <p className="text-xs text-amber-600 mt-2">
            Sin módulos seleccionados = todos los módulos habilitados (equivale a Enterprise).
          </p>
        )}
      </div>

      {/* Row 7: Orden */}
      <div className="w-32">
        <label className="block text-sm font-medium text-gray-700 mb-1">Orden</label>
        <Input {...register('orden')} type="number" min="0" />
      </div>

      {/* Actions */}
      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : 'Guardar'}
        </Button>
      </div>
    </form>
  );
};
