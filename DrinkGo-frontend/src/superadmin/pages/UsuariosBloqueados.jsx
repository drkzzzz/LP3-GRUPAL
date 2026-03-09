import { useState, useMemo } from 'react';
import { LockOpen, Lock, RefreshCw, Users, Shield } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { lockoutService } from '../services/lockoutService';
import { Card } from '../components/ui/Card';
import { Table } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { formatDateTime } from '@/shared/utils/formatters';

export const UsuariosBloqueados = () => {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(1);
  const pageSize = 15;
  const [tipoFilter, setTipoFilter] = useState('');
  const [estadoFilter, setEstadoFilter] = useState('');

  const { data: todos = [], isLoading, refetch } = useQuery({
    queryKey: ['todos-usuarios'],
    queryFn: lockoutService.getTodos,
    refetchInterval: 30000,
  });

  const desbloquearMutation = useMutation({
    mutationFn: ({ tipo, id }) =>
      tipo === 'plataforma'
        ? lockoutService.desbloquearPlataforma(id)
        : lockoutService.desbloquearAdmin(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['todos-usuarios'] });
    },
  });

  const bloqueadosCount = useMemo(() => todos.filter((u) => u.bloqueado).length, [todos]);

  const filtered = useMemo(() => {
    let result = todos;
    if (tipoFilter) result = result.filter((u) => u.tipo === tipoFilter);
    if (estadoFilter === 'bloqueado') result = result.filter((u) => u.bloqueado);
    if (estadoFilter === 'activo') result = result.filter((u) => !u.bloqueado);
    return result;
  }, [todos, tipoFilter, estadoFilter]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page]);

  const columns = [
    {
      key: 'index',
      title: '#',
      width: '55px',
      render: (_, __, i) => (page - 1) * pageSize + i + 1,
    },
    {
      key: 'tipo',
      title: 'Tipo',
      width: '130px',
      render: (_, row) =>
        row.tipo === 'plataforma' ? (
          <Badge variant="info">Plataforma</Badge>
        ) : (
          <Badge variant="warning">Admin Negocio</Badge>
        ),
    },
    {
      key: 'nombre',
      title: 'Nombre completo',
      render: (_, row) => `${row.nombres} ${row.apellidos}`,
    },
    {
      key: 'email',
      title: 'Email',
      dataIndex: 'email',
    },
    {
      key: 'negocio',
      title: 'Negocio',
      render: (_, row) =>
        row.negocio ? row.negocio.nombreComercial || row.negocio.razonSocial : '—',
    },
    {
      key: 'rol',
      title: 'Rol',
      render: (_, row) => (
        <span className="capitalize text-sm text-gray-700">{row.rol}</span>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '110px',
      render: (_, row) =>
        row.bloqueado ? (
          <Badge variant="error">Bloqueado</Badge>
        ) : (
          <Badge variant="success">Activo</Badge>
        ),
    },
    {
      key: 'intentosFallidos',
      title: 'Intentos',
      width: '85px',
      render: (_, row) => (
        <span className={row.intentosFallidos > 0 ? 'font-semibold text-orange-600' : 'text-gray-400'}>
          {row.intentosFallidos}
        </span>
      ),
    },
    {
      key: 'bloqueadoHasta',
      title: 'Bloqueado hasta',
      render: (_, row) => (row.bloqueadoHasta ? formatDateTime(row.bloqueadoHasta) : '—'),
    },
    {
      key: 'acciones',
      title: 'Acciones',
      width: '130px',
      render: (_, row) => (
        <Button
          variant={row.bloqueado ? 'danger' : 'outline'}
          size="sm"
          onClick={() => desbloquearMutation.mutate({ tipo: row.tipo, id: row.id })}
          disabled={!row.bloqueado || desbloquearMutation.isPending}
          title={row.bloqueado ? 'Desbloquear cuenta' : 'La cuenta no está bloqueada'}
        >
          <LockOpen className="w-4 h-4" />
          Desbloquear
        </Button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-blue-100 rounded-lg">
            <Shield className="w-5 h-5 text-blue-600" />
          </div>
          <div>
            <h1 className="text-xl font-semibold text-gray-900">Control de Acceso</h1>
            <p className="text-sm text-gray-500">
              {todos.length} usuarios totales
              {bloqueadosCount > 0 && (
                <span className="ml-2 text-red-600 font-medium">· {bloqueadosCount} bloqueado{bloqueadosCount !== 1 ? 's' : ''}</span>
              )}
            </p>
          </div>
        </div>
        <Button variant="outline" size="sm" onClick={() => refetch()}>
          <RefreshCw className="w-4 h-4" />
          Actualizar
        </Button>
      </div>

      {/* Filtros */}
      <div className="flex gap-3 flex-wrap">
        <select
          value={tipoFilter}
          onChange={(e) => { setTipoFilter(e.target.value); setPage(1); }}
          className="text-sm border border-gray-300 rounded-lg px-3 py-1.5 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">Todos los tipos</option>
          <option value="plataforma">Plataforma</option>
          <option value="admin">Admin Negocio</option>
        </select>

        <select
          value={estadoFilter}
          onChange={(e) => { setEstadoFilter(e.target.value); setPage(1); }}
          className="text-sm border border-gray-300 rounded-lg px-3 py-1.5 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">Todos los estados</option>
          <option value="bloqueado">Bloqueados</option>
          <option value="activo">Activos</option>
        </select>
      </div>

      {/* Tabla */}
      <Card className="p-0 overflow-hidden">
        <Table
          columns={columns}
          data={paginatedData}
          loading={isLoading}
          emptyText="No se encontraron usuarios"
          pagination={{
            current: page,
            pageSize,
            total: filtered.length,
            onChange: (p) => setPage(p),
          }}
        />
      </Card>
    </div>
  );
};
