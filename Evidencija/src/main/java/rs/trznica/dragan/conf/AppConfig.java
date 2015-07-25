package rs.trznica.dragan.conf;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({"classpath:database.properties"})
@EnableJpaRepositories(basePackages = "rs.trznica.dragan.dao")
@EnableTransactionManagement
public class AppConfig {

	@Resource
	private Environment env;

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getRequiredProperty("db.driver"));
		dataSource.setUrl(env.getRequiredProperty("db.url"));
		dataSource.setUsername(env.getRequiredProperty("db.username"));
		dataSource.setPassword(env.getRequiredProperty("db.password"));
		return dataSource;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("hibernate.ejb.naming_strategy"));
		properties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
		properties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
		return properties;
	}

	@Bean
	@DependsOn("dataSource")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
		factoryBean.setPackagesToScan("rs.trznica.dragan.entities.tankovanje");
		factoryBean.setJpaProperties(hibernateProperties());
		return factoryBean;
	}

	@Bean
	@DependsOn("entityManagerFactory")
	public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
		return transactionManager;
	}
}
