package ttps.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ttps.spring.config.PersistenceConfig;

@Configuration
@EnableTransactionManagement
@Import(PersistenceConfig.class)
@ComponentScan(basePackages = {
    "ttps.spring.persistence.dao.impl",
    "ttps.spring.services",
    "ttps.spring.models"
})
public class TestConfig {
    // Esta clase permite que Spring cargue todos los beans necesarios para los tests
}

