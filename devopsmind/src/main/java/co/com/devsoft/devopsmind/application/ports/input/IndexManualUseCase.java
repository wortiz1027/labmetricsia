package co.com.devsoft.devopsmind.application.ports.input;

import co.com.devsoft.devopsmind.domain.model.ManualId;

public interface IndexManualUseCase {
    void index(ManualId id, String rawText, String sectionName);
}
