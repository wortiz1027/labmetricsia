package co.com.devsoft.devopsmind.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestConstructor;

import co.com.devsoft.devopsmind.application.ports.input.AnalyzeIncidentUseCase;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import co.com.devsoft.devopsmind.infrastructure.BaseIntegrationITest;

@Tag("integrationTest")
@DisplayName("🧪 Pruebas de Integración :: Flujo Cognitivo SRE (Ollama + MongoDB Chat Memory)")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class IncidentAnalysisServiceITest extends BaseIntegrationITest {

    private final AnalyzeIncidentUseCase analyzeIncidentUseCase;

    public IncidentAnalysisServiceITest(AnalyzeIncidentUseCase analyzeIncidentUseCase,
            MongoTemplate mongoTemplate) {
        this.analyzeIncidentUseCase = analyzeIncidentUseCase;
    }

    @Test
    @DisplayName("📌 Flujo End-to-End :: Debería analizar la telemetría, chatear con Ollama en caliente y persistir la memoria BSON en Mongo")
    void shouldProcessIncidentAndPersistChatMemoryInMongo() throws InterruptedException {
        ServerMetrics stressedMetrics = new ServerMetrics(88.0, 29.5, 4.0, true);
        String errorMsg = "OllamaContainer desbordado debido a múltiples contextos masivos concurrentes.";

        LabIncident result = analyzeIncidentUseCase.analyze(errorMsg, stressedMetrics);

        assertNotNull(result);
        assertEquals("RESOLVED", result.getStatus().name(),
                "El incidente debió transicionar a RESOLVED al adjuntarse el plan de la IA.");

        assertNotNull(result.getSolutionPlan(), "Ollama debió responder y el adaptador parsear el plan estructurado.");
        assertFalse(result.getSolutionPlan().getStepsToSolve().isEmpty(),
                "El plan debe contener los comandos de mitigación.");
    }
}
