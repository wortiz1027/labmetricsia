package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

public record ServerMetricsRequest(
        double cpuUsagePercentage,
        double ramUsageGigabytes,
        double gpuVramUsageGigabytes,
        boolean isSwapActive) {
}
