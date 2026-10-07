package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;

@Service
public class BienvenidaService {
        private static final String INSTITUCION = "Sistema Control Inventario Bodega Automotriz";

    public String mensajeGeneral() {
        return "¡Bienvenido(a) a " + INSTITUCION + "!";
    }

    public String mensajePara(String nombre) {
        Usuario user = new Usuario(nombre);
        return "Hola, " + user.getNombre()
                + ". Gracias por visitar " + INSTITUCION + ".";
    }
}
