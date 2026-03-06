/**
 * Cajas.jsx
 * ─────────
 * Página de gestión de cajas registradoras.
 * Flujo: primero aperturar caja → luego ir al POS a vender.
 */
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Plus, Edit, Monitor, Clock, Power,
  LogIn, ArrowRight, User, Users, Banknote,

} from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { Modal } from '@/admin/components/ui/Modal';
import { Input } from '@/admin/components/ui/Input';
import { StatCard } from '@/admin/components/ui/StatCard';
import { useUsuarios } from '@/admin/usuarios/hooks/useUsuarios';
import { ModalCerrarCaja } from '../components/ModalCerrarCaja';
import { SinCajaAsignada } from '../components/SinCajaAsignada';
import {
  useCajas,
  useSesionActiva,
  useSesionActions,
  useSesiones,
  useResumenTurno,
} from '../hooks/useCajas';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

export const Cajas = () => {
  const navigate = useNavigate();
  const { user, negocio, sede } = useAdminAuthStore();
  const { cajas, isLoading, crear, actualizar, toggleHabilitada, isCreating, isUpdating, isToggling, alcanceCajas, cajaAsignada } = useCajas();

  const esSoloCaja = alcanceCajas === 'caja_asignada';

  const { usuarios: todosUsuarios, isLoading: loadingUsuarios } = useUsuarios(negocio?.id);
  const { sesion, hasSesion, isLoading: loadingSesion, refetch: refetchSesion } = useSesionActiva();
  const { sesiones, isLoading: loadingSesiones } = useSesiones();
  const { abrirCaja, cerrarCaja, isAbriendo, isCerrando } = useSesionActions();
  const { resumen } = useResumenTurno(sesion?.id);

  const [showCrearModal, setShowCrearModal] = useState(false);
  const [showAbrirModal, setShowAbrirModal] = useState(false);
  const [showCerrarModal, setShowCerrarModal] = useState(false);
  const [editCaja, setEditCaja] = useState(null);
  const [formData, setFormData] = useState({
    nombreCaja: '',
    codigo: '',
    usuarioAsignadoId: '',
  });

  /* Apertura de caja */
  const [abrirCajaId, setAbrirCajaId] = useState('');
  const [montoApertura, setMontoApertura] = useState('100.00');

  /* Si es cajero con alcance caja_asignada pero sin caja asignada -> bloquear */
  if (esSoloCaja && !cajaAsignada) {
    return <SinCajaAsignada titulo="Cajas Registradoras" />;
  }

  /* Usuarios activos (para dropdown de asignar cajero) */
  /* Solo mostrar usuarios que NO están asignados a otra caja (excepto la caja que se está editando) */
  const usuariosDisponibles = todosUsuarios.filter((u) => {
    if (u.estaActivo === false) return false;
    const cajaAsignadaDelUsuario = cajas.find(
      (c) => c.estaActivo !== false && c.usuarioAsignado?.id === u.id,
    );
    // Si no está asignado a ninguna caja, está disponible
    if (!cajaAsignadaDelUsuario) return true;
    // Si estamos editando una caja y este usuario ya está asignado a ESA caja, permitir
    if (editCaja && cajaAsignadaDelUsuario.id === editCaja.id) return true;
    return false;
  });

  const cajasActivas = cajas.filter((c) => c.estaActivo !== false);
  const cajasHabilitadas = cajasActivas.filter((c) => c.estaHabilitada !== false);
  const sesionesAbiertas = sesiones.filter((s) => s.estadoSesion === 'abierta');

  /* ─── CRUD Cajas ─── */
  const openCrear = () => {
    setEditCaja(null);
    setFormData({ nombreCaja: '', codigo: '', usuarioAsignadoId: '' });
    setShowCrearModal(true);
  };

  const openEditar = (caja) => {
    setEditCaja(caja);
    setFormData({
      nombreCaja: caja.nombreCaja || '',
      codigo: caja.codigo || '',
      usuarioAsignadoId: caja.usuarioAsignado?.id?.toString() || '',
    });
    setShowCrearModal(true);
  };

  const handleSave = async () => {
    const payload = {
      negocioId: negocio?.id,
      sedeId: sede?.id,
      nombreCaja: formData.nombreCaja,
      codigo: formData.codigo,
      usuarioAsignadoId: formData.usuarioAsignadoId
        ? parseInt(formData.usuarioAsignadoId)
        : null,
    };

    if (editCaja) {
      await actualizar({ ...payload, id: editCaja.id });
    } else {
      await crear(payload);
    }
    setShowCrearModal(false);
  };

  const updateForm = (field, value) =>
    setFormData((prev) => ({ ...prev, [field]: value }));

  /* ─── Apertura de caja ─── */
  const handleOpenAbrirModal = () => {
    // Si es cajero con caja asignada, preseleccionar su caja automáticamente
    if (esSoloCaja && cajaAsignada) {
      setAbrirCajaId(String(cajaAsignada.id));
    } else {
      setAbrirCajaId('');
    }
    setMontoApertura('100.00');
    setShowAbrirModal(true);
  };

  const handleAbrirCaja = async () => {
    if (!abrirCajaId) return;
    await abrirCaja({
      cajaId: parseInt(abrirCajaId),
      usuarioId: user?.id,
      negocioId: negocio?.id,
      montoApertura: parseFloat(montoApertura) || 0,
    });
    setShowAbrirModal(false);
    refetchSesion();
  };

  /* ─── Cierre de caja ─── */
  const handleCerrarCaja = async (data) => {
    await cerrarCaja(data);
    setShowCerrarModal(false);
    refetchSesion();
  };

  /* ─── Ir al POS ─── */
  const goToPOS = () => navigate('/admin/ventas/pos');

  /* ─── Columnas tabla cajas ─── */

  const cajasColumns = [
    { key: 'index', title: '#', width: '60px', render: (_, __, i) => i + 1 },
    { key: 'codigo', title: 'Código', dataIndex: 'codigo' },
    { key: 'nombre', title: 'Nombre', dataIndex: 'nombreCaja' },
    {
      key: 'cajero',
      title: 'Cajero Asignado',
      render: (_, row) => {
        const u = row.usuarioAsignado;
        if (!u) return <span className="text-gray-400 text-xs">Sin asignar</span>;
        return (
          <div className="flex items-center gap-1.5">
            <User size={14} className="text-gray-400" />
            <span className="text-sm">{u.nombres || ''} {u.apellidos || ''}</span>
          </div>
        );
      },
    },
    {
      key: 'estado',
      title: 'Estado',
      render: (_, row) => {
        const habilitada = row.estaHabilitada !== false;
        if (!habilitada) {
          return <Badge variant="default">Deshabilitada</Badge>;
        }
        const abierta = sesionesAbiertas.some((s) => s.caja?.id === row.id);
        return (
          <Badge variant={abierta ? 'success' : 'info'}>
            {abierta ? 'En uso' : 'Disponible'}
          </Badge>
        );
      },
    },
    // Ocultar acciones para cajeros con alcance caja_asignada
    ...(!esSoloCaja ? [{
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, row) => {
        const habilitada = row.estaHabilitada !== false;
        return (
          <div className="flex justify-center gap-2">
            <button
              title="Editar"
              onClick={() => openEditar(row)}
              className="text-gray-500 hover:text-gray-700"
            >
              <Edit size={16} />
            </button>
            <button
              title={habilitada ? 'Deshabilitar caja' : 'Habilitar caja'}
              onClick={() => toggleHabilitada(row.id)}
              disabled={isToggling}
              className={habilitada
                ? 'text-green-500 hover:text-red-500'
                : 'text-red-400 hover:text-green-500'}
            >
              <Power size={16} />
            </button>
          </div>
        );
      },
    }] : []),
  ];

  /* Cajas disponibles para aperturar (habilitadas y sin sesión abierta) */
  const cajasDisponibles = cajasHabilitadas.filter(
    (c) => !sesionesAbiertas.some((s) => s.caja?.id === c.id),
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Cajas Registradoras</h1>
        <p className="text-gray-600 mt-1">
          Apertura de cajas, gestión de turnos y sesiones
        </p>
      </div>

      {/* Banner alcance restringido */}
      {esSoloCaja && cajaAsignada && (
        <div className="flex items-center gap-2 bg-amber-50 border border-amber-200 rounded-lg px-4 py-3">
          <Monitor size={16} className="text-amber-600 shrink-0" />
          <p className="text-sm text-amber-700">
            Tienes acceso únicamente a tu caja asignada: <strong>{cajaAsignada.nombreCaja}</strong> ({cajaAsignada.codigo})
          </p>
        </div>
      )}

      {/* ═══ BANNER SESIÓN ACTIVA / APERTURAR ═══ */}
      {!loadingSesion && (
        <>
          {hasSesion ? (
            /* ── Sesión activa: info y acciones ── */
            <Card className="border-green-200 bg-gradient-to-r from-green-50 to-emerald-50">
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                <div className="flex items-start gap-4">
                  <div className="p-3 bg-green-100 rounded-xl shrink-0">
                    <Power size={24} className="text-green-600" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <h2 className="text-lg font-bold text-gray-900">Turno Activo</h2>
                      <Badge variant="success">Abierta</Badge>
                    </div>
                    <div className="space-y-1 text-sm text-gray-600">
                      <div className="flex items-center gap-2">
                        <User size={14} className="text-gray-400" />
                        <span>
                          <strong>Cajero:</strong>{' '}
                          {user?.nombres || ''} {user?.apellidos || user?.email || '—'}
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Monitor size={14} className="text-gray-400" />
                        <span>
                          <strong>Caja:</strong>{' '}
                          {sesion?.caja?.nombreCaja || '—'} ({sesion?.caja?.codigo || '—'})
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Clock size={14} className="text-gray-400" />
                        <span>
                          <strong>Desde:</strong> {formatDateTime(sesion?.fechaApertura)}
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Banknote size={14} className="text-gray-400" />
                        <span>
                          <strong>Monto apertura:</strong> {formatCurrency(sesion?.montoApertura || 0)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="flex flex-col gap-2 sm:items-end shrink-0">
                  <Button onClick={goToPOS} className="w-full sm:w-auto">
                    <ArrowRight size={18} className="mr-2" />
                    Ir al Punto de Venta
                  </Button>
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => setShowCerrarModal(true)}
                    className="w-full sm:w-auto"
                  >
                    <Power size={16} className="mr-1" />
                    Cerrar Caja
                  </Button>
                </div>
              </div>

              {/* Mini resumen del turno */}
              {resumen && (
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mt-4 pt-4 border-t border-green-200">
                  <div className="text-center">
                    <p className="text-xs text-gray-500">Ventas</p>
                    <p className="text-lg font-bold text-gray-900">
                      {resumen.cantidadVentas ?? resumen.totalVentasCompletadas ?? 0}
                    </p>
                  </div>
                  <div className="text-center">
                    <p className="text-xs text-gray-500">Total vendido</p>
                    <p className="text-lg font-bold text-green-700">
                      {formatCurrency(resumen.totalVentas ?? 0)}
                    </p>
                  </div>
                  <div className="text-center">
                    <p className="text-xs text-gray-500">Efectivo</p>
                    <p className="text-lg font-bold text-gray-900">
                      {formatCurrency(resumen.totalEfectivo ?? 0)}
                    </p>
                  </div>
                  <div className="text-center">
                    <p className="text-xs text-gray-500">Digital</p>
                    <p className="text-lg font-bold text-gray-900">
                      {formatCurrency(
                        (resumen.totalTarjeta ?? 0) +
                          (resumen.totalYape ?? 0) +
                          (resumen.totalPlin ?? 0) +
                          (resumen.totalOtros ?? 0),
                      )}
                    </p>
                  </div>
                </div>
              )}
            </Card>
          ) : (
            /* ── Sin sesión: invitar a aperturar ── */
            <Card className="border-yellow-200 bg-gradient-to-r from-yellow-50 to-amber-50">
              <div className="flex flex-col sm:flex-row items-center gap-5">
                <div className="p-4 bg-yellow-100 rounded-xl shrink-0">
                  <LogIn size={32} className="text-yellow-600" />
                </div>
                <div className="flex-1 text-center sm:text-left">
                  <h2 className="text-lg font-bold text-gray-900 mb-1">
                    No tiene un turno abierto
                  </h2>
                  <p className="text-sm text-gray-600">
                    Para registrar ventas debe aperturar una caja.
                    Se registrará su usuario, la hora de apertura y el monto inicial de efectivo.
                  </p>
                  <div className="flex items-center gap-2 mt-2 text-sm text-gray-500 justify-center sm:justify-start">
                    <User size={14} />
                    <span>
                      Usuario: <strong>{user?.nombres || ''} {user?.apellidos || user?.email || '—'}</strong>
                    </span>
                  </div>
                </div>
                <Button
                  onClick={handleOpenAbrirModal}
                  disabled={cajasDisponibles.length === 0}
                  className="whitespace-nowrap shrink-0"
                >
                  <Power size={18} className="mr-2" />
                  Aperturar Caja
                </Button>
              </div>
            </Card>
          )}
        </>
      )}

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard title="Cajas registradas" value={cajasActivas.length} icon={Monitor} />
        <StatCard title="Sesiones abiertas" value={sesionesAbiertas.length} icon={Clock} />
        <StatCard title="Total sesiones" value={sesiones.length} icon={Users} />
      </div>

      {/* Tabla de cajas */}
      <Card>
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-gray-900">Cajas</h2>
          {!esSoloCaja && (
            <Button onClick={openCrear} size="sm">
              <Plus size={16} className="mr-1" />
              Nueva Caja
            </Button>
          )}
        </div>
        <Table columns={cajasColumns} data={cajasActivas} loading={isLoading} />
      </Card>

      {/* ═══ MODALES ═══ */}

      {/* Modal Crear/Editar Caja */}
      <Modal
        isOpen={showCrearModal}
        onClose={() => setShowCrearModal(false)}
        title={editCaja ? 'Editar Caja' : 'Nueva Caja Registradora'}
        size="sm"
      >
        <div className="space-y-4">
          <Input
            label="Nombre de la Caja"
            required
            value={formData.nombreCaja}
            onChange={(e) => updateForm('nombreCaja', e.target.value)}
            placeholder="Ej: Caja Principal"
          />
          <Input
            label="Código"
            required
            value={formData.codigo}
            onChange={(e) => updateForm('codigo', e.target.value)}
            placeholder="Ej: CAJA-001"
          />

          {/* Dropdown asignar cajero */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Cajero Asignado
            </label>
            <select
              value={formData.usuarioAsignadoId}
              onChange={(e) => updateForm('usuarioAsignadoId', e.target.value)}
              className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              disabled={loadingUsuarios}
            >
              <option value="">Sin asignar</option>
              {usuariosDisponibles.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nombres || ''} {u.apellidos || ''} — {u.email || ''}
                </option>
              ))}
            </select>
            <p className="text-xs text-gray-500 mt-1">
              Opcional. Solo necesario si desea restringir esta caja a un cajero específico.
            </p>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <Button
              variant="outline"
              onClick={() => setShowCrearModal(false)}
              disabled={isCreating || isUpdating}
            >
              Cancelar
            </Button>
            <Button
              onClick={handleSave}
              disabled={!formData.nombreCaja || !formData.codigo || isCreating || isUpdating}
            >
              {isCreating || isUpdating ? 'Guardando...' : 'Guardar'}
            </Button>
          </div>
        </div>
      </Modal>

      {/* Modal Aperturar Caja */}
      <Modal
        isOpen={showAbrirModal}
        onClose={() => setShowAbrirModal(false)}
        title="Aperturar Caja"
        size="sm"
      >
        <div className="space-y-4">
          {/* Info del usuario que apertura */}
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
            <p className="text-xs font-semibold text-blue-700 mb-1">Abierta por</p>
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                <User size={16} className="text-blue-600" />
              </div>
              <div>
                <p className="text-sm font-medium text-gray-900">
                  {user?.nombres || ''} {user?.apellidos || ''}
                </p>
                <p className="text-xs text-gray-500">{user?.email || '—'}</p>
              </div>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Caja Registradora <span className="text-red-500">*</span>
            </label>
            {esSoloCaja && cajaAsignada ? (
              /* Cajero con caja asignada: mostrar solo su caja (sin dropdown) */
              <div className="bg-green-50 border border-green-200 rounded-lg px-3 py-2">
                <p className="text-sm font-medium text-green-800">
                  {cajaAsignada.nombreCaja} ({cajaAsignada.codigo})
                </p>
                <p className="text-xs text-green-600 mt-0.5">Tu caja asignada</p>
              </div>
            ) : cajasDisponibles.length === 0 ? (
              <p className="text-sm text-red-500">
                No hay cajas disponibles. Todas están en uso o no se han creado.
              </p>
            ) : (
              <select
                value={abrirCajaId}
                onChange={(e) => {
                  setAbrirCajaId(e.target.value);
                  const caja = cajasHabilitadas.find((c) => c.id === parseInt(e.target.value));
                }}
                className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
              >
                <option value="">Seleccione una caja...</option>
                {cajasDisponibles.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nombreCaja} ({c.codigo})
                  </option>
                ))}
              </select>
            )}
          </div>

          <Input
            label="Monto de Apertura (S/)"
            required
            type="number"
            min={0}
            step={0.01}
            value={montoApertura}
            onChange={(e) => setMontoApertura(e.target.value)}
            placeholder="0.00"
          />

          <div className="flex justify-end gap-2 pt-2">
            <Button
              variant="outline"
              onClick={() => setShowAbrirModal(false)}
              disabled={isAbriendo}
            >
              Cancelar
            </Button>
            <Button onClick={handleAbrirCaja} disabled={!abrirCajaId || isAbriendo}>
              {isAbriendo ? 'Abriendo...' : 'Aperturar Caja'}
            </Button>
          </div>
        </div>
      </Modal>

      {/* Modal Cerrar Caja */}
      <ModalCerrarCaja
        isOpen={showCerrarModal}
        onClose={() => setShowCerrarModal(false)}
        onConfirm={handleCerrarCaja}
        sesion={sesion}
        resumen={resumen}
        isLoading={isCerrando}
      />

    </div>
  );
};
