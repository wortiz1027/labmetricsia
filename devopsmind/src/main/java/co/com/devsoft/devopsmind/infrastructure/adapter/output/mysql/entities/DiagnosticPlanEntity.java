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
@Table(name = "tb_diagnostic_plans")
public class DiagnosticPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private LabIncidentEntity incident;

    @Column(name = "analysis_conclusion", nullable = false, columnDefinition = "TEXT")
    private String analysisConclusion;

    @Column(name = "steps_to_solve", nullable = false, columnDefinition = "JSON")
    private String stepsToSolve; // 🎯 Guardamos la estructura List<String> como JSON en crudo stringificado

    @Column(name = "requires_kernel_reboot", nullable = false)
    private boolean requiresKernelReboot;

    public DiagnosticPlanEntity() {
    }

    // Getters y Setters estándar
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

    public String getAnalysisConclusion() {
        return analysisConclusion;
    }

    public void setAnalysisConclusion(String analysisConclusion) {
        this.analysisConclusion = analysisConclusion;
    }

    public String getStepsToSolve() {
        return stepsToSolve;
    }

    public void setStepsToSolve(String stepsToSolve) {
        this.stepsToSolve = stepsToSolve;
    }

    public boolean isRequiresKernelReboot() {
        return requiresKernelReboot;
    }

    public void setRequiresKernelReboot(boolean requiresKernelReboot) {
        this.requiresKernelReboot = requiresKernelReboot;
    }
}
