package co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.LabIncidentEntity;

public interface LabIncidentMySQLRepository extends JpaRepository<LabIncidentEntity, String> {
    List<LabIncidentEntity> findBy(String status);
}
