/**
 * ComprobanteViewModal.jsx
 * ────────────────────────
 * Modal que muestra el comprobante completo de una venta.
 * Se usa desde la pestaña Comprobantes en Facturación.
 * Obtiene los datos de la venta (ítems, pagos) vía backend
 * y muestra un comprobante visual con opción de imprimir/descargar.
 */
import { useRef, useEffect, useState } from 'react';
import { Printer, X, Download, Loader2 } from 'lucide-react';
import { ventasService } from '@/admin/ventas/services/ventasService';
import { facturacionService } from '@/admin/facturacion/services/facturacionService';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { formatCurrency } from '@/shared/utils/formatters';

const MOTIVOS_NC = {
  '01': 'ANULACIÓN DE LA OPERACIÓN',
  '02': 'ANULACIÓN POR ERROR EN EL RUC',
  '03': 'CORRECCIÓN POR ERROR EN LA DESCRIPCIÓN',
  '04': 'DESCUENTO GLOBAL',
  '06': 'DEVOLUCIÓN TOTAL',
  '07': 'DEVOLUCIÓN POR ÍTEM',
  '09': 'DISMINUCIÓN EN EL VALOR',
};
const MOTIVOS_ND = {
  '01': 'INTERESES POR MORA',
  '02': 'AUMENTO EN EL VALOR',
  '03': 'PENALIDADES / OTROS CONCEPTOS',
};

/* ─── Constantes ─── */
const TIPO_LABEL = {
  boleta: 'BOLETA DE VENTA ELECTRÓNICA',
  factura: 'FACTURA ELECTRÓNICA',
  nota_credito: 'NOTA DE CRÉDITO ELECTRÓNICA',
  nota_debito: 'NOTA DE DÉBITO ELECTRÓNICA',
  nota_venta: 'NOTA DE VENTA',
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('es-PE', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  });
};

const formatTime = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleTimeString('es-PE', {
    hour: '2-digit', minute: '2-digit',
  });
};

/**
 * @param {Object} props.doc - Comprobante (DocumentosFacturacion) from backend
 * @param {Function} props.onClose - Close callback
 */
