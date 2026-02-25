import { useState, useEffect } from 'react';
import { Save, TestTube, Mail, Globe, CreditCard, AlertCircle } from 'lucide-react';
import configService from '../services/configService';

export default function ConfiguracionGlobal() {
  const [config, setConfig] = useState(null);
  const [emailTemplates, setEmailTemplates] = useState([]);
  const [integrations, setIntegrations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [activeTab, setActiveTab] = useState('general');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [configData, templatesData, integrationsData] = await Promise.all([
        configService.getConfig(),
        configService.getEmailTemplates(),
        configService.getIntegrations(),
      ]);
      setConfig(configData);
      setEmailTemplates(templatesData);
      setIntegrations(integrationsData);
    } catch (error) {
      console.error('Error cargando configuración:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveGeneral = async () => {
    setSaving(true);
    try {
      await configService.updateConfig(config);
      alert('Configuración guardada exitosamente');
    } catch (error) {
      alert('Error al guardar la configuración');
    } finally {
      setSaving(false);
    }
  };

  const handleTestIntegration = async (tipo) => {
    try {
      const result = await configService.testIntegration(tipo);
      alert(result.mensaje || 'Test exitoso');
    } catch (error) {
      alert('Error en el test de integración');
    }
  };

  if (loading) {
    return <div className="p-6">Cargando configuración...</div>;
  }

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Configuración Global</h1>
        <p className="text-gray-600 mt-1">Parámetros que afectan a toda la plataforma</p>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8">
          {[
            { id: 'general', label: 'Parámetros Generales', icon: Globe },
            { id: 'emails', label: 'Plantillas de Email', icon: Mail },
            { id: 'integraciones', label: 'Integraciones', icon: CreditCard },
          ].map((tab) => {
            const Icon = tab.icon;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center py-4 px-1 border-b-2 font-medium text-sm ${
                  activeTab === tab.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <Icon className="h-5 w-5 mr-2" />
                {tab.label}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Parámetros Generales */}
      {activeTab === 'general' && config && (
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-lg font-semibold mb-4">Parámetros del Sistema</h2>
          
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  IGV por Defecto (%)
                </label>
                <input
                  type="number"
                  value={config.igv_porcentaje || 18}
                  onChange={(e) => setConfig({ ...config, igv_porcentaje: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  step="0.01"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Moneda Principal
                </label>
                <select
                  value={config.moneda_principal || 'PEN'}
                  onChange={(e) => setConfig({ ...config, moneda_principal: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                >
                  <option value="PEN">Soles (PEN)</option>
                  <option value="USD">Dólares (USD)</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tiempo de Sesión (minutos)
                </label>
                <input
                  type="number"
                  value={config.tiempo_sesion || 60}
                  onChange={(e) => setConfig({ ...config, tiempo_sesion: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Máx. Intentos de Login
                </label>
                <input
                  type="number"
                  value={config.max_intentos_login || 5}
                  onChange={(e) => setConfig({ ...config, max_intentos_login: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                />
              </div>
            </div>

            <div className="pt-4">
              <button
                onClick={handleSaveGeneral}
                disabled={saving}
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 flex items-center"
              >
                <Save className="h-4 w-4 mr-2" />
                {saving ? 'Guardando...' : 'Guardar Configuración'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Plantillas de Email */}
      {activeTab === 'emails' && (
        <div className="bg-white rounded-lg shadow">
          <div className="p-6">
            <h2 className="text-lg font-semibold mb-4">Plantillas de Correo Electrónico</h2>
            <div className="space-y-4">
              {emailTemplates.length > 0 ? (
                emailTemplates.map((template) => (
                  <div key={template.id} className="border border-gray-200 rounded-lg p-4">
                    <div className="flex justify-between items-start">
                      <div>
                        <h3 className="font-medium text-gray-900">{template.nombre}</h3>
                        <p className="text-sm text-gray-600 mt-1">{template.descripcion}</p>
                      </div>
                      <button className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                        Editar
                      </button>
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-gray-500 text-center py-8">No hay plantillas configuradas</p>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Integraciones */}
      {activeTab === 'integraciones' && (
        <div className="space-y-4">
          {integrations.length > 0 ? (
            integrations.map((integration) => (
              <div key={integration.id} className="bg-white rounded-lg shadow p-6">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-lg font-semibold">{integration.nombre}</h3>
                    <p className="text-sm text-gray-600 mt-1">{integration.descripcion}</p>
                  </div>
                  <span
                    className={`px-3 py-1 rounded-full text-xs font-medium ${
                      integration.activo
                        ? 'bg-green-100 text-green-700'
                        : 'bg-gray-100 text-gray-600'
                    }`}
                  >
                    {integration.activo ? 'Activo' : 'Inactivo'}
                  </span>
                </div>

                <div className="space-y-3">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      API Key
                    </label>
                    <input
                      type="password"
                      value={integration.api_key || ''}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                      placeholder="••••••••••••••••"
                    />
                  </div>

                  <div className="flex gap-2">
                    <button
                      onClick={() => handleTestIntegration(integration.tipo)}
                      className="flex items-center px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                    >
                      <TestTube className="h-4 w-4 mr-2" />
                      Probar Conexión
                    </button>
                    <button className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                      <Save className="h-4 w-4 mr-2" />
                      Guardar
                    </button>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="bg-white rounded-lg shadow p-6 text-center text-gray-500">
              No hay integraciones configuradas
            </div>
          )}
        </div>
      )}
    </div>
  );
}
