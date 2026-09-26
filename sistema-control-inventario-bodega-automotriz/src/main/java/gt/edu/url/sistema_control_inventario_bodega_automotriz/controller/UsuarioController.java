package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.UsuarioService;

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

    // Procesa el formulario de Registro
    @PostMapping("/auth/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuarioRegistro) {
        usuarioService.guardar(usuarioRegistro);
        System.out.println("¡Usuario registrado con éxito: " + usuarioRegistro.getCorreo() + "!");
        return "redirect:/login?exito=true";
    }

    // Procesa el formulario de Inicio de Sesión
    @PostMapping("/auth/login")
    public String procesarLogin(@ModelAttribute Usuario usuarioLogin, Model model) {
        Usuario usuarioEncontrado = usuarioService.validarLogin(usuarioLogin.getCorreo(), usuarioLogin.getContrasena());
        
        if (usuarioEncontrado != null) {
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

    @GetMapping("/usuarios/lista")
    public String listarUsuarios(Model model) {
        List<Usuario> lista = usuarioService.listarTodos();
        model.addAttribute("usuarios", lista);
        model.addAttribute("total", lista.size());
        return "listaUsuarios";
    }
}