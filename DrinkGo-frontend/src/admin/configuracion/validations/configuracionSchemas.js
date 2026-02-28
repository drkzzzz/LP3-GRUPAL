/**
 * configuracionSchemas.js
 * ───────────────────────
 * Esquemas Zod para todas las entidades del módulo Configuración.
 */
import { z } from 'zod';

/* ================================================================
 *  NEGOCIO
 * ================================================================ */
export const negocioSchema = z.object({
  razonSocial: z
    .string()
    .min(3, 'Razón social debe tener al menos 3 caracteres')
    .max(200, 'Máximo 200 caracteres'),
  nombreComercial: z
    .string()
    .max(200, 'Máximo 200 caracteres')
    .optional()
    .or(z.literal('')),
  ruc: z
    .string()
    .length(11, 'RUC debe tener 11 dígitos')
    .regex(/^\d+$/, 'Solo números'),
  tipoDocumentoFiscal: z.enum(['RUC', 'DNI', 'CE', 'OTRO'], {
    errorMap: () => ({ message: 'Seleccione un tipo de documento' }),
  }),
  representanteLegal: z
    .string()
    .max(200, 'Máximo 200 caracteres')
    .optional()
    .or(z.literal('')),
  documentoRepresentante: z
    .string()
    .max(20, 'Máximo 20 caracteres')
    .optional()
    .or(z.literal('')),
  tipoNegocio: z
    .string()
    .max(100, 'Máximo 100 caracteres')
    .optional()
    .or(z.literal('')),
  email: z.string().email('Email inválido'),
  telefono: z
    .string()
    .regex(/^9\d{8}$/, 'Teléfono inválido (formato: 9XXXXXXXX)')
    .optional()
    .or(z.literal('')),
  direccion: z
    .string()
    .max(300, 'Máximo 300 caracteres')
    .optional()
    .or(z.literal('')),
  ciudad: z
    .string()
    .max(100, 'Máximo 100 caracteres')
    .optional()
    .or(z.literal('')),
  departamento: z
    .string()
    .max(100, 'Máximo 100 caracteres')
    .optional()
    .or(z.literal('')),
  pais: z.string().max(3).default('PE'),
  codigoPostal: z
    .string()
    .max(20, 'Máximo 20 caracteres')
    .optional()
    .or(z.literal('')),
  urlLogo: z
    .string()
    .url('URL inválida')
    .optional()
    .or(z.literal('')),
  aplicaIgv: z.boolean().default(true),
  porcentajeIgv: z
    .number({ invalid_type_error: 'Ingrese un número válido' })
    .min(0, 'Mínimo 0%')
    .max(100, 'Máximo 100%')
    .default(18),
});

/* ================================================================
 *  SEDE
 * ================================================================ */
export const sedeSchema = z.object({
  codigo: z
    .string()
    .min(2, 'Código debe tener al menos 2 caracteres')
    .max(20, 'Máximo 20 caracteres')
    .regex(/^[A-Z0-9\-]+$/, 'Solo letras mayúsculas, números y guiones'),
  nombre: z
    .string()
    .min(3, 'Nombre debe tener al menos 3 caracteres')
    .max(150, 'Máximo 150 caracteres'),
  direccion: z
    .string()
    .min(5, 'Dirección debe tener al menos 5 caracteres')
    .max(300, 'Máximo 300 caracteres'),
  ciudad: z.string().max(100).optional().or(z.literal('')),
  departamento: z.string().max(100).optional().or(z.literal('')),
  pais: z.string().max(3).default('PE'),
  codigoPostal: z.string().max(20).optional().or(z.literal('')),
  telefono: z
    .string()
    .regex(/^9\d{8}$/, 'Teléfono inválido (formato: 9XXXXXXXX)')
    .optional()
    .or(z.literal('')),
  esPrincipal: z.boolean().default(false),
  deliveryHabilitado: z.boolean().default(false),
  recojoHabilitado: z.boolean().default(false),
});

/* ================================================================
 *  ALMACÉN
 * ================================================================ */
export const almacenSchema = z.object({
  codigo: z
    .string()
    .min(2, 'Código debe tener al menos 2 caracteres')
    .max(20, 'Máximo 20 caracteres')
    .regex(/^[A-Z0-9\-]+$/, 'Solo letras mayúsculas, números y guiones'),
  nombre: z
    .string()
    .min(3, 'Nombre debe tener al menos 3 caracteres')
    .max(150, 'Máximo 150 caracteres'),
  descripcion: z
    .string()
    .max(300, 'Máximo 300 caracteres')
    .optional()
    .or(z.literal('')),
  esPredeterminado: z.boolean().default(false),
});

