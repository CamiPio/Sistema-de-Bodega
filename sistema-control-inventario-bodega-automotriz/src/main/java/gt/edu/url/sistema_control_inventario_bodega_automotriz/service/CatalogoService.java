package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;

@Service
public class CatalogoService {

    private final CatalogoRepository repositorio;

    // Inyección de dependencias por constructor (Estándar de clase)
    public CatalogoService(CatalogoRepository repositorio) {
        this.repositorio = repositorio;
    }

    // Listar todos los registros desde la base de datos
    public List<Catalogo> listarTodos() {
        return repositorio.findAll();
    }

    // Buscar por ID con manejo seguro de excepciones
    public Catalogo buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un repuesto con id " + id));
    }

    // Guardar (Funciona tanto para INSERT como para UPDATE gracias al ID)
    public Catalogo guardar(Catalogo catalogo) {
        return repositorio.save(catalogo);
    }

    // Eliminar por ID
    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

    // Retorna el total de registros convirtiendo el long de JPA a int
    public int contar() {
        return (int) repositorio.count();
    }

    // Busca por nombre
    public Catalogo buscarPorNombre(String nombre) {
        return repositorio.findByNombreIgnoreCase(nombre).orElse(null);
    }
}