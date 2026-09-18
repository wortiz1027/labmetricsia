package co.com.devsoft.devopsmind.application.ports.input;

import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ServerMetrics;

public interface AnalyzeIncidentUseCase {

    LabIncident analyze(String errorDescription, ServerMetrics metrics);

}
