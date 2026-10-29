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
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.PrestamoRepository;
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
    private PrestamoRepository prestamoRepository;

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
    void catalogoMuestraPanelDeInventarioYFormularioSeparado() throws Exception {
        MockHttpSession sesionAdmin = new MockHttpSession();
        sesionAdmin.setAttribute("usuarioRol", "ADMIN");
        mockMvc.perform(get("/catalogo").session(sesionAdmin).with(user("admin@bodega.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Panel de inventario")))
                .andExpect(content().string(containsString("/catalogo/nuevo")))
                .andExpect(content().string(containsString("Herramientas registradas")));

        mockMvc.perform(get("/catalogo/nuevo").with(user("admin@bodega.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("formulario-catalogo"))
                .andExpect(content().string(containsString("Agregar herramienta")));
    }

    @Test
    void catalogoDeUsuarioMuestraSoloElConteoYOfrecePrestamoParaHerramientasDisponibles() throws Exception {
        Catalogo disponible = guardarHerramienta("Herramienta disponible prueba", 4, 2);
        Catalogo agotada = guardarHerramienta("Herramienta agotada prueba", 2, 0);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioRol", "USUARIO");
        session.setAttribute("usuarioId", 501L);

        try {
            String contenido = mockMvc.perform(get("/catalogo").session(session)
                            .with(user("usuario@bodega.com").roles("USUARIO")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Herramientas registradas")))
                    .andExpect(content().string(containsString("Mis préstamos")))
                    .andExpect(content().string(containsString("Realizar préstamo")))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertFalse(contenido.contains("Unidades disponibles"));
            assertFalse(contenido.contains("Préstamos activos"));
            assertFalse(contenido.contains("Unidades en inventario"));
            assertTrue(contenido.contains("/prestamos/nuevo?idProducto=" + disponible.getId()));
            assertFalse(contenido.contains("/prestamos/nuevo?idProducto=" + agotada.getId()));
        } finally {
            catalogoRepository.deleteAllById(java.util.List.of(disponible.getId(), agotada.getId()));
        }
    }

    @Test
    void accesoDirectoAlPrestamoPreseleccionaLaHerramienta() throws Exception {
        Catalogo disponible = guardarHerramienta("Herramienta directa prueba", 3, 1);
        Usuario usuario = guardarUsuario("directo.prueba@bodega.com");
        try {
            mockMvc.perform(get("/prestamos/nuevo").param("idProducto", disponible.getId().toString())
                            .with(user(usuario.getCorreo()).roles("USUARIO")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString(
                            "name=\"idProducto\" value=\"" + disponible.getId() + "\"")))
                    .andExpect(content().string(containsString("Herramienta seleccionada:")))
                    .andExpect(content().string(org.hamcrest.Matchers.not(containsString("<select"))))
                    .andExpect(content().string(containsString("Enviar solicitud")))
                    .andExpect(content().string(org.hamcrest.Matchers.not(containsString("nombreSolicitante"))))
                    .andExpect(content().string(org.hamcrest.Matchers.not(containsString("observacionEntrega"))));
        } finally {
            catalogoRepository.deleteById(disponible.getId());
            usuarioRepository.delete(usuario);
        }
    }

    @Test
    void solicitudNuevaQuedaPendienteYAsociadaConLaCuentaAutenticada() throws Exception {
        Catalogo disponible = guardarHerramienta("Herramienta de cuenta prueba", 2, 1);
        Usuario usuario = guardarUsuario("solicitud.prueba@bodega.com");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioRol", "USUARIO");
        session.setAttribute("usuarioId", usuario.getId());

        try {
            mockMvc.perform(post("/prestamos/solicitar").session(session)
                            .with(user(usuario.getCorreo()).roles("USUARIO"))
                            .with(csrf())
                            .param("idProducto", disponible.getId().toString()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prestamos"));

            Prestamo prestamo = prestamoRepository.findAll().stream()
                    .filter(registro -> usuario.getId().equals(registro.getIdUsuario()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(usuario.getId(), prestamo.getIdUsuario());
            assertEquals("Usuario Prueba", prestamo.getNombreSolicitante());
            assertTrue(prestamo.isEstadoPendiente());
            assertFalse(prestamo.isEstadoActivo());
            assertEquals(null, prestamo.getObservacionEntrega());
            assertEquals(1, catalogoRepository.findById(disponible.getId()).orElseThrow().getDisponibilidad());
        } finally {
            prestamoRepository.findAll().stream()
                    .filter(registro -> usuario.getId().equals(registro.getIdUsuario()))
                    .forEach(prestamoRepository::delete);
            catalogoRepository.deleteById(disponible.getId());
            usuarioRepository.delete(usuario);
        }
    }

    @Test
    void misPrestamosSoloMuestraLosRegistrosDeLaCuentaAutenticada() throws Exception {
        Usuario usuario = guardarUsuario("mis.prestamos@bodega.com");
        Usuario otroUsuario = guardarUsuario("otro.usuario@bodega.com");
        Prestamo propio = guardarPrestamo(usuario.getId(), "Préstamo de mi cuenta prueba");
        Prestamo ajeno = guardarPrestamo(otroUsuario.getId(), "Préstamo de otra cuenta prueba");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioRol", "USUARIO");
        session.setAttribute("usuarioId", usuario.getId());

        try {
            String contenido = mockMvc.perform(get("/prestamos").session(session)
                            .with(user(usuario.getCorreo()).roles("USUARIO")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Mis Préstamos Activos")))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertTrue(contenido.contains(propio.getNombreSolicitante()));
            assertFalse(contenido.contains(ajeno.getNombreSolicitante()));
        } finally {
            prestamoRepository.deleteAllById(java.util.List.of(propio.getIdPresamo(), ajeno.getIdPresamo()));
            usuarioRepository.deleteAll(java.util.List.of(usuario, otroUsuario));
        }
    }

    @Test
    void administradorVeYSolicitaAprobacionDePrestamosPendientes() throws Exception {
        Catalogo herramienta = guardarHerramienta("Herramienta para aprobación prueba", 3, 2);
        Prestamo solicitud = guardarPrestamo(900L, "Solicitud pendiente visible");
        solicitud.setIdProducto(herramienta.getId());
        solicitud.setEstadoActivo(false);
        solicitud.setEstadoPendiente(true);
        prestamoRepository.save(solicitud);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioRol", "ADMIN");

        try {
            mockMvc.perform(get("/prestamos").session(session)
                            .with(user("admin@bodega.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Solicitudes pendientes de aprobación")))
                    .andExpect(content().string(containsString("Solicitud pendiente visible")))
                    .andExpect(content().string(containsString("Aprobar y entregar")))
                    .andExpect(content().string(containsString("Comentario de salida de la herramienta")))
                    .andExpect(content().string(containsString("/prestamos#solicitudes")))
                    .andExpect(content().string(containsString("contador-notificaciones")));

            mockMvc.perform(post("/prestamos/aprobar/" + solicitud.getIdPresamo())
                            .with(user("admin@bodega.com").roles("ADMIN"))
                            .with(csrf())
                            .param("observacionEntrega", "Entregada en buen estado"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prestamos"));

            Prestamo aprobado = prestamoRepository.findById(solicitud.getIdPresamo()).orElseThrow();
            assertTrue(aprobado.isEstadoActivo());
            assertFalse(aprobado.isEstadoPendiente());
            assertEquals("Entregada en buen estado", aprobado.getObservacionEntrega());
            assertEquals(1, catalogoRepository.findById(herramienta.getId()).orElseThrow().getDisponibilidad());
        } finally {
            prestamoRepository.deleteById(solicitud.getIdPresamo());
            catalogoRepository.deleteById(herramienta.getId());
        }
    }

    @Test
    void usuarioNoAdministradorNoPuedeAprobarSolicitudes() throws Exception {
        mockMvc.perform(post("/prestamos/aprobar/1")
                        .with(user("usuario@bodega.com").roles("USUARIO"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/acceso-denegado"));
    }

    @Test
    void usuarioNoAdministradorNoPuedeRegistrarPrestamosDirectamente() throws Exception {
        mockMvc.perform(post("/prestamos/guardar")
                        .with(user("usuario@bodega.com").roles("USUARIO"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/acceso-denegado"));
    }

    @Test
    void usuarioNoAdministradorNoPuedeRegistrarDevoluciones() throws Exception {
        mockMvc.perform(post("/prestamos/devolver/1")
                        .with(user("usuario@bodega.com").roles("USUARIO"))
                        .with(csrf())
                        .param("observacionDevolucion", "Buen estado"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/acceso-denegado"));
    }

    @Test
    void administradorPuedeRegistrarPrestamoParaPersonaSinCuenta() throws Exception {
        Catalogo herramienta = guardarHerramienta("Herramienta sin cuenta prueba", 4, 3);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioRol", "ADMIN");
        String solicitante = "Persona sin cuenta registrada";

        try {
            mockMvc.perform(post("/prestamos/guardar").session(session)
                            .with(user("admin@bodega.com").roles("ADMIN"))
                            .with(csrf())
                            .param("idProducto", herramienta.getId().toString())
                            .param("nombreSolicitante", solicitante)
                            .param("fecha", java.time.LocalDate.now().toString())
                            .param("observacionEntrega", "Entregada en buen estado"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prestamos"));

            Prestamo prestamo = prestamoRepository.findAll().stream()
                    .filter(registro -> solicitante.equals(registro.getNombreSolicitante()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(null, prestamo.getIdUsuario());
            assertTrue(prestamo.isEstadoActivo());
            assertEquals("Entregada en buen estado", prestamo.getObservacionEntrega());
            assertEquals(2, catalogoRepository.findById(herramienta.getId()).orElseThrow().getDisponibilidad());
        } finally {
            prestamoRepository.findAll().stream()
                    .filter(registro -> solicitante.equals(registro.getNombreSolicitante()))
                    .forEach(prestamoRepository::delete);
            catalogoRepository.deleteById(herramienta.getId());
        }
    }

    @Test
    void administradorFiltraSolicitudesPorIntervaloDeFechas() throws Exception {
        Prestamo solicitudDentro = guardarPrestamo(901L, "Solicitud dentro intervalo");
        solicitudDentro.setEstadoActivo(false);
        solicitudDentro.setEstadoPendiente(true);
        solicitudDentro.setFecha(java.time.LocalDate.of(2026, 10, 10));
        prestamoRepository.save(solicitudDentro);
        Prestamo solicitudFuera = guardarPrestamo(902L, "Solicitud fuera intervalo");
        solicitudFuera.setEstadoActivo(false);
        solicitudFuera.setEstadoPendiente(true);
        solicitudFuera.setFecha(java.time.LocalDate.of(2026, 10, 20));
        prestamoRepository.save(solicitudFuera);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioRol", "ADMIN");

        try {
            String contenido = mockMvc.perform(get("/prestamos").session(session)
                            .param("desde", "2026-10-01")
                            .param("hasta", "2026-10-15")
                            .with(user("admin@bodega.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertTrue(contenido.contains("Solicitud dentro intervalo"));
            assertFalse(contenido.contains("Solicitud fuera intervalo"));
        } finally {
            prestamoRepository.deleteAllById(java.util.List.of(
                    solicitudDentro.getIdPresamo(), solicitudFuera.getIdPresamo()));
        }
    }

    @Test
    void usuarioNoAdministradorNoPuedeAbrirFormularioNuevoDeHerramientas() throws Exception {
        mockMvc.perform(get("/catalogo/nuevo").with(user("usuario@bodega.com").roles("USUARIO")))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/acceso-denegado"));
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
    void menuConservaLaPosicionDeSusElementosParaAmbosRolesYNotificacionesSoloParaAdmin() throws Exception {
        MockHttpSession sesionAdmin = new MockHttpSession();
        sesionAdmin.setAttribute("usuarioRol", "ADMIN");
        String contenidoAdmin = mockMvc.perform(get("/catalogo").session(sesionAdmin)
                        .with(user("admin@bodega.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MockHttpSession sesionUsuario = new MockHttpSession();
        sesionUsuario.setAttribute("usuarioRol", "USUARIO");
        String contenidoUsuario = mockMvc.perform(get("/catalogo").session(sesionUsuario)
                        .with(user("usuario@bodega.com").roles("USUARIO")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(contenidoAdmin.indexOf("Catálogo") < contenidoAdmin.indexOf("id=\"opRecurso\""));
        assertTrue(contenidoAdmin.indexOf("id=\"opRecurso\"") < contenidoAdmin.indexOf("id=\"opPerfil\""));
        assertTrue(contenidoAdmin.indexOf("id=\"opPerfil\"") < contenidoAdmin.indexOf("class=\"contenedor-busqueda\""));
        assertTrue(contenidoAdmin.indexOf("class=\"contenedor-busqueda\"") < contenidoAdmin.indexOf("id=\"menuBtn\""));
        assertTrue(contenidoUsuario.indexOf("Catálogo") < contenidoUsuario.indexOf("id=\"opRecurso\""));
        assertTrue(contenidoUsuario.indexOf("id=\"opRecurso\"") < contenidoUsuario.indexOf("id=\"opPerfil\""));
        assertTrue(contenidoUsuario.indexOf("id=\"opPerfil\"") < contenidoUsuario.indexOf("class=\"contenedor-busqueda\""));
        assertTrue(contenidoUsuario.indexOf("class=\"contenedor-busqueda\"") < contenidoUsuario.indexOf("id=\"menuBtn\""));
        assertTrue(contenidoAdmin.indexOf("id=\"opRecurso\"") < contenidoAdmin.indexOf("Lista de Usuarios"));
        assertTrue(contenidoAdmin.contains("notificaciones-superior"));
        assertTrue(contenidoUsuario.contains("notificaciones-placeholder"));
        assertFalse(contenidoUsuario.contains("notificaciones-superior"));
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
                .andExpect(content().string(containsString("querySelectorAll('.boton-registro')")))
                .andExpect(content().string(containsString("querySelectorAll('.boton-iniciar')")))
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

    private Catalogo guardarHerramienta(String nombre, int cantidad, int disponibilidad) {
        Catalogo herramienta = new Catalogo();
        herramienta.setNombre(nombre);
        herramienta.setCategoria("Prueba");
        herramienta.setDescripcion("Herramienta usada en pruebas de catálogo.");
        herramienta.setCantidad(cantidad);
        herramienta.setDisponibilidad(disponibilidad);
        return catalogoRepository.save(herramienta);
    }

    private Usuario guardarUsuario(String correo) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario");
        usuario.setApellido("Prueba");
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode("Prueba123!"));
        usuario.setRol(RolUsuario.USUARIO);
        return usuarioRepository.save(usuario);
    }

    private Prestamo guardarPrestamo(Long idUsuario, String nombreSolicitante) {
        Prestamo prestamo = new Prestamo();
        prestamo.setIdUsuario(idUsuario);
        prestamo.setIdProducto(999L);
        prestamo.setFecha(java.time.LocalDate.now());
        prestamo.setNombreSolicitante(nombreSolicitante);
        prestamo.setEstadoActivo(true);
        return prestamoRepository.save(prestamo);
    }
}
