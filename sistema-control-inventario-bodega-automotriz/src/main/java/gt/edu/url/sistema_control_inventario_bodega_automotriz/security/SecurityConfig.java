package gt.edu.url.sistema_control_inventario_bodega_automotriz.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Usuario;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.service.UsuarioService;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            UsuarioDetailsService usuarioDetailsService,
                                            UsuarioService usuarioService,
                                            SecurityContextRepository securityContextRepository) throws Exception {
        http
            .userDetailsService(usuarioDetailsService)
            .securityContext(contexto -> contexto.securityContextRepository(securityContextRepository))
            .authorizeHttpRequests(reglas -> reglas
                .requestMatchers("/", "/login", "/login.css", "/styles.css", "/barraMenu.css",
                        "/**/*.css", "/**/*.js", "/auth/login", "/auth/registrar",
                        "/error", "/acceso-denegado", "/h2-console/**").permitAll()
                .requestMatchers("/usuarios/**", "/catalogo/nuevo", "/catalogo/guardar",
                        "/catalogo/editar/**", "/catalogo/eliminar/**", "/formulario",
                        "/prestamos/guardar", "/prestamos/historial").hasRole("ADMIN")
                .requestMatchers("/prestamos/solicitar").hasRole("USUARIO")
                .requestMatchers("/prestamos/aprobar/**", "/prestamos/rechazar/**",
                        "/prestamos/devolver/**").hasRole("ADMIN")
                .requestMatchers("/catalogo/**", "/prestamos/**", "/perfil/**").authenticated()
                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .failureUrl("/login?error")
                .successHandler((request, response, authentication) -> {
                    Usuario usuario = usuarioService.buscarPorCorreo(authentication.getName())
                            .orElseThrow(() -> new IllegalStateException(
                                    "No se encontró el usuario autenticado."));
                    var session = request.getSession(true);
                    session.setAttribute("usuarioId", usuario.getId());
                    session.setAttribute("usuarioRol", usuario.getRol().name());
                    response.sendRedirect(request.getContextPath() + "/catalogo");
                }))
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .exceptionHandling(excepciones -> excepciones
                .accessDeniedPage("/acceso-denegado"));

        return http.build();
    }
}