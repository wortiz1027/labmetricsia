package co.com.devsoft.devopsmind.domain.repository;

import co.com.devsoft.devopsmind.domain.model.DiagnosticPlan;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;

public interface DiagnosticEngineAi {

    DiagnosticPlan generatePlan(String errorDescription, ServerMetrics metrics, String formattedContext);

}
