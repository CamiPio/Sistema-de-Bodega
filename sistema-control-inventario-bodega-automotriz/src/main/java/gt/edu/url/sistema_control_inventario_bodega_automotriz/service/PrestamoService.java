package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private CatalogoRepository catalogoRepository;

    public List<Prestamo> listarActivos() {
        return prestamoRepository.findAll();
    }

    public void registrarPrestamo(Prestamo prestamo) {
        boolean activo = true; 
        prestamo.setEstadoActivo(activo);
        prestamoRepository.save(prestamo);

        Catalogo producto = catalogoRepository.findById(Long.valueOf(prestamo.getIdProducto())).orElse(null);
        if (producto != null) {
            int disponibilidadActual = Integer.parseInt(producto.getDisponibilidad());
            if (disponibilidadActual > 0) {
                producto.setDisponibilidad(String.valueOf(disponibilidadActual - 1));
                catalogoRepository.save(producto);
            }
        }
    }

    public void registrarDevolucion(Long idPrestamo, String observacionEntrega) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo).orElse(null);
        if (prestamo != null) {
            boolean inactivo = false; 
            prestamo.setEstadoActivo(inactivo);
            prestamo.setObservacionEntrega(observacionEntrega);
            prestamoRepository.save(prestamo);

            Catalogo producto = catalogoRepository.findById(Long.valueOf(prestamo.getIdProducto())).orElse(null);
            if (producto != null) {
                int disponibilidadActual = Integer.parseInt(producto.getDisponibilidad());
                producto.setDisponibilidad(String.valueOf(disponibilidadActual + 1));
                catalogoRepository.save(producto);
            }
        }
    }
}