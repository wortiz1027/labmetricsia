package co.com.devsoft.devopsmind.infrastructure.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import co.com.devsoft.devopsmind.domain.model.DiagnosticPlan;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import co.com.devsoft.devopsmind.domain.repository.DiagnosticEngineAi;

@Component
public class DiagnosticSpringAiAdapter implements DiagnosticEngineAi {

    private final ChatClient chatClient;

    public DiagnosticSpringAiAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultSystem("""
                Eres un Ingeniero DevOps SRE experto en el laboratorio LabMetricsIA.
                Tu misión es analizar la descripción de un error y las métricas de hardware actuales,
                utilizar el contexto técnico provisto de los manuales y devolver un plan de diagnóstico estructurado.
                Debes responder estrictamente en el siguiente formato separado por la palabra '|':
                CONCLUSION_DEL_ANALISIS | PASO_1, PASO_2, PASO_3 | REQUIERE_REBOOT_KERNEL(true/false)
                """).build();
    }

    @Override
    public DiagnosticPlan generatePlan(String errorDescription, ServerMetrics metrics, String formattedContext) {
        String template = """
                CONTEXTO DE MANUALES TÉCNICOS: %s
                DETALLES DEL FALLO ACTUAL:
                    - Error: %s
                    - Uso CPU: %.2f%%
                    - RAM Usada: %.2f GB
                Por favor, genera la conclusión, los comandos específicos de solución y evalúa si requiere reiniciar.
                """;

        String promptUser = String.format(template, formattedContext, errorDescription,
                metrics.getCpuUsagePercentage(), metrics.getRamUsageGigabytes());

        String rawAiResponse = chatClient.prompt()
                .user(promptUser)
                .call()
                .content();

        return parseAIResponse(rawAiResponse);
    }

     private DiagnosticPlan parseAIResponse(String response) {
        try {
            String[] parts = response.split("\\|");
            String conclusion = parts[0].trim();
            List<String> steps = Arrays.stream(parts[1].split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            boolean reboot = Boolean.parseBoolean(parts[2].trim());

            return new DiagnosticPlan(conclusion, steps, reboot);
        } catch (Exception e) {
            return new DiagnosticPlan("Mitigación de contingencia: No se pudo parsear el formato estructurado del LLM.",
                    List.of("Verificar estado de sockets", "Ejecutar docker service restart"),
                    Boolean.FALSE);
        }
    }
}
