package co.com.devsoft.devopsmind.domain.model;

import java.util.UUID;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;
import co.com.devsoft.devopsmind.domain.exception.InvalidDomainStateException;

public class LabIncident {

    private final String id;
    private final String errorDescription;
    private IncidentStatus status;
    private IncidentSeverity severity = IncidentSeverity.LOW;
    private ServerMetrics metrics;
    private DiagnosticPlan solutionPlan;

    public LabIncident(String errorDescription) {

        if (errorDescription == null || errorDescription.trim().isEmpty()) {
            throw new InvalidDomainDataException("La descripción del error no puede estar vacía");
        }

        this.id = UUID.randomUUID().toString();
        this.errorDescription = errorDescription;
        this.status = IncidentStatus.OPEN;
    }

    public void evaluateSeverity(ServerMetrics metrics) {
        if (metrics == null) {
            throw new InvalidDomainDataException("Las métricas de hardware no pueden ser nulas");
        }

        this.metrics = metrics;
        this.status = IncidentStatus.ANALYZING;

        if (metrics.hasMemoryAlert() || metrics.requiresCacheFlush()) {
            this.severity = IncidentSeverity.CRITICAL;
            System.out.println(
                    String.format("⚠️ [Dominio] Alerta crítica de recursos detectada para el incidente: %s", id));
        } else if (metrics.requiresCacheFlush()) {
            this.severity = IncidentSeverity.HIGH;
        } else {
            this.severity = IncidentSeverity.MEDIUM;

        }
    }

    public void attachDiagnosis(DiagnosticPlan plan) {
        if (this.status != IncidentStatus.ANALYZING) {
            throw new InvalidDomainStateException(
                    "No se puede adjuntar un diagnóstico a un incidente que no ha sido analizado");
        }

        if (plan == null) {
            throw new InvalidDomainDataException("El plan de diagnóstico no puede ser nulo");
        }

        this.solutionPlan = plan;

        if (!plan.isRequiresKernelReboot() && !plan.hasForbiddenCommands()) {
            this.status = IncidentStatus.RESOLVED;
        }
    }

    public String getId() {
        return id;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public ServerMetrics getMetrics() {
        return metrics;
    }

    public DiagnosticPlan getSolutionPlan() {
        return solutionPlan;
    }

}
