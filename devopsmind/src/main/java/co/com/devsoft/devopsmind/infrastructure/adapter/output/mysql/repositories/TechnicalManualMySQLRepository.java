package co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.TechnicalManualEntity;

public interface TechnicalManualMySQLRepository extends JpaRepository<TechnicalManualEntity, String> {

    @Query("SELECT m FROM TechnicalManualEntity m WHERE m.isDeprecated = false")
    List<TechnicalManualEntity> findByIsDeprecatedFalse();

}
