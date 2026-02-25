import { useState, useEffect } from 'react';
import { Search, Download, AlertTriangle, Info, AlertCircle } from 'lucide-react';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';
import auditoriaService from '../services/auditoriaService';

const severityConfig = {
  info: { color: 'bg-blue-100 text-blue-700', icon: Info, label: 'Info' },
  warning: { color: 'bg-yellow-100 text-yellow-700', icon: AlertTriangle, label: 'Advertencia' },
  critical: { color: 'bg-red-100 text-red-700', icon: AlertCircle, label: 'Crítico' },
};

export default function AuditoriaLogs() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    busqueda: '',
    fecha_inicio: '',
    fecha_fin: '',
    severidad: '',
    modulo: '',
  });
  const [selectedLog, setSelectedLog] = useState(null);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalElements: 0,
  });

  useEffect(() => {
    fetchLogs();
  }, [filters, pagination.page]);

  const fetchLogs = async () => {
    setLoading(true);
    try {
      const response = await auditoriaService.getAll({
        ...filters,
        page: pagination.page,
        size: pagination.size,
      });
      setLogs(response.content || response);
      if (response.totalElements) {
        setPagination((prev) => ({
          ...prev,
          totalElements: response.totalElements,
        }));
      }
    } catch (error) {
      console.error('Error cargando logs:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async (format) => {
    try {
      const blob = await auditoriaService.exportLogs(filters, format);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `auditoria_${Date.now()}.${format}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      alert('Error al exportar logs');
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
    setPagination((prev) => ({ ...prev, page: 0 }));
  };

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Auditoría y Logs del Sistema</h1>
        <p className="text-gray-600 mt-1">Registro completo de actividades críticas</p>
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-lg shadow p-4 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
          <div className="md:col-span-2">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
              <input
                type="text"
                name="busqueda"
                value={filters.busqueda}
                onChange={handleFilterChange}
                placeholder="Buscar en logs..."
                className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <div>
            <input
              type="date"
              name="fecha_inicio"
              value={filters.fecha_inicio}
              onChange={handleFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              placeholder="Fecha inicio"
            />
          </div>

          <div>
            <input
              type="date"
              name="fecha_fin"
              value={filters.fecha_fin}
              onChange={handleFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              placeholder="Fecha fin"
            />
          </div>

          <div>
            <select
              name="severidad"
              value={filters.severidad}
              onChange={handleFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
            >
              <option value="">Todas las severidades</option>
              <option value="info">Info</option>
              <option value="warning">Advertencia</option>
              <option value="critical">Crítico</option>
            </select>
          </div>
        </div>

        <div className="mt-4 flex justify-between items-center">
          <button
            onClick={fetchLogs}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Aplicar Filtros
          </button>

          <div className="flex gap-2">
            <button
              onClick={() => handleExport('csv')}
              className="flex items-center px-3 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              <Download className="h-4 w-4 mr-2" />
              Exportar CSV
            </button>
            <button
              onClick={() => handleExport('json')}
              className="flex items-center px-3 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              <Download className="h-4 w-4 mr-2" />
              Exportar JSON
            </button>
          </div>
        </div>
      </div>

      {/* Tabla de Logs */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-gray-500">Cargando logs...</div>
        ) : logs.length === 0 ? (
          <div className="p-8 text-center text-gray-500">No se encontraron registros</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Fecha/Hora
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Usuario
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Acción
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Módulo
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Severidad
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    IP
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {logs.map((log) => {
                  const SeverityIcon = severityConfig[log.severidad]?.icon || Info;
                  const severityColor = severityConfig[log.severidad]?.color || 'bg-gray-100 text-gray-700';

                  return (
                    <tr key={log.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {log.creado_en ? format(new Date(log.creado_en), 'dd/MM/yyyy HH:mm', { locale: es }) : '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {log.usuario_email || 'Sistema'}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        <div className="max-w-xs truncate">{log.accion}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                        {log.modulo || '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${severityColor}`}>
                          <SeverityIcon className="h-3 w-3 mr-1" />
                          {severityConfig[log.severidad]?.label || log.severidad}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                        {log.ip_origen || '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">
                        <button
                          onClick={() => setSelectedLog(log)}
                          className="text-blue-600 hover:text-blue-900 font-medium"
                        >
                          Ver Detalle
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Paginación */}
        {logs.length > 0 && (
          <div className="px-6 py-4 border-t border-gray-200 flex items-center justify-between">
            <div className="text-sm text-gray-700">
              Mostrando {pagination.page * pagination.size + 1} -{' '}
              {Math.min((pagination.page + 1) * pagination.size, pagination.totalElements)} de{' '}
              {pagination.totalElements} registros
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => setPagination((prev) => ({ ...prev, page: Math.max(0, prev.page - 1) }))}
                disabled={pagination.page === 0}
                className="px-3 py-1 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Anterior
              </button>
              <button
                onClick={() => setPagination((prev) => ({ ...prev, page: prev.page + 1 }))}
                disabled={(pagination.page + 1) * pagination.size >= pagination.totalElements}
                className="px-3 py-1 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Siguiente
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Modal de Detalle */}
      {selectedLog && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[80vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-start mb-4">
                <h2 className="text-xl font-bold">Detalle del Registro de Auditoría</h2>
                <button
                  onClick={() => setSelectedLog(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="text-sm font-medium text-gray-700">Usuario</label>
                  <p className="mt-1 text-gray-900">{selectedLog.usuario_email || 'Sistema'}</p>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-700">Acción</label>
                  <p className="mt-1 text-gray-900">{selectedLog.accion}</p>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-700">Descripción</label>
                  <p className="mt-1 text-gray-900">{selectedLog.descripcion || 'Sin descripción'}</p>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm font-medium text-gray-700">Fecha y Hora</label>
                    <p className="mt-1 text-gray-900">
                      {selectedLog.creado_en
                        ? format(new Date(selectedLog.creado_en), "dd/MM/yyyy HH:mm:ss", { locale: es })
                        : '-'}
                    </p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-700">IP de Origen</label>
                    <p className="mt-1 text-gray-900">{selectedLog.ip_origen || '-'}</p>
                  </div>
                </div>

                {selectedLog.datos_adicionales && (
                  <div>
                    <label className="text-sm font-medium text-gray-700">Datos Adicionales</label>
                    <pre className="mt-1 p-3 bg-gray-50 rounded text-sm overflow-x-auto">
                      {JSON.stringify(JSON.parse(selectedLog.datos_adicionales), null, 2)}
                    </pre>
                  </div>
                )}
              </div>

              <div className="mt-6 flex justify-end">
                <button
                  onClick={() => setSelectedLog(null)}
                  className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
