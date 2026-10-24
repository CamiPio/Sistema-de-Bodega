package gt.edu.url.sistema_control_inventario_bodega_automotriz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UsuarioEdicionForm {

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Escribe un correo válido.")
    private String correo;

    @Pattern(regexp = "^$|.{6,}", message = "La contraseña debe tener al menos 6 caracteres.")
    private String contrasena;

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