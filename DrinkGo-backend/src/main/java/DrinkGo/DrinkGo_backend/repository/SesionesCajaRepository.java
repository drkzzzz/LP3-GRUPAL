package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.SesionesCaja;

public interface SesionesCajaRepository extends JpaRepository<SesionesCaja, Long> {

    Optional<SesionesCaja> findFirstByUsuarioIdAndEstadoSesion(
            Long usuarioId, SesionesCaja.EstadoSesion estadoSesion);

    List<SesionesCaja> findByCajaIdOrderByFechaAperturaDesc(Long cajaId);

    List<SesionesCaja> findByCajaNegocioIdOrderByFechaAperturaDesc(Long negocioId);

    Optional<SesionesCaja> findFirstByCajaIdAndEstadoSesion(
            Long cajaId, SesionesCaja.EstadoSesion estadoSesion);

    /**
     * Busca sesión abierta del usuario CON bloqueo pesimista para evitar
     * que dos requests concurrentes (doble-click) pasen ambos la validación.
     */
    @Query(value = "SELECT * FROM sesiones_caja WHERE esta_activo = 1 AND usuario_id = :usuarioId AND estado_sesion = :estado FOR UPDATE", nativeQuery = true)
    Optional<SesionesCaja> findByUsuarioIdAndEstadoForUpdate(
            @Param("usuarioId") Long usuarioId,
            @Param("estado") String estado);

    /**
     * Busca sesión abierta de la caja CON bloqueo pesimista.
     */
    @Query(value = "SELECT * FROM sesiones_caja WHERE esta_activo = 1 AND caja_id = :cajaId AND estado_sesion = :estado FOR UPDATE", nativeQuery = true)
    Optional<SesionesCaja> findByCajaIdAndEstadoForUpdate(
            @Param("cajaId") Long cajaId,
            @Param("estado") String estado);
}
