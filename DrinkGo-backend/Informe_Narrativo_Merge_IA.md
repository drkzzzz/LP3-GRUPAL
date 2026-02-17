# Informe Narrativo -- Resolución de Conflictos de Merge con IA

**Fecha:** 2026-02-16 08:47:44

------------------------------------------------------------------------

# 1️⃣ Primer conflicto -- Archivo pom.xml

## 🔹 Mi primer "Ahora esto:"

El conflicto que envié fue el siguiente:

``` xml
?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

<modelVersion>4.0.0</modelVersion>
<parent>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-parent</artifactId>
<version>4.0.2</version>
<relativePath/> </parent>

<groupId>DrinkGo</groupId>
<artifactId>DrinkGo-backend</artifactId>
<version>0.0.1-SNAPSHOT</version>

<properties>
<java.version>17</java.version>
</properties>

<dependencies>

<dependency>
<groupId>org.springframework.boot</groupId>
<<<<<<< feature/Bl5
<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
=======
<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
>>>>>>> master

</dependencies>
```

Este conflicto mostraba claramente diferencias entre la rama
`feature/Bl5` y la rama `master`. La rama master incluía nuevas
dependencias como:

-   spring-boot-starter-actuator
-   jjwt (JWT para autenticación)
-   spring-security-test

Mientras que feature/Bl5 tenía una estructura más simple.

------------------------------------------------------------------------

## 🔹 Respuesta que devolvió Gemini

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.2</version>
        <relativePath/>
    </parent>

    <groupId>DrinkGo</groupId>
    <artifactId>DrinkGo-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.6</version>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.6</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.6</version>
            <scope>runtime</scope>
        </dependency>

    </dependencies>

</project>
```

------------------------------------------------------------------------

# 🔎 Análisis que realicé como IA

En este primer conflicto detecté lo siguiente:

1.  Gemini resolvió correctamente el conflicto sintáctico.
2.  Se conservaron dependencias de ambas ramas.
3.  No hubo pérdida de funcionalidad en este archivo específico.
4.  La solución fue estructuralmente correcta para compilación.

En este caso puntual, el merge fue seguro porque:

-   Solo se trataba de dependencias Maven.
-   No había lógica de negocio involucrada.
-   No afectaba el modelo de dominio.

------------------------------------------------------------------------

# ⚠️ Diferencia con conflictos posteriores

Mientras que este conflicto era estructural (POM), los conflictos
posteriores que analizamos eran de:

-   Servicios de Inventario
-   Lógica de vencimiento
-   Generación automática de movimientos
-   Descuento de stock
-   Soft delete y auditoría

Ahí fue donde el riesgo arquitectónico apareció.

------------------------------------------------------------------------

# 🧠 Conclusión del Primer Caso

Este primer "Ahora esto:" fue un conflicto técnico normal. La resolución
fue correcta y no implicó degradación del dominio.

Los problemas reales comenzaron cuando los conflictos involucraron
lógica de negocio.

------------------------------------------------------------------------

# 📌 Estructura para los siguientes conflictos

Para cada nuevo "Ahora esto:" el documento seguirá el siguiente formato:

1.  Código con conflicto
2.  Código devuelto por Gemini
3.  Análisis técnico del impacto
4.  Evaluación de riesgo empresarial

------------------------------------------------------------------------

# Informe Narrativo -- Resolución de Conflictos de Merge con IA

**Última actualización:** 2026-02-16 08:51:17

------------------------------------------------------------------------

# 2️⃣ Segundo conflicto -- DetalleTransferenciaRequest.java

## 🔹 Mi "Ahora esto:"

``` java
package DrinkGo.DrinkGo_backend.dto;

<<<<<<< feature/Bl5
import jakarta.validation.constraints.*;

public class DetalleTransferenciaRequest {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    private Long loteId;

    @NotNull(message = "La cantidad_solicitada es obligatoria")
    @Min(value = 1, message = "La cantidad_solicitada debe ser al menos 1")
    private Integer cantidadSolicitada;

    @Size(max = 300, message = "Las notas no pueden exceder 300 caracteres")
    private String notas;

=======

public class DetalleTransferenciaRequest {

    private Long productoId;
    private Long loteId;
    private Integer cantidadSolicitada;

>>>>>>> master
}
```

En este conflicto se observaban diferencias claras entre ramas:

### Rama feature/Bl5:

-   Uso de validaciones con Jakarta Validation.
-   Restricciones obligatorias (@NotNull).
-   Validación mínima (@Min).
-   Campo adicional `notas` con límite de tamaño.

### Rama master:

-   DTO simplificado.
-   Sin validaciones.
-   Sin campo `notas`.
-   Enfoque más básico.

------------------------------------------------------------------------

## 🔹 Respuesta que devolvió Gemini

``` java
package DrinkGo.DrinkGo_backend.dto;

import jakarta.validation.constraints.*;

public class DetalleTransferenciaRequest {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    private Long loteId;

    @NotNull(message = "La cantidad_solicitada es obligatoria")
    @Min(value = 1, message = "La cantidad_solicitada debe ser al menos 1")
    private Integer cantidadSolicitada;

    @Size(max = 300, message = "Las notas no pueden exceder 300 caracteres")
    private String notas;

    public DetalleTransferenciaRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
```

------------------------------------------------------------------------

# 🔎 Análisis que realicé

En este caso específico:

✔ Gemini conservó la versión más robusta (feature/Bl5).\
✔ No eliminó validaciones.\
✔ No degradó estructura del DTO.\
✔ Mantiene coherencia para APIs REST empresariales.

Este conflicto NO implicó pérdida de lógica de negocio crítica.

Sin embargo, hay un punto arquitectónico relevante:

Las validaciones en DTO son parte de la capa de entrada (boundary
validation). Eliminar esas validaciones (como en master) habría reducido
seguridad y consistencia.

Por lo tanto, en este caso:

-   La decisión de conservar la versión con validaciones fue correcta.
-   No hubo degradación del dominio.
-   Fue una resolución segura.

------------------------------------------------------------------------

# 📊 Evaluación de Riesgo

Nivel de riesgo: 🟢 Bajo

Motivo: Se trataba de un DTO de entrada. No afectaba reglas internas de
inventario ni movimientos automáticos.

------------------------------------------------------------------------

(Fin del segundo bloque documentado)


---

# Caso 3 — Conflicto en AlertaInventario (Entidad JPA)

## Mi "Ahora esto:" fue:

```java
package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;

<<<<<<< feature/Bl5
import java.time.LocalDateTime;

/**
 * Alertas de inventario automáticas (RF-INV-004..005).
 * Mapeo exacto de la tabla alertas_inventario de drinkgo_database.sql.
 * Genera alertas de stock bajo, próximo a vencer, vencido, etc.
 */
@Entity
@Table(name = "alertas_inventario")
=======
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_inventario")
@SQLDelete(sql = "UPDATE alertas_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
>>>>>>> master
public class AlertaInventario {
...
}
```

## Gemini me devolvió:

```java
package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

/**
 * Alertas de inventario automáticas (RF-INV-004..005).
 * Genera alertas de stock bajo, próximo a vencer, vencido, etc.
 */
@Entity
@Table(name = "alertas_inventario")
@SQLDelete(sql = "UPDATE alertas_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class AlertaInventario {

    public enum TipoAlerta {
        stock_bajo, sobrestock, proximo_vencer, vencido, punto_reorden
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }
}
```

## Análisis técnico

Aquí ocurrió algo mucho más delicado que en los casos anteriores.

En la rama feature/Bl5, la entidad tenía:

- Campos productoId y almacenId explícitos.
- Relaciones @ManyToOne con Producto y Almacen.
- Getters híbridos que devolvían ID desde la relación.
- Control manual del campo creadoEn.

En la rama master, en cambio:

- No existían relaciones @ManyToOne.
- Se manejaban solo IDs simples.
- Se implementaba Soft Delete con @SQLDelete y @SQLRestriction.
- Se agregaba el campo eliminadoEn.

Gemini generó una tercera versión híbrida que:

✔ Conserva las relaciones @ManyToOne.
✔ Mantiene Soft Delete.
✔ Agrega @PrePersist para creadoEn.
✘ Elimina los campos productoId y almacenId explícitos.
✘ Cambia el modelo de acceso a IDs.

Impacto arquitectónico:

- Si otros servicios usaban getProductoId() directamente, ahora eso desaparece.
- Si existían queries por productoId en el repository, podrían romperse.
- Se modifica el contrato implícito del modelo.

Nivel de riesgo: ALTO.

Este no fue un simple fix de compilación.
Fue una modificación estructural del modelo de dominio.

Aquí ya no estamos hablando de “resolver conflicto”, sino de redefinir cómo la entidad representa sus relaciones en JPA.


---

# Caso 4 — Conflicto en Almacen (Entidad estructural crítica)

## Mi "Ahora esto:" fue:

```java
// VERSION feature/Bl5
@Entity
@Table(name = "almacenes")
public class Almacen {

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "tipo_almacenamiento", nullable = false)
    private String tipoAlmacenamiento;

    @Column(name = "es_predeterminado", nullable = false)
    private Boolean esPredeterminado;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;
}

// VERSION master
@Entity
@Table(name = "almacen", schema = "drinkgo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sede_id", "codigo"}))
public class Almacen {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    private OffsetDateTime creadoEn;
    private OffsetDateTime actualizadoEn;
}

```

## Gemini me devolvió:

```java
@Entity
@Table(name = "almacenes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"sede_id", "codigo"}))
public class Almacen {

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "tipo_almacenamiento", nullable = false)
    private String tipoAlmacenamiento = "general";

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}

