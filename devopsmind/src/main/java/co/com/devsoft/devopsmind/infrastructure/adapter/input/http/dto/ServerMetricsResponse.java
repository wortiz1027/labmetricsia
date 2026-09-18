package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

public record ServerMetricsResponse(
        double cpuUsagePercentage,
        double ramUsageGigabytes,
        double gpuVramUsageGigabytes,
        boolean isSwapActive,
        boolean hasMemoryAlert,
        boolean requiresCacheFlush) {
}
