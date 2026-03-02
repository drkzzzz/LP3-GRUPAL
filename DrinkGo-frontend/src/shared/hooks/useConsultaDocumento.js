/**
 * useConsultaDocumento.js
 * ───────────────────────
 * Hook reutilizable para consultar RUC (SUNAT) y DNI (RENIEC)
 * usando TanStack Query. Provee estados de loading, error y data,
 * más funciones manuales para disparar la consulta.
 */
import { useState, useCallback } from 'react';
import { consultarRuc, consultarDni } from '@/shared/services/consultaDocumentoService';

/**
 * Hook para consultar RUC.
 * Retorna { consultarRuc, data, isLoading, error, reset }
 */
export const useConsultarRuc = () => {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const buscar = useCallback(async (numero) => {
    if (!numero || !/^\d{11}$/.test(numero)) {
      setError('El RUC debe tener 11 dígitos');
      return null;
    }
    setIsLoading(true);
    setError(null);
    try {
      const resultado = await consultarRuc(numero);
      if (resultado.error) {
        setError(resultado.error);
        setData(null);
        return null;
      }
      setData(resultado);
      return resultado;
    } catch (err) {
      const message = err.response?.data?.error || 'Error al consultar RUC';
      setError(message);
      setData(null);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setIsLoading(false);
  }, []);

  return { buscar, data, isLoading, error, reset };
};

/**
 * Hook para consultar DNI.
 * Retorna { consultarDni, data, isLoading, error, reset }
 */
export const useConsultarDni = () => {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const buscar = useCallback(async (numero) => {
    if (!numero || !/^\d{8}$/.test(numero)) {
      setError('El DNI debe tener 8 dígitos');
      return null;
    }
    setIsLoading(true);
    setError(null);
    try {
      const resultado = await consultarDni(numero);
      if (resultado.error) {
        setError(resultado.error);
        setData(null);
        return null;
      }
      setData(resultado);
      return resultado;
    } catch (err) {
      const message = err.response?.data?.error || 'Error al consultar DNI';
      setError(message);
      setData(null);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setIsLoading(false);
  }, []);

  return { buscar, data, isLoading, error, reset };
};
