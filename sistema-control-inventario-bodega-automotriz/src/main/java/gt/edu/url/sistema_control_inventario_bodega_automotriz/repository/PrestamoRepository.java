package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByEstadoActivo(boolean estadoActivo);

    boolean existsByIdProductoAndEstadoActivo(Long idProducto, boolean estadoActivo);
}