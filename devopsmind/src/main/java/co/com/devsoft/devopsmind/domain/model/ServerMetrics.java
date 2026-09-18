package co.com.devsoft.devopsmind.domain.model;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;

public class ServerMetrics {

    private final double cpuUsagePercentage;
    private final double ramUsageGigabytes;
    private final double gpuVramUsageGigabytes;
    private final boolean isSwapActive;

    public ServerMetrics(double cpuUsagePercentage, double ramUsageGigabytes,
            double gpuVramUsageGigabytes, boolean isSwapActive) {

        if (cpuUsagePercentage < 0 || cpuUsagePercentage > 100) {
            throw new InvalidDomainDataException("El porcentaje de CPU debe estar entre 0 y 100");
        }

        this.cpuUsagePercentage = cpuUsagePercentage;
        this.ramUsageGigabytes = ramUsageGigabytes;
        this.gpuVramUsageGigabytes = gpuVramUsageGigabytes;
        this.isSwapActive = isSwapActive;
    }

    public boolean hasMemoryAlert() {
        return this.ramUsageGigabytes > 28.0 || isSwapActive;
    }

    public boolean requiresCacheFlush() {
        return cpuUsagePercentage > 90.0 && gpuVramUsageGigabytes > 10.0;
    }

    public double getCpuUsagePercentage() {
        return cpuUsagePercentage;
    }

    public double getRamUsageGigabytes() {
        return ramUsageGigabytes;
    }

    public double getGpuVramUsageGigabytes() {
        return gpuVramUsageGigabytes;
    }

    public boolean isSwapActive() {
        return isSwapActive;
    }

}
