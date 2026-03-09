import { useState } from 'react';
import { CreditCard, Package, Warehouse, Truck, FileText, History } from 'lucide-react';
import { VentasReporteTab } from '@/admin/reportes/components/tabs/VentasReporteTab';
import { ProductosReporteTab } from '@/admin/reportes/components/tabs/ProductosReporteTab';
import { InventarioReporteTab } from '@/admin/reportes/components/tabs/InventarioReporteTab';
import { ComprasGastosReporteTab } from '@/admin/reportes/components/tabs/ComprasGastosReporteTab';
import { ComprobantesReporteTab } from '@/admin/reportes/components/tabs/ComprobantesReporteTab';
import { MovimientosInventarioReporteTab } from '@/admin/reportes/components/tabs/MovimientosInventarioReporteTab';

const TABS = [
  { id: 'ventas',        label: 'Ventas',          icon: CreditCard, Component: VentasReporteTab },
  { id: 'productos',     label: 'Productos',        icon: Package,    Component: ProductosReporteTab },
  { id: 'inventario',    label: 'Inventario',       icon: Warehouse,  Component: InventarioReporteTab },
  { id: 'compras',       label: 'Compras y Gastos', icon: Truck,      Component: ComprasGastosReporteTab },
  { id: 'comprobantes',  label: 'Comprobantes',     icon: FileText,   Component: ComprobantesReporteTab },
  { id: 'movimientos',   label: 'Movimientos',      icon: History,    Component: MovimientosInventarioReporteTab },
];

export const Reportes = () => {
  const [activeTab, setActiveTab] = useState('ventas');
  const { Component: ActiveComponent } = TABS.find((t) => t.id === activeTab) ?? {};

  return (
    <div className="space-y-6">
      {/* Encabezado */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Reportes</h1>
        <p className="text-gray-600 mt-1">Análisis y estadísticas del negocio</p>
      </div>

      {/* Barra de tabs */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex gap-1 overflow-x-auto">
          {TABS.map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              onClick={() => setActiveTab(id)}
              className={`flex items-center gap-2 px-4 py-3 text-sm font-medium border-b-2 whitespace-nowrap transition-colors ${
                activeTab === id
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              <Icon size={16} />
              {label}
            </button>
          ))}
        </nav>
      </div>

      {/* Contenido del tab activo */}
      {ActiveComponent && <ActiveComponent />}
    </div>
  );
};
