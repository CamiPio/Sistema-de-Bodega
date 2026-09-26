package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;

@Service
public class CatalogoService {

    // La "memoria" de la aplicación: se vacía cada vez que reinicias.
    private final List<Catalogo> catalogos = new ArrayList<>();

    public void guardar(Catalogo catalogo) {
        catalogos.add(catalogo);
    }

    public List<Catalogo> listarTodos() {
        return catalogos;
    }

    public int contar() {
        return catalogos.size();
    }

    public Catalogo buscarPorNombre(String nombre) {
        for (Catalogo productoCatalogo : catalogos) {
            if (productoCatalogo.getNombre().equalsIgnoreCase(nombre)) {
                return productoCatalogo;
            }
        }
        return null; // Retorna nulo si encuentra el nombre
    }
}