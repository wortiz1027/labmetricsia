package co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.mappers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import co.com.devsoft.devopsmind.domain.model.DiagnosticPlan;
import co.com.devsoft.devopsmind.domain.model.IncidentStatus;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.DiagnosticPlanEntity;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.LabIncidentEntity;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.ServerMetricsEntity;

public final class LabIncidentMapper {

    private LabIncidentMapper() {
        throw new AssertionError("No se permite instanciar LabIncidentMapper");
    }

    public static LabIncidentEntity toEntity(LabIncident domain) {
        if (domain == null) return null;

        LabIncidentEntity entity = new LabIncidentEntity();
        entity.setId(domain.getId());
        entity.setErrorDescription(domain.getErrorDescription());
        entity.setStatus(domain.getStatus().name());

        if (domain.getMetrics() != null) {
            ServerMetricsEntity metricsEntity = new ServerMetricsEntity();

            metricsEntity.setIncident(entity);
            metricsEntity.setCpuUsagePercentage(domain.getMetrics().getCpuUsagePercentage());
            metricsEntity.setRamUsageGigabytes(domain.getMetrics().getRamUsageGigabytes());
            metricsEntity.setGpuVramUsageGigabytes(domain.getMetrics().getGpuVramUsageGigabytes());
            metricsEntity.setSwapActive(domain.getMetrics().isSwapActive());

            entity.setMetrics(metricsEntity);
        }

        if (domain.getSolutionPlan() != null) {
            DiagnosticPlanEntity planEntity = new DiagnosticPlanEntity();
            planEntity.setIncident(entity);
            planEntity.setAnalysisConclusion(domain.getSolutionPlan().getAnalysisConclusion());
            planEntity.setRequiresKernelReboot(domain.getSolutionPlan().isRequiresKernelReboot());

            String jsonSteps = domain.getSolutionPlan().getStepsToSolve().stream()
                    .map(step -> "\"" + step.replace("\"", "\\\"") + "\"")
                    .collect(Collectors.joining(",", "[", "]"));
            planEntity.setStepsToSolve(jsonSteps);

            entity.setSolutionPlan(planEntity);
        }

        return entity;
    }

    public static LabIncident toDomain(LabIncidentEntity entity) {
        if (entity == null) return null;

        LabIncident domain = new LabIncident(entity.getErrorDescription());

        try {
            java.lang.reflect.Field idField = LabIncident.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(domain, entity.getId());

            java.lang.reflect.Field statusField = LabIncident.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(domain, IncidentStatus.valueOf(entity.getStatus()));
        } catch (Exception e) {
            throw new RuntimeException("Error fatal reconstruyendo la integridad del Agregado LabIncident", e);
        }

        if (entity.getMetrics() != null) {
            ServerMetrics metrics = new ServerMetrics(
                    entity.getMetrics().getCpuUsagePercentage(),
                    entity.getMetrics().getRamUsageGigabytes(),
                    entity.getMetrics().getGpuVramUsageGigabytes(),
                    entity.getMetrics().isSwapActive()
            );
            domain.evaluateSeverity(metrics);
        }

        if (entity.getSolutionPlan() != null) {
            String cleanJson = entity.getSolutionPlan().getStepsToSolve()
                    .replaceAll("[\\[\\]\"]", "");
            List<String> steps = Arrays.asList(cleanJson.split(","));

            DiagnosticPlan plan = new DiagnosticPlan(
                    entity.getSolutionPlan().getAnalysisConclusion(),
                    steps,
                    entity.getSolutionPlan().isRequiresKernelReboot()
            );
            domain.attachDiagnosis(plan);
        }

        return domain;
    }

}
