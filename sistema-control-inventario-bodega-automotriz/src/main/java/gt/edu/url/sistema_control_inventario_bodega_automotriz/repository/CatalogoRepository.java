package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {

    // Busca un repuesto por su nombre exacto ignorando mayúsculas/minúsculas (retorna Optional)
    Optional<Catalogo> findByNombreIgnoreCase(String nombre);
}