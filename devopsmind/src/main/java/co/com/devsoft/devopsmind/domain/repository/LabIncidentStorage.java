package co.com.devsoft.devopsmind.domain.repository;

import java.util.List;
import java.util.Optional;

import co.com.devsoft.devopsmind.domain.model.IncidentStatus;
import co.com.devsoft.devopsmind.domain.model.LabIncident;

public interface LabIncidentStorage {

    LabIncident save(LabIncident incident);

    Optional<LabIncident> findBy(String id);

    List<LabIncident> findByStatus(IncidentStatus status);

}
