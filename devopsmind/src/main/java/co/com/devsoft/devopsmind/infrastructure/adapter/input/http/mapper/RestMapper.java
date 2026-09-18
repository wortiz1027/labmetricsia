package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.mapper;

import co.com.devsoft.devopsmind.domain.model.*;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.DiagnosticPlanResponse;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.KnowledgeChunkResponse;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.LabIncidentResponse;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.ServerMetricsRequest;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.ServerMetricsResponse;

import java.util.Optional;

public final class RestMapper {

    private RestMapper() {
        throw new AssertionError("No se permite instanciar RestMapper");
    }

    public static Optional<KnowledgeChunkResponse> toResponse(KnowledgeChunk domain) {
        if (domain == null)
            return Optional.empty();
        return Optional.of(new KnowledgeChunkResponse(
                domain.getContent(),
                domain.getSectionName(),
                domain.getTokenCountEstimate(),
                domain.formatForContext()));
    }

    public static ServerMetrics toDomain(ServerMetricsRequest request) {
        if (request == null)
            return null;
        return new ServerMetrics(
                request.cpuUsagePercentage(),
                request.ramUsageGigabytes(),
                request.gpuVramUsageGigabytes(),
                request.isSwapActive());
    }

    public static ServerMetricsResponse toResponse(ServerMetrics domain) {
        if (domain == null)
            return null;
        return new ServerMetricsResponse(
                domain.getCpuUsagePercentage(),
                domain.getRamUsageGigabytes(),
                domain.getGpuVramUsageGigabytes(),
                domain.isSwapActive(),
                domain.hasMemoryAlert(),
                domain.requiresCacheFlush());
    }

    public static DiagnosticPlanResponse toResponse(DiagnosticPlan domain) {
        if (domain == null)
            return null;
        return new DiagnosticPlanResponse(
                domain.getAnalysisConclusion(),
                domain.getStepsToSolve(),
                domain.isRequiresKernelReboot(),
                domain.hasForbiddenCommands());
    }

    public static LabIncidentResponse toResponse(LabIncident domain) {
        if (domain == null)
            return null;

        String severityName = "MEDIUM";
        try {
            java.lang.reflect.Field severityField = LabIncident.class.getDeclaredField("severity");
            severityField.setAccessible(true);
            severityName = severityField.get(domain).toString();
        } catch (Exception ignored) {
        }

        return new LabIncidentResponse(
                domain.getId(),
                domain.getErrorDescription(),
                domain.getStatus().name(),
                severityName,
                toResponse(domain.getMetrics()),
                toResponse(domain.getSolutionPlan()));
    }
}
