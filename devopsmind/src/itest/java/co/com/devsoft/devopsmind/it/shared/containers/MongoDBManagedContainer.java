package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoDBManagedContainer implements ManagedContainer {

    private static final String EXPECTED_DATABASE = "ai_chat_memory";
    private final String NETWORK_ALIAS = "ntw_ai_itest";
    private final String CONTAINER_IMAGE_NAME = "mongo:latest";

    @Container
    @SuppressWarnings("resource")
    private final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse(CONTAINER_IMAGE_NAME))
            .withExposedPorts(27017)
            .withEnv("MONGO_INITDB_DATABASE", EXPECTED_DATABASE);

    public MongoDBManagedContainer() {
    }

    @Override
    public void start(Network network) {
        if (!MONGO.isRunning()) {
            MONGO.withNetwork(network)
                    .withNetworkAliases(NETWORK_ALIAS);

            MONGO.start();
        }
    }

    @Override
    public void stop() {
        if (MONGO.isRunning())
            MONGO.stop();
    }

    @Override
    public boolean isRunning() {
        return MONGO.isRunning();
    }

    @Override
    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("TEST_CONTAINER_MONGO_URI", () -> MONGO.getReplicaSetUrl(EXPECTED_DATABASE));
    }

    public String getRealReplicaSetUrl() {
        return MONGO.getReplicaSetUrl(EXPECTED_DATABASE);
    }
}
