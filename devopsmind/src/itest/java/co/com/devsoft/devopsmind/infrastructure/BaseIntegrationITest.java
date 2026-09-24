package co.com.devsoft.devopsmind.infrastructure;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import co.com.devsoft.devopsmind.it.shared.containers.ManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.MongoDBManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.MySQLManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.OllamaManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.PostgreSQLManagedContainer;

@Tags({
        @Tag("integrationTest")
})
@ActiveProfiles("itest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationITest {

    private static final List<ManagedContainer> CONTAINERS;

    static {
        CONTAINERS = List.of(
                new MySQLManagedContainer(),
                new PostgreSQLManagedContainer(),
                new MongoDBManagedContainer(),
                new OllamaManagedContainer());

        CONTAINERS.forEach(ManagedContainer::start);

        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> CONTAINERS.forEach(ManagedContainer::stop)));
    }

    @DynamicPropertySource
    static void configuredDynamicProperties(DynamicPropertyRegistry registry) {
        CONTAINERS.forEach(container -> container.registerProperties(registry));
    }
}