```

## Análisis técnico

Este conflicto fue uno de los más delicados hasta ahora porque no era solo un cambio de campos, sino un choque de modelo de negocio.

Diferencias estructurales originales:

feature/Bl5:
- Usaba negocio_id.
- Tabla llamada "almacenes".
- tipo_almacenamiento.
- es_predeterminado.
- esta_activo.
- LocalDateTime.
- actualizado_en controlado por BD.

master:
- Migración a arquitectura multi-tenant (tenant_id).
- Tabla singular "almacen" en schema "drinkgo".
- Cambio de naming (tipo, activo).
- Uso de OffsetDateTime.
- Control automático con @PrePersist y @PreUpdate.
- UniqueConstraint por sede_id + codigo.

Lo que hizo Gemini fue crear una versión híbrida que:

✔ Mantiene negocio_id (no tenant_id).
✔ Mantiene nombre de tabla "almacenes".
✔ Adopta UniqueConstraint.
✔ Agrega control automático de timestamps.
✔ Estandariza defaults en atributos.
✘ Elimina schema "drinkgo".
✘ Elimina OffsetDateTime.
✘ Ignora completamente el modelo multi-tenant.
✘ Cambia nombres de columnas respecto a master.

Impacto arquitectónico:

Esto ya no es un simple merge. Aquí se está decidiendo:

- ¿El sistema será multi-tenant o no?
- ¿La base usa schema explícito?
- ¿Se usa OffsetDateTime (zona horaria) o LocalDateTime?
- ¿El naming final de columnas cuál es?

Nivel de riesgo: MUY ALTO.

Porque Almacen es entidad estructural base del inventario.
Si esta entidad cambia:

- Se afectan relaciones con Stock.
- Se afectan movimientos.
- Se afectan alertas.
- Se afectan constraints de base de datos.

Este fue un conflicto de definición de arquitectura, no solo de sintaxis.


# Caso 5 — Desde aqui solo te pasare lo que pedi y la respuesta de gemini:

# Mi "Ahora esto" fue:


package DrinkGo.DrinkGo_backend.entity;



<<<<<<< feature/Bl5

import jakarta.persistence.*;



/**

 * Items de transferencia de inventario.

 * Mapeo exacto de la tabla detalle_transferencias_inventario de drinkgo_database.sql.

 */

=======

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;



>>>>>>> master

@Entity

@Table(name = "detalle_transferencias_inventario")

public class DetalleTransferenciaInventario {



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



<<<<<<< feature/Bl5

    @Column(name = "transferencia_id", nullable = false, insertable = false, updatable = false)

    private Long transferenciaId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "transferencia_id", nullable = false)

    private TransferenciaInventario transferencia;



    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)

    private Long productoId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "producto_id", nullable = false)

    private Producto producto;



    @Column(name = "lote_id", insertable = false, updatable = false)

    private Long loteId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "lote_id")

    private LoteInventario lote;



=======

    @Column(name = "transferencia_id", nullable = false)

    private Long transferenciaId;



    @Column(name = "producto_id", nullable = false)

    private Long productoId;



    @Column(name = "lote_id")

    private Long loteId;



>>>>>>> master

    @Column(name = "cantidad_solicitada", nullable = false)

    private Integer cantidadSolicitada;



    @Column(name = "cantidad_enviada")

    private Integer cantidadEnviada;



    @Column(name = "cantidad_recibida")

    private Integer cantidadRecibida;



    @Column(name = "notas", length = 300)

    private String notas;



<<<<<<< feature/Bl5

    // ── Getters y Setters ──



    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }



    public Long getTransferenciaId() { return transferencia != null ? transferencia.getId() : transferenciaId; }



    public TransferenciaInventario getTransferencia() { return transferencia; }

    public void setTransferencia(TransferenciaInventario transferencia) { this.transferencia = transferencia; }



    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }



    public Producto getProducto() { return producto; }

    public void setProducto(Producto producto) { this.producto = producto; }



    public Long getLoteId() { return lote != null ? lote.getId() : loteId; }



    public LoteInventario getLote() { return lote; }

    public void setLote(LoteInventario lote) { this.lote = lote; }



    public Integer getCantidadSolicitada() { return cantidadSolicitada; }

    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }



    public Integer getCantidadEnviada() { return cantidadEnviada; }

    public void setCantidadEnviada(Integer cantidadEnviada) { this.cantidadEnviada = cantidadEnviada; }



    public Integer getCantidadRecibida() { return cantidadRecibida; }

    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }



    public String getNotas() { return notas; }

    public void setNotas(String notas) { this.notas = notas; }

=======

    // --- Getters y Setters ---



    public Long getId() {

        return id;

    }



    public void setId(Long id) {

        this.id = id;

    }



    public Long getTransferenciaId() {

        return transferenciaId;

    }



    public void setTransferenciaId(Long transferenciaId) {

        this.transferenciaId = transferenciaId;

    }



    public Long getProductoId() {

        return productoId;

    }



    public void setProductoId(Long productoId) {

        this.productoId = productoId;

    }



    public Long getLoteId() {

        return loteId;

    }



    public void setLoteId(Long loteId) {

        this.loteId = loteId;

    }



    public Integer getCantidadSolicitada() {

        return cantidadSolicitada;

    }



    public void setCantidadSolicitada(Integer cantidadSolicitada) {

        this.cantidadSolicitada = cantidadSolicitada;

    }



    public Integer getCan

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Items de transferencia de inventario.
 * Mapeo de la tabla detalle_transferencias_inventario de drinkgo_database.sql.
 */
@Entity
@Table(name = "detalle_transferencias_inventario")
public class DetalleTransferenciaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transferencia_id", nullable = false, insertable = false, updatable = false)
    private Long transferenciaId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_id", nullable = false)
    private TransferenciaInventario transferencia;

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "lote_id", insertable = false, updatable = false)
    private Long loteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LoteInventario lote;

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_enviada")
    private Integer cantidadEnviada;

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida;

    @Column(name = "notas", length = 300)
    private String notas;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTransferenciaId() { 
        return transferencia != null ? transferencia.getId() : transferenciaId; 
    }

    public TransferenciaInventario getTransferencia() { return transferencia; }
    public void setTransferencia(TransferenciaInventario transferencia) { 
        this.transferencia = transferencia; 
    }

    public Long getProductoId() { 
        return producto != null ? producto.getId() : productoId; 
    }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Long getLoteId() { 
        return lote != null ? lote.getId() : loteId; 
    }

    public LoteInventario getLote() { return lote; }
    public void setLote(LoteInventario lote) { this.lote = lote; }

    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { 
        this.cantidadSolicitada = cantidadSolicitada; 
    }

    public Integer getCantidadEnviada() { return cantidadEnviada; }
    public void setCantidadEnviada(Integer cantidadEnviada) { 
        this.cantidadEnviada = cantidadEnviada; 
    }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { 
        this.cantidadRecibida = cantidadRecibida; 
    }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}

# Sexto Caso

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.entity;



import jakarta.persistence.*;

<<<<<<< feature/Bl5

=======

import org.hibernate.annotations.SQLDelete;

import org.hibernate.annotations.SQLRestriction;



>>>>>>> master

import java.math.BigDecimal;

import java.time.LocalDate;

import java.time.LocalDateTime;



<<<<<<< feature/Bl5

/**

 * Lotes de inventario con sistema FIFO (RF-INV-002..003).

 * Mapeo exacto de la tabla lotes_inventario de drinkgo_database.sql.

 * Los lotes se consumen ordenados por fecha_recepcion ASC (FIFO estricto).

 */

@Entity

@Table(name = "lotes_inventario")

public class LoteInventario {



    public enum EstadoLote {

=======

@Entity

@Table(name = "lotes_inventario")

@SQLDelete(sql = "UPDATE lotes_inventario SET eliminado_en = NOW() WHERE id = ?")

@SQLRestriction("eliminado_en IS NULL")

public class LoteInventario {



    public enum LoteEstado {

>>>>>>> master

        disponible, agotado, vencido, cuarentena, devuelto

    }



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @Column(name = "negocio_id", nullable = false)

    private Long negocioId;



<<<<<<< feature/Bl5

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)

    private Long productoId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "producto_id", nullable = false)

    private Producto producto;



    @Column(name = "almacen_id", nullable = false, insertable = false, updatable = false)

    private Long almacenId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "almacen_id", nullable = false)

    private Almacen almacen;



=======

    @Column(name = "producto_id", nullable = false)

    private Long productoId;



    @Column(name = "almacen_id", nullable = false)

    private Long almacenId;



>>>>>>> master

    @Column(name = "numero_lote", nullable = false, length = 50)

    private String numeroLote;



    @Column(name = "cantidad_inicial", nullable = false)

    private Integer cantidadInicial;



    @Column(name = "cantidad_restante", nullable = false)

    private Integer cantidadRestante;



    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)

    private BigDecimal precioCompra;



    @Column(name = "fecha_fabricacion")

    private LocalDate fechaFabricacion;



    @Column(name = "fecha_vencimiento")

    private LocalDate fechaVencimiento;



    @Column(name = "fecha_recepcion", nullable = false)

    private LocalDate fechaRecepcion;



    @Column(name = "proveedor_id")

    private Long proveedorId;



    @Column(name = "orden_compra_id")

    private Long ordenCompraId;



    @Enumerated(EnumType.STRING)

    @Column(name = "estado", nullable = false)

<<<<<<< feature/Bl5

    private EstadoLote estado = EstadoLote.disponible;

=======

    private LoteEstado estado = LoteEstado.disponible;

>>>>>>> master



    @Column(name = "notas", columnDefinition = "TEXT")

    private String notas;



<<<<<<< feature/Bl5

    @Column(name = "creado_en", nullable = false, updatable = false)

    private LocalDateTime creadoEn;



    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false)

    private LocalDateTime actualizadoEn;



    // ── Getters y Setters ──



    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }



    public Long getNegocioId() { return negocioId; }

    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }



    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }



    public Producto getProducto() { return producto; }

    public void setProducto(Producto producto) { this.producto = producto; }



    public Long getAlmacenId() { return almacen != null ? almacen.getId() : almacenId; }



    public Almacen getAlmacen() { return almacen; }

    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }



    public String getNumeroLote() { return numeroLote; }

    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }



    public Integer getCantidadInicial() { return cantidadInicial; }

    public void setCantidadInicial(Integer cantidadInicial) { this.cantidadInicial = cantidadInicial; }



    public Integer getCantidadRestante() { return cantidadRestante; }

    public void setCantidadRestante(Integer cantidadRestante) { this.cantidadRestante = cantidadRestante; }



    public BigDecimal getPrecioCompra() { return precioCompra; }

    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }



    public LocalDate getFechaFabricacion() { return fechaFabricacion; }

    public void setFechaFabricacion(LocalDate fechaFabricacion) { this.fechaFabricacion = fechaFabricacion; }



    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }



    public LocalDate getFechaRecepcion() { return fechaRecepcion; }

    public void setFechaRecepcion(LocalDate fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }



    public Long getProveedorId() { return proveedorId; }

    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }



    public Long getOrdenCompraId() { return ordenCompraId; }

    public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }



    public EstadoLote getEstado() { return estado; }

    public void setEstado(EstadoLote estado) { this.estado = estado; }



    public String getNotas() { return notas; }

    public void setNotas(String notas) { this.notas = notas; }



    public LocalDateTime getCreadoEn() { return creadoEn; }

    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }



    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

=======

    @Column(name = "creado_en", insertable = false, updatable = false)

    private LocalDateTime creadoEn;



    @Column(name = "actualizado_en", insertable = false, updatable = false)

    private LocalDateTime actualizadoEn;



    @Column(name = "eliminado_en")

    private LocalDateTime eliminadoEn;



    // --- Getters y Setters ---



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



    public Long getProductoId() {

        return productoId;

    }



    public void setProductoId(Long productoId) {

        this.productoId = productoId;

    }



    public Long getAlmacenId() {

        return almacenId;

    }



    public void setAlmacenId(Long almacenId) {

        this.almacenId = almacenId;

    }



    public String getNumeroLote() {

        return numeroLote;

    }



    public void setNumeroLote(String numeroLote) {

        this.numeroLote = numeroLote;

    }



    public Integer getCantidadInicial() {

        return cantidadInicial;

    }



    public void setCantidadInicial(Integer cantidadInicial) {

        this.cantidadInicial = cantidadInicial;

    }



    public Integer getCantidadRestante() {

        return cantidadRestante;

    }



    public void setCantidadRestante(Integer cantidadRestante) {

        this.cantidadRestante = cantidadRestante;

    }



    public BigDecimal getPrecioCompra() {

        return precioCompra;

    }



    public void setPrecioCompra(BigDecimal precioCompra) {

        this.precioCompra = precioCompra;

    }



    public LocalDate getFechaFabricacion() {

        return fechaFabricacion;

    }



    public void setFechaFabricacion(LocalDate fechaFabricacion) {

        this.fechaFabricacion = fechaFabricacion;

    }



    public LocalDate getFechaVencimiento() {

        return fechaVencimiento;

    }



    public void setFechaVencimiento(LocalDate fechaVencimiento) {

        this.fechaVencimiento = fechaVencimiento;

    }



    public LocalDate getFechaRecepcion() {

        return fechaRecepcion;

    }



    public void setFechaRecepcion(LocalDate fechaRecepcion) {

        this.fechaRecepcion = fechaRecepcion;

    }

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lotes de inventario con sistema FIFO (RF-INV-002..003).
 * Los lotes se consumen ordenados por fecha_recepcion ASC (FIFO estricto).
 */
@Entity
@Table(name = "lotes_inventario")
@SQLDelete(sql = "UPDATE lotes_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class LoteInventario {

    public enum LoteEstado {
        disponible, agotado, vencido, cuarentena, devuelto
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "almacen_id", nullable = false, insertable = false, updatable = false)
    private Long almacenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @Column(name = "cantidad_restante", nullable = false)
    private Integer cantidadRestante;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "fecha_fabricacion")
    private LocalDate fechaFabricacion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDate fechaRecepcion;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "orden_compra_id")
    private Long ordenCompraId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private LoteEstado estado = LoteEstado.disponible;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Long getAlmacenId() { return almacen != null ? almacen.getId() : almacenId; }

    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Integer getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(Integer cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    public Integer getCantidadRestante() { return cantidadRestante; }
    public void setCantidadRestante(Integer cantidadRestante) { this.cantidadRestante = cantidadRestante; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public LocalDate getFechaFabricacion() { return fechaFabricacion; }
    public void setFechaFabricacion(LocalDate fechaFabricacion) { this.fechaFabricacion = fechaFabricacion; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDate getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDate fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public Long getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }

    public LoteEstado getEstado() { return estado; }
    public void setEstado(LoteEstado estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}

# Septimo Caso

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.entity;



import jakarta.persistence.*;

<<<<<<< feature/Bl5

import java.math.BigDecimal;

import java.time.LocalDateTime;



/**

 * Movimientos de inventario (RF-INV-004..006).

 * Mapeo exacto de la tabla movimientos_inventario de drinkgo_database.sql.

 * Registra cada entrada y salida con referencia al lote utilizado.

 */

@Entity

@Table(name = "movimientos_inventario")

public class MovimientoInventario {



    public enum TipoMovimiento {

        entrada_compra, salida_venta, entrada_devolucion, salida_devolucion,

        entrada_transferencia, salida_transferencia, ajuste_entrada, ajuste_salida,

        merma, rotura, vencimiento, stock_inicial, entrada_produccion, salida_produccion

=======

import org.hibernate.annotations.SQLDelete;

import org.hibernate.annotations.SQLRestriction;



import java.math.BigDecimal;

import java.time.LocalDateTime;



@Entity

@Table(name = "movimientos_inventario")

@SQLDelete(sql = "UPDATE movimientos_inventario SET eliminado_en = NOW() WHERE id = ?")

@SQLRestriction("eliminado_en IS NULL")

public class MovimientoInventario {



    public enum TipoMovimiento {

        entrada_compra,

        salida_venta,

        entrada_devolucion,

        salida_devolucion,

        entrada_transferencia,

        salida_transferencia,

        ajuste_entrada,

        ajuste_salida,

        merma,

        rotura,

        vencimiento,

        stock_inicial,

        entrada_produccion,

        salida_produccion

>>>>>>> master

    }



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @Column(name = "negocio_id", nullable = false)

    private Long negocioId;



<<<<<<< feature/Bl5

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)

    private Long productoId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "producto_id", nullable = false)

    private Producto producto;



    @Column(name = "almacen_id", nullable = false, insertable = false, updatable = false)

    private Long almacenId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "almacen_id", nullable = false)

    private Almacen almacen;



    @Column(name = "lote_id", insertable = false, updatable = false)

    private Long loteId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "lote_id")

    private LoteInventario lote;



=======

    @Column(name = "producto_id", nullable = false)

    private Long productoId;



    @Column(name = "almacen_id", nullable = false)

    private Long almacenId;



    @Column(name = "lote_id")

    private Long loteId;



>>>>>>> master

    @Enumerated(EnumType.STRING)

    @Column(name = "tipo_movimiento", nullable = false)

    private TipoMovimiento tipoMovimiento;



    @Column(name = "cantidad", nullable = false)

    private Integer cantidad;



    @Column(name = "costo_unitario", precision = 10, scale = 2)

    private BigDecimal costoUnitario;



    @Column(name = "tipo_referencia", length = 50)

    private String tipoReferencia;



    @Column(name = "referencia_id")

    private Long referenciaId;



    @Column(name = "motivo", length = 300)

    private String motivo;



    @Column(name = "realizado_por")

    private Long realizadoPor;



<<<<<<< feature/Bl5

    @Column(name = "creado_en", nullable = false, updatable = false)

    private LocalDateTime creadoEn;



    // ── Getters y Setters ──



    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }



    public Long getNegocioId() { return negocioId; }

    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }



    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }



    public Producto getProducto() { return producto; }

    public void setProducto(Producto producto) { this.producto = producto; }



    public Long getAlmacenId() { return almacen != null ? almacen.getId() : almacenId; }



    public Almacen getAlmacen() { return almacen; }

    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }



    public Long getLoteId() { return lote != null ? lote.getId() : loteId; }



    public LoteInventario getLote() { return lote; }

    public void setLote(LoteInventario lote) { this.lote = lote; }



    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }



    public Integer getCantidad() { return cantidad; }

    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }



    public BigDecimal getCostoUnitario() { return costoUnitario; }

    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }



    public String getTipoReferencia() { return tipoReferencia; }

    public void setTipoReferencia(String tipoReferencia) { this.tipoReferencia = tipoReferencia; }



    public Long getReferenciaId() { return referenciaId; }

    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }



    public String getMotivo() { return motivo; }

    public void setMotivo(String motivo) { this.motivo = motivo; }



    public Long getRealizadoPor() { return realizadoPor; }

    public void setRealizadoPor(Long realizadoPor) { this.realizadoPor = realizadoPor; }



    public LocalDateTime getCreadoEn() { return creadoEn; }

    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

=======

    @Column(name = "creado_en", insertable = false, updatable = false)

    private LocalDateTime creadoEn;



    // --- Getters y Setters ---



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



    public Long getProductoId() {

        return productoId;

    }



    public void setProductoId(Long productoId) {

        this.productoId = productoId;

    }



    public Long getAlmacenId() {

        return almacenId;

    }



    public void setAlmacenId(Long almacenId) {

        this.almacenId = almacenId;

    }



    public Long getLoteId() {

        return loteId;

    }



    public void setLoteId(Long loteId) {

        this.loteId = loteId;

    }



    public TipoMovimiento getTipoMovimiento() {

        return tipoMovimiento;

    }



    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {

        this.tipoMovimiento = tipoMovimiento;

    }



    public Integer getCantidad() {

        return cantidad;

    }



    public void setCantidad(Integer cantidad) {

        this.cantidad = cantidad;

    }



    public BigDecimal getCostoUnitario() {

        return costoUnitario;

    }



    public void setCostoUnitario(BigDecimal costoUnitario) {

        this.costoUnitario = costoUnitario;

    }



    public String getTipoReferencia() {

        return tipoReferencia;

    }



    public void setTipoReferencia(String tipoReferencia) {

        this.tipoReferencia = tipoReferencia;

    }



    public Long getReferenciaId() {

        return referenciaId;

    }



    public void setReferenciaId(Long referenciaId) 

# Respuesta Geminii:

package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Movimientos de inventario (RF-INV-004..006).
 * Registra cada entrada y salida con referencia al lote utilizado.
 */
@Entity
@Table(name = "movimientos_inventario")
@SQLDelete(sql = "UPDATE movimientos_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class MovimientoInventario {

    public enum TipoMovimiento {
        entrada_compra, salida_venta, entrada_devolucion, salida_devolucion,
        entrada_transferencia, salida_transferencia, ajuste_entrada, ajuste_salida,
        merma, rotura, vencimiento, stock_inicial, entrada_produccion, salida_produccion
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "almacen_id", nullable = false, insertable = false, updatable = false)
    private Long almacenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(name = "lote_id", insertable = false, updatable = false)
    private Long loteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LoteInventario lote;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "tipo_referencia", length = 50)
    private String tipoReferencia;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "motivo", length = 300)
    private String motivo;

    @Column(name = "realizado_por")
    private Long realizadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Long getAlmacenId() { return almacen != null ? almacen.getId() : almacenId; }

    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }

    public Long getLoteId() { return lote != null ? lote.getId() : loteId; }

    public LoteInventario getLote() { return lote; }
    public void setLote(LoteInventario lote) { this.lote = lote; }

    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }

    public String getTipoReferencia() { return tipoReferencia; }
    public void setTipoReferencia(String tipoReferencia) { this.tipoReferencia = tipoReferencia; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Long getRealizadoPor() { return realizadoPor; }
    public void setRealizadoPor(Long realizadoPor) { this.realizadoPor = realizadoPor; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}

# Octavo Caso

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.entity;



import jakarta.persistence.*;

<<<<<<< feature/Bl5

import java.time.LocalDateTime;



/**

 * Stock actual por producto y almacén (RF-INV-001).

 * Mapeo exacto de la tabla stock_inventario de drinkgo_database.sql.

 * La columna cantidad_disponible es GENERATED ALWAYS (calculada por MySQL).

 */

@Entity

@Table(name = "stock_inventario")

=======

import org.hibernate.annotations.SQLDelete;

import org.hibernate.annotations.SQLRestriction;



import java.time.LocalDateTime;



@Entity

@Table(name = "stock_inventario")

@SQLDelete(sql = "UPDATE stock_inventario SET eliminado_en = NOW() WHERE id = ?")

@SQLRestriction("eliminado_en IS NULL")

>>>>>>> master

public class StockInventario {



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @Column(name = "negocio_id", nullable = false)

    private Long negocioId;



<<<<<<< feature/Bl5

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)

    private Long productoId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "producto_id", nullable = false)

    private Producto producto;



    @Column(name = "almacen_id", nullable = false, insertable = false, updatable = false)

    private Long almacenId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "almacen_id", nullable = false)

    private Almacen almacen;



=======

    @Column(name = "producto_id", nullable = false)

    private Long productoId;



    @Column(name = "almacen_id", nullable = false)

    private Long almacenId;



>>>>>>> master

    @Column(name = "cantidad_en_mano", nullable = false)

    private Integer cantidadEnMano = 0;



    @Column(name = "cantidad_reservada", nullable = false)

    private Integer cantidadReservada = 0;



<<<<<<< feature/Bl5

    /** Columna generada por MySQL: cantidad_en_mano - cantidad_reservada */

=======

    // Columna generada por MySQL: cantidad_en_mano - cantidad_reservada

>>>>>>> master

    @Column(name = "cantidad_disponible", insertable = false, updatable = false)

    private Integer cantidadDisponible;



    @Column(name = "ultimo_conteo_en")

    private LocalDateTime ultimoConteoEn;



    @Column(name = "ultimo_movimiento_en")

    private LocalDateTime ultimoMovimientoEn;



<<<<<<< feature/Bl5

    @Column(name = "creado_en", nullable = false, updatable = false)

    private LocalDateTime creadoEn;



    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false)

    private LocalDateTime actualizadoEn;



    // ── Getters y Setters ──



    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }



    public Long getNegocioId() { return negocioId; }

    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }



    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }



    public Producto getProducto() { return producto; }

    public void setProducto(Producto producto) { this.producto = producto; }



    public Long getAlmacenId() { return almacen != null ? almacen.getId() : almacenId; }



    public Almacen getAlmacen() { return almacen; }

    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }



    public Integer getCantidadEnMano() { return cantidadEnMano; }

    public void setCantidadEnMano(Integer cantidadEnMano) { this.cantidadEnMano = cantidadEnMano; }



    public Integer getCantidadReservada() { return cantidadReservada; }

    public void setCantidadReservada(Integer cantidadReservada) { this.cantidadReservada = cantidadReservada; }



    public Integer getCantidadDisponible() { return cantidadDisponible; }



    public LocalDateTime getUltimoConteoEn() { return ultimoConteoEn; }

    public void setUltimoConteoEn(LocalDateTime ultimoConteoEn) { this.ultimoConteoEn = ultimoConteoEn; }



    public LocalDateTime getUltimoMovimientoEn() { return ultimoMovimientoEn; }

    public void setUltimoMovimientoEn(LocalDateTime ultimoMovimientoEn) { this.ultimoMovimientoEn = ultimoMovimientoEn; }



    public LocalDateTime getCreadoEn() { return creadoEn; }

    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }



    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

=======

    @Column(name = "creado_en", insertable = false, updatable = false)

    private LocalDateTime creadoEn;



    @Column(name = "actualizado_en", insertable = false, updatable = false)

    private LocalDateTime actualizadoEn;



    @Column(name = "eliminado_en")

    private LocalDateTime eliminadoEn;



    // --- Getters y Setters ---



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



    public Long getProductoId() {

        return productoId;

    }



    public void setProductoId(Long productoId) {

        this.productoId = productoId;

    }



    public Long getAlmacenId() {

        return almacenId;

    }



    public void setAlmacenId(Long almacenId) {

        this.almacenId = almacenId;

    }



    public Integer getCantidadEnMano() {

        return cantidadEnMano;

    }



    public void setCantidadEnMano(Integer cantidadEnMano) {

        this.cantidadEnMano = cantidadEnMano;

    }



    public Integer getCantidadReservada() {

        return cantidadReservada;

    }



    public void setCantidadReservada(Integer cantidadReservada) {

        this.cantidadReservada = cantidadReservada;

    }

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

/**
 * Stock actual por producto y almacén (RF-INV-001).
 * La columna cantidad_disponible es calculada por la base de datos (mano - reservada).
 */
@Entity
@Table(name = "stock_inventario")
@SQLDelete(sql = "UPDATE stock_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class StockInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "almacen_id", nullable = false, insertable = false, updatable = false)
    private Long almacenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(name = "cantidad_en_mano", nullable = false)
    private Integer cantidadEnMano = 0;

    @Column(name = "cantidad_reservada", nullable = false)
    private Integer cantidadReservada = 0;

    /** Columna generada por MySQL: cantidad_en_mano - cantidad_reservada */
    @Column(name = "cantidad_disponible", insertable = false, updatable = false)
    private Integer cantidadDisponible;

    @Column(name = "ultimo_conteo_en")
    private LocalDateTime ultimoConteoEn;

    @Column(name = "ultimo_movimiento_en")
    private LocalDateTime ultimoMovimientoEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Long getAlmacenId() { return almacen != null ? almacen.getId() : almacenId; }

    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }

    public Integer getCantidadEnMano() { return cantidadEnMano; }
    public void setCantidadEnMano(Integer cantidadEnMano) { this.cantidadEnMano = cantidadEnMano; }

    public Integer getCantidadReservada() { return cantidadReservada; }
    public void setCantidadReservada(Integer cantidadReservada) { this.cantidadReservada = cantidadReservada; }

    public Integer getCantidadDisponible() { return cantidadDisponible; }

    public LocalDateTime getUltimoConteoEn() { return ultimoConteoEn; }
    public void setUltimoConteoEn(LocalDateTime ultimoConteoEn) { this.ultimoConteoEn = ultimoConteoEn; }

    public LocalDateTime getUltimoMovimientoEn() { return ultimoMovimientoEn; }
    public void setUltimoMovimientoEn(LocalDateTime ultimoMovimientoEn) { this.ultimoMovimientoEn = ultimoMovimientoEn; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}

# Decimo Caso: 

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.entity;



import jakarta.persistence.*;

<<<<<<< feature/Bl5

=======

import org.hibernate.annotations.SQLDelete;

import org.hibernate.annotations.SQLRestriction;



>>>>>>> master

import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;



<<<<<<< feature/Bl5

/**

 * Transferencias entre almacenes (RF-INV-005).

 * Mapeo exacto de la tabla transferencias_inventario de drinkgo_database.sql.

 * Mantiene trazabilidad completa del proceso de transferencia.

 */

@Entity

@Table(name = "transferencias_inventario")

public class TransferenciaInventario {



    public enum EstadoTransferencia {

=======

@Entity

@Table(name = "transferencias_inventario")

@SQLDelete(sql = "UPDATE transferencias_inventario SET eliminado_en = NOW() WHERE id = ?")

@SQLRestriction("eliminado_en IS NULL")

public class TransferenciaInventario {



    public enum TransferenciaEstado {

>>>>>>> master

        borrador, pendiente, en_transito, recibida, cancelada

    }



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @Column(name = "negocio_id", nullable = false)

    private Long negocioId;



    @Column(name = "numero_transferencia", nullable = false, length = 30)

    private String numeroTransferencia;



<<<<<<< feature/Bl5

    @Column(name = "almacen_origen_id", nullable = false, insertable = false, updatable = false)

    private Long almacenOrigenId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "almacen_origen_id", nullable = false)

    private Almacen almacenOrigen;



    @Column(name = "almacen_destino_id", nullable = false, insertable = false, updatable = false)

    private Long almacenDestinoId;



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "almacen_destino_id", nullable = false)

    private Almacen almacenDestino;



    @Enumerated(EnumType.STRING)

    @Column(name = "estado", nullable = false)

    private EstadoTransferencia estado = EstadoTransferencia.borrador;

=======

    @Column(name = "almacen_origen_id", nullable = false)

    private Long almacenOrigenId;



    @Column(name = "almacen_destino_id", nullable = false)

    private Long almacenDestinoId;



    @Enumerated(EnumType.STRING)

    @Column(name = "estado", nullable = false)

    private TransferenciaEstado estado = TransferenciaEstado.borrador;

>>>>>>> master



    @Column(name = "solicitado_por")

    private Long solicitadoPor;



    @Column(name = "aprobado_por")

    private Long aprobadoPor;



    @Column(name = "recibido_por")

    private Long recibidoPor;



    @Column(name = "notas", columnDefinition = "TEXT")

    private String notas;



    @Column(name = "solicitado_en")

    private LocalDateTime solicitadoEn;



    @Column(name = "aprobado_en")

    private LocalDateTime aprobadoEn;



    @Column(name = "despachado_en")

    private LocalDateTime despachadoEn;



    @Column(name = "recibido_en")

    private LocalDateTime recibidoEn;



<<<<<<< feature/Bl5

    @Column(name = "creado_en", nullable = false, updatable = false)

    private LocalDateTime creadoEn;



    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false)

    private LocalDateTime actualizadoEn;



    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<DetalleTransferenciaInventario> detalles = new ArrayList<>();



    // ── Getters y Setters ──



    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }



    public Long getNegocioId() { return negocioId; }

    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }



    public String getNumeroTransferencia() { return numeroTransferencia; }

    public void setNumeroTransferencia(String numeroTransferencia) { this.numeroTransferencia = numeroTransferencia; }



    public Long getAlmacenOrigenId() { return almacenOrigen != null ? almacenOrigen.getId() : almacenOrigenId; }



    public Almacen getAlmacenOrigen() { return almacenOrigen; }

    public void setAlmacenOrigen(Almacen almacenOrigen) { this.almacenOrigen = almacenOrigen; }



    public Long getAlmacenDestinoId() { return almacenDestino != null ? almacenDestino.getId() : almacenDestinoId; }



    public Almacen getAlmacenDestino() { return almacenDestino; }

    public void setAlmacenDestino(Almacen almacenDestino) { this.almacenDestino = almacenDestino; }



    public EstadoTransferencia getEstado() { return estado; }

    public void setEstado(EstadoTransferencia estado) { this.estado = estado; }



    public Long getSolicitadoPor() { return solicitadoPor; }

    public void setSolicitadoPor(Long solicitadoPor) { this.solicitadoPor = solicitadoPor; }



    public Long getAprobadoPor() { return aprobadoPor; }

    public void setAprobadoPor(Long aprobadoPor) { this.aprobadoPor = aprobadoPor; }



    public Long getRecibidoPor() { return recibidoPor; }

    public void setRecibidoPor(Long recibidoPor) { this.recibidoPor = recibidoPor; }



    public String getNotas() { return notas; }

    public void setNotas(String notas) { this.notas = notas; }



    public LocalDateTime getSolicitadoEn() { return solicitadoEn; }

    public void setSolicitadoEn(LocalDateTime solicitadoEn) { this.solicitadoEn = solicitadoEn; }



    public LocalDateTime getAprobadoEn() { return aprobadoEn; }

    public void setAprobadoEn(LocalDateTime aprobadoEn) { this.aprobadoEn = aprobadoEn; }



    public LocalDateTime getDespachadoEn() { return despachadoEn; }

    public void setDespachadoEn(LocalDateTime despachadoEn) { this.despachadoEn = despachadoEn; }



    public LocalDateTime getRecibidoEn() { return recibidoEn; }

    public void setRecibidoEn(LocalDateTime recibidoEn) { this.recibidoEn = recibidoEn; }



    public LocalDateTime getCreadoEn() { return creadoEn; }

    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }



    public LocalDateTime getActualizadoEn() { return actualizadoEn; }



    public List<DetalleTransferenciaInventario> getDetalles() { return detalles; }

    public void setDetalles(List<DetalleTransferenciaInventario> detalles) { this.detalles = detalles; }

=======

    @Column(name = "creado_en", insertable = false, updatable = false)

    private LocalDateTime creadoEn;



    @Column(name = "actualizado_en", insertable = false, updatable = false)

    private LocalDateTime actualizadoEn;



    @Column(name = "eliminado_en")

    private LocalDateTime eliminadoEn;



    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)

    @JoinColumn(name = "transferencia_id")

    private List<DetalleTransferenciaInventario> detalles = new ArrayList<>();



    // --- Getters y Setters ---



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



    public String getNumeroTransferencia() {

        return numeroTransferencia;

    }



    public void setNumeroTransferencia(String numeroTransferencia) {

        this.numeroTransferencia = numeroTransferencia;

    }



    public Long getAlmacenOrigenId() {

        return almacenOrigenId;

    }



    public void setAlmacenOrigenId(Long almacenOrigenId) {

        this.almacenOrigenId = almacenOrigenId;

    }



    public Long getAlmacenDestinoId() {

        return almacenDestinoId;

    }



    public void setAlmacenDestinoId(Long almacenDestinoId) {

        this.almacenDestinoId = almacenDestinoId;

    }



    public TransferenciaEstado getEstado() {

        return estado;

    }



    public void setEstado(TransferenciaEstado estado) {

        this.estado = estado;

    }



    public Long getSolicitadoPor() {

        return solicitadoPor;

    }



    public void setSolicitadoPor(Long solicitadoPor) {

        this.solicitadoPor = solicitadoPor;

    }



    public Long getAprobadoPor() {

        return aprobadoPor;

    }



    public void setAprobadoPor(Long aprobadoPor) {

        this.aprobadoPor = aprobadoPor;

    }



    public Long getRecibidoPor() {

        return recibidoPor;

    }



    public void setRecibidoPor(Long recibidoPor) {

        this.recibidoPor = recibidoPor;

    }



    public String getNotas() {

        return notas;

    }



    public void setNotas(String notas) {

        this.notas = notas;

    }

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Transferencias entre almacenes (RF-INV-005).
 * Mapeo de la tabla transferencias_inventario de drinkgo_database.sql.
 * Mantiene trazabilidad completa del proceso: solicitud -> tránsito -> recepción.
 */
@Entity
@Table(name = "transferencias_inventario")
@SQLDelete(sql = "UPDATE transferencias_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class TransferenciaInventario {

    public enum TransferenciaEstado {
        borrador, pendiente, en_transito, recibida, cancelada
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "numero_transferencia", nullable = false, length = 30)
    private String numeroTransferencia;

    @Column(name = "almacen_origen_id", nullable = false, insertable = false, updatable = false)
    private Long almacenOrigenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_origen_id", nullable = false)
    private Almacen almacenOrigen;

    @Column(name = "almacen_destino_id", nullable = false, insertable = false, updatable = false)
    private Long almacenDestinoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_destino_id", nullable = false)
    private Almacen almacenDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private TransferenciaEstado estado = TransferenciaEstado.borrador;

    @Column(name = "solicitado_por")
    private Long solicitadoPor;

    @Column(name = "aprobado_por")
    private Long aprobadoPor;

    @Column(name = "recibido_por")
    private Long recibidoPor;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "solicitado_en")
    private LocalDateTime solicitadoEn;

    @Column(name = "aprobado_en")
    private LocalDateTime aprobadoEn;

    @Column(name = "despachado_en")
    private LocalDateTime despachadoEn;

    @Column(name = "recibido_en")
    private LocalDateTime recibidoEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTransferenciaInventario> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        if (this.solicitadoEn == null) this.solicitadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNumeroTransferencia() { return numeroTransferencia; }
    public void setNumeroTransferencia(String numeroTransferencia) { this.numeroTransferencia = numeroTransferencia; }

    public Long getAlmacenOrigenId() { 
        return almacenOrigen != null ? almacenOrigen.getId() : almacenOrigenId; 
    }

    public Almacen getAlmacenOrigen() { return almacenOrigen; }
    public void setAlmacenOrigen(Almacen almacenOrigen) { this.almacenOrigen = almacenOrigen; }

    public Long getAlmacenDestinoId() { 
        return almacenDestino != null ? almacenDestino.getId() : almacenDestinoId; 
    }

    public Almacen getAlmacenDestino() { return almacenDestino; }
    public void setAlmacenDestino(Almacen almacenDestino) { this.almacenDestino = almacenDestino; }

    public TransferenciaEstado getEstado() { return estado; }
    public void setEstado(TransferenciaEstado estado) { this.estado = estado; }

    public Long getSolicitadoPor() { return solicitadoPor; }
    public void setSolicitadoPor(Long solicitadoPor) { this.solicitadoPor = solicitadoPor; }

    public Long getAprobadoPor() { return aprobadoPor; }
    public void setAprobadoPor(Long aprobadoPor) { this.aprobadoPor = aprobadoPor; }

    public Long getRecibidoPor() { return recibidoPor; }
    public void setRecibidoPor(Long recibidoPor) { this.recibidoPor = recibidoPor; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public LocalDateTime getSolicitadoEn() { return solicitadoEn; }
    public void setSolicitadoEn(LocalDateTime solicitadoEn) { this.solicitadoEn = solicitadoEn; }

    public LocalDateTime getAprobadoEn() { return aprobadoEn; }
    public void setAprobadoEn(LocalDateTime aprobadoEn) { this.aprobadoEn = aprobadoEn; }

    public LocalDateTime getDespachadoEn() { return despachadoEn; }
    public void setDespachadoEn(LocalDateTime despachadoEn) { this.despachadoEn = despachadoEn; }

    public LocalDateTime getRecibidoEn() { return recibidoEn; }
    public void setRecibidoEn(LocalDateTime recibidoEn) { this.recibidoEn = recibidoEn; }

    public LocalDateTime getCreadoEn() { return creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }

    public List<DetalleTransferenciaInventario> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleTransferenciaInventario> detalles) { this.detalles = detalles; }
}

# Onceavo Caso:

# Mi Ahora esto:

package DrinkGo.DrinkGo_backend.repository;



import DrinkGo.DrinkGo_backend.entity.AlertaInventario;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



import java.util.List;

import java.util.Optional;



<<<<<<< feature/Bl5

/**

 * Repositorio para alertas de inventario (RF-INV-004..005).

 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).

 */

@Repository

public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {



    /** Listar todas las alertas de un negocio */

    List<AlertaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);



    /** Buscar alerta por ID y negocio */

    Optional<AlertaInventario> findByIdAndNegocioId(Long id, Long negocioId);



    /** Listar alertas activas (no resueltas) de un negocio */

    List<AlertaInventario> findByNegocioIdAndEstaResueltaFalseOrderByCreadoEnDesc(Long negocioId);



    /** Listar alertas por tipo */

    List<AlertaInventario> findByNegocioIdAndTipoAlertaOrderByCreadoEnDesc(

            Long negocioId, AlertaInventario.TipoAlerta tipoAlerta);



    /** Verificar si ya existe una alerta activa del mismo tipo para un producto en un almacén */

    boolean existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResueltaFalse(

            Long negocioId, Long productoId, Long almacenId, AlertaInventario.TipoAlerta tipoAlerta);

=======

@Repository

public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.AlertaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para alertas de inventario (RF-INV-004..005).
 * Todas las consultas filtran por negocio_id para garantizar la seguridad de los datos.
 */
@Repository
public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {

    /** Listar todas las alertas de un negocio ordenadas por las más recientes */
    List<AlertaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Buscar alerta por ID y negocio para evitar acceso no autorizado */
    Optional<AlertaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar alertas filtrando por su estado (resueltas o no) */
    List<AlertaInventario> findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(
            Long negocioId, Boolean estaResuelta);

    /** Listar alertas activas (no resueltas) - Método de conveniencia */
    default List<AlertaInventario> findActivas(Long negocioId) {
        return findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(negocioId, false);
    }

    /** Listar alertas por tipo (ej: stock_bajo, vencido) */
    List<AlertaInventario> findByNegocioIdAndTipoAlertaOrderByCreadoEnDesc(
            Long negocioId, AlertaInventario.TipoAlerta tipoAlerta);

    /** * Verificar si ya existe una alerta del mismo tipo para un producto en un almacén.
     * Útil para evitar duplicar alertas de 'stock bajo' si ya hay una pendiente.
     */
    boolean existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResuelta(
            Long negocioId, Long productoId, Long almacenId, 
            AlertaInventario.TipoAlerta tipoAlerta, Boolean estaResuelta);
}

# Toceavo Caso:

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.repository;



import DrinkGo.DrinkGo_backend.entity.Almacen;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< feature/Bl5

import org.springframework.stereotype.Repository;



import java.util.Optional;



/**

 * Repositorio de referencia para almacenes.

 * Utilizado para validación en operaciones del Bloque 5.

 */

@Repository

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {



    /** Buscar almacén por ID y negocio (validación multi-tenant) */

    Optional<Almacen> findByIdAndNegocioId(Long id, Long negocioId);

=======

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;



import java.util.List;

import java.util.Optional;



@Repository

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

    

    List<Almacen> findBySedeIdAndActivoTrue(Long sedeId);

    

    List<Almacen> findByTenantIdAndActivoTrue(Long tenantId);

    

    Optional<Almacen> findBySedeIdAndCodigo(Long sedeId, String codigo);

    

    Optional<Almacen> findByIdAndTenantId(Long id, Long tenantId);

    

    @Query("SELECT a FROM Almacen a WHERE a.sedeId = :sedeId AND a.esPrincipal = true AND a.activo = true")

    Optional<Almacen> findAlmacenPrincipal(@Param("sedeId") Long sedeId);

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    
    /** Buscar almacén por ID y negocio (validación multi-tenant) */
    Optional<Almacen> findByIdAndNegocioId(Long id, Long negocioId);

    List<Almacen> findBySedeIdAndEstaActivoTrue(Long sedeId);
    
    List<Almacen> findByNegocioIdAndEstaActivoTrue(Long negocioId);
    
    Optional<Almacen> findBySedeIdAndCodigo(Long sedeId, String codigo);
    
    @Query("SELECT a FROM Almacen a WHERE a.sedeId = :sedeId AND a.esPrincipal = true AND a.estaActivo = true")
    Optional<Almacen> findAlmacenPrincipal(@Param("sedeId") Long sedeId);
    
    @Query("SELECT a FROM Almacen a WHERE a.sedeId = :sedeId AND a.tipoAlmacenamiento = :tipo AND a.estaActivo = true")
    List<Almacen> findBySedeIdAndTipo(@Param("sedeId") Long sedeId, @Param("tipo") String tipo);
    
    @Query("SELECT a FROM Almacen a WHERE a.negocioId = :negocioId AND a.tipoAlmacenamiento = 'frio' AND a.estaActivo = true")
    List<Almacen> findAlmacenesFrios(@Param("negocioId") Long negocioId);
    
    boolean existsBySedeIdAndCodigo(Long sedeId, String codigo);
    
    long countBySedeIdAndEstaActivoTrue(Long sedeId);
}

# Treceavo caso:

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.repository;



import DrinkGo.DrinkGo_backend.entity.LoteInventario;

<<<<<<< feature/Bl5

=======

import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;

>>>>>>> master

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;



import java.time.LocalDate;

import java.util.List;

<<<<<<< feature/Bl5

import java.util.Optional;



/**

 * Repositorio para lotes de inventario (RF-INV-002..003).

 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).

 * El orden FIFO se garantiza con fecha_recepcion ASC.

 */

@Repository

public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {



    /** Listar todos los lotes de un negocio */

    List<LoteInventario> findByNegocioId(Long negocioId);



    /** Buscar lote por ID y negocio */

    Optional<LoteInventario> findByIdAndNegocioId(Long id, Long negocioId);



    /**

     * FIFO: Obtener lotes disponibles de un producto en un almacén,

     * ordenados por fecha de recepción ascendente (más antiguos primero).

     * EXCLUYE lotes vencidos (fecha_vencimiento < hoy) para evitar vender producto expirado.

     */

    @Query("SELECT l FROM LoteInventario l WHERE l.productoId = :productoId " +

           "AND l.almacenId = :almacenId AND l.negocioId = :negocioId " +

           "AND l.estado = :estado AND l.cantidadRestante > :cantidadMinima " +

           "AND (l.fechaVencimiento IS NULL OR l.fechaVencimiento >= CURRENT_DATE) " +

           "ORDER BY l.fechaRecepcion ASC")

    List<LoteInventario> findLotesFIFODisponibles(

            @Param("productoId") Long productoId,

            @Param("almacenId") Long almacenId,

            @Param("negocioId") Long negocioId,

            @Param("estado") LoteInventario.EstadoLote estado,

            @Param("cantidadMinima") Integer cantidadMinima);



    /** Buscar lotes próximos a vencer (para alertas) */

    List<LoteInventario> findByNegocioIdAndEstadoAndFechaVencimientoBetween(

            Long negocioId, LoteInventario.EstadoLote estado, LocalDate desde, LocalDate hasta);



    /** Buscar lotes vencidos */

    List<LoteInventario> findByNegocioIdAndEstadoAndFechaVencimientoBefore(

            Long negocioId, LoteInventario.EstadoLote estado, LocalDate fecha);



    /** Buscar lotes por producto y negocio */

    List<LoteInventario> findByProductoIdAndNegocioId(Long productoId, Long negocioId);



    /** Verificar existencia de número de lote en el negocio */

    boolean existsByNumeroLoteAndNegocioId(String numeroLote, Long negocioId);

=======



@Repository

public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {



    List<LoteInventario> findByNegocioIdAndProductoIdOrderByFechaRecepcionAsc(

            Long negocioId, Long productoId);



    List<LoteInventario> findByNegocioIdAndProductoIdAndAlmacenIdOrderByFechaRecepcionAsc(

            Long negocioId, Long productoId, Long almacenId);

# Respuesta gemini:

package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para lotes de inventario (RF-INV-002..003).
 * Garantiza el cumplimiento del sistema FIFO (First-In, First-Out).
 */
@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {

    /** Listar todos los lotes de un negocio */
    List<LoteInventario> findByNegocioId(Long negocioId);

    /** Buscar lote por ID y negocio (Seguridad multi-tenant) */
    Optional<LoteInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /**
     * CONSULTA FIFO MAESTRA:
     * Obtiene lotes disponibles ordenados por fecha de recepción (más antiguos primero).
     * Filtra automáticamente lotes agotados y lotes cuya fecha de vencimiento ya pasó.
     */
    @Query("SELECT l FROM LoteInventario l WHERE l.negocioId = :negocioId " +
           "AND l.productoId = :productoId AND l.almacenId = :almacenId " +
           "AND l.estado = :estado AND l.cantidadRestante > 0 " +
           "AND (l.fechaVencimiento IS NULL OR l.fechaVencimiento >= :hoy) " +
           "ORDER BY l.fechaRecepcion ASC, l.id ASC")
    List<LoteInventario> findLotesFIFODisponibles(
            @Param("negocioId") Long negocioId,
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId,
            @Param("estado") LoteEstado estado,
            @Param("hoy") LocalDate hoy);

    /** Buscar lotes próximos a vencer (útil para el sistema de alertas) */
    @Query("SELECT l FROM LoteInventario l WHERE l.negocioId = :negocioId " +
           "AND l.estado = :estado AND l.fechaVencimiento BETWEEN :desde AND :hasta")
    List<LoteInventario> findProximosAVencer(
            @Param("negocioId") Long negocioId, 
            @Param("estado") LoteEstado estado, 
            @Param("desde") LocalDate desde, 
            @Param("hasta") LocalDate hasta);

    /** Buscar lotes que ya vencieron pero siguen marcados como disponibles en el sistema */
    List<LoteInventario> findByNegocioIdAndEstadoAndFechaVencimientoBefore(
            Long negocioId, LoteEstado estado, LocalDate fecha);

    /** Buscar lotes por producto y negocio */
    List<LoteInventario> findByProductoIdAndNegocioId(Long productoId, Long negocioId);

    /** Verificar si un número de lote ya existe en el negocio para evitar duplicados */
    boolean existsByNegocioIdAndNumeroLote(Long negocioId, String numeroLote);
}

# Catorceavo Caso:

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.repository;



import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



import java.util.List;

import java.util.Optional;



<<<<<<< feature/Bl5

/**

 * Repositorio para movimientos de inventario (RF-INV-004..006).

 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).

 */

@Repository

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {



    /** Listar todos los movimientos de un negocio, ordenados por fecha descendente */

    List<MovimientoInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);



    /** Buscar movimiento por ID y negocio */

    Optional<MovimientoInventario> findByIdAndNegocioId(Long id, Long negocioId);



    /** Listar movimientos de un producto */

    List<MovimientoInventario> findByProductoIdAndNegocioIdOrderByCreadoEnDesc(Long productoId, Long negocioId);



    /** Listar movimientos de un almacén */

    List<MovimientoInventario> findByAlmacenIdAndNegocioIdOrderByCreadoEnDesc(Long almacenId, Long negocioId);



    /** Listar movimientos de un lote */

    List<MovimientoInventario> findByLoteIdAndNegocioIdOrderByCreadoEnDesc(Long loteId, Long negocioId);

=======

@Repository

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {



    Optional<MovimientoInventario> findByIdAndNegocioId(Long id, Long negocioId);

# Respuesta gemini:

package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para movimientos de inventario (RF-INV-004..006).
 * Todas las consultas filtran por negocio_id para garantizar el aislamiento de datos.
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /** Buscar movimiento por ID y negocio (Seguridad multi-tenant) */
    Optional<MovimientoInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar el historial completo de un negocio */
    List<MovimientoInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Kardex por producto: historial de movimientos de un producto específico */
    List<MovimientoInventario> findByNegocioIdAndProductoIdOrderByCreadoEnDesc(
            Long negocioId, Long productoId);

    /** Historial de un almacén específico */
    List<MovimientoInventario> findByNegocioIdAndAlmacenIdOrderByCreadoEnDesc(
            Long negocioId, Long almacenId);

    /** Historial detallado: producto específico en un almacén específico */
    List<MovimientoInventario> findByNegocioIdAndProductoIdAndAlmacenIdOrderByCreadoEnDesc(
            Long negocioId, Long productoId, Long almacenId);

    /** Filtrar por tipo (ej: solo 'salida_venta' o solo 'merma') */
    List<MovimientoInventario> findByNegocioIdAndTipoMovimientoOrderByCreadoEnDesc(
            Long negocioId, MovimientoInventario.TipoMovimiento tipoMovimiento);

    /** Historial de un lote específico (Trazabilidad de lote) */
    List<MovimientoInventario> findByLoteIdAndNegocioIdOrderByCreadoEnDesc(
            Long loteId, Long negocioId);
}

# Quinceavo Caso:

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.repository;



import DrinkGo.DrinkGo_backend.entity.StockInventario;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< feature/Bl5

=======

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

>>>>>>> master

import org.springframework.stereotype.Repository;



import java.util.List;

import java.util.Optional;



<<<<<<< feature/Bl5

/**

 * Repositorio para stock de inventario (RF-INV-001).

 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).

 */

@Repository

public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {



    /** Listar todo el stock de un negocio */

    List<StockInventario> findByNegocioId(Long negocioId);



    /** Buscar stock por ID y negocio */

    Optional<StockInventario> findByIdAndNegocioId(Long id, Long negocioId);



    /** Buscar stock por producto y almacén dentro del negocio */

    Optional<StockInventario> findByProductoIdAndAlmacenIdAndNegocioId(Long productoId, Long almacenId, Long negocioId);



    /** Listar stock de un producto en todos los almacenes del negocio */

    List<StockInventario> findByProductoIdAndNegocioId(Long productoId, Long negocioId);



    /** Listar stock de un almacén específico */

    List<StockInventario> findByAlmacenIdAndNegocioId(Long almacenId, Long negocioId);

=======

@Repository

public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {



    List<StockInventario> findByNegocioId(Long negocioId);



    List<StockInventario>

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.StockInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para stock de inventario (RF-INV-001).
 * Gestiona las existencias actuales filtrando siempre por negocio_id.
 */
@Repository
public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {

    /** Buscar stock por ID y negocio (Seguridad multi-tenant) */
    Optional<StockInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar todo el stock de un negocio */
    List<StockInventario> findByNegocioId(Long negocioId);

    /** Listar stock de un producto en todos los almacenes del negocio */
    List<StockInventario> findByNegocioIdAndProductoId(Long negocioId, Long productoId);

    /** Buscar el registro de stock específico de un producto en un almacén determinado */
    Optional<StockInventario> findByNegocioIdAndProductoIdAndAlmacenId(
            Long negocioId, Long productoId, Long almacenId);

    /** Listar existencias de un almacén específico */
    List<StockInventario> findByNegocioIdAndAlmacenId(Long negocioId, Long almacenId);

    /** * Consulta para detectar stock bajo comparando con el stock_minimo definido en Producto.
     * Se usa Native Query para facilitar el JOIN con la tabla de productos de MySQL.
     */
    @Query(value = "SELECT si.* FROM stock_inventario si " +
            "INNER JOIN productos p ON si.producto_id = p.id " +
            "WHERE si.negocio_id = :negocioId " +
            "AND si.cantidad_en_mano <= p.stock_minimo " +
            "AND p.esta_activo = 1", 
            nativeQuery = true)
    List<StockInventario> findStockBajo(@Param("negocioId") Long negocioId);
}

# 16avo Caso

# Mi Ahora esto:

package DrinkGo.DrinkGo_backend.repository;



import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



import java.util.List;

import java.util.Optional;



<<<<<<< feature/Bl5

/**

 * Repositorio para transferencias entre almacenes (RF-INV-005).

 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).

 */

@Repository

public interface TransferenciaInventarioRepository extends JpaRepository<TransferenciaInventario, Long> {



    /** Listar todas las transferencias de un negocio */

    List<TransferenciaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);



    /** Buscar transferencia por ID y negocio */

    Optional<TransferenciaInventario> findByIdAndNegocioId(Long id, Long negocioId);



    /** Verificar existencia de número de transferencia en el negocio */

    boolean existsByNumeroTransferenciaAndNegocioId(String numeroTransferencia, Long negocioId);



    /** Listar transferencias por estado */

    List<TransferenciaInventario> findByNegocioIdAndEstadoOrderByCreadoEnDesc(

            Long negocioId, TransferenciaInventario.EstadoTransferencia estado);

=======

@Repository

# Respuesta Gemini:

package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario;
import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario.TransferenciaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para transferencias entre almacenes (RF-INV-005).
 * Mantiene la trazabilidad de documentos de movimiento de stock.
 */
@Repository
public interface TransferenciaInventarioRepository extends JpaRepository<TransferenciaInventario, Long> {

    /** Listar todas las transferencias de un negocio ordenadas por la más reciente */
    List<TransferenciaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Buscar transferencia por ID y negocio (Seguridad multi-tenant) */
    Optional<TransferenciaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** * Listar transferencias filtrando por su estado (ej: borrador, en_transito, recibida).
     */
    List<TransferenciaInventario> findByNegocioIdAndEstadoOrderByCreadoEnDesc(
            Long negocioId, TransferenciaEstado estado);

    /** * Verificar si ya existe el número de documento de transferencia en el negocio.
     * Útil para validaciones antes de guardar.
     */
    boolean existsByNegocioIdAndNumeroTransferencia(Long negocioId, String numeroTransferencia);

    /** Conteo total de transferencias realizadas por el negocio */
    long countByNegocioId(Long negocioId);
}

# 17avo Caso:

# Mi ahora esto:

package DrinkGo.DrinkGo_backend.service;



<<<<<<< feature/Bl5

import DrinkGo.DrinkGo_backend.dto.AlertaInventarioResponse;

import DrinkGo.DrinkGo_backend.entity.*;

import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;

import DrinkGo.DrinkGo_backend.repository.*;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import java.math.BigDecimal;

import java.time.LocalDate;

import java.time.LocalDateTime;

import java.util.List;

import java.util.Optional;

import java.util.stream.Collectors;



/**

 * Servicio para alertas de inventario (RF-INV-004..005).

 * Genera automáticamente alertas de:

 * - Stock bajo

 * - Próximo a vencer

 * - Vencido

 * - Sobrestock

 * - Punto de reorden

=======

import DrinkGo.DrinkGo_backend.dto.AlertaCreateRequest;

import DrinkGo.DrinkGo_backend.dto.AlertaUpdateRequest;

import DrinkGo.DrinkGo_backend.entity.AlertaInventario;

import DrinkGo.DrinkGo_backend.entity.AlertaInventario.TipoAlerta;

import DrinkGo.DrinkGo_backend.entity.LoteInventario;

import DrinkGo.DrinkGo_backend.entity.StockInventario;

import DrinkGo.DrinkGo_backend.repository.AlertaInventarioRepository;

import DrinkGo.DrinkGo_backend.repository.LoteInventarioRepository;

import DrinkGo.DrinkGo_backend.repository.StockInventarioRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDate;

import java.time.LocalDateTime;

import java.util.List;



/**

 * Servicio de alertas de inventario - Bloque 5.

 * Implementa RF-INV-004 y RF-INV-005:

 * - Alertas de stock bajo

 * - Alertas de productos próximos a vencer

 * - Alertas de productos vencidos

>>>>>>> master

 */

@Service

public class AlertaInventarioService {



<<<<<<< feature/Bl5

    private final AlertaInventarioRepository alertaRepository;

    private final StockInventarioRepository stockRepository;

    private final LoteInventarioRepository loteRepository;

    private final MovimientoInventarioRepository movimientoRepository;



    public AlertaInventarioService(AlertaInventarioRepository alertaRepository,

                                    StockInventarioRepository stockRepository,

                                    LoteInventarioRepository loteRepository,

                                    MovimientoInventarioRepository movimientoRepository) {

        this.alertaRepository = alertaRepository;

        this.stockRepository = stockRepository;

        this.loteRepository = loteRepository;

        this.movimientoRepository = movimientoRepository;

    }



    /** Listar todas las alertas del negocio */

    public List<AlertaInventarioResponse> listar(Long negocioId) {

        return alertaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId).stream()

                .map(this::convertirAResponse)

                .collect(Collectors.toList());

    }



    /** Listar alertas activas (no resueltas) */

    public List<AlertaInventarioResponse> listarActivas(Long negocioId) {

        return alertaRepository.findByNegocioIdAndEstaResueltaFalseOrderByCreadoEnDesc(negocioId).stream()

                .map(this::convertirAResponse)

                .collect(Collectors.toList());

    }



    /** Obtener alerta por ID */

    public AlertaInventarioResponse obtenerPorId(Long id, Long negocioId) {

        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(id, negocioId)

                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta de inventario", id));

        return convertirAResponse(alerta);

    }



    /** Resolver alerta (borrado lógico: esta_resuelta = true) */

    @Transactional

    public void resolver(Long id, Long negocioId, Long usuarioId) {

        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(id, negocioId)

                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta de inventario", id));



        alerta.setEstaResuelta(true);

        alerta.setResueltaEn(LocalDateTime.now());

        alerta.setResueltaPor(usuarioId);

        alertaRepository.save(alerta);

    }



    /**

     * Verificar y generar alertas automáticas para un producto en un almacén.

     * Se ejecuta después de cada movimiento de inventario.

     */

    @Transactional

    public void verificarAlertas(Long negocioId, Producto producto, Almacen almacen) {

        // 1. Verificar stock bajo

        verificarStockBajo(negocioId, producto, almacen);



        // 2. Verificar sobrestock

        verificarSobrestock(negocioId, producto, almacen);



        // 3. Verificar punto de reorden

        verificarPuntoReorden(negocioId, producto, almacen);



        // 4. Verificar lotes próximos a vencer

        verificarProximoAVencer(negocioId, producto);



        // 5. Verificar lotes vencidos

        verificarVencidos(negocioId, producto);

    }



    // ── Verificaciones de alertas ──



    private void verificarStockBajo(Long negocioId, Producto producto, Almacen almacen) {

        if (producto.getStockMinimo() == null || producto.getStockMinimo() <= 0) return;



        Optional<StockInventario> stockOpt = stockRepository

                .findByProductoIdAndAlmacenIdAndNegocioId(producto.getId(), almacen.getId(), negocioId);



        if (stockOpt.isPresent()) {

            int stockActual = stockOpt.get().getCantidadEnMano();

            if (stockActual <= producto.getStockMinimo()) {

                crearAlertaSiNoExiste(negocioId, producto, almacen,

                        AlertaInventario.TipoAlerta.stock_bajo,

                        "Stock bajo para '" + producto.getNombre() + "' en almacén '" +

                        almacen.getNombre() + "'. Actual: " + stockActual +

                        ", Mínimo: " + producto.getStockMinimo(),

                        producto.getStockMinimo(), stockActual);

            }

        }

    }



    private void verificarSobrestock(Long negocioId, Producto producto, Almacen almacen) {

        if (producto.getStockMaximo() == null || producto.getStockMaximo() <= 0) return;



        Optional<StockInventario> stockOpt = stockRepository

                .findByProductoIdAndAlmacenIdAndNegocioId(producto.getId(), almacen.getId(), negocioId);



        if (stockOpt.isPresent()) {

            int stockActual = stockOpt.get().getCantidadEnMano();

            if (stockActual > producto.getStockMaximo()) {

                crearAlertaSiNoExiste(negocioId, producto, almacen,

                        AlertaInventario.TipoAlerta.sobrestock,

                        "Sobrestock para '" + producto.getNombre() + "' en almacén '" +

                        almacen.getNombre() + "'. Actual: " + stockActual +

                        ", Máximo: " + producto.getStockMaximo(),

                        producto.getStockMaximo(), stockActual);

            }

        }

    }



    private void verificarPuntoReorden(Long negocioId, Producto producto, Almacen almacen) {

        if (producto.getPuntoReorden() == null || producto.getPuntoReorden() <= 0) return;



        Optional<StockInventario> stockOpt = stockRepository

                .findByProductoIdAndAlmacenIdAndNegocioId(producto.getId(), almacen.getId(), negocioId);



        if (stockOpt.isPresent()) {

            int stockActual = stockOpt.get().getCantidadEnMano();

            if (stockActual <= producto.getPuntoReorden()) {

                crearAlertaSiNoExiste(negocioId, producto, almacen,

                        AlertaInventario.TipoAlerta.punto_reorden,

                        "Punto de reorden alcanzado para '" + producto.getNombre() +

                        "'. Actual: " + stockActual + ", Reorden: " + producto.getPuntoReorden(),

                        producto.getPuntoReorden(), stockActual);

            }

        }

    }



    private void verificarProximoAVencer(Long negocioId, Producto producto) {

        LocalDate hoy = LocalDate.now();

        LocalDate limite = hoy.plusDays(30); // Alertar 30 días antes del vencimiento



        List<LoteInventario> lotesProximos = loteRepository

                .findByNegocioIdAndEstadoAndFechaVencimientoBetween(

                        negocioId, LoteInventario.EstadoLote.disponible, hoy, limite);



        for (LoteInventario lote : lotesProximos) {

            if (lote.getProductoId().equals(producto.getId())) {

                // No necesitamos el almacén para alertas de vencimiento

                crearAlertaSiNoExiste(negocioId, producto, null,

                        AlertaInventario.TipoAlerta.proximo_vencer,

                        "Lote '" + lote.getNumeroLote() + "' del producto '" +

                        producto.getNombre() + "' próximo a vencer el " +

                        lote.getFechaVencimiento() + ". Cantidad restante: " + lote.getCantidadRestante(),

                        null, lote.getCantidadRestante());

            }

        }

    }



    private void verificarVencidos(Long negocioId, Producto producto) {

        LocalDate hoy = LocalDate.now();



        List<LoteInventario> lotesVencidos = loteRepository

                .findByNegocioIdAndEstadoAndFechaVencimientoBefore(

                        negocioId, LoteInventario.EstadoLote.disponible, hoy);



        for (LoteInventario lote : lotesVencidos) {

            if (lote.getProductoId().equals(producto.getId())) {

                int cantidadRestante = lote.getCantidadRestante();



                // BUG-3 FIX: Descontar cantidadRestante del stock antes de marcar como vencido

                if (cantidadRestante > 0) {

                    Optional<StockInventario> stockOpt = stockRepository

                            .findByProductoIdAndAlmacenIdAndNegocioId(

                                    lote.getProductoId(), lote.getAlmacenId(), negocioId);



                    if (stockOpt.isPresent()) {

                        StockInventario stock = stockOpt.get();

                        int nuevoStock = stock.getCantidadEnMano() - cantidadRestante;

                        stock.setCantidadEnMano(Math.max(nuevoStock, 0));

                        stock.setUltimoMovimientoEn(LocalDateTime.now());

                        stockRepository.save(stock);

                    }



                    // Registrar movimiento de vencimiento

                    Almacen almacenLote = lote.getAlmacen();

                    MovimientoInventario mov = new MovimientoInventario();

                    mov.setNegocioId(negocioId);

                    mov.setProducto(producto);

                    mov.setAlmacen(almacenLote);

                    mov.setLote(lote);

                    mov.setTipoMovimiento(MovimientoInventario.TipoMovimiento.vencimiento);

                    mov.setCantidad(cantidadRestante);

                    mov.setCostoUnitario(lote.getPrecioCompra());

                    mov.setTipoReferencia("lote_vencido");

                    mov.setReferenciaId(lote.getId());

                    mov.setMotivo("Lote '" + lote.getNumeroLote() + "' vencido el " + lote.getFechaVencimiento());

                    mov.setCreadoEn(LocalDateTime.now());

                    movimientoRepository.save(mov);

                }



                // Marcar lote como vencido y poner cantidad en 0

                lote.setEstado(LoteInventario.EstadoLote.vencido);

                lote.setCantidadRestante(0);

                loteRepository.save(lote);



                crearAlertaSiNoExiste(negocioId, producto, null,

                        AlertaInventario.TipoAlerta.vencido,

                        "Lote '" + lote.getNumeroLote() + "' del producto '" +

                        producto.getNombre() + "' VENCIDO desde " +

                        lote.getFechaVencimiento() + ". Cantidad descartada: " + cantidadRestante,

                        null, cantidadRestante);

            }

        }

    }



    private void crearAlertaSiNoExiste(Long negocioId, Producto producto, Almacen almacen,

                                        AlertaInventario.TipoAlerta tipo, String mensaje,

                                        Integer valorUmbral, Integer valorActual) {

        Long almacenId = almacen != null ? almacen.getId() : null;



        boolean yaExiste = alertaRepository

                .existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResueltaFalse(

                        negocioId, producto.getId(), almacenId, tipo);

=======

    @Autowired

    private AlertaInventarioRepository alertaRepository;



    @Autowired

    private StockInventarioRepository stockRepository;



    @Autowired

    private LoteInventarioRepository loteRepository;



    // Días de anticipación para alertas de próximo a vencer

    private static final int DIAS_ALERTA_VENCIMIENTO = 30;



    /**

     * Listar todas las alertas del negocio.

     */

    public List<AlertaInventario> listarAlertas(Long negocioId) {

        return alertaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId);

    }



    /**

     * Listar alertas activas (no resueltas).

     */

    public List<AlertaInventario> listarAlertasActivas(Long negocioId) {

        return alertaRepository.findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(negocioId, false);

    }



    /**

     * Listar alertas por tipo.

     */

    public List<AlertaInventario> listarAlertasPorTipo(Long negocioId, TipoAlerta tipo) {

        return alertaRepository.findByNegocioIdAndTipoAlertaOrderByCreadoEnDesc(negocioId, tipo);

    }



    /**

     * Crear una alerta de inventario manualmente (POST).

     */

    @Transactional

    public AlertaInventario crearAlerta(Long negocioId, AlertaCreateRequest request) {

        if (request.getProductoId() == null) {

            throw new RuntimeException("El campo 'productoId' es obligatorio");

        }

        if (request.getTipoAlerta() == null || request.getTipoAlerta().isBlank()) {

            throw new RuntimeException("El campo 'tipoAlerta' es obligatorio");

        }

        if (request.getMensaje() == null || request.getMensaje().isBlank()) {

            throw new RuntimeException("El campo 'mensaje' es obligatorio");

        }



        TipoAlerta tipo;

        try {

            tipo = TipoAlerta.valueOf(request.getTipoAlerta());

        } catch (IllegalArgumentException e) {

            throw new RuntimeException("Tipo de alerta inválido: " + request.getTipoAlerta() +

                    ". Valores válidos: stock_bajo, sobrestock, proximo_vencer, vencido, punto_reorden");

        }



        AlertaInventario alerta = new AlertaInventario();

        alerta.setNegocioId(negocioId);

        alerta.setProductoId(request.getProductoId());

        alerta.setAlmacenId(request.getAlmacenId());

        alerta.setTipoAlerta(tipo);

        alerta.setMensaje(request.getMensaje());

        alerta.setValorUmbral(request.getValorUmbral());

        alerta.setValorActual(request.getValorActual());

        alerta.setEstaResuelta(false);



        return alertaRepository.save(alerta);

    }



    /**

     * Resolver (marcar como resuelta) una alerta.

     */

    @Transactional

    public AlertaInventario resolverAlerta(Long negocioId, Long alertaId) {

        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)

                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertaId));



        if (alerta.getEstaResuelta()) {

            throw new RuntimeException("La alerta ya está resuelta");

        }



        alerta.setEstaResuelta(true);

        alerta.setResueltaEn(LocalDateTime.now());

        return alertaRepository.save(alerta);

    }



    /**

     * Actualizar una alerta de inventario.

     */

    @Transactional

    public AlertaInventario actualizarAlerta(Long negocioId, Long alertaId, AlertaUpdateRequest request) {

        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)

                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertaId));



        if (request.getMensaje() != null) {

            alerta.setMensaje(request.getMensaje());

        }

        if (request.getValorUmbral() != null) {

            alerta.setValorUmbral(request.getValorUmbral());

        }

        if (request.getValorActual() != null) {

            alerta.setValorActual(request.getValorActual());

        }

        if (request.getEstaResuelta() != null) {

            alerta.setEstaResuelta(request.getEstaResuelta());

            if (request.getEstaResuelta()) {

                alerta.setResueltaEn(LocalDateTime.now());

            }

        }



        return alertaRepository.save(alerta);

    }



    /**

     * Eliminar alerta (borrado lógico con @SQLDelete).

     */

    @Transactional

    public void eliminarAlerta(Long negocioId, Long alertaId) {

        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)

                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertaId));



        alertaRepository.delete(alerta);

    }



    /**

     * Verificar y generar alertas para un producto específico en un almacén.

     * Se llama automáticamente después de cada movimiento de inventario.

     */

    @Transactional

    public void verificarAler

# Respuesta gemini:

package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.AlertaCreateRequest;
import DrinkGo.DrinkGo_backend.dto.AlertaInventarioResponse;
import DrinkGo.DrinkGo_backend.dto.AlertaUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para alertas de inventario (RF-INV-004..005).
 * Detecta automáticamente stock bajo, productos próximos a vencer y vencidos.
 */
@Service
public class AlertaInventarioService {

    @Autowired
    private AlertaInventarioRepository alertaRepository;

    @Autowired
    private StockInventarioRepository stockRepository;

    @Autowired
    private LoteInventarioRepository loteRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    private static final int DIAS_ALERTA_VENCIMIENTO = 30;

    // ============================================================
    // CONSULTAS Y CRUD
    // ============================================================

    public List<AlertaInventarioResponse> listar(Long negocioId) {
        return alertaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<AlertaInventarioResponse> listarActivas(Long negocioId) {
        return alertaRepository.findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(negocioId, false).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void resolver(Long id, Long negocioId, Long usuarioId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        alerta.setEstaResuelta(true);
        alerta.setResueltaEn(LocalDateTime.now());
        alerta.setResueltaPor(usuarioId);
        alertaRepository.save(alerta);
    }

    @Transactional
    public void eliminarAlerta(Long negocioId, Long alertaId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alertaRepository.delete(alerta);
    }

    // ============================================================
    // LÓGICA DE VERIFICACIÓN AUTOMÁTICA
    // ============================================================

    /**
     * Verifica y genera alertas automáticas para un producto.
     * Se debe llamar después de cada entrada o salida de stock.
     */
    @Transactional
    public void verificarAlertas(Long negocioId, Producto producto, Almacen almacen) {
        verificarStockBajo(negocioId, producto, almacen);
        verificarProximoAVencer(negocioId, producto);
        verificarVencidos(negocioId, producto);
    }

    private void verificarStockBajo(Long negocioId, Producto producto, Almacen almacen) {
        if (producto.getStockMinimo() == null) return;

        Optional<StockInventario> stockOpt = stockRepository
                .findByNegocioIdAndProductoIdAndAlmacenId(negocioId, producto.getId(), almacen.getId());

        if (stockOpt.isPresent()) {
            int stockActual = stockOpt.get().getCantidadEnMano();
            if (stockActual <= producto.getStockMinimo()) {
                crearAlertaSiNoExiste(negocioId, producto, almacen,
                        AlertaInventario.TipoAlerta.stock_bajo,
                        "Stock bajo para '" + producto.getNombre() + "' en " + almacen.getNombre() + 
                        ". Actual: " + stockActual + ", Mínimo: " + producto.getStockMinimo(),
                        producto.getStockMinimo(), stockActual);
            }
        }
    }

    private void verificarProximoAVencer(Long negocioId, Producto producto) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(DIAS_ALERTA_VENCIMIENTO);

        List<LoteInventario> lotesProximos = loteRepository.findProximosAVencer(
                negocioId, LoteInventario.LoteEstado.disponible, hoy, limite);

        for (LoteInventario lote : lotesProximos) {
            if (lote.getProductoId().equals(producto.getId())) {
                long dias = ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento());
                crearAlertaSiNoExiste(negocioId, producto, lote.getAlmacen(),
                        AlertaInventario.TipoAlerta.proximo_vencer,
                        "Lote '" + lote.getNumeroLote() + "' vence en " + dias + " días.",
                        DIAS_ALERTA_VENCIMIENTO, (int) dias);
            }
        }
    }

    private void verificarVencidos(Long negocioId, Producto producto) {
        LocalDate hoy = LocalDate.now();
        List<LoteInventario> vencidos = loteRepository.findByNegocioIdAndEstadoAndFechaVencimientoBefore(
                negocioId, LoteInventario.LoteEstado.disponible, hoy);

        for (LoteInventario lote : vencidos) {
            if (lote.getProductoId().equals(producto.getId())) {
                crearAlertaSiNoExiste(negocioId, producto, lote.getAlmacen(),
                        AlertaInventario.TipoAlerta.vencido,
                        "¡Lote '" + lote.getNumeroLote() + "' VENCIDO!",
                        null, lote.getCantidadRestante());
            }
        }
    }

    private void crearAlertaSiNoExiste(Long negocioId, Producto producto, Almacen almacen,
                                      AlertaInventario.TipoAlerta tipo, String mensaje,
                                      Integer umbral, Integer actual) {
        Long almacenId = (almacen != null) ? almacen.getId() : null;
        boolean yaExiste = alertaRepository.existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResuelta(
                negocioId, producto.getId(), almacenId, tipo, false);

        if (!yaExiste) {
            AlertaInventario alerta = new AlertaInventario();
            alerta.setNegocioId(negocioId);
            alerta.setProducto(producto);
            alerta.setAlmacen(almacen);
            alerta.setTipoAlerta(tipo);
            alerta.setMensaje(mensaje);
            alerta.setValorUmbral(umbral);
            alerta.setValorActual(actual);
            alerta.setEstaResuelta(false);
            alertaRepository.save(alerta);
        }
    }

    private AlertaInventarioResponse convertirAResponse(AlertaInventario alerta) {
        AlertaInventarioResponse resp = new AlertaInventarioResponse();
        resp.setId(alerta.getId());
        resp.setNegocioId(alerta.getNegocioId());
        resp.setProductoId(alerta.getProductoId());
        resp.setProductoNombre(alerta.getProducto() != null ? alerta.getProducto().getNombre() : "N/A");
        resp.setAlmacenId(alerta.getAlmacenId());
        resp.setAlmacenNombre(alerta.getAlmacen() != null ? alerta.getAlmacen().getNombre() : "N/A");
        resp.setTipoAlerta(alerta.getTipoAlerta().name());
        resp.setMensaje(alerta.getMensaje());
        resp.setValorUmbral(alerta.getValorUmbral());
        resp.setValorActual(alerta.getValorActual());
        resp.setEstaResuelta(alerta.getEstaResuelta());
        resp.setResueltaEn(alerta.getResueltaEn());
        resp.setResueltaPor(alerta.getResueltaPor());
        resp.setCreadoEn(alerta.getCreadoEn());
        return resp;
    }
}