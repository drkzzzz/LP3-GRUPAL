/**
 * RolesTab.jsx
 * ────────────
 * Tab CRUD de Roles del negocio + gestión de permisos por rol.
 */
import { useState, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Edit, Trash2, Shield, ShieldCheck, Key, Check, X, Loader2, ChevronDown } from 'lucide-react';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useRoles } from '../../hooks/useRoles';
import { rolSchema } from '../../validations/usuariosClientesSchemas';
import { rolesPermisosService, permisosSistemaService } from '../../services/usuariosClientesService';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Button } from '@/admin/components/ui/Button';
import { Modal } from '@/admin/components/ui/Modal';
import { Card } from '@/admin/components/ui/Card';
import { Input } from '@/admin/components/ui/Input';
import { Textarea } from '@/admin/components/ui/Textarea';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { message } from '@/shared/utils/notifications';

const ACCION_COLORS = {
  ver: 'bg-blue-100 text-blue-700',
  crear: 'bg-green-100 text-green-700',
  editar: 'bg-amber-100 text-amber-700',
  eliminar: 'bg-red-100 text-red-700',
  exportar: 'bg-purple-100 text-purple-700',
  aprobar: 'bg-teal-100 text-teal-700',
  configurar: 'bg-gray-100 text-gray-700',
  completo: 'bg-indigo-100 text-indigo-700',
};

/* ─── Inline form de rol ─── */
const RolForm = ({ initialData, negocioId, onSubmit, onCancel, isLoading }) => {
  const isEdit = !!initialData?.id;
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(rolSchema),
    defaultValues: {
      nombre: initialData?.nombre || '',
      descripcion: initialData?.descripcion || '',
      estaActivo: initialData?.estaActivo ?? true,
    },
  });

  const handleFormSubmit = (data) => {
    onSubmit({
      ...(isEdit && { id: initialData.id }),
      negocio: { id: negocioId },
      nombre: data.nombre,
      descripcion: data.descripcion || null,
      estaActivo: data.estaActivo,
    });
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <Input
        label="Nombre del rol"
        required
        {...register('nombre')}
        error={errors.nombre?.message}
        placeholder="Ej: Vendedor, Almacenero, Supervisor"
      />
      <Textarea
        label="Descripción"
        {...register('descripcion')}
        error={errors.descripcion?.message}
        placeholder="Describe las responsabilidades de este rol..."
        rows={3}
      />
      <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
        <input
          type="checkbox"
          {...register('estaActivo')}
          className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
        />
        Rol activo
      </label>
      <div className="flex justify-end gap-2 pt-1">
        <Button variant="outline" type="button" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading ? 'Guardando...' : isEdit ? 'Actualizar rol' : 'Crear rol'}
        </Button>
      </div>
    </form>
  );
};

/* ─── Modal de permisos por rol ─── */
/** Submódulos que soportan alcance "caja_asignada" */
const PERMISOS_CON_ALCANCE = new Set([
  'ventas.cajas',
  'ventas.movimientos',
  'ventas.historial',
  'facturacion.comprobantes',
]);

