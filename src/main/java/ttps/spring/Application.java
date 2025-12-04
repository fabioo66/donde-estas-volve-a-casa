package ttps.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Clase principal de la aplicación Spring Boot
 * "Dónde Estás, Volvé a Casa" - Sistema de búsqueda y reporte de mascotas perdidas
 */
@SpringBootApplication
@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