export const ComprobanteViewModal = ({ doc, onClose }) => {
  const printRef = useRef(null);
  const negocio = useAdminAuthStore((s) => s.negocio);

  const [loading, setLoading] = useState(true);
  const [ventaData, setVentaData] = useState(null);
  const [items, setItems] = useState([]);
  const [pagos, setPagos] = useState([]);
  const [metodosPago, setMetodosPago] = useState([]);

  /* ─── Fetch venta data ─── */
  useEffect(() => {
    if (!doc) return;

    const esNota = doc.tipoDocumento === 'nota_credito' || doc.tipoDocumento === 'nota_debito';

    const fetchData = async () => {
      setLoading(true);
      try {
        const ventaId = doc.ventaId || doc.venta?.id;

        const [venta, pagosRes, metodos] = await Promise.all([
          ventaId && negocio?.id
            ? ventasService.getById(ventaId, negocio.id).catch(() => null)
            : Promise.resolve(null),
          ventaId
            ? ventasService.getPagos(ventaId).catch(() => [])
            : Promise.resolve([]),
          negocio?.id
            ? ventasService.getMetodosPago(negocio.id).catch(() => [])
            : Promise.resolve([]),
        ]);

        setVentaData(venta);
        setPagos(Array.isArray(pagosRes) ? pagosRes : []);
        setMetodosPago(Array.isArray(metodos) ? metodos : []);

        if (esNota) {
          // Para NC/ND: obtener los ítems propios del documento (detalles de la nota)
          const notaItems = await facturacionService.getComprobanteItems(doc.id).catch(() => []);
          setItems(Array.isArray(notaItems) ? notaItems : []);
        } else {
          // Para boleta/factura: obtener detalle de la venta
          const detalle = ventaId
            ? await ventasService.getDetalle(ventaId).catch(() => [])
            : [];
          setItems(Array.isArray(detalle) ? detalle : []);
        }
      } catch {
        // Silently handle — show what we have
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [doc, negocio?.id]);

  if (!doc) return null;

  /* ─── Derivar datos del comprobante ─── */
  const tipoLabel = TIPO_LABEL[doc.tipoDocumento] || 'COMPROBANTE DE VENTA';
  const numeroDocumento = doc.numeroDocumento || '-';

  /* NC/ND detection */
  const esNota = doc.tipoDocumento === 'nota_credito' || doc.tipoDocumento === 'nota_debito';
  const esNotaCredito = doc.tipoDocumento === 'nota_credito';
  const codigoMotivo = doc.codigoMotivoNota || '';
  const descripcionMotivoSunat = esNotaCredito
    ? (MOTIVOS_NC[codigoMotivo] || '')
    : (MOTIVOS_ND[codigoMotivo] || '');
  const documentoAfectado = doc.documentoReferenciaNumero || '-';
  const mostrarItemsNota = codigoMotivo === '07';

  /* Emisor (negocio + sede de la venta) */
  const sede = ventaData?.sede || null;
  const nombreNegocio = negocio?.nombreComercial || negocio?.razonSocial || 'Mi Negocio';
  const rucNegocio = negocio?.ruc || '';
  const telefonoNegocio = sede?.telefono || negocio?.telefono || '';
  const direccionNegocio = sede?.direccion
    ? [sede.direccion, sede.ciudad, sede.departamento].filter(Boolean).join(', ')
    : [negocio?.direccion, negocio?.ciudad, negocio?.departamento].filter(Boolean).join(', ');

  /* Receptor (cliente) — prefer flat fields from entity, fallback to venta */
  const clienteNombre =
    doc.clienteNombre?.trim() ||
    ventaData?.docClienteNombre ||
    doc.razonSocialReceptor ||
    doc.receptor ||
    'Cliente General';
  const clienteDoc =
    doc.clienteNumeroDocumento ||
    ventaData?.docClienteNumero ||
    '-';

  /* Fecha */
  const fecha = doc.fechaEmision || doc.creadoEn;
  const fechaStr = formatDate(fecha);
  const horaStr = formatTime(ventaData?.fechaVenta || doc.creadoEn);

  /* Pagos con nombre de método */
  const pagosResumen = pagos.map((p) => {
    const metodo = metodosPago.find((m) => m.id === (p.metodoPagoId || p.metodoPago?.id));
    const nombre = metodo?.nombre || p.metodoPagoNombre || p.metodoPago?.nombre || 'Otro';
    // Construir detalle adicional (banco, últimos 4 dígitos, titular)
    const partes = [];
    if (p.banco) partes.push(p.banco);
    if (p.ultimosCuatroDigitos) partes.push(`****${p.ultimosCuatroDigitos}`);
    if (p.nombreTitular) partes.push(p.nombreTitular);
    if (p.numeroReferencia && !p.ultimosCuatroDigitos) partes.push(`Op. ${p.numeroReferencia}`);
    return { nombre, detalle: partes.join(' · '), monto: p.monto };
  });

  const metodoPagoTexto = pagosResumen.length === 0
    ? '-'
    : pagosResumen.length === 1
      ? pagosResumen[0].nombre
      : `Mixto: ${pagosResumen.map((p) => `${p.nombre} ${formatCurrency(p.monto)}`).join(', ')}`;

  /* Totales: prefer comprobante data, fallback to venta */
  const subtotal = doc.subtotal ?? ventaData?.subtotal ?? 0;
  const descuento = ventaData?.montoDescuento ?? 0;
  const igv = doc.impuestos ?? ventaData?.montoImpuesto ?? 0;
  const costoEnvio = parseFloat(ventaData?.costoEnvio || 0);
  const total = doc.total ?? ventaData?.total ?? 0;

  /* ─── Imprimir ─── */
  const handlePrint = () => {
    const content = printRef.current;
    if (!content) return;

    const printWindow = window.open('', '_blank', 'width=800,height=600');
    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>${tipoLabel} ${numeroDocumento}</title>
        <style>
          @page { size: A4; margin: 15mm 20mm; }
          * { margin: 0; padding: 0; box-sizing: border-box; }
          body { font-family: 'Segoe UI', Arial, sans-serif; color: #1a1a1a; font-size: 12px; line-height: 1.5; }
          .comprobante { max-width: 700px; margin: 0 auto; padding: 20px 0; }
        </style>
      </head>
      <body>
        ${content.innerHTML}
      </body>
      </html>
    `);
    printWindow.document.close();
    printWindow.focus();
    setTimeout(() => {
      printWindow.print();
      printWindow.close();
    }, 250);
  };

  /* ─── Item name resolver ─── */
  const getItemName = (item) => {
    // DetalleVentas serializes producto as nested object or productoId
    return item.producto?.nombre || item.nombreProducto || `Producto #${item.productoId || item.producto?.id || '?'}`;
  };

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={onClose}>
      <div
        className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal header */}
        <div className="flex items-center justify-between px-5 py-3 border-b border-gray-200 shrink-0">
          <h2 className="text-sm font-bold text-gray-900">Comprobante de Venta</h2>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              disabled={loading}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50"
            >
              <Download size={14} />
              Descargar
            </button>
            <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
              <X size={18} />
            </button>
          </div>
        </div>

        {/* Content */}
        <div className="overflow-y-auto flex-1 p-5">
          {loading ? (
            <div className="flex flex-col items-center justify-center py-16 text-gray-400">
              <Loader2 size={32} className="animate-spin mb-3" />
              <p className="text-sm">Cargando comprobante...</p>
            </div>
          ) : (
            <div ref={printRef}>
              <div style={{ maxWidth: 700, margin: '0 auto' }}>

                {esNota ? (
                  /* ══ NOTA DE CRÉDITO / DÉBITO ══ */
                  <div style={{ fontFamily: 'Arial, sans-serif', fontSize: 12, color: '#1a1a1a' }}>

                    {/* Cabecera: Emisor + tipo de nota (estilo SUNAT) */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 18, paddingBottom: 14, borderBottom: '2px solid #222' }}>
                      <div>
                        <div style={{ fontWeight: 800, fontSize: 15, marginBottom: 2 }}>{nombreNegocio}</div>
                        {rucNegocio && <div style={{ fontSize: 11, color: '#555' }}>RUC: {rucNegocio}</div>}
                        {direccionNegocio && <div style={{ fontSize: 11, color: '#555' }}>{direccionNegocio}</div>}
                        <div style={{ fontSize: 11, color: '#555', marginTop: 3 }}>Fecha de Emisión: {fechaStr}</div>
                      </div>
                      <div style={{ border: '2px solid #222', padding: '10px 18px', textAlign: 'center', minWidth: 220 }}>
                        <div style={{ fontWeight: 800, fontSize: 12, letterSpacing: '0.03em' }}>
                          {esNotaCredito ? 'NOTA DE CRÉDITO ELECTRÓNICA' : 'NOTA DE DÉBITO ELECTRÓNICA'}
                        </div>
                        {rucNegocio && <div style={{ fontSize: 11, marginTop: 3 }}>RUC: {rucNegocio}</div>}
                        <div style={{ fontWeight: 700, fontSize: 14, marginTop: 4, letterSpacing: '0.05em' }}>
                          {numeroDocumento}
                        </div>
                      </div>
                    </div>

                    {/* Motivo SUNAT (derecha) */}
                    {descripcionMotivoSunat && (
                      <div style={{ textAlign: 'right', fontWeight: 800, fontSize: 13, marginBottom: 14, marginTop: 12 }}>
                        {descripcionMotivoSunat}
                      </div>
                    )}

                    {/* Documento que modifica */}
                    <div style={{ marginBottom: 14 }}>
                      <div style={{ fontWeight: 700, fontSize: 12, borderBottom: '1px solid #999', paddingBottom: 4, marginBottom: 8 }}>
                        Documento que modifica:
                      </div>
                      <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 11 }}>
                        <tbody>
                          <tr>
                            <td style={{ width: 150, padding: '3px 0', color: '#555', verticalAlign: 'top' }}>
                              {esNotaCredito ? 'Boleta / Factura' : 'Documento Original'}
                            </td>
                            <td style={{ padding: '3px 0', fontWeight: 700 }}>: {documentoAfectado}</td>
                          </tr>
                          <tr>
                            <td style={{ padding: '3px 0', color: '#555' }}>Señor(es)</td>
                            <td style={{ padding: '3px 0', fontWeight: 600 }}>: {clienteNombre}</td>
                          </tr>
                          {clienteDoc !== '-' && (
                            <tr>
                              <td style={{ padding: '3px 0', color: '#555' }}>Doc. Identidad</td>
                              <td style={{ padding: '3px 0' }}>: {clienteDoc}</td>
                            </tr>
                          )}
                          <tr>
                            <td style={{ padding: '3px 0', color: '#555' }}>Tipo de Moneda</td>
                            <td style={{ padding: '3px 0' }}>: SOLES</td>
                          </tr>
                          <tr>
                            <td style={{ padding: '6px 0 3px', color: '#555', fontWeight: 700 }}>Motivo o Sustento</td>
                            <td style={{ padding: '6px 0 3px', fontWeight: 700 }}>
                              : {(doc.descripcionMotivoNota || '-').toUpperCase()}
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>

                    {/* Tabla de ítems — solo motivo 07 */}
                    {mostrarItemsNota && items.length > 0 && (
                      <table style={{ width: '100%', borderCollapse: 'collapse', marginBottom: 12, fontSize: 11 }}>
                        <thead>
                          <tr style={{ background: '#f3f4f6' }}>
                            <th style={{ border: '1px solid #ccc', padding: '6px 8px', textAlign: 'center', width: 60 }}>Cantidad</th>
                            <th style={{ border: '1px solid #ccc', padding: '6px 8px', textAlign: 'left' }}>Descripción</th>
                            <th style={{ border: '1px solid #ccc', padding: '6px 8px', textAlign: 'right', width: 100 }}>Valor Unitario</th>
                            <th style={{ border: '1px solid #ccc', padding: '6px 8px', textAlign: 'right', width: 100 }}>Total</th>
                          </tr>
                        </thead>
                        <tbody>
                          {items.map((item, idx) => (
                            <tr key={idx}>
                              <td style={{ border: '1px solid #ccc', padding: '5px 8px', textAlign: 'center' }}>{Number(item.cantidad)}</td>
                              <td style={{ border: '1px solid #ccc', padding: '5px 8px' }}>
                                {item.producto?.nombre || getItemName(item)}
                              </td>
                              <td style={{ border: '1px solid #ccc', padding: '5px 8px', textAlign: 'right' }}>{formatCurrency(item.precioUnitario)}</td>
                              <td style={{ border: '1px solid #ccc', padding: '5px 8px', textAlign: 'right' }}>{formatCurrency(item.total || item.precioUnitario * item.cantidad)}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    )}
                {/* ─── Datos cliente ─── */}
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '4px 24px', fontSize: 11, marginBottom: 12 }}>
                  <div>
                    <span style={{ fontWeight: 600, color: '#333' }}>Cliente: </span>
                    <span style={{ color: '#555' }}>{clienteNombre}</span>
                  </div>
                  <div>
                    <span style={{ fontWeight: 600, color: '#333' }}>Fecha: </span>
                    <span style={{ color: '#555' }}>{fechaStr}{horaStr ? ` ${horaStr}` : ''}</span>
                  </div>
                  <div>
                    <span style={{ fontWeight: 600, color: '#333' }}>Documento: </span>
                    <span style={{ color: '#555' }}>{clienteDoc}</span>
                  </div>
                  <div>
                    <span style={{ fontWeight: 600, color: '#333' }}>Método de Pago: </span>
                    <span style={{ color: '#555' }}>{metodoPagoTexto}</span>
                  </div>
                  {pagosResumen.length === 1 && pagosResumen[0].detalle && (
                    <div>
                      <span style={{ fontWeight: 600, color: '#333' }}>Detalle de Pago: </span>
                      <span style={{ color: '#555' }}>{pagosResumen[0].detalle}</span>
                    </div>
                  )}
                </div>

                    {/* Totales */}
                    <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                      <table style={{ borderCollapse: 'collapse', fontSize: 11, width: 260 }}>
                        <tbody>
                          <tr>
                            <td style={{ border: '1px solid #ccc', padding: '4px 10px', color: '#555' }}>Sub Total Ventas</td>
                            <td style={{ border: '1px solid #ccc', padding: '4px 10px', textAlign: 'right' }}>{formatCurrency(subtotal)}</td>
                          </tr>
                          <tr>
                            <td style={{ border: '1px solid #ccc', padding: '4px 10px', color: '#555' }}>IGV ({negocio?.porcentajeIgv ?? 18}%)</td>
                            <td style={{ border: '1px solid #ccc', padding: '4px 10px', textAlign: 'right' }}>{formatCurrency(igv)}</td>
                          </tr>
                          <tr>
                            <td style={{ border: '1px solid #ccc', padding: '5px 10px', fontWeight: 700, background: '#f3f4f6' }}>Importe Total</td>
                            <td style={{ border: '1px solid #ccc', padding: '5px 10px', textAlign: 'right', fontWeight: 700, background: '#f3f4f6' }}>{formatCurrency(total)}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>

                    {/* Pie */}
                    <div style={{ marginTop: 20, borderTop: '1px solid #ddd', paddingTop: 10, textAlign: 'center', fontSize: 10, color: '#888', fontStyle: 'italic' }}>
                      Esta es una representación impresa de la {esNotaCredito ? 'nota de crédito' : 'nota de débito'} electrónica.
                    </div>
                  </div>
                ) : (
                  /* ══ BOLETA / FACTURA / NOTA DE VENTA ══ */
                  <>
                    {/* ─── Header: Emisor + Tipo ─── */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16, paddingBottom: 12, borderBottom: '2px solid #d97706' }}>
                      <div style={{ flex: 1 }}>
                        <div style={{ fontSize: 18, fontWeight: 700, color: '#1a1a1a', marginBottom: 4 }}>{nombreNegocio}</div>
                        <div style={{ fontSize: 11, color: '#555', lineHeight: 1.7 }}>
                          {direccionNegocio && <>{direccionNegocio}<br /></>}
                          {telefonoNegocio && <>Teléfono: {telefonoNegocio}<br /></>}
                          {rucNegocio && <>RUC: {rucNegocio}</>}
                        </div>
                      </div>
                      <div style={{ textAlign: 'right', border: '2px solid #d97706', padding: '10px 16px', borderRadius: 4, minWidth: 220 }}>
                        <div style={{ fontSize: 12, fontWeight: 700, color: '#d97706' }}>{tipoLabel}</div>
                        <div style={{ fontSize: 13, fontWeight: 600, color: '#333', marginTop: 2 }}>{numeroDocumento}</div>
                      </div>
                    </div>

                    {/* ─── Datos cliente ─── */}
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '4px 24px', fontSize: 11, marginBottom: 12 }}>
                      <div>
                        <span style={{ fontWeight: 600, color: '#333' }}>Cliente: </span>
                        <span style={{ color: '#555' }}>{clienteNombre}</span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 600, color: '#333' }}>Fecha: </span>
                        <span style={{ color: '#555' }}>{fechaStr}{horaStr ? ` ${horaStr}` : ''}</span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 600, color: '#333' }}>Documento: </span>
                        <span style={{ color: '#555' }}>{clienteDoc}</span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 600, color: '#333' }}>Método de Pago: </span>
                        <span style={{ color: '#555' }}>{metodoPagoTexto}</span>
                      </div>
                    </div>

                    {/* ─── Tabla de ítems ─── */}
                    {items.length > 0 ? (
                      <table style={{ width: '100%', borderCollapse: 'collapse', margin: '12px 0' }}>
                        <thead>
                          <tr>
                            <th style={{ background: '#f9fafb', borderTop: '1px solid #ddd', borderBottom: '1px solid #ddd', padding: '8px 10px', textAlign: 'left', fontSize: 11, fontWeight: 600, color: '#333', width: 50 }}>
                              Cant.
                            </th>
                            <th style={{ background: '#f9fafb', borderTop: '1px solid #ddd', borderBottom: '1px solid #ddd', padding: '8px 10px', textAlign: 'left', fontSize: 11, fontWeight: 600, color: '#333' }}>
                              Descripción
                            </th>
                            <th style={{ background: '#f9fafb', borderTop: '1px solid #ddd', borderBottom: '1px solid #ddd', padding: '8px 10px', textAlign: 'right', fontSize: 11, fontWeight: 600, color: '#333', width: 90 }}>
                              P. Unit.
                            </th>
                            <th style={{ background: '#f9fafb', borderTop: '1px solid #ddd', borderBottom: '1px solid #ddd', padding: '8px 10px', textAlign: 'right', fontSize: 11, fontWeight: 600, color: '#333', width: 90 }}>
                              Total
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          {items.map((item, idx) => (
                            <tr key={idx}>
                              <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444' }}>
                                {Number(item.cantidad)}
                              </td>
                              <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444' }}>
                                {getItemName(item)}
                              </td>
                              <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444', textAlign: 'right' }}>
                                {formatCurrency(item.precioUnitario)}
                              </td>
                              <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444', textAlign: 'right' }}>
                                {formatCurrency(item.subtotal || item.precioUnitario * item.cantidad)}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    ) : (
                      <div style={{ textAlign: 'center', padding: '16px 0', fontSize: 11, color: '#999', borderTop: '1px solid #eee', borderBottom: '1px solid #eee', margin: '12px 0' }}>
                        Detalle de ítems no disponible
                      </div>
                    )}

                    {costoEnvio > 0 && (
                      <div style={{ display: 'flex', justifyContent: 'space-between', padding: '3px 0', fontSize: 11, color: '#555' }}>
                        <span>Delivery:</span>
                        <span>{formatCurrency(costoEnvio)}</span>
                      </div>
                    )}
                    {descuento > 0 && (
                      <div style={{ display: 'flex', justifyContent: 'space-between', padding: '3px 0', fontSize: 11, color: '#dc2626' }}>
                        <span>Descuento:</span>
                        <span>-{formatCurrency(descuento)}</span>
                      </div>
                    )}

                    {/* ─── Totales ─── */}
                    <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 4 }}>
                      <div style={{ width: 240 }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '3px 0', fontSize: 11, color: '#555' }}>
                          <span>Subtotal:</span>
                          <span>{formatCurrency(subtotal)}</span>
                        </div>
                        {descuento > 0 && (
                          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '3px 0', fontSize: 11, color: '#dc2626' }}>
                            <span>Descuento:</span>
                            <span>-{formatCurrency(descuento)}</span>
                          </div>
                        )}
                        {igv > 0 && (
                          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '3px 0', fontSize: 11, color: '#555' }}>
                            <span>IGV ({negocio?.porcentajeIgv ?? 18}%):</span>
                            <span>{formatCurrency(igv)}</span>
                          </div>
                        )}
                        <div style={{
                          display: 'flex', justifyContent: 'space-between', padding: '6px 0 3px',
                          fontSize: 13, fontWeight: 700, color: '#1a1a1a', borderTop: '2px solid #333',
                        }}>
                          <span>TOTAL:</span>
                          <span>{formatCurrency(total)}</span>
                        </div>
                      </div>
                    </div>
                  </>
                )}

                {/* ─── Footer ─── */}
                <div style={{ marginTop: 24, textAlign: 'center', borderTop: '1px dashed #ccc', paddingTop: 12 }}>
                  <p style={{ fontSize: 11, color: '#666' }}>¡Gracias por su preferencia!</p>
                  {doc.tipoDocumento !== 'nota_venta' ? (
                    <p style={{ fontWeight: 600, fontSize: 10, color: '#888', marginTop: 4 }}>
                      Este comprobante es válido como documento tributario
                    </p>
                  ) : (
                    <p style={{ fontWeight: 600, fontSize: 10, color: '#888', marginTop: 4 }}>
                      Documento interno — No es comprobante tributario
                    </p>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Modal footer */}
        <div className="flex justify-end gap-2 px-5 py-3 border-t border-gray-200 bg-gray-50 rounded-b-lg shrink-0">
          <button
            onClick={onClose}
            className="px-4 py-2 text-xs font-medium text-gray-700 bg-white border border-gray-200 rounded-md hover:bg-gray-100 transition-colors"
          >
            Cerrar
          </button>
          <button
            onClick={handlePrint}
            disabled={loading}
            className="flex items-center gap-1.5 px-4 py-2 text-xs font-medium text-white bg-green-600 rounded-md hover:bg-green-700 transition-colors disabled:opacity-50"
          >
            <Printer size={14} />
            Imprimir comprobante
          </button>
        </div>
      </div>
    </div>
  );
};
