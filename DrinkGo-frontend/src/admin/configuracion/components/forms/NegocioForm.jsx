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
import { Save, Search, Loader2, CheckCircle, AlertCircle } from 'lucide-react';
import { useConsultarRuc, useConsultarDni } from '@/shared/hooks/useConsultaDocumento';

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
  const rucLookup = useConsultarRuc();
  const dniLookup = useConsultarDni();

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
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

  const rucValue = watch('ruc');
  const docRepValue = watch('documentoRepresentante');

  /** Buscar datos de empresa por RUC (PeruDevs API) */
  const handleBuscarRuc = async () => {
    const resultado = await rucLookup.buscar(rucValue);
    if (resultado) {
      setValue('razonSocial', resultado.razon_social || '', { shouldDirty: true });
      setValue('nombreComercial', resultado.nombre_comercial || '', { shouldDirty: true });
      setValue('direccion', resultado.direccion?.trim() || '', { shouldDirty: true });
    }
  };

  /** Buscar datos de persona por DNI (representante) — PeruDevs API */
  const handleBuscarDni = async () => {
    const resultado = await dniLookup.buscar(docRepValue);
    if (resultado) {
      const nombre = `${resultado.nombres} ${resultado.apellido_paterno} ${resultado.apellido_materno}`;
      setValue('representanteLegal', nombre.trim(), { shouldDirty: true });
    }
  };

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
          <div>
            <div className="flex items-end gap-2">
              <div className="flex-1">
                <Input
                  label="RUC"
                  required
                  {...register('ruc')}
                  error={errors.ruc?.message}
                  placeholder="20123456789"
                  maxLength={11}
                />
              </div>
              <button
                type="button"
                onClick={handleBuscarRuc}
                disabled={rucLookup.isLoading || !rucValue || rucValue.length !== 11}
                title="Buscar datos por RUC (SUNAT)"
                className="mb-0.5 flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {rucLookup.isLoading ? <Loader2 size={16} className="animate-spin" /> : <Search size={16} />}
                SUNAT
              </button>
            </div>
            {rucLookup.data && (
              <p className="text-xs text-green-600 mt-1 flex items-center gap-1">
                <CheckCircle size={12} /> {rucLookup.data.razon_social} — {rucLookup.data.estado}
              </p>
            )}
            {rucLookup.error && (
              <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                <AlertCircle size={12} /> {rucLookup.error}
              </p>
            )}
          </div>
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
          <div>
            <div className="flex items-end gap-2">
              <div className="flex-1">
                <Input
                  label="Documento del Representante"
                  {...register('documentoRepresentante')}
                  error={errors.documentoRepresentante?.message}
                  placeholder="43215678"
                  maxLength={8}
                />
              </div>
              <button
                type="button"
                onClick={handleBuscarDni}
                disabled={dniLookup.isLoading || !docRepValue || docRepValue.length !== 8}
                title="Buscar datos por DNI (RENIEC)"
                className="mb-0.5 flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {dniLookup.isLoading ? <Loader2 size={16} className="animate-spin" /> : <Search size={16} />}
                RENIEC
              </button>
            </div>
            {dniLookup.data && (
              <p className="text-xs text-green-600 mt-1 flex items-center gap-1">
                <CheckCircle size={12} /> {dniLookup.data.full_name}
              </p>
            )}
            {dniLookup.error && (
              <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                <AlertCircle size={12} /> {dniLookup.error}
              </p>
            )}
          </div>
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
