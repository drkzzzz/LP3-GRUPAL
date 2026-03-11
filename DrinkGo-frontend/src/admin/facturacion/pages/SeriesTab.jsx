/**
 * SeriesTab.jsx
 * ─────────────
 * Series de facturación - CRUD.
 * Layout simplificado: contador + botón + tabla.
 */
import { useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Plus, Edit, Trash2, Hash, Info } from 'lucide-react';
import { useSeries, useCrearSerie, useActualizarSerie, useEliminarSerie } from '../hooks/useFacturacion';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

const TIPO_DOC_LABELS = {
  boleta: 'Boleta',
  factura: 'Factura',
  nota_credito: 'Nota de Crédito',
  nota_debito: 'Nota de Débito',
};

/**
 * Reglas de prefijo SUNAT por tipo de documento:
 *   boleta       → debe empezar con B (ej: B001)
 *   factura      → debe empezar con F (ej: F001)
 *   nota_credito → debe empezar con BC (para boletas) o FC (para facturas)
 *   nota_debito  → debe empezar con BD (para boletas) o FD (para facturas)
 */
const SERIE_PREFIX_RULES = {
  boleta:       { prefixes: ['B'],          prefixLen: 1, hint: 'B001, B002...' },
  factura:      { prefixes: ['F'],          prefixLen: 1, hint: 'F001, F002...' },
  nota_credito: { prefixes: ['BC', 'FC'],   prefixLen: 2, hint: 'BC01 (boleta) o FC01 (factura)' },
  nota_debito:  { prefixes: ['BD', 'FD'],   prefixLen: 2, hint: 'BD01 (boleta) o FD01 (factura)' },
};

const validarPrefijo = (serie, tipoDoc) => {
  if (!serie || serie.length < 4) return 'El código debe tener al menos 4 caracteres';
  const rule = SERIE_PREFIX_RULES[tipoDoc];
  if (!rule) return null;
  const prefix = serie.substring(0, rule.prefixLen).toUpperCase();
  if (!rule.prefixes.includes(prefix)) {
    return `Debe empezar con ${rule.prefixes.join(' o ')} (ej: ${rule.hint})`;
  }
  return null;
};

