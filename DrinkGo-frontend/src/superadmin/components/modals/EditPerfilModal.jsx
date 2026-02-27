import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Modal } from '../ui/Modal';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';

const perfilSchema = z.object({
  nombres: z.string().min(2, 'El nombre debe tener al menos 2 caracteres'),
  apellidos: z.string().min(2, 'Los apellidos deben tener al menos 2 caracteres'),
  email: z.string().email('Email inválido'),
  telefono: z
    .string()
    .regex(/^9\d{8}$/, 'Teléfono inválido (formato: 9XXXXXXXX)')
    .or(z.literal(''))
    .optional(),
});

export const EditPerfilModal = ({ isOpen, onClose, perfil, onSubmit, isLoading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(perfilSchema),
    defaultValues: {
      nombres: perfil?.nombres || '',
      apellidos: perfil?.apellidos || '',
      email: perfil?.email || '',
      telefono: perfil?.telefono || '',
    },
  });

  const handleFormSubmit = async (data) => {
    await onSubmit(data);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Editar Perfil" size="md">
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Nombres"
            {...register('nombres')}
            error={errors.nombres?.message}
            placeholder="Ingrese sus nombres"
          />
          <Input
            label="Apellidos"
            {...register('apellidos')}
            error={errors.apellidos?.message}
            placeholder="Ingrese sus apellidos"
          />
        </div>

        <Input
          label="Email"
          type="email"
          {...register('email')}
          error={errors.email?.message}
          placeholder="correo@ejemplo.com"
        />

        <Input
          label="Teléfono"
          {...register('telefono')}
          error={errors.telefono?.message}
          placeholder="987654321"
          maxLength={9}
        />

        <div className="flex justify-end gap-2 pt-4">
          <Button type="button" variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? 'Guardando...' : 'Guardar Cambios'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
