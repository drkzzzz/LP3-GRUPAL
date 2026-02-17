package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.CajaRegistradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para cajas registradoras (RF-VEN-001).
 * Bloque 8 - Ventas, POS y Cajas.
 */
@Repository
public interface CajaRegistradoraRepository extends JpaRepository<CajaRegistradora, Long> {

    /** Buscar caja por ID y negocio (Seguridad multi-tenant) */
    Optional<CajaRegistradora> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar todas las cajas de un negocio */
    List<CajaRegistradora> findByNegocioId(Long negocioId);

    /** Listar cajas de una sede específica */
    List<CajaRegistradora> findByNegocioIdAndSedeId(Long negocioId, Long sedeId);

    /** Listar cajas activas de un negocio */
    List<CajaRegistradora> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    /** Listar cajas activas de una sede */
    List<CajaRegistradora> findByNegocioIdAndSedeIdAndEstaActivo(Long negocioId, Long sedeId, Boolean estaActivo);

    /** Buscar caja por código en un negocio */
    Optional<CajaRegistradora> findByNegocioIdAndCodigoCaja(Long negocioId, String codigoCaja);
}
