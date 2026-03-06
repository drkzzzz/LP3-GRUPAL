/**
 * Buscador de clientes con creación rápida
 * Permite buscar clientes existentes o crear uno nuevo on-the-fly
 */
import { useState } from 'react';
import { useBuscarClientes, useCrearClienteRapido } from '@/admin/hooks/useClientes';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { User, UserPlus, Search, Phone, MapPin, Loader2 } from 'lucide-react';

export function BuscadorCliente({ clienteSeleccionado, onClienteSeleccionado }) {
  const [busqueda, setBusqueda] = useState('');
  const [mostrarFormCrear, setMostrarFormCrear] = useState(false);
  const [nuevoCliente, setNuevoCliente] = useState({
    nombres: '',
    apellidos: '',
    numeroDocumento: '',
    telefono: '',
    direccion: '',
  });
  
  const debouncedSearch = useDebounce(busqueda, 400);
  const { data: clientesEncontrados = [], isLoading: buscandoClientes } = 
    useBuscarClientes(debouncedSearch, debouncedSearch.trim().length >= 2);
  
  const { mutateAsync: crearCliente, isPending: creandoCliente } = useCrearClienteRapido();
  
  const handleSeleccionarCliente = (cliente) => {
    onClienteSeleccionado(cliente);
    setBusqueda('');
  };
  
  const handleCrearCliente = async () => {
    if (!nuevoCliente.nombres || !nuevoCliente.telefono) {
      alert('Nombre y teléfono son obligatorios');
      return;
    }
    
    try {
      const clienteCreado = await crearCliente(nuevoCliente);
      onClienteSeleccionado(clienteCreado);
      setMostrarFormCrear(false);
      setNuevoCliente({ nombres: '', apellidos: '', numeroDocumento: '', telefono: '', direccion: '' });
      setBusqueda('');
    } catch (error) {
      console.error('Error al crear cliente:', error);
    }
  };
  
  return (
    <div className="space-y-4">
      {/* Cliente seleccionado */}
      {clienteSeleccionado ? (
        <div className="bg-green-50 border border-green-200 rounded-lg p-4">
          <div className="flex items-start justify-between">
            <div className="flex items-start gap-3">
              <div className="bg-green-100 p-2 rounded-full">
                <User className="w-5 h-5 text-green-600" />
              </div>
              <div>
                <p className="font-medium text-gray-900">
                  {clienteSeleccionado.nombres} {clienteSeleccionado.apellidos}
                </p>
                <div className="flex items-center gap-4 mt-1 text-sm text-gray-600">
                  <span className="flex items-center gap-1">
                    <Phone className="w-3.5 h-3.5" />
                    {clienteSeleccionado.telefono}
                  </span>
                </div>
                {clienteSeleccionado.direccion && (
                  <p className="text-sm text-gray-500 mt-1">
                    {clienteSeleccionado.direccion}
                  </p>
                )}
              </div>
            </div>
            <button
              type="button"
              onClick={() => onClienteSeleccionado(null)}
              className="text-sm text-red-600 hover:text-red-700"
            >
              Cambiar
            </button>
          </div>
        </div>
      ) : (
        <>
          {/* Buscador */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Buscar Cliente *
            </label>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                placeholder="Buscar por nombre, teléfono o documento..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
                className="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {buscandoClientes && (
                <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400 animate-spin" />
              )}
            </div>
            
            {/* Resultados de búsqueda */}
            {debouncedSearch.trim().length >= 2 && clientesEncontrados.length > 0 && (
              <div className="mt-2 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                {clientesEncontrados.map((cliente) => (
                  <button
                    key={cliente.id}
                    type="button"
                    onClick={() => handleSeleccionarCliente(cliente)}
                    className="w-full text-left px-4 py-3 hover:bg-gray-50 border-b border-gray-100 last:border-b-0"
                  >
                    <p className="font-medium text-gray-900">
                      {cliente.nombres} {cliente.apellidos}
                    </p>
                    <div className="flex items-center gap-3 mt-1 text-sm text-gray-600">
                      <span>{cliente.telefono}</span>
                    </div>
                  </button>
                ))}
              </div>
            )}
            
            {/* Sin resultados */}
            {debouncedSearch.trim().length >= 2 && 
             !buscandoClientes && 
             clientesEncontrados.length === 0 && (
              <p className="mt-2 text-sm text-gray-500">
                No se encontraron clientes
              </p>
            )}
          </div>
          
          {/* Botón crear cliente */}
          <button
            type="button"
            onClick={() => setMostrarFormCrear(!mostrarFormCrear)}
            className="flex items-center gap-2 text-blue-600 hover:text-blue-700 text-sm font-medium"
          >
            <UserPlus className="w-4 h-4" />
            {mostrarFormCrear ? 'Cancelar' : 'Crear nuevo cliente'}
          </button>
          
          {/* Formulario crear cliente */}
          {mostrarFormCrear && (
            <div className="bg-gray-50 p-4 rounded-lg space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nombres *
                  </label>
                  <input
                    type="text"
                    required
                    value={nuevoCliente.nombres}
                    onChange={(e) => setNuevoCliente({...nuevoCliente, nombres: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Apellidos
                  </label>
                  <input
                    type="text"
                    value={nuevoCliente.apellidos}
                    onChange={(e) => setNuevoCliente({...nuevoCliente, apellidos: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  />
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    DNI / Documento
                  </label>
                  <input
                    type="text"
                    value={nuevoCliente.numeroDocumento}
                    onChange={(e) => setNuevoCliente({...nuevoCliente, numeroDocumento: e.target.value})}
                    placeholder="12345678"
                    maxLength="20"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Teléfono *
                  </label>
                  <input
                    type="tel"
                    required
                    value={nuevoCliente.telefono}
                    onChange={(e) => setNuevoCliente({...nuevoCliente, telefono: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  />
                </div>
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Dirección
                </label>
                <input
                  type="text"
                  value={nuevoCliente.direccion}
                  onChange={(e) => setNuevoCliente({...nuevoCliente, direccion: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  placeholder="Calle y número, distrito"
                />
              </div>
              
              <button
                type="button"
                onClick={handleCrearCliente}
                disabled={creandoCliente || !nuevoCliente.nombres || !nuevoCliente.telefono}
                className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 flex items-center justify-center gap-2"
              >
                {creandoCliente ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    Creando...
                  </>
                ) : (
                  <>
                    <UserPlus className="w-4 h-4" />
                    Crear Cliente
                  </>
                )}
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
