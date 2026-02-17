package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SesionCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para sesiones de caja (RF-VEN-001..002).
 * Bloque 8 - Ventas, POS y Cajas.
 */
@Repository
public interface SesionCajaRepository extends JpaRepository<SesionCaja, Long> {

    /** Buscar sesión por ID y negocio (Seguridad multi-tenant) */
    Optional<SesionCaja> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar todas las sesiones de un negocio */
    List<SesionCaja> findByNegocioId(Long negocioId);

    /** Listar sesiones de una caja específica */
    List<SesionCaja> findByNegocioIdAndCajaId(Long negocioId, Long cajaId);

    /** Buscar sesión abierta en una caja (solo puede haber una) */
    Optional<SesionCaja> findByNegocioIdAndCajaIdAndEstaAbierta(Long negocioId, Long cajaId, Boolean estaAbierta);

    /** Listar sesiones abiertas de un negocio */
    List<SesionCaja> findByNegocioIdAndEstaAbierta(Long negocioId, Boolean estaAbierta);

    /** Listar sesiones por usuario que abrió */
    List<SesionCaja> findByNegocioIdAndUsuarioAperturaId(Long negocioId, Long usuarioAperturaId);

    /** Contar sesiones abiertas en una caja */
    @Query("SELECT COUNT(s) FROM SesionCaja s WHERE s.negocioId = :negocioId AND s.cajaId = :cajaId AND s.estaAbierta = true")
    long countSesionesAbiertasPorCaja(@Param("negocioId") Long negocioId, @Param("cajaId") Long cajaId);
}
