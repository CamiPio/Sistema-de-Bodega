package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.CatalogoService;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.PrestamoService;
import jakarta.validation.Valid;

@Controller
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final PrestamoService prestamoService;

    public CatalogoController(CatalogoService catalogoService, PrestamoService prestamoService) {
        this.catalogoService = catalogoService;
        this.prestamoService = prestamoService;
    }

    @GetMapping("/catalogo")
    public String mostrarCatalogo(@RequestParam(required = false) String q, Model model) {
        cargarCatalogo(model, new Catalogo());
        model.addAttribute("busqueda", q == null ? "" : q);
        model.addAttribute("listaCatalogo", catalogoService.buscar(q));
        return "catalogo";
    }

    @PostMapping("/catalogo/guardar")
    public String guardarCatalogo(@Valid @ModelAttribute("catalogoNuevo") Catalogo catalogo,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        if (catalogo.getId() != null && catalogoService.buscarPorId(catalogo.getId()) == null) {
            bindingResult.rejectValue("id", "catalogo.noExiste", "El elemento que intentas editar ya no existe.");
        }
        if (bindingResult.hasErrors()) {
            cargarCatalogo(model, catalogo);
            return "catalogo";
        }

        boolean esEdicion = catalogo.getId() != null;
        try {
            catalogoService.guardar(catalogo);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("catalogo.invalido", e.getMessage());
            cargarCatalogo(model, catalogo);
            return "catalogo";
        }
        redirectAttributes.addFlashAttribute("mensajeCatalogo",
                esEdicion ? "Herramienta actualizada correctamente." : "Herramienta registrada correctamente.");
        return "redirect:/catalogo";
    }

    @GetMapping("/catalogo/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model,
                                           RedirectAttributes redirectAttributes) {
        Catalogo catalogoExistente = catalogoService.buscarPorId(id);
        if (catalogoExistente == null) {
            redirectAttributes.addFlashAttribute("errorCatalogo", "No se encontró la herramienta solicitada.");
            return "redirect:/catalogo";
        }
        model.addAttribute("catalogoNuevo", catalogoExistente);
        model.addAttribute("listaCatalogo", catalogoService.listarTodos());
        return "catalogo";
    }

    @PostMapping("/catalogo/eliminar/{id}")
    public String eliminarCatalogo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (catalogoService.buscarPorId(id) == null) {
            redirectAttributes.addFlashAttribute("errorCatalogo", "No se encontró la herramienta que deseas eliminar.");
            return "redirect:/catalogo";
        }
        if (prestamoService.tienePrestamosActivos(id)) {
            redirectAttributes.addFlashAttribute("errorCatalogo",
                    "No puedes eliminar una herramienta que tiene préstamos activos.");
            return "redirect:/catalogo";
        }
        catalogoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeCatalogo", "Herramienta eliminada correctamente.");
        return "redirect:/catalogo";
    }

    private void cargarCatalogo(Model model, Catalogo catalogo) {
        model.addAttribute("catalogoNuevo", catalogo);
        model.addAttribute("listaCatalogo", catalogoService.listarTodos());
    }
}
