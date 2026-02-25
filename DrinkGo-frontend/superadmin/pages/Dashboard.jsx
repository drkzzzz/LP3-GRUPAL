import { useState, useEffect } from 'react';
import { Building2, DollarSign, Users, TrendingUp } from 'lucide-react';
import StatCard from '../components/ui/StatCard';
import Card from '../components/ui/Card';
import dashboardService from '../services/dashboardService';

/**
 * Dashboard del SuperAdmin
 * Métricas clave de la plataforma SaaS
 */
const Dashboard = () => {
  const [metrics, setMetrics] = useState(null);
  const [monthlyRevenue, setMonthlyRevenue] = useState([]);
  const [planDistribution, setPlanDistribution] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setIsLoading(true);
      const [metricsData, revenueData, plansData] = await Promise.all([
        dashboardService.getMetrics(),
        dashboardService.getMonthlyRevenue(new Date().getFullYear()),
        dashboardService.getPlanDistribution(),
      ]);
      
      setMetrics(metricsData);
      setMonthlyRevenue(revenueData);
      setPlanDistribution(plansData);
    } catch (error) {
      console.error('Error cargando dashboard:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Cargando dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Título */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard Plataforma</h1>
        <p className="text-gray-600 mt-1">Vista general de la plataforma DrinkGo</p>
      </div>

      {/* Métricas principales */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Negocios"
          value={metrics?.totalNegocios || 0}
          icon={Building2}
          color="blue"
          trend="up"
          trendValue={`+${metrics?.negociosNuevosEsteMes || 0} este mes`}
        />
        
        <StatCard
          title="Negocios Activos"
          value={metrics?.negociosActivos || 0}
          icon={TrendingUp}
          color="green"
        />
        
        <StatCard
          title="Ingresos Mensuales"
          value={`S/ ${(metrics?.ingresosMensuales || 0).toLocaleString('es-PE')}`}
          icon={DollarSign}
          color="purple"
          trend={metrics?.tendenciaIngresos > 0 ? 'up' : 'down'}
          trendValue={`${Math.abs(metrics?.tendenciaIngresos || 0)}% vs mes anterior`}
        />
        
        <StatCard
          title="Negocios Morosos"
          value={metrics?.negociosMorosos || 0}
          icon={Users}
          color={metrics?.negociosMorosos > 0 ? 'red' : 'green'}
        />
      </div>

      {/* Gráficos y tablas */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Distribución por planes */}
        <Card title="Distribución por Planes">
          <div className="space-y-4">
            {planDistribution.map((plan, index) => (
              <div key={index}>
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium text-gray-700">{plan.nombre}</span>
                  <span className="text-sm text-gray-600">{plan.cantidad} negocios</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full transition-all"
                    style={{ width: `${(plan.cantidad / metrics?.totalNegocios * 100) || 0}%` }}
                  />
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Ingresos mensuales */}
        <Card title="Ingresos Mensuales (Últimos 6 meses)">
          <div className="space-y-3">
            {monthlyRevenue.slice(-6).map((item, index) => (
              <div key={index} className="flex items-center justify-between">
                <span className="text-sm text-gray-700">{item.mes}</span>
                <span className="text-sm font-semibold text-gray-900">
                  S/ {item.monto.toLocaleString('es-PE')}
                </span>
              </div>
            ))}
          </div>
        </Card>
      </div>

      {/* Negocios recientes */}
      <Card title="Negocios Registrados Recientemente">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Negocio</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">RUC</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Plan</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha Registro</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {metrics?.negociosRecientes?.map((negocio) => (
                <tr key={negocio.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {negocio.razonSocial}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {negocio.ruc}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {negocio.planNombre}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {new Date(negocio.fechaRegistro).toLocaleDateString('es-PE')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs rounded-full ${
                      negocio.estado === 'activo' 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-gray-100 text-gray-800'
                    }`}>
                      {negocio.estado}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};

export default Dashboard;