export const SeriesTab = () => {
  const { negocioId } = useOutletContext();
  const negocio = useAdminAuthStore((s) => s.negocio);
  const tienePse = negocio?.tienePse ?? false;
  const sedeId = negocio?.sedeId || negocio?.sedes?.[0]?.id || 1;

  const { data: series = [], isLoading } = useSeries(negocioId);
  const crearSerie = useCrearSerie();
  const actualizarSerie = useActualizarSerie();
  const eliminarSerie = useEliminarSerie();

  const [showForm, setShowForm] = useState(false);
  const [editingSerie, setEditingSerie] = useState(null);
  const [formData, setFormData] = useState({
    tipoDocumento: 'boleta',
    serie: '',
    esPredeterminada: false,
  });
  const [serieError, setSerieError] = useState(null);

  const resetForm = () => {
    setFormData({ tipoDocumento: 'boleta', serie: '', esPredeterminada: false });
    setSerieError(null);
    setEditingSerie(null);
    setShowForm(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Validar prefijo según tipo de documento
    if (!editingSerie) {
      const err = validarPrefijo(formData.serie, formData.tipoDocumento);
      if (err) {
        setSerieError(err);
        return;
      }
    }
    setSerieError(null);
    try {
      if (editingSerie) {
        await actualizarSerie.mutateAsync({
          id: editingSerie.id,
          data: {
            serie: formData.serie.toUpperCase(),
            esPredeterminada: formData.esPredeterminada,
          },
        });
      } else {
        await crearSerie.mutateAsync({
          negocioId,
          sedeId,
          tipoDocumento: formData.tipoDocumento,
          serie: formData.serie.toUpperCase(),
          esPredeterminada: formData.esPredeterminada,
        });
      }
      resetForm();
    } catch (err) {
      // Backend may return additional validation errors
      const msg = err?.response?.data?.error || err?.message;
      if (msg) setSerieError(msg);
    }
  };

  const handleEdit = (serie) => {
    setEditingSerie(serie);
    setFormData({
      tipoDocumento: serie.tipoDocumento,
      serie: serie.serie,
      esPredeterminada: serie.esPredeterminada || false,
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    try {
      await eliminarSerie.mutateAsync(id);
    } catch (err) {
      console.error('Error al eliminar serie:', err);
    }
  };

  return (
    <div className="space-y-6">
      {/* ─── Header ─── */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Series de Facturación</h1>
        <p className="text-gray-600 mt-1">
          {tienePse
            ? 'Administre las series de documentos electrónicos de su negocio'
            : 'Administre las series de documentos de su negocio'}
        </p>
      </div>

      {/* ─── Guía de prefijos (solo con PSE) ─── */}
      {tienePse && (
        <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
          <div className="flex items-start gap-3">
            <Info size={18} className="text-blue-600 shrink-0 mt-0.5" />
            <div className="text-sm text-blue-800 space-y-1">
              <p className="font-semibold">Prefijos requeridos según tipo de documento:</p>
              <div className="grid grid-cols-2 gap-x-8 gap-y-0.5 mt-1">
                <span><span className="font-mono font-bold">B001</span> — Boleta de venta</span>
                <span><span className="font-mono font-bold">F001</span> — Factura</span>
                <span><span className="font-mono font-bold">BC01</span> — Nota de Crédito sobre Boleta</span>
                <span><span className="font-mono font-bold">FC01</span> — Nota de Crédito sobre Factura</span>
                <span><span className="font-mono font-bold">BD01</span> — Nota de Débito sobre Boleta</span>
                <span><span className="font-mono font-bold">FD01</span> — Nota de Débito sobre Factura</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ─── Card ─── */}
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        {/* Contador + Botón */}
        <div className="flex items-center justify-between mb-5">
          <div className="flex items-center gap-2 text-gray-600">
            <Hash size={18} className="text-gray-400" />
            <span className="text-sm">{series.length} serie(s) registrada(s)</span>
          </div>
          <button
            onClick={() => { resetForm(); setShowForm(true); }}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 text-sm font-medium"
          >
            <Plus size={18} />
            Nueva Serie
          </button>
        </div>

        {/* ─── Tabla ─── */}
        {isLoading ? (
          <div className="text-center py-12 text-gray-500">Cargando series...</div>
        ) : series.length === 0 ? (
          <div>
            <table className="w-full text-sm">
              <thead>
                <tr className="border-y border-gray-200 bg-gray-50/50">
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[50px]">#</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Serie</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Tipo de Documento</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Último Correlativo</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Predeterminada</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Estado</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[120px]">Acciones</th>
                </tr>
              </thead>
            </table>
            <div className="text-center py-10 text-gray-400">No hay series registradas</div>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-y border-gray-200 bg-gray-50/50">
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[50px]">#</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Serie</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Tipo de Documento</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Último Correlativo</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Predeterminada</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Estado</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[120px]">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {series.map((serie, idx) => (
                  <tr key={serie.id} className="border-b border-gray-100 hover:bg-gray-50/50">
                    <td className="py-3 px-3 text-gray-400">{idx + 1}</td>
                    <td className="py-3 px-3 font-mono font-semibold">{serie.serie}</td>
                    <td className="py-3 px-3">{TIPO_DOC_LABELS[serie.tipoDocumento] || serie.tipoDocumento}</td>
                    <td className="py-3 px-3 text-center font-mono">{serie.numeroActual || 0}</td>
                    <td className="py-3 px-3 text-center">
                      {serie.esPredeterminada ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700 border border-green-300">
                          Sí
                        </span>
                      ) : (
                        <span className="text-gray-400">No</span>
                      )}
                    </td>
                    <td className="py-3 px-3 text-center">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${
                        serie.estaActivo
                          ? 'bg-green-100 text-green-700 border-green-300'
                          : 'bg-red-100 text-red-700 border-red-300'
                      }`}>
                        {serie.estaActivo ? 'Activa' : 'Inactiva'}
                      </span>
                    </td>
                    <td className="py-3 px-3">
                      <div className="flex justify-center gap-2">
                        <button title="Editar" onClick={() => handleEdit(serie)} className="text-gray-500 hover:text-gray-700">
                          <Edit size={16} />
                        </button>
                        <button title="Eliminar" onClick={() => handleDelete(serie.id)} className="text-red-500 hover:text-red-700">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ─── Modal Crear/Editar ─── */}
      {showForm && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => resetForm()}>
          <div className="bg-white rounded-xl max-w-md w-full p-6" onClick={(e) => e.stopPropagation()}>
            <h2 className="text-lg font-bold text-gray-900 mb-4">
              {editingSerie ? 'Editar Serie' : 'Nueva Serie'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Tipo de Documento <span className="text-red-500">*</span>
                </label>
                <select
                  value={formData.tipoDocumento}
                  onChange={(e) => {
                    const newTipo = e.target.value;
                    const rule = SERIE_PREFIX_RULES[newTipo];
                    // Pre-fill first prefix chars only if the field is empty or starts with old prefix
                    const autoPrefix = rule?.prefixLen === 2 && !formData.serie ? rule.prefixes[0] : '';
                    setFormData({ ...formData, tipoDocumento: newTipo, serie: autoPrefix });
                    setSerieError(null);
                  }}
                  disabled={!!editingSerie}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 bg-white"
                >
                  <option value="boleta">Boleta</option>
                  <option value="factura">Factura</option>
                  {tienePse && <option value="nota_credito">Nota de Crédito</option>}
                  {tienePse && <option value="nota_debito">Nota de Débito</option>}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Código de Serie <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={formData.serie}
                  onChange={(e) => {
                    const val = e.target.value.toUpperCase();
                    setFormData({ ...formData, serie: val });
                    setSerieError(null);
                  }}
                  placeholder={SERIE_PREFIX_RULES[formData.tipoDocumento]?.hint || 'B001, F001...'}
                  maxLength={10}
                  className={`w-full border rounded-lg px-3 py-2 text-sm font-mono focus:outline-none focus:ring-2 ${
                    serieError ? 'border-red-400 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500'
                  }`}
                  required
                />
                {serieError && (
                  <p className="text-xs text-red-500 mt-1">{serieError}</p>
                )}
                {!serieError && (
                  <p className="text-xs text-gray-400 mt-1">
                    Prefijo requerido: {SERIE_PREFIX_RULES[formData.tipoDocumento]?.prefixes.join(' o ')} — Ej: {SERIE_PREFIX_RULES[formData.tipoDocumento]?.hint}
                  </p>
                )}
              </div>
              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  id="esPredeterminada"
                  checked={formData.esPredeterminada}
                  onChange={(e) => setFormData({ ...formData, esPredeterminada: e.target.checked })}
                  className="rounded border-gray-300"
                />
                <label htmlFor="esPredeterminada" className="text-sm text-gray-700">
                  Serie predeterminada para este tipo de documento
                </label>
              </div>
              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={resetForm} className="px-4 py-2 text-sm bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300">
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={crearSerie.isPending || actualizarSerie.isPending}
                  className="px-4 py-2 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
                >
                  {(crearSerie.isPending || actualizarSerie.isPending) ? 'Guardando...' : 'Guardar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
