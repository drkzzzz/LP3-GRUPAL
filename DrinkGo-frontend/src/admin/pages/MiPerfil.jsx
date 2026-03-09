import { useState } from 'react';
import {
  User,
  Mail,
  Phone,
  Shield,
  Clock,
  Edit,
  KeyRound,
  CalendarDays,
  Building2,
  BadgeCheck,
} from 'lucide-react';
import { usePerfilAdmin } from '@/admin/hooks/usePerfilAdmin';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { EditPerfilAdminModal } from '@/admin/components/modals/EditPerfilAdminModal';
import { CambiarContrasenaAdminModal } from '@/admin/components/modals/CambiarContrasenaAdminModal';
import { formatDateTime } from '@/shared/utils/formatters';

const InfoRow = ({ icon: Icon, label, value, muted = false }) => (
  <div className="flex items-start gap-3 py-3 border-b border-gray-100 last:border-0">
    <div className="bg-gray-100 rounded-lg p-2 shrink-0">
      <Icon size={16} className="text-gray-600" />
    </div>
    <div className="min-w-0">
      <p className="text-xs text-gray-500 mb-0.5">{label}</p>
      <p className={`text-sm font-medium ${muted ? 'text-gray-400 italic' : 'text-gray-900'} break-all`}>
        {value || '—'}
      </p>
    </div>
  </div>
);

