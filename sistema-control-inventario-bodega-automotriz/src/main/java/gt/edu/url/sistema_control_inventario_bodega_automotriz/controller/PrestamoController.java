package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import java.time.LocalDate;

import jakarta.validation.Valid;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.CatalogoService;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.PrestamoService;
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

    public PrestamoController(PrestamoService prestamoService, CatalogoService catalogoService) {
        this.prestamoService = prestamoService;
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public String listarPrestamosActivos(Model model) {
        model.addAttribute("prestamos", prestamoService.listarActivos());
        return "prestamos-activos";
    }

    @GetMapping("/nuevo")
    public String formularioPrestamo(Model model) {
        Prestamo prestamo = new Prestamo();
        prestamo.setFecha(LocalDate.now());
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("productos", catalogoService.listarTodos());
        model.addAttribute("fechaMaxima", LocalDate.now());
        return "registro-prestamo";
    }

    @PostMapping("/guardar")
    public String guardarPrestamo(@Valid @ModelAttribute("prestamo") Prestamo prestamo,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productos", catalogoService.listarTodos());
            model.addAttribute("fechaMaxima", LocalDate.now());
            return "registro-prestamo";
        }
        try {
            prestamoService.registrarPrestamo(prestamo);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("prestamo.invalido", e.getMessage());
            model.addAttribute("productos", catalogoService.listarTodos());
            model.addAttribute("fechaMaxima", LocalDate.now());
            return "registro-prestamo";
        }
        redirectAttributes.addFlashAttribute("mensajePrestamo", "Préstamo registrado correctamente.");
        return "redirect:/prestamos";
    }

    @PostMapping("/devolver/{id}")
    public String procesarDevolucion(@PathVariable Long id,
                                     @RequestParam(name = "observacionEntrega", required = false) String observacionEntrega,
                                     RedirectAttributes redirectAttributes) {
        try {
            prestamoService.registrarDevolucion(id, observacionEntrega);
            redirectAttributes.addFlashAttribute("mensajePrestamo", "Devolución registrada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorPrestamo", e.getMessage());
        }
        return "redirect:/prestamos";
    }
}