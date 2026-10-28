package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

import java.time.LocalDate;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.converter.LocalDateStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPresamo;

    @NotNull(message = "La fecha es obligatoria.")
    @PastOrPresent(message = "La fecha del préstamo no puede ser futura.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateStringConverter.class)
    @Column(nullable = false, length = 10)
    private LocalDate fecha;

    @NotNull(message = "El ID del producto es obligatorio.")
    @Positive(message = "Selecciona una herramienta válida.")
    @Column(nullable = false)
    private Long idProducto;

    @Column(name = "usuario_id")
    private Long idUsuario;

    @NotBlank(message = "El nombre del solicitante es obligatorio.")
    @Size(max = 120, message = "El nombre del solicitante no puede exceder 120 caracteres.")
    @Column(nullable = false, length = 120)
    private String nombreSolicitante;

    @Column(nullable = false)
    private boolean estadoActivo;

    private Boolean estadoPendiente;

    private Boolean estadoRechazado;

    @Size(max = 500, message = "La observación no puede exceder 500 caracteres.")
    @Column(length = 500)
    private String observacionEntrega;

    @Size(max = 500, message = "La observación de devolución no puede exceder 500 caracteres.")
    @Column(length = 500)
    private String observacionDevolucion;

    // Constructor vacío
    public Prestamo() {
    }

    // Constructor completo
    public Prestamo(Long idPresamo, LocalDate fecha, Long idProducto, String nombreSolicitante, boolean estadoActivo, String observacionEntrega) {
        this.idPresamo = idPresamo;
        this.fecha = fecha;
        this.idProducto = idProducto;
        this.nombreSolicitante = nombreSolicitante;
        this.estadoActivo = estadoActivo;
        this.observacionEntrega = observacionEntrega;
    }

    // Getters y Setters
    public Long getIdPresamo() {
        return idPresamo;
    }

    public void setIdPresamo(Long idPresamo) {
        this.idPresamo = idPresamo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    public boolean isEstadoPendiente() {
        return Boolean.TRUE.equals(estadoPendiente);
    }

    public void setEstadoPendiente(boolean estadoPendiente) {
        this.estadoPendiente = estadoPendiente;
    }

    public boolean isEstadoRechazado() {
        return Boolean.TRUE.equals(estadoRechazado);
    }

    public void setEstadoRechazado(boolean estadoRechazado) {
        this.estadoRechazado = estadoRechazado;
    }

    public String getObservacionEntrega() {
        return observacionEntrega;
    }

    public void setObservacionEntrega(String observacionEntrega) {
        this.observacionEntrega = observacionEntrega;
    }

    public String getObservacionDevolucion() {
        return observacionDevolucion;
    }

    public void setObservacionDevolucion(String observacionDevolucion) {
        this.observacionDevolucion = observacionDevolucion;
    }
}