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

    public List<Prestamo> listarPendientes() {
        return prestamoRepository.findByEstadoPendiente(true);
    }

    public long contarPendientes() {
        return prestamoRepository.countByEstadoPendienteTrue();
    }

    public List<Prestamo> listarActivosPorUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El usuario indicado no es válido.");
        }
        return prestamoRepository.findByIdUsuarioAndEstadoActivo(idUsuario, true);
    }

    public List<Prestamo> listarPorUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El usuario indicado no es válido.");
        }
        return prestamoRepository.findByIdUsuario(idUsuario);
    }

    public boolean tienePrestamosActivos(Long idProducto) {
        return prestamoRepository.existsByIdProductoAndEstadoActivo(idProducto, true);
    }

    @Transactional
    public void registrarPrestamo(Prestamo prestamo) {
        Catalogo producto = validarPrestamo(prestamo);
        int disponibles = obtenerDisponibilidad(producto);
        if (disponibles == 0) {
            throw new IllegalArgumentException("No hay unidades disponibles de esa herramienta.");
        }
        prestamo.setEstadoActivo(true);
        prestamo.setEstadoPendiente(false);
        prestamo.setEstadoRechazado(false);
        producto.setDisponibilidad(disponibles - 1);
        catalogoRepository.save(producto);
        prestamoRepository.save(prestamo);
    }

    @Transactional
    public void registrarSolicitud(Prestamo prestamo) {
        if (prestamo.getIdUsuario() == null) {
            throw new IllegalArgumentException("La solicitud debe estar asociada a un usuario.");
        }
        Catalogo producto = validarPrestamo(prestamo);
        if (obtenerDisponibilidad(producto) == 0) {
            throw new IllegalArgumentException("No hay unidades disponibles de esa herramienta.");
        }
        prestamo.setEstadoActivo(false);
        prestamo.setEstadoPendiente(true);
        prestamo.setEstadoRechazado(false);
        prestamoRepository.save(prestamo);
    }

    @Transactional
    public void aprobarSolicitud(Long idPrestamo, String observacionEntrega) {
        if (observacionEntrega == null || observacionEntrega.isBlank()) {
            throw new IllegalArgumentException("El comentario de salida es obligatorio para entregar la herramienta.");
        }
        if (observacionEntrega.length() > 500) {
            throw new IllegalArgumentException("El comentario de salida no puede exceder 500 caracteres.");
        }
        Prestamo prestamo = buscarPrestamoPendiente(idPrestamo);
        Catalogo producto = catalogoRepository.findById(prestamo.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("La herramienta de esta solicitud ya no existe."));
        int disponibles = obtenerDisponibilidad(producto);
        if (disponibles == 0) {
            throw new IllegalArgumentException("No hay unidades disponibles para entregar esta herramienta.");
        }
        producto.setDisponibilidad(disponibles - 1);
        prestamo.setObservacionEntrega(observacionEntrega.trim());
        prestamo.setEstadoPendiente(false);
        prestamo.setEstadoActivo(true);
        catalogoRepository.save(producto);
        prestamoRepository.save(prestamo);
    }

    @Transactional
    public void rechazarSolicitud(Long idPrestamo) {
        Prestamo prestamo = buscarPrestamoPendiente(idPrestamo);
        prestamo.setEstadoPendiente(false);
        prestamo.setEstadoRechazado(true);
        prestamoRepository.save(prestamo);
    }

    @Transactional
    public void registrarDevolucion(Long idPrestamo, String observacionDevolucion) {
        registrarDevolucion(idPrestamo, observacionDevolucion, null);
    }

    @Transactional
    public void registrarDevolucion(Long idPrestamo, String observacionDevolucion, Long idUsuario) {
        if (idPrestamo == null) {
            throw new IllegalArgumentException("El préstamo indicado no es válido.");
        }
        if (observacionDevolucion == null || observacionDevolucion.isBlank()) {
            throw new IllegalArgumentException("La observación de devolución es obligatoria.");
        }
        if (observacionDevolucion.length() > 500) {
            throw new IllegalArgumentException("La observación no puede exceder 500 caracteres.");
        }
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo que intentas devolver."));
        if (idUsuario != null && !idUsuario.equals(prestamo.getIdUsuario())) {
            throw new IllegalArgumentException("No tienes permiso para devolver este préstamo.");
        }
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
        prestamo.setEstadoPendiente(false);
        prestamo.setObservacionDevolucion(observacionDevolucion.trim());
        producto.setDisponibilidad(disponibles + 1);
        catalogoRepository.save(producto);
        prestamoRepository.save(prestamo);
    }

    private Catalogo validarPrestamo(Prestamo prestamo) {
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
        validarCantidad(producto);
        prestamo.setNombreSolicitante(prestamo.getNombreSolicitante().trim());
        if (prestamo.getObservacionEntrega() != null) {
            prestamo.setObservacionEntrega(prestamo.getObservacionEntrega().trim());
        }
        return producto;
    }

    private int obtenerDisponibilidad(Catalogo producto) {
        validarCantidad(producto);
        int disponibles = producto.getDisponibilidad() == null
                ? producto.getCantidad()
                : producto.getDisponibilidad();
        if (disponibles < 0 || disponibles > producto.getCantidad()) {
            throw new IllegalArgumentException("La disponibilidad registrada para esta herramienta no es válida.");
        }
        return disponibles;
    }

    private void validarCantidad(Catalogo producto) {
        if (producto.getCantidad() == null || producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad registrada para esta herramienta no es válida.");
        }
    }

    private Prestamo buscarPrestamoPendiente(Long idPrestamo) {
        if (idPrestamo == null) {
            throw new IllegalArgumentException("La solicitud indicada no es válida.");
        }
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("No existe la solicitud indicada."));
        if (!prestamo.isEstadoPendiente()) {
            throw new IllegalArgumentException("Esta solicitud ya fue atendida.");
        }
        return prestamo;
    }
}