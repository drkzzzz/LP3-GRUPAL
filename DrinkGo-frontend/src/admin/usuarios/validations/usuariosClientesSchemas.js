/**
 * usuariosClientesSchemas.js
 * ──────────────────────────
 * Esquemas de validación Zod para Usuarios, Clientes y Roles.
 * Compatible con Zod v4 (sin .or(), sin errorMap en enum).
 */
import { z } from 'zod';

/* ================================================================
 *  USUARIO
 * ================================================================ */
export const usuarioSchema = z
  .object({
    nombres: z.string().min(2, 'Nombres muy cortos'),
    apellidos: z.string().min(2, 'Apellidos muy cortos'),
    email: z.string().email('Correo electrónico inválido'),
    tipoDocumento: z.string().min(1, 'Tipo de documento requerido'),
    numeroDocumento: z.string().min(1, 'Número de documento requerido'),
    telefono: z
      .string()
      .optional()
      .default('')
      .refine((v) => !v || /^9\d{8}$/.test(v), 'Teléfono inválido (formato: 9XXXXXXXX)'),
    password: z.string().optional().default(''),
    confirmPassword: z.string().optional().default(''),
    estaActivo: z.boolean().default(true),
  })
  .superRefine((data, ctx) => {
    // Validar número de documento según tipo
    const doc = (data.numeroDocumento || '').trim();
    if (data.tipoDocumento === 'DNI' && !/^\d{8}$/.test(doc)) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'DNI debe tener exactamente 8 dígitos numéricos', path: ['numeroDocumento'] });
    } else if (data.tipoDocumento === 'CE' && !/^[A-Z0-9]{9}$/.test(doc)) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'CE debe tener 9 caracteres alfanuméricos en mayúsculas', path: ['numeroDocumento'] });
    } else if (data.tipoDocumento === 'PASAPORTE' && doc.length < 6) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'Pasaporte muy corto (mínimo 6 caracteres)', path: ['numeroDocumento'] });
    }
    // Validar contraseña
    if (data.password && data.password.length > 0 && data.password.length < 6) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'Mínimo 6 caracteres', path: ['password'] });
    }
    if (data.password && data.password !== data.confirmPassword) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'Las contraseñas no coinciden', path: ['confirmPassword'] });
    }
  });

/* ================================================================
 *  CLIENTE
 * ================================================================ */
export const clienteSchema = z.object({
  tipoDocumento: z.string().min(1, 'Tipo de documento requerido'),
  numeroDocumento: z.string().min(6, 'Número muy corto').max(20, 'Número muy largo'),
  nombres: z.string().optional().default(''),
  apellidos: z.string().optional().default(''),
  razonSocial: z.string().optional().default(''),
  nombreComercial: z.string().optional().default(''),
  email: z
    .string()
    .optional()
    .default('')
    .refine((v) => !v || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v), 'Correo inválido'),
  telefono: z
    .string()
    .optional()
    .default('')
    .refine((v) => !v || /^9\d{8}$/.test(v), 'Teléfono inválido (9XXXXXXXX)'),
  fechaNacimiento: z.string().optional().default(''),
  direccion: z.string().optional().default(''),
});

/* ================================================================
 *  ROL
 * ================================================================ */
export const rolSchema = z.object({
  nombre: z.string().min(2, 'Nombre muy corto').max(50, 'Nombre muy largo'),
  descripcion: z.string().optional().default(''),
  estaActivo: z.boolean().default(true),
});
