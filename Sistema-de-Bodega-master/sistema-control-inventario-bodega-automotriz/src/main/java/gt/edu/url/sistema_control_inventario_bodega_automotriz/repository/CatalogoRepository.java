package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;

public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
}