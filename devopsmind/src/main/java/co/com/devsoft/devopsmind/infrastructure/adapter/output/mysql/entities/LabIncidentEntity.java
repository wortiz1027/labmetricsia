package co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_lab_incidents")
public class LabIncidentEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "error_description", nullable = false, columnDefinition = "TEXT")
    private String errorDescription;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @OneToOne(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServerMetricsEntity metrics;

    @OneToOne(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DiagnosticPlanEntity solutionPlan;

    public LabIncidentEntity() {
    }

    // Getters y Setters estándar
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public ServerMetricsEntity getMetrics() {
        return metrics;
    }

    public void setMetrics(ServerMetricsEntity metrics) {
        this.metrics = metrics;
    }

    public DiagnosticPlanEntity getSolutionPlan() {
        return solutionPlan;
    }

    public void setSolutionPlan(DiagnosticPlanEntity solutionPlan) {
        this.solutionPlan = solutionPlan;
    }
}
