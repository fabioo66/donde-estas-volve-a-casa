package ttps.spring.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration   // Marks the class as a configuration class for Spring.
@EnableTransactionManagement   //Enables declarative transaction management via Spring's @Transactional annotation.
@ComponentScan(basePackages = "ttps.spring")
public class PersistenceConfig {
 	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUsername("valen");
		driverManagerDataSource.setPassword("valen");
		driverManagerDataSource.setUrl("jdbc:mysql://localhost:3307/proyectoTTPS?useSSL=false&amp;serverTimezone=UTC");
		driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		return driverManagerDataSource;
	}
	

	/**
	 * El localContainerEntityManagerFactoryBean es un componente de Spring que facilita la configuración de la 
	 * fábrica de administradores de entidades de JPA. Este bean permite la integración de JPA con el contenedor
	 * de Spring, proporcionando una forma sencilla de gestionar las entidades y las transacciones.
	 * @return
	 */
	
	@Bean 
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan("ttps.spring");
		emf.setEntityManagerFactoryInterface(EntityManagerFactory.class);
		JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		emf.setJpaVendorAdapter(jpaVendorAdapter);
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create");
        emf.setJpaProperties(jpaProperties);
		return emf;		
	}
		
	@Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
