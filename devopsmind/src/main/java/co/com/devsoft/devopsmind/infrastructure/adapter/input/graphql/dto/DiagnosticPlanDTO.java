package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto;

import java.util.List;

public record DiagnosticPlanDTO(String analysisConclusion,
        List<String> stepsToSolve,
        boolean requiresKernelReboot,
        boolean hasForbiddenCommands) {

}
