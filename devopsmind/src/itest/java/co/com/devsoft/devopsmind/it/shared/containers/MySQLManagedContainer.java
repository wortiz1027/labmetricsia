package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLManagedContainer implements ManagedContainer {

    private final MySQLContainer MYSQL;

    @SuppressWarnings("resource")
    public MySQLManagedContainer() {
        MYSQL = new MySQLContainer(DockerImageName.parse("mysql:9.7.2"))
                .withDatabaseName("labmetricsia_test_db")
                .withUsername("devops_user")
                .withPassword("devops_pass")
                .withCommand(
                        "--character-set-server=utf8mb4",
                        "--collation-server=utf8mb4_unicode_ci");
    }

    @Override
    public void start() {
        if (!MYSQL.isRunning())
            MYSQL.start();
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
        registry.add("TEST_CONTAINER_MYSQL_URL", MYSQL::getJdbcUrl);
        registry.add("TEST_CONTAINER_MYSQL_USER", MYSQL::getUsername);
        registry.add("TEST_CONTAINER_MYSQL_PASSWORD", MYSQL::getPassword);
    }

}
