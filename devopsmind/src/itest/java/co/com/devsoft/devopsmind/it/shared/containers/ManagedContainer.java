package co.com.devsoft.devopsmind.it.shared.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface ManagedContainer {

    void start();

    void stop();

    boolean isRunning();

    void registerProperties(DynamicPropertyRegistry registry);

}
