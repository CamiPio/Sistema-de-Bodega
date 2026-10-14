package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.PrestamoRepository;

@Service
public class PrestamoService {

    private final PrestamoRepository repositorio;

    public PrestamoService(PrestamoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Prestamo> listarTodos() {
        return repositorio.findAll();
    }

    public Prestamo buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un préstamo con id " + id));
    }

    public Prestamo guardar(Prestamo prestamo) {
        return repositorio.save(prestamo);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }
}