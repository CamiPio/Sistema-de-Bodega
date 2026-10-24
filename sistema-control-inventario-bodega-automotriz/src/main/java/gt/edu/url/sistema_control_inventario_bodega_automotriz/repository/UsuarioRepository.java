package gt.edu.url.sistema_control_inventario_bodega_automotriz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.RolUsuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Busca usuarios cuyo nombre contenga el texto ingresado
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    long countByRol(RolUsuario rol);
}
