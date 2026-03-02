/**
 * UsuarioForm.jsx
 * ───────────────
 * Formulario para crear / editar un Usuario del negocio.
 */
import { useState, useEffect } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import { useQuery } from '@tanstack/react-query';
import { zodResolver } from '@hookform/resolvers/zod';
import { usuarioSchema } from '../../validations/usuariosClientesSchemas';
import { usuariosRolesService } from '../../services/usuariosClientesService';
import { Input } from '@/admin/components/ui/Input';
import { Button } from '@/admin/components/ui/Button';
import { Select } from '@/admin/components/ui/Select';

const TIPO_DOC_OPTS = [
  { value: 'DNI', label: 'DNI' },
  { value: 'CE', label: 'Carnet de Extranjería' },
  { value: 'PASAPORTE', label: 'Pasaporte' },
  { value: 'OTRO', label: 'Otro' },
];

export const UsuarioForm = ({
  initialData,
  negocioId,
  roles = [],
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  const isEdit = !!initialData?.id;

  // Rol seleccionado (gestionado fuera de react-hook-form para evitar coerción)
  const [selectedRolId, setSelectedRolId] = useState(null);

  // Obtener el rol actual del usuario cuando estamos en modo edición
  const { data: usuariosRolesData = [] } = useQuery({
    queryKey: ['usuario-roles', initialData?.id],
    queryFn: () => usuariosRolesService.getByUsuarioId(initialData.id),
    enabled: isEdit,
    staleTime: 0,
  });

  useEffect(() => {
    if (usuariosRolesData.length > 0) {
      // El backend devuelve { rolId, rolNombre } (relación @JsonIgnore, IDs computados)
      setSelectedRolId(usuariosRolesData[0]?.rolId ?? usuariosRolesData[0]?.rol?.id ?? null);
    }
  }, [usuariosRolesData]);

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(usuarioSchema),
    defaultValues: {
      nombres: initialData?.nombres || '',
      apellidos: initialData?.apellidos || '',
      email: initialData?.email || '',
      tipoDocumento: initialData?.tipoDocumento || 'DNI',
      numeroDocumento: initialData?.numeroDocumento || '',
      telefono: initialData?.telefono || '',
      password: '',
      confirmPassword: '',
      estaActivo: initialData?.estaActivo ?? true,
    },
  });

  const tipoDoc = useWatch({ control, name: 'tipoDocumento' });

  // maxLength dinámico según tipo de documento
  const docMaxLength = tipoDoc === 'DNI' ? 8 : tipoDoc === 'CE' ? 9 : 20;

  // Permite solo dígitos en el input (+ teclas de navegación)
  const onlyDigits = (e) => {
    const allowed = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End'];
    if (!allowed.includes(e.key) && !/^\d$/.test(e.key)) e.preventDefault();
  };

  const handleFormSubmit = (formData) => {
    const payload = {
      ...(isEdit && { id: initialData.id }),
      negocio: { id: negocioId },
      nombres: formData.nombres,
      apellidos: formData.apellidos,
      email: formData.email,
      tipoDocumento: formData.tipoDocumento,
      numeroDocumento: formData.numeroDocumento,
      telefono: formData.telefono || null,
      estaActivo: formData.estaActivo,
    };

    // Solo enviar password si se ingresó una
    if (formData.password) {
      payload.hashContrasena = formData.password;
    }

    // Incluir el rolId seleccionado para que el padre lo gestione
    payload.rolId = selectedRolId;

    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      {/* Nombres y Apellidos */}
      <div className="grid grid-cols-2 gap-4">
        <Input
          label="Nombres"
          required
          {...register('nombres')}
          error={errors.nombres?.message}
          placeholder="Juan Carlos"
        />
        <Input
          label="Apellidos"
          required
          {...register('apellidos')}
          error={errors.apellidos?.message}
          placeholder="García López"
        />
      </div>

      {/* Email */}
      <Input
        label="Correo electrónico"
        type="email"
        required
        {...register('email')}
        error={errors.email?.message}
        placeholder="usuario@empresa.com"
      />

      {/* Tipo y Número de Documento */}
      <div className="grid grid-cols-2 gap-4">
        <Select
          label="Tipo de documento"
          required
          {...register('tipoDocumento')}
          error={errors.tipoDocumento?.message}
          options={TIPO_DOC_OPTS}
        />
        <Input
          label="Número de documento"
          required
          {...register('numeroDocumento')}
          error={errors.numeroDocumento?.message}
          placeholder={tipoDoc === 'DNI' ? '12345678' : tipoDoc === 'CE' ? 'A12345678' : 'Número'}
          maxLength={docMaxLength}
          onKeyDown={tipoDoc === 'DNI' ? onlyDigits : undefined}
          inputMode="numeric"
        />
      </div>

      {/* Teléfono */}
      <Input
        label="Teléfono"
        {...register('telefono')}
        error={errors.telefono?.message}
        placeholder="987654321"
        maxLength={9}
        onKeyDown={onlyDigits}
        inputMode="numeric"
      />

      {/* Rol */}
      {roles.length > 0 && (
        <Select
          label="Rol"
          value={selectedRolId ?? ''}
          onChange={(e) => setSelectedRolId(e.target.value ? Number(e.target.value) : null)}
          options={roles.map((r) => ({ value: r.id, label: r.nombre }))}
          placeholder="Sin rol asignado"
        />
      )}

      {/* Contraseña */}
      <div className="border-t border-gray-100 pt-4">
        <p className="text-sm font-medium text-gray-700 mb-3">
          {isEdit ? 'Nueva contraseña (dejar en blanco para no cambiar)' : 'Contraseña'}
        </p>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label={isEdit ? 'Nueva contraseña' : 'Contraseña'}
            type="password"
            required={!isEdit}
            {...register('password')}
            error={errors.password?.message}
            placeholder="••••••••"
          />
          <Input
            label="Confirmar contraseña"
            type="password"
            required={!isEdit}
            {...register('confirmPassword')}
            error={errors.confirmPassword?.message}
            placeholder="••••••••"
          />
        </div>
      </div>

      {/* Estado */}
      <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
        <input
          type="checkbox"
          {...register('estaActivo')}
          className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
        />
        Usuario activo
      </label>

      {/* Acciones */}
      <div className="flex justify-end gap-2 pt-2">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar usuario' : 'Crear usuario'}
        </Button>
      </div>
    </form>
  );
};
