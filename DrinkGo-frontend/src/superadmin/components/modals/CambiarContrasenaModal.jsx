import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Eye, EyeOff, ShieldCheck, ShieldAlert } from 'lucide-react';
import { Modal } from '../ui/Modal';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';

const passwordSchema = z
  .object({
    contrasenaActual: z.string().min(1, 'La contraseña actual es requerida'),
    nuevaContrasena: z
      .string()
      .min(8, 'Debe tener al menos 8 caracteres')
      .regex(/[A-Z]/, 'Debe contener al menos una letra mayúscula')
      .regex(/[a-z]/, 'Debe contener al menos una letra minúscula')
      .regex(/\d/, 'Debe contener al menos un número')
      .regex(
        /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/,
        'Debe contener al menos un carácter especial (!@#$%^&*...)',
      ),
    confirmarContrasena: z.string().min(1, 'Debe confirmar la nueva contraseña'),
  })
  .refine((data) => data.nuevaContrasena === data.confirmarContrasena, {
    message: 'Las contraseñas no coinciden',
    path: ['confirmarContrasena'],
  });

const PASSWORD_RULES = [
  { label: 'Al menos 8 caracteres', test: (v) => v.length >= 8 },
  { label: 'Una letra mayúscula', test: (v) => /[A-Z]/.test(v) },
  { label: 'Una letra minúscula', test: (v) => /[a-z]/.test(v) },
  { label: 'Un número', test: (v) => /\d/.test(v) },
  {
    label: 'Un carácter especial (!@#$%^&*...)',
    test: (v) => /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(v),
  },
];

export const CambiarContrasenaModal = ({ isOpen, onClose, onSubmit, isLoading }) => {
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(passwordSchema),
    defaultValues: {
      contrasenaActual: '',
      nuevaContrasena: '',
      confirmarContrasena: '',
    },
  });

  const newPassword = watch('nuevaContrasena', '');

  const handleFormSubmit = async (data) => {
    await onSubmit(data);
    reset();
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Cambiar Contraseña" size="md">
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-5">
        {/* Current Password */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Contraseña Actual <span className="text-red-500">*</span>
          </label>
          <div className="relative">
            <input
              type={showCurrent ? 'text' : 'password'}
              {...register('contrasenaActual')}
              className={`block w-full border rounded-lg px-3 py-2 text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 pr-10 ${
                errors.contrasenaActual ? 'border-red-300 text-red-900' : 'border-gray-300 text-gray-900'
              }`}
              placeholder="Ingrese su contraseña actual"
            />
            <button
              type="button"
              onClick={() => setShowCurrent(!showCurrent)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
            >
              {showCurrent ? <EyeOff size={16} /> : <Eye size={16} />}
            </button>
          </div>
          {errors.contrasenaActual && (
            <p className="text-sm text-red-500 mt-1">{errors.contrasenaActual.message}</p>
          )}
        </div>

        {/* New Password */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Nueva Contraseña <span className="text-red-500">*</span>
          </label>
          <div className="relative">
            <input
              type={showNew ? 'text' : 'password'}
              {...register('nuevaContrasena')}
              className={`block w-full border rounded-lg px-3 py-2 text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 pr-10 ${
                errors.nuevaContrasena ? 'border-red-300 text-red-900' : 'border-gray-300 text-gray-900'
              }`}
              placeholder="Ingrese la nueva contraseña"
            />
            <button
              type="button"
              onClick={() => setShowNew(!showNew)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
            >
              {showNew ? <EyeOff size={16} /> : <Eye size={16} />}
            </button>
          </div>
          {errors.nuevaContrasena && (
            <p className="text-sm text-red-500 mt-1">{errors.nuevaContrasena.message}</p>
          )}
        </div>

        {/* Password Strength Indicator */}
        {newPassword && (
          <div className="bg-gray-50 rounded-lg p-3 space-y-2">
            <p className="text-xs font-medium text-gray-600 mb-1">Requisitos de contraseña:</p>
            {PASSWORD_RULES.map((rule, index) => {
              const passed = rule.test(newPassword);
              return (
                <div key={index} className="flex items-center gap-2">
                  {passed ? (
                    <ShieldCheck size={14} className="text-green-500 shrink-0" />
                  ) : (
                    <ShieldAlert size={14} className="text-gray-400 shrink-0" />
                  )}
                  <span
                    className={`text-xs ${
                      passed ? 'text-green-600' : 'text-gray-500'
                    }`}
                  >
                    {rule.label}
                  </span>
                </div>
              );
            })}
          </div>
        )}

        {/* Confirm Password */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Repetir Nueva Contraseña <span className="text-red-500">*</span>
          </label>
          <div className="relative">
            <input
              type={showConfirm ? 'text' : 'password'}
              {...register('confirmarContrasena')}
              className={`block w-full border rounded-lg px-3 py-2 text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 pr-10 ${
                errors.confirmarContrasena ? 'border-red-300 text-red-900' : 'border-gray-300 text-gray-900'
              }`}
              placeholder="Repita la nueva contraseña"
            />
            <button
              type="button"
              onClick={() => setShowConfirm(!showConfirm)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
            >
              {showConfirm ? <EyeOff size={16} /> : <Eye size={16} />}
            </button>
          </div>
          {errors.confirmarContrasena && (
            <p className="text-sm text-red-500 mt-1">{errors.confirmarContrasena.message}</p>
          )}
        </div>

        <div className="flex justify-end gap-2 pt-4">
          <Button type="button" variant="outline" onClick={handleClose}>
            Cancelar
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? 'Cambiando...' : 'Cambiar Contraseña'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
