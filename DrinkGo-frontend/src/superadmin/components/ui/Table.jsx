import { clsx } from 'clsx';
import { ChevronLeft, ChevronRight } from 'lucide-react';

/**
 * Reusable Table with client-side pagination.
 *
 * @param {object} props
 * @param {Array} props.columns   – [{ key, title, dataIndex, width, align, render }]
 * @param {Array} props.data      – rows (already filtered if needed)
 * @param {boolean} props.loading
 * @param {object}  props.pagination – { current, pageSize, total, onChange, pageSizeOptions }
 * @param {string}  props.emptyText
 */
export const Table = ({
  columns = [],
  data = [],
  loading = false,
  pagination,
  emptyText = 'No se encontraron registros',
}) => {
  /* ---------- helpers ---------- */
  const resolveValue = (row, dataIndex) => {
    if (!dataIndex) return undefined;
    if (Array.isArray(dataIndex)) {
      return dataIndex.reduce((obj, key) => obj?.[key], row);
    }
    return row?.[dataIndex];
  };

  /* ---------- pagination calc ---------- */
  const page = pagination?.current ?? 1;
  const pageSize = pagination?.pageSize ?? 10;
  const total = pagination?.total ?? data.length;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  /* ---------- loading skeleton ---------- */
  if (loading) {
    return (
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  style={{ width: col.width }}
                >
                  {col.title}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {Array.from({ length: 5 }).map((_, i) => (
              <tr key={i}>
                {columns.map((col) => (
                  <td key={col.key} className="px-4 py-3">
                    <div className="h-4 bg-gray-200 rounded animate-pulse" />
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }

  return (
    <div>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={clsx(
                    'px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider',
                    col.align === 'center' ? 'text-center' : 'text-left',
                  )}
                  style={{ width: col.width }}
                >
                  {col.title}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {data.length === 0 ? (
              <tr>
                <td
                  colSpan={columns.length}
                  className="px-4 py-8 text-center text-sm text-gray-500"
                >
                  {emptyText}
                </td>
              </tr>
            ) : (
              data.map((row, rowIndex) => (
                <tr
                  key={row.id ?? rowIndex}
                  className="hover:bg-gray-50 transition-colors"
                >
                  {columns.map((col) => {
                    const value = resolveValue(row, col.dataIndex);
                    return (
                      <td
                        key={col.key}
                        className={clsx(
                          'px-4 py-3 text-sm text-gray-700 whitespace-nowrap',
                          col.align === 'center' && 'text-center',
                        )}
                        style={{ width: col.width }}
                      >
                        {col.render
                          ? col.render(value, row, rowIndex)
                          : (value ?? '—')}
                      </td>
                    );
                  })}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {pagination && (
        <div className="flex items-center justify-between px-4 py-3 border-t border-gray-200">
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <span>Filas por página:</span>
            <select
              value={pageSize}
              onChange={(e) =>
                pagination.onChange?.(1, Number(e.target.value))
              }
              className="border border-gray-300 rounded px-2 py-1 text-sm"
            >
              {(pagination.pageSizeOptions || [5, 10, 20, 50]).map((opt) => (
                <option key={opt} value={opt}>
                  {opt}
                </option>
              ))}
            </select>
            <span className="ml-2">
              Total: {total} registros
            </span>
          </div>

          <div className="flex items-center gap-1">
            <button
              onClick={() => pagination.onChange?.(page - 1, pageSize)}
              disabled={page <= 1}
              className="p-1 rounded hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              <ChevronLeft size={18} />
            </button>
            <span className="text-sm text-gray-700 px-2">
              Página {page} de {totalPages}
            </span>
            <button
              onClick={() => pagination.onChange?.(page + 1, pageSize)}
              disabled={page >= totalPages}
              className="p-1 rounded hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