export const MiPerfil = () => {
  const { perfil, isLoading, isError, refetch, updatePerfil, isUpdating, changePassword, isChangingPassword } =
    usePerfilAdmin();
  const { user, login, token, negocio, permisos, sede, cajaAsignada } = useAdminAuthStore();

  const [editOpen, setEditOpen] = useState(false);
  const [passwordOpen, setPasswordOpen] = useState(false);

  const handleUpdatePerfil = async (datos) => {
    await updatePerfil(datos);
    login(
      { ...user, nombres: datos.nombres, apellidos: datos.apellidos, email: datos.email },
      token,
      negocio,
      permisos,
      sede,
      cajaAsignada,
    );
    await refetch();
    setEditOpen(false);
  };

  const handleChangePassword = async (datos) => {
    await changePassword(datos);
    setPasswordOpen(false);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex flex-col items-center justify-center h-64 gap-3">
        <p className="text-red-500 font-medium">Error al cargar el perfil</p>
        <Button variant="outline" onClick={() => refetch()}>
          Reintentar
        </Button>
      </div>
    );
  }

  const nombreCompleto = perfil
    ? `${perfil.nombres} ${perfil.apellidos}`
    : user
      ? `${user.nombres} ${user.apellidos}`
      : '—';

  const iniciales = nombreCompleto
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((n) => n[0].toUpperCase())
    .join('');

  const roles = perfil?.roles || [];

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-8">
      {/* Header Card */}
      <Card className="overflow-hidden p-0">
        <div className="relative">
          {/* Gradient Banner */}
          <div className="h-24 bg-gradient-to-r from-green-600 to-green-700 absolute inset-x-0 top-0" />

          <div className="relative pt-12 pb-6 px-6">
            {/* Avatar + Buttons */}
            <div className="flex items-end justify-between">
              <div className="bg-white rounded-2xl p-1 shadow-md -mt-2">
                <div className="w-20 h-20 rounded-xl bg-gradient-to-br from-green-500 to-green-700 flex items-center justify-center">
                  <span className="text-white text-2xl font-bold">{iniciales}</span>
                </div>
              </div>

              <div className="flex gap-2 mb-1">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setEditOpen(true)}
                  className="flex items-center gap-1.5"
                >
                  <Edit size={14} />
                  Editar Perfil
                </Button>
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => setPasswordOpen(true)}
                  className="flex items-center gap-1.5"
                >
                  <KeyRound size={14} />
                  Cambiar Contraseña
                </Button>
              </div>
            </div>

            {/* Name & Roles */}
            <div className="mt-3">
              <h1 className="text-xl font-bold text-gray-900">{nombreCompleto}</h1>
              <p className="text-sm text-gray-500 mt-0.5">{perfil?.email || user?.email}</p>
              <div className="flex flex-wrap gap-1.5 mt-2">
                {roles.length > 0 ? (
                  roles.map((rol) => (
                    <Badge key={rol} variant="success">
                      {rol}
                    </Badge>
                  ))
                ) : (
                  <Badge variant="default">Sin rol asignado</Badge>
                )}
              </div>
            </div>
          </div>
        </div>
      </Card>

      {/* Info Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Información Personal */}
        <Card className="p-0">
          <div className="p-5">
            <div className="flex items-center gap-2 mb-4">
              <div className="bg-green-100 rounded-lg p-1.5">
                <User size={16} className="text-green-700" />
              </div>
              <h2 className="text-sm font-semibold text-gray-900">Información Personal</h2>
            </div>
            <div className="divide-y divide-gray-100">
              <InfoRow icon={User} label="Nombres" value={perfil?.nombres} />
              <InfoRow icon={User} label="Apellidos" value={perfil?.apellidos} />
              <InfoRow icon={Mail} label="Correo electrónico" value={perfil?.email} />
              <InfoRow
                icon={Phone}
                label="Teléfono"
                value={perfil?.telefono}
                muted={!perfil?.telefono}
              />
            </div>
          </div>
        </Card>

        {/* Información de Cuenta */}
        <Card className="p-0">
          <div className="p-5">
            <div className="flex items-center gap-2 mb-4">
              <div className="bg-green-100 rounded-lg p-1.5">
                <Shield size={16} className="text-green-700" />
              </div>
              <h2 className="text-sm font-semibold text-gray-900">Información de Cuenta</h2>
            </div>
            <div className="divide-y divide-gray-100">
              <InfoRow
                icon={BadgeCheck}
                label="Roles asignados"
                value={roles.length > 0 ? roles.join(', ') : null}
                muted={roles.length === 0}
              />
              <InfoRow
                icon={Building2}
                label="Negocio"
                value={perfil?.negocio?.nombre || negocio?.nombre}
              />
              <InfoRow
                icon={CalendarDays}
                label="Miembro desde"
                value={formatDateTime(perfil?.creadoEn)}
              />
              <InfoRow
                icon={Clock}
                label="Último acceso"
                value={formatDateTime(perfil?.ultimoAccesoEn ?? new Date())}
              />
            </div>
          </div>
        </Card>
      </div>

      {/* Security Card */}
      <Card className="p-0">
        <div className="p-5">
          <div className="flex items-center gap-2 mb-4">
            <div className="bg-green-100 rounded-lg p-1.5">
              <KeyRound size={16} className="text-green-700" />
            </div>
            <h2 className="text-sm font-semibold text-gray-900">Seguridad</h2>
          </div>
          <div className="bg-gray-50 rounded-lg p-4 flex items-start gap-3">
            <KeyRound size={18} className="text-gray-400 mt-0.5 shrink-0" />
            <div>
              <p className="text-sm font-medium text-gray-700">Contraseña</p>
              <p className="text-xs text-gray-500 mt-0.5">
                Mantén tu cuenta segura usando una contraseña fuerte con mayúsculas, números y
                caracteres especiales. Se recomienda cambiarla periódicamente.
              </p>
              <Button
                variant="outline"
                size="sm"
                className="mt-3"
                onClick={() => setPasswordOpen(true)}
              >
                <KeyRound size={14} className="mr-1.5" />
                Cambiar contraseña
              </Button>
            </div>
          </div>
        </div>
      </Card>

      {/* Modals */}
      <EditPerfilAdminModal
        isOpen={editOpen}
        onClose={() => setEditOpen(false)}
        perfil={perfil}
        onSave={handleUpdatePerfil}
        isLoading={isUpdating}
      />
      <CambiarContrasenaAdminModal
        isOpen={passwordOpen}
        onClose={() => setPasswordOpen(false)}
        onSave={handleChangePassword}
        isLoading={isChangingPassword}
      />
    </div>
  );
};

