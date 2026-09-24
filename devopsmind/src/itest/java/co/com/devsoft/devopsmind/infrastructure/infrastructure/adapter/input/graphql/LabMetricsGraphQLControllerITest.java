package co.com.devsoft.devopsmind.infrastructure.infrastructure.adapter.input.graphql;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.TestConstructor;

import co.com.devsoft.devopsmind.infrastructure.BaseIntegrationITest;

@Tag("integrationTest")
@AutoConfigureHttpGraphQlTester
@DisplayName("🧪 Integración End-to-End :: Frontera GraphQL API")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class LabMetricsGraphQLControllerITest extends BaseIntegrationITest {

    private final HttpGraphQlTester graphQlTester;

    public LabMetricsGraphQLControllerITest(HttpGraphQlTester httpGraphQlTester) {
        // WebTestClient.Builder clientBuilder = WebTestClient.bindToServer()
        // .baseUrl("/graphql");

        // WebTestClient timedWebTestClient = clientBuilder.build().mutate()
        // .responseTimeout(Duration.ofMinutes(1))
        // .build();

        // this.graphQlTester = HttpGraphQlTester.create(timedWebTestClient);
        this.graphQlTester = httpGraphQlTester.mutate()
                .webTestClient(builder -> builder.responseTimeout(Duration.ofMinutes(1)))
                .build();
    }

    @Test
    @DisplayName("📌 Mutation reportIncident :: Debería ejecutar el documento GraphQL por red, invocar Ollama y retornar el Grafo estructurado")
    void shouldExecuteGraphQLMutationAndReturnValidPayloadE2E() {
        String mutationDocument = """
                mutation {
                    reportIncident(
                        errorDescription: "Alerta de desbordamiento de sockets en el kernel del Ryzen 7",
                        metrics: {
                            cpuUsagePercentage: 95.0,
                            ramUsageGigabytes: 30.0,
                            gpuVramUsageGigabytes: 2.0,
                            isSwapActive: true
                        }
                    ) {
                        id
                        errorDescription
                        status
                        solutionPlan {
                            analysisConclusion
                        }
                    }
                }
                """;

        graphQlTester.document(mutationDocument)
                .execute()
                .errors().verify()
                .path("reportIncident")
                .hasValue()
                .path("reportIncident.status").entity(String.class).isEqualTo("RESOLVED")
                .path("reportIncident.solutionPlan.analysisConclusion").hasValue()
                .path("reportIncident.errorDescription").entity(String.class)
                .satisfies(description -> assertTrue(description.contains("Alerta de desbordamiento"),
                        "\"La descripción devuelta en el grafo debe contener el texto original."));
    }
}
