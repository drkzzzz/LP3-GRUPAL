package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PaginaTiendaOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaginaTiendaOnlineRepository extends JpaRepository<PaginaTiendaOnline, Long> {

    List<PaginaTiendaOnline> findByNegocioId(Long negocioId);

    List<PaginaTiendaOnline> findByNegocioIdAndEstaPublicadoTrue(Long negocioId);

    Optional<PaginaTiendaOnline> findByNegocioIdAndSlug(Long negocioId, String slug);

    Optional<PaginaTiendaOnline> findByIdAndNegocioId(Long id, Long negocioId);

    boolean existsByNegocioIdAndSlug(Long negocioId, String slug);

    @Query("SELECT p FROM PaginaTiendaOnline p WHERE p.negocioId = :negocioId ORDER BY p.orden ASC, p.titulo ASC")
    List<PaginaTiendaOnline> findByNegocioIdOrdenadas(@Param("negocioId") Long negocioId);

    @Query("SELECT p FROM PaginaTiendaOnline p WHERE p.negocioId = :negocioId AND p.estaPublicado = true ORDER BY p.orden ASC, p.titulo ASC")
    List<PaginaTiendaOnline> findByNegocioIdPublicadasOrdenadas(@Param("negocioId") Long negocioId);
}
