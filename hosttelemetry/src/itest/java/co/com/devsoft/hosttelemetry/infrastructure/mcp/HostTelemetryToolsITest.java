package co.com.devsoft.hosttelemetry.infrastructure.mcp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import co.com.devsoft.hosttelemetry.Application;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;

@Tags({
        @Tag("integrationTest")
})
@ActiveProfiles("itest")
@DisplayName("🧪 Pruebas de Integración E2E :: Frontera MCP Polimórfica")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HostTelemetryToolsITest {

    @LocalServerPort
    private int port;

    @Autowired
    private McpTestClientProvider clientProvider;

    private McpSyncClient mcpSyncClient;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port;
        this.mcpSyncClient = clientProvider.create(baseUrl);
        this.mcpSyncClient.initialize();
    }

    @AfterEach
    void tearDown() {
        if (this.mcpSyncClient != null) {
            this.mcpSyncClient.close();
        }
    }

    @Test
    @DisplayName("📌 E2E :: El cliente polimórfico debería descubrir las herramientas sin importar el transporte")
    void shouldDiscoverExposedMcpTool() {
        List<McpSchema.Tool> discoveredTools = mcpSyncClient.listTools().tools();

        assertNotNull(discoveredTools);
        boolean toolExists = discoveredTools.stream()
                .anyMatch(tool -> "getTopCpuProcesses".equals(tool.name()));

        assertTrue(toolExists, "La herramienta 'getTopCpuProcesses' debió ser listada por el transporte.");
    }

    @Test
    @DisplayName("📌 E2E :: Debería ejecutar el flujo sobre el protocolo seleccionado y retornar los datos estocásticos")
    void shouldCallMcpToolAndReturnDynamicMarkdownTable() {
        String toolName = "getTopCpuProcesses";
        Map<String, Object> arguments = Map.of("limit", 2);

        var callRequest = new io.modelcontextprotocol.spec.McpSchema.CallToolRequest(
                toolName,
                arguments,
                null);

        McpSchema.CallToolResult executionResult = mcpSyncClient.callTool(callRequest);

        assertNotNull(executionResult);
        assertFalse(executionResult.isError());

        List<McpSchema.Content> contentList = executionResult.content();
        assertFalse(contentList.isEmpty(), "El resultado debe contener al menos un bloque de contenido.");

        McpSchema.Content content = contentList.get(0);

        if (content instanceof io.modelcontextprotocol.spec.McpSchema.TextContent textContent) {
            String reportText = textContent.text();
            assertTrue(reportText.contains("TELEMETRÍA DE PROCESOS DEL HOST"));
            assertTrue(reportText.contains("ollama_container_daemon"));
        } else {
            String reportText = content.toString();
            assertTrue(reportText.contains("TELEMETRÍA DE PROCESOS DEL HOST"));
        }
    }
}
