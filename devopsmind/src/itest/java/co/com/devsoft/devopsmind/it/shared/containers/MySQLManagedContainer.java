package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLManagedContainer implements ManagedContainer {

    private final MySQLContainer MYSQL;
    private final String NETWORK_ALIAS = "ntw_ai_itest";
    private final String CONTAINER_IMAGE_NAME = "mysql:9.7.2";

    @SuppressWarnings("resource")
    public MySQLManagedContainer() {
        MYSQL = new MySQLContainer(DockerImageName.parse(CONTAINER_IMAGE_NAME))
                .withDatabaseName("labmetricsia_test_db")
                .withUsername("devops_user")
                .withPassword("devops_pass")
                .withCommand(
                        "--character-set-server=utf8mb4",
                        "--collation-server=utf8mb4_unicode_ci");
    }

    @Override
    public void start(Network network) {
        if (!MYSQL.isRunning()) {
            MYSQL.withNetwork(network)
                    .withNetworkAliases(NETWORK_ALIAS);

            MYSQL.start();
        }
    }

    @Override
    public void stop() {
        if (MYSQL.isRunning())
            MYSQL.stop();
    }

    @Override
    public boolean isRunning() {
        return MYSQL.isRunning();
    }

    @Override
    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("TEST_CONTAINER_MYSQL_URL",
                () -> MYSQL.getJdbcUrl() + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("TEST_CONTAINER_MYSQL_USER", MYSQL::getUsername);
        registry.add("TEST_CONTAINER_MYSQL_PASSWORD", MYSQL::getPassword);
    }

}
