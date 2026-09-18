package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

import java.util.List;

public record DiagnosticPlanResponse(
        String analysisConclusion,
        List<String> stepsToSolve,
        boolean requiresKernelReboot,
        boolean hasForbiddenCommands) {
}
