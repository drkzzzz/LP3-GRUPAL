/**
 * ComprobanteVenta.jsx
 * ────────────────────
 * Comprobante de venta imprimible.
 * Estructura válida para emisión tributaria (SUNAT Perú):
 *   - Datos del emisor (negocio + sede)
 *   - Tipo y número de comprobante
 *   - Datos del receptor (cliente)
 *   - Detalle de ítems (cantidad, descripción, P.Unit, Total)
 *   - Subtotal, IGV, Descuento, Total
 *   - Método de pago
 *   - Pie de página legal
 */
import React, { useRef } from 'react';
import { Printer, X } from 'lucide-react';
import { formatCurrency } from '@/shared/utils/formatters';

const TIPO_COMPROBANTE_LABEL = {
  boleta: 'BOLETA DE VENTA ELECTRÓNICA',
  factura: 'FACTURA ELECTRÓNICA',
  nota_venta: 'NOTA DE VENTA',
};

const TIPO_COMPROBANTE_SHORT = {
  boleta: 'BOLETA DE VENTA',
  factura: 'FACTURA',
  nota_venta: 'NOTA DE VENTA',
};

/**
 * @param {Object} props
 * @param {Object} props.venta - Venta response from backend
 * @param {Array}  props.items - Cart items [{producto, cantidad, descuento}]
 * @param {Array}  props.pagos - Payment methods [{metodoPagoId, monto, tipoReferencia}]
 * @param {Object} props.negocio - Negocio data {razonSocial, nombreComercial, ruc, telefono, email, direccion, ciudad, urlLogo}
 * @param {Object} props.sede - Sede data {nombre, direccion, telefono, ciudad}
 * @param {Array}  props.metodosPago - Available payment methods for label lookup
 * @param {Function} props.onClose - Close callback
 */
