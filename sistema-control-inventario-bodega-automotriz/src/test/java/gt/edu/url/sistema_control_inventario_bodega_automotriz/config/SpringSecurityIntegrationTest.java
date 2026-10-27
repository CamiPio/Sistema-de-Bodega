package gt.edu.url.sistema_control_inventario_bodega_automotriz.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.RolUsuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.UsuarioRepository;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:security-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class SpringSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void redirigeALoginCuandoNoHayAutenticacion() throws Exception {
        mockMvc.perform(get("/catalogo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void usuarioAutenticadoPuedeConsultarElCatalogo() throws Exception {
        mockMvc.perform(get("/catalogo").with(user("usuario@bodega.com").roles("USUARIO")))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioNoAdministradorNoPuedeConsultarLaListaDeUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios/lista").with(user("usuario@bodega.com").roles("USUARIO")))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/acceso-denegado"));
    }

    @Test
    void usuarioNoAdministradorNoPuedeAbrirElFormularioDeHerramientas() throws Exception {
        mockMvc.perform(get("/catalogo/editar/1").with(user("usuario@bodega.com").roles("USUARIO")))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/acceso-denegado"));
    }

    @Test
    void muestraLaPlantillaDeAccesoDenegado() throws Exception {
        mockMvc.perform(get("/acceso-denegado"))
                .andExpect(status().isOk())
                .andExpect(view().name("acceso-denegado"));
    }

    @Test
    void menuIncluyeAccionesDePerfilYCerrarSesion() throws Exception {
        mockMvc.perform(get("/perfil").with(user("admin@bodega.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(content().string(containsString("Editar mi perfil")))
                .andExpect(content().string(containsString("Cerrar Sesión")))
                .andExpect(content().string(containsString("barraMenu.js")))
                .andExpect(content().string(containsString("id=\"menuBtn\"")))
                .andExpect(content().string(containsString("_csrf")));
    }

    @Test
    void recursoJavaScriptDeLaBarraEsAccesible() throws Exception {
        mockMvc.perform(get("/barraMenu.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inicializarMenu")));
    }

    @Test
    void accesoYRegistroConservanSusRutasYPermitenMostrarContrasena() throws Exception {
        mockMvc.perform(get("/password-toggle.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("checkbox.checked ? 'text' : 'password'")));

        mockMvc.perform(get("/password-validation.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("faltan ${remaining}")))
                .andExpect(content().string(containsString("if (forbidden.length > 0)")));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/auth/login\"")))
                .andExpect(content().string(containsString("action=\"/auth/registrar\"")))
                .andExpect(content().string(containsString("data-password-toggle=\"contrasenaLogin\"")))
                .andExpect(content().string(containsString("data-password-toggle=\"contrasenaRegistro\"")))
                .andExpect(content().string(containsString("data-password-policy")))
                .andExpect(content().string(containsString("password-validation.js")));
    }

    @Test
    void regresarDesdeLaListaDeUsuariosUsaElFormularioDeLogout() throws Exception {
        mockMvc.perform(get("/usuarios/lista").with(user("admin@bodega.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/auth/logout\"")))
                .andExpect(content().string(containsString("Cerrar sesión y regresar al Login / Registro")))
                .andExpect(content().string(containsString("_csrf")));
    }

    @Test
    void regresarAlRegistroDesdeLaListaCierraLaSesion() throws Exception {
        var resultadoLogin = mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("correo", "admin@bodega.com")
                        .param("contrasena", "123456789012"))
                .andExpect(redirectedUrl("/catalogo"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) resultadoLogin.getRequest().getSession(false);

        mockMvc.perform(get("/usuarios/lista").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cerrar sesión y regresar al Login / Registro")));
        mockMvc.perform(post("/auth/logout").session(session).with(csrf()))
                .andExpect(redirectedUrl("/login"));

        assertTrue(session.isInvalid());
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void administradorPuedeConsultarLaListaDeUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios/lista").with(user("admin@bodega.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void procesaElFormularioDeLoginConLasCredencialesDelProyecto() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("correo", "inexistente@bodega.com")
                        .param("contrasena", "incorrecta"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void registroRechazaContrasenaSinLosRequisitosYNoCreaLaCuenta() throws Exception {
        String correo = "contrasena.invalida@bodega.com";

        mockMvc.perform(post("/auth/registrar")
                        .with(csrf())
                        .param("nombre", "Usuario")
                        .param("apellido", "Prueba")
                        .param("correo", correo)
                        .param("contrasena", "abc123"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("class=\"contenedor active\"")))
                .andExpect(content().string(containsString("Falta: una mayúscula; un carácter especial.")));

        assertTrue(usuarioRepository.findByCorreoIgnoreCase(correo).isEmpty());
    }

    @Test
    void registroRechazaCaracteresProhibidosEnLaContrasena() throws Exception {
        String correo = "contrasena.prohibida@bodega.com";

        mockMvc.perform(post("/auth/registrar")
                        .with(csrf())
                        .param("nombre", "Usuario")
                        .param("apellido", "Prueba")
                        .param("correo", correo)
                        .param("contrasena", "Abc123-!"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("class=\"contenedor active\"")))
                .andExpect(content().string(containsString("quitar el carácter no permitido «-»")));

        assertTrue(usuarioRepository.findByCorreoIgnoreCase(correo).isEmpty());
    }

    @Test
    void registroAceptaContrasenaQueCumpleLaPolitica() throws Exception {
        String correo = "contrasena.valida@bodega.com";
        String contrasena = "Abc123!";

        mockMvc.perform(post("/auth/registrar")
                        .with(csrf())
                        .param("nombre", "Usuario")
                        .param("apellido", "Prueba")
                        .param("correo", correo)
                        .param("contrasena", contrasena))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Usuario creado = usuarioRepository.findByCorreoIgnoreCase(correo).orElseThrow();
        try {
            assertEquals(RolUsuario.USUARIO, creado.getRol());
            assertTrue(passwordEncoder.matches(contrasena, creado.getContrasena()));
        } finally {
            usuarioRepository.delete(creado);
        }
    }

    @Test
    void pantallaDeLoginNoMuestraAvisoDeSesionCerrada() throws Exception {
        String contenido = mockMvc.perform(get("/login?logout"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertFalse(contenido.contains("Sesión cerrada correctamente"));
    }

    @Test
    void buscarEnLaBarraFiltraHerramientasPorNombreCategoriaDescripcionUObservacion() throws Exception {
        Catalogo herramienta = new Catalogo();
        herramienta.setNombre("Filtro búsqueda especial");
        herramienta.setCategoria("Filtros");
        herramienta.setDescripcion("Elemento para probar la búsqueda");
        herramienta.setCantidad(3);
        herramienta.setDisponibilidad(3);
        herramienta.setObservacion("Lote de prueba barra");
        herramienta = catalogoRepository.save(herramienta);

        try {
            mockMvc.perform(get("/catalogo").param("q", "búsqueda especial")
                            .with(user("usuario@bodega.com").roles("USUARIO")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Filtro búsqueda especial")))
                    .andExpect(content().string(containsString("name=\"q\"")))
                    .andExpect(content().string(containsString("búsqueda especial")));
            mockMvc.perform(get("/catalogo").param("q", "elemento para probar")
                            .with(user("usuario@bodega.com").roles("USUARIO")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Filtro búsqueda especial")));
            mockMvc.perform(get("/catalogo").param("q", "lote de prueba barra")
                            .with(user("usuario@bodega.com").roles("USUARIO")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Filtro búsqueda especial")));
        } finally {
            catalogoRepository.deleteById(herramienta.getId());
        }
    }

    @Test
    void autenticaAdminConContrasenaBcryptYConservaSuRolEnLaSesion() throws Exception {
        Usuario admin = usuarioRepository.findByCorreoIgnoreCase("admin@bodega.com").orElseThrow();
        assertTrue(admin.getContrasena().startsWith("$2"));
        assertTrue(passwordEncoder.matches("123456789012", admin.getContrasena()));

        var resultadoLogin = mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("correo", "admin@bodega.com")
                        .param("contrasena", "123456789012"))
                .andExpect(redirectedUrl("/catalogo"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) resultadoLogin.getRequest().getSession(false);
        assertEquals("ADMIN", session.getAttribute("usuarioRol"));
        mockMvc.perform(get("/usuarios/lista").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void actualizaElPerfilSinCambiarElRolYConservaLaSesion() throws Exception {
        Usuario admin = usuarioRepository.findByCorreoIgnoreCase("admin@bodega.com").orElseThrow();
        var resultado = mockMvc.perform(post("/perfil")
                        .with(user("admin@bodega.com").roles("ADMIN"))
                        .with(csrf())
                        .param("nombre", "Administrador actualizado")
                        .param("apellido", admin.getApellido())
                        .param("correo", admin.getCorreo())
                        .param("contrasena", ""))
                .andExpect(redirectedUrl("/perfil"))
                .andReturn();

        assertEquals("Administrador actualizado", usuarioRepository.findById(admin.getId()).orElseThrow().getNombre());
        assertEquals("ADMIN", usuarioRepository.findById(admin.getId()).orElseThrow().getRol().name());
        mockMvc.perform(get("/perfil").session((MockHttpSession) resultado.getRequest().getSession(false)))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarCorreoYContrasenaActualizaLaAutenticacionSinCambiarElRol() throws Exception {
        Usuario admin = usuarioRepository.findByCorreoIgnoreCase("admin@bodega.com").orElseThrow();
        String correoAnterior = admin.getCorreo();
        String contrasenaAnterior = admin.getContrasena();
        String correoNuevo = "admin.perfil@bodega.com";

        try {
            var resultado = mockMvc.perform(post("/perfil")
                            .with(user(correoAnterior).roles("ADMIN"))
                            .with(csrf())
                            .param("nombre", admin.getNombre())
                            .param("apellido", admin.getApellido())
                            .param("correo", correoNuevo)
                            .param("contrasena", "NuevaClave9!"))
                    .andExpect(redirectedUrl("/perfil"))
                    .andReturn();

            Usuario actualizado = usuarioRepository.findByCorreoIgnoreCase(correoNuevo).orElseThrow();
            assertEquals(RolUsuario.ADMIN, actualizado.getRol());
            assertTrue(passwordEncoder.matches("NuevaClave9!", actualizado.getContrasena()));
            mockMvc.perform(get("/perfil").session(
                            (MockHttpSession) resultado.getRequest().getSession(false)))
                    .andExpect(status().isOk());
        } finally {
            admin.setCorreo(correoAnterior);
            admin.setContrasena(contrasenaAnterior);
            usuarioRepository.save(admin);
        }
    }

    @Test
    void cerrarSesionDesdeElMenuUsaElEndpointProtegidoPorCsrf() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .with(user("admin@bodega.com").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void exigeTokenCsrfParaModificarElCatalogo() throws Exception {
        mockMvc.perform(post("/catalogo/guardar").with(user("admin@bodega.com").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/acceso-denegado"));
    }
}
