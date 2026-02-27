import { useState } from 'react';
import {
  User,
  Mail,
  Phone,
  Shield,
  Clock,
  Edit,
  KeyRound,
  ArrowLeft,
  CalendarDays,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { usePerfil } from '../hooks/usePerfil';
import { useAuthStore } from '@/stores/authStore';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { EditPerfilModal } from '../components/modals/EditPerfilModal';
import { CambiarContrasenaModal } from '../components/modals/CambiarContrasenaModal';
import { formatDateTime, formatPhone } from '@/shared/utils/formatters';

const ROL_LABELS = {
  superadmin: 'Super Administrador',
  soporte_plataforma: 'Soporte Plataforma',
  visualizador_plataforma: 'Visualizador Plataforma',
};

export const MiPerfil = () => {
  const navigate = useNavigate();
  const { user, login: updateAuthUser } = useAuthStore();
  const {
    perfil,
    isLoading,
    isError,
    updatePerfil,
    isUpdating,
    changePassword,
    isChangingPassword,
    refetch,
  } = usePerfil();

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);

  const handleUpdatePerfil = async (data) => {
    await updatePerfil(data);
    // Update the auth store so sidebar/header reflect changes
    if (user) {
      updateAuthUser(
        {
          ...user,
          nombres: data.nombres,
          apellidos: data.apellidos,
          email: data.email,
          nombre: `${data.nombres} ${data.apellidos}`,
        },
        useAuthStore.getState().token,
      );
    }
    await refetch();
    setIsEditModalOpen(false);
  };

  const handleChangePassword = async (data) => {
    await changePassword(data);
    setIsPasswordModalOpen(false);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
      </div>
    );
  }

  if (isError || !perfil) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500 text-lg">Error al cargar el perfil</p>
        <Button variant="outline" className="mt-4" onClick={() => refetch()}>
          Reintentar
        </Button>
      </div>
    );
  }

  const displayName = `${perfil.nombres} ${perfil.apellidos}`;
  const initials = `${perfil.nombres?.[0] || ''}${perfil.apellidos?.[0] || ''}`.toUpperCase();

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate(-1)}
          className="p-2 rounded-lg hover:bg-gray-100 text-gray-500 transition-colors"
          title="Volver"
        >
          <ArrowLeft size={20} />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Mi Perfil</h1>
          <p className="text-gray-600 mt-1">
            Gestiona tu información personal y seguridad
          </p>
        </div>
      </div>

      {/* Profile Header Card */}
      <Card className="relative overflow-hidden">
        {/* Background decoration */}
        <div className="absolute top-0 left-0 right-0 h-24 bg-gradient-to-r from-blue-600 to-blue-700 -mx-6 -mt-6" />

        <div className="relative pt-12 flex flex-col sm:flex-row items-start sm:items-end gap-4">
          {/* Avatar */}
          <div className="w-20 h-20 bg-white rounded-full border-4 border-white shadow-md flex items-center justify-center shrink-0">
            <span className="text-2xl font-bold text-blue-600">{initials}</span>
          </div>

          <div className="flex-1 min-w-0">
            <h2 className="text-xl font-bold text-gray-900">{displayName}</h2>
            <p className="text-sm text-gray-500">{perfil.email}</p>
            <Badge variant="info" className="mt-1">
              {ROL_LABELS[perfil.rol] || perfil.rol}
            </Badge>
          </div>

          {/* Actions */}
          <div className="flex gap-2 shrink-0">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setIsEditModalOpen(true)}
            >
              <Edit size={16} />
              Editar Perfil
            </Button>
            <Button
              variant="primary"
              size="sm"
              onClick={() => setIsPasswordModalOpen(true)}
            >
              <KeyRound size={16} />
              Cambiar Contraseña
            </Button>
          </div>
        </div>
      </Card>

      {/* Info Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Personal Info */}
        <Card>
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            Información Personal
          </h3>

          <div className="space-y-4">
            <InfoRow
              icon={User}
              label="Nombres"
              value={perfil.nombres}
            />
            <InfoRow
              icon={User}
              label="Apellidos"
              value={perfil.apellidos}
            />
            <InfoRow
              icon={Mail}
              label="Email"
              value={perfil.email}
            />
            <InfoRow
              icon={Phone}
              label="Teléfono"
              value={perfil.telefono ? formatPhone(perfil.telefono) : 'No registrado'}
              muted={!perfil.telefono}
            />
          </div>
        </Card>

        {/* Account Info */}
        <Card>
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">
              Información de Cuenta
            </h3>
          </div>

          <div className="space-y-4">
            <InfoRow
              icon={Shield}
              label="Rol"
              value={ROL_LABELS[perfil.rol] || perfil.rol}
            />
            <InfoRow
              icon={CalendarDays}
              label="Fecha de Creación"
              value={formatDateTime(perfil.creadoEn)}
            />
            <InfoRow
              icon={Clock}
              label="Última Actualización"
              value={formatDateTime(perfil.actualizadoEn)}
            />
            <InfoRow
              icon={Clock}
              label="Último Acceso"
              value={formatDateTime(perfil.ultimoAccesoEn)}
            />
          </div>
        </Card>
      </div>

      {/* Security Section */}
      <Card>
        <div>
          <h3 className="text-lg font-semibold text-gray-900">Seguridad</h3>
          <p className="text-sm text-gray-500 mt-1">
            Gestiona la contraseña de tu cuenta
          </p>
        </div>
        <div className="mt-4 p-4 bg-gray-50 rounded-lg">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <KeyRound size={18} className="text-blue-600" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-900">Contraseña</p>
              <p className="text-xs text-gray-500">
                Se recomienda cambiar la contraseña periódicamente para mayor seguridad
              </p>
            </div>
          </div>
        </div>
      </Card>

      {/* Edit Profile Modal */}
      {isEditModalOpen && (
        <EditPerfilModal
          isOpen={isEditModalOpen}
          onClose={() => setIsEditModalOpen(false)}
          perfil={perfil}
          onSubmit={handleUpdatePerfil}
          isLoading={isUpdating}
        />
      )}

      {/* Change Password Modal */}
      <CambiarContrasenaModal
        isOpen={isPasswordModalOpen}
        onClose={() => setIsPasswordModalOpen(false)}
        onSubmit={handleChangePassword}
        isLoading={isChangingPassword}
      />
    </div>
  );
};

/* ── Helper Component ──────────────────── */
const InfoRow = ({ icon: Icon, label, value, muted = false }) => (
  <div className="flex items-start gap-3">
    <div className="p-2 bg-gray-100 rounded-lg shrink-0">
      <Icon size={16} className="text-gray-500" />
    </div>
    <div className="min-w-0 flex-1">
      <p className="text-xs text-gray-500">{label}</p>
      <p className={`text-sm font-medium ${muted ? 'text-gray-400 italic' : 'text-gray-900'} truncate`}>
        {value}
      </p>
    </div>
  </div>
);
