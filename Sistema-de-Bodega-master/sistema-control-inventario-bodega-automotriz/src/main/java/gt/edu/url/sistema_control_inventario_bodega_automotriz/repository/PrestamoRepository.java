package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
}