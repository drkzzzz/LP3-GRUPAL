/**
 * inventarioSchemas.js
 * ────────────────────
 * Esquemas de validación Zod para los formularios del módulo Inventario.
 * Cada esquema refleja los campos requeridos por las entidades del backend.
 */
import { z } from 'zod';

/* ================================================================
 *  ALMACÉN
 * ================================================================ */
export const almacenSchema = z.object({
  codigo: z
    .string()
    .min(1, 'El código es obligatorio')
    .max(20, 'Máximo 20 caracteres'),
  nombre: z
    .string()
    .min(1, 'El nombre es obligatorio')
    .max(150, 'Máximo 150 caracteres'),
  descripcion: z
    .string()
    .max(300, 'Máximo 300 caracteres')
    .optional()
    .default(''),
  sedeId: z
    .string()
    .min(1, 'La sede es obligatoria'),
  esPredeterminado: z
    .boolean()
    .optional()
    .default(false),
});

/* ================================================================
 *  ENTRADA DE LOTE (Recepción de mercancía)
 * ================================================================ */
export const loteEntradaSchema = z.object({
  productoId: z
    .string()
    .min(1, 'El producto es obligatorio'),
  almacenId: z
    .string()
    .min(1, 'El almacén es obligatorio'),
  numeroLote: z
    .string()
    .min(1, 'El número de lote es obligatorio'),
  cantidadInicial: z
    .string()
    .min(1, 'La cantidad es obligatoria')
    .refine((v) => Number(v) > 0, 'La cantidad debe ser mayor a 0'),
  costoUnitario: z
    .string()
    .min(1, 'El costo unitario es obligatorio')
    .refine((v) => Number(v) >= 0, 'El costo debe ser mayor o igual a 0'),
  fechaVencimiento: z
    .string()
    .optional()
    .default(''),
  observaciones: z
    .string()
    .optional()
    .default(''),
});

/* ================================================================
 *  AJUSTE DE INVENTARIO (Manual)
 * ================================================================ */
export const ajusteInventarioSchema = z.object({
  productoId: z
    .string()
    .min(1, 'El producto es obligatorio'),
  almacenId: z
    .string()
    .min(1, 'El almacén es obligatorio'),
  tipoAjuste: z
    .string()
    .min(1, 'El tipo de ajuste es obligatorio'),
  cantidad: z
    .string()
    .min(1, 'La cantidad es obligatoria')
    .refine((v) => Number(v) > 0, 'La cantidad debe ser mayor a 0'),
  motivoMovimiento: z
    .string()
    .min(1, 'El motivo es obligatorio'),
  referenciaDocumento: z
    .string()
    .optional()
    .default(''),
});

/* ================================================================
 *  TRANSFERENCIA ENTRE ALMACENES
 * ================================================================ */
export const transferenciaSchema = z.object({
  productoId: z
    .string()
    .min(1, 'El producto es obligatorio'),
  almacenOrigenId: z
    .string()
    .min(1, 'El almacén de origen es obligatorio'),
  almacenDestinoId: z
    .string()
    .min(1, 'El almacén de destino es obligatorio'),
  cantidad: z
    .string()
    .min(1, 'La cantidad es obligatoria')
    .refine((v) => Number(v) > 0, 'La cantidad debe ser mayor a 0'),
  motivoMovimiento: z
    .string()
    .optional()
    .default(''),
}).refine(
  (data) => data.almacenOrigenId !== data.almacenDestinoId,
  { message: 'Los almacenes de origen y destino deben ser diferentes', path: ['almacenDestinoId'] },
);
