/**
 * ClienteForm.jsx
 * ───────────────
 * Formulario para crear / editar un Cliente.
 *
 * Funcionalidades especiales:
 * - Lookup automático vía DNI → llena nombres, apellidos y fecha de nacimiento
 * - Lookup automático vía RUC → llena razón social y nombre comercial
 * - Validación de mayoría de edad (≥ 18 años) bloqueante
 */
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { differenceInYears, parseISO, format } from 'date-fns';
import { Search, AlertTriangle, CheckCircle, Loader2 } from 'lucide-react';
import { clienteSchema } from '../../validations/usuariosClientesSchemas';
import { consultaDocumentoService } from '../../services/usuariosClientesService';
import { Input } from '@/admin/components/ui/Input';
import { Button } from '@/admin/components/ui/Button';
import { Select } from '@/admin/components/ui/Select';
import { message } from '@/shared/utils/notifications';

const TIPO_DOC_OPTS = [
  { value: 'DNI', label: 'DNI' },
  { value: 'RUC', label: 'RUC' },
  { value: 'CE', label: 'Carnet de Extranjería' },
  { value: 'PASAPORTE', label: 'Pasaporte' },
  { value: 'OTRO', label: 'Otro' },
];

/* ─── Calcula edad a partir de fecha string (YYYY-MM-DD) ─── */
const calcularEdad = (fechaStr) => {
  if (!fechaStr) return null;
  try {
    return differenceInYears(new Date(), parseISO(fechaStr));
  } catch {
    return null;
  }
};

