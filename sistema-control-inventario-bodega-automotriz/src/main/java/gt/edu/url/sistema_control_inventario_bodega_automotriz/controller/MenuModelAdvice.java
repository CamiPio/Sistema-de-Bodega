package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.PrestamoService;

@ControllerAdvice(annotations = Controller.class)
public class MenuModelAdvice {

    private final PrestamoService prestamoService;

    public MenuModelAdvice(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @ModelAttribute
    public void agregarNotificaciones(Model model, Authentication authentication) {
        boolean esAdministrador = authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        model.addAttribute("solicitudesPendientesCount",
                esAdministrador ? prestamoService.contarPendientes() : 0L);
    }
}
