/**
 * MesaFloorPage.jsx
 * ─────────────────
 * RF-VTA-011/012/013/014: Página principal de Gestión de Mesas.
 * Muestra un plano de mesas por sede, permite:
 *  - Abrir cuenta (mesa disponible)
 *  - Ver y gestionar cuenta abierta (mesa ocupada)
 *  - Cerrar cuenta y transferir
 */
import { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { RefreshCw, Grid3x3, Info } from 'lucide-react';
import { useCartStore } from '../stores/cartStore';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useSedesConfig } from '@/admin/configuracion/hooks/useSedesConfig';
import { useMesasConfig } from '@/admin/configuracion/hooks/useMesasConfig';
import {
  useCuentasAbiertasPorSede,
  useCuentaDetalle,
  useCuentasMesaMutations,
} from '../hooks/useCuentasMesa';
import { usuariosService } from '@/admin/usuarios/services/usuariosClientesService';
import { clientesService } from '@/admin/usuarios/services/usuariosClientesService';
import { AbrirCuentaModal } from '../components/AbrirCuentaModal';
import { CuentaDetailPanel } from '../components/CuentaDetailPanel';
import { CerrarCuentaModal } from '../components/CerrarCuentaModal';
import { TransferirModal } from '../components/TransferirModal';

/* Colores por estado de mesa */
const ESTADO_STYLES = {
  disponible: {
    card: 'bg-green-50 border-green-300 hover:bg-green-100 cursor-pointer',
    badge: 'bg-green-100 text-green-700',
    label: 'Disponible',
  },
  ocupada: {
    card: 'bg-red-50 border-red-300 hover:bg-red-100 cursor-pointer',
    badge: 'bg-red-100 text-red-700',
    label: 'Ocupada',
  },
  reservada: {
    card: 'bg-yellow-50 border-yellow-300 cursor-default',
    badge: 'bg-yellow-100 text-yellow-700',
    label: 'Reservada',
  },
  mantenimiento: {
    card: 'bg-gray-100 border-gray-300 cursor-default',
    badge: 'bg-gray-200 text-gray-600',
    label: 'Mantenimiento',
  },
};

