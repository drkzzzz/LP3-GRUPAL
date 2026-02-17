package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidad AsignacionEtiquetaProducto - Relación N:M entre productos y etiquetas
 * BLOQUE 3: Catálogo de Productos
 */
@Entity
@Table(name = "asignacion_etiquetas_producto")
@IdClass(AsignacionEtiquetaProducto.AsignacionEtiquetaProductoId.class)
public class AsignacionEtiquetaProducto {

    @Id
    @Column(name = "producto_id")
    private Long productoId;

    @Id
    @Column(name = "etiqueta_id")
    private Long etiquetaId;

    // Getters y Setters

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getEtiquetaId() {
        return etiquetaId;
    }

    public void setEtiquetaId(Long etiquetaId) {
        this.etiquetaId = etiquetaId;
    }

    // Clase ID compuesta
    public static class AsignacionEtiquetaProductoId implements Serializable {
        private Long productoId;
        private Long etiquetaId;

        public AsignacionEtiquetaProductoId() {
        }

        public AsignacionEtiquetaProductoId(Long productoId, Long etiquetaId) {
            this.productoId = productoId;
            this.etiquetaId = etiquetaId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof AsignacionEtiquetaProductoId))
                return false;
            AsignacionEtiquetaProductoId that = (AsignacionEtiquetaProductoId) o;
            return Objects.equals(productoId, that.productoId) &&
                    Objects.equals(etiquetaId, that.etiquetaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productoId, etiquetaId);
        }

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Long getEtiquetaId() {
            return etiquetaId;
        }

        public void setEtiquetaId(Long etiquetaId) {
            this.etiquetaId = etiquetaId;
        }
    }
}
