package ttps.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuración principal de la aplicación Spring.
 * Importa la configuración de persistencia y escanea los componentes de Spring.
 */
@Configuration
@ComponentScan(basePackages = {
    "ttps.spring.services",
    "ttps.spring.persistence.dao"
})
@Import(PersistenceConfig.class)
public class AppConfig {
    // Esta clase puede extenderse con más configuraciones según sea necesario
}


