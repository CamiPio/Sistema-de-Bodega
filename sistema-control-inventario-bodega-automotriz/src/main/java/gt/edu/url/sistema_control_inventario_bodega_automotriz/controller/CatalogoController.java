package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.CatalogoService;
import jakarta.validation.Valid; // Asegúrate de tener tu servicio de catálogo

@Controller
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    // Este es el método que responde cuando el login hace redirect:/catalogo
    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model) {
        model.addAttribute("listaCatalogo", catalogoService.listarTodos());
        model.addAttribute("catalogoNuevo", new Catalogo()); // Objeto para el formulario de registro de repuestos
        return "catalogo"; // Busca el archivo catalogo.html en src/main/resources/templates/
    }

    // Método para procesar el guardado de un nuevo elemento en el catálogo
    @PostMapping("/catalogo/guardar")
    public String guardarCatalogo(@Valid @ModelAttribute("catalogoNuevo") Catalogo catalogo) {
        catalogoService.guardar(catalogo);
        return "redirect:/catalogo"; // Recarga la página para mostrar el cambio
    }
}
