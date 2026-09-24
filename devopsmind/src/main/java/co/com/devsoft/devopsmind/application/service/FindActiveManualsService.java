package co.com.devsoft.devopsmind.application.service;

import java.util.List;

import co.com.devsoft.devopsmind.application.ports.input.FindActiveManualsUseCase;
import co.com.devsoft.devopsmind.application.ports.input.UseCase;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@UseCase(description = "Recupera la lista de manuales de ingeniería activos y no obsoletos indexados en el sistema")
public class FindActiveManualsService implements FindActiveManualsUseCase {

    private final TechnicalManualStorage manualStorage;

    public FindActiveManualsService(TechnicalManualStorage manualStorage) {
        this.manualStorage = manualStorage;
    }

    @Override
    public List<TechnicalManual> findActive() {
        return manualStorage.findActiveManuals();
    }
}
