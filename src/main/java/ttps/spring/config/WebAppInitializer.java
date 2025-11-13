package ttps.spring.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
//import org.springframework.web.servlet.DispatcherServlet;

/**
 * Configuración de inicialización de la aplicación web Spring sin web.xml.
 * Esta clase reemplaza la configuración tradicional de web.xml para aplicaciones Spring.
 */
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Crear el contexto raíz de la aplicación
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        // Gestionar el ciclo de vida del contexto raíz
        servletContext.addListener(new ContextLoaderListener(rootContext));

        // Opcional: Si necesitas DispatcherServlet para Spring MVC
        // AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        // ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
        // dispatcher.setLoadOnStartup(1);
        // dispatcher.addMapping("/");
    }
}

