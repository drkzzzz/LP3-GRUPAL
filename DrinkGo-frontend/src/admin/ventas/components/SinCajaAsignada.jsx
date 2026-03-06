/**
 * SinCajaAsignada.jsx
 * ───────────────────
 * Pantalla de bloqueo para cajeros con alcance "Solo su caja"
 * que aún no tienen una caja asignada por el administrador.
 */
import { AlertCircle, Monitor } from 'lucide-react';

export const SinCajaAsignada = ({ titulo = 'Sin caja asignada' }) => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">{titulo}</h1>
      </div>

      <div className="flex flex-col items-center justify-center bg-white rounded-xl border border-gray-200 shadow-sm py-16 px-6">
        <div className="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center mb-6">
          <Monitor size={40} className="text-red-500" />
        </div>

        <h2 className="text-xl font-semibold text-gray-900 mb-2">
          No tienes una caja asignada
        </h2>

        <p className="text-gray-500 text-center max-w-md mb-4">
          Tu cuenta tiene permisos restringidos a una caja específica, pero el administrador
          aún no te ha asignado una caja registradora. Contacta al administrador para que
          te asigne una caja.
        </p>

        <div className="flex items-center gap-2 bg-amber-50 border border-amber-200 rounded-lg px-4 py-3">
          <AlertCircle size={16} className="text-amber-600 shrink-0" />
          <p className="text-sm text-amber-700">
            El administrador debe asignarte una caja desde <strong>Ventas / POS → Caja</strong> editando una caja registradora.
          </p>
        </div>
      </div>
    </div>
  );
};
