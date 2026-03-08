/**
 * PagoModal.jsx
 * ─────────────
 * Modal de cobro: datos del cliente (DNI/RUC), tipo de comprobante,
 * y pagos mixtos (abonos).
 *
 * Reglas de redondeo (moneda peruana):
 *  • EFECTIVO: no existen monedas < S/0.10. El monto en efectivo que debe
 *    cubrir el cliente se redondea al S/0.10 más cercano.
 *    Ej: S/ 7.43 → S/ 7.40, S/ 7.47 → S/ 7.50
 *  • DIGITAL (yape, plin, transferencia, tarjeta, qr, etc.):
 *    monto exacto con céntimos.
 *  • MIXTO: los métodos digitales cubren su parte exacta, y el saldo
 *    restante en efectivo se redondea.
 *
 * El cajero escribe lo que el cliente ENTREGA (no el total).
 * Si paga con efectivo de más, se muestra el vuelto a devolver.
 */
import { useState, useEffect, useMemo } from 'react';
import { CreditCard, Banknote, Smartphone, QrCode, Plus, X, AlertCircle, Search, Loader2 } from 'lucide-react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { Badge } from '@/admin/components/ui/Badge';
import { formatCurrency } from '@/shared/utils/formatters';
import { useConsultarRuc, useConsultarDni } from '@/shared/hooks/useConsultaDocumento';

const ICON_MAP = {
  efectivo: Banknote,
  tarjeta_credito: CreditCard,
  tarjeta_debito: CreditCard,
  yape: Smartphone,
  plin: Smartphone,
  billetera_digital: Smartphone,
  transferencia_bancaria: CreditCard,
  qr: QrCode,
  otro: CreditCard,
};

const COMPROBANTE_OPTIONS = [
  { value: 'nota_venta', label: 'Nota de Venta', seriePrefix: null },
  { value: 'boleta', label: 'Boleta', seriePrefix: 'B' },
  { value: 'factura', label: 'Factura', seriePrefix: 'F' },
];

/** Tipos de método de pago que se consideran "efectivo físico" */
const CASH_TYPES = new Set(['efectivo']);

/** Redondea al S/ 0.10 más cercano (regla de céntimos peruanos) */
const redondearEfectivo = (monto) => +(Math.round(monto * 10) / 10).toFixed(2);

/** ¿El tipo de este método de pago es efectivo? */
const esMetodoEfectivo = (metodosPago, metodoPagoId) => {
  const m = metodosPago.find((mp) => mp.id === Number(metodoPagoId));
  return m ? CASH_TYPES.has(m.tipo) : false;
};

const DOC_TYPES = [
  { value: '', label: 'Sin documento' },
  { value: 'DNI', label: 'DNI' },
  { value: 'RUC', label: 'RUC' },
];

/**
 * Validates customer document number based on type.
 * DNI = exactly 8 digits, RUC = exactly 11 digits.
 */
const validateDocNumber = (tipoDoc, numero, tipoComprobante) => {
  if (!tipoDoc || !numero) return null;
  const clean = numero.replace(/\s/g, '');
  if (tipoDoc === 'DNI') {
    if (!/^\d{8}$/.test(clean)) return 'DNI debe tener exactamente 8 dígitos';
  } else if (tipoDoc === 'RUC') {
    if (!/^\d{11}$/.test(clean)) return 'RUC debe tener exactamente 11 dígitos';
    if (clean.length === 11 && tipoComprobante === 'factura' && !/^(10|20)/.test(clean)) {
      return 'RUC debe iniciar con 10 (persona natural) o 20 (persona jurídica)';
    }
  }
  return null;
};

