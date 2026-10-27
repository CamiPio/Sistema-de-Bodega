package gt.edu.url.sistema_control_inventario_bodega_automotriz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Escribe un correo válido.")
    @Size(max = 254, message = "El correo no puede exceder 254 caracteres.")
    @Column(nullable = false, unique = true, length = 254)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9\\s()\\x2d´”`’])[^()\\x2d´”`’]+$",
        message = "La contraseña debe incluir minúscula, mayúscula, número y carácter especial; no puede contener (), -, ´, ”, ` ni ’."
    )
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RolUsuario rol = RolUsuario.USUARIO;

    // Constructor vacío (necesario para frameworks como Spring/Jackson)
    public Usuario() {
    }

    // Constructor con un solo parámetro (usado por BienvenidoService)
    public Usuario(String nombre) {
        this.nombre = nombre;
    }

    // Constructor completo
    public Usuario(Long idUsuario, String nombre, String apellido, String correo, String contrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public Long getId() {
        return idUsuario;
    }

    public void setId(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