const PermisosModal = ({ rol, onClose }) => {
  const queryClient = useQueryClient();
  const [openModulos, setOpenModulos] = useState(new Set());

  /* Todos los permisos del sistema */
  const { data: todosPermisos = [], isLoading: loadingPermisos } = useQuery({
    queryKey: ['permisos-sistema'],
    queryFn: permisosSistemaService.getAll,
    staleTime: 1000 * 60 * 10,
  });

  /* Permisos ya asignados al rol */
  const { data: rolesPermisos = [], isLoading: loadingAsignados } = useQuery({
    queryKey: ['roles-permisos'],
    queryFn: rolesPermisosService.getAll,
    staleTime: 0,
  });

  /* Map: permisoId → { rpId, alcance } (para revocar y ver alcance) */
  const asignadosAEsteRol = useMemo(
    () =>
      new Map(
        rolesPermisos
          .filter((rp) => rp.rol?.id === rol.id || rp.rolId === rol.id)
          .map((rp) => [
            rp.permiso?.id ?? rp.permisoId,
            { rpId: rp.id, alcance: rp.alcance || 'completo' },
          ]),
      ),
    [rolesPermisos, rol.id],
  );

  const assignMutation = useMutation({
    mutationFn: rolesPermisosService.assign,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['roles-permisos'] }),
    onError: () => message.error('Error al asignar permiso'),
  });

  const revokeMutation = useMutation({
    mutationFn: rolesPermisosService.revoke,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['roles-permisos'] }),
    onError: () => message.error('Error al revocar permiso'),
  });

  const updateMutation = useMutation({
    mutationFn: rolesPermisosService.update,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['roles-permisos'] }),
    onError: () => message.error('Error al actualizar alcance'),
  });

  const handleToggle = async (permiso) => {
    const asignado = asignadosAEsteRol.get(permiso.id);
    if (asignado) {
      await revokeMutation.mutateAsync(asignado.rpId);
    } else {
      await assignMutation.mutateAsync({
        rol: { id: rol.id },
        permiso: { id: permiso.id },
        alcance: 'completo',
      });
    }
  };

  /** Cambiar alcance de un permiso ya asignado */
  const handleAlcanceChange = async (permiso, nuevoAlcance) => {
    const asignado = asignadosAEsteRol.get(permiso.id);
    if (!asignado) return;
    await updateMutation.mutateAsync({
      id: asignado.rpId,
      rol: { id: rol.id },
      permiso: { id: permiso.id },
      alcance: nuevoAlcance,
    });
  };

  const toggleModulo = (codigo) => {
    setOpenModulos((prev) => {
      const next = new Set(prev);
      if (next.has(codigo)) next.delete(codigo);
      else next.add(codigo);
      return next;
    });
  };

  /**
   * Agrupa permisos: padres con sus hijos.
   * Detecta si es submódulo por el '.' en modulo.codigo (ej: 'ventas.pos').
   */
  const grupos = useMemo(() => {
    const map = {};
    // Primero pasar para registrar padres
    todosPermisos.forEach((p) => {
      const codigo = p.modulo?.codigo ?? '';
      if (!codigo.includes('.')) {
        if (!map[codigo]) map[codigo] = { nombre: p.modulo?.nombre ?? codigo, permiso: null, hijos: [] };
        map[codigo].permiso = p;
        map[codigo].nombre = p.modulo?.nombre ?? codigo;
        if (!map[codigo].orden) map[codigo].orden = p.modulo?.orden ?? 99;
      }
    });
    // Luego clasificar hijos
    todosPermisos.forEach((p) => {
      const codigo = p.modulo?.codigo ?? '';
      if (codigo.includes('.')) {
        const parentCodigo = codigo.substring(0, codigo.indexOf('.'));
        if (!map[parentCodigo]) map[parentCodigo] = { nombre: parentCodigo, permiso: null, hijos: [], orden: 99 };
        map[parentCodigo].hijos.push(p);
      }
    });
    // Ordenar hijos por orden del módulo
    Object.values(map).forEach((g) => {
      g.hijos.sort((a, b) => (a.modulo?.orden ?? 0) - (b.modulo?.orden ?? 0));
    });
    // Ordenar padres por orden
    return Object.entries(map).sort(([, a], [, b]) => (a.orden ?? 99) - (b.orden ?? 99));
  }, [todosPermisos]);

  const loading = loadingPermisos || loadingAsignados;
  const mutating = assignMutation.isPending || revokeMutation.isPending || updateMutation.isPending;

  const PermisoRow = ({ permiso, indent = false }) => {
    const asignado = asignadosAEsteRol.get(permiso.id);
    const activo = !!asignado;
    const moduloCodigo = permiso.modulo?.codigo ?? '';
    const soportaAlcance = PERMISOS_CON_ALCANCE.has(moduloCodigo);
    const alcanceActual = asignado?.alcance || 'completo';

    return (
      <div
        className={[
          'flex items-center gap-3 p-2.5 rounded-lg border transition-all select-none',
          indent ? 'ml-5' : '',
          activo ? 'border-green-300 bg-green-50' : 'border-gray-200 bg-white hover:bg-gray-50',
          mutating ? 'opacity-60 pointer-events-none' : '',
        ].join(' ')}
      >
        <label className="flex items-center gap-3 flex-1 min-w-0 cursor-pointer">
          <input
            type="checkbox"
            checked={activo}
            onChange={() => handleToggle(permiso)}
            className="rounded border-gray-300 text-green-600 focus:ring-green-500"
          />
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-800">{permiso.nombre}</p>
            {permiso.descripcion && (
              <p className="text-xs text-gray-500 truncate">{permiso.descripcion}</p>
            )}
          </div>
        </label>
        {/* Selector de alcance para permisos que lo soportan */}
        {activo && soportaAlcance && (
          <select
            value={alcanceActual}
            onChange={(e) => handleAlcanceChange(permiso, e.target.value)}
            onClick={(e) => e.stopPropagation()}
            className="text-xs border border-gray-300 rounded-md px-2 py-1 bg-white focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="completo">Completo</option>
            <option value="caja_asignada">Solo su caja</option>
          </select>
        )}
        {permiso.tipoAccion && (
          <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
            ACCION_COLORS[permiso.tipoAccion] || 'bg-gray-100 text-gray-600'
          }`}>
            {permiso.tipoAccion}
          </span>
        )}
        {activo && <Check size={14} className="text-green-600 shrink-0" />}
      </div>
    );
  };

  return (
    <div>
      {/* Header del rol */}
      <div className="flex items-center gap-3 px-1 pb-4 mb-4 border-b border-gray-100">
        <div className="w-9 h-9 rounded-lg bg-indigo-100 flex items-center justify-center">
          <Shield size={18} className="text-indigo-600" />
        </div>
        <div>
          <p className="font-semibold text-gray-900">{rol.nombre}</p>
          {rol.descripcion && <p className="text-xs text-gray-500">{rol.descripcion}</p>}
        </div>
        {mutating && <Loader2 size={16} className="ml-auto animate-spin text-blue-500" />}
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-8">
          <Loader2 size={24} className="animate-spin text-gray-400" />
        </div>
      ) : todosPermisos.length === 0 ? (
        <p className="text-sm text-gray-500 text-center py-8">
          No hay permisos registrados en el sistema.
        </p>
      ) : (
        <div className="space-y-2 max-h-[55vh] overflow-y-auto pr-1">
          {grupos.map(([codigoPadre, grupo]) => {
            const tieneHijos = grupo.hijos.length > 0;
            const isOpen = openModulos.has(codigoPadre);
            const todosEnGrupo = [grupo.permiso, ...grupo.hijos].filter(Boolean);
            const activosEnGrupo = todosEnGrupo.filter((p) => asignadosAEsteRol.has(p.id)).length;

            return (
              <div key={codigoPadre} className="rounded-lg border border-gray-200 overflow-hidden">
                {/* Cabecera del módulo */}
                <div
                  className={`flex items-center gap-2 px-3 py-2 bg-gray-50 ${
                    tieneHijos ? 'cursor-pointer hover:bg-gray-100' : ''
                  }`}
                  onClick={tieneHijos ? () => toggleModulo(codigoPadre) : undefined}
                >
                  <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide flex-1">
                    {grupo.nombre}
                  </p>
                  {activosEnGrupo > 0 && (
                    <span className="text-xs bg-green-100 text-green-700 px-1.5 py-0.5 rounded-full font-medium">
                      {activosEnGrupo}/{todosEnGrupo.length}
                    </span>
                  )}
                  {tieneHijos && (
                    <ChevronDown
                      size={14}
                      className={`text-gray-400 transition-transform duration-200 ${
                        isOpen ? 'rotate-180' : ''
                      }`}
                    />
                  )}
                </div>

                {/* Permisos del módulo (visible si no tiene hijos, o si está abierto) */}
                {(!tieneHijos || isOpen) && (
                  <div className="p-2 space-y-1.5">
                    {grupo.permiso && <PermisoRow permiso={grupo.permiso} indent={false} />}
                    {grupo.hijos.map((hijo) => (
                      <PermisoRow key={hijo.id} permiso={hijo} indent={true} />
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

      <div className="flex justify-end pt-4 mt-4 border-t border-gray-100">
        <Button variant="outline" onClick={onClose}>
          Cerrar
        </Button>
      </div>
    </div>
  );
};

/* ─── Tab principal ─── */
export const RolesTab = () => {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;

  const { roles, isLoading, createRol, updateRol, deleteRol, isCreating, isUpdating } = useRoles();

  const [search, setSearch] = useState('');
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingRol, setEditingRol] = useState(null);
  const [deletingId, setDeletingId] = useState(null);
  const [permisosRol, setPermisosRol] = useState(null);

  const filtered = useMemo(() => {
    const q = search.toLowerCase();
    return roles.filter(
      (r) =>
        r.nombre?.toLowerCase().includes(q) ||
        r.descripcion?.toLowerCase().includes(q),
    );
  }, [roles, search]);

  const handleCreate = async (data) => {
    await createRol(data);
    setIsCreateOpen(false);
  };

  const handleUpdate = async (data) => {
    await updateRol(data);
    setEditingRol(null);
  };

  const handleDelete = async () => {
    if (!deletingId) return;
    await deleteRol(deletingId);
    setDeletingId(null);
  };

  const columns = [
    {
      key: 'index',
      title: '#',
      width: '52px',
      render: (_, __, i) => i + 1,
    },
    {
      key: 'nombre',
      title: 'Nombre',
      render: (_, row) => (
        <div className="flex items-center gap-2">
          {row.esRolSistema ? (
            <ShieldCheck size={16} className="text-blue-500 shrink-0" />
          ) : (
            <Shield size={16} className="text-gray-400 shrink-0" />
          )}
          <div>
            <p className="font-medium text-gray-900">{row.nombre}</p>
            {row.descripcion && (
              <p className="text-xs text-gray-500 max-w-xs truncate">{row.descripcion}</p>
            )}
          </div>
        </div>
      ),
    },
    {
      key: 'tipo',
      title: 'Tipo',
      width: '120px',
      render: (_, row) =>
        row.esRolSistema ? (
          <Badge variant="info">Sistema</Badge>
        ) : (
          <Badge variant="warning">Personalizado</Badge>
        ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '90px',
      align: 'center',
      render: (_, row) =>
        row.estaActivo ? (
          <Badge variant="success">Activo</Badge>
        ) : (
          <Badge variant="error">Inactivo</Badge>
        ),
    },
    {
      key: 'creado',
      title: 'Creado',
      width: '120px',
      render: (_, row) =>
        row.creadoEn
          ? new Date(row.creadoEn).toLocaleDateString('es-PE')
          : '—',
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <div className="flex justify-center gap-1">
          <button
            title="Gestionar permisos"
            onClick={() => setPermisosRol(row)}
            className="p-1.5 rounded-md text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 transition-colors"
          >
            <Key size={15} />
          </button>
          <button
            title="Editar"
            onClick={() => setEditingRol(row)}
            disabled={row.esRolSistema}
            className="p-1.5 rounded-md text-gray-400 hover:text-amber-600 hover:bg-amber-50 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
          >
            <Edit size={15} />
          </button>
          <button
            title="Eliminar"
            onClick={() => setDeletingId(row.id)}
            disabled={row.esRolSistema}
            className="p-1.5 rounded-md text-gray-400 hover:text-red-600 hover:bg-red-50 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
          >
            <Trash2 size={15} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <>
      <Card>
        {/* Info sobre roles de sistema */}
        <div className="flex items-start gap-2 bg-blue-50 border border-blue-100 rounded-lg px-4 py-3 mb-5">
          <ShieldCheck size={16} className="text-blue-500 mt-0.5 shrink-0" />
          <p className="text-sm text-blue-700">
            Los <strong>roles de sistema</strong> son predefinidos y no pueden eliminarse.
            Usa el ícono <Key size={12} className="inline" /> para ver o asignar permisos a cualquier rol.
          </p>
        </div>

        {/* Barra superior */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-5">
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar rol..."
            className="border border-gray-300 rounded-lg px-4 py-2 text-sm w-full sm:w-72 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <Button onClick={() => setIsCreateOpen(true)} className="shrink-0">
            <Plus size={16} className="mr-1.5" />
            Nuevo rol
          </Button>
        </div>

        <Table
          columns={columns}
          data={filtered}
          loading={isLoading}
          emptyText="No se encontraron roles"
        />
      </Card>

      {/* MODAL CREAR */}
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Crear nuevo rol" size="sm">
        <RolForm
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* MODAL EDITAR */}
      <Modal
        isOpen={!!editingRol}
        onClose={() => setEditingRol(null)}
        title="Editar rol"
        size="sm"
      >
        {editingRol && (
          <RolForm
            initialData={editingRol}
            negocioId={negocioId}
            onSubmit={handleUpdate}
            onCancel={() => setEditingRol(null)}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* MODAL PERMISOS */}
      <Modal
        isOpen={!!permisosRol}
        onClose={() => setPermisosRol(null)}
        title={`Permisos — ${permisosRol?.nombre || ''}`}
        size="lg"
      >
        {permisosRol && (
          <PermisosModal rol={permisosRol} onClose={() => setPermisosRol(null)} />
        )}
      </Modal>

      {/* CONFIRM DIALOG */}
      <ConfirmDialog
        isOpen={!!deletingId}
        onClose={() => setDeletingId(null)}
        onConfirm={handleDelete}
        title="Eliminar rol"
        message="¿Deseas eliminar este rol? Asegúrate de que no tenga usuarios asignados."
        confirmText="Eliminar"
        variant="danger"
      />
    </>
  );
};



