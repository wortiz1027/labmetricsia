package co.com.devsoft.devopsmind.infrastructure.adapter.input.http;

import co.com.devsoft.devopsmind.application.ports.input.*;
import co.com.devsoft.devopsmind.domain.exception.ResourceNotFoundException;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tags({
    @Tag("unitTest")
})
@WebMvcTest(LabMetricsRestController.class)
@DisplayName("🧪 Pruebas Unitarias de Infraestructura :: Frontera RESTful API")
class LabMetricsRestControllerUTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IndexManualUseCase indexManualUseCase;
    @MockitoBean
    private AnalyzeIncidentUseCase analyzeIncidentUseCase;
    @MockitoBean
    private FindIncidentByIdUseCase findIncidentByIdUseCase;
    @MockitoBean
    private SearchKnowledgeUseCase searchKnowledgeUseCase;

    @Test
    @DisplayName("📌 POST /api/v1/lab/incidents :: Debería recibir el JSON Request y retornar 201 Created con el Java Record estructurado")
    void shouldReportIncidentAndReturnCreated() throws Exception {
        // GIVEN: Un incidente procesado legítimo retornado por el caso de uso
        LabIncident analyzedIncident = new LabIncident("Error de socket HTTP");
        when(analyzeIncidentUseCase.analyze(anyString(), any(ServerMetrics.class))).thenReturn(analyzedIncident);

        // Payload JSON inmutable que mapea con IncidentReportRequest
        String requestJson = """
                {
                    "errorDescription": "Fallo crítico en HikariPool",
                    "metrics": {
                        "cpuUsagePercentage": 75.5,
                        "ramUsageGigabytes": 22.1,
                        "gpuVramUsageGigabytes": 4.0,
                        "isSwapActive": false
                    }
                }
                """;

        // WHEN & THEN: Simulamos el disparo de red vía HTTP POST
        mockMvc.perform(post("/api/v1/lab/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated()) // Valida HTTP 201
                .andExpect(jsonPath("$.errorDescription").value("Error de socket HTTP")) // Valida la respuesta DTO
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("📌 GET /api/v1/lab/incidents/{id} :: Debería retornar 404 Not Found con el DTO RestErrorResponse estructurado globalmente")
    void shouldReturnNotFoundWhenIncidentDoesNotExist() throws Exception {
        // GIVEN: El caso de uso de consulta lanza la excepción semántica de negocio
        String fakeId = "INC-40404";
        when(findIncidentByIdUseCase.findById(fakeId)).thenThrow(
                new ResourceNotFoundException("El incidente con ID '" + fakeId + "' no existe en los registros."));

        // WHEN & THEN: Disparamos la consulta HTTP GET
        mockMvc.perform(get("/api/v1/lab/incidents/" + fakeId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // 🛡️ Valida HTTP 404 mapeado por tu @RestControllerAdvice
                .andExpect(jsonPath("$.errorType").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("El incidente con ID 'INC-40404' no existe en los registros."))
                .andExpect(jsonPath("$.path").value("/api/v1/lab/incidents/" + fakeId))
                .andExpect(jsonPath("$.timestamp").exists()); // Estandarización de auditoría inmutable
    }
}
