package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

public class OllamaManagedContainer implements ManagedContainer {

    private final OllamaContainer OLLAMA;
    private final String NETWORK_ALIAS = "ntw_ai_itest";
    private final String CONTAINER_IMAGE_NAME = "ollama/ollama:latest";

    @SuppressWarnings("resource")
    public OllamaManagedContainer() {
        OLLAMA = new OllamaContainer(DockerImageName.parse(CONTAINER_IMAGE_NAME))
                .withCommand("serve");
    }

    @Override
    public void start(Network network) {
        if (!OLLAMA.isRunning()) {
            OLLAMA.withNetwork(network)
                    .withNetworkAliases(NETWORK_ALIAS);

            OLLAMA.start();
            
            try {
                OLLAMA.execInContainer("ollama", "pull", "mxbai-embed-large");
                OLLAMA.execInContainer("ollama", "pull", "llama3.2");
            } catch (Exception e) {
                throw new RuntimeException("Error inicializando entorno cognitivo de integración con ollama", e);
            }
        }
    }

    @Override
    public void stop() {
        if (OLLAMA.isRunning())
            OLLAMA.stop();
    }

    @Override
    public boolean isRunning() {
        return OLLAMA.isRunning();
    }

    @Override
    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("TEST_CONTAINER_OLLAMA_URL", OLLAMA::getEndpoint);
    }

}
