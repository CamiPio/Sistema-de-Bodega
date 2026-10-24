package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.CatalogoService;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private CatalogoService catalogoService;

    // 1. Listar los préstamos en la vista
    @GetMapping
    public String listarPrestamosActivos(Model model) {
        model.addAttribute("prestamos", prestamoService.listarActivos());
        return "prestamos-activos"; 
    }

    // 2. Mostrar el formulario para un nuevo préstamo
    @GetMapping("/nuevo")
    public String formularioPrestamo(Model model) {
        model.addAttribute("prestamo", new Prestamo());
        model.addAttribute("productos", catalogoService.listarTodos());
        return "registro-prestamo"; 
    }

    // 3. Método POST para guardar el préstamo enviado desde el formulario
    @PostMapping("/guardar")
    public String guardarPrestamo(@ModelAttribute Prestamo prestamo) {
        prestamoService.registrarPrestamo(prestamo);
        return "redirect:/prestamos";
    }

    // 4. Procesar la devolución o entrega de la herramienta
    @PostMapping("/devolver/{id}")
    public String procesarDevolucion(@PathVariable Long id, @RequestParam("observacionEntrega") String observacionEntrega) {
        prestamoService.registrarDevolucion(id, observacionEntrega);
        return "redirect:/prestamos";
    }
}