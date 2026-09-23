package co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_server_metrics")
public class ServerMetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private LabIncidentEntity incident;

    @Column(name = "cpu_usage_percentage", nullable = false)
    private double cpuUsagePercentage;

    @Column(name = "ram_usage_gigabytes", nullable = false)
    private double ramUsageGigabytes;

    @Column(name = "gpu_vram_usage_gigabytes", nullable = false)
    private double gpuVramUsageGigabytes;

    @Column(name = "is_swap_active", nullable = false)
    private boolean isSwapActive;

    public ServerMetricsEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LabIncidentEntity getIncident() {
        return incident;
    }

    public void setIncident(LabIncidentEntity incident) {
        this.incident = incident;
    }

    public double getCpuUsagePercentage() {
        return cpuUsagePercentage;
    }

    public void setCpuUsagePercentage(double cpuUsagePercentage) {
        this.cpuUsagePercentage = cpuUsagePercentage;
    }

    public double getRamUsageGigabytes() {
        return ramUsageGigabytes;
    }

    public void setRamUsageGigabytes(double ramUsageGigabytes) {
        this.ramUsageGigabytes = ramUsageGigabytes;
    }

    public double getGpuVramUsageGigabytes() {
        return gpuVramUsageGigabytes;
    }

    public void setGpuVramUsageGigabytes(double gpuVramUsageGigabytes) {
        this.gpuVramUsageGigabytes = gpuVramUsageGigabytes;
    }

    public boolean isSwapActive() {
        return isSwapActive;
    }

    public void setSwapActive(boolean swapActive) {
        isSwapActive = swapActive;
    }
}
