package co.com.devsoft.devopsmind.infrastructure.adapter.output;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.com.devsoft.devopsmind.domain.model.IncidentStatus;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.repository.LabIncidentStorage;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.LabIncidentEntity;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.mappers.LabIncidentMapper;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.repositories.LabIncidentMySQLRepository;

@Component
public class LabIncidentStorageAdapter implements LabIncidentStorage {

    private final LabIncidentMySQLRepository repository;

    public LabIncidentStorageAdapter(LabIncidentMySQLRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional("transactionManager")
    public LabIncident save(LabIncident incident) {
        LabIncidentEntity entity = LabIncidentMapper.toEntity(incident);

        repository.save(entity);
        return incident;
    }

    @Override
    public Optional<LabIncident> findBy(String id) {
        return repository.findById(id)
                .map(LabIncidentMapper::toDomain);
    }

    @Override
    public List<LabIncident> findByStatus(IncidentStatus status) {
        return repository.findBy(status.name())
                .stream()
                .map(LabIncidentMapper::toDomain)
                .collect(Collectors.toList());
    }

}
