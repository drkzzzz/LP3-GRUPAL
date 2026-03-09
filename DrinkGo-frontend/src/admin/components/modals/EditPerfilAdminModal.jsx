import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Modal } from '@/admin/components/ui/Modal';
import { Input } from '@/admin/components/ui/Input';
import { Button } from '@/admin/components/ui/Button';

const schema = z.object({
  nombres: z.string().min(2, 'El nombre debe tener al menos 2 caracteres'),
  apellidos: z.string().min(2, 'Los apellidos deben tener al menos 2 caracteres'),
  email: z.string().email('Email inválido'),
  telefono: z
    .string()
    .optional()
    .refine((v) => !v || /^9\d{8}$/.test(v), { message: 'Teléfono inválido (formato: 9XXXXXXXX)' }),
});

export const EditPerfilAdminModal = ({ isOpen, onClose, perfil, onSave, isLoading }) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ resolver: zodResolver(schema) });

  useEffect(() => {
    if (perfil && isOpen) {
      reset({
        nombres: perfil.nombres || '',
        apellidos: perfil.apellidos || '',
        email: perfil.email || '',
        telefono: perfil.telefono || '',
      });
    }
  }, [perfil, isOpen, reset]);

  const onSubmit = (data) => {
    onSave(data);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Editar Información Personal" size="md">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Nombres"
            required
            {...register('nombres')}
            error={errors.nombres?.message}
            placeholder="Ingrese sus nombres"
          />
          <Input
            label="Apellidos"
            required
            {...register('apellidos')}
            error={errors.apellidos?.message}
            placeholder="Ingrese sus apellidos"
          />
        </div>

        <Input
          label="Email"
          type="email"
          required
          {...register('email')}
          error={errors.email?.message}
          placeholder="correo@ejemplo.com"
        />

        <Input
          label="Teléfono"
          {...register('telefono')}
          error={errors.telefono?.message}
          placeholder="9XXXXXXXX (opcional)"
        />

        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? 'Guardando...' : 'Guardar cambios'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
