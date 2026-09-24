package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import co.com.devsoft.devopsmind.application.ports.input.AnalyzeIncidentUseCase;
import co.com.devsoft.devopsmind.application.ports.input.FindActiveManualsUseCase;
import co.com.devsoft.devopsmind.application.ports.input.FindIncidentByIdUseCase;
import co.com.devsoft.devopsmind.application.ports.input.IndexManualUseCase;
import co.com.devsoft.devopsmind.application.ports.input.SearchKnowledgeUseCase;
import co.com.devsoft.devopsmind.domain.exception.ResourceNotFoundException;
import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;

@Tags({
    @Tag("unitTest")
})
@GraphQlTest(LabMetricsGraphQLController.class)
@DisplayName("🧪 Pruebas Unitarias de Infraestructura :: Frontera GraphQL API")
class LabMetricsGraphQLControllerUTest {

    @Autowired
    private GraphQlTester graphQlTester;

    // 🎯 Inyectamos los simuladores de los Casos de Uso en el contexto del Grafo
    @MockitoBean
    private IndexManualUseCase indexManualUseCase;
    @MockitoBean
    private AnalyzeIncidentUseCase analyzeIncidentUseCase;
    @MockitoBean
    private FindActiveManualsUseCase findActiveManualsUseCase;
    @MockitoBean
    private FindIncidentByIdUseCase findIncidentByIdUseCase;
    @MockitoBean
    private SearchKnowledgeUseCase searchKnowledgeUseCase;

    @Test
    @DisplayName("📌 Query searchKnowledge :: Debería ejecutar el documento GraphQL y retornar la lista estructurada de Chunks")
    void shouldSearchKnowledgeAndReturnPayload() {
        // GIVEN: Una respuesta simulada del caso de uso de consulta RAG con datos del
        // dominio rico
        List<KnowledgeChunk> mockChunks = List.of(
                new KnowledgeChunk("Configuracion de balanceadores de carga en nodos distribuidos.", "LB_CFG"));
        when(searchKnowledgeUseCase.search("balanceadores", 1)).thenReturn(mockChunks);

        // El manifiesto de la consulta GraphQL en texto plano exacto (lo que enviaría
        // GraphiQL)
        String queryDocument = """
                query {
                    searchKnowledge(query: "balanceadores", maxResults: 1) {
                        content
                        sectionName
                        tokenCountEstimate
                    }
                }
                """;

        // WHEN & THEN: Disparamos la consulta contra el tester y auditamos el grafo
        // resultante
        graphQlTester.document(queryDocument)
                .execute()
                .errors().verify() // Certifica que la respuesta no contiene ningún error en el protocolo
                .path("searchKnowledge")
                .entityList(Object.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("📌 Query incidentById (Camino de Error) :: Debería interceptar la excepción de dominio y transformarla en un error oficial de GraphQL")
    void shouldHandleResourceNotFoundExceptionInGraphQL() {
        // GIVEN: El caso de uso lanza la excepción de negocio al no encontrar el ID
        String targetId = "INC-INVALID";
        when(findIncidentByIdUseCase.findById(targetId)).thenThrow(
                new ResourceNotFoundException("El incidente solicitado con ID 'INC-INVALID' no fue localizado."));

        String queryDocument = """
                query {
                    incidentById(id: "INC-INVALID") {
                        id
                        errorDescription
                    }
                }
                """;

        // WHEN & THEN: Disparamos y validamos que la llave 'errors' del JSON de la red
        // se pueble correctamente
        graphQlTester.document(queryDocument)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertEquals(1, errors.size());
                    var error = errors.get(0);
                    // 🛡️ Certificamos que tu GraphQLExceptionHandlerAdvice mapeó el código
                    // semántico oficial
                    assertEquals("NOT_FOUND", error.getErrorType().toString());
                    assertEquals("El incidente solicitado con ID 'INC-INVALID' no fue localizado.", error.getMessage());
                });
    }

    // Auxiliar utilitario para las aserciones internas
    private void assertEquals(Object expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}
