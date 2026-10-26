package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.converter.NullableIntegerStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria.")
    @Size(max = 80, message = "La categoría no puede exceder 80 caracteres.")
    @Column(nullable = false, length = 80)
    private String categoria;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres.")
    @Column(nullable = false, length = 500)
    private String descripcion;

    @NotNull(message = "La cantidad es obligatoria.")
    @PositiveOrZero(message = "La cantidad no puede ser negativa.")
    @Column(nullable = false)
    private Integer cantidad;

    @PositiveOrZero(message = "La disponibilidad no puede ser negativa.")
    @Convert(converter = NullableIntegerStringConverter.class)
    @Column(length = 10)
    private Integer disponibilidad;

    @Size(max = 500, message = "La observación no puede exceder 500 caracteres.")
    @Column(length = 500)
    private String observacion;

    // Constructor vacío
    public Catalogo() {
    }

    // Constructor completo
    public Catalogo(Long id, String nombre, String categoria, String descripcion, Integer cantidad, Integer disponibilidad, String observacion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.disponibilidad = disponibilidad;
        this.observacion = observacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Integer disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
