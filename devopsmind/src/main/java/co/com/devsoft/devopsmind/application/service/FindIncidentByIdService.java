package co.com.devsoft.devopsmind.application.service;

import java.util.Optional;

import co.com.devsoft.devopsmind.application.ports.input.FindIncidentByIdUseCase;
import co.com.devsoft.devopsmind.application.ports.input.UseCase;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.repository.LabIncidentStorage;

@UseCase(description = "Busca un incidente específico de hardware y su plan cognitivo asociado mediante su ID de auditoría")
public class FindIncidentByIdService implements FindIncidentByIdUseCase {

    private final LabIncidentStorage incidentStorage;

    public FindIncidentByIdService(LabIncidentStorage incidentStorage) {
        this.incidentStorage = incidentStorage;
    }

    @Override
    public Optional<LabIncident> findById(String id) {
        return incidentStorage.findBy(id);
    }
}
