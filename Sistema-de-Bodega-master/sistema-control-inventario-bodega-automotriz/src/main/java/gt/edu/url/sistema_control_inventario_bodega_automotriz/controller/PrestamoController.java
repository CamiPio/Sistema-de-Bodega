package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @GetMapping
    public String listarPrestamosActivos() {
        return "prestamos/lista"; // Plantilla templates/prestamos/lista.html
    }

    @GetMapping("/nuevo")
    public String formularioPrestamo() {
        return "prestamos/formulario"; // Plantilla templates/prestamos/formulario.html
    }
    
    @PostMapping("/devolver")
    public String procesarDevolucion() {
        // La verificación de la herramienta antes de entregar se hace en el Servicio[cite: 3]
        return "redirect:/prestamos";
    }
}