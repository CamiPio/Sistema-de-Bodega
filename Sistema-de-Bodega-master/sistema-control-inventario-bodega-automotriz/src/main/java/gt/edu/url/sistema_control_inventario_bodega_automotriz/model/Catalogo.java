package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity 
public class Catalogo {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCatalogo;

    @NotBlank(message = "El nombre es obligatorio.")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria.")
    @Column(nullable = false)
    private String categoria;

    private String descripcion;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 0, message = "La cantidad no puede ser negativa.")
    @Column(nullable = false)
    private Integer cantidad;

    @NotBlank(message = "La disponibilidad es obligatoria.")
    @Column(nullable = false)
    private String disponibilidad;

    private String observacion;  
    
    // Constructor vacío
    public Catalogo() {
    }

    // Constructor completo
    public Catalogo(Long idCatalogo, String nombre, String categoria, String descripcion, Integer cantidad, String disponibilidad, String observacion) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.disponibilidad = disponibilidad;
        this.observacion = observacion;
    }

    public Long getId() {
        return idCatalogo;
    }

    public void setId(Long idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
