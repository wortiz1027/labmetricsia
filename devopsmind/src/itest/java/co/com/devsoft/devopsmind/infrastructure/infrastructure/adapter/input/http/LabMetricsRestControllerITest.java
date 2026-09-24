package co.com.devsoft.devopsmind.infrastructure.infrastructure.adapter.input.http;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.reactive.server.WebTestClient;

import co.com.devsoft.devopsmind.infrastructure.BaseIntegrationITest;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.IncidentReportRequest;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.ServerMetricsRequest;

@Tags({
        @Tag("integrationTest")
})
@AutoConfigureWebTestClient
@DisplayName("🧪 Integración End-to-End :: Frontera RESTful API con WebTestClient")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class LabMetricsRestControllerITest extends BaseIntegrationITest {

    // @LocalServerPort
    // private int port;
    private final WebTestClient webTestClient;

    // 🎯 CALIBRACIÓN DE TIMEOUT POR CONSTRUCTOR
    public LabMetricsRestControllerITest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMinutes(1))
                .build();
    }

    @Test
    @DisplayName("📌 POST /api/v1/lab/incidents :: Flujo E2E Completo")
    void shouldReportIncidentThroughNetworkAndReturn201() {
        ServerMetricsRequest metrics = new ServerMetricsRequest(92.5, 28.0, 6.0, true);
        IncidentReportRequest request = new IncidentReportRequest("Fallo crítico en el pool de sockets de red",
                metrics);

        webTestClient.post()
                .uri("/api/v1/lab/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("RESOLVED")
                .jsonPath("$.solutionPlan.analysisConclusion").exists()
                .jsonPath("$.solutionPlan.stepsToSolve").isNotEmpty();
    }

}
