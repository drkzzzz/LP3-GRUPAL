package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ModuloSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para ModuloSistema
 */
@Repository
public interface ModuloSistemaRepository extends JpaRepository<ModuloSistema, Long> {

    Optional<ModuloSistema> findByCodigo(String codigo);

    List<ModuloSistema> findByModuloPadreId(Long moduloPadreId);

    List<ModuloSistema> findByModuloPadreIdIsNull();

    List<ModuloSistema> findByEstaActivoTrue();

    List<ModuloSistema> findByEstaActivoTrueOrderByOrdenAsc();

    boolean existsByCodigo(String codigo);
}
