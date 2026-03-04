package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;
import DrinkGo.DrinkGo_backend.repository.SedesRepository;
import DrinkGo.DrinkGo_backend.repository.SuscripcionesRepository;
import DrinkGo.DrinkGo_backend.service.ISedesService;

@Service
public class SedesService implements ISedesService {
    @Autowired
    private SedesRepository repoSedes;

    @Autowired
    private SuscripcionesRepository repoSuscripciones;

    public List<Sedes> buscarTodos() {
        return repoSedes.findAll();
    }

    public List<Sedes> buscarPorNegocio(Long negocioId) {
        return repoSedes.findByNegocio_Id(negocioId);
    }

    @Transactional
    public void guardar(Sedes sedes) {
        Long negocioId = sedes.getNegocio().getId();

        // Validar límite de sedes según el plan
        Optional<Suscripciones> suscripcionOpt = repoSuscripciones
                .findFirstByNegocioIdAndEstado(negocioId, Suscripciones.EstadoSuscripcion.activa);

        if (suscripcionOpt.isPresent()) {
            Integer maxSedes = suscripcionOpt.get().getPlan().getMaxSedes();
            if (maxSedes != null) {
                long sedesActuales = repoSedes.countAllByNegocioId(negocioId);
                if (sedesActuales >= maxSedes) {
                    throw new RuntimeException(
                            "Límite de sedes alcanzado. Su plan permite máximo " + maxSedes + " sede(s).");
                }
            }
        }

        // Bloquear si ya existe otra sede principal
        if (Boolean.TRUE.equals(sedes.getEsPrincipal())) {
            long otherPrincipal = repoSedes.countOtherPrincipalByNegocioId(negocioId, 0L);
            if (otherPrincipal > 0) {
                throw new RuntimeException(
                        "Ya existe una sede principal. Primero desmarca la sede principal actual.");
            }
        }

        // Auto-generar código de sede
        long total = repoSedes.countAllByNegocioId(negocioId);
        String codigo = String.format("SEDE-%03d", total + 1);
        sedes.setCodigo(codigo);

        repoSedes.save(sedes);
    }

    @Transactional
    public void modificar(Sedes sedes) {
        // Si se intenta marcar como principal, verificar que no haya otra
        if (Boolean.TRUE.equals(sedes.getEsPrincipal()) && sedes.getNegocio() != null) {
            long otherPrincipal = repoSedes.countOtherPrincipalByNegocioId(
                    sedes.getNegocio().getId(), sedes.getId());
            if (otherPrincipal > 0) {
                throw new RuntimeException(
                        "Ya existe una sede principal. Primero desmarca la sede principal actual.");
            }
        }
        repoSedes.save(sedes);
    }

    public Optional<Sedes> buscarId(Long id) {
        return repoSedes.findById(id);
    }

    public void eliminar(Long id) {
        repoSedes.deleteById(id);
    }
}
