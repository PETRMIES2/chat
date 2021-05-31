package utils;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
public class EntityBuilderSessionFactoryConfig {

    public static final String ENTITY_BUILDER_SESSION_FACTORY = "entityBuilderSessionFactory";

    @Inject
    @Bean(name = ENTITY_BUILDER_SESSION_FACTORY)
    public LocalSessionFactoryBean testDataSessionFactory(DataSource dataSource) throws PropertyVetoException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(new String[] { "com.sope.domain" });
        sessionFactory.setHibernateProperties(entityBuilderHibernateProperties());
        return sessionFactory;
    }

    private Properties entityBuilderHibernateProperties() {
        Properties entityBuilderHibernateProperties = new Properties();
        entityBuilderHibernateProperties.setProperty("hibernate.show_sql.auto", "false");
        entityBuilderHibernateProperties.setProperty("hibernate.format_sql", "false");
        entityBuilderHibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        entityBuilderHibernateProperties.setProperty("hibernate.connection.characterEncoding", "UTF-8");
        entityBuilderHibernateProperties.setProperty("hibernate.connection.charSet", "UTF-8");
        entityBuilderHibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "false");
        entityBuilderHibernateProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        entityBuilderHibernateProperties.setProperty("hibernate.max_fetch_depth", "3");
        entityBuilderHibernateProperties.setProperty("hibernate.generate_statistics", "false");
        entityBuilderHibernateProperties.setProperty("hibernate.jdbc.batch_size", "100");
        entityBuilderHibernateProperties.setProperty("hibernate.max_fetch_depth", "3");
        entityBuilderHibernateProperties.setProperty("hibernate.order_updates", "false");
        entityBuilderHibernateProperties.setProperty("hibernate.use_sql_comments", "false");
        entityBuilderHibernateProperties.setProperty("hibernate.jdbc.use_get_generated_keys", "true");
        entityBuilderHibernateProperties.setProperty("hibernate.cache.use_query_cache", "false");
        entityBuilderHibernateProperties.setProperty("hibernate.id.new_generator_mappings", "false");
        return entityBuilderHibernateProperties;
    }
}
