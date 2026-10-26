package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.RolUsuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repositorio, PasswordEncoder passwordEncoder) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;

        for (Usuario usuario : repositorio.findAll()) {
            boolean necesitaGuardar = false;
            if (usuario.getRol() == null) {
                usuario.setRol("admin@bodega.com".equalsIgnoreCase(usuario.getCorreo())
                        ? RolUsuario.ADMIN
                        : RolUsuario.USUARIO);
                necesitaGuardar = true;
            }
            if (usuario.getContrasena() != null && !esHashBcrypt(usuario.getContrasena())) {
                usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                necesitaGuardar = true;
            }
            if (necesitaGuardar) {
                repositorio.save(usuario);
            }
        }
    }

    public List<Usuario> listarTodos() {
        return repositorio.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con id " + id));
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return repositorio.findByCorreoIgnoreCase(correo);
    }

    // Buscar usuario por nombre (retorna una lista por si hay coincidencias parciales)
    public List<Usuario> buscarPorNombre(String nombre) {
        return repositorio.findByNombreContainingIgnoreCase(nombre);
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getRol() == null) {
            usuario.setRol(RolUsuario.USUARIO);
        }
        if (!esHashBcrypt(usuario.getContrasena())) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        try {
            return repositorio.save(usuario);
        } catch (DataIntegrityViolationException e) {
            // Capturamos el error de unicidad (correo duplicado) y lanzamos un mensaje amigable
            throw new RuntimeException("El correo electrónico '" + usuario.getCorreo() + "' ya se encuentra registrado en el sistema.", e);
        }
    }

    public Usuario registrar(Usuario usuario) {
        usuario.setRol(RolUsuario.USUARIO);
        return guardar(usuario);
    }

    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getRol() == RolUsuario.ADMIN
                && repositorio.countByRol(RolUsuario.ADMIN) <= 1) {
            throw new IllegalStateException("No se puede eliminar al último administrador.");
        }
        repositorio.deleteById(id);
    }

    public Usuario actualizar(Long id, String nombre, String apellido, String correo, String contrasena) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        if (contrasena != null && !contrasena.isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(contrasena));
        }
        try {
            return repositorio.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("El correo electrónico '" + correo + "' ya se encuentra registrado en el sistema.", e);
        }
    }

    public void actualizarRol(Long id, RolUsuario rol) {
        Usuario usuario = buscarPorId(id);
        if (rol == RolUsuario.USUARIO && usuario.getRol() == RolUsuario.ADMIN
                && repositorio.countByRol(RolUsuario.ADMIN) <= 1) {
            throw new IllegalStateException("Debe existir al menos un administrador.");
        }
        usuario.setRol(rol);
        repositorio.save(usuario);
    }

    // Método para validar el inicio de sesión contra la base de datos
    public Usuario validarLogin(String correo, String contrasena) {
        if (correo == null || contrasena == null) {
            return null;
        }
        Usuario usuario = repositorio.findByCorreoIgnoreCase(correo).orElse(null);
        if (usuario == null) {
            return null;
        }

        String contrasenaGuardada = usuario.getContrasena();
        if (esHashBcrypt(contrasenaGuardada)) {
            return passwordEncoder.matches(contrasena, contrasenaGuardada) ? usuario : null;
        }
        if (!contrasenaGuardada.equals(contrasena)) {
            return null;
        }

        usuario.setContrasena(passwordEncoder.encode(contrasena));
        return repositorio.save(usuario);
    }

    private boolean esHashBcrypt(String valor) {
        return valor != null && (valor.startsWith("$2a$") || valor.startsWith("$2b$")
                || valor.startsWith("$2y$"));
    }
    
}