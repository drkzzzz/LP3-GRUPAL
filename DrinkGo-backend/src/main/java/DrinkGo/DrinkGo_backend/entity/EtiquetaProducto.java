package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;

/**
 * Entidad EtiquetaProducto - Tags para productos
 * BLOQUE 3: Catálogo de Productos
 */
@Entity
@Table(name = "etiquetas_producto")
public class EtiquetaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "color", length = 7)
    private String color;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
