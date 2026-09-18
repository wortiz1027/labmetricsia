package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

public record IncidentReportRequest(
        String errorDescription,
        ServerMetricsRequest metrics) {
}
