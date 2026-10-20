package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;

@Service
public class PrestamoService {

    // La "memoria" de la aplicación: se vacía cada vez que reinicias.
    private final List<Prestamo> prestamos = new ArrayList<>();

    public void guardar(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    public List<Prestamo> listarTodos() {
        return prestamos;
    }

    public int contar() {
        return prestamos.size();
    }

    public Prestamo buscarPorNombre(String nombre) {
        for (Prestamo productoPrestamo : prestamos) {
            if (productoPrestamo.getNombreSolicitante().equalsIgnoreCase(nombre)) {
                return productoPrestamo;
            }
        }
        return null; // Retorna nulo si encuentra el nombre
    }
}