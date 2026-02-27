import { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import {
  KeyRound,
  Eye,
  EyeOff,
  UserPlus,
  Loader2,
  Check,
  Copy,
  ShieldCheck,
} from 'lucide-react';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { negociosService } from '../../services/negociosService';

export const CredencialesPanel = ({
  negocio,
  onCreateUsuario,
  onUpdateUsuario,
  isCreating,
  isUpdating,
}) => {
  const queryClient = useQueryClient();

  const { data: usuarios = [], isLoading: isLoadingUsuarios } = useQuery({
    queryKey: ['usuarios-negocio', negocio?.id],
    queryFn: () => negociosService.getUsuariosByNegocio(negocio.id),
    enabled: !!negocio?.id,
  });
  const [showNewForm, setShowNewForm] = useState(false);
  const [newEmail, setNewEmail] = useState('');
  const [newNombres, setNewNombres] = useState('');
  const [newApellidos, setNewApellidos] = useState('');
  const [newPassword, setNewPassword] = useState('Admin123!');
  const [showNewPass, setShowNewPass] = useState(false);

  const [resetFor, setResetFor] = useState(null); // user.id
  const [resetPass, setResetPass] = useState('');
  const [showResetPass, setShowResetPass] = useState(false);

  const [copiedId, setCopiedId] = useState(null);

  const copyToClipboard = (text, id) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const defaultEmailPlaceholder = negocio?.email
    ? `admin@${negocio.email.split('@')[1]}`
    : 'admin@negocio.com';

  const handleCreate = async () => {
    if (!newEmail || !newPassword || !newNombres || !newApellidos) return;
    await onCreateUsuario({
      negocio: { id: negocio.id },
      email: newEmail,
      hashContrasena: newPassword,
      nombres: newNombres,
      apellidos: newApellidos,
      estaActivo: 1,
    });
    queryClient.invalidateQueries({ queryKey: ['usuarios-negocio', negocio.id] });
    setShowNewForm(false);
    setNewEmail('');
    setNewNombres('');
    setNewApellidos('');
    setNewPassword('Admin123!');
  };

  const handleResetPassword = async (user) => {
    if (!resetPass) return;
    await onUpdateUsuario({
      ...user,
      hashContrasena: resetPass,
      negocio: { id: negocio.id },
    });
    queryClient.invalidateQueries({ queryKey: ['usuarios-negocio', negocio.id] });
    setResetFor(null);
    setResetPass('');
    setShowResetPass(false);
  };

  return (
    <div className="space-y-4">
      {/* Header note */}
      <div className="flex items-start gap-2 bg-blue-50 border border-blue-200 rounded-lg p-3 text-sm text-blue-800">
        <ShieldCheck size={16} className="shrink-0 mt-0.5 text-blue-600" />
        <p>
          Credenciales de acceso al módulo admin del negocio. Las contraseñas se
          almacenan cifradas.
        </p>
      </div>

      {/* Existing users */}
      {isLoadingUsuarios ? (
        <div className="flex items-center justify-center py-6 text-gray-400 text-sm gap-2">
          <Loader2 size={16} className="animate-spin" />
          Cargando credenciales...
        </div>
      ) : usuarios.length === 0 ? (
        <div className="text-center py-6 text-gray-400 text-sm">
          No hay usuarios administradores para este negocio.
        </div>
      ) : (
        <div className="space-y-3">
          {usuarios.map((user) => (
            <div
              key={user.id}
              className="border border-gray-200 rounded-lg p-4 space-y-3"
            >
              <div className="flex items-center gap-3">
                {/* Avatar */}
                <div className="w-9 h-9 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-sm shrink-0">
                  {(user.nombres?.[0] || 'U').toUpperCase()}
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900">
                    {user.nombres} {user.apellidos}
                  </p>
                  <div className="flex items-center gap-1.5 mt-0.5">
                    <p className="text-xs text-gray-500 truncate">{user.email}</p>
                    <button
                      type="button"
                      onClick={() =>
                        copyToClipboard(user.email, `email-${user.id}`)
                      }
                      className="text-gray-400 hover:text-blue-600 shrink-0 transition-colors"
                      title="Copiar email"
                    >
                      {copiedId === `email-${user.id}` ? (
                        <Check size={12} className="text-green-500" />
                      ) : (
                        <Copy size={12} />
                      )}
                    </button>
                  </div>
                </div>

                {/* Reset btn */}
                <Button
                  type="button"
                  size="sm"
                  variant="outline"
                  onClick={() => {
                    if (resetFor === user.id) {
                      setResetFor(null);
                    } else {
                      setResetFor(user.id);
                      setResetPass('');
                      setShowResetPass(false);
                    }
                  }}
                >
                  <KeyRound size={14} className="mr-1" />
                  {resetFor === user.id ? 'Cancelar' : 'Cambiar clave'}
                </Button>
              </div>

              {/* Reset password form */}
              {resetFor === user.id && (
                <div className="flex items-center gap-2 pt-1">
                  <div className="relative flex-1">
                    <Input
                      type={showResetPass ? 'text' : 'password'}
                      placeholder="Nueva contraseña"
                      value={resetPass}
                      onChange={(e) => setResetPass(e.target.value)}
                    />
                    <button
                      type="button"
                      onClick={() => setShowResetPass(!showResetPass)}
                      className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                    >
                      {showResetPass ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                  <Button
                    type="button"
                    size="sm"
                    onClick={() => handleResetPassword(user)}
                    disabled={isUpdating || !resetPass}
                  >
                    {isUpdating ? (
                      <Loader2 size={14} className="animate-spin" />
                    ) : (
                      'Guardar'
                    )}
                  </Button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* New user form */}
      {showNewForm ? (
        <div className="border border-dashed border-indigo-300 bg-indigo-50 rounded-lg p-4 space-y-3">
          <p className="text-sm font-semibold text-indigo-800">
            Nuevo usuario administrador
          </p>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-medium text-gray-700 block mb-1">
                Nombres <span className="text-red-500">*</span>
              </label>
              <Input
                value={newNombres}
                onChange={(e) => setNewNombres(e.target.value)}
                placeholder="Juan"
              />
            </div>
            <div>
              <label className="text-xs font-medium text-gray-700 block mb-1">
                Apellidos <span className="text-red-500">*</span>
              </label>
              <Input
                value={newApellidos}
                onChange={(e) => setNewApellidos(e.target.value)}
                placeholder="Pérez"
              />
            </div>
          </div>
          <div>
            <label className="text-xs font-medium text-gray-700 block mb-1">
              Email <span className="text-red-500">*</span>
            </label>
            <Input
              type="email"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              placeholder={defaultEmailPlaceholder}
            />
          </div>
          <div>
            <label className="text-xs font-medium text-gray-700 block mb-1">
              Contraseña <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <Input
                type={showNewPass ? 'text' : 'password'}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Mínimo 6 caracteres"
              />
              <button
                type="button"
                onClick={() => setShowNewPass(!showNewPass)}
                className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                {showNewPass ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>
          <div className="flex gap-2 justify-end">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => setShowNewForm(false)}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              size="sm"
              onClick={handleCreate}
              disabled={
                isCreating ||
                !newEmail ||
                !newPassword ||
                !newNombres ||
                !newApellidos
              }
            >
              {isCreating && (
                <Loader2 size={14} className="animate-spin mr-1" />
              )}
              Crear usuario
            </Button>
          </div>
        </div>
      ) : (
        <Button
          type="button"
          variant="outline"
          size="sm"
          onClick={() => setShowNewForm(true)}
          className="w-full"
        >
          <UserPlus size={14} className="mr-2" />
          Agregar usuario administrador
        </Button>
      )}
    </div>
  );
};
