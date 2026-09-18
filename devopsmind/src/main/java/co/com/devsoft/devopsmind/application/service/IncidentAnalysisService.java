package co.com.devsoft.devopsmind.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.devsoft.devopsmind.application.ports.input.AnalyzeIncidentUseCase;
import co.com.devsoft.devopsmind.domain.model.DiagnosticPlan;
import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import co.com.devsoft.devopsmind.domain.repository.LabIncidentStorage;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@Service
public class IncidentAnalysisService implements AnalyzeIncidentUseCase {

    private static final Logger log = LoggerFactory.getLogger(IncidentAnalysisService.class);

    private final LabIncidentStorage incidentStorage;
    private final TechnicalManualStorage manualStorage;
    private final ChatClient chatClient;

    public IncidentAnalysisService(LabIncidentStorage incidentStorage,
            TechnicalManualStorage manualStorage,
            ChatClient.Builder chatClientBuilder) {
        this.incidentStorage = incidentStorage;
        this.manualStorage = manualStorage;
        this.chatClient = chatClientBuilder
                .defaultSystem(
                        """
                                Eres un Ingeniero DevOps SRE experto en el laboratorio LabMetricsIA.
                                Tu misión es analizar la descripción de un error y las métricas de hardware actuales,
                                utilizar el contexto técnico provisto de los manuales y devolver un plan de diagnóstico estructurado.
                                Debes responder estrictamente en el siguiente formato separado por la palabra '|':
                                CONCLUSION_DEL_ANALISIS | PASO_1, PASO_2, PASO_3 | REQUIERE_REBOOT_KERNEL(true/false)
                                """)
                .build();
    }

    @Override
    @Transactional("transactionManager")
    public LabIncident analyze(String errorDescription, ServerMetrics metrics) {
        log.info("🚨 [Servicio Aplicación] Procesando nuevo incidente de observabilidad...");

        LabIncident incident = new LabIncident(errorDescription);

        List<KnowledgeChunk> contextChunks = this.manualStorage.findRelevantChunks(errorDescription, 3);

        String formattedContext = contextChunks.stream()
                .map(KnowledgeChunk::formatForContext)
                .collect(Collectors.joining("\n"));
        String template = """
                CONTEXTO DE MANUALES TÉCNICOS: %s
                DETALLES DEL FALLO ACTUAL:
                    - Error: %s
                    - Uso CPU: %.2f%%
                    - RAM Usada: %.2f GB
                Por favor, genera la conclusión, los comandos específicos de solución y evalúa si requiere reiniciar.
                """;
        String promptUser = String.format(template, formattedContext, incident.getErrorDescription(),
                metrics.getCpuUsagePercentage(), metrics.getRamUsageGigabytes());

        log.info("🤖 [Servicio Aplicación] Invocando LLM local vía Ollama...");
        String rawAiResponse = chatClient.prompt()
                .user(promptUser)
                .call()
                .content();

        DiagnosticPlan aiPlan = parseAIResponse(rawAiResponse);

        if (aiPlan.hasForbiddenCommands()) {
            log.info("🤖 [Servicio Aplicación] Invocando LLM local vía Ollama...");
            aiPlan = new DiagnosticPlan(
                    "ANÁLISIS ABORTADO: La IA sugirió una instrucción catalogada como destructiva o peligrosa para el host.",
                    List.of("Revisar manualmente los logs del contenedor ya que la automatización fue bloqueada por seguridad."),
                    Boolean.FALSE);
        }

        incident.attachDiagnosis(aiPlan);

        incidentStorage.save(incident);
        log.info(String.format(
                "🎉 [Servicio Aplicación] Análisis de incidente completado de forma segura. Estado Final: {}",
                incident.getStatus()));

        return incident;
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
