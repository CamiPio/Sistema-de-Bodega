package gt.edu.url.sistema_control_inventario_bodega_automotriz.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.RolUsuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.UsuarioRepository;

@Component
public class CargadorUsuarios implements CommandLineRunner {

    private static final String CORREO_ADMIN = "admin@bodega.com";
    private static final String CONTRASENA_ADMIN = "123456789012";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CargadorUsuarios(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByCorreoIgnoreCase(CORREO_ADMIN).isEmpty()) {
            Usuario u = new Usuario();
            u.setCorreo(CORREO_ADMIN);
            u.setContrasena(passwordEncoder.encode(CONTRASENA_ADMIN));
            u.setNombre("Administrador");
            u.setApellido("Sistema");
            u.setRol(RolUsuario.ADMIN);
            usuarioRepository.save(u);
        }
    }
}