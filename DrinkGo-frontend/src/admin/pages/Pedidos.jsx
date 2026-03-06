/**
 * Página principal de gestión de pedidos
 * RF-PED-001 a RF-PED-006
 */
import { useState } from 'react';
import { ListaPedidosTab } from '../pedidos/components/tabs/ListaPedidosTab';
import { CrearPedidoManualModal } from '../pedidos/components/modals/CrearPedidoManualModal';
import { Plus } from 'lucide-react';

export const Pedidos = () => {
  const [mostrarModalCrear, setMostrarModalCrear] = useState(false);

  const handlePedidoCreado = (pedido) => {
    console.log('✅ Pedido creado:', pedido);
    // El hook ya invalida el cache, la tabla se actualizará automáticamente
  };

  return (
    <div className="space-y-6">
      {/* Header with action button */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Pedidos</h1>
          <p className="text-gray-600 mt-1">
            Gestión de pedidos en tiempo real con seguimiento y estado
          </p>
        </div>
        
        {/* Botón Nuevo Pedido */}
        <button
          onClick={() => setMostrarModalCrear(true)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
        >
          <Plus className="w-5 h-5" />
          Nuevo Pedido
        </button>
      </div>

      {/* Main content */}
      <ListaPedidosTab />
      
      {/* Modal de crear pedido */}
      <CrearPedidoManualModal
        isOpen={mostrarModalCrear}
        onClose={() => setMostrarModalCrear(false)}
        onPedidoCreado={handlePedidoCreado}
      />
    </div>
  );
};
