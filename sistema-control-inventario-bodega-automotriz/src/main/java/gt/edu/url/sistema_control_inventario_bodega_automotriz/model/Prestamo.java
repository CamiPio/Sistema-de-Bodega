package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPresamo;

    @NotBlank(message = "La fecha es obligatoria.")
    @Column(nullable = false)
    private String fecha;

    @NotNull(message = "El ID del producto es obligatorio.")
    @Column(nullable = false)
    private Integer idProducto; // <--- Cambiado de int a Integer

    @NotBlank(message = "El nombre del solicitante es obligatorio.")
    @Column(nullable = false)
    private String nombreSolicitante;

    @NotNull(message = "El estado activo es obligatorio.")
    @Column(nullable = false)
    private boolean estadoActivo;

    private String observacionEntrega;

    // Constructor vacío
    public Prestamo() {
    }

    // Constructor completo (con Integer en idProducto)
    public Prestamo(int idPresamo, String fecha, Integer idProducto, String nombreSolicitante, boolean estadoActivo, String observacionEntrega) {
        this.idPresamo = idPresamo;
        this.fecha = fecha;
        this.idProducto = idProducto;
        this.nombreSolicitante = nombreSolicitante;
        this.estadoActivo = estadoActivo;
        this.observacionEntrega = observacionEntrega;
    }

    // Getters y Setters
    public int getIdPresamo() {
        return idPresamo;
    }

    public void setIdPresamo(int idPresamo) {
        this.idPresamo = idPresamo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getIdProducto() { // <--- Retorna Integer
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) { // <--- Recibe Integer
        this.idProducto = idProducto;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public boolean isEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

    public String getObservacionEntrega() {
        return observacionEntrega;
    }

    public void setObservacionEntrega(String observacionEntrega) {
        this.observacionEntrega = observacionEntrega;
    }
}