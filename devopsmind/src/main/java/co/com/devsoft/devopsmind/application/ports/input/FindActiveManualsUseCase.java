package co.com.devsoft.devopsmind.application.ports.input;

import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import java.util.List;

public interface FindActiveManualsUseCase {
    List<TechnicalManual> findActive();
}
