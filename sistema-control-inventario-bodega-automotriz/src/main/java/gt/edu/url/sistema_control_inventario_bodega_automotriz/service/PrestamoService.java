package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.PrestamoRepository;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;

    private final CatalogoRepository catalogoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, CatalogoRepository catalogoRepository) {
        this.prestamoRepository = prestamoRepository;
        this.catalogoRepository = catalogoRepository;
    }

    public List<Prestamo> listarActivos() {
        return prestamoRepository.findByEstadoActivo(true);
    }

    public boolean tienePrestamosActivos(Long idProducto) {
        return prestamoRepository.existsByIdProductoAndEstadoActivo(idProducto, true);
    }

    @Transactional
    public void registrarPrestamo(Prestamo prestamo) {
        if (prestamo.getIdProducto() == null || prestamo.getIdProducto() <= 0) {
            throw new IllegalArgumentException("Debes seleccionar una herramienta.");
        }
        if (prestamo.getFecha() == null || prestamo.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha del préstamo es obligatoria y no puede ser futura.");
        }
        if (prestamo.getNombreSolicitante() == null || prestamo.getNombreSolicitante().isBlank()
                || prestamo.getNombreSolicitante().length() > 120) {
            throw new IllegalArgumentException("El nombre del solicitante es obligatorio y no puede exceder 120 caracteres.");
        }
        if (prestamo.getObservacionEntrega() != null && prestamo.getObservacionEntrega().length() > 500) {
            throw new IllegalArgumentException("La observación no puede exceder 500 caracteres.");
        }
        Catalogo producto = catalogoRepository.findById(prestamo.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("La herramienta seleccionada no existe."));
        if (producto.getCantidad() == null || producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad registrada para esta herramienta no es válida.");
        }
        int disponibles = producto.getDisponibilidad() == null
                ? producto.getCantidad()
                : producto.getDisponibilidad();
        if (disponibles < 0 || disponibles > producto.getCantidad()) {
            throw new IllegalArgumentException("La disponibilidad registrada para esta herramienta no es válida.");
        }
        if (disponibles == 0) {
            throw new IllegalArgumentException("No hay unidades disponibles de esa herramienta.");
        }

        prestamo.setNombreSolicitante(prestamo.getNombreSolicitante().trim());
        if (prestamo.getObservacionEntrega() != null) {
            prestamo.setObservacionEntrega(prestamo.getObservacionEntrega().trim());
        }
        prestamo.setEstadoActivo(true);
        producto.setDisponibilidad(disponibles - 1);
        catalogoRepository.save(producto);
        prestamoRepository.save(prestamo);
    }

    @Transactional
    public void registrarDevolucion(Long idPrestamo, String observacionEntrega) {
        if (idPrestamo == null) {
            throw new IllegalArgumentException("El préstamo indicado no es válido.");
        }
        if (observacionEntrega == null || observacionEntrega.isBlank()) {
            throw new IllegalArgumentException("La observación de devolución es obligatoria.");
        }
        if (observacionEntrega.length() > 500) {
            throw new IllegalArgumentException("La observación no puede exceder 500 caracteres.");
        }
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo que intentas devolver."));
        if (!prestamo.isEstadoActivo()) {
            throw new IllegalArgumentException("Este préstamo ya fue devuelto.");
        }
        Catalogo producto = catalogoRepository.findById(prestamo.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("La herramienta de este préstamo ya no existe."));
        if (producto.getCantidad() == null || producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad registrada para esta herramienta no es válida.");
        }
        int disponibles = producto.getDisponibilidad() == null
                ? 0
                : producto.getDisponibilidad();
        if (disponibles >= producto.getCantidad()) {
            throw new IllegalArgumentException("La disponibilidad ya alcanzó la cantidad total registrada.");
        }

        prestamo.setEstadoActivo(false);
        prestamo.setObservacionEntrega(observacionEntrega.trim());
        producto.setDisponibilidad(disponibles + 1);
        catalogoRepository.save(producto);
        prestamoRepository.save(prestamo);
    }
}