export const ComprobanteVenta = ({
  venta,
  items = [],
  pagos = [],
  negocio,
  sede,
  metodosPago = [],
  onClose,
}) => {
  const printRef = useRef(null);

  if (!venta) return null;

  /* ─── Derivar datos ─── */
  const tipoLabel = TIPO_COMPROBANTE_LABEL[venta.tipoComprobante] || 'COMPROBANTE DE VENTA';
  const tipoShort = TIPO_COMPROBANTE_SHORT[venta.tipoComprobante] || 'COMPROBANTE';

  const nombreNegocio = negocio?.nombreComercial || negocio?.razonSocial || 'Mi Negocio';
  const rucNegocio = negocio?.ruc || '';
  const telefonoNegocio = sede?.telefono || negocio?.telefono || '';
  const direccionSede = sede?.direccion
    ? `${sede.direccion}${sede.ciudad ? `, ${sede.ciudad}` : ''}${sede.departamento ? ` - ${sede.departamento}` : ''}`
    : negocio?.direccion
      ? `${negocio.direccion}${negocio.ciudad ? `, ${negocio.ciudad}` : ''}${negocio.departamento ? ` - ${negocio.departamento}` : ''}`
      : '';

  const fechaVenta = venta.fechaVenta
    ? new Date(venta.fechaVenta).toLocaleDateString('es-PE', {
        year: 'numeric', month: '2-digit', day: '2-digit',
      })
    : new Date().toLocaleDateString('es-PE');

  const horaVenta = venta.fechaVenta
    ? new Date(venta.fechaVenta).toLocaleTimeString('es-PE', {
        hour: '2-digit', minute: '2-digit',
      })
    : '';

  /* Número de comprobante — usar el de la facturación si existe, sino el de venta */
  const numeroComprobante = venta.numeroDocumentoFacturacion || venta.numeroVenta || '-';

  /* Cliente */
  const clienteNombre = venta.docClienteNombre || 'Cliente General';
  const clienteDoc = venta.docClienteNumero || '-';

  /* Pagos con nombre */
  const pagosResumen = pagos.map((p) => {
    const metodo = metodosPago.find((m) => m.id === p.metodoPagoId);
    return { nombre: metodo?.nombre || 'Otro', monto: p.monto };
  });

  const metodoPagoTexto = pagosResumen.length === 1
    ? `${pagosResumen[0].nombre}`
    : `Mixto: ${pagosResumen.map((p) => `${p.nombre} ${formatCurrency(p.monto)}`).join(', ')}`;

  /* Totales */
  const subtotal = venta.subtotal ?? items.reduce((acc, i) => acc + i.producto.precioVenta * i.cantidad, 0);
  const descuento = venta.montoDescuento ?? 0;
  const igv = venta.montoImpuesto ?? 0;
  const total = venta.total ?? 0;

  /* ─── Imprimir ─── */
  const handlePrint = () => {
    const content = printRef.current;
    if (!content) return;

    const printWindow = window.open('', '_blank', 'width=800,height=600');
    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>${tipoShort} ${numeroComprobante}</title>
        <style>
          @page { size: A4; margin: 15mm 20mm; }
          * { margin: 0; padding: 0; box-sizing: border-box; }
          body { font-family: 'Segoe UI', Arial, sans-serif; color: #1a1a1a; font-size: 12px; line-height: 1.5; }
          .comprobante { max-width: 700px; margin: 0 auto; padding: 20px 0; }

          /* Header */
          .header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 2px solid #d97706; }
          .emisor-info { flex: 1; }
          .emisor-nombre { font-size: 20px; font-weight: 700; color: #1a1a1a; margin-bottom: 4px; }
          .emisor-detalle { font-size: 11px; color: #555; line-height: 1.6; }
          .tipo-box { text-align: right; border: 2px solid #d97706; padding: 10px 16px; border-radius: 4px; min-width: 220px; }
          .tipo-label { font-size: 14px; font-weight: 700; color: #d97706; }
          .tipo-numero { font-size: 13px; font-weight: 600; color: #333; margin-top: 2px; }

          /* Separator */
          hr.sep { border: none; border-top: 1px solid #d97706; margin: 12px 0; }

          /* Datos cliente */
          .datos-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 4px 24px; font-size: 11px; margin-bottom: 12px; }
          .datos-grid .label { font-weight: 600; color: #333; }
          .datos-grid .value { color: #555; }

          /* Tabla items */
          table { width: 100%; border-collapse: collapse; margin: 12px 0; }
          thead th { background: #f9fafb; border-top: 1px solid #ddd; border-bottom: 1px solid #ddd; padding: 8px 10px; text-align: left; font-size: 11px; font-weight: 600; color: #333; }
          thead th.right { text-align: right; }
          tbody td { padding: 6px 10px; font-size: 11px; border-bottom: 1px solid #eee; color: #444; }
          tbody td.right { text-align: right; }

          /* Totales */
          .totales { display: flex; justify-content: flex-end; margin-top: 4px; }
          .totales-table { width: 260px; }
          .totales-row { display: flex; justify-content: space-between; padding: 3px 0; font-size: 11px; color: #555; }
          .totales-row.total { border-top: 2px solid #333; padding-top: 6px; font-size: 13px; font-weight: 700; color: #1a1a1a; }
          .totales-row.descuento { color: #dc2626; }

          /* Footer */
          .footer { margin-top: 24px; text-align: center; border-top: 1px dashed #ccc; padding-top: 12px; }
          .footer p { font-size: 11px; color: #666; }
          .footer .legal { font-weight: 600; font-size: 10px; color: #888; margin-top: 4px; }
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
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors"
            >
              <Printer size={14} />
              Imprimir
            </button>
            <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
              <X size={18} />
            </button>
          </div>
        </div>

        {/* Comprobante content (printable) */}
        <div className="overflow-y-auto flex-1 p-5">
          <div ref={printRef}>
            <div className="comprobante" style={{ maxWidth: 700, margin: '0 auto' }}>
              {/* ─── Header: Emisor + Tipo comprobante ─── */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16, paddingBottom: 12, borderBottom: '2px solid #d97706' }}>
                {/* Emisor */}
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 18, fontWeight: 700, color: '#1a1a1a', marginBottom: 4 }}>
                    {nombreNegocio}
                  </div>
                  <div style={{ fontSize: 11, color: '#555', lineHeight: 1.7 }}>
                    {direccionSede && <>{direccionSede}<br /></>}
                    {telefonoNegocio && <>Teléfono: {telefonoNegocio}<br /></>}
                    {rucNegocio && <>RUC: {rucNegocio}</>}
                  </div>
                </div>
                {/* Tipo + Número */}
                <div style={{ textAlign: 'right', border: '2px solid #d97706', padding: '10px 16px', borderRadius: 4, minWidth: 220 }}>
                  <div style={{ fontSize: 13, fontWeight: 700, color: '#d97706' }}>{tipoLabel}</div>
                  <div style={{ fontSize: 13, fontWeight: 600, color: '#333', marginTop: 2 }}>{numeroComprobante}</div>
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
                  <span style={{ color: '#555' }}>{fechaVenta}{horaVenta ? ` ${horaVenta}` : ''}</span>
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
                  {items.map((item, idx) => {
                    const esCombo = item.producto._tipo === 'combo';
                    return esCombo ? (
                      <React.Fragment key={idx}>
                        {/* Fila del combo */}
                        <tr>
                          <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: 'none', color: '#444', fontWeight: 600 }}>
                            {item.cantidad}
                          </td>
                          <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: 'none', color: '#6b21a8', fontWeight: 600 }}>
                            📦 {item.producto.nombre}
                          </td>
                          <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: 'none', color: '#444', textAlign: 'right', fontWeight: 600 }}>
                            {formatCurrency(item.producto.precioVenta)}
                          </td>
                          <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: 'none', color: '#444', textAlign: 'right', fontWeight: 600 }}>
                            {formatCurrency(item.producto.precioVenta * item.cantidad)}
                          </td>
                        </tr>
                        {/* Componentes del combo */}
                        {(item.producto._productosCombo || []).map((comp, cIdx) => (
                          <tr key={`${idx}-c-${cIdx}`}>
                            <td style={{ padding: '2px 10px 2px 20px', fontSize: 10, borderBottom: cIdx === (item.producto._productosCombo || []).length - 1 ? '1px solid #eee' : 'none', color: '#888' }}>
                              {comp.cantidad * item.cantidad}
                            </td>
                            <td style={{ padding: '2px 10px', fontSize: 10, borderBottom: cIdx === (item.producto._productosCombo || []).length - 1 ? '1px solid #eee' : 'none', color: '#888' }}>
                              └ {comp.nombre}
                            </td>
                            <td style={{ padding: '2px 10px', fontSize: 10, borderBottom: cIdx === (item.producto._productosCombo || []).length - 1 ? '1px solid #eee' : 'none', color: '#888', textAlign: 'right' }}>
                              {formatCurrency(comp.precioVenta)}
                            </td>
                            <td style={{ padding: '2px 10px', fontSize: 10, borderBottom: cIdx === (item.producto._productosCombo || []).length - 1 ? '1px solid #eee' : 'none', color: '#888', textAlign: 'right' }}>
                            </td>
                          </tr>
                        ))}
                      </React.Fragment>
                    ) : (
                      <tr key={idx}>
                        <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444' }}>
                          {item.cantidad}
                        </td>
                        <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444' }}>
                          {item.producto.nombre}
                          {item.producto._promocion && (
                            <span style={{ fontSize: 9, color: '#ea580c', marginLeft: 4 }}>
                              🏷️ {item.producto._promocion.nombre}
                            </span>
                          )}
                        </td>
                        <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444', textAlign: 'right' }}>
                          {formatCurrency(item.producto.precioVenta)}
                        </td>
                        <td style={{ padding: '6px 10px', fontSize: 11, borderBottom: '1px solid #eee', color: '#444', textAlign: 'right' }}>
                          {formatCurrency(item.producto.precioVenta * item.cantidad)}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>

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

              {/* ─── Footer ─── */}
              <div style={{ marginTop: 24, textAlign: 'center', borderTop: '1px dashed #ccc', paddingTop: 12 }}>
                <p style={{ fontSize: 11, color: '#666' }}>¡Gracias por su preferencia!</p>
                {venta.tipoComprobante !== 'nota_venta' && (
                  <p style={{ fontWeight: 600, fontSize: 10, color: '#888', marginTop: 4 }}>
                    Este comprobante es válido como documento tributario
                  </p>
                )}
                {venta.tipoComprobante === 'nota_venta' && (
                  <p style={{ fontWeight: 600, fontSize: 10, color: '#888', marginTop: 4 }}>
                    Documento interno — No es comprobante tributario
                  </p>
                )}
              </div>
            </div>
          </div>
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
            className="flex items-center gap-1.5 px-4 py-2 text-xs font-medium text-white bg-green-600 rounded-md hover:bg-green-700 transition-colors"
          >
            <Printer size={14} />
            Imprimir comprobante
          </button>
        </div>
      </div>
    </div>
  );
};
