package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para negocios (tenants).
 * Utilizado para validación de seguridad multi-tenant.
 */
@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Long> {
}
