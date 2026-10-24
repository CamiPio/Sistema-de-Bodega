package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.RolUsuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Inyección de dependencias del servicio de usuarios
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Muestra la pantalla principal de Login y Registro
    @GetMapping({"/", "/login"})
    public String mostrarLogin(Model model) {
        model.addAttribute("usuarioLogin", new Usuario());
        model.addAttribute("usuarioRegistro", new Usuario());
        return "login"; // Carga tu archivo login.html
    }

    // Procesa el formulario de Registro con validaciones activas
    @PostMapping("/auth/registrar")
    public String registrarUsuario(@Valid @ModelAttribute("usuarioRegistro") Usuario usuarioRegistro, 
                                   BindingResult bindingResult, Model model,
                                   RedirectAttributes redirectAttributes) {
        
        // Si hay errores en las validaciones (campos vacíos, contraseña corta, etc.)
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarioLogin", new Usuario()); // Mantiene el objeto login vivo
            return "login"; // Recarga la vista de login/registro mostrando los errores
        }

        try {
            usuarioService.registrar(usuarioRegistro);
            System.out.println("¡Usuario registrado con éxito: " + usuarioRegistro.getCorreo() + "!");
        } catch (RuntimeException e) {
            // Capturamos el error de correo duplicado lanzado desde el servicio
            model.addAttribute("errorRegistro", e.getMessage()); // Mensaje específico para el formulario de registro
            model.addAttribute("usuarioLogin", new Usuario());   // Mantiene vivo el objeto de login
            model.addAttribute("usuarioRegistro", usuarioRegistro); // Mantiene los datos que ingresó el usuario
            return "login"; // Recarga la misma vista mostrando el error
        }
        
        // Te redirige directamente a la lista de usuarios para visualizarlos
        redirectAttributes.addFlashAttribute("mensajeRegistro", "Cuenta creada. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }

    // Procesa el formulario de Inicio de Sesión
    @PostMapping("/auth/login")
    public String procesarLogin(@ModelAttribute Usuario usuarioLogin, Model model, HttpSession session) {
        Usuario usuarioEncontrado = usuarioService.validarLogin(usuarioLogin.getCorreo(), usuarioLogin.getContrasena());
        
        if (usuarioEncontrado != null) {
            session.setAttribute("usuarioId", usuarioEncontrado.getId());
            session.setAttribute("usuarioRol", usuarioEncontrado.getRol().name());
            // Si las credenciales son correctas, redirige al catálogo de la bodega
            return "redirect:/catalogo";
        } else {
            // Si fallan, recarga el login enviando un mensaje de error
            model.addAttribute("error", "Correo o contraseña incorrectos");
            model.addAttribute("usuarioLogin", new Usuario());
            model.addAttribute("usuarioRegistro", new Usuario());
            return "login";
        }
    }

    // Muestra la lista de usuarios registrados
    @GetMapping("/usuarios/lista")
    public String listarUsuarios(Model model) {
        List<Usuario> lista = usuarioService.listarTodos();
        model.addAttribute("usuarios", lista);
        model.addAttribute("total", lista.size());
        model.addAttribute("roles", RolUsuario.values());
        return "listaUsuarios"; // Apunta al archivo listaUsuarios.html en templates
    }

    @PostMapping("/usuarios/{id}/rol")
    public String actualizarRol(@PathVariable Long id, @RequestParam RolUsuario rol,
                                RedirectAttributes redirectAttributes) {
        try {
            usuarioService.actualizarRol(id, rol);
            redirectAttributes.addFlashAttribute("mensaje", "Rol actualizado correctamente.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorRol", e.getMessage());
        }
        return "redirect:/usuarios/lista";
    }

    @PostMapping("/auth/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
}