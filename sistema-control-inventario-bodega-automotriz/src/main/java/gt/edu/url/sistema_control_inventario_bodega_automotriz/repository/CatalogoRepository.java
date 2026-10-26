package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
    // Spring Data JPA provee automáticamente los métodos findAll, save, findById, deleteById
    List<Catalogo> findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrObservacionContainingIgnoreCase(
            String nombre, String categoria, String descripcion, String observacion);
}