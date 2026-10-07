package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}