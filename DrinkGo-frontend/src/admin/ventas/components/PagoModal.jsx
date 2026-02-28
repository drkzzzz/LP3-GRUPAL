/**
 * PagoModal.jsx
 * ─────────────
 * Modal de cobro: datos del cliente (DNI/RUC), tipo de comprobante,
 * y pagos mixtos (abonos) que deben sumar al menos el total.
 */
import { useState, useEffect, useMemo } from 'react';
import { CreditCard, Banknote, Smartphone, QrCode, Plus, X, AlertCircle } from 'lucide-react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { Badge } from '@/admin/components/ui/Badge';
import { formatCurrency } from '@/shared/utils/formatters';

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
  { value: 'nota_venta', label: 'Nota de Venta' },
  { value: 'boleta', label: 'Boleta' },
  { value: 'factura', label: 'Factura' },
];

const DOC_TYPES = [
  { value: '', label: 'Sin documento' },
  { value: 'DNI', label: 'DNI' },
  { value: 'RUC', label: 'RUC' },
];

/**
 * Validates customer document number based on type.
 * DNI = exactly 8 digits, RUC = exactly 11 digits.
 */
const validateDocNumber = (tipoDoc, numero) => {
  if (!tipoDoc || !numero) return null; // no validation needed
  const clean = numero.replace(/\s/g, '');
  if (tipoDoc === 'DNI') {
    if (!/^\d{8}$/.test(clean)) return 'DNI debe tener exactamente 8 dígitos';
  } else if (tipoDoc === 'RUC') {
    if (!/^\d{11}$/.test(clean)) return 'RUC debe tener exactamente 11 dígitos';
  }
  return null;
};

