package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.RolUsuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repositorio;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void configurar() {
        when(repositorio.count()).thenReturn(1L);
        when(repositorio.findAll()).thenReturn(List.of());
        usuarioService = new UsuarioService(repositorio, passwordEncoder);
    }

    @Test
    void registrarGuardaLaContrasenaComoHashYAsignaRolUsuario() {
        Usuario usuario = new Usuario();
        usuario.setContrasena("clave-segura");
        when(passwordEncoder.encode("clave-segura")).thenReturn("$2a$hash");
        when(repositorio.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Usuario guardado = usuarioService.registrar(usuario);

        assertEquals("$2a$hash", guardado.getContrasena());
        assertEquals(RolUsuario.USUARIO, guardado.getRol());
        verify(passwordEncoder).encode("clave-segura");
    }

    @Test
    void loginVerificaHashBcrypt() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("persona@ejemplo.com");
        usuario.setContrasena("$2a$hash");
        when(repositorio.findByCorreoIgnoreCase("persona@ejemplo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave-segura", "$2a$hash")).thenReturn(true);

        assertSame(usuario, usuarioService.validarLogin("persona@ejemplo.com", "clave-segura"));
        verify(passwordEncoder).matches("clave-segura", "$2a$hash");
    }

    @Test
    void loginActualizaCredencialHeredadaEnTextoPlano() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("persona@ejemplo.com");
        usuario.setContrasena("clave-antigua");
        when(repositorio.findByCorreoIgnoreCase("persona@ejemplo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("clave-antigua")).thenReturn("$2a$hash-nuevo");
        when(repositorio.save(usuario)).thenReturn(usuario);

        Usuario autenticado = usuarioService.validarLogin("persona@ejemplo.com", "clave-antigua");

        assertSame(usuario, autenticado);
        assertTrue(autenticado.getContrasena().startsWith("$2a$"));
    }

    @Test
    void actualizarSinContrasenaConservaHashYRol() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Nombre anterior");
        usuario.setContrasena("$2a$hash-existente");
        usuario.setRol(RolUsuario.ADMIN);
        when(repositorio.findById(7L)).thenReturn(Optional.of(usuario));
        when(repositorio.save(usuario)).thenReturn(usuario);

        Usuario actualizado = usuarioService.actualizar(7L, "Nuevo", "Apellido", "nuevo@ejemplo.com", "");

        assertEquals("$2a$hash-existente", actualizado.getContrasena());
        assertEquals(RolUsuario.ADMIN, actualizado.getRol());
        verify(passwordEncoder, never()).encode(any());
    }
}