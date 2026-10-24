package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio) {
        this.repositorio = repositorio;

        // Si la base de datos está totalmente vacía, creamos e insertamos el admin por defecto
        if (repositorio.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("General");
            admin.setCorreo("admin@bodega.com");
            admin.setContrasena("123456789012"); // Asegúrate de cumplir las reglas de @Size en tu modelo
            repositorio.save(admin);
        }
    }

    public List<Usuario> listarTodos() {
        return repositorio.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con id " + id));
    }

    // Buscar usuario por nombre (retorna una lista por si hay coincidencias parciales)
    public List<Usuario> buscarPorNombre(String nombre) {
        return repositorio.findByNombreContainingIgnoreCase(nombre);
    }

    public Usuario guardar(Usuario usuario) {
        try {
            return repositorio.save(usuario);
        } catch (DataIntegrityViolationException e) {
            // Capturamos el error de unicidad (correo duplicado) y lanzamos un mensaje amigable
            throw new RuntimeException("El correo electrónico '" + usuario.getCorreo() + "' ya se encuentra registrado en el sistema.", e);
        }
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

    // Método para validar el inicio de sesión contra la base de datos
    public Usuario validarLogin(String correo, String contrasena) {
        List<Usuario> usuarios = repositorio.findAll();
        for (Usuario user : usuarios) {
            if (user.getCorreo().equalsIgnoreCase(correo) && user.getContrasena().equals(contrasena)) {
                return user;
            }
        }
        return null;
    }
    
}