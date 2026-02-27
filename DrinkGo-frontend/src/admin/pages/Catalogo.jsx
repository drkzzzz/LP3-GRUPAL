/**
 * Catalogo.jsx
 * ────────────
 * Página principal del módulo Catálogo del Panel Admin.
 * Navegación por pestañas: Productos, Categorías, Marcas, Unidades, Combos, Promociones.
 */
import { useState } from 'react';
import {
  Package,
  FolderTree,
  Award,
  Ruler,
  Gift,
  Tag,
} from 'lucide-react';
import clsx from 'clsx';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { ProductosTab } from '../catalogo/components/tabs/ProductosTab';
import { CategoriasTab } from '../catalogo/components/tabs/CategoriasTab';
import { MarcasTab } from '../catalogo/components/tabs/MarcasTab';
import { UnidadesMedidaTab } from '../catalogo/components/tabs/UnidadesMedidaTab';
import { CombosTab } from '../catalogo/components/tabs/CombosTab';
import { PromocionesTab } from '../catalogo/components/tabs/PromocionesTab';

const TABS = [
  { key: 'productos', label: 'Productos', icon: Package },
  { key: 'categorias', label: 'Categorías', icon: FolderTree },
  { key: 'marcas', label: 'Marcas', icon: Award },
  { key: 'unidades', label: 'Unidades', icon: Ruler },
  { key: 'combos', label: 'Combos', icon: Gift },
  { key: 'promociones', label: 'Promociones', icon: Tag },
];

export const Catalogo = () => {
  const [activeTab, setActiveTab] = useState('productos');
  const negocio = useAdminAuthStore((s) => s.negocio);
  const negocioId = negocio?.id;

  const renderTab = () => {
    switch (activeTab) {
      case 'productos':
        return <ProductosTab negocioId={negocioId} />;
      case 'categorias':
        return <CategoriasTab negocioId={negocioId} />;
      case 'marcas':
        return <MarcasTab negocioId={negocioId} />;
      case 'unidades':
        return <UnidadesMedidaTab negocioId={negocioId} />;
      case 'combos':
        return <CombosTab negocioId={negocioId} />;
      case 'promociones':
        return <PromocionesTab negocioId={negocioId} />;
      default:
        return <ProductosTab negocioId={negocioId} />;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Catálogo de Productos</h1>
        <p className="text-gray-600 mt-1">
          Gestión de productos, categorías, marcas, combos, descuentos y promociones
        </p>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex gap-1 overflow-x-auto" aria-label="Tabs del catálogo">
          {TABS.map(({ key, label, icon: Icon }) => (
            <button
              key={key}
              onClick={() => setActiveTab(key)}
              className={clsx(
                'flex items-center gap-2 px-4 py-2.5 text-sm font-medium border-b-2 transition-colors whitespace-nowrap',
                activeTab === key
                  ? 'border-green-600 text-green-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
              )}
            >
              <Icon size={16} />
              {label}
            </button>
          ))}
        </nav>
      </div>

      {/* Active tab content */}
      {renderTab()}
    </div>
  );
};
