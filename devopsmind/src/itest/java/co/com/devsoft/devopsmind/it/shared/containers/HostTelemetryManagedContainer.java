package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class HostTelemetryManagedContainer implements ManagedContainer {

    @Container
    private final GenericContainer<?> MCP_SERVER_HOST_TELEMETRY;
    private static final int INTERNAL_PORT = 8079;
    private final String NETWORK_ALIAS = "ntw_ai_itest";
    private final String CONTAINER_IMAGE_NAME = "wortiz1027/mcp-server-host-telemetry:latest";

    @SuppressWarnings("resource")
    public HostTelemetryManagedContainer() {
        MCP_SERVER_HOST_TELEMETRY = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE_NAME))
                .withExposedPorts(INTERNAL_PORT);
    }

    @Override
    public void start(Network network) {
        if (!MCP_SERVER_HOST_TELEMETRY.isRunning()) {
            MCP_SERVER_HOST_TELEMETRY.withNetwork(network)
                    .withNetworkAliases(NETWORK_ALIAS);

            MCP_SERVER_HOST_TELEMETRY.start();
        }
    }

    @Override
    public void stop() {
        if (MCP_SERVER_HOST_TELEMETRY.isRunning())
            MCP_SERVER_HOST_TELEMETRY.stop();
    }

    @Override
    public boolean isRunning() {
        return MCP_SERVER_HOST_TELEMETRY.isRunning();
    }

    @Override
    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("MCP_SERVER_HOSTNAME", () -> MCP_SERVER_HOST_TELEMETRY.getHost());
        registry.add("MCP_SERVER_PORT", () -> MCP_SERVER_HOST_TELEMETRY.getMappedPort(INTERNAL_PORT));
        registry.add("BASE_LOGS_DIR", () -> "/app/logs");
    }

}
