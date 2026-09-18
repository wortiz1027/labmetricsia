package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto;

public record ServerMetricsInputDTO(double cpuUsagePercentage, double ramUsageGigabytes, double gpuVramUsageGigabytes, boolean isSwapActive) {

}
