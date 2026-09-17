package gt.edu.url.sistema_control_inventario_bodega_automotriz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.BienvenidaService;

@RestController
public class BienvenidaController {

    private final BienvenidaService bienvenidaService;

    public BienvenidaController(BienvenidaService bienvenidaService) {
        this.bienvenidaService = bienvenidaService;
    }

    @GetMapping("/api/saludo")
    public String saludo() {
        return bienvenidaService.mensajeGeneral();
    }

    @GetMapping("/api/bienvenida")
    public String bienvenida(@RequestParam(defaultValue = "visitante") String nombre) {
        return bienvenidaService.mensajePara(nombre);
    }
}