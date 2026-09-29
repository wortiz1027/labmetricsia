package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLManagedContainer implements ManagedContainer {

    private final PostgreSQLContainer POSTGRES;
    private final String NETWORK_ALIAS = "ntw_ai_itest";
    private final String CONTAINER_IMAGE_NAME = "pgvector/pgvector:pg16";

    @SuppressWarnings("resource")
    public PostgreSQLManagedContainer() {
        POSTGRES = new PostgreSQLContainer(DockerImageName.parse(CONTAINER_IMAGE_NAME))
                .withDatabaseName("vector_store_test_db")
                .withUsername("postgres_user")
                .withPassword("postgres_pass");
    }

    @Override
    public void start(Network network) {
        if (!POSTGRES.isRunning()) {
            POSTGRES.withNetwork(network)
                    .withNetworkAliases(NETWORK_ALIAS);

            POSTGRES.start();
        }
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
