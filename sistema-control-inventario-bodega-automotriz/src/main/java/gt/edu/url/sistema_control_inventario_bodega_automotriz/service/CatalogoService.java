package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.List;
import org.springframework.stereotype.Service;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;

@Service
public class CatalogoService {

    // Repositorio inyectado en lugar del ArrayList en memoria
    private final CatalogoRepository catalogoRepository;

    // Inyección mediante constructor
    public CatalogoService(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    public void guardar(Catalogo catalogo) {
        // Validación: Si la disponibilidad viene vacía o nula, 
        // le asigna automáticamente el mismo valor que tenga la cantidad total.
        if (catalogo.getDisponibilidad() == null || catalogo.getDisponibilidad().trim().isEmpty()) {
            catalogo.setDisponibilidad(String.valueOf(catalogo.getCantidad()));
        }
        
        catalogoRepository.save(catalogo);
    }

    public List<Catalogo> listarTodos() {
        return catalogoRepository.findAll();
    }

    public long contar() {
        return catalogoRepository.count();
    }

    public Catalogo buscarPorId(Long id) {
        return catalogoRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        catalogoRepository.deleteById(id);
    }
}