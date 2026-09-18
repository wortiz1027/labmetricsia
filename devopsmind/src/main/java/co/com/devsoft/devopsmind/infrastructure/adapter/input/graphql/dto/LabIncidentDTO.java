package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto;

public record LabIncidentDTO(String id,
        String errorDescription,
        String status,
        String severity,
        ServerMetricsOutputDTO metrics,
        DiagnosticPlanDTO solutionPlan) {

}
