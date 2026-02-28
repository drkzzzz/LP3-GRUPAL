/**
 * NegocioForm.jsx
 * ───────────────
 * Formulario para editar datos del negocio actual.
 * Usa React Hook Form + Zod.
 */
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { negocioSchema } from '../../validations/configuracionSchemas';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';
import { Save } from 'lucide-react';

const DOC_TYPE_OPTIONS = [
  { value: 'RUC', label: 'RUC' },
  { value: 'DNI', label: 'DNI' },
  { value: 'CE', label: 'Carnet de Extranjería' },
  { value: 'OTRO', label: 'Otro' },
];

const TIPO_NEGOCIO_OPTIONS = [
  { value: 'Licorería', label: 'Licorería' },
  { value: 'Distribuidora', label: 'Distribuidora' },
  { value: 'Licorería Premium', label: 'Licorería Premium' },
  { value: 'Bodega', label: 'Bodega' },
  { value: 'Otro', label: 'Otro' },
];

export const NegocioForm = ({ initialData, onSubmit, isLoading }) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isDirty },
  } = useForm({
    resolver: zodResolver(negocioSchema),
    defaultValues: {
      razonSocial: '',
      nombreComercial: '',
      ruc: '',
      tipoDocumentoFiscal: 'RUC',
      representanteLegal: '',
      documentoRepresentante: '',
      tipoNegocio: '',
      email: '',
      telefono: '',
      direccion: '',
      ciudad: '',
      departamento: '',
      pais: 'PE',
      codigoPostal: '',
      urlLogo: '',
      aplicaIgv: true,
      porcentajeIgv: 18,
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        razonSocial: initialData.razonSocial || '',
        nombreComercial: initialData.nombreComercial || '',
        ruc: initialData.ruc || '',
        tipoDocumentoFiscal: initialData.tipoDocumentoFiscal || 'RUC',
        representanteLegal: initialData.representanteLegal || '',
        documentoRepresentante: initialData.documentoRepresentante || '',
        tipoNegocio: initialData.tipoNegocio || '',
        email: initialData.email || '',
        telefono: initialData.telefono || '',
        direccion: initialData.direccion || '',
        ciudad: initialData.ciudad || '',
        departamento: initialData.departamento || '',
        pais: initialData.pais || 'PE',
        codigoPostal: initialData.codigoPostal || '',
        urlLogo: initialData.urlLogo || '',
        aplicaIgv: initialData.aplicaIgv ?? true,
        porcentajeIgv: initialData.porcentajeIgv ?? 18,
      });
    }
  }, [initialData, reset]);

  const handleFormSubmit = (data) => {
    onSubmit({ ...initialData, ...data });
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      {/* ─── Datos Legales ─── */}
      <div>
        <h3 className="text-sm font-semibold text-gray-800 uppercase tracking-wider mb-3">
          Datos Legales
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Razón Social"
            required
            {...register('razonSocial')}
            error={errors.razonSocial?.message}
            placeholder="LICORERIA DON PEPE S.A.C."
          />
          <Input
            label="Nombre Comercial"
            {...register('nombreComercial')}
            error={errors.nombreComercial?.message}
            placeholder="Don Pepe Licores"
          />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
          <Select
            label="Tipo Documento Fiscal"
            required
            options={DOC_TYPE_OPTIONS}
            {...register('tipoDocumentoFiscal')}
            error={errors.tipoDocumentoFiscal?.message}
          />
          <Input
            label="RUC"
            required
            {...register('ruc')}
            error={errors.ruc?.message}
            placeholder="20123456789"
            maxLength={11}
          />
          <Select
            label="Tipo de Negocio"
            options={TIPO_NEGOCIO_OPTIONS}
            placeholder="Seleccione..."
            {...register('tipoNegocio')}
            error={errors.tipoNegocio?.message}
          />
        </div>
      </div>

      {/* ─── Representante Legal ─── */}
      <div>
        <h3 className="text-sm font-semibold text-gray-800 uppercase tracking-wider mb-3">
          Representante Legal
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Nombre del Representante"
            {...register('representanteLegal')}
            error={errors.representanteLegal?.message}
            placeholder="José Pérez García"
          />
          <Input
            label="Documento del Representante"
            {...register('documentoRepresentante')}
            error={errors.documentoRepresentante?.message}
            placeholder="43215678"
          />
        </div>
      </div>

      {/* ─── Contacto ─── */}
      <div>
        <h3 className="text-sm font-semibold text-gray-800 uppercase tracking-wider mb-3">
          Contacto
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Email"
            required
            type="email"
            {...register('email')}
            error={errors.email?.message}
            placeholder="contacto@donpepe.com"
          />
          <Input
            label="Teléfono"
            {...register('telefono')}
            error={errors.telefono?.message}
            placeholder="987123456"
            maxLength={9}
          />
        </div>
      </div>

      {/* ─── Dirección ─── */}
      <div>
        <h3 className="text-sm font-semibold text-gray-800 uppercase tracking-wider mb-3">
          Dirección Fiscal
        </h3>
        <div className="grid grid-cols-1 gap-4">
          <Input
            label="Dirección"
            {...register('direccion')}
            error={errors.direccion?.message}
            placeholder="Av. Los Incas 1234, Cercado de Lima"
          />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-4">
          <Input
            label="Ciudad"
            {...register('ciudad')}
            error={errors.ciudad?.message}
            placeholder="Lima"
          />
          <Input
            label="Departamento"
            {...register('departamento')}
            error={errors.departamento?.message}
            placeholder="Lima"
          />
          <Input
            label="País"
            {...register('pais')}
            error={errors.pais?.message}
            placeholder="PE"
            maxLength={3}
          />
          <Input
            label="Código Postal"
            {...register('codigoPostal')}
            error={errors.codigoPostal?.message}
            placeholder="15001"
          />
        </div>
      </div>

      {/* ─── Branding ─── */}
      <div>
        <h3 className="text-sm font-semibold text-gray-800 uppercase tracking-wider mb-3">
          Branding
        </h3>
        <Input
          label="URL del Logo"
          {...register('urlLogo')}
          error={errors.urlLogo?.message}
          placeholder="https://ejemplo.com/logo.png"
        />
      </div>

      {/* ─── Impuestos ─── */}
      <div>
        <h3 className="text-sm font-semibold text-gray-800 uppercase tracking-wider mb-3">
          Configuración de Impuestos
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="flex items-center gap-3 py-2">
            <input
              type="checkbox"
              id="aplicaIgv"
              {...register('aplicaIgv')}
              className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
            />
            <label htmlFor="aplicaIgv" className="text-sm text-gray-700">
              Aplica IGV
            </label>
          </div>
          <Input
            label="Porcentaje IGV (%)"
            type="number"
            step="0.01"
            {...register('porcentajeIgv', { valueAsNumber: true })}
            error={errors.porcentajeIgv?.message}
            placeholder="18.00"
          />
        </div>
      </div>

      {/* ─── Botón guardar ─── */}
      <div className="flex justify-end pt-4 border-t border-gray-200">
        <Button type="submit" disabled={isLoading || !isDirty}>
          <Save size={16} />
          {isLoading ? 'Guardando...' : 'Guardar Cambios'}
        </Button>
      </div>
    </form>
  );
};
