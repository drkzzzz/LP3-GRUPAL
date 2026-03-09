import { useQuery } from '@tanstack/react-query';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { ventasService } from '@/admin/ventas/services/ventasService';
import { getPedidos } from '@/admin/pedidos/services/pedidosApi';
import { stockInventarioService, lotesInventarioService } from '@/admin/inventario/services/inventarioService';
import { getClientes } from '@/admin/services/clientesApi';
import { devolucionesService } from '@/admin/devoluciones/services/devolucionesService';

const now = new Date();
const MES = now.getMonth();
const ANIO = now.getFullYear();
const MES_PREVIO = MES === 0 ? 11 : MES - 1;
const ANIO_PREVIO = MES === 0 ? ANIO - 1 : ANIO;

const esMesActual = (fecha) => {
  if (!fecha) return false;
  const d = new Date(fecha);
  return d.getMonth() === MES && d.getFullYear() === ANIO;
};

const esMesPrevio = (fecha) => {
  if (!fecha) return false;
  const d = new Date(fecha);
  return d.getMonth() === MES_PREVIO && d.getFullYear() === ANIO_PREVIO;
};

const THIRTY_DAYS = Array.from({ length: 30 }, (_, i) => {
  const d = new Date();
  d.setDate(d.getDate() - (29 - i));
  return d.toISOString().split('T')[0];
});

