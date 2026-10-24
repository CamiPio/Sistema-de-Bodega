package gt.edu.url.sistema_control_inventario_bodega_automotriz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutorizacionInterceptor autorizacionInterceptor;

    public WebConfig(AutorizacionInterceptor autorizacionInterceptor) {
        this.autorizacionInterceptor = autorizacionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autorizacionInterceptor);
    }
}