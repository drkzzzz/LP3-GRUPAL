package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.Marca;
import DrinkGo.DrinkGo_backend.repository.MarcaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @Transactional(readOnly = true)
    public List<Marca> findByNegocio(Long negocioId) {
        return marcaRepository.findByNegocioIdOrderByNombreAsc(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Marca> findActivas(Long negocioId) {
        return marcaRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<Marca> findByPaisOrigen(Long negocioId, String paisOrigen) {
        return marcaRepository.findByNegocioIdAndPaisOrigen(negocioId, paisOrigen);
    }

    @Transactional(readOnly = true)
    public Optional<Marca> findById(Long id) {
        return marcaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Marca> findBySlug(Long negocioId, String slug) {
        return marcaRepository.findByNegocioIdAndSlug(negocioId, slug);
    }

    @Transactional
    public Marca crear(Marca marca) {
        return marcaRepository.save(marca);
    }

    @Transactional
    public Marca actualizar(Marca marca) {
        return marcaRepository.save(marca);
    }

    @Transactional
    public void eliminar(Long id) {
        marcaRepository.deleteById(id);
    }
}
