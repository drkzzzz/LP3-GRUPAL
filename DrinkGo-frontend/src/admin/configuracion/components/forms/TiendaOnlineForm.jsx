/**
 * TiendaOnlineForm.jsx
 * ────────────────────
 * Formulario para la configuración de la tienda online.
 * Es un formulario único (no CRUD de tabla), solo create/update.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { tiendaOnlineSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Button } from '@/admin/components/ui/Button';
import { Save, Globe } from 'lucide-react';

export const TiendaOnlineForm = ({ initialData, onSubmit, isLoading }) => {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    setValue,
    formState: { errors, isDirty },
  } = useForm({
    resolver: zodResolver(tiendaOnlineSchema),
    defaultValues: {
      estaHabilitado: false,
      nombreTienda: '',
      slugTienda: '',
      dominioPersonalizado: '',
      colorPrimario: '',
      colorSecundario: '',
      mensajeBienvenida: '',
    },
  });

  const isEnabled = watch('estaHabilitado');
  const colorPrimario = watch('colorPrimario');
  const colorSecundario = watch('colorSecundario');

  useEffect(() => {
    if (initialData) {
      let cv = {};
      try { cv = initialData.configVisual ? JSON.parse(initialData.configVisual) : {}; } catch { cv = {}; }
      reset({
        estaHabilitado: initialData.estaHabilitado ?? false,
        nombreTienda: initialData.nombreTienda || '',
        slugTienda: initialData.slugTienda || '',
        dominioPersonalizado: initialData.dominioPersonalizado || '',
        colorPrimario: cv.color_primario || '',
        colorSecundario: cv.color_secundario || '',
        mensajeBienvenida: cv.mensaje_bienvenida || '',
      });
    }
  }, [initialData, reset]);

  const handleFormSubmit = (data) => {
    onSubmit({ ...initialData, ...data });
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      {/* Toggle principal */}
      <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-200">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-green-100 rounded-lg">
            <Globe size={20} className="text-green-600" />
          </div>
          <div>
            <p className="text-sm font-semibold text-gray-900">Tienda Online</p>
            <p className="text-xs text-gray-500">
              Habilita la tienda online para que tus clientes puedan comprar por internet
            </p>
          </div>
        </div>
        <label className="relative inline-flex items-center cursor-pointer">
          <input
            type="checkbox"
            {...register('estaHabilitado')}
            className="sr-only peer"
          />
          <div className="w-11 h-6 bg-gray-200 peer-focus:ring-4 peer-focus:ring-green-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-600" />
        </label>
      </div>

      {/* Campos (sólo visibles si habilitado) */}
      <div className={isEnabled ? 'space-y-4' : 'space-y-4 opacity-50 pointer-events-none'}>
        <Input
          label="Nombre de la Tienda"
          {...register('nombreTienda')}
          error={errors.nombreTienda?.message}
          placeholder="Don Pepe Licores"
        />

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <Input
              label="Slug (URL amigable)"
              {...register('slugTienda')}
              error={errors.slugTienda?.message}
              placeholder="don-pepe-licores"
            />
            <p className="text-xs text-gray-400 mt-1">
              URL: drinkgo.com/<span className="font-medium">{watch('slugTienda') || 'tu-tienda'}</span>
            </p>
          </div>
          <Input
            label="Dominio Personalizado"
            {...register('dominioPersonalizado')}
            error={errors.dominioPersonalizado?.message}
            placeholder="www.mitienda.com"
          />
        </div>

        {/* Colores de marca */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Color Primario</label>
            <div className="flex items-center gap-2">
              <input
                type="color"
                value={colorPrimario || '#000000'}
                onChange={(e) => setValue('colorPrimario', e.target.value, { shouldDirty: true })}
                className="h-10 w-14 rounded border border-gray-300 cursor-pointer p-0.5"
              />
              <Input
                {...register('colorPrimario')}
                placeholder="#3B82F6"
                error={errors.colorPrimario?.message}
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Color Secundario</label>
            <div className="flex items-center gap-2">
              <input
                type="color"
                value={colorSecundario || '#000000'}
                onChange={(e) => setValue('colorSecundario', e.target.value, { shouldDirty: true })}
                className="h-10 w-14 rounded border border-gray-300 cursor-pointer p-0.5"
              />
              <Input
                {...register('colorSecundario')}
                placeholder="#10B981"
                error={errors.colorSecundario?.message}
              />
            </div>
          </div>
        </div>

        {/* Mensaje de bienvenida */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Mensaje de Bienvenida</label>
          <textarea
            {...register('mensajeBienvenida')}
            rows={3}
            placeholder="¡Bienvenido a nuestra tienda!"
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 resize-none"
          />
          {errors.mensajeBienvenida && (
            <p className="text-xs text-red-500 mt-1">{errors.mensajeBienvenida.message}</p>
          )}
        </div>
      </div>

      {/* Botón guardar */}
      <div className="flex justify-end pt-4 border-t border-gray-200">
        <Button type="submit" disabled={isLoading || !isDirty}>
          <Save size={16} className="mr-2" />
          {isLoading ? 'Guardando...' : 'Guardar Configuración'}
        </Button>
      </div>
    </form>
  );
};
