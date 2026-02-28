/**
 * ProveedoresTab.jsx
 * ──────────────────
 * CRUD de proveedores del negocio: listado, creación, edición y eliminación.
 * Cada proveedor pertenece a un negocio (multi-tenant).
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  Truck,
  CheckCircle,
  XCircle,
  Eye,
  Phone,
  Mail,
} from 'lucide-react';
import { useProveedores } from '../../hooks/useProveedores';
import { proveedorSchema } from '../../validations/comprasSchemas';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatDateTime } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

const TIPO_DOCUMENTO_OPTIONS = [
  { value: 'RUC', label: 'RUC' },
  { value: 'DNI', label: 'DNI' },
];

export const ProveedoresTab = () => {
  const { negocioId } = useOutletContext();

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [filterEstado, setFilterEstado] = useState('todos');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const {
    proveedores,
    isLoading,
    createProveedor,
    updateProveedor,
    deleteProveedor,
    isCreating,
    isUpdating,
    isDeleting,
  } = useProveedores(negocioId);

  /* ─── Form ─── */
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(proveedorSchema),
    defaultValues: {
      razonSocial: '',
      nombreComercial: '',
      tipoDocumento: '',
      numeroDocumento: '',
      direccion: '',
      telefono: '',
      email: '',
      contactoPrincipal: '',
      telefonoContacto: '',
      emailContacto: '',
      observaciones: '',
      estaActivo: true,
    },
  });

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    let result = proveedores;

    if (filterEstado !== 'todos') {
      const isActive = filterEstado === 'activos';
      result = result.filter((p) => (p.estaActivo !== false) === isActive);
    }

    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (p) =>
          p.razonSocial?.toLowerCase().includes(q) ||
          p.nombreComercial?.toLowerCase().includes(q) ||
          p.numeroDocumento?.toLowerCase().includes(q) ||
          p.email?.toLowerCase().includes(q),
      );
    }

    return result;
  }, [proveedores, debouncedSearch, filterEstado]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(
    () => ({
      total: proveedores.length,
      activos: proveedores.filter((p) => p.estaActivo !== false).length,
      inactivos: proveedores.filter((p) => p.estaActivo === false).length,
    }),
    [proveedores],
  );

  /* ─── Handlers ─── */
  const openCreate = () => {
    setEditing(null);
    reset({
      razonSocial: '',
      nombreComercial: '',
      tipoDocumento: '',
      numeroDocumento: '',
      direccion: '',
      telefono: '',
      email: '',
      contactoPrincipal: '',
      telefonoContacto: '',
      emailContacto: '',
      observaciones: '',
      estaActivo: true,
    });
    setIsFormOpen(true);
  };

  const openEdit = (item) => {
    setEditing(item);
    reset({
      razonSocial: item.razonSocial || '',
      nombreComercial: item.nombreComercial || '',
      tipoDocumento: item.tipoDocumento || '',
      numeroDocumento: item.numeroDocumento || '',
      direccion: item.direccion || '',
      telefono: item.telefono || '',
      email: item.email || '',
      contactoPrincipal: item.contactoPrincipal || '',
      telefonoContacto: item.telefonoContacto || '',
      emailContacto: item.emailContacto || '',
      observaciones: item.observaciones || '',
      estaActivo: item.estaActivo !== false,
    });
    setIsFormOpen(true);
  };

  const openDetail = (item) => {
    setSelected(item);
    setIsDetailOpen(true);
  };

  const openDelete = (item) => {
    setSelected(item);
    setIsDeleteOpen(true);
  };

  const onSubmit = async (formData) => {
    const payload = {
      negocio: { id: negocioId },
      razonSocial: formData.razonSocial.trim(),
      nombreComercial: formData.nombreComercial?.trim() || null,
      tipoDocumento: formData.tipoDocumento,
      numeroDocumento: formData.numeroDocumento.trim(),
      direccion: formData.direccion?.trim() || null,
      telefono: formData.telefono?.trim() || null,
      email: formData.email?.trim() || null,
      contactoPrincipal: formData.contactoPrincipal?.trim() || null,
      telefonoContacto: formData.telefonoContacto?.trim() || null,
      emailContacto: formData.emailContacto?.trim() || null,
      observaciones: formData.observaciones?.trim() || null,
      estaActivo: formData.estaActivo,
    };

    if (editing) {
      await updateProveedor({ ...payload, id: editing.id });
    } else {
      await createProveedor(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteProveedor(selected.id);
    setIsDeleteOpen(false);
    setSelected(null);
  };

  /* ─── Columnas ─── */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => (
        <span className="text-gray-400 text-xs">{(page - 1) * pageSize + i + 1}</span>
      ),
    },
    {
      key: 'razonSocial',
      title: 'Razón Social',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.razonSocial}</p>
          {row.nombreComercial && (
            <p className="text-xs text-gray-500 truncate">{row.nombreComercial}</p>
          )}
        </div>
      ),
    },
    {
      key: 'documento',
      title: 'Documento',
      width: '160px',
      render: (_, row) => (
        <div>
          <span className="text-xs text-gray-500">{row.tipoDocumento}: </span>
          <span className="text-sm font-mono text-gray-900">{row.numeroDocumento}</span>
        </div>
      ),
    },
    {
      key: 'contacto',
      title: 'Contacto',
      width: '180px',
      render: (_, row) => (
        <div className="space-y-0.5">
          {row.telefono && (
            <div className="flex items-center gap-1 text-xs text-gray-600">
              <Phone size={12} />
              <span>{row.telefono}</span>
            </div>
          )}
          {row.email && (
            <div className="flex items-center gap-1 text-xs text-gray-600">
              <Mail size={12} />
              <span className="truncate">{row.email}</span>
            </div>
          )}
        </div>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '100px',
      align: 'center',
      render: (_, row) =>
        row.estaActivo !== false ? (
          <Badge variant="success">Activo</Badge>
        ) : (
          <Badge variant="error">Inactivo</Badge>
        ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <div className="flex items-center justify-center gap-1">
          <button
            title="Ver detalle"
            onClick={() => openDetail(row)}
            className="p-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition-colors"
          >
            <Eye size={15} />
          </button>
          <button
            title="Editar"
            onClick={() => openEdit(row)}
            className="p-1.5 rounded hover:bg-blue-50 text-blue-500 hover:text-blue-700 transition-colors"
          >
            <Pencil size={15} />
          </button>
          <button
            title="Eliminar"
            onClick={() => openDelete(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
          >
            <Trash2 size={15} />
          </button>
        </div>
      ),
    },
  ];

  const isSaving = isCreating || isUpdating;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Proveedores</h1>
          <p className="text-gray-600 mt-1">
            Gestión de proveedores del negocio
          </p>
        </div>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
        >
          <Plus size={18} />
          Nuevo Proveedor
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard title="Total proveedores" value={stats.total} icon={Truck} />
        <StatCard
          title="Activos"
          value={stats.activos}
          icon={CheckCircle}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Inactivos"
          value={stats.inactivos}
          icon={XCircle}
          className="border-l-4 border-l-red-500"
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-72">
            <Search
              size={16}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <input
              type="text"
              placeholder="Buscar por razón social, RUC..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPage(1);
              }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <div className="flex items-center gap-2">
            <select
              value={filterEstado}
              onChange={(e) => {
                setFilterEstado(e.target.value);
                setPage(1);
              }}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="todos">Todos los estados</option>
              <option value="activos">Activos</option>
              <option value="inactivos">Inactivos</option>
            </select>
          </div>
        </div>

        <Table
          columns={columns}
          data={paginatedData}
          loading={isLoading}
          pagination={{
            current: page,
            pageSize,
            total: filtered.length,
            onChange: (newPage, newSize) => {
              setPage(newPage);
              setPageSize(newSize);
            },
          }}
        />
      </Card>

      {/* ─── Modal: Crear / Editar Proveedor ─── */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => {
          setIsFormOpen(false);
          setEditing(null);
        }}
        title={editing ? 'Editar Proveedor' : 'Nuevo Proveedor'}
        size="lg"
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {/* Datos principales */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Razón Social"
              required
              placeholder="Distribuidora ABC S.A.C."
              {...register('razonSocial')}
              error={errors.razonSocial?.message}
            />
            <Input
              label="Nombre Comercial"
              placeholder="ABC Licores"
              {...register('nombreComercial')}
              error={errors.nombreComercial?.message}
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Select
              label="Tipo de Documento"
              required
              placeholder="Seleccione..."
              options={TIPO_DOCUMENTO_OPTIONS}
              {...register('tipoDocumento')}
              error={errors.tipoDocumento?.message}
            />
            <Input
              label="Número de Documento"
              required
              placeholder="20123456789"
              {...register('numeroDocumento')}
              error={errors.numeroDocumento?.message}
            />
          </div>

          <Input
            label="Dirección"
            placeholder="Av. Principal 123, Lima"
            {...register('direccion')}
            error={errors.direccion?.message}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Teléfono"
              placeholder="01-234-5678"
              {...register('telefono')}
              error={errors.telefono?.message}
            />
            <Input
              label="Email"
              type="email"
              placeholder="ventas@proveedor.com"
              {...register('email')}
              error={errors.email?.message}
            />
          </div>

          {/* Contacto */}
          <div className="border-t border-gray-200 pt-4">
            <h3 className="text-sm font-semibold text-gray-700 mb-3">Persona de Contacto</h3>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <Input
                label="Nombre"
                placeholder="Juan Pérez"
                {...register('contactoPrincipal')}
                error={errors.contactoPrincipal?.message}
              />
              <Input
                label="Teléfono"
                placeholder="987654321"
                {...register('telefonoContacto')}
                error={errors.telefonoContacto?.message}
              />
              <Input
                label="Email de contacto"
                type="email"
                placeholder="contacto@proveedor.com"
                {...register('emailContacto')}
                error={errors.emailContacto?.message}
              />
            </div>
          </div>

          <Textarea
            label="Observaciones"
            placeholder="Notas adicionales sobre el proveedor..."
            rows={2}
            {...register('observaciones')}
            error={errors.observaciones?.message}
          />

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="estaActivo"
              {...register('estaActivo')}
              className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
            />
            <label htmlFor="estaActivo" className="text-sm text-gray-700">
              Proveedor activo
            </label>
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <Button
              type="button"
              variant="secondary"
              onClick={() => {
                setIsFormOpen(false);
                setEditing(null);
              }}
            >
              Cancelar
            </Button>
            <Button type="submit" disabled={isSaving}>
              {isSaving ? 'Guardando...' : editing ? 'Guardar cambios' : 'Crear proveedor'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* ─── Modal: Detalle de Proveedor ─── */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelected(null);
        }}
        title="Detalle del Proveedor"
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Razón Social</p>
                <p className="text-sm text-gray-900">{selected.razonSocial}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Nombre Comercial</p>
                <p className="text-sm text-gray-900">{selected.nombreComercial || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Documento</p>
                <p className="text-sm text-gray-900">
                  {selected.tipoDocumento}: {selected.numeroDocumento}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Estado</p>
                {selected.estaActivo !== false ? (
                  <Badge variant="success">Activo</Badge>
                ) : (
                  <Badge variant="error">Inactivo</Badge>
                )}
              </div>
            </div>

            <div className="border-t border-gray-200 pt-3">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Dirección</p>
                  <p className="text-sm text-gray-900">{selected.direccion || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Teléfono</p>
                  <p className="text-sm text-gray-900">{selected.telefono || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Email</p>
                  <p className="text-sm text-gray-900">{selected.email || '—'}</p>
                </div>
              </div>
            </div>

            <div className="border-t border-gray-200 pt-3">
              <h3 className="text-sm font-semibold text-gray-700 mb-2">Persona de Contacto</h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Nombre</p>
                  <p className="text-sm text-gray-900">{selected.contactoPrincipal || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Teléfono</p>
                  <p className="text-sm text-gray-900">{selected.telefonoContacto || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Email</p>
                  <p className="text-sm text-gray-900">{selected.emailContacto || '—'}</p>
                </div>
              </div>
            </div>

            {selected.observaciones && (
              <div className="border-t border-gray-200 pt-3">
                <p className="text-xs text-gray-500 uppercase font-semibold">Observaciones</p>
                <p className="text-sm text-gray-900">{selected.observaciones}</p>
              </div>
            )}

            <div className="border-t border-gray-200 pt-3">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Creado</p>
                  <p className="text-sm text-gray-900">{formatDateTime(selected.creadoEn)}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500 uppercase font-semibold">Actualizado</p>
                  <p className="text-sm text-gray-900">{formatDateTime(selected.actualizadoEn)}</p>
                </div>
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <Button
                variant="secondary"
                onClick={() => {
                  setIsDetailOpen(false);
                  setSelected(null);
                }}
              >
                Cerrar
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* ─── Modal: Confirmar eliminación ─── */}
      <Modal
        isOpen={isDeleteOpen}
        onClose={() => {
          setIsDeleteOpen(false);
          setSelected(null);
        }}
        title="Eliminar Proveedor"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar al proveedor{' '}
            <strong className="text-gray-900">{selected?.razonSocial}</strong>?
            Esta acción no se puede deshacer.
          </p>
          <div className="flex justify-end gap-3">
            <Button
              variant="secondary"
              onClick={() => {
                setIsDeleteOpen(false);
                setSelected(null);
              }}
            >
              Cancelar
            </Button>
            <Button variant="danger" onClick={handleDelete} disabled={isDeleting}>
              {isDeleting ? 'Eliminando...' : 'Eliminar'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