export const MesaFloorPage = () => {
  const navigate = useNavigate();
  const loadFromMesa = useCartStore((s) => s.loadFromMesa);
  const { negocio, sede: sedeAuth, user } = useAdminAuthStore();
  const negocioId = negocio?.id;

  /* ── Datos base ── */
  const { sedes } = useSedesConfig(negocioId);
  const { mesas, isLoading: loadingMesas } = useMesasConfig(negocioId);

  /* Sedes con mesas habilitadas */
  const sedesConMesas = useMemo(() => sedes.filter((s) => s.tieneMesas), [sedes]);

  /* Sede seleccionada — default a la sede del usuario o la primera disponible */
  const [sedeId, setSedeId] = useState(() => sedeAuth?.id ?? null);
  const sedeActual = sedeId ?? sedesConMesas[0]?.id ?? null;

  /* ── Mesas filtradas por sede actual ── */
  const mesasDeSede = useMemo(
    () => mesas.filter((m) => (m.sede?.id ?? m.sedeId) === sedeActual),
    [mesas, sedeActual],
  );

  /* ── Cuentas abiertas ── */
  const { cuentas, isLoading: loadingCuentas, refetch } = useCuentasAbiertasPorSede(sedeActual);

  /* Índice rápido: mesaId → cuenta */
  const cuentaPorMesa = useMemo(() => {
    const map = {};
    cuentas.forEach((c) => {
      if (c.mesa?.id) map[c.mesa.id] = c;
    });
    return map;
  }, [cuentas]);

  /* ── Meseros y clientes (para AbrirCuentaModal) ── */
  const { data: meseros = [] } = useQuery({
    queryKey: ['usuarios-negocio', negocioId],
    queryFn: () => usuariosService.getByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 5,
  });

  const { data: clientes = [] } = useQuery({
    queryKey: ['clientes-negocio', negocioId],
    queryFn: () => clientesService.getByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 5,
  });

  /* ── Mutaciones ── */
  const { abrirCuenta, transferirMesa, transferirProductos, cerrarCuenta } =
    useCuentasMesaMutations(sedeActual);

  /* ── Estado de UI ── */
  const [mesaSeleccionada, setMesaSeleccionada] = useState(null);
  const [cuentaDetalle, setCuentaDetalle] = useState(null);
  const [showAbrirModal, setShowAbrirModal] = useState(false);
  const [showCerrarModal, setShowCerrarModal] = useState(false);
  const [showTransferirModal, setShowTransferirModal] = useState(false);

  /* Detalles de la cuenta en panel */
  const { detalles: detallesPanel } = useCuentaDetalle(cuentaDetalle?.id);

  /* ── Handlers ── */
  const handleMesaClick = (mesa) => {
    const cuenta = cuentaPorMesa[mesa.id];
    if (mesa.estado === 'disponible') {
      setMesaSeleccionada(mesa);
      setShowAbrirModal(true);
    } else if (mesa.estado === 'ocupada' && cuenta) {
      setCuentaDetalle(cuenta);
    }
  };

  const handleAbrirCuenta = ({ meseroId, numComensales, clienteId }) => {
    abrirCuenta.mutate(
      { negocioId, mesaId: mesaSeleccionada.id, meseroId, numComensales, clienteId },
      {
        onSuccess: () => {
          setShowAbrirModal(false);
          setMesaSeleccionada(null);
        },
      },
    );
  };

  const handleCerrarCuenta = ({ cuentaId, usuarioId }) => {
    cerrarCuenta.mutate(
      { cuentaId, usuarioId },
      {
        onSuccess: () => {
          setShowCerrarModal(false);
          setCuentaDetalle(null);
        },
      },
    );
  };

  /**
   * Carga los ítems de la cuenta en el carrito del POS y navega al POS.
   * @param {Array}   detalles        - ítems de la cuenta
   * @param {boolean} closeOnSuccess  - si el POS debe cerrar la cuenta tras cobrar
   */
  const handleCobrar = (detalles, { closeOnSuccess = true } = {}) => {
    if (!detalles?.length || !cuentaDetalle) return;
    loadFromMesa(detalles, {
      cuentaId: cuentaDetalle.id,
      numeroCuenta: cuentaDetalle.numeroCuenta,
      mesaNombre: cuentaDetalle.mesa?.nombre,
      sedeId: sedeActual,
      detalleIds: detalles.map((d) => d.id),
      closeOnSuccess,
    });
    navigate('/admin/ventas/pos');
  };

  const handleTransferirMesa = ({ cuentaId, nuevaMesaId }) => {
    transferirMesa.mutate(
      { cuentaId, nuevaMesaId },
      {
        onSuccess: () => {
          setShowTransferirModal(false);
          setCuentaDetalle(null);
        },
      },
    );
  };

  const handleTransferirProductos = ({ cuentaId, cuentaDestinoId, detalleIds }) => {
    transferirProductos.mutate(
      { cuentaOrigenId: cuentaId, cuentaDestinoId, detalleIds },
      {
        onSuccess: () => {
          setShowTransferirModal(false);
        },
      },
    );
  };

  /* Mesas disponibles para transferencia (misma sede, estado=disponible) */
  const mesasDisponibles = useMemo(
    () => mesasDeSede.filter((m) => m.estado === 'disponible'),
    [mesasDeSede],
  );

  const isLoading = loadingMesas || loadingCuentas;

  return (
    <div className="flex h-full">
      {/* ══ Área principal ══ */}
      <div className="flex-1 overflow-y-auto p-6">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <Grid3x3 size={22} className="text-green-600" />
            <h1 className="text-xl font-bold text-gray-900">Gestión de Mesas</h1>
          </div>
          <button
            onClick={() => refetch()}
            className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-green-600 px-3 py-1.5 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <RefreshCw size={14} className={isLoading ? 'animate-spin' : ''} />
            Actualizar
          </button>
        </div>

        {/* Tabs de sede */}
        {sedesConMesas.length > 1 && (
          <div className="flex gap-1 mb-4 border-b border-gray-200">
            {sedesConMesas.map((s) => (
              <button
                key={s.id}
                onClick={() => { setSedeId(s.id); setCuentaDetalle(null); }}
                className={`px-4 py-2 text-sm font-medium rounded-t-lg transition-colors ${
                  sedeActual === s.id
                    ? 'border-b-2 border-green-600 text-green-700 bg-green-50'
                    : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'
                }`}
              >
                {s.nombre}
              </button>
            ))}
          </div>
        )}

        {/* Leyenda */}
        <div className="flex flex-wrap gap-3 mb-5">
          {Object.entries(ESTADO_STYLES).map(([estado, st]) => (
            <span key={estado} className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium ${st.badge}`}>
              {st.label}
            </span>
          ))}
        </div>

        {/* Grid de mesas */}
        {isLoading ? (
          <div className="flex items-center justify-center h-40 text-gray-400">
            <RefreshCw size={24} className="animate-spin mr-2" /> Cargando mesas...
          </div>
        ) : mesasDeSede.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-40 text-gray-400 gap-2">
            <Info size={28} />
            <p className="text-sm">No hay mesas configuradas para esta sede.</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
            {mesasDeSede.map((mesa) => {
              const cuenta = cuentaPorMesa[mesa.id];
              const esSeleccionada = cuentaDetalle?.mesa?.id === mesa.id;
              const styles = ESTADO_STYLES[mesa.estado] ?? ESTADO_STYLES.disponible;
              return (
                <button
                  key={mesa.id}
                  onClick={() => handleMesaClick(mesa)}
                  className={`relative flex flex-col items-center justify-center rounded-xl border-2 p-4 min-h-[110px] transition-all ${
                    styles.card
                  } ${esSeleccionada ? 'ring-2 ring-green-500 ring-offset-1' : ''}`}
                >
                  <span className="text-xs font-semibold text-gray-500 mb-1">{mesa.nombre}</span>
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${styles.badge}`}>
                    {styles.label}
                  </span>
                  {cuenta && (
                    <div className="mt-2 text-center">
                      <p className="text-xs font-bold text-gray-700">{cuenta.numeroCuenta}</p>
                      {cuenta.total > 0 && (
                        <p className="text-xs text-gray-500">S/ {Number(cuenta.total).toFixed(2)}</p>
                      )}
                    </div>
                  )}
                  <span className="absolute bottom-2 right-2 text-xs text-gray-400">
                    cap. {mesa.capacidad}
                  </span>
                </button>
              );
            })}
          </div>
        )}
      </div>

      {/* ══ Panel de detalle de cuenta ══ */}
      {cuentaDetalle && (
        <CuentaDetailPanel
          key={cuentaDetalle.id}
          cuenta={cuentaDetalle}
          sedeId={sedeActual}
          onClose={() => setCuentaDetalle(null)}
          onCobrar={handleCobrar}
          onCerrarSinCobrar={() => setShowCerrarModal(true)}
          onTransferir={() => setShowTransferirModal(true)}
        />
      )}

      {/* ══ Modales ══ */}
      <AbrirCuentaModal
        isOpen={showAbrirModal}
        onClose={() => { setShowAbrirModal(false); setMesaSeleccionada(null); }}
        mesa={mesaSeleccionada}
        meseros={meseros}
        clientes={clientes}
        onConfirm={handleAbrirCuenta}
        isLoading={abrirCuenta.isPending}
      />

      {showCerrarModal && cuentaDetalle && (
        <CerrarCuentaModal
          isOpen={showCerrarModal}
          onClose={() => setShowCerrarModal(false)}
          cuenta={cuentaDetalle}
          detalles={detallesPanel}
          usuarioId={user?.id}
          onConfirm={handleCerrarCuenta}
          isLoading={cerrarCuenta.isPending}
        />
      )}

      {showTransferirModal && cuentaDetalle && (
        <TransferirModal
          isOpen={showTransferirModal}
          onClose={() => setShowTransferirModal(false)}
          cuenta={cuentaDetalle}
          detalles={detallesPanel}
          cuentasAbiertas={cuentas}
          mesasDisponibles={mesasDisponibles}
          onTransferirMesa={handleTransferirMesa}
          onTransferirProductos={handleTransferirProductos}
          isLoading={transferirMesa.isPending || transferirProductos.isPending}
        />
      )}
    </div>
  );
};