/* ================================================================
 *  MÉTODO DE PAGO
 * ================================================================ */
export const metodoPagoSchema = z.object({
  nombre: z
    .string()
    .min(2, 'Nombre debe tener al menos 2 caracteres')
    .max(100, 'Máximo 100 caracteres'),
  codigo: z
    .string()
    .min(2, 'Código debe tener al menos 2 caracteres')
    .max(50, 'Máximo 50 caracteres')
    .regex(/^[a-z0-9_]+$/, 'Solo minúsculas, números y guión bajo'),
  tipo: z.enum(
    ['efectivo', 'tarjeta_credito', 'tarjeta_debito', 'transferencia_bancaria', 'billetera_digital', 'yape', 'plin', 'qr', 'otro'],
    { errorMap: () => ({ message: 'Seleccione un tipo de método de pago' }) },
  ),
  disponiblePos: z.boolean().default(true),
  disponibleTiendaOnline: z.boolean().default(false),
  orden: z
    .number({ invalid_type_error: 'Ingrese un número' })
    .int('Debe ser un número entero')
    .min(0, 'Mínimo 0')
    .default(0),
});

/* ================================================================
 *  ZONA DE DELIVERY
 * ================================================================ */
export const zonaDeliverySchema = z.object({
  nombre: z
    .string()
    .min(3, 'Nombre debe tener al menos 3 caracteres')
    .max(100, 'Máximo 100 caracteres'),
  descripcion: z
    .string()
    .max(300, 'Máximo 300 caracteres')
    .optional()
    .or(z.literal('')),
  tarifaDelivery: z
    .number({ invalid_type_error: 'Ingrese un monto válido' })
    .min(0, 'Tarifa debe ser mayor o igual a 0')
    .default(0),
  montoMinimoPedido: z
    .number({ invalid_type_error: 'Ingrese un monto válido' })
    .min(0, 'Monto mínimo debe ser mayor o igual a 0')
    .optional()
    .nullable(),
});

/* ================================================================
 *  MESA
 * ================================================================ */
export const mesaSchema = z.object({
  nombre: z
    .string()
    .min(1, 'Nombre es requerido')
    .max(100, 'Máximo 100 caracteres'),
  capacidad: z
    .number({ invalid_type_error: 'Ingrese un número' })
    .int('Debe ser un número entero')
    .min(1, 'Mínimo 1 persona')
    .max(50, 'Máximo 50 personas')
    .default(4),
  estado: z
    .enum(['disponible', 'ocupada', 'reservada', 'mantenimiento'], {
      errorMap: () => ({ message: 'Seleccione un estado válido' }),
    })
    .default('disponible'),
});

/* ================================================================
 *  CAJA REGISTRADORA
 * ================================================================ */
export const cajaRegistradoraSchema = z.object({
  codigo: z
    .string()
    .min(2, 'Código debe tener al menos 2 caracteres')
    .max(20, 'Máximo 20 caracteres')
    .regex(/^[A-Z0-9\-]+$/, 'Solo letras mayúsculas, números y guiones'),
  nombreCaja: z
    .string()
    .min(3, 'Nombre debe tener al menos 3 caracteres')
    .max(100, 'Máximo 100 caracteres'),
  montoAperturaDefecto: z
    .number({ invalid_type_error: 'Ingrese un monto válido' })
    .min(0, 'Monto debe ser mayor o igual a 0')
    .default(0),
});

/* ================================================================
 *  CONFIGURACIÓN TIENDA ONLINE
 * ================================================================ */
export const tiendaOnlineSchema = z.object({
  estaHabilitado: z.boolean().default(false),
  nombreTienda: z
    .string()
    .max(200, 'Máximo 200 caracteres')
    .optional()
    .or(z.literal('')),
  slugTienda: z
    .string()
    .max(100, 'Máximo 100 caracteres')
    .regex(/^[a-z0-9-]+$/, 'Solo letras minúsculas, números y guiones')
    .optional()
    .or(z.literal('')),
  dominioPersonalizado: z
    .string()
    .max(200, 'Máximo 200 caracteres')
    .optional()
    .or(z.literal('')),
});
