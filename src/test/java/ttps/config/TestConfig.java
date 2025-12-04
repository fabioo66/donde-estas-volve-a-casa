package ttps.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para tests con Spring Boot.
 * @SpringBootTest carga automáticamente toda la configuración de la aplicación.
 */
@Configuration
@SpringBootTest(classes = ttps.spring.Application.class)
public class TestConfig {
    // Spring Boot Test autoconfigura todo lo necesario para los tests
}
