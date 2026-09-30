package co.com.devsoft.devopsmind.infrastructure;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;

import co.com.devsoft.devopsmind.it.shared.containers.HostTelemetryManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.ManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.MongoDBManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.MySQLManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.OllamaManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.OpenBaoManagedContainer;
import co.com.devsoft.devopsmind.it.shared.containers.PostgreSQLManagedContainer;

@Tags({
        @Tag("integrationTest")
})
@ActiveProfiles("itest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationITest {

    protected static final Network SHARED_NETWORK = Network.newNetwork();

    private static final List<ManagedContainer> CONTAINERS = List.of(
            new MySQLManagedContainer(),
            new PostgreSQLManagedContainer(),
            new MongoDBManagedContainer(),
            new OllamaManagedContainer(),
            new HostTelemetryManagedContainer(),
            new OpenBaoManagedContainer());
    static {
        CONTAINERS.forEach(container -> container.start(SHARED_NETWORK));

        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> CONTAINERS.forEach(container -> container.stop())));
    }

    @DynamicPropertySource
    static void configuredDynamicProperties(DynamicPropertyRegistry registry) {
        CONTAINERS.forEach(container -> container.registerProperties(registry));
    }
}
