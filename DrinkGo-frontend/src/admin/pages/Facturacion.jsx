import { ClipboardList } from 'lucide-react';

export const Facturacion = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Facturación Electrónica</h1>
        <p className="text-gray-600 mt-1">
          Emisión de comprobantes electrónicos, boletas, facturas y notas de crédito (SUNAT)
        </p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-12 flex flex-col items-center justify-center text-center">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <ClipboardList size={32} className="text-gray-500" />
        </div>
        <h2 className="text-lg font-semibold text-gray-800 mb-2">
          Facturación Electrónica
        </h2>
        <p className="text-gray-500 max-w-sm">
          Emisión y consulta de comprobantes electrónicos (boletas, facturas, notas de crédito)
          integrado con SUNAT. Módulo en construcción.
        </p>
      </div>
    </div>
  );
};
