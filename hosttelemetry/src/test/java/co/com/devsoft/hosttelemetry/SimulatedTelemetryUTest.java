package co.com.devsoft.hosttelemetry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.devsoft.hosttelemetry.application.bussines.ProcessService;
import co.com.devsoft.hosttelemetry.domain.ProcessInfo;
import co.com.devsoft.hosttelemetry.domain.Result;
import co.com.devsoft.hosttelemetry.domain.TelemetryProvider;

@Tags({
    @Tag("unitTest")
})
@ExtendWith(MockitoExtension.class)
@DisplayName("🧪 Pruebas Unitarias :: Proveedor de Telemetría Estocástica")
public class SimulatedTelemetryUTest {

    @Mock
    private TelemetryProvider telemetryProvider;

    @InjectMocks
    private ProcessService process;

    @Test
    @DisplayName("📌 getTopCpuProcesses :: Debería retornar una tabla Markdown perfecta cuando el dominio responde Success")
    void shouldReturnMarkdownTableOnSuccess() {
        int limit = 2;
        List<ProcessInfo> mockProcess = List.of(
                new ProcessInfo(1111, "test_process_1", 50.0, "1.0 GB"),
                new ProcessInfo(2222, "test_process_2", 40.0, "2.0 GB"));

        when(telemetryProvider.getTopCpuProcesses(limit)).thenReturn(Result.success(mockProcess));

        String resultMarkdown = process.getTopCpuProcesses(limit);

        assertNotNull(resultMarkdown);
        assertTrue(resultMarkdown.contains("TELEMETRÍA DE PROCESOS DEL HOST"));
        assertTrue(resultMarkdown.contains("test_process_1"));
        assertTrue(resultMarkdown.contains("test_process_2"));

        verify(telemetryProvider, times(1)).getTopCpuProcesses(limit);
    }

    @Test
    @DisplayName("📌 getTopCpuProcesses :: Debería retornar un mensaje de error mitigado cuando el dominio responde Failure")
    void shouldReturnErrorMessageOnFailure() {
        // GIVEN: Forzamos un escenario trágico en el mock
        int limit = 3;
        String fakeErrorMessage = "Acceso denegado al kernel del host por restricciones de seguridad.";

        when(telemetryProvider.getTopCpuProcesses(limit))
                .thenReturn(Result.failure(fakeErrorMessage, new RuntimeException("OS Error")));

        // WHEN: Ejecutamos el método
        String resultMarkdown = process.getTopCpuProcesses(limit);

        // THEN: Validamos la contención del fallo funcional a través del .fold()
        assertNotNull(resultMarkdown);
        assertTrue(resultMarkdown.contains("❌ FALLO DE TELEMETRÍA"));
        assertTrue(resultMarkdown.contains(fakeErrorMessage));

        verify(telemetryProvider, times(1)).getTopCpuProcesses(limit);
    }

}
