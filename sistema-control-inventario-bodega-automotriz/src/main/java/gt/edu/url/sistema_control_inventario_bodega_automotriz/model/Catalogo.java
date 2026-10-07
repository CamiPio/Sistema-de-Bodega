package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

public class Catalogo {
    private String nombre;
    private String categoria;
    private String descripcion;
    private Integer cantidad;
    private String disponibilidad;
    private String observacion;

    // Constructor vacío
    public Catalogo() {
    }

    // Constructor completo
    public Catalogo(String nombre, String categoria, String descripcion, Integer cantidad, String disponibilidad, String observacion) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.disponibilidad = disponibilidad;
        this.observacion = observacion;
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
