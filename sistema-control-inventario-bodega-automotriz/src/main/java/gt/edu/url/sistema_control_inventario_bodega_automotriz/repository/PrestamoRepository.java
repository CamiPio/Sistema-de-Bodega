package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
}