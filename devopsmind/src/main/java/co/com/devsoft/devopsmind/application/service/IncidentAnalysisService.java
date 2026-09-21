package co.com.devsoft.devopsmind.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.devsoft.devopsmind.application.ports.input.AnalyzeIncidentUseCase;
import co.com.devsoft.devopsmind.application.ports.input.UseCase;
import co.com.devsoft.devopsmind.domain.model.DiagnosticPlan;
import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import co.com.devsoft.devopsmind.domain.repository.DiagnosticEngineAi;
import co.com.devsoft.devopsmind.domain.repository.LabIncidentStorage;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@UseCase(description = "Orquesta la segmentación semántica de manuales técnicos en párrafos informativos e indexa sus embeddings vectoriales de 1536 dimensiones en pgvector")
public class IncidentAnalysisService implements AnalyzeIncidentUseCase {

    private static final Logger log = LoggerFactory.getLogger(IncidentAnalysisService.class);

    private final LabIncidentStorage incidentStorage;
    private final TechnicalManualStorage manualStorage;
    private final DiagnosticEngineAi aiEngine;

    public IncidentAnalysisService(LabIncidentStorage incidentStorage,
            TechnicalManualStorage manualStorage,
            DiagnosticEngineAi aiEngine) {
        this.incidentStorage = incidentStorage;
        this.manualStorage = manualStorage;
        this.aiEngine = aiEngine;
    }

    @Override
    public LabIncident analyze(String errorDescription, ServerMetrics metrics) {
        log.info("🚨 [Servicio Aplicación] Procesando nuevo incidente de observabilidad...");

        LabIncident incident = new LabIncident(errorDescription);

        List<KnowledgeChunk> contextChunks = this.manualStorage.findRelevantChunks(errorDescription, 3);

        String formattedContext = contextChunks.stream()
                .map(KnowledgeChunk::formatForContext)
                .collect(Collectors.joining("\n"));

        log.info("🤖 [Servicio Aplicación] Solicitando diagnóstico al motor cognitivo abstratado...");
        DiagnosticPlan aiPlan = this.aiEngine.generatePlan(errorDescription, metrics, formattedContext);

        if (aiPlan.hasForbiddenCommands()) {
            log.warn(
                    "⚠️ [DOMINIO - ALERTA] ¡Comando prohibido detectado en la propuesta del LLM! Bloqueando mitigación.");
            aiPlan = new DiagnosticPlan(
                    "ANÁLISIS ABORTADO: La IA sugirió una instrucción catalogada como destructiva o peligrosa para el host.",
                    List.of("Revisar manualmente los logs del contenedor ya que la automatización fue bloqueada por seguridad."),
                    Boolean.FALSE);
        }

        incident.attachDiagnosis(aiPlan);
        this.incidentStorage.save(incident);

        log.info("🎉 [Servicio Aplicación] Análisis de incidente completado de forma segura. Estado Final: {}",
                incident.getStatus());

        return incident;
    }

}
