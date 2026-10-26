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
        if (catalogo.getCantidad() == null || catalogo.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero igual o mayor que cero.");
        }
        Catalogo existente = catalogo.getId() == null
                ? null
                : catalogoRepository.findById(catalogo.getId())
                        .orElseThrow(() -> new IllegalArgumentException("No existe la herramienta que intentas actualizar."));
        if (existente == null) {
            catalogo.setDisponibilidad(catalogo.getCantidad());
        } else {
            if (existente.getCantidad() == null || existente.getCantidad() < 0) {
                throw new IllegalArgumentException("La cantidad almacenada para esta herramienta no es válida.");
            }
            Integer disponibilidadAnterior = existente.getDisponibilidad();
            int disponiblesAntes = disponibilidadAnterior == null
                    ? existente.getCantidad()
                    : disponibilidadAnterior;
            if (disponiblesAntes < 0 || disponiblesAntes > existente.getCantidad()) {
                throw new IllegalArgumentException("La disponibilidad almacenada para esta herramienta no es válida.");
            }
            int unidadesPrestadas = existente.getCantidad() - disponiblesAntes;
            if (catalogo.getCantidad() < unidadesPrestadas) {
                throw new IllegalArgumentException("La cantidad no puede ser menor que las herramientas actualmente prestadas.");
            }
            catalogo.setDisponibilidad(catalogo.getCantidad() - unidadesPrestadas);
        }

        catalogoRepository.save(catalogo);
    }

    public List<Catalogo> listarTodos() {
        return catalogoRepository.findAll();
    }

    public List<Catalogo> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarTodos();
        }
        String consulta = termino.trim();
        return catalogoRepository
                .findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrObservacionContainingIgnoreCase(
                        consulta, consulta, consulta, consulta);
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