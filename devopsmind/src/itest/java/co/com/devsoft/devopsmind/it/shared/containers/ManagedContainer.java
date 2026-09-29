package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface ManagedContainer {

    void start(Network network);

    void stop();

    boolean isRunning();

    void registerProperties(DynamicPropertyRegistry registry);

}
