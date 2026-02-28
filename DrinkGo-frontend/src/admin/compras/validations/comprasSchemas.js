import { z } from 'zod';

/* ================================================================
 *  PROVEEDOR
 * ================================================================ */
export const proveedorSchema = z
  .object({
    razonSocial: z
      .string()
      .min(1, 'La razón social es obligatoria')
      .max(200, 'Máximo 200 caracteres'),
    nombreComercial: z.string().max(200, 'Máximo 200 caracteres').optional().default(''),
    tipoDocumento: z.string().min(1, 'El tipo de documento es obligatorio'),
    numeroDocumento: z
      .string()
      .min(1, 'El número de documento es obligatorio')
      .max(20, 'Máximo 20 caracteres'),
    direccion: z.string().max(300, 'Máximo 300 caracteres').optional().default(''),
    telefono: z
      .string()
      .max(30, 'Máximo 30 caracteres')
      .optional()
      .default('')
      .refine((v) => !v || /^\d{9}$/.test(v), 'El teléfono debe tener exactamente 9 dígitos'),
    email: z
      .string()
      .max(150, 'Máximo 150 caracteres')
      .optional()
      .default('')
      .refine((v) => !v || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v), 'Email no válido'),
    contactoPrincipal: z.string().max(200, 'Máximo 200 caracteres').optional().default(''),
    telefonoContacto: z.string().max(30, 'Máximo 30 caracteres').optional().default(''),
    emailContacto: z.string().max(150, 'Máximo 150 caracteres').optional().default(''),
    observaciones: z.string().optional().default(''),
    estaActivo: z.boolean().optional().default(true),
  })
  .superRefine((data, ctx) => {
    if (data.tipoDocumento === 'DNI' && data.numeroDocumento) {
      if (!/^\d{8}$/.test(data.numeroDocumento)) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'El DNI debe tener exactamente 8 dígitos',
          path: ['numeroDocumento'],
        });
      }
    }
    if (data.tipoDocumento === 'RUC' && data.numeroDocumento) {
      if (!/^\d{11}$/.test(data.numeroDocumento)) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'El RUC debe tener exactamente 11 dígitos',
          path: ['numeroDocumento'],
        });
      }
    }
  });

/* ================================================================
 *  PRODUCTO POR PROVEEDOR
 * ================================================================ */
export const productoProveedorSchema = z.object({
  proveedorId: z.string().min(1, 'El proveedor es obligatorio'),
  productoId: z.string().min(1, 'El producto es obligatorio'),
  skuProveedor: z.string().max(50, 'Máximo 50 caracteres').optional().default(''),
  precioCompra: z
    .string()
    .optional()
    .default('0')
    .refine((v) => !v || Number(v) >= 0, 'El precio debe ser mayor o igual a 0'),
  tiempoEntregaDias: z
    .string()
    .optional()
    .default('')
    .refine((v) => !v || Number(v) >= 0, 'Debe ser un número positivo'),
  esPredeterminado: z.boolean().optional().default(false),
});

/* ================================================================
 *  ORDEN DE COMPRA
 * ================================================================ */
export const ordenCompraSchema = z.object({
  proveedorId: z.string().min(1, 'El proveedor es obligatorio'),
  sedeId: z.string().min(1, 'La sede es obligatoria'),
  almacenId: z.string().min(1, 'El almacén es obligatorio'),
  notas: z.string().optional().default(''),
  items: z
    .array(
      z.object({
        productoId: z.string().min(1, 'El producto es obligatorio'),
        cantidadSolicitada: z
          .string()
          .min(1, 'La cantidad es obligatoria')
          .refine((v) => Number(v) > 0, 'La cantidad debe ser mayor a 0'),
        precioUnitario: z
          .string()
          .min(1, 'El precio es obligatorio')
          .refine((v) => Number(v) > 0, 'El precio debe ser mayor a 0'),
      }),
    )
    .min(1, 'Debe agregar al menos un producto a la orden'),
});

/* ================================================================
 *  DETALLE DE ORDEN DE COMPRA (un ítem)
 * ================================================================ */
export const detalleOrdenCompraSchema = z.object({
  productoId: z.string().min(1, 'El producto es obligatorio'),
  cantidadSolicitada: z
    .string()
    .min(1, 'La cantidad es obligatoria')
    .refine((v) => Number(v) > 0, 'La cantidad debe ser mayor a 0'),
  precioUnitario: z
    .string()
    .min(1, 'El precio unitario es obligatorio')
    .refine((v) => Number(v) > 0, 'El precio debe ser mayor a 0'),
});

/* ================================================================
 *  RECEPCIÓN DE MERCANCÍA (actualizar cantidades recibidas)
 * ================================================================ */
export const recepcionItemSchema = z.object({
  cantidadRecibida: z
    .string()
    .min(1, 'La cantidad recibida es obligatoria')
    .refine((v) => Number(v) >= 0, 'La cantidad debe ser mayor o igual a 0'),
});
