package utils.database.memory;

import java.util.Random;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan({ "com.sope" })
public class InMemoryDatabaseConfiguration {

    @Bean
    public DataSource dataSource() {
        String randomName = "MEMORY_TEST_" + new Random().nextInt(Integer.MAX_VALUE);
        String jdbcUrl = String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=0;DB_CLOSE_ON_EXIT=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS %s", randomName, randomName);

        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");

        System.out.println("Using memory url: " + jdbcUrl);
        config.addDataSourceProperty("url", jdbcUrl);
        return new HikariDataSource(config);
    }
}