export const useDashboardAdmin = () => {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;

  const ventasQ = useQuery({
    queryKey: ['dashboard-ventas', negocioId],
    queryFn: () => ventasService.getByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 3,
  });

  const pedidosQ = useQuery({
    queryKey: ['dashboard-pedidos'],
    queryFn: getPedidos,
    staleTime: 1000 * 60 * 3,
  });

  const stockQ = useQuery({
    queryKey: ['dashboard-stock'],
    queryFn: stockInventarioService.getAll,
    staleTime: 1000 * 60 * 5,
  });

  const clientesQ = useQuery({
    queryKey: ['dashboard-clientes'],
    queryFn: getClientes,
    staleTime: 1000 * 60 * 5,
  });

  const devolucionesQ = useQuery({
    queryKey: ['dashboard-devoluciones'],
    queryFn: devolucionesService.getAll,
    staleTime: 1000 * 60 * 3,
  });

  const lotesQ = useQuery({
    queryKey: ['dashboard-lotes'],
    queryFn: lotesInventarioService.getAll,
    staleTime: 1000 * 60 * 5,
  });

  // Top 5 productos: obtener detalles de todas las ventas del mes en paralelo
  const topProductosQ = useQuery({
    queryKey: ['dashboard-top-productos', negocioId],
    queryFn: async () => {
      if (!negocioId) return [];
      const ventas = await ventasService.getByNegocio(negocioId);
      const ventasMes = ventas.filter(v => esMesActual(v.creadoEn || v.fechaVenta));
      if (!ventasMes.length) return [];
      const detallesResults = await Promise.all(
        ventasMes.slice(0, 60).map(v => ventasService.getDetalle(v.id).catch(() => []))
      );
      const allItems = detallesResults.flat();
      const stats = {};
      allItems.forEach(d => {
        if (!d.productoId) return; // combos sin producto directo, se omiten
        if (!stats[d.productoId])
          stats[d.productoId] = { nombre: d.nombreProducto || '—', sku: '', cantidad: 0 };
        stats[d.productoId].cantidad += Number(d.cantidad || 0);
      });
      return Object.entries(stats)
        .sort(([, a], [, b]) => b.cantidad - a.cantidad)
        .slice(0, 5)
        .map(([, v], i) => ({ rank: i + 1, ...v }));
    },
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 5,
  });

  const ventas = ventasQ.data || [];
  const pedidos = pedidosQ.data || [];
  const stock = stockQ.data || [];
  const clientes = Array.isArray(clientesQ.data) ? clientesQ.data : [];
  const devoluciones = devolucionesQ.data || [];
  const lotes = (lotesQ.data || []).filter(
    l => (l.negocio?.id ?? l.negocioId) === negocioId,
  );

  // ── Ventas ──────────────────────────────────────────────────────────
  const ventasMes = ventas.filter(v => esMesActual(v.creadoEn || v.fechaVenta));
  const totalVentasMes = ventasMes.reduce((s, v) => s + (Number(v.total) || 0), 0);
  const ventasMesPrevio = ventas.filter(v => esMesPrevio(v.creadoEn || v.fechaVenta));
  const totalVentasMesPrevio = ventasMesPrevio.reduce((s, v) => s + (Number(v.total) || 0), 0);
  const variacionVentas = totalVentasMesPrevio === 0
    ? (totalVentasMes > 0 ? 100 : 0)
    : ((totalVentasMes - totalVentasMesPrevio) / totalVentasMesPrevio) * 100;

  // ── Pedidos ─────────────────────────────────────────────────────────
  const pedidosMes = pedidos.filter(p => esMesActual(p.creadoEn));
  const pedidosMesPrevio = pedidos.filter(p => esMesPrevio(p.creadoEn));
  const cantidadPedidosMes = pedidosMes.length;
  const variacionPedidos = pedidosMesPrevio.length === 0
    ? (cantidadPedidosMes > 0 ? 100 : 0)
    : ((cantidadPedidosMes - pedidosMesPrevio.length) / pedidosMesPrevio.length) * 100;

  const pedidosPorEstado = pedidos.reduce((acc, p) => {
    const e = p.estadoPedido || 'otro';
    acc[e] = (acc[e] || 0) + 1;
    return acc;
  }, {});

  const donutData = Object.entries(pedidosPorEstado).map(([name, value]) => ({ name, value }));

  const pedidosRecientes = [...pedidos]
    .sort((a, b) => new Date(b.creadoEn || 0) - new Date(a.creadoEn || 0))
    .slice(0, 5);

  // ── Clientes ─────────────────────────────────────────────────────────
  const clientesNuevosMes = clientes.filter(c => esMesActual(c.creadoEn)).length;

  // ── Stock ─────────────────────────────────────────────────────────────
  const stockAlerts = stock.filter(s => {
    const disponible = Number(s.cantidadDisponible) || 0;
    const minimo = Number(s.stockMinimo || s.producto?.stockMinimo || 5);
    return disponible <= minimo;
  });
  const valorInventario = stock.reduce(
    (s, item) => s + (Number(item.cantidadActual) || 0) * (Number(item.costoPromedio) || 0),
    0,
  );

  // ── Lotes vencidos / próximos a vencer ───────────────────────────────
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);
  const lotesVencidos = lotes.filter(l => {
    if (!l.fechaVencimiento) return false;
    const vence = new Date(l.fechaVencimiento);
    return vence < hoy && Number(l.cantidadActual || 0) > 0;
  });
  const lotesProximos = lotes.filter(l => {
    if (!l.fechaVencimiento) return false;
    const vence = new Date(l.fechaVencimiento);
    const dias = Math.ceil((vence - hoy) / (1000 * 60 * 60 * 24));
    return dias >= 0 && dias <= 30 && Number(l.cantidadActual || 0) > 0;
  });
  const cantidadAlertasStock = stockAlerts.length + lotesVencidos.length + lotesProximos.length;

  // ── Top 5 productos ──────────────────────────────────────────────────
  const topProductos = topProductosQ.data || [];

  // ── Devoluciones ──────────────────────────────────────────────────────
  const devolucionesPendientes = devoluciones.filter(d =>
    ['pendiente', 'en_proceso', 'recibido'].includes(d.estadoDevolucion || d.estado || ''),
  ).length;

  // ── Gráfico ventas 30 días ────────────────────────────────────────────
  const ventasPorDia = {};
  ventas.forEach(v => {
    const raw = v.creadoEn || v.fechaVenta || '';
    const fecha = raw.split('T')[0];
    if (THIRTY_DAYS.includes(fecha)) {
      ventasPorDia[fecha] = (ventasPorDia[fecha] || 0) + (Number(v.total) || 0);
    }
  });
  const chartVentas = THIRTY_DAYS.map(d => ({
    fecha: d,
    total: ventasPorDia[d] || 0,
    label: new Date(d + 'T12:00:00').toLocaleDateString('es-PE', { day: 'numeric', month: 'short' }),
  }));

  const isLoading = ventasQ.isLoading || pedidosQ.isLoading || stockQ.isLoading || topProductosQ.isLoading;

  const refetch = () => {
    ventasQ.refetch();
    pedidosQ.refetch();
    stockQ.refetch();
    clientesQ.refetch();
    devolucionesQ.refetch();
    lotesQ.refetch();
    topProductosQ.refetch();
  };

  return {
    isLoading,
    refetch,
    // Ventas
    totalVentasMes,
    variacionVentas,
    // Pedidos
    cantidadPedidosMes,
    variacionPedidos,
    donutData,
    pedidosRecientes,
    // Clientes
    clientesNuevosMes,
    // Stock
    cantidadAlertasStock,
    valorInventario,
    stockAlerts: stockAlerts.slice(0, 5),
    // Lotes
    lotesVencidos: lotesVencidos.slice(0, 5),
    lotesProximos: lotesProximos.slice(0, 5),
    // Devoluciones
    devolucionesPendientes,
    // Charts
    chartVentas,
    topProductos,
    // Negocio
    negocioNombre: negocio?.nombre || negocio?.razonSocial || '',
  };
};
