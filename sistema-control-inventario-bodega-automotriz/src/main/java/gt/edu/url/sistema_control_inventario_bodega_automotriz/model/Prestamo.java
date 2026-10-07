package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

public class Prestamo {
    private int idPresamo;
    private String fecha;
    private int idProducto;
    private String nombreSolicitante;
    private boolean estadoActivo;
    private String observacionEntrega;

    // Constructor vacío
    public Prestamo() {
    }

    // Constructor completo
    public Prestamo(int idPresamo, String fecha, int idProducto, String nombreSolicitante, boolean estadoActivo, String observacionEntrega) {
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

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
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
