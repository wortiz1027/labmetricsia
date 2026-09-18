package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto;

public record ServerMetricsOutputDTO(double cpuUsagePercentage,
        double ramUsageGigabytes,
        double gpuVramUsageGigabytes,
        boolean isSwapActive,
        boolean hasMemoryAlert,
        boolean requiresCacheFlush) {

}
