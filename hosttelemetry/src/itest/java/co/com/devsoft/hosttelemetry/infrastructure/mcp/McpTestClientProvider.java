package co.com.devsoft.hosttelemetry.infrastructure.mcp;

import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;

@Component
public class McpTestClientProvider {

    private final String protocol;

    private final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL);

    public McpTestClientProvider(@Value("${spring.ai.mcp.server.protocol:streamable}") String protocol) {
        this.protocol = protocol;
    }

    public McpSyncClient create(String baseUrl) {

        String resolvedProtocol = protocol.trim().toLowerCase();

        return switch (resolvedProtocol) {

            case "streamable" -> McpClient.sync(
                    HttpClientStreamableHttpTransport.builder(baseUrl)
                            .clientBuilder(clientBuilder)
                            .endpoint("/mcp") // Ruta estandarizada por Spring AI
                            .build())
                    .build();

            default -> throw new IllegalArgumentException(String.format("%s%s",
                    "Unrecognized MCP protocol: ", protocol));
        };
    }
}
