package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoDBManagedContainer implements ManagedContainer {
    private static final String EXPECTED_DATABASE = "ai_chat_memory";

    @Container
    private final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    public MongoDBManagedContainer() {
    }

    @Override
    public void start() {
        if (!MONGO.isRunning())
            MONGO.start();
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
