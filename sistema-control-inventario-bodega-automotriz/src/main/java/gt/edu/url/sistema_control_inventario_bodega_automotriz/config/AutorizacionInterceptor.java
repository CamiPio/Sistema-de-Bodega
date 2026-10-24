package gt.edu.url.sistema_control_inventario_bodega_automotriz.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.UsuarioRepository;

@Component
public class AutorizacionInterceptor implements HandlerInterceptor {

    private final UsuarioRepository usuarioRepository;

    public AutorizacionInterceptor(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean rutaProtegida = ruta.startsWith("/catalogo") || ruta.startsWith("/prestamos")
                || ruta.startsWith("/usuarios");

        if (!rutaProtegida) {
            return true;
        }

        var session = request.getSession(false);
        Object usuarioId = session == null
                ? null
            : session.getAttribute("usuarioId");
        if (session == null || usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        Usuario usuario = usuarioRepository.findById((Long) usuarioId).orElse(null);
        if (usuario == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        String rol = usuario.getRol().name();
        session.setAttribute("usuarioRol", rol);
        boolean requiereAdmin = ruta.startsWith("/usuarios")
                || ruta.equals("/catalogo/guardar")
                || ruta.startsWith("/catalogo/editar/")
                || ruta.startsWith("/catalogo/eliminar/");
        if (requiereAdmin && !"ADMIN".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/catalogo?accesoDenegado");
            return false;
        }

        return true;
    }
}