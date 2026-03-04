import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
  Wine,
  LogOut,
  Building2,
  MapPin,
  ChevronRight,
  Loader2,
  Search,
  AlertCircle,
  Code2,
} from 'lucide-react';
import { programadorService } from '../services/programadorService';
import { useAuthStore } from '@/stores/authStore';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { message } from '@/shared/utils/notifications';

const ESTADO_COLORS = {
  activo: 'bg-green-100 text-green-700',
  pendiente: 'bg-yellow-100 text-yellow-700',
  suspendido: 'bg-orange-100 text-orange-700',
  cancelado: 'bg-red-100 text-red-700',
};

export const ProgramadorNegocios = () => {
  const navigate = useNavigate();
  const { user, token, logout: superadminLogout, getModulosAsignados } = useAuthStore();
  const { loginProgramador } = useAdminAuthStore();

  const [busqueda, setBusqueda] = useState('');
  const [negocioExpandido, setNegocioExpandido] = useState(null);

  const modulos = getModulosAsignados();

  const { data: negocios = [], isLoading, isError, refetch } = useQuery({
    queryKey: ['programador-negocios-con-sedes'],
    queryFn: programadorService.getNegociosConSedes,
    retry: 1,
    staleTime: 30_000,
  });

  const negociosFiltrados = negocios.filter((n) => {
    const q = busqueda.toLowerCase();
    return (
      (n.nombreComercial ?? '').toLowerCase().includes(q) ||
      (n.razonSocial ?? '').toLowerCase().includes(q) ||
      (n.ruc ?? '').includes(q)
    );
  });

  const handleSeleccionarSede = (negocio, sede) => {
    loginProgramador(user, token, negocio, sede, modulos);
    message.success(`Ingresando a ${negocio.nombreComercial ?? negocio.razonSocial} — ${sede.nombre}`);
    navigate('/admin/dashboard', { replace: true });
  };

  const handleLogout = () => {
    superadminLogout();
    navigate('/superadmin/login', { replace: true });
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-gray-900 border-b border-gray-700 px-6 py-4">
        <div className="max-w-6xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-600 rounded-lg">
              <Wine size={20} className="text-white" />
            </div>
            <div>
              <h1 className="text-lg font-bold text-white">DrinkGo</h1>
              <p className="text-xs text-gray-400">Modo Programador</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            {/* Módulos asignados */}
            <div className="hidden sm:flex items-center gap-2 bg-gray-800 rounded-lg px-3 py-1.5">
              <Code2 size={14} className="text-blue-400" />
              <span className="text-xs text-gray-300">
                {modulos.length > 0
                  ? modulos.map((m) => m.replace('m.', '')).join(', ')
                  : 'Sin módulos asignados'}
              </span>
            </div>

            {/* Usuario */}
            <span className="text-sm text-gray-300 hidden sm:block">
              {user?.nombres} {user?.apellidos}
            </span>

            <button
              onClick={handleLogout}
              className="flex items-center gap-2 text-gray-400 hover:text-white text-sm transition-colors"
            >
              <LogOut size={16} />
              <span className="hidden sm:block">Salir</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-8">
        {/* Page title */}
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Selecciona un Negocio</h2>
          <p className="text-sm text-gray-500 mt-1">
            Elige el negocio y la sede donde quieres probar tus módulos asignados.
          </p>
          {modulos.length > 0 && (
            <div className="mt-3 flex flex-wrap gap-2">
              {modulos.map((m) => (
                <span
                  key={m}
                  className="inline-flex items-center gap-1 bg-blue-50 text-blue-700 text-xs font-medium px-2.5 py-1 rounded-full border border-blue-100"
                >
                  <Code2 size={10} />
                  {m.replace('m.', '')}
                </span>
              ))}
            </div>
          )}
        </div>

        {/* Search */}
        <div className="relative mb-6">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Buscar negocio por nombre o RUC..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            className="w-full pl-9 pr-4 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
          />
        </div>

        {/* Loading state */}
        {isLoading && (
          <div className="flex items-center justify-center py-20">
            <Loader2 size={32} className="animate-spin text-blue-600" />
          </div>
        )}

        {/* Error state */}
        {isError && (
          <div className="flex flex-col items-center justify-center py-20 gap-3">
            <AlertCircle size={40} className="text-red-400" />
            <p className="text-gray-600 text-sm">No se pudieron cargar los negocios.</p>
            <button
              onClick={() => refetch()}
              className="text-sm text-blue-600 hover:text-blue-800 font-medium"
            >
              Reintentar
            </button>
          </div>
        )}

        {/* Empty state */}
        {!isLoading && !isError && negociosFiltrados.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20 gap-3">
            <Building2 size={40} className="text-gray-300" />
            <p className="text-gray-500 text-sm">
              {busqueda ? 'No se encontraron resultados.' : 'No hay negocios registrados aún.'}
            </p>
          </div>
        )}

        {/* Negocios grid */}
        {!isLoading && !isError && negociosFiltrados.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {negociosFiltrados.map((negocio) => {
              const isExpanded = negocioExpandido === negocio.id;
              const tieneLabel =
                negocio.nombreComercial && negocio.nombreComercial !== negocio.razonSocial;

              return (
                <div
                  key={negocio.id}
                  className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden"
                >
                  {/* Cabecera del negocio */}
                  <button
                    onClick={() => setNegocioExpandido(isExpanded ? null : negocio.id)}
                    className="w-full p-4 text-left hover:bg-gray-50 transition-colors"
                  >
                    <div className="flex items-start gap-3">
                      {/* Logo / placeholder */}
                      <div className="w-10 h-10 rounded-lg bg-blue-100 flex items-center justify-center shrink-0 overflow-hidden">
                        {negocio.urlLogo ? (
                          <img
                            src={negocio.urlLogo}
                            alt={negocio.nombreComercial}
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <Building2 size={18} className="text-blue-600" />
                        )}
                      </div>

                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-semibold text-gray-900 truncate">
                          {negocio.nombreComercial ?? negocio.razonSocial}
                        </p>
                        {tieneLabel && (
                          <p className="text-xs text-gray-400 truncate">{negocio.razonSocial}</p>
                        )}
                        <div className="flex items-center gap-2 mt-1">
                          {negocio.ruc && (
                            <span className="text-xs text-gray-400">RUC {negocio.ruc}</span>
                          )}
                          <span
                            className={`text-xs font-medium px-1.5 py-0.5 rounded-full ${
                              ESTADO_COLORS[negocio.estado] ?? 'bg-gray-100 text-gray-600'
                            }`}
                          >
                            {negocio.estado}
                          </span>
                        </div>
                      </div>

                      <ChevronRight
                        size={16}
                        className={`text-gray-400 transition-transform shrink-0 mt-0.5 ${
                          isExpanded ? 'rotate-90' : ''
                        }`}
                      />
                    </div>
                  </button>

                  {/* Sedes (expandibles) */}
                  {isExpanded && (
                    <div className="border-t border-gray-100 bg-gray-50 px-4 py-3 space-y-2">
                      {negocio.sedes && negocio.sedes.length > 0 ? (
                        negocio.sedes.map((sede) => (
                          <button
                            key={sede.id}
                            onClick={() => handleSeleccionarSede(negocio, sede)}
                            className="w-full flex items-center gap-3 p-2.5 rounded-lg bg-white border border-gray-200 hover:border-blue-400 hover:bg-blue-50 transition-colors group text-left"
                          >
                            <div className="w-7 h-7 rounded-md bg-blue-50 flex items-center justify-center shrink-0 group-hover:bg-blue-100">
                              <MapPin size={13} className="text-blue-600" />
                            </div>
                            <div className="flex-1 min-w-0">
                              <p className="text-sm font-medium text-gray-800 truncate group-hover:text-blue-700">
                                {sede.nombre}
                              </p>
                              {sede.ciudad && (
                                <p className="text-xs text-gray-400">{sede.ciudad}</p>
                              )}
                            </div>
                            {sede.esPrincipal && (
                              <span className="text-xs bg-blue-100 text-blue-600 px-1.5 py-0.5 rounded-full font-medium shrink-0">
                                Principal
                              </span>
                            )}
                            <ChevronRight size={14} className="text-gray-300 group-hover:text-blue-500 shrink-0" />
                          </button>
                        ))
                      ) : (
                        <p className="text-xs text-gray-400 text-center py-2">
                          Este negocio no tiene sedes registradas.
                        </p>
                      )}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
};
