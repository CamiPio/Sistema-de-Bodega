package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.CatalogoService;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.PrestamoService;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    private final CatalogoService catalogoService;

    private final UsuarioService usuarioService;

    public PrestamoController(PrestamoService prestamoService, CatalogoService catalogoService,
                              UsuarioService usuarioService) {
        this.prestamoService = prestamoService;
        this.catalogoService = catalogoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarPrestamosActivos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model, Authentication authentication) {
        boolean esAdministrador = esAdministrador(authentication);
        model.addAttribute("esAdministrador", esAdministrador);
        if (esAdministrador) {
            List<Prestamo> prestamos = prestamoService.listarActivos();
            List<Prestamo> solicitudes = prestamoService.listarPendientes();
            if (desde != null && hasta != null && desde.isAfter(hasta)) {
                model.addAttribute("errorFechas", "La fecha inicial no puede ser posterior a la fecha final.");
                prestamos = List.of();
                solicitudes = List.of();
            } else {
                prestamos = filtrarPorFechas(prestamos, desde, hasta);
                solicitudes = filtrarPorFechas(solicitudes, desde, hasta);
            }
            model.addAttribute("prestamos", prestamos);
            model.addAttribute("solicitudesPendientes", solicitudes);
            model.addAttribute("fechaDesde", desde);
            model.addAttribute("fechaHasta", hasta);
        } else {
            model.addAttribute("prestamos", prestamoService.listarPorUsuario(obtenerUsuario(authentication).getId()));
        }
        return "prestamos-activos";
    }

    @GetMapping("/nuevo")
    public String formularioPrestamo(@RequestParam(required = false) Long idProducto, Model model,
                                     Authentication authentication) {
        Prestamo prestamo = new Prestamo();
        prestamo.setFecha(LocalDate.now());
        boolean esAdministrador = esAdministrador(authentication);
        model.addAttribute("esAdministrador", esAdministrador);
        if (!esAdministrador) {
            Usuario usuario = obtenerUsuario(authentication);
            prestamo.setIdUsuario(usuario.getId());
            prestamo.setNombreSolicitante(nombreCompleto(usuario));
        }
        if (idProducto != null) {
            var producto = catalogoService.buscarPorId(idProducto);
            int disponibles = producto == null
                    ? 0
                    : Objects.requireNonNullElse(producto.getDisponibilidad(),
                            Objects.requireNonNullElse(producto.getCantidad(), 0));
            if (producto != null && disponibles > 0) {
                prestamo.setIdProducto(producto.getId());
                if (!esAdministrador) {
                    model.addAttribute("herramientaSeleccionada", producto);
                }
            } else {
                model.addAttribute("errorPrestamo", "La herramienta seleccionada ya no está disponible.");
            }
        }
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("productos", catalogoService.listarTodos());
        model.addAttribute("fechaMaxima", LocalDate.now());
        return "registro-prestamo";
    }

    @PostMapping("/guardar")
    public String guardarPrestamo(@Valid @ModelAttribute("prestamo") Prestamo prestamo,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes,
                                  Authentication authentication) {
        boolean esAdministrador = esAdministrador(authentication);
        if (esAdministrador) {
            prestamo.setIdUsuario(null);
        } else {
            Usuario usuario = obtenerUsuario(authentication);
            prestamo.setIdUsuario(usuario.getId());
            prestamo.setNombreSolicitante(nombreCompleto(usuario));
        }
        model.addAttribute("esAdministrador", esAdministrador);
        if (bindingResult.hasErrors()) {
            model.addAttribute("productos", catalogoService.listarTodos());
            model.addAttribute("fechaMaxima", LocalDate.now());
            return "registro-prestamo";
        }
        try {
            if (esAdministrador) {
                prestamoService.registrarPrestamo(prestamo);
            } else {
                prestamoService.registrarSolicitud(prestamo);
            }
        } catch (IllegalArgumentException e) {
            bindingResult.reject("prestamo.invalido", e.getMessage());
            model.addAttribute("productos", catalogoService.listarTodos());
            model.addAttribute("fechaMaxima", LocalDate.now());
            return "registro-prestamo";
        }
        redirectAttributes.addFlashAttribute("mensajePrestamo", esAdministrador
                ? "Préstamo registrado y entregado correctamente."
                : "Solicitud enviada al administrador para su aprobación.");
        return "redirect:/prestamos";
    }

    @PostMapping("/solicitar")
    public String solicitarPrestamo(@RequestParam Long idProducto, Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(authentication);
        Prestamo solicitud = new Prestamo();
        solicitud.setIdProducto(idProducto);
        solicitud.setIdUsuario(usuario.getId());
        solicitud.setNombreSolicitante(nombreCompleto(usuario));
        solicitud.setFecha(LocalDate.now());
        solicitud.setObservacionEntrega(null);
        try {
            prestamoService.registrarSolicitud(solicitud);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorPrestamo", e.getMessage());
            return "redirect:/prestamos/nuevo?idProducto=" + idProducto;
        }
        redirectAttributes.addFlashAttribute("mensajePrestamo", "Solicitud enviada al administrador para su aprobación.");
        return "redirect:/prestamos";
    }

    @PostMapping("/aprobar/{id}")
    public String aprobarSolicitud(@PathVariable Long id,
                                   @RequestParam(name = "observacionEntrega", required = false) String observacionEntrega,
                                   RedirectAttributes redirectAttributes) {
        try {
            prestamoService.aprobarSolicitud(id, observacionEntrega);
            redirectAttributes.addFlashAttribute("mensajePrestamo",
                    "Solicitud aprobada y préstamo entregado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorPrestamo", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    @PostMapping("/rechazar/{id}")
    public String rechazarSolicitud(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            prestamoService.rechazarSolicitud(id);
            redirectAttributes.addFlashAttribute("mensajePrestamo", "Solicitud rechazada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorPrestamo", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    @PostMapping("/devolver/{id}")
    public String procesarDevolucion(@PathVariable Long id,
                                     @RequestParam(name = "observacionDevolucion", required = false) String observacionDevolucion,
                                     RedirectAttributes redirectAttributes,
                                     Authentication authentication) {
        try {
            Long idUsuario = esAdministrador(authentication) ? null : obtenerUsuario(authentication).getId();
            prestamoService.registrarDevolucion(id, observacionDevolucion, idUsuario);
            redirectAttributes.addFlashAttribute("mensajePrestamo", "Devolución registrada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorPrestamo", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    private boolean esAdministrador(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private Usuario obtenerUsuario(Authentication authentication) {
        return usuarioService.buscarPorCorreo(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("No se encontró la cuenta del usuario autenticado."));
    }

    private String nombreCompleto(Usuario usuario) {
        return usuario.getNombre().trim() + " " + usuario.getApellido().trim();
    }

    private List<Prestamo> filtrarPorFechas(List<Prestamo> prestamos, LocalDate desde, LocalDate hasta) {
        return prestamos.stream()
                .filter(prestamo -> desde == null || !prestamo.getFecha().isBefore(desde))
                .filter(prestamo -> hasta == null || !prestamo.getFecha().isAfter(hasta))
                .toList();
    }
}