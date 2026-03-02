package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.HistorialPse;

@Repository
public interface HistorialPseRepository extends JpaRepository<HistorialPse, Long> {

    List<HistorialPse> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    List<HistorialPse> findByDocumentoIdOrderByCreadoEnDesc(Long documentoId);
}
