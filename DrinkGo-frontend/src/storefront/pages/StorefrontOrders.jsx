import { useEffect } from 'react';
import { useOutletContext, useNavigate, Link } from 'react-router-dom';
import {
  Package,
  Clock,
  CheckCircle,
  XCircle,
  Truck,
  ChevronRight,
  ShoppingBag,
  Loader2,
} from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { useStorefrontAuthStore } from '../stores/storefrontAuthStore';
import { storefrontService } from '../services/storefrontService';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';

const STATUS_CONFIG = {
  pendiente: { label: 'Pendiente', icon: Clock, color: 'bg-yellow-100 text-yellow-700' },
  confirmado: { label: 'Confirmado', icon: CheckCircle, color: 'bg-blue-100 text-blue-700' },
  en_preparacion: { label: 'En Preparación', icon: Package, color: 'bg-purple-100 text-purple-700' },
  listo_recojo: { label: 'Listo para Recojo', icon: Package, color: 'bg-green-100 text-green-700' },
  en_camino: { label: 'En Camino', icon: Truck, color: 'bg-indigo-100 text-indigo-700' },
  entregado: { label: 'Entregado', icon: CheckCircle, color: 'bg-green-100 text-green-700' },
  cancelado: { label: 'Cancelado', icon: XCircle, color: 'bg-red-100 text-red-700' },
};

const StatusBadge = ({ status }) => {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.pendiente;
  const Icon = cfg.icon;
  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium ${cfg.color}`}>
      <Icon size={13} />
      {cfg.label}
    </span>
  );
};

export const StorefrontOrders = () => {
  const { slug } = useOutletContext();
  const navigate = useNavigate();
  const isAuthenticated = useStorefrontAuthStore((s) => s.isAuthenticated());

  useEffect(() => {
    if (!isAuthenticated) {
      navigate(`/tienda/${slug}/login?redirect=mis-pedidos`, { replace: true });
    }
  }, [isAuthenticated, slug, navigate]);

  const { data: pedidos = [], isLoading } = useQuery({
    queryKey: ['storefront-mis-pedidos', slug],
    queryFn: () => storefrontService.getMisPedidos(slug),
    enabled: !!slug && isAuthenticated,
  });

  if (!isAuthenticated) return null;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Mis Pedidos</h1>

      {isLoading ? (
        <div className="text-center py-16">
          <Loader2 size={32} className="animate-spin text-amber-500 mx-auto" />
          <p className="text-gray-500 text-sm mt-3">Cargando pedidos...</p>
        </div>
      ) : pedidos.length === 0 ? (
        <div className="text-center py-16">
          <ShoppingBag size={48} className="mx-auto mb-4 text-gray-300" />
          <h2 className="text-lg font-bold text-gray-900 mb-2">Sin pedidos aún</h2>
          <p className="text-gray-500 mb-6">Comienza explorando nuestro catálogo</p>
          <Link
            to={`/tienda/${slug}/catalogo`}
            className="inline-flex items-center gap-2 px-6 py-3 bg-amber-500 hover:bg-amber-600 text-white font-semibold rounded-xl transition-colors"
          >
            Ver Catálogo
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {pedidos.map((pedido) => (
            <div
              key={pedido.id}
              className="bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start justify-between mb-3">
                <div>
                  <p className="font-semibold text-gray-900">
                    Pedido #{pedido.numeroPedido || pedido.id}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    {pedido.createdAt ? formatDateTime(pedido.createdAt) : '—'}
                  </p>
                </div>
                <StatusBadge status={pedido.estadoPedido} />
              </div>

              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-4 text-gray-600">
                  <span className="inline-flex items-center gap-1">
                    {pedido.tipoPedido === 'delivery' ? (
                      <><Truck size={14} /> Delivery</>
                    ) : (
                      <><Package size={14} /> Recojo</>
                    )}
                  </span>
                  {pedido.detalles && (
                    <span>
                      {pedido.detalles.length} producto{pedido.detalles.length !== 1 ? 's' : ''}
                    </span>
                  )}
                </div>
                <div className="flex items-center gap-3">
                  <span className="font-bold text-gray-900">
                    {formatCurrency(pedido.total)}
                  </span>
                  <ChevronRight size={16} className="text-gray-400" />
                </div>
              </div>

              {/* Brief detail list */}
              {pedido.detalles && pedido.detalles.length > 0 && (
                <div className="mt-3 pt-3 border-t border-gray-100">
                  <div className="flex flex-wrap gap-2">
                    {pedido.detalles.slice(0, 3).map((d, i) => (
                      <span key={i} className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded-md">
                        {d.producto?.nombre || d.nombreProducto || `Producto ${d.productoId}`} ×{d.cantidad}
                      </span>
                    ))}
                    {pedido.detalles.length > 3 && (
                      <span className="text-xs text-gray-400">
                        +{pedido.detalles.length - 3} más
                      </span>
                    )}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
