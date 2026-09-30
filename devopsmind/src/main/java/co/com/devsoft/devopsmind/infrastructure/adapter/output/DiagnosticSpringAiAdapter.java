package co.com.devsoft.devopsmind.infrastructure.adapter.output;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import co.com.devsoft.devopsmind.domain.model.DiagnosticPlan;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import co.com.devsoft.devopsmind.domain.repository.DiagnosticEngineAi;

@Component
public class DiagnosticSpringAiAdapter implements DiagnosticEngineAi {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticSpringAiAdapter.class);

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("classpath:/prompts/system-sre-agent.st")
    private Resource systemPromptResource;

    @Value("classpath:/prompts/user-sre-task.st")
    private Resource userPromptResource;

    private final ChatClient chatClient;

    public DiagnosticSpringAiAdapter(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
            SyncMcpToolCallbackProvider telemetryToolsProvider) {
        Object[] tools = (telemetryToolsProvider != null && telemetryToolsProvider.getToolCallbacks() != null)
                ? (Object[]) telemetryToolsProvider.getToolCallbacks()
                : new Object[0];

        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(tools)
                .build();
    }

    @Override
    public DiagnosticPlan generatePlan(String errorDescription, ServerMetrics metrics, String formattedContext) {
        log.info("🤖 [AI Adapter] Lanzando tubería cognitiva con renderizado de plantillas .st unificadas...");

        String currentEnv = (activeProfile != null) ? activeProfile.toUpperCase() : "DEVELOPMENT";

        String rawAiResponse = chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(systemPromptResource)
                        .param("environment", currentEnv) // Llena {environment} en system-sre-agent.st
                        .param("maxSteps", 3) // Llena {maxSteps} en system-sre-agent.st
                )
                .user(userSpec -> userSpec
                        .text(userPromptResource) // 🎯 Carga los placeholders dinámicos
                        .param("error", errorDescription) // Fills {error}
                        .param("cpu", metrics.getCpuUsagePercentage()) // Fills {cpu}
                        .param("ram", metrics.getRamUsageGigabytes()) // Fills {ram}
                        .param("context", formattedContext) // Fills {context}
                )
                .advisors(a -> a.param("chat_memory_conversation_id", "LAB-SRE-CONVERSATION-THREAD"))
                .call()
                .content();

        return parseAIResponse(rawAiResponse);
    }

    private DiagnosticPlan parseAIResponse(String response) {
        try {
            if (response == null)
                throw new IllegalArgumentException("La respuesta del LLM es nula.");

            String cleanResponse = response.replaceAll("\\n", "").trim();
            String[] parts = cleanResponse.split("\\|");

            String conclusion = parts[0].trim();
            List<String> steps = Arrays.stream(parts[1].split(","))
                    .map(s -> s.trim())
                    .collect(Collectors.toList());

            boolean reboot = Boolean.parseBoolean(parts[2].trim());

            return new DiagnosticPlan(conclusion, steps, reboot);
        } catch (Exception e) {
            log.warn("⚠️ [AI Adapter] Error de parseo en el modelo de 3B. Ejecutando plan de fallback.");
            return new DiagnosticPlan("Mitigación de contingencia: No se pudo parsear el formato estructurado del LLM.",
                    List.of("Verificar estado de sockets", "Ejecutar docker service restart"),
                    Boolean.FALSE);
        }
    }
}
