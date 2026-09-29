package co.com.devsoft.hosttelemetry;

import co.com.devsoft.hosttelemetry.application.bussines.ProcessService;
import co.com.devsoft.hosttelemetry.infrastructure.mcp.HostTelemetryTools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tags({
    @Tag("unitTest")
})
@ExtendWith(MockitoExtension.class)
@DisplayName("🧪 Pruebas Unitarias :: Frontera MCP Tools (HostTelemetryTools)")
public class HostTelemetryToolsUTest {

    @Mock
    private ProcessService processService;

    @InjectMocks
    private HostTelemetryTools hostTelemetryTools;

    @Test
    @DisplayName("📌 fetchTopCpuProcessesReport :: Debería delegar el límite enviado por el LLM hacia el servicio")
    void shouldDelegateWithProvidedLimit() {
        // GIVEN: El LLM nos envía un límite explícito de 5 procesos
        Integer limitInput = 5;
        String expectedResponse = "### Tabla Simulada de Markdown";
        when(processService.getTopCpuProcesses(limitInput)).thenReturn(expectedResponse);

        // WHEN: Se ejecuta la herramienta expuesta
        String actualResponse = hostTelemetryTools.fetchTopCpuProcessesReport(limitInput);

        // THEN: Validamos la transferencia de datos y la delegación limpia
        assertEquals(expectedResponse, actualResponse);
        verify(processService, times(1)).getTopCpuProcesses(limitInput);
    }

    @Test
    @DisplayName("📌 fetchTopCpuProcessesReport :: Debería aplicar el fallback por defecto de 3 si el parámetro del LLM viene nulo")
    void shouldApplyDefaultLimitOfThreeWhenInputIsNull() {
        // GIVEN: El LLM omitió el parámetro opcional (limit = null)
        Integer limitInput = null;
        int defaultFallbackLimit = 3;
        String expectedResponse = "### Tabla por defecto";
        when(processService.getTopCpuProcesses(defaultFallbackLimit)).thenReturn(expectedResponse);

        // WHEN: Se ejecuta la herramienta
        String actualResponse = hostTelemetryTools.fetchTopCpuProcessesReport(limitInput);

        // THEN: Certificamos que la regla de negocio del DTO de red aplicó el fallback de forma atómica
        assertEquals(expectedResponse, actualResponse);
        verify(processService, times(1)).getTopCpuProcesses(defaultFallbackLimit);
    }

}
