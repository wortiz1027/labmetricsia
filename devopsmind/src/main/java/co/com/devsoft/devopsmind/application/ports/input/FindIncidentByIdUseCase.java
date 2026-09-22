package co.com.devsoft.devopsmind.application.ports.input;

import co.com.devsoft.devopsmind.domain.model.LabIncident;
import java.util.Optional;

public interface FindIncidentByIdUseCase {
    Optional<LabIncident> findById(String id);
}
