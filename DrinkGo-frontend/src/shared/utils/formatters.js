/**
 * Format currency for Peru (Soles)
 * @param {number} amount
 * @returns {string} "S/ 1,234.56"
 */
export const formatCurrency = (amount) => {
  if (amount == null) return 'S/ 0.00';
  return new Intl.NumberFormat('es-PE', {
    style: 'currency',
    currency: 'PEN',
  }).format(amount);
};

/**
 * Format date to DD/MM/YYYY
 * @param {string|Date} date
 * @returns {string}
 */
export const formatDate = (date) => {
  if (!date) return '—';
  return new Date(date).toLocaleDateString('es-PE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
};

/**
 * Format datetime to DD/MM/YYYY HH:mm
 * @param {string|Date} datetime
 * @returns {string}
 */
export const formatDateTime = (datetime) => {
  if (!datetime) return '—';
  return new Date(datetime).toLocaleDateString('es-PE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * Format phone number  987 654 321
 * @param {string} phone
 * @returns {string}
 */
export const formatPhone = (phone) => {
  if (!phone) return '—';
  return phone.replace(/(\d{3})(\d{3})(\d{3})/, '$1 $2 $3');
};

/**
 * Format RUC  20-12345678-9
 * @param {string} ruc
 * @returns {string}
 */
export const formatRUC = (ruc) => {
  if (!ruc) return '—';
  return ruc.replace(/(\d{2})(\d{8})(\d{1})/, '$1-$2-$3');
};

/**
 * Truncate text
 */
export const truncate = (text, maxLength = 50) => {
  if (!text) return '';
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
};
