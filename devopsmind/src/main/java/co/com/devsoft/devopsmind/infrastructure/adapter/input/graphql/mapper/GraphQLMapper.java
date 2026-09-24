package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import co.com.devsoft.devopsmind.domain.model.DiagnosticPlan;
import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.DiagnosticPlanDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.KnowledgeChunkDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.LabIncidentDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.ServerMetricsInputDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.ServerMetricsOutputDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.TechnicalManualDTO;

public class GraphQLMapper {

    private GraphQLMapper() {
        throw new AssertionError("No se permite instanciar GraphQLMapper");
    }

    public static Optional<KnowledgeChunkDTO> toDTO(KnowledgeChunk domain) {
        if (domain == null) return Optional.empty();

        return Optional.of(
            new KnowledgeChunkDTO(
                domain.getContent(),
                domain.getSectionName(),
                domain.getTokenCountEstimate(),
                domain.formatForContext()
            )
        );
    }

    public static TechnicalManualDTO toDTO(TechnicalManual domain) {
        if (domain == null)
            return null;

        List<KnowledgeChunkDTO> chunkDTOs = domain.getChunks().stream()
                .map(GraphQLMapper::toDTO)
                .filter(opt -> opt != null && opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());

        return new TechnicalManualDTO(
                domain.getId().getValue(),
                domain.getTitle(),
                domain.isDeprecated(),
                chunkDTOs);
    }

    public static ServerMetrics toDomain(ServerMetricsInputDTO input) {
        if (input == null) return null;
        return new ServerMetrics(
            input.cpuUsagePercentage(),
            input.ramUsageGigabytes(),
            input.gpuVramUsageGigabytes(),
            input.isSwapActive()
        );
    }

    public static ServerMetricsOutputDTO toDTO(ServerMetrics domain) {
        if (domain == null) return null;

        return new ServerMetricsOutputDTO(
            domain.getCpuUsagePercentage(),
            domain.getRamUsageGigabytes(),
            domain.getGpuVramUsageGigabytes(),
            domain.isSwapActive(),
            domain.hasMemoryAlert(),
            domain.requiresCacheFlush()
        );
    }

    public static DiagnosticPlanDTO toDTO(DiagnosticPlan domain) {
        if (domain == null) return null;
        return new DiagnosticPlanDTO(
            domain.getAnalysisConclusion(),
            domain.getStepsToSolve(),
            domain.isRequiresKernelReboot(),
            domain.hasForbiddenCommands()
        );
    }

    public static LabIncidentDTO toDTO(LabIncident domain) {
        if (domain == null) return null;

        String severityName = "MEDIUM";
        try {
            java.lang.reflect.Field severityField = LabIncident.class.getDeclaredField("severity");
            severityField.setAccessible(true);
            severityName = severityField.get(domain).toString();
        } catch (Exception ignored) {}

        return new LabIncidentDTO(
            domain.getId(),
            domain.getErrorDescription(),
            domain.getStatus().name(),
            severityName,
            toDTO(domain.getMetrics()),
            toDTO(domain.getSolutionPlan())
        );
    }

}
