package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLManagedContainer implements ManagedContainer {

    private final PostgreSQLContainer POSTGRES;

    @SuppressWarnings("resource")
    public PostgreSQLManagedContainer() {
        POSTGRES = new PostgreSQLContainer(DockerImageName.parse("pgvector/pgvector:pg16"))
                .withDatabaseName("vector_store_test_db")
                .withUsername("postgres_user")
                .withPassword("postgres_pass");
    }

    @Override
    public void start() {
        if (!POSTGRES.isRunning())
            POSTGRES.start();
    }

    @Override
    public void stop() {
        if (POSTGRES.isRunning())
            POSTGRES.stop();
    }

    @Override
    public boolean isRunning() {
        return POSTGRES.isRunning();
    }

    @Override
    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("TEST_CONTAINER_POSTGRES_URL", POSTGRES::getJdbcUrl);
        registry.add("TEST_CONTAINER_POSTGRES_USER", POSTGRES::getUsername);
        registry.add("TEST_CONTAINER_POSTGRES_PASSWORD", POSTGRES::getPassword);
    }

}
