package DrinkGo.DrinkGo_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Negocios;

public interface NegociosRepository extends JpaRepository<Negocios, Long> {
    Optional<Negocios> findByRuc(String ruc);

    Optional<Negocios> findByEmail(String email);
}
