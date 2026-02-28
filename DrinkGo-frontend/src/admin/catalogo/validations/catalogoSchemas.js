/**
 * catalogoSchemas.js
 * ──────────────────
 * Esquemas de validación Zod para todos los formularios del módulo Catálogo.
 * Cada esquema refleja los campos obligatorios de la entidad del backend.
 */
import { z } from 'zod';

/* ================================================================
 *  PRODUCTO
 * ================================================================ */
export const productoSchema = z.object({
  nombre: z
    .string()
    .min(1, 'El nombre es obligatorio')
    .max(250, 'Máximo 250 caracteres'),
  sku: z
    .string()
    .min(1, 'El SKU es obligatorio'),
  slug: z
    .string()
    .min(1, 'El slug es obligatorio')
    .max(250, 'Máximo 250 caracteres'),
  descripcion: z.string().optional().default(''),
  urlImagen: z.string().optional().default(''),
  categoriaId: z
    .string()
    .optional()
    .default(''),
  marcaId: z
    .string()
    .optional()
    .default(''),
  unidadMedidaId: z
    .string()
    .optional()
    .default(''),
  gradoAlcoholico: z
    .string()
    .optional()
    .default(''),
  tasaImpuesto: z.string().optional().default('18'),
  impuestoIncluido: z.boolean().optional().default(true),
  permiteDescuento: z.boolean().optional().default(true),
  estaActivo: z.boolean().optional().default(true),
});

/* ================================================================
 *  CATEGORÍA
 * ================================================================ */
export const categoriaSchema = z.object({
  nombre: z
    .string()
    .min(1, 'El nombre es obligatorio'),
  slug: z
    .string()
    .min(1, 'El slug es obligatorio'),
  descripcion: z.string().optional().default(''),
  esAlcoholica: z.boolean().optional().default(false),
  visibleTiendaOnline: z.boolean().optional().default(true),
  estaActivo: z.boolean().optional().default(true),
});

/* ================================================================
 *  MARCA
 * ================================================================ */
export const marcaSchema = z.object({
  nombre: z
    .string()
    .min(1, 'El nombre es obligatorio'),
  slug: z
    .string()
    .min(1, 'El slug es obligatorio'),
  paisOrigen: z.string().optional().default(''),
  urlLogo: z.string().optional().default(''),
  descripcion: z.string().optional().default(''),
  estaActivo: z.boolean().optional().default(true),
});

/* ================================================================
 *  UNIDAD DE MEDIDA
 * ================================================================ */
export const unidadMedidaSchema = z.object({
  codigo: z
    .string()
    .min(1, 'El código es obligatorio'),
  nombre: z
    .string()
    .min(1, 'El nombre es obligatorio'),
  abreviatura: z
    .string()
    .min(1, 'La abreviatura es obligatoria'),
  tipo: z
    .string()
    .min(1, 'El tipo es obligatorio'),
  estaActivo: z.boolean().optional().default(true),
});

/* ================================================================
 *  COMBO
 * ================================================================ */
export const comboSchema = z.object({
  nombre: z
    .string()
    .min(1, 'El nombre es obligatorio')
    .max(250, 'Máximo 250 caracteres'),
  slug: z
    .string()
    .min(1, 'El slug es obligatorio')
    .max(250, 'Máximo 250 caracteres'),
  descripcion: z.string().optional().default(''),
  urlImagen: z.string().optional().default(''),
  precioRegular: z
    .string()
    .min(1, 'El precio regular es obligatorio'),
  precioCombo: z
    .string()
    .min(1, 'El precio combo es obligatorio'),
  porcentajeDescuento: z.string().optional().default(''),
  fechaInicio: z.string().optional().default(''),
  fechaFin: z.string().optional().default(''),
  visiblePos: z.boolean().optional().default(true),
  visibleTiendaOnline: z.boolean().optional().default(false),
  esDestacado: z.boolean().optional().default(false),
  estaActivo: z.boolean().optional().default(true),
});

/* ================================================================
 *  PROMOCIÓN / DESCUENTO
 * ================================================================ */
export const promocionSchema = z.object({
  nombre: z
    .string()
    .min(1, 'El nombre es obligatorio')
    .max(200, 'Máximo 200 caracteres'),
  codigo: z.string().optional().default(''),
  tipoDescuento: z
    .string()
    .min(1, 'El tipo de descuento es obligatorio'),
  valorDescuento: z
    .string()
    .min(1, 'El valor del descuento es obligatorio'),
  montoMinimoCompra: z.string().optional().default(''),
  maxUsos: z.string().optional().default(''),
  aplicaA: z
    .string()
    .min(1, 'Debe seleccionar a quién aplica'),
  validoDesde: z
    .string()
    .min(1, 'La fecha de inicio es obligatoria'),
  validoHasta: z
    .string()
    .min(1, 'La fecha de fin es obligatoria'),
  estaActivo: z.boolean().optional().default(true),
});
