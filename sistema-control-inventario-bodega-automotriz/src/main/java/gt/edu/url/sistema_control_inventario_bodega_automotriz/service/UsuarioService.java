package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;

@Service
public class UsuarioService {

    // Memoria temporal para almacenar los usuarios registrados
    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioService() {
        // (Opcional) Agregamos un usuario por defecto para pruebas rápidas de login
        usuarios.add(new Usuario("Admin", "General", "admin@bodega.com", "123456789012"));
    }

    // Registrar un nuevo usuario
    public void guardar(Usuario usuario) {
        usuarios.add(usuario);
    }

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarios;
    }

    // Validar si las credenciales de inicio de sesión son correctas
    public Usuario validarLogin(String correo, String contrasena) {
        for (Usuario user : usuarios) {
            if (user.getCorreo().equalsIgnoreCase(correo) && user.getContrasena().equals(contrasena)) {
                return user; // Retorna el usuario si coincide
            }
        }
        return null; 
    }
}