export const ClienteForm = ({
  initialData,
  negocioId,
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const isEdit = !!initialData?.id;
  const [consultando, setConsultando] = useState(false);
  const [consultaResult, setConsultaResult] = useState(null); // 'ok' | 'error' | null
  const [edadError, setEdadError] = useState(null);

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(clienteSchema),
    defaultValues: {
      tipoDocumento: initialData?.tipoDocumento || 'DNI',
      numeroDocumento: initialData?.numeroDocumento || '',
      nombres: initialData?.nombres || '',
      apellidos: initialData?.apellidos || '',
      razonSocial: initialData?.razonSocial || '',
      nombreComercial: initialData?.nombreComercial || '',
      email: initialData?.email || '',
      telefono: initialData?.telefono || '',
      fechaNacimiento: initialData?.fechaNacimiento || '',
      direccion: initialData?.direccion || '',
    },
  });

  const tipoDoc = watch('tipoDocumento');
  const numeroDoc = watch('numeroDocumento');
  const fechaNacimientoVal = watch('fechaNacimiento');

  const edad = calcularEdad(fechaNacimientoVal);
  const esMenorDeEdad = edad !== null && edad < 18;

  /* ─── Consulta automática de documento ─── */
  const handleConsultarDocumento = async () => {
    if (!numeroDoc || numeroDoc.trim() === '') {
      message.warning('Ingresa un número de documento primero');
      return;
    }

    setConsultando(true);
    setConsultaResult(null);

    try {
      if (tipoDoc === 'DNI') {
        if (!/^\d{8}$/.test(numeroDoc.trim())) {
          message.error('El DNI debe tener exactamente 8 dígitos');
          setConsultaResult('error');
          return;
        }

        const data = await consultaDocumentoService.consultarDni(numeroDoc.trim());

        // Mapeamos campos de la respuesta de PeruDevs
        // La API retorna snake_case: apellido_paterno, apellido_materno
        const nombres = data.nombres || data.nombre || '';
        const apellidos = data.apellidos ||
          [data.apellido_paterno || data.apellidoPaterno, data.apellido_materno || data.apellidoMaterno]
            .filter(Boolean)
            .join(' ');
        // La API PeruDevs v2 devuelve fechaNacimiento en formato DD/MM/YYYY o YYYY-MM-DD
        let fechaNac = data.fechaNacimiento || data.fecha_nacimiento || '';
        if (fechaNac && /^\d{2}\/\d{2}\/\d{4}$/.test(fechaNac)) {
          const [d, m, y] = fechaNac.split('/');
          fechaNac = `${y}-${m}-${d}`;
        }

        if (nombres) setValue('nombres', nombres);
        if (apellidos) setValue('apellidos', apellidos);
        if (fechaNac) {
          setValue('fechaNacimiento', fechaNac);
          const edadCalculada = calcularEdad(fechaNac);
          if (edadCalculada !== null && edadCalculada < 18) {
            setEdadError(
              `⚠️ Esta persona tiene ${edadCalculada} años. No se puede registrar un cliente menor de 18 años.`,
            );
          } else {
            setEdadError(null);
          }
        }

        setConsultaResult('ok');
        message.success('Datos encontrados y autocompletados');
      } else if (tipoDoc === 'RUC') {
        if (!/^\d{11}$/.test(numeroDoc.trim())) {
          message.error('El RUC debe tener exactamente 11 dígitos');
          setConsultaResult('error');
          return;
        }

        const data = await consultaDocumentoService.consultarRuc(numeroDoc.trim());
        const razonSocial = data.razonSocial || data.nombre || '';
        const nombreComercial = data.nombreComercial || data.nombre_comercial || '';

        if (razonSocial) setValue('razonSocial', razonSocial);
        if (nombreComercial) setValue('nombreComercial', nombreComercial);

        setConsultaResult('ok');
        message.success('Datos de empresa encontrados y autocompletados');
      } else {
        message.info('La consulta automática está disponible solo para DNI y RUC');
      }
    } catch (err) {
      setConsultaResult('error');
      message.error(err.response?.data?.error || 'No se pudo consultar el documento');
    } finally {
      setConsultando(false);
    }
  };

  /* ─── Vigilar cambio manual de fecha para validar edad ─── */
  const handleFechaNacimientoChange = (e) => {
    const val = e.target.value;
    setValue('fechaNacimiento', val);
    const edadCalculada = calcularEdad(val);
    if (edadCalculada !== null && edadCalculada < 18) {
      setEdadError(
        `Esta persona tiene ${edadCalculada} año${edadCalculada !== 1 ? 's' : ''}. No se puede registrar un cliente menor de 18 años.`,
      );
    } else {
      setEdadError(null);
    }
  };

  const handleFormSubmit = (formData) => {
    // Bloqueo definitivo de menores de edad
    if (formData.fechaNacimiento) {
      const edadFinal = calcularEdad(formData.fechaNacimiento);
      if (edadFinal !== null && edadFinal < 18) {
        message.error('No se puede registrar un cliente menor de 18 años');
        return;
      }
    }

    const payload = {
      ...(isEdit && { id: initialData.id }),
      negocio: { id: negocioId },
      tipoDocumento: formData.tipoDocumento,
      numeroDocumento: formData.numeroDocumento,
      nombres: formData.nombres || null,
      apellidos: formData.apellidos || null,
      razonSocial: formData.razonSocial || null,
      nombreComercial: formData.nombreComercial || null,
      email: formData.email || null,
      telefono: formData.telefono || null,
      fechaNacimiento: formData.fechaNacimiento || null,
      direccion: formData.direccion || null,
    };

    onSubmit(payload);
  };

  const esDNI = tipoDoc === 'DNI';
  const esRUC = tipoDoc === 'RUC';

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-5">
      {/* TIPO Y NÚMERO DE DOCUMENTO */}
      <div className="grid grid-cols-3 gap-3 items-end">
        <Select
          label="Tipo documento"
          required
          {...register('tipoDocumento')}
          error={errors.tipoDocumento?.message}
          options={TIPO_DOC_OPTS}
        />
        <div className="col-span-2">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Número de documento <span className="text-red-500">*</span>
          </label>
          <div className="flex gap-2">
            <div className="relative flex-1">
              <input
                {...register('numeroDocumento')}
                placeholder={esDNI ? '12345678' : esRUC ? '20123456789' : 'Número'}
                maxLength={esRUC ? 11 : esDNI ? 8 : 20}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
              {consultaResult === 'ok' && (
                <CheckCircle
                  size={16}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-green-500"
                />
              )}
            </div>
            {(esDNI || esRUC) && (
              <button
                type="button"
                onClick={handleConsultarDocumento}
                disabled={consultando}
                title="Consultar documento"
                className="flex items-center gap-1.5 px-3 py-2 bg-blue-50 hover:bg-blue-100 text-blue-700 border border-blue-200 rounded-lg text-sm font-medium transition-colors disabled:opacity-50 whitespace-nowrap"
              >
                {consultando ? (
                  <Loader2 size={15} className="animate-spin" />
                ) : (
                  <Search size={15} />
                )}
                {consultando ? 'Consultando...' : 'Auto-llenar'}
              </button>
            )}
          </div>
          {errors.numeroDocumento && (
            <p className="text-xs text-red-500 mt-1">{errors.numeroDocumento.message}</p>
          )}
        </div>
      </div>

      {/* DATOS PERSONALES (DNI / CE / Pasaporte) */}
      {!esRUC && (
        <>
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Nombres"
              {...register('nombres')}
              error={errors.nombres?.message}
              placeholder="Juan Carlos"
            />
            <Input
              label="Apellidos"
              {...register('apellidos')}
              error={errors.apellidos?.message}
              placeholder="García López"
            />
          </div>

          {/* FECHA DE NACIMIENTO + VALIDACIÓN EDAD */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha de nacimiento
            </label>
            <input
              type="date"
              value={fechaNacimientoVal}
              onChange={handleFechaNacimientoChange}
              max={format(new Date(), 'yyyy-MM-dd')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            {/* Estado de edad en tiempo real */}
            {edad !== null && !esMenorDeEdad && (
              <p className="text-xs text-green-600 mt-1 flex items-center gap-1">
                <CheckCircle size={13} />
                {edad} años — mayor de edad ✓
              </p>
            )}
            {(esMenorDeEdad || edadError) && (
              <div className="mt-2 flex items-start gap-2 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                <AlertTriangle size={16} className="text-red-500 mt-0.5 flex-shrink-0" />
                <p className="text-sm text-red-700">
                  {edadError ||
                    `Esta persona tiene ${edad} año${edad !== 1 ? 's' : ''}. No se puede registrar un cliente menor de 18 años.`}
                </p>
              </div>
            )}
            {errors.fechaNacimiento && (
              <p className="text-xs text-red-500 mt-1">{errors.fechaNacimiento.message}</p>
            )}
          </div>
        </>
      )}

      {/* DATOS EMPRESA (RUC) */}
      {esRUC && (
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Razón Social"
            {...register('razonSocial')}
            error={errors.razonSocial?.message}
            placeholder="Empresa S.A.C."
          />
          <Input
            label="Nombre Comercial"
            {...register('nombreComercial')}
            error={errors.nombreComercial?.message}
            placeholder="Empresa"
          />
        </div>
      )}

      {/* CONTACTO */}
      <div className="grid grid-cols-2 gap-4">
        <Input
          label="Correo electrónico"
          type="email"
          {...register('email')}
          error={errors.email?.message}
          placeholder="cliente@email.com"
        />
        <Input
          label="Teléfono"
          {...register('telefono')}
          error={errors.telefono?.message}
          placeholder="987654321"
          maxLength={9}
        />
      </div>

      {/* DIRECCIÓN */}
      <Input
        label="Dirección"
        {...register('direccion')}
        error={errors.direccion?.message}
        placeholder="Av. Principal 123, Lima"
      />

      {/* ACCIONES */}
      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading || esMenorDeEdad}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar cliente' : 'Registrar cliente'}
        </Button>
      </div>
    </form>
  );
};
