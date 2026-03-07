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
        return repoSedes.findByNegocioId(negocioId);
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
    public void modificar(Sedes updatedSedes) {
        Sedes existing = repoSedes.findById(updatedSedes.getId())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con id: " + updatedSedes.getId()));

        // Validar principal solo si se está marcando como principal
        if (Boolean.TRUE.equals(updatedSedes.getEsPrincipal())) {
            long otherPrincipal = repoSedes.countOtherPrincipalByNegocioId(
                    existing.getNegocio().getId(), existing.getId());
            if (otherPrincipal > 0) {
                throw new RuntimeException(
                        "Ya existe una sede principal. Primero desmarca la sede principal actual.");
            }
        }

        // Actualizar solo los campos editables (no tocar codigo, negocio, etc.)
        existing.setNombre(updatedSedes.getNombre());
        existing.setDireccion(updatedSedes.getDireccion());
        existing.setCiudad(updatedSedes.getCiudad());
        existing.setDepartamento(updatedSedes.getDepartamento());
        existing.setPais(updatedSedes.getPais() != null ? updatedSedes.getPais() : "PE");
        existing.setCodigoPostal(updatedSedes.getCodigoPostal());
        existing.setTelefono(updatedSedes.getTelefono());
        existing.setEsPrincipal(updatedSedes.getEsPrincipal() != null ? updatedSedes.getEsPrincipal() : false);
        existing.setDeliveryHabilitado(
                updatedSedes.getDeliveryHabilitado() != null ? updatedSedes.getDeliveryHabilitado() : false);
        existing.setRecojoHabilitado(
                updatedSedes.getRecojoHabilitado() != null ? updatedSedes.getRecojoHabilitado() : false);
        existing.setTieneMesas(updatedSedes.getTieneMesas() != null ? updatedSedes.getTieneMesas() : false);

        repoSedes.save(existing);
    }

    public Optional<Sedes> buscarId(Long id) {
        return repoSedes.findById(id);
    }

    public void eliminar(Long id) {
        repoSedes.deleteById(id);
    }
}
