package co.com.devsoft.devopsmind.infrastructure.config.database;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class SQLDatasourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.storages.providers.mysql")
    public DataSourceProperties mysqlProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource mysqlDataSource() {
        HikariDataSource dataSource = mysqlProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        runFlywayMigration(
                dataSource,
                "classpath:db/migration/mysql",
                "schema_version_mysql");

        return dataSource;
    }

    @Bean
    @ConfigurationProperties("spring.storages.providers.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource() {
        HikariDataSource dataSource = postgresDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        runFlywayMigration(
                dataSource,
                "classpath:db/migration/postgres",
                "schema_version_postgres");

        return dataSource;
    }

    private void runFlywayMigration(DataSource dataSource, String location, String table) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(location)
                .table(table)
                .baselineOnMigrate(Boolean.TRUE)
                .load();

        flyway.migrate();
    }
}
