package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}