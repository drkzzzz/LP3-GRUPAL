/**
 * UsuariosTab.jsx
 * ───────────────
 * Tab CRUD de Usuarios del negocio.
 * Permite crear, editar y desactivar usuarios.
 */
import { useState, useMemo } from 'react';
import { Plus, Eye, Edit, Trash2, UserCheck, UserX } from 'lucide-react';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useUsuarios } from '../../hooks/useUsuarios';
import { useRoles } from '../../hooks/useRoles';
import { usuariosRolesService } from '../../services/usuariosClientesService';
import { UsuarioForm } from '../forms/UsuarioForm';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Button } from '@/admin/components/ui/Button';
import { Modal } from '@/admin/components/ui/Modal';
import { Card } from '@/admin/components/ui/Card';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { useDebounce } from '@/shared/hooks/useDebounce';

const TIPO_DOC_LABELS = {
  DNI: 'DNI',
  CE: 'CE',
  PASAPORTE: 'Pasaporte',
  OTRO: 'Otro',
};

export const UsuariosTab = () => {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;

  const { usuarios, isLoading, createUsuario, updateUsuario, deleteUsuario, isCreating, isUpdating } =
    useUsuarios(negocioId);

  const { roles } = useRoles();

  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 400);

  const [page, setPage] = useState(1);
  const PAGE_SIZE = 10;

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingUsuario, setEditingUsuario] = useState(null);
  const [viewingUsuario, setViewingUsuario] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  /* ─── Filtrado local ─── */
  const filtered = useMemo(() => {
    const q = debouncedSearch.toLowerCase();
    return usuarios.filter(
      (u) =>
        u.nombres?.toLowerCase().includes(q) ||
        u.apellidos?.toLowerCase().includes(q) ||
        u.email?.toLowerCase().includes(q) ||
        u.numeroDocumento?.includes(q),
    );
  }, [usuarios, debouncedSearch]);

  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    const { rolId, ...usuarioData } = data;
    const createdUser = await createUsuario(usuarioData);
    // Asignar rol si se seleccionó uno
    if (rolId && createdUser?.id) {
      try {
        await usuariosRolesService.assign({ usuario: { id: createdUser.id }, rol: { id: rolId } });
      } catch (_) {
        // El usuario fue creado; el rol podrá asignarse luego
      }
    }
    setIsCreateOpen(false);
  };

  const handleUpdate = async (data) => {
    const { rolId, ...usuarioData } = data;
    await updateUsuario(usuarioData);
    // Actualizar la asignación de rol
    try {
      const currentRoles = await usuariosRolesService.getByUsuarioId(usuarioData.id);
      for (const ur of currentRoles) {
        await usuariosRolesService.revoke(ur.id);
      }
      if (rolId) {
        await usuariosRolesService.assign({ usuario: { id: usuarioData.id }, rol: { id: rolId } });
      }
    } catch (_) {
      // no bloquear al usuario
    }
    setEditingUsuario(null);
  };

  const handleDelete = async () => {
    if (!deletingId) return;
    await deleteUsuario(deletingId);
    setDeletingId(null);
  };

  /* ─── Columnas ─── */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '52px',
      render: (_, __, i) => (page - 1) * PAGE_SIZE + i + 1,
    },
    {
      key: 'nombre',
      title: 'Nombre',
      render: (_, row) => (
        <div>
          <p className="font-medium text-gray-900">
            {row.nombres} {row.apellidos}
          </p>
          <p className="text-xs text-gray-500">{row.email}</p>
        </div>
      ),
    },
    {
      key: 'documento',
      title: 'Documento',
      width: '140px',
      render: (_, row) => (
        <span className="text-sm text-gray-700">
          {TIPO_DOC_LABELS[row.tipoDocumento] || row.tipoDocumento}: {row.numeroDocumento}
        </span>
      ),
    },
    {
      key: 'telefono',
      title: 'Teléfono',
      width: '130px',
      render: (_, row) => row.telefono || <span className="text-gray-400 text-xs">—</span>,
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
      key: 'actions',
      title: 'Acciones',
      width: '110px',
      align: 'center',
      render: (_, row) => (
        <div className="flex justify-center gap-1">
          <button
            title="Ver detalles"
            onClick={() => setViewingUsuario(row)}
            className="p-1.5 rounded-md text-gray-400 hover:text-blue-600 hover:bg-blue-50 transition-colors"
          >
            <Eye size={15} />
          </button>
          <button
            title="Editar"
            onClick={() => setEditingUsuario(row)}
            className="p-1.5 rounded-md text-gray-400 hover:text-amber-600 hover:bg-amber-50 transition-colors"
          >
            <Edit size={15} />
          </button>
          <button
            title="Desactivar"
            onClick={() => setDeletingId(row.id)}
            className="p-1.5 rounded-md text-gray-400 hover:text-red-600 hover:bg-red-50 transition-colors"
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
        {/* Barra superior */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-5">
          <input
            type="text"
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(1); }}
            placeholder="Buscar por nombre, email o documento..."
            className="border border-gray-300 rounded-lg px-4 py-2 text-sm w-full sm:w-96 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <Button onClick={() => setIsCreateOpen(true)} className="shrink-0">
            <Plus size={16} className="mr-1.5" />
            Nuevo usuario
          </Button>
        </div>

        <Table
          columns={columns}
          data={paginated}
          loading={isLoading}
          pagination={{
            current: page,
            pageSize: PAGE_SIZE,
            total: filtered.length,
            onChange: (p) => setPage(p),
          }}
          emptyText="No se encontraron usuarios"
        />
      </Card>

      {/* MODAL CREAR */}
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Nuevo usuario" size="lg">
        <UsuarioForm
          negocioId={negocioId}
          roles={roles}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* MODAL EDITAR */}
      <Modal
        isOpen={!!editingUsuario}
        onClose={() => setEditingUsuario(null)}
        title="Editar usuario"
        size="lg"
      >
        {editingUsuario && (
          <UsuarioForm
            initialData={editingUsuario}
            negocioId={negocioId}
            roles={roles}
            onSubmit={handleUpdate}
            onCancel={() => setEditingUsuario(null)}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* MODAL VER DETALLE */}
      <Modal
        isOpen={!!viewingUsuario}
        onClose={() => setViewingUsuario(null)}
        title="Detalle de usuario"
        size="md"
      >
        {viewingUsuario && (
          <div className="space-y-3 text-sm">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center">
                <span className="text-blue-700 font-bold text-lg">
                  {viewingUsuario.nombres?.[0]?.toUpperCase()}{viewingUsuario.apellidos?.[0]?.toUpperCase()}
                </span>
              </div>
              <div>
                <p className="font-semibold text-gray-900 text-base">
                  {viewingUsuario.nombres} {viewingUsuario.apellidos}
                </p>
                <p className="text-gray-500">{viewingUsuario.email}</p>
              </div>
            </div>
            {[
              ['Tipo Documento', TIPO_DOC_LABELS[viewingUsuario.tipoDocumento] || '—'],
              ['Número Documento', viewingUsuario.numeroDocumento || '—'],
              ['Teléfono', viewingUsuario.telefono || '—'],
              ['Estado', viewingUsuario.estaActivo ? 'Activo' : 'Inactivo'],
              ['Último acceso', viewingUsuario.ultimoAccesoEn
                ? new Date(viewingUsuario.ultimoAccesoEn).toLocaleString('es-PE')
                : 'Nunca'],
              ['Registrado', viewingUsuario.creadoEn
                ? new Date(viewingUsuario.creadoEn).toLocaleDateString('es-PE')
                : '—'],
            ].map(([label, value]) => (
              <div key={label} className="flex justify-between border-b border-gray-100 pb-2">
                <span className="text-gray-500">{label}</span>
                <span className="font-medium text-gray-900">{value}</span>
              </div>
            ))}
          </div>
        )}
      </Modal>

      {/* CONFIRM DIALOG */}
      <ConfirmDialog
        isOpen={!!deletingId}
        onClose={() => setDeletingId(null)}
        onConfirm={handleDelete}
        title="Desactivar usuario"
        message="¿Deseas desactivar este usuario? Perderá acceso al sistema pero se conservará su historial."
        confirmText="Desactivar"
        variant="danger"
      />
    </>
  );
};
