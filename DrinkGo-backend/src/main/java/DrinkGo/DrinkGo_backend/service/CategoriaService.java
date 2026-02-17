package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.Categoria;
import DrinkGo.DrinkGo_backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> findByNegocio(Long negocioId) {
        return categoriaRepository.findByNegocioIdOrderByOrdenAsc(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Categoria> findActivas(Long negocioId) {
        return categoriaRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<Categoria> findPrincipales(Long negocioId) {
        return categoriaRepository.findByNegocioIdAndPadreIdIsNull(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Categoria> findSubcategorias(Long negocioId, Long padreId) {
        return categoriaRepository.findByNegocioIdAndPadreId(negocioId, padreId);
    }

    @Transactional(readOnly = true)
    public List<Categoria> findVisiblesTiendaOnline(Long negocioId) {
        return categoriaRepository.findByNegocioIdAndVisibleTiendaOnline(negocioId, true);
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> findBySlug(Long negocioId, String slug) {
        return categoriaRepository.findByNegocioIdAndSlug(negocioId, slug);
    }

    @Transactional
    public Categoria crear(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
}