export const PagoModal = ({
  isOpen,
  onClose,
  onConfirm,
  total,
  metodosPago = [],
  series = [],
  isLoading = false,
}) => {
  const [pagos, setPagos] = useState([]);
  const [tipoComprobante, setTipoComprobante] = useState('nota_venta');
  const [tipoDocumento, setTipoDocumento] = useState('');
  const [docClienteNumero, setDocClienteNumero] = useState('');
  const [docClienteNombre, setDocClienteNombre] = useState('');
  const [docClienteDireccion, setDocClienteDireccion] = useState('');
  const [docError, setDocError] = useState(null);

  const rucLookup = useConsultarRuc();
  const dniLookup = useConsultarDni();

  /* Inicializar al abrir */
  useEffect(() => {
    if (isOpen) {
      setTipoComprobante('nota_venta');
      setTipoDocumento('');
      setDocClienteNumero('');
      setDocClienteNombre('');
      setDocClienteDireccion('');
      setDocError(null);
      rucLookup.reset();
      dniLookup.reset();

      if (metodosPago.length > 0) {
        const efectivo = metodosPago.find(
          (m) => m.tipo === 'efectivo' || m.nombre?.toLowerCase() === 'efectivo',
        );
        /* Iniciar con método efectivo seleccionado pero monto vacío.
           El cajero debe ingresar lo que el cliente entrega. */
        setPagos([
          { metodoPagoId: efectivo?.id || metodosPago[0]?.id, monto: '' },
        ]);
      } else {
        setPagos([]);
      }
    }
  }, [isOpen, metodosPago]);

  /* Forzar tipo documento según comprobante */
  useEffect(() => {
    if (tipoComprobante === 'factura') {
      setTipoDocumento('RUC');
    } else if (tipoComprobante === 'boleta') {
      setTipoDocumento((prev) => prev || 'DNI');
    }
  }, [tipoComprobante]);

  /* Validar documento */
  useEffect(() => {
    setDocError(validateDocNumber(tipoDocumento, docClienteNumero, tipoComprobante));
  }, [tipoDocumento, docClienteNumero, tipoComprobante]);

  /* Filtrar opciones de comprobante según series disponibles */
  const comprobanteOptions = useMemo(() => {
    return COMPROBANTE_OPTIONS.filter((opt) => {
      // nota_venta siempre disponible (no requiere serie SUNAT)
      if (!opt.seriePrefix) return true;
      // Solo mostrar si hay al menos una serie activa con ese prefijo
      return series.some(
        (s) => s.estaActivo !== false && s.serie?.toUpperCase().startsWith(opt.seriePrefix),
      );
    });
  }, [series]);

  /* Tipo de contribuyente según prefijo RUC */
  const esPersonaJuridica = docClienteNumero.startsWith('20');
  const esPersonaNatural = docClienteNumero.startsWith('10');
  const tipoContribuyente = esPersonaJuridica ? 'jurídica' : esPersonaNatural ? 'natural' : null;

  /* ── Calcular totales con redondeo de efectivo ── */
  const { totalDigital, totalEfectivo, hasCashRow, cashCount } = useMemo(() => {
    let totalDigital = 0, totalEfectivo = 0, hasCashRow = false, cashCount = 0;
    for (const p of pagos) {
      const amt = parseFloat(p.monto) || 0;
      if (esMetodoEfectivo(metodosPago, p.metodoPagoId)) {
        hasCashRow = true;
        cashCount++;
        totalEfectivo += amt;
      } else {
        totalDigital += amt;
      }
    }
    return { totalDigital, totalEfectivo, hasCashRow, cashCount };
  }, [pagos, metodosPago]);

  const totalPagado = +(totalDigital + totalEfectivo).toFixed(2);

  /*
   * Vuelto SOLO cuando el pago es 100% efectivo (un único método, efectivo).
   * En pago mixto NUNCA hay vuelto: cada método se limita a completar.
   */
  const esPagoUnicoEfectivo = pagos.length === 1 && hasCashRow;

  /* Cobro en efectivo redondeado (solo aplica si hay fila efectivo) */
  const restoCash = Math.max(0, +(total - totalDigital).toFixed(2));
  const cobrarEfectivo = hasCashRow && restoCash > 0 ? redondearEfectivo(restoCash) : 0;

  /* Vuelto: SOLO en pago único efectivo */
  const vuelto = esPagoUnicoEfectivo && totalEfectivo > cobrarEfectivo
    ? +(totalEfectivo - cobrarEfectivo).toFixed(2)
    : 0;

  /* Pendiente por pagar */
  const pendiente = esPagoUnicoEfectivo
    ? Math.max(0, +(cobrarEfectivo - totalEfectivo).toFixed(2))
    : Math.max(0, +(total - totalPagado).toFixed(2));

  /* Helper: get icon for a method */
  const getMethodInfo = (metodoPagoId) => {
    const metodo = metodosPago.find((m) => m.id === parseInt(metodoPagoId));
    const tipo = metodo?.tipo || 'otro';
    const Icon = ICON_MAP[tipo] || CreditCard;
    return { Icon, nombre: metodo?.nombre || 'Método', tipo };
  };

  /* Pago CRUD */
  const usedMethodIds = useMemo(
    () => new Set(pagos.map((p) => String(p.metodoPagoId))),
    [pagos],
  );

  const updatePago = (index, field, value) => {
    setPagos((prev) => {
      const isSingleCash = prev.length === 1 && esMetodoEfectivo(metodosPago,
        field === 'metodoPagoId' && index === 0 ? value : prev[0]?.metodoPagoId);
      return prev.map((p, i) => {
        if (i !== index) return p;
        const updated = { ...p, [field]: value };
        /* Pago único efectivo → sin tope (genera vuelto) */
        if (isSingleCash) return updated;
        /* Mixto: TODOS los métodos tienen tope = total − suma de los demás */
        if (field === 'monto' || field === 'metodoPagoId') {
          const numVal = parseFloat(updated.monto);
          if (!isNaN(numVal) && numVal > 0) {
            const otherSum = prev.reduce((acc, pp, j) =>
              j === i ? acc : acc + (parseFloat(pp.monto) || 0), 0);
            const max = +Math.max(0, total - otherSum).toFixed(2);
            if (numVal > max) return { ...updated, monto: max };
          }
        }
        return updated;
      });
    });
  };

  const addPago = () => {
    const nextMethod = metodosPago.find((m) => !usedMethodIds.has(String(m.id)));
    const remaining = +Math.max(0, total - totalPagado).toFixed(2);
    setPagos((prev) => [
      ...prev,
      {
        metodoPagoId: nextMethod?.id || metodosPago[0]?.id,
        monto: remaining > 0 ? remaining : '',
      },
    ]);
  };

  /* ¿Se puede agregar otro método? Solo si falta monto y hay métodos disponibles */
  const canAddMethod = totalPagado < total - 0.009
    && metodosPago.some((m) => !usedMethodIds.has(String(m.id)));

  const removePago = (index) => {
    if (pagos.length <= 1) return;
    setPagos((prev) => prev.filter((_, i) => i !== index));
  };

  /* Set all amount to single method */
  const setFullAmount = (index) => {
    const otherSum = pagos.reduce(
      (acc, p, i) => (i !== index ? acc + (parseFloat(p.monto) || 0) : acc), 0,
    );
    const remaining = Math.max(0, total - otherSum);
    const isCash = esMetodoEfectivo(metodosPago, pagos[index].metodoPagoId);
    /* Efectivo único → redondeado; mixto → cualquier método toma lo que falta */
    updatePago(index, 'monto',
      isCash && pagos.length === 1 ? redondearEfectivo(remaining) : +remaining.toFixed(2));
  };

  /* Validation */
  const docRequired = tipoComprobante === 'factura';
  // RUC 10 (persona natural) no siempre cuenta con dirección fiscal en la API;
  // se permite null para ellos. RUC 20 sí requiere dirección.
  const direccionRequerida = docRequired && esPersonaJuridica;
  const isDocValid =
    !docRequired ||
    (!docError &&
      docClienteNumero.length > 0 &&
      docClienteNombre.length > 0 &&
      (!direccionRequerida || docClienteDireccion.length > 0));
  const isPaymentValid = (() => {
    const anythingPaid = pagos.some((p) => parseFloat(p.monto) > 0);
    if (!anythingPaid) return false;
    if (esPagoUnicoEfectivo) {
      /* Único efectivo: cubrir el redondeado */
      return totalEfectivo >= cobrarEfectivo - 0.009;
    }
    /* Mixto / 100% digital: la suma debe cubrir el total exacto, sin exceder */
    return totalPagado >= total - 0.009 && totalPagado <= total + 0.009;
  })();
  const canConfirm = isPaymentValid && isDocValid && !docError;

  const handleConfirm = () => {
    if (!canConfirm) return;
    const cleanPagos = pagos
      .filter((p) => parseFloat(p.monto) > 0)
      .map((p) => {
        const isCash = esMetodoEfectivo(metodosPago, p.metodoPagoId);
        const montoIngresado = parseFloat(p.monto);
        return {
          metodoPagoId: parseInt(p.metodoPagoId),
          monto: esPagoUnicoEfectivo && isCash ? cobrarEfectivo : montoIngresado,
          tipoReferencia: getMethodInfo(p.metodoPagoId).tipo,
          numeroReferencia: null,
          ...(esPagoUnicoEfectivo && isCash && {
            montoRecibido: montoIngresado,
            montoCambio: vuelto,
          }),
        };
      });

    onConfirm({
      pagos: cleanPagos,
      tipoComprobante,
      docClienteNumero: docClienteNumero || null,
      docClienteNombre: docClienteNombre || (tipoComprobante !== 'factura' ? 'CLIENTE VARIOS' : null),
      docClienteDireccion: docClienteDireccion || null,
    });
  };

  /** Buscar datos por documento */
  const handleBuscarDocumento = async () => {
    if (tipoDocumento === 'RUC' && /^\d{11}$/.test(docClienteNumero)) {
      const resultado = await rucLookup.buscar(docClienteNumero);
      if (resultado) {
        setDocClienteNombre(resultado.razon_social || '');
        // RUC 20 (persona jurídica) trae dirección de SUNAT; RUC 10 no siempre
        if (resultado.direccion) {
          setDocClienteDireccion(resultado.direccion);
        }
      }
    } else if (tipoDocumento === 'DNI' && /^\d{8}$/.test(docClienteNumero)) {
      // DNI 00000000 = venta anónima, no consultar API
      if (docClienteNumero === '00000000') {
        setDocClienteNombre('CLIENTE VARIOS');
        return;
      }
      const resultado = await dniLookup.buscar(docClienteNumero);
      if (resultado) {
        setDocClienteNombre(resultado.nombre_completo || '');
      }
    }
  };

  const isBuscando = rucLookup.isLoading || dniLookup.isLoading;

  /* Max digits for doc type */
  const docMaxLength = tipoDocumento === 'DNI' ? 8 : tipoDocumento === 'RUC' ? 11 : 20;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Registrar Pago" size="lg">
      <div className="space-y-5">
        {/* ── Total a pagar ── */}
        <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-center">
          <p className="text-sm text-green-700">Total a pagar</p>
          <p className="text-3xl font-bold text-green-800">
            {formatCurrency(total)}
          </p>
        </div>

        {/* ── Comprobante + Documento ── */}
        <div className="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 className="text-sm font-semibold text-gray-700">Datos del comprobante</h3>

          <div className="grid grid-cols-3 gap-3">
            {/* Tipo comprobante */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">
                Comprobante
              </label>
              <select
                value={tipoComprobante}
                onChange={(e) => setTipoComprobante(e.target.value)}
                className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                {comprobanteOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
              </select>
            </div>

            {/* Tipo documento */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">
                Tipo Doc. {docRequired && <span className="text-red-500">*</span>}
              </label>
              <select
                value={tipoDocumento}
                onChange={(e) => {
                  setTipoDocumento(e.target.value);
                  setDocClienteNumero('');
                }}
                disabled={tipoComprobante === 'factura'}
                className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 disabled:bg-gray-100"
              >
                {DOC_TYPES.map((opt) => (
                  <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
              </select>
            </div>

            {/* Número documento + búsqueda */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">
                N° Documento {docRequired && <span className="text-red-500">*</span>}
              </label>
              <div className="flex gap-1.5">
                <input
                  type="text"
                  inputMode="numeric"
                  value={docClienteNumero}
                  onChange={(e) => setDocClienteNumero(e.target.value.replace(/\D/g, ''))}
                  maxLength={docMaxLength}
                  placeholder={
                    tipoDocumento === 'DNI'
                      ? '12345678'
                      : tipoDocumento === 'RUC'
                        ? '20123456789'
                        : 'N° documento'
                  }
                  disabled={!tipoDocumento}
                  className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 disabled:bg-gray-100"
                />
                <button
                  type="button"
                  onClick={handleBuscarDocumento}
                  disabled={isBuscando || !tipoDocumento || !docClienteNumero || (tipoDocumento === 'DNI' && docClienteNumero.length !== 8) || (tipoDocumento === 'RUC' && docClienteNumero.length !== 11)}
                  title="Buscar datos por documento"
                  className="shrink-0 flex items-center justify-center px-2.5 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {isBuscando ? <Loader2 size={14} className="animate-spin" /> : <Search size={14} />}
                </button>
              </div>
              {docError && (
                <p className="text-xs text-red-500 mt-0.5 flex items-center gap-1">
                  <AlertCircle size={12} /> {docError}
                </p>
              )}
              {(rucLookup.error || dniLookup.error) && (
                <p className="text-xs text-red-500 mt-0.5 flex items-center gap-1">
                  <AlertCircle size={12} /> {rucLookup.error || dniLookup.error}
                </p>
              )}
            </div>
          </div>

          {/* Info contribuyente (para factura) */}
          {tipoComprobante === 'factura' && tipoContribuyente && (
            <>
              {/* Badge tipo contribuyente */}
              <div className="flex items-center gap-2">
                <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                  esPersonaJuridica
                    ? 'bg-blue-100 text-blue-700'
                    : 'bg-amber-100 text-amber-700'
                }`}>
                  {esPersonaJuridica ? 'Persona Jurídica (RUC 20)' : 'Persona Natural con Negocio (RUC 10)'}
                </span>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">
                    {esPersonaJuridica ? 'Razón Social' : 'Nombre Completo'} <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    value={docClienteNombre}
                    onChange={(e) => setDocClienteNombre(e.target.value)}
                    placeholder={esPersonaJuridica ? 'Ej: Distribuidora ABC S.A.C.' : 'Ej: Juan Pérez López'}
                    className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">
                    Dirección Fiscal {esPersonaJuridica && <span className="text-red-500">*</span>}
                    {esPersonaNatural && <span className="text-gray-400 text-xs">(opcional)</span>}
                  </label>
                  <input
                    type="text"
                    value={docClienteDireccion}
                    onChange={(e) => setDocClienteDireccion(e.target.value)}
                    placeholder={esPersonaNatural ? 'Sin dirección si no aplica' : 'Ej: Av. Los Olivos 123, Lima'}
                    className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
              </div>
            </>
          )}

          {/* Aviso cuando RUC no tiene prefijo válido aún */}
          {tipoComprobante === 'factura' && docClienteNumero.length > 0 && !tipoContribuyente && !docError && (
            <p className="text-xs text-amber-600 flex items-center gap-1">
              <AlertCircle size={12} /> Ingrese un RUC que inicie con 10 o 20
            </p>
          )}

          {/* Nombre cliente (para boleta, opcional) */}
          {tipoComprobante === 'boleta' && tipoDocumento && (
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">
                Nombre del cliente
                <span className="ml-1 text-gray-400 font-normal">(opcional — vacío = Cliente General)</span>
              </label>
              <input
                type="text"
                value={docClienteNombre}
                onChange={(e) => setDocClienteNombre(e.target.value)}
                placeholder="Cliente General"
                className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
          )}
        </div>

        {/* ── Métodos de pago (abonos) ── */}
        <div>
          <div className="flex justify-between items-center mb-3">
            <h3 className="text-sm font-semibold text-gray-700">
              Métodos de pago
            </h3>
            <Button variant="outline" size="sm" onClick={addPago} disabled={!canAddMethod}>
              <Plus size={14} className="mr-1" />
              Agregar método
            </Button>
          </div>

          {metodosPago.length === 0 && (
            <div className="flex items-center gap-2 p-3 bg-yellow-50 text-yellow-700 rounded-lg border border-yellow-200 text-sm">
              <AlertCircle size={16} />
              <span>No hay métodos de pago configurados. Vaya a <strong>Configuración → Métodos de Pago</strong> para crear uno.</span>
            </div>
          )}

          <div className="space-y-2 max-h-56 overflow-y-auto pr-1">
            {pagos.map((pago, index) => {
              const { Icon, nombre } = getMethodInfo(pago.metodoPagoId);
              const isCashPago = esMetodoEfectivo(metodosPago, pago.metodoPagoId);
              return (
                <div key={index} className="space-y-1">
                  <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg border border-gray-200">
                    {/* Icon */}
                    <div className="p-2 bg-white rounded-lg border border-gray-100 shrink-0">
                      <Icon size={18} className="text-gray-600" />
                    </div>

                    {/* Selector de método */}
                    <select
                      value={pago.metodoPagoId}
                      onChange={(e) => updatePago(index, 'metodoPagoId', e.target.value)}
                      className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                    >
                      {metodosPago.map((m) => (
                        <option key={m.id} value={m.id}>
                          {m.nombre}
                        </option>
                      ))}
                    </select>

                    {/* Monto */}
                    <div className="w-32 relative">
                      <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">
                        S/
                      </span>
                      <input
                        type="text"
                        inputMode="decimal"
                        value={pago.monto}
                        onChange={(e) => {
                          // Solo permitir dígitos y un punto decimal
                          const raw = e.target.value;
                          if (raw === '' || /^\d*\.?\d{0,2}$/.test(raw)) {
                            updatePago(index, 'monto', raw);
                          }
                        }}
                        onBlur={() => {
                          // Efectivo: redondear al 0.10 más cercano
                          const val = parseFloat(pago.monto);
                          if (!isNaN(val) && val > 0 && isCashPago) {
                            updatePago(index, 'monto', String(redondearEfectivo(val)));
                          }
                        }}
                        onKeyDown={(e) => {
                          // Bloquear letras y símbolos no numéricos
                          if (['e', 'E', '+', '-'].includes(e.key)) e.preventDefault();
                        }}
                        onFocus={(e) => e.target.select()}
                        placeholder={isCashPago ? 'Recibido' : '0.00'}
                        className="w-full border border-gray-300 rounded-lg pl-8 pr-3 py-2 text-sm text-right font-medium focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                    </div>

                    {/* Quick: set full remaining amount */}
                    <button
                      type="button"
                      title={isCashPago ? 'Monto exacto (sin vuelto)' : 'Poner monto restante'}
                      onClick={() => setFullAmount(index)}
                      className="text-xs text-green-600 hover:text-green-800 font-medium shrink-0"
                    >
                      Todo
                    </button>

                    {/* Remove */}
                    {pagos.length > 1 && (
                      <button
                        type="button"
                        title="Quitar"
                        onClick={() => removePago(index)}
                        className="text-gray-400 hover:text-red-500 shrink-0"
                      >
                        <X size={16} />
                      </button>
                    )}
                  </div>


                </div>
              );
            })}
          </div>
        </div>

        {/* ── Resumen de pagos ── */}
        <div className="border-t border-gray-200 pt-3 space-y-1.5">
          {/* Cobro en efectivo redondeado (solo pago único efectivo) */}
          {esPagoUnicoEfectivo && cobrarEfectivo > 0 && cobrarEfectivo !== +total.toFixed(2) && (
            <div className="flex justify-between text-xs text-gray-500">
              <span>Cobro en efectivo (redondeado)</span>
              <span>{formatCurrency(cobrarEfectivo)}</span>
            </div>
          )}

          {/* Falta */}
          {pendiente > 0.009 && (
            <div className="flex justify-between text-sm">
              <span className="text-red-600 flex items-center gap-1">
                <AlertCircle size={14} /> Falta
              </span>
              <span className="font-medium text-red-600">{formatCurrency(pendiente)}</span>
            </div>
          )}

          {/* Vuelto — destacado */}
          {vuelto > 0.009 && (
            <div className="flex justify-between items-center bg-green-50 border border-green-200 rounded-lg p-2.5 -mx-1">
              <span className="text-green-700 font-semibold text-sm">Vuelto a entregar</span>
              <span className="font-bold text-green-700 text-lg">{formatCurrency(vuelto)}</span>
            </div>
          )}
        </div>
      </div>

      {/* Footer */}
      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200 mt-4">
        <Button variant="outline" onClick={onClose} disabled={isLoading}>
          Cancelar
        </Button>
        <Button onClick={handleConfirm} disabled={!canConfirm || isLoading}>
          {isLoading ? 'Procesando...' : `Confirmar Venta ${formatCurrency(total)}`}
        </Button>
      </div>
    </Modal>
  );
};
