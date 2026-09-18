package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

public record LabIncidentResponse(
        String id,
        String errorDescription,
        String status,
        String severity,
        ServerMetricsResponse metrics,
        DiagnosticPlanResponse solutionPlan) {
}
