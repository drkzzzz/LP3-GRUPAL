import { useState, useMemo } from 'react';
import { Search, Shield, Clock, Filter, Eye, ChevronDown } from 'lucide-react';
import { useAuditoria } from '../hooks/useAuditoria';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '../components/ui/Card';
import { Table } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { formatDateTime } from '@/shared/utils/formatters';

const SEVERIDAD_COLORS = {
  info: 'info',
  warning: 'warning',
  error: 'error',
  critical: 'error',
};

export const AuditoriaLogs = () => {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(15);
  const [searchTerm, setSearchTerm] = useState('');
  const [severidadFilter, setSeveridadFilter] = useState('');
  const [accionFilter, setAccionFilter] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  const { registros, isLoading } = useAuditoria();

  /* ---------- extract filter options ---------- */
  const accionOptions = useMemo(() => {
    const unique = [...new Set(registros.map((r) => r.accion).filter(Boolean))];
    return unique.sort();
  }, [registros]);

  /* ---------- client-side filtering ---------- */
  const filtered = useMemo(() => {
    let result = registros;
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (r) =>
          r.descripcion?.toLowerCase().includes(q) ||
          r.tipoEntidad?.toLowerCase().includes(q) ||
          r.usuario?.toLowerCase().includes(q) ||
          r.accion?.toLowerCase().includes(q),
      );
    }
    if (severidadFilter) {
      result = result.filter((r) => r.severidad === severidadFilter);
    }
    if (accionFilter) {
      result = result.filter((r) => r.accion === accionFilter);
    }
    return result;
  }, [registros, debouncedSearch, severidadFilter, accionFilter]);

  /* ---------- client-side pagination ---------- */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ---------- handlers ---------- */
  const handleView = (registro) => {
    setSelected(registro);
    setIsDetailOpen(true);
  };

  /* ---------- columns ---------- */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '60px',
      render: (_, __, i) => (page - 1) * pageSize + i + 1,
    },
    {
      key: 'fecha',
      title: 'Fecha',
      width: '160px',
      render: (_, row) => (
        <span className="text-sm">
          {row.creadoEn ? formatDateTime(row.creadoEn) : '—'}
        </span>
      ),
    },
    {
      key: 'usuario',
      title: 'Usuario',
      dataIndex: 'usuario',
      width: '180px',
    },
    {
      key: 'accion',
      title: 'Acción',
      dataIndex: 'accion',
      width: '140px',
      render: (val) => (
        <span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded">
          {val}
        </span>
      ),
    },
    {
      key: 'tipoEntidad',
      title: 'Entidad',
      dataIndex: 'tipoEntidad',
      width: '140px',
    },
    {
      key: 'descripcion',
      title: 'Descripción',
      dataIndex: 'descripcion',
      render: (val) => (
        <span className="text-sm text-gray-600 line-clamp-1">{val || '—'}</span>
      ),
    },
    {
      key: 'severidad',
      title: 'Severidad',
      width: '110px',
      render: (_, row) => (
        <Badge variant={SEVERIDAD_COLORS[row.severidad] || 'info'}>
          {row.severidad?.toUpperCase()}
        </Badge>
      ),
    },
    {
      key: 'actions',
      title: '',
      width: '60px',
      align: 'center',
      render: (_, row) => (
        <button
          title="Ver detalles"
          onClick={() => handleView(row)}
          className="p-1.5 rounded hover:bg-gray-100 text-blue-500 hover:text-blue-700"
        >
          <Eye size={16} />
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <div className="p-2 bg-blue-50 rounded-lg">
          <Shield size={24} className="text-blue-600" />
        </div>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Auditoría</h1>
          <p className="text-gray-600 mt-0.5">
            Registro de actividades y cambios en la plataforma
          </p>
        </div>
      </div>

      {/* Card */}
      <Card>
        {/* Search + Filters */}
        <div className="flex flex-col gap-3 mb-4">
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-1 sm:max-w-sm">
              <Search
                size={16}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <input
                type="text"
                placeholder="Buscar en logs..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setPage(1);
                }}
                className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <select
              value={severidadFilter}
              onChange={(e) => {
                setSeveridadFilter(e.target.value);
                setPage(1);
              }}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todas las severidades</option>
              <option value="info">Info</option>
              <option value="warning">Warning</option>
              <option value="error">Error</option>
              <option value="critical">Critical</option>
            </select>

            <select
              value={accionFilter}
              onChange={(e) => {
                setAccionFilter(e.target.value);
                setPage(1);
              }}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todas las acciones</option>
              {accionOptions.map((a) => (
                <option key={a} value={a}>
                  {a}
                </option>
              ))}
            </select>
          </div>

          <div className="flex items-center gap-2 text-sm text-gray-500">
            <Clock size={14} />
            <span>{filtered.length} registros encontrados</span>
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

      {/* Detail Modal */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelected(null);
        }}
        title="Detalle del Registro de Auditoría"
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500">ID</p>
                <p className="font-mono text-sm">{selected.id}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Fecha</p>
                <p className="text-sm">
                  {selected.creadoEn ? formatDateTime(selected.creadoEn) : '—'}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Usuario</p>
                <p className="text-sm font-medium">{selected.usuario || '—'}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Acción</p>
                <span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded">
                  {selected.accion}
                </span>
              </div>
              <div>
                <p className="text-sm text-gray-500">Tipo Entidad</p>
                <p className="text-sm">{selected.tipoEntidad || '—'}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">ID Entidad</p>
                <p className="font-mono text-sm">{selected.entidadId || '—'}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Severidad</p>
                <Badge variant={SEVERIDAD_COLORS[selected.severidad] || 'info'}>
                  {selected.severidad?.toUpperCase()}
                </Badge>
              </div>
              <div>
                <p className="text-sm text-gray-500">IP</p>
                <p className="font-mono text-sm">{selected.ip || '—'}</p>
              </div>
            </div>

            <div>
              <p className="text-sm text-gray-500 mb-1">Descripción</p>
              <p className="text-sm bg-gray-50 rounded-lg p-3">
                {selected.descripcion || 'Sin descripción'}
              </p>
            </div>

            {selected.datosAnteriores && (
              <div>
                <p className="text-sm text-gray-500 mb-1">Datos Anteriores</p>
                <pre className="text-xs bg-gray-50 rounded-lg p-3 overflow-x-auto">
                  {typeof selected.datosAnteriores === 'string'
                    ? selected.datosAnteriores
                    : JSON.stringify(selected.datosAnteriores, null, 2)}
                </pre>
              </div>
            )}

            {selected.datosNuevos && (
              <div>
                <p className="text-sm text-gray-500 mb-1">Datos Nuevos</p>
                <pre className="text-xs bg-gray-50 rounded-lg p-3 overflow-x-auto">
                  {typeof selected.datosNuevos === 'string'
                    ? selected.datosNuevos
                    : JSON.stringify(selected.datosNuevos, null, 2)}
                </pre>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};
