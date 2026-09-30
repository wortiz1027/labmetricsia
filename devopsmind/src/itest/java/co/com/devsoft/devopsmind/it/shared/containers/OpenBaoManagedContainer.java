package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class OpenBaoManagedContainer implements ManagedContainer {

    @Container
    private GenericContainer<?> OPENBAO;
    private static final int OPENBAO_PORT = 8200;
    private static final String ROOT_TOKEN = "01a0f069-db3f-7c49-8d64-b4b68e4ccb02";
    private final String NETWORK_ALIAS = "ntw_ai_itest";
    private final String CONTAINER_IMAGE_NAME = "openbao/openbao:latest";

    @SuppressWarnings("resource")
    public OpenBaoManagedContainer() {
        OPENBAO = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE_NAME))
                .withExposedPorts(OPENBAO_PORT)
                .withCommand("server", "-dev", "-dev-root-token-id=".formatted(ROOT_TOKEN))
                .withEnv("BAO_ALLOW_INSECURE_GATEWAYS", "true");
    }

    @Override
    public void start(Network network) {
        if (!OPENBAO.isRunning()) {
            OPENBAO.withNetwork(network)
                    .withNetworkAliases(NETWORK_ALIAS);

            OPENBAO.start();
        }
    }

    @Override
    public void stop() {
        if (OPENBAO.isRunning())
            OPENBAO.stop();
    }

    @Override
    public boolean isRunning() {
        return OPENBAO.isRunning();
    }

    @Override
    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("SECRET_MANAGER_HOSTNAME", OPENBAO::getHost);
        registry.add("SECRET_MANAGER_PORT", () -> OPENBAO.getMappedPort(OPENBAO_PORT));
        registry.add("TOKEN_SECRET_MANAGER", () -> ROOT_TOKEN);
    }

}