export const PagoModal = ({
  isOpen,
  onClose,
  onConfirm,
  total,
  metodosPago = [],
  isLoading = false,
}) => {
  const [pagos, setPagos] = useState([]);
  const [tipoComprobante, setTipoComprobante] = useState('nota_venta');
  const [tipoDocumento, setTipoDocumento] = useState('');
  const [docClienteNumero, setDocClienteNumero] = useState('');
  const [docClienteNombre, setDocClienteNombre] = useState('');
  const [docError, setDocError] = useState(null);

  /* Inicializar al abrir */
  useEffect(() => {
    if (isOpen && metodosPago.length > 0) {
      const efectivo = metodosPago.find(
        (m) => m.tipo === 'efectivo' || m.nombre?.toLowerCase() === 'efectivo',
      );
      setPagos([
        { metodoPagoId: efectivo?.id || metodosPago[0]?.id, monto: total },
      ]);
      setTipoComprobante('nota_venta');
      setTipoDocumento('');
      setDocClienteNumero('');
      setDocClienteNombre('');
      setDocError(null);
    }
  }, [isOpen, metodosPago, total]);

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
    setDocError(validateDocNumber(tipoDocumento, docClienteNumero));
  }, [tipoDocumento, docClienteNumero]);

  /* Calcular totales */
  const totalPagado = useMemo(
    () => pagos.reduce((acc, p) => acc + (parseFloat(p.monto) || 0), 0),
    [pagos],
  );
  const pendiente = total - totalPagado;
  const vuelto = totalPagado > total ? totalPagado - total : 0;

  /* Helper: get icon for a method */
  const getMethodInfo = (metodoPagoId) => {
    const metodo = metodosPago.find((m) => m.id === parseInt(metodoPagoId));
    const tipo = metodo?.tipo || 'otro';
    const Icon = ICON_MAP[tipo] || CreditCard;
    return { Icon, nombre: metodo?.nombre || 'Método', tipo };
  };

  /* Pago CRUD */
  const updatePago = (index, field, value) => {
    setPagos((prev) =>
      prev.map((p, i) => (i === index ? { ...p, [field]: value } : p)),
    );
  };

  const addPago = () => {
    const remaining = Math.max(0, total - totalPagado);
    setPagos((prev) => [
      ...prev,
      { metodoPagoId: metodosPago[0]?.id, monto: remaining > 0 ? remaining : 0 },
    ]);
  };

  const removePago = (index) => {
    if (pagos.length <= 1) return;
    setPagos((prev) => prev.filter((_, i) => i !== index));
  };

  /* Set all amount to single method */
  const setFullAmount = (index) => {
    const otherSum = pagos.reduce(
      (acc, p, i) => (i !== index ? acc + (parseFloat(p.monto) || 0) : acc),
      0,
    );
    const remaining = Math.max(0, total - otherSum);
    updatePago(index, 'monto', remaining);
  };

  /* Validation */
  const docRequired = tipoComprobante === 'factura';
  const isDocValid =
    !docRequired || (!docError && docClienteNumero.length > 0 && docClienteNombre.length > 0);
  const isPaymentValid = totalPagado >= total && pagos.some((p) => parseFloat(p.monto) > 0);
  const canConfirm = isPaymentValid && isDocValid && !docError;

  const handleConfirm = () => {
    if (!canConfirm) return;
    const cleanPagos = pagos
      .filter((p) => parseFloat(p.monto) > 0)
      .map((p) => ({
        metodoPagoId: parseInt(p.metodoPagoId),
        monto: parseFloat(p.monto),
        tipoReferencia: getMethodInfo(p.metodoPagoId).tipo,
        numeroReferencia: null,
      }));

    onConfirm({
      pagos: cleanPagos,
      tipoComprobante,
      docClienteNumero: docClienteNumero || null,
      docClienteNombre: docClienteNombre || null,
    });
  };

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
                {COMPROBANTE_OPTIONS.map((opt) => (
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

            {/* Número documento */}
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">
                N° Documento {docRequired && <span className="text-red-500">*</span>}
              </label>
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
              {docError && (
                <p className="text-xs text-red-500 mt-0.5 flex items-center gap-1">
                  <AlertCircle size={12} /> {docError}
                </p>
              )}
            </div>
          </div>

          {/* Razón social (para factura) */}
          {tipoComprobante === 'factura' && (
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">
                Razón Social <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={docClienteNombre}
                onChange={(e) => setDocClienteNombre(e.target.value)}
                placeholder="Nombre o razón social del cliente"
                className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
          )}

          {/* Nombre cliente (para boleta, opcional) */}
          {tipoComprobante === 'boleta' && tipoDocumento && (
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">
                Nombre del cliente (opcional)
              </label>
              <input
                type="text"
                value={docClienteNombre}
                onChange={(e) => setDocClienteNombre(e.target.value)}
                placeholder="Nombre del cliente"
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
            <Button variant="outline" size="sm" onClick={addPago}>
              <Plus size={14} className="mr-1" />
              Agregar método
            </Button>
          </div>

          <div className="space-y-2">
            {pagos.map((pago, index) => {
              const { Icon, nombre } = getMethodInfo(pago.metodoPagoId);
              return (
                <div
                  key={index}
                  className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg border border-gray-200"
                >
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
                      type="number"
                      min={0}
                      step={0.01}
                      value={pago.monto}
                      onChange={(e) => updatePago(index, 'monto', e.target.value)}
                      onFocus={(e) => e.target.select()}
                      className="w-full border border-gray-300 rounded-lg pl-8 pr-3 py-2 text-sm text-right font-medium focus:outline-none focus:ring-2 focus:ring-green-500"
                    />
                  </div>

                  {/* Quick: set full remaining amount */}
                  <button
                    type="button"
                    title="Poner monto restante"
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
              );
            })}
          </div>
        </div>

        {/* ── Resumen de pagos ── */}
        <div className="border-t border-gray-200 pt-3 space-y-1">
          <div className="flex justify-between text-sm">
            <span className="text-gray-600">Total pagado</span>
            <span className={`font-medium ${totalPagado >= total ? 'text-green-700' : 'text-gray-900'}`}>
              {formatCurrency(totalPagado)}
            </span>
          </div>
          {pendiente > 0.009 && (
            <div className="flex justify-between text-sm">
              <span className="text-red-600 flex items-center gap-1">
                <AlertCircle size={14} /> Falta
              </span>
              <span className="font-medium text-red-600">{formatCurrency(pendiente)}</span>
            </div>
          )}
          {vuelto > 0.009 && (
            <div className="flex justify-between text-sm">
              <span className="text-green-600">Vuelto</span>
              <span className="font-bold text-green-600">{formatCurrency(vuelto)}</span>
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
