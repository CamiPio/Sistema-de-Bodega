package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // Método para buscar préstamos según su estado activo (ej. 1 para activos, 0 para devueltos)
    List<Prestamo> findByEstadoActivo(int estadoActivo);
}