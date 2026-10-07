package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

public class Usuario {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;

    // Constructor vacío (necesario para frameworks como Spring/Jackson)
    public Usuario() {
    }

    // Constructor con un solo parámetro (usado por BienvenidoService)
    public Usuario(String nombre) {
        this.nombre = nombre;
    }

    // Constructor completo
    public Usuario(String nombre, String apellido, String correo, String contrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
