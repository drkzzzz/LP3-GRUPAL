package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import DrinkGo.DrinkGo_backend.entity.Devoluciones;

public interface DevolucionesRepository extends JpaRepository<Devoluciones, Long> {

    List<Devoluciones> findByNegocioIdOrderBySolicitadoEnDesc(Long negocioId);

    List<Devoluciones> findByVentaIdOrderBySolicitadoEnDesc(Long ventaId);

    @Query("SELECT d FROM Devoluciones d WHERE d.cliente.id = :clienteId ORDER BY d.solicitadoEn DESC")
    List<Devoluciones> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(d.numeroDevolucion, 5) AS int)), 0) FROM Devoluciones d WHERE d.negocio.id = :negocioId")
    Long findMaxNumeroByNegocioId(@Param("negocioId") Long negocioId);
}
