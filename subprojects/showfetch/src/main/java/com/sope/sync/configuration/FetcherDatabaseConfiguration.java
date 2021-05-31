package com.sope.sync.configuration;

import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({ "classpath:sopeapplication.properties" })
@EnableTransactionManagement
public class FetcherDatabaseConfiguration {

	@Inject
	private Environment environment;

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(new String[] { "com.sope.domain" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty("datasource.driverClassName"));
		dataSource.setUrl(environment.getRequiredProperty("datasource.url"));
		dataSource.setUsername(environment.getRequiredProperty("datasource.username"));
		dataSource.setPassword(environment.getRequiredProperty("datasource.password"));
		return dataSource;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		
		properties.put("hibernate.c3p0.min_size", "5");
		properties.put("hibernate.c3p0.max_size", "20");
		properties.put("hibernate.c3p0.timeout", "300");
		properties.put("hibernate.c3p0.max_statements", "50");
		properties.put("hibernate.c3p0.idle_test_period", "3000");
        properties.put("hibernate.cache.use_second_level_cache", "false");
		return properties;
	}

	@Bean
	@Inject
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory);
		return transactionManager;
	}
}