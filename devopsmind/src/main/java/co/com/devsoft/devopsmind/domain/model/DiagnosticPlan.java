package co.com.devsoft.devopsmind.domain.model;

import java.util.List;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;

public class DiagnosticPlan {

    private final String analysisConclusion;
    private final List<String> stepsToSolve;
    private final boolean requiresKernelReboot;

    public DiagnosticPlan(String analysisConclusion, List<String> stepsToSolve, boolean requiresKernelReboot) {

        if (analysisConclusion == null || analysisConclusion.trim().isEmpty()) {
            throw new InvalidDomainDataException("La conclusión del análisis no puede estar vacía");
        }

        if (stepsToSolve == null || stepsToSolve.isEmpty()) {
            throw new InvalidDomainDataException("La lista de pasos de solución no puede estar vacía");
        }

        this.analysisConclusion = analysisConclusion;
        this.stepsToSolve = List.copyOf(stepsToSolve);
        this.requiresKernelReboot = requiresKernelReboot;
    }

    public boolean hasForbiddenCommands() {
        for (String step : stepsToSolve) {
            String lowerStep = step.toLowerCase();
            if (lowerStep.contains("rm -rf /") || lowerStep.contains("mkfs") || lowerStep.contains(":(){:|:&};:"))
                return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public String getAnalysisConclusion() {
        return analysisConclusion;
    }

    public List<String> getStepsToSolve() {
        return stepsToSolve;
    }

    public boolean isRequiresKernelReboot() {
        return requiresKernelReboot;
    }

}
