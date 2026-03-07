/**
 * ClientesTab.jsx
 * ───────────────
 * Tab CRUD de Clientes del negocio.
 * Incluye búsqueda, paginación, consulta DNI/RUC y validación de mayoría de edad.
 */
import { useState, useMemo } from 'react';
import { Plus, Eye, Edit, Trash2, UserCheck, RefreshCw } from 'lucide-react';
import { differenceInYears, parseISO } from 'date-fns';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useClientes } from '../../hooks/useClientes';
import { ClienteForm } from '../forms/ClienteForm';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Button } from '@/admin/components/ui/Button';
import { Modal } from '@/admin/components/ui/Modal';
import { Card } from '@/admin/components/ui/Card';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { useDebounce } from '@/shared/hooks/useDebounce';

const TIPO_DOC_LABELS = {
  DNI: 'DNI',
  RUC: 'RUC',
  CE: 'CE',
  PASAPORTE: 'Pasaporte',
  OTRO: 'Otro',
};

const calcEdad = (fecha) => {
  if (!fecha) return null;
  try { return differenceInYears(new Date(), parseISO(fecha)); }
  catch { return null; }
};

const formatCurrencyPEN = (val) =>
  val != null
    ? new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(val)
    : '—';

export const ClientesTab = () => {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;

  const { clientes, isLoading, refetch, createCliente, updateCliente, deleteCliente, isCreating, isUpdating } =
    useClientes(negocioId);

  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 400);

  const [page, setPage] = useState(1);
  const PAGE_SIZE = 10;

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [viewingCliente, setViewingCliente] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  /* ─── Filtrado local ─── */
  const filtered = useMemo(() => {
    const q = debouncedSearch.toLowerCase();
    return clientes.filter(
      (c) =>
        c.nombres?.toLowerCase().includes(q) ||
        c.apellidos?.toLowerCase().includes(q) ||
        c.razonSocial?.toLowerCase().includes(q) ||
        c.email?.toLowerCase().includes(q) ||
        c.numeroDocumento?.includes(q) ||
        c.telefono?.includes(q),
    );
  }, [clientes, debouncedSearch]);

  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    await createCliente(data);
    setIsCreateOpen(false);
  };

  const handleUpdate = async (data) => {
    await updateCliente(data);
    setEditingCliente(null);
  };

  const handleDelete = async () => {
    if (!deletingId) return;
    await deleteCliente(deletingId);
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
      key: 'cliente',
      title: 'Cliente',
      render: (_, row) => {
        const nombre =
          row.tipoDocumento === 'RUC'
            ? row.razonSocial || row.nombreComercial
            : `${row.nombres || ''} ${row.apellidos || ''}`.trim();
        const edad = calcEdad(row.fechaNacimiento);
        return (
          <div>
            <p className="font-medium text-gray-900">{nombre || '—'}</p>
            <p className="text-xs text-gray-500">
              {TIPO_DOC_LABELS[row.tipoDocumento]}: {row.numeroDocumento}
            </p>
            {edad !== null && (
              <p className="text-xs text-gray-400">{edad} años</p>
            )}
          </div>
        );
      },
    },
    {
      key: 'contacto',
      title: 'Contacto',
      render: (_, row) => (
        <div className="text-sm">
          {row.email && <p className="text-gray-700">{row.email}</p>}
          {row.telefono && <p className="text-gray-500">{row.telefono}</p>}
          {!row.email && !row.telefono && <span className="text-gray-400 text-xs">Sin contacto</span>}
        </div>
      ),
    },
    {
      key: 'totalCompras',
      title: 'Total Compras',
      width: '130px',
      align: 'right',
      render: (_, row) => (
        <span className="font-medium text-gray-800">
          {formatCurrencyPEN(row.totalCompras)}
        </span>
      ),
    },
    {
      key: 'ultimaCompra',
      title: 'Última Compra',
      width: '140px',
      render: (_, row) =>
        row.ultimaCompraEn ? (
          <span className="text-sm text-gray-600">
            {new Date(row.ultimaCompraEn).toLocaleDateString('es-PE')}
          </span>
        ) : (
          <span className="text-gray-400 text-xs">Sin compras</span>
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
            onClick={() => setViewingCliente(row)}
            className="p-1.5 rounded-md text-gray-400 hover:text-blue-600 hover:bg-blue-50 transition-colors"
          >
            <Eye size={15} />
          </button>
          <button
            title="Editar"
            onClick={() => setEditingCliente(row)}
            className="p-1.5 rounded-md text-gray-400 hover:text-amber-600 hover:bg-amber-50 transition-colors"
          >
            <Edit size={15} />
          </button>
          <button
            title="Eliminar"
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
            placeholder="Buscar por nombre, documento, email o teléfono..."
            className="border border-gray-300 rounded-lg px-4 py-2 text-sm w-full sm:w-96 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <div className="flex gap-2 shrink-0">
            <Button variant="outline" onClick={() => refetch()} disabled={isLoading}>
              <RefreshCw size={16} className={`mr-1.5 ${isLoading ? 'animate-spin' : ''}`} />
              Actualizar
            </Button>
            <Button onClick={() => setIsCreateOpen(true)}>
              <Plus size={16} className="mr-1.5" />
              Nuevo cliente
            </Button>
          </div>
        </div>

        {/* Estadística rápida */}
        <div className="flex gap-4 mb-4">
          <div className="bg-blue-50 rounded-lg px-4 py-2 text-center">
            <p className="text-xs text-blue-600 font-medium">Total Clientes</p>
            <p className="text-xl font-bold text-blue-700">{clientes.length}</p>
          </div>
          <div className="bg-green-50 rounded-lg px-4 py-2 text-center">
            <p className="text-xs text-green-600 font-medium">Resultado búsqueda</p>
            <p className="text-xl font-bold text-green-700">{filtered.length}</p>
          </div>
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
          emptyText="No se encontraron clientes"
        />
      </Card>

      {/* MODAL CREAR */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Registrar nuevo cliente"
        size="lg"
      >
        <ClienteForm
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* MODAL EDITAR */}
      <Modal
        isOpen={!!editingCliente}
        onClose={() => setEditingCliente(null)}
        title="Editar cliente"
        size="lg"
      >
        {editingCliente && (
          <ClienteForm
            initialData={editingCliente}
            negocioId={negocioId}
            onSubmit={handleUpdate}
            onCancel={() => setEditingCliente(null)}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* MODAL VER DETALLE */}
      <Modal
        isOpen={!!viewingCliente}
        onClose={() => setViewingCliente(null)}
        title="Detalle del cliente"
        size="md"
      >
        {viewingCliente && (
          <div className="space-y-3 text-sm">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center">
                <UserCheck size={22} className="text-green-700" />
              </div>
              <div>
                <p className="font-semibold text-gray-900 text-base">
                  {viewingCliente.tipoDocumento === 'RUC'
                    ? viewingCliente.razonSocial
                    : `${viewingCliente.nombres || ''} ${viewingCliente.apellidos || ''}`.trim()}
                </p>
                <p className="text-gray-500 text-xs">
                  {TIPO_DOC_LABELS[viewingCliente.tipoDocumento]}: {viewingCliente.numeroDocumento}
                </p>
              </div>
            </div>
            {[
              ['Email', viewingCliente.email || '—'],
              ['Teléfono', viewingCliente.telefono || '—'],
              ['Fecha Nacimiento', viewingCliente.fechaNacimiento || '—'],
              ['Edad', viewingCliente.fechaNacimiento ? `${calcEdad(viewingCliente.fechaNacimiento)} años` : '—'],
              ['Dirección', viewingCliente.direccion || '—'],
              ['Total Compras', formatCurrencyPEN(viewingCliente.totalCompras)],
              ['Última Compra', viewingCliente.ultimaCompraEn
                ? new Date(viewingCliente.ultimaCompraEn).toLocaleDateString('es-PE')
                : 'Sin compras'],
              ['Registrado', viewingCliente.creadoEn
                ? new Date(viewingCliente.creadoEn).toLocaleDateString('es-PE')
                : '—'],
            ].map(([label, value]) => (
              <div key={label} className="flex justify-between border-b border-gray-100 pb-2">
                <span className="text-gray-500">{label}</span>
                <span className="font-medium text-gray-900 text-right max-w-xs">{value}</span>
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
        title="Eliminar cliente"
        message="¿Estás seguro de eliminar este cliente? Esta acción no se puede deshacer."
        confirmText="Eliminar"
        variant="danger"
      />
    </>
  );
};